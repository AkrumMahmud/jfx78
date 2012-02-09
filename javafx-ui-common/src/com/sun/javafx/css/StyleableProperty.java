/*
 * Copyright (c) 2010, 2012, Oracle and/or its affiliates. All rights reserved.
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

package com.sun.javafx.css;

import com.sun.javafx.css.StyleHelper.StyleCacheKey;
import com.sun.javafx.css.converters.FontConverter;
import com.sun.javafx.css.converters.SizeConverter;
import java.lang.ref.Reference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.Node;

import javafx.beans.value.WritableValue;
import javafx.scene.Parent;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;


public abstract class StyleableProperty<N extends Node, V> {
    
    /**
     * Set the value of the corresponding property on the given Node.
     * @param node The node on which the property value is being set
     * @param value The value to which the property is set
     */
    public void set(N node, V value, Stylesheet.Origin origin) {
        final WritableValue<V> writable = getWritableValue(node);
        assert (writable instanceof Property);
        final Property<V> cssProperty = (Property<V>)writable;
        cssProperty.applyStyle(origin, value != null ? value : getInitialValue());            
    }

    /** @deprecated Use {@link StyleableProperty#set(javafx.scene.Node, java.lang.Object, com.sun.javafx.css.Stylesheet.Origin)} */
    public void set(N node, V value) {
        set(node, value, Stylesheet.Origin.USER_AGENT);
    }    

    /**
     * Return the StyleableProperty associated with the given WritableValue. Will
     * return null if the WritableValue is not a Property.
     */
    public static StyleableProperty getStyleableProperty(WritableValue writableValue) {
        if (writableValue instanceof Property) {
            
            return ((Property)writableValue).getStyleableProperty();
        }
        
        return null;
    }
    
    /**
     * Return the Stylesheet.Origin associated with the given WritableValue. Will
     * return null if the WritableValue is not a Property.
     */
    public static Stylesheet.Origin getOrigin(WritableValue writableValue) {
        if (writableValue instanceof Property) {
            
            return ((Property)writableValue).getOrigin();
        }
        
        return null;
    }
    
    public static Styleable createStyleable(final Node node) {
        
        return new Styleable() {

            Styleable parent = null;
            List<StyleableProperty> styleableProperties = null;
            
            @Override
            public String getId() {
                return node.getId();
            }

            @Override
            public List<String> getStyleClass() {
                return node.getStyleClass();
            }
            
            @Override
            public String getStyle() {
                return node.getStyle();
            }

            @Override
            public Styleable getStyleableParent() {
                if (parent == null && node.getParent() != null) {
                    parent = createStyleable(node.getParent());
                };
                return parent;
            }

            @Override
            public StyleHelper getStyleHelper() {
                return node.impl_getStyleHelper();
            }

            @Override
            public Reference<StyleCacheKey> getStyleCacheKey() {
                return node.impl_getStyleCacheKey();
            }

            @Override
            public List<StyleableProperty> getStyleableProperties() {
                if (styleableProperties == null) {
                    styleableProperties = StyleableProperty.getStyleables(node);
                }
                return styleableProperties;
            }

        };
        
    }
    
    /**
     * Return the Styles that match this property for the given Node. The 
     * list is sorted by descending specificity. 
     */
    public List<Style> getMatchingStyles(final Node node) {
        if (node != null) {
            return getMatchingStyles(createStyleable(node));
        }
        return Collections.EMPTY_LIST;        
    }

    public List<Style> getMatchingStyles(Styleable styleable) {
        if (styleable != null) {
            StyleHelper helper = styleable.getStyleHelper();
            return (helper != null) 
                ? helper.getMatchingStyles(styleable, this) 
                : Collections.EMPTY_LIST; 
        }
        return Collections.EMPTY_LIST;
    }
    /**
     * Check to see if the corresponding property on the given node is
     * settable. This method is called before any styles are looked up for the
     * given property. It is abstract so that the code can check if the property 
     * is settable without expanding the property. Generally, the property is 
     * settable if it is not null or is not bound. 
     * 
     * @param node The node on which the property value is being set
     * @return true if the property can be set.
     */
    public abstract boolean isSettable(N node);

    /**
     * Return the corresponding <code>javafx.beans.value.WriteableValue</code> for
     * the given Node. Note that calling this method will cause the property
     * to be expanded. 
     * @param node
     * @return 
     */
    public abstract WritableValue<V> getWritableValue(N node);
    
    private static Map<Class,List<StyleableProperty>> styleablesCache = null;

    private static Method getMethod_impl_CSS_STYLEABLES(final Class nodeClass) {

        return AccessController.doPrivileged(new PrivilegedAction<Method>() {
            @Override public Method run() {
                try {
                    return nodeClass.getDeclaredMethod("impl_CSS_STYLEABLES");
                } catch (NoSuchMethodException nsme) {
                    return null;
                }
            }
        });

    }

    /**
     * 
     * @param node
     * @return 
     */
    public static List<StyleableProperty> getStyleables(final Node node) {
        
        final Class theClass = node.impl_getClassToStyle();
        
        return getStyleables(theClass);
    }
    
    public static List<StyleableProperty> getStyleables(final Class theClass) {
        
        if (styleablesCache != null) {            
            List<StyleableProperty> styleables = styleablesCache.get(theClass);
            if (styleables != null) return styleables;
        }

        // TODO: fix this so it doesn't have to use reflection
        final List<StyleableProperty> styleables = new ArrayList<StyleableProperty>();
        Class clazz = theClass;
        do {

            Method meth = getMethod_impl_CSS_STYLEABLES(clazz);
            if (meth != null) {
                try {
                    styleables.addAll((List<StyleableProperty>)meth.invoke(null));
                    // impl_CSS_STYLEABLES returns the StyleableProperties for
                    // the given class and all of its super classes, so
                    // once we hit a class with that method, there is no
                    // need to keep going.
                    break;
                } catch (IllegalAccessException iae) {
                    // TODO: use logger here
                    System.err.println(iae.toString());
                } catch (InvocationTargetException ite) {
                    // TODO: use logger here
                    System.err.println(ite.toString());
                } catch (ExceptionInInitializerError eiie) {
                    // TODO: use logger here
                    System.err.println(eiie.toString());
                } 
            }

            if (Node.class.equals(clazz)) break;

            clazz = clazz.getSuperclass();

        } while (clazz != null);

        if (styleablesCache == null) {
            styleablesCache = new HashMap<Class,List<StyleableProperty>>();
        }
        styleablesCache.put(theClass, styleables);

        return styleables;
    }

    public static abstract class FONT<T extends Node> extends StyleableProperty<T,Font> {
        
        public FONT(String property, Font initial) {
            super(property, FontConverter.getInstance(), initial, true, createSubProperties(property, initial));
        }
        
        private static List<StyleableProperty> createSubProperties(String property, Font initial) {
            
            Font defaultFont = initial != null ? initial : Font.getDefault();
            
            final StyleableProperty<Node,Size> SIZE = 
                new StyleableProperty<Node,Size>(property.concat("-size"),
                    SizeConverter.getInstance(),
                    new Size(defaultFont.getSize(), SizeUnits.PT),
                    true) {

                @Override
                public boolean isSettable(Node node) {
                    return false;
                }

                @Override
                public WritableValue<Size> getWritableValue(Node node) {
                    return null;
                }
                        
            };

            final StyleableProperty<Node,FontWeight> WEIGHT = 
                new StyleableProperty<Node,FontWeight>(property.concat("-weight"),
                    SizeConverter.getInstance(),
                    FontWeight.NORMAL,
                    true) {

                @Override
                public boolean isSettable(Node node) {
                    return false;
                }

                @Override
                public WritableValue<FontWeight> getWritableValue(Node node) {
                    return null;
                }
                        
            };

            final StyleableProperty<Node,FontPosture> STYLE = 
                new StyleableProperty<Node,FontPosture>(property.concat("-style"),
                    SizeConverter.getInstance(),
                    FontPosture.REGULAR,
                    true) {

                @Override
                public boolean isSettable(Node node) {
                    return false;
                }

                @Override
                public WritableValue<FontPosture> getWritableValue(Node node) {
                    return null;
                }
                        
            };

            final StyleableProperty<Node,String> FAMILY = 
                new StyleableProperty<Node,String>(property.concat("-family"),
                    SizeConverter.getInstance(),
                    defaultFont.getFamily(),
                    true) {

                @Override
                public boolean isSettable(Node node) {
                    return false;
                }

                @Override
                public WritableValue<String> getWritableValue(Node node) {
                    return null;
                }
                        
            };
            
            final List<StyleableProperty> subProperties = new ArrayList<StyleableProperty>();
            Collections.addAll(subProperties, FAMILY, SIZE, STYLE, WEIGHT);
            return Collections.unmodifiableList(subProperties);
            
        }
    }
            
    private final String property;
    /**
     * @return the CSS property name
     */
    public final String getProperty() {
        return property;
    }

    private final StyleConverter converter;
    /**
     * @return The CSS converter that handles conversion from a CSS value to a Java Object
     */
    public final StyleConverter getConverter() {
        return converter;
    }

    private final V initialValue;
    /**
     * @return The initial value of the property, possibly null
     */
    public final V getInitialValue() {
        return initialValue;
    }
    
    private final List<StyleableProperty> subProperties;
    /**
     * The sub-properties refers to the constituent properties of this property,
     * if any. For example, "-fx-font-weight" is sub-property of "-fx-font".
     */
    public final List<StyleableProperty> getSubProperties() {
        return subProperties;
    }

    private final boolean inherits;
    /**
     * If true, the value of this property is the same as
     * the parent's computed value of this property.
     * @default false
     * @see <a href="http://www.w3.org/TR/css3-cascade/#inheritance">CSS Inheritance</a>
     */
    public final boolean isInherits() {
        return inherits;
    }

    /**
     * Construct a StyleableProperty with the given parameters and no sub-properties.
     * @param property the CSS property
     * @param converter the StyleConverter used to convert the CSS parsed value to a Java object.
     * @param initalValue the CSS string 
     * @param inherits true if this property uses CSS inheritance
     * @param subProperties the sub-properties of this property. For example,
     * the -fx-font property has the sub-properties -fx-font-family, 
     * -fx-font-size, -fx-font-weight, and -fx-font-style.
     */
    protected StyleableProperty(
            final String property, 
            final StyleConverter converter, 
            final V initialValue, 
            boolean inherits, 
            final List<StyleableProperty> subProperties) {        
        
        this.property = property;
        this.converter = converter;
        this.initialValue = initialValue;
        this.inherits = inherits;
        this.subProperties = subProperties != null ? Collections.unmodifiableList(subProperties) : null;        
        
        if (this.property == null || this.converter == null) 
            throw new IllegalArgumentException("neither property nor converter can be null");
    }

    /**
     * Construct a StyleableProperty with the given parameters and no sub-properties.
     * @param property the CSS property
     * @param converter the StyleConverter used to convert the CSS parsed value to a Java object.
     * @param initalValue the CSS string 
     * @param inherits true if this property uses CSS inheritance
     */
    protected StyleableProperty(
            final String property, 
            final StyleConverter converter, 
            final V initialValue, 
            boolean inherits) {
        this(property, converter, initialValue, inherits, null);
    }

    /**
     * Construct a StyleableProperty with the given parameters, inherit set to
     * false and no sub-properties.
     * @param property the CSS property
     * @param converter the StyleConverter used to convert the CSS parsed value to a Java object.
     * @param initalValue the CSS string 
     */
    protected StyleableProperty(
            final String property, 
            final StyleConverter converter, 
            final V initialValue) {
        this(property, converter, initialValue, false, null);
    }

    /**
     * Construct a StyleableProperty with the given parameters, initialValue is
     * null, inherit is set to false, and no sub-properties.
     * @param property the CSS property
     * @param converter the StyleConverter used to convert the CSS parsed value to a Java object.
     * @param initalValue the CSS string 
     */
    protected StyleableProperty(
            final String property, 
            final StyleConverter converter) {
        this(property, converter, null, false, null);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StyleableProperty<N, V> other = (StyleableProperty<N, V>) obj;
        if ((this.property == null) ? (other.property != null) : !this.property.equals(other.property)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + (this.property != null ? this.property.hashCode() : 0);
        return hash;
    }
    
    
    @Override public String toString() {
        return  new StringBuilder("StyleableProperty {")
            .append("property: ").append(property)
            .append(", converter: ").append(converter.toString())
            .append(", initalValue: ").append(String.valueOf(initialValue))
            .append(", inherits: ").append(inherits)
            .append(", subProperties: ").append(
                (subProperties != null) ? subProperties.toString() : "[]")
            .append("}").toString();
    }


}