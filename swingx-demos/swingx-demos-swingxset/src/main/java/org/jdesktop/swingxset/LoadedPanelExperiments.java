/*
 * $Id: LoadedPanelExperiments.java 4097 2011-11-30 19:22:13Z kschaefe $
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
 *
 */
package org.jdesktop.swingxset;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.MultiSplitLayout;
import org.jdesktop.swingx.SwingXUtilities;

/**
 * 
 */
public class LoadedPanelExperiments {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(LoadedPanelExperiments.class.getName());
    
    private JComponent createSplitPane() {
        final JComponent demoContainer = new JXPanel();
        demoContainer.setLayout(new BorderLayout()); //BoxLayout(demoContainer, BoxLayout.LINE_AXIS));
        demoContainer.setBorder(BorderFactory.createLineBorder(Color.RED));
        
//      <snip> MultiSplit layout declaration
        String layout = 
            "(ROW " +
                 "(LEAF name=selector weight=0.3)" +
                 "(COLUMN weight=0.7 " +
                     "(LEAF name= demo weight=0.7)" +
                     "(LEAF name=source weight=0.3)" +
                 ")" +
            ")"; 
        MultiSplitLayout multiSplitLayout = new MultiSplitLayout(MultiSplitLayout.parseModel(layout));
//        </snip>
        JXMultiSplitPane splitPane = new JXMultiSplitPane();
        splitPane.setLayout(multiSplitLayout);
        splitPane.add("selector", createButtonStack(demoContainer));
        splitPane.add("demo", demoContainer);
        return splitPane;
    }


    private JComponent createPlainContent() {
        final JComponent demoContainer = new JXPanel(new BorderLayout());
        demoContainer.setBorder(BorderFactory.createLineBorder(Color.RED));
        JComponent splitPane = new JXPanel(new BorderLayout());
        splitPane.add(createButtonStack(demoContainer), BorderLayout.WEST);
        splitPane.add(demoContainer);
        return splitPane;
    }
    

    private JComponent createButtonStack(JComponent demoContainer) {
        JComponent buttonStack = new JXTaskPaneContainer();
        JXTaskPane taskPane = new JXTaskPane();
        taskPane.setTitle("demo");
        taskPane.add(createTextAction(demoContainer));
        taskPane.add(createPageAction(demoContainer));
        buttonStack.add(taskPane);
        return buttonStack;
    }
    
    /**
     * @param demoContainer
     * @return
     */
    private Action createTextAction(final JComponent demoContainer) {
        Action action = new AbstractAction("add editor - setText") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                demoContainer.removeAll();
                demoContainer.add(new JScrollPane(createEditorSetText()));
                JXMultiSplitPane pane = SwingXUtilities.getAncestor(JXMultiSplitPane.class, demoContainer);
                if (pane != null) {
                    pane.revalidate();
                } else {
                    demoContainer.revalidate();
                }
                
            }
        };
        return action;
    }

    /**
     * @param demoContainer
     * @return
     */
    private Action createPageAction(final JComponent demoContainer) {
        Action page = new AbstractAction("add editor - setPage") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                demoContainer.removeAll();
                demoContainer.add(new JScrollPane(createEditorSetPage()));
                demoContainer.revalidate();
                
            }
        };
        return page;
    }
    /**
     * @return
     */
    protected JComponent createEditorSetPage() {
        final JEditorPane editor = createEditor();
        URL descriptionURL = getHTMLDescription();
        try {
            editor.setPage(descriptionURL);
        } catch (IOException e) {
            System.err.println("couldn't load description from URL:" + descriptionURL);
        }
        PropertyChangeListener l = new PropertyChangeListener() {
            
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                JXMultiSplitPane pane = SwingXUtilities.getAncestor(JXMultiSplitPane.class, editor);
                if (pane != null) {
                    MultiSplitLayout layout = pane.getMultiSplitLayout();
                    layout.layoutByWeight(pane.getParent());
                } 

            }
        };
        editor.addPropertyChangeListener("page", l);
        return editor;
    }
    /**
     * @return
     */
    protected JComponent createEditorSetText() {
        final JEditorPane editor = createEditor();
        editor.setText(dummyText);
        return editor;
    }

    /**
     * @return
     */
    private JEditorPane createEditor() {
        final JEditorPane editor = new JEditorPane();
        editor.setContentType("text/html");
        editor.setEditable(false);
        editor.setOpaque(true);
        return editor;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createFrame(new LoadedPanelExperiments().createPlainContent(), false);
                createFrame(new LoadedPanelExperiments().createSplitPane(), true);
            }

            /**
             * @param content
             * @param split 
             */
            private JXFrame createFrame(JComponent content, boolean split) {
                JXFrame frame = new JXFrame("LoadedPanelExperiments" + (split ? " MultiSplitPane" : " Plain"));
                frame.add(content);
                frame.setSize(400, 200);
                if (split) {
                    frame.setLocation(frame.getLocation().x + 400, frame.getLocation().y);
                }
                frame.setVisible(true);
                return frame;
            }
        });
    }

    public URL getHTMLDescription() {
        // by default look for an html file with the same name as the demo class
        return getClass().getResource(htmlURL);    
    }

    private String htmlURL = "MultiSplitPaneDemo.html";
    
    String dummyText = 
        "<html>" 
    +" <head>"
    +"  <title>JXMultiSplitPane Demo</title>"
    +"</head>"
    +"<body>"
    +"JXMultiSplitPane is a container that can be split into multiple resizeable"
    +"areas. The layout is configured using MultiSplitLayout layout manager."
    +"<p>"
    +"The MultiSplitLayout layout manager recursively arranges its components in"
    +"row and column groups called Splits. Elements of the layout are"
    +"separated by gaps called Dividers. The overall layout is defined with a"
    +"simple tree model whose nodes are instances of MultiSplitLayout.Split,"
    +"MultiSplitLayout. Divider, and MultiSplitLayout.Leaf. Named Leaf nodes"
    +"represent the space allocated to a component that was added with a"
    +"constraint that matches the Leaf's name. Extra space is distributed among"
    +"row/column siblings according to their 0.0 to 1.0 weight. If no weights"
    +"are specified then the last sibling always gets all of the extra space,"
    +"or space reduction."
    +"<p>"
    +"Although MultiSplitLayout can be used with any Container, it's the default"
    +"layout manager for JXMultiSplitPane. JXMultiSplitPane supports"
    +"interactively dragging the Dividers, accessibility, and other features"
    +"associated with split panes."
    +"</body>"
    +"</html>";

}
