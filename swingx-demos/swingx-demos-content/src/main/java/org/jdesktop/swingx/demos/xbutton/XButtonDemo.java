/*
 * $Id: XButtonDemo.java 4097 2011-11-30 19:22:13Z kschaefe $
 *
 * Copyright 2009 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx.demos.xbutton;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jdesktop.application.Application;

import com.sun.swingset3.DemoProperties;

/**
 * A demo for the {@code JXButton}.
 *
 * @author Karl George Schaefer
 */
@DemoProperties(
    value = "JXButton Demo",
    category = "Controls",
    description = "Demonstrates JXButton, an extended button control",
    sourceFiles = {
        "org/jdesktop/swingx/demos/xbutton/XButtonDemo.java",
        "org/jdesktop/swingx/demos/xbutton/resources/XButtonDemo.properties",
        "org/jdesktop/swingx/demos/xbutton/resources/XButtonDemo.html",
        "org/jdesktop/swingx/demos/xbutton/resources/images/XButtonDemo.png"
    }
)
@SuppressWarnings("serial")
public class XButtonDemo extends JPanel {
    
    /**
     * main method allows us to run as a standalone demo.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame(XButtonDemo.class.getAnnotation(DemoProperties.class).value());
                
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(new XButtonDemo());
                frame.setPreferredSize(new Dimension(800, 600));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
    
    public XButtonDemo() {
        createBusyLabelDemo();
        
        Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(this);
        
        bind();
    }

    private void createBusyLabelDemo() {
        // TODO Auto-generated method stub
        add(new JLabel("Not Implemented"));
    }
    
    private void bind() {
        // TODO Auto-generated method stub
        
    }
}
