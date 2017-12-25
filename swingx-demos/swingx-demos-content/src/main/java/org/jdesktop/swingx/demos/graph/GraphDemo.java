/*
 * $Id: GraphDemo.java 4097 2011-11-30 19:22:13Z kschaefe $
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
package org.jdesktop.swingx.demos.graph;

import static org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jdesktop.application.Application;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.swingx.JXGraph;
import org.jdesktop.swingx.binding.NumberConverter;

import com.sun.swingset3.DemoProperties;

/**
 * A demo for the {@code JXGraph}.
 * 
 * @author Karl George Schaefer
 */
@DemoProperties(value = "JXGraph Demo", 
        category = "Visualization", 
        description = "Demonstrates JXGraph, a graphing display.", sourceFiles = {
        "org/jdesktop/swingx/demos/graph/GraphDemo.java",
        "org/jdesktop/swingx/demos/graph/resources/GraphDemo.properties",
        "org/jdesktop/swingx/demos/graph/resources/GraphDemo.html",
        "org/jdesktop/swingx/demos/graph/resources/images/GraphDemo.png"
})
@SuppressWarnings("serial")
public class GraphDemo extends JPanel {
    private SimpleLinePlot plot;

    private JFormattedTextField formula;

    /**
     * main method allows us to run as a standalone demo.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame(GraphDemo.class.getAnnotation(DemoProperties.class)
                        .value());

                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(new GraphDemo());
                frame.setPreferredSize(new Dimension(800, 600));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    public GraphDemo() {
        super(new BorderLayout());

        createGraphDemo();

        Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(this);

        bind();
    }

    //TODO inject properties
    private void createGraphDemo() {
        JPanel controlPanel = new JPanel();
        JLabel label = new JLabel("y = ");
        controlPanel.add(label);

        formula = new JFormattedTextField(NumberFormat.getNumberInstance());
        formula.setName("formula");
        formula.setValue(1.0);
        controlPanel.add(formula);

        label = new JLabel("x");
        controlPanel.add(label);
        add(controlPanel, BorderLayout.NORTH);

        Point2D origin = new Point2D.Double(0.0d, 0.0d);
        Rectangle2D view = new Rectangle2D.Double(-10.0d, -10.0d, 20.0d, 20.0d);
        JXGraph graph = new JXGraph(origin, view, 5, 5, 5, 5);
        plot = new SimpleLinePlot();
        graph.addPlots(Color.RED, plot);
        add(graph);
    }

    @SuppressWarnings("unchecked")
    private void bind() {
        Binding b = Bindings.createAutoBinding(READ,
                formula, BeanProperty.create("value"),
                plot, BeanProperty.create("coefficient"));
        b.setConverter(new NumberConverter());
        b.bind();
    }
}
