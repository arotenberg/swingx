/*
 * $Id: DemoCalendarHeaderHandler.java 4097 2011-11-30 19:22:13Z kschaefe $
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

import org.jdesktop.swingx.plaf.basic.SpinningCalendarHeaderHandler;

/**
 * Subclassed for easier demo'ing the - limited - configuration options
 * of the current year choosing header.
 * 
 * @author Jeanette Winzenburg
 */
public class DemoCalendarHeaderHandler extends SpinningCalendarHeaderHandler {

    /**
     * Instantiates a CalendarHeaderHandler with year spinner not 
     * focusable and month navigation arrows not around month.
     */
    public DemoCalendarHeaderHandler() {
        this(false, false);
    }

    /**
     * Instantiates a CalenderHeaderHandler with customized arrow/spinner
     * properties.
     * 
     * @param focusable spinner focusable
     * @param surrounds month navigation arrows around month text
     */
    public DemoCalendarHeaderHandler(boolean focusable, boolean surrounds) {
        super();
        getHeaderComponent().setSpinnerFocusable(focusable);
        getHeaderComponent().setArrowsSurroundMonth(surrounds);
    }
}
