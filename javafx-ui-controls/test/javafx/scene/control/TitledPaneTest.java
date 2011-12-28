/*
 * Copyright (c) 2010, Oracle and/or its affiliates. All rights reserved.
 */

package javafx.scene.control;

import com.sun.javafx.css.StyleableProperty;
import static javafx.scene.control.ControlTestUtils.*;
import com.sun.javafx.pgstub.StubToolkit;
import com.sun.javafx.tk.Toolkit;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import static org.junit.Assert.*;


import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author srikalyc
 */
public class TitledPaneTest {
    private TitledPane titledPane;//Empty string
    private TitledPane titledPaneWithTitleAndNode;//With title And Node
    private Node node;
    private Toolkit tk;

    @Before public void setup() {
        node = new Rectangle();
        tk = (StubToolkit)Toolkit.getToolkit();//This step is not needed (Just to make sure StubToolkit is loaded into VM)
        titledPane = new TitledPane();
        titledPaneWithTitleAndNode = new TitledPane("title", node);
    }
    
   
   
    /*********************************************************************
     * Tests for default values                                         *
     ********************************************************************/
    
    @Test public void defaultConstructorShouldSetStyleClassTo_titledpane() {
        assertStyleClassContains(titledPane, "titled-pane");
    }
    
    @Test public void defaultConstructorShouldEmptyTitleAndNullContent() {
        assertEquals(titledPane.textProperty().get(), "");
        assertNull(titledPane.contentProperty().get());
    }
    
    @Test public void twoArgConstructorShouldSetStyleClassTo_titledpane() {
        assertStyleClassContains(titledPane, "titled-pane");
    }
    
    @Test public void twoArgConstructorShouldEmptyTitleAndNullContent() {
        assertEquals(titledPaneWithTitleAndNode.textProperty().get(), "title");
        assertSame(titledPaneWithTitleAndNode.contentProperty().getValue(), node);
    }
    
    @Test public void defaultExpandedIsTrue() {
        assertTrue(titledPane.isExpanded());
    }

    @Test public void defaultAnimated() {
        assertTrue(titledPane.isAnimated());
    }

    @Test public void defaultCollapsible() {
        assertTrue(titledPane.isCollapsible());
    }


    /*********************************************************************
     * Tests for property binding                                        *
     ********************************************************************/
    
    @Test public void checkContentPropertyBind() {
        ObjectProperty objPr = new SimpleObjectProperty<Node>(null);
        titledPane.contentProperty().bind(objPr);
        assertEquals("ContentProperty cannot be bound", titledPane.contentProperty().getValue(), null);
        Node nde = new Rectangle();
        objPr.setValue(nde);
        assertEquals("ContentProperty cannot be bound", titledPane.contentProperty().getValue(), nde);
    }
    
    @Test public void checkExpandedPropertyBind() {
        BooleanProperty objPr = new SimpleBooleanProperty(true);
        titledPane.expandedProperty().bind(objPr);
        assertEquals("Expanded cannot be bound", titledPane.expandedProperty().getValue(), true);
        objPr.setValue(false);
        assertEquals("Expanded cannot be bound", titledPane.expandedProperty().getValue(), false);
    }
    
    @Test public void checkAnimatedPropertyBind() {
        BooleanProperty objPr = new SimpleBooleanProperty(true);
        titledPane.animatedProperty().bind(objPr);
        assertEquals("Animated cannot be bound", titledPane.animatedProperty().getValue(), true);
        objPr.setValue(false);
        assertEquals("Animated cannot be bound", titledPane.animatedProperty().getValue(), false);
    }
    
    @Test public void checkCollapsiblePropertyBind() {
        BooleanProperty objPr = new SimpleBooleanProperty(true);
        titledPane.collapsibleProperty().bind(objPr);
        assertEquals("Collapsible cannot be bound", titledPane.collapsibleProperty().getValue(), true);
        objPr.setValue(false);
        assertEquals("Collapsible cannot be bound", titledPane.collapsibleProperty().getValue(), false);
    }
    

    @Test public void contentPropertyHasBeanReference() {
        assertSame(titledPane, titledPane.contentProperty().getBean());
    }

    @Test public void contenPropertyHasName() {
        assertEquals("content", titledPane.contentProperty().getName());
    }

    @Test public void animatedPropertyHasBeanReference() {
        assertSame(titledPane, titledPane.animatedProperty().getBean());
    }

    @Test public void animatedPropertyHasName() {
        assertEquals("animated", titledPane.animatedProperty().getName());
    }

    @Test public void collapsiblePropertyHasBeanReference() {
        assertSame(titledPane, titledPane.collapsibleProperty().getBean());
    }

    @Test public void collapsiblePropertyHasName() {
        assertEquals("collapsible", titledPane.collapsibleProperty().getName());
    }

    
    /*********************************************************************
     * Check for Pseudo classes                                          *
     ********************************************************************/
    @Test public void settingExpandedTrueSetsPseudoClass() {
        titledPane.setExpanded(true);
        assertPseudoClassExists(titledPane, "expanded");
        assertPseudoClassDoesNotExist(titledPane, "collapsed");
    }

    @Test public void clearingExpandedClearsPseudoClass() {
        titledPane.setExpanded(true);
        titledPane.setExpanded(false);
        assertPseudoClassDoesNotExist(titledPane, "expanded");
        assertPseudoClassExists(titledPane, "collapsed");
    }

    
    /*********************************************************************
     * CSS related Tests                                                 *
     ********************************************************************/
    @Test public void whenAnimatedIsBound_impl_cssSettable_ReturnsFalse() {
        StyleableProperty styleable = StyleableProperty.getStyleableProperty(titledPane.animatedProperty());
        assertTrue(styleable.isSettable(titledPane));
        BooleanProperty other = new SimpleBooleanProperty();
        titledPane.animatedProperty().bind(other);
        assertFalse(styleable.isSettable(titledPane));
    }

    @Test public void whenAnimatedIsSpecifiedViaCSSAndIsNotBound_impl_cssSettable_ReturnsTrue() {
        StyleableProperty styleable = StyleableProperty.getStyleableProperty(titledPane.animatedProperty());
        styleable.set(titledPane, false);
        assertTrue(styleable.isSettable(titledPane));
    }

    @Test public void whenCollapsibleIsBound_impl_cssSettable_ReturnsFalse() {
        StyleableProperty styleable = StyleableProperty.getStyleableProperty(titledPane.collapsibleProperty());
        assertTrue(styleable.isSettable(titledPane));
        BooleanProperty other = new SimpleBooleanProperty();
        titledPane.collapsibleProperty().bind(other);
        assertFalse(styleable.isSettable(titledPane));
    }

    @Test public void whenCollapsibleIsSpecifiedViaCSSAndIsNotBound_impl_cssSettable_ReturnsTrue() {
        StyleableProperty styleable = StyleableProperty.getStyleableProperty(titledPane.collapsibleProperty());
        styleable.set(titledPane, false);
        assertTrue(styleable.isSettable(titledPane));
    }



    /*********************************************************************
     * Miscellaneous Tests                                               *
     ********************************************************************/
    @Test public void setContentAndSeeValueIsReflectedInModel() {
        Node nde = new Rectangle();
        titledPane.setContent(nde);
        assertSame(titledPane.contentProperty().getValue(), nde);
    }
    
    @Test public void setContentAndSeeValue() {
        Node nde = new Rectangle();
        titledPane.setContent(nde);
        assertSame(titledPane.getContent(), nde);
    }
    
    @Test public void setExpandedAndSeeValueIsReflectedInModel() {
        titledPane.setExpanded(true);
        assertTrue(titledPane.expandedProperty().getValue());
    }
    
    @Test public void setExpandedAndSeeValue() {
        titledPane.setExpanded(false);
        assertFalse(titledPane.isExpanded());
    }
    
    @Test public void setAnimatedAndSeeValueIsReflectedInModel() {
        titledPane.setAnimated(true);
        assertTrue(titledPane.animatedProperty().getValue());
    }
    
    @Test public void setAnimatedAndSeeValue() {
        titledPane.setAnimated(false);
        assertFalse(titledPane.isAnimated());
    }
    
    @Test public void setCollapsibleAndSeeValueIsReflectedInModel() {
        titledPane.setCollapsible(true);
        assertTrue(titledPane.collapsibleProperty().getValue());
    }
    
    @Test public void setCollapsibleAndSeeValue() {
        titledPane.setCollapsible(false);
        assertFalse(titledPane.isCollapsible());
    }
    
    
}