/*
 * $Id: DemoCalendarRenderingHandler.java 4097 2011-11-30 19:22:13Z kschaefe $
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
package org.jdesktop.swingx.plaf.basic;

import java.util.Calendar;

import javax.swing.JComponent;

import org.jdesktop.swingx.JXMonthView;
import org.jdesktop.swingx.decorator.CompoundHighlighter;
import org.jdesktop.swingx.decorator.Highlighter;

/**
 * An example of a custom CalendarRenderingHandler. As of laziness (mine :-) this
 * extends the BasicXX to widen access and provide hooks to add Highlighters.
 * 
 * @author Jeanette Winzenburg
 */
public class DemoCalendarRenderingHandler extends BasicCalendarRenderingHandler {

    private DemoCalendarAdapter adapter;
    private CompoundHighlighter demoHighlighter;

    /**
     * Sets the <code>Highlighter</code>s to the table, replacing any old settings.
     * None of the given Highlighters must be null.<p>
     * 
     * @param highlighters zero or more not null highlighters to use for renderer decoration.
     * @throws NullPointerException if array is null or array contains null values.
     */
    // <snip> Custom CalendarRenderingHandler
    // Minimal api to allow adding Highlighters. 
    public void setHighlighters(Highlighter... highlighters) {
        getHighlighter().setHighlighters(highlighters);
    }
    // </snip>
    
    /**
     * Appends the <code>Highlighter</code>s to the end of the list of used
     * <code>Highlighter</code>s. None of the given Highlighters must be null.
     * <p>
     * 
     * @param highlighters zero or more not null highlighters to use for renderer decoration.
     * @throws NullPointerException if array is null or array contains null values.
     */
    // <snip> Custom CalendarRenderingHandler
    // Minimal api to allow adding Highlighters. 
    public void addHighlighters(Highlighter... highlighters) {
        for (Highlighter highlighter : highlighters) {
            getHighlighter().addHighlighter(highlighter);
        }
    }
    // </snip>
    
    /**
     * Overridden to apply the additional highlighters, if any.
     */
    @Override
    public JComponent prepareRenderingComponent(JXMonthView monthView,
            Calendar calendar, CalendarState dayState) {
        JComponent component = super.prepareRenderingComponent(monthView, calendar, dayState);
         return (JComponent) getHighlighter().highlight(
                 component, getCalendarAdapter(monthView, calendar, dayState));
    }

    /**
     * Returns a CompoundHighlighter containing the additional highlighters, if 
     * any.
     * 
     * @return a CompoundHighlighter containing the additional higlighters, 
     *    guaranteed to never be null, but might be empty.
     */
    protected CompoundHighlighter getHighlighter() {
        if (demoHighlighter == null) {
            demoHighlighter = new CompoundHighlighter();
        }
        return demoHighlighter;
    }

    /**
     * Returns a CalendarAdapter installed with the given monthView and 
     * state.
     * 
     * @param monthView
     * @param calendar
     * @param dayState
     * @return
     */
    protected DemoCalendarAdapter getCalendarAdapter(JXMonthView monthView,
            Calendar calendar, CalendarState dayState) {
        if (adapter == null) {
            adapter = new DemoCalendarAdapter(monthView);
        }
        return adapter.install(calendar, dayState);
    }
    
    /**
     * CalendarAdapter, subclassed for public access and fix of selected
     * day state.
     *  
     */
    public static class DemoCalendarAdapter extends CalendarAdapter {

        /**
         * Instantiates a ComponentAdapter with the given monthView.
         * 
         * @param component
         */
        public DemoCalendarAdapter(JXMonthView component) {
            super(component);
        }
        
        /**
         * {@inheritDoc} <p>
         * 
         * Overridden to return a publicly accessible class.
         */
        @Override
        public DemoCalendarAdapter install(Calendar calendar, CalendarState dayState) {
            return (DemoCalendarAdapter) super.install(calendar, dayState);
        }

        /**
         * {@inheritDoc} <p>
         * 
         * Overridden to report selected only if a day of the month. 
         */
        @Override
        public boolean isSelected() {
            // PENDING JW: this should be handled in super
            if (!isDayInMonth()) return false;
            return super.isSelected();
        }
        
        /**
         * Returns a clone of the current calendar.
         * @return
         */
        public Calendar getCalendar() {
            // PENDING JW: this should be handled in super
            return (Calendar) calendar.clone();
        }
        
        /**
         * @param dayState
         * @return
         */
        public boolean isDayInMonth() {
            return (CalendarState.IN_MONTH == getCalendarState()) 
                || (CalendarState.TODAY == getCalendarState());
        }
        
    }
}
