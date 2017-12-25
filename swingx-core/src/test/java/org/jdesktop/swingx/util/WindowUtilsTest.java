/*
 * Created on 19.08.2005
 *
 */
package org.jdesktop.swingx.util;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTable;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.test.EDTRunner;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(EDTRunner.class)
public class WindowUtilsTest extends InteractiveTestCase {

    public static void main(String[] args) {
        WindowUtilsTest test = new WindowUtilsTest();
        try {
            test.runInteractiveTests("interactiveCenteringTestOnUnownedDialogOnSecondScreen");
        } catch (Exception ex) {

        }
    }

    private static GraphicsConfiguration getSecondWindowConfiguration() {
        GraphicsDevice[] screens = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        
        if (screens.length < 2) {
            return null;
        }
        
        return screens[1].getDefaultConfiguration();
    }
    
    public void interactiveCenteringTest() {
        JFrame frame = new JFrame("I should be centered");
        frame.add(new JTable(5, 5));
        frame.pack();
        frame.setLocation(WindowUtils.getPointForCentering(frame));
        frame.setVisible(true);
    }
    
    public void interactiveCenteringTestOnSecondWindow() {
        if (getSecondWindowConfiguration() == null) {
            return;
        }
        
        JFrame frame = new JFrame("I should be centered on the second screen", getSecondWindowConfiguration());
        frame.add(new JTable(5, 5));
        frame.pack();
        frame.setLocation(WindowUtils.getPointForCentering(frame));
        frame.setVisible(true);
    }
    
    public void interactiveCenteringTestOnUnownedDialog() {
        JFrame frame = new JFrame("Unowned Dialog Test");
        frame.add(new JButton(new AbstractAction("Create Centered Dialog") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog dialog = new JDialog((JFrame) null, "Unowned Dialog");
                dialog.add(new JTable(5, 5));
                dialog.pack();
                dialog.setLocation(WindowUtils.getPointForCentering(dialog));
                dialog.setVisible(true);
            }
        }));
        frame.pack();
        frame.setLocation(WindowUtils.getPointForCentering(frame));
        frame.setVisible(true);
    }
    
    public void interactiveCenteringTestOnUnownedDialogOnSecondScreen() {
        if (getSecondWindowConfiguration() == null) {
            return;
        }
        
        JFrame frame = new JFrame("Unowned Dialog Second Screen Test");
        frame.add(new JButton(new AbstractAction("Create Centered Dialog On Second Screen") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog dialog = new JDialog((JFrame) null, "Unowned Dialog On Second Screen", false, getSecondWindowConfiguration());
                dialog.add(new JTable(5, 5));
                dialog.pack();
                dialog.setLocation(WindowUtils.getPointForCentering(dialog));
                dialog.setVisible(true);
            }
        }));
        frame.pack();
        frame.setLocation(WindowUtils.getPointForCentering(frame));
        frame.setVisible(true);
    }
    
    public void interactiveCenteringTestOnOwnedDialog() {
        final JFrame frame = new JFrame("Owned Dialog Test");
        frame.add(new JButton(new AbstractAction("Create Centered Dialog") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog dialog = new JDialog(frame, "Owned Dialog");
                dialog.add(new JTable(5, 5));
                dialog.pack();
                dialog.setLocation(WindowUtils.getPointForCentering(dialog));
                dialog.setVisible(true);
            }
        }));
        frame.pack();
        frame.setLocation(WindowUtils.getPointForCentering(frame));
        frame.setVisible(true);
    }
    
    public void interactiveCenteringTestOnDispledDialog() {
        final JFrame frame = new JFrame("Displayed Dialog Test");
        final JDialog dialog = new JDialog(frame, "Displayed Dialog");
        frame.add(new JButton(new AbstractAction("Center Displayed Dialog") {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.setLocation(WindowUtils.getPointForCentering(dialog));
                dialog.setVisible(true);
            }
        }));
        
        frame.pack();
        frame.setLocation(WindowUtils.getPointForCentering(frame));
        frame.setVisible(true);
        dialog.add(new JTable(5, 5));
        dialog.pack();
        dialog.setLocation(WindowUtils.getPointForCentering(dialog));
        dialog.setVisible(true);
    }
    
    /**
     * TODO formally test...
     *
     */
    @Test
    public void testDummy() {
        
    }
}
