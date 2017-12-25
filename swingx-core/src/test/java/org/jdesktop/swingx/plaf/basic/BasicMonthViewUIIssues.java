/*
 * $Id: BasicMonthViewUIIssues.java 3876 2010-11-03 10:53:27Z kleopatra $
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
package org.jdesktop.swingx.plaf.basic;

import java.awt.ComponentOrientation;
import java.awt.GraphicsEnvironment;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Logger;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXMonthView;
import org.jdesktop.swingx.calendar.CalendarUtils;
import org.junit.Test;

/**
 * Tests to expose known issues of BasicMonthViewUI.
 * 
 * @author Jeanette Winzenburg
 */
public class BasicMonthViewUIIssues extends InteractiveTestCase {
    @SuppressWarnings("all")
    private static final Logger LOG = Logger
            .getLogger(BasicMonthViewUIIssues.class.getName());

    public static void main(String[] args) {
//      setSystemLF(true);
      BasicMonthViewUIIssues  test = new BasicMonthViewUIIssues();
      try {
          test.runInteractiveTests();
//        test.runInteractiveTests(".*Simple.*");
      } catch (Exception e) {
          System.err.println("exception when executing interactive tests:");
          e.printStackTrace();
      }
  }

    
    /**
     * Issue #1245-swingx: incorrect month/dayofweek names for non-core-supported Locales.
     */
    @Test
    public void testLocaleByProviderUIMonthNames() {
        fail("TBD: need test that Locales from service providers are used");
        Locale serbianLatin = getLocal("sh");
        if (serbianLatin == null) {
            LOG.fine("can't run, no service provider for serbian latin" );
            return;
        }
        JXMonthView monthView = new JXMonthView();
        monthView.setLocale(serbianLatin);
        // deprecation removal
//        assertMonths(monthView, serbianLatin);
    }
    
    /**
     * @param string
     * @return
     */
    private Locale getLocal(String language) {
        Locale[] available = Locale.getAvailableLocales();
        for (Locale locale : available) {
            if (language.equals(locale.getLanguage())) return locale;
        }
        return null;
    }


    /**
     * Issue #786-swingx: IllegalStateException when paintDays of April 2008.
     * 
     * Set the default timezone and get the default calendar.
     * 
     */
    public void testTimeZoneCairoCalendarUtils() {
        TimeZone cairo = TimeZone.getTimeZone("Africa/Cairo");
        Calendar calendar = Calendar.getInstance(cairo);
        assertEquals(cairo, calendar.getTimeZone());
        calendar.set(2008, Calendar.MARCH, 31);
        CalendarUtils.startOfMonth(calendar);
        calendar.add(Calendar.MONTH, 1);
        assertTrue(CalendarUtils.isStartOfMonth(calendar));
        CalendarUtils.startOfWeek(calendar);
        // simulate the painting loop
        for (int week = 0; week < 6; week++) {
            for (int day = 0; day < 7; day++) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                assertTrue("must be start of day " + calendar.getTime(),
                        CalendarUtils.isStartOfDay(calendar));
            }
            assertTrue("must be start of week " + calendar.getTime(),
                    CalendarUtils.isStartOfWeek(calendar));
        }
    }
    
    /**
     * Test getDayBounds(Date) for leading dates are null. The assumption is
     * wrong for a leading date in the second month - it's contained in the
     * first!
     */
    public void testDayBoundsLeadingDatesNull() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run test - headless environment");
            return;
        }
        BasicMonthViewUI ui = getRealizedMonthViewUI(ComponentOrientation.LEFT_TO_RIGHT);
        // the ui's calendar is configured to the first displayed day
        Calendar calendar = ui.getCalendar();
        calendar.add(Calendar.MONTH, 1);
        int month = calendar.get(Calendar.MONTH);
        CalendarUtils.startOfWeek(calendar);
        assertFalse("sanity - we have leading dates in the month", month == calendar.get(Calendar.MONTH));
        assertEquals("leading dates must return null bounds", null, 
                ui.getDayBounds(calendar.getTime()));
    }


    /**
     * Returns the ui of a realized JXMonthView with 2 columns and the 
     * given componentOrientation without showingWeekNumbers.
     * 
     * NOTE: this must not be used in a headless environment.
     * 
     * @param co
     * @return
     */
    private BasicMonthViewUI getRealizedMonthViewUI(ComponentOrientation co) {
        return getRealizedMonthViewUI(co, false);
    }

    /**
     * Returns the ui of a realized JXMonthView with
     * given componentOrientation and showingWeekNumbers flag.
     * It's prefColumns/Rows are set to 2. The first displayedDate is 
     * 20. Feb. 2008 (to have fixed leading/trailing dates)
     * 
     * The frame is packed and it's size extended by 40, 40 to
     * give a slight off-position (!= 0) of the months shown. 
     * 
     * 
     * 
     * NOTE: this must not be used in a headless environment.
     * 
     * @param co the componentOrientation to use
     * @return
     */
    private BasicMonthViewUI getRealizedMonthViewUI(ComponentOrientation co,
            boolean isShowingWeekNumbers) {
        JXMonthView monthView = new JXMonthView();
        monthView.setPreferredColumnCount(2);
        monthView.setPreferredRowCount(2);
        monthView.setComponentOrientation(co);
        monthView.setShowingWeekNumber(isShowingWeekNumbers);
        Calendar calendar = monthView.getCalendar();
        calendar.set(2008, Calendar.FEBRUARY, 20);
        monthView.setFirstDisplayedDay(calendar.getTime());
        JXFrame frame = new JXFrame();
        frame.add(monthView);
        frame.pack();
        frame.setSize(frame.getWidth() + 40, frame.getHeight() + 40);
        frame.setVisible(true);
        BasicMonthViewUI ui = (BasicMonthViewUI) monthView.getUI();
        return ui;
    }

    
    /**
     * Do nothing - just to keep the test runner from complaining 
     * if there are no issues.
     *
     */
    public void testDummy() {
        
    }
}
