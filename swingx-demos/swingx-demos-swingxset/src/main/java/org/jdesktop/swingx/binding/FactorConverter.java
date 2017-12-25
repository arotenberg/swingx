/*
 * $Id: FactorConverter.java 4097 2011-11-30 19:22:13Z kschaefe $
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
 *
 * @author Karl George Schaefer
 */
//No speed testing performed on this class, but I bet it's slow
@SuppressWarnings("unchecked")
public class FactorConverter<N extends Number> extends Converter<N, N> {
    private N factor;
    
    public FactorConverter(N factor) {
        factor.getClass(); //null check
        
        this.factor = factor;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public N convertForward(N value) {
        Double d = Double.valueOf(value.doubleValue() * factor.doubleValue());
        
        if (value instanceof Byte) {
            return (N) Byte.valueOf(d.byteValue());
        } else if (value instanceof Short) {
            return (N) Short.valueOf(d.shortValue());
        } else if (value instanceof Integer) {
            return (N) Integer.valueOf(d.intValue());
        } else if (value instanceof Long) {
            return (N) Long.valueOf(d.longValue());
        } else if (value instanceof Float) {
            return (N) Float.valueOf(d.floatValue());
        } else {
            return (N) d;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public N convertReverse(N value) {
        Double d = Double.valueOf(value.doubleValue() / factor.doubleValue());
        
        if (value instanceof Byte) {
            return (N) Byte.valueOf(d.byteValue());
        } else if (value instanceof Short) {
            return (N) Short.valueOf(d.shortValue());
        } else if (value instanceof Integer) {
            return (N) Integer.valueOf(d.intValue());
        } else if (value instanceof Long) {
            return (N) Long.valueOf(d.longValue());
        } else if (value instanceof Float) {
            return (N) Float.valueOf(d.floatValue());
        } else {
            return (N) d;
        }
    }
}
