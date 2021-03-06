/*
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.oracle.javafx.scenebuilder.kit.editor.panel.inspector.popupeditors;

import com.oracle.javafx.scenebuilder.kit.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.kit.util.control.effectpicker.EffectPicker;
import java.util.List;
import java.util.Set;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.Effect;

/**
 * Popup editor for the Effect property.
 */
public class EffectPopupEditor extends PopupEditor {

    private final EffectPicker effectPicker = new EffectPicker();
    private final List<MenuItem> effectMenuItems;

    private final ChangeListener<Effect> effectChangeListener = new ChangeListener<Effect>() {
        @Override
        public void changed(ObservableValue<? extends Effect> ov, Effect oldValue, Effect newValue) {
            final String valueAsString = effectPicker.getEffectPath();
            commitValue(newValue, valueAsString);
            // Refresh MenuButton items if needed
            updateMenuButton(newValue);
            displayValueAsString(valueAsString);
        }
    };

    @SuppressWarnings("LeakingThisInConstructor")
    public EffectPopupEditor(ValuePropertyMetadata propMeta, Set<Class<?>> selectedClasses) {
        super(propMeta, selectedClasses);
        plugEditor(this, effectPicker);
        effectMenuItems = effectPicker.getMenuItems();
    }

    @Override
    public void setPopupContentValue(Object value) {
        assert value == null || value instanceof Effect;
        effectPicker.rootEffectProperty().removeListener(effectChangeListener);
        effectPicker.setRootEffectProperty((Effect) value);
        // Refresh MenuButton items if needed
        updateMenuButton((Effect) value);
        effectPicker.rootEffectProperty().addListener(effectChangeListener);
        // Update the menu button string
        displayValueAsString(effectPicker.getEffectPath());
    }

    @Override
    public void resetPopupContent() {
        effectPicker.reset();
    }

    private void updateMenuButton(Effect value) {
        if (value != null) {
            if (popupMb.getItems().contains(popupMenuItem) == false) {
                popupMb.getItems().removeAll(effectMenuItems);
                popupMb.getItems().add(popupMenuItem);
            }
        } else {
            if (popupMb.getItems().contains(popupMenuItem) == true) {
                popupMb.getItems().addAll(effectMenuItems);
                popupMb.getItems().remove(popupMenuItem);
            }
        }
    }
}
