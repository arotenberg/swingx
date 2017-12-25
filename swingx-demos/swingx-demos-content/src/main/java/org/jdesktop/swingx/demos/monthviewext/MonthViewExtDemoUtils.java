/*
 * $Id: MonthViewExtDemoUtils.java 4097 2011-11-30 19:22:13Z kschaefe $
 *
 * Copyright 2009 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx.demos.monthviewext;

import java.awt.Component;
import java.util.Calendar;

import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.plaf.basic.DemoCalendarRenderingHandler.DemoCalendarAdapter;

/**
 * Utilities for extended MonthView demo.
 * 
 * @author Jeanette Winzenburg
 */
public class MonthViewExtDemoUtils {

    public static class DayOfWeekHighlightPredicate implements HighlightPredicate {

        private int dayOfWeek;

        public DayOfWeekHighlightPredicate(int dayOfWeek) {
            this.dayOfWeek = dayOfWeek;
        }
        
        @Override
        public boolean isHighlighted(Component renderer,
                ComponentAdapter adapter) {
            // <snip> Custom CalendarRenderingHandler
            // predicate specialized on adapter of a calendar
            // return true for day of week as configured
            if (!(adapter instanceof DemoCalendarAdapter)) return false;
            Calendar calendar = ((DemoCalendarAdapter) adapter).getCalendar();
            return ((DemoCalendarAdapter) adapter).isDayInMonth() 
                  && calendar.get(Calendar.DAY_OF_WEEK) == dayOfWeek;
            // </snip>
        }
        
    }
    
    /**
     * PENDING JW: should be in core SwingX.
     */
    public static final HighlightPredicate SELECTED = new HighlightPredicate() {
        
        @Override
        public boolean isHighlighted(Component renderer,
                ComponentAdapter adapter) {
            return adapter.isSelected();
        }
        
    };

    private MonthViewExtDemoUtils() {}
}
