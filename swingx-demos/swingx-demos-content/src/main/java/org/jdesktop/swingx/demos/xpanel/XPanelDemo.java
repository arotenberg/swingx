/*
 * $Id: XPanelDemo.java 4097 2011-11-30 19:22:13Z kschaefe $
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
package org.jdesktop.swingx.demos.xpanel;

import static org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import org.jdesktop.application.Application;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.binding.PercentConverter;

import com.sun.swingset3.DemoProperties;

/**
 * A demo for the {@code JXPanel}.
 *
 * @author Karl George Schaefer
 * @author Richard Bair (original JXPanelTranslucencyDemoPanel)
 */
@DemoProperties(
    value = "JXPanel Demo",
    category = "Containers",
    description = "Demonstrates JXPanel, a container supporting Painters and transparency.",
    sourceFiles = {
        "org/jdesktop/swingx/demos/xpanel/XPanelDemo.java",
        "org/jdesktop/swingx/demos/xpanel/resources/XPanelDemo.properties",
        "org/jdesktop/swingx/demos/xpanel/resources/XPanelDemo.html",
        "org/jdesktop/swingx/demos/xpanel/resources/images/XPanelDemo.png"
    }
)
@SuppressWarnings("serial")
public class XPanelDemo extends JPanel {
    private JXPanel panel;
    private JSlider alphaSlider;
    
    /**
     * main method allows us to run as a standalone demo.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame(XPanelDemo.class.getAnnotation(DemoProperties.class).value());
                
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(new XPanelDemo());
                frame.setPreferredSize(new Dimension(800, 600));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
    
    public XPanelDemo() {
        super(new BorderLayout());
        
        createXPanelDemo();
        
        Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(this);
        
        bind();
    }

    private void createXPanelDemo() {
        setBackground(javax.swing.UIManager.getDefaults().getColor("ScrollBar.thumb"));
        
        panel = new JXPanel();
        panel.setName("panel");
        panel.add(new JSplitPane());
        add(panel);
        
        alphaSlider = new JSlider();
        alphaSlider.setName("alphaSlider");
        Dictionary<Integer, JComponent> labels = new Hashtable<Integer, JComponent>();
        //TODO can we fill these labels from the properties file?
        labels.put(0, new JLabel("Transparent"));
        labels.put(100, new JLabel("Opaque"));
        alphaSlider.setLabelTable(labels);

        add(alphaSlider, BorderLayout.SOUTH);
    }
    
    @SuppressWarnings("unchecked")
    private void bind() {
        Binding b = Bindings.createAutoBinding(READ,
                alphaSlider, BeanProperty.create("value"),
                panel, BeanProperty.create("alpha"));
        b.setConverter(new PercentConverter());
        b.bind();
    }
}
