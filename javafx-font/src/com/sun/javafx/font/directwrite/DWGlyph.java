/*
 * Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package com.sun.javafx.font.directwrite;

import com.sun.javafx.font.FontResource;
import com.sun.javafx.font.Glyph;
import com.sun.javafx.font.PrismFontFactory;
import com.sun.javafx.geom.Point2D;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.Shape;

public class DWGlyph implements Glyph {
    private DWFontStrike strike;
    private DWRITE_GLYPH_METRICS metrics;
    private DWRITE_GLYPH_RUN run;
    private float pixelXAdvance, pixelYAdvance;
    private RECT rect;
    private boolean drawShapes;

    private static final boolean CACHE_TARGET = true;
    private static IWICBitmap cachedBitmap;
    private static ID2D1RenderTarget cachedTarget;
    private static final int BITMAP_WIDTH = 256;
    private static final int BITMAP_HEIGHT = 256;
    private static final int BITMAP_PIXEL_FORMAT = OS.GUID_WICPixelFormat32bppPBGRA;
    private static D2D1_COLOR_F BLACK = new D2D1_COLOR_F(0f, 0f, 0f, 1f);
    private static D2D1_COLOR_F WHITE = new D2D1_COLOR_F(1f, 1f, 1f, 1f);
    private static D2D1_MATRIX_3X2_F D2D2_MATRIX_IDENTITY = new D2D1_MATRIX_3X2_F(1,0, 0,1, 0,0);

    DWGlyph(DWFontStrike strike, int glyphCode, boolean drawShapes) {
        this.strike = strike;
        this.drawShapes = drawShapes;

        IDWriteFontFace face = strike.getFontFace();
        run = new DWRITE_GLYPH_RUN();
        run.fontFace = face.ptr;
        run.fontEmSize = strike.getSize();
        run.glyphCount = 1;
        run.glyphIndices = (short)glyphCode;
        run.glyphAdvances = 0;
        run.bidiLevel = 0; //should not matter as it draws just one glyph
        run.isSideways = false;

        /* Note: glyphs can be created on the JFX thread to create shapes
         * for measuring. Therefore, avoid touching any native resource
         * (WICFactory or D2DFactory) here as they are not thread safe.
         */
    }

    void checkMetrics() {
        if (metrics != null) return;
        //TODO could the metrics cached in DWFontFile be used ?
        IDWriteFontFace face = strike.getFontFace();
        metrics = face.GetDesignGlyphMetrics(run.glyphIndices, false);
        float upem = strike.getUpem();
        pixelXAdvance = metrics.advanceWidth * strike.getSize() / upem;
        pixelYAdvance = 0;
        if (strike.matrix != null) {
            Point2D pt = new Point2D(pixelXAdvance, pixelYAdvance);
            strike.getTransform().transform(pt, pt);
            pixelXAdvance = pt.x;
            pixelYAdvance = pt.y;
        }
    }

    void checkBounds() {
        if (rect != null) return;
        /* Note that when generating the glyph image this bounds will be
         * recomputed (respecting the correct subpixel alignment).
         */
        int textureType = OS.DWRITE_TEXTURE_CLEARTYPE_3x1;
        IDWriteGlyphRunAnalysis runAnalysis = createAnalysis(0, 0);
        rect = runAnalysis.GetAlphaTextureBounds(textureType);
        runAnalysis.Release();
    }

    byte[] getLCDMask(float subPixelX, float subPixelY) {
        int textureType = OS.DWRITE_TEXTURE_CLEARTYPE_3x1;
        IDWriteGlyphRunAnalysis runAnalysis = createAnalysis(subPixelX, subPixelY);
        rect = runAnalysis.GetAlphaTextureBounds(textureType);
        byte[] buffer = runAnalysis.CreateAlphaTexture(textureType, rect);
        runAnalysis.Release();
        return buffer;
    }

    byte[] getGrayMask(float subPixelX, float subPixelY) {

        //TODO is using 3x1 for GrayScale (to measure) always correct ?
        int textureType = OS.DWRITE_TEXTURE_CLEARTYPE_3x1;
        IDWriteGlyphRunAnalysis runAnalysis = createAnalysis(subPixelX, subPixelY);
        rect = runAnalysis.GetAlphaTextureBounds(textureType);
        runAnalysis.Release();

        /* Increase the RECT */
        rect.left--;
        rect.top--;
        rect.right++;
        rect.bottom++;
        float glyphX = rect.left;
        float glyphY = rect.top;
        int w = rect.right - rect.left;
        int h = rect.bottom - rect.top;
        boolean cache = CACHE_TARGET && BITMAP_WIDTH >= w && BITMAP_HEIGHT >= h;
        IWICBitmap bitmap;
        ID2D1RenderTarget target;
        if (cache) {
            bitmap = getCachedBitmap();
            target = getCachedRenderingTarget();
        } else {
            bitmap = createBitmap(w, h);
            target = createRenderingTarget(bitmap);
        }

        DWRITE_MATRIX matrix = strike.matrix;
        D2D1_MATRIX_3X2_F transform;
        if (matrix != null) {
            transform = new D2D1_MATRIX_3X2_F(matrix.m11, matrix.m12,
                                              matrix.m21, matrix.m22,
                                              -glyphX, -glyphY);
            glyphX = glyphY = 0;
        } else {
            transform = D2D2_MATRIX_IDENTITY;
            glyphX -= subPixelX;
            glyphY -= subPixelY;
        }

        target.BeginDraw();
        target.SetTransform(transform);
        target.Clear(WHITE);
        D2D1_POINT_2F pt = new D2D1_POINT_2F(-glyphX, -glyphY);
        ID2D1Brush brush = target.CreateSolidColorBrush(BLACK);
        target.SetTextAntialiasMode(OS.D2D1_TEXT_ANTIALIAS_MODE_GRAYSCALE);
        target.DrawGlyphRun(pt, run, brush, OS.DWRITE_MEASURING_MODE_NATURAL);
        int hr = target.EndDraw();
        brush.Release();

        if (hr != OS.S_OK) {
            /* handling errors such as D2DERR_RECREATE_TARGET */
            bitmap.Release();
            cachedBitmap = null;
            target.Release();
            cachedTarget = null;
            if (PrismFontFactory.debugFonts) {
                System.err.println("Rendering failed=" + hr);
            }
            rect.left = rect.top = rect.right = rect.bottom = 0;
            return null;
        }

        IWICBitmapLock lock = bitmap.Lock(0, 0, w, h, OS.WICBitmapLockRead);
        //TODO instead of copying the entire buffer to java it would be faster to
        //blit in native code.
        byte[] buffer = lock.GetDataPointer();
        int stride = lock.GetStride();
        byte[] result = new byte[w*h];
        int i = 0, j = 0;
        byte one = (byte)0xFF;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                result[i++] = (byte)(one - buffer[j + (x*4)]);
            }
            j += stride;
        }
        lock.Release();

        if (!cache) {
            bitmap.Release();
            target.Release();
        }
        return result;
    }

    IDWriteGlyphRunAnalysis createAnalysis(float x, float y) {
        IDWriteFactory factory = DWFactory.getDWriteFactory();
        int renderingMode = OS.DWRITE_RENDERING_MODE_NATURAL;
        int measuringMode = OS.DWRITE_MEASURING_MODE_NATURAL;
        DWRITE_MATRIX matrix = strike.matrix; /* can be null */
        float dpi = 1;  /* Assumes WICBitmap has 96 dpi */
        return factory.CreateGlyphRunAnalysis(run, dpi, matrix, renderingMode, measuringMode, x, y);
    }

    IWICBitmap getCachedBitmap() {
        if (cachedBitmap == null) {
            cachedBitmap = createBitmap(BITMAP_WIDTH, BITMAP_HEIGHT);
        }
        return cachedBitmap;
    }

    ID2D1RenderTarget getCachedRenderingTarget() {
        if (cachedTarget == null) {
            cachedTarget = createRenderingTarget(getCachedBitmap());
        }
        return cachedTarget;
    }

    IWICBitmap createBitmap(int width, int height) {
        IWICImagingFactory factory = DWFactory.getWICFactory();
        return  factory.CreateBitmap(width, height, BITMAP_PIXEL_FORMAT, OS.WICBitmapCacheOnDemand);
    }

    ID2D1RenderTarget createRenderingTarget(IWICBitmap bitmap) {
        D2D1_RENDER_TARGET_PROPERTIES prop = new D2D1_RENDER_TARGET_PROPERTIES();
        /* All values set to defaults */
        prop.type = OS.D2D1_RENDER_TARGET_TYPE_DEFAULT;
        prop.pixelFormat.format = OS.DXGI_FORMAT_UNKNOWN;
        prop.pixelFormat.alphaMode = OS.D2D1_ALPHA_MODE_UNKNOWN;
        prop.dpiX = 0;
        prop.dpiY = 0;
        prop.usage = OS.D2D1_RENDER_TARGET_USAGE_NONE;
        prop.minLevel = OS.D2D1_FEATURE_LEVEL_DEFAULT;
        ID2D1Factory factory = DWFactory.getD2DFactory();
        return factory.CreateWicBitmapRenderTarget(bitmap, prop);
    }

    @Override
    public int getGlyphCode() {
        return run.glyphIndices;
    }

    @Override
    public RectBounds getBBox() {
        return strike.getBBox(run.glyphIndices);
    }

    @Override
    public float getAdvance() {
        checkMetrics();
        float upem = strike.getUpem();
        return metrics.advanceWidth * strike.getSize() / upem;
    }

    @Override
    public Shape getShape() {
        return strike.createGlyphOutline(run.glyphIndices);
    }

    @Override
    public byte[] getPixelData() {
        return getPixelData(0, 0);
    }

    @Override
    public byte[] getPixelData(float x, float y) {
        return isLCDGlyph() ? getLCDMask(x, y) : getGrayMask(x, y);
    }

    @Override
    public float getPixelXAdvance() {
        checkMetrics();
        return pixelXAdvance;
    }

    @Override
    public float getPixelYAdvance() {
        checkMetrics();
        return pixelYAdvance;
    }

    @Override
    public int getWidth() {
        checkBounds();
        return (rect.right - rect.left) * (isLCDGlyph() ? 3 : 1);
    }

    @Override
    public int getHeight() {
        checkBounds();
        return rect.bottom - rect.top;
    }

    @Override
    public int getOriginX() {
        checkBounds();
        return rect.left;
    }

    @Override
    public int getOriginY() {
        checkBounds();
        return rect.top;
    }

    @Override
    public boolean isLCDGlyph() {
        return strike.getAAMode() == FontResource.AA_LCD;
    }

}