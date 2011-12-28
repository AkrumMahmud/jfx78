/*
 * Copyright (c) 2010, Oracle and/or its affiliates. All rights reserved.
 */

package javafx.scene.control;

import com.sun.javafx.css.StyleableProperty;
import static javafx.scene.control.ControlTestUtils.*;
import com.sun.javafx.pgstub.StubToolkit;
import com.sun.javafx.tk.Toolkit;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Side;
import static org.junit.Assert.*;


import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author srikalyc
 */
public class TabPaneTest {
    private TabPane tabPane;//Empty string
    private Toolkit tk;
    private TabPane.TabPaneSelectionModel sm;
    private Tab tab1;
    private Tab tab2;

    @Before public void setup() {
        tk = (StubToolkit)Toolkit.getToolkit();//This step is not needed (Just to make sure StubToolkit is loaded into VM)
        tabPane = new TabPane();
        tab1 = new Tab("one");
        tab2 = new Tab("two");
        sm = new TabPane.TabPaneSelectionModel(tabPane);
    }
    
   
   
    /*********************************************************************
     * Tests for default values                                         *
     ********************************************************************/
    
    @Test public void defaultConstructorShouldSetStyleClassTo_tabpane() {
        assertStyleClassContains(tabPane, "tab-pane");
    }
    
    @Test public void defaultConstructorSelectionModelNotNull() {
        assertNotNull(tabPane.getSelectionModel());
    }
    
    @Test public void defaultConstructorInitialTabsEmpty() {
        assertNotNull(tabPane.getTabs());
        assertEquals(tabPane.getTabs().size(), 0.0, 0.0);
    }
    
    @Test public void defaultSideIsTop() {
        assertSame(tabPane.getSide(), Side.TOP);
    }

    @Test public void defaultConstructorTabClosingPolicy() {
        assertNotNull(tabPane.getTabClosingPolicy());
        assertSame(tabPane.getTabClosingPolicy(), TabPane.TabClosingPolicy.SELECTED_TAB);
    }

    @Test public void defaultConstructorRotateGraphic() {
        assertFalse(tabPane.isRotateGraphic());
    }
    
    @Test public void defaultTabMinWidth() {
        assertEquals(tabPane.getTabMinWidth(), 0.0, 0.0);
    }

    @Test public void defaultTabMaxWidth() {
        assertEquals(tabPane.getTabMaxWidth(), Double.MAX_VALUE, 0.0);
    }

    @Test public void defaultTabMinHeight() {
        assertEquals(tabPane.getTabMinHeight(), 0.0, 0.0);
    }

    @Test public void defaultTabMaxHeight() {
        assertEquals(tabPane.getTabMaxHeight(), Double.MAX_VALUE, 0.0);
    }

    @Test public void defaultSelectionModelEmpty() {
        assertTrue(tabPane.getSelectionModel().isEmpty());
    }
    
    
    /*********************************************************************
     * Tests for property binding                                        *
     ********************************************************************/
    
    @Test public void checkSelectionModelPropertyBind() {
        ObjectProperty objPr = new SimpleObjectProperty<SingleSelectionModel<Tab>>(null);
        tabPane.selectionModelProperty().bind(objPr);
        assertNull("selectionModel cannot be bound", tabPane.selectionModelProperty().getValue());
        objPr.setValue(sm);
        assertSame("selectionModel cannot be bound", tabPane.selectionModelProperty().getValue(), sm);
    }
    
    @Test public void checkSidePropertyBind() {
        ObjectProperty objPr = new SimpleObjectProperty<Side>(Side.BOTTOM);
        tabPane.sideProperty().bind(objPr);
        assertSame("side cannot be bound", tabPane.sideProperty().getValue(), Side.BOTTOM);
        objPr.setValue(Side.RIGHT);
        assertSame("side cannot be bound", tabPane.sideProperty().getValue(), Side.RIGHT);
    }
    
    @Test public void checkTabClosingPropertyBind() {
        ObjectProperty objPr = new SimpleObjectProperty<TabPane.TabClosingPolicy>(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.tabClosingPolicyProperty().bind(objPr);
        assertSame("side cannot be bound", tabPane.tabClosingPolicyProperty().getValue(), TabPane.TabClosingPolicy.UNAVAILABLE);
        objPr.setValue(TabPane.TabClosingPolicy.ALL_TABS);
        assertSame("side cannot be bound", tabPane.tabClosingPolicyProperty().getValue(), TabPane.TabClosingPolicy.ALL_TABS);
    }
    
    @Test public void checkRotateGraphicsPropertyBind() {
        BooleanProperty objPr = new SimpleBooleanProperty(false);
        tabPane.rotateGraphicProperty().bind(objPr);
        assertFalse("rotateGraphic cannot be bound", tabPane.rotateGraphicProperty().getValue());
        objPr.setValue(true);
        assertTrue("rotateGraphic cannot be bound", tabPane.rotateGraphicProperty().getValue());
    }
    
    @Test public void checkTabMinWidthPropertyBind() {
        DoubleProperty objPr = new SimpleDoubleProperty(2.0);
        tabPane.tabMinWidthProperty().bind(objPr);
        assertEquals("tabMinWidthProperty cannot be bound", tabPane.tabMinWidthProperty().getValue(), 2.0, 0.0);
        objPr.setValue(5.0);
        assertEquals("tabMinWidthProperty cannot be bound", tabPane.tabMinWidthProperty().getValue(), 5.0, 0.0);
    }
    
    @Test public void checkTabMaxWidthPropertyBind() {
        DoubleProperty objPr = new SimpleDoubleProperty(2.0);
        tabPane.tabMaxWidthProperty().bind(objPr);
        assertEquals("tabMaxWidthProperty cannot be bound", tabPane.tabMaxWidthProperty().getValue(), 2.0, 0.0);
        objPr.setValue(5.0);
        assertEquals("tabMaxWidthProperty cannot be bound", tabPane.tabMaxWidthProperty().getValue(), 5.0, 0.0);
    }
    
    
    @Test public void checkTabMinHeightPropertyBind() {
        DoubleProperty objPr = new SimpleDoubleProperty(2.0);
        tabPane.tabMinHeightProperty().bind(objPr);
        assertEquals("tabMinHeightProperty cannot be bound", tabPane.tabMinHeightProperty().getValue(), 2.0, 0.0);
        objPr.setValue(5.0);
        assertEquals("tabMinHeightProperty cannot be bound", tabPane.tabMinHeightProperty().getValue(), 5.0, 0.0);
    }
    
    @Test public void checkTabMaxHeightPropertyBind() {
        DoubleProperty objPr = new SimpleDoubleProperty(2.0);
        tabPane.tabMaxHeightProperty().bind(objPr);
        assertEquals("tabMaxHeightProperty cannot be bound", tabPane.tabMaxHeightProperty().getValue(), 2.0, 0.0);
        objPr.setValue(5.0);
        assertEquals("tabMaxHeightProperty cannot be bound", tabPane.tabMaxHeightProperty().getValue(), 5.0, 0.0);
    }
    
    @Test public void selectionModelPropertyHasBeanReference() {
        assertSame(tabPane, tabPane.selectionModelProperty().getBean());
    }

    @Test public void selectionModelPropertyHasName() {
        assertEquals("selectionModel", tabPane.selectionModelProperty().getName());
    }

    @Test public void sidePropertyHasBeanReference() {
        assertSame(tabPane, tabPane.sideProperty().getBean());
    }

    @Test public void sidePropertyHasName() {
        assertEquals("side", tabPane.sideProperty().getName());
    }

    @Test public void tabClosingPolicyPropertyHasBeanReference() {
        assertSame(tabPane, tabPane.tabClosingPolicyProperty().getBean());
    }

    @Test public void tabClosingPolicyPropertyHasName() {
        assertEquals("tabClosingPolicy", tabPane.tabClosingPolicyProperty().getName());
    }

    @Test public void rotateGraphicPropertyHasBeanReference() {
        assertSame(tabPane, tabPane.rotateGraphicProperty().getBean());
    }

    @Test public void rotateGraphicPropertyHasName() {
        assertEquals("rotateGraphic", tabPane.rotateGraphicProperty().getName());
    }

    @Test public void tabMinWidthPropertyHasBeanReference() {
        assertSame(tabPane, tabPane.tabMinWidthProperty().getBean());
    }

    @Test public void tabMinWidthPropertyHasName() {
        assertEquals("tabMinWidth", tabPane.tabMinWidthProperty().getName());
    }

    @Test public void tabMaxWidthPropertyHasBeanReference() {
        assertSame(tabPane, tabPane.tabMaxWidthProperty().getBean());
    }

    @Test public void tabMaxWidthPropertyHasName() {
        assertEquals("tabMaxWidth", tabPane.tabMaxWidthProperty().getName());
    }

    @Test public void tabMinHeightPropertyHasBeanReference() {
        assertSame(tabPane, tabPane.tabMinHeightProperty().getBean());
    }

    @Test public void tabMinHeightPropertyHasName() {
        assertEquals("tabMinHeight", tabPane.tabMinHeightProperty().getName());
    }

    @Test public void tabMaxHeightPropertyHasBeanReference() {
        assertSame(tabPane, tabPane.tabMaxHeightProperty().getBean());
    }

    @Test public void tabMaxHeightPropertyHasName() {
        assertEquals("tabMaxHeight", tabPane.tabMaxHeightProperty().getName());
    }


    
    
    /*********************************************************************
     * Check for Pseudo classes                                          *
     ********************************************************************/
    @Test public void settingSideSetsPseudoClass() {
        tabPane.setSide(Side.BOTTOM);
        assertPseudoClassExists(tabPane, "bottom");
        assertPseudoClassDoesNotExist(tabPane, "top");
        assertPseudoClassDoesNotExist(tabPane, "left");
        assertPseudoClassDoesNotExist(tabPane, "right");
    }

    @Test public void clearingSideClearsPseudoClass() {
        tabPane.setSide(Side.BOTTOM);
        tabPane.setSide(Side.LEFT);
        assertPseudoClassExists(tabPane, "left");
        assertPseudoClassDoesNotExist(tabPane, "bottom");
        assertPseudoClassDoesNotExist(tabPane, "top");
        assertPseudoClassDoesNotExist(tabPane, "right");
    }


    
    /*********************************************************************
     * CSS related Tests                                                 *
     ********************************************************************/
    @Test public void whenTabMinWidthIsBound_impl_cssSettable_ReturnsFalse() {
        StyleableProperty styleable = StyleableProperty.getStyleableProperty(tabPane.tabMinWidthProperty());
        assertTrue(styleable.isSettable(tabPane));        
        DoubleProperty other = new SimpleDoubleProperty(30.0);
        tabPane.tabMinWidthProperty().bind(other);
        assertFalse(styleable.isSettable(tabPane));        
    }

    @Test public void whenTabMinWidthIsSpecifiedViaCSSAndIsNotBound_impl_cssSettable_ReturnsTrue() {
        StyleableProperty styleable = StyleableProperty.getStyleableProperty(tabPane.tabMinWidthProperty());
        styleable.set(tabPane, 43.0);
        assertTrue(styleable.isSettable(tabPane));        
    }

    @Test public void canSpecifyTabMinWidthViaCSS() {
        StyleableProperty styleable = StyleableProperty.getStyleableProperty(tabPane.tabMinWidthProperty());
        styleable.set(tabPane, 34.0);
        assertEquals(34.0, tabPane.getTabMinWidth(), 0.0);
    }

    @Test public void whenTabMaxWidthIsBound_impl_cssSettable_ReturnsFalse() {
        StyleableProperty styleable = StyleableProperty.getStyleableProperty(tabPane.tabMaxWidthProperty());
        assertTrue(styleable.isSettable(tabPane));        
        DoubleProperty other = new SimpleDoubleProperty(30.0);
        tabPane.tabMaxWidthProperty().bind(other);
        assertFalse(styleable.isSettable(tabPane));        
    }

    @Test public void whenTabMaxWidthIsSpecifiedViaCSSAndIsNotBound_impl_cssSettable_ReturnsTrue() {
        StyleableProperty styleable = StyleableProperty.getStyleableProperty(tabPane.tabMaxWidthProperty());
        styleable.set(tabPane, 43.0);
        assertTrue(styleable.isSettable(tabPane));        
    }

    @Test public void canSpecifyTabMaxWidthViaCSS() {
        StyleableProperty styleable = StyleableProperty.getStyleableProperty(tabPane.tabMaxWidthProperty());
        styleable.set(tabPane, 34.0);
        assertEquals(34.0, tabPane.getTabMaxWidth(), 0.0);
    }

    @Test public void whenTabMinHeightIsBound_impl_cssSettable_ReturnsFalse() {
        StyleableProperty styleable = StyleableProperty.getStyleableProperty(tabPane.tabMinHeightProperty());
        assertTrue(styleable.isSettable(tabPane));
        DoubleProperty other = new SimpleDoubleProperty(30.0);
        tabPane.tabMinHeightProperty().bind(other);
        assertFalse(styleable.isSettable(tabPane));
    }

    @Test public void whenTabMinHeightIsSpecifiedViaCSSAndIsNotBound_impl_cssSettable_ReturnsTrue() {
        StyleableProperty styleable = StyleableProperty.getStyleableProperty(tabPane.tabMinHeightProperty());
        styleable.set(tabPane, 43.0);
        assertTrue(styleable.isSettable(tabPane));        
    }

    @Test public void canSpecifyTabMinHeightViaCSS() {
        StyleableProperty styleable = StyleableProperty.getStyleableProperty(tabPane.tabMinHeightProperty());
        styleable.set(tabPane, 34.0);
        assertEquals(34.0, tabPane.getTabMinHeight(), 0.0);
    }

    @Test public void whenTabMaxHeightIsBound_impl_cssSettable_ReturnsFalse() {
        StyleableProperty styleable = StyleableProperty.getStyleableProperty(tabPane.tabMaxHeightProperty());
        assertTrue(styleable.isSettable(tabPane));
        DoubleProperty other = new SimpleDoubleProperty(30.0);
        tabPane.tabMaxHeightProperty().bind(other);
        assertFalse(styleable.isSettable(tabPane));
    }

    @Test public void whenTabMaxHeightIsSpecifiedViaCSSAndIsNotBound_impl_cssSettable_ReturnsTrue() {
        StyleableProperty styleable = StyleableProperty.getStyleableProperty(tabPane.tabMaxHeightProperty());
        styleable.set(tabPane, 43.0);
        assertTrue(styleable.isSettable(tabPane));        
    }

    @Test public void canSpecifyTabMaxHeightViaCSS() {
        StyleableProperty styleable = StyleableProperty.getStyleableProperty(tabPane.tabMaxHeightProperty());
        styleable.set(tabPane, 34.0);
        assertEquals(34.0, tabPane.getTabMaxHeight(), 0.0);
    }



    /*********************************************************************
     * Miscellaneous Tests                                         *
     ********************************************************************/
    @Test public void setSelectionModelAndSeeValueIsReflectedInModel() {
        tabPane.setSelectionModel(sm);
        assertSame(tabPane.selectionModelProperty().getValue(), sm);
    }
    
    @Test public void setselectionModelAndSeeValue() {
        tabPane.setSelectionModel(sm);
        assertSame(tabPane.getSelectionModel(), sm);
    }
    
    @Test public void setSideAndSeeValueIsReflectedInModel() {
        tabPane.setSide(Side.BOTTOM);
        assertSame(tabPane.sideProperty().getValue(), Side.BOTTOM);
    }
    
    @Test public void setSideAndSeeValue() {
        tabPane.setSide(Side.LEFT);
        assertSame(tabPane.getSide(), Side.LEFT);
    }
    
    @Test public void setTabClosingPolicyAndSeeValueIsReflectedInModel() {
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        assertSame(tabPane.tabClosingPolicyProperty().getValue(), TabPane.TabClosingPolicy.ALL_TABS);
    }
    
    @Test public void setTabClosingPolicyAndSeeValue() {
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        assertSame(tabPane.getTabClosingPolicy(), TabPane.TabClosingPolicy.UNAVAILABLE);
    }
    
    @Test public void setRotateGraphicAndSeeValueIsReflectedInModel() {
        tabPane.setRotateGraphic(true);
        assertTrue(tabPane.rotateGraphicProperty().getValue());
    }
    
    @Test public void setRotateGraphicAndSeeValue() {
        tabPane.setRotateGraphic(true);
        assertTrue(tabPane.isRotateGraphic());
    }
    
    @Test public void setTabMinWidthAndSeeValueIsReflectedInModel() {
        tabPane.setTabMinWidth(30.0);
        assertEquals(tabPane.tabMinWidthProperty().getValue(), 30.0, 0.0);
    }
    
    @Test public void setTabMinWidthAndSeeValue() {
        tabPane.setTabMinWidth(30.0);
        assertEquals(tabPane.getTabMinWidth(), 30.0, 0.0);
    }
    
    @Test public void setTabMaxWidthAndSeeValueIsReflectedInModel() {
        tabPane.setTabMaxWidth(30.0);
        assertEquals(tabPane.tabMaxWidthProperty().getValue(), 30.0, 0.0);
    }
    
    @Test public void setTabMaxWidthAndSeeValue() {
        tabPane.setTabMaxWidth(30.0);
        assertEquals(tabPane.getTabMaxWidth(), 30.0, 0.0);
    }
    
    @Test public void setTabMinHeightAndSeeValueIsReflectedInModel() {
        tabPane.setTabMinHeight(30.0);
        assertEquals(tabPane.tabMinHeightProperty().getValue(), 30.0, 0.0);
    }
    
    @Test public void setTabMinHeightAndSeeValue() {
        tabPane.setTabMinHeight(30.0);
        assertEquals(tabPane.getTabMinHeight(), 30.0, 0.0);
    }
    
    @Test public void setTabMaxHeightAndSeeValueIsReflectedInModel() {
        tabPane.setTabMaxHeight(30.0);
        assertEquals(tabPane.tabMaxHeightProperty().getValue(), 30.0, 0.0);
    }
    
    @Test public void setTabMaxHeightAndSeeValue() {
        tabPane.setTabMaxHeight(30.0);
        assertEquals(tabPane.getTabMaxHeight(), 30.0, 0.0);
    }
    
    @Test public void addTabsCheckItemCountInSelectionModel() {
        tabPane.getTabs().add(tab1);
        tabPane.getTabs().add(tab2);
        assertEquals(tabPane.getSelectionModel().getItemCount(), 2.0, 0.0);
    }
    
    @Test public void addTabsCheckItemInSelectionModel() {
        tabPane.getTabs().add(tab1);
        tabPane.getTabs().add(tab2);
        assertSame(tabPane.getSelectionModel().getModelItem(0), tab1);
        assertSame(tabPane.getSelectionModel().getModelItem(1), tab2);
    }
    
    @Test public void addTabsCheckSelectUsingObjInSelectionModel() {
        tabPane.getTabs().add(tab1);
        tabPane.getTabs().add(tab2);
        tabPane.getSelectionModel().select(tab1);
        assertTrue(tabPane.getSelectionModel().isSelected(0));
    }
    
    @Test public void addTabsCheckSelectUsingIndexInSelectionModel() {
        tabPane.getTabs().add(tab1);
        tabPane.getTabs().add(tab2);
        tabPane.getSelectionModel().select(1);
        assertTrue(tabPane.getSelectionModel().isSelected(1));
    }
    
    @Test public void addTabsCheckClearAndSelectUsingIndexInSelectionModel() {
        tabPane.getTabs().add(tab1);
        tabPane.getTabs().add(tab2);
        tabPane.getSelectionModel().clearAndSelect(1);
        assertTrue(tabPane.getSelectionModel().isSelected(1));
    }
    
    @Test public void addTabsCheckSelectMultipleItemsAndClearOneItemInSelectionModel() {
        tabPane.getTabs().add(tab1);
        tabPane.getTabs().add(tab2);
        tabPane.getSelectionModel().select(0);
        tabPane.getSelectionModel().select(1);
        tabPane.getSelectionModel().clearSelection(0);
        assertFalse(tabPane.getSelectionModel().isSelected(0));
        assertTrue(tabPane.getSelectionModel().isSelected(1));
    }
    
    @Test public void addTabsCheckSelectMultipleItemsAndClearAllSelectionInSelectionModel() {
        tabPane.getTabs().add(tab1);
        tabPane.getTabs().add(tab2);
        tabPane.getSelectionModel().select(0);
        tabPane.getSelectionModel().select(1);
        tabPane.getSelectionModel().clearSelection();
        assertFalse(tabPane.getSelectionModel().isSelected(0));
        assertFalse(tabPane.getSelectionModel().isSelected(1));
        assertTrue(tabPane.getSelectionModel().isEmpty());
    }

    @Test public void addTabsCheckSelectUsingIteratorKindOfMethodsInSelectionModel() {
        tabPane.getTabs().add(tab1);
        tabPane.getTabs().add(tab2);
        tabPane.getSelectionModel().selectFirst();
        assertTrue(tabPane.getSelectionModel().isSelected(0));
        assertFalse(tabPane.getSelectionModel().isSelected(1));
        tabPane.getSelectionModel().selectNext();
        assertFalse(tabPane.getSelectionModel().isSelected(0));
        assertTrue(tabPane.getSelectionModel().isSelected(1));
        tabPane.getSelectionModel().clearSelection();
        tabPane.getSelectionModel().selectLast();
        assertFalse(tabPane.getSelectionModel().isSelected(0));
        assertTrue(tabPane.getSelectionModel().isSelected(1));
        tabPane.getSelectionModel().selectPrevious();
        assertTrue(tabPane.getSelectionModel().isSelected(0));
        assertFalse(tabPane.getSelectionModel().isSelected(1));
    }
}