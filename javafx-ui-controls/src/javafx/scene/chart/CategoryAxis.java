/*
 * Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved.
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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.WritableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Dimension2D;
import javafx.geometry.Side;
import javafx.util.Duration;

import com.sun.javafx.charts.ChartLayoutAnimator;
import com.sun.javafx.css.StyleableBooleanProperty;
import com.sun.javafx.css.StyleableDoubleProperty;
import com.sun.javafx.css.StyleableProperty;
import com.sun.javafx.css.converters.BooleanConverter;
import com.sun.javafx.css.converters.SizeConverter;
import java.util.Collections;

/**
 * A axis implementation that will works on string categories where each 
 * value as a unique category(tick mark) along the axis.
 */
public final class CategoryAxis extends Axis<String> {

    // -------------- PRIVATE FIELDS -------------------------------------------
    private List<String> allDataCategories = new ArrayList<String>();
    /** This is the gap between one category and the next along this axis */
    private final DoubleProperty firstCategoryPos = new SimpleDoubleProperty(this, "firstCategoryPos", 0);
    private Object currentAnimationID;
    private final ChartLayoutAnimator animator = new ChartLayoutAnimator(this);
    private ListChangeListener<String> itemsListener = new ListChangeListener<String>() {
        @Override public void onChanged(Change<? extends String> c) {
            requestAxisLayout();
        }
    };
    
    // -------------- PUBLIC PROPERTIES ----------------------------------------

    /** The margin between the axis start and the first tick-mark */
    private DoubleProperty startMargin = new StyleableDoubleProperty(5) {
        @Override protected void invalidated() {
            requestAxisLayout();
        }

        @Override public StyleableProperty getStyleableProperty() {
            return StyleableProperties.START_MARGIN;
        }
        
        @Override
        public Object getBean() {
            return CategoryAxis.this;
        }

        @Override
        public String getName() {
            return "startMargin";
        }
    };
    public final double getStartMargin() { return startMargin.getValue(); }
    public final void setStartMargin(double value) { startMargin.setValue(value); }
    public final DoubleProperty startMarginProperty() { return startMargin; }

    /** The margin between the last tick mark and the axis end */
    private DoubleProperty endMargin = new StyleableDoubleProperty(5) {
        @Override protected void invalidated() {
            requestAxisLayout();
        }


        @Override public StyleableProperty getStyleableProperty() {
            return StyleableProperties.END_MARGIN;
        }

        @Override
        public Object getBean() {
            return CategoryAxis.this;
        }

        @Override
        public String getName() {
            return "endMargin";
        }
    };
    public final double getEndMargin() { return endMargin.getValue(); }
    public final void setEndMargin(double value) { endMargin.setValue(value); }
    public final DoubleProperty endMarginProperty() { return endMargin; }

    /** If this is true then half the space between ticks is left at the start
     * and end
     */
    private BooleanProperty gapStartAndEnd = new StyleableBooleanProperty(true) {
        @Override protected void invalidated() {
            requestAxisLayout();
        }


        @Override public StyleableProperty getStyleableProperty() {
            return StyleableProperties.GAP_START_AND_END;
        }
        
        @Override
        public Object getBean() {
            return CategoryAxis.this;
        }

        @Override
        public String getName() {
            return "gapStartAndEnd";
        }
    };
    public final boolean isGapStartAndEnd() { return gapStartAndEnd.getValue(); }
    public final void setGapStartAndEnd(boolean value) { gapStartAndEnd.setValue(value); }
    public final BooleanProperty gapStartAndEndProperty() { return gapStartAndEnd; }

    /**
     * The ordered list of categories plotted on this axis. This is set automatically based on the charts data if
     * autoRanging is true. Duplicate categories may cause odd results.
     */
    private ObjectProperty<ObservableList<String>> categories = new ObjectPropertyBase<ObservableList<String>>() {
        ObservableList<String> old;
        @Override protected void invalidated() {
            final ObservableList<String> newItems = get();
            if (old != newItems) {
                // Add and remove listeners
                if (old != null) old.removeListener(itemsListener);
                if (newItems != null) newItems.addListener(itemsListener);
                old = newItems;
            }
        }

        @Override
        public Object getBean() {
            return CategoryAxis.this;
        }

        @Override
        public String getName() {
            return "categories";
        }
    };
    public final void setCategories(ObservableList<String> value) {
        categories.set(value);
        requestAxisLayout();
    }

    /**
     * Returns a {@link ObservableList} of categories plotted on this axis.
     *
     * @return ObservableList of categories for this axis.
     * @see #categories
     */
    public final ObservableList<String> getCategories() {
        return categories.get();
    }

    /** This is the gap between one category and the next along this axis */
    private final ReadOnlyDoubleWrapper categorySpacing = new ReadOnlyDoubleWrapper(this, "categorySpacing", 1);
    public final double getCategorySpacing() {
        return categorySpacing.get();
    }
    public final ReadOnlyDoubleProperty categorySpacingProperty() {
        return categorySpacing.getReadOnlyProperty();
    }

    // -------------- CONSTRUCTORS -------------------------------------------------------------------------------------

    /**
     * Create a auto-ranging category axis
     */
    public CategoryAxis() { }

    /**
     * Create a category axis with the given categories. This will not auto-range but be fixed with the given categories.
     *
     * @param categories List of the categories for this axis
     */
    public CategoryAxis(ObservableList<String> categories) {
        setAnimated(false);
        setCategories(categories);
        allDataCategories.addAll(categories);
    }

    // -------------- PRIVATE METHODS ----------------------------------------------------------------------------------

    private double calculateNewSpacing(double length, List<String> categories) {
        final Side side = getSide();
        double newCategorySpacing = 1;
        if(side != null && categories != null) {
            double bVal = (isGapStartAndEnd() ? (categories.size()) : (categories.size() - 1));
            // RT-14092 flickering  : check if bVal is 0
            newCategorySpacing = (bVal == 0) ? 1 : (length-getStartMargin()-getEndMargin()) / bVal;
        }
        // if autoranging is off setRange is not called so we update categorySpacing
        if (!isAutoRanging()) categorySpacing.set(newCategorySpacing);
        return newCategorySpacing;
    }

    private double calculateNewFirstPos(double length, double catSpacing) {
        final Side side = getSide();
        double newPos = 1;
        if(side != null) {
            double offset = ((isGapStartAndEnd()) ? (catSpacing / 2) : (0));
            if (side.equals(Side.TOP) || side.equals(Side.BOTTOM)) { // HORIZONTAL
                newPos = 0 + getStartMargin() + offset;
            }  else { // VERTICAL
                newPos = length - getStartMargin() - offset;
            }
        }
        // if autoranging is off setRange is not called so we update first cateogory pos.
        if (!isAutoRanging()) firstCategoryPos.set(newPos);
        return newPos;
    }

    // -------------- PROTECTED METHODS --------------------------------------------------------------------------------

    /**
     * Called to get the current axis range.
     *
     * @return A range object that can be passed to setRange() and calculateTickValues()
     */
    @Override protected Object getRange() {
        return new Object[]{ getCategories(), categorySpacing.get(), firstCategoryPos.get(), getTickLabelRotation() };
    }

    /**
     * Called to set the current axis range to the given range. If isAnimating() is true then this method should
     * animate the range to the new range.
     *
     * @param range A range object returned from autoRange()
     * @param animate If true animate the change in range
     */
    @Override protected void setRange(Object range, boolean animate) {
        Object[] rangeArray = (Object[]) range;
        @SuppressWarnings({"unchecked"}) List<String> categories = (List<String>)rangeArray[0];
        double newCategorySpacing = (Double)rangeArray[1];
        double newFirstCategoryPos = (Double)rangeArray[2];
        double tickLabelRotation = (Double)rangeArray[3];
        setTickLabelRotation(tickLabelRotation);
        setCategories(FXCollections.<String>observableArrayList(categories));
        if (animate) {
            animator.stop(currentAnimationID);
            currentAnimationID = animator.animate(
                new KeyFrame(Duration.ZERO,
                    new KeyValue(firstCategoryPos, firstCategoryPos.get()),
                    new KeyValue(categorySpacing, categorySpacing.get())
                ),
                new KeyFrame(Duration.millis(1000),
                    new KeyValue(firstCategoryPos,newFirstCategoryPos),
                    new KeyValue(categorySpacing,newCategorySpacing)
                )
            );
        } else {
            categorySpacing.set(newCategorySpacing);
            firstCategoryPos.set(newFirstCategoryPos);
        }
    }

    /**
     * This calculates the categories based on the data provided to invalidateRange() method. This must not
     * effect the state of the axis, changing any properties of the axis. Any results of the auto-ranging should be
     * returned in the range object. This will we passed to setRange() if it has been decided to adopt this range for
     * this axis.
     *
     * @param length The length of the axis in screen coordinates
     * @return Range information, this is implementation dependent
     */
    @Override protected Object autoRange(double length) {
        final Side side = getSide();
        final boolean vertical = Side.LEFT.equals(side) || Side.RIGHT.equals(side);
        // TODO check if we can display all categories
        final double newCategorySpacing = calculateNewSpacing(length,allDataCategories);
        final double newFirstPos = calculateNewFirstPos(length, newCategorySpacing);
        double tickLabelRotation = 0;
        double requiredLengthToDisplay = calculateRequiredSize(vertical,tickLabelRotation);
        if (requiredLengthToDisplay > length) {
            // change text to vertical
            tickLabelRotation = 90;
        }
        return new Object[]{allDataCategories, newCategorySpacing, newFirstPos, tickLabelRotation};
    }

    private double calculateRequiredSize(boolean axisVertical, double tickLabelRotation) {
        double requiredLengthToDisplay = Double.MAX_VALUE;
        // Calculate the max space required between categories labels
        double maxReqTickGap = 0;
        double last = 0;
        boolean first = true;
        for (String category: allDataCategories) {
            Dimension2D textSize = measureTickMarkSize(category, tickLabelRotation);
            double size = (axisVertical || (tickLabelRotation != 0)) ? textSize.getHeight() : textSize.getWidth();
            // TODO better handle calculations for rotated text, overlapping text etc
            if (first) {
                first = false;
                last = size/2;
            } else {
                maxReqTickGap = Math.max(maxReqTickGap, last + 6 + (size/2) );
            }
        }
        return getStartMargin() + maxReqTickGap*allDataCategories.size() + getEndMargin();
    }

    /**
     * Calculate a list of all the data values for each tick mark in range
     *
     * @param length The length of the axis in display units
     * @return A list of tick marks that fit along the axis if it was the given length
     */
    @Override protected List<String> calculateTickValues(double length, Object range) {
        Object[] rangeArray = (Object[]) range;
        //noinspection unchecked
        return (List<String>)rangeArray[0];
    }

    /**
     * Get the string label name for a tick mark with the given value
     *
     * @param value The value to format into a tick label string
     * @return A formatted string for the given value
     */
    @Override protected String getTickMarkLabel(String value) {
        // TODO use formatter
        return value;
    }

    /**
     * Measure the size of the label for given tick mark value. This uses the font that is set for the tick marks
     *
     * @param value tick mark value
     * @param range range to use during calculations
     * @return size of tick mark label for given value
     */
    @Override protected Dimension2D measureTickMarkSize(String value, Object range) {
        final Object[] rangeArray = (Object[]) range;
        final double tickLabelRotation = (Double)rangeArray[3];
        return measureTickMarkSize(value,tickLabelRotation);
    }

    // -------------- METHODS ------------------------------------------------------------------------------------------

    /**
     * Called when data has changed and the range may not be valid any more. This is only called by the chart if
     * isAutoRanging() returns true. If we are auto ranging it will cause layout to be requested and auto ranging to
     * happen on next layout pass.
     *
     * @param data The current set of all data that needs to be plotted on this axis
     */
    @Override public void invalidateRange(List<String> data) {
        super.invalidateRange(data);
        // Create unique set of category names
        LinkedHashSet<String> categoryNames = new LinkedHashSet<String>();
        categoryNames.addAll(data);
        allDataCategories.clear();
        allDataCategories.addAll(categoryNames);
    }

    /**
     * Get the display position along this axis for a given value
     *
     * @param value The data value to work out display position for
     * @return display position or Double.NaN if zero is not in current range;
     */
    @Override public double getDisplayPosition(String value) {
        // find index of value
        if (Side.TOP.equals(getSide()) || Side.BOTTOM.equals(getSide())) { // HORIZONTAL
            return firstCategoryPos.get() + getCategories().indexOf("" + value) * categorySpacing.get();
        } else {
            return firstCategoryPos.get() + getCategories().indexOf("" + value) * categorySpacing.get() * -1;
        }
    }

    /**
     * Get the data value for the given display position on this axis. If the axis
     * is a CategoryAxis this will be the nearest value.
     *
     * @param  displayPosition A pixel position on this axis
     * @return the nearest data value to the given pixel position or
     *         null if not on axis;
     */
    @Override public String getValueForDisplay(double displayPosition) {
        if (getSide().equals(Side.TOP) || getSide().equals(Side.BOTTOM)) { // HORIZONTAL
            if (displayPosition < 0 || displayPosition > getHeight()) return null;
            double d = (displayPosition - firstCategoryPos.get()) /   categorySpacing.get();
            return toRealValue(d);
        } else { // VERTICAL
            if (displayPosition < 0 || displayPosition > getWidth()) return null;
            double d = (displayPosition - firstCategoryPos.get()) /   (categorySpacing.get() * -1);
            return toRealValue(d);
        }
    }

    /**
     * Checks if the given value is plottable on this axis
     *
     * @param value The value to check if its on axis
     * @return true if the given value is plottable on this axis
     */
    @Override public boolean isValueOnAxis(String value) {
        return getCategories().indexOf("" + value) != -1;
    }

    /**
     * All axis values must be representable by some numeric value. This gets the numeric value for a given data value.
     *
     * @param value The data value to convert
     * @return Numeric value for the given data value
     */
    @Override public double toNumericValue(String value) {
        return getCategories().indexOf(value);
    }

    /**
     * All axis values must be representable by some numeric value. This gets the data value for a given numeric value.
     *
     * @param value The numeric value to convert
     * @return Data value for given numeric value
     */
    @Override public String toRealValue(double value) {
        int index = (int)Math.round(value);
        List<String> categories = getCategories();
        if (index >= 0 && index < categories.size()) {
            return getCategories().get(index);
        } else {
            return null;
        }
    }

    /**
     * Get the display position of the zero line along this axis. As there is no concept of zero on a CategoryAxis
     * this is always Double.NaN.
     *
     * @return always Double.NaN for CategoryAxis
     */
    @Override public double getZeroPosition() {
        return Double.NaN;
    }

    // -------------- STYLESHEET HANDLING ------------------------------------------------------------------------------

    /** @treatasprivate implementation detail */
    private static class StyleableProperties {
        private static final StyleableProperty<CategoryAxis,Number> START_MARGIN =
            new StyleableProperty<CategoryAxis,Number>("-fx-start-margin",
                SizeConverter.getInstance(), 5.0) {

            @Override
            public boolean isSettable(CategoryAxis n) {
                return n.startMargin == null || !n.startMargin.isBound();
            }

            @Override
            public WritableValue<Number> getWritableValue(CategoryAxis n) {
                return n.startMarginProperty();
            }
        };
        
        private static final StyleableProperty<CategoryAxis,Number> END_MARGIN =
            new StyleableProperty<CategoryAxis,Number>("-fx-end-margin",
                SizeConverter.getInstance(), 5.0) {

            @Override
            public boolean isSettable(CategoryAxis n) {
                return n.endMargin == null || !n.endMargin.isBound();
            }

            @Override
            public WritableValue<Number> getWritableValue(CategoryAxis n) {
                return n.endMarginProperty();
            }
        };
        
        private static final StyleableProperty<CategoryAxis,Boolean> GAP_START_AND_END =
            new StyleableProperty<CategoryAxis,Boolean>("-fx-gap-start-and-end",
                BooleanConverter.getInstance(), Boolean.TRUE) {

            @Override
            public boolean isSettable(CategoryAxis n) {
                return n.gapStartAndEnd == null || !n.gapStartAndEnd.isBound();
            }

            @Override
            public WritableValue<Boolean> getWritableValue(CategoryAxis n) {
                return n.gapStartAndEndProperty();
            }
        };

        private static final List<StyleableProperty> STYLEABLES;
        static {
        final List<StyleableProperty> styleables =
            new ArrayList<StyleableProperty>(Axis.impl_CSS_STYLEABLES());
            Collections.addAll(styleables,
                START_MARGIN,
                END_MARGIN,
                GAP_START_AND_END
            );
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    /**
     * @treatasprivate implementation detail
     * @deprecated This is an internal API that is not intended for use and will be removed in the next version
     */
    @Deprecated
    public static List<StyleableProperty> impl_CSS_STYLEABLES() {
        return CategoryAxis.StyleableProperties.STYLEABLES;
    }

}
