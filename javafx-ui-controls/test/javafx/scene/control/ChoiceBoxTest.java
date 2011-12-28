/*
 * Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved.
 */
package javafx.scene.control;

import static javafx.scene.control.ControlTestUtils.assertPseudoClassDoesNotExist;
import static javafx.scene.control.ControlTestUtils.assertPseudoClassExists;
import static javafx.scene.control.ControlTestUtils.assertStyleClassContains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class ChoiceBoxTest {
    private ChoiceBox<String> box;
    
    @Before public void setup() {
        box = new ChoiceBox<String>();
    }
    
    /*********************************************************************
     * Tests for the constructors                                        *
     ********************************************************************/
    
    @Test public void noArgConstructorSetsTheStyleClass() {
        assertStyleClassContains(box, "choice-box");
    }
    
    @Test public void noArgConstructorSetsNonNullSelectionModel() {
        assertNotNull(box.getSelectionModel());
    }
    
    @Test public void noArgConstructorSetsNonNullItems() {
        assertNotNull(box.getItems());
    }
    
    @Test public void noArgConstructor_selectedItemIsNull() {
        assertNull(box.getSelectionModel().getSelectedItem());
    }
    
    @Test public void noArgConstructor_selectedIndexIsNegativeOne() {
        assertEquals(-1, box.getSelectionModel().getSelectedIndex());
    }
    
    @Test public void singleArgConstructorSetsTheStyleClass() {
        final ChoiceBox<String> b2 = new ChoiceBox<String>(FXCollections.observableArrayList("Hi"));
        assertStyleClassContains(b2, "choice-box");
    }
    
    @Test public void singleArgConstructorSetsNonNullSelectionModel() {
        final ChoiceBox<String> b2 = new ChoiceBox<String>(FXCollections.observableArrayList("Hi"));
        assertNotNull(b2.getSelectionModel());
    }
    
    @Test public void singleArgConstructorAllowsNullItems() {
        final ChoiceBox<String> b2 = new ChoiceBox<String>(null);
        assertNull(b2.getItems());
    }
    
    @Test public void singleArgConstructorTakesItems() {
        ObservableList<String> items = FXCollections.observableArrayList("Hi");
        final ChoiceBox<String> b2 = new ChoiceBox<String>(items);
        assertSame(items, b2.getItems());
    }
    
    @Test public void singleArgConstructor_selectedItemIsNull() {
        final ChoiceBox<String> b2 = new ChoiceBox<String>(FXCollections.observableArrayList("Hi"));
        assertNull(b2.getSelectionModel().getSelectedItem());
    }
    
    @Test public void singleArgConstructor_selectedIndexIsNegativeOne() {
        final ChoiceBox<String> b2 = new ChoiceBox<String>(FXCollections.observableArrayList("Hi"));
        assertEquals(-1, b2.getSelectionModel().getSelectedIndex());
    }
    
    /*********************************************************************
     * Tests for selection model                                         *
     ********************************************************************/
    
    @Test public void selectionModelCanBeNull() {
        box.setSelectionModel(null);
        assertNull(box.getSelectionModel());
    }

    @Test public void selectionModelCanBeBound() {
        SingleSelectionModel<String> sm = new ChoiceBox.ChoiceBoxSelectionModel<String>(box);
        ObjectProperty<SingleSelectionModel<String>> other = new SimpleObjectProperty<SingleSelectionModel<String>>(sm);
        box.selectionModelProperty().bind(other);
        assertSame(sm, box.getSelectionModel());
    }

    @Test public void selectionModelCanBeChanged() {
        SingleSelectionModel<String> sm = new ChoiceBox.ChoiceBoxSelectionModel<String>(box);
        box.setSelectionModel(sm);
        assertSame(sm, box.getSelectionModel());
    }
    
    @Test public void canSetSelectedItemToAnItemEvenWhenThereAreNoItems() {
        final String randomString = new String("I AM A CRAZY RANDOM STRING");
        box.getSelectionModel().select(randomString);
        assertEquals(-1, box.getSelectionModel().getSelectedIndex());
        assertSame(randomString, box.getSelectionModel().getSelectedItem());
    }
        
    @Test public void canSetSelectedItemToAnItemNotInTheChoiceBoxItems() {
        box.getItems().addAll("Apple", "Orange", "Banana");
        final String randomString = new String("I AM A CRAZY RANDOM STRING");
        box.getSelectionModel().select(randomString);
        assertEquals(-1, box.getSelectionModel().getSelectedIndex());
        assertSame(randomString, box.getSelectionModel().getSelectedItem());
    }
        
    @Test public void settingTheSelectedItemToAnItemInItemsResultsInTheCorrectSelectedIndex() {
        box.getItems().addAll("Apple", "Orange", "Banana");
        box.getSelectionModel().select("Orange");
        assertEquals(1, box.getSelectionModel().getSelectedIndex());
        assertSame("Orange", box.getSelectionModel().getSelectedItem());
    }
    
    @Test public void settingTheSelectedItemToANonexistantItemAndThenAddingItemsWhichContainsItResultsInCorrectSelectedIndex() {
        box.getSelectionModel().select("Orange");
        box.getItems().addAll("Apple", "Orange", "Banana");
        assertEquals(1, box.getSelectionModel().getSelectedIndex());
        assertSame("Orange", box.getSelectionModel().getSelectedItem());
    }

    @Test public void settingTheSelectedItemToANonexistantItemAndThenSettingItemsWhichContainsItResultsInCorrectSelectedIndex() {
        box.getSelectionModel().select("Orange");
        box.getItems().setAll("Apple", "Orange", "Banana");
        assertEquals(1, box.getSelectionModel().getSelectedIndex());
        assertSame("Orange", box.getSelectionModel().getSelectedItem());
    }
    
    @Test public void ensureSelectionClearsWhenAllItemsAreRemoved_selectIndex0() {
        box.getItems().addAll("Apple", "Orange", "Banana");
        box.getSelectionModel().select(0);
        box.getItems().clear();
        assertEquals(-1, box.getSelectionModel().getSelectedIndex());
        assertEquals(null, box.getSelectionModel().getSelectedItem());
    }

    @Test public void ensureSelectionClearsWhenSettingSelectionBeforePopulatingItemsAndAllItemsAreRemoved() {
        box.getSelectionModel().select("Banana");
        box.getItems().addAll("Apple", "Orange", "Banana");
        box.getItems().clear();
        assertEquals(-1, box.getSelectionModel().getSelectedIndex());
        assertEquals(null, box.getSelectionModel().getSelectedItem());
    }

    @Test public void ensureSelectionClearsWhenSettingSelectionBeforePopulatingItemsAndSelectedItemIsRemoved() {
        box.getSelectionModel().select("Banana");
        box.getItems().addAll("Apple", "Orange", "Banana");
        box.getItems().remove("Banana");
        assertEquals(-1, box.getSelectionModel().getSelectedIndex());
        assertEquals(null, box.getSelectionModel().getSelectedItem());
    }

    @Test public void ensureSelectionClearsWhenAllItemsAreRemoved_selectIndex2() {
        box.getItems().addAll("Apple", "Orange", "Banana");
        box.getSelectionModel().select(2);
        box.getItems().clear();
        assertEquals(-1, box.getSelectionModel().getSelectedIndex());
        assertEquals(null, box.getSelectionModel().getSelectedItem());
    }
    
    @Test public void ensureSelectedItemRemainsAccurateWhenItemsAreCleared() {
        box.getItems().addAll("Apple", "Orange", "Banana");
        box.getSelectionModel().select(2);
        box.getItems().clear();
        assertNull(box.getSelectionModel().getSelectedItem());
        assertEquals(-1, box.getSelectionModel().getSelectedIndex());
        
        box.getItems().addAll("Kiwifruit", "Mandarin", "Pineapple");
        box.getSelectionModel().select(2);
        assertEquals("Pineapple", box.getSelectionModel().getSelectedItem());
    }
    
    @Test public void ensureSelectionIsCorrectWhenItemsChange() {
        box.setItems(FXCollections.observableArrayList("Item 1"));
        box.getSelectionModel().select(0);
        assertEquals("Item 1", box.getSelectionModel().getSelectedItem());
        
        box.setItems(FXCollections.observableArrayList("Item 2"));
        assertEquals(-1, box.getSelectionModel().getSelectedIndex());
        assertEquals(null, box.getSelectionModel().getSelectedItem());
    }
    
    /*********************************************************************
     * Tests for showing property                                        *
     ********************************************************************/
    
    @Test public void showingIsFalseByDefault() {
        assertFalse(box.isShowing());
    }
    
    @Test public void showingCanBeSet() {
        box.show();
        assertTrue(box.isShowing());
    }
    
    @Test public void showingCanBeCleared() {
        box.show();
        box.hide();
        assertFalse(box.isShowing());
    }
    
    @Test public void showDoesntWorkWhenDisabled() {
        box.setDisable(true);
        box.show();
        assertFalse(box.isShowing());
    }
    
    @Ignore("impl_cssSet API removed")
    @Test public void cannotSpecifyShowingViaCSS() {
//        box.impl_cssSet("-fx-showing", true);
        assertFalse(box.isShowing());
    }
    
    @Test public void settingShowingSetsPseudoClass() {
        box.show();
        assertPseudoClassExists(box, "showing");
    }
    
    @Test public void clearingArmedClearsPseudoClass() {
        box.show();
        box.hide();
        assertPseudoClassDoesNotExist(box, "showing");
    }
}