/*
 * $Id: JXMonthViewIssues.java 3473 2009-08-27 13:17:10Z kleopatra $
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
package org.jdesktop.swingx;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import org.jdesktop.swingx.JXMonthViewTest.Clock;
import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.calendar.CalendarUtils;
import org.jdesktop.swingx.calendar.DaySelectionModel;
import org.jdesktop.swingx.calendar.DateSelectionModel.SelectionMode;
import org.jdesktop.swingx.event.DateSelectionEvent.EventType;
import org.jdesktop.swingx.test.DateSelectionReport;
import org.jdesktop.test.PropertyChangeReport;
import org.jdesktop.test.TestUtils;
import org.junit.Test;

/**
 * Test to expose known issues with JXMonthView.
 * 
 * @author Jeanette Winzenburg
 */
public class JXMonthViewIssues extends InteractiveTestCase {
    @SuppressWarnings("all")
    private static final Logger LOG = Logger.getLogger(JXMonthViewIssues.class
            .getName());


    public static void main(String[] args) {
      setSystemLF(true);
      InteractiveTestCase  test = new JXMonthViewIssues();
      try {
//          test.runInteractiveTests();
//        test.runInteractiveTests("interactive.*Locale.*");
//          test.runInteractiveTests("interactive.*AutoScroll.*");
        test.runInteractiveTests("interactive.*PaintNPE.*");
      } catch (Exception e) {
          System.err.println("exception when executing interactive tests:");
          e.printStackTrace();
      }
  }
    // pre-defined dates - initialized in setUpCalendar
    protected Date today;
    protected Date tomorrow;
    @SuppressWarnings("unused")
    protected Date afterTomorrow;
    protected Date yesterday;
    // the calendar to use, its date is initialized with the today-field in setUpCalendar
    protected Calendar calendar;
    
    /**
     * Issue #1125-swingx: JXMonthView today incorrect.
     * Visualize remaining problem: not kept in synch automatically (note: here
     * we do simulate the case that the monthView is not showing - if it is, the
     * internal timer should take over. Ehem ...).
     */
    public void interactiveTodayUpdate() {
        final Clock clock = new Clock();
        JXMonthView monthView = JXMonthViewTest.createMonthViewWithClock(clock);
        JXFrame frame = wrapInFrame(monthView, "today ");
        Action nextDay = new AbstractAction("next today - update via addNotify") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                clock.nextDay();
                
            }
        };
        addAction(frame, nextDay);
        show(frame);
    }

    /**
     * Issue #1125-swingx: JXMonthView today incorrect.
     * Here we test the update if not added, still failing ...
     */
    @Test
    public void testUpdateTodayInAddNotify() {
        Clock clock = new Clock();
        clock.nextDay();
        JXMonthView monthView = JXMonthViewTest.createMonthViewWithClock(clock);
        assertEquals("sanity: monthView takes clock current initially", 
                CalendarUtils.startOfDay(monthView.getCalendar(), clock.getCurrentDate()), monthView.getToday());
        // increment clock
        clock.nextDay();
        assertEquals(" monthView must update today in addNotify", 
                CalendarUtils.startOfDay(monthView.getCalendar(), clock.getCurrentDate()), monthView.getToday());
    }

    public void interactiveMonthViewAntialisedPaint() {
        JXMonthView custom =  new JXMonthView() {
            
            @Override
            public void paint(Graphics g) {
                Toolkit tk = Toolkit.getDefaultToolkit ();
                Map<?, ?> map = (Map<?, ?>) (tk.getDesktopProperty ("awt.font.desktophints"));
                LOG.info("hints " + map);
                if (map != null) {
                    ((Graphics2D) g).addRenderingHints(map);
                }
                
                super.paint(g);
            }
            
        };
        
        final JXMonthView monthView = new JXMonthView();
        JComponent comp = Box.createHorizontalBox();
        comp.add(custom);
        comp.add(monthView);
        JXFrame frame = wrapInFrame(comp, "antialiased 1.6paint left");
        show(frame);
    }
    
    /**
     * Issue #736-swingx: monthView cannot cope with minimalDaysInFirstWeek.
     * 
     */
    public void interactiveDayAt() {
        final JXMonthView monthView = new JXMonthView();
        monthView.setTraversable(true);
        monthView.setShowingWeekNumber(true);
        monthView.setShowingLeadingDays(true);
        monthView.setShowingTrailingDays(true);
        monthView.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                LOG.info("dayAt " + e.getPoint() + ": "
                        + "\n" + monthView.getDayAtLocation(e.getX(), e.getY())
                                );
            }
            
        });
        Action action = new AbstractActionExt("toggle minimal") {

            public void actionPerformed(ActionEvent e) {
                int minimal = monthView.getSelectionModel().getMinimalDaysInFirstWeek();
                monthView.getSelectionModel().setMinimalDaysInFirstWeek(minimal > 1 ? 1 : 4);
            }
            
        };
        final JXFrame frame = wrapInFrame(monthView, "click day");
        addAction(frame, action);
        show(frame);
    }

    /**
     * Issue #736-swingx: monthView cannot cope with minimalDaysInFirstWeek.
     * 
     * Here: look at impact of forcing the minimalDays to a value different
     * from the calendar. Days must be displayed in starting from the 
     * first row under the days-of-week.
     * 
     * Not yet completely fixed: for very late firstDayOfWeek, the Jan is incompletely
     * painted for mininalDays > 1. Rare enough to ignore for now?
     */
    public void interactiveMinimalDaysInFirstWeek() {
        final JXMonthView monthView = new JXMonthView();
        monthView.setTraversable(true);
        monthView.setShowingWeekNumber(true);
        monthView.setShowingLeadingDays(true);
        monthView.setShowingTrailingDays(true);
        Action action = new AbstractActionExt("toggle minimal") {

            public void actionPerformed(ActionEvent e) {
                int minimal = monthView.getSelectionModel().getMinimalDaysInFirstWeek();
                monthView.getSelectionModel().setMinimalDaysInFirstWeek(minimal > 1 ? 1 : 4);
            }
            
        };
        final JXFrame frame = wrapInFrame(monthView, "click unselectable fires ActionEvent");
        addAction(frame, action);
        addComponentOrientationToggle(frame);
        final JComboBox dayOfWeekComboBox = new JComboBox(new String[]{"Sunday", "Monday", "Tuesday",
                "Wednesday", "Thursday", "Friday", "Saturday"});
        dayOfWeekComboBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int selected = dayOfWeekComboBox.getSelectedIndex();
                monthView.setFirstDayOfWeek(selected + Calendar.SUNDAY);
                
            }
            
        });
        dayOfWeekComboBox.setSelectedIndex(monthView.getFirstDayOfWeek() - Calendar.SUNDAY);
        addStatusComponent(frame, dayOfWeekComboBox);
        show(frame);
    }

    /**
     * Issue #567-swingx: JXDatepicker - clicking on unselectable date clears
     * picker's selection.
     * 
     * Here: visualize JXMonthView's behaviour. It fires a commit ... probably the 
     * wrong thing to do?. 
     * PENDING: better control the bounds ... 
     * PENDING: move into monthView after rename
     */
    public void interactiveBoundsMonthViewClickUnselectable() {
        JXMonthView monthView = new JXMonthView();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 7);
        monthView.setLowerBound(calendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, 20);
        monthView.setUpperBound(calendar.getTime());
        ActionListener l = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                LOG.info("got action " + e);
                
            }
            
        };
        monthView.addActionListener(l);
        showInFrame(monthView, "click unselectable fires ActionEvent");
    }

    
    /**
     * Issue #657-swingx: JXMonthView - unintuitive week-wise navigation with bounds
     * 
     * In a month, keyboard navigation beyond the upper/lower bound is prevented.
     * There's a leak in the region of the leading/trailing dates 
     * when navigating week-wise. 
     * 
     * PENDING: move into monthView after rename
     */
    public void interactiveBoundsNavigateBeyond() {
        JXMonthView monthView = new JXMonthView();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 2);
        // access the model directly requires to "clean" the date
        monthView.setLowerBound(calendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, 27);
        monthView.setUpperBound(calendar.getTime());
        ActionListener l = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                LOG.info("got action " + e);
                
            }
            
        };
        monthView.addActionListener(l);
        showInFrame(monthView, "navigate beyond bounds");
    }

    

    /**
     * Issue #657-swingx: JXMonthView - unintuitive week-wise navigation with bounds
     * 
     * Can't navigate at all if today is beyound the bounds
     * PENDING: move into monthView after rename
     */
    public void interactiveBoundsNavigateLocked() {
        JXMonthView monthView = new JXMonthView();
        // same time as monthView's today
        Calendar calendar = Calendar.getInstance();
        // set upper bound a week before today, 
        // to block navigation into all directions
        calendar.add(Calendar.DAY_OF_MONTH, -8);
        monthView.setUpperBound(calendar.getTime());
        ActionListener l = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                LOG.info("got action " + e);
                
            }
            
        };
        monthView.addActionListener(l);
        showInFrame(monthView, "navigate: locked for today beyond bounds");
    }

    
//----------------------

    /**
     * Issue #618-swingx: JXMonthView displays problems with non-default
     * timezones.
     * 
     * Here: test that the first displayed date is offset by offset diff of 
     * timezones.
     * Configure the monthView with a fixed timezone to clear up the mist ...
     * 
     */
    public void testTimeZoneChangeToday() {
        Locale componentDefault = JComponent.getDefaultLocale();
        try {
        FixedLocaleSelectionModel model = new FixedLocaleSelectionModel();
        JComponent.setDefaultLocale(model.getLocale());
        Locale.setDefault(model.getLocale());
        Calendar modelCal = model.getCalendar();
        Date fixed = modelCal.getTime();
        

        JXMonthView monthView = new JXMonthView(fixed, model);
        Calendar myCal = monthView.getCalendar();
//        myCal.setTimeInMillis(myCal.getTimeInMillis() + 2667600000l);
        LOG.info("monthView timezone " + monthView.getTimeZone() 
                + "\n" + myCal + 
                "\n" + monthView.getLocale());
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, monthView.getLocale());
        format.setTimeZone(myCal.getTimeZone());
        LOG.info("" + format.format(myCal.getTime()));

        
        // config with a known timezone and date
        TimeZone tz = TimeZone.getTimeZone("GMT+4");
        Calendar calendar = Calendar.getInstance(tz);
        calendar.set(2008, Calendar.MARCH, 31, 11, 45);
        Date today = calendar.getTime();
        
        monthView.setTimeZone(tz);
        monthView.setFirstDisplayedDay(today);
        Date anchor = monthView.getAnchorDate();
        assertEquals(today, anchor);
        Date firstDisplayed = monthView.getFirstDisplayedDay();
        calendar.setTime(firstDisplayed);
        assertTrue(CalendarUtils.isStartOfMonth(calendar));
        
        // get another timezone with known offset
        TimeZone tzOther = TimeZone.getTimeZone("GMT+7");
        // newOffset minus oldOffset (real time, adjusted to DST)
        int oldOffset = tz.getOffset(anchor.getTime());
        int newOffset = tzOther.getOffset(anchor.getTime());
        int realOffset = oldOffset - newOffset;
        monthView.setTimeZone(tzOther);
        Calendar otherCalendar = Calendar.getInstance(tzOther);
        otherCalendar.setTime(monthView.getFirstDisplayedDay());
        assertTrue(CalendarUtils.isStartOfMonth(otherCalendar));
        // PENDING JW: sure this is the correct direction of the shift?
        // yeah, think so: the anchor is fixed, moving the timezone results
        // in a shift into the opposite direction of the offset
        assertEquals("first displayed must be offset by real offset", 
                realOffset,  monthView.getFirstDisplayedDay().getTime() - firstDisplayed.getTime());

        } finally {
            // reset componentLocale
            JComponent.setDefaultLocale(componentDefault);
            Locale.setDefault(componentDefault);
        }
        
    }

    /**
     * Try to track spurious timezone failures. Extended to mimic server
     * context 31mar2008, us/pacific timezone, en_US locale. No luck...
     */
    public static class FixedLocaleSelectionModel extends DaySelectionModel {

        @Override
        public void setLocale(Locale locale) {
            this.locale = new Locale("en", "US"); //Locale.US;
            calendar = Calendar.getInstance(TimeZone.getTimeZone("US/Pacific"), this.locale);
            calendar.set(2008, Calendar.MARCH, 31, 11, 45);
            LOG.info("calendar timezone " + calendar.getTimeZone() 
                    + "\n" + calendar + 
                    "\n" + this.locale);
            DateFormat format = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, this.locale);
            format.setTimeZone(calendar.getTimeZone());
            LOG.info("" + format.format(calendar.getTime()));
            fireValueChanged(EventType.CALENDAR_CHANGED);
        }
        
    }

    /**
     * Issue #618-swingx: JXMonthView displays problems with non-default
     * timezones.
     * 
     * Here: test that the first displayed date is offset by offset diff of 
     * timezones.
     * Configure the monthView with a fixed timezone to clear up the mist ...
     * 
     */
    public void testTimeZoneChangeOffsetFirstDisplayedDate() {
        JXMonthView monthView = new JXMonthView();
        // config with a known timezone and date
        TimeZone tz = TimeZone.getTimeZone("GMT+4");
        monthView.setTimeZone(tz);
        Calendar calendar = Calendar.getInstance(tz);
        Date today = calendar.getTime();
        monthView.setFirstDisplayedDay(today);
        Date anchor = monthView.getAnchorDate();
        assertEquals(today, anchor);
        Date firstDisplayed = monthView.getFirstDisplayedDay();
        calendar.setTime(firstDisplayed);
        assertTrue(CalendarUtils.isStartOfMonth(calendar));
        
        // get another timezone with known offset
        TimeZone tzOther = TimeZone.getTimeZone("GMT+7");
        // newOffset minus oldOffset (real time, adjusted to DST)
        int oldOffset = tz.getOffset(anchor.getTime());
        int newOffset = tzOther.getOffset(anchor.getTime());
        int realOffset = oldOffset - newOffset;
        monthView.setTimeZone(tzOther);
        Calendar otherCalendar = Calendar.getInstance(tzOther);
        otherCalendar.setTime(monthView.getFirstDisplayedDay());
        assertTrue(CalendarUtils.isStartOfMonth(otherCalendar));
        // PENDING JW: sure this is the correct direction of the shift?
        // yeah, think so: the anchor is fixed, moving the timezone results
        // in a shift into the opposite direction of the offset
        assertEquals("first displayed must be offset by real offset", 
                realOffset,  monthView.getFirstDisplayedDay().getTime() - firstDisplayed.getTime());
    }
    

    
    /**
     * characterize what a weekinterval selection is meant to do.
     * MultipleIntervalSelection where each interval is one or more weeks?
     * Here: does not snap start/end of week to start/end of day? 
     */
    @SuppressWarnings("deprecation")
    public void testWeekInterval() {
        JXMonthView monthView = new JXMonthView();
        CalendarUtils.startOfWeek(calendar);
        calendar.add(Calendar.WEEK_OF_YEAR, 3);
//        calendar.add(Calendar.HOUR_OF_DAY, - 3);
        monthView.cleanupWeekSelectionDates(today, calendar.getTime());
        assertNotNull("not doing anything?", monthView.modifiedEndDate);
        calendar.setTime(monthView.modifiedStartDate);
        LOG.info("start/end" + monthView.modifiedStartDate + "/" + monthView.modifiedEndDate);
        assertTrue("interval first must be start of week", CalendarUtils.isStartOfWeek(calendar));
        calendar.setTime(monthView.modifiedEndDate);
        assertTrue("interval last must be end of week", CalendarUtils.isEndOfWeek(calendar));
    }
    
    /**
     * characterize what a weekinterval selection is meant to do.
     * MultipleIntervalSelection where each interval is one or more weeks?
     * Here: does nothing if single day selected?
     */
    @SuppressWarnings("deprecation")
    public void testWeekIntervalOneDay() {
        JXMonthView monthView = new JXMonthView();
        monthView.cleanupWeekSelectionDates(today, today);
        calendar.setTime(monthView.modifiedEndDate);
        assertTrue("interval last must be end of week", CalendarUtils.isEndOfWeek(calendar));
        calendar.setTime(monthView.modifiedStartDate);
        assertTrue("interval first must be start of week", CalendarUtils.isStartOfWeek(calendar));
        LOG.info("start/end" + monthView.modifiedStartDate + "/" + monthView.modifiedEndDate);
    }
    
    /**
     * characterize what a weekinterval selection is meant to do.
     * MultipleIntervalSelection where each interval is one or more weeks?
     * Here: does nothing if two days interval?
     */
    @SuppressWarnings("deprecation")
    public void testWeekIntervalTwoDays() {
        JXMonthView monthView = new JXMonthView();
        monthView.cleanupWeekSelectionDates(today, tomorrow);
        calendar.setTime(monthView.modifiedEndDate);
        assertTrue("interval last must be end of week", CalendarUtils.isEndOfWeek(calendar));
        calendar.setTime(monthView.modifiedStartDate);
        assertTrue("interval first must be start of week", CalendarUtils.isStartOfWeek(calendar));
        LOG.info("start/end" + monthView.modifiedStartDate + "/" + monthView.modifiedEndDate);
    }
    /**
     * Issue #733-swingx: TimeZone in model and monthView not synched.
     *  
     *  Test that the selected is normalized in the monthView's timezone. 
     */
    public void testCalendarsTimeZoneFlaggedDate() {
        JXMonthView monthView = new JXMonthView();
        // config with a known timezone and date
        TimeZone tz = TimeZone.getTimeZone("GMT+4");
        monthView.setTimeZone(tz);
        Date date = new Date();
        monthView.setFlaggedDates(new Date[] {date});
        assertTrue(monthView.isFlaggedDate(date));
        fail("no way to test same normalization for flagged and selected dates");
    }

    /**
     * Issue #733-swingx: TimeZone in model and monthView not synched.
     * 
     * Test selected - tells nothing, because it's normalized in the 
     * model's (default) calendar.
     */
    public void testCalendarsTimeZoneSelectedDate() {
        JXMonthView monthView = new JXMonthView();
        // config with a known timezone and date
        TimeZone tz = TimeZone.getTimeZone("GMT+4");
        monthView.setTimeZone(tz);
        Date date = new Date();
        monthView.setSelectionDate(date);
        assertTrue(monthView.isSelected(date));
        fail("test passes - but tells nothing");
    }

    public void testNoSelectionMode() {
//        JXMonthView monthView = new JXMonthView();
//        monthView.setSelectionMode(SelectionMode.NO_SELECTION);
//
//        Date date = new Date();
//        monthView.setSelectionInterval(date, date);
//        assertTrue(monthView.isSelectionEmpty());
        fail("revisit: no selection mode");
    }

    /**
     * temporarily removed weekinterval selection.
     * Need to review - why not in selectionModel?
     */
    public void testWeekIntervalSelection() {
//        // PENDING: simplify to use pre-defined dates
//        JXMonthView monthView = new JXMonthView(Locale.US);
//        monthView.setSelectionMode(JXMonthView.SelectionMode.WEEK_INTERVAL_SELECTION);
//
//        // Use a known date that falls on a Sunday, which just happens to be my birthday.
//        calendar.set(Calendar.YEAR, 2006);
//        calendar.set(Calendar.MONTH, Calendar.APRIL);
//        calendar.set(Calendar.DAY_OF_MONTH, 9);
//        CalendarUtils.startOfDay(calendar);
//        Date startDate = calendar.getTime();
////        Date startDate = cleanupDate(calendar);
//
//        Date endDate;
//        calendar.set(Calendar.DAY_OF_MONTH, 13);
//        endDate = calendar.getTime();
//
//        monthView.setSelectionInterval(startDate, endDate);
//        SortedSet<Date> selection = monthView.getSelection();
//        assertTrue(startDate.equals(selection.first()));
//        assertTrue(endDate.equals(selection.last()));
//
//        calendar.set(Calendar.DAY_OF_MONTH, 20);
//        endDate = calendar.getTime();
//        monthView.setSelectionInterval(startDate, endDate);
//
//        calendar.set(Calendar.DAY_OF_MONTH, 22);
//        endDate = calendar.getTime();
//        selection = monthView.getSelection();
//
//        assertEquals(startDate, selection.first());
//        assertTrue(endDate.equals((selection.last())));
        fail("revisit: week selection");
    }

    public void testModelSelectionUpdate() {
//        JXMonthView monthView = new JXMonthView();
//
//        // The JXMonthView uses an underlying model mode of single selection when it is in no selection mode.
//        monthView.setSelectionMode(SelectionMode.NO_SELECTION);
//        assertTrue(
//                DateSelectionModel.SelectionMode.SINGLE_SELECTION == monthView.getSelectionModel().getSelectionMode());
//
//        monthView.setSelectionMode(SelectionMode.SINGLE_SELECTION);
//        assertTrue(
//                DateSelectionModel.SelectionMode.SINGLE_SELECTION == monthView.getSelectionModel().getSelectionMode());
//
//        monthView.setSelectionMode(SelectionMode.SINGLE_INTERVAL_SELECTION);
//        assertTrue(
//                DateSelectionModel.SelectionMode.SINGLE_INTERVAL_SELECTION ==
//                        monthView.getSelectionModel().getSelectionMode());
//
//        // The JXMonthView uses an underlying model mode of single interval selection when it is in week selection mode.
//        monthView.setSelectionMode(SelectionMode.WEEK_INTERVAL_SELECTION);
//        assertTrue(
//                DateSelectionModel.SelectionMode.SINGLE_INTERVAL_SELECTION ==
//                        monthView.getSelectionModel().getSelectionMode());
//
//        monthView.setSelectionMode(SelectionMode.MULTIPLE_INTERVAL_SELECTION);
//        assertTrue(
//                DateSelectionModel.SelectionMode.MULTIPLE_INTERVAL_SELECTION ==
//                        monthView.getSelectionModel().getSelectionMode());
        fail("revisit: selection modes");

    }

    /**
     * Issue #618-swingx: JXMonthView displays problems with non-default
     * timezones.
     * 
     * Here: test today notification.
     */
    public void testTimeZoneChangeTodayNotification() {
        JXMonthView monthView = new JXMonthView();
        TimeZone other = getTimeZone(monthView.getTimeZone(), CalendarUtils.THREE_HOURS);
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        monthView.setTimeZone(other);
        Calendar calendar = Calendar.getInstance();
        CalendarUtils.startOfDay(calendar);
        Date today = calendar.getTime();
        calendar.setTimeZone(other);
        CalendarUtils.startOfDay(calendar);
        Date otherToday = calendar.getTime(); 
            // sanity
        assertFalse(today.equals(otherToday));
        TestUtils.assertPropertyChangeEvent(report, 
                "today", today.getTime(), otherToday.getTime(), false);
        fail("spurious failures - probably wrong assumption in Timezone math");
    }
   
   
   
   /**
    * BasicMonthViewUI: use adjusting api in keyboard actions.
    * Here: test add selection action.
    * 
    * TODO: this fails (unrelated to the adjusting) because the
    * the selectionn changing event type is DATES_SET instead of 
    * the expected DATES_ADDED.  What's wrong - expectation or type?
    */
   public void testAdjustingSetOnAdd() {
       JXMonthView view = new JXMonthView();
       // otherwise the add action isn't called
       view.setSelectionMode(SelectionMode.SINGLE_INTERVAL_SELECTION);
       DateSelectionReport report = new DateSelectionReport(view.getSelectionModel());
       Action select = view.getActionMap().get("adjustSelectionNextDay");
       select.actionPerformed(null);
       assertTrue("ui keyboard action must have started model adjusting", 
               view.getSelectionModel().isAdjusting());
       assertEquals(2, report.getEventCount());
       // assert that the adjusting is fired before the add
       // only: the ui fires a set instead - bug or feature?
        assertEquals(EventType.DATES_ADDED, report.getLastEvent().getEventType());
   }

//------------------- utility
   
   /**
    * Returns a timezone with a rawoffset with a different offset.
    * 
    * 
    * PENDING: this is acutally for european time, not really thought of 
    *   negative/rolling +/- problem?
    * 
    * @param timeZone the timezone to start with 
    * @param diffRawOffset the raw offset difference.
    * @return
    */
   @SuppressWarnings("unused")
   private TimeZone getTimeZone(TimeZone timeZone, int diffRawOffset) {
       int offset = timeZone.getRawOffset();
       int newOffset = offset < 0 ? offset + diffRawOffset : offset - diffRawOffset;
       String[] availableIDs = TimeZone.getAvailableIDs(newOffset);
       TimeZone newTimeZone = TimeZone.getTimeZone(availableIDs[0]);
       return newTimeZone;
   }

   @SuppressWarnings("unused")
   private String[] getTimeZoneIDs() {
       List<String> zoneIds = new ArrayList<String>();
       for (int i = -12; i <= 12; i++) {
           String sign = i < 0 ? "-" : "+";
           zoneIds.add("GMT" + sign + i);
       }
       return zoneIds.toArray(new String[zoneIds.size()]);
   }
   
  
    @Override
    protected void setUp() throws Exception {
        setUpCalendar();
    }
    /**
     * Initializes the calendar to the default instance and the predefined dates
     * in the coordinate system of the calendar. Note that the hour is set
     * to "about" in all dates, to be reasonably well into the day. The time
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

  
}
