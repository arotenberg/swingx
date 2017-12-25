/*
 * $Id: DaySelectionModelTest.java 3473 2009-08-27 13:17:10Z kleopatra $
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
package org.jdesktop.swingx.calendar;

import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jdesktop.swingx.calendar.DateSelectionModel.SelectionMode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test DaySelectionModel.
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class DaySelectionModelTest extends AbstractTestDateSelectionModel {


    @Before
    public void setUpJ4() throws Exception {
        setUp();
    }
    
    @After
    public void tearDownJ4() throws Exception {
        tearDown();
    }
    
    /**
     * DaySelectionModel normalizes to start of day.
     */
    @Test
    public void testNormalizedDateStartOfDay() {
        assertEquals(startOfDay(today), model.getNormalizedDate(today));
        assertNotSame(startOfDay(today), model.getNormalizedDate(today));
    }


    /**
     * setSelectionInterval must throw NPE if given date is null
     */
    @Test
    public void testSetIntervalNulls() {
        try {
            model.setSelectionInterval(null, null);
            fail("normalizedDate must throw NPE if date is null");
        } catch (NullPointerException e) {
            // expected 
        } catch (Exception e) {
            fail("unexpected exception " + e);
        }
        
    }
    /**
     * setSelectionInterval must throw NPE if given date is null
     */
    @Test
    public void testAddIntervalNulls() {
        try {
            model.addSelectionInterval(null, null);
            fail("normalizedDate must throw NPE if date is null");
        } catch (NullPointerException e) {
            // expected 
        } catch (Exception e) {
            fail("unexpected exception " + e);
        }
        
    }
    
    /**
     * removeSelectionInterval must throw NPE if given date is null
     */
    @Test
    public void testRemoveIntervalNulls() {
        try {
            model.removeSelectionInterval(null, null);
            fail("normalizedDate must throw NPE if date is null");
        } catch (NullPointerException e) {
            // expected 
        } catch (Exception e) {
            fail("unexpected exception " + e);
        }
        
    }

     

    /**
     * Set unselectable and test that all dates of the day are unselectable.
     */
    @Test
    public void testUnselectableDatesCompleteDay() {
        SortedSet<Date> unselectableDates = new TreeSet<Date>();
        unselectableDates.add(today);
        model.setUnselectableDates(unselectableDates);
        // all dates in today must be rejected
        assertTrue("raw today must be unselectable", 
                model.isUnselectableDate(today));
        assertTrue("start of today must be unselectable", 
                model.isUnselectableDate(startOfDay(today)));
        assertTrue("end of today must be unselectable", 
                model.isUnselectableDate(endOfDay(today)));
        // remove the unselectable 
        model.setUnselectableDates(new TreeSet<Date>());
        assertFalse(model.isUnselectableDate(today));
        assertFalse(model.isUnselectableDate(startOfDay(today)));
        assertFalse(model.isUnselectableDate(endOfDay(today)));
    }

    /**
     * Issue #494-swingx: JXMonthView changed all passed-in dates
     *
     */
    @Test
    public void testCleanupCopyDate() {
        Date copy = new Date(today.getTime());
        model.setSelectionInterval(today, today);
        assertEquals("the date used for selection must be unchanged", copy, today);
    }
   
    @Test
    public void testEmptySelectionInitial() {
        assertTrue(model.isSelectionEmpty());
        SortedSet<Date> selection = model.getSelection();
        assertTrue(selection.isEmpty());
    }
    
    @Test
    public void testEmptySelectionClear() {
        model.setSelectionInterval(today, today);
        // sanity
        assertTrue(1 == model.getSelection().size());

        model.clearSelection();
        assertTrue(model.isSelectionEmpty());
        assertTrue(model.getSelection().isEmpty());
    }

    @Test
    public void testSingleSelection() {
        model.setSelectionMode(SelectionMode.SINGLE_SELECTION);

        model.setSelectionInterval(today, today);
        assertTrue(1 == model.getSelection().size());
        assertEquals(startOfDay(today), model.getFirstSelectionDate());

        model.setSelectionInterval(today, afterTomorrow);
        assertTrue(1 == model.getSelection().size());
        assertEquals(startOfDay(today), model.getFirstSelectionDate());
    }
    
    @Test
    public void testSingleIntervalSelection() {
        model.setSelectionMode(SelectionMode.SINGLE_INTERVAL_SELECTION);

        model.setSelectionInterval(today, today);
        assertTrue(1 == model.getSelection().size());
        assertEquals(startOfDay(today), model.getFirstSelectionDate());

        model.setSelectionInterval(today, tomorrow);
        
        assertEquals(2, model.getSelection().size());
        assertEquals(startOfDay(today), model.getFirstSelectionDate());
        assertEquals(startOfDay(tomorrow), model.getLastSelectionDate());
    }

    @Test
    public void testWeekIntervalSelection() {
        //TODO...
    }

    @Test
    public void testMultipleIntervalSelection() {
        model.setSelectionMode(SelectionMode.MULTIPLE_INTERVAL_SELECTION);

        model.setSelectionInterval(yesterday, yesterday);
        model.addSelectionInterval(afterTomorrow, afterTomorrow);
        
        assertEquals(2, model.getSelection().size());
        assertEquals(startOfDay(yesterday), model.getFirstSelectionDate());
        assertEquals(startOfDay(afterTomorrow), model.getLastSelectionDate());
        
    }

    @Override
    protected void setUp() throws Exception {
        setUpCalendar();
        model = new DaySelectionModel();
    }

    
}
