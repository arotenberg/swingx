/*
 * $Id: ErrorPaneDemo.java 4097 2011-11-30 19:22:13Z kschaefe $
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
package org.jdesktop.swingx.demos.errorpane;

import java.awt.Dimension;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jdesktop.application.Action;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.VerticalLayout;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingxset.DefaultDemoPanel;

import com.sun.swingset3.DemoProperties;

/**
 * A demo for the {@code JXErrorPane}.
 *
 * @author Karl George Schaefer
 * @author joshy (original JXErrorPaneDemo)
 */
@DemoProperties(
    value = "JXErrorPane Demo",
    category = "Controls",
    description = "Demonstrates JXErrorPane, a control for displaying errors",
    sourceFiles = {
        "org/jdesktop/swingx/demos/errorpane/ErrorPaneDemo.java",
        "org/jdesktop/swingx/demos/errorpane/resources/ErrorPaneDemo.properties",
        "org/jdesktop/swingx/demos/errorpane/resources/ErrorPaneDemo.html",
        "org/jdesktop/swingx/demos/errorpane/resources/images/ErrorPaneDemo.png"
    }
)
@SuppressWarnings("serial")
public class ErrorPaneDemo extends DefaultDemoPanel {
    private JButton basic;
    private JButton owner;
    private JButton nested;
    
    /**
     * main method allows us to run as a standalone demo.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame(ErrorPaneDemo.class.getAnnotation(DemoProperties.class).value());
                
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(new ErrorPaneDemo());
                frame.setPreferredSize(new Dimension(800, 600));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
    
    /**
     * {@inheritDoc}
     */
    protected void createDemo() {
        setLayout(new VerticalLayout(5));
        
        basic = new JButton();
        add(basic);
        
        owner = new JButton();
        add(owner);
        
        nested = new JButton();
        add(nested);
    }
    
    /**
     * {@inheritDoc}
     */
    protected void bind() {
        basic.setAction(map.get("generateBasicDialog"));
        owner.setAction(map.get("generateDialogWithOwner"));
        nested.setAction(map.get("generateNestedExceptions"));
    }
    
    @Action
    public void generateBasicDialog() {
        JXErrorPane.showDialog(new Exception());
    }
    
    @Action
    public void generateDialogWithOwner() {
        ErrorInfo info = new ErrorInfo("title", "basic error message", null, "category",
                new Exception(), Level.ALL, null);
        JXErrorPane.showDialog(this, info);
    }

    @Action
    public void generateNestedExceptions() {
        Exception ex = new Exception("I'm a secondary exception", new Exception("I'm the cause"));
        ErrorInfo info = new ErrorInfo("Title", "basic error message", null, "category", ex,
                Level.ALL, null);
        JXErrorPane.showDialog(this, info);
    }
}
