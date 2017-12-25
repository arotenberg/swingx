/*
 * $Id: JXTreeTableIssues.java 4244 2012-10-02 13:05:04Z kleopatra $
 * 
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * California 95054, U.S.A. All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */
package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.TableModelEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTreeTable.TreeTableModelAdapter;
import org.jdesktop.swingx.JXTreeTableUnitTest.JXTreeTableA;
import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.hyperlink.AbstractHyperlinkAction;
import org.jdesktop.swingx.renderer.CellContext;
import org.jdesktop.swingx.renderer.CheckBoxProvider;
import org.jdesktop.swingx.renderer.ComponentProvider;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.DefaultTreeRenderer;
import org.jdesktop.swingx.renderer.HyperlinkProvider;
import org.jdesktop.swingx.renderer.LabelProvider;
import org.jdesktop.swingx.renderer.RendererVisualCheck.TextAreaProvider;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.renderer.StringValues;
import org.jdesktop.swingx.renderer.WrappingIconPanel;
import org.jdesktop.swingx.renderer.WrappingProvider;
import org.jdesktop.swingx.table.ColumnFactory;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.test.ActionMapTreeTableModel;
import org.jdesktop.swingx.test.ComponentTreeTableModel;
import org.jdesktop.swingx.test.TreeTableUtils;
import org.jdesktop.swingx.treetable.AbstractMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.FileSystemModel;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableNode;
import org.jdesktop.test.TableModelReport;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test to exposed known issues of <code>JXTreeTable</code>. <p>
 * 
 * Ideally, there would be at least one failing test method per open
 * issue in the issue tracker. Plus additional failing test methods for
 * not fully specified or not yet decided upon features/behaviour.<p>
 * 
 * Once the issues are fixed and the corresponding methods are passing, they
 * should be moved over to the XXTest. 
 * 
 * @author Jeanette Winzenburg
 */
public class JXTreeTableIssues extends InteractiveTestCase {
    private static final Logger LOG = Logger.getLogger(JXTreeTableIssues.class
            .getName());
    public static void main(String[] args) {
        setSystemLF(true);
        JXTreeTableIssues test = new JXTreeTableIssues();
        try {
//            test.runInteractiveTests();
//            test.runInteractiveTests(".*Combo.*");
//            test.runInteractiveTests(".*Text.*");
//            test.runInteractiveTests(".*TreeExpand.*");
//            test.runInteractiveTests("interactive.*EditWith.*");
//            test.runInteractiveTests("interactive.*Clip.*");
            test.runInteractive("Prototype");
//          test.runInteractiveTests("interactive.*CustomColor.*");
              
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }

    /**
     * Issue #1509: configure column width based on prototype not working at all 
     * for hierarchical column.
     * 
     */
    public void interactivePrototype() {
        ColumnFactory factory = new ColumnFactory() {

            @Override
            protected int calcPrototypeWidth(JXTable table,
                    TableColumnExt columnExt) {
                if (isHierarchicalPrototype(table, columnExt))  {
                    return calcHierarchicalPrototypeWidth((JXTreeTable) table, columnExt);
                }
                return super.calcPrototypeWidth(table, columnExt);
            }

            protected boolean isHierarchicalPrototype(JXTable table,
                    TableColumnExt columnExt) {
                return (table instanceof JXTreeTable) 
                        && ((JXTreeTable) table).getTreeTableModel().getHierarchicalColumn() == columnExt.getModelIndex()
                        && columnExt.getPrototypeValue() != null;
            }

            TreeCellRenderer dummy = new DefaultTreeCellRenderer();
            protected int calcHierarchicalPrototypeWidth(JXTreeTable table,
                    TableColumnExt columnExt) {
                JXTree renderer = (JXTree) getCellRenderer(table, columnExt);
                // commented lines would be the obvious step down into the "real" sizing
                // requirements, but giving reasonable result due to internal black magic
//                TreeCellRenderer treeRenderer = renderer.getCellRenderer();
//                Component comp = treeRenderer.getTreeCellRendererComponent(renderer, 
//                        columnExt.getPrototypeValue(), false, false, false, -1, false);
                // instead, measure the dummy
                Component comp = dummy.getTreeCellRendererComponent(renderer, 
                        columnExt.getPrototypeValue(), false, false, false, -1, false);
                
                return Math.max(renderer.getPreferredSize().width, comp.getPreferredSize().width);
            }
            
        };
        JXTreeTable table = new JXTreeTable();
        table.setColumnFactory(factory);
        table.setTreeTableModel(new FileSystemModel());
        table.getColumnExt(0).setPrototypeValue("long longer longest still not enough to really see some effect of the prototype if availabel");
        // Issue #1510: prototype value handling broken in underlying JXTable
        // need to manually force the config
        table.getColumnFactory().configureColumnWidths(table, table.getColumnExt(0));
        showWithScrollingInFrame(table, "Issue #1509: prototype on hierarchical");
    }
    
    /**
     * Issue #1379-swingx: support access to underlying TreeTableModel in TreeTableModelAdapter.
     * 
     * PENDING JW:
     * The bind isn't really fool-proof tight: its possible for subclasses to bind an
     * arbitrary tree to a TreeTableModelAdapter.
     * Dont want to change, though, as client code might have mis-used ... 
     */
    @Test(expected = IllegalStateException.class)
    public void testTreeTableAdapterBind() {
        JXTreeTableA table = new JXTreeTableA(createActionTreeModel());
        TreeTableModelAdapter model = table.createAdapter(new JTree()); 
        model.bind(table);
    }
   

    /**
     * Custom renderer colors of Swingx DefaultTreeRenderer not respected.
     * (same in J/X/Tree). 
     * 
     * A bit surprising - probably due to the half-hearted support (backward
     * compatibility) of per-provider colors: they are set by the glueing
     * renderer to the provider's default visuals. Which is useless if the
     * provider is a wrapping provider - the wrappee's default visuals are unchanged.
     * 
     * PENDING JW: think about complete removal. Client code should move over
     * completely to highlighter/renderer separation anyway.
     * 
     * 
     */
    public void interactiveXRendererCustomColor() {
        JXTreeTable treeTable = new JXTreeTable(new FileSystemModel());
        treeTable.addHighlighter(HighlighterFactory.createSimpleStriping());
        DefaultTreeRenderer swingx = new DefaultTreeRenderer();
        // in a treetable this has no effect: treetable.applyRenderer 
        // internally resets them to the same colors as tree itself
        // (configured by the table's highlighters
        swingx.setBackground(Color.YELLOW);
        treeTable.setTreeCellRenderer(swingx);
        JTree tree = new JXTree(treeTable.getTreeTableModel());
        DefaultTreeRenderer other = new DefaultTreeRenderer();
        other.setBackground(Color.YELLOW);
//        other.setBackgroundSelectionColor(Color.RED);
        tree.setCellRenderer(other);
        JXFrame frame = wrapWithScrollingInFrame(treeTable, tree, "swingx renderers - highlight complete cell");
        frame.setVisible(true);  
    }

    /**
     * Custom renderer colors of core DefaultTreeCellRenderer not respected.
     * This is intentional: treeTable's highlighters must rule, so the
     * renderer colors are used to force the treecellrenderer to use the 
     * correct values.
     */
    public void interactiveCoreRendererCustomColor() {
        JXTreeTable treeTable = new JXTreeTable(new FileSystemModel());
        treeTable.addHighlighter(HighlighterFactory.createSimpleStriping());
        DefaultTreeCellRenderer legacy = createBackgroundTreeRenderer();
        // in a treetable this has no effect: treetable.applyRenderer 
        // internally resets them to the same colors as tree itself
        // (configured by the table's highlighters
        legacy.setBackgroundNonSelectionColor(Color.YELLOW);
        legacy.setBackgroundSelectionColor(Color.RED);
        treeTable.setTreeCellRenderer(legacy);
        JTree tree = new JXTree(treeTable.getTreeTableModel());
        DefaultTreeCellRenderer other = createBackgroundTreeRenderer();
        other.setBackgroundNonSelectionColor(Color.YELLOW);
        other.setBackgroundSelectionColor(Color.RED);
        tree.setCellRenderer(other);
        JXFrame frame = wrapWithScrollingInFrame(treeTable, tree, "legacy renderers - highlight complete cell");
        frame.setVisible(true);  
    }

    private DefaultTreeCellRenderer createBackgroundTreeRenderer() {
        DefaultTreeCellRenderer legacy = new DefaultTreeCellRenderer() {

            @Override
            public Component getTreeCellRendererComponent(JTree tree,
                    Object value, boolean sel, boolean expanded, boolean leaf,
                    int row, boolean hasFocus) {
                Component comp = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
                        row, hasFocus);
                if (sel) {
                    comp.setBackground(getBackgroundSelectionColor());
                } else {
                    comp.setBackground(getBackgroundNonSelectionColor());
                }
                return comp;
            }
            
            
        };
        return legacy;
    }


    
    /**
     * Issue #493-swingx: incorrect table events fired.
     * Issue #592-swingx: (no structureChanged table events) is a special
     *   case of the former.
     * 
     * Here: add support to prevent a structureChanged even when setting
     * the root. May be required if the columns are stable and the
     * model lazily loaded. Quick hack would be to add a clientProperty?
     * 
     * @throws InvocationTargetException 
     * @throws InterruptedException 
     */
    public void testTableEventOnSetRootNoStructureChange() throws InterruptedException, InvocationTargetException {
        TreeTableModel model = createCustomTreeTableModelFromDefault();
        final JXTreeTable table = new JXTreeTable(model);
        table.setRootVisible(true);
        table.expandAll();
        final TableModelReport report = new TableModelReport();
        table.getModel().addTableModelListener(report);
        ((DefaultTreeTableModel) model).setRoot(new DefaultMutableTreeTableNode("other"));  
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                assertEquals("tableModel must have fired", 1, report.getEventCount());
                assertTrue("event type must be dataChanged " + TableModelReport.printEvent(report.getLastEvent()), 
                        report.isDataChanged(report.getLastEvent()));
            }
        });        
        
    }



    /**
     * Issue #576-swingx: sluggish scrolling (?).
     * Here - use default model
     */
    public void interactiveScrollAlternateHighlightDefaultModel() {
        final JXTable table = new JXTable(0, 6);
        DefaultMutableTreeTableNode root = new DefaultMutableTreeTableNode("root");
        for (int i = 0; i < 5000; i++) {
            root.insert(new DefaultMutableTreeTableNode(i), i);
        }
        final JXTreeTable treeTable = new JXTreeTable(new DefaultTreeTableModel(root));
        treeTable.expandAll();
        table.setModel(treeTable.getModel());
        final Highlighter hl =
            HighlighterFactory.createAlternateStriping(UIManager.getColor("Panel.background"),
            Color.WHITE);
        treeTable.setHighlighters(hl);
        table.setHighlighters(hl);
        final JXFrame frame = wrapWithScrollingInFrame(treeTable, table, "sluggish scrolling");
        Action toggleHighlighter = new AbstractActionExt("toggle highlighter") {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (treeTable.getHighlighters().length == 0) {
                    treeTable.addHighlighter(hl);
                    table.addHighlighter(hl);
                } else {
                    treeTable.removeHighlighter(hl);
                    table.removeHighlighter(hl);
                }
                
            }
            
        };
        Action scroll = new AbstractActionExt("start scroll") {

            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < table.getRowCount(); i++) {
                    table.scrollRowToVisible(i);
                    treeTable.scrollRowToVisible(i);
                }
                
            }
            
        };
        addAction(frame, toggleHighlighter);
        addAction(frame, scroll);
        frame.setVisible(true);
    }
    

    /**
     * Issue #576-swingx: sluggish scrolling (?)
     * 
     * Here: use FileSystemModel
     */
    public void interactiveScrollAlternateHighlight() {
        final JXTable table = new JXTable(0, 6);
        final JXTreeTable treeTable = new JXTreeTable(new FileSystemModel());
        final Highlighter hl =
            HighlighterFactory.createAlternateStriping(UIManager.getColor("Panel.background"),
            Color.WHITE);
        treeTable.setHighlighters(hl);
        table.setHighlighters(hl);
        final JXFrame frame = wrapWithScrollingInFrame(treeTable, table, "sluggish scrolling");
        Action expand = new AbstractActionExt("start expand") {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < 5000; i++) {
                    treeTable.expandRow(i);
                }
                table.setModel(treeTable.getModel());
            }
            
        };
        Action toggleHighlighter = new AbstractActionExt("toggle highlighter") {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (treeTable.getHighlighters().length == 0) {
                    treeTable.addHighlighter(hl);
                    table.addHighlighter(hl);
                } else {
                    treeTable.removeHighlighter(hl);
                    table.removeHighlighter(hl);
                }
                
            }
            
        };
        Action scroll = new AbstractActionExt("start scroll") {

            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < table.getRowCount(); i++) {
                    table.scrollRowToVisible(i);
                    treeTable.scrollRowToVisible(i);
                }
                
            }
            
        };
        addAction(frame, expand);
        addAction(frame, toggleHighlighter);
        addAction(frame, scroll);
        frame.setVisible(true);
    }
    

    /**
     * Issue #493-swingx: JXTreeTable.TreeTableModelAdapter: Inconsistency
     * firing update.
     * 
     * Test update events after updating table.
     * 
     * from tiberiu@dev.java.net
     * 
     * @throws InvocationTargetException 
     * @throws InterruptedException 
     */
    public void testTableEventUpdateOnTreeTableSetValueForRoot() throws InterruptedException, InvocationTargetException {
        TreeTableModel model = createCustomTreeTableModelFromDefault();
        final JXTreeTable table = new JXTreeTable(model);
        table.setRootVisible(true);
        table.expandAll();
        final int row = 0;
        // sanity
        assertEquals("JTree", table.getValueAt(row, 0).toString());
        assertTrue("root must be editable", table.getModel().isCellEditable(0, 0));
        final TableModelReport report = new TableModelReport();
        table.getModel().addTableModelListener(report);
        // doesn't fire or isn't detectable? 
        // Problem was: model was not-editable.
        table.setValueAt("games", row, 0);
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                assertEquals("tableModel must have fired", 1, report.getEventCount());
                assertEquals("the event type must be update " + TableModelReport.printEvent(report.getLastEvent())
                        , 1, report.getUpdateEventCount());
                TableModelEvent event = report.getLastUpdateEvent();
                assertEquals("the updated row ", row, event.getFirstRow());
            }
        });        
    }

    // -------------- interactive tests


    /**
     * Issue #493-swingx: JXTreeTable.TreeTableModelAdapter: Inconsistency
     * firing update on a recursive delete on a parent node.
     * 
     * By recursive delete on a parent node it is understood that first we 
     * remove its children and then the parent node. After each child removed
     * we are making an update over the parent. During this update the problem 
     * occurs: the index row for the parent is -1 and hence it is made an update
     * over the row -1 (the header) and as it can be seen the preffered widths
     * of column header are not respected anymore and are restored to the default
     * preferences (all equal).
     * 
     * from tiberiu@dev.java.net
     */
    public void interactiveTreeTableModelAdapterDeleteUpdate() {
        final DefaultTreeTableModel customTreeTableModel = (DefaultTreeTableModel) 
            createCustomTreeTableModelFromDefault();
        final JXTreeTable table = new JXTreeTable(customTreeTableModel);
        table.setRootVisible(true);
        table.expandAll();
        table.getColumn("A").setPreferredWidth(100);
        table.getColumn("A").setMinWidth(100);
        table.getColumn("A").setMaxWidth(100);
        JXTree xtree = new JXTree(customTreeTableModel);
        xtree.setRootVisible(true);
        xtree.expandAll();
        final JXFrame frame = wrapWithScrollingInFrame(table, xtree,
                "JXTreeTable.TreeTableModelAdapter: Inconsistency firing update on recursive delete");
        final MutableTreeTableNode deletedNode = (MutableTreeTableNode) table.getPathForRow(6).getLastPathComponent();
        MutableTreeTableNode child1 = (MutableTreeTableNode) table.getPathForRow(6+1).getLastPathComponent();
        MutableTreeTableNode child2 = (MutableTreeTableNode) table.getPathForRow(6+2).getLastPathComponent();
        MutableTreeTableNode child3 = (MutableTreeTableNode) table.getPathForRow(6+3).getLastPathComponent();
        MutableTreeTableNode child4 = (MutableTreeTableNode) table.getPathForRow(6+4).getLastPathComponent();
        
        final MutableTreeTableNode[] children = {child1, child2, child3, child4 };
        final String[] values = {"v1", "v2", "v3", "v4"};
        final ActionListener l = new ActionListener() {
            int count = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                if (count > values.length) return;
                if (count == values.length) {
                    customTreeTableModel.removeNodeFromParent(deletedNode);
                    count++;
                } else {
                    // one in each run
                  removeChild(customTreeTableModel, deletedNode, children, values);
                  count++;
                  // all in one
//                    for (int i = 0; i < values.length; i++) {
//                        removeChild(customTreeTableModel, deletedNode, children, values);
//                        count++;
//                    }     
                }
            }
            /**
             * @param customTreeTableModel
             * @param deletedNode
             * @param children
             * @param values
             */
            private void removeChild(final DefaultTreeTableModel customTreeTableModel, final MutableTreeTableNode deletedNode, final MutableTreeTableNode[] children, final String[] values) {
                customTreeTableModel.removeNodeFromParent(children[count]);
                customTreeTableModel.setValueAt(values[count], deletedNode, 0);
            }
            
        };
        Action changeValue = new AbstractAction("delete node sports recursively") {
            Timer timer;
            @Override
            public void actionPerformed(ActionEvent e) {
                if (timer == null) {
                    timer = new Timer(10, l);
                    timer.start();
                } else {
                    timer.stop();
                    setEnabled(false);
                }
                
            }
        };
        addAction(frame, changeValue);
        frame.setVisible(true);
    }


    
    /**
	 * Issue #493-swingx: JXTreeTable.TreeTableModelAdapter: Inconsistency
	 * firing update. Use the second child of root - first is accidentally okay.
	 * 
	 * from tiberiu@dev.java.net
	 * 
	 * TODO DefaultMutableTreeTableNodes do not allow value changes, so this
	 * test will never work
	 */
    public void interactiveTreeTableModelAdapterUpdate() {
        TreeTableModel customTreeTableModel = createCustomTreeTableModelFromDefault();

        final JXTreeTable table = new JXTreeTable(customTreeTableModel);
        table.setRootVisible(true);
        table.expandAll();
        table.setLargeModel(true);
        JXTree xtree = new JXTree(customTreeTableModel);
        xtree.setRootVisible(true);
        xtree.expandAll();
        final JXFrame frame = wrapWithScrollingInFrame(table, xtree,
                "JXTreeTable.TreeTableModelAdapter: Inconsistency firing update");
        Action changeValue = new AbstractAction("change sports to games") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newValue = "games";
                table.getTreeTableModel().setValueAt(newValue,
                        table.getPathForRow(6).getLastPathComponent(), 0);
            }
        };
        addAction(frame, changeValue);
        Action changeRoot = new AbstractAction("change root") {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeTableNode newRoot = new DefaultMutableTreeTableNode("new Root");
                ((DefaultTreeTableModel) table.getTreeTableModel()).setRoot(newRoot);
            }
        };
        addAction(frame, changeRoot);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Issue #493-swingx: JXTreeTable.TreeTableModelAdapter: Inconsistency
     * firing delete.
     * 
     * from tiberiu@dev.java.net
     */
    public void interactiveTreeTableModelAdapterDelete() {
        final TreeTableModel customTreeTableModel = createCustomTreeTableModelFromDefault();
        final JXTreeTable table = new JXTreeTable(customTreeTableModel);
        table.setRootVisible(true);
        table.expandAll();
        JXTree xtree = new JXTree(customTreeTableModel);
        xtree.setRootVisible(true);
        xtree.expandAll();
        final JXFrame frame = wrapWithScrollingInFrame(table, xtree,
                "JXTreeTable.TreeTableModelAdapter: Inconsistency firing update");
        Action changeValue = new AbstractAction("delete first child of sports") {
            @Override
            public void actionPerformed(ActionEvent e) {
                MutableTreeTableNode firstChild = (MutableTreeTableNode) table.getPathForRow(6 +1).getLastPathComponent();
                ((DefaultTreeTableModel) customTreeTableModel).removeNodeFromParent(firstChild);
            }
        };
        addAction(frame, changeValue);
        frame.setVisible(true);
    }

    /**
     * Issue #493-swingx: JXTreeTable.TreeTableModelAdapter: Inconsistency
     * firing delete.
     * 
     * from tiberiu@dev.java.net
     */
    public void interactiveTreeTableModelAdapterMutateSelected() {
        final TreeTableModel customTreeTableModel = createCustomTreeTableModelFromDefault();
        final JXTreeTable table = new JXTreeTable(customTreeTableModel);
        table.setRootVisible(true);
        table.expandAll();
        JXTree xtree = new JXTree(customTreeTableModel);
        xtree.setRootVisible(true);
        xtree.expandAll();
        final JXFrame frame = wrapWithScrollingInFrame(table, xtree,
                "JXTreeTable.TreeTableModelAdapter: Inconsistency firing delete expanded folder");
        Action changeValue = new AbstractAction("delete selected node") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();
                if (row < 0) return;
                MutableTreeTableNode firstChild = (MutableTreeTableNode) table.getPathForRow(row).getLastPathComponent();
                ((DefaultTreeTableModel) customTreeTableModel).removeNodeFromParent(firstChild);
            }
        };
        addAction(frame, changeValue);
        Action changeValue1 = new AbstractAction("insert as first child of selected node") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();
                if (row < 0) return;
                
                MutableTreeTableNode firstChild = (MutableTreeTableNode) table.getPathForRow(row).getLastPathComponent();
                MutableTreeTableNode newChild = new DefaultMutableTreeTableNode("inserted");
                ((DefaultTreeTableModel) customTreeTableModel)
                  .insertNodeInto(newChild, firstChild, 0);
            }
        };
        addAction(frame, changeValue1);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Issue #493-swingx: JXTreeTable.TreeTableModelAdapter: Inconsistency
     * firing delete.
     * 
     * from tiberiu@dev.java.net
     */
    public void interactiveTreeTableModelAdapterMutateSelectedDiscontinous() {
        final TreeTableModel customTreeTableModel = createCustomTreeTableModelFromDefault();
        final JXTreeTable table = new JXTreeTable(customTreeTableModel);
        table.setRootVisible(true);
        table.expandAll();
        JXTree xtree = new JXTree(customTreeTableModel);
        xtree.setRootVisible(true);
        xtree.expandAll();
        final JXFrame frame = wrapWithScrollingInFrame(table, xtree,
                "JXTreeTable.TreeTableModelAdapter: Inconsistency firing delete expanded folder");
        Action changeValue = new AbstractAction("delete selected node + sibling") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();
                if (row < 0) return;
                MutableTreeTableNode firstChild = (MutableTreeTableNode) table.getPathForRow(row).getLastPathComponent();
                MutableTreeTableNode parent = (MutableTreeTableNode) firstChild.getParent();
                MutableTreeTableNode secondNextSibling = null;
                int firstIndex = parent.getIndex(firstChild);
                if (firstIndex + 2 < parent.getChildCount()) {
                    secondNextSibling = (MutableTreeTableNode) parent.getChildAt(firstIndex + 2);
                }
                if (secondNextSibling != null) {
                	((DefaultTreeTableModel) customTreeTableModel).removeNodeFromParent(secondNextSibling);
                }
                
                ((DefaultTreeTableModel) customTreeTableModel).removeNodeFromParent(firstChild);
            }
        };
        addAction(frame, changeValue);
        Action changeValue1 = new AbstractAction("insert as first child of selected node") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();
                if (row < 0) return;
                
                MutableTreeTableNode firstChild = (MutableTreeTableNode) table.getPathForRow(row).getLastPathComponent();
                MutableTreeTableNode newChild = new DefaultMutableTreeTableNode("inserted");
                ((DefaultTreeTableModel) customTreeTableModel)
                  .insertNodeInto(newChild, firstChild, 0);
            }
        };
        addAction(frame, changeValue1);
        frame.pack();
        frame.setVisible(true);
    }
    /**
     * Creates and returns a custom model from JXTree default model. The model
     * is of type DefaultTreeModel, allowing for easy insert/remove.
     * 
     * @return
     */
    private TreeTableModel createCustomTreeTableModelFromDefault() {
        JXTree tree = new JXTree();
        DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
        TreeTableModel customTreeTableModel = TreeTableUtils
                .convertDefaultTreeModel(treeModel);

        return customTreeTableModel;
    }

    /**
     * A TreeTableModel inheriting from DefaultTreeModel (to ease
     * insert/delete).
     */
    public static class CustomTreeTableModel extends DefaultTreeTableModel {

        /**
         * @param root
         */
        public CustomTreeTableModel(TreeTableNode root) {
            super(root);
        }

        @Override
        public int getColumnCount() {
            return 1;
        }

        @Override
        public String getColumnName(int column) {
            return "User Object";
        }

        @Override
        public Object getValueAt(Object node, int column) {
            return ((DefaultMutableTreeNode) node).getUserObject();
        }

        @Override
        public boolean isCellEditable(Object node, int column) {
            return true;
        }

        @Override
        public void setValueAt(Object value, Object node, int column) {
            ((MutableTreeTableNode) node).setUserObject(value);
            modelSupport.firePathChanged(new TreePath(getPathToRoot((TreeTableNode) node)));
        }

    }

    /**
     * Issue #??-swingx: hyperlink in JXTreeTable hierarchical column not
     * active.
     * 
     */
    public void interactiveTreeTableLinkRendererSimpleText() {
        AbstractHyperlinkAction<Object> simpleAction = new AbstractHyperlinkAction<Object>(null) {

            @Override
            public void actionPerformed(ActionEvent e) {
                LOG.info("hit: " + getTarget());
                
            }
            
        };
        JXTreeTable tree = new JXTreeTable(new FileSystemModel());
        HyperlinkProvider provider =  new HyperlinkProvider(simpleAction);
        tree.getColumn(2).setCellRenderer(new DefaultTableRenderer(provider));
        tree.setTreeCellRenderer(new DefaultTreeRenderer( //provider));
                new WrappingProvider(provider)));
//        tree.setCellRenderer(new LinkRenderer(simpleAction));
        tree.setHighlighters(HighlighterFactory.createSimpleStriping());
        JFrame frame = wrapWithScrollingInFrame(tree, "table and simple links");
        frame.setVisible(true);
    }

    /**
     * Issue ??-swingx: hyperlink/rollover in hierarchical column.
     *
     */
    public void testTreeRendererInitialRollover() {
        JXTreeTable tree = new JXTreeTable(new FileSystemModel());
        assertEquals(tree.isRolloverEnabled(), ((JXTree) tree.getCellRenderer(0, 0)).isRolloverEnabled());
    }

    /**
     * Issue ??-swingx: hyperlink/rollover in hierarchical column.
     *
     */
    public void testTreeRendererModifiedRollover() {
        JXTreeTable tree = new JXTreeTable(new FileSystemModel());
        tree.setRolloverEnabled(!tree.isRolloverEnabled());
        assertEquals(tree.isRolloverEnabled(), ((JXTree) tree.getCellRenderer(0, 0)).isRolloverEnabled());
    }

    /**
     * example how to use a custom component as
     * renderer in tree column of TreeTable.
     *
     */
    public void interactiveTreeTableCustomRenderer() {
        JXTreeTable tree = new JXTreeTable(new FileSystemModel());
        
        StringValue sv = new StringValue( ){

            @Override
            public String getString(Object value) {
                return "..." + StringValues.TO_STRING.getString(value);
            }
            
        };
        ComponentProvider<?> provider = new CheckBoxProvider(sv);
//            /**
//             * custom tooltip: show row. Note: the context is that 
//             * of the rendering tree. No way to get at table state?
//             */
//            @Override
//            protected void configureState(CellContext context) {
//                super.configureState(context);
//                rendererComponent.setToolTipText("Row: " + context.getRow());
//            }
//            
//        };
        tree.setTreeCellRenderer(new DefaultTreeRenderer(provider));
        tree.setHighlighters(HighlighterFactory.createSimpleStriping());
        JFrame frame = wrapWithScrollingInFrame(tree, "treetable and custom renderer");
        frame.setVisible(true);
    }

    /**
     * Quick example to use a TextArea in the hierarchical column
     * of a treeTable. Not really working .. the wrap is not reliable?.
     *
     */
    public void interactiveTextAreaTreeTable() {
        TreeTableModel model = createTreeTableModelWithLongNode();
        JXTreeTable treeTable = new JXTreeTable(model);
        treeTable.setVisibleRowCount(5);
        treeTable.setRowHeight(50);
        treeTable.getColumnExt(0).setPreferredWidth(200);
        TreeCellRenderer renderer = new DefaultTreeRenderer(
                new WrappingProvider(new TextAreaProvider()));
        treeTable.setTreeCellRenderer(renderer);
        showWithScrollingInFrame(treeTable, "TreeTable with text wrapping");
    }
    
    /**
     * @return
     */
    private TreeTableModel createTreeTableModelWithLongNode() {
        MutableTreeTableNode root = createLongNode("some really, maybe really really long text -  "
                + "wrappit .... where needed ");
        root.insert(createLongNode("another really, maybe really really long text -  "
                + "with nothing but junk. wrappit .... where needed"), 0);
        root.insert(createLongNode("another really, maybe really really long text -  "
                + "with nothing but junk. wrappit .... where needed"), 0);
        MutableTreeTableNode node = createLongNode("some really, maybe really really long text -  "
                + "wrappit .... where needed ");
        node.insert(createLongNode("another really, maybe really really long text -  "
                + "with nothing but junk. wrappit .... where needed"), 0);
        root.insert(node, 0);
        root.insert(createLongNode("another really, maybe really really long text -  "
                + "with nothing but junk. wrappit .... where needed"), 0);
        Vector<String> ids = new Vector<String>();
        ids.add("long text");
        ids.add("dummy");
        return new DefaultTreeTableModel(root, ids);
    }

    /**
     * @param string
     * @return
     */
    private MutableTreeTableNode createLongNode(final String string) {
        AbstractMutableTreeTableNode node = new AbstractMutableTreeTableNode() {
            Object rnd = Math.random();
            @Override
            public int getColumnCount() {
                return 2;
            }

            @Override
            public Object getValueAt(int column) {
                if (column == 0) {
                    return string;
                }
                return rnd;
            }
            
        };
        node.setUserObject(string);
        return node;
    }


    /**
     * Experiments to try and understand clipping issues: occasionally, the text
     * in the tree column is clipped even if there is enough space available.
     * 
     * To visualize the rendering component's size we use a WrappingIconProvider
     * which sets a red border.
     * 
     * Calling packxx might pose a problem: for non-large models the node size
     * is cached. In this case, packing before replacing the renderer will lead
     * to incorrect sizes which are hard to invalidate (no way except faking a
     * structural tree event? temporaryly set large model and back, plus repaint
     * works). <p>
     * 
     * both pack-moments seem okay (without ellipses, always filling the box),
     * probably due to fixing swingx-1232 (by forcing the xtree to fire a 
     * change notification on setRenderer)
     */
    public void interactiveTreeTableClipIssueWrappingProvider() {
        final JXTreeTable treeTable = new JXTreeTable(createActionTreeModel());
        treeTable.setHorizontalScrollEnabled(true);
        treeTable.setColumnControlVisible(true);
        // BEWARE: do not pack before setting the renderer
        treeTable.packColumn(0, -1);

        StringValue format = new StringValue() {

            @Override
            public String getString(Object value) {
                if (value instanceof Action) {
                    return ((Action) value).getValue(Action.NAME) + "xx";
                }
                return StringValues.TO_STRING.getString(value);
            }

        };
        ComponentProvider<?> tableProvider = new LabelProvider(format);
        WrappingProvider wrappingProvider = new WrappingProvider(tableProvider) {
            Border redBorder = BorderFactory.createLineBorder(Color.RED);

            @Override
            public WrappingIconPanel getRendererComponent(CellContext context) {
                super.getRendererComponent(context);
                rendererComponent.setBorder(redBorder);
                return rendererComponent;
            }

        };
        DefaultTreeRenderer treeCellRenderer = new DefaultTreeRenderer(
                wrappingProvider);
        treeTable.setTreeCellRenderer(treeCellRenderer);
        treeTable.setHighlighters(HighlighterFactory.createSimpleStriping());
        // at this point a pack is okay, caching will get the correct values
        // treeTable.packColumn(0, -1);

        final JXTree tree = new JXTree(treeTable.getTreeTableModel());
        tree.setCellRenderer(treeCellRenderer);
        tree.setScrollsOnExpand(false);
        JXFrame frame = wrapWithScrollingInFrame(treeTable, tree,
                "treetable and tree with wrapping provider");
        // revalidate doesn't help
        Action revalidate = new AbstractActionExt("revalidate") {

            @Override
            public void actionPerformed(ActionEvent e) {
                treeTable.revalidate();
                tree.revalidate();
                treeTable.repaint();
            }
        };

        // hack around incorrect cached node sizes
        Action large = new AbstractActionExt("large-circle") {

            @Override
            public void actionPerformed(ActionEvent e) {
                treeTable.setLargeModel(true);
                treeTable.setLargeModel(false);
                treeTable.repaint();
            }
        };
        addAction(frame, revalidate);
        addAction(frame, large);
        show(frame);
    }

    /**
     * Experiments to try and understand clipping issues: occasionally, the text
     * in the tree column is clipped even if there is enough space available.
     * 
     * Here we don't change any renderers.
     */
    public void interactiveTreeTableClipIssueDefaultRenderer() {
        final JXTreeTable treeTable = new JXTreeTable(createActionTreeModel());
        treeTable.setHorizontalScrollEnabled(true);
        treeTable.setRootVisible(true);
        treeTable.collapseAll();
        treeTable.packColumn(0, -1);
        
        final JTree tree = new JTree(treeTable.getTreeTableModel());
        tree.collapseRow(0);
        JXFrame frame = wrapWithScrollingInFrame(treeTable, 
                tree, "JXTreeTable vs. core JTree: default renderer");
        Action revalidate = new AbstractActionExt("revalidate") {

            @Override
            public void actionPerformed(ActionEvent e) {
                treeTable.revalidate();
                tree.revalidate();
                
            } };
        addAction(frame, revalidate);
        frame.setVisible(true);
    }

    /**
     * Experiments to try and understand clipping issues: occasionally, the text
     * in the tree column is clipped even if there is enough space available.
     * Here we set a custom (unchanged default) treeCellRenderer which 
     * removes ellipses altogether.
     * 
     */
    public void interactiveTreeTableClipIssueCustomDefaultRenderer() {
        TreeCellRenderer renderer = new DefaultTreeCellRenderer() {

            @Override
            public Component getTreeCellRendererComponent(JTree tree,
                    Object value, boolean sel, boolean expanded, boolean leaf,
                    int row, boolean hasFocus) {
                return super.getTreeCellRendererComponent(tree, value, sel,
                        expanded, leaf, row, hasFocus);
            }

        };
        final JXTreeTable treeTable = new JXTreeTable(createActionTreeModel());
        treeTable.setTreeCellRenderer(renderer);
        treeTable.setHorizontalScrollEnabled(true);
        treeTable.setRootVisible(true);
        treeTable.collapseAll();
        treeTable.packColumn(0, -1);

        final JTree tree = new JTree(treeTable.getTreeTableModel());
        tree.setCellRenderer(renderer);
        tree.collapseRow(0);
        JXFrame frame = wrapWithScrollingInFrame(treeTable, tree,
                "JXTreeTable vs. JTree: custom default renderer without clip");
        Action revalidate = new AbstractActionExt("revalidate") {

            @Override
            public void actionPerformed(ActionEvent e) {
                treeTable.revalidate();
                tree.revalidate();

            }
        };
        addAction(frame, revalidate);
        frame.setVisible(true);
    }

    /**
     * Dirty example how to configure a custom renderer to use
     * treeTableModel.getValueAt(...) for showing.
     * 
     */
    public void interactiveTreeTableGetValueRenderer() {
        JXTreeTable tree = new JXTreeTable(new ComponentTreeTableModel(new JXFrame()));
        ComponentProvider<?> provider = new CheckBoxProvider(StringValues.TO_STRING) {
            
            @Override
            protected String getValueAsString(CellContext context) {
                // this is dirty because the design idea was to keep the renderer 
                // unaware of the context type
                TreeTableModel model = (TreeTableModel) ((JXTree) context.getComponent()).getModel();
                // beware: currently works only if the node is not a DefaultMutableTreeNode
                // otherwise the WrappingProvider tries to be smart and replaces the node
                // by the userObject before passing on to the wrappee! 
                Object nodeValue = model.getValueAt(context.getValue(), 0);
                return formatter.getString(nodeValue);            
              }

          
        };
        tree.setTreeCellRenderer(new DefaultTreeRenderer(provider));
        tree.expandAll();
        tree.setHighlighters(HighlighterFactory.createSimpleStriping());
        JFrame frame = wrapWithScrollingInFrame(tree, "treeTable and getValueAt renderer");
        frame.setVisible(true);
    }


//------------- unit tests    
    /**
     * Issue #399-swingx: editing terminated by selecting editing row.
     *
     */
    public void testSelectionKeepsEditingWithExpandsTrue() {
        JXTreeTable treeTable = new JXTreeTable(new FileSystemModel()) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
            
        };
        // sanity: default value of expandsSelectedPath
        assertTrue(treeTable.getExpandsSelectedPaths());
        boolean canEdit = treeTable.editCellAt(1, 2);
        // sanity: editing started
        assertTrue(canEdit);
        // sanity: nothing selected
        assertTrue(treeTable.getSelectionModel().isSelectionEmpty());
        int editingRow = treeTable.getEditingRow();
        treeTable.setRowSelectionInterval(editingRow, editingRow);
        assertEquals("after selection treeTable editing state must be unchanged", canEdit, treeTable.isEditing());
    }
    
    /**
     * Issue #212-jdnc: reuse editor, install only once.
     * 
     */
    public void testReuseEditor() {
        //TODO rework this test, since we no longer use TreeTableModel.class
//        JXTreeTable treeTable = new JXTreeTable(treeTableModel);
//        CellEditor editor = treeTable.getDefaultEditor(TreeTableModel.class);
//        assertTrue(editor instanceof TreeTableCellEditor);
//        treeTable.setTreeTableModel(simpleTreeTableModel);
//        assertSame("hierarchical editor must be unchanged", editor, 
//                treeTable.getDefaultEditor(TreeTableModel.class));
        fail("#212-jdnc - must be revisited after treeTableModel overhaul");
    }

    /**
     * sanity: toggling select/unselect via mouse the lead is
     * always painted, doing unselect via model (clear/remove path) 
     * seems to clear the lead?
     *
     */
    public void testBasicTreeLeadSelection() {
        JXTree tree = new JXTree();
        TreePath path = tree.getPathForRow(0);
        tree.setSelectionPath(path);
        assertEquals(0, tree.getSelectionModel().getLeadSelectionRow());
        assertEquals(path, tree.getLeadSelectionPath());
        tree.removeSelectionPath(path);
        assertNotNull(tree.getLeadSelectionPath());
        assertEquals(0, tree.getSelectionModel().getLeadSelectionRow());
    }

    
    /**
     * Issue #341-swingx: missing synch of lead.  
     * test lead after setting selection via table.
     *
     *  PENDING: this passes locally, fails on server
     */
    public void testLeadSelectionFromTable() {
        JXTreeTable treeTable = prepareTreeTable(false);
        assertEquals(-1, treeTable.getSelectionModel().getLeadSelectionIndex());
        assertEquals(-1, treeTable.getTreeSelectionModel().getLeadSelectionRow());
        treeTable.setRowSelectionInterval(0, 0);
        assertEquals(treeTable.getSelectionModel().getLeadSelectionIndex(), 
                treeTable.getTreeSelectionModel().getLeadSelectionRow());
        fail("lead selection synch passes locally, fails on server");
    }
    
    /**
     * Issue #341-swingx: missing synch of lead.  
     * test lead after setting selection via treeSelection.
     *  PENDING: this passes locally, fails on server
     *
     */
    public void testLeadSelectionFromTree() {
        JXTreeTable treeTable = prepareTreeTable(false);
        assertEquals(-1, treeTable.getSelectionModel().getLeadSelectionIndex());
        assertEquals(-1, treeTable.getTreeSelectionModel().getLeadSelectionRow());
        treeTable.getTreeSelectionModel().setSelectionPath(treeTable.getPathForRow(0));
        assertEquals(treeTable.getSelectionModel().getLeadSelectionIndex(), 
                treeTable.getTreeSelectionModel().getLeadSelectionRow());
        assertEquals(0, treeTable.getTreeSelectionModel().getLeadSelectionRow());
        fail("lead selection synch passes locally, fails on server");
    }


    /**
     * Issue #341-swingx: missing synch of lead.  
     * test lead after remove selection via tree.
     *
     */
    public void testLeadAfterRemoveSelectionFromTree() {
        JXTreeTable treeTable = prepareTreeTable(true);
        treeTable.getTreeSelectionModel().removeSelectionPath(
                treeTable.getTreeSelectionModel().getLeadSelectionPath());
        assertEquals(treeTable.getSelectionModel().getLeadSelectionIndex(), 
                treeTable.getTreeSelectionModel().getLeadSelectionRow());
        
    }
    
    /**
     * Issue #341-swingx: missing synch of lead.  
     * test lead after clear selection via table.
     *
     */
    public void testLeadAfterClearSelectionFromTable() {
        JXTreeTable treeTable = prepareTreeTable(true);
        treeTable.clearSelection();
        assertEquals(treeTable.getSelectionModel().getLeadSelectionIndex(), 
                treeTable.getTreeSelectionModel().getLeadSelectionRow());
        
    }

    /**
     * Issue #341-swingx: missing synch of lead.  
     * test lead after clear selection via table.
     *
     */
    public void testLeadAfterClearSelectionFromTree() {
        JXTreeTable treeTable = prepareTreeTable(true);
        treeTable.getTreeSelectionModel().clearSelection();
        assertEquals(treeTable.getSelectionModel().getLeadSelectionIndex(), 
                treeTable.getTreeSelectionModel().getLeadSelectionRow());
        
    }

    /**
     * creates and configures a treetable for usage in selection tests.
     * 
     * @param selectFirstRow boolean to indicate if the first row should
     *   be selected.
     * @return
     */
    protected JXTreeTable prepareTreeTable(boolean selectFirstRow) {
        JXTreeTable treeTable = new JXTreeTable(new ComponentTreeTableModel(new JXFrame()));
        treeTable.setRootVisible(true);
        // sanity: assert that we have at least two rows to change selection
        assertTrue(treeTable.getRowCount() > 1);
        if (selectFirstRow) {
            treeTable.setRowSelectionInterval(0, 0);
        }
        return treeTable;
    }


    public void testDummy() {
        
    }

    /**
     * @return
     */
    private TreeTableModel createActionTreeModel() {
        JXTable table = new JXTable(10, 10);
        table.setHorizontalScrollEnabled(true);
        return new ActionMapTreeTableModel(table);
    }

}
