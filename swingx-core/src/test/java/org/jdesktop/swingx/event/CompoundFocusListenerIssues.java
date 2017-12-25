/*
 * Created on 23.04.2009
 *
 */
package org.jdesktop.swingx.event;

import java.awt.Component;
import java.awt.DefaultKeyboardFocusManager;
import java.awt.GraphicsEnvironment;
import java.awt.KeyboardFocusManager;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.SwingXUtilities;
import org.jdesktop.test.PropertyChangeReport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class CompoundFocusListenerIssues extends CompoundFocusListenerTest {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(CompoundFocusListenerIssues.class.getName());

    /**
     * Test that listener copes with change of keyboardFocusManager.
     * 
     * Not yet implemented.
     * 
     * @throws InterruptedException
     * @throws InvocationTargetException
     */
    @Test
    public void testKeyboardFocusManager() throws InterruptedException, InvocationTargetException {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run test - headless environment");
            return;
        }
        JXDatePicker picker = getRealizedDatePicker();
        final KeyboardFocusManager oldManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        Component focusOwner = oldManager.getPermanentFocusOwner();
        assertFalse("sanity: initial focus must not be in picker", 
                SwingXUtilities.isDescendingFrom(focusOwner, picker));
        CompoundFocusListener l = new CompoundFocusListener(picker);
        final PropertyChangeReport report = new PropertyChangeReport();
        l.addPropertyChangeListener(report);
        KeyboardFocusManager customManager = new DefaultKeyboardFocusManager();
        KeyboardFocusManager.setCurrentKeyboardFocusManager(customManager);
        LOG.info("after custom? ");
        picker.requestFocusInWindow();
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                try {
                    assertEquals(1, report.getEventCount("focused"));
                } finally {
                    KeyboardFocusManager.setCurrentKeyboardFocusManager(oldManager);
                }
            }
        });
    }

}
