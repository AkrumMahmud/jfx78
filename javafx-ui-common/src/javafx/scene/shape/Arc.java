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
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 */

package javafx.scene.shape;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;

import com.sun.javafx.geom.Arc2D;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.sg.PGArc;
import com.sun.javafx.sg.PGNode;
import com.sun.javafx.tk.Toolkit;


/**
 * The {@code Arc} class represents a 2D arc object, defined by a center point,
 * start angle (in degrees), angular extent (length of the arc in degrees),
 * and an arc type ({@link ArcType#OPEN}, {@link ArcType#CHORD},
 * or {@link ArcType#ROUND}).
 *
 * <p>Example usage: the following code creates an Arc which is centered around
 * 50,50, has a radius of 25 and extends from the angle 45 to the angle 315
 * (270 degrees long), and is round.
 *
<PRE>
import javafx.scene.shape.*;

Arc arc = new Arc();
arc.setCenterX(50.0f);
arc.setCenterY(50.0f);
arc.setRadiusX(25.0f);
arc.setRadiusY(25.0f);
arc.setStartAngle(45.0f);
arc.setLength(270.0f);
arc.setType(ArcType.ROUND);
</PRE>
 *
 * @profile common
 */
public class Arc extends Shape {

    private final Arc2D shape = new Arc2D();

    static com.sun.javafx.sg.PGArc.ArcType toPGArcType(ArcType type) {
        switch (type) {
        case OPEN:
            return PGArc.ArcType.OPEN;
        case CHORD:
            return PGArc.ArcType.CHORD;
        default:
            return PGArc.ArcType.ROUND;
        }
    }

    /**
     * Creates an empty instance of Arc.
     */
    public Arc() {
    }

    /**
     * Creates a new instance of Arc.
     * @param centerX the X coordinate of the center point of the arc
     * @param centerY the Y coordinate of the center point of the arc
     * @param radiusX the overall width (horizontal radius) of the full ellipse
     * of which this arc is a partial section
     * @param radiusY the overall height (vertical radius) of the full ellipse
     * of which this arc is a partial section
     * @param startAngle the starting angle of the arc in degrees
     * @param length the angular extent of the arc in degrees
     */
    public Arc(double centerX, double centerY, double radiusX, double radiusY, double startAngle, double length) {
        setCenterX(centerX);
        setCenterY(centerY);
        setRadiusX(radiusX);
        setRadiusY(radiusY);
        setStartAngle(startAngle);
        setLength(length);
    }

    /**
     * Defines the X coordinate of the center point of the arc.
     *
     * @profile common
     * @defaultvalue 0.0
     */
    private DoubleProperty centerX;



    public final void setCenterX(double value) {
        centerXProperty().set(value);
    }

    public final double getCenterX() {
        return centerX == null ? 0.0 : centerX.get();
    }

    public final DoubleProperty centerXProperty() {
        if (centerX == null) {
            centerX = new DoublePropertyBase() {

                @Override
                public void invalidated() {
                    impl_markDirty(DirtyBits.NODE_GEOMETRY);
                    impl_geomChanged();
                }

                @Override
                public Object getBean() {
                    return Arc.this;
                }

                @Override
                public String getName() {
                    return "centerX";
                }
            };
        }
        return centerX;
    }

    /**
     * Defines the Y coordinate of the center point of the arc.
     *
     * @profile common
     * @defaultvalue 0.0
     */
    private DoubleProperty centerY;



    public final void setCenterY(double value) {
        centerYProperty().set(value);
    }

    public final double getCenterY() {
        return centerY == null ? 0.0 : centerY.get();
    }

    public final DoubleProperty centerYProperty() {
        if (centerY == null) {
            centerY = new DoublePropertyBase() {

                @Override
                public void invalidated() {
                    impl_markDirty(DirtyBits.NODE_GEOMETRY);
                    impl_geomChanged();
                }

                @Override
                public Object getBean() {
                    return Arc.this;
                }

                @Override
                public String getName() {
                    return "centerY";
                }
            };
        }
        return centerY;
    }

    /**
     * Defines the overall width (horizontal radius) of the full ellipse
     * of which this arc is a partial section.
     *
     * @profile common
     * @defaultvalue 0.0
     */
    private DoubleProperty radiusX;



    public final void setRadiusX(double value) {
        radiusXProperty().set(value);
    }

    public final double getRadiusX() {
        return radiusX == null ? 0.0 : radiusX.get();
    }

    public final DoubleProperty radiusXProperty() {
        if (radiusX == null) {
            radiusX = new DoublePropertyBase() {

                @Override
                public void invalidated() {
                    impl_markDirty(DirtyBits.NODE_GEOMETRY);
                    impl_geomChanged();
                }

                @Override
                public Object getBean() {
                    return Arc.this;
                }

                @Override
                public String getName() {
                    return "radiusX";
                }
            };
        }
        return radiusX;
    }

    /**
     * Defines the overall height (veritcal radius) of the full ellipse
     * of which this arc is a partial section.
     *
     * @profile common
     * @defaultvalue 0.0
     */
    private DoubleProperty radiusY;



    public final void setRadiusY(double value) {
        radiusYProperty().set(value);
    }

    public final double getRadiusY() {
        return radiusY == null ? 0.0 : radiusY.get();
    }

    public final DoubleProperty radiusYProperty() {
        if (radiusY == null) {
            radiusY = new DoublePropertyBase() {

                @Override
                public void invalidated() {
                    impl_markDirty(DirtyBits.NODE_GEOMETRY);
                    impl_geomChanged();
                }

                @Override
                public Object getBean() {
                    return Arc.this;
                }

                @Override
                public String getName() {
                    return "radiusY";
                }
            };
        }
        return radiusY;
    }

    /**
     * Defines the starting angle of the arc in degrees.
     *
     * @profile common
     * @defaultvalue 0.0
     */
    private DoubleProperty startAngle;



    public final void setStartAngle(double value) {
        startAngleProperty().set(value);
    }

    public final double getStartAngle() {
        return startAngle == null ? 0.0 : startAngle.get();
    }

    public final DoubleProperty startAngleProperty() {
        if (startAngle == null) {
            startAngle = new DoublePropertyBase() {

                @Override
                public void invalidated() {
                    impl_markDirty(DirtyBits.NODE_GEOMETRY);
                    impl_geomChanged();
                }

                @Override
                public Object getBean() {
                    return Arc.this;
                }

                @Override
                public String getName() {
                    return "startAngle";
                }
            };
        }
        return startAngle;
    }

    /**
     * Defines the angular extent of the arc in degrees.
     *
     * @profile common
     * @defaultvalue 0.0
     */
    private DoubleProperty length;



    public final void setLength(double value) {
        lengthProperty().set(value);
    }

    public final double getLength() {
        return length == null ? 0.0 : length.get();
    }

    public final DoubleProperty lengthProperty() {
        if (length == null) {
            length = new DoublePropertyBase() {

                @Override
                public void invalidated() {
                    impl_markDirty(DirtyBits.NODE_GEOMETRY);
                    impl_geomChanged();
                }

                @Override
                public Object getBean() {
                    return Arc.this;
                }

                @Override
                public String getName() {
                    return "length";
                }
            };
        }
        return length;
    }

    /**
     * Defines the closure type for the arc:
     * {@link ArcType#OPEN}, {@link ArcType#CHORD},or {@link ArcType#ROUND}.
     *
     * @profile common
     * @defaultvalue OPEN
     */
    private ObjectProperty<ArcType> type;



    public final void setType(ArcType value) {
        typeProperty().set(value);
    }

    public final ArcType getType() {
        return type == null ? ArcType.OPEN : type.get();
    }

    public final ObjectProperty<ArcType> typeProperty() {
        if (type == null) {
            type = new ObjectPropertyBase<ArcType>(ArcType.OPEN) {

                @Override
                public void invalidated() {
                    impl_markDirty(DirtyBits.NODE_GEOMETRY);
                    impl_geomChanged();
                }

                @Override
                public Object getBean() {
                    return Arc.this;
                }

                @Override
                public String getName() {
                    return "type";
                }
            };
        }
        return type;
    }

    /**
     * @treatasprivate implementation detail
     * @deprecated This is an internal API that is not intended for use and will be removed in the next version
     */
    @Deprecated
    @Override protected PGNode impl_createPGNode() {
        return Toolkit.getToolkit().createPGArc();
    }

    PGArc getPGArc() {
        return (PGArc) impl_getPGNode();
    }

    /**
     * @treatasprivate implementation detail
     * @deprecated This is an internal API that is not intended for use and will be removed in the next version
     */
    @Deprecated
    @Override public Arc2D impl_configShape() {
        short tmpType;
        switch (getType()) {
        case OPEN:
            tmpType = 0;
            break;
        case CHORD:
            tmpType = 1;
            break;
        default:
            tmpType = 2;
            break;
        }

        shape.setArc(
            (float)(getCenterX() - getRadiusX()), // x
            (float)(getCenterY() - getRadiusY()), // y
            (float)(getRadiusX() * 2.0), // w
            (float)(getRadiusY() * 2.0), // h
            (float)getStartAngle(),
            (float)getLength(),
            tmpType);

        return shape;
    }

    /**
     * @treatasprivate implementation detail
     * @deprecated This is an internal API that is not intended for use and will be removed in the next version
     */
    @Deprecated
    @Override public void impl_updatePG() {
        super.impl_updatePG();

        if (impl_isDirty(DirtyBits.NODE_GEOMETRY)) {
            PGArc peer = getPGArc();
             peer.updateArc((float)getCenterX(),
                (float)getCenterY(),
                (float)getRadiusX(),
                (float)getRadiusY(),
                (float)getStartAngle(),
                (float)getLength(),
                toPGArcType(getType()));
        }
    }
}