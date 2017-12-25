/*
 * $Id: MultiThumbSliderDemo.java 4097 2011-11-30 19:22:13Z kschaefe $
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
package org.jdesktop.swingx.demos.multithumbslider;

import javax.swing.JLabel;

import org.jdesktop.swingxset.DefaultDemoPanel;

import com.sun.swingset3.DemoProperties;

/**
 * A demo for the {@code JXMultiThumbSlider}.
 *
 * @author Karl George Schaefer
 */
@DemoProperties(
    value = "JXMultiThumbSlider Demo",
    category = "Controls",
    description = "Demonstrates JXMultiThumbSlider, a control containing one or more thumbs on the same slider.",
    sourceFiles = {
        "org/jdesktop/swingx/demos/multithumbslider/MultiThumbSliderDemo.java",
        "org/jdesktop/swingx/demos/multithumbslider/resources/MultiThumbSliderDemo.properties",
        "org/jdesktop/swingx/demos/multithumbslider/resources/MultiThumbSliderDemo.html",
        "org/jdesktop/swingx/demos/multithumbslider/resources/images/MultiThumbSliderDemo.png"
    }
)
@SuppressWarnings("serial")
//TODO complete the demo
public class MultiThumbSliderDemo extends DefaultDemoPanel {
    
    /**
     * main method allows us to run as a standalone demo.
     */
    public static void main(String[] args) {
        runDemo(MultiThumbSliderDemo.class);
    }

    protected void createDemo() {
        add(new JLabel("Not Implemented"));
    }
}
