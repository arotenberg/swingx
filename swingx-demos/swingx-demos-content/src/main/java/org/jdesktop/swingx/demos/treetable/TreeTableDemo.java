/*
 * $Id: TreeTableDemo.java 4185 2012-06-22 13:39:48Z kschaefe $
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
package org.jdesktop.swingx.demos.treetable;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;
import javax.swing.tree.TreePath;

import org.jdesktop.application.Action;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.demos.tree.TreeDemoIconValues.LazyLoadingIconValue;
import org.jdesktop.swingx.demos.tree.XTreeDemo;
import org.jdesktop.swingx.event.AbstractInputEventDispatcher;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.DefaultTreeRenderer;
import org.jdesktop.swingx.renderer.IconValue;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.renderer.StringValues;
import org.jdesktop.swingx.table.ColumnFactory;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.jdesktop.swingx.util.PaintUtils;
import org.jdesktop.swingxset.JXDemoFrame;
import org.jdesktop.swingxset.util.ComponentModels;
import org.jdesktop.swingxset.util.DemoUtils;

import com.sun.swingset3.DemoProperties;

/**
 * A demo for the {@code JXTreeTable}.
 *
 * @author Karl George Schaefer
 */
@DemoProperties(
    value = "JXTreeTable Demo",
    category = "Data",
    description = "Demonstrates JXTreeTable, a tree-based, grid component.",
    sourceFiles = {
            "org/jdesktop/swingx/demos/treetable/TreeTableDemo.java",
            "org/jdesktop/swingx/demos/tree/TreeDemoIconValues.java",
        "org/jdesktop/swingxset/JXDemoFrame.java",
        "org/jdesktop/swingx/demos/treetable/resources/TreeTableDemo.properties"
    }
)
@SuppressWarnings("serial")
public class TreeTableDemo extends JPanel {
    
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(TreeTableDemo.class
            .getName());
    
    private JXTreeTable treeTable;
    private boolean initialized;
    private JButton refreshButton;
    private JButton expandButton;
    private JButton collapseButton;
    private AbstractInputEventDispatcher inputEventDispatcher;
    private AbstractHighlighter mouseOverHighlighter;
    
    public TreeTableDemo() {
        super(new BorderLayout());
        initComponents();
        configureComponents();
        DemoUtils.injectResources(this);
        bind();
    }
    
//---------------- public api for Binding/Action control
    

    @Action
    public void refreshModel() {
        treeTable.setTreeTableModel(createTreeModel());
        expandAll();
        treeTable.packColumn(treeTable.getHierarchicalColumn(), -1);
    }
    
    // <snip> JXTreeTable convenience api
    // expand/collapse all nodes
    @Action
    public void expandAll() {
        treeTable.expandAll();
    }

    @Action
    public void collapseAll() {
        treeTable.collapseAll();
    }
    // </snip>
    
//------------------- config/bind  
    

    private void bind() {
        // <snip>JXTreeTable column customization
        // configure and install a custom columnFactory, arguably data related ;-)
        ColumnFactory factory = new ColumnFactory() {
            String[] columnNameKeys = { "componentType", "componentName", "componentLocation", "componentSize" };

            @Override
            public void configureTableColumn(TableModel model,
                    TableColumnExt columnExt) {
                super.configureTableColumn(model, columnExt);
                if (columnExt.getModelIndex() < columnNameKeys.length) {
                    columnExt.setTitle(DemoUtils.getResourceString(TreeTableDemo.class, 
                            columnNameKeys[columnExt.getModelIndex()]));
                }
            }
            
        };
        treeTable.setColumnFactory(factory);
        // </snip>

    }
   
    private void configureComponents() {
        // <snip> JXTreeTable rendering
        // StringValue provides node text, used in hierarchical column
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
                    return simpleName;
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
        // <snip> JXTreeTable rendering
        // IconValue provides node icon (same as in XTreeDemo)
        IconValue iv = new LazyLoadingIconValue(XTreeDemo.class, keyValue, "fallback.png");
        // create and set a tree renderer using the custom Icon-/StringValue
        treeTable.setTreeCellRenderer(new DefaultTreeRenderer(iv, sv));
        // string representation for use of Dimension/Point class
        StringValue locSize = new StringValue() {
            
            @Override
            public String getString(Object value) {
                int x;
                int y;
                if (value instanceof Dimension) {
                    x = ((Dimension) value).width;
                    y = ((Dimension) value).height;
                } else if (value instanceof Point) {
                    x = ((Point) value).x;
                    y = ((Point) value).y;
                } else {
                    return StringValues.TO_STRING.getString(value);
                }
                return "(" + x + ", " + y + ")";
            }
        };
        treeTable.setDefaultRenderer(Point.class, new DefaultTableRenderer(locSize, JLabel.CENTER));
        treeTable.setDefaultRenderer(Dimension.class, treeTable.getDefaultRenderer(Point.class));
        // </snip>
        
        mouseOverHighlighter = new ColorHighlighter(HighlightPredicate.NEVER, 
                PaintUtils.setSaturation(Color.MAGENTA, 0.3f), null);
        treeTable.addHighlighter(mouseOverHighlighter);
        
        treeTable.setColumnControlVisible(true);
        
        
        refreshButton.setAction(DemoUtils.getAction(this, "refreshModel"));
        expandButton.setAction(DemoUtils.getAction(this, "expandAll"));
        collapseButton.setAction(DemoUtils.getAction(this, "collapseAll"));
        
        // Demo specific config
        DemoUtils.setSnippet("JXTreeTable convenience api", expandButton, collapseButton);
        DemoUtils.setSnippet("JXTreeTable rendering", treeTable);
    }

    
    // <snip> Input-/FocusEvent notification 
    // update Highlighter's predicate to highlight the tree row
    // which contains the component under the current mouse position
    protected void updateHighlighter(Component component) {
        mouseOverHighlighter.setHighlightPredicate(HighlightPredicate.NEVER);
        if (component != null) {
        
            List<Component> pathList = new ArrayList<Component>();
            while (component != null) {
                pathList.add(0, component);
                component = component.getParent();
            }
            final TreePath treePath = new TreePath(pathList.toArray());
            treeTable.scrollPathToVisible(treePath);
            HighlightPredicate predicate = new HighlightPredicate() {
                
                @Override
                public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
                    return adapter.row == treeTable.getRowForPath(treePath);
                }
            };
            mouseOverHighlighter.setHighlightPredicate(predicate);
            // </snip>

        }
    }

    /**
     * Overridden to create and install the component tree model.
     */
    @Override
    public void addNotify() {
        super.addNotify();
        if (!initialized) {
            initialized = true;
            refreshModel();
        }
        installInputEventListener();
    }

    @Override
    public void removeNotify() {
        uninstallInputEventListener();
        super.removeNotify();
    }
    // <snip> Input-/FocusEvent notification 
    // install custom dispatcher with demo frame
    private void installInputEventListener() {
        if (!(SwingUtilities.getWindowAncestor(this) instanceof JXDemoFrame)) return;
        JXDemoFrame window = (JXDemoFrame) SwingUtilities.getWindowAncestor(this);
        if (inputEventDispatcher == null) {
            inputEventDispatcher = new AbstractInputEventDispatcher() {
                // updates Highlighter for mouseEntered/mouseExited
                // of all components in the frame's container hierarchy
                @Override
                protected void processMouseEvent(MouseEvent e) {
                    if (MouseEvent.MOUSE_ENTERED == e.getID()) {
                        updateHighlighter(e.getComponent());
                    } else if (MouseEvent.MOUSE_EXITED == e.getID()) {
                        updateHighlighter(null);
                    }
                }
                
            };
        }
        window.setInputEventDispatcher(inputEventDispatcher); 
        // </snip>

    }

    private void uninstallInputEventListener() {
        if (!(SwingUtilities.getWindowAncestor(this) instanceof JXDemoFrame)) return;
        JXDemoFrame window = (JXDemoFrame) SwingUtilities.getWindowAncestor(this);
        window.setInputEventDispatcher(null);
        updateHighlighter(null);
        
    }

    private TreeTableModel createTreeModel() {
       Window window = SwingUtilities.getWindowAncestor(this);
       return ComponentModels.getTreeTableModel(window != null ? window : this);
    }

    //----------------- init
    
    private void initComponents() {
        treeTable = new JXTreeTable();
        treeTable.setName("componentTreeTable");
        add(new JScrollPane(treeTable));

        JComponent control = new JXPanel();
        refreshButton = new JButton();
        refreshButton.setName("refreshButton");

        expandButton = new JButton();
        expandButton.setName("expandTreeTableButton");
        
        collapseButton = new JButton();
        collapseButton.setName("collapseTreeTableButton");
        
//        control.add(refreshButton);
        control.add(expandButton);
        control.add(collapseButton);
        add(control, BorderLayout.SOUTH);

    }
    
    /**
     * main method allows us to run as a standalone demo.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame(TreeTableDemo.class.getAnnotation(DemoProperties.class).value());
                
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(new TreeTableDemo());
                frame.setPreferredSize(new Dimension(800, 600));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }


}

