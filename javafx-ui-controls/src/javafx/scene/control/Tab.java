/*
 * Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved.
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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.Node;

import com.sun.javafx.event.EventHandlerManager;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

/**
 * <p>Tabs are placed within a {@link TabPane}, where each tab represents a single
 * 'page'.</p>
 * <p>Tabs can contain any {@link Node} such as UI controls or groups
 * of nodes added to a layout container.</p>
 * <p>When the user clicks
 * on a Tab in the TabPane the Tab content becomes visible to the user.</p>
 */
public class Tab implements EventTarget {

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a tab with no title.
     */
    public Tab() {
        this(null);
    }

    /**
     * Creates a tab with a text title.
     *
     * @param text The title of the tab.
     */
    public Tab(String text) {
        setText(text);
        styleClass.addAll(DEFAULT_STYLE_CLASS);
    }
    

    /***************************************************************************
     *                                                                         *
     * Properties                                                              *
     *                                                                         *
     **************************************************************************/

    private StringProperty id;

    /**
     * Sets the id of this tab. This simple string identifier is useful for
     * finding a specific Tab within the {@code TabPane}. The default value is {@code null}.
     */
    public final void setId(String value) { idProperty().set(value); }

    /**
     * The id of this tab.
     *
     * @return The id of the tab.
     */
    public final String getId() { return id == null ? null : id.get(); }

    /**
     * The id of this tab.
     */
    public final StringProperty idProperty() {
        if (id == null) {
            id = new SimpleStringProperty(this, "id");
        }
        return id;
    }

    private StringProperty style;

    /**
     * A string representation of the CSS style associated with this
     * tab. This is analogous to the "style" attribute of an
     * HTML element. Note that, like the HTML style attribute, this
     * variable contains style properties and values and not the
     * selector portion of a style rule.
     * <p>
     * Parsing this style might not be supported on some limited
     * platforms. It is recommended to use a standalone CSS file instead.
     *     
     */
    public final void setStyle(String value) { styleProperty().set(value); }

    /**
     * The CSS style string associated to this tab.
     *
     * @return The CSS style string associated to this tab.
     */
    public final String getStyle() { return style == null ? null : style.get(); }

    /**
     * The CSS style string associated to this tab.
     */
    public final StringProperty styleProperty() {
        if (style == null) {
            style = new SimpleStringProperty(this, "style");
        }
        return style;
    }
    
    private ReadOnlyBooleanWrapper selected;

    final void setSelected(boolean value) {
        selectedPropertyImpl().set(value);
    }

    /**
     * <p>Represents whether this tab is the currently selected tab,
     * To change the selected Tab use {@code tabPane.getSelectionModel().select()}
     * </p>
     */
    public final boolean isSelected() {
        return selected == null ? false : selected.get();
    }

    /**
     * The currently selected tab.
     */
    public final ReadOnlyBooleanProperty selectedProperty() {
        return selectedPropertyImpl().getReadOnlyProperty();
    }

    private ReadOnlyBooleanWrapper selectedPropertyImpl() {
        if (selected == null) {
            selected = new ReadOnlyBooleanWrapper() {
                @Override protected void invalidated() {
                    if (getOnSelectionChanged() != null) {
                        Event.fireEvent(Tab.this, new Event(SELECTION_CHANGED_EVENT));
                    }
                }

                @Override
                public Object getBean() {
                    return Tab.this;
                }

                @Override
                public String getName() {
                    return "selected";
                }
            };
        }
        return selected;
    }

    private ReadOnlyObjectWrapper<TabPane> tabPane;

    final void setTabPane(TabPane value) {
        tabPanePropertyImpl().set(value);
    }

    /**
     * <p>A reference to the TabPane that contains this tab instance.</p>
     */
    public final TabPane getTabPane() {
        return tabPane == null ? null : tabPane.get();
    }

    /**
     * The TabPane that contains this tab.
     */
    public final ReadOnlyObjectProperty<TabPane> tabPaneProperty() {
        return tabPanePropertyImpl().getReadOnlyProperty();
    }

    private ReadOnlyObjectWrapper<TabPane> tabPanePropertyImpl() {
        if (tabPane == null) {
            tabPane = new ReadOnlyObjectWrapper<TabPane>(this, "tabPane");
        }
        return tabPane;
    }

    private StringProperty text;

    /**
     * <p>Sets the text to show in the tab to allow the user to differentiate between
     * the function of each tab. The text is always visible
     * </p>
     */
    public final void setText(String value) {
        textProperty().set(value);
    }

    /**
     * The text shown in the tab.
     *
     * @return The text shown in the tab.
     */
    public final String getText() {
        return text == null ? null : text.get();
    }

    /**
     * The text shown in the tab.
     */
    public final StringProperty textProperty() {
        if (text == null) {
            text = new SimpleStringProperty(this, "text");
        }
        return text;
    }

    private ObjectProperty<Node> graphic;

    /**
     * <p>Sets the graphic to show in the tab to allow the user to differentiate
     * between the function of each tab. By default the graphic does not rotate
     * based on the TabPane.tabPosition value, but it can be set to rotate by
     * setting TabPane.rotateGraphic to true.</p>
     */
    public final void setGraphic(Node value) {
        graphicProperty().set(value);
    }

    /**
     * The graphic shown in the tab.
     *
     * @return The graphic shown in the tab.
     */
    public final Node getGraphic() {
        return graphic == null ? null : graphic.get();
    }

    /**
     * The graphic in the tab.
     * 
     * @return The graphic in the tab.
     */
    public final ObjectProperty<Node> graphicProperty() {
        if (graphic == null) {
            graphic = new SimpleObjectProperty<Node>(this, "graphic");
        }
        return graphic;
    }

    private ObjectProperty<Node> content;

    /**
     * <p>The content to show within the main TabPane area. The content
     * can be any Node such as UI controls or groups of nodes added
     * to a layout container.</p>
     */
    public final void setContent(Node value) {
        contentProperty().set(value);
    }

    /**
     * <p>The content associated with the tab.</p>
     *
     * @return The content associated with the tab.
     */
    public final Node getContent() {
        return content == null ? null : content.get();
    }

    /**
     * <p>The content associated with the tab.</p>
     */
    public final ObjectProperty<Node> contentProperty() {
        if (content == null) {
            content = new SimpleObjectProperty<Node>(this, "content");
        }
        return content;
    }


    private ObjectProperty<ContextMenu> contextMenu;

    /**
     * <p>Specifies the context menu to show when the user right-clicks on the tab.
     * </p>
     */
    public final void setContextMenu(ContextMenu value) {
        contextMenuProperty().set(value);
    }

    /**
     * The context menu associated with the tab.
     * @return The context menu associated with the tab.
     */
    public final ContextMenu getContextMenu() {
        return contextMenu == null ? null : contextMenu.get();
    }

    /**
     * The context menu associated with the tab.
     */
    public final ObjectProperty<ContextMenu> contextMenuProperty() {
        if (contextMenu == null) {
            contextMenu = new SimpleObjectProperty<ContextMenu>(this, "contextMenu");
        }
        return contextMenu;
    }

    private BooleanProperty closable;

    /**
     * <p>Sets {@code true} if the tab is closable.  If this is set to {@code false},
     * then regardless of the TabClosingPolicy, it will not be
     * possible for the user to close this tab. Therefore, when this
     * property is {@code false}, no 'close' button will be shown on the tab.
     * The default is {@code true}.</p>
     *
     */
    public final void setClosable(boolean value) {
        closableProperty().set(value);
    }

    /**
     * Returns {@code true} if this tab is closable.
     *
     * @return {@code true} if the tab is closable.
     */
    public final boolean isClosable() {
        return closable == null ? true : closable.get();
    }

    /**
     * The closable state for this tab.
     */
    public final BooleanProperty closableProperty() {
        if (closable == null) {
            closable = new SimpleBooleanProperty(this, "closable", true);
        }
        return closable;
    }


    /**
     * <p>Called when the tab becomes selected or unselected.</p>
     */
    public static final EventType<Event> SELECTION_CHANGED_EVENT = new EventType<Event>();
    private ObjectProperty<EventHandler<Event>> onSelectionChanged;

    /**
     * Defines a function to be called when a selection changed has occurred on the tab.
     */
    public final void setOnSelectionChanged(EventHandler<Event> value) {
        onSelectionChangedProperty().set(value);
    }

    /**
     * The event handler that is associated with a selection on the tab.
     *
     * @return The event handler that is associated with a tab selection.
     */
    public final EventHandler<Event> getOnSelectionChanged() {
        return onSelectionChanged == null ? null : onSelectionChanged.get();
    }

    /**
     * The event handler that is associated with a selection on the tab.
     */
    public final ObjectProperty<EventHandler<Event>> onSelectionChangedProperty() {
        if (onSelectionChanged == null) {
            onSelectionChanged = new ObjectPropertyBase<EventHandler<Event>>() {
                @Override protected void invalidated() {
                    setEventHandler(SELECTION_CHANGED_EVENT, get());
                }

                @Override
                public Object getBean() {
                    return Tab.this;
                }

                @Override
                public String getName() {
                    return "onSelectionChanged";
                }
            };
        }
        return onSelectionChanged;
    }

    /**
     * <p>Called when a user closes this tab. This is useful for freeing up memory.</p>
     */
    public static final EventType<Event> CLOSED_EVENT = new EventType<Event>();
    private ObjectProperty<EventHandler<Event>> onClosed;

    /**
     * Defines a function to be called when the tab is closed.
     */
    public final void setOnClosed(EventHandler<Event> value) {
        onClosedProperty().set(value);
    }

    /**
     * The event handler that is associated with the tab when the tab is closed.
     *
     * @return The event handler that is associated with the tab when the tab is closed.
     */
    public final EventHandler<Event> getOnClosed() {
        return onClosed == null ? null : onClosed.get();
    }

    /**
     * The event handler that is associated with the tab when the tab is closed.
     */
    public final ObjectProperty<EventHandler<Event>> onClosedProperty() {
        if (onClosed == null) {
            onClosed = new ObjectPropertyBase<EventHandler<Event>>() {
                @Override protected void invalidated() {
                    setEventHandler(CLOSED_EVENT, get());
                }

                @Override
                public Object getBean() {
                    return Tab.this;
                }

                @Override
                public String getName() {
                    return "onClosed";
                }
            };
        }
        return onClosed;
    }

    private ObjectProperty<Tooltip> tooltip;

    /**
     * <p>Specifies the tooltip to show when the user hovers over the tab.</p>
     */
    public final void setTooltip(Tooltip value) { tooltipProperty().setValue(value); }

    /**
     * The tooltip associated with this tab.
     * @return The tooltip associated with this tab.
     */
    public final Tooltip getTooltip() { return tooltip == null ? null : tooltip.getValue(); }

    /**
     * The tooltip associated with this tab.
     */
    public final ObjectProperty<Tooltip> tooltipProperty() {
        if (tooltip == null) {
            tooltip = new SimpleObjectProperty<Tooltip>(this, "tooltip");
        }
        return tooltip;
    }
    
    private final ObservableList<String> styleClass = FXCollections.observableArrayList();

    /**
     * A list of String identifiers which can be used to logically group
     * Nodes, specifically for an external style engine. This variable is
     * analogous to the "class" attribute on an HTML element and, as such,
     * each element of the list is a style class to which this Node belongs.
     *
     * @see <a href="http://www.w3.org/TR/css3-selectors/#class-html">CSS3 class selectors</a>
     */
    public ObservableList<String> getStyleClass() {
        return styleClass;
    }
    
    private final EventHandlerManager eventHandlerManager =
            new EventHandlerManager(this);

    /**
     * @treatasprivate implementation detail
     * @deprecated This is an internal API that is not intended for use and will be removed in the next version
     */
    @Override
    public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
        return tail.prepend(eventHandlerManager);
    }

    /**
     * @treatasprivate implementation detail
     * @deprecated This is an internal API that is not intended for use and will be removed in the next version
     */
    protected <E extends Event> void setEventHandler(EventType<E> eventType, EventHandler<E> eventHandler) {
        eventHandlerManager.setEventHandler(eventType, eventHandler);
    }

    /***************************************************************************
     *                                                                         *
     * Stylesheet Handling                                                     *
     *                                                                         *
     **************************************************************************/

    private static final String DEFAULT_STYLE_CLASS = "tab";
}