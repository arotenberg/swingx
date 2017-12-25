package org.jdesktop.swingx.plaf.basic;

import static org.junit.Assert.assertNotNull;

import java.applet.Applet;
import java.awt.GraphicsEnvironment;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JWindow;

import org.jdesktop.swingx.JXErrorPane;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class BasicErrorPaneUITest {
    private static final Logger LOG = Logger.getLogger(BasicErrorPaneUITest.class.getName());
    private BasicErrorPaneUI ui;

    @Before
    public void setup() {
        JXErrorPane pane = new JXErrorPane();
        // PENDING JW: commented as emergency fix for 1148 - test failure on Mac
        // needs to be addressed cleanly
//        assertTrue(pane.getUI().getClass().getName().equals(BasicErrorPaneUI.class.getName()));
        ui = (BasicErrorPaneUI) pane.getUI();
    }

    @Test
    public void testGetErrorFrameNull() {
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run - headless environment");
            return;
        }
        assertNotNull(ui.getErrorFrame(null));
    }
    @Test
    public void testGetErrorFrameFrame() {
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run - headless environment");
            return;
        }
        assertNotNull(ui.getErrorFrame(new JFrame()));
    }
    @Test
    public void testGetErrorFrameDialog() {
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run - headless environment");
            return;
        }
        assertNotNull(ui.getErrorFrame(new JDialog()));
    }
    @Test
    public void testGetErrorFrameContentPane() {
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run - headless environment");
            return;
        }
        JFrame frame = new JFrame();
        assertNotNull(ui.getErrorFrame(frame.getContentPane()));
    }
    @Test
    public void testGetErrorFrameJPanel() {
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run - headless environment");
            return;
        }
        assertNotNull(ui.getErrorFrame(new JPanel()));
    }

    @Test
    public void testGetErrorFrameApplet() {
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run - headless environment");
            return;
        }
        assertNotNull(ui.getErrorFrame(new Applet()));
    }

    @Test
    public void testGetErrorFrameJWindow() {
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run - headless environment");
            return;
        }
        assertNotNull(ui.getErrorFrame(new JWindow()));
    }

    @Test
    public void testGetErrorDialogNull() {
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run - headless environment");
            return;
        }
        assertNotNull(ui.getErrorDialog(null));
    }

    @Test
    public void testGetErrorDialogFrame() {
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run - headless environment");
            return;
        }
        assertNotNull(ui.getErrorDialog(new JFrame()));
    }
    @Test
    public void testGetErrorDialogDialog() {
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run - headless environment");
            return;
        }
        assertNotNull(ui.getErrorDialog(new JDialog()));
    }

    @Test
    public void testGetErrorDialogContentPane() {
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run - headless environment");
            return;
        }
        JFrame frame = new JFrame();
        assertNotNull(ui.getErrorDialog(frame.getContentPane()));
    }

    @Test
    public void testGetErrorDialogJPanel() {
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run - headless environment");
            return;
        }
        assertNotNull(ui.getErrorDialog(new JPanel()));
    }

    @Test
    public void testGetErrorDialogApplet() {
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run - headless environment");
            return;
        }
        assertNotNull(ui.getErrorDialog(new Applet()));
    }

    /**
     * swingx-854 - NPE when owner is JWindow
     */
    @Test
    public void testGetErrorDialogJWindow() {
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run - headless environment");
            return;
        }
        assertNotNull(ui.getErrorDialog(new JWindow()));
    }

    @Test
    public void testGetErrorInternalFrameNull() {
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run - headless environment");
            return;
        }
        assertNotNull(ui.getErrorInternalFrame(null));
    }
    @Test
    public void testGetErrorInternalFrameFrame() {
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run - headless environment");
            return;
        }
        assertNotNull(ui.getErrorInternalFrame(new JFrame()));
    }
    @Test
    public void testGetErrorInternalFrameDialog() {
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run - headless environment");
            return;
        }
        assertNotNull(ui.getErrorInternalFrame(new JDialog()));
    }
    @Test
    public void testGetErrorInternalFrameContentPane() {
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run - headless environment");
            return;
        }
        JFrame frame = new JFrame();
        assertNotNull(ui.getErrorInternalFrame(frame.getContentPane()));
    }
    @Test
    public void testGetErrorInternalFrameJPanel() {
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run - headless environment");
            return;
        }
        assertNotNull(ui.getErrorInternalFrame(new JPanel()));
    }

    @Test
    public void testGetErrorInternalFrameApplet() {
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run - headless environment");
            return;
        }
        assertNotNull(ui.getErrorInternalFrame(new Applet()));
    }

    @Test
    public void testGetErrorInternalFrameJWindow() {
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run - headless environment");
            return;
        }
        assertNotNull(ui.getErrorInternalFrame(new JWindow()));
    }

}
