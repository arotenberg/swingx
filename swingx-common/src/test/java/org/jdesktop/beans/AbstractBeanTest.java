/*
 * $Id: AbstractBeanTest.java 4086 2011-11-15 21:16:47Z kschaefe $
 *
 * Copyright 2007 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.beans;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

/**
 * Testing AbstractBean.
 * 
 * @author Jeanette Winzenburg
 */
@SuppressWarnings("nls")
public class AbstractBeanTest {
    protected static class CloneableBean extends AbstractBean implements Cloneable {
        public void setProperty(String property) {
            firePropertyChange("property", null, property);
        }
        
        @Override
        public CloneableBean clone() {
            try {
                return (CloneableBean) super.clone();
            } catch (CloneNotSupportedException e) {
                // will not happen
            }
            return null;
        }
    }
    
    /**
     * test clone: listener on original must not be registered to clone.
     */
    @Test
    public void testClone() {
        CloneableBean bean = new CloneableBean();
        PropertyChangeListener pcl = mock(PropertyChangeListener.class);
        bean.addPropertyChangeListener(pcl);
        
        String property = "dummy";
        bean.setProperty(property);
        
        ArgumentCaptor<PropertyChangeEvent> captor = ArgumentCaptor.forClass(PropertyChangeEvent.class);
        verify(pcl).propertyChange(captor.capture());
        assertThat(bean, is(captor.getValue().getSource()));
        assertThat("property", is(captor.getValue().getPropertyName()));
        
        CloneableBean clone = bean.clone();
        clone.setProperty("other");
        
        assertThat(0, is(clone.getPropertyChangeListeners().length));
        verifyNoMoreInteractions(pcl);
    }
}
