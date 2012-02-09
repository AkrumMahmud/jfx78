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
 */
package com.sun.javafx.css.converters;

import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;

import com.sun.javafx.css.Size;
import com.sun.javafx.css.SizeUnits;
import com.sun.javafx.css.StyleConverter;
import com.sun.javafx.css.ParsedValue;


public final class PaintConverter extends StyleConverter<ParsedValue<?, Paint>, Paint> {

    // lazy, thread-safe instatiation
    private static class Holder {
        static PaintConverter INSTANCE = new PaintConverter();
        static SequenceConverter SEQUENCE_INSTANCE = new SequenceConverter();
        static LinearGradientConverter LINEAR_GRADIENT_INSTANCE = new LinearGradientConverter();
        static RadialGradientConverter RADIAL_GRADIENT_INSTANCE = new RadialGradientConverter();
    }

    public static PaintConverter getInstance() {
        return Holder.INSTANCE;
    }

    private PaintConverter() {
        super();
    }

    @Override
    public Paint convert(ParsedValue<ParsedValue<?, Paint>, Paint> value, Font font) {
        Object obj = (Object) value.getValue();
        if (obj instanceof Paint) {
            return (Paint) obj;
        }
        return value.getValue().convert(font);
    }

    @Override
    public String toString() {
        return "PaintConverter";
    }

    /**
     * Convert [<paint]+ to Paint[]
     */
    public static final class SequenceConverter extends StyleConverter<ParsedValue<?, Paint>[], Paint[]> {

        public static SequenceConverter getInstance() {
            return Holder.SEQUENCE_INSTANCE;
        }

        private SequenceConverter() {
            super();
        }

        @Override
        public Paint[] convert(ParsedValue<ParsedValue<?, Paint>[], Paint[]> value, Font font) {
            ParsedValue<?, Paint>[] values = value.getValue();
            Paint[] paints = new Paint[values.length];
            for (int p = 0; p < values.length; p++) {
                paints[p] = values[p].convert(font);
            }
            return paints;
        }

        @Override
        public String toString() {
            return "Paint.SequenceConverter";
        }
    }


    public static final class LinearGradientConverter extends StyleConverter<ParsedValue[], Paint> {

        public static LinearGradientConverter getInstance() {
            return Holder.LINEAR_GRADIENT_INSTANCE;
        }

        private LinearGradientConverter() {
            super();
        }

        @Override
        public Paint convert(ParsedValue<ParsedValue[], Paint> value, Font font) {
            ParsedValue[] values = value.getValue();
            int v = 0;
            final Size startX = (Size) ((ParsedValue<?, Size>) values[v++]).convert(font);
            final Size startY = (Size) ((ParsedValue<?, Size>) values[v++]).convert(font);
            final Size endX = (Size) ((ParsedValue<?, Size>) values[v++]).convert(font);
            final Size endY = (Size) ((ParsedValue<?, Size>) values[v++]).convert(font);
            boolean proportional = startX.getUnits() == SizeUnits.PERCENT && startX.getUnits() == startY.getUnits() && startX.getUnits() == endX.getUnits() && startX.getUnits() == endY.getUnits();
            final CycleMethod cycleMethod = (CycleMethod) values[v++].convert(font);
            final Stop[] stops = new Stop[values.length - v];
            for (int s = v; s < values.length; s++) {
                stops[s - v] = (Stop) values[s].convert(font);
            }
            return new LinearGradient(startX.pixels(font), startY.pixels(font), endX.pixels(font), endY.pixels(font), proportional, cycleMethod, stops);
        }

        @Override
        public String toString() {
            return "LinearGradientConverter";
        }
    }

    public static final class RadialGradientConverter extends StyleConverter<ParsedValue[], Paint> {

        public static RadialGradientConverter getInstance() {
            return Holder.RADIAL_GRADIENT_INSTANCE;
        }

        private RadialGradientConverter() {
            super();
        }

        @Override
        public Paint convert(ParsedValue<ParsedValue[], Paint> value, Font font) {
            final ParsedValue[] values = value.getValue();
            int v = 0;
            // First four values are for startX, startY, endX, endY
            // and are type ParsedValue<Value<?,Size>,Double>. To figure out
            // proportional, we need to get to the Size. getValue() will
            // return ParsedValue<?,Size>, so getValue().convert(font) will
            // give us the size.
            final Size focusAngle = values[v++] != null ? (Size) ((ParsedValue<?, Size>) values[v-1]).convert(font) : null;
            final Size focusDistance = values[v++] != null ? (Size) ((ParsedValue<?, Size>) values[v-1]).convert(font) : null;
            final Size centerX = values[v++] != null ? (Size) ((ParsedValue<?, Size>) values[v-1]).convert(font) : null;
            final Size centerY = values[v++] != null ? (Size) ((ParsedValue<?, Size>) values[v-1]).convert(font) : null;
            final Size radius = (Size) ((ParsedValue<?, Size>) values[v++]).convert(font);
            boolean proportional = radius.getUnits().equals(SizeUnits.PERCENT);
            boolean unitsAgree = centerX != null ? proportional == centerX.getUnits().equals(SizeUnits.PERCENT) : true;
            unitsAgree = unitsAgree && centerY != null ? proportional == centerY.getUnits().equals(SizeUnits.PERCENT) : true;
            if (!unitsAgree) {
                throw new IllegalArgumentException("units do not agree");
            }
            final CycleMethod cycleMethod = (CycleMethod) values[v++].convert(font);
            final Stop[] stops = new Stop[values.length - v];
            for (int s = v; s < values.length; s++) {
                stops[s - v] = (Stop) values[s].convert(font);
            }
            //If the focus-angle is a percentage, the value is mutiplied
            // by 360, modulo 360.
            double fa = 0;
            if (focusAngle != null) {
                fa = focusAngle.pixels(font);
                if (focusAngle.getUnits().equals(SizeUnits.PERCENT)) {
                    fa = (fa * 360) % 360;
                }
            }
            return new RadialGradient(fa, focusDistance != null ? focusDistance.pixels() : 0, centerX != null ? centerX.pixels() : 0, centerY != null ? centerY.pixels() : 0, radius != null ? radius.pixels() : 1, proportional, cycleMethod, stops);
        }

        @Override
        public String toString() {
            return "RadialGradientConverter";
        }
    }
}