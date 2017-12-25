/*
 * $Id: XLocalizeIssues.java 3665 2010-04-22 09:55:53Z kleopatra $
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

import java.awt.event.ActionEvent;
import java.util.Locale;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.plaf.LookAndFeelAddons;
import org.junit.After;
import org.junit.Before;
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
public class XLocalizeIssues extends InteractiveTestCase {
    @SuppressWarnings("all")
    private static final Logger LOG = Logger.getLogger(XLocalizeIssues.class
            .getName());
    private static final Locale A_LOCALE = Locale.FRANCE;
    private static final Locale OTHER_LOCALE = Locale.GERMANY;

    private static Locale defaultLocale = Locale.getDefault();
    
    private Locale originalLocale;
    private Locale alternativeLocale;
    
    // test scope is static anyway...
    static {
        // force the addon to load
        LookAndFeelAddons.getAddon();
    }
    public static void main(String[] args) {
//      setSystemLF(true);
      XLocalizeIssues test = new XLocalizeIssues();
      try {
        test.runInteractiveTests();
//          test.runInteractiveTests("interactive.*TwoTable.*");
      } catch (Exception e) {
          System.err.println("exception when executing interactive tests:");
          e.printStackTrace();
      }

    }

//    @BeforeClass
//    public static void classSetUp() {
//        defaultLocale = Locale.getDefault();
//    }
    
    @Override
    @Before
    public void setUp() throws Exception {
        originalLocale = defaultLocale;
        alternativeLocale = OTHER_LOCALE.equals(originalLocale) ? A_LOCALE : OTHER_LOCALE;
        super.setUp();
    }
    
    

    @Override
    @After
    public void tearDown() throws Exception {
        Locale.setDefault(defaultLocale);
        UIManager.getDefaults().setDefaultLocale(defaultLocale);
        JComponent.setDefaultLocale(defaultLocale);
        super.tearDown();
    }

    @Test
    public void testComponentDefault() {
        Locale.setDefault(alternativeLocale);
        UIManager.getDefaults().setDefaultLocale(alternativeLocale);
        JComponent.setDefaultLocale(Locale.getDefault());
    }
    
    @Test
    public void testDefaultLocales() {
        assertDefaults();
    }
    
    
    private void assertDefaults() {
        assertEquals(defaultLocale, Locale.getDefault());
        assertEquals(defaultLocale, UIManager.getDefaults().getDefaultLocale());
        assertEquals(defaultLocale, JComponent.getDefaultLocale());
    }
    /**
     * similar to Issue #459-swingx: errorPane properties not updated on setLocale.<p>
     * 
     * Seem to have some unrelated open issues ...
     *
     */
    public void interactiveErrorPane() {
        final JXTable table = new JXTable(10, 3);
        table.setColumnControlVisible(true);
        table.getColumnExt(0).setTitle(Locale.getDefault().getLanguage());
        final JXFrame frame = wrapWithScrollingInFrame(table,
                "ErrorPane and default locale?");
        Action toggleLocale = new AbstractActionExt("toggleLocale") {

            public void actionPerformed(ActionEvent e) {
                Locale old = Locale.getDefault();
                Locale.setDefault(old == A_LOCALE ? OTHER_LOCALE
                        : A_LOCALE);
                // make sure newly created comps get the new locale by default
                // Note: this does not effect components which are already
                // created
                JComponent.setDefaultLocale(Locale.getDefault());
                table.getColumnExt(0).setTitle(
                        Locale.getDefault().getLanguage());
                table.setLocale(Locale.getDefault());

            }

        };
        final JXErrorPane errorPane = new JXErrorPane();
        // work around issue #??-swingx: errorPane must cope with null errorInfo.
        errorPane.setErrorInfo(new ErrorInfo("title", "xxxx-yyy", null, null, null, null, null));
        Action open = new AbstractActionExt("open error") {

            public void actionPerformed(ActionEvent e) {
                // we are fine if the chooser is re-created in each call
                // _and_ the JComponent.defaultLocale is kept in synch with
                // with Locale.getDefault()
                // JFileChooser chooser = new JFileChooser();
                // otherwise we have to update the chooser's locale manually
                if (!Locale.getDefault().equals(errorPane.getLocale())) {
                    errorPane.setLocale(Locale.getDefault());
                    // need to explicitly trigger re-install to pick up the new
                    // locale-dependent state
                    // this throws java error - UIDefaults.getUI() failed ... ??
                    errorPane.updateUI();
                }
                JXErrorPane.showDialog(frame, errorPane);
            }

        };
        addAction(frame, toggleLocale);
        addAction(frame, open);
        frame.setVisible(true);
    }

//------------------ core components for comparison.
    
    /**
     * Trying to understand how core Swing handles <code>Locale</code>
     * in <code>JFileChooser</code>.
     * <p>
     * 
     * Not very intuitive: the default locale for all J** is controlled by the
     * static JComponent.getDefaultLocale() which is automatically set the very
     * first time any component is instantiated to the Locale.getDefault().
     * Changing the Locale-defined default later on has no effect, must do so
     * explicitly
     * <p>
     * 
     * Changing a component's Locale after instantiating is not trivial as well:
     * need to explicitly call updateUI to trigger the ui-delegate into action.
     * <p>
     * 
     * <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4423439">
     * Core bug JFileChooser </a>
     * 
     */
    public void interactiveFileChooser() {
        final JXTable table = new JXTable(10, 3);
        table.getColumnExt(0).setTitle(Locale.getDefault().getLanguage());
        final JXFrame frame = wrapWithScrollingInFrame(table,
                "FileChooser and default locale?");
        Action toggleLocale = new AbstractActionExt("toggleLocale") {

            public void actionPerformed(ActionEvent e) {
                Locale old = Locale.getDefault();
                Locale.setDefault(old == A_LOCALE ? OTHER_LOCALE
                        : A_LOCALE);
                // make sure newly created comps get the new locale by default
                // Note: this does not effect components which are already
                // created
                JComponent.setDefaultLocale(Locale.getDefault());
                // as of 1.4 the defaults defaultLocale is for backward compatibility
                // only - newer delegates should use the getters with the local param
                // so shouldn't need to do this
//                UIManager.getDefaults().setDefaultLocale(Locale.getDefault());
                table.getColumnExt(0).setTitle(
                        Locale.getDefault().getLanguage());

            }

        };
        final JFileChooser chooser = new JFileChooser();
        Action open = new AbstractActionExt("open") {

            public void actionPerformed(ActionEvent e) {
                // we are fine if the chooser is re-created in each call
                // _and_ the JComponent.defaultLocale is kept in synch with
                // with Locale.getDefault()
                // JFileChooser chooser = new JFileChooser();
                // otherwise we have to update the chooser's locale manually
                if (!Locale.getDefault().equals(chooser.getLocale())) {
                    chooser.setLocale(Locale.getDefault());
                    // need to explicitly trigger re-install to pick up the new
                    // locale-dependent state
//                    chooser.updateUI();
                }
                chooser.showOpenDialog(frame);
            }

        };
        addAction(frame, toggleLocale);
        addAction(frame, open);
        frame.setVisible(true);
    }
    /**
     * Trying to understand how core Swing handles <code>Locale</code>
     * switching in <code>JOptionPane</code>.
     * <p>
     * 
     * Not very intuitive: the default locale for all J** is controlled by the
     * static JComponent.getDefaultLocale() which is automatically set the very
     * first time any component is instantiated to the Locale.getDefault().
     * Changing the Locale-defined default later on has no effect.
     * <p>
     * 
     * <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4884480 ">
     * Core bug JOptionPane</a>
     * 
     */
    public void interactiveOptionPane() {
        final JXTable table = new JXTable(10, 3);
        table.getColumnExt(0).setTitle(Locale.getDefault().getLanguage());
        final JXFrame frame = wrapWithScrollingInFrame(table,
                "JOptionPane and default locale?");
        Action toggleLocale = new AbstractActionExt("toggleLocale") {

            public void actionPerformed(ActionEvent e) {
                Locale old = Locale.getDefault();
                Locale.setDefault(old == A_LOCALE ? OTHER_LOCALE
                        : A_LOCALE);
                // make sure newly created comps get the new locale by default
                // Note: this does not effect components which are already
                // created
                JComponent.setDefaultLocale(Locale.getDefault());
                table.getColumnExt(0).setTitle(
                        Locale.getDefault().getLanguage());

            }

        };
        Action open = new AbstractActionExt("open") {

            public void actionPerformed(ActionEvent e) {
                // title not localized ...
                JOptionPane.showConfirmDialog(frame, "abcdefghijklmnopqrstxyz - 123456789");

            }

        };
        addAction(frame, toggleLocale);
        addAction(frame, open);
        frame.setVisible(true);
    }
    
    /**
     * do nothing except make the testrunner happy.
     */
    public void testDummy() {
        
    }
}
