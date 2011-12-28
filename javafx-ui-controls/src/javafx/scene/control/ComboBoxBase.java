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

package javafx.scene.control;

import com.sun.javafx.css.StyleManager;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;

/**
 * Abstract base class for ComboBox-like controls. A ComboBox typically has
 * a button that, when clicked, will pop up some means of allowing a user
 * to select one or more values (depending on the implementation). This base
 * class makes no assumptions about what happens when the {@link #show()} and
 * {@link #hide()} methods are called, however commonly this results in either
 * a popup or dialog appearing that allows for the user to provide the 
 * required information.
 * 
 * <p>A ComboBox has a {@link #valueProperty() value} property that represents
 * the current user input. This may be based on a selection from a drop-down list,
 * or it may be from user input when the ComboBox is 
 * {@link #editableProperty() editable}.
 * 
 * <p>An {@link #editableProperty() editable} ComboBox is one which provides some
 * means for an end-user to provide input for values that are not otherwise
 * options available to them. For example, in the {@link ComboBox} implementation,
 * an editable ComboBox provides a {@link TextField} that may be typed into.
 * As mentioned above, when the user commits textual input into the textfield
 * (commonly by pressing the Enter keyboard key), the 
 * {@link #valueProperty() value} property will be updated.
 * 
 * <p>The purpose of the separation between this class and, say, {@link ComboBox} 
 * is to allow for ComboBox-like controls that do not necessarily pop up a list 
 * of items. Examples of other implementations include color pickers, calendar 
 * pickers, etc. The  {@link ComboBox} class provides the default, and most commonly
 * expected implementation. Refer to that classes javadoc for more information.
 * 
 * @see ComboBox
 * @param <T> The type of the value that has been selected or otherwise
 *      entered in to this ComboBox.
 */
public abstract class ComboBoxBase<T> extends Control {
    
    /**
     * Creates a default ComboBoxBase instance.
     */
    public ComboBoxBase() {
        getStyleClass().add(DEFAULT_STYLE_CLASS);
    }
    

    // --- value
    /**
     * The value of this ComboBox is defined as the selected item if the input
     * is not editable, or if it is editable, the most recent user action: 
     * either the value input by the user, or the last selected item.
     */
    public ObjectProperty<T> valueProperty() { return value; }
    private ObjectProperty<T> value = new SimpleObjectProperty<T>(this, "value") {
        @Override protected void invalidated() {
            super.invalidated();
            fireEvent(new ActionEvent());
        }
    };
    public final void setValue(T value) { valueProperty().set(value); }
    public final T getValue() { return valueProperty().get(); }
    
    
    // --- editable
    /**
     * Specifies whether the ComboBox allows for user input. When editable is 
     * true, the ComboBox has a text input area that a user may type in to. This
     * input is then available via the {@link #valueProperty() value} property.
     */
    public BooleanProperty editableProperty() { return editable; }
    public final void setEditable(boolean value) { editableProperty().set(value); }
    public final boolean isEditable() { return editableProperty().get(); }
    private BooleanProperty editable = new SimpleBooleanProperty(this, "editable", false) {
        @Override protected void invalidated() {
            impl_pseudoClassStateChanged(PSEUDO_CLASS_EDITABLE);
        }
    };
    
    
    // --- showing
    /**
     * Represents the current state of the ComboBox popup, and whether it is 
     * currently visible on screen (although it may be hidden behind other windows).
     */
    public BooleanProperty showingProperty() { return showing; }
    public final boolean isShowing() { return showingProperty().get(); }
    private BooleanProperty showing = new SimpleBooleanProperty(this, "showing", false) {
        @Override protected void invalidated() {
            impl_pseudoClassStateChanged(PSEUDO_CLASS_SHOWING);
        }
    };
    
    
    // --- prompt text
    /**
     * The {@code ComboBox} prompt text to display, or <tt>null</tt> if no 
     * prompt text is displayed.
     */
    private StringProperty promptText = new SimpleStringProperty(this, "promptText", "") {
        @Override protected void invalidated() {
            // Strip out newlines
            String txt = get();
            if (txt != null && txt.contains("\n")) {
                txt = txt.replace("\n", "");
                set(txt);
            }
        }
    };
    public final StringProperty promptTextProperty() { return promptText; }
    public final String getPromptText() { return promptText.get(); }
    public final void setPromptText(String value) { promptText.set(value); }
    
    
    // --- armed
    /**
     * Indicates that the ComboBox has been "armed" such that a mouse release
     * will cause the ComboBox {@link #show()} method to be invoked. This is 
     * subtly different from pressed. Pressed indicates that the mouse has been
     * pressed on a Node and has not yet been released. {@code arm} however
     * also takes into account whether the mouse is actually over the
     * ComboBox and pressed.
     */
    public BooleanProperty armedProperty() { return armed; }
    private final void setArmed(boolean value) { armedProperty().set(value); }
    public final boolean isArmed() { return armedProperty().get(); }
    private BooleanProperty armed = new SimpleBooleanProperty(this, "armed", false) {
        @Override protected void invalidated() {
            impl_pseudoClassStateChanged(PSEUDO_CLASS_ARMED);
        }
    };
    
    
    // --- On Action
    /**
     * The ComboBox action, which is invoked whenever the ComboBox 
     * {@link #valueProperty() value} property is changed. This
     * may be due to the value property being programmatically changed, when the
     * user selects an item in a popup list or dialog, or, in the case of 
     * {@link #editableProperty() editable} ComboBoxes, it may be when the user 
     * provides their own input (be that via a {@link TextField} or some other
     * input mechanism.
     */
    public final ObjectProperty<EventHandler<ActionEvent>> onActionProperty() { return onAction; }
    public final void setOnAction(EventHandler<ActionEvent> value) { onActionProperty().set(value); }
    public final EventHandler<ActionEvent> getOnAction() { return onActionProperty().get(); }
    private ObjectProperty<EventHandler<ActionEvent>> onAction = new ObjectPropertyBase<EventHandler<ActionEvent>>() {
        @Override protected void invalidated() {
            setEventHandler(ActionEvent.ACTION, get());
        }

        @Override
        public Object getBean() {
            return ComboBoxBase.this;
        }

        @Override
        public String getName() {
            return "onAction";
        }
    };
    
    
    /***************************************************************************
     *                                                                         *
     * Methods                                                                 *
     *                                                                         *
     **************************************************************************/

    /**
     * Requests that the ComboBox display the popup aspect of the user interface.
     * As mentioned in the {@link ComboBoxBase} class javadoc, what is actually
     * shown when this method is called is undefined, but commonly it is some
     * form of popup or dialog window.
     */
    public void show() {
        if (!isDisabled()) showing.set(true);
    }

    /**
     * Closes the popup / dialog that was shown when {@link #show()} was called.
     */
    public void hide() {
        showing.set(false);
    }
    
    /**
     * Arms the ComboBox. An armed ComboBox will show a popup list on the next 
     * expected UI gesture.
     *
     * @expert This function is intended to be used by experts, primarily
     *         by those implementing new Skins or Behaviors. It is not common
     *         for developers or designers to access this function directly.
     */
    public void arm() {
        setArmed(true);
    }

    /**
     * Disarms the ComboBox. See {@link #arm()}.
     *
     * @expert This function is intended to be used by experts, primarily
     *         by those implementing new Skins or Behaviors. It is not common
     *         for developers or designers to access this function directly.
     */
    public void disarm() {
        setArmed(false);
    }
    
    
    /***************************************************************************
     *                                                                         *
     * Stylesheet Handling                                                     *
     *                                                                         *
     **************************************************************************/

    private static final String DEFAULT_STYLE_CLASS = "combo-box-base";
    
    private static final String PSEUDO_CLASS_EDITABLE = "editable";
    private static final String PSEUDO_CLASS_SHOWING = "showing";
    private static final String PSEUDO_CLASS_ARMED = "armed";
    
    private static final long PSEUDO_CLASS_EDITABLE_MASK
            = StyleManager.getInstance().getPseudoclassMask(PSEUDO_CLASS_EDITABLE);
    private static final long PSEUDO_CLASS_SHOWING_MASK
            = StyleManager.getInstance().getPseudoclassMask(PSEUDO_CLASS_SHOWING);
    private static final long PSEUDO_CLASS_ARMED_MASK
            = StyleManager.getInstance().getPseudoclassMask(PSEUDO_CLASS_ARMED);
    
    /**
     * @treatasprivate implementation detail
     * @deprecated This is an internal API that is not intended for use and will be removed in the next version
     */
    @Deprecated @Override public long impl_getPseudoClassState() {
        long mask = super.impl_getPseudoClassState();
        if (isEditable()) mask |= PSEUDO_CLASS_EDITABLE_MASK;
        if (isShowing()) mask |= PSEUDO_CLASS_SHOWING_MASK;
        if (isArmed()) mask |= PSEUDO_CLASS_ARMED_MASK;
        return mask;
    }
}