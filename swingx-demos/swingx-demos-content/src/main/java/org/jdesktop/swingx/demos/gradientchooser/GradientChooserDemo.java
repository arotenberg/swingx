/*
 * $Id: GradientChooserDemo.java 4097 2011-11-30 19:22:13Z kschaefe $
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
package org.jdesktop.swingx.demos.gradientchooser;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jdesktop.application.Application;
import org.jdesktop.swingx.JXGradientChooser;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.MattePainter;

import com.sun.swingset3.DemoProperties;

/**
 * A demo for the {@code JXGradientChooser}.
 *
 * @author Karl George Schaefer
 * @author joshy (original JXGradientChooserDemo)
 */
@DemoProperties(
    value = "JXGradientChooser Demo",
    category = "Choosers",
    description = "Demonstrates JXGradientChooser, a component which allows the user to create a gradient.",
    sourceFiles = {
        "org/jdesktop/swingx/demos/gradientchooser/GradientChooserDemo.java",
        "org/jdesktop/swingx/demos/gradientchooser/resources/GradientChooserDemo.properties",
        "org/jdesktop/swingx/demos/gradientchooser/resources/GradientChooserDemo.html"
    }
)
@SuppressWarnings("serial")
public class GradientChooserDemo extends JXPanel {
    private MultipleGradientPaint gradient;
    private JButton showChooser;
    
    /**
     * main method allows us to run as a standalone demo.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame(GradientChooserDemo.class.getAnnotation(DemoProperties.class).value());
                
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(new GradientChooserDemo());
                frame.setPreferredSize(new Dimension(800, 600));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
    
    public GradientChooserDemo() {
        float[] vals = { 0.0f, 1.0f };
        Color[] colors = { Color.WHITE, Color.BLUE };
        gradient = new LinearGradientPaint(0f, 0f, 1f, 1f, vals, colors);
        
        setBackgroundPainter(new MattePainter(gradient, true));
        
        createGradientChooserDemo();
        
        Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(this);
        
        bind();
    }

    private void createGradientChooserDemo() {
        showChooser = new JButton();
        showChooser.setName("showChooser");
        add(showChooser);
    }
    
    private void bind() {
        showChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gradient = JXGradientChooser.showDialog(GradientChooserDemo.this,
                        "Pick a Gradient", gradient);
                setBackgroundPainter(new MattePainter(gradient, true));
            }
        });
    }
}
