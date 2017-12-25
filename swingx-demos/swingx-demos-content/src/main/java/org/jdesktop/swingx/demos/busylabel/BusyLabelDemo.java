/*
 * $Id: BusyLabelDemo.java 4097 2011-11-30 19:22:13Z kschaefe $
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
package org.jdesktop.swingx.demos.busylabel;

import static org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;

import org.jdesktop.application.Application;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.VerticalLayout;
import org.jdesktop.swingx.binding.FactorConverter;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.util.PaintUtils;

import com.sun.swingset3.DemoProperties;

/**
 * A demo for the {@code JXBusyLabel}.
 *
 * @author Karl George Schaefer
 * @author rah003 (original JXBusyLabelDemoPanel)
 */
@DemoProperties(
    value = "JXBusyLabel Demo",
    category = "Decorators",
    description = "Demonstrates JXBusyLabel, a control for identifying busy periods",
    sourceFiles = {
        "org/jdesktop/swingx/demos/busylabel/BusyLabelDemo.java",
        "org/jdesktop/swingx/demos/busylabel/resources/BusyLabelDemo.properties",
        "org/jdesktop/swingx/demos/busylabel/resources/BusyLabelDemo.html",
        "org/jdesktop/swingx/demos/busylabel/resources/images/BusyLabelDemo.png"
    }
)
//TODO demo incomplete
@SuppressWarnings("serial")
public class BusyLabelDemo extends JXPanel {
    private JXBusyLabel label;
    
    private JSlider speedSlider;
    
    private JSlider pointsSlider;
    
    private JSlider trailSlider;
    
    /**
     * main method allows us to run as a standalone demo.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame(BusyLabelDemo.class.getAnnotation(DemoProperties.class).value());
                
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(new BusyLabelDemo());
                frame.setPreferredSize(new Dimension(800, 600));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
    
    public BusyLabelDemo() {
        super(new BorderLayout());
        
        createBusyLabelDemo();
        
        Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(this);
        
        bind();
    }

    private void createBusyLabelDemo() {
        setBackgroundPainter(new MattePainter(PaintUtils.BLUE_EXPERIENCE, true));
        
        //TODO uncomment when SwingX #999 is fixed
//        label = new JXBusyLabel();
        label = new JXBusyLabel(new Dimension(50, 50));
        label.setName("busyLabel");
        label.getBusyPainter().setHighlightColor(new Color(44, 61, 146).darker());
        label.getBusyPainter().setBaseColor(new Color(168, 204, 241).brighter());
        label.setBusy(true);
        add(label, BorderLayout.NORTH);
        
        JPanel control = new JPanel(new VerticalLayout());
        control.setName("controlPanel");
        
        speedSlider = new JSlider();
        speedSlider.setName("speedSlider");
        Dictionary<Integer, JComponent> labels = new Hashtable<Integer, JComponent>();
        //TODO can we fill these labels from the properties file?
        labels.put(1, new JLabel("Faster"));
        labels.put(50, new JLabel("Slower"));
        speedSlider.setLabelTable(labels);
        control.add(speedSlider);
        
        pointsSlider = new JSlider();
        pointsSlider.setName("pointsSlider");
        labels = new Hashtable<Integer, JComponent>();
        //TODO can we fill these labels from the properties file?
        labels.put(1, new JLabel("Fewer Points"));
        labels.put(50, new JLabel("More Points"));
        pointsSlider.setLabelTable(labels);
        control.add(pointsSlider);
        
        trailSlider = new JSlider();
        trailSlider.setName("trailSlider");
        labels = new Hashtable<Integer, JComponent>();
        //TODO can we fill these labels from the properties file?
        labels.put(1, new JLabel("Short Trail"));
        labels.put(20, new JLabel("Long Trail"));
        trailSlider.setLabelTable(labels);
        control.add(trailSlider);

        add(control);
    }
    
    @SuppressWarnings("unchecked")
    private void bind() {
        Binding b = Bindings.createAutoBinding(READ,
                speedSlider, BeanProperty.create("value"),
                label, BeanProperty.create("delay"));
        b.setConverter(new FactorConverter<Integer>(10));
        b.bind();
        
        Bindings.createAutoBinding(READ,
                pointsSlider, BeanProperty.create("value"),
                label, ELProperty.create("${busyPainter.points}")).bind();
        
        Bindings.createAutoBinding(READ,
                trailSlider, BeanProperty.create("value"),
                label, ELProperty.create("${busyPainter.trailLength}")).bind();
    }
}
