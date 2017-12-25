/*
 * $Id: DatePickerFormatterIssues.java 3140 2008-12-16 15:09:09Z kleopatra $
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
import java.util.Locale;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.jdesktop.swingx.JXDatePicker;
import org.junit.After;
import org.junit.Before;

/**
 * Unit tests for <code>DatePickerFormatter</code>.
 * 
 * @author Jeanette Winzenburg
 */
public class DatePickerFormatterIssues extends TestCase {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(DatePickerFormatterIssues.class.getName());
    
    @SuppressWarnings("unused")
    private Calendar cal;
    private Locale originalLocale;

    

    @Override
    @Before
    public void setUp() {
        originalLocale = Locale.getDefault();
        cal = Calendar.getInstance();
        // force loading of resources
        new JXDatePicker();
    }

    @Override
    @After
    public void tearDown() {
        Locale.setDefault(originalLocale);
    }
    
    /**
     * Dummy method to keep the testRunner happy ...
     */
    public void testDummy() {
        
    }

}
