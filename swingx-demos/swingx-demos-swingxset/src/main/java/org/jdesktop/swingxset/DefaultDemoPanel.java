/*
 * $Id: DefaultDemoPanel.java 4097 2011-11-30 19:22:13Z kschaefe $
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
package org.jdesktop.swingxset;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationActionMap;
import org.jdesktop.swingx.JXPanel;

import com.sun.swingset3.DemoProperties;

/**
 * {@code DefaultDemoPanel} ensures the creation of demos using a standard format.
 * <ol>
 * <li>Create the demo components.</li>
 * <li>Inject properties and resources.</li>
 * <li>Bind components together.</li>
 * 
 * @author Karl George Schaefer
 */
public abstract class DefaultDemoPanel extends JXPanel {
    /**
     * The application action map for this demo.
     */
    protected final ApplicationActionMap map;
    
    /**
     * Used to ensure that DefaultDemoPanel.injectResources is called.
     * <p>
     * TODO replace with JSR-305
     * <a href="http://code.google.com/p/jsr-305/source/browse/trunk/ri/src/main/java/javax/annotation/OverridingMethodsMustInvokeSuper.java">
     * OverridingMethodsMustInvokeSuper</a>
     */
    private boolean injectRan = false;
    
    public DefaultDemoPanel() {
        createDemo();
        injectResources();
        
        if (!injectRan) {
            throw new IllegalStateException("must call super.injectReources");
        }
        
        map = Application.getInstance().getContext().getActionMap(this);

        bind();
    }

    /**
     * A utility method for demo panels to run standalone. Should be invoked in main.
     * 
     * @param clazz
     *            the class to run
     */
    protected static void runDemo(final Class<? extends DefaultDemoPanel> clazz) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    JFrame frame = new JFrame(clazz.getAnnotation(DemoProperties.class).value());
                    
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.getContentPane().add(clazz.newInstance());
                    frame.setPreferredSize(new Dimension(800, 600));
                    frame.pack();
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    /**
     * This method creates the demo components and layout.  This method is called by the constructor.
     */
    protected abstract void createDemo();
    
    /**
     * This method injects resource into the demo.  This method is called by the constructor.
     * <p>
     * Overriding classes must call super, failure to do so will result in an error.
     */
    protected void injectResources() {
        injectRan = true;
        Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(this);
    }
    
    /**
     * This method binds objects together.  This method is called by the constructor.
     */
    protected void bind() {
        //does nothing
    }
}
