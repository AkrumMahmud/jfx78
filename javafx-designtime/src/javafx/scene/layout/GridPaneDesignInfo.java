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

package javafx.scene.layout;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;

public class GridPaneDesignInfo extends PaneDesignInfo {
    public GridPaneDesignInfo() {
        super(GridPane.class);
    }

    protected GridPaneDesignInfo(Class type) {
        super(type);
    }

    public int getRowCount(GridPane pane) {
        int numRows = pane.getRowConstraints().size();
        for (int i = 0; i < pane.getChildren().size(); i++) {
            Node child = pane.getChildren().get(i);
            if (child.isManaged()) {
                int rowIndex = GridPane.getNodeRowIndex(child);
                int rowEnd = GridPane.getNodeRowEnd(child);
                numRows = Math.max(numRows, (rowEnd != GridPane.REMAINING? rowEnd : rowIndex) + 1);
            }
        }
        return numRows;
    }

    public int getColumnCount(GridPane pane) {
        int numColumns = pane.getColumnConstraints().size();
        for (int i = 0; i < pane.getChildren().size(); i++) {
            Node child = pane.getChildren().get(i);
            if (child.isManaged()) {
                int columnIndex = GridPane.getNodeColumnIndex(child);
                int columnEnd = GridPane.getNodeColumnEnd(child);
                numColumns = Math.max(numColumns, (columnEnd != GridPane.REMAINING? columnEnd : columnIndex) + 1);
            }
        }
        return numColumns;
    }

    public Bounds getCellBounds(GridPane pane, int columnIndex, int rowIndex) {
        final double snaphgap = pane.snapSpace(pane.getHgap());
        final double snapvgap = pane.snapSpace(pane.getVgap());
        final double top = pane.snapSpace(pane.getInsets().getTop());
        final double left = pane.snapSpace(pane.getInsets().getLeft());

        // Compute row height
        double[] rowHeights = pane.getRowHeights();
        double minY = top;
        double height = rowHeights[rowIndex];

        for (int j = 0; j < rowIndex; j++) {
            minY += rowHeights[j] + snapvgap;
        }

        // Compute column width
        double[] columnWidths = pane.getColumnWidths();
        double minX = left;
        double width = columnWidths[columnIndex];

        for (int j = 0; j < columnIndex; j++) {
            minX += columnWidths[j] + snaphgap;
        }

        return new BoundingBox(minX, minY, width, height);
    }
}