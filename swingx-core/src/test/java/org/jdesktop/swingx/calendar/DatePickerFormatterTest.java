/*
 * $Id: DatePickerFormatterTest.java 3186 2009-01-20 14:15:18Z kleopatra $
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

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.calendar.DatePickerFormatter.DatePickerFormatterUIResource;
import org.jdesktop.swingx.plaf.UIManagerExt;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for <code>DatePickerFormatter</code>.
 * 
 * Extracted from JXDatePickerTest.
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class DatePickerFormatterTest extends TestCase {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(DatePickerFormatterTest.class.getName());
    
    /**
     * Issue #945-swingx: don't force default format for Locales without
     * properties.
     * 
     * Instead, use the system's default date format instance.
     */
    @Test
    public void testPickerFormatterNoDefaults() {
        Locale.setDefault(Locale.JAPAN);
        if (UIManagerExt.getString("JXDatePicker.longFormat", Locale
                .getDefault()) != null) {
            LOG.info("can't run formatter fallback - has format entry");
            return;
        }
        DatePickerFormatter formatter = new DatePickerFormatter();
        assertNotNull(formatter.getFormats());
        assertTrue("must have formats:  " + formatter.getFormats().length,
                formatter.getFormats().length > 0);
        assertEquals(DateFormat.getDateInstance(DateFormat.SHORT), formatter
                .getFormats()[0]);
    }

    /**
     * Issue #945-swingx: don't force default format for Locales without
     * properties.
     * 
     * LinkFormat pattern in base defaults to DateFormat's default with 
     * style DEFAULT.
     */
    @Test
    public void testPickerFormatterDefaultToday() {
        Locale.setDefault(Locale.JAPAN);
        if (UIManagerExt.getString("JXDatePicker.longFormat", Locale
                .getDefault()) != null) {
            LOG.info("can't run formatter fallback - has format entry");
            return;
        }
        String pattern = UIManagerExt.getString("JXDatePicker.linkFormat");
        assertNotNull("linkFormat entry is available", pattern);
        MessageFormat format = new MessageFormat(pattern, Locale.getDefault());
        assertEquals(DateFormat.getDateInstance(), format
                .getFormats()[0]);
    }
    
    /**
     * Issue #691-swingx: locale setting not taken.
     * Here: test contructor with locale.
     */
    @Test
    public void testPickerFormatterCustomLocale() {
        Locale locale = Locale.FRENCH;
        DatePickerFormatter formatter = new DatePickerFormatter(locale);
        SimpleDateFormat format = (SimpleDateFormat) formatter.getFormats()[0];
        String pattern = UIManagerExt.getString("JXDatePicker.longFormat", locale);
        assertEquals("format pattern must be same as from localized resource", 
                pattern, format.toPattern());
    }

    /**
     * Issue #691-swingx: locale setting not taken.
     * Here: test contructor with locale in uiresource.
     */
    @Test
    public void testPickerFormatterUIResourceCustomLocale() {
        Locale locale = Locale.FRENCH;
        DatePickerFormatter formatter = new DatePickerFormatterUIResource(locale);
        SimpleDateFormat format = (SimpleDateFormat) formatter.getFormats()[0];
        String pattern = UIManagerExt.getString("JXDatePicker.longFormat", locale);
        assertEquals("format pattern must be same as from localized resource", 
                pattern, format.toPattern());
    }
    
    /**
     * Issue #691-swingx: locale setting not taken.
     * Here: test empty contructor == default locale in uiresource.
     * Adjusted to fix of Issue ??-swingx: use system default format
     * if resources for Locale have no JXDatePicker entry
     */
    @Test
    public void testPickerFormatterUIResourceDefaultLocale() {
        DatePickerFormatter formatter = new DatePickerFormatterUIResource();
        SimpleDateFormat format = (SimpleDateFormat) formatter.getFormats()[0];
        String pattern = UIManagerExt.getString("JXDatePicker.longFormat");
        if (pattern != null) {
            assertEquals(pattern, format.toPattern()); 
        } else {
            LOG.info("can't run test, no datePicker resource entries for Locale " + Locale.getDefault());
        }
    }

    /**
     * Issue #691-swingx: locale setting not taken.
     * Here: test empty contructor == default locale.
     * Adjusted to fix of Issue ??-swingx: use system default format
     * if resources for Locale have no JXDatePicker entry
     */
    @Test
    public void testPickerFormatterDefaultLocale() {
        DatePickerFormatter formatter = new DatePickerFormatter();
        SimpleDateFormat format = (SimpleDateFormat) formatter.getFormats()[0];
        String pattern = UIManagerExt.getString("JXDatePicker.longFormat");
        if (pattern != null) {
            assertEquals(pattern, format.toPattern()); 
        } else {
            LOG.info("can't run test, no datePicker resource entries for Locale " + Locale.getDefault());
        }
    }

    /**
     * Issue #584-swingx: need to clarify null handling.
     * 
     * here: test formatter constructor with empty formats array
     * 
     */
    @Test
    public void testPickerFormatterEmptyFormats() {
        DateFormat[] formats = new DateFormat[0];
        DatePickerFormatter formatter = new DatePickerFormatter(formats);
        assertNotNull(formatter.getFormats());
        assertEquals(formats.length, formatter.getFormats().length);
    }

    /**
     * Issue #584-swingx: need to clarify null handling.
     * 
     * here: test formatter constructor with formats array contain
     * null
     * 
     */
    @Test
    public void testPickerFormatterConstructorWithNullFormats() {
        DateFormat[] formats = new DateFormat[] { null };
        try {
            new DatePickerFormatter(formats);
            fail("constructor must throw NPE if array contains null formats");
        } catch (NullPointerException e) {
            // doc'ed behaviour
        }
    }


    /**
     * Issue #584-swingx: need to clarify null handling.
     * 
     * here: test default constructor
     * Adjusted to fix of Issue ??-swingx: use system default format
     * if resources for Locale have no JXDatePicker entry
     */
    @Test
    public void testPickerFormatterDefaultConstructor() {
        DatePickerFormatter formatter = new DatePickerFormatter();
        assertNotNull(formatter.getFormats());
        int uiFormats = 0;
        if (UIManagerExt.getString("JXDatePicker.longFormat") != null) {
            uiFormats++;
        }
        if (UIManagerExt.getString("JXDatePicker.mediumFormat") != null) {
            uiFormats++;
        }
        if (UIManagerExt.getString("JXDatePicker.shortFormat") != null) {
            uiFormats++;
        }
        if (uiFormats == 0) {
            uiFormats = 1;
        }
        assertEquals(uiFormats, formatter.getFormats().length);
    }

    /**
     * Issue #584-swingx: need to clarify null handling.
     * 
     * here: test constructor with null parameter has same defaults
     *   as parameterless
     * 
     */
    @Test
    public void testPickerFormatterConstructorWithParameterNull() {
        DatePickerFormatter defaultFormatter = new DatePickerFormatter();
        DateFormat[] defaultFormats = defaultFormatter.getFormats();
        DatePickerFormatter formatter = new DatePickerFormatter((DateFormat[])null);
        DateFormat[] formats = formatter.getFormats();
        assertNotNull("formats must not be null", formats);
        assertEquals(defaultFormats.length, formats.length);
        for (int i = 0; i < defaultFormats.length; i++) {
            assertEquals("format must be equals to default at " + i, 
                    defaultFormats[i], formats[i]);
        }
    }
    
    /**
     * Issue #584-swingx: need to clarify null handling.
     * 
     * here: picker formatter must protect itself against 
     *   null formats.
     * 
     */
    @Test
    public void testPickerFormatterUnsafeGetFormats() {
        DatePickerFormatter picker = new DatePickerFormatter();
        DateFormat[] formats = picker.getFormats();
        formats[0] = null;
        try {
            picker.valueToString(new Date());
        } catch (ParseException e) {
            // doc'ed - but there is no parsing involved?
        }
        // other exceptions are unexpected ...
    }



    /**
     * Issue #584-swingx: need to clarify null handling.
     * 
     * here: picker formatter must protect itself against 
     *   empty formats.
     * 
     */
    @Test
    public void testPickerFormatterEmptyValueToString() {
        DatePickerFormatter picker = new DatePickerFormatter(
                new DateFormat[0]);
        try {
            picker.valueToString(new Date());
        } catch (ParseException e) {
            // doc'ed - but there is no parsing involved?
        }
        // other exceptions are unexpected ...
    }

    /**
     * Issue #584-swingx: need to clarify null handling.
     * 
     * here: picker formatter must protect itself against empty formats.
     * 
     */
    @Test
    public void testPickerFormatterEmptyStringToValue() {
        DatePickerFormatter picker = new DatePickerFormatter(new DateFormat[0]);
        try {
            picker.stringToValue("unparseble");
        } catch (ParseException e) {
            // expected
        }
        // other exceptions are unexpected ...
    }
    
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

}
