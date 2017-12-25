/*
 * $Id: StringValuesTest.java 3996 2011-04-06 17:21:00Z kleopatra $
 *
 * Copyright 2008 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx.renderer;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import junit.framework.TestCase;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.renderer.StringValues.StringValueUIResource;
import org.jdesktop.swingx.search.PatternModel;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 *
 * @author Karl George Schaefer
 */
@RunWith(JUnit4.class)
public class StringValuesTest extends TestCase {

    @Test
    public void testLocaleSVPrefixNotContained() {
        Map<Object, String> lookup = new HashMap<Object, String>();
        String value = "not contained";
        lookup.put(value, "unreasonableDummyKey");
//        StringValue sv = new LocalizableStringValue(lookup, "prefix.", Locale.GERMANY);
        StringValue sv = new LocalizableStringValue(lookup, "prefix.");
        
        assertEquals(value, sv.getString(value));
    }
    
    @Test
    public void testLocaleSVPrefixContained() {
        Map<Object, String> lookup = new HashMap<Object, String>();
        lookup.put(PatternModel.MATCH_RULE_ENDSWITH, PatternModel.MATCH_RULE_ENDSWITH);
        StringValue sv = new LocalizableStringValue(lookup, PatternModel.SEARCH_PREFIX, Locale.GERMAN);
        assertEquals("endet mit", sv.getString(PatternModel.MATCH_RULE_ENDSWITH));
    }
    
    @Test
    public void testLocaleSVDirectContained() {
        Map<Object, String> lookup = new HashMap<Object, String>();
        lookup.put(PatternModel.MATCH_RULE_ENDSWITH, PatternModel.SEARCH_PREFIX + PatternModel.MATCH_RULE_ENDSWITH);
        StringValue sv = new LocalizableStringValue(lookup,  Locale.GERMAN);
        assertEquals("endet mit", sv.getString(PatternModel.MATCH_RULE_ENDSWITH));
    }
    
    @Test
    public void testLocaleSVNotContained() {
        Map<Object, String> lookup = new HashMap<Object, String>();
        String value = "not contained";
        lookup.put(value, "unreasonableDummyKey");
        StringValue sv = new LocalizableStringValue(lookup);
        assertEquals(value, sv.getString(value));
    }
    
    @Test
    public void testLocaleSVDefaultLocale() {
        Locale old = Locale.getDefault();
        try {
            LocalizableStringValue sv = new LocalizableStringValue(new HashMap<Object, String>());
            assertEquals(old, sv.getLocale());
            Locale other = Locale.CHINESE;
            if (old.equals(other)) {
                other = Locale.ENGLISH;
            }
            Locale.setDefault(other);
            assertEquals(other, sv.getLocale());
        } finally {
            Locale.setDefault(old);
        }
    }
    
    @Test (expected = NullPointerException.class)
    public void testLocaleStringValueConstructor() {
        new LocalizableStringValue(null);
    }
    
    @Test
    public void testUIResource() {
        final String constant = "dummy";
        StringValue sv = new StringValue() {

            @Override
            public String getString(Object value) {
                return constant;
            }
            
        };
        StringValue wrapper = new StringValueUIResource(sv);
        assertEquals(constant, wrapper.getString("whatever"));
    }
    
    @Test (expected = NullPointerException.class)
    public void testUIResourceConstructor() {
        new StringValueUIResource(null);
    }
    
    /**
     * Issue #1273-swingx: Locale-dependent StringValues constants 
     *    not updated on changing default.
     *    
     */
    @Test
    public void testLocaleDateToString() {
        Locale locale = Locale.getDefault();
        try {
            Calendar number = Calendar.getInstance();
            number.set(Calendar.MONTH, Calendar.DECEMBER);
            Locale.setDefault(Locale.GERMAN);
            String german = StringValues.DATE_TO_STRING.getString(number.getTime());
            assertTrue("formatted German " + german, german.indexOf(".") >= 0);
            Locale.setDefault(Locale.US);
            String us = StringValues.DATE_TO_STRING.getString(number.getTime());
            assertTrue("formatted us " + us, us.indexOf("Dec") >= 0);
        } finally {
            Locale.setDefault(locale);
        }
    }
    
    /**
     * Issue #1273-swingx: Locale-dependent StringValues constants 
     *    not updated on changing default.
     */
    @Test
    public void testLocaleNumberToString() {
        Locale locale = Locale.getDefault();
        try {
            float number = 10.5f;
            Locale.setDefault(Locale.US);
            String us = StringValues.NUMBER_TO_STRING.getString(number);
            assertTrue("formatted us " + us, us.indexOf(".") > 0);
            Locale.setDefault(Locale.GERMAN);
            String german = StringValues.NUMBER_TO_STRING.getString(number);
            assertTrue("formatted German " + german, german.indexOf(",") > 0);
        } finally {
            Locale.setDefault(locale);
        }
    }

    
    @Test
    public void testFileNameWithNonFile() {
        Object o = new Object();
        
        assertEquals(StringValues.TO_STRING.getString(o),
                StringValues.FILE_NAME.getString(o));
    }
    
    //not asserting the output of file name just that it isn't empty
    // PENDING JW: don't quite understand what we are testing here - need to update to 
    // TO_STRING as of fixing #972?
    @Test
    public void testFileNameWithFile() throws Exception {
        File f = File.createTempFile("svt", "tmp");
        f.deleteOnExit();
        
        assertNotSame(StringValues.EMPTY.getString(f),
                StringValues.FILE_NAME.getString(f));
    }
    
    @Test
    public void testFileTypeWithNonFile() {
        Object o = new Object();
        
        assertEquals(StringValues.TO_STRING.getString(o),
                StringValues.FILE_TYPE.getString(o));
    }
    
    //not asserting the output of file type just that it isn't empty
    @Test
    public void testFileTypeWithFile() throws Exception {
        File f = File.createTempFile("svt", "tmp");
        f.deleteOnExit();
        
        assertNotSame(StringValues.EMPTY.getString(f),
                StringValues.FILE_TYPE.getString(f));
    }
    
    @BeforeClass
    public static void beforeClass() {
        // force loading of resource files
        new JXTable();
    }
}
