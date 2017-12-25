/**
 * $Id: JXMonthViewTest.java 3689 2010-05-03 12:49:24Z kleopatra $
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
package org.jdesktop.swingx;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.jdesktop.swingx.calendar.CalendarUtils;
import org.jdesktop.swingx.calendar.DateSelectionModel;
import org.jdesktop.swingx.calendar.DaySelectionModel;
import org.jdesktop.swingx.calendar.SingleDaySelectionModel;
import org.jdesktop.swingx.calendar.DateSelectionModel.SelectionMode;
import org.jdesktop.swingx.event.DateSelectionEvent.EventType;
import org.jdesktop.swingx.hyperlink.AbstractHyperlinkAction;
import org.jdesktop.swingx.test.DateSelectionReport;
import org.jdesktop.test.ActionReport;
import org.jdesktop.test.PropertyChangeReport;
import org.jdesktop.test.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test case for <code>JXMonthView</code>
 *
 * There's another class with passing unit tests for JXMonthView (JXMonthViewVisualTest)
 * because this 
 * extended mock while the other extends InteractiveTestCase. Both are expected
 * to pass. As mock had been removed, they should be merged.
 * 
 * @author Joshua Outwater
 */
@RunWith(JUnit4.class)
public class JXMonthViewTest extends InteractiveTestCase {
    private static final Logger LOG = Logger.getLogger(JXMonthViewTest.class
            .getName());
    private Locale componentLocale;
    // pre-defined reference dates - all relative to current date at around 5 am
    private Date today;
    private Date tomorrow;
    private Date afterTomorrow;
    private Date yesterday;
    // calendar default instance init with today
    private Calendar calendar;

    private JXMonthView monthView;
    
    @Override
    @Before
       public void setUp() {
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

        //the test is configured for a US defaulted system
        //the localization tests handle actual localization issues
        componentLocale = JComponent.getDefaultLocale();
//        LOG.info("componentLocale " + componentLocale);
//        JComponent.setDefaultLocale(Locale.US);
        monthView = new JXMonthView();
    }

    @Override
    @After
       public void tearDown() {
        JComponent.setDefaultLocale(componentLocale);
    }

    public static void main(String[] args) {
        JXMonthViewTest test = new JXMonthViewTest();
        try {
            test.runInteractiveTests();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Issue #1318-swingx: NPE in removeNotify
     * @throws InvocationTargetException 
     * @throws InterruptedException 
     */
    @Test
    public void testTodayTimerNPE() throws InterruptedException, InvocationTargetException {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run ui test - headless environment");
            return;
        }
        JXMonthView monthView = new JXMonthView();
        final JXFrame frame = new JXFrame();
        frame.add(monthView);
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                frame.dispose();
                
            }
        });
    }
    
    /**
     * Issue #1046-swingx: month title not updated when traversing months
     * (programatically or by navigating in monthView)
     * Issue #1245-swingx: incorrect month/dayofweek names for non-core-supported Locales.
     * 
     */
    @Test
    public void testZoomableNameByLocaleProvider() {
        Locale serbianLatin = getLocal("sh");
        if (serbianLatin == null) {
            LOG.fine("can't run, no service provider for serbian latin" );
            return;
        }
        JXMonthView monthView = new JXMonthView();
        AbstractHyperlinkAction<?> action = (AbstractHyperlinkAction<?>) monthView.getActionMap().get("zoomOut");
        assertSame(monthView, action.getTarget());
        monthView.setLocale(serbianLatin);
        String[] monthNames = DateFormatSymbols.getInstance(monthView.getLocale()).getMonths();
        Calendar calendar = monthView.getCalendar();
        int month = calendar.get(Calendar.MONTH);
        assertTrue("name must be updated with locale, expected: " + monthNames[month] + " was: " + action.getName(), 
                action.getName().startsWith(monthNames[month]));
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
     * Issue #1143-swingx: NPE after setTimeZone/setModel.
     */
    @Test
    public void testFirstDisplayedDayAfterTimeZone() {
        JXMonthView monthView = new JXMonthView();
        TimeZone tz = TimeZone.getTimeZone("GMT+4");
        if (monthView.getTimeZone().equals(tz)) {
            tz = TimeZone.getTimeZone("GMT+5");
        }
        monthView.setTimeZone(tz);
        assertTrue(CalendarUtils.isStartOfDay(monthView.getCalendar()));
        monthView.setSelectionModel(new SingleDaySelectionModel());
        assertTrue(CalendarUtils.isStartOfDay(monthView.getCalendar()));
    }
    
    /**
     * Issue #1125-swingx: JXMonthView today incorrect.
     * 
     */
    @Test
    public void testUpdateTodayInAddNotify() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run ui test - headless environment");
            return;
        }
        Clock clock = new Clock();
        clock.nextDay();
        JXMonthView monthView = createMonthViewWithClock(clock);
        assertEquals("sanity: monthView takes clock current initially", 
                CalendarUtils.startOfDay(monthView.getCalendar(), clock.getCurrentDate()), monthView.getToday());
        // increment clock
        clock.nextDay();
        // realize monthView
        showInFrame(monthView, "");
        assertEquals(" monthView must update today in addNotify", 
                CalendarUtils.startOfDay(monthView.getCalendar(), clock.getCurrentDate()), monthView.getToday());
    }

    /**
     * Creates and returns a JXMonthView which uses the given time source for current.
     * 
     * @param clock the time source used by monthView.
     */
    public static JXMonthView createMonthViewWithClock(final Clock clock) {
        JXMonthView monthView = new JXMonthView() {

            @Override
            Date getCurrentDate() {
                return clock.getCurrentDate();
            }
            
        };
        return monthView;
    }
    
    /**
     * Quick class to inject a current time source.
     */
    public static class Clock {
        private static int millisPerDay = 24 * 60 * 60 * 1000;
        private Date current;

        /**
         * Instantiates a time source with default current.
         */
        public Clock() {
            this(null);
        }
        
        /**
         * Instantiates a time source with the given date as current. If null, uses
         * System.currentTimeMillis().
         * 
         * @param date the current date.
         */
        public Clock(Date date) {
            this.current = date != null ? date : new Date(System.currentTimeMillis()); 
        }
        /**
         * Returns the current time.
         * 
         * @return current time.
         */
        public Date getCurrentDate() {
            return current;
        }
        
        /**
         * Increments current by one day.
         */
        public void nextDay() {
            current = new Date(current.getTime() + millisPerDay);
        }
        
    }
    /**
     * Issue #1072-swingx: nav icons incorrect for RToL if zoomable
     */
    @Test
    public void testNavigationIconsUpdatedWithCO() {
       Action action = monthView.getActionMap().get("nextMonth"); 
       if (monthView.getComponentOrientation().isLeftToRight()) {
           Icon icon = (Icon) action.getValue(Action.SMALL_ICON);
           assertNotNull("sanity: the decorated month nav action has an icon", icon);
           assertEquals(UIManager.getIcon("JXMonthView.monthUpFileName"), icon);
           monthView.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
//           assertNotSame(icon, action.getValue(Action.SMALL_ICON));
           assertEquals(action.getValue(Action.SMALL_ICON), UIManager.getIcon("JXMonthView.monthDownFileName"));
       } else {
           
       }
    }
    
    /**
     * Issue #1046-swingx: month title not updated when traversing months
     * (programatically or by navigating in monthView)
     */
    @Test
    public void testZoomableNameOnMonthChange() {
        JXMonthView monthView = new JXMonthView();
        AbstractHyperlinkAction<?> action = (AbstractHyperlinkAction<?>) monthView.getActionMap().get("zoomOut");
        assertSame(monthView, action.getTarget());
        String[] monthNames = DateFormatSymbols.getInstance(monthView.getLocale()).getMonths();
        Calendar calendar = monthView.getCalendar();
        int month = calendar.get(Calendar.MONTH);
        assertTrue(action.getName().startsWith(monthNames[month]));
        calendar.add(Calendar.MONTH, 1);
        monthView.setFirstDisplayedDay(calendar.getTime());
        int nextMonth = calendar.get(Calendar.MONTH);
        assertTrue("month changed: old/new " + month + "/" + nextMonth, nextMonth != month);
        assertTrue("name must be updated, expected: " + monthNames[nextMonth] + " was: " + action.getName()
                , action.getName().startsWith(monthNames[nextMonth]));
    }

    /**
     * Issue #1046-swingx: month title not updated when traversing months
     * (programatically or by navigating in monthView)
     */
    @Test
    public void testZoomableNameOnLocaleChange() {
        JXMonthView monthView = new JXMonthView();
        AbstractHyperlinkAction<?> action = (AbstractHyperlinkAction<?>) monthView.getActionMap().get("zoomOut");
        assertSame(monthView, action.getTarget());
        Locale locale = Locale.FRENCH;
        if (locale.equals(monthView.getLocale())) {
            locale = Locale.GERMAN;
        }
        monthView.setLocale(locale);
        String[] monthNames = DateFormatSymbols.getInstance(monthView.getLocale()).getMonths();
        Calendar calendar = monthView.getCalendar();
        int month = calendar.get(Calendar.MONTH);
        assertTrue("name must be updated with locale, expected: " + monthNames[month] + " was: " + action.getName(), 
                action.getName().startsWith(monthNames[month]));
    }

    /**
     * Issue #1046-swingx: month title not updated when traversing months
     * (programatically or by navigating in monthView)
     * 
     * Test that zoomOutActin is installed for zoomable.
     */
    @Test
    public void testZoomableZoomOutAction() {
        JXMonthView monthView = new JXMonthView();
        assertNotNull("monthView must have zoomOutAction", monthView.getActionMap().get("zoomOut"));
        monthView.setZoomable(true);
        assertNotNull("monthView must have zoomOutAction", monthView.getActionMap().get("zoomOut"));
    }
    
    
    /**
     * Test that navigational actions are installed
     */
    @Test
    public void testNavigationActionsInstalled() {
        JXMonthView monthView = new JXMonthView();
        assertActionInstalled(monthView, "scrollToNextMonth");
        assertActionInstalled(monthView, "scrollToPreviousMonth");
        // actions mapped by CalendarHeaderHandler
        assertActionInstalled(monthView, "nextMonth");
        assertActionInstalled(monthView, "previousMonth");
    }
    
    /**
     * @param monthView
     * @param actionKey
     */
    private void assertActionInstalled(JXMonthView monthView, String actionKey) {
        assertNotNull("ui must have installed action for " + actionKey, monthView.getActionMap().get(actionKey));
    }

    /**
     * Test that navigational actions are working as expected.
     */
    @Test
    public void testNavigationActionsWorking() {
        assertActionPerformed(new JXMonthView(), "scrollToNextMonth", Calendar.MONTH, 1);
        assertActionPerformed(new JXMonthView(), "nextMonth", Calendar.MONTH, 1);
        assertActionPerformed(new JXMonthView(), "scrollToPreviousMonth", Calendar.MONTH, -1);
        assertActionPerformed(new JXMonthView(), "previousMonth", Calendar.MONTH, -1);
    }
    
    private void assertActionPerformed(JXMonthView monthView, String actionKey, int calendarField, int amount) {
        Calendar calendar = monthView.getCalendar();
        calendar.add(calendarField, amount);
        Action action = monthView.getActionMap().get(actionKey);
        action.actionPerformed(null);
        assertEquals(calendar.getTime(), monthView.getFirstDisplayedDay());
    }


    @Test
    public void testZoomableProperty() {
        assertFalse("default zoomable is off", monthView.isZoomable());
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        monthView.setZoomable(true);
        TestUtils.assertPropertyChangeEvent(report, "zoomable", false, true);
        assertTrue("traversable follows zoomable", monthView.isTraversable());
    }
    
    /**
     * Issue #932-swingx: ui overwrites custom settings.
     * test daysoftheweekforeground property.
     * 
     */
    @Test
    public void testDaysOfTheWeekForegroundUpdateUI() {
        Color old = monthView.getDaysOfTheWeekForeground();
        Color color = Color.PINK;
        if (color.equals(old)) {
            color = Color.MAGENTA;
        }
        monthView.setDaysOfTheWeekForeground(color);
        monthView.updateUI();
        assertEquals(color, monthView.getDaysOfTheWeekForeground());
    }

    /**
     * Issue #??-swingx: test accaptable values in setDayForeground.
     * Doc'ed to accept Calendar.SUNDAY - Calendar.SATURDAY, but not 
     * enforced.
     * 
     */
    @Test(expected=IllegalArgumentException.class)
    public void testDayForegroundBeforeSunday() {
        monthView.setDayForeground(Calendar.SUNDAY - 1, Color.RED);
    }

    /**
     * Issue #??-swingx: test accaptable values in setDayForeground.
     * Doc'ed to accept Calendar.SUNDAY - Calendar.SATURDAY, but not 
     * enforced.
     * 
     */
    @Test(expected=IllegalArgumentException.class)
    public void testDayForegroundAfterSaturday() {
        monthView.setDayForeground(Calendar.SATURDAY + 1, Color.RED);
    }

 
    /**
     * Issue #931-swingx: missing change notification.
     * test setting monthStringInsets property.
     * 
     */
    @Test
    public void testPreferredRowCount() {
        int old = monthView.getPreferredRowCount();
        int color = old + 20;
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        monthView.setPreferredRowCount(color);
        assertEquals(color, monthView.getPreferredRowCount());
        TestUtils.assertPropertyChangeEvent(report, "preferredRowCount", old, color);
    }

    /**
     * Issue #931-swingx: missing change notification.
     * test setting monthStringInsets property.
     * 
     */
    @Test
    public void testPreferredColumnCount() {
        int old = monthView.getPreferredColumnCount();
        int color = old + 20;
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        monthView.setPreferredColumnCount(color);
        assertEquals(color, monthView.getPreferredColumnCount());
        TestUtils.assertPropertyChangeEvent(report, "preferredColumnCount", old, color);
    }

    /**
     * Issue #931-swingx: missing change notification.
     * test setting monthStringInsets property.
     * 
     */
    @Test
    public void testMonthStringInsets() {
        Insets old = monthView.getMonthStringInsets();
        Insets color = new Insets(21, 22, 23, 24);
        assertFalse("sanity: the new insets are not equals", color.equals(old));
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        monthView.setMonthStringInsets(color);
        assertEquals(color, monthView.getMonthStringInsets());
        TestUtils.assertPropertyChangeEvent(report, "monthStringInsets", old, color);
    }

    /**
     * Issue #931-swingx: missing change notification.
     * test setting monthStringBackground property.
     * 
     */
    @Test
    public void testTodayBackground() {
        Color old = monthView.getTodayBackground();
        Color color = Color.PINK;
        if (color.equals(old)) {
            color = Color.MAGENTA;
        }
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        monthView.setTodayBackground(color);
        assertEquals(color, monthView.getTodayBackground());
        TestUtils.assertPropertyChangeEvent(report, "todayBackground", old, color);
    }

    
    /**
     * Issue #931-swingx: missing change notification.
     * test setting monthStringBackground property.
     * 
     */
    @Test
    public void testMonthStringBackground() {
        Color old = monthView.getMonthStringBackground();
        Color color = Color.PINK;
        if (color.equals(old)) {
            color = Color.MAGENTA;
        }
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        monthView.setMonthStringBackground(color);
        assertEquals(color, monthView.getMonthStringBackground());
        TestUtils.assertPropertyChangeEvent(report, "monthStringBackground", old, color);
    }

    /**
     * Issue #931-swingx: missing change notification.
     * test setting monthStringForeground property.
     * 
     */
    @Test
    public void testMonthStringForeground() {
        Color old = monthView.getMonthStringForeground();
        Color color = Color.PINK;
        if (color.equals(old)) {
            color = Color.MAGENTA;
        }
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        monthView.setMonthStringForeground(color);
        assertEquals(color, monthView.getMonthStringForeground());
        TestUtils.assertPropertyChangeEvent(report, "monthStringForeground", old, color);
    }
    /**
     * Issue #931-swingx: missing change notification.
     * test setting font property - was fired twice due to unneeded 
     * override of setFont (removed).
     * 
     */
    @Test
    public void testFont() {
        Font old = monthView.getFont();
        Font padding = old.deriveFont(old.getSize2D() * 2);
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        monthView.setFont(padding);
        assertEquals(padding, monthView.getFont());
        TestUtils.assertPropertyChangeEvent(report, "font", old, padding);
    }

    /**
     * Issue #931-swingx: missing change notification.
     * test setting boxPaddingY property.
     * 
     */
    @Test
    public void testBoxPaddingY() {
        int old = monthView.getBoxPaddingY();
        int padding = old + 2;
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        monthView.setBoxPaddingY(padding);
        assertEquals(padding, monthView.getBoxPaddingY());
        TestUtils.assertPropertyChangeEvent(report, "boxPaddingY", old, padding);
    }

    /**
     * Issue #931-swingx: missing change notification.
     * test setting boxPaddingX property.
     * 
     */
    @Test
    public void testBoxPaddingX() {
        int old = monthView.getBoxPaddingX();
        int padding = old + 2;
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        monthView.setBoxPaddingX(padding);
        assertEquals(padding, monthView.getBoxPaddingX());
        TestUtils.assertPropertyChangeEvent(report, "boxPaddingX", old, padding);
    }

    /**
     * Issue #931-swingx: missing change notification.
     * test setting selectionBackground property.
     * 
     */
    @Test
    public void testDaysOfTheWeekForeground() {
        Color old = monthView.getDaysOfTheWeekForeground();
        Color color = Color.PINK;
        if (color.equals(old)) {
            color = Color.MAGENTA;
        }
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        monthView.setDaysOfTheWeekForeground(color);
        assertEquals(color, monthView.getDaysOfTheWeekForeground());
        TestUtils.assertPropertyChangeEvent(report, "daysOfTheWeekForeground", old, color);
    }

    /**
     * Issue #931-swingx: missing change notification.
     * test setting selectionBackground property.
     * 
     */
    @Test
    public void testFlaggedDayForeground() {
        Color old = monthView.getFlaggedDayForeground();
        Color color = Color.PINK;
        if (color.equals(old)) {
            color = Color.MAGENTA;
        }
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        monthView.setFlaggedDayForeground(color);
        assertEquals(color, monthView.getFlaggedDayForeground());
        TestUtils.assertPropertyChangeEvent(report, "flaggedDayForeground", old, color);
    }

    /**
     * Issue #931-swingx: missing change notification.
     * test setting selectionBackground property.
     * 
     */
    @Test
    public void testSelectionForeground() {
        Color old = monthView.getSelectionForeground();
        Color color = Color.PINK;
        if (color.equals(old)) {
            color = Color.MAGENTA;
        }
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        monthView.setSelectionForeground(color);
        assertEquals(color, monthView.getSelectionForeground());
        TestUtils.assertPropertyChangeEvent(report, "selectionForeground", old, color);
    }

    /**
     * Issue #931-swingx: missing change notification.
     * test setting selectionBackground property.
     * 
     */
    @Test
    public void testSelectionBackground() {
        Color old = monthView.getSelectionBackground();
        Color color = Color.PINK;
        if (color.equals(old)) {
            color = Color.MAGENTA;
        }
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        monthView.setSelectionBackground(color);
        assertEquals(color, monthView.getSelectionBackground());
        TestUtils.assertPropertyChangeEvent(report, "selectionBackground", old, color);
    }
    
    @Test
    public void testInitialOpaque() {
        assertTrue(monthView.isOpaque());
    }
    
    @Test
    public void testInitialBoxPadding() {
        assertTrue(monthView.getBoxPaddingX() > 0);
        assertEquals(UIManager.getInt("JXMonthView.boxPaddingX"), monthView.getBoxPaddingX());
        assertTrue(monthView.getBoxPaddingY() > 0);
        assertEquals(UIManager.getInt("JXMonthView.boxPaddingY"), monthView.getBoxPaddingY());
        
    }
    
    
    /**
     * Issue #752-swingx: custom daysOfWeek lost in updateUI
     */
    @Test
    public void testDaysOfWeekUpdateUI() {
        JXMonthView monthView = new JXMonthView();
        String[] days = {"S", "M", "D", "M", "D", "F", "S"};
        monthView.setDaysOfTheWeek(days);
        assertEquals(Arrays.asList(days), Arrays.asList(monthView.getDaysOfTheWeek()));
        monthView.updateUI();
        assertEquals(Arrays.asList(days), Arrays.asList(monthView.getDaysOfTheWeek()));
    }
    
    /**
     * Issue #752-swingx: custom daysOfWeek lost in updateUI
     */
    @Test
    public void testDaysOfWeekUpdateUIAllowNull() {
        JXMonthView monthView = new JXMonthView();
        String[] days = null;
        monthView.setDaysOfTheWeek(days);
        assertNotNull("daysOfTheWeek must not be null", monthView.getDaysOfTheWeek());
       
   }

    /**
     * Issue #752-swingx: custom daysOfWeek lost in updateUI
     */
    @Test
    public void testDaysOfWeekInitial() {
        JXMonthView monthView = new JXMonthView();
        assertNotNull("daysOfTheWeek must not be null", monthView.getDaysOfTheWeek());
    }
    
    /**
     * Issue #752-swingx: custom daysOfWeek lost in updateUI
     */
    @Test
    public void testDaysOfWeekCopied() {
        JXMonthView monthView = new JXMonthView();
        assertNotSame(monthView.getDaysOfTheWeek(), monthView.getDaysOfTheWeek());
    }
    
    /**
     * Issue #752-swingx: custom daysOfWeek lost in updateUI
     */
    @Test
    public void testDaysOfWeekReset() {
        JXMonthView monthView = new JXMonthView();
        // start off with ui-provided dates
        String[] uiDays = monthView.getDaysOfTheWeek();
        String[] days = {"S", "M", "D", "M", "D", "F", "S"};
        // set the custom
        monthView.setDaysOfTheWeek(days);
        // sanity
        assertEquals(Arrays.asList(days), Arrays.asList(monthView.getDaysOfTheWeek()));
        monthView.setDaysOfTheWeek(null);
        assertEquals("use ui-provided daysOfWeek after reset", 
                Arrays.asList(uiDays), Arrays.asList(monthView.getDaysOfTheWeek()));
    }



    /**
     * Issue #751-swingx: property naming violations
     */
    @Test
    public void testShowingLeadingNotification() {
        JXMonthView monthView = new JXMonthView();
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        boolean showing = monthView.isShowingLeadingDays();
        monthView.setShowingLeadingDays(!showing);
        TestUtils.assertPropertyChangeEvent(report, "showingLeadingDays", showing, !showing);
    }

    /**
     * Issue #751-swingx: property naming violations
     */
    @Test
    public void testShowingLeadingNoNotification() {
        JXMonthView monthView = new JXMonthView();
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        boolean showing = monthView.isShowingLeadingDays();
        monthView.setShowingLeadingDays(showing);
        assertEquals(0, report.getEventCount("showingLeadingDates"));
    }



    /**
     * Issue #751-swingx: property naming violations
     */
    @Test
    public void testShowingTrailingNotification() {
        JXMonthView monthView = new JXMonthView();
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        boolean showing = monthView.isShowingTrailingDays();
        monthView.setShowingTrailingDays(!showing);
        TestUtils.assertPropertyChangeEvent(report, "showingTrailingDays", showing, !showing);
    }

    /**
     * Issue #751-swingx: property naming violations
     */
    @Test
    public void testShowingTrailingNoNotification() {
        JXMonthView monthView = new JXMonthView();
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        boolean showing = monthView.isShowingTrailingDays();
        monthView.setShowingTrailingDays(showing);
        assertEquals(0, report.getEventCount("showingTrailingDays"));
    }

    /**
     * Issue #751-swingx: property naming violations
     */
    @Test
    public void testShowingWeekNumbersNotification() {
        JXMonthView monthView = new JXMonthView();
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        boolean showing = monthView.isShowingWeekNumber();
        monthView.setShowingWeekNumber(!showing);
        TestUtils.assertPropertyChangeEvent(report, "showingWeekNumber", showing, !showing);
    }

    /**
     * Issue #751-swingx: property naming violations
     */
    @Test
    public void testShowingWeekNumbersNoNotification() {
        JXMonthView monthView = new JXMonthView();
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        boolean showing = monthView.isShowingWeekNumber();
        monthView.setShowingWeekNumber(showing);
        assertEquals(0, report.getEventCount("showingWeekNumber"));
    }

    /**
     * Issue #751-swingx: property naming violations
     */
    @Test
    public void testTraversableNotification() {
        JXMonthView monthView = new JXMonthView();
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        boolean showing = monthView.isTraversable();
        monthView.setTraversable(!showing);
        TestUtils.assertPropertyChangeEvent(report, "traversable", showing, !showing);
    }

    /**
     * Issue #751-swingx: property naming violations
     */
    @Test
    public void testTraversableNoNotification() {
        JXMonthView monthView = new JXMonthView();
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        boolean showing = monthView.isTraversable();
        monthView.setTraversable(showing);
        assertEquals(0, report.getEventCount("traversable"));
    }


    /**
     * Issue #733-swingx: model and monthView cal not synched.
     * 
     * Here: test that model settings are respected in constructor - minimaldays.
     */
    @Test
    public void testCalendarsContructorUnchangedFirstDayOfWeek() {
        DateSelectionModel model = new DaySelectionModel();
        int first = model.getFirstDayOfWeek() + 1;
        model.setFirstDayOfWeek(first);
        JXMonthView monthView = new JXMonthView(new Date(), model);
        assertEquals("model's calendar properties must be unchanged: minimalDays", 
                first, model.getFirstDayOfWeek());
        // sanity: taken in monthView
        assertEquals("monthView's calendar properties must be synched", 
                first, monthView.getFirstDayOfWeek());
    }
    /**
     * Issue #733-swingx: model and monthView cal not synched.
     * 
     * Here: test that monthView is updated to model after setSelectionModel.
     */
    @Test
    public void testCalendarsSetModel() {
        JXMonthView monthView = new JXMonthView();
        int firstDayOfWeek = monthView.getFirstDayOfWeek();
        Locale locale = Locale.UK;
        if (locale.equals(monthView.getLocale())) {
            locale = Locale.FRENCH;
        }
        TimeZone tz = TimeZone.getTimeZone("GMT+4");
        if (monthView.getTimeZone().equals(tz)) {
            tz = TimeZone.getTimeZone("GMT+5");
        }
        DateSelectionModel model = new DaySelectionModel(locale);
        model.setTimeZone(tz);
        int modelMinimal = model.getMinimalDaysInFirstWeek();
        monthView.setSelectionModel(model);
        assertEquals("timeZone must be updated from model", tz, monthView.getTimeZone());
        // Issue 1143
        assertTrue("firstDisplayedDay must be updated to start of day", CalendarUtils.isStartOfDay(monthView.getCalendar()));
        assertEquals("Locale must be updated from model", locale, monthView.getLocale());
        // be aware if it makes no sense to assert
        if (firstDayOfWeek != model.getFirstDayOfWeek()) {
            assertEquals("firstDayOfWeek must be updated from model", 
                    model.getFirstDayOfWeek(), monthView.getFirstDayOfWeek());
        } else {
            LOG.info("cannot assert firstDayOfWeek - was same");
        }
        // @KEEP - this is an open issue: monthView must not change the
        // model settings but minimalDaysInFirstWeek > 1 confuse the 
        // BasicMonthViewUI - remove if passing in xIssues
        assertEquals("model minimals must not be changed", 
                modelMinimal, model.getMinimalDaysInFirstWeek());
    }

    /**
     * Issue #736-swingx: model and monthView cal not synched.
     * 
     * Here: test that model settings are respected in constructor - minimaldays.
     */
    @SuppressWarnings("unused")
    @Test
    public void testCalendarsContructorUnchangedMinimalDaysOfModel() {
        DateSelectionModel model = new DaySelectionModel();
        int first = model.getMinimalDaysInFirstWeek() + 1;
        model.setMinimalDaysInFirstWeek(first);
        JXMonthView monthView = new JXMonthView(new Date(), model);
        assertEquals("model's calendar properties must be unchanged: minimalDays", 
                first, model.getMinimalDaysInFirstWeek());
    }

    /**
     * Issue #736-swingx: model and monthView cal not synched.
     * 
     * Here: test that model settings are respected in setModel - minimaldays.
     * 
     * Model must not reset minimalDaysInfirstWeek, but Locales with values
     * > 1 confuse the BasicDatePickerUI - need to track down and solve there.
     */
    @Test
    public void testCalendarsSetModelUnchangedMinimalDaysInFirstWeek() {
        JXMonthView monthView = new JXMonthView();
        DateSelectionModel model = new DaySelectionModel();
        int first = model.getMinimalDaysInFirstWeek() + 1;
        model.setMinimalDaysInFirstWeek(first);
        monthView.setSelectionModel(model);
        assertEquals("model minimals must not be changed", 
                first, model.getMinimalDaysInFirstWeek());
    }
    

    /**
     * Issue #733-swingx: model and monthView cal not synched.
     * 
     * Here: minimal days of first week.
     */
    @Test
    public void testCalendarsMinimalDaysOfFirstWeekModelChanged() {
        JXMonthView monthView = new JXMonthView();
        int first = monthView.getCalendar().getMinimalDaysInFirstWeek() + 1;
        assertTrue(first <= Calendar.SATURDAY);
        monthView.getSelectionModel().setMinimalDaysInFirstWeek(first);
        assertEquals(first, monthView.getCalendar().getMinimalDaysInFirstWeek());
    }
    

    /**
     * Issue #733-swingx: TimeZone in model and monthView not synched.
     *  
     *  Test that the selected is normalized in the monthView's timezone. 
     */
    @Test
    public void testCalendarsTimeZoneNormalizedDate() {
        JXMonthView monthView = new JXMonthView();
        // config with a known timezone
        TimeZone tz = TimeZone.getTimeZone("GMT+4");
        if (tz.equals(monthView.getTimeZone())) {
            tz = TimeZone.getTimeZone("GMT+5");
        }
        monthView.setTimeZone(tz);
        monthView.setSelectionDate(new Date());
        Date selected = monthView.getSelectionDate();
        Calendar calendar = monthView.getCalendar();
        assertEquals(selected, CalendarUtils.startOfDay(calendar, selected));
    }
    
    /**
     * Issue #733-swingx: model and monthView cal not synched.
     * 
     * Here: timezone of flagged dates not synched.
     * This was introduced by moving the control of flagged dates into
     * a internal model. Need to synch that model as well.
     */
    @Test
    public void testFlaggedDatesTimeZone() {
        JXMonthView monthView = new JXMonthView();
        // config with a known timezone and date
        TimeZone tz = TimeZone.getTimeZone("GMT+4");
        if (tz.equals(monthView.getTimeZone())) {
            tz = TimeZone.getTimeZone("GMT+5");
        }
        monthView.setTimeZone(tz);
        monthView.setFlaggedDates(today);
        Date flagged = monthView.getFlaggedDates().first();
        assertEquals(flagged, CalendarUtils.startOfDay(monthView.getCalendar(), flagged));
    }
    
    /**
     * Issue #733-swingx: model and monthView cal not synched.
     * 
     * Here: setting the timezone clears the flagged dates, must notify of change.
      */
    @Test
    public void testFlaggedDatesTimeZoneNotifyOnChange() {
        JXMonthView monthView = new JXMonthView();
        monthView.setFlaggedDates(today);
        SortedSet<Date> flagged = monthView.getFlaggedDates();
        // config with a known timezone and date
        TimeZone tz = TimeZone.getTimeZone("GMT+4");
        if (tz.equals(monthView.getTimeZone())) {
            tz = TimeZone.getTimeZone("GMT+5");
        }
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        monthView.setTimeZone(tz);
        TestUtils.assertPropertyChangeEvent(report, "flaggedDates", 
                flagged, monthView.getFlaggedDates(), false);
    }
    
    /**
     * Issue #733-swingx: model and monthView cal not synched.
     * 
     * Here: setting the timezone clears the flagged dates, must notify of change.
      */
    @Test
    public void testFlaggedDatesTimeZoneNotNotifyWithoutChange() {
        JXMonthView monthView = new JXMonthView();
        // config with a known timezone and date
        TimeZone tz = TimeZone.getTimeZone("GMT+4");
        if (tz.equals(monthView.getTimeZone())) {
            tz = TimeZone.getTimeZone("GMT+5");
        }
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        monthView.setTimeZone(tz);
        assertEquals("no change in flaggedDates must not fire", 0, report.getEventCount("flaggedDates"));
    }
    
    /**
     * Issue #733-swingx: model and monthView cal not synched.
     * 
     * Here: Locale changed in monthView.
     */
    @Test
    public void testCalendarsLocaleChangedMonthView() {
        JXMonthView monthView = new JXMonthView();
        Locale locale = Locale.UK;
        if (locale.equals(monthView.getLocale())) {
            locale = Locale.FRENCH;
        }
        monthView.setLocale(locale);
        assertEquals("locale set in monthView must be passed to model", 
                locale, monthView.getSelectionModel().getLocale());
    }
    
    /**
     * Issue #733-swingx: model and monthView cal not synched.
     * 
     * Here: Locale changed in selection model.
     */
    @Test
    public void testCalendarsLocaleChangedModel() {
        JXMonthView monthView = new JXMonthView();
        Locale locale = Locale.UK;
        if (locale.equals(monthView.getLocale())) {
            locale = Locale.FRENCH;
        }
        monthView.getSelectionModel().setLocale(locale);
        assertEquals("locale set in model must be passed to monthView", 
                locale, monthView.getLocale());
    }
    
    

    /**
     * Issue #733-swingx: model and monthView cal not synched.
     * 
     * Here: Locale changed in monthView.
     */
    @Test
    public void testCalendarsLocaleContructor() {
        Locale locale = Locale.UK;
        if (locale.equals(JComponent.getDefaultLocale())) {
            locale = Locale.FRENCH;
        }
        JXMonthView monthView = new JXMonthView(locale);
        assertEquals("initial locale in constructor must be passed to model", 
                locale, monthView.getSelectionModel().getLocale());
    }

    /**
     * Issue #733-swingx: model and monthView cal not synched.
     * 
     * Here: set first day of week in monthView.
     */
    @Test
    public void testCalendarsFirstDayOfWeekMonthView() {
        JXMonthView monthView = new JXMonthView();
        int first = monthView.getFirstDayOfWeek() + 1;
        // sanity
        assertTrue(first <= Calendar.SATURDAY);
        monthView.setFirstDayOfWeek(first);
        assertEquals(first, monthView.getFirstDayOfWeek());
        assertEquals(first, monthView.getCalendar().getFirstDayOfWeek());
        assertEquals(first, monthView.getSelectionModel().getFirstDayOfWeek());
    }
    
    /**
     * Issue #733-swingx: model and monthView cal not synched.
     * 
     * Here: set first day of week in model.
     */
    @Test
    public void testCalendarsFirstDayOfWeekModel() {
        JXMonthView monthView = new JXMonthView();
        int first = monthView.getFirstDayOfWeek() + 1;
        // sanity
        assertTrue(first <= Calendar.SATURDAY);
        monthView.getSelectionModel().setFirstDayOfWeek(first);
        assertEquals(first, monthView.getFirstDayOfWeek());
        assertEquals(first, monthView.getCalendar().getFirstDayOfWeek());
        assertEquals(first, monthView.getSelectionModel().getFirstDayOfWeek());
    }
    /**
     * Issue #733-swingx: model and monthView cal not synched.
     * 
     * Here: first day of week.
     */
    @Test
    public void testCalendarsFirstDayOfWeekInitial() {
        JXMonthView monthView = new JXMonthView();
        assertEquals(monthView.getFirstDayOfWeek(), 
                monthView.getSelectionModel().getFirstDayOfWeek());
    }
    /**
     * Issue #733-swingx: model and monthView cal not synched.
     * 
     * Here: minimal days of first week.
     */
    @Test
    public void testCalendarsMinimalDaysOfFirstWeekInitial() {
        JXMonthView monthView = new JXMonthView();
        int first = monthView.getCalendar().getMinimalDaysInFirstWeek();
        assertEquals(first, monthView.getSelectionModel().getMinimalDaysInFirstWeek());
    }
    
    
    /**
     * Issue ??-swingx: selection related properties must be independent 
     * of way-of setting.
     * 
     * View must delegate to model, so asking view or model with same 
     * parameters must return the same result.
     * 
     * Here: isSelected
     * 
     */
    @Test
    public void testMonthViewSameAsSelectionModelIsSelected() {
        JXMonthView monthView = new JXMonthView();
        // guard against accidental startofday
        calendar.set(Calendar.HOUR_OF_DAY, 5);
        Date date = calendar.getTime();
        monthView.setSelectionDate(date);
        assertTrue(monthView.isSelected(date));
        assertTrue(monthView.getSelectionModel().isSelected(date));
    }
    
    /**
     * Issue ??-swingx: selection related properties must be independent 
     * of way-of setting.
     * 
     * View must delegate to model, so asking view or model with same 
     * parameters must return the same result.
     * 
     * Here: isSelected
     * 
     */
    @Test
    public void testMonthViewSameAsSelectionModelSelectedDate() {
        JXMonthView monthView = new JXMonthView();
        // guard against accidental startofday
        calendar.set(Calendar.HOUR_OF_DAY, 5);
        Date date = calendar.getTime();
        monthView.setSelectionDate(date);
        assertEquals(monthView.getSelectionDate(), 
                monthView.getSelectionModel().getFirstSelectionDate());
    }

    /**
     * Issue ??-swingx: selection related properties must be independent 
     * of way-of setting.
     * 
     * View must delegate to model, so asking view or model with same 
     * parameters must return the same result.
     * 
     * Here: isSelected
     * 
     */
    @Test
    public void testMonthViewSameAsSelectionModelIsUnselectable() {
        JXMonthView monthView = new JXMonthView();
        // guard against accidental startofday
        calendar.set(Calendar.HOUR_OF_DAY, 5);
        Date date = calendar.getTime();
        monthView.setUnselectableDates(date);
        assertTrue(monthView.isUnselectableDate(date));
        assertTrue(monthView.getSelectionModel().isUnselectableDate(date));
    }

    /**
     * Issue ??-swingx: selection related properties must be independent 
     * of way-of setting.
     * 
     * View must delegate to model, so asking view or model with same 
     * parameters must return the same result.
     * 
     * Here: set unselectables on model
     * 
     */
    @Test
    public void testSelectionModelSameAsMonthViewIsUnselectableDate() {
        JXMonthView monthView = new JXMonthView();
        // guard against accidental startofday
        calendar.set(Calendar.HOUR_OF_DAY, 5);
        Date date = calendar.getTime();
        SortedSet<Date> unselectables = new TreeSet<Date>();
        unselectables.add(date);
        monthView.getSelectionModel().setUnselectableDates(unselectables);
        assertTrue(monthView.getSelectionModel().isUnselectableDate(date));
        assertTrue(monthView.isUnselectableDate(date));
    }
    
    /**
     * Issue ??-swingx: selection related properties must be independent 
     * of way-of setting.
     * 
     * View must delegate to model, so asking view or model with same 
     * parameters must return the same result.
     * 
     * Here: set selected on model
     * 
     */
    @Test
    public void testSelectionModelSameAsMonthViewIsSelected() {
        JXMonthView monthView = new JXMonthView();
        // guard against accidental startofday
        calendar.set(Calendar.HOUR_OF_DAY, 5);
        Date date = calendar.getTime();
        monthView.getSelectionModel().setSelectionInterval(date, date);
        assertTrue(monthView.getSelectionModel().isSelected(date));
        assertTrue(monthView.isSelected(date));
    }
    
    /**
     * Issue ??-swingx: selection related properties must be independent 
     * of way-of setting.
     * 
     * View must delegate to model, so asking view or model with same 
     * parameters must return the same result.
     * 
     * Here: set selected on model, ask for selected date
     * 
     */
    @Test
    public void testSelectionModelSameAsMonthViewSelectedDate() {
        JXMonthView monthView = new JXMonthView();
        // guard against accidental startofday
        calendar.set(Calendar.HOUR_OF_DAY, 5);
        Date date = calendar.getTime();
        monthView.getSelectionModel().setSelectionInterval(date, date);
        assertEquals(monthView.getSelectionModel().getFirstSelectionDate(), 
                monthView.getSelectionDate());
    }
    

    /**
     * #703-swingx: set date to first of next doesn't update the view.
     * 
     * Behaviour is consistent with core components, must not update
     * 
     */
    @Test
    public void testAutoScrollOnSelection() {
        JXMonthView us = new JXMonthView();
        final Calendar today = Calendar.getInstance();
        CalendarUtils.endOfMonth(today);
        us.setSelectionDate(today.getTime());
        Date first = us.getFirstDisplayedDay();
        today.add(Calendar.DAY_OF_MONTH, 2);
        us.setSelectionDate(today.getTime());
        assertEquals(first, us.getFirstDisplayedDay());
    }

    /**
     * #705-swingx: revalidate must not reset first firstDisplayed.
     * 
     * 
     */
    @Test
    public void testAutoScrollOnSelectionRevalidate() throws InterruptedException, InvocationTargetException {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run test - headless environment");
            return;
        }
        final JXMonthView us = new JXMonthView();
        final Calendar today = Calendar.getInstance();
        CalendarUtils.endOfMonth(today);
        us.setSelectionDate(today.getTime());
        final JXFrame frame = new JXFrame();
        frame.add(us);
        final Date first = us.getFirstDisplayedDay();
        today.add(Calendar.DAY_OF_MONTH, 2);
        us.setSelectionDate(today.getTime());
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                us.revalidate();
                // need to validate frame - why?
                frame.validate();
                assertEquals("firstDisplayed must not be changed on revalidate", 
                        first, us.getFirstDisplayedDay());
//                assertEquals(first, us.getFirstDisplayedDate());
//                fail("weird (threading issue?): the firstDisplayed is changed in layoutContainer - not testable here");
            }
        });
    }
    
    /**
     * Issue 711-swingx: today is notify-only property.
     * Today is start of day.
     */
    @Test
    public void testTodayInitial() {
        JXMonthView monthView = new JXMonthView();
        CalendarUtils.startOfDay(calendar);
        assertEquals(calendar.getTime(), monthView.getToday());
    }
    
    /**
     * Issue 711-swingx: today is notify-only property.
     * Increment sets to start of day of tomorrow.
     */
    @Test
    public void testTodayIncrement() {
        JXMonthView monthView = new JXMonthView();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        CalendarUtils.startOfDay(calendar);
        monthView.incrementToday();
        assertEquals(calendar.getTime(), monthView.getToday());
    }
    
    
    /**
     * Issue 711-swingx: today is notify-only property.
     * SetToday should 
     */
    @Test
    public void testTodaySetNotification() {
        JXMonthView monthView = new JXMonthView();
        Date today = monthView.getToday();
        // tomorrow
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        monthView.setToday(calendar.getTime());
        CalendarUtils.startOfDay(calendar);
        TestUtils.assertPropertyChangeEvent(report, "today", 
                today, calendar.getTime());
    }

    
    /**
     * Issue 711-swingx: today is notify-only property.
     * SetToday should 
     */
    @Test
    public void testTodaySet() {
        JXMonthView monthView = new JXMonthView();
        // tomorrow
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        monthView.setToday(calendar.getTime());
        CalendarUtils.startOfDay(calendar);
        assertEquals(calendar.getTime(), monthView.getToday());
    }

    /**
     * For safety, getToday should return a clone.
     */
    @Test
    public void testTodayCopy() {
        JXMonthView monthView = new JXMonthView();
        Date today = monthView.getToday();
        Date other = monthView.getToday();
        assertNotNull(today);
        assertNotSame(today, other);
    }
    
    /**
     * Issue #708-swingx: updateUI changes state.
     * 
     * Here: test that today is unchanged.
     */
    @Test
    public void testUpdateUIToday() {
        JXMonthView monthView = new JXMonthView();
        Date first = monthView.getToday();
        monthView.updateUI();
        assertEquals(first, monthView.getToday());
    };

    
    /**
     * Issue #711-swingx: remove fake property change notification.
     * 
     * Here: test that ensureVisibleDate fires once only.
     */
    @Test
    public void testEnsureVisibleDateNofication() {
        JXMonthView monthView = new JXMonthView();
        Date firstDisplayedDate = monthView.getFirstDisplayedDay();
        // previous month
        calendar.add(Calendar.MONTH, -1);
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        monthView.ensureDateVisible(calendar.getTime());
        CalendarUtils.startOfMonth(calendar);
        TestUtils.assertPropertyChangeEvent(report, "firstDisplayedDay", 
                firstDisplayedDate, calendar.getTime(), false);
    }

    
    /**
     * Issue #711-swingx: remove fake property change notification.
     * 
     * Here: test that setFirstDisplayedDate fires once only.
     */
    @Test
    public void testFirstDisplayedDateNofication() {
        JXMonthView monthView = new JXMonthView();
        Date firstDisplayedDate = monthView.getFirstDisplayedDay();
        // previous month
        calendar.add(Calendar.MONTH, -1);
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        monthView.setFirstDisplayedDay(calendar.getTime());
        CalendarUtils.startOfMonth(calendar);
        TestUtils.assertPropertyChangeEvent(report, "firstDisplayedDay", 
                firstDisplayedDate, calendar.getTime(), false);
    }
    
    /**
     * Issue #708-swingx
     * 
     * test update of lastDisplayedDate if resized.
     */
    @Test
    public void testLastDisplayedOnResize() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run test - headless environment");
            return;
        }
        // get a reference width so we can simulate a one-month resize
        JXMonthView compare = new JXMonthView();
        compare.setPreferredColumnCount(2);
        JXMonthView monthView = new JXMonthView();
        JXFrame frame = new JXFrame();
        frame.add(monthView);
        frame.pack();
        Date last = monthView.getLastDisplayedDay();
        // set a size that should guarantee the same number of columns as the compare monthView
        frame.setSize(compare.getPreferredSize().width + 50, monthView.getPreferredSize().height + 50);
        frame.validate();
        // build a date corresponding to the expected end of next month
        calendar.setTime(last);
        // next month
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        CalendarUtils.endOfMonth(calendar);
        assertEquals(calendar.getTime(), monthView.getLastDisplayedDay());
    }

    
    /**
     * Issue #708-swingx: updateUI changes state.
     * 
     * Here: test that firstDisplayedDate is unchanged.
     */
    @Test
    public void testUpdateUIFirst() {
        final JXMonthView monthView = new JXMonthView();
        Date first = monthView.getFirstDisplayedDay();
        monthView.updateUI();
        assertEquals(first, monthView.getFirstDisplayedDay());
    };


    /**
     * Issue #708-swingx: updateUI changes state.
     * 
     * Here: test that lastDisplayedDate is unchanged.
     */
    @Test
    public void testUpdateUILast() {
        final JXMonthView monthView = new JXMonthView();
        Date first = monthView.getLastDisplayedDay();
        monthView.updateUI();
        assertEquals(first, monthView.getLastDisplayedDay());
    };

    /**
     * Issue #659-swingx: lastDisplayedDate must be synched.
     * test that lastDisplayed from monthView is same as lastDisplayed from ui.
     * 
     * Here: initial packed size - one month shown.
     * 
     * @throws InvocationTargetException 
     * @throws InterruptedException 
     */
    @Test
    public void testLastDisplayedDateUIInitial() throws InterruptedException, InvocationTargetException {
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run lastDisplayedDate - headless");
            return;
        }
        final JXMonthView monthView = new JXMonthView();
        final JXFrame frame = wrapInFrame(monthView, "");
        frame.setVisible(true);
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                Date uiLast = monthView.getUI().getLastDisplayedDay();
                Date viewLast = monthView.getLastDisplayedDay();
                assertEquals(uiLast, viewLast);
            }
        });
    }
    
    /**
     * 
     * Issue #659-swingx: lastDisplayedDate must be synched.
     * 
     * test that lastDisplayed from monthView is same as lastDisplayed from ui.
     * 
     * Here: change the size of the view which allows the ui to display more
     * columns/rows.
     * 
     * @throws InvocationTargetException 
     * @throws InterruptedException 
     */
    @Test
    public void testLastDisplayedDateSizeChanged() throws InterruptedException, InvocationTargetException {
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run lastDisplayedDate - headless");
            return;
        }
        final JXMonthView monthView = new JXMonthView();
        final JXFrame frame = wrapInFrame(monthView, "");
        frame.setVisible(true);
        frame.setSize(frame.getWidth() * 3, frame.getHeight() * 2);
        // force a revalidate
        frame.invalidate();
        frame.validate();
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                Date uiLast = monthView.getUI().getLastDisplayedDay();
                Date viewLast = monthView.getLastDisplayedDay();
                assertEquals(uiLast, viewLast);
            }
        });
    }
    

    /**
     * 
     * Issue #659-swingx: lastDisplayedDate must be synched.
     * 
     * test that ensureDateVisible works as doc'ed if multiple months shown: 
     * if the new date is in the
     * month following the last visible then the first must be set in a manner that
     * the date must be visible in the last month. 
     * 
     * @throws InvocationTargetException 
     * @throws InterruptedException 
     */
    @Test
    public void testLastDisplayedDateSizeChangedEnsureVisible() throws InterruptedException, InvocationTargetException {
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run lastDisplayedDate - headless");
            return;
        }
        final JXMonthView monthView = new JXMonthView();
        final JXFrame frame = wrapInFrame(monthView, "");
        frame.setVisible(true);
        frame.setSize(frame.getWidth() * 3, frame.getHeight() * 2);
        // force a revalidate
        frame.invalidate();
        frame.validate();
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(monthView.getFirstDisplayedDay());
                int firstMonth = calendar.get(Calendar.MONTH);
                Date uiLast = monthView.getUI().getLastDisplayedDay();
                calendar.setTime(uiLast);
                int lastMonth = calendar.get(Calendar.MONTH);
                // sanity: more than one month shown
                assertFalse(firstMonth == lastMonth);
                // first day of next month 
                calendar.add(Calendar.DATE, 1);
                // sanity
                int newLastMonth = calendar.get(Calendar.MONTH);
                assertFalse(lastMonth == newLastMonth);
                monthView.ensureDateVisible(calendar.getTime());
                CalendarUtils.endOfMonth(calendar);
                Date newUILast = monthView.getUI().getLastDisplayedDay();
                assertEquals(newUILast, monthView.getLastDisplayedDay());
                calendar.setTime(newUILast);
//                LOG.info("first/last: " + new Date(monthView.getFirstDisplayedDate()) + 
//                        "/" + new Date(newUILast));
                assertEquals(newLastMonth, calendar.get(Calendar.MONTH));
            }
        });
    }
    


    /**
     * Issue #660-swingx: JXMonthView must protect its calendar.
     * Client manipulation on calendar must not change internal state.
     * 
     * This is guaranteed by returning a clone instead of the life object.
     */
    @Test
    public void testMonthViewCalendarInvariant() {
        JXMonthView monthView = new JXMonthView();
        TimeZone tz = monthView.getTimeZone();
        Calendar calendar = monthView.getCalendar();
        calendar.setTimeZone(getTimeZone(tz, CalendarUtils.THREE_HOURS));
        assertEquals("monthView must protect its calendar", tz, monthView.getTimeZone());
    }

    /**
     * Issue #660-swingx: JXMonthView must protect its calendar.
     * 
     * Added invariant to the monthView's getCalender: clone and
     * config to firstDisplayDate.
     * 
     * The various tests are various contexts which broke the 
     * expectation before fixing the issue. 
     * Here the context is: select.
     */
    @Test
   public void testMonthViewCalendarInvariantOnSetSelection() {
      JXMonthView monthView = new JXMonthView();
      assertEquals(1, monthView.getCalendar().get(Calendar.DATE));
      Date first = monthView.getFirstDisplayedDay();
      assertEquals("monthViews calendar represents the first day of the month", 
              first, monthView.getCalendar().getTime());
      Calendar cal = Calendar.getInstance();
      // add one day, now we are on the second
      cal.setTime(first);
      cal.add(Calendar.DATE, 1);
      Date date = cal.getTime();
      monthView.addSelectionInterval(date , date);
      assertEquals("selection must not change the calendar", 
              first, monthView.getCalendar().getTime());
   }

   /**
    * Issue #660-swingx: JXMonthView must protect its calendar.
    * 
    * Added invariant to the monthView's getCalender: clone and
    * config to firstDisplayDate.
    * 
    * The various tests are various contexts which broke the 
    * expectation before fixing the issue. 
    * Here the context is: check for selection.
    */
    @Test
   public void testMonthViewCalendarInvariantOnQuerySelectioon() {
      JXMonthView monthView = new JXMonthView();
      assertEquals(1, monthView.getCalendar().get(Calendar.DATE));
      Date first = monthView.getFirstDisplayedDay();
      assertEquals("monthViews calendar represents the first day of the month", 
              first, monthView.getCalendar().getTime());
      Calendar cal = Calendar.getInstance();
      // add one day, now we are on the second
      cal.setTime(first);
      cal.add(Calendar.DATE, 1);
      Date date = cal.getTime();
      monthView.isSelected(date);
      assertEquals("query selection must not change the calendar", 
              first, monthView.getCalendar().getTime());
   }


    /**
     * Issue #660-swingx: JXMonthView must protect its calendar.
     * 
     * Added invariant to the monthView's getCalender: clone and
     * config to firstDisplayDate.
     * 
     * The various tests are various contexts which broke the 
     * expectation before fixing the issue. 
     * Here the context is: set first displayed date (formerly left
     * the calendar at the last displayed date).
     */
    @Test
    public void testMonthViewCalendarInvariantOnSetFirstDisplayedDate() {
      JXMonthView monthView = new JXMonthView();
      Date first = monthView.getFirstDisplayedDay();
      Calendar cal = Calendar.getInstance();
      // add one day, now we are on the second
      cal.setTime(first);
      cal.add(Calendar.MONTH, 1);
      Date next = cal.getTime();
      monthView.setFirstDisplayedDay(next);
      assertEquals("monthViews calendar represents the first day of the month", 
              next, monthView.getCalendar().getTime());
    }
    
    /**
     * safety net: add api ensureDateVisible with Date parameter
     */
    @Test
    public void testEnsureDateVisibleDateParamNextYear() {
        JXMonthView monthView = new JXMonthView();
        Calendar temp = (Calendar) calendar.clone();
        CalendarUtils.startOfMonth(temp);
        // sanity
        assertEquals("sanity...", temp.getTime(), monthView.getFirstDisplayedDay());
        calendar.add(Calendar.YEAR, 1);
        Date nextYear = calendar.getTime();
        temp = (Calendar) calendar.clone();
        CalendarUtils.startOfMonth(temp);
        monthView.ensureDateVisible(nextYear);
        assertEquals("must be scrolled to next year", 
                temp.getTime(), monthView.getFirstDisplayedDay());
    }
    
    /**
     * safety net: add api ensureDateVisible with Date parameter
     */
    @Test
    public void testEnsureDateVisibleDateParamNextMonth() {
        JXMonthView monthView = new JXMonthView();
        Calendar temp = (Calendar) calendar.clone();
        CalendarUtils.startOfMonth(temp);
        assertEquals("sanity..", temp.getTime(), monthView.getFirstDisplayedDay());
        calendar.add(Calendar.MONTH, 1);
        Date nextMonth = calendar.getTime();
        temp = (Calendar) calendar.clone();
        CalendarUtils.startOfMonth(temp);
        monthView.ensureDateVisible(nextMonth);
        assertEquals("must be scrolled to next month", 
                temp.getTime(), monthView.getFirstDisplayedDay());
    }

    /**
     * safety net: add api ensureDateVisible with Date parameter
     */
    @Test
    public void testEnsureDateVisibleDateParamThisMonth() {
        JXMonthView monthView = new JXMonthView();
        Calendar temp = (Calendar) calendar.clone();
        CalendarUtils.startOfMonth(temp);
        Date first = monthView.getFirstDisplayedDay();
        assertEquals("sanity...", temp.getTime(), first);
        CalendarUtils.endOfMonth(calendar);
        Date thisMonth = calendar.getTime();
        monthView.ensureDateVisible(thisMonth);
        assertEquals("same month, nothing changed", 
                first, monthView.getFirstDisplayedDay());
    }


    /**
     * safety net: refactor ensureDateVisible
     */
    @Test
    public void testEnsureDateVisibleNextYear() {
        JXMonthView monthView = new JXMonthView();
        Calendar temp = (Calendar) calendar.clone();
        CalendarUtils.startOfMonth(temp);
        assertEquals("sanity...", temp.getTime(), monthView.getFirstDisplayedDay());
        calendar.add(Calendar.YEAR, 1);
        Date nextYear = calendar.getTime();
        temp = (Calendar) calendar.clone();
        CalendarUtils.startOfMonth(temp);
        monthView.ensureDateVisible(nextYear);
        assertEquals("must be scrolled to next year", temp.getTime(), monthView.getFirstDisplayedDay());
    }
    
    /**
     * safety net: refactor ensureDateVisible
     */
    @Test
    public void testEnsureDateVisibleNextMonth() {
        JXMonthView monthView = new JXMonthView();
        Calendar temp = (Calendar) calendar.clone();
        CalendarUtils.startOfMonth(temp);
        assertEquals("sanity ...", temp.getTime(), monthView.getFirstDisplayedDay());
        calendar.add(Calendar.MONTH, 1);
        Date nextMonth = calendar.getTime();
        temp = (Calendar) calendar.clone();
        CalendarUtils.startOfMonth(temp);
        monthView.ensureDateVisible(nextMonth);
        assertEquals("must be scrolled to next month", 
                temp.getTime(), monthView.getFirstDisplayedDay());
    }

    /**
     * safety net: refactor ensureDateVisible
     */
    @Test
    public void testEnsureDateVisibleThisMonth() {
        JXMonthView monthView = new JXMonthView();
        Calendar temp = (Calendar) calendar.clone();
        CalendarUtils.startOfMonth(temp);
        Date first = monthView.getFirstDisplayedDay();
        assertEquals("sanity ...", temp.getTime(), first);
        CalendarUtils.endOfMonth(calendar);
        Date thisMonth = calendar.getTime();
        monthView.ensureDateVisible(thisMonth);
        assertEquals("same month, nothing changed", first, monthView.getFirstDisplayedDay());
    }

    /**
     * safety net: move responsibility for lastDisplayedDate completely into ui.
     */
    @Test
    public void testLastDisplayedDateInitial() {
        JXMonthView monthView = new JXMonthView();
        calendar.setTime(monthView.getFirstDisplayedDay());
        CalendarUtils.endOfMonth(calendar);
        assertEquals(calendar.getTime(), monthView.getLastDisplayedDay());
    }
    
    /**
     * Issue #618-swingx: JXMonthView displays problems with non-default
     * timezones.
     * 
     * Selected dates are "start of day" in the timezone they had been 
     * selected. As such they make no sense in a new timezone: must
     * either be adjusted or cleared. Currently we clear the selection. 
     */
    @Test
    public void testTimeZoneChangeClearSelection() {
        JXMonthView monthView = new JXMonthView();
        Date date = new Date();
        monthView.setSelectionDate(date);
        // sanity
        assertTrue(monthView.isSelected(date));
        monthView.setTimeZone(getTimeZone(monthView.getTimeZone(), CalendarUtils.THREE_HOURS));
        // accidentally passes - because it is meaningful only in the timezone 
        // it was set ...
        assertFalse(monthView.isSelected(date));
        assertTrue("selection must have been cleared", monthView.isSelectionEmpty());
    }
    
    /**
     * Issue #618-swingx: JXMonthView displays problems with non-default
     * timezones.
     * 
     * Bound dates are "start of day" in the timezone they had been 
     * set. As such they make no sense in a new timezone: must
     * either be adjusted or cleared. Currently we clear the bound. 
     */
    @Test
    public void testTimeZoneChangeResetLowerBound() {
        JXMonthView monthView = new JXMonthView();
        monthView.setLowerBound(yesterday);
        monthView.setTimeZone(getTimeZone(monthView.getTimeZone(), CalendarUtils.THREE_HOURS));
        assertEquals("lowerBound must have been reset", null, monthView.getLowerBound());
    }
    
    /**
     * Issue #618-swingx: JXMonthView displays problems with non-default
     * timezones.
     * 
     * Bound dates are "start of day" in the timezone they had been 
     * set. As such they make no sense in a new timezone: must
     * either be adjusted or cleared. Currently we clear the bound. 
     */
    @Test
    public void testTimeZoneChangeResetUpperBound() {
        JXMonthView monthView = new JXMonthView();
        monthView.setUpperBound(yesterday);
        monthView.setTimeZone(getTimeZone(monthView.getTimeZone(), CalendarUtils.THREE_HOURS));
        assertEquals("upperbound must have been reset", null, monthView.getUpperBound());
    }
    
    /**
     * Issue #618-swingx: JXMonthView displays problems with non-default
     * timezones.
     * 
     * Flagged dates are "start of day" in the timezone they had been 
     * set. As such they make no sense in a new timezone: must
     * either be adjusted or cleared. Currently we clear them. 
     */
    @Test
    public void testTimeZoneChangeResetFlaggedDates() {
        JXMonthView monthView = new JXMonthView();
        monthView.setFlaggedDates(new Date[] {yesterday});
        monthView.setTimeZone(getTimeZone(monthView.getTimeZone(), CalendarUtils.THREE_HOURS));
        // accidentally passes - because it is meaningful only in the timezone 
        // it was set ...
        assertFalse(monthView.isFlaggedDate(yesterday));
        // missing api
        // assertEquals(0, monthView.getFlaggedDates().size());
        assertFalse("flagged dates must have been cleared", monthView.hasFlaggedDates());
    }
    
    /**
     * Issue #618-swingx: JXMonthView displays problems with non-default
     * timezones.
     * 
     * Unselectable dates are "start of day" in the timezone they had been 
     * set. As such they make no sense in a new timezone: must
     * either be adjusted or cleared. Currently we clear them. 
     */
    @Test
    public void testTimeZoneChangeResetUnselectableDates() {
        JXMonthView monthView = new JXMonthView();
        monthView.setUnselectableDates(yesterday);
        monthView.setTimeZone(getTimeZone(monthView.getTimeZone(), CalendarUtils.THREE_HOURS));
        // accidentally passes - because it is meaningful only in the timezone 
        // it was set ...
        assertFalse(monthView.isUnselectableDate(yesterday));
        // missing api on JXMonthView
        assertEquals("unselectable dates must have been cleared", 
                0, monthView.getSelectionModel().getUnselectableDates().size());
    }
    
    /**
     * test anchor: set to param as passed int setFirstDisplayedDate
     */
    @Test
    public void testAnchorDateInitial() {
        JXMonthView monthView = new JXMonthView();
        // sometime next month
        calendar.add(Calendar.MONTH, 1);
        monthView.setFirstDisplayedDay(calendar.getTime());
        assertEquals(calendar.getTime(), monthView.getAnchorDate());
        CalendarUtils.startOfMonth(calendar);
        assertEquals(calendar.getTime(), monthView.getFirstDisplayedDay());
    }

    /**
     * Issue #618-swingx: JXMonthView displays problems with non-default
     * timezones.
     * 
     * Here: test anchor invariant to time zone change
     */
    @Test
    public void testTimeZoneChangeAnchorInvariant() {
        JXMonthView monthView = new JXMonthView();
        Date anchor = monthView.getAnchorDate();
        TimeZone timeZone = monthView.getTimeZone();
        // just interested in a different timezone, no quantification intended
        monthView.setTimeZone(getTimeZone(timeZone, CalendarUtils.THREE_HOURS));
        assertEquals("anchor must be invariant to timezone change", 
                anchor, monthView.getAnchorDate());
    }

    /**
     * Issue #618-swingx: JXMonthView displays problems with non-default
     * timezones.
     * 
     * Here: test that the first displayed date is offset by offset diff of
     * timezones. Configure the monthView with a fixed timezone to clear up the
     * mist ...
     * 
     * This did fail on the server on 31mar2008, us/pacific timezone, en_US
     * locale. Trying to sim the context then.
     * 
     * Failed again at Tue Sep 30 10:12:31 PDT 2008, en_US locale. Next
     * (testTimeZoneChangeOffsetFirstDisplayedDate) failed at the same time, 
     */
    @Test
    public void testTimeZoneChangeToday() {
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
        Calendar localCal = Calendar.getInstance();
        String server = "server locale: " + Locale.getDefault()
                + "\n server timezone: " + localCal.getTimeZone()
                + "\n server local time: " + localCal.getTime();
        String timeZones = "first timeZone " + tz
                + "\n first timezone offset / min " + oldOffset / (1000 * 60)
                + "\n second timezone " + tzOther
                + "\n second timezone offset / min " + newOffset / (1000 * 60);
        String monthViewProps = "monthView locale: " + monthView.getLocale()
                + "\n monthView anchor " + anchor
                + "\n monthView firstDisplayed " + monthView.getFirstDisplayedDay();

        assertEquals(
                "first displayed must be offset by real offset "
                        + "\n ********** spurious failure - so try extensiv debug output:"
                        + "\n " + server 
                        + "\n " + timeZones 
                        + "\n " + monthViewProps, 
                (realOffset) / (1000 * 60),
                (monthView.getFirstDisplayedDay().getTime() - firstDisplayed.getTime()) / (1000 * 60));
/*
 * The output of failure:
 * 
 * junit.framework.AssertionFailedError: first displayed must be offset by real offset 
 ********** spurious failure - so try extensiv debug output:
 server locale: en_US
 server timezone: sun.util.calendar.ZoneInfo[id="US/Pacific",offset=-28800000,dstSavings=3600000,useDaylight=true,transitions=185,
lastRule=java.util.SimpleTimeZone[id=US/Pacific,offset=-28800000,dstSavings=3600000,useDaylight=true,startYear=0,startMode=3,startMonth=2,startDay=8,startDayOfWeek=1,startTime=7200000,startTimeMode=0,endMode=3,endMonth=10,endDay=1,endDayOfWeek=1,endTime=7200000,endTimeMode=0]]
 server local time: Tue Sep 30 10:12:31 PDT 2008
 first timeZone sun.util.calendar.ZoneInfo[id="GMT+04:00",offset=14400000,dstSavings=0,useDaylight=false,transitions=0,lastRule=null]
 first timezone offset / min 240
 second timezone sun.util.calendar.ZoneInfo[id="GMT+07:00",offset=25200000,dstSavings=0,useDaylight=false,transitions=0,lastRule=null]
 second timezone offset / min 420
 monthView locale: en_US
 monthView anchor Tue Sep 30 10:12:31 PDT 2008
 monthView firstDisplayed Tue Sep 30 10:00:00 PDT 2008 expected:<-180> but was:<43020>
        at org.jdesktop.swingx.JXMonthViewTest.testTimeZoneChangeToday(JXMonthViewTest.java:1385)
        at org.jmock.core.VerifyingTestCase.runBare(VerifyingTestCase.java:39)


 */
    
    }   

    /**
     * Issue #618-swingx: JXMonthView displays problems with non-default
     * timezones.
     * 
     * Here: test that the first displayed date is offset by offset diff of 
     * timezones.
     * Configure the monthView with a fixed timezone to clear up the mist ...
     * 
     * failed along with previous test, (Tue Sep 30 10:12:31 PDT 2008, en_US locale)
     * error out:
     * first displayed must be offset by real offset expected:
     *  <-10800000> but was:<2581200000>
     * 
     */
    @Test
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
    private TimeZone getTimeZone(TimeZone timeZone, int diffRawOffset) {
        int offset = timeZone.getRawOffset();
        int newOffset = offset < 0 ? offset + diffRawOffset : offset - diffRawOffset;
        String[] availableIDs = TimeZone.getAvailableIDs(newOffset);
        TimeZone newTimeZone = TimeZone.getTimeZone(availableIDs[0]);
        return newTimeZone;
    }
    
    /**
     * Issue #618-swingx: JXMonthView displays problems with non-default
     * timezones.
     * 
     * Here: test timezone fire
     */
    @Test
    public void testTimeZoneChangeNotification() {
        JXMonthView monthView = new JXMonthView();
        TimeZone timezone = monthView.getTimeZone();
        int offset = timezone.getRawOffset();
        int oneHour = 60 * 1000 * 60;
        int newOffset = offset < 0 ? offset + oneHour : offset - oneHour;
        String[] availableIDs = TimeZone.getAvailableIDs(newOffset);
        TimeZone newTimeZone = TimeZone.getTimeZone(availableIDs[0]);
        // sanity
        assertFalse(timezone.equals(newTimeZone));
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        monthView.setTimeZone(newTimeZone);
        TestUtils.assertPropertyChangeEvent(report, 
                "timeZone", timezone, newTimeZone, false);
    }
    

    /**
     * Issue #563-swingx: keybindings active if not focused.
     * Test that the bindings are dynamically installed when
     * shown in popup and de-installed if shown not in popup.
    */
    @Test
    public void testComponentInputMapEnabledControlsFocusedKeyBindings() {
        JXMonthView monthView = new JXMonthView();
        // initial: no bindings
        assertEquals("monthView must not have in-focused keyBindings", 0, 
                monthView.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).size());
        monthView.setComponentInputMapEnabled(true);
        // setting the flag installs bindings
        assertTrue("monthView must have in-focused keyBindings after showing in popup",  
              monthView.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).size() > 0);
        monthView.setComponentInputMapEnabled(false);
        // resetting the flag uninstalls the bindings
        assertEquals("monthView must not have in-focused keyBindings", 0, 
                monthView.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).size());
    }

    /**
     * Test default value and property change notificateion of 
     * the componentInputMapEnabled property.
     *
     */
    @Test
    public void testComponentInputMapEnabled() {
        JXMonthView monthView = new JXMonthView();
        assertFalse("the default value must be false", 
                monthView.isComponentInputMapEnabled());
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        monthView.setComponentInputMapEnabled(true);
        TestUtils.assertPropertyChangeEvent(report, 
                "componentInputMapEnabled", false, true, false);
        report.clear();
        monthView.setComponentInputMapEnabled(false);
        TestUtils.assertPropertyChangeEvent(report, 
                "componentInputMapEnabled", true, false, false);
    }
    
    /**
     * test doc'ed behaviour: model must not be null.
     *
     */
    @Test
    public void testSetModelNull() {
        JXMonthView monthView = new JXMonthView();
        assertNotNull(monthView.getSelectionModel());
        try {
            monthView.setSelectionModel(null);
            fail("null model must not be accepted");
        } catch (NullPointerException ex) {
            // expected - but how do we test fail-fast implemented
        }
    }
    
    /**
     * Enhanced commit/cancel.
     * 
     * test that actions resets model.adjusting to false.
     */
    @Test
    public void testCommitCancelResetsAdjusting() {
        JXMonthView monthView = new JXMonthView();
        monthView.getSelectionModel().setAdjusting(true);
        monthView.commitSelection();
        assertFalse("commit must reset adjusting", 
                monthView.getSelectionModel().isAdjusting());
        monthView.getSelectionModel().setAdjusting(true);
        monthView.cancelSelection();
        assertFalse("cancel must reset adjusting", 
                monthView.getSelectionModel().isAdjusting());
        
    }
    /**
     * Enhanced commit/cancel.
     * 
     * test that actions fire as expected.
     *
     */
    @Test
    public void testCommitCancelAPIFires() {
        JXMonthView picker = new JXMonthView();
        ActionReport report = new ActionReport();
        picker.addActionListener(report);
        picker.commitSelection();
        assertEquals(1, report.getEventCount());
        assertEquals(JXMonthView.COMMIT_KEY, report.getLastActionCommand());
        report.clear();
        picker.cancelSelection();
        assertEquals(1, report.getEventCount());
        assertEquals(JXMonthView.CANCEL_KEY, report.getLastActionCommand());
    }
    
    /**
     * Enhanced commit/cancel.
     * 
     * test that actions fire as expected.
     *
     */
    @Test
    public void testCommitCancelActionsFire() {
        JXMonthView picker = new JXMonthView();
        Action commitAction = picker.getActionMap().get(JXMonthView.COMMIT_KEY);
        ActionReport report = new ActionReport();
        picker.addActionListener(report);
        commitAction.actionPerformed(null);
        assertEquals(1, report.getEventCount());
        assertEquals(JXMonthView.COMMIT_KEY, report.getLastActionCommand());
        report.clear();
        Action cancelAction = picker.getActionMap().get(JXMonthView.CANCEL_KEY);
        cancelAction.actionPerformed(null);
        assertEquals(1, report.getEventCount());
        assertEquals(JXMonthView.CANCEL_KEY, report.getLastActionCommand());
    }


    /**
     * Enhanced commit/cancel.
     * 
     * test that actions are registered.
     *
     */
    @Test
    public void testCommitCancelActionExist() {
        JXMonthView picker = new JXMonthView();
        assertNotNull(picker.getActionMap().get(JXMonthView.CANCEL_KEY));
        assertNotNull(picker.getActionMap().get(JXMonthView.COMMIT_KEY));
    }
    
    /**
     * Enhanced commit/cancel.
     * 
     * test that actions are the same for new/old cancel/accept.
     *
     */
    @Test
    public void testCommitCancelSameAsOld() {
        JXMonthView picker = new JXMonthView();
        assertSame(picker.getActionMap().get("cancelSelection"),
                picker.getActionMap().get(JXMonthView.CANCEL_KEY));
        assertSame(picker.getActionMap().get("acceptSelection"),
                picker.getActionMap().get(JXMonthView.COMMIT_KEY));
    }

    /**
     * BasicMonthViewUI: use adjusting api in keyboard actions.
     * Here: test reset in cancel action.
     */
    @Test
    public void testAdjustingResetOnCancel() {
        JXMonthView view = new JXMonthView();
        Action select = view.getActionMap().get("selectNextDay");
        select.actionPerformed(null);
        DateSelectionReport report = new DateSelectionReport(view.getSelectionModel());
        Action cancel = view.getActionMap().get("cancelSelection");
        cancel.actionPerformed(null);
        assertFalse("ui keyboard action must have stopped model adjusting", 
                view.getSelectionModel().isAdjusting());
        assertEquals(2, report.getEventCount());
    }
    /**
     * BasicMonthViewUI: use adjusting api in keyboard actions.
     * Here: test reset in accept action.
     */
    @Test
    public void testAdjustingResetOnAccept() {
        JXMonthView view = new JXMonthView();
        Action select = view.getActionMap().get("selectNextDay");
        select.actionPerformed(null);
        DateSelectionReport report = new DateSelectionReport(view.getSelectionModel());
        Action cancel = view.getActionMap().get("acceptSelection");
        cancel.actionPerformed(null);
        assertFalse("ui keyboard action must have stopped model adjusting", 
                view.getSelectionModel().isAdjusting());
        assertEquals(1, report.getEventCount());
        assertEquals(EventType.ADJUSTING_STOPPED, report.getLastEvent().getEventType());
    }

    /**
     * BasicMonthViewUI: use adjusting api in keyboard actions.
     * Here: test set selection action.
     */
    @Test
    public void testAdjustingSetOnSelect() {
        JXMonthView view = new JXMonthView();
        DateSelectionReport report = new DateSelectionReport(view.getSelectionModel());
        Action select = view.getActionMap().get("selectNextDay");
        select.actionPerformed(null);
        assertTrue("ui keyboard action must have started model adjusting", 
                view.getSelectionModel().isAdjusting());
        assertEquals(2, report.getEventCount());
        // assert that the adjusting is fired before the set
        assertEquals(EventType.DATES_SET, report.getLastEvent().getEventType());
    }
 
    /**
     * BasicMonthViewUI: use adjusting api in keyboard actions.
     * Here: test add selection action.
     */
    @Test
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
        // only: the type a set instead or the expected added - bug or feature?
        // assertEquals(EventType.DATES_ADDED, report.getLastEvent().getEventType());
        // for now we are only interested in the adjusting (must not be the last)
        // so go for what's actually fired instead of what's expected
         assertEquals(EventType.DATES_SET, report.getLastEvent().getEventType());
        
    }

    /**
     * Issue #557-swingx: always fire actionEvent after esc/enter.
     * 
     * test fire after accept.
     */
    @Test
    public void testFireOnKeyboardAccept()  {
        JXMonthView monthView = new JXMonthView();
        Date date = new Date();
        monthView.setSelectionInterval(date, date);
        ActionReport report = new ActionReport();
        monthView.addActionListener(report);
        Action accept = monthView.getActionMap().get("acceptSelection"); 
        accept.actionPerformed(null);
        assertEquals(1, report.getEventCount());
    }

    /**
     * Issue #557-swingx: always fire actionEvent after esc/enter.
     * 
     * test fire after cancel.
     */
    @Test
    public void testFireOnKeyboardCancel()  {
        JXMonthView monthView = new JXMonthView();
        Date date = new Date();
        monthView.setSelectionInterval(date, date);
        ActionReport report = new ActionReport();
        monthView.addActionListener(report);
        Action accept = monthView.getActionMap().get("cancelSelection");
        accept.actionPerformed(null);
        assertEquals(1, report.getEventCount());
    }

    /**
     * expose more selection constraint methods in JXMonthView
     *
     */
    @Test
    public void testUpperBound() {
        JXMonthView view = new JXMonthView();
        view.setUpperBound(today);
        assertEquals(startOfDay(today), view.getUpperBound());
        // remove again
        view.setUpperBound(null);
        assertEquals(null, view.getUpperBound());
    }
    
    /**
     * expose more selection constraint methods in JXMonthView
     *
     */
    @Test
    public void testLowerBound() {
        JXMonthView view = new JXMonthView();
        view.setLowerBound(today);
        assertEquals(startOfDay(today), view.getLowerBound());
        // remove again
        view.setLowerBound(null);
        assertEquals(null, view.getLowerBound());
    }

    /**
     * test unselectable: use methods with Date.
     *
     */
    @Test
    public void testUnselectableDate() {
        JXMonthView monthView = new JXMonthView();
        // initial
        assertFalse(monthView.isUnselectableDate(today));
        // set unselectable today
        monthView.setUnselectableDates(today);
        assertTrue("raqw today must be unselectable", 
                monthView.isUnselectableDate(today));
        assertTrue("start of today must be unselectable", 
                monthView.isUnselectableDate(startOfDay(today)));
        assertTrue("end of today must be unselectable", 
                monthView.isUnselectableDate(endOfDay(today)));
        monthView.setUnselectableDates();
        assertFalse(monthView.isUnselectableDate(today));
        assertFalse(monthView.isUnselectableDate(startOfDay(today)));
        assertFalse(monthView.isUnselectableDate(endOfDay(today)));
    }

    /**
     * test unselectable: use methods with Date.
     * test NPE as doc'ed.
     */
    @Test
    public void testUnselectableDatesNPE() {
        JXMonthView monthView = new JXMonthView();
        try {
            monthView.setUnselectableDates((Date[])null);
            fail("null array must throw NPE");
        } catch (NullPointerException e) {
            // expected
        }
        try {
            monthView.setUnselectableDates(new Date[] {new Date(), null});
            fail("null elements must throw NPE");
        } catch (NullPointerException e) {
            // expected
        }
    }

   
    /**
     * Issue #494-swingx: JXMonthView changed all passed-in dates
     *
     */
    @Test
    public void testCleanupCopyDate() {
        JXMonthView monthView = new JXMonthView();
        Date copy = new Date(today.getTime());
        monthView.setSelectionInterval(today, today);
        assertEquals("the date used for selection must be unchanged", copy, today);
    }
    /**
     * test cover method: isSelectedDate
     *
     */
    @Test
    public void testIsSelectedDate() {
        JXMonthView monthView = new JXMonthView();
        monthView.setSelectionDate(today);
        assertTrue(monthView.isSelected(today));
        assertTrue(monthView.isSelected(startOfDay(today)));
    }
    
    /**
     * Sanity: test against regression
     * test cover method: isSelectedDate
     *
     */
    @Test
    public void testIsSelectedDate494() {
        JXMonthView monthView = new JXMonthView();
        Date copy = new Date(today.getTime());
        monthView.setSelectionDate(today);
        // use today
        monthView.isSelected(today);
        assertEquals("date must not be changed in isSelected", copy, today);
    }
   
    /**
     * test cover method: setSelectedDate
     *
     */
    @Test
    public void testSetSelectedDate() {
        JXMonthView monthView = new JXMonthView();
        Date copy = new Date(today.getTime());
        monthView.setSelectionDate(today);
        // sanity: date unchanged
        assertEquals(copy, today);
        assertEquals(startOfDay(today), monthView.getSelectionDate());
        monthView.setSelectionDate(null);
        assertTrue(monthView.isSelectionEmpty());
    }
    

    /**
     * test new (convenience) api on JXMonthView
     *
     */
    @Test
    public void testGetSelected() {
        JXMonthView monthView = new JXMonthView();
        assertNull(monthView.getSelectionDate());
        monthView.setSelectionInterval(today, today);
        assertEquals("same day", startOfDay(today), monthView.getSelectionDate());
        // clear selection
        monthView.clearSelection();
        assertNull(monthView.getSelectionDate());
    }
    
    
    @Test
    public void testDefaultConstructor() {
        JXMonthView monthView = new JXMonthView(Locale.US);
        assertTrue(monthView.isSelectionEmpty());
        assertTrue(SelectionMode.SINGLE_SELECTION == monthView.getSelectionMode());
        assertTrue(Calendar.SUNDAY == monthView.getFirstDayOfWeek());
    }

    @Test
    public void testLocale() {
        Locale[] locales = Locale.getAvailableLocales();

        for (Locale l : locales) {
            JComponent.setDefaultLocale(l);

            JXMonthView monthView = new JXMonthView();
            Locale locale = monthView.getLocale();
            Calendar cal = Calendar.getInstance(locale);
            int expectedFirstDayOfWeek = cal.getFirstDayOfWeek();

            assertTrue(expectedFirstDayOfWeek == monthView.getFirstDayOfWeek());
        }
    }

    @Test
    public void testEmptySelectionInitial() {
        JXMonthView monthView = new JXMonthView();
        assertTrue(monthView.isSelectionEmpty());
        SortedSet<Date> selection = monthView.getSelection();
        assertTrue(selection.isEmpty());
    }
    
    @Test
    public void testEmptySelectionClear() {
        JXMonthView monthView = new JXMonthView();
        monthView.setSelectionInterval(today, today);
        // sanity
        assertTrue(1 == monthView.getSelection().size());

        monthView.clearSelection();
        assertTrue(monthView.isSelectionEmpty());
        assertTrue(monthView.getSelection().isEmpty());
    }

    @Test
    public void testSelectionModes() {
        JXMonthView monthView = new JXMonthView();
        assertEquals(SelectionMode.SINGLE_SELECTION, monthView
                .getSelectionMode());
        for (SelectionMode mode : SelectionMode.values()) {
            monthView.setSelectionMode(mode);
            assertEquals(mode, monthView.getSelectionModel().getSelectionMode());
            assertEquals(mode, monthView.getSelectionMode());
        }

    }

    @Test
    public void testSingleSelection() {
        JXMonthView monthView = new JXMonthView();
        monthView.setSelectionMode(SelectionMode.SINGLE_SELECTION);

        monthView.setSelectionInterval(yesterday, yesterday);
        assertTrue(1 == monthView.getSelection().size());
        assertEquals(startOfDay(yesterday), monthView.getFirstSelectionDate());

        monthView.setSelectionInterval(yesterday, afterTomorrow);
        assertTrue(1 == monthView.getSelection().size());
        assertEquals(startOfDay(yesterday), monthView.getFirstSelectionDate());
    }

    @Test
    public void testSingleIntervalSelection() {
        JXMonthView monthView = new JXMonthView();
        monthView.setSelectionMode(SelectionMode.SINGLE_INTERVAL_SELECTION);

        monthView.setSelectionInterval(yesterday, yesterday);
        assertTrue(1 == monthView.getSelection().size());
        assertEquals(startOfDay(yesterday), monthView.getFirstSelectionDate());

        monthView.setSelectionInterval(yesterday, tomorrow);
        
        assertTrue(3 == monthView.getSelection().size());
        assertEquals(startOfDay(yesterday), monthView.getFirstSelectionDate());
        assertEquals(startOfDay(tomorrow), monthView.getLastSelectionDate());

    }



    @Test
    public void testMultipleIntervalSelection() {
        JXMonthView monthView = new JXMonthView();
        monthView.setSelectionMode(SelectionMode.MULTIPLE_INTERVAL_SELECTION);

        monthView.setSelectionInterval(yesterday, yesterday);
        monthView.addSelectionInterval(afterTomorrow, afterTomorrow);
        
        assertEquals(2, monthView.getSelection().size());
        assertEquals(startOfDay(yesterday), monthView.getFirstSelectionDate());
        assertEquals(startOfDay(afterTomorrow), monthView.getLastSelectionDate());
    }



    @Test
    public void testDateSelectionListener() {
        JXMonthView monthView = new JXMonthView();
        DateSelectionReport listener = new DateSelectionReport();
        monthView.getSelectionModel().addDateSelectionListener(listener);

        Date date = new Date();
        monthView.setSelectionInterval(date, date);
        assertThat(listener.getEventCount(), is(1));
        assertThat(listener.getLastEvent().getEventType(), is(EventType.DATES_SET));
    }


    /**
     * test setting/checking flagged dates (api with Date)
     */
    @Test
    public void testFlaggedDateRemoveNotify() {
        JXMonthView monthView = new JXMonthView();
        
        monthView.setFlaggedDates(tomorrow, yesterday);
        SortedSet<Date> oldFlagged = monthView.getFlaggedDates();
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        monthView.removeFlaggedDates(tomorrow);
        TestUtils.assertPropertyChangeEvent(report, "flaggedDates", 
                oldFlagged, monthView.getFlaggedDates());
    }

    /**
     * test setting/checking flagged dates (api with Date)
     */
    @Test
    public void testFlaggedDateRemove() {
        JXMonthView monthView = new JXMonthView();
        
        monthView.addFlaggedDates(tomorrow, yesterday);
        assertEquals(2, monthView.getFlaggedDates().size());
        monthView.removeFlaggedDates(tomorrow);
        assertTrue(monthView.isFlaggedDate(yesterday));
        assertFalse(monthView.isFlaggedDate(tomorrow));
    }

    
    /**
     * test setting/checking flagged dates (api with Date)
     */
    @Test
    public void testFlaggedDateClear() {
        JXMonthView monthView = new JXMonthView();
        
        monthView.addFlaggedDates(tomorrow, yesterday);
        assertEquals(2, monthView.getFlaggedDates().size());
        monthView.clearFlaggedDates();
        assertFalse("flagged dates must be cleared", monthView.hasFlaggedDates());
    }

    /**
     * test setting/checking flagged dates (api with Date)
     */
    @Test
    public void testFlaggedDateClearNotify() {
        JXMonthView monthView = new JXMonthView();
        
        monthView.setFlaggedDates(tomorrow, yesterday);
        SortedSet<Date> oldFlagged = monthView.getFlaggedDates();
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        monthView.clearFlaggedDates();
        TestUtils.assertPropertyChangeEvent(report, "flaggedDates", 
                oldFlagged, monthView.getFlaggedDates());
    }

    /**
     * test setting/checking flagged dates (api with Date)
     */
    @Test
    public void testFlaggedDateAdd() {
        JXMonthView monthView = new JXMonthView();
        
        monthView.setFlaggedDates(yesterday);
        monthView.addFlaggedDates(tomorrow);
        assertEquals(2, monthView.getFlaggedDates().size());
        assertTrue(monthView.isFlaggedDate(yesterday));
        assertTrue(monthView.isFlaggedDate(tomorrow));
    }
    /**
     * test setting/checking flagged dates (api with Date)
     */
    @Test
    public void testFlaggedDateAddNotify() {
        JXMonthView monthView = new JXMonthView();
        
        monthView.setFlaggedDates(yesterday);
        SortedSet<Date> oldFlagged = monthView.getFlaggedDates();
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        monthView.addFlaggedDates(tomorrow);
        TestUtils.assertPropertyChangeEvent(report, "flaggedDates", 
                oldFlagged, monthView.getFlaggedDates());
    }
    /**
     * test setting/checking flagged dates (api with Date)
     */
    @Test
    public void testFlaggedDateSet() {
        JXMonthView monthView = new JXMonthView();
        monthView.setFlaggedDates(today);
        assertTrue(monthView.isFlaggedDate(today));
        monthView.setFlaggedDates();
        assertFalse(monthView.isFlaggedDate(today));
    }
    /**
     * test setting/checking flagged dates (api with Date)
     */
    @Test
    public void testFlaggedDateNotification() {
        JXMonthView monthView = new JXMonthView();
        SortedSet<Date> oldFlagged = monthView.getFlaggedDates();
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        monthView.setFlaggedDates(today);
        TestUtils.assertPropertyChangeEvent(report, "flaggedDates", 
                oldFlagged, monthView.getFlaggedDates());
    }

    /**
     * test setting/checking flagged dates (api with Date)
     */
    @Test
    public void testFlaggedDateGet() {
        JXMonthView monthView = new JXMonthView();
        Date date = new Date();
        SortedSet<Date> set = new TreeSet<Date>();
        set.add(monthView.getSelectionModel().getNormalizedDate(date));
        monthView.setFlaggedDates(date);
        assertEquals(set, monthView.getFlaggedDates());
    }
   
    @Test
    public void testShowLeadingDates() {
        JXMonthView monthView = new JXMonthView();
        assertFalse(monthView.isShowingLeadingDays());
        monthView.setShowingLeadingDays(true);
        assertTrue(monthView.isShowingLeadingDays());
    }

    @Test
    public void testShowTrailingDates() {
        JXMonthView monthView = new JXMonthView();
        assertFalse(monthView.isShowingTrailingDays());
        monthView.setShowingTrailingDays(true);
        assertTrue(monthView.isShowingTrailingDays());
    }

    private Date startOfDay(Date date) {
        return CalendarUtils.startOfDay(calendar, date);
    }
 
    private Date endOfDay(Date date) {
        return CalendarUtils.endOfDay(calendar, date);
    }
}