/**
 * $Id: SingleDaySelectionModelTest.java 3473 2009-08-27 13:17:10Z kleopatra $
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
 */
package org.jdesktop.swingx.calendar;

import java.util.Calendar;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.jdesktop.swingx.calendar.DateSelectionModel.SelectionMode;
import org.jdesktop.swingx.event.DateSelectionEvent;
import org.jdesktop.swingx.test.DateSelectionReport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for the DefaultDateSelectionModel.
 * 
 * Moved from swingx to calendar package as of version 1.15
 */
@RunWith(JUnit4.class)
public class SingleDaySelectionModelTest extends AbstractTestDateSelectionModel {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(SingleDaySelectionModelTest.class.getName());

    /**
     * SingleSelectionDateModel doesn't normalize
     */
    @Test
    public void testNormalizedDateUnchanged() {
        Date date = calendar.getTime();
        Date normalized = model.getNormalizedDate(date);
        assertEquals(date, normalized);
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
     * Always single selection by definition of SingleDateSelectionModel.
     */
    @Test
    public void testSelectionMode() {
        // initial
        assertEquals(SelectionMode.SINGLE_SELECTION, model.getSelectionMode());
        for (SelectionMode mode : SelectionMode.values()) {
            model.setSelectionMode(mode);
            assertEquals(SelectionMode.SINGLE_SELECTION, model.getSelectionMode());
        }
    }

    /**
     * 
     */
    @Test
    public void testIsSelectedAllInSameDay() {
       model.setSelectionInterval(today, today);
       // sanity: today itself
       assertTrue(model.isSelected(today));
       assertTrue("every date contained in today must be regarded as selected - startofday", 
               model.isSelected(startOfDay(today)));
       assertTrue("every date contained in today must be regarded as selected - endofday", 
               model.isSelected(endOfDay(today)));
    }

    
   /**
     * Issue #625-swingx: DateSelectionModel must not fire if unchanged.
     * Here: test setSelection with single mode
     */
    @Test
    public void testSelectionSetNotFireIfSameSingle() {
        final Date date = calendar.getTime();
        model.setSelectionMode(DateSelectionModel.SelectionMode.SINGLE_SELECTION);
        model.setSelectionInterval(date, date);
        // sanity
        assertTrue(model.isSelected(date));
        DateSelectionReport report = new DateSelectionReport(model);
        model.setSelectionInterval(date, date);
        assertEquals("selection must not fire on selecting already selected date",
                0,
                report.getEventCount());
    }

    /**
     * Issue #625-swingx: DateSelectionModel must not fire if unchanged.
     * Here: test addSelection with single mode
     */
    @Test
    public void testSelectionAddNotFireIfSameSingle() {
        final Date date = calendar.getTime();
        model.setSelectionMode(DateSelectionModel.SelectionMode.SINGLE_SELECTION);
        model.setSelectionInterval(date, date);
        // sanity
        assertTrue(model.isSelected(date));
        DateSelectionReport report = new DateSelectionReport(model);
        model.addSelectionInterval(date, date);
        assertEquals("selection must not fire on selecting already selected date",
                0,
                report.getEventCount());
    }

 
    /**
     * Issue #625-swingx: DateSelectionModel must not fire if unchanged.
     * Here: test setSelection with single interval mode
     */
    @Test
    public void testSelectionSetNotFireIfSameSingleInterval() {
        final Date date = calendar.getTime();
        calendar.add(Calendar.DATE, 2);
        final Date end = calendar.getTime();
        model.setSelectionMode(DateSelectionModel.SelectionMode.SINGLE_INTERVAL_SELECTION);
        model.setSelectionInterval(date, end);
        // sanity
        assertTrue(model.isSelected(date));
        DateSelectionReport report = new DateSelectionReport(model);
        model.setSelectionInterval(date, end);
        assertEquals("selection must not fire on selecting already selected date",
                0,
                report.getEventCount());
    }

    /**
     * Issue #625-swingx: DateSelectionModel must not fire if unchanged.
     * Here: test addSelection with single interval mode
     */
    @Test
    public void testSelectionAddNotFireIfSameSingleInterval() {
        final Date date = calendar.getTime();
        calendar.add(Calendar.DATE, 2);
        final Date end = calendar.getTime();
        model.setSelectionMode(DateSelectionModel.SelectionMode.SINGLE_INTERVAL_SELECTION);
        model.setSelectionInterval(date, end);
        // sanity
        assertTrue(model.isSelected(date));
        DateSelectionReport report = new DateSelectionReport(model);
        model.addSelectionInterval(date, end);
        assertEquals("selection must not fire on selecting already selected date",
                0,
                report.getEventCount());
    }

    /**
     * Issue #625-swingx: DateSelectionModel must not fire if unchanged.
     * Here: test setSelection with multiple interval mode
     */
    @Test
    public void testSelectionSetNotFireIfSameMultipleInterval() {
        final Date date = calendar.getTime();
        calendar.add(Calendar.DATE, 2);
        final Date end = calendar.getTime();
        model.setSelectionMode(DateSelectionModel.SelectionMode.MULTIPLE_INTERVAL_SELECTION);
        model.setSelectionInterval(date, end);
        // sanity
        assertTrue(model.isSelected(date));
        DateSelectionReport report = new DateSelectionReport(model);
        model.setSelectionInterval(date, end);
        assertEquals("selection must not fire on selecting already selected date",
                0,
                report.getEventCount());
    }

    /**
     * Issue #625-swingx: DateSelectionModel must not fire if unchanged.
     * Here: test addSelection with multiple interval mode
     */
    @Test
    public void testSelectionAddNotFireIfSameMultipeInterval() {
        final Date date = calendar.getTime();
        calendar.add(Calendar.DATE, 2);
        final Date end = calendar.getTime();
        model.setSelectionMode(DateSelectionModel.SelectionMode.MULTIPLE_INTERVAL_SELECTION);
        model.setSelectionInterval(date, end);
        // sanity
        assertTrue(model.isSelected(date));
        DateSelectionReport report = new DateSelectionReport(model);
        model.addSelectionInterval(date, end);
        assertEquals("selection must not fire on selecting already selected date",
                0,
                report.getEventCount());
    }
    /**
     * related to #625-swingx: DateSelectionModel must not fire on clearing empty selection.
     */
    @Test
    public void testDateSelectionClearSelectionNotFireIfUnselected() {
        // sanity
        assertTrue(model.isSelectionEmpty());
        DateSelectionReport report = new DateSelectionReport(model);
        model.clearSelection();
        assertEquals("selection must not fire on clearing empty selection",
                0,
                report.getEventCount());
    }
    
    /**
     * related to #625-swingx: DateSelectionModel must fire SELECTION_CLEARED if 
     * had selection.
     * Testing here for sanity reasons ... be sure we didn't prevent the firing
     * altogether while changing.
     */
    @Test
    public void testDateSelectionClearSelectionFireIfSelected() {
        Date date = new Date();
        model.setSelectionInterval(date, date);
        // sanity
        assertFalse(model.isSelectionEmpty());
        DateSelectionReport report = new DateSelectionReport(model);
        model.clearSelection();
        assertEquals("selection must fire on clearing selection",
                1,
                report.getEventCount());
        assertEquals("event type must be SELECTION_CLEARED",
                DateSelectionEvent.EventType.SELECTION_CLEARED,
                report.getLastEventType());
    }
    
    /**
     * related to #625-swingx: DateSelectionModel must not fire on clearing empty selection.
     */
    @Test
    public void testDateSelectionSetSelectionNotFireIfSelected() {
        Date date = new Date();
        model.setSelectionInterval(date, date);
        // sanity
        assertTrue(model.isSelected(date));
        DateSelectionReport report = new DateSelectionReport(model);
        model.setSelectionInterval(date, date);
        assertEquals("selection must not fire on selecting already selected date",
                0,
                report.getEventCount());
    }
    
    /**
     * related to #625-swingx: DateSelectionModel must fire SELECTION_CLEARED if 
     * had selection.
     * Testing here for sanity reasons ... be sure we didn't prevent the firing
     * altogether while changing.
     */
    @Test
    public void testDateSelectionSetSelectionFire() {
        model.setSelectionInterval(today, today);
        // sanity
        assertTrue(model.isSelected(today));
        DateSelectionReport report = new DateSelectionReport(model);
        model.setSelectionInterval(tomorrow, tomorrow);
        assertEquals("selection must fire on selection",
                1,
                report.getEventCount());
        assertEquals("event type must be DATES_SET",
                DateSelectionEvent.EventType.DATES_SET,
                report.getLastEventType());
    }
    



    
    /**
     *  respect lower bound - the bound itself 
     *  a valid selection.
     *
     */
    @Test
    public void testLowerBoundSameDay() {
        model.setLowerBound(endOfDay(today));
        // the bound itself is allowed
        model.setSelectionInterval(today, today);
        assertTrue("same day as lower bound is allowed", model.isSelected(today));
    }
    
 
    /**
     *  respect upper bound - the bound itself 
     *  a valid selection.
     *
     */
    @Test
    public void testUpperBoundSameDay() {
        model.setUpperBound(startOfDay(today));
        // the bound itself is allowed
        model.setSelectionInterval(today, today);
        assertEquals(today, model.getFirstSelectionDate());
        assertTrue("same day as upper bound is allowed", model.isSelected(today));
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
     * test removeInterval: any selection in the given date ranged must be removed.
     */
    @Test
    public void testRemoveInterval() {
        model.setSelectionInterval(today, today);
        model.removeSelectionInterval(yesterday, tomorrow);
        assertTrue("selection must be empty: selected today and removed interval [yesterday, tomorrow]", 
                model.isSelectionEmpty());
    }

    /**
     * test the only mode we have: set single, set interval, add all must
     * result in the same: set selection to the startDate.
     */
    @Test
    public void testSingleSelection() {
        model.setSelectionInterval(today, today);
        assertTrue(!model.isSelectionEmpty());
        assertTrue(1 == model.getSelection().size());
        assertTrue(today.equals(model.getFirstSelectionDate()));

        model.setSelectionInterval(today, tomorrow);
        assertTrue(!model.isSelectionEmpty());
        assertTrue(1 == model.getSelection().size());
        assertTrue(today.equals(model.getFirstSelectionDate()));

        model.addSelectionInterval(tomorrow, tomorrow);
        assertTrue(!model.isSelectionEmpty());
        assertTrue(1 == model.getSelection().size());
        assertTrue(tomorrow.equals(model.getFirstSelectionDate()));
    }

    
    @Test
    public void testUnselectableDatesSetClearsSelection() {
        // Make sure the unselectable dates returns an empty set if it hasn't been
        // used.
        SortedSet<Date> unselectableDates = model.getUnselectableDates();
        assertTrue(unselectableDates.isEmpty());

        model.setSelectionInterval(today, today);

        unselectableDates = new TreeSet<Date>();
        unselectableDates.add(startOfDay(today));
        unselectableDates.add(startOfDay(yesterday));
        unselectableDates.add(startOfDay(tomorrow));
        model.setUnselectableDates(unselectableDates);
        // Make sure the unselectable dates is the same as what we set.
        SortedSet<Date> result = model.getUnselectableDates();
        assertEquals(unselectableDates, result);

        assertTrue("today must be removed", model.isSelectionEmpty());

    }

    
    // pre-defined reference dates - all relative to current date at around 5 am
    
    @Override
    @Before
       public void setUp() {
        setUpCalendar();
        model = new SingleDaySelectionModel();

    }

}