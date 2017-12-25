/*
 * $Id: XFrameDemo.java 4097 2011-11-30 19:22:13Z kschaefe $
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
package org.jdesktop.swingx.demos.xframe;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jdesktop.application.Application;

import com.sun.swingset3.DemoProperties;

/**
 * A demo for the {@code JXFrame}.
 *
 * @author Karl George Schaefer
 */
@DemoProperties(
    value = "JXFrame Demo",
    category = "Toplevel Containers",
    description = "Demonstrates JXFrame, an extended toplevel frame container",
    sourceFiles = {
        "org/jdesktop/swingx/demos/xframe/XFrameDemo.java",
        "org/jdesktop/swingx/demos/xframe/resources/XFrameDemo.properties",
        "org/jdesktop/swingx/demos/xframe/resources/XFrameDemo.html"
    }
)
@SuppressWarnings("serial")
public class XFrameDemo extends JPanel {
    
    /**
     * main method allows us to run as a standalone demo.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame(XFrameDemo.class.getAnnotation(DemoProperties.class).value());
                
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(new XFrameDemo());
                frame.setPreferredSize(new Dimension(800, 600));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
    
    public XFrameDemo() {
        createXFrameDemo();
        
        Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(this);
        
        bind();
    }

    private void createXFrameDemo() {
        // TODO Auto-generated method stub
        add(new JLabel("Not Implemented"));
    }
    
    private void bind() {
        // TODO Auto-generated method stub
        
    }
}
