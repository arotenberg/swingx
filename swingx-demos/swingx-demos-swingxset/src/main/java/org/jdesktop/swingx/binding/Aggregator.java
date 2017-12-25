/*
 * $Id: Aggregator.java 4097 2011-11-30 19:22:13Z kschaefe $
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

import org.jdesktop.beans.AbstractBean;
import org.jdesktop.beansbinding.Property;
import org.jdesktop.beansbinding.PropertyStateEvent;
import org.jdesktop.beansbinding.PropertyStateListener;

/**
 * An {@code Aggregator} is a binding mechanism to convert a collection of source values into one
 * output value. Typically, these case convert source values into {@link java.util.Collection
 * collections}, {@link java.util.Map maps}, or arrays; however, it is possible to aggregate
 * properties of a single object. This allows multiple input sources to create (or modify) a single
 * object.
 * 
 * @author Karl George Schaefer
 * 
 * @param <SV>
 *            the source value; one of the sources being aggregated
 * @param <AV>
 *            the aggregate value; what the sources are converted to
 */
public abstract class Aggregator<SV, AV> extends AbstractBean {
    private class PSL implements PropertyStateListener {

        /**
         * {@inheritDoc}
         */
        public void propertyStateChanged(PropertyStateEvent pse) {
            fireValueChanged();
        }
    }
    
    private final PropertyStateListener psl = new PSL();
    private final Class<SV> sourceType;
    private AV value;
    
    protected Aggregator(Class<SV> sourceType) {
        sourceType.getClass(); // null check
        
        this.sourceType = sourceType;
    }

    private void fireValueChanged() {
        AV oldValue = getValue();
        value = aggregateSourceValues();
        firePropertyChange("value", oldValue, getValue());
    }
    
    protected abstract <S> void addSourceImpl(S object, Property<S, SV> property);
    
    public <S> void addSource(S object, Property<S, SV> property) {
        property.addPropertyStateListener(object, psl);
        
        addSourceImpl(object, property);
        
        fireValueChanged();
    }

    public <S> void removeSource(S object, Property<S, SV> property) {
        property.removePropertyStateListener(object, psl);
        fireValueChanged();
    }
    
    protected abstract AV aggregateSourceValues();
    
    public final Class<SV> getSourceType() {
        return sourceType;
    }
    
    public final AV getValue() {
        return value;
    }
}
