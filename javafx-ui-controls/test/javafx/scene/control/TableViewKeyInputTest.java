/*
 * Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved.
 */
package javafx.scene.control;

import com.sun.javafx.scene.control.behavior.ListViewAnchorRetriever;
import com.sun.javafx.scene.control.behavior.TableViewAnchorRetriever;
import static org.junit.Assert.*;

import java.util.List;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

import javafx.stage.Stage;
import org.junit.After;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("Disabling tests as they fail with OOM in continuous builds")
public class TableViewKeyInputTest {
    private TableView<String> tableView;
    private TableView.TableViewSelectionModel<String> sm;
    private TableView.TableViewFocusModel<String> fm;
    
    private KeyEventFirer keyboard;
    
    private Stage stage;
    private Scene scene;
    private Group group;
    
    private final TableColumn<String, String> col1 = new TableColumn<String, String>();
    private final TableColumn<String, String> col2 = new TableColumn<String, String>();
    private final TableColumn<String, String> col3 = new TableColumn<String, String>();
    private final TableColumn<String, String> col4 = new TableColumn<String, String>();
    private final TableColumn<String, String> col5 = new TableColumn<String, String>();
    
    @Before public void setup() {
        tableView = new TableView<String>();
        sm = tableView.getSelectionModel();
        fm = tableView.getFocusModel();
        
        sm.setSelectionMode(SelectionMode.MULTIPLE);
        sm.setCellSelectionEnabled(false);
        
        tableView.getItems().setAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
        tableView.getColumns().setAll(col1, col2, col3, col4, col5);
        
        sm.clearAndSelect(0);
        
        keyboard = new KeyEventFirer(tableView);
        
        group = new Group();
        scene = new Scene(group);
        
        stage = new Stage();
        stage.setScene(scene);
        
        group.getChildren().setAll(tableView);
        stage.show();
    }
    
    @After public void tearDown() {
        stage.hide();
    }
    
    /***************************************************************************
     * Util methods
     **************************************************************************/
    
    private String debug() {
        StringBuilder sb = new StringBuilder("Selected Cells: [");
        
        List<TablePosition> cells = sm.getSelectedCells();
        for (TablePosition tp : cells) {
            sb.append("(");
            sb.append(tp.getRow());
            sb.append(",");
            sb.append(tp.getColumn());
            sb.append("), ");
        }
        
        sb.append("] \nFocus: (" + fm.getFocusedCell().getRow() + ", " + fm.getFocusedCell().getColumn() + ")");
        sb.append(" \nAnchor: (" + getAnchor().getRow() + ", " + getAnchor().getColumn() + ")");
        return sb.toString();
    }
    
    // Returns true if ALL indices are selected
    private boolean isSelected(int... indices) {
        for (int index : indices) {
            if (! sm.isSelected(index)) return false;
        }
        return true;
    }
    
    // Returns true if ALL indices are NOT selected
    private boolean isNotSelected(int... indices) {
        for (int index : indices) {
            if (sm.isSelected(index)) return false;
        }
        return true;
    }
    
    private TablePosition getAnchor() {
        return TableViewAnchorRetriever.getAnchor(tableView);
    }
    
    private boolean isAnchor(int row) {
        TablePosition tp = new TablePosition(tableView, row, null);
        return getAnchor() != null && getAnchor().equals(tp);
    }
    
    private boolean isAnchor(int row, int col) {
        TablePosition tp = new TablePosition(tableView, row, tableView.getColumns().get(col));
        return getAnchor() != null && getAnchor().equals(tp);
    }
    
    /***************************************************************************
     * General tests
     **************************************************************************/    
    
    @Test public void testInitialState() {
        assertTrue(sm.isSelected(0));
        assertEquals(1, sm.getSelectedIndices().size());
        assertEquals(1, sm.getSelectedItems().size());
    }
    
    
    /***************************************************************************
     * Tests for row-based single selection
     **************************************************************************/
    
    @Test public void testDownArrowChangesSelection() {
        sm.clearAndSelect(0);
        keyboard.doDownArrowPress();
        assertFalse(sm.isSelected(0));
        assertTrue(sm.isSelected(1));
    }
    
    @Test public void testDownArrowDoesNotChangeSelectionWhenAtLastIndex() {
        int endIndex = tableView.getItems().size() - 1;
        sm.clearAndSelect(endIndex);
        assertTrue(sm.isSelected(endIndex));
        keyboard.doDownArrowPress();
        assertTrue(sm.isSelected(endIndex));
    }
    
    @Test public void testUpArrowDoesNotChangeSelectionWhenAt0Index() {
        sm.clearAndSelect(0);
        keyboard.doUpArrowPress();
        testInitialState();
    }
    
    @Test public void testUpArrowChangesSelection() {
        sm.clearAndSelect(1);
        keyboard.doUpArrowPress();
        assertFalse(sm.isSelected(1));
        assertTrue(sm.isSelected(0));
    }
    
    @Test public void testLeftArrowDoesNotChangeState() {
        keyboard.doLeftArrowPress();
        testInitialState();
    }
    
    @Test public void testRightArrowDoesNotChangeState() {
        keyboard.doRightArrowPress();
        testInitialState();
    }
    
    // test 19
    @Test public void testCtrlDownMovesFocusButLeavesSelectionAlone() {
        assertTrue(fm.isFocused(0));
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
        assertTrue(fm.isFocused(1));
        assertTrue(sm.isSelected(0));
        assertFalse(sm.isSelected(1));
    }
    
    // test 20
    @Test public void testCtrlUpDoesNotMoveFocus() {
        assertTrue(fm.isFocused(0));
        keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
        assertTrue(fm.isFocused(0));
        assertTrue(sm.isSelected(0));
    }
    
    // test 21
    @Test public void testCtrlLeftDoesNotMoveFocus() {
        assertTrue(fm.isFocused(0));
        keyboard.doLeftArrowPress(KeyModifier.getShortcutKey());
        assertTrue(fm.isFocused(0));
        assertTrue(sm.isSelected(0));
    }
    
    // test 22
    @Test public void testCtrlRightDoesNotMoveFocus() {
        assertTrue(fm.isFocused(0));
        keyboard.doRightArrowPress(KeyModifier.getShortcutKey());
        assertTrue(fm.isFocused(0));
        assertTrue(sm.isSelected(0));
    }
    
    // test 23
    @Test public void testCtrlUpMovesFocus() {
        sm.clearAndSelect(1);
        assertTrue(fm.isFocused(1));
        assertTrue(sm.isSelected(1));
        keyboard.doUpArrowPress(KeyModifier.getShortcutKey());
        assertTrue(fm.isFocused(0));
        assertTrue(sm.isSelected(1));
    }
    
    // test 24
    @Test public void testCtrlDownDoesNotMoveFocusWhenAtLastIndex() {
        int endIndex = tableView.getItems().size() - 1;
        sm.clearAndSelect(endIndex);
        assertTrue(fm.isFocused(endIndex));
        assertTrue(sm.isSelected(endIndex));
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
        assertTrue(fm.isFocused(endIndex));
        assertTrue(sm.isSelected(endIndex));
    }
    
    // test 25
    @Test public void testCtrlDownArrowWithSpaceChangesAnchor() {
        sm.clearAndSelect(0);
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());    // move focus to 1
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());    // move focus to 2
        keyboard.doKeyPress(KeyCode.SPACE, KeyModifier.getShortcutKey());  // select 2
        assertTrue(isSelected(0, 2));
        assertTrue(isNotSelected(1));
        assertTrue(isAnchor(2));
    }
    
    // test 26
    @Test public void testCtrlUpArrowWithSpaceChangesAnchor() {
        sm.clearAndSelect(2);
        keyboard.doUpArrowPress(KeyModifier.getShortcutKey());    // move focus to 1
        keyboard.doUpArrowPress(KeyModifier.getShortcutKey());    // move focus to 0
        keyboard.doKeyPress(KeyCode.SPACE, KeyModifier.getShortcutKey());  // select 0
        assertTrue(isSelected(0, 2));
        assertTrue(isNotSelected(1));
        assertTrue(isAnchor(0));
    }
    
    // test 44
    @Test public void testHomeKey() {
        sm.clearAndSelect(3);
        keyboard.doKeyPress(KeyCode.HOME);
        assertTrue(isSelected(0));
        assertTrue(isNotSelected(1,2,3));
    }
    
    // test 45
    @Test public void testEndKey() {
        sm.clearAndSelect(3);
        keyboard.doKeyPress(KeyCode.END);
        assertTrue(isSelected(tableView.getItems().size() - 1));
        assertTrue(isNotSelected(1,2,3));
    }
    
    // test 53
    @Test public void testCtrlHome() {
        sm.clearAndSelect(5);
        keyboard.doKeyPress(KeyCode.HOME, KeyModifier.getShortcutKey());
        assertTrue(isSelected(5));
        assertTrue(fm.isFocused(0));
    }
    
    // test 54
    @Test public void testCtrlEnd() {
        sm.clearAndSelect(5);
        keyboard.doKeyPress(KeyCode.END, KeyModifier.getShortcutKey());
        assertTrue(isSelected(5));
        assertTrue(fm.isFocused(tableView.getItems().size() - 1));
    }
    
    // test 68
    @Test public void testCtrlSpaceToClearSelection() {
        sm.clearAndSelect(5);
        assertTrue(isSelected(5));
        assertTrue(fm.isFocused(5));
        keyboard.doKeyPress(KeyCode.SPACE, KeyModifier.getShortcutKey());
        assertTrue(isNotSelected(5));
        assertTrue(debug(), fm.isFocused(5));
        assertTrue(isAnchor(5));
    }
    
    
    
    /***************************************************************************
     * Tests for row-based multiple selection
     **************************************************************************/
    
    @Test public void testShiftDownArrowIncreasesSelection() {
        sm.clearAndSelect(0);
        keyboard.doDownArrowPress(KeyModifier.SHIFT);
        assertTrue(sm.isSelected(0));
        assertTrue(sm.isSelected(1));
    }
    
    @Test public void testShiftDownArrowDoesNotChangeSelectionWhenAtLastIndex() {
        int endIndex = tableView.getItems().size() - 1;
        sm.clearAndSelect(endIndex);
        assertTrue(sm.isSelected(endIndex));
        keyboard.doDownArrowPress(KeyModifier.SHIFT);
        assertTrue(sm.isSelected(endIndex));
    }
    
    @Test public void testShiftUpArrowIncreasesSelection() {
        sm.clearAndSelect(1);
        keyboard.doUpArrowPress(KeyModifier.SHIFT);
        assertTrue(sm.isSelected(0));
        assertTrue(sm.isSelected(1));
    }
    
    @Test public void testShiftUpArrowWhenAt0Index() {
        sm.clearAndSelect(0);
        keyboard.doUpArrowPress(KeyModifier.SHIFT);
        assertTrue(sm.isSelected(0));
    }
    
    @Test public void testShiftLeftArrowWhenAt0Index() {
        sm.clearAndSelect(0);
        keyboard.doLeftArrowPress(KeyModifier.SHIFT);
        assertTrue(sm.isSelected(0));
        assertFalse(sm.isSelected(1));
    }
    
    @Test public void testShiftRightArrowWhenAt0Index() {
        sm.clearAndSelect(0);
        keyboard.doRightArrowPress(KeyModifier.SHIFT);
        assertTrue(sm.isSelected(0));
        assertFalse(sm.isSelected(1));
    }
    
    @Test public void testShiftDownTwiceThenShiftUp() {
        sm.clearAndSelect(0);
        keyboard.doDownArrowPress(KeyModifier.SHIFT);
        keyboard.doDownArrowPress(KeyModifier.SHIFT);
        keyboard.doUpArrowPress(KeyModifier.SHIFT);
        assertTrue(debug(), sm.isSelected(0));
        assertTrue(sm.isSelected(1));
        assertFalse(sm.isSelected(2));
    }
    
    @Test public void testShiftUpTwiceThenShiftDownFrom0Index() {
        sm.clearAndSelect(0);
        keyboard.doUpArrowPress(KeyModifier.SHIFT);
        keyboard.doUpArrowPress(KeyModifier.SHIFT);
        keyboard.doDownArrowPress(KeyModifier.SHIFT);
        assertTrue(sm.isSelected(0));
        assertTrue(sm.isSelected(1));
        assertFalse(sm.isSelected(2));
    }
    
    @Test public void testShiftLeftTwiceThenShiftRight() {
        sm.clearAndSelect(0);
        keyboard.doLeftArrowPress(KeyModifier.SHIFT);
        keyboard.doLeftArrowPress(KeyModifier.SHIFT);
        keyboard.doRightArrowPress(KeyModifier.SHIFT);
        assertTrue(sm.isSelected(0));
        assertFalse(sm.isSelected(1));
        assertFalse(sm.isSelected(2));
    }
    
    @Test public void testShiftRightTwiceThenShiftLeft() {
        sm.clearAndSelect(0);
        keyboard.doRightArrowPress(KeyModifier.SHIFT);
        keyboard.doRightArrowPress(KeyModifier.SHIFT);
        keyboard.doLeftArrowPress(KeyModifier.SHIFT);
        assertTrue(sm.isSelected(0));
        assertFalse(sm.isSelected(1));
        assertFalse(sm.isSelected(2));
    }
    
    @Test public void testShiftUpTwiceThenShiftDown() {
        sm.clearAndSelect(2);                           // select 2
        keyboard.doUpArrowPress(KeyModifier.SHIFT);     // also select 1
        keyboard.doUpArrowPress(KeyModifier.SHIFT);     // also select 0
        keyboard.doDownArrowPress(KeyModifier.SHIFT);   // deselect 0
        assertFalse(debug(), sm.isSelected(0));
        assertTrue(sm.isSelected(1));
        assertTrue(sm.isSelected(2));
        assertFalse(sm.isSelected(3));
    }
    
    // test 18 from Jindra's testcases.rtf file
    @Test public void testShiftDownTwiceThenShiftUpWhenAtLastIndex() {
        int endIndex = tableView.getItems().size() - 1;
        sm.clearAndSelect(endIndex);
        keyboard.doDownArrowPress(KeyModifier.SHIFT);
        keyboard.doDownArrowPress(KeyModifier.SHIFT);
        keyboard.doUpArrowPress(KeyModifier.SHIFT);
        assertTrue(sm.isSelected(endIndex));
        assertTrue(sm.isSelected(endIndex - 1));
        assertFalse(sm.isSelected(endIndex - 2));
    }
    
    // test 27
    @Test public void testCtrlDownArrowWithSpaceChangesAnchor_extended() {
        sm.clearAndSelect(0);
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());    // move focus to 1
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());    // move focus to 2
        keyboard.doKeyPress(KeyCode.SPACE, KeyModifier.getShortcutKey());  // select 2
        
        keyboard.doUpArrowPress(KeyModifier.getShortcutKey());    // move focus to 1
        keyboard.doUpArrowPress(KeyModifier.getShortcutKey());    // move focus to 0
        keyboard.doKeyPress(KeyCode.SPACE, KeyModifier.getShortcutKey());  // deselect 0
        assertTrue(isSelected(2));
        assertTrue(isNotSelected(0, 1));
        assertTrue(isAnchor(0));
    }
    
    // test 28
    @Test public void testCtrlUpArrowWithSpaceChangesAnchor_extended() {
        sm.clearAndSelect(2);
        keyboard.doUpArrowPress(KeyModifier.getShortcutKey());    // move focus to 1
        keyboard.doUpArrowPress(KeyModifier.getShortcutKey());    // move focus to 0
        keyboard.doKeyPress(KeyCode.SPACE, KeyModifier.getShortcutKey());  // select 0
        
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());    // move focus to 1
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());    // move focus to 2
        keyboard.doKeyPress(KeyCode.SPACE, KeyModifier.getShortcutKey());  // deselect 2
        assertTrue(isSelected(0));
        assertTrue(isNotSelected(1, 2));
        assertTrue(isAnchor(2));
    }
    
    // test 29
    @Test public void testCtrlDownArrowWithSpaceChangesAnchor_extended2() {
        sm.clearAndSelect(0);
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());    // move focus to 1
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());    // move focus to 2
        keyboard.doKeyPress(KeyCode.SPACE, KeyModifier.getShortcutKey());  // select 2
        
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());    // move focus to 3
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());    // move focus to 4
        keyboard.doKeyPress(KeyCode.SPACE, KeyModifier.getShortcutKey());  // select 4
        assertTrue(isSelected(0, 2, 4));
        assertTrue(isNotSelected(1, 3, 5));
        assertTrue(isAnchor(4));
    }
    
    // test 30
    @Test public void testCtrlUpArrowWithSpaceChangesAnchor_extended2() {
        sm.clearAndSelect(4);
        keyboard.doUpArrowPress(KeyModifier.getShortcutKey());    // move focus to 3
        keyboard.doUpArrowPress(KeyModifier.getShortcutKey());    // move focus to 2
        keyboard.doKeyPress(KeyCode.SPACE, KeyModifier.getShortcutKey());  // select 2
        
        keyboard.doUpArrowPress(KeyModifier.getShortcutKey());    // move focus to 1
        keyboard.doUpArrowPress(KeyModifier.getShortcutKey());    // move focus to 0
        keyboard.doKeyPress(KeyCode.SPACE, KeyModifier.getShortcutKey());  // select 0
        assertTrue(isSelected(0, 2, 4));
        assertTrue(isNotSelected(1, 3));
        assertTrue(isAnchor(0));
    }
    
    // test 31
    @Test public void testCtrlDownArrowThenShiftSpaceToSelectRange() {
        sm.clearAndSelect(0);
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());    // move focus to 1
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());    // move focus to 2
        keyboard.doKeyPress(KeyCode.SPACE, KeyModifier.SHIFT);  // select 0,1,2
        assertTrue(isSelected(0, 1, 2));
        assertTrue(isNotSelected(3));
        assertTrue(isAnchor(0));
    }
    
    // test 32
    @Test public void testCtrlUpArrowThenShiftSpaceToSelectRange() {
        sm.clearAndSelect(2);
        keyboard.doUpArrowPress(KeyModifier.getShortcutKey());    // move focus to 1
        keyboard.doUpArrowPress(KeyModifier.getShortcutKey());    // move focus to 0
        keyboard.doKeyPress(KeyCode.SPACE, KeyModifier.SHIFT);  // select 0,1,2
        assertTrue(isSelected(0, 1, 2));
        assertTrue(isNotSelected(3));
        assertTrue(debug(), isAnchor(2));
    }
    
    // test 33
    @Test public void testCtrlDownArrowThenSpaceToChangeSelection() {
        sm.clearAndSelect(0);
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());    // move focus to 1
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());    // move focus to 2
        keyboard.doKeyPress(KeyCode.SPACE, KeyModifier.getShortcutKey());  // select 2, keeping 0 selected
        assertTrue(isSelected(0, 2));
        assertTrue(isNotSelected(1, 3));
        assertTrue(isAnchor(2));
        
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());    // move focus to 3
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());    // move focus to 4
        keyboard.doKeyPress(KeyCode.SPACE, KeyModifier.SHIFT);  // select 2,3,4
        assertTrue(isSelected(2, 3, 4));
        assertTrue(isNotSelected(0, 1));
        assertTrue(isAnchor(2));
    }
    
    // test 34
    @Test public void testCtrlUpArrowThenSpaceToChangeSelection() {
        sm.clearAndSelect(4);
        keyboard.doUpArrowPress(KeyModifier.getShortcutKey());    // move focus to 3
        keyboard.doUpArrowPress(KeyModifier.getShortcutKey());    // move focus to 2
        keyboard.doKeyPress(KeyCode.SPACE, KeyModifier.getShortcutKey());  // select 2, keeping 4 selected
        assertTrue(isSelected(2, 4));
        assertTrue(isNotSelected(0, 1, 3));
        assertTrue(isAnchor(2));
        
        keyboard.doUpArrowPress(KeyModifier.getShortcutKey());    // move focus to 1
        keyboard.doUpArrowPress(KeyModifier.getShortcutKey());    // move focus to 0
        keyboard.doKeyPress(KeyCode.SPACE, KeyModifier.SHIFT);  // select 0,1,2
        assertTrue(isSelected(0, 1, 2));
        assertTrue(isNotSelected(3, 4));
        assertTrue(debug(), isAnchor(2));
    }
    
    // test 35
    @Test public void testCtrlDownTwiceThenShiftDown() {
        sm.clearAndSelect(0);
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());    // move focus to 1
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());    // move focus to 2
        keyboard.doKeyPress(KeyCode.DOWN, KeyModifier.SHIFT);  // select 0,1,2,3
        assertTrue(isSelected(0, 1, 2, 3));
    }
    
    // test 36
    @Test public void testCtrlUpTwiceThenShiftDown() {
        sm.clearAndSelect(3);
        keyboard.doUpArrowPress(KeyModifier.getShortcutKey());    // move focus to 2
        keyboard.doUpArrowPress(KeyModifier.getShortcutKey());    // move focus to 1
        keyboard.doUpArrowPress(KeyModifier.getShortcutKey());    // move focus to 0
        keyboard.doKeyPress(KeyCode.DOWN, KeyModifier.SHIFT);  // select 1,2,3
        assertTrue(isSelected(1, 2, 3));
        assertTrue(isNotSelected(0));
    }
    
    // test 37
    @Test public void testCtrlDownThriceThenShiftUp() {
        sm.clearAndSelect(0);
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());    // move focus to 1
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());    // move focus to 2
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());    // move focus to 3
        keyboard.doKeyPress(KeyCode.UP, KeyModifier.SHIFT);  // select 0,1,2
        assertTrue(isSelected(0, 1, 2));
        assertTrue(isNotSelected(3, 4));
    }
    
    // test 38
    @Test public void testCtrlUpTwiceThenShiftUp() {
        sm.clearAndSelect(3);
        keyboard.doUpArrowPress(KeyModifier.getShortcutKey());    // move focus to 2
        keyboard.doUpArrowPress(KeyModifier.getShortcutKey());    // move focus to 1
        keyboard.doKeyPress(KeyCode.UP, KeyModifier.SHIFT);  // select 0,1,2,3
        assertTrue(isSelected(0, 1, 2, 3));
        assertTrue(isNotSelected(4));
    }
    
    // test 39
    @Test public void testCtrlDownTwiceThenSpace_extended() {
        sm.clearAndSelect(0);
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());    // move focus to 1
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());    // move focus to 2
        keyboard.doKeyPress(KeyCode.SPACE, KeyModifier.getShortcutKey());  // select 0,2
        assertTrue(isSelected(0, 2));
        assertTrue(isNotSelected(1, 3));
        assertTrue(isAnchor(2));
        
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());    // move focus to 3
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());    // move focus to 4
        keyboard.doDownArrowPress(KeyModifier.SHIFT);   // select 2,3,4,5
        assertTrue(isSelected(2, 3, 4, 5));
        assertTrue(isNotSelected(0, 1));
        assertTrue(isAnchor(2));
    }
    
    // test 40
    @Test public void testCtrlUpTwiceThenSpace_extended() {
        sm.clearAndSelect(5);
        keyboard.doUpArrowPress(KeyModifier.getShortcutKey());    // move focus to 4
        keyboard.doUpArrowPress(KeyModifier.getShortcutKey());    // move focus to 3
        keyboard.doKeyPress(KeyCode.SPACE, KeyModifier.getShortcutKey());  // select 3,5
        assertTrue(isSelected(3,5));
        assertTrue(isNotSelected(0,1,2,4));
        assertTrue(isAnchor(3));
        
        keyboard.doUpArrowPress(KeyModifier.getShortcutKey());    // move focus to 2
        keyboard.doUpArrowPress(KeyModifier.getShortcutKey());    // move focus to 1
        keyboard.doUpArrowPress(KeyModifier.getShortcutKey());    // move focus to 0
        keyboard.doDownArrowPress(KeyModifier.SHIFT);   // select 1,2,3
        assertTrue(isSelected(1,2,3));
        assertTrue(isNotSelected(0,4,5));
        assertTrue(isAnchor(3));
    }
    
    // test 41
    @Test public void testCtrlDownTwiceThenSpace_extended2() {
        sm.clearAndSelect(0);
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());    // move focus to 1
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());    // move focus to 2
        keyboard.doKeyPress(KeyCode.SPACE, KeyModifier.getShortcutKey());  // select 0,2
        assertTrue(isSelected(0,2));
        assertTrue(isNotSelected(1,3,4));
        assertTrue(isAnchor(2));
        
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());    // move focus to 3
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());    // move focus to 4
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());    // move focus to 5
        keyboard.doUpArrowPress(KeyModifier.SHIFT);     // select 2,3,4
        assertTrue(isSelected(2,3,4));
        assertTrue(isNotSelected(0,1,5));
        assertTrue(isAnchor(2));
    }
    
    // test 50
    @Test public void testCtrlDownThenShiftHome() {
        sm.clearAndSelect(0);
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());    // move focus to 1
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());    // move focus to 2
        keyboard.doKeyPress(KeyCode.SPACE, KeyModifier.getShortcutKey());  // select 0,2
        assertTrue(isSelected(0,2));
        assertTrue(isNotSelected(1,3,4));
        assertTrue(isAnchor(2));
        
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());    // move focus to 3
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());    // move focus to 4
        keyboard.doKeyPress(KeyCode.HOME, KeyModifier.SHIFT);
        assertTrue(isSelected(0,1,2));
        assertTrue(isNotSelected(3,4));
        assertTrue(debug(),isAnchor(2));
    }
    
    // test 51
    @Test public void testCtrlUpThenShiftEnd() {
        sm.clearAndSelect(5);
        keyboard.doUpArrowPress(KeyModifier.getShortcutKey());    // move focus to 4
        keyboard.doUpArrowPress(KeyModifier.getShortcutKey());    // move focus to 3
        keyboard.doKeyPress(KeyCode.SPACE, KeyModifier.getShortcutKey());  // select 3,5
        assertTrue(isSelected(3,5));
        assertTrue(isNotSelected(1,2,4));
        assertTrue(isAnchor(3));
        
        keyboard.doUpArrowPress(KeyModifier.getShortcutKey());    // move focus to 2
        keyboard.doUpArrowPress(KeyModifier.getShortcutKey());    // move focus to 1
        keyboard.doKeyPress(KeyCode.END, KeyModifier.SHIFT);
        assertTrue(isSelected(3,4,5,6,7,8,9));
        assertTrue(isNotSelected(0,1,2));
        assertTrue(debug(),isAnchor(3));
    }
    
    // test 42
    @Test public void testCtrlUpTwiceThenSpace_extended2() {
        sm.clearAndSelect(5);
        keyboard.doUpArrowPress(KeyModifier.getShortcutKey());    // move focus to 4
        keyboard.doUpArrowPress(KeyModifier.getShortcutKey());    // move focus to 3
        keyboard.doKeyPress(KeyCode.SPACE, KeyModifier.getShortcutKey());  // select 3,5
        assertTrue(isSelected(3,5));
        assertTrue(isNotSelected(0,1,2,4));
        assertTrue(isAnchor(3));
        
        keyboard.doUpArrowPress(KeyModifier.getShortcutKey());    // move focus to 2
        keyboard.doUpArrowPress(KeyModifier.getShortcutKey());    // move focus to 1
        keyboard.doUpArrowPress(KeyModifier.SHIFT);     // select 0,1,2,3
        assertTrue(isSelected(0,1,2,3));
        assertTrue(isNotSelected(4,5));
        assertTrue(isAnchor(3));
    }
    
    // test 46
    @Test public void testHomeKey_withSelectedItems() {
        sm.clearSelection();
        sm.selectRange(4, 11);
        keyboard.doKeyPress(KeyCode.HOME);
        assertTrue(isSelected(0));
        assertTrue(isNotSelected(1,2,3,4,5,6,7,8,9,10,11));
    }
    
    // test 47
    @Test public void testEndKey_withSelectedItems() {
        sm.clearSelection();
        sm.selectRange(4, 11);
        keyboard.doKeyPress(KeyCode.END);
        assertTrue(isSelected(tableView.getItems().size() - 1));
        assertTrue(isNotSelected(1,2,3,4,5,6,7,8));
    }
    
    // test 48
    @Test public void testShiftHome() {
        sm.clearAndSelect(3);
        keyboard.doKeyPress(KeyCode.HOME, KeyModifier.SHIFT);
        assertTrue(isSelected(0,1,2,3));
        assertTrue(isNotSelected(4,5));
        assertTrue(debug(), isAnchor(3));
    }
    
    // test 49
    @Test public void testShiftEnd() {
        sm.clearAndSelect(3);
        keyboard.doKeyPress(KeyCode.END, KeyModifier.SHIFT);
        assertTrue(isSelected(3,4,5,6,7,8,9));
        assertTrue(isNotSelected(0,1,2));
        assertTrue(debug(), isAnchor(3));
    }
    
    // test 52
    @Test public void testShiftHomeThenShiftEnd() {
        sm.clearAndSelect(5);
        keyboard.doKeyPress(KeyCode.HOME, KeyModifier.SHIFT);
        assertTrue(isSelected(0,1,2,3,4,5));
        assertTrue(isAnchor(5));
        
        keyboard.doKeyPress(KeyCode.END, KeyModifier.SHIFT);
        assertTrue(isSelected(5,6,7,8,9));
        assertTrue(isAnchor(5));
    }
    
    // test 65
    @Test public void testShiftPageUp() {
        sm.clearAndSelect(0);
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
        keyboard.doKeyPress(KeyCode.SPACE, KeyModifier.getShortcutKey());
        assertTrue(isSelected(0,2));
        assertTrue(isAnchor(2));
        
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());
        keyboard.doKeyPress(KeyCode.PAGE_UP, KeyModifier.SHIFT);
        assertTrue(isSelected(0,1,2));
        assertTrue(isAnchor(2));
    }
    
    // test 67
    @Test public void testCtrlAToSelectAll() {
        sm.clearAndSelect(5);
        keyboard.doKeyPress(KeyCode.A, KeyModifier.getShortcutKey());
        assertTrue(isSelected(0,1,2,3,4,5,6,7,8,9));
    }
    
    
    /***************************************************************************
     * Tests for cell-based multiple selection
     **************************************************************************/    
    
    @Ignore("Bug persists")
    @Test public void testSelectionPathDeviationWorks1() {
        // select horizontally, then select two items vertically, then go back
        // in opposite direction
        sm.setCellSelectionEnabled(true);
        sm.clearAndSelect(1, col1);
        
        keyboard.doRightArrowPress(KeyModifier.SHIFT);   // select (1, col2)
        keyboard.doRightArrowPress(KeyModifier.SHIFT);   // select (1, col3)
        keyboard.doDownArrowPress(KeyModifier.SHIFT);    // select (2, col3)
        keyboard.doDownArrowPress(KeyModifier.SHIFT);    // select (3, col3)
        assertTrue(sm.isSelected(1, col3));
        assertTrue(sm.isSelected(2, col3));
        assertTrue(sm.isSelected(3, col3));
        
        keyboard.doUpArrowPress(KeyModifier.SHIFT);    // deselect (3, col3)
        assertTrue(sm.isSelected(1, col3));
        assertTrue(sm.isSelected(2, col3));
        assertFalse(sm.isSelected(3, col3));
        
        keyboard.doUpArrowPress(KeyModifier.SHIFT);    // deselect (2, col3)
        assertTrue(sm.isSelected(1, col3));
        assertFalse(sm.isSelected(2, col3));
        assertFalse(sm.isSelected(3, col3));
        
        keyboard.doUpArrowPress(KeyModifier.SHIFT);    // deselect (1, col3)
        assertFalse(debug(), sm.isSelected(1, col3));
        assertFalse(sm.isSelected(2, col3));
        assertFalse(sm.isSelected(3, col3));
        
        keyboard.doLeftArrowPress(KeyModifier.SHIFT);    // deselect (1, col2)
        assertFalse(sm.isSelected(1, col2));
    }
    
    
    /***************************************************************************
     * Tests for specific bug reports
     **************************************************************************/
    
    @Test public void test_rt18488_selectToLeft() {
        sm.setCellSelectionEnabled(true);
        sm.clearAndSelect(1, col5);
        
        keyboard.doLeftArrowPress(KeyModifier.SHIFT);   // select (1, col4)
        keyboard.doLeftArrowPress(KeyModifier.SHIFT);   // select (1, col3)
        keyboard.doLeftArrowPress(KeyModifier.SHIFT);   // select (1, col2)
        keyboard.doLeftArrowPress(KeyModifier.SHIFT);   // select (1, col1)
        assertTrue(sm.isSelected(1, col5));
        assertTrue(sm.isSelected(1, col4));
        assertTrue(sm.isSelected(1, col3));
        assertTrue(sm.isSelected(1, col2));
        assertTrue(sm.isSelected(1, col1));
        
        keyboard.doRightArrowPress(KeyModifier.SHIFT);   // deselect (1, col1)
        assertTrue(sm.isSelected(1, col5));
        assertTrue(sm.isSelected(1, col4));
        assertTrue(sm.isSelected(1, col3));
        assertTrue(debug(), sm.isSelected(1, col2));
        assertFalse(sm.isSelected(1, col1));
    }
    
    @Test public void test_rt18488_selectToRight() {
        sm.setCellSelectionEnabled(true);
        sm.clearAndSelect(1, col1);
        
        keyboard.doRightArrowPress(KeyModifier.SHIFT);   // select (1, col2)
        keyboard.doRightArrowPress(KeyModifier.SHIFT);   // select (1, col3)
        keyboard.doRightArrowPress(KeyModifier.SHIFT);   // select (1, col4)
        keyboard.doRightArrowPress(KeyModifier.SHIFT);   // select (1, col5)
        assertTrue(sm.isSelected(1, col5));
        assertTrue(sm.isSelected(1, col4));
        assertTrue(sm.isSelected(1, col3));
        assertTrue(sm.isSelected(1, col2));
        assertTrue(sm.isSelected(1, col1));
        
        keyboard.doLeftArrowPress(KeyModifier.SHIFT);   // deselect (1, col5)
        assertFalse(sm.isSelected(1, col5));
        assertTrue(sm.isSelected(1, col4));
        assertTrue(sm.isSelected(1, col3));
        assertTrue(sm.isSelected(1, col2));
        assertTrue(sm.isSelected(1, col1));
    }
    
    @Test public void test_rt18488_comment1() {
        sm.setCellSelectionEnabled(true);
        sm.clearAndSelect(1, col1);
        
        keyboard.doRightArrowPress(KeyModifier.SHIFT);   // select (1, col2)
        keyboard.doRightArrowPress(KeyModifier.SHIFT);   // select (1, col3)
        keyboard.doRightArrowPress(KeyModifier.SHIFT);   // select (1, col4)
        keyboard.doRightArrowPress(KeyModifier.SHIFT);   // select (1, col5)
        keyboard.doDownArrowPress(KeyModifier.SHIFT);    // select (2, col5)
        
        assertTrue(sm.isSelected(2, col5));
        assertTrue(sm.isSelected(1, col5));
        assertTrue(sm.isSelected(1, col4));
        assertTrue(sm.isSelected(1, col3));
        assertTrue(sm.isSelected(1, col2));
        assertTrue(sm.isSelected(1, col1));
        
        keyboard.doUpArrowPress(KeyModifier.SHIFT);     // deselect (2, col5)
        assertFalse(sm.isSelected(2, col5));
        assertTrue(sm.isSelected(1, col5));
        assertTrue(sm.isSelected(1, col4));
        assertTrue(sm.isSelected(1, col3));
        assertTrue(sm.isSelected(1, col2));
        assertTrue(sm.isSelected(1, col1));
    }
    
    @Ignore("Bug persists")
    @Test public void test_rt18536_positive_horizontal() {
        // Test shift selection when focus is elsewhere (so as to select a range)
        sm.setCellSelectionEnabled(true);
        sm.clearAndSelect(1, col1);
        
        // move focus by holding down ctrl button
        keyboard.doRightArrowPress(KeyModifier.getShortcutKey());   // move focus to (1, col2)
        keyboard.doRightArrowPress(KeyModifier.getShortcutKey());   // move focus to (1, col3)
        keyboard.doRightArrowPress(KeyModifier.getShortcutKey());   // move focus to (1, col4)
        keyboard.doRightArrowPress(KeyModifier.getShortcutKey());   // move focus to (1, col5)
        assertTrue(fm.isFocused(1, col5));
        
        // press shift + space to select all cells between (1, col1) and (1, col5)
        keyboard.doKeyPress(KeyCode.SPACE, KeyModifier.SHIFT);
        assertTrue(sm.isSelected(1, col5));
        assertTrue(debug(), sm.isSelected(1, col4));
        assertTrue(sm.isSelected(1, col3));
        assertTrue(sm.isSelected(1, col2));
        assertTrue(sm.isSelected(1, col1));
    }
    
    @Ignore("Bug persists")
    @Test public void test_rt18536_negative_horizontal() {
        // Test shift selection when focus is elsewhere (so as to select a range)
        sm.setCellSelectionEnabled(true);
        sm.clearAndSelect(1, col5);
        
        // move focus by holding down ctrl button
        keyboard.doLeftArrowPress(KeyModifier.getShortcutKey());   // move focus to (1, col4)
        keyboard.doLeftArrowPress(KeyModifier.getShortcutKey());   // move focus to (1, col3)
        keyboard.doLeftArrowPress(KeyModifier.getShortcutKey());   // move focus to (1, col2)
        keyboard.doLeftArrowPress(KeyModifier.getShortcutKey());   // move focus to (1, col1)
        assertTrue(fm.isFocused(1, col1));
        
        // press shift + space to select all cells between (1, col1) and (1, col5)
        keyboard.doKeyPress(KeyCode.SPACE, KeyModifier.SHIFT);
        assertTrue(debug(), sm.isSelected(1, col5));
        assertTrue(sm.isSelected(1, col4));
        assertTrue(sm.isSelected(1, col3));
        assertTrue(sm.isSelected(1, col2));
        assertTrue(sm.isSelected(1, col1));
    }
    
    @Test public void test_rt18536_positive_vertical() {
        // Test shift selection when focus is elsewhere (so as to select a range)
        sm.setCellSelectionEnabled(true);
        sm.clearAndSelect(1, col5);
        
        // move focus by holding down ctrl button
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());   // move focus to (2, col5)
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());   // move focus to (3, col5)
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());   // move focus to (4, col5)
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());   // move focus to (5, col5)
        assertTrue(fm.isFocused(5, col5));
        
        // press shift + space to select all cells between (1, col5) and (5, col5)
        keyboard.doKeyPress(KeyCode.SPACE, KeyModifier.SHIFT);
        assertTrue(sm.isSelected(1, col5));
        assertTrue(sm.isSelected(2, col5));
        assertTrue(sm.isSelected(3, col5));
        assertTrue(sm.isSelected(4, col5));
        assertTrue(sm.isSelected(5, col5));
    }
    
    @Test public void test_rt18536_negative_vertical() {
        // Test shift selection when focus is elsewhere (so as to select a range)
        sm.setCellSelectionEnabled(true);
        sm.clearAndSelect(5, col5);
        
        // move focus by holding down ctrl button
        keyboard.doUpArrowPress(KeyModifier.getShortcutKey());   // move focus to (4, col5)
        keyboard.doUpArrowPress(KeyModifier.getShortcutKey());   // move focus to (3, col5)
        keyboard.doUpArrowPress(KeyModifier.getShortcutKey());   // move focus to (2, col5)
        keyboard.doUpArrowPress(KeyModifier.getShortcutKey());   // move focus to (1, col5)
        assertTrue(fm.isFocused(1, col5));
        
        // press shift + space to select all cells between (1, col5) and (5, col5)
        keyboard.doKeyPress(KeyCode.SPACE, KeyModifier.SHIFT);
        assertTrue(sm.isSelected(1, col5));
        assertTrue(sm.isSelected(2, col5));
        assertTrue(sm.isSelected(3, col5));
        assertTrue(sm.isSelected(4, col5));
        assertTrue(sm.isSelected(5, col5));
    }
    
    @Test public void test_rt18642() {
        sm.setCellSelectionEnabled(false);
        sm.clearAndSelect(1);                          // select 1
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());   // shift focus to 2
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());   // shift focus to 3
        keyboard.doKeyPress(KeyCode.SPACE, KeyModifier.getShortcutKey()); // set anchor, and also select, 3
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());   // shift focus to 4
        keyboard.doDownArrowPress(KeyModifier.getShortcutKey());   // shift focus to 5
        keyboard.doKeyPress(KeyCode.SPACE, KeyModifier.getShortcutKey()); // set anchor, and also select, 5
        
        assertTrue(isSelected(1, 3, 5));
        assertTrue(isNotSelected(0, 2, 4));
        
        // anchor is at 5, so shift+UP should select rows 4 and 5 only
        keyboard.doUpArrowPress(KeyModifier.SHIFT);   
        assertTrue(isSelected(4, 5));
        assertTrue(isNotSelected(0, 1, 2, 3));
    }
}