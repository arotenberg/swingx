/*
 * $Id: XLocalizeTest.java 3924 2011-02-22 11:39:58Z kleopatra $
 *
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx;

import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.JComponent;

import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.plaf.LookAndFeelAddons;
import org.jdesktop.swingx.plaf.UIManagerExt;
import org.jdesktop.swingx.search.PatternModel;
import org.jdesktop.test.PropertyChangeReport;
import org.jdesktop.test.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test to expose known issues around <code>Locale</code> setting.
 * 
 * Ideally, there would be at least one failing test method per open
 * Issue in the issue tracker. Plus additional failing test methods for
 * not fully specified or not yet decided upon features/behaviour.
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class XLocalizeTest extends InteractiveTestCase {
    @SuppressWarnings("all")
    private static final Logger LOG = Logger.getLogger(XLocalizeTest.class
            .getName());
    private static final Locale A_LOCALE = Locale.FRENCH;
    private static final Locale OTHER_LOCALE = Locale.GERMAN;


    private Locale originalLocale;
    // test scope is static anyway...
    static {
        // force the addon to load
        LookAndFeelAddons.getAddon();
    }
    public static void main(String[] args) {
//      setSystemLF(true);
      XLocalizeTest test = new XLocalizeTest();
      try {
        test.runInteractiveTests();
//          test.runInteractiveTests("interactive.*TwoTable.*");
      } catch (Exception e) {
          System.err.println("exception when executing interactive tests:");
          e.printStackTrace();
      }

    }

    @Override
    protected void setUp() throws Exception {
        originalLocale = Locale.getDefault();
        super.setUp();
    }
    
    

    @Override
    protected void tearDown() throws Exception {
        Locale.setDefault(originalLocale);
        super.tearDown();
    }

    /**
     * Issue #1394-swingx: missing properties for _en
     * 
     * fix - added empty properties file named swingx_en
     */
    @Test
    public void testEnglishLocaleSwingX() {
        String close = UIManagerExt.getString("Search.close", Locale.US);
        assertEquals("Close", close);
    }
    
    /**
     * Issue #1394-swingx: missing properties for _en
     * 
     * fix - added empty properties file named TipOfTheDay_en
     */
    @Test
    public void testEnglishLocaleTipOfTheDay() {
        new JXTipOfTheDay();
        String close = UIManagerExt.getString("TipOfTheDay.closeText", Locale.US);
        assertEquals("Close", close);
    }
    
    
    /**
     * Issue #1394-swingx: missing properties for _en
     * 
     * fix - added empty properties file named LoginPane_en
     */
    @Test
    public void testEnglishLogin() {
        new JXLoginPane();
        String close = UIManagerExt.getString("JXLoginPane.cancelString", Locale.US);
        assertEquals("Cancel", close);
    }
    
    /**
     * Issue #1394-swingx: missing properties for _en
     * 
     * fix - added empty properties file named ErrorPane_en
     */
    @Test
    public void testEnglishError() {
        new JXErrorPane();
        String close = UIManagerExt.getString("JXErrorPane.ok_button_text", Locale.US);
        assertEquals("Close", close);
    }
    
    
    /**
     * Issue #1394-swingx: missing properties for _en
     * 
     * fix - added empty properties file named SearchField_en
     */
    @Test
    public void testEnglishSearchFile() {
        new JXSearchField();
        String close = UIManagerExt.getString("SearchField.prompt", Locale.US);
        assertEquals("Search", close);
    }
    

    /**
     * test correct PropertyChangeNotification: must fire after
     * all internal state is set ... dooohhh.
     * 
     * Here: test JXDialog.
     */
    /**
     * test correct PropertyChangeNotification: must fire after
     * all internal state is set ...
     * 
     * Here: test FindPanel
     */
    @Test
    public void testLocaleDialogPropertyNotificationInListener() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run localeDialogPropertyNotificationInListener - headless environment");
            return;
        }
        final String prefix = PatternModel.SEARCH_PREFIX;
        final JXFindPanel findPanel = new JXFindPanel();
        final JXDialog dialog = new JXDialog(findPanel);
        final String titleKey = AbstractPatternPanel.SEARCH_TITLE;
        // JW: arrrgghh ... dirty! Consequence of dirty initialization 
        // of AbstractPatternPanel subclasses ...
        findPanel.addNotify();
        String name = dialog.getTitle();
        String uiValue = UIManagerExt.getString(prefix + titleKey, findPanel
                .getLocale());
        // sanity
        assertNotNull(uiValue);
        assertEquals(name, uiValue);
        final Locale alternative = getAlternativeLocale(dialog);
        PropertyChangeListener report = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                // sanity
                // wrong assumption: find widgets name is changed as well
//                assertTrue("locale property changed, instead: " + evt.getPropertyName(), "locale".equals(evt.getPropertyName()));
                if (!"locale".equals(evt.getPropertyName())) return;
                String altUIValue = UIManagerExt.getString(prefix + titleKey,
                        alternative);
                String altName = dialog.getTitle();
                assertEquals("name must be updated before fire propertyChange", 
                        altUIValue, altName);

                
            }};
        dialog.addPropertyChangeListener(report);
        PropertyChangeReport r = new PropertyChangeReport();
        dialog.addPropertyChangeListener(r);
        dialog.setLocale(alternative);
        // sanity: guarantee that we got a locale change notification
        assertEquals(1, r.getEventCount("locale"));
    }


    /**
     * test correct PropertyChangeNotification: must fire after
     * all internal state is set ... dooohhh.
     * 
     * Here: test FindBar.
     */
    @Test
    public void testLocaleFindBarPropertyNotificationInListener() {
        final String prefix = PatternModel.SEARCH_PREFIX;
        final JXFindBar findPanel = new JXFindBar();
        final String actionCommand = JXFindBar.FIND_NEXT_ACTION_COMMAND;
        // JW: arrrgghh ... dirty! Consequence of dirty initialization 
        // of AbstractPatternPanel subclasses ...
        findPanel.addNotify();
        
        final Action action = findPanel.getActionMap().get(actionCommand);
        String name = (String) action.getValue(Action.NAME);
        String uiValue = UIManagerExt.getString(prefix + actionCommand, findPanel
                .getLocale());
        // sanity
        assertNotNull(uiValue);
        assertEquals(name, uiValue);
        final Locale alternative = getAlternativeLocale(findPanel);
        PropertyChangeListener report = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                // sanity
//                assertTrue("locale property changed", "locale".equals(evt.getPropertyName()));
                if (!"locale".equals(evt.getPropertyName())) return;
                String altUIValue = UIManagerExt.getString(prefix + actionCommand,
                        alternative);
                String altName = (String) action.getValue(Action.NAME);
                assertEquals("name must be updated before fire propertyChange", 
                        altUIValue, altName);

                
            }};
            PropertyChangeReport r = new PropertyChangeReport();
            findPanel.addPropertyChangeListener(r);
            findPanel.setLocale(alternative);
            assertEquals(1, r.getEventCount("locale"));
    }


    /**
     * test correct PropertyChangeNotification: must fire after
     * all internal state is set ...
     * 
     * Here: test FindPanel
     */
    @Test
    public void testLocaleFindPanelPropertyNotificationInListener() {
        final String prefix = PatternModel.SEARCH_PREFIX;
        final JXFindPanel findPanel = new JXFindPanel();
        final String actionCommand = AbstractPatternPanel.MATCH_ACTION_COMMAND;
        // JW: arrrgghh ... dirty! Consequence of dirty initialization 
        // of AbstractPatternPanel subclasses ...
        findPanel.addNotify();
        
        final Action action = findPanel.getActionMap().get(actionCommand);
        String name = (String) action.getValue(Action.NAME);
        String uiValue = UIManagerExt.getString(prefix + actionCommand, findPanel
                .getLocale());
        // sanity
        assertNotNull(uiValue);
        assertEquals(name, uiValue);
        final Locale alternative = getAlternativeLocale(findPanel);
        PropertyChangeListener report = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                // sanity
                // wrong assumption: find widgets name is changed as well
//                assertTrue("locale property changed, instead: " + evt.getPropertyName(), "locale".equals(evt.getPropertyName()));
                if (!"locale".equals(evt.getPropertyName())) return;
                String altUIValue = UIManagerExt.getString(prefix + actionCommand,
                        alternative);
                String altName = (String) action.getValue(Action.NAME);
                assertEquals("name must be updated before fire propertyChange", 
                        altUIValue, altName);

                
            }};
        findPanel.addPropertyChangeListener(report);
        PropertyChangeReport r = new PropertyChangeReport();
        findPanel.addPropertyChangeListener(r);
        findPanel.setLocale(alternative);
        assertEquals(1, r.getEventCount("locale"));
    }

    /**
     * test correct PropertyChangeNotification: must fire after
     * all internal state is set ... dooohhh.
     */
    @Test
    public void testLocaleTablePropertyNotification() {
        String prefix = "JXTable.";
        JXTable table = new JXTable(10, 2);
        String actionCommand = JXTable.HORIZONTALSCROLL_ACTION_COMMAND;
        
        Action action = table.getActionMap().get(actionCommand);
        String name = (String) action.getValue(Action.NAME);
        String uiValue = UIManagerExt.getString(prefix + actionCommand, table
                .getLocale());
        // sanity
        assertNotNull(uiValue);
        assertEquals(name, uiValue);
        Locale old = table.getLocale();
        Locale alternative = getAlternativeLocale(table);
        PropertyChangeReport report = new PropertyChangeReport();
        table.addPropertyChangeListener(report);
        table.setLocale(alternative);
        TestUtils.assertPropertyChangeEvent(report, "locale", old, alternative);
    }

    /**
     * test correct PropertyChangeNotification: must fire after
     * all internal state is set ... 
     * 
     * Here: test JXTable.
     */
    @Test
    public void testLocaleTablePropertyNotificationInListener() {
        final String prefix = "JXTable.";
        final JXTable table = new JXTable(10, 2);
        final String actionCommand = JXTable.HORIZONTALSCROLL_ACTION_COMMAND;
        
        final Action action = table.getActionMap().get(actionCommand);
        String name = (String) action.getValue(Action.NAME);
        String uiValue = UIManagerExt.getString(prefix + actionCommand, table
                .getLocale());
        // sanity
        assertNotNull(uiValue);
        assertEquals(name, uiValue);
        final Locale alternative = getAlternativeLocale(table);
        PropertyChangeListener report = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                // sanity
                assertTrue("locale property changed", "locale".equals(evt.getPropertyName()));
                String altUIValue = UIManagerExt.getString(prefix + actionCommand,
                        alternative);
                String altName = (String) action.getValue(Action.NAME);
                assertEquals("name must be updated before fire propertyChange", 
                        altUIValue, altName);

                
            }};
        table.addPropertyChangeListener(report);
        table.setLocale(alternative);
    }

    private Locale getAlternativeLocale(final Component table) {
        Locale alternative = OTHER_LOCALE;
        if (alternative.getLanguage().equals(table.getLocale().getLanguage())) {
            alternative = A_LOCALE;
        }
        return alternative;
    }


    /**
     * Issue #635-swingx: find widgets must support dynamic localization
     * Here: test findPanel's actions (incomplete ..)
     */
    @Test
    public void testLocaleFindPanel() {
        JXFindPanel panel = new JXFindPanel();
        // JW: arrrgghh ... dirty! Consequence of dirty initialization 
        // of AbstractPatternPanel subclasses ...
        panel.addNotify();
        String prefix = PatternModel.SEARCH_PREFIX;
        assertLocaleActionUpdate(panel, prefix, AbstractPatternPanel.MATCH_ACTION_COMMAND);
        assertLocaleActionUpdate(panel, prefix, JXFindPanel.FIND_NEXT_ACTION_COMMAND);
        assertLocaleActionUpdate(panel, prefix, JXFindPanel.FIND_PREVIOUS_ACTION_COMMAND);
    }
    
    /**
     * Issue #459-swingx: JXTable setLocale doesn't update localized column
     * control properties. <p>
     * 
     * Pass/fail expectation:
     * <ul>
     * <li> fails always with jdk5 independent of LookAndFeelAddon resource
     * bundle registration.
     * <li> fails with jdk6 and LookAndFeelAddon copy resource bundle values.
     * <li> passes with jdk6 and LookAndFeelAddon addResourceBundle.
     * </ul>
     */
    @Test
    public void testLocaleColumnControl() {
       String prefix = "JXTable.";
       JXTable table = new JXTable(10, 2);
       assertLocaleActionUpdate(table, prefix, JXTable.HORIZONTALSCROLL_ACTION_COMMAND);
       assertLocaleActionUpdate(table, prefix, JXTable.PACKALL_ACTION_COMMAND);
       assertLocaleActionUpdate(table, prefix, JXTable.PACKSELECTED_ACTION_COMMAND);
    }

    private void assertLocaleActionUpdate(JComponent table, String prefix, String actionCommand) {
        Action action = table.getActionMap().get(actionCommand);
        String name = (String) action.getValue(Action.NAME);
        String uiValue = UIManagerExt.getString(prefix + actionCommand, table
                .getLocale());
        // sanity
        assertNotNull(uiValue);
        assertEquals(name, uiValue);
        Locale alternative = OTHER_LOCALE;
        if (alternative.getLanguage().equals(table.getLocale().getLanguage())) {
            alternative = A_LOCALE;
        }
        table.setLocale(alternative);
        String altUIValue = UIManagerExt.getString(prefix + actionCommand,
                table.getLocale());
        // sanity
        assertNotNull(altUIValue);
        // sanity to track unexpected failure during refactoring
        assertFalse("new uiValue  must be different: " + uiValue + "/"
                + altUIValue, uiValue.equals(altUIValue));
        String altName = (String) action.getValue(Action.NAME);
        // here are the real asserts
        assertFalse("new action name must be different: " + name + "/"
                + altName, name.equals(altName));
        assertEquals(altName, altUIValue);
    }
    
    /**
     * Issue #466-swingx: locale-dependent values not accessible. This looks
     * like a side-effect of the first go on #159-swingx (not all values of
     * resourceBundle available).
     * <p>
     * 
     * Could be core problem (in jdk5, fixed in jdk6) around classloader and
     * ResourceBundle: <a
     * href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4834404">
     * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4834404 </a>
     * 
     * 
     */
    @Test
    public void testGetLocaleUIDefaults() {
        String key = "JXTable.column.packAll";
        Object alternativeValue = UIManagerExt.getString(key, OTHER_LOCALE);
        // sanity - the value must be available
        assertNotNull(alternativeValue);
        Object defaultValue = UIManagerExt.getString(key, A_LOCALE);
        // sanity - the value must be available
        assertNotNull(defaultValue);
        assertFalse("values must be different: " + defaultValue + "/" + alternativeValue, defaultValue.equals(alternativeValue));
    }
    
    /**
     * Issue #459-swingx: columnControl properties not updated on locale setting.
     *
     */
    public void interactiveLocaleColumnControl() {
        final JXTable table = new JXTable(10, 4);
        table.setColumnControlVisible(true);
        table.getColumnExt(0).setTitle(table.getLocale().getLanguage());
        Action toggleLocale = new AbstractActionExt("toggleLocale") {

            @Override
            public void actionPerformed(ActionEvent e) {
                Locale old = table.getLocale();
                table.setLocale(old == A_LOCALE ? OTHER_LOCALE : A_LOCALE);
                table.getColumnExt(0).setTitle(table.getLocale().getLanguage());
                
            }
            
        };
        JXFrame frame = wrapWithScrollingInFrame(table, "toggle locale on table - column control not updated");
        addAction(frame, toggleLocale);
        frame.setVisible(true);
    }
    
    /**
     * Issue #459-swingx: columnControl properties not updated on locale setting.
     *
     * 
     */
    public void interactiveLocaleColumnControlTwoTables() {
        final JXTable table = new JXTable(10, 4);
        table.setColumnControlVisible(true);
        table.getColumnExt(0).setTitle(table.getLocale().getLanguage());
        JXTable other = new JXTable(10, 4);
        other.setColumnControlVisible(true);
        other.setLocale(A_LOCALE);
        other.getColumnExt(0).setTitle(other.getLocale().getLanguage());
        JXFrame frame = wrapWithScrollingInFrame(table, other, "different locals: de <--> vs fr");
        addSearchModeToggle(frame);
        addMessage(frame, "Find panel/bar should be localized per-table");
        frame.pack();
        frame.setVisible(true);
    }


}
