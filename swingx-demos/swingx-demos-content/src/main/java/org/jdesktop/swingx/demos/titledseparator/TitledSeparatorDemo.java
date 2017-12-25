/*
 * $Id: TitledSeparatorDemo.java 4097 2011-11-30 19:22:13Z kschaefe $
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
package org.jdesktop.swingx.demos.titledseparator;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.jdesktop.application.Application;
import org.jdesktop.swingx.JXTitledSeparator;
import org.jdesktop.swingx.VerticalLayout;

import com.sun.swingset3.DemoProperties;

/**
 * A demo for the {@code JXTitledSeparator}.
 *
 * @author Karl George Schaefer
 * @author rbair (original JXTitledSeparatorDemo)
 */
@DemoProperties(
    value = "JXTitledSeparator Demo",
    category = "Decorators",
    description = "Demonstrates JXTitledSeparator, a text separator.",
    sourceFiles = {
        "org/jdesktop/swingx/demos/titledseparator/TitledSeparatorDemo.java",
        "org/jdesktop/swingx/demos/titledseparator/resources/TitledSeparatorDemo.properties",
        "org/jdesktop/swingx/demos/titledseparator/resources/TitledSeparatorDemo.html",
        "org/jdesktop/swingx/demos/titledseparator/resources/images/TitledSeparatorDemo.png"
    }
)
@SuppressWarnings("serial")
public class TitledSeparatorDemo extends JPanel {
    
    /**
     * main method allows us to run as a standalone demo.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame(TitledSeparatorDemo.class.getAnnotation(DemoProperties.class).value());
                
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(new TitledSeparatorDemo());
                frame.setPreferredSize(new Dimension(800, 600));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
    
    public TitledSeparatorDemo() {
        setLayout(new VerticalLayout(3));
        
        createTitledSeparatorDemo();
        
        Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(this);
        
        bind();
    }

    //TODO inject properties
    private void createTitledSeparatorDemo() {
        add(new JXTitledSeparator());
        add(new JXTitledSeparator()).setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        JXTitledSeparator s = new JXTitledSeparator();
        s.setTitle("Custom Title");
        add(s);
        
        s = new JXTitledSeparator();
        s.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        s.setTitle("Custom Title");
        add(s);
        
        s = new JXTitledSeparator();
        s.setFont(new Font("Times New Roman", Font.ITALIC, 16));
        s.setTitle("Custom Font");
        add(s);
        
        s = new JXTitledSeparator();
        s.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        s.setFont(new Font("Times New Roman", Font.ITALIC, 16));
        s.setTitle("Custom Font");
        add(s);
        
        s = new JXTitledSeparator();
        s.setForeground(Color.BLUE.darker());
        s.setTitle("Custom Foreground");
        add(s);
        
        s = new JXTitledSeparator();
        s.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        s.setForeground(Color.BLUE.darker());
        s.setTitle("Custom Foreground");
        add(s);
        
        s = new JXTitledSeparator();
        s.setHorizontalAlignment(SwingConstants.CENTER);
        s.setTitle("Center Alignment");
        add(s);
        
        s = new JXTitledSeparator();
        s.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        s.setHorizontalAlignment(SwingConstants.CENTER);
        s.setTitle("Center Alignment");
        add(s);
        
        s = new JXTitledSeparator();
        s.setHorizontalAlignment(SwingConstants.TRAILING);
        s.setTitle("Trailing Alignment");
        add(s);
        
        s = new JXTitledSeparator();
        s.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        s.setHorizontalAlignment(SwingConstants.TRAILING);
        s.setTitle("Trailing Alignment");
        add(s);
        
        s = new JXTitledSeparator();
        s.setHorizontalAlignment(SwingConstants.LEADING);
        s.setTitle("Leading Alignment");
        add(s);
        
        s = new JXTitledSeparator();
        s.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        s.setHorizontalAlignment(SwingConstants.LEADING);
        s.setTitle("Leading Alignment");
        add(s);
        
        s = new JXTitledSeparator();
        s.setHorizontalAlignment(SwingConstants.LEFT);
        s.setTitle("Left Alignment");
        add(s);
        
        s = new JXTitledSeparator();
        s.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        s.setHorizontalAlignment(SwingConstants.LEFT);
        s.setTitle("Left Alignment");
        add(s);
        
        s = new JXTitledSeparator();
        s.setHorizontalAlignment(SwingConstants.RIGHT);
        s.setTitle("Right Alignment");
        add(s);
        
        s = new JXTitledSeparator();
        s.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        s.setHorizontalAlignment(SwingConstants.RIGHT);
        s.setTitle("Right Alignment");
        add(s);
        
        Icon icon = Application.getInstance().getContext()
                .getResourceMap(TitledSeparatorDemo.class).getIcon("greenOrb");
        
        s = new JXTitledSeparator();
        s.setIcon(icon);
        s.setTitle("Custom Icon");
        add(s);
        
        s = new JXTitledSeparator();
        s.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        s.setIcon(icon);
        s.setTitle("Custom Icon");
        add(s);
        
        s = new JXTitledSeparator();
        s.setIcon(icon);
        s.setHorizontalTextPosition(SwingConstants.LEFT);
        s.setTitle("Custom Icon, LEFT Horizontal Text Position");
        add(s);
        
        s = new JXTitledSeparator();
        s.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        s.setIcon(icon);
        s.setHorizontalTextPosition(SwingConstants.LEFT);
        s.setTitle("Custom Icon, LEFT Horizontal Text Position");
        add(s);
    }
    
    private void bind() {
        // TODO Auto-generated method stub
        
    }
}
