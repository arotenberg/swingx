/**
 * $Id: DefaultDateSelectionModelTest.java 3283 2009-03-09 09:42:58Z kleopatra $
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
public class DefaultDateSelectionModelTest extends AbstractTestDateSelectionModel {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(DefaultDateSelectionModelTest.class.getName());
    
    /**
     * DefaultDateSelectionModel impl doesn't normalize.
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
        model.addDateSelectionListener(report);
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
        Date date = new Date();
        model.setSelectionInterval(date, date);
        // sanity
        assertTrue(model.isSelected(date));
        DateSelectionReport report = new DateSelectionReport(model);
        // modify the date
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, 1);
        model.setSelectionInterval(calendar.getTime(), calendar.getTime());
        assertEquals("selection must fire on selection",
                1,
                report.getEventCount());
        assertEquals("event type must be DATES_SET",
                DateSelectionEvent.EventType.DATES_SET,
                report.getLastEventType());
    }
    



    
    /**
     *  respect upper bound - the bound itself 
     *  a valid selection.
     *
     */
    @Test
    public void testLowerBoundJustBelow() {
        model.setLowerBound(today);
        // the bound itself is allowed
        calendar.add(Calendar.HOUR_OF_DAY, -1);
        model.setSelectionInterval(calendar.getTime(), calendar.getTime());
        assertEquals("future selection just above must be rejected", null, 
                model.getFirstSelectionDate());
    }
    /**
     *  respect upper bound - the bound itself 
     *  a valid selection.
     *
     */
    @Test
    public void testLowerBoundJustAbove() {
        model.setLowerBound(today);
        // the bound itself is allowed
        calendar.add(Calendar.HOUR_OF_DAY, +1);
        model.setSelectionInterval(calendar.getTime(), calendar.getTime());
        assertEquals("selected just below upper bound", calendar.getTime(), 
                model.getFirstSelectionDate());
    }
    

     /**
     *  respect upper bound - the bound itself 
     *  a valid selection.
     *
     */
    @Test
    public void testUpperBoundJustAbove() {
        model.setUpperBound(today);
        // the bound itself is allowed
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        model.setSelectionInterval(calendar.getTime(), calendar.getTime());
        assertEquals("future selection just above must be rejected", null, 
                model.getFirstSelectionDate());
    }
    /**
     *  respect upper bound - the bound itself 
     *  a valid selection.
     *
     */
    @Test
    public void testUpperBoundJustBelow() {
        model.setUpperBound(today);
        // the bound itself is allowed
        calendar.add(Calendar.HOUR_OF_DAY, -1);
        model.setSelectionInterval(calendar.getTime(), calendar.getTime());
        assertEquals("selected just below upper bound", calendar.getTime(), 
                model.getFirstSelectionDate());
    }
    

    @Test
    public void testSingleSelection() {
        model.setSelectionMode(DateSelectionModel.SelectionMode.SINGLE_SELECTION);
        Date today = new Date();
        model.setSelectionInterval(today, today);
        SortedSet<Date> selection = model.getSelection();
        assertTrue(!selection.isEmpty());
        assertTrue(1 == selection.size());
        assertTrue(today.equals(selection.first()));

        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.roll(Calendar.DAY_OF_MONTH, 1);
        Date tomorrow = cal.getTime();
        model.setSelectionInterval(today, tomorrow);
        selection = model.getSelection();
        assertTrue(!selection.isEmpty());
        assertTrue(1 == selection.size());
        assertTrue(today.equals(selection.first()));

        model.addSelectionInterval(tomorrow, tomorrow);
        selection = model.getSelection();
        assertTrue(!selection.isEmpty());
        assertTrue(1 == selection.size());
        assertTrue(tomorrow.equals(selection.first()));
    }

    @Test
    public void testSingleIntervalSelection() {
        model.setSelectionMode(DateSelectionModel.SelectionMode.SINGLE_INTERVAL_SELECTION);
        Date startDate = new Date();
        // PENDING JW: hit US DST switching again ... how to handle that? 
        Calendar cal = Calendar.getInstance(); //TimeZone.getTimeZone("US/Eastern"));
        cal.setTime(startDate);
//        cal.set(2009, Calendar.MARCH, 5, 2, 0);
//        startDate = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 5);
        Date endDate = cal.getTime();
        model.setSelectionInterval(startDate, endDate);
        SortedSet<Date> selection = model.getSelection();
        assertEquals(startDate, selection.first());
        assertEquals(endDate, selection.last());

        cal.setTime(startDate);
        cal.roll(Calendar.MONTH, 1);
        Date startDateNextMonth = cal.getTime();
        model.addSelectionInterval(startDateNextMonth, startDateNextMonth);
        selection = model.getSelection();
        assertEquals(startDateNextMonth, selection.first());
        assertEquals(startDateNextMonth, selection.last());
    }

    /**
     * characterize behaviour on setSelectionInterval depending on selection mode.
     * Here: compare singleInterval and multipleInterval - must be same.
     */
    @Test
    public void testSetIntervalSameForSingleAndMultipleIntervalMode() {
        Date date = new Date();
        // pre-select 
        model.setSelectionMode(DateSelectionModel.SelectionMode.SINGLE_INTERVAL_SELECTION);
        model.setSelectionInterval(date, date);
        
        // get span in future
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, 5);
        Date startDate = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 5);
        Date endDate = cal.getTime();
        // set the interval
        model.setSelectionInterval(startDate, endDate);
        SortedSet<Date> selection = model.getSelection();
        // sanity
        assertEquals(6, selection.size());

        // prepare model for multiple interval selection
        model.clearSelection();
        model.setSelectionMode(DateSelectionModel.SelectionMode.MULTIPLE_INTERVAL_SELECTION);
        model.setSelectionInterval(date, date);
        
        model.setSelectionInterval(startDate, endDate);
        assertEquals(selection.size(), model.getSelection().size());
        assertEquals(selection.first(), model.getSelection().first());
        assertEquals(selection.last(), model.getSelection().last());
    }
    
    @Test
    public void testUnselectableDatesMultipleInterval() {
        // Make sure the unselectable dates returns an empty set if it hasn't been
        // used.
        SortedSet<Date> unselectableDates = model.getUnselectableDates();
        assert(unselectableDates.isEmpty());

        model.setSelectionMode(DateSelectionModel.SelectionMode.MULTIPLE_INTERVAL_SELECTION);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());

        Date today = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date tPlus1 = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date tPlus2 = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date tPlus3 = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date tPlus4 = cal.getTime();

        model.setSelectionInterval(today, tPlus4);
        SortedSet<Date> selection = model.getSelection();
        assertTrue(!selection.isEmpty());
        assertTrue(5 == selection.size());
        assertTrue(today.equals(selection.first()));
        assertTrue(tPlus4.equals(selection.last()));

        unselectableDates = new TreeSet<Date>();
        unselectableDates.add(tPlus1);
        unselectableDates.add(tPlus3);
        model.setUnselectableDates(unselectableDates);

        // Make sure setting the unselectable dates to include a selected date removes
        // it from the selected set.
        selection = model.getSelection();
        assertTrue(!selection.isEmpty());
        assertTrue(3 == selection.size());
        assertTrue(selection.contains(today));
        assertTrue(selection.contains(tPlus2));
        assertTrue(selection.contains(tPlus4));

        // Make sure the unselectable dates is the same as what we set.
        SortedSet<Date> result = model.getUnselectableDates();
        assertTrue(unselectableDates.equals(result));
    }
    
    @Override
    @Before
       public void setUp() {
        setUpCalendar();
        model = new DefaultDateSelectionModel();
    }

}