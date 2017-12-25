/*
 * $Id: NumberConverter.java 4097 2011-11-30 19:22:13Z kschaefe $
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
package org.jdesktop.swingx.binding;

import org.jdesktop.beansbinding.Converter;

/**
 * This converter alters the component orientation of a component based on a Boolean value.
 * 
 * <pre>
 * true =&gt; ComponentOrientation.RIGHT_TO_LEFT
 * false =&gt; ComponentOrientation.LEFT_TO_RIGHT
 * </pre>
 * 
 * @author Karl George Schaefer
 */
//TODO can we make this a singleton?
public class NumberConverter extends Converter<Number, Double> {
    /**
     * {@inheritDoc}
     */
    @Override
    public Double convertForward(Number value) {
        return value.doubleValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Number convertReverse(Double value) {
        return value;
    }
}
