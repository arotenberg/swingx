/*
 * Created on 23.04.2009
 *
 */
package org.jdesktop.swingx.event;

import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.KeyboardFocusManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.SwingXUtilities;
import org.jdesktop.test.PropertyChangeReport;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@Ignore("automated testing fails")
@RunWith(JUnit4.class)
public class CompoundFocusListenerTest extends InteractiveTestCase {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(CompoundFocusListenerTest.class.getName());
    
    public static void main(String[] args) {
        CompoundFocusListenerTest test = new CompoundFocusListenerTest();
        // KEEP this is global state - uncomment for debug painting completely
//        UIManager.put(CalendarHeaderHandler.uiControllerID, "org.jdesktop.swingx.plaf.basic.SpinningCalendarHeaderHandler");
//        UIManager.put(SpinningCalendarHeaderHandler.FOCUSABLE_SPINNER_TEXT, Boolean.TRUE);
//        UIManager.put("JXDatePicker.forceZoomable", Boolean.TRUE);
        try {
            test.runInteractiveTests();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void interactiveCompoundFocusedProperty() {
        JXPanel panel = new JXPanel();
        panel.setName("plain-panel");
        // add some components inside
        panel.add(new JTextField("something to .... focus"));
        JXDatePicker picker = new JXDatePicker(new Date());
        picker.setName("picker");
        panel.add(picker);
        JComboBox combo = new JComboBox(new Object[] {"dooooooooo", 1, 2, 3, 4 });
        combo.setEditable(true);
        panel.add(new JButton("something else to ... focus"));
        panel.add(combo);
        panel.setBorder(new TitledBorder("has focus dispatcher"));
        // register the compound dispatcher
        CompoundFocusListener focusDispatcher = new CompoundFocusListener(picker);
        PropertyChangeListener l = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                LOG.info("focused on picker " +  evt.getNewValue());
                
            }};
        focusDispatcher.addPropertyChangeListener(l);    
        
        JComponent box = Box.createVerticalBox();
        box.add(panel);
        showInFrame(box, "CompoundFocusListener (on datePicker)");
    }


    @Test(expected = NullPointerException.class)
    public void testNullRoot() {
        new CompoundFocusListener(null);
    }
    
    @Test
    public void testFocusedInitialFalse() {
        JComboBox field = new JComboBox();
        field.setEditable(true);
        assertFalse("sanity: combo not focused", field.hasFocus());
        CompoundFocusListener l = new CompoundFocusListener(field);
        assertEquals(field.hasFocus(), l.isFocused());
    }

    @Test
    public void testFocusedInitialTrue() throws InterruptedException, InvocationTargetException {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run test - headless environment");
            return;
        }
        final JXDatePicker picker = getRealizedDatePicker();
        picker.requestFocusInWindow();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
                assertTrue("sanity: focus transfered into picker hierarchy", 
                        SwingXUtilities.isDescendingFrom(focusOwner, picker));
                CompoundFocusListener l = new CompoundFocusListener(picker);
                assertTrue("listener must take initial focused state ", l.isFocused());
            }
        });
    }

    @Test
    public void testReleaseRemovesListeners() {
        CompoundFocusListener l = new CompoundFocusListener(new JXDatePicker());
        final PropertyChangeReport report = new PropertyChangeReport();
        l.addPropertyChangeListener(report);
        l.release();
        assertRemoved(report, l.getPropertyChangeListeners());
    }
    private void assertRemoved(PropertyChangeReport report,
            PropertyChangeListener[] propertyChangeListeners) {
        for (PropertyChangeListener l : propertyChangeListeners) {
            assertNotSame("property change listener must have been removed", report, l);
        }
    }

    /**
     * Test no property change fired after release.
     * 
     * @throws InterruptedException
     * @throws InvocationTargetException
     */
    @Test
    public void testRelease() throws InterruptedException, InvocationTargetException {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run test - headless environment");
            return;
        }
        JXDatePicker picker = getRealizedDatePicker();
        Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
        assertFalse("sanity: initial focus must not be in picker", 
                SwingXUtilities.isDescendingFrom(focusOwner, picker));
        CompoundFocusListener l = new CompoundFocusListener(picker);
        final PropertyChangeReport report = new PropertyChangeReport();
        l.addPropertyChangeListener(report);
        l.release();
        picker.requestFocusInWindow();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                assertEquals(0, report.getEventCount("focused"));
            }
        });
    }

    /**
     * Test property change fired on focusGained.
     * 
     * @throws InterruptedException
     * @throws InvocationTargetException
     */
    @Test
    public void testFireFocusedProperty() throws InterruptedException, InvocationTargetException {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run test - headless environment");
            return;
        }
        JXDatePicker picker = getRealizedDatePicker();
        Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
        assertFalse("sanity: initial focus must not be in picker", 
                SwingXUtilities.isDescendingFrom(focusOwner, picker));
        CompoundFocusListener l = new CompoundFocusListener(picker);
        final PropertyChangeReport report = new PropertyChangeReport();
        l.addPropertyChangeListener(report);
        picker.requestFocusInWindow();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                assertEquals(1, report.getEventCount("focused"));
            }
        });
    }

    /**
     * @return
     */
    protected JXDatePicker getRealizedDatePicker() {
        JXFrame frame = new JXFrame();
        final JXDatePicker picker = new JXDatePicker();
        frame.add(new JTextField(10));
        frame.add(picker);
        frame.add(new JTextField(10));
        frame.pack();
        frame.setVisible(true);
        return picker;
    }

}
