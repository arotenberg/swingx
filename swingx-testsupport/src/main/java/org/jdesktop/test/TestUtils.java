/*
 * TestUtils.java
 *
 * Created on October 31, 2006, 9:08 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import junit.framework.Assert;

/**
 * Extends assert to get all the ease-of-use assert methods
 * @author rbair
 */
public final class TestUtils extends Assert {
    private static final Logger LOG = Logger.getLogger(TestUtils.class
            .getName());
    private TestUtils() {}
    
    public static void assertContainsType(Object[] objects, Class<?> clazz, int count) {
        if (objects.length == 0 && count == 0) return;
        assertTrue("not enough elements: expected == " + count 
                +" but was == " + objects.length, count <= objects.length);
        int found = 0;
        for (Object object : objects) {
            if (clazz.isAssignableFrom(object.getClass())) {
                found++;
            }
        };
        assertEquals("unexpected number of elements of type " + clazz, count, found);
    }
    
    /**
     * Asserts the last received propertyChangeEvent of the 
     * report against the expected values.
     * 
     * @param report the PropertyReport which received the event
     * @param source the expected event source
     * @param property the expected name of the property
     * @param oldValue the expected old value 
     * @param newValue the expected new value
     */
    public static void assertPropertyChangeEvent(PropertyChangeReport report, 
            Object source, String property, Object oldValue, Object newValue) {
        assertPropertyChangeEvent(report, property, oldValue, newValue);
        assertEquals("event source", source, report.getLastSource());
    }

    /**
     * Asserts the last received propertyChangeEvent of the 
     * report against the expected values.
     * 
     * @param report the PropertyReport which received the event
     * @param property the expected name of the property
     * @param oldValue the expected old value 
     * @param newValue the expected new value
     */
    public static void assertPropertyChangeEvent(PropertyChangeReport report, String property, Object oldValue, Object newValue) {
        if (report.getEventCount() > 1) {
            LOG.info("events: " + report.getEventNames());
        }
        assertEquals("exactly one event", 1, report.getEventCount());
        assertEquals("property", property, report.getLastProperty());
        assertEquals("last old value", oldValue, report.getLastOldValue());
        assertEquals("last new value", newValue, report.getLastNewValue());
    }

    /**
     * Asserts the last received propertyChangeEvent of the 
     * report against the expected values.
     * 
     * @param report the PropertyReport which received the event
     * @param property the expected name of the property
     * @param oldValue the expected old value 
     * @param newValue the expected new value
     * @param single flag to denote if we expect one event only
     */
    public static void assertPropertyChangeEvent(PropertyChangeReport report, 
            String property, Object oldValue, Object newValue, boolean single) {
        if (report.getEventCount() > 1) {
            LOG.info("events: " + report.getEventNames());
        }
        if (single) {
            assertEquals("exactly one event", 1, report.getEventCount());
            assertEquals("property", property, report.getLastProperty());
            assertEquals("last old value", oldValue, report.getLastOldValue());
            assertEquals("last new value", newValue, report.getLastNewValue());
        } else {
            assertEquals("one event of property " + property, 1, report.getEventCount(property));
            assertEquals("old property", oldValue, report.getLastOldValue(property));
            assertEquals("new property", newValue, report.getLastNewValue(property));
        }
    }

    /**
     * Asserts the last received propertyChangeEvent of the 
     * report against the expected values.
     * 
     * @param report the PropertyReport which received the event
     * @param property the expected name of the property
     * @param oldValue the expected old value 
     * @param newValue the expected new value
     * @param single flag to denote if we expect one event only
     * @param verifyArrayItems check array items one by one rather then whole arrays
     */
    public static void assertPropertyChangeEvent(PropertyChangeReport report, 
            String property, Object oldValue, Object newValue, boolean single, boolean verifyArrayItems) {
        if (report.getEventCount() > 1) {
            LOG.info("events: " + report.getEventNames());
        }
        if (single) {
            assertEquals("exactly one event", 1, report.getEventCount());
            assertEquals("property", property, report.getLastProperty());
            if (verifyArrayItems && oldValue != null && oldValue.getClass().isArray()) {
                List l1 = Arrays.asList((Object[]) oldValue);
                List l2 = Arrays.asList((Object[]) report.getLastOldValue());
                assertEquals("last old value", l1.size(), l2.size());
                for (int i = 0; i < l1.size();i++) {
                    assertEquals("last old value", l1.get(i), l2.get(i));
                }
            } else {
                assertEquals("last old value", oldValue, report.getLastOldValue());
            }
            if (verifyArrayItems && newValue != null && newValue.getClass().isArray()) {
                List l1 = Arrays.asList(newValue);
                List l2 = Arrays.asList(report.getLastNewValue());
                assertEquals("last new value", l1.size(), l2.size());
                for (int i = 0; i < l1.size();i++) {
                    assertEquals("last new value", l1.get(i), l2.get(i));
                }
            } else {
                assertEquals("last new value", newValue, report.getLastNewValue());
            }
        } else {
            assertEquals("one event of property " + property, 1, report.getEventCount(property));
            if (verifyArrayItems && oldValue != null && oldValue.getClass().isArray()) {
                List l1 = Arrays.asList((Object[]) oldValue);
                List l2 = Arrays.asList((Object[]) report.getLastOldValue(property));
                assertEquals("old value", l1.size(), l2.size());
                for (int i = 0; i < l1.size();i++) {
                    assertEquals("old value", l1.get(i), l2.get(i));
                }
            } else {
                assertEquals("old property " + property, oldValue, report.getLastOldValue(property));
            }
            if (verifyArrayItems && newValue != null && newValue.getClass().isArray()) {
                Collection l1 = newValue instanceof Collection ? (Collection) newValue : Arrays.asList((Object[])newValue);
                Collection l2 = report.getLastNewValue(property) instanceof Collection ? (Collection) report.getLastNewValue(property) : Arrays.asList((Object[]) report.getLastNewValue(property));
                assertEquals("new value of property " + property, l1.size(), l2.size());
                int index = 0;
                for (Iterator i1 = l1.iterator(), i2 = l2.iterator(); i1.hasNext() && i2.hasNext(); ) {
                    Object o1 = i1.next(); 
                    Object o2 = i2.next(); 
//                    if (o1 instanceof Date) {
//                        o1 = ((Date) o1).getTime();
//                        o2 = ((Date) o2).getTime();
//                    }
                    assertEquals("new value [" + index++ + "] of property " + property, o1, o2);
                }
            } else {
                assertEquals("new value of property " + property, newValue, report.getLastNewValue(property));
            }
        }
    }

    /**
     * Asserts the last received propertyChangeEvent of the 
     * report against the expected values (arrays).
     * 
     * @param report the PropertyReport which received the event
     * @param property the expected name of the property
     * @param oldValue the expected old aray value 
     * @param newValue the expected new array value
     */
    public static void assertPropertyChangeEvent(PropertyChangeReport report, String property, Object[] oldValue, Object[] newValue) {
        if (report.getEventCount() > 1) {
            LOG.info("events: " + report.getEventNames());
        }
        assertEquals("exactly one event", 1, report.getEventCount());
        assertEquals("property", property, report.getLastProperty());
        assertTrue("last old array value", Arrays.equals(oldValue, (Object[]) report.getLastOldValue()));
        assertTrue("last new array value", Arrays.equals(newValue, (Object[])report.getLastNewValue()));
    }
}
