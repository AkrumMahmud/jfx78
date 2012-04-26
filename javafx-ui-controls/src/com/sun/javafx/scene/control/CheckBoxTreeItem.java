/*
 * Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved.
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
package com.sun.javafx.scene.control;

import com.sun.javafx.scene.control.cell.CheckBoxCellFactory;
import com.sun.javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TreeItem;

/**
 * TreeItem subclass that adds support for being in selected, unselected, and
 * indeterminate states. This is useful when used in conjunction with a TreeView
 * which has a {@link CheckBoxCellFactory} installed.
 * 
 * <p>A CheckBoxTreeItem can be {@link #independentProperty() independent} or 
 * dependent. By default, CheckBoxTreeItem instances are dependent, which means 
 * that any changes to the selection state of a TreeItem will have an impact on 
 * parent and children CheckBoxTreeItem instances. 
 * 
 * <p>A simple example of using the CheckBoxTreeItem class, in conjunction with 
 * {@link CheckBoxCellFactory} or {@link CheckBoxTreeCell} classes is shown 
 * below:
 * 
 * <pre><code>
 * // create the tree model
 * CheckBoxTreeItem&lt;String&gt; jonathanGiles = new CheckBoxTreeItem&lt;String&gt;("Jonathan");
 * CheckBoxTreeItem&lt;String&gt; juliaGiles = new CheckBoxTreeItem&lt;String&gt;("Julia");
 * CheckBoxTreeItem&lt;String&gt; mattGiles = new CheckBoxTreeItem&lt;String&gt;("Matt");
 * CheckBoxTreeItem&lt;String&gt; sueGiles = new CheckBoxTreeItem&lt;String&gt;("Sue");
 * CheckBoxTreeItem&lt;String&gt; ianGiles = new CheckBoxTreeItem&lt;String&gt;("Ian");
 * 
 * CheckBoxTreeItem&lt;String&gt; gilesFamily = new CheckBoxTreeItem&lt;String&gt;("Giles Family");
 * gilesFamily.setExpanded(true);
 * gilesFamily.getChildren().addAll(jonathanGiles, juliaGiles, mattGiles, sueGiles, ianGiles);
 * 
 * // create the treeView
 * final TreeView&lt;String&gt; treeView = new TreeView&lt;String&gt;();
 * treeView.setRoot(gilesFamily);
 *       
 * // set the cell factory
 * treeView.setCellFactory(CheckBoxCellFactory.&lt;String&gt;forTreeView());</code></pre>
 *
 * @see CheckBoxTreeCell
 * @see CheckBoxCellFactory
 * @see TreeItem
 * @see CheckBox
 */
// TODO the TreeModificationEvent doesn't actually bubble in the same way as
// TreeItem - it just looks that way as the 'bubbling' is done via changing the
// state of all parent items.
public class CheckBoxTreeItem<T> extends TreeItem<T> {
    
    /**
     * An EventType used when the CheckBoxTreeItem selection / indeterminate
     * state changes.
     * 
     * @param <T> The type of the value contained within the TreeItem.
     */
    @SuppressWarnings("unchecked")
    public static <T> EventType<TreeModificationEvent<T>> checkBoxSelectionChangedEvent() {
        return (EventType<TreeModificationEvent<T>>) CHECK_BOX_SELECTION_CHANGED_EVENT;
    }
    private static final EventType<?> CHECK_BOX_SELECTION_CHANGED_EVENT
            = new EventType<TreeModificationEvent<Object>>("checkBoxSelectionChangedEvent");

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/
    
    /**
     * Creates a CheckBoxTreeItem with the value property set to the provided 
     * object.
     * 
     * @param value The object to be stored as the value of this TreeItem.
     */
    public CheckBoxTreeItem(T value) {
        this(value, null, false);
    }

    /**
     * Creates a CheckBoxTreeItem with the value property set to the provided 
     * object, and the graphic set to the provided Node.
     * 
     * @param value The object to be stored as the value of this CheckBoxTreeItem.
     * @param graphic The Node to show in the TreeView next to this CheckBoxTreeItem.
     */
    public CheckBoxTreeItem(T value, Node graphic) {
        this(value, graphic, false);
    }
    
    /**
     * Creates a CheckBoxTreeItem with the value property set to the provided 
     * object, the graphic set to the provided Node, and the initial state
     * of the {@link #selectedProperty()} set to the provided boolean value.
     * 
     * @param value The object to be stored as the value of this CheckBoxTreeItem.
     * @param graphic The Node to show in the TreeView next to this CheckBoxTreeItem.
     * @param selected The initial value of the 
     *            {@link #selectedProperty() selected} property.
     */
    public CheckBoxTreeItem(T value, Node graphic, boolean selected) {
        this(value, graphic, selected, false);
    }

    /**
     * Creates a CheckBoxTreeItem with the value property set to the provided 
     * object, the graphic set to the provided Node, the initial state
     * of the {@link #selectedProperty()} set to the provided boolean value, and
     * the initial state of the {@link #independentProperty() independent} 
     * property to the provided boolean value.
     * 
     * @param value The object to be stored as the value of this CheckBoxTreeItem.
     * @param graphic The Node to show in the TreeView next to this CheckBoxTreeItem.
     * @param selected The initial value of the 
     *            {@link #selectedProperty() selected} property.
     * @param independent The initial value of the 
     *            {@link #independentProperty() independent} property
     */
    public CheckBoxTreeItem(T value, Node graphic, boolean selected, boolean independent) {
        super(value, graphic);
        setSelected(selected);
        setIndependent(independent);
        
        selectedProperty().addListener(stateChangeListener);
        indeterminateProperty().addListener(stateChangeListener);
    }

    
    
    /***************************************************************************
     *                                                                         *
     * Callbacks                                                               *
     *                                                                         *
     **************************************************************************/   
    private final ChangeListener<Boolean> stateChangeListener = new ChangeListener<Boolean>() {
        @Override public void changed(ObservableValue<? extends Boolean> ov, Boolean oldVal, Boolean newVal) {
            updateState();
        }
    };
    
    
    /***************************************************************************
     *                                                                         *
     * Properties                                                              *
     *                                                                         *
     **************************************************************************/ 
    
    // --- Selected
    private final BooleanProperty selected = new SimpleBooleanProperty(this, "selected", false) {
        @Override protected void invalidated() {
            super.invalidated();
            fireEvent(CheckBoxTreeItem.this, true);
        }
    };
    /** Sets the selected state of this CheckBoxTreeItem. */
    public final void setSelected(Boolean value) { selectedProperty().setValue(value); }
    /** Returns the selected state of this CheckBoxTreeItem. */
    public final Boolean isSelected() { return selected == null ? false : selected.getValue(); }
    /** A {@link BooleanProperty} used to represent the selected state of this CheckBoxTreeItem. */
    public final BooleanProperty selectedProperty() { return selected; }
    
    
    // --- Indeterminate
    private final BooleanProperty indeterminate = new SimpleBooleanProperty(this, "indeterminate", false) {
        @Override protected void invalidated() {
            super.invalidated();
            fireEvent(CheckBoxTreeItem.this, false);
        }
    };
    /** Sets the indeterminate state of this CheckBoxTreeItem. */
    public final void setIndeterminate(Boolean value) { indeterminateProperty().setValue(value); }
    /** Returns the indeterminate state of this CheckBoxTreeItem. */
    public final Boolean isIndeterminate() { return indeterminate == null ? false : indeterminate.getValue(); }
    /** A {@link BooleanProperty} used to represent the indeterminate state of this CheckBoxTreeItem. */
    public final BooleanProperty indeterminateProperty() { return indeterminate; }
    
    
    // --- Independent
    /** 
     * A {@link BooleanProperty} used to represent the independent state of this CheckBoxTreeItem. 
     * The independent state is used to represent whether changes to a single
     * CheckBoxTreeItem should influence the state of its parent and children.
     * 
     * <p>By default, the independent property is false, which means that when
     * a CheckBoxTreeItem has state changes to the selected or indeterminate
     * properties, the state of related CheckBoxTreeItems will possibly be changed.
     * If the independent property is set to true, the state of related CheckBoxTreeItems
     * will <b>never</b> change.
     */
    public final BooleanProperty independentProperty() { return independent; }
    private final BooleanProperty independent = new SimpleBooleanProperty(this, "independent", false);
    public final void setIndependent(Boolean value) { independentProperty().setValue(value); }
    public final Boolean isIndependent() { return independent == null ? false : independent.getValue(); }
    
    
    
    /***************************************************************************
     *                                                                         *
     * Private Implementation                                                  *
     *                                                                         *
     **************************************************************************/
    
    private static boolean updateLock = false;
    
    private void updateState() {
        if (isIndependent()) return;
        
        boolean firstLock = ! updateLock;
        
        // toggle parent (recursively up to root)
        updateLock = true;
        updateUpwards();
        
        if (firstLock) updateLock = false;
        
        // toggle children
        if (updateLock) return;
        updateDownwards();
    }

    private void updateUpwards() {
        if (! (getParent() instanceof CheckBoxTreeItem)) return;
        
        CheckBoxTreeItem<?> parent = (CheckBoxTreeItem<?>) getParent();
        int selectCount = 0;
        int indeterminateCount = 0;
        for (TreeItem<?> child : parent.getChildren()) {
            if (! (child instanceof CheckBoxTreeItem)) continue;
            
            CheckBoxTreeItem<?> cbti = (CheckBoxTreeItem<?>) child;
            
            selectCount += cbti.isSelected() && ! cbti.isIndeterminate() ? 1 : 0;
            indeterminateCount += cbti.isIndeterminate() ? 1 : 0;
        }
        
        if (selectCount == parent.getChildren().size()) {
            parent.setSelected(true);
            parent.setIndeterminate(false);
        } else if (selectCount == 0 && indeterminateCount == 0) {
            parent.setSelected(false);
            parent.setIndeterminate(false);
        } else {
            parent.setIndeterminate(true);
        }
    }
    
    private void updateDownwards() {
        // If this node is not a leaf, we also put all
        // children into the same state as this branch
        if (! isLeaf()) {
            for (TreeItem<T> child : getChildren()) {
                if (child instanceof CheckBoxTreeItem) {
                    CheckBoxTreeItem<?> cbti = ((CheckBoxTreeItem<?>) child);
                    cbti.setSelected(isSelected());
                }
            }
        }
    }
    
    private void fireEvent(CheckBoxTreeItem item, boolean selectionChanged) {
        Event evt = new CheckBoxTreeItem.TreeModificationEvent(CHECK_BOX_SELECTION_CHANGED_EVENT, item, selectionChanged);
        Event.fireEvent(this, evt);
    }
    
    
    /**
     * A TreeModificationEvent class that works in a similar vein to the 
     * {@link javafx.scene.control.TreeItem.TreeModificationEvent} class, in that
     * this event will bubble up the CheckBoxTreeItem hierarchy, until the parent
     * node is null.
     * 
     * @param <T> The type of the value contained within the
     *      {@link CheckBoxTreeItem#valueProperty() value} property.
     */
    public static class TreeModificationEvent<T> extends Event {

        private final CheckBoxTreeItem<T> treeItem;
        private final boolean selectionChanged;

        /**
         * Creates a default TreeModificationEvent instance to represent the 
         * change in selection/indeterminate states for the given CheckBoxTreeItem
         * instance.
         */
        public TreeModificationEvent(EventType<? extends Event> eventType, CheckBoxTreeItem<T> treeItem, boolean selectionChanged) {
            super(eventType);
            this.treeItem = treeItem;
            this.selectionChanged = selectionChanged;
        }
        
        /** 
         * Returns the CheckBoxTreeItem that this event occurred upon.
         * @return The CheckBoxTreeItem that this event occurred upon.
         */
        public CheckBoxTreeItem<T> getTreeItem() {
            return treeItem;
        }
        
        /**
         * Indicates the the reason for this event is that the selection on the
         * CheckBoxTreeItem changed (as opposed to it becoming indeterminate).
         */
        public boolean wasSelectionChanged() {
            return selectionChanged;
        }
        
        /**
         * Indicates the the reason for this event is that the indeterminate
         * state on the CheckBoxTreeItem changed (as opposed to it becoming 
         * selected or unselected).
         */
        public boolean wasIndeterminateChanged() {
            return ! selectionChanged;
        }
    }
}