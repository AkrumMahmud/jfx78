/* 
 * Copyright (c) 2011, 2013, Oracle and/or its affiliates. All rights reserved.
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

package javafx.scene.chart;

/**
Builder class for javafx.scene.chart.NumberAxis
@see javafx.scene.chart.NumberAxis
@deprecated This class is deprecated and will be removed in the next version
* @since JavaFX 2.0
*/
@javax.annotation.Generated("Generated by javafx.builder.processor.BuilderProcessor")
@Deprecated
public final class NumberAxisBuilder extends javafx.scene.chart.ValueAxisBuilder<java.lang.Number, javafx.scene.chart.NumberAxisBuilder> {
    protected NumberAxisBuilder() {
    }
    
    /** Creates a new instance of NumberAxisBuilder. */
    @SuppressWarnings({"deprecation", "rawtypes", "unchecked"})
    public static javafx.scene.chart.NumberAxisBuilder create() {
        return new javafx.scene.chart.NumberAxisBuilder();
    }
    
    private int __set;
    public void applyTo(javafx.scene.chart.NumberAxis x) {
        super.applyTo(x);
        int set = __set;
        if ((set & (1 << 0)) != 0) x.setForceZeroInRange(this.forceZeroInRange);
        if ((set & (1 << 1)) != 0) x.setTickUnit(this.tickUnit);
    }
    
    private boolean forceZeroInRange;
    /**
    Set the value of the {@link javafx.scene.chart.NumberAxis#isForceZeroInRange() forceZeroInRange} property for the instance constructed by this builder.
    */
    public javafx.scene.chart.NumberAxisBuilder forceZeroInRange(boolean x) {
        this.forceZeroInRange = x;
        __set |= 1 << 0;
        return this;
    }
    
    private double tickUnit;
    /**
    Set the value of the {@link javafx.scene.chart.NumberAxis#getTickUnit() tickUnit} property for the instance constructed by this builder.
    */
    public javafx.scene.chart.NumberAxisBuilder tickUnit(double x) {
        this.tickUnit = x;
        __set |= 1 << 1;
        return this;
    }
    
    /**
    Make an instance of {@link javafx.scene.chart.NumberAxis} based on the properties set on this builder.
    */
    public javafx.scene.chart.NumberAxis build() {
        javafx.scene.chart.NumberAxis x = new javafx.scene.chart.NumberAxis();
        applyTo(x);
        return x;
    }
}
