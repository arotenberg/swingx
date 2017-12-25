/*
 * $Id: CalendarUtilsIssues.java 3916 2011-01-12 10:21:58Z kleopatra $
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

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

import junit.framework.TestCase;

/**
 * 
 * @author Jeanette Winzenburg
 */
public class CalendarUtilsIssues extends TestCase {

    
    @Test
    public void testSameDecade() {
        // PENDING JW: implement CalendarUtils.sameDecade
        fail("implement CalendarUtils.sameDecade");
    }
    
    /**
     */
    public void testStartOfDSTBerlin() {
        TimeZone berlin = TimeZone.getTimeZone("Europe/Berlin");
        Calendar calendar = Calendar.getInstance(berlin);
        calendar.set(Calendar.YEAR, 2008);
        Date startOfDST = CalendarUtils.getStartOfDST(calendar);
        assertNotNull("timezone has DST, must have first day of DST", startOfDST);
        assertTrue(berlin.inDaylightTime(startOfDST));
        assertEquals(startOfDST, calendar.getTime());
        assertEquals(30, calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.MARCH, calendar.get(Calendar.MONTH));
        assertTrue(CalendarUtils.isEndOfDay(calendar));
    }
    
    /**
     */
    public void testEndOfDSTBerlin() {
        TimeZone berlin = TimeZone.getTimeZone("Europe/Berlin");
        Calendar calendar = Calendar.getInstance(berlin);
        calendar.set(Calendar.YEAR, 2008);
        Date lastOfDST = CalendarUtils.getEndOfDST(calendar);
        assertNotNull("timezone has DST, must have first day of DST", lastOfDST);
        assertEquals(lastOfDST, calendar.getTime());
        assertEquals(26, calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.OCTOBER, calendar.get(Calendar.MONTH));
        assertFalse(berlin.inDaylightTime(lastOfDST));
        assertTrue(CalendarUtils.isEndOfDay(calendar));
    }
    
    public void testStartOfDSTCairo() {
        TimeZone cairo = TimeZone.getTimeZone("Africa/Cairo");
        Calendar calendar = Calendar.getInstance(cairo);
        calendar.set(Calendar.YEAR, 2008);
        Date startOfDST = CalendarUtils.getStartOfDST(calendar);
        assertNotNull("timezone has DST, must have first day of DST", startOfDST);
        assertEquals(startOfDST, calendar.getTime());
        assertTrue(CalendarUtils.isEndOfDay(calendar));
//        assertEquals(25, calendar.get(Calendar.DAY_OF_MONTH));
//        assertEquals(Calendar.APRIL, calendar.get(Calendar.MONTH));
    }
    
    public void testEndOfDSTCairo() {
        TimeZone cairo = TimeZone.getTimeZone("Africa/Cairo");
        Calendar calendar = Calendar.getInstance(cairo);
        calendar.set(Calendar.YEAR, 2008);
        Date startOfDST = CalendarUtils.getEndOfDST(calendar);
        if (calendar.getTimeZone().useDaylightTime()) {
            assertNotNull("timezone has DST, must have first day of DST", startOfDST);
        } 
        assertEquals(startOfDST, calendar.getTime());
        assertTrue(CalendarUtils.isEndOfDay(calendar));
//        assertEquals(24, calendar.get(Calendar.DAY_OF_MONTH));
//        assertEquals(Calendar.SEPTEMBER, calendar.get(Calendar.MONTH));
    }
    

}
