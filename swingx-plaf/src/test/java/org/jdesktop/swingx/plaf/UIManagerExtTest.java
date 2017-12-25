/*
 * $Id: UIManagerExtTest.java 4075 2011-11-11 19:57:07Z kschaefe $
 * 
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.plaf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.util.Locale;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests the functionality of {@code UIManagerExt}.
 * 
 * @author Karl George Schaefer
 */
public class UIManagerExtTest {
    private static final Logger LOG = Logger.getLogger(UIManagerExtTest.class
            .getName());

    /**
     * {@inheritDoc}
     */
    @Before
    public void setUp() {
        UIManagerExt.addResourceBundle("org.jdesktop.swingx.plaf.UIManagerExtTest");
    }
    
    /**
     * Ensure that the {@code getSafeXXX} methods always return
     * {@code UIResource}.
     */
    @Test
    public void testGetSafeMethodsReturnUIResource() {
        assertTrue(UIManagerExt.getSafeBorder("", BorderFactory
                .createEmptyBorder()) instanceof UIResource);
        assertTrue(UIManagerExt.getSafeColor("", Color.RED) instanceof UIResource);
        assertTrue(UIManagerExt.getSafeDimension("", new Dimension()) instanceof UIResource);
        assertTrue(UIManagerExt.getSafeFont("", new Font("Dialog", Font.BOLD,
                12)) instanceof UIResource);
        assertTrue(UIManagerExt.getSafeIcon("", new ImageIcon()) instanceof UIResource);
        assertTrue(UIManagerExt.getSafeInsets("", new Insets(0, 0, 0, 0)) instanceof UIResource);
    }

    /**
     * test that we get a boolean from the localized resource.
     */
    @Test
    public void testGetBooleanFromResource() {
        String key = "goodBoolean";
        Locale locale = JComponent.getDefaultLocale();
        String columnString = UIManagerExt.getString(key, locale);
        if (columnString == null) {
            LOG.info("cant run test - no resource found for key: " + key);
            return;
        }
        Object value = UIManagerExt.getBoolean(key, locale);
        assertNotNull(value);
        assertEquals(Boolean.valueOf(columnString), value);
    }

    /**
     * test that a value in the UIManager is not overwritten.
     */
    @Test
    public void testGetBooleanUIManagerFirst() {
        String key = "goodBoolean";
        Locale locale = JComponent.getDefaultLocale();
        String columnString = UIManagerExt.getString(key, locale);
        if (columnString == null) {
            LOG.info("cant run test - no resource found for key: " + key);
            return;
        }
        
        try {
            Boolean temp = true;
            UIManager.put(key, temp);
            Object value = UIManagerExt.getBoolean(key, locale);
            assertNotNull(value);
            assertEquals(temp, value);
        } finally {
            // restore uimanager
            UIManager.put(key, null);
        }
    }

    /**
     * test that the getBoolean doesn't choke on non-Booleans
     */
    @Test
    public void testGetBooleanLenientWithUnparseable() {
        String key = "badBoolean";
        Locale locale = JComponent.getDefaultLocale();
        String columnString = UIManagerExt.getString(key, locale);
        if (columnString == null) {
            LOG.info("cant run test - no resource found for key: " + key);
            return;
        }
        
        UIManagerExt.getBoolean(key, locale);
    }
    
    /**
     * test that we get a boolean from the localized resource.
     */
    @Test
    public void testGetColorFromResource() {
        String key = "goodColor";
        Locale locale = JComponent.getDefaultLocale();
        String columnString = UIManagerExt.getString(key, locale);
        if (columnString == null) {
            LOG.info("cant run test - no resource found for key: " + key);
            return;
        }
        Object value = UIManagerExt.getColor(key, locale);
        assertNotNull(value);
        assertEquals(Color.decode(columnString), value);
    }
    
    /**
     * test that a value in the UIManager is not overwritten.
     */
    @Test
    public void testGetColorUIManagerFirst() {
        String key = "goodColor";
        Locale locale = JComponent.getDefaultLocale();
        String columnString = UIManagerExt.getString(key, locale);
        if (columnString == null) {
            LOG.info("cant run test - no resource found for key: " + key);
            return;
        }
        
        try {
            Color temp = Color.RED;
            UIManager.put(key, temp);
            Object value = UIManagerExt.getColor(key, locale);
            assertNotNull(value);
            assertEquals(temp, value);
        } finally {
            // restore uimanager
            UIManager.put(key, null);
        }
    }
    
    /**
     * test that the getBoolean doesn't choke on non-Booleans
     */
    @Test
    public void testGetColorLenientWithUnparseable() {
        String key = "badColor";
        Locale locale = JComponent.getDefaultLocale();
        String columnString = UIManagerExt.getString(key, locale);
        if (columnString == null) {
            LOG.info("cant run test - no resource found for key: " + key);
            return;
        }
        
        try {
            Color.decode(columnString);
            LOG.info("cant run test - valid integer: " + columnString);
        } catch (Exception ex) {
            // must not bark
            UIManagerExt.getColor(key, locale);
        }
    }

    //TODO remove or reenable after getFont is finalized
//    /**
//     * test that we get a font from the localized resource.
//     */
//    public void testGetFontFromResource() {
//        String key = "goodFont";
//        Locale locale = JComponent.getDefaultLocale();
//        String columnString = UIManagerExt.getString(key, locale);
//        if (columnString == null) {
//            LOG.info("cant run test - no resource found for key: " + key);
//            return;
//        }
//        Object value = UIManagerExt.getFont(key, locale);
//        assertNotNull(value);
//        assertEquals(Font.decode(columnString), value);
//    }
//    
//    /**
//     * test that a value in the UIManager is not overwritten.
//     */
//    public void testGetFontUIManagerFirst() {
//        String key = "goodFont";
//        Locale locale = JComponent.getDefaultLocale();
//        String columnString = UIManagerExt.getString(key, locale);
//        if (columnString == null) {
//            LOG.info("cant run test - no resource found for key: " + key);
//            return;
//        }
//        
//        try {
//            Font temp = new Font("Dialog", Font.ITALIC, 11);
//            UIManager.put(key, temp);
//            Object value = UIManagerExt.getFont(key, locale);
//            assertNotNull(value);
//            assertEquals(temp, value);
//        } finally {
//            // restore uimanager
//            UIManager.put(key, null);
//        }
//    }
//    
//    /**
//     * test that the getFont doesn't choke on non-Fonts
//     */
//    public void testGetFontLenientWithUnparseable() {
//        String key = "badFont";
//        Locale locale = JComponent.getDefaultLocale();
//        String columnString = UIManagerExt.getString(key, locale);
//        if (columnString == null) {
//            LOG.info("cant run test - no resource found for key: " + key);
//            return;
//        }
//        
//        try {
//            System.err.println(Font.decode(columnString));
//            Font.decode(columnString);
//            LOG.info("cant run test - valid integer: " + columnString);
//        } catch (Exception ex) {
//            // must not bark
//            UIManagerExt.getFont(key, locale);
//        }
//    }
    
    /**
     * test that we get an int from the localized resource.
     */
    @Test
    public void testGetIntFromResource() {
        String key = "goodInt";
        Locale locale = JComponent.getDefaultLocale();
        String columnString = UIManagerExt.getString(key, locale);
        if (columnString == null) {
            LOG.info("cant run test - no resource found for key: " + key);
            return;
        }
        Object value = UIManagerExt.getInt(key, locale);
        assertNotNull(value);
        assertEquals(Integer.decode(columnString), value);
    }

    /**
     * test that a value in the UIManager is not overwritten.
     */
    @Test
    public void testGetIntUIManagerFirst() {
        String key = "goodInt";
        Locale locale = JComponent.getDefaultLocale();
        String columnString = UIManagerExt.getString(key, locale);
        if (columnString == null) {
            LOG.info("cant run test - no resource found for key: " + key);
            return;
        }
        
        try {
            Integer temp = 150;
            UIManager.put(key, temp);
            Object value = UIManagerExt.getInt(key, locale);
            assertNotNull(value);
            assertEquals(temp, value);
        } finally {
            // restore uimanager
            UIManager.put(key, null);
        }
    }

    /**
     * test that the getInt doesn't choke on non-numbers
     */
    @Test
    public void testGetIntLenientWithUnparseable() {
        String key = "badInt";
        Locale locale = JComponent.getDefaultLocale();
        String columnString = UIManagerExt.getString(key, locale);
        if (columnString == null) {
            LOG.info("cant run test - no resource found for key: " + key);
            return;
        }
        try {
            Integer.decode(columnString);
            LOG.info("cant run test - valid integer: " + columnString);
        } catch (Exception ex) {
            // must not bark
            UIManagerExt.getInt(key, locale);
        }
    }
}
