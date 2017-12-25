/*
 * $Id: XEditorPaneDemo.java 4097 2011-11-30 19:22:13Z kschaefe $
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
package org.jdesktop.swingx.demos.xeditorpane;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.io.IOException;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationActionMap;
import org.jdesktop.swingx.JXEditorPane;
import org.jdesktop.swingx.appframework.StyledTextActions;

import com.sun.swingset3.DemoProperties;

/**
 * A demo for the {@code JXEditorPane}.
 *
 * @author Karl George Schaefer
 * @author Mark Davidson (original JXEditorPaneVisualCheck)
 */
@DemoProperties(
    value = "JXEditorPane Demo",
    category = "Text",
    description = "Demonstrates JXEditorPane, an extended text editing component",
    sourceFiles = {
        "org/jdesktop/swingx/demos/xeditorpane/XEditorPaneDemo.java",
        "org/jdesktop/swingx/demos/xeditorpane/resources/XEditorPaneDemo.properties",
        "org/jdesktop/swingx/demos/xeditorpane/resources/XEditorPaneDemo.html"
    }
)
@SuppressWarnings("serial")
//@ProxyActions({"bold", "italic", "underline"})
public class XEditorPaneDemo extends JPanel {
    private JToolBar toolbar;
    private JXEditorPane editor;
    
    private ApplicationActionMap map;
    
    /**
     * main method allows us to run as a standalone demo.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                StyledTextActions.install(Application.getInstance().getContext());
                
                JFrame frame = new JFrame(XEditorPaneDemo.class.getAnnotation(DemoProperties.class).value());
                
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(new XEditorPaneDemo());
                frame.setPreferredSize(new Dimension(800, 600));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
    
    public XEditorPaneDemo() {
        super(new BorderLayout());
        
        createXEditorPaneDemo();
        
        Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(this);
        
        bind();
    }

    private void createXEditorPaneDemo() {
        //PENDING hack around AppFramework bug #92
        toolbar = new JToolBar() {
            @Override
            protected void addImpl(Component comp, Object constraints, int index) {
                comp.setFocusable(false);
                super.addImpl(comp, constraints, index);
            }
        };
        add(toolbar, BorderLayout.NORTH);
        
        URL url = XEditorPaneDemo.class.getResource("resources/Bike.html");
        editor = null;
        
        try {
            editor = new JXEditorPane(url);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//        editor.setEditable(false);
        editor.setPreferredSize(new Dimension(600, 400));

//        toolbar.add(editor.getParagraphSelector());

        add(new JScrollPane(editor));
    }
    
    private void bind() {
        map = Application.getInstance().getContext().getActionMap(this);

        toolbar.add(map.get("cut"));
        toolbar.add(map.get("copy"));
        toolbar.add(map.get("paste"));
        toolbar.addSeparator();
        //TODO doesn't work this way because the action is uninstalled and reinstalled every call
//        toolbar.add(editor.getActionMap().get("undo"));
//        toolbar.add(editor.getActionMap().get("redo"));
        toolbar.addSeparator();
        toolbar.add(new JToggleButton(map.get("bold")));
        toolbar.add(new JToggleButton(map.get("italic")));
        toolbar.add(new JToggleButton(map.get("underline")));
        toolbar.add(editor.getParagraphSelector());
        
        //TODO this is the "SwingX" way to do proxy style actions.
//        //AbstractActionExt[] actions = new AbstractActionExt[14];
//        List<AbstractActionExt> actions = new ArrayList<AbstractActionExt>();
//        actions.add(ActionFactory.createTargetableAction("insert-break", "LF", "F"));
//
//        actions.add(ActionFactory.createTargetableAction("left-justify", "Left", "L", true,
//                                                          "position-group"));
//        actions.add(ActionFactory.createTargetableAction("center-justify", "Center", "C", true,
//                                                          "position-group"));
//        actions.add(ActionFactory.createTargetableAction("right-justify", "Right", "R", true,
//                                                          "position-group"));
//
//        actions.add(ActionFactory.createTargetableAction("font-bold", "Bold", "B", true));
//        actions.add(ActionFactory.createTargetableAction("font-italic", "Italic", "I", true));
//        actions.add(ActionFactory.createTargetableAction("font-underline", "Underline", "U", true));
//
//        actions.add(ActionFactory.createTargetableAction("InsertUnorderedList", "UL", "U", true));
//        actions.add(ActionFactory.createTargetableAction("InsertOrderedList", "OL", "O", true));
//        actions.add(ActionFactory.createTargetableAction("InsertHR", "HR", "H"));
//        ActionManager manager = ActionManager.getInstance();
//        
//        List<Object> actionNames = new ArrayList<Object>();
//        for (AbstractActionExt ext : actions) {
//            manager.addAction(ext);
//            actionNames.add(ext.getActionCommand());
//            
//        }
//        
//        // Populate the toolbar. Must use the ActionContainerFactory to ensure
//        // that toggle actions are supported.
//        ActionContainerFactory factory = new ActionContainerFactory(manager);
//
//        toolbar = factory.createToolBar(actionNames);
//        add(toolbar, BorderLayout.NORTH);
    }
}
