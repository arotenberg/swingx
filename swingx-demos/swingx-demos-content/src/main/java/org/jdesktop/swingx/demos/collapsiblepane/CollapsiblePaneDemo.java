/*
 * $Id: CollapsiblePaneDemo.java 4097 2011-11-30 19:22:13Z kschaefe $
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
package org.jdesktop.swingx.demos.collapsiblepane;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import org.jdesktop.application.Application;
import org.jdesktop.swingx.JXCollapsiblePane;
import org.jdesktop.swingx.JXFindPanel;

import com.sun.swingset3.DemoProperties;

/**
 * A demo for the {@code JXCollapsiblePane}.
 *
 * @author Karl George Schaefer
 */
@DemoProperties(
    value = "JXCollapsiblePane Demo",
    category = "Containers",
    description = "Demonstrates JXCollapsiblePane, a container for dynamically hiding contents",
    sourceFiles = {
        "org/jdesktop/swingx/demos/collapsiblepane/CollapsiblePaneDemo.java",
        "org/jdesktop/swingx/demos/collapsiblepane/resources/CollapsiblePaneDemo.properties",
        "org/jdesktop/swingx/demos/collapsiblepane/resources/CollapsiblePaneDemo.html",
        "org/jdesktop/swingx/demos/collapsiblepane/resources/images/CollaspiblePaneDemo.png"
    }
)
@SuppressWarnings("serial")
public class CollapsiblePaneDemo extends JPanel {
    private JXCollapsiblePane collapsiblePane;
    private CardLayout containerStack;
    private JButton previousButton;
    private JButton collapsingButton;
    private JButton nextButton;
    
    /**
     * main method allows us to run as a standalone demo.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame(CollapsiblePaneDemo.class.getAnnotation(DemoProperties.class).value());
                
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(new CollapsiblePaneDemo());
                frame.setPreferredSize(new Dimension(800, 600));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
    
    public CollapsiblePaneDemo() {
        createCollapsiblePaneDemo();
        
        Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(this);
        
        bind();
    }
    
    private void createCollapsiblePaneDemo() {
        setLayout(new BorderLayout());
        
        collapsiblePane = new JXCollapsiblePane();
        collapsiblePane.setName("collapsiblePane");
        add(collapsiblePane, BorderLayout.NORTH);
        
        containerStack = new CardLayout();
        collapsiblePane.setLayout(containerStack);
        collapsiblePane.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        
        collapsiblePane.add(new JTree(), "");
        collapsiblePane.add(new JTable(4, 4), "");
        collapsiblePane.add(new JXFindPanel(), "");
        
        add(new JLabel("Main Content Goes Here", JLabel.CENTER));
        
        JPanel buttonPanel = new JPanel();
        add(buttonPanel, BorderLayout.SOUTH);
        
        previousButton = new JButton();
        previousButton.setName("previousButton");
        buttonPanel.add(previousButton);
        
        collapsingButton = new JButton();
        collapsingButton.setName("toggleButton");
        buttonPanel.add(collapsingButton);
        
        nextButton = new JButton();
        nextButton.setName("nextButton");
        buttonPanel.add(nextButton);
    }
    
    private void bind() {
        collapsingButton.addActionListener(collapsiblePane.getActionMap().get(
                JXCollapsiblePane.TOGGLE_ACTION));
        
        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                containerStack.next(collapsiblePane.getContentPane());
            }
        });
        
        previousButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                containerStack.previous(collapsiblePane.getContentPane());
            }
        });
    }
}
