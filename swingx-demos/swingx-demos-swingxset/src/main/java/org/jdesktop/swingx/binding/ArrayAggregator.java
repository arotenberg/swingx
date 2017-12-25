/*
 * $Id: ArrayAggregator.java 4097 2011-11-30 19:22:13Z kschaefe $
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

import java.lang.reflect.Array;
import java.util.LinkedHashSet;
import java.util.Set;

import org.jdesktop.beansbinding.Property;

/**
 * An aggregator that converts source properties into an array.
 * 
 * @author Karl George Schaefer
 */
public class ArrayAggregator<V> extends Aggregator<V, V[]> {
    private static class PropertyEntry {
        private final Object o;
        private final Property p;
        
        public PropertyEntry(Object o, Property p) {
            o.getClass(); // null check
            p.getClass(); // null check
            
            this.o = o;
            this.p = p;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            
            result = prime * result + o.hashCode();
            result = prime * result + p.hashCode();
            
            return result;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object obj) {
            return obj instanceof PropertyEntry 
                    && o.equals(((PropertyEntry) obj).o)
                    && p.equals(((PropertyEntry) obj).p);
        }
    }
    
    private final Set<PropertyEntry> entries;

    /**
     * Creates an array aggregator for the specified source type.
     * 
     * @param sourceType
     *            the type to create the aggregator for
     */
    public ArrayAggregator(Class<V> sourceType) {
        super(sourceType);
        
        entries = new LinkedHashSet<PropertyEntry>();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    protected V[] aggregateSourceValues() {
        V[] sourceValues = (V[]) Array.newInstance(getSourceType(), entries.size());

        {
            int i = 0;
            
            for (PropertyEntry entry : entries) {
                sourceValues[i++] = (V) entry.p.getValue(entry.o);
            }
        }
        
        return sourceValues;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected <S> void addSourceImpl(S object, Property<S, V> property) {
        PropertyEntry entry = new PropertyEntry(object, property);
        
        if (entries.contains(entry)) {
            entries.remove(entry);
        }
        
        entries.add(entry);
    }
}
