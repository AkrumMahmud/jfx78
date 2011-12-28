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

package com.sun.javafx.scene.control.skin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.StackPane;
import javafx.scene.input.ScrollEvent;

import com.sun.javafx.Utils;
import com.sun.javafx.scene.control.behavior.ScrollBarBehavior;

public class ScrollBarSkin extends SkinBase<ScrollBar, ScrollBarBehavior> {

    /***************************************************************************
     *                                                                         *
     * UI Subcomponents                                                        *
     *                                                                         *
     **************************************************************************/

    public static int DEFAULT_LENGTH = 100;
    public static int DEFAULT_WIDTH = 20;

    private StackPane thumb;
    private StackPane track;
    private EndButton incButton;
    private EndButton decButton;

    private double trackLength;
    private double thumbLength;

    private double preDragThumbPos;
    private Point2D dragStart; // in the track's coord system

    private double trackPos;

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    public ScrollBarSkin(ScrollBar scrollbar) {
        super(scrollbar, new ScrollBarBehavior(scrollbar));
        initialize();
        requestLayout();
        // Register listeners
        registerChangeListener(scrollbar.minProperty(), "MIN");
        registerChangeListener(scrollbar.maxProperty(), "MAX");
        registerChangeListener(scrollbar.valueProperty(), "VALUE");
        registerChangeListener(scrollbar.orientationProperty(), "ORIENTATION");
        registerChangeListener(scrollbar.visibleAmountProperty(), "VISIBLE_AMOUNT");
    }

    /**
     * Initializes the ScrollBarSkin. Creates the scene and sets up all the
     * bindings for the group.
     */
    private void initialize() {
        decButton = new EndButton("decrement-button", "decrement-arrow");

        track = new StackPane();
        track.getStyleClass().setAll("track");

        thumb = new StackPane();
        thumb.getStyleClass().setAll("thumb");

        incButton = new EndButton("increment-button", "increment-arrow");

        ObservableList<Node> contentObservableList = FXCollections.<Node>observableArrayList();
        contentObservableList.add(incButton);
        contentObservableList.add(decButton);
        contentObservableList.add(track);
        contentObservableList.add(thumb);
        getChildren().clear();
        getChildren().addAll(contentObservableList);

        incButton.setOnMousePressed(new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override public void handle(javafx.scene.input.MouseEvent me) {
                /*
                ** if the tracklenght isn't greater than do nothing....
                */
                if (!thumb.isVisible() || trackLength > thumbLength) {
                    getBehavior().incButtonPressed(me);
                }
                me.consume();
            }
        });
        incButton.setOnMouseReleased(new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override public void handle(javafx.scene.input.MouseEvent me) {
                /*
                ** if the tracklenght isn't greater than do nothing....
                */
                if (!thumb.isVisible() || trackLength > thumbLength) {
                    getBehavior().incButtonReleased(me);
                }
                me.consume();
            }
        });

        decButton.setOnMousePressed(new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override public void handle(javafx.scene.input.MouseEvent me) {
                /*
                ** if the tracklenght isn't greater than do nothing....
                */
                if (!thumb.isVisible() || trackLength > thumbLength) {
                    getBehavior().decButtonPressed(me);
                }
                me.consume();
            }
        });
        decButton.setOnMouseReleased(new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override public void handle(javafx.scene.input.MouseEvent me) {
                /*
                ** if the tracklenght isn't greater than do nothing....
                */
                if (!thumb.isVisible() || trackLength > thumbLength) {
                    getBehavior().decButtonReleased(me);
                }
                me.consume();
            }
        });

        track.setOnMousePressed( new EventHandler<javafx.scene.input.MouseEvent>() {
           @Override public void handle(javafx.scene.input.MouseEvent me) {
               if (!thumb.isPressed()) {
                   if (getSkinnable().getOrientation() == Orientation.VERTICAL) {
                       if (trackLength != 0) {
                           getBehavior().trackPress(me, me.getY() / trackLength);
                           me.consume();
                       }
                   } else {
                       if (trackLength != 0) {
                           getBehavior().trackPress(me, me.getX() / trackLength);
                           me.consume();
                       }
                   }
               }
           }
        });

        track.setOnMouseReleased( new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override public void handle(javafx.scene.input.MouseEvent me) {
                getBehavior().trackRelease(me, 0.0f);
                me.consume();
            }
        });

        thumb.setOnMousePressed(new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override public void handle(javafx.scene.input.MouseEvent me) {
                /*
                ** if max isn't greater than min then there is nothing to do here
                */
                if (getSkinnable().getMax() > getSkinnable().getMin()) {
                    dragStart = thumb.localToParent(me.getX(), me.getY());
                    double clampedValue = Utils.clamp(getSkinnable().getMin(), getSkinnable().getValue(), getSkinnable().getMax());
                    preDragThumbPos = (clampedValue - getSkinnable().getMin()) / (getSkinnable().getMax() - getSkinnable().getMin());
                    me.consume();
                }
            }
        });


        thumb.setOnMouseDragged(new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override public void handle(javafx.scene.input.MouseEvent me) {
                /*
                ** if max isn't greater than min then there is nothing to do here
                */
                if (getSkinnable().getMax() > getSkinnable().getMin()) {
                    /*
                    ** if the tracklength isn't greater then do nothing....
                    */
                    if (trackLength > thumbLength) {
                        Point2D cur = thumb.localToParent(me.getX(), me.getY());
                        double dragPos = getSkinnable().getOrientation() == Orientation.VERTICAL ? cur.getY() - dragStart.getY(): cur.getX() - dragStart.getX();
                        getBehavior().thumbDragged(me, preDragThumbPos + dragPos / (trackLength - thumbLength));
                    }

                    me.consume();
                }
            }
        });

        setOnScroll(new EventHandler<javafx.scene.input.ScrollEvent>() {
            @Override public void handle(ScrollEvent event) {
                /*
                ** if the tracklength isn't greater then do nothing....
                */
                if (trackLength > thumbLength) {

                    double dx = event.getDeltaX();
                    double dy = event.getDeltaY();

                    /*
                    ** in 2.0 a horizontal scrollbar would scroll on a vertical
                    ** drag on a tracker-pad. We need to keep this behavior.
                    */
                    dx = (Math.abs(dx) < Math.abs(dy) ? dy : dx);

                    /*
                    ** we only consume an event that we've used.
                    */
                    ScrollBar sb = (ScrollBar) getSkinnable();

                    double delta = (getSkinnable().getOrientation() == Orientation.VERTICAL ? dy : dx);

                    if (delta > 0.0 && sb.getValue() > sb.getMin()) {
                        sb.decrement();
                        event.consume();
                    } else if (delta < 0.0 && sb.getValue() < sb.getMax()) {
                        sb.increment();
                        event.consume();
                    }
                }
            }
        });
    }



    @Override protected void handleControlPropertyChanged(String p) {
        super.handleControlPropertyChanged(p);
        if (p == "ORIENTATION") {
            requestLayout();
        } else if (p == "MIN" || p == "MAX" || p == "VALUE" || p == "VISIBLE_AMOUNT") {
            positionThumb();
            requestLayout();
        }
    }

    /***************************************************************************
     *                                                                         *
     * Layout                                                                  *
     *                                                                         *
     **************************************************************************/

    /*
     * Gets the breadth of the scrollbar. The "breadth" is the distance
     * across the scrollbar, i.e. if vertical the width, otherwise the height.
     * This is determined by the greater of the breadths of the end-buttons.
     */
    double getBreadth() {
        if (getSkinnable().getOrientation() == Orientation.VERTICAL) {
            return Math.max(decButton.prefWidth(-1)+getInsets().getLeft()+getInsets().getRight(), incButton.prefWidth(-1)+getInsets().getLeft()+getInsets().getRight());
        } else {
            return Math.max(decButton.prefHeight(-1)+getInsets().getTop()+getInsets().getBottom(), incButton.prefHeight(-1)+getInsets().getTop()+getInsets().getBottom());
        }
    }

    double minThumbLength() {
        return 1.5f * getBreadth();
    }

    double minTrackLength() {
        return 2.0f * getBreadth();
    }

    /*
     * Minimum length is the length of the end buttons plus twice the
     * minimum thumb length, which should be enough for a reasonably-sized
     * track. Minimum breadth is determined by the breadths of the
     * end buttons.
     */
    @Override protected double computeMinWidth(double height) {
        if (getSkinnable().getOrientation() == Orientation.VERTICAL) {
            return getBreadth();
        } else {
            return decButton.minWidth(-1) + incButton.minWidth(-1) + minTrackLength()+getInsets().getLeft()+getInsets().getRight();
        }
    }

    @Override protected double computeMinHeight(double width) {
        if (getSkinnable().getOrientation() == Orientation.VERTICAL) {
            return decButton.minHeight(-1) + incButton.minHeight(-1) + minTrackLength()+getInsets().getTop()+getInsets().getBottom();
        } else {
            return getBreadth();
        }
    }

    /*
     * Preferred size. The breadth is determined by the breadth of
     * the end buttons. The length is a constant default length.
     * Usually applications or other components will either set a
     * specific length using LayoutInfo or will stretch the length
     * of the scrollbar to fit a container.
     */
    @Override protected double computePrefWidth(double height) {
        return getSkinnable().getOrientation() == Orientation.VERTICAL ? getBreadth() : DEFAULT_LENGTH+getInsets().getLeft()+getInsets().getRight();
    }

    @Override protected double computePrefHeight(double height) {
        return getSkinnable().getOrientation() == Orientation.VERTICAL ? DEFAULT_LENGTH+getInsets().getTop()+getInsets().getBottom() : getBreadth();
    }

    @Override protected double computeMaxWidth(double height) {
        return getSkinnable().getOrientation() == Orientation.VERTICAL ? getSkinnable().prefWidth(-1) : Double.MAX_VALUE;
    }

    @Override protected double computeMaxHeight(double width) {
        return getSkinnable().getOrientation() == Orientation.VERTICAL ? Double.MAX_VALUE : getSkinnable().prefHeight(-1);
    }

    /**
     * Called when ever either min, max or value changes, so thumb's layoutX, Y is recomputed.
     */
    void positionThumb() {
        ScrollBar s = getSkinnable();
        double clampedValue = Utils.clamp(s.getMin(), s.getValue(), s.getMax());
        trackPos = (s.getMax() - s.getMin() > 0) ? ((trackLength - thumbLength) * (clampedValue - s.getMin()) / (s.getMax() - s.getMin())) : (0.0F);

        if (s.getOrientation() == Orientation.VERTICAL) {
            trackPos += decButton.prefHeight(-1);
        } else {
            trackPos += decButton.prefWidth(-1);
        }

        thumb.setTranslateX( s.getOrientation() == Orientation.VERTICAL ? getInsets().getLeft() : trackPos + getInsets().getLeft());
        thumb.setTranslateY( s.getOrientation() == Orientation.VERTICAL ? trackPos + getInsets().getTop() : getInsets().getTop());
    }

    @Override protected void layoutChildren() {
        // compute x,y,w,h of content area
        double x = getInsets().getLeft();
        double y = getInsets().getTop();
        double w = getWidth() - (getInsets().getLeft() + getInsets().getRight());
        double h = getHeight() - (getInsets().getTop() + getInsets().getBottom());

        /**
         * Compute the percentage length of thumb as (visibleAmount/range)
         * if max isn't greater than min then there is nothing to do here
         */
        double visiblePortion;
        if (getSkinnable().getMax() > getSkinnable().getMin()) {
            visiblePortion = getSkinnable().getVisibleAmount()/(getSkinnable().getMax() - getSkinnable().getMin());
        }
        else {
            visiblePortion = 1.0;
        }
        

        if (getSkinnable().getOrientation() == Orientation.VERTICAL) {
            double decHeight = decButton.prefHeight(-1);
            double incHeight = incButton.prefHeight(-1);

            trackLength = h - (decHeight + incHeight);
            thumbLength = Utils.clamp(minThumbLength(), (trackLength * visiblePortion), trackLength);

            decButton.resizeRelocate(x, y, w, decHeight+3);
            incButton.resizeRelocate(x, y + h - (incHeight+4), w, incHeight+4);
            track.resizeRelocate(x, y + decHeight, w, trackLength);
            thumb.resize(x >= 0 ? w : w + x, thumbLength); // Account for negative padding (see also RT-10719)
            positionThumb();
        } else {
            double decWidth = decButton.prefWidth(-1);
            double incWidth = incButton.prefWidth(-1);

            trackLength = w - (decWidth + incWidth);
            thumbLength = Utils.clamp(minThumbLength(), (trackLength * visiblePortion), trackLength);

            decButton.resizeRelocate(x, y, decWidth+3, h);
            incButton.resizeRelocate(x + w - incWidth-4, y, incWidth+4, h);
            track.resizeRelocate(x + decWidth, y, trackLength, h);
            thumb.resize(thumbLength, y >= 0 ? h : h + y); // Account for negative padding (see also RT-10719)
            positionThumb();
        }

        // things should be invisible only when well below minimum length
        if (getSkinnable().getOrientation() == Orientation.VERTICAL && h >= (computeMinHeight(-1) - (getInsets().getTop()+getInsets().getBottom())) ||
            getSkinnable().getOrientation() == Orientation.HORIZONTAL && w >= (computeMinWidth(-1) - (getInsets().getLeft()+getInsets().getRight()))) {
            track.setVisible(true);
            thumb.setVisible(true);
            incButton.setVisible(true);
            decButton.setVisible(true);
        }
        else {
            track.setVisible(false);
            thumb.setVisible(false);

            /*
            ** once the space is big enough for one button we 
            ** can look at drawing
            */
            if (h >= decButton.computeMinWidth(-1)) {
                decButton.setVisible(true);
            }
            else {
                decButton.setVisible(false);
            }
            if (h >= incButton.computeMinWidth(-1)) {
                incButton.setVisible(true);
            }
            else {
                incButton.setVisible(false);
            }

        }
    }
}

class EndButton extends StackPane {

    private StackPane arrow;

    public EndButton(String styleClass, String arrowStyleClass) {
        getStyleClass().setAll(styleClass);
        arrow = new StackPane();
        arrow.getStyleClass().setAll(arrowStyleClass);
        getChildren().clear();
        getChildren().addAll(arrow);
        requestLayout();
    }

    @Override protected void layoutChildren() {
        double aw = arrow.prefWidth(-1);
        double ah = arrow.prefHeight(-1);

        double Ypos = (getHeight() - (getInsets().getTop()+getInsets().getBottom() + ah))/2;
        double Xpos = (getWidth() - (getInsets().getLeft()+getInsets().getRight() + aw))/2;

        arrow.resizeRelocate(Xpos+getInsets().getLeft(), Ypos+getInsets().getBottom(), aw, ah);
    }

    @Override protected double computeMinHeight(double width) {
        return prefHeight(-1);
    }

    @Override protected double computeMinWidth(double height) {
        return prefWidth(-1);
    }
}