/*
 * Created on 31.01.2006
 *
 */
package org.jdesktop.swingx.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;

import org.jdesktop.test.EDTRunner;
import org.jdesktop.test.PropertyChangeReport;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EDTRunner.class)
public class ActionIssues {

    /**
     * core issue: 
     * set selected via putValue leads to inconsistent state.
     *
     */
    @Test
    public void testFireSelected() {
        AbstractActionExt action = new AbstractActionExt("dummy") {

            public void actionPerformed(ActionEvent e) {
                // nothing to do
                
            }

            @Override
            public void itemStateChanged(ItemEvent e) {
                // nothing to do
                
            }
            
        };
        PropertyChangeListener l = new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if ("selected".equals(evt.getPropertyName())) {
                    assertEquals(evt.getNewValue(), ((AbstractActionExt) evt.getSource()).isSelected());
                }
                
            }
            
        };
        action.addPropertyChangeListener(l);
        action.putValue("selected", true);
        
    }
    //--------------------- core: selected is not a bean property
    
    /**
     * unexpected core behaviour: selected is not a bound property!
     * PENDING: is it in Mustang?
     */
    @Test
    public void testToggleButtonPropertyChangeSelected() {
        JToggleButton button = new JToggleButton();
        PropertyChangeReport report = new PropertyChangeReport();
        button.addPropertyChangeListener(report);
        boolean selected = button.isSelected();
        button.setSelected(!selected);
        // sanity...
        assertEquals(selected, !button.isSelected());
        assertEquals("must have one event for selected", 1, report.getEventCount("selected"));
    }
    
    @Test
    public void testCheckBoxPropertyChangeSelected() {
        JCheckBox button = new JCheckBox();
        PropertyChangeReport report = new PropertyChangeReport();
        button.addPropertyChangeListener(report);
        boolean selected = button.isSelected();
        button.setSelected(!selected);
        // sanity...
        assertEquals(selected, !button.isSelected());
        assertEquals("must have one event for selected", 1, report.getEventCount("selected"));
    }
    
    @Test
    public void testRadioButtonPropertyChangeSelected() {
        JRadioButton button = new JRadioButton();
        PropertyChangeReport report = new PropertyChangeReport();
        button.addPropertyChangeListener(report);
        boolean selected = button.isSelected();
        button.setSelected(!selected);
        // sanity...
        assertEquals(selected, !button.isSelected());
        assertEquals("must have one event for selected", 1, report.getEventCount("selected"));
    }
    
    @Test
    public void testCheckBoxMenuItemPropertyChangeSelected() {
        JMenuItem button = new JCheckBoxMenuItem();
        PropertyChangeReport report = new PropertyChangeReport();
        button.addPropertyChangeListener(report);
        boolean selected = button.isSelected();
        button.setSelected(!selected);
        // sanity...
        assertEquals(selected, !button.isSelected());
        assertEquals("must have one event for selected", 1, report.getEventCount("selected"));
    }
    
    /**
     * Template to try and test memory leaks (from Palantir blog).
     * TODO apply for listener problems
     */
    @Test
    public void testMemory() {
        //create test object
        Object testObject = new Object();
        // create queue and weak reference
        ReferenceQueue<Object> queue = new ReferenceQueue<Object>();
        WeakReference<Object> ref = new WeakReference<Object>(testObject, queue);
        // set hard reference to null
        testObject = null;
//        force garbage collection
        System.gc();
        // soft reference should now be enqueued (no leak)
        assertTrue(ref.isEnqueued());
    }
}
