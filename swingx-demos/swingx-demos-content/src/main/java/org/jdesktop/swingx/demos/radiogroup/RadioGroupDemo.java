/*
 * $Id: RadioGroupDemo.java 4097 2011-11-30 19:22:13Z kschaefe $
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
package org.jdesktop.swingx.demos.radiogroup;

import javax.swing.JButton;

import org.jdesktop.swingx.JXRadioGroup;
import org.jdesktop.swingxset.DefaultDemoPanel;

import com.sun.swingset3.DemoProperties;

/**
 * A demo for the {@code JXRadioGroup}.
 *
 * @author Karl George Schaefer
 */
@DemoProperties(
    value = "JXRadioGroup Demo",
    category = "Controls",
    description = "Demonstrates JXRadioGroup, a control for creating groups of buttons.",
    sourceFiles = {
        "org/jdesktop/swingx/demos/radiogroup/RadioGroupDemo.java",
        "org/jdesktop/swingx/demos/radiogroup/resources/RadioGroupDemo.properties",
        "org/jdesktop/swingx/demos/radiogroup/resources/RadioGroupDemo.html",
        "org/jdesktop/swingx/demos/radiogroup/resources/images/RadioGroupDemo.png"
    }
)
@SuppressWarnings("serial")
public class RadioGroupDemo extends DefaultDemoPanel {
    
    /**
     * main method allows us to run as a standalone demo.
     */
    public static void main(String[] args) {
        runDemo(RadioGroupDemo.class);
    }

    protected void createDemo() {
        JXRadioGroup<String> rg = JXRadioGroup.create(new String[]{"a", "b", "c"});
//        rg.setText("Title");
//        rg.setMnemonic(KeyEvent.VK_T);
        add(rg);
        add(new JButton("something else"));
    }
}
