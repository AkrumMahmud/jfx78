/*
 * Copyright (c) 2011, 2012, Oracle and/or its affiliates. All rights reserved.
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
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package javafx.scene;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javafx.scene.effect.BlendMode;
import javafx.scene.effect.Shadow;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.sun.javafx.css.StyleableProperty;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.test.OnInvalidateMethodsTestBase;

@RunWith(Parameterized.class)
public class Node_onInvalidate_Test extends OnInvalidateMethodsTestBase {

    public Node_onInvalidate_Test(Configuration configuration) {
        super(configuration);
    }

    @Parameters
    public static Collection<Object[]>data() {
        Object[][] data = new Object[][] {
            {new Configuration(Rectangle.class, "visible", false, new DirtyBits[] {DirtyBits.NODE_VISIBLE, DirtyBits.NODE_BOUNDS})},
            {new Configuration(Rectangle.class, "cursor", Cursor.WAIT, new StyleableProperty[] {findCssStyleableProperty("-fx-cursor")})},
            {new Configuration(Rectangle.class, "opacity", 0.5, new StyleableProperty[] {findCssStyleableProperty("-fx-opacity")})},
            {new Configuration(Rectangle.class, "opacity", 0.5, new DirtyBits[] {DirtyBits.NODE_OPACITY})},
            {new Configuration(Rectangle.class, "blendMode", BlendMode.DARKEN, new StyleableProperty[] {findCssStyleableProperty("-fx-blend-mode")})},
            {new Configuration(Rectangle.class, "blendMode", BlendMode.DARKEN, new DirtyBits[] {DirtyBits.NODE_BLENDMODE})},
            {new Configuration(Rectangle.class, "cache", true, new DirtyBits[] {DirtyBits.NODE_CACHE})},
            {new Configuration(Rectangle.class, "cacheHint", CacheHint.QUALITY, new DirtyBits[] {DirtyBits.NODE_CACHE})},
            {new Configuration(Rectangle.class, "effect", new Shadow(), new StyleableProperty[] {findCssStyleableProperty("-fx-effect")})},
            {new Configuration(Rectangle.class, "translateX", 1.5, new StyleableProperty[] {findCssStyleableProperty("-fx-translate-x")})},
            {new Configuration(Rectangle.class, "translateX", 1.5, new DirtyBits[] {DirtyBits.NODE_TRANSFORM})},
            {new Configuration(Rectangle.class, "translateY", 1.5, new StyleableProperty[] {findCssStyleableProperty("-fx-translate-y")})},
            {new Configuration(Rectangle.class, "translateY", 1.5, new DirtyBits[] {DirtyBits.NODE_TRANSFORM})},
            {new Configuration(Rectangle.class, "translateZ", 1.5, new StyleableProperty[] {findCssStyleableProperty("-fx-translate-z")})},
            {new Configuration(Rectangle.class, "translateZ", 1.5, new DirtyBits[] {DirtyBits.NODE_TRANSFORM})},
            {new Configuration(Rectangle.class, "scaleX", 5.5, new StyleableProperty[] {findCssStyleableProperty("-fx-scale-x")})},
            {new Configuration(Rectangle.class, "scaleX", 5.5, new DirtyBits[] {DirtyBits.NODE_TRANSFORM})},
            {new Configuration(Rectangle.class, "scaleY", 5.5, new StyleableProperty[] {findCssStyleableProperty("-fx-scale-y")})},
            {new Configuration(Rectangle.class, "scaleY", 5.5, new DirtyBits[] {DirtyBits.NODE_TRANSFORM})},
            {new Configuration(Rectangle.class, "scaleZ", 5.5, new StyleableProperty[] {findCssStyleableProperty("-fx-scale-z")})},
            {new Configuration(Rectangle.class, "scaleZ", 5.5, new DirtyBits[] {DirtyBits.NODE_TRANSFORM})},
            {new Configuration(Rectangle.class, "rotate", 55, new StyleableProperty[] {findCssStyleableProperty("-fx-rotate")})},
            {new Configuration(Rectangle.class, "rotate", 55, new DirtyBits[] {DirtyBits.NODE_TRANSFORM})},
            {new Configuration(Rectangle.class, "rotationAxis", Rotate.X_AXIS, new DirtyBits[] {DirtyBits.NODE_TRANSFORM})},
            {new Configuration(Rectangle.class, "clip", new Rectangle(10, 10), new DirtyBits[] {DirtyBits.NODE_CLIP})},
            {new Configuration(Rectangle.class, "focusTraversable", true, new StyleableProperty[] {findCssStyleableProperty("-fx-focus-traversable")})}
        };
        return Arrays.asList(data);
    }


    public static StyleableProperty findCssStyleableProperty(String propertyName) {
        final List<StyleableProperty> keys = Node.impl_CSS_STYLEABLES();
        for(StyleableProperty styleable : keys) {
            if (styleable.getProperty().equals(propertyName)) return styleable;
        }
        return null;
    }

}