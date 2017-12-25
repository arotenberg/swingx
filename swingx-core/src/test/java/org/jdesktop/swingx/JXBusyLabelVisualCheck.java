/*
 * $Id: JXBusyLabelVisualCheck.java 3473 2009-08-27 13:17:10Z kleopatra $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
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
 */
package org.jdesktop.swingx;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Visual tests of JXBusyLabel issues.
 * @author had
 *
 */
public class JXBusyLabelVisualCheck extends InteractiveTestCase {


    private static JDialog createDialog(JFrame owner) {
    	JDialog d = new JDialog(owner, "Dialog");
    	d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    	JXBusyLabel bl = new JXBusyLabel();
    	d.add(bl);
    	bl.setBusy(true);
    	d.setVisible(true);
    	return d;
    	
    }
    
    /**
     * Test for issue #795 - size is set to 0 using default const.
     */
    public void interactiveNoArgConst() {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        final JXBusyLabel label = new JXBusyLabel();
        label.setText("hi there");
        f.add(label);
        f.add(new JButton(new AbstractAction("click me") {
            public void actionPerformed(ActionEvent e) {
                label.setBusy(!label.isBusy());
            }
        }), BorderLayout.SOUTH);
        f.pack();
        f.setVisible(true);
        
    }
    /**
     * Test for memory leak issue #626.
     */
    public void interactiveMemoryLeak() {
        final JFrame f = new JFrame();
        JXHeader header = new JXHeader();
        header.setTitle("<html><B>JXBusyLabelIssue #626</b>");
        header.setDescription("<html>Memory leak test. By clicking 'start' button, creation of 100 "
				    + "new dialogs will be triggered. Similarly 'Stop' button will then destroy all "
					+ "created dialogs. It should be possible to see memory consumption on the "
					+ "System.out as well as change in speed of busy label rotation when all dialogs "
					+ "are created/destroyed.");
        f.add(header, BorderLayout.NORTH);
        JXBusyLabel bl = new JXBusyLabel();
        f.add(bl);
        bl.setBusy(true);
    	final List<JDialog> l = new ArrayList<JDialog>();
    	JPanel control = new JPanel();
        JButton b = new JButton(new AbstractAction("Start") {
            public void actionPerformed(ActionEvent e) {
            	System.gc();
            	System.out.println("Start mem(u):" + (Runtime.getRuntime().totalMemory() - 
            			Runtime.getRuntime().freeMemory()));
                // just reset the model
            	for (int i = 0; i < 100; i++) {
            		l.add(createDialog(f));
            	}
            	System.gc();
            	System.out.println("alloc mem(u):" + (Runtime.getRuntime().totalMemory() - 
            			Runtime.getRuntime().freeMemory()));
            }

        });
        control.add(b);
        b = new JButton(new AbstractAction("Stop") {
            public void actionPerformed(ActionEvent e) {
                // just reset the model
            	for (int i = 0; i < 100; i++) {
            		if (l.size() > 0) {
            		    JDialog d = l.remove(0);
            		    d.dispose();
            		}
            	}
            	System.gc();
            	System.out.println("Stop mem(u):" + (Runtime.getRuntime().totalMemory() - 
            			Runtime.getRuntime().freeMemory()));
            }
        });
        control.add(b);
        f.add(control, BorderLayout.SOUTH);
        f.setPreferredSize(new Dimension(400, 400));
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setVisible(true);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        JXBusyLabelVisualCheck test = new JXBusyLabelVisualCheck();
        try {
            test.runInteractiveTests();
          } catch (Exception e) {
              System.err.println("exception when executing interactive tests:");
              e.printStackTrace();
          } 
    }

    /**
     * do nothing test - keep the testrunner happy.
     */
    public void testDummy() {
    }

}
