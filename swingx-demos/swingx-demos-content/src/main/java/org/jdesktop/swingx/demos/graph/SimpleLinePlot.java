/*
 * $Id: SimpleLinePlot.java 4097 2011-11-30 19:22:13Z kschaefe $
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
package org.jdesktop.swingx.demos.graph;

import org.jdesktop.swingx.JXGraph.Plot;

/**
 * A simple graph plot that can be updated.
 * 
 * @author Karl George Schaefer
 */
public class SimpleLinePlot extends Plot {
    private double coefficient = 1.0;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public double compute(double value) {
        return value * getCoefficient();
    }

    /**
     * @param coefficient the coefficient to set
     */
    public void setCoefficient(double coefficient) {
        double oldValue = getCoefficient();
        this.coefficient = coefficient;
        firePropertyChange("coefficient", oldValue, getCoefficient());
    }

    /**
     * @return the coefficient
     */
    public double getCoefficient() {
        return coefficient;
    }

}
