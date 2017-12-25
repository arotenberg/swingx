/*
 * $Id: DisplayInfoArrayConverter.java 4097 2011-11-30 19:22:13Z kschaefe $
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
 *
 */
package org.jdesktop.swingx.binding;

import java.lang.reflect.Array;

import org.jdesktop.beansbinding.Converter;
import org.jdesktop.swingx.util.Contract;
 
/**
 * Forward converter from DisplayInfo to array of contained element.
 * 
 * @author Karl George Schaefer
 */
public class DisplayInfoArrayConverter<T> extends Converter<DisplayInfo<T>, T[]> {
    private Class<T> type;
    private T[] empty;
    
    public DisplayInfoArrayConverter(Class<T> type) {
        this.type = Contract.asNotNull(type, "type cannot be null");
        empty = createArray(0);
    }
 
    /**
     * {@inheritDoc}
     * 
     * Implemented to return an array of length 1 containing the info's element, if not
     * null, empty array otherwise. 
     */
    @Override
    public T[] convertForward(DisplayInfo<T> info) {
        if (info.getValue() == null) {
            return empty;
        }
        T[] arr = createArray(1);
        arr[0] = info.getValue();
        return arr;
    }
 
    /**
     * {@inheritDoc} <p>
     * 
     * @throws UnsupportedOperationException.
     */
    @Override
    public DisplayInfo<T> convertReverse(T[] value) {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates and returns an array of the generic type with length i.
     * 
     * @param length the length of the array to create
     * @return array of lengt i
     */
    @SuppressWarnings("unchecked")
    private T[] createArray(int length) {
        return (T[]) Array.newInstance(type, length);
    }


}
