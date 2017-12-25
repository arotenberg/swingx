/*
 * Created on 22.07.2005
 *
 */
package org.jdesktop.swingx;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.util.Date;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXRootPane.XRootLayout;
import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.test.AncientSwingTeam;
import org.jdesktop.test.PropertyChangeReport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


    
/**
 * @author  Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class JXRootPaneTest extends InteractiveTestCase {
 
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(JXRootPaneTest.class
            .getName());
    
    
    /**
     * Issue #936-swingx: JXRootPane cannot cope with default laf decoration. 
     * 
     * testing implementation detail: layout's delegate is null by default
     */
    @Test
    public void testLayoutDelegateNull() {
        JXRootPane pane = new JXRootPane();
        assertTrue(pane.getLayout() instanceof XRootLayout);
        assertNull(((XRootLayout) pane.getLayout()).delegate);
    }
    
    /**
     * Issue #936-swingx: JXRootPane cannot cope with default laf decoration. 
     * 
     * testing implementation detail: layout's delegate is not null with laf decoration
     * 
     * How-to check if LAF supports laf decoration? Here we rely on default 
     * laf is metal which does support it
     */
    @Test
    public void testLayoutDelegateLAF() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run test - headless environment");
            return;
        }
        if (!UIManager.getLookAndFeel().getSupportsWindowDecorations()) {
            LOG.info("cannot run test - unsupported laf window decoration");
            return;
        }
        JFrame.setDefaultLookAndFeelDecorated(true);
        JXFrame frame = new JXFrame();
        JXRootPane pane = frame.getRootPaneExt();
        try {
            assertTrue(pane.getLayout() instanceof XRootLayout);
            assertNotNull(((XRootLayout) pane.getLayout()).delegate);
            pane.setWindowDecorationStyle(JRootPane.NONE);
            assertNull(((XRootLayout) pane.getLayout()).delegate);
        } finally {
            JFrame.setDefaultLookAndFeelDecorated(false);
        }
    }
    /**
     * Issue #936-swingx: JXRootPane cannot cope with default laf decoration. 
     * 
     * here the actual test: pref size with laf decoration 
     */
    @Test
    public void testLayoutWithLAFDecoration() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run testLAFDecorationLayout - headless environment");
            return;
        }
        if (!UIManager.getLookAndFeel().getSupportsWindowDecorations()) {
            LOG.info("cannot run test - unsupported laf window decoration");
            return;
        }
        
        JFrame.setDefaultLookAndFeelDecorated(true);
        JXFrame frame = new JXFrame();
        frame.add(new JXTable(new AncientSwingTeam()));
        frame.pack();
        Dimension dim = frame.getSize();
        assertEquals(dim, frame.getPreferredSize());
        JXStatusBar bar = new JXStatusBar();
        bar.add(new JLabel("need some content"));
        frame.setStatusBar(bar);
        frame.pack();
        try {
            assertEquals(dim.height + bar.getPreferredSize().height, frame.getPreferredSize().height);
        } finally {
            JFrame.setDefaultLookAndFeelDecorated(false);
        }
    }

    /**
     * Issue #936-swingx: JXRootPane cannot cope with default laf decoration. 
     * Compare: no laf decoration
     */
    @Test
    public void testLayoutWithOut() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run testLAFDecorationLayout - headless environment");
            return;
        }
        JXFrame frame = new JXFrame();
        frame.add(new JXTable(new AncientSwingTeam()));
        frame.pack();
        Dimension dim = frame.getSize();
        assertEquals(dim, frame.getPreferredSize());
        JXStatusBar bar = new JXStatusBar();
        bar.add(new JLabel("need some content"));
        frame.setStatusBar(bar);
        frame.pack();
        try {
            assertEquals(dim.height + bar.getPreferredSize().height, frame.getPreferredSize().height);
        } finally {
            JFrame.setDefaultLookAndFeelDecorated(false);
        }
    }

    /**
     * Issue #566: JXRootPane eats escape from popups (JXDatePicker).
     *
     */
    @Test
    public void testDefaultCancel() {
        JXRootPane rootPane = new JXRootPane();
        assertNull(rootPane.getCancelButton());
        Action action = rootPane.getActionMap().get("esc-action");
        assertFalse(action.isEnabled());
    }
    
    /**
     * Test setStatusBar analogous to setToolBar 
     * (triggered by 
     * Issue #499-swingx: old toolbar not removed on setting new).
     * 
     * had not been broken. 
     */
    @Test
    public void testStatusBarSet() {
        JXRootPane rootPane = new JXRootPane();
        JXStatusBar toolBar = new JXStatusBar();
        rootPane.setStatusBar(toolBar);
        assertTrue(SwingUtilities.isDescendingFrom(toolBar, rootPane));
        rootPane.setStatusBar(new JXStatusBar());
        assertFalse(SwingUtilities.isDescendingFrom(toolBar, rootPane));
    }
    
    /**
     * Test setStatusBar analogous to setToolBar 
     * (triggered by 
     * Issue #499-swingx: old toolbar not removed on setting new).
     *
     * Additional fix: rootPane must fire property change event on
     * setStatusBar. 
     * 
     */
    @Test
    public void testStatusBarFirePropertyChange() {
        JXRootPane rootPane = new JXRootPane();
        JXStatusBar toolBar = new JXStatusBar();
        rootPane.setStatusBar(toolBar);
        assertTrue(SwingUtilities.isDescendingFrom(toolBar, rootPane));
        PropertyChangeReport report = new PropertyChangeReport();
        rootPane.addPropertyChangeListener(report);
        rootPane.setStatusBar(new JXStatusBar());
        assertEquals("set statusBar must have fire exactly one property change", 1, report.getEventCount());
        assertTrue(report.hasEvents("statusBar"));
    }

    /**
     * Issue #499-swingx: old toolbar not removed on setting new.
     *
     */
    @Test
    public void testToolBarSet() {
        JXRootPane rootPane = new JXRootPane();
        JToolBar toolBar = new JToolBar();
        rootPane.setToolBar(toolBar);
        assertTrue(SwingUtilities.isDescendingFrom(toolBar, rootPane));
        rootPane.setToolBar(new JToolBar());
        assertFalse(SwingUtilities.isDescendingFrom(toolBar, rootPane));
    }
    
    /**
     * Issue #499-swingx: old toolbar not removed on setting new.
     *
     * Additional fix: rootPane must fire property change event on
     * setToolBar. 
     * 
     * PENDING: similar issue with statusbar?
     */
    @Test
    public void testToolBarFirePropertyChange() {
        JXRootPane rootPane = new JXRootPane();
        JToolBar toolBar = new JToolBar();
        rootPane.setToolBar(toolBar);
        assertTrue(SwingUtilities.isDescendingFrom(toolBar, rootPane));
        PropertyChangeReport report = new PropertyChangeReport();
        rootPane.addPropertyChangeListener(report);
        rootPane.setToolBar(new JToolBar());
        assertEquals(1, report.getEventCount());
        assertTrue(report.hasEvents("toolBar"));
    }
    /**
     * Issue #66-swingx: setStatusBar(null) throws NPE.
     *
     */
    @Test
    public void testStatusBarNPE() {
        JXRootPane rootPane = new JXRootPane();
        rootPane.setStatusBar(null);
    }
    /**
     * Issue #1358-swingx: popups not closed if has cancelButton.
     * 
     * To reproduce: open popup on any of the components, press escape. 
     * expected: popup closed, cancel action not triggered
     * bug: popup still open, cancel action triggered
     */
    public void interactiveDefaultCancelButton() {
        Action cancel = new AbstractActionExt("cancel") {

            public void actionPerformed(ActionEvent e) {
                LOG.info("performed: cancel action");
                
            }
            
        };
        JPopupMenu menu = new JPopupMenu();
        menu.add("dummy");
        menu.add("other");
        JXTree tree = new JXTree();
        tree.expandAll();
        tree.setComponentPopupMenu(menu);
        JXFrame frame = wrapWithScrollingInFrame(tree, "1358-swingx - popup not closed with cancelButton");
        final JButton cancelButton = new JButton(cancel);
        cancelButton.setComponentPopupMenu(menu);
        frame.setCancelButton(cancelButton);
        JFormattedTextField formatted = new JFormattedTextField(new Date());
        formatted.setComponentPopupMenu(menu);
        frame.add(formatted, BorderLayout.SOUTH);
        JXDatePicker picker = new JXDatePicker(new Date());
        picker.setComponentPopupMenu(menu);
        frame.add(picker, BorderLayout.NORTH);
        addStatusComponent(frame, cancelButton);
        addStatusMessage(frame, "open popup, press escape");
        show(frame, 600, 400);
        
    }
    
    
    public void interactiveTestStatusBar() {
        JXTable table = new JXTable(new DefaultTableModel(10, 3));
        final JXFrame frame = wrapWithScrollingInFrame(table, "Statusbar");
        Action action = new AbstractAction("toggle StatusBar") {

            public void actionPerformed(ActionEvent e) {
                JXStatusBar bar = frame.getRootPaneExt().getStatusBar();
                frame.getRootPaneExt().setStatusBar(bar != null ? null : new JXStatusBar());
                frame.getRootPaneExt().revalidate();
            }
            
        };
        addAction(frame, action);
        frame.setVisible(true);
    }
    
    public static void main(String args[]) {
        setSystemLF(true);
        JXRootPaneTest test = new JXRootPaneTest();
        try {
//          test.runInteractiveTests();
            test.runInteractiveTests("interactive.*Cancel.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }

}
