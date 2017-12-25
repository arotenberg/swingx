/*
 * $Id: DateSelectionModelIssues.java 3612 2010-02-17 11:49:28Z kleopatra $
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
package org.jdesktop.swingx.calendar;

import java.util.Calendar;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.event.DateSelectionEvent;
import org.jdesktop.swingx.test.DateSelectionReport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Test to expose known Issues with <code>DateSelectionModel</code>
 *  and implementations.
 * 
 * Moved from swingx to calendar package as of version 1.8.
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class DateSelectionModelIssues extends InteractiveTestCase {

    protected DateSelectionModel model;
    // pre-defined dates - initialized in setUpCalendar
    protected Date today;
    protected Date tomorrow;
    @SuppressWarnings("unused")
    protected Date afterTomorrow;
    protected Date yesterday;
    // the calendar to use, its date is initialized with the today-field in setUpCalendar
    protected Calendar calendar;
    
    /**
     * Issue #1271-swingx: undefined behaviour of old selection if hasSelection
     * 
     * One option - currently implemented in default implementations - is to
     * clear the old selection. If so, they must fire a cleared event.
     * 
     */
    @Test
    public void testUnselectableDatesSelectedWhileHasValidSelectionSingleDaySelectionModel() {
        DateSelectionModel model = new SingleDaySelectionModel();
        assertUnselectableDatesSelectedWhileHasValidSelection(model);
    }
    
    /**
     * Issue #1271-swingx: undefined behaviour of old selection if hasSelection
     * 
     * One option - currently implemented in default implementations - is to
     * clear the old selection. If so, they must fire a cleared event.
     * 
     */
    @Test
    public void testUnselectableDatesSelectedWhileHasValidSelectionDefaultDateSelectionModel() {
        DateSelectionModel model = new DefaultDateSelectionModel();
        assertUnselectableDatesSelectedWhileHasValidSelection(model);
    }
    
    /**
     * Issue #1271-swingx: undefined behaviour of old selection if hasSelection
     * 
     * One option - currently implemented in default implementations - is to
     * clear the old selection. If so, they must fire a cleared event.
     * 
     */
    @Test
    public void testUnselectableDatesSelectedWhileHasValidSelectionDaySelectionModel() {
        DateSelectionModel model = new DaySelectionModel();
        assertUnselectableDatesSelectedWhileHasValidSelection(model);
    }



    /**
     * @param model
     */
    private void assertUnselectableDatesSelectedWhileHasValidSelection(
            DateSelectionModel model) {
        SortedSet<Date> unselectableDates = new TreeSet<Date>();
        unselectableDates.add(tomorrow);
        model.setUnselectableDates(unselectableDates);
        // valid selection
        model.setSelectionInterval(today, today);
        DateSelectionReport report = new DateSelectionReport(model);
        model.setSelectionInterval(tomorrow, tomorrow);
        if (model.isSelectionEmpty()) {
            assertEquals("implementation clears old selection, must fire clear event", 
                    1, report.getEventCount(DateSelectionEvent.EventType.SELECTION_CLEARED));
        } else {
            assertEquals("implementation does not clear old selection, old must be unchanged", 
                    today, model.getSelection().first());
        }
    }

    

    /**
     * Hmm ... missing api or overshooting? 
     */
    @Test
    public void testSelectionListening() {
        fail("DateSelectionModel - missing api: getListeners?");
    }

    @Test
    public void testUnselectableDatesCleanupOneRemovedEvent() {
        fail("TODO: test that we fire only one remove event");
    }
    
    /**
     * Event properties should be immutable.
     *
     */
    @Test
    public void testEventImmutable() {
        DateSelectionReport report = new DateSelectionReport(model);
        Date date = new Date();
        model.setSelectionInterval(date, date);
        assertEquals(1, report.getEventCount());
        DateSelectionEvent event = report.getLastEvent();
        // sanity
        assertEquals(date, event.getSelection().first());
        Date next = new Date();
        model.setSelectionInterval(next, next);
        assertSame(date, event.getSelection().first());
    }
    
    /**
     * Inconsistency in TreeSet in contains with null parameter
     * if empty --> nothing 
     * if not empty --> throws NPE
     */
    @Test
    public void testSortedSetContainsNull() {
        SortedSet<Date> dates = new TreeSet<Date>();
        dates.contains(null); 
        dates.add(new Date());
        dates.contains(null);
    }
    
    /**
     * Initializes the calendar to the default instance and the predefined dates
     * in the coordinate system of the calendar. Note that the hour is set
     * to "about 5" in all dates, to be reasonably well into the day. The time
     * fields of all dates are the same, the calendar is pre-set with the
     * today field.
     */
    protected void setUpCalendar() {
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 5);
        today = calendar.getTime();
        
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        yesterday = calendar.getTime();
        
        calendar.add(Calendar.DAY_OF_MONTH, 2);
        tomorrow = calendar.getTime();
        
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        afterTomorrow = calendar.getTime();
        
        calendar.setTime(today);
    }
    
    @Override
    @Before
    public void setUp() throws Exception {
        setUpCalendar();
        model = new DaySelectionModel();
//        model = new DefaultDateSelectionModel();
    }

    
    
}
