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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

import com.sun.javafx.scene.control.behavior.MenuButtonBehaviorBase;

/**
 * Base class for MenuButtonSkin and SplitMenuButtonSkin. It consists of the
 * label, the arrowButton with its arrow shape, and the popup.
 */
public abstract class MenuButtonSkinBase<C extends MenuButton, B extends MenuButtonBehaviorBase<C>> extends SkinBase<C, B> {

    /***************************************************************************
     *                                                                         *
     * UI Subcomponents                                                        *
     *                                                                         *
     **************************************************************************/

    protected final LabeledImpl label;
    protected final StackPane arrow;
    protected final StackPane arrowButton;
    protected final ContextMenu popup;

    /**
     * If true, the control should behave like a button for mouse button events.
     */
    protected boolean behaveLikeButton = false;

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    public MenuButtonSkinBase(final C control, final B behavior) {
        super(control, behavior);

        if (control.getOnMousePressed() == null) {
            control.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent e) {
                    getBehavior().mousePressed(e, behaveLikeButton);
                }
            });
        }

        if (control.getOnMouseReleased() == null) {
            control.setOnMouseReleased(new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent e) {
                    getBehavior().mouseReleased(e, behaveLikeButton);
                }
            });
        }

        /*
         * Create the objects we will be displaying.
         */
        label = new MenuLabeledImpl(getSkinnable());
        label.setMnemonicParsing(control.isMnemonicParsing());
        label.setLabelFor(control);
        label.setFocusTraversable(true);

        arrow = new StackPane();
        arrow.getStyleClass().setAll("arrow");
        arrow.setMaxWidth(USE_PREF_SIZE);
        arrow.setMaxHeight(USE_PREF_SIZE);

        arrowButton = new StackPane();
        arrowButton.getStyleClass().setAll("arrow-button");
        arrowButton.getChildren().add(arrow);

        popup = new ContextMenu();
        popup.getItems().clear();
        popup.getItems().addAll(getSkinnable().getItems());

        getChildren().clear();
        getChildren().addAll(label, arrowButton);

        requestLayout();
        
        control.getItems().addListener(new ListChangeListener() {
            @Override public void onChanged(Change c) {
                while (c.next()) {
                    popup.getItems().removeAll(c.getRemoved());
                    popup.getItems().addAll(c.getFrom(), c.getAddedSubList());
                }
            }
        });
        
        if (getSkinnable().getScene() != null) {
            addAccelerators();
        }
        control.sceneProperty().addListener(new ChangeListener<Scene>() {
                @Override
                    public void changed(ObservableValue<? extends Scene> scene, Scene oldValue, Scene newValue) {
                    if (getSkinnable().getScene() != null) {
                        addAccelerators();
                    }
                }
            });

//        If setOnAction() is overridden the code below causes the popup to show and hide.
//        control.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
//                @Override public void handle(ActionEvent e) {
//                    if (!popup.isVisible()) {
//                        show();
//                    }
//                    else {
//                        hide();
//                    }
//
//                }
//            });

        // Register listeners
        registerChangeListener(control.showingProperty(), "SHOWING");
        registerChangeListener(control.focusedProperty(), "FOCUSED");
        registerChangeListener(control.mnemonicParsingProperty(), "MNEMONIC_PARSING");
        registerChangeListener(popup.showingProperty(), "POPUP_VISIBLE");
    }

    /***************************************************************************
     *                                                                         *
     * Control change handlers                                                 *
     *                                                                         *
     **************************************************************************/

    private void show() {
        if (!popup.isShowing()) {
            popup.show(getSkinnable(), getSkinnable().getPopupSide(), 0, 0);
            
//            if (getSkinnable().isOpenVertically()) {
//                // FIXME ugly hack - need to work out why we need '12' for
//                // MenuButton/SplitMenuButton, but not for Menus
//                double indent = getSkinnable().getStyleClass().contains("menu") ? 0 : 12;
//                popup.show(getSkinnable(), Side.BOTTOM, indent, 0);
//            } else {
//                popup.show(getSkinnable(), Side.RIGHT, 0, 12);
//            }
        }
    }

    private void hide() {
        if (popup.isShowing()) {
            popup.hide();
//            popup.getAnchor().requestFocus();
        }
    }

    /**
     * Handles changes to properties of the MenuButton.
     */
    @Override protected void handleControlPropertyChanged(String p) {
        super.handleControlPropertyChanged(p);

        if (p == "SHOWING") {
            if (getSkinnable().isShowing()) {
                show();
            } else {
                hide();
            }
        } else if (p == "FOCUSED") {
           // Handle tabbing away from an open MenuButton
           if (!getSkinnable().isFocused() && getSkinnable().isShowing()) {
               hide();
           }
           if (!getSkinnable().isFocused() && popup.isShowing()) {
               hide();
           }
        } else if (p == "POPUP_VISIBLE") {
            if (!popup.isShowing() && getSkinnable().isShowing()) {
                // Popup was dismissed. Maybe user clicked outside or typed ESCAPE.
                // Make sure button is in sync.
                getSkinnable().hide();
            }

            if (popup.isShowing()) {
                Utils.addMnemonics(popup, getSkinnable().getScene());
            }
            else {
                Utils.removeMnemonics(popup, getSkinnable().getScene());
            }


        } else if (p == "MNEMONIC_PARSING") {
            label.setMnemonicParsing(getSkinnable().isMnemonicParsing());
            requestLayout();
        }
    }

    /***************************************************************************
     *                                                                         *
     * Layout                                                                  *
     *                                                                         *
     **************************************************************************/

    @Override protected double computePrefWidth(double height) {
        final Insets padding = getInsets();
        final Insets arrowButtonPadding = arrowButton.getInsets();
        return padding.getLeft()
                + label.prefWidth(height)
                + arrowButtonPadding.getLeft()
                + arrow.prefWidth(height)
                + arrowButtonPadding.getRight()
                + padding.getRight();
    }

    @Override protected double computePrefHeight(double width) {
        final Insets padding = getInsets();
        final Insets arrowButtonPadding = arrowButton.getInsets();
        return padding.getTop()
                + Math.max(label.prefHeight(width), arrowButtonPadding.getTop() + arrow.prefHeight(-1) + arrowButtonPadding.getBottom())
                + padding.getBottom();
    }

    @Override protected double computeMaxWidth(double height) {
        return getSkinnable().prefWidth(height);
    }

    @Override protected double computeMaxHeight(double width) {
        return getSkinnable().prefHeight(width);
    }

    @Override protected void layoutChildren() {
        final Insets padding = getInsets();
        final Insets arrowButtonPadding = arrowButton.getInsets();

        // x, y, w, h are the content area that will hold the label and arrow */
        final double x = padding.getLeft();
        final double y = padding.getTop();
        final double w = getWidth() - (padding.getLeft() + padding.getRight());
        final double h = getHeight() - (padding.getTop() + padding.getBottom());

        final double arrowWidth = arrow.prefWidth(-1);
        final double arrowButtonWidth = arrowButtonPadding.getLeft() + arrowWidth + arrowButtonPadding.getRight();

        label.resizeRelocate(x, y, w - arrowButtonWidth, h);

        arrowButton.resize(arrowButtonWidth, h);
        positionInArea(arrowButton, getWidth() - padding.getRight() - arrowButtonWidth, y, arrowButtonWidth, h,
                /*baseline ignored*/0, HPos.CENTER, VPos.CENTER);

        // size popup to its pref size
//        popup.autosize();
    }

    
    private void addAccelerators() {
        for (final MenuItem menuitem : popup.getItems()) {

            /*
            ** check is there are any accelerators in this menu
            */
            if (menuitem.getAccelerator() != null) {
                if (getSkinnable().getScene().getAccelerators() != null) {
                    
                    Runnable acceleratorRunnable = new Runnable() {
                            public void run() {
                                menuitem.fire();
                            }
                        };
                    getSkinnable().getScene().getAccelerators().put(menuitem.getAccelerator(), acceleratorRunnable);
                }
            }
        }
    }


    private class MenuLabeledImpl extends LabeledImpl {

        MenuButton button;
        public MenuLabeledImpl(MenuButton b) {
            super(b);
            button = b;
            addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
                    @Override public void handle(ActionEvent e) {
                        button.fireEvent(new ActionEvent());
                        e.consume();
                    }
                });
        }
        
        /**
         * Fires a new ActionEvent.
         */
        public void fire() {
            button.fire();
        }
    }
}