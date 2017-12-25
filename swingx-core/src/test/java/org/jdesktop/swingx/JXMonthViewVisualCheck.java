/*
 * $Id: JXMonthViewVisualCheck.java 4310 2013-08-27 10:38:06Z kleopatra $
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

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.jdesktop.swingx.JXMonthViewTest.Clock;
import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.calendar.CalendarUtils;
import org.jdesktop.swingx.calendar.DateSelectionModel;
import org.jdesktop.swingx.calendar.DateSelectionModel.SelectionMode;
import org.jdesktop.swingx.calendar.SingleDaySelectionModel;
import org.jdesktop.swingx.event.DateSelectionEvent;
import org.jdesktop.swingx.event.DateSelectionListener;
import org.jdesktop.swingx.test.XTestUtils;

/**
 * Test to expose known issues with JXMonthView.
 * 
 * @author Jeanette Winzenburg
 */
public class JXMonthViewVisualCheck extends InteractiveTestCase {
    private static final Logger LOG = Logger.getLogger(JXMonthViewVisualCheck.class
            .getName());

    @SuppressWarnings("unused")
    private Calendar calendar;

    public static void main(String[] args) {
//        UIManager.put("JXDatePicker.forceZoomable", Boolean.TRUE);
      setSystemLF(true);
      JXMonthViewVisualCheck  test = new JXMonthViewVisualCheck();
      try {
//          test.runInteractiveTests();
//        test.runInteractiveTests(".*Event.*");
//          test.runInteractiveTests("interactive.*Zoomable.*");
//          test.runInteractiveTests("interactive.*Title.*");
//          test.runInteractiveTests("interactive.*TimeZone.*");
//          test.runInteractiveTests("interactive.*Disable.*");
        test.runInteractive("Unselectable");
      } catch (Exception e) {
          System.err.println("exception when executing interactive tests:");
          e.printStackTrace();
      }
  }
    
    /**
     * unselectableDayForeground reported to have no effect
     * http://stackoverflow.com/q/15926663/203657
     * can't reproduce - seems to work here
     */
    public void interactiveUnselectableDateForeground() {
        Color old = UIManager.getColor("JXMonthView.unselectableDayForeground");
        UIManager.put("JXMonthView.unselectableDayForeground", Color.BLUE);
        JXDatePicker picker = new JXDatePicker();
        JXMonthView monthView = new JXMonthView();
        monthView = picker.getMonthView();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        monthView.setUnselectableDates(cal.getTime());
        showInFrame(picker, "custom unselectable");
        UIManager.put("JXMonthView.unselectableDayForeground", old);
        
    }
    /**
     * LastDisplayed incorrect?
     */
    public void interactiveLastDisplayedMultiple() {
        final JXMonthView monthView = new JXMonthView();
        monthView.setPreferredColumnCount(2);
        JXFrame frame = wrapInFrame(monthView, "lastDisplayed");
        Action action = new AbstractAction("print last") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                LOG.info("last displayed " + monthView.getLastDisplayedDay());
            }
        };
        addAction(frame, action);
        show(frame);
    }

    /**
     * Issue #1125-swingx: JXMonthView today incorrect.
     * Visual partly fix: updated in addNotify.
     */
    public void interactiveTodayUpdateInPicker() {
        final Clock clock = new Clock();
        JXMonthView monthView = JXMonthViewTest.createMonthViewWithClock(clock);
        JXDatePicker picker = new JXDatePicker();
        picker.setMonthView(monthView);
        JXFrame frame = wrapInFrame(picker, "today ");
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
     * Issue #1028-swingx: monthView title looks wrong if box paddings = 0
     */
    public void interactiveTitleBorder() {
        JXMonthView monthView = new JXMonthView();
        monthView.setBoxPaddingX(0);
        monthView.setBoxPaddingY(0);
        monthView.setTraversable(true);
        JComponent comp = Box.createHorizontalBox();
        comp.add(monthView);
        JXMonthView other = new JXMonthView();
        other.setTraversable(true);
        comp.add(other);
        showInFrame(comp, "monthView title border");
        
    }

    /**
     * Issue 807-swingx: JXMonthView must have visual clue if enabled.
     */
    public void interactiveDisabled() {
        JXMonthView monthView = new JXMonthView();
        monthView.setEnabled(false);
        JComponent comp = Box.createHorizontalBox();
        comp.add(monthView);
        comp.add(new JXMonthView());
        showInFrame(comp, "disabled <--> enabled");
    }
    
    
    public void interactiveRevalidateOnZoomable() {
        final JXMonthView monthView = new JXMonthView();
        final Font font = monthView.getFont();
        JXFrame frame = wrapInFrame(monthView, "Revalidate on zoomable");
        Action toggleBackground = new AbstractAction("toggleZoomable") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                monthView.setZoomable(!monthView.isZoomable());
                        
            }
            
        };
        addAction(frame, toggleBackground);
        Action toggleTraversable = new AbstractAction("toggleTraversable") {
            
            private boolean traversable;

            @Override
            public void actionPerformed(ActionEvent e) {
                traversable = !traversable;
                monthView.setTraversable(traversable);
                        
            }
            
        };
        addAction(frame, toggleTraversable);
        Action toggleFont = new AbstractAction("toggleFont") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                
                if (monthView.getFont().isItalic()) {
                    monthView.setFont(font);
                } else {
                    monthView.setFont(font.deriveFont(Font.ITALIC));
                }
            }
            
        };
        addAction(frame, toggleFont);
        show(frame);

    }
    /**
     * Issue #931-swingx: JXMonthView not repainted on property change.
     * 
     * look for
     * - selectionBackground
     * - selectionForeground (not taken?)
     * - flaggedDateForeground
     * - componentOrientation (duplicate #996-swingx)
     * 
     */
    public void interactiveRepaintOnPropertyChange() {
        final JXMonthView monthView = new JXMonthView();
        final Font font = monthView.getFont();
        monthView.setSelectionDate(CalendarUtils.startOfWeek(calendar, new Date()));
        monthView.setFlaggedDates(CalendarUtils.endOfWeek(calendar, new Date()));
        final Color selectionBackground = monthView.getSelectionBackground();
        final Color selectionForeground = monthView.getSelectionForeground();
        final Color flaggedDayForeground = monthView.getFlaggedDayForeground();
        JXFrame frame = wrapInFrame(monthView, "repaint on propertyChange");
        Action toggleBackground = new AbstractAction("toggleSelectionBack") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                monthView.setSelectionBackground(
                        Color.PINK.equals(monthView.getSelectionBackground()) ? 
                                selectionBackground : Color.PINK);
            }
            
        };
        addAction(frame, toggleBackground);
        Action toggleForeground = new AbstractAction("toggleSelectionFore") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                
                monthView.setSelectionForeground(
                        Color.RED.equals(monthView.getSelectionForeground()) ? 
                                selectionForeground : Color.RED);
            }
            
        };
        addAction(frame, toggleForeground);
        Action toggleFlaggedForeground = new AbstractAction("toggleFlaggedFore") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                
                monthView.setFlaggedDayForeground(
                        Color.GREEN.equals(monthView.getFlaggedDayForeground()) ? 
                                flaggedDayForeground : Color.GREEN);
            }
            
        };
        addAction(frame, toggleFlaggedForeground);
        
        Action toggleCO = new AbstractAction("toggleCO") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                
                if (monthView.getComponentOrientation().isLeftToRight()) {
                    monthView.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                } else {
                    monthView.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
                    
                }
            }
            
        };
        addAction(frame, toggleCO);
        Action toggleFont = new AbstractAction("toggleFont") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                
                if (monthView.getFont().isItalic()) {
                    monthView.setFont(font);
                } else {
                    monthView.setFont(font.deriveFont(Font.ITALIC));
                }
            }
            
        };
        addAction(frame, toggleFont);
        Action toggleEnabled = new AbstractAction("toggleEnabled") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                monthView.setEnabled(!monthView.isEnabled());
            }
            
        };
        addAction(frame, toggleEnabled);
        show(frame);
        
    }

    /**
     * Issue #786-swingx: IllegalStateException when paintDays of April 2008.
     * 
     * Problem was in that particular timezone - traversing to April 
     * 
     * Assumption of staying at startOfWeek in paintDays is wrong if the month
     * is the month of turning on the DST. Remove the check for now.
     * 
     */
    public void interactiveTimeZoneDST() {
        JXMonthView monthView = new JXMonthView();
        monthView.setTraversable(true);
        Calendar calendar = monthView.getCalendar();
        calendar.set(2008, Calendar.MARCH, 31);
        monthView.ensureDateVisible(calendar.getTime());
        TimeZone cairo = TimeZone.getTimeZone("Africa/Cairo");
        monthView.setTimeZone(cairo);
        JXFrame frame = showInFrame(monthView, "MonthView: DST");
        addStatusMessage(frame, "IllegalState in April");
    }


    /**
     * Issue #749-swingx: enhanced flagged dates support (add/remove)
     * 
     * Visually check if the monthView is updated on toggling several properties.
     */
    public void interactiveToggleProperties() {
        final JXMonthView monthView = new JXMonthView(); 
        monthView.setTraversable(true);
        final JXFrame frame = showInFrame(monthView, "MonthView - click property and see the change");
        final Calendar calendar = monthView.getCalendar();
        calendar.add(Calendar.DATE, 5);
        Action unselectable = new AbstractActionExt("lowerbound") {
            @Override
            public void actionPerformed(ActionEvent e) {
                monthView.setLowerBound(monthView.getLowerBound() == null ? calendar.getTime() : null);
            }
            
        };
        addAction(frame, unselectable);
       Action action = new AbstractActionExt("today flag") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (monthView.hasFlaggedDates()) {
                    monthView.clearFlaggedDates();
                } else {
                    monthView.setFlaggedDates(monthView.getToday());
                }
            }
            
        };
        addAction(frame, action);
        Action trailing = new AbstractActionExt("trailing") {
            @Override
            public void actionPerformed(ActionEvent e) {
                monthView.setShowingTrailingDays(!monthView.isShowingTrailingDays());
            }
            
        };
        addAction(frame, trailing);
        Action leading = new AbstractActionExt("leading") {
            @Override
            public void actionPerformed(ActionEvent e) {
                monthView.setShowingLeadingDays(!monthView.isShowingLeadingDays());
            }
            
        };
        addAction(frame, leading);
        Action weekNumbers = new AbstractActionExt("weekNumbers") {
            @Override
            public void actionPerformed(ActionEvent e) {
                monthView.setShowingWeekNumber(!monthView.isShowingWeekNumber());
            }
            
        };
        addAction(frame, weekNumbers);
        Action traversable = new AbstractActionExt("traversable") {
            @Override
            public void actionPerformed(ActionEvent e) {
                monthView.setTraversable(!monthView.isTraversable());
            }
            
        };
        addAction(frame, traversable);
        Action firstDay = new AbstractActionExt("firstDay") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int firstDay = monthView.getFirstDayOfWeek();
                monthView.setFirstDayOfWeek(firstDay == Calendar.SUNDAY ? 
                        Calendar.MONDAY : Calendar.SUNDAY);
            }
            
        };
        addAction(frame, firstDay);
        Action today = new AbstractActionExt("today") {
            @Override
            public void actionPerformed(ActionEvent e) {
                monthView.incrementToday();
            }
            
        };
        addAction(frame, today);
        Action daysOfWeek = new AbstractActionExt("daysOfWeek") {
            String[] days = {"S", "M", "D", "M", "D", "F", "S"};
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] dof = monthView.getDaysOfTheWeek();
                if (dof[0].equals(days[0])) {
                    monthView.setDaysOfTheWeek(null);
                } else {
                    monthView.setDaysOfTheWeek(days);
                }    
            }
            
        };
        addAction(frame, daysOfWeek);
        frame.pack();
    };
    
    /**
     * Issue #736-swingx: monthView cannot cope with minimalDaysInFirstWeek.
     * 
     * Here: look at impact of forcing the minimalDays to a value different
     * from the calendar. Days must be displayed in starting from the 
     * first row under the days-of-week.
     */
    public void interactiveMinimalDaysInFirstWeek() {
        final JXMonthView monthView = new JXMonthView();
        monthView.setTraversable(true);
        monthView.setShowingWeekNumber(true);
        monthView.setShowingLeadingDays(true);
        monthView.setShowingTrailingDays(true);
        Action action = new AbstractActionExt("toggle minimal") {

            @Override
            public void actionPerformed(ActionEvent e) {
                int minimal = monthView.getSelectionModel().getMinimalDaysInFirstWeek();
                monthView.getSelectionModel().setMinimalDaysInFirstWeek(minimal > 1 ? 1 : 4);
            }
            
        };
        final JXFrame frame = wrapInFrame(monthView, "click unselectable fires ActionEvent");
        addAction(frame, action);
        addComponentOrientationToggle(frame);
        JXStatusBar bar = getStatusBar(frame);
        final JComboBox dayOfWeekComboBox = new JComboBox(new String[]{"Sunday", "Monday", "Tuesday",
                "Wednesday", "Thursday", "Friday", "Saturday"});
        dayOfWeekComboBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int selected = dayOfWeekComboBox.getSelectedIndex();
                monthView.setFirstDayOfWeek(selected + Calendar.SUNDAY);
                
            }
            
        });
        dayOfWeekComboBox.setSelectedIndex(monthView.getFirstDayOfWeek() - Calendar.SUNDAY);
        bar.add(dayOfWeekComboBox);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Issue #736-swingx: monthView cannot cope with minimalDaysInFirstWeek.
     * 
     * Here: look at impact of forcing the minimalDays to a value different
     * from the calendar. Days must be displayed in starting from the 
     * first row under the days-of-week. Selection must be reflected in the 
     * datepicker.
     */
    public void interactiveMinimalDaysInFirstWeekPicker() {
        JXDatePicker picker = new JXDatePicker();
        final JXMonthView monthView = picker.getMonthView();
        monthView.setShowingWeekNumber(true);
        monthView.setShowingLeadingDays(true);
        monthView.setShowingTrailingDays(true);
        Action action = new AbstractActionExt("toggle minimal") {

            @Override
            public void actionPerformed(ActionEvent e) {
                int minimal = monthView.getSelectionModel().getMinimalDaysInFirstWeek();
                monthView.getSelectionModel().setMinimalDaysInFirstWeek(minimal > 1 ? 1 : 4);
            }
            
        };
        final JXFrame frame = wrapInFrame(picker, "click unselectable fires ActionEvent");
        addAction(frame, action);
        frame.pack();
        frame.setVisible(true);
    }


    
    /**
     * Issue #711-swingx: fake properties.
     * 
     * visually testing today increment (it's not public api but can't
     * think of a way to simulate the timer).
     */
    public void interactiveSetToday() {
        final JXMonthView monthView = new JXMonthView(); 
        monthView.setTraversable(true);
        final JXFrame frame = showInFrame(monthView, "MonthView today");
        Action action = new AbstractActionExt("increment today - programmatically") {
            @Override
            public void actionPerformed(ActionEvent e) {
                monthView.incrementToday();
            }
            
        };
        addAction(frame, action);
        frame.pack();
    };


    /**
     * Issue #706-swingx: picker doesn't update monthView.
     * 
     * Here: visualize weird side-effects of monthView.updateUI - year 
     * incremented.
     */
    public void interactiveUpdateUIMonthView() {
//        calendar.set(1955, 10, 9);
        final JXMonthView monthView = new JXMonthView(); 
        monthView.setTraversable(true);
        final JXFrame frame = showInFrame(monthView, "MonthView update ui - visible month kept");
        Action action = new AbstractActionExt("toggleUI") {
            @Override
            public void actionPerformed(ActionEvent e) {
                monthView.updateUI();
            }
            
        };
        addAction(frame, action);
        frame.pack();
    };

    /**
     * Issue #706-swingx: picker doesn't update monthView.
     * 
     * Show toggle of UI (selectin color)
     */
    public void interactiveUpdateUIMonthViewCustomUI() {
        final JXMonthView monthView = new JXMonthView();
        monthView.setSelectionDate(new Date());
        final JXFrame frame = showInFrame(monthView, "MonthView custom ui (selection color)");
        Action action = new AbstractActionExt("toggleUI") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String uiClass = (String) UIManager.get(JXMonthView.uiClassID);
                boolean custom = uiClass.indexOf("Custom") > 0;
                if (!custom) {
                    UIManager.put(JXMonthView.uiClassID, "org.jdesktop.swingx.test.CustomMonthViewUI");
                } else {
                    UIManager.put(JXMonthView.uiClassID, null);
                }
                monthView.updateUI();
                custom = !custom;
            }
            
        };
        addAction(frame, action);
        frame.pack();
    };
    
    /**
     * #705-swingx: JXMonthview must not scroll on revalidate.
     * 
     * Misbehaviour here : multi-month spanning selection, travers two month into the future and
     * resize the frame - jumps back to first. Auto-scroll in the delegates
     * selection listener would have a similar effect.
     * 
     */
    public void interactiveAutoScrollOnResize() {
        final JXMonthView us = new JXMonthView();
        us.setTraversable(true);
        us.setSelectionMode(SelectionMode.SINGLE_INTERVAL_SELECTION);
        final Calendar today = Calendar.getInstance();
        CalendarUtils.endOfMonth(today);
        Date start = today.getTime();
        today.add(Calendar.DAY_OF_MONTH, 60);
        us.setSelectionInterval(start, today.getTime());
        JXFrame frame = wrapInFrame(us, "resize");
        // quick check if lastDisplayed is updated on resize
        Action printLast = new AbstractActionExt("log last") {

            @Override
            public void actionPerformed(ActionEvent e) {
                
                LOG.info("last updated?" + us.getLastDisplayedDay());
            }
            
        };
        addAction(frame, printLast);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * #703-swingx: set date to first of next doesn't update the view.
     * 
     * Behaviour is consistent with core components. Except that it is doing 
     * too much: revalidate most probably shouldn't change the scrolling state?
     * 
     * Simulated misbehaviour here: multi-month spanning selection, travers into the future and
     * add selection at the end - jumps back to first. Auto-scroll in the delegates
     * selection listener would have the effect.
     * 
     */
    public void interactiveAutoScrollOnSelectionSim() {
        final JXMonthView us = new JXMonthView();
        us.setTraversable(true);
        us.setSelectionMode(SelectionMode.SINGLE_INTERVAL_SELECTION);
        final Calendar today = Calendar.getInstance();
        CalendarUtils.endOfMonth(today);
        Date start = today.getTime();
        today.add(Calendar.DAY_OF_MONTH, 60);
        us.setSelectionInterval(start, today.getTime());
        JXFrame frame = wrapInFrame(us, "Simulate autoscroll on selection");
        Action nextMonthInterval = new AbstractActionExt("add selected") {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (us.isSelectionEmpty()) return;
                Date start = us.getSelectionDate();
                
                today.setTime(us.getLastSelectionDate());
                today.add(Calendar.DAY_OF_MONTH, 5);
                us.addSelectionInterval(start, today.getTime());
                // here we simulate an auto-scroll
                us.ensureDateVisible(start);
            }
            
        };
        addAction(frame, nextMonthInterval);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * #681-swingx: first row overlaps days.
     * 
     * Looks like a problem with the constructor taking a locale? 
     * Default is okay (even if German), US is okay, explicit german is wrong.
     */
    public void interactiveFirstRowOfMonthSetLocale() {
        JPanel p = new JPanel();
        // default constructor
        p.add(new JXMonthView());
        // explicit us locale
        JXMonthView us = new JXMonthView();
        us.setLocale(Locale.US);
        p.add(us);
        // explicit german locale
        JXMonthView german = new JXMonthView();
        german.setLocale(Locale.GERMAN);
        p.add(german);
        showInFrame(p, "first row overlapping - setLocale");
    }

   
    /**
     * #681-swingx: first row overlaps days.
     * 
     * Looks like a problem with the constructor taking a locale? 
     * Default is okay (even if German), US is okay, explicit german is wrong.
     */
    public void interactiveFirstRowOfMonthLocaleConstructor() {
        JPanel p = new JPanel();
        // default constructor
        p.add(new JXMonthView());
        // explicit us locale
        p.add(new JXMonthView(Locale.US));
//         explicit german locale
        p.add(new JXMonthView(Locale.GERMAN));
        showInFrame(p, "first row overlapping - constructor");
    }
    /**
     * #681-swingx: first row overlaps days.
     * Here everything looks okay.
     * 
     * @see #interactiveFirstRowOfMonthLocaleDependent()
     */
    public void interactiveFirstRowOfMonth() {
        JXMonthView monthView = new JXMonthView();
        calendar.set(2008, 0, 1);
        monthView.setSelectionDate(calendar.getTime());
        showInFrame(monthView, "first row");
    }

    /**
     * Issue #618-swingx: JXMonthView displays problems with non-default
     * timezones.
     * 
     */
    public void interactiveUpdateLocale() {
        JComponent panel = Box.createVerticalBox();

        final JXDatePicker picker = new JXDatePicker(new Date());
        final JXMonthView monthView = new JXMonthView();
        monthView.setTraversable(true);
        monthView.setShowingWeekNumber(true);
        final JComboBox zoneSelector = new JComboBox(Locale.getAvailableLocales());
        // Synchronize the monthView's and selector's zones.
        zoneSelector.setSelectedItem(monthView.getLocale());

        // Set the monthView's time zone based on the selected time zone.
        zoneSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                Locale zone = (Locale) zoneSelector.getSelectedItem();
                monthView.setLocale(zone);
                picker.setLocale(zone);
//                if ("sh".equals(zone.getLanguage()) ){
//                    String[] months = DateFormatSymbols.getInstance(zone).getMonths();
//                    SimpleDateFormat simple = new SimpleDateFormat("MMMM", zone);
//                    DateFormat format = DateFormat.getDateInstance(DateFormat.FULL, zone);
//                    LOG.info("serbian latin: " + zone + 
//                            "/" + format.format(new Date()) +
//                            " / " + months[0] + 
//                            " / " + simple.format(new Date()));
//                }
//                
                
            }
        });

        panel.add(picker);
        panel.add(monthView);
        panel.add(zoneSelector);
        showInFrame(panel, "Locale");
    }

    
    /**
     * Issue #618-swingx: JXMonthView displays problems with non-default
     * timezones.
     * 
     */
    public void interactiveUpdateWeekOnSetTimeZone() {
        JComponent panel = Box.createVerticalBox();
        List<TimeZone> zones = new ArrayList<TimeZone>();
        for (int i = -14; i < 13; i++) {
            String id = "GMT" + i;
            if (i >= 0) {
                id = "GMT+" + i;
            }
            TimeZone zone = TimeZone.getTimeZone(id);
            zones.add(zone);
        }
        final JComboBox zoneSelector = new JComboBox(zones.toArray());
        
        final JXMonthView monthView = new JXMonthView();
        monthView.setTraversable(true);
        monthView.setShowingWeekNumber(true);
        // Synchronize the picker and selector's zones.
        zoneSelector.setSelectedItem(monthView.getTimeZone().getID());
        
        // Set the picker's time zone based on the selected time zone.
        zoneSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
//                String zone = (String) zoneSelector.getSelectedItem();
                TimeZone tz = (TimeZone) zoneSelector.getSelectedItem();
                monthView.setTimeZone(tz);
                DateFormat format = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
                format.setTimeZone(tz);
                DateFormat week = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
                week.setTimeZone(tz);
                ((SimpleDateFormat) week).applyPattern("w");
                Calendar calendar = Calendar.getInstance(tz);
                calendar.add(Calendar.HOUR_OF_DAY, - 12);
                LOG.info("now: " + format.format(calendar.getTime()) + 
                        " / " + calendar.get(Calendar.WEEK_OF_YEAR) + week.format(calendar.getTime()));
            }
        });
        
        panel.add(monthView);
        panel.add(zoneSelector);
        showInFrame(panel, "today - on custom TimeZones");
    }
    
    /**
     * Issue #618-swingx: JXMonthView displays problems with non-default
     * timezones.
     * 
     */
    public void interactiveUpdateTodayOnSetTimeZone() {
        JComponent panel = Box.createVerticalBox();
        
        final JComboBox zoneSelector = new JComboBox(TimeZone.getAvailableIDs());
        final JXMonthView monthView = new JXMonthView();
        monthView.setTraversable(true);
        monthView.setShowingWeekNumber(true);
        // Synchronize the picker and selector's zones.
        zoneSelector.setSelectedItem(monthView.getTimeZone().getID());
        
        // Set the picker's time zone based on the selected time zone.
        zoneSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                String zone = (String) zoneSelector.getSelectedItem();
                TimeZone tz = TimeZone.getTimeZone(zone);
                monthView.setTimeZone(tz);
                
                assertEquals(tz, monthView.getCalendar().getTimeZone());
            }
        });
        
        panel.add(monthView);
        panel.add(zoneSelector);
        showInFrame(panel, "today - on setTimeZone");
    }
    
    /**
     * Issue #618-swingx: JXMonthView displays problems with non-default
     * timezones.
     * Issue #1143-swingx: JXMonthView NPE on setTimeZone/setModel
     * 
     */
    public void interactiveUpdateTodayOnSetModelTimeZone() {
        JComponent panel = Box.createVerticalBox();

        final JComboBox zoneSelector = new JComboBox(TimeZone.getAvailableIDs());
        final JXMonthView monthView = new JXMonthView();
        monthView.setShowingWeekNumber(true);
        monthView.setTraversable(true);
        // Synchronize the picker and selector's zones.
        zoneSelector.setSelectedItem(monthView.getTimeZone().getID());

        // Set the picker's time zone based on the selected time zone.
        zoneSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                String zone = (String) zoneSelector.getSelectedItem();
                TimeZone tz = TimeZone.getTimeZone(zone);
                DateSelectionModel model = new SingleDaySelectionModel();
                model.setTimeZone(tz);
                monthView.setSelectionModel(model);
              
                assertEquals(tz, monthView.getCalendar().getTimeZone());
            }
        });

        panel.add(monthView);
        panel.add(zoneSelector);
        showInFrame(panel, "today - on setModel with different TimeZone");
    }
    
    /**
     * Issue #618-swingx: JXMonthView displays problems with non-default
     * timezones.
     * Issue #658-swingx: JXDatePicker today is not updated on timezone.
     * 
     * 
     */
    public void interactiveTimeZoneClearDateState() {
        JPanel panel = new JPanel();

        final JComboBox zoneSelector = new JComboBox(TimeZone.getAvailableIDs());
        final JXDatePicker picker = new JXDatePicker(new Date());
        final JXMonthView monthView = new JXMonthView();
        monthView.setSelectionDate(picker.getDate());
        monthView.setLowerBound(XTestUtils.getStartOfToday(-10));
        monthView.setUpperBound(XTestUtils.getStartOfToday(10));
        monthView.setUnselectableDates(XTestUtils.getStartOfToday(2));
        monthView.setFlaggedDates(new Date[] {XTestUtils.getStartOfToday(4)});
        monthView.setTraversable(true);
        // Synchronize the picker and selector's zones.
        zoneSelector.setSelectedItem(picker.getTimeZone().getID());

        // Set the picker's time zone based on the selected time zone.
        zoneSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                String zone = (String) zoneSelector.getSelectedItem();
                TimeZone tz = TimeZone.getTimeZone(zone);
                picker.setTimeZone(tz);
                monthView.setTimeZone(tz);
              
                assertEquals(tz, monthView.getCalendar().getTimeZone());
            }
        });

        panel.add(zoneSelector);
        panel.add(picker);
        panel.add(monthView);
        JXFrame frame = showInFrame(panel, "clear internal date-related state");
        Action assertAction = new AbstractActionExt("assert dates") {

            @Override
            public void actionPerformed(ActionEvent e) {
                Calendar cal = monthView.getCalendar();
                DateFormat format = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
                format.setTimeZone(monthView.getTimeZone());
                LOG.info("cal/firstDisplayed " + 
                        format.format(cal.getTime()) + " / " 
                        +format.format(monthView.getFirstDisplayedDay()));
            }
            
        };
        addAction(frame, assertAction);
        frame.pack();
    }
    
    /**
     * Issue #659-swingx: lastDisplayedDate must be synched.
     * 
     */
    public void interactiveLastDisplayed() {
        final JXMonthView month = new JXMonthView();
        month.setSelectionMode(SelectionMode.SINGLE_INTERVAL_SELECTION);
        month.setTraversable(true);
        Action action = new AbstractActionExt("check lastDisplayed") {

            @Override
            public void actionPerformed(ActionEvent e) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(month.getLastDisplayedDay());
                Date viewLast = cal.getTime();
                cal.setTime(month.getUI().getLastDisplayedDay());
                Date uiLast = cal.getTime();
                LOG.info("last(view/ui): " + viewLast + "/" + uiLast);
                
            }
            
        };
        JXFrame frame = wrapInFrame(month, "default - for debugging only");
        addAction(frame, action);
        frame.setVisible(true);
    }


    /**
     * Issue #637-swingx: make JXMonthView Locale-aware.
     * 
     * Applied the patch as provided by pes17.
     * 
     */
    public void interactiveLocale() {
        JXMonthView monthView = new JXMonthView(Locale.GERMAN);
        JXMonthView other = new JXMonthView(Locale.FRANCE);
        JComponent comp = new JPanel();
        comp.add(monthView);
        comp.add(other);
        showInFrame(comp, "Localized monthView");
    }

    /**
     * Issue #563-swingx: arrow keys active even if not focused.
     * focus the button and use the arrow keys: selection moves.
     * Reason was that the WHEN_IN_FOCUSED_WINDOW key bindings
     * were always installed. 
     * 
     * Fixed by dynamically bind/unbind component input map bindings
     * based on the JXMonthView's componentInputMapEnabled property.
     *
     */
    public void interactiveMistargetedKeyStrokes() {
        JXMonthView month = new JXMonthView();
        JComponent panel = new JPanel();
        panel.add(new JButton("something to focus"));
        panel.add(month);
        showInFrame(panel, "default - for debugging only");
    }
    
    /**
     * Issue #563-swingx: arrow keys active even if not focused.
     * focus the button and use the arrow keys: selection moves.
     *
     * Fixed by dynamically bind/unbind component input map bindings
     * based on the JXMonthView's componentInputMapEnabled property.
     */
    public void interactiveMistargetedKeyStrokesPicker() {
        JXMonthView month = new JXMonthView();
        JComponent panel = new JPanel();
        JXDatePicker button = new JXDatePicker();
        panel.add(button);
        panel.add(month);
        showInFrame(panel, "default - for debugging only");
    }
    
    /**
     * Informally testing adjusting property on mouse events.
     * 
     * Hmm .. not formally testable without mocks/ui unit tests?
     *
     */
    public void interactiveAdjustingOnMouse() {
        final JXMonthView month = new JXMonthView();
        // we rely on being notified after the ui delegate ... brittle.
        MouseAdapter m = new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                LOG.info("pressed - expect true " + month.getSelectionModel().isAdjusting());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                LOG.info("released - expect false" + month.getSelectionModel().isAdjusting());
            }
            
        };
        month.addMouseListener(m);
        showInFrame(month, "Mouse and adjusting - state on pressed/released");
    }

    /**
     * Issue #555-swingx: multiple selection with keyboard not working
     * Happens for standalone, okay for monthview in popup.
     * 
     * Fixed as a side-effect of cleanup of input map bindings. 
     * 
     * Regression #555 
     * 
     * Issue #996-swingx: NPE while dragging 
     * 
     * to reproduce: first click into a empty (leading/trailing) day then
     * drag into month. Fixed (brutish? - not entirely sure as the user
     * interaction is suboptimal anyway ..)
     */
    public void interactiveMultipleSelectionWithKeyboard() {
        JXMonthView single = new JXMonthView();
        single.setSelectionMode(SelectionMode.SINGLE_SELECTION);
        JXMonthView interval = new JXMonthView();
        interval.setSelectionMode(SelectionMode.SINGLE_INTERVAL_SELECTION);
        JXMonthView multiple = new JXMonthView();
        multiple.setSelectionMode(SelectionMode.MULTIPLE_INTERVAL_SELECTION);
        // for comparison: single interval in popup is working
        JXDatePicker picker = new JXDatePicker();
        JXMonthView intervalForPicker = new JXMonthView();
        intervalForPicker.setSelectionMode(SelectionMode.MULTIPLE_INTERVAL_SELECTION);
        picker.setMonthView(intervalForPicker);
        
        JComponent comp = new JPanel();
        comp.add(single);
        comp.add(interval);
        comp.add(multiple);
        comp.add(picker);
        showInFrame(comp, "select interval with keyboard: single vs. singleInt vs. multipleInt");
        
    }
    /**
     * Issue #??-swingx: esc/enter does not always fire actionEvent.
     * 
     * Fixed: committing/canceling user gestures always fire.
     * 
     * Open: mouse-gestures?
     *
     */
    public void interactiveMonthViewEvents() {
        JXMonthView monthView = new JXMonthView();
        JXMonthView interval = new JXMonthView();
        interval.setSelectionMode(SelectionMode.SINGLE_INTERVAL_SELECTION);
        JXMonthView multiple = new JXMonthView();
        multiple.setSelectionMode(SelectionMode.MULTIPLE_INTERVAL_SELECTION);
        ActionListener l = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                LOG.info("got action from: " + e.getSource().getClass().getName() + 
                        "\n" + e);
            }
            
        };
        monthView.addActionListener(l);
        interval.addActionListener(l);
        multiple.addActionListener(l);
        DateSelectionListener d = new DateSelectionListener() {

            @Override
            public void valueChanged(DateSelectionEvent ev) {
                LOG.info("got selection from: " + ev.getSource().getClass().getName() + 
                        "\n" + ev);
            }
            
        };
        monthView.getSelectionModel().addDateSelectionListener(d);
        interval.getSelectionModel().addDateSelectionListener(d);
        multiple.getSelectionModel().addDateSelectionListener(d);
        
        JXDatePicker picker = new JXDatePicker();
        JXMonthView intervalForPicker = new JXMonthView();
        intervalForPicker.setSelectionMode(SelectionMode.SINGLE_INTERVAL_SELECTION);
        // JW: this picker comes up with today - should have taken the
        // empty selection (which it does the unit test)
        picker.setMonthView(intervalForPicker);
        
        JComponent comp = new JPanel();
        comp.add(monthView);
        comp.add(interval);
        comp.add(multiple);
        comp.add(picker);
        JXFrame frame = showInFrame(comp, "events from monthView");
        // JXRootPane eats esc 
        frame.getRootPaneExt().getActionMap().remove("esc-action");

    }

    /**
     * Issue #426-swingx: NPE on traversing 
     * 
     * example from bug report
     *
     */
    public void interactiveMonthViewTravers() {
        JXMonthView monthView = new JXMonthView();
        monthView.setTraversable(true);
        showInFrame(monthView, "travers throws NPE");
    }

//----------------------
    @Override
    protected void setUp() throws Exception {
        calendar = Calendar.getInstance();
    }

    
    /**
     * do nothing test - keep the testrunner happy.
     */
    public void testDummy() {
    }

}
