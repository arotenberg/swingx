/*
 * $Id: BasicMonthViewVisualCheck.java 4032 2011-07-08 09:20:19Z kleopatra $
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
package org.jdesktop.swingx.plaf.basic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXMonthView;
import org.jdesktop.swingx.SwingXUtilities;
import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.calendar.DateSelectionModel.SelectionMode;
import org.jdesktop.swingx.plaf.basic.BasicMonthViewUI.RenderingHandler;
import org.jdesktop.swingx.renderer.FormatStringValue;
import org.jdesktop.swingx.renderer.StringValue;

/**
 * TODO add type doc
 * 
 * @author Jeanette Winzenburg
 */
public class BasicMonthViewVisualCheck extends InteractiveTestCase {

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(BasicMonthViewVisualCheck.class.getName());
    
    public static void main(String[] args) {
        BasicMonthViewVisualCheck test = new BasicMonthViewVisualCheck();
        try {
//            test.runInteractiveTests();
            test.runInteractiveTests(".*Rendering.*");
//            test.runInteractiveTests(".*Debug.*");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class MyMonthViewUI extends BasicMonthViewUI {

        @SuppressWarnings({"UnusedDeclaration"})
        public static ComponentUI createUI(JComponent c) {
            return new MyMonthViewUI();
        }

        @Override
        protected CalendarRenderingHandler createRenderingHandler() {
            return new MyCalendarRenderingHandler();
        }
        
        
    }
    
    /**
     * Issue #1062-swingx: core formats don't use arabic digits. Opened rendering 
     */
    public static class MyCalendarRenderingHandler extends RenderingHandler {

        @Override
        protected StringValue createDayStringValue(Locale locale) {
            if (locale == null) {
                locale = Locale.getDefault();
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("d", locale);
            LOG.info("got here? " + locale.getCountry() + "-" + locale.getLanguage());
            adjustFormatSymbols(locale, (DecimalFormat) dateFormat.getNumberFormat());
            FormatStringValue sv = new FormatStringValue(dateFormat) {

                @Override
                public String getString(Object value) {
                    if (value instanceof Calendar) {
                        ((DateFormat) getFormat()).setTimeZone(((Calendar) value).getTimeZone());
                        value = ((Calendar) value).getTime();
                    }
                    return super.getString(value);
                }

            };
            return sv;
        }

        /**
         * @param locale
         * @param df
         */
        private void adjustFormatSymbols(Locale locale, DecimalFormat df) {
            if ("ar".equals(locale.getLanguage())) {
                DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
                // set the beginning of the range to Arabic digits
                dfs.setZeroDigit('\u0660');
                df.setDecimalFormatSymbols(dfs);
            }
        }

        @Override
        protected StringValue createWeekOfYearStringValue(Locale locale) {
            if (locale == null) {
                locale = Locale.getDefault();
            }
            DecimalFormat format = (DecimalFormat) NumberFormat.getInstance(locale);
            adjustFormatSymbols(locale, format);
            StringValue wsv = new FormatStringValue(format) {

                @Override
                public String getString(Object value) {
                    if (value instanceof Calendar) {
                        value = ((Calendar) value).get(Calendar.WEEK_OF_YEAR);
                    }
                    return super.getString(value);
                }

            };
            return wsv;
        }

        
        
    }
    /**
     * Issue #750-swingx: use rendering to side-step antialiase probs.
     * 
     * Debugging ...
     */
    public void interactiveRenderingOn() {
        new JXMonthView();
        // KEEP this is global state - uncomment for debug painting completely
//        // use spinning navigation header if zoomable
//        UIManager.put(CalendarHeaderHandler.uiControllerID, "org.jdesktop.swingx.plaf.basic.SpinningCalendarHeaderHandler");
//        // configure header: arrows around month text
//        UIManager.put(SpinningCalendarHeaderHandler.ARROWS_SURROUND_MONTH, Boolean.TRUE);
//        // configuter header: allow focus in spinner text (== editable)
//        UIManager.put(SpinningCalendarHeaderHandler.FOCUSABLE_SPINNER_TEXT, Boolean.TRUE);
//        // custom ui delegate for arabic digits in formats
//        UIManager.put(JXMonthView.uiClassID, "org.jdesktop.swingx.plaf.basic.BasicMonthViewUIVisualCheck$MyMonthViewUI");
//        // force picker to use a zoomable monthView by default
//        UIManager.put("JXDatePicker.forceZoomable", Boolean.TRUE);
        // KEEP this is global state - uncomment for debug painting completely
        UIManager.put("JXMonthView.trailingDayForeground", Color.YELLOW);
        UIManager.put("JXMonthView.leadingDayForeground", Color.ORANGE);
        UIManager.put("JXMonthView.weekOfTheYearForeground", Color.GREEN);
        UIManager.put("JXMonthView.unselectableDayForeground", Color.MAGENTA);
        String frameTitle = "Debug painting: rendering on";
        showDebugMonthView(frameTitle);
    }

    /**
     * @param frameTitle
     * @param disableRendering
     */
    private void showDebugMonthView(String frameTitle) {
        final JXMonthView monthView = new JXMonthView();
        LOG.info("todayBack: " + monthView.getTodayBackground());
//        monthView.setZoomable(true);
        monthView.setDayForeground(Calendar.SUNDAY, Color.BLUE);
        monthView.setDaysOfTheWeekForeground(Color.RED);
        monthView.setFlaggedDayForeground(Color.CYAN);
        monthView.setSelectionBackground(Color.GRAY);
        monthView.setSelectionForeground(Color.GREEN);
        monthView.setTodayBackground(Color.PINK);
        monthView.setTraversable(true);
        monthView.setShowingWeekNumber(true);
        monthView.setShowingLeadingDays(true);
        monthView.setShowingTrailingDays(true);
        monthView.setSelectionMode(SelectionMode.SINGLE_INTERVAL_SELECTION);
        monthView.setPreferredColumnCount(2);
        monthView.setPreferredRowCount(2);
        final JXFrame frame = wrapInFrame(monthView, frameTitle);
        addComponentOrientationToggle(frame);
        Action toggleTraversable = new AbstractAction("toggle zoomable") {

            public void actionPerformed(ActionEvent e) {
                monthView.setZoomable(!monthView.isZoomable());
                
            }
            
        };
        addAction(frame, toggleTraversable);
        addEnabledToggle(frame, monthView);
        final JXDatePicker picker = new JXDatePicker();
        picker.getMonthView().setShowingWeekNumber(monthView.isShowingWeekNumber());
        picker.getMonthView().setZoomable(true);
        Action toggleShowingWeekNumbers = new AbstractAction("toggle weekNumbers") {
            
            public void actionPerformed(ActionEvent e) {
                monthView.setShowingWeekNumber(!monthView.isShowingWeekNumber());
                picker.getMonthView().setShowingWeekNumber(monthView.isShowingWeekNumber());
            }
            
        };
        addAction(frame, toggleShowingWeekNumbers);
        picker.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals(JXDatePicker.CANCEL_KEY)) return;
                if (picker.getDate() == null) return;
                monthView.setFlaggedDates(picker.getDate());
            }
            
        });
        final JXDatePicker unselectable = new JXDatePicker();
        unselectable.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals(JXDatePicker.CANCEL_KEY)) return;
                if (unselectable.getDate() == null) return;
                monthView.setUnselectableDates(unselectable.getDate());
            }
            
        });
        final JComboBox zoneSelector = new JComboBox(Locale.getAvailableLocales());
        // Synchronize the monthView's and selector's zones.
        zoneSelector.setSelectedItem(monthView.getLocale());

        // Set the monthView's time zone based on the selected time zone.
        zoneSelector.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                Locale zone = (Locale) zoneSelector.getSelectedItem();
                SwingXUtilities.setComponentTreeLocale(frame, zone);
//                monthView.setLocale(zone);
//                monthView.revalidate();
            }
        });


        JComponent pickers = Box.createHorizontalBox();
        pickers.add(new JLabel("Flagged: "));
        pickers.add(picker);
        pickers.add(new JLabel("Unselectable: "));
        pickers.add(unselectable);
        pickers.add(new JLabel("Locale: "));
        pickers.add(zoneSelector);
        frame.add(pickers, BorderLayout.SOUTH);
        show(frame);
    }


    /**
     * Issue #736-swingx: monthView cannot cope with minimalDaysInFirstWeek.
     * 
     * Debugging ...
     */
    public void interactiveDayAt() {
        final JXMonthView monthView = new JXMonthView();
        monthView.setTraversable(true);
        monthView.setShowingWeekNumber(true);
        monthView.setShowingLeadingDays(true);
        monthView.setShowingTrailingDays(true);
        monthView.setSelectionMode(SelectionMode.SINGLE_INTERVAL_SELECTION);
        monthView.setPreferredColumnCount(2);
        monthView.setPreferredRowCount(2);
        final BasicMonthViewUI ui = ((BasicMonthViewUI) monthView.getUI());
        monthView.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
//                LOG.info("calendar grid" + ui.calendarGrid);
//                LOG.info("dayAt " + e.getPoint() + ": "
//                        + "\n" + monthView.getDayAtLocation(e.getX(), e.getY()));
//                Calendar monthAtLocation = ui.getMonthAtLocation(e.getX(), e.getY());
//                LOG.info("month start " + 
//                        (monthAtLocation != null ? monthAtLocation.getTime() : null));
                
//                Point p = ui.getMonthGridPositionAtLocation(e.getX(), e.getY());
//                LOG.info("month bounds from logical " + 
//                        p + " \n " +
//                        ui.getMonthBounds(p.y, p.x));
//                LOG.info("month bounds at location" + 
//                        ui.getMonthBoundsAtLocation(e.getX(), e.getY()));
                LOG.info("day grid position " + 
                        ui.getDayGridPositionAtLocation(e.getX(), e.getY()) 
                      + "\nday bounds " + 
                        ui.getDayBoundsAtLocation(e.getX(), e.getY()));
            }
            
        });
        Action action = new AbstractActionExt("toggle minimal") {

            public void actionPerformed(ActionEvent e) {
                int minimal = monthView.getSelectionModel().getMinimalDaysInFirstWeek();
                monthView.getSelectionModel().setMinimalDaysInFirstWeek(minimal > 1 ? 1 : 4);
            }
            
        };
        final JXFrame frame = wrapInFrame(monthView, "test mapping: printed on mouse release");
        addAction(frame, action);
        addComponentOrientationToggle(frame);
        frame.pack();
        frame.setVisible(true);
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
        monthView.setSelectionMode(SelectionMode.SINGLE_INTERVAL_SELECTION);
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
        frame.pack();
        frame.setVisible(true);
    }


}
