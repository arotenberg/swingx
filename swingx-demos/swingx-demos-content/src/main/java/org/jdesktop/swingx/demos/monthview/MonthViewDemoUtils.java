/*
 * $Id: MonthViewDemoUtils.java 4097 2011-11-30 19:22:13Z kschaefe $
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
package org.jdesktop.swingx.demos.monthview;

import java.util.Calendar;
import java.util.Date;

import org.jdesktop.beansbinding.Converter;
import org.jdesktop.swingx.util.Contract;

/**
 * Utilities for base MonthViewDemo.
 * 
 * @author Jeanette Winzenburg
 */
public class MonthViewDemoUtils {
    public static class DayOfWeekConverter extends Converter<Date, Integer> {

        Calendar calendar;
        
        public DayOfWeekConverter(Calendar calendar) {
            this.calendar = Contract.asNotNull(calendar, "calendar must not be null");
        }
        
        @Override
        public Integer convertForward(Date date) {
            calendar.setTime(date);
            return calendar.get(Calendar.DAY_OF_WEEK);
        }

        @Override
        public Date convertReverse(Integer arg0) {
            return null;
        }
        
    }
}

