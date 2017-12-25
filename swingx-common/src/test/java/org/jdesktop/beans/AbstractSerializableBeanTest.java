/*
 * $Id: AbstractSerializableBeanTest.java 4088 2011-11-17 19:53:49Z kschaefe $
 *
 * Copyright 2008 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.beans;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.Serializable;

import junit.framework.TestCase;

import org.jdesktop.test.SerializableSupport;
import org.junit.Test;

/**
 * @author <a href="mailto:jesse@swank.ca">Jesse Wilson</a>
 */
@SuppressWarnings({"nls", "serial"})
public class AbstractSerializableBeanTest {
    private static class ShoeSizeCap implements VetoableChangeListener, Serializable {
        @Override
        public void vetoableChange(PropertyChangeEvent event) throws PropertyVetoException {
            if("size".equals(event.getPropertyName()) && ((Integer)event.getNewValue()).intValue() > 13) {
                throw new PropertyVetoException("Feet too big!", event);
            }
        }
    }

    private static class ShoeColorListener implements PropertyChangeListener, Serializable {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            //does nothing
        }
    }
    
    public static class Shoe extends AbstractSerializableBean {
        private Color color;
        private int size;

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) throws PropertyVetoException {
            fireVetoableChange("color", this.color, color);
            Color oldColor = getColor();
            this.color = color;
            firePropertyChange("color", oldColor, getColor());
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) throws PropertyVetoException {
            fireVetoableChange("size", this.size, size);
            int oldSize = getSize();
            this.size = size;
            firePropertyChange("size", oldSize, getSize());
        }
    }

    @Test
    public void testShoeSerialize() throws Exception {

        Shoe leftShoe = new Shoe();
        leftShoe.addVetoableChangeListener(new ShoeSizeCap());
        leftShoe.addPropertyChangeListener(new ShoeColorListener());
        leftShoe.setColor(Color.GREEN);
        leftShoe.setSize(10);
        try {
            leftShoe.setSize(20);
            fail("Expected property veto exception");
        } catch(PropertyVetoException e) {
            // expected
        }

        Shoe rightShoe = SerializableSupport.serialize(leftShoe);
        assertEquals(Color.GREEN, rightShoe.getColor());
        
        PropertyChangeListener[] pcls = rightShoe.getPropertyChangeListeners();
        assertEquals(1, pcls.length);
        assertTrue(pcls[0] instanceof ShoeColorListener);
        
        assertEquals(10, rightShoe.getSize());
        
        try {
            rightShoe.setSize(20);
            fail("Expected property veto exception");
        } catch(PropertyVetoException e) {
            // expected
        }
    }
}
