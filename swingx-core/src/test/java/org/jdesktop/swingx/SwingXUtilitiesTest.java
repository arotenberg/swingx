/*
 * $Id: SwingXUtilitiesTest.java 4043 2011-07-19 18:37:02Z kschaefe $
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
package org.jdesktop.swingx;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.awt.Color;
import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JWindow;
import javax.swing.RepaintManager;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.UIResource;

import org.hamcrest.CoreMatchers;
import org.jdesktop.swingx.JXCollapsiblePane.CollapsiblePaneContainer;
import org.jdesktop.swingx.plaf.basic.BasicDatePickerUI;
import org.jdesktop.test.EDTRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Matchers;


/**
 * Contains tests for SwingXUtilities.
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class SwingXUtilitiesTest extends InteractiveTestCase {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(SwingXUtilitiesTest.class.getName());
    public static void main(String args[]) {
        setSystemLF(true);
//        Locale.setDefault(new Locale("es"));
        SwingXUtilitiesTest test = new SwingXUtilitiesTest();
        try {
          test.runInteractiveTests();
//            test.runInteractiveTests("interactive.*Compare.*");
//            test.runInteractiveTests("interactive.*Tree.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }
    
    /**
     * Test doc'ed contract of isUIInstallable.
     */
    @Test
    public void testUIInstallable() {
        assertEquals("null must be uiInstallable ", true, SwingXUtilities.isUIInstallable(null));
        assertUIInstallable(new Color(10, 10, 10));
        assertUIInstallable(new ColorUIResource(10, 10, 10));
    }

    /**
     * @param color
     */
    private void assertUIInstallable(Object color) {
        assertEquals("uiInstallabe must be same ", color instanceof UIResource, SwingXUtilities.isUIInstallable(color));
    }
    
    @Test
    public void testUpdateAllComponentTreeUIs() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run test - headless environment");
            return;
        }
        if (isCrossPlatformLFSameAsSystem()) {
            LOG.info("cannot run test - no safe LFs to toggle");
            return;
        }
        List<RootPaneContainer> toplevels = new ArrayList<RootPaneContainer>();
        for (int i = 0; i < 10; i++) {
            JXFrame frame = new JXFrame();
            toplevels.add(frame);
            toplevels.add(new JDialog(frame));
            toplevels.add(new JWindow(frame));
        }
        // sanity
        if (!UIManager.getLookAndFeel().isNativeLookAndFeel()) {
            LOG.warning("Assumption is to start with native LaF. Found " + UIManager.getLookAndFeel() + " instead.");
        }
        setSystemLF(false);
        SwingXUtilities.updateAllComponentTreeUIs();
        // sanity
        for (RootPaneContainer window : toplevels) {
            JRootPane rootPane = window.getRootPane();
            assertEquals(UIManager.get(rootPane.getUIClassID()),  
                    rootPane.getUI().getClass().getName());
        }
    }



    /**
     * @return boolean indicating if system and 
     *   cross platform LF are different.
     */
    private boolean isCrossPlatformLFSameAsSystem() {
        return UIManager.getCrossPlatformLookAndFeelClassName().equals(
                UIManager.getSystemLookAndFeelClassName());
    }

    
    @Before
    public void setUpJ4() throws Exception {
        setUp();
    }
    
    @After
    public void tearDownJ4() throws Exception {
        tearDown();
    }
    
    
    @Override
    protected void setUp() throws Exception {
        setSystemLF(true);
    }

    @Test(expected = NullPointerException.class)
    public void testGetTranslucentRepaintManagerWithNull() {
        SwingXUtilities.getTranslucentRepaintManager(null);
    }
    
    @Test
    public void testGetTranslucentRepaintManagerWithTranslucent() {
        RepaintManagerX rmx = new RepaintManagerX(new RepaintManager());
        RepaintManager rm = SwingXUtilities.getTranslucentRepaintManager(rmx);
        
        assertSame(rmx, rm);
    }
    
    @Test
    public void testGetTranslucentRepaintManagerWithNonTranslucent() {
        RepaintManager rm = new RepaintManager();
        RepaintManager rmx = SwingXUtilities.getTranslucentRepaintManager(rm);
        
        assertNotSame(rm, rmx);
        assertTrue(rmx.getClass().isAnnotationPresent(TranslucentRepaintManager.class));
    }
    
    @Test
    public void testGetTranslucentRepaintManagerWithForwardingAndTranslucent() {
        RepaintManagerX rmx = new RepaintManagerX(new RepaintManager());
        ForwardingRepaintManager frm = new ForwardingRepaintManager(rmx);
        RepaintManager rm = SwingXUtilities.getTranslucentRepaintManager(frm);
        
        assertSame(frm, rm);
    }
    
    @Test
    public void testGetTranslucentRepaintManagerWithForwardingAndNonTranslucent() {
        ForwardingRepaintManager frm = new ForwardingRepaintManager(new RepaintManager());
        RepaintManager rm = SwingXUtilities.getTranslucentRepaintManager(frm);
        
        assertNotSame(frm, rm);
        assertTrue(rm.getClass().isAnnotationPresent(TranslucentRepaintManager.class));
    }
    
    @Test
    public void testInvokeLater() {
        assertThat(SwingUtilities.isEventDispatchThread(), is(false));
        
        Callable<Void> callable = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                assertThat(SwingUtilities.isEventDispatchThread(), is(true));
                
                //wait a long time
                Thread.sleep(1000);
                
                return null;
            }
        };
        
        long start = System.currentTimeMillis();
        FutureTask<Void> task = SwingXUtilities.invokeLater(callable);
        assertThat((System.currentTimeMillis() - start) < 100, is(true));
    }
    
    @Test
    public void testInvokeAndWait() throws Exception {
        assertThat(SwingUtilities.isEventDispatchThread(), is(false));
        
        Callable<Boolean> callable = new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                assertThat(SwingUtilities.isEventDispatchThread(), is(true));
                
                //wait a long time
                Thread.sleep(1000);
                
                return true;
            }
        };
        
        long start = System.currentTimeMillis();
        assertThat(SwingXUtilities.invokeAndWait(callable), is(true));
        assertThat((System.currentTimeMillis() - start) > 1000, is(true));
    }
    
    @RunWith(EDTRunner.class)
    public static class GetAncestorTest {
        private Component source;
        
        @Before
        public void setUp() {
            JXTaskPane pane = new JXTaskPane();
            source = pane.add(new JButton());
            
            JXTaskPaneContainer tpc = new JXTaskPaneContainer();
            tpc.add(pane);
            
            JPanel panel = new JPanel();
            panel.add(tpc);
        }
        
        @Test
        public void testNullClass() {
            assertThat(SwingXUtilities.getAncestor(null, source), is(nullValue()));
        }
        
        @Test
        public void testNullSource() {
            assertThat(SwingXUtilities.getAncestor(JPanel.class, null), is(nullValue()));
        }
        
        @Test
        public void testFindAncestorClass() {
            assertThat(SwingXUtilities.getAncestor(JXTaskPane.class, source), is(not(nullValue())));
        }
        
        @Test
        public void testFindAncestorInterface() {
            assertThat(SwingXUtilities.getAncestor(
                    CollapsiblePaneContainer.class, source),
                    is(not(nullValue())));
        }
        
        @Test
        public void testFindMissingAncestorClass() {
            assertThat(SwingXUtilities.getAncestor(JComboBox.class, source),
                    is(nullValue()));
        }
        
        @Test
        public void testFindMissingAncestorInterface() {
            assertThat(SwingXUtilities.getAncestor(PropertyChangeListener.class, source),
                    is(nullValue()));
        }
    }
    
    @Test
    public void testDescendingNull() {
        assertFalse("both nulls are not descending", SwingXUtilities.isDescendingFrom(null, null));
        assertFalse("null comp is not descending", SwingXUtilities.isDescendingFrom(null, new JScrollPane()));
        assertFalse("comp is not descending null parent", SwingXUtilities.isDescendingFrom(new JLabel(), null));
    }
    
    @Test
    public void testDescendingSame() {
        JComponent comp = new JLabel();
        assertTrue("same component must be interpreted as descending", 
                SwingXUtilities.isDescendingFrom(comp, comp));
    }
    
    @Test
    public void testDescendingPopup() throws InterruptedException, InvocationTargetException {
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run - headless");
            return;
        }
        final JXDatePicker picker = new JXDatePicker();
        JXFrame frame = new JXFrame("showing", false);
        frame.add(picker);
        frame.pack();
        frame.setVisible(true);
        assertFalse(SwingXUtilities.isDescendingFrom(picker.getMonthView(), picker));
        Action togglePopup = picker.getActionMap().get("TOGGLE_POPUP");
        togglePopup.actionPerformed(null);
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                assertTrue("popup visible ", ((BasicDatePickerUI) picker.getUI()).isPopupVisible());
                assertTrue(SwingXUtilities.isDescendingFrom(picker.getMonthView(), picker));
                
            }
        });
        frame.dispose();
    }
}
