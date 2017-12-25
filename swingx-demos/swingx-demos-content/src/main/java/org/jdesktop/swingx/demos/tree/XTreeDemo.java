/*
 * Created on 18.04.2008
 *
 */
package org.jdesktop.swingx.demos.tree;

/*
 * Copyright 2007-2008 Sun Microsystems, Inc.  All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Sun Microsystems nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.jdesktop.application.Action;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.demos.tree.TreeDemoIconValues.FilteredIconValue;
import org.jdesktop.swingx.demos.tree.TreeDemoIconValues.LazyLoadingIconValue;
import org.jdesktop.swingx.renderer.DefaultTreeRenderer;
import org.jdesktop.swingx.renderer.IconValue;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.renderer.StringValues;
import org.jdesktop.swingx.renderer.WrappingIconPanel;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.jdesktop.swingxset.util.ComponentModels;
import org.jdesktop.swingxset.util.DemoUtils;

import com.sun.swingset3.DemoProperties;

/**
 * JXTree Demo
 * 
 * PENDING JW: make editable to demonstrate terminate enhancement. 
 *
 *@author Jeanette Winzenburg, Berlin
 */
@DemoProperties(
        value = "JXTree Demo",
        category = "Data",
        description = "Demonstrates JXTree, an enhanced tree component",
        sourceFiles = {
                "org/jdesktop/swingx/demos/tree/XTreeDemo.java",
                "org/jdesktop/swingx/demos/tree/TreeDemoIconValues.java"
                }
)
public class XTreeDemo extends JPanel {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(XTreeDemo.class
            .getName());
    private JXTree tree;
    private JButton refreshButton;
    private JButton expandButton;
    private JButton collapseButton;
    /**
     * TreeDemo Constructor
     */
    public XTreeDemo() {
        super(new BorderLayout());
        initComponents();
        configureComponents();
        DemoUtils.injectResources(this);
        bind();
    }

//---------------- public api for Binding/Action control
    

    @Action
    public void refreshModel() {
        tree.setModel(createTreeModel());
    }
    // <snip> JXTree convenience api
    // expand/collapse all nodes
    @Action
    public void expandAll() {
        tree.expandAll();
    }

    @Action
    public void collapseAll() {
        tree.collapseAll();
    }
    // </snip>

//---------------- binding/configure
    
    private void configureComponents() {
        // <snip> JXTree rendering
        // StringValue provides node text: concat several 
        StringValue sv = new StringValue() {
            
            @Override
            public String getString(Object value) {
                if (value instanceof Component) {
                    Component component = (Component) value;
                    String simpleName = component.getClass().getSimpleName();
                    if (simpleName.length() == 0){
                        // anonymous class
                        simpleName = component.getClass().getSuperclass().getSimpleName();
                    }
                    return simpleName + "(" + component.getName() + ")";
                }
                return StringValues.TO_STRING.getString(value);
            }
        };
        // </snip>
        
        // StringValue for lazy icon loading
        StringValue keyValue = new StringValue() {
            
            @Override
            public String getString(Object value) {
                if (value == null) return "";
                String simpleClassName = value.getClass().getSimpleName();
                if (simpleClassName.length() == 0){
                    // anonymous class
                    simpleClassName = value.getClass().getSuperclass().getSimpleName();
                }
                return simpleClassName + ".png";
            }
        };
        // <snip> JXTree rendering
        // IconValue provides node icon 
        IconValue iv = new LazyLoadingIconValue(getClass(), keyValue, "fallback.png");
        // create and set a tree renderer using the custom Icon-/StringValue
        tree.setCellRenderer(new DefaultTreeRenderer(iv, sv));
        // </snip>
        tree.setRowHeight(-1);
        
        // <snip> JXTree rollover
        // enable and register a highlighter
        tree.setRolloverEnabled(true);
        tree.addHighlighter(createRolloverIconHighlighter(iv));
        // </snip>
        
        refreshButton.setAction(DemoUtils.getAction(this, "refreshModel"));
        expandButton.setAction(DemoUtils.getAction(this, "expandAll"));
        collapseButton.setAction(DemoUtils.getAction(this, "collapseAll"));
        
        // Demo specific config
        DemoUtils.setSnippet("JXTree convenience api", expandButton, collapseButton);
        DemoUtils.setSnippet("JXTree rendering", tree);
    }

    // <snip> JXTree rollover
    // custom implementation of Highlighter which highlights 
    // by changing the node icon on rollover
    private Highlighter createRolloverIconHighlighter(IconValue delegate) {
        // the icon look-up is left to an IconValue
        final IconValue iv = new FilteredIconValue(delegate);
        AbstractHighlighter hl = new AbstractHighlighter(HighlightPredicate.ROLLOVER_ROW) {

            /**
             * {@inheritDoc} <p>
             * 
             * Implemented to highlight by setting the node icon.
             */
            @Override
            protected Component doHighlight(Component component,
                    ComponentAdapter adapter) {
                Icon icon = iv.getIcon(adapter.getValue());
                if (icon != null) {
                    ((WrappingIconPanel) component).setIcon(icon);
                }
                return component;
            }
            // </snip>
            
            /**
             * {@inheritDoc} <p>
             * 
             * Implementated to return true if the component is a WrappingIconPanel,
             * a panel implemenation specialized for rendering tree nodes.
             * 
             */
            @Override
            protected boolean canHighlight(Component component,
                    ComponentAdapter adapter) {
                return component instanceof WrappingIconPanel;
            }
            
        };
        return hl;
    }

    
    private void bind() {
        // example model is component hierarchy of demo application
        // bind in addNotify
        tree.setModel(null);
    }

    /**
     * Overridden to create and install the component tree model.
     */
    @Override
    public void addNotify() {
        super.addNotify();
        if (tree.getModel() == null) {
            tree.setModel(createTreeModel());
        }
    }

    private TreeTableModel createTreeModel() {
       Window window = SwingUtilities.getWindowAncestor(this);
       return ComponentModels.getTreeTableModel(window != null ? window : this);
    }

//-------------- init ui
    /**
     * 
     */
    private void initComponents() {
        tree = new JXTree();
        tree.setName("componentTree");
        tree.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(new JScrollPane(tree), BorderLayout.CENTER);
        
        JComponent control = new JXPanel();
        refreshButton = new JButton();
        refreshButton.setName("refreshButton");

        expandButton = new JButton();
        expandButton.setName("expandButton");
        
        collapseButton = new JButton();
        collapseButton.setName("collapseButton");
        
//        control.add(refreshButton);
        control.add(expandButton);
        control.add(collapseButton);
        add(control, BorderLayout.SOUTH);
    }

    /**
     * main method allows us to run as a standalone demo.
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame(XTreeDemo.class.getAnnotation(DemoProperties.class).value());

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new XTreeDemo());
        frame.setPreferredSize(new Dimension(800, 600));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}


