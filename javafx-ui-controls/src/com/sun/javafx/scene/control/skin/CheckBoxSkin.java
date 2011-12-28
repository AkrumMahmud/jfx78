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

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.StackPane;

import com.sun.javafx.scene.control.behavior.ButtonBehavior;

/**
 * Skin for tri state selection Control.
 */
public class CheckBoxSkin extends LabeledSkinBase<CheckBox, ButtonBehavior<CheckBox>> {

    private StackPane box;
    private StackPane innerbox;

    public CheckBoxSkin(CheckBox checkbox) {
        super(checkbox, new ButtonBehavior<CheckBox>(checkbox));

        box = new StackPane();
        box.getStyleClass().setAll("box");
        innerbox = new StackPane();
        innerbox.getStyleClass().setAll("mark");
        box.getChildren().add(innerbox);
        updateChildren();
    }

    @Override protected void updateChildren() {
        super.updateChildren();
        getChildren().add(box);
    }

    @Override protected double computePrefWidth(double height) {
        return super.computePrefWidth(height) + snapSize(box.prefWidth(-1));
    }

    @Override protected double computePrefHeight(double width) {
        return Math.max(super.computePrefHeight(width - box.prefWidth(-1)),
                        getInsets().getTop() + box.prefHeight(-1) + getInsets().getBottom());
    }


    @Override protected void layoutChildren() {
        Insets padding = getInsets();

        final double w = getWidth() - padding.getLeft() - padding.getRight();
        final double h = getHeight() - padding.getTop() - padding.getBottom();
        final double boxWidth = box.prefWidth(-1);
        final double boxHeight = box.prefHeight(-1);
        final double labelWidth = Math.min(prefWidth(-1) - boxWidth, w - snapSize(boxWidth));
        final double labelHeight = Math.min(prefHeight(labelWidth), h);
        final double maxHeight = Math.max(boxHeight, labelHeight);
        final double x = Utils.computeXOffset(w, labelWidth + boxWidth, getSkinnable().getAlignment().getHpos()) + padding.getLeft();
        final double y = Utils.computeYOffset(h, maxHeight, getSkinnable().getAlignment().getVpos()) + padding.getTop();

        layoutLabelInArea(x + boxWidth, y, labelWidth, maxHeight, Pos.CENTER_LEFT);
        box.resize(snapSize(boxWidth), snapSize(boxHeight));
        positionInArea(box, x, y, boxWidth, maxHeight, getBaselineOffset(), HPos.CENTER, VPos.CENTER);
    }
}