/*
 * $Id: WeakEventListenerListTest.java 3473 2009-08-27 13:17:10Z kleopatra $
 *
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx.event;

import javax.swing.event.ChangeListener;

import junit.framework.TestCase;

import org.jdesktop.test.ChangeReport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for SwingX' EventListenerList. Testing the 
 * mechanics of mutations and returned listener arrays. <p>
 * 
 * PENDING: any way to unit test memory leaks?
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class WeakEventListenerListTest extends TestCase {

    /**
     * Test intial getListenerList
     *
     */
    @Test
    public void testGetListenerListInitial() {
        WeakEventListenerList list = new WeakEventListenerList();
        // must return empty array
        assertNotNull("array must be not null", list.getListenerList());
        assertEquals("array must be empty", 0, list.getListenerList().length);
    }
    
    /**
     * test sequence in array.
     *
     */
    @Test
    public void testGetListenerListAddRemove() {
        WeakEventListenerList list = new WeakEventListenerList();
        // add one changeListener
        ChangeReport changeReport = new ChangeReport();
        list.add(ChangeListener.class, changeReport);
        Object[] array = list.getListenerList();
        assertEquals("array must have 2 elements", 2, array.length);
        assertSame("listener class must be at 0", ChangeListener.class, array[0]);
        assertSame("added listener must be at 1", changeReport, array[1]);
        // add a second
        ChangeReport otherReport = new ChangeReport();
        list.add(ChangeListener.class, otherReport);
        Object[] otherArray = list.getListenerList();
        assertEquals("array must have 4 elements", 4, otherArray.length);
        assertSame(ChangeListener.class, otherArray[0]);
        assertSame(changeReport, otherArray[1]);
        assertSame(ChangeListener.class, otherArray[2]);
        assertSame(otherReport, otherArray[3]);
        list.remove(ChangeListener.class, changeReport);
        Object[] removedArray = list.getListenerList();
        assertEquals("array must have 2 elements", 2, removedArray.length);
        assertSame("listener class must be at 0", ChangeListener.class, removedArray[0]);
        assertSame("added listener must be at 1", otherReport, removedArray[1]);
    }
    
    @Test
    public void testGetListenersInitial() {
        WeakEventListenerList list = new WeakEventListenerList();
        // must return empty array
        assertNotNull("array must be not null", list.getListeners(ChangeListener.class));
        assertEquals("array must be empty", 0, list.getListeners(ChangeListener.class).length);
        
    }

    /**
     * test sequence in array.
     *
     */
    @Test
    public void testGetListenersAddRemove() {
        WeakEventListenerList list = new WeakEventListenerList();
        // add one changeListener
        ChangeReport changeReport = new ChangeReport();
        list.add(ChangeListener.class, changeReport);
        ChangeListener[] array = list.getListeners(ChangeListener.class);
        assertEquals("array must have 2 elements", 1, array.length);
        assertSame("added listener must be at 0", changeReport, array[0]);
        // add a second
        ChangeReport otherReport = new ChangeReport();
        list.add(ChangeListener.class, otherReport);
        ChangeListener[] otherArray = list.getListeners(ChangeListener.class);
        assertEquals("array must have 2 elements", 2, otherArray.length);
        assertSame(changeReport, otherArray[0]);
        assertSame(otherReport, otherArray[1]);
        list.remove(ChangeListener.class, changeReport);
        ChangeListener[] removedArray = list.getListeners(ChangeListener.class);
        assertEquals("array length", 1, removedArray.length);
        assertSame("last added listener must be at 0", otherReport, removedArray[0]);
    }

}
