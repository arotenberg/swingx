/*
 * $Id: ImageViewDemo.java 4097 2011-11-30 19:22:13Z kschaefe $
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
package org.jdesktop.swingx.demos.imageview;

import static org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.swingx.JXImageView;
import org.jdesktop.swingx.VerticalLayout;
import org.jdesktop.swingxset.DefaultDemoPanel;

import com.sun.swingset3.DemoProperties;

/**
 * A demo for the {@code JXImageView}.
 *
 * @author Karl George Schaefer
 * @author jm158417 (original JXImageViewDemoPanel)
 */
@DemoProperties(
    value = "JXImageView Demo",
    category = "Controls",
    description = "Demonstrates JXImageView, a control for displaying and manipulating images.",
    sourceFiles = {
        "org/jdesktop/swingx/demos/imageview/ImageViewDemo.java",
        "org/jdesktop/swingx/demos/imageview/resources/ImageViewDemo.properties",
        "org/jdesktop/swingx/demos/imageview/resources/ImageViewDemo.html",
        "org/jdesktop/swingx/demos/imageview/resources/images/ImageViewDemo.png",
        "org/jdesktop/swingx/demos/imageview/resources/images/flower.thumbnail.jpg"
    }
)
@SuppressWarnings("serial")
public class ImageViewDemo extends DefaultDemoPanel {
    private JXImageView imageView;
    private JButton openButton;
    private JButton saveButton;
    private JButton rotateCWButton;
    private JButton rotateCCWButton;
    private JButton zoomInButton;
    private JButton zoomOutButton;
    private JToggleButton dragEnabledButton;
    
    /**
     * main method allows us to run as a standalone demo.
     */
    public static void main(String[] args) {
        runDemo(ImageViewDemo.class);
    }

    /**
     * {@inheritDoc}
     */
    protected void createDemo() {
        setLayout(new BorderLayout());
        
        imageView = new JXImageView();
        imageView.setName("imageView");
        add(imageView);
        
        JPanel sidebar = new JPanel(new VerticalLayout());
        openButton = new JButton();
        openButton.setName("openButton");
        sidebar.add(openButton);
        
        saveButton = new JButton();
        saveButton.setName("saveButton");
        sidebar.add(saveButton);
        
        rotateCWButton = new JButton();
        rotateCWButton.setName("rotateCW");
        sidebar.add(rotateCWButton);
        
        rotateCCWButton = new JButton();
        rotateCCWButton.setName("rotateCCW");
        sidebar.add(rotateCCWButton);
        
        zoomInButton = new JButton();
        zoomInButton.setName("zoomIn");
        sidebar.add(zoomInButton);
        
        zoomOutButton = new JButton();
        zoomOutButton.setName("zoomOut");
        sidebar.add(zoomOutButton);
        
        dragEnabledButton = new JToggleButton();
        dragEnabledButton.setName("dragEnabled");
        sidebar.add(dragEnabledButton);
        
        add(sidebar, BorderLayout.LINE_START);
    }
    
    /**
     * {@inheritDoc}
     */
    protected void bind() {
        openButton.addActionListener(imageView.getOpenAction());
        saveButton.addActionListener(imageView.getSaveAction());
        rotateCWButton.addActionListener(imageView.getRotateClockwiseAction());
        rotateCCWButton.addActionListener(imageView.getRotateCounterClockwiseAction());
        zoomInButton.addActionListener(imageView.getZoomInAction());
        zoomOutButton.addActionListener(imageView.getZoomOutAction());
        Bindings.createAutoBinding(READ,
                dragEnabledButton, BeanProperty.create("selected"),
                imageView, BeanProperty.create("dragEnabled")).bind();
    }
}
