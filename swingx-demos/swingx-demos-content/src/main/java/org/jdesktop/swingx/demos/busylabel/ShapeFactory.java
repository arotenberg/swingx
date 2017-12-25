/*
 * $Id: ShapeFactory.java 4097 2011-11-30 19:22:13Z kschaefe $
 *
 * Copyright 2009 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jdesktop.swingx.demos.busylabel;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

/**
 * A factory for shapes used by the busy painter in the busy label demo.
 * 
 * @author Karl George Schaefer
 * @author rah003 (original JXBusyLabelDemoPanel)
 */
public class ShapeFactory {
    private static final float CNSTS = 50.0f;
    
    private ShapeFactory() {
        //does nothing
    }
    
    public static Shape createEllipticalPoint(int xFactor, int yFactor) {
        return new Ellipse2D.Float(0f, 0f, xFactor * CNSTS / 100f, yFactor * CNSTS / 100f);
    }
    
    public static Shape createLinearPoint(int xFactor, int yFactor) {
        return new Rectangle2D.Float(0f, 0f, xFactor * CNSTS / 100f, 1f);
    }
    
    public static Shape createSquarePoint(int xFactor, int yFactor) {
        return new Rectangle2D.Float(0f, 0f, xFactor * CNSTS / 100f, yFactor * CNSTS / 100f);
    }
    
    public static Shape createRectangularPoint(int xFactor, int yFactor) {
        return new Rectangle2D.Float(0f, 0f, xFactor * CNSTS / 100f, yFactor * CNSTS / 500f);
    }
    
    public static Shape createRoundedRectangularPoint(int xFactor, int yFactor) {
        return new RoundRectangle2D.Float(0f, 0f, xFactor * CNSTS / 100f,
                yFactor * CNSTS / 500f, CNSTS / 5f, CNSTS / 5f);
    }

    public static Shape createEllipticalPath(int barLength, int width, int height, int xFactor, int yFactor) {
        return new Ellipse2D.Float(barLength / 2, barLength / 2,
                (width - barLength) * xFactor, (height - barLength)
                * yFactor);
    }
    
//    public static Shape createRectangularPath() {
//        return new Rectangle2D.Float(barLength / 2,
//                barLength / 2, (width - barLength) * xFactor,
//                (height - barLength) * yFactor);
//    }
//    
//    public static Shape createRoundedRectangularPath() {
//        return new RoundRectangle2D.Float(barLength / 2,
//                barLength / 2, (width - barLength) * xFactor,
//                (height - barLength) * yFactor, 10, 10);
//    }
//    
//    public static Shape createQuadPath() {
//        GeneralPath gp = new GeneralPath();
//        float hh = ((float) height) / 2;
//        float wh = ((float) width) / 2;
//        gp.moveTo(barLength, hh);
//        gp.quadTo(wh / 2, 0, wh * 2 - barLength, hh);
//        gp.quadTo(wh / 2, hh * 2f, barLength, hh);
//        return gp;
//    }
//    
//    public static Shape createLinearPath() {
//        return new Rectangle2D.Float(barLength / 2,
//                barLength / 2, (width - barLength) * xFactor, 1);
//    }
}
