/*
 * $Id: MultiSplitPaneDemo.java 4097 2011-11-30 19:22:13Z kschaefe $
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
package org.jdesktop.swingx.demos.multisplitpane;

import java.awt.Dimension;
import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.BorderFactory;

import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.MultiSplitLayout;

import org.jdesktop.application.Application;

import com.sun.swingset3.DemoProperties;

/**
 * A demo for the {@code JXMultiSplitPane}.
 *
 * @author Karl George Schaefer, Luan O'Carroll
 */
@DemoProperties(
    value = "JXMultiSplitPane Demo",
    category = "Containers",
    description = "Demonstrates JXMultiSplitPane, a container that allows arbitrary resizing children.",
    sourceFiles = {
        "org/jdesktop/swingx/demos/multisplitpane/MultiSplitPaneDemo.java",
        "org/jdesktop/swingx/demos/multisplitpane/resources/MultiSplitPaneDemo.properties",
        "org/jdesktop/swingx/demos/multisplitpane/resources/MultiSplitPaneDemo.html",
        "org/jdesktop/swingx/demos/multisplitpane/resources/images/MultiSplitPaneDemo.png"
    }
)
@SuppressWarnings("serial")
public class MultiSplitPaneDemo extends JPanel {
    
    /**
     * main method allows us to run as a standalone demo.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame(MultiSplitPaneDemo.class.getAnnotation(DemoProperties.class).value());
                
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(new MultiSplitPaneDemo());
                frame.setPreferredSize(new Dimension(800, 600));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
    
    public MultiSplitPaneDemo() {
        createMultiSplitPaneDemo();
        
        Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(this);
        
        bind();
    }

    //TODO enable resource injection for the components in this demo
    private void createMultiSplitPaneDemo() {
      setLayout( new BorderLayout());

      JXMultiSplitPane msp = new JXMultiSplitPane();

      String layoutDef = "(COLUMN (ROW weight=0.8 (COLUMN weight=0.25 " +
          "(LEAF name=left.top weight=0.5) (LEAF name=left.middle weight=0.5))" +
          "(LEAF name=editor weight=0.75)) (LEAF name=bottom weight=0.2))";

      MultiSplitLayout.Node modelRoot = MultiSplitLayout.parseModel( layoutDef );
      msp.getMultiSplitLayout().setModel( modelRoot );

      msp.add( new JButton( "Left Top" ), "left.top" );
      msp.add( new JButton( "Left Middle" ), "left.middle" );
      msp.add( new JButton( "Editor" ), "editor" );
      msp.add( new JButton( "Bottom" ), "bottom" );

      // ADDING A BORDER TO THE MULTISPLITPANE CAUSES ALL SORTS OF ISSUES
      msp.setBorder( BorderFactory.createEmptyBorder( 4, 4, 4, 4 ) );

      add( msp, BorderLayout.CENTER );
    }
    
    private void bind() {
        //no bindings
    }
}
