/*
 * $Id: CalendarUtilsTest.java 3916 2011-01-12 10:21:58Z kleopatra $
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
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Logger;

import org.jdesktop.swingx.InteractiveTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests CalendarUtils.
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class CalendarUtilsTest extends InteractiveTestCase {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(CalendarUtilsTest.class
            .getName());
    /**
     * default calendar instance
     */
    private Calendar todayGerman;
    private Calendar todayUS;
    private Calendar midJune;

    @Test
    public void testGetYearInDecade() {
        midJune.set(Calendar.YEAR, 2015);
        assertEquals(5, CalendarUtils.get(midJune, CalendarUtils.YEAR_IN_DECADE));
    }
    
    @Test
    public void testSetYearInDecade() {
        midJune.set(Calendar.YEAR, 2015);
        CalendarUtils.set(midJune, CalendarUtils.YEAR_IN_DECADE, 7);
        assertEquals(7, CalendarUtils.get(midJune, CalendarUtils.YEAR_IN_DECADE));
    }
    
    @Test
    public void testAddDecadeField() {
        todayGerman.set(Calendar.YEAR, 2015);
        CalendarUtils.add(todayGerman, CalendarUtils.DECADE, 1);
        assertEquals(2025, todayGerman.get(Calendar.YEAR));
    }
    
    @Test
    public void testAddDecadeFieldNegative() {
        todayGerman.set(Calendar.YEAR, 2015);
        CalendarUtils.add(todayGerman, CalendarUtils.DECADE, -1);
        assertEquals(2005, todayGerman.get(Calendar.YEAR));
    }
    
    @Test
    public void testAddNativeField() {
        todayGerman.set(Calendar.YEAR, 2015);
        CalendarUtils.add(todayGerman, Calendar.YEAR, 10);
        assertEquals(2025, todayGerman.get(Calendar.YEAR));
    }
    
    @Test
    public void testSetDecadeField() {
        todayGerman.set(Calendar.YEAR, 2015);
        CalendarUtils.set(todayGerman, CalendarUtils.DECADE, 2020);
        assertEquals(2025, todayGerman.get(Calendar.YEAR));
    }
    
    @Test
    public void testSetNativeField() {
        todayGerman.set(Calendar.YEAR, 2015);
        CalendarUtils.set(todayGerman, Calendar.YEAR, 2025);
        assertEquals(2025, todayGerman.get(Calendar.YEAR));
    }
    
    @Test
    public void testGetDecadeField() {
        todayGerman.set(Calendar.YEAR, 2025);
        assertEquals(2020, CalendarUtils.get(todayGerman, CalendarUtils.DECADE));
    }
    
    @Test
    public void testGetNativeField() {
        todayGerman.set(Calendar.YEAR, 2015);
        assertEquals(2015, CalendarUtils.get(todayGerman, Calendar.YEAR));
    }
//    
//    @Test
//    public void testSetNativeFieldNegative() {
//        Calendar clone = (Calendar) todayGerman.clone();
//        clone.add(Calendar.MONTH, -13);
//        todayGerman.set(Calendar.MONTH, -13);
//        assertEquals(clone.getTime(), todayGerman.getTime());
//        assertEquals("adjusted date" + todayGerman.getTime(),-1, todayGerman.get(Calendar.MONTH));
//    }
//    
    
    @Test
    public void testStartOfDecade() {
        todayGerman.set(Calendar.YEAR, 2005);
        CalendarUtils.startOfDecade(todayGerman);
        assertTrue(CalendarUtils.isStartOfYear(todayGerman));
        assertEquals(2000, todayGerman.get(Calendar.YEAR));
    }

    @Test
    public void testStartOfDecadeWithReturn() {
        midJune.add(Calendar.YEAR, 15);
        Date startOf10YearsFuture = CalendarUtils.startOfDecade(todayGerman, midJune.getTime());
        CalendarUtils.startOfDecade(midJune);
        assertTrue(CalendarUtils.isStartOfDecade(todayGerman));
        assertEquals(midJune.getTime(), startOf10YearsFuture);
    }
    
    @Test
    public void testIsStartOfDecade() {
        todayGerman.set(Calendar.YEAR, 2000);
        CalendarUtils.startOfYear(todayGerman);
        assertTrue(CalendarUtils.isStartOfDecade(todayGerman));
        todayGerman.add(Calendar.YEAR, 1);
        assertFalse(CalendarUtils.isStartOfDecade(todayGerman));
    }
    
    @Test
    public void testStartOfDecadeByField() {
        todayGerman.set(Calendar.YEAR, 2005);
        CalendarUtils.startOf(todayGerman, CalendarUtils.DECADE);
        assertTrue(CalendarUtils.isStartOfYear(todayGerman));
        assertEquals(2000, todayGerman.get(Calendar.YEAR));
    }
    
    @Test
    public void testIsStartOfDecadeByField() {
        todayGerman.set(Calendar.YEAR, 2000);
        CalendarUtils.startOfYear(todayGerman);
        assertTrue(CalendarUtils.isStartOf(todayGerman, CalendarUtils.DECADE));
        todayGerman.add(Calendar.YEAR, 1);
        assertFalse(CalendarUtils.isStartOf(todayGerman, CalendarUtils.DECADE));
    }
    
    @Test
    public void testSameByDecadeField() {
        Date now = todayGerman.getTime();
        CalendarUtils.startOfDecade(todayGerman);
        Date start = todayGerman.getTime();
        assertTrue(CalendarUtils.isSame(todayGerman, now, CalendarUtils.DECADE));
        assertEquals("Calendar unchanged by same decade query", start, todayGerman.getTime());
        todayGerman.add(Calendar.YEAR, -1);
        assertFalse(CalendarUtils.isSame(todayGerman, now, CalendarUtils.DECADE));
    }
    
    @Test
    public void testSameByDayField() {
        Date now = todayGerman.getTime();
        CalendarUtils.endOfDay(todayGerman);
        Date end = todayGerman.getTime();
        assertTrue(CalendarUtils.isSame(todayGerman, now, Calendar.DAY_OF_MONTH));
        assertEquals(end, todayGerman.getTime());
        todayGerman.add(Calendar.DAY_OF_MONTH, 1);
        assertFalse(CalendarUtils.isSame(todayGerman, now, Calendar.DAY_OF_MONTH));
    }
    
    @Test
    public void testStartOfYearField() {
        CalendarUtils.startOf(midJune, Calendar.YEAR);
        assertTrue(CalendarUtils.isStartOfYear(midJune));
        assertTrue(CalendarUtils.isStartOf(midJune, Calendar.YEAR));
    }
    
    @Test
    public void testStartOfDayField() {
        CalendarUtils.startOf(midJune, Calendar.DAY_OF_MONTH);
        assertTrue(CalendarUtils.isStartOfDay(midJune));
        assertTrue(CalendarUtils.isStartOf(midJune, Calendar.DAY_OF_MONTH));
    }

    @Test
    public void testStartOfMonthField() {
        CalendarUtils.startOf(midJune, Calendar.MONTH);
        assertTrue(CalendarUtils.isStartOfMonth(midJune));
        assertTrue(CalendarUtils.isStartOf(midJune, Calendar.MONTH));
    }
    
    @Test
    public void testStartOfYear() {
        int year = midJune.get(Calendar.YEAR);
        CalendarUtils.startOfYear(midJune);
        assertEquals(Calendar.JANUARY, midJune.get(Calendar.MONTH));
        assertTrue(CalendarUtils.isStartOfMonth(midJune));
        assertEquals(year, midJune.get(Calendar.YEAR));
    }
    
    @Test
    public void testStartOfYearWithReturn() {
        midJune.add(Calendar.YEAR, 10);
        Date startOf10YearsFuture = CalendarUtils.startOfYear(todayGerman, midJune.getTime());
        CalendarUtils.startOfYear(midJune);
        assertTrue(CalendarUtils.isStartOfMonth(todayGerman));
        assertEquals("start of year with return must be same as changing start-of-year", 
                startOf10YearsFuture, midJune.getTime());
    }
    
    @Test
    public void testIsStartOfYear() {
        CalendarUtils.startOfYear(midJune);
        assertTrue(CalendarUtils.isStartOfYear(midJune));
        midJune.add(Calendar.MILLISECOND, -1);
        Date changed = midJune.getTime();
        assertFalse(CalendarUtils.isStartOfYear(midJune));
        assertEquals("calendar must be unchanged", changed, midJune.getTime());
    }
    
    @Test
    public void testWeekOfYearInFeb() {
        todayGerman.set(2008, Calendar.FEBRUARY, 1);
        Date firstOfFeb = todayGerman.getTime();
        CalendarUtils.startOfDay(todayGerman);
        assertTrue(CalendarUtils.isSameDay(todayGerman, firstOfFeb));
        assertTrue(CalendarUtils.isStartOfMonth(todayGerman));
        Date startOfFirstOfFeb = todayGerman.getTime();
        CalendarUtils.startOfWeek(todayGerman);
        assertTrue("expected calendar before firstOfFeb " 
                + todayGerman.getTime() + " / " + startOfFirstOfFeb , 
                todayGerman.getTime().before(startOfFirstOfFeb));
    }
    
    /**
     * test to characterize startofweek behaviour is we are in a 
     * calendar with minimalDays > 1.
     */
    @Test
    public void testWeekOfYearInDecember() {
        // a date before the first week of the month
        todayGerman.set(2007, Calendar.DECEMBER, 1);
        Date firstOfDecember = todayGerman.getTime();
        CalendarUtils.startOfWeek(todayGerman);
//        int weekOfYear = todayGerman.get(Calendar.WEEK_OF_YEAR);
        todayGerman.setTime(firstOfDecember);
        CalendarUtils.endOfMonth(todayGerman);
        // we crossed the year boundary
        assertEquals(1, todayGerman.get(Calendar.WEEK_OF_YEAR));
    }

    /**
     * test to characterize startofweek behaviour is we are in a 
     * calendar with minimalDays > 1.
     */
    @Test
    public void testStartOfWeekBeforeFirstWeekOfMonth() {
        // a date before the first week of the month
        todayGerman.set(2008, Calendar.FEBRUARY, 1);
        assertEquals(0, todayGerman.get(Calendar.WEEK_OF_MONTH));
        CalendarUtils.startOfWeek(todayGerman);
        assertEquals(Calendar.JANUARY, todayGerman.get(Calendar.MONTH));
    }
    
    /**
     * test to characterize startofweek behaviour is we are in a 
     * calendar with minimalDays > 1.
     */
    @Test
    public void testStartOfWeekBeforeFirstWeekOfYear() {
        // a date before the first week of the year
        todayGerman.set(2010, Calendar.JANUARY, 1);
        assertEquals(0, todayGerman.get(Calendar.WEEK_OF_MONTH));
        assertEquals(53, todayGerman.get(Calendar.WEEK_OF_YEAR));
        CalendarUtils.startOfWeek(todayGerman);
        assertEquals(Calendar.DECEMBER, todayGerman.get(Calendar.MONTH));
    }
    
    @Test
    public void testSameDay() {
        Date now = todayGerman.getTime();
        CalendarUtils.endOfDay(todayGerman);
        Date end = todayGerman.getTime();
        assertTrue(CalendarUtils.isSameDay(todayGerman, now));
        assertEquals(end, todayGerman.getTime());
        todayGerman.add(Calendar.DAY_OF_MONTH, 1);
        assertFalse(CalendarUtils.isSameDay(todayGerman, now));
    }
    
    @Test
    public void testAreEqual() {
        assertTrue(CalendarUtils.areEqual(null, null));
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        assertFalse(CalendarUtils.areEqual(now, null));
        assertFalse(CalendarUtils.areEqual(null, now));
        assertTrue(CalendarUtils.areEqual(now, now));
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        assertFalse(CalendarUtils.areEqual(now, calendar.getTime()));
    }
    @Test
    public void testIsStartOfWeek() {
        CalendarUtils.startOfWeek(midJune);
        assertTrue(CalendarUtils.isStartOfWeek(midJune));
        midJune.add(Calendar.MILLISECOND, -1);
        Date date = midJune.getTime();
        assertFalse(CalendarUtils.isStartOfWeek(midJune));
        assertEquals("calendar must be unchanged", date, midJune.getTime());
    }
 
    /**
     */
    @Test
    public void testIsEndOfWeek() {
        CalendarUtils.endOfWeek(midJune);
        assertTrue(CalendarUtils.isEndOfWeek(midJune));
        midJune.add(Calendar.MILLISECOND, 1);
        Date date = midJune.getTime();
        assertFalse(CalendarUtils.isEndOfWeek(midJune));
        assertEquals("calendar must be unchanged", date, midJune.getTime());
    }

    /**
     */
    @Test
    public void testEndOfWeek() {
        int week = midJune.get(Calendar.WEEK_OF_YEAR);
        CalendarUtils.endOfWeek(midJune);
        assertEquals(week, midJune.get(Calendar.WEEK_OF_YEAR));
        midJune.add(Calendar.MILLISECOND, 1);
        assertEquals(week + 1 , midJune.get(Calendar.WEEK_OF_YEAR));
    }

    /**
     */
    @Test
    public void testEndOfWeekWithReturn() {
        Date date = midJune.getTime();
        Date start = CalendarUtils.endOfWeek(midJune, date);
        assertTrue(CalendarUtils.isEndOfWeek(midJune));
        assertEquals(start, midJune.getTime());
    }
    /**
     */
    @Test
    public void testStartOfWeekWithReturn() {
        Date date = midJune.getTime();
        Date start = CalendarUtils.startOfWeek(midJune, date);
        assertTrue(CalendarUtils.isStartOfWeek(midJune));
        assertEquals(start, midJune.getTime());
    }

    @Test
    public void testStartOfWeekFromMiddle() {
        int day = Calendar.WEDNESDAY;
        todayGerman.set(Calendar.DAY_OF_WEEK, day);
        int week = todayGerman.get(Calendar.WEEK_OF_YEAR);
        CalendarUtils.startOfWeek(todayGerman);
        assertEquals(week, todayGerman.get(Calendar.WEEK_OF_YEAR));
        assertEquals(todayGerman.getFirstDayOfWeek(), todayGerman.get(Calendar.DAY_OF_WEEK));
    }
    
    @Test
    public void testStartOfWeekFromFirst() {
        todayGerman.set(Calendar.DAY_OF_WEEK, todayGerman.getFirstDayOfWeek());
        int week = todayGerman.get(Calendar.WEEK_OF_YEAR);
        CalendarUtils.startOfWeek(todayGerman);
        assertEquals(week, todayGerman.get(Calendar.WEEK_OF_YEAR));
        assertEquals(todayGerman.getFirstDayOfWeek(), todayGerman.get(Calendar.DAY_OF_WEEK));
    }
    
    @Test
    public void testStartOfWeekFromLast() {
        todayGerman.set(Calendar.DAY_OF_WEEK, todayGerman.getFirstDayOfWeek());
        int week = todayGerman.get(Calendar.WEEK_OF_YEAR);
        todayGerman.add(Calendar.DATE, 6);
        // sanity
        assertEquals(week, todayGerman.get(Calendar.WEEK_OF_YEAR));
        CalendarUtils.startOfWeek(todayGerman);
        assertEquals(week, todayGerman.get(Calendar.WEEK_OF_YEAR));
        assertEquals(todayGerman.getFirstDayOfWeek(), todayGerman.get(Calendar.DAY_OF_WEEK));
    }
    
    @Test
    public void testStartOfWeekFromFirstJan() {
        todayGerman.set(Calendar.MONTH, Calendar.JANUARY);
        todayGerman.set(Calendar.DATE, 1);
        if (todayGerman.get(Calendar.DAY_OF_WEEK) == todayGerman.getFirstDayOfWeek()) {
            todayGerman.add(Calendar.YEAR, -1);
        }
        int week = todayGerman.get(Calendar.WEEK_OF_YEAR);
        CalendarUtils.startOfWeek(todayGerman);
        assertEquals(week, todayGerman.get(Calendar.WEEK_OF_YEAR));
        assertEquals(todayGerman.getFirstDayOfWeek(), todayGerman.get(Calendar.DAY_OF_WEEK));
    }
    
    @Test
    public void testStartOfWeekUS() {
        int day = Calendar.WEDNESDAY;
        assertFalse(day == todayUS.getFirstDayOfWeek());
        int week = todayUS.get(Calendar.WEEK_OF_YEAR);
        CalendarUtils.startOfWeek(todayUS);
        assertEquals(week, todayUS.get(Calendar.WEEK_OF_YEAR));
    }
    
    
    @Test
    public void testIsStartOfMonth() {
        // want to be in the middle of a year
        int month = 5;
        todayGerman.set(Calendar.MONTH, month);
        CalendarUtils.startOfMonth(todayGerman);
        Date start = todayGerman.getTime();
        assertTrue(CalendarUtils.isStartOfMonth(todayGerman));
        // sanity: calendar must not be changed
        assertEquals(start, todayGerman.getTime());
        todayGerman.add(Calendar.MILLISECOND, 1);
        assertFalse(CalendarUtils.isStartOfMonth(todayGerman));
    }
    
    @Test
    public void testIsEndOfMonth() {
        // want to be in the middle of a year
        int month = 5;
        todayGerman.set(Calendar.MONTH, month);
        CalendarUtils.endOfMonth(todayGerman);
        Date start = todayGerman.getTime();
        assertTrue(CalendarUtils.isEndOfMonth(todayGerman));
        assertEquals(start, todayGerman.getTime());
        todayGerman.add(Calendar.MILLISECOND, -1);
        assertFalse(CalendarUtils.isEndOfMonth(todayGerman));
        // sanity: calendar must not be changed
    }
    
    @Test
    public void testEndOfMonth() {
        // want to be in the middle of a year
        int month = midJune.get(Calendar.MONTH);
        CalendarUtils.endOfMonth(midJune);
        assertEquals(month, midJune.get(Calendar.MONTH));
        midJune.add(Calendar.MILLISECOND, 1);
        assertEquals(month + 1, midJune.get(Calendar.MONTH));
    }

    @Test
    public void testStartOfMonth() {
        // want to be in the middle of a year
        int month = midJune.get(Calendar.MONTH);
        CalendarUtils.startOfMonth(midJune);
        assertEquals(month, midJune.get(Calendar.MONTH));
        midJune.add(Calendar.MILLISECOND, -1);
        assertEquals(month - 1, midJune.get(Calendar.MONTH));
    }
    
    @Test
    public void testEndOfDay() {
        // want to be in the middle of a month
        int day = midJune.get(Calendar.DAY_OF_MONTH);
        CalendarUtils.endOfDay(midJune);
        assertEquals(day, midJune.get(Calendar.DATE));
        midJune.add(Calendar.MILLISECOND, 1);
        assertEquals(day + 1, midJune.get(Calendar.DATE));
    }

    @Test
    public void testEndOfDayWithReturn() {
        Date date = midJune.getTime();
        Date start = CalendarUtils.endOfDay(midJune, date);
        assertTrue(CalendarUtils.isEndOfDay(midJune));
        assertEquals(start, midJune.getTime());
    }
    
    @Test
    public void testStartOfDay() {
        // want to be in the middle of a month
        int day = midJune.get(Calendar.DAY_OF_MONTH);
        CalendarUtils.startOfDay(midJune);
        assertEquals(day, midJune.get(Calendar.DATE));
        midJune.add(Calendar.MILLISECOND, -1);
        assertEquals(day - 1, midJune.get(Calendar.DATE));
    }

    @Test
    public void testStartOfDayWithReturn() {
        Date date = midJune.getTime();
        Date start = CalendarUtils.startOfDay(midJune, date);
        assertTrue(CalendarUtils.isStartOfDay(midJune));
        assertEquals(start, midJune.getTime());
    }
    
    @Test
    public void testStartOfDayUnique() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+0"));
        CalendarUtils.startOfMonth(calendar);
        // sanity
        assertTrue(CalendarUtils.isStartOfDay(calendar));
        assertNotStartOfDayInTimeZones(calendar, "GMT+");
        assertNotStartOfDayInTimeZones(calendar, "GMT-");
    }
    
    private void assertNotStartOfDayInTimeZones(Calendar calendar, String id) {
        for (int i = 1; i < 13; i++) {
            calendar.setTimeZone(TimeZone.getTimeZone(id + i));
            assertFalse(CalendarUtils.isStartOfDay(calendar));
        }
    }

    @Test
    public void testStartOfMonthUnique() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+0"));
        CalendarUtils.startOfMonth(calendar);
        // sanity
        assertTrue(CalendarUtils.isStartOfMonth(calendar));
        assertNotStartOfMonthInTimeZones(calendar, "GMT+");
        assertNotStartOfMonthInTimeZones(calendar, "GMT-");
    }

    private void assertNotStartOfMonthInTimeZones(Calendar calendar, String id) {
        for (int i = 1; i < 13; i++) {
            calendar.setTimeZone(TimeZone.getTimeZone(id + i));
            assertFalse(CalendarUtils.isStartOfMonth(calendar));
        }
    }
    /**
     * sanity ...
     */
    @Test
    public void testNextMonthCal() {
        todayGerman.set(Calendar.MONTH, Calendar.JANUARY);
        Date date = todayGerman.getTime();
        for (int i = Calendar.JANUARY; i <= Calendar.DECEMBER; i++) {
            int month = todayGerman.get(Calendar.MONTH);
            CalendarUtils.startOfMonth(todayGerman);
            assertEquals(month, todayGerman.get(Calendar.MONTH));
            CalendarUtils.endOfMonth(todayGerman);
            assertEquals(month, todayGerman.get(Calendar.MONTH));
            // restore original and add
            todayGerman.setTime(date);
            todayGerman.add(Calendar.MONTH, 1);
            date = todayGerman.getTime();
            if (i < Calendar.DECEMBER) {
                assertEquals(month + 1, todayGerman.get(Calendar.MONTH));
            } else {
                assertEquals(Calendar.JANUARY, todayGerman.get(Calendar.MONTH));
            }
        }
    }
    
    @Test
    public void testNextMonth() {
        todayGerman.set(Calendar.MONTH, Calendar.JANUARY);
        todayGerman.set(Calendar.DATE, 31);
        for (int i = Calendar.JANUARY; i <= Calendar.DECEMBER; i++) {
            int month = todayGerman.get(Calendar.MONTH);
            long nextMonth = DateUtils.getNextMonth(todayGerman.getTimeInMillis());
            todayGerman.setTimeInMillis(nextMonth);
            if (i < Calendar.DECEMBER) {
                assertEquals(month + 1, todayGerman.get(Calendar.MONTH));
            } else {
                assertEquals(Calendar.JANUARY, todayGerman.get(Calendar.MONTH));
            }
        }
    }

    //----------------- semantic startOf/endOf must flush the calendar
    @Test
    public void testFlushedStartOfWeek() {
        CalendarUtils.startOfWeek(midJune);
        assertFlushed(midJune);
    }
    
    @Test
    public void testFlushedStartOfDay() {
        CalendarUtils.startOfDay(midJune);
        assertFlushed(midJune);
    }
    
    @Test
    public void testFlushedStartOfMonth() {
        CalendarUtils.startOfMonth(midJune);
        assertFlushed(midJune);
    }
    
    @Test
    public void testFlushedStartOfYear() {
        CalendarUtils.startOfYear(midJune);
        assertFlushed(midJune);
    }
    
    @Test
    public void testFlushedStartOfDecade() {
        CalendarUtils.startOfDecade(midJune);
        assertFlushed(midJune);
    }
    
    @Test
    public void testFlushedEndOfWeek() {
        CalendarUtils.endOfWeek(midJune);
        assertFlushed(midJune);
    }
    
    @Test
    public void testFlushedEndOfDay() {
       CalendarUtils.endOfDay(midJune); 
       assertFlushed(midJune);
    }
    
    @Test
    public void testFlushedEndOfMonth() {
        CalendarUtils.endOfMonth(midJune);
        assertFlushed(midJune);
    }
    
    @Test
    public void testFlushedInitially() {
        assertFlushed(todayGerman);
        assertFlushed(todayUS);
        assertFlushed(midJune);
    }
    /**
     * @param todayGerman2
     */
    private void assertFlushed(Calendar calendar) {
        assertTrue("must be flushed but was: " + calendar, CalendarUtils.isFlushed(calendar));
        
    }
    @Override
    protected void setUp() throws Exception {
        todayGerman = Calendar.getInstance(Locale.GERMAN);
        todayUS = Calendar.getInstance(Locale.US);
        midJune = Calendar.getInstance(Locale.GERMAN);
        midJune.set(Calendar.DAY_OF_MONTH, 14);
        midJune.set(Calendar.MONTH, Calendar.JUNE);
        midJune.getTimeInMillis();
    }
 
    @Before
    public void setUpJ4() throws Exception {
        setUp();
    }
    
    @After
    public void tearDownJ4() throws Exception {
        tearDown();
    }

}
