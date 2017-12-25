/*
 * $Id: JXTreeVisualCheck.java 4301 2013-07-04 12:36:17Z kleopatra $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.CompoundHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.decorator.PatternPredicate;
import org.jdesktop.swingx.decorator.SearchPredicate;
import org.jdesktop.swingx.decorator.HighlightPredicate.DepthHighlightPredicate;
import org.jdesktop.swingx.renderer.DefaultListRenderer;
import org.jdesktop.swingx.renderer.DefaultTreeRenderer;
import org.jdesktop.swingx.renderer.IconValues;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.renderer.StringValues;
import org.jdesktop.swingx.renderer.WrappingProvider;
import org.jdesktop.swingx.renderer.HighlighterClientVisualCheck.FontHighlighter;
import org.jdesktop.swingx.test.ActionMapTreeTableModel;
import org.jdesktop.swingx.treetable.FileSystemModel;
import org.jdesktop.swingx.treetable.TreeTableNode;
import org.jdesktop.test.AncientSwingTeam;

public class JXTreeVisualCheck extends JXTreeUnitTest {
    @SuppressWarnings("all")
    private static final Logger LOG = Logger.getLogger(JXTreeVisualCheck.class
            .getName());

    public static void main(String[] args) {
//      setSystemLF(true);
      JXTreeVisualCheck test = new JXTreeVisualCheck();
      setLAF("Wind");
      try {
//          test.runInteractiveTests();
//          test.runInteractiveTests("interactive.*Renderer.*");
//          test.runInteractiveTests("interactive.*RToL.*");
//          test.runInteractiveTests("interactive.*Revalidate.*");
//          test.runInteractiveTests("interactiveRootExpansionTest");
//        test.runInteractiveTests("interactive.*UpdateUI.*");
//          test.runInteractiveTests("interactive.*CellHeight.*");
//        test.runInteractiveTests("interactive.*RendererSize.*");
//          test.runInteractive("NextMatch");
        test.runInteractive("PopupTrigger");
      } catch (Exception e) {
          System.err.println("exception when executing interactive tests:");
          e.printStackTrace();
      }
  }

    
    /**
     * Issue #1563-swingx: find cell that was clicked for componentPopup
     * 
     * Example of how to use:
     * - in actionPerformed
     * - in popupMenuWillBecomeVisible
     */
    public void interactivePopupTriggerLocation() {
        JXTree table = new JXTree();
        table.expandAll();
        JPopupMenu popup = new JPopupMenu();
        Action action = new AbstractAction("cell found in actionPerformed") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                JXTree table = SwingXUtilities.getAncestor(JXTree.class, (Component) e.getSource());
                Point trigger = table.getPopupTriggerLocation();
                Point cell = null;
                if (trigger != null) {
                    int row = table.getRowForLocation(trigger.x, trigger.y);
                    table.setSelectionRow(row);
                    cell = new Point(0, row);
                } else {
                    table.clearSelection();
                }
                LOG.info("popupTrigger/cell " + trigger + "/" + cell);
            }
        };
        popup.add(action);
        
        final Action onShowing = new AbstractAction("dynamic: ") {

            @Override
            public void actionPerformed(ActionEvent e) {
                LOG.info("" + getValue(NAME));
            }
            
        };
        popup.add(onShowing);
        
        PopupMenuListener l = new PopupMenuListener() {
            
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                // doesn't work: popup itself cannot be used as
                // starting component, bug?
//                JXTree tree = SwingXUtilities.getAncestor(JXTree.class, 
//                        (Component) e.getSource());
                JXTree tree = (JXTree) ((JPopupMenu) e.getSource()).getInvoker();
                Point trigger = tree.getPopupTriggerLocation();
                Point cell = null;
                if (trigger != null) {
                    int row = tree.getRowForLocation(trigger.x, trigger.y);
                    tree.setSelectionRow(row);
                    tree.setLeadSelectionPath(tree.getPathForRow(row));
                    cell = new Point(0, row);
                }
                onShowing.putValue(Action.NAME, "popupTrigger/cell " + trigger + "/" + cell);
            }
            
            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }
            
            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        };
        popup.addPopupMenuListener(l);
        table.setComponentPopupMenu(popup);
        showWithScrollingInFrame(table, "PopupTriggerLocation");
    }
    

    /**
     * Issue 1483-swingx: getNextMatch must respect StringValue
     */
    public void interactiveNextMatch() {
        JXTree tree = new JXTree(new FileSystemModel(new File(".")));
        tree.setCellRenderer(new DefaultTreeRenderer(IconValues.FILE_ICON, StringValues.FILE_NAME));
        showWithScrollingInFrame(tree, "nextMatch?");
    }
    

    public void interactiveDynamicCellHeightTree() {
      final JXTree tree = new  JXTree(AncientSwingTeam.createNamedColorTreeModel());
      
      TreeSelectionListener l = new TreeSelectionListener() {
          
          @Override
          public void valueChanged(TreeSelectionEvent e) {
              LOG.info("height " + tree.getRowHeight());
//              tree.setRowHeight(-1);
              SwingUtilities.invokeLater(new Runnable() {
                  public void run() {
                      tree.invalidateCellSizeCache();
                  }
              });
              
          }
          
      };
      tree.addTreeSelectionListener(l);
      tree.setCellRenderer(new DefaultTreeRenderer());
//      tree.setLargeModel(true);
      tree.setRowHeight(0);
      HighlightPredicate selected = new HighlightPredicate() {
          
          @Override
          public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
              return adapter.isSelected();
          }
      };
      Highlighter hl = new FontHighlighter(selected, tree.getFont().deriveFont(50f));
      tree.addHighlighter(hl);
      showWithScrollingInFrame(tree, "big font on focus");
  }


    /**
     * Issue #1231-swingx: tree cell renderer size problems.
     * 
     * Cache not invalidated on setCellRenderer due to not firing a 
     * propertyChange (it's wrapped). Problem or not is LAF dependent ;-)
     * The not-firing is clearly a bug.
     * 
     */
    public void interactiveRendererSize() {
        JXTree tree = new JXTree();
        tree.setCellRenderer(new CustomCellRenderer());
        tree.expandAll();
        showWithScrollingInFrame(tree, "sizing problems in renderer");
    }

    private static class CustomCellRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean selected, boolean expanded, boolean leaf, int row,
                boolean hasFocus) {
            return super.getTreeCellRendererComponent(tree, "XX " + value,
                    selected, expanded, leaf, row, hasFocus);
        }
    }



    /**
     * Issue #862-swingx: JXTree - add api for selection colors.
     */
    public void interactiveSelectionColors() {
        final JXTree tree = new JXTree();
        // use SwingX renderer which is aware of per-tree selection colors
        tree.setCellRenderer(new DefaultTreeRenderer());
        final Color uiBackground = tree.getSelectionBackground();
        final Color uiForeground = tree.getSelectionForeground();
        Action toggleSelectionColors = new AbstractAction("toggle selection colors") {
            
            public void actionPerformed(ActionEvent e) {
                if (tree.getSelectionBackground() == uiBackground) {
                    tree.setSelectionBackground(Color.BLUE);
                    tree.setSelectionForeground(Color.RED);
                } else {
                    tree.setSelectionBackground(uiBackground);
                    tree.setSelectionForeground(uiForeground);
                }
                
            }
            
        };
        JXFrame frame = wrapWithScrollingInFrame(tree, "selection color property in JXTree");
        addAction(frame, toggleSelectionColors);
        show(frame);
    }
    
    /**
     * Issue #862-swingx: JXTree - add api for selection colors.
     * Compare with JList repaint behaviour.
     */
    public void interactiveSelectionColorsList() {
        final JXList tree = new JXList(new Object[]{"one", "two", "three"});
        // use SwingX renderer which is aware of per-tree selection colors
        tree.setCellRenderer(new DefaultListRenderer());
        final Color uiBackground = tree.getSelectionBackground();
        final Color uiForeground = tree.getSelectionForeground();
        Action toggleSelectionColors = new AbstractAction("toggle selection colors") {
            
            public void actionPerformed(ActionEvent e) {
                if (tree.getSelectionBackground() == uiBackground) {
                    tree.setSelectionBackground(Color.BLUE);
                    tree.setSelectionForeground(Color.RED);
                } else {
                    tree.setSelectionBackground(uiBackground);
                    tree.setSelectionForeground(uiForeground);
                }
                
            }
            
        };
        JXFrame frame = wrapWithScrollingInFrame(tree, "selection color property - compare list repaint");
        addAction(frame, toggleSelectionColors);
        show(frame);
    }
    /**
     * Requirements
     * - no icons, use IconValue.NONE
     * - don't unwrap user object
     */
    public void interactiveNoIconsNoUnwrap() {
        TreeModel model = new ActionMapTreeTableModel(new JXTable());
        JXTree tree = new JXTree(model);
        StringValue sv = new StringValue() {

            public String getString(Object value) {
                if ((value instanceof TreeTableNode) 
                        && ((TreeTableNode) value).getColumnCount() > 0) {
                    value = ((TreeTableNode) value).getValueAt(0);
                }
                return StringValues.TO_STRING.getString(value);
            }
            
        };
        DefaultTreeRenderer renderer = new DefaultTreeRenderer(IconValues.NONE, sv);
        ((WrappingProvider) renderer.getComponentProvider()).setUnwrapUserObject(false);
        tree.setCellRenderer(renderer);
        JXFrame frame = wrapWithScrollingInFrame(tree, "WrappingProvider: no icons, no unwrapped userObject");
        frame.pack();
        frame.setSize(400, 200);
        frame.setVisible(true);
    }


    /**
     * 
     * Requirements:
     * - striping effect on leaf, extend to full width of tree
     * - striping relative to parent (not absolute position of row)
     * 
     * Trick (from forum): set the rendering component's pref width
     *   in the renderer.
     *   
     * Applied to SwingX: 
     * - use a highlighter for the pref width setting
     * - use a predicate to decide which striping to turn on
     *   
     * Problem: difficult to get rid off size cache of BasicTreeUI.
     * 
     * The sizing of the nodes is cached before the actual expansion.
     * That is the row index is invalid at the time of messaging the
     * renderer, so decoration which effects the size (like setting pref
     * f.i.) is ignored. Except for largeModel and fixed row height.
     * 
     * Note: as is, it cannot cope with RToL component orientation.
     */
    public void interactiveExpandToWidthHighlighter() {
        final JXTree tree = new JXTree();
        tree.setCellRenderer(new DefaultTreeRenderer());
        tree.expandRow(3);
        tree.setRowHeight(20);
        tree.setLargeModel(true);
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        int indent = ((BasicTreeUI) tree.getUI()).getLeftChildIndent()
           + ((BasicTreeUI) tree.getUI()).getRightChildIndent();
        int depthOffset = getDepthOffset(tree);
        HighlightPredicate evenChild = new HighlightPredicate() {

            public boolean isHighlighted(Component renderer,
                    ComponentAdapter adapter) {
                if (!(adapter.getComponent() instanceof JTree)) return false;
                TreePath path = ((JTree) adapter.getComponent()).getPathForRow(adapter.row);
                return path == null ? false : 
                    (adapter.row - ((JTree) adapter.getComponent()).getRowForPath(path.getParentPath())) % 2 == 0;
            }

        };
        HighlightPredicate oddChild = new HighlightPredicate() {

            public boolean isHighlighted(Component renderer,
                    ComponentAdapter adapter) {
                if (!(adapter.getComponent() instanceof JTree)) return false;
                TreePath path = ((JTree) adapter.getComponent()).getPathForRow(adapter.row);
                return path == null ? false : 
                    (adapter.row - ((JTree) adapter.getComponent()).getRowForPath(path.getParentPath())) % 2 == 1;
            }
            
        };
        final ExtendToWidthHighlighter extendToWidthHighlighter = new ExtendToWidthHighlighter(null, indent, depthOffset);
        tree.setHighlighters(
                new CompoundHighlighter(
                        HighlightPredicate.IS_LEAF,
                        extendToWidthHighlighter,
                        new ColorHighlighter(evenChild, HighlighterFactory.BEIGE, null),
                        new ColorHighlighter(oddChild, HighlighterFactory.LINE_PRINTER, null)),
                        new ColorHighlighter(HighlightPredicate.IS_FOLDER, null, Color.RED)
        );
        
        final JXFrame frame = wrapWithScrollingInFrame(tree, "tree-wide cell renderer");
        Action rootVisible = new AbstractActionExt("toggle root visible") {

            public void actionPerformed(ActionEvent e) {
                tree.setRootVisible(!tree.isRootVisible());
                extendToWidthHighlighter.setDepthOffset(getDepthOffset(tree));
            }
            
        };
        addAction(frame, rootVisible);
        Action handleVisible = new AbstractActionExt("toggle handles") {

            public void actionPerformed(ActionEvent e) {
                tree.setShowsRootHandles(!tree.getShowsRootHandles());
                extendToWidthHighlighter.setDepthOffset(getDepthOffset(tree));
            }
            
        };
        addAction(frame, handleVisible);
        addComponentOrientationToggle(frame);
        show(frame, 400, 400);
    }

    /**
     * C&p from BasicTreeUI: adjust the depth to root/handle visibility.
     * @param tree
     * @return
     */
    protected int getDepthOffset(JTree tree) {
        if(tree.isRootVisible()) {
            if(tree.getShowsRootHandles()) {
                return 1;
            } 
        } else if(!tree.getShowsRootHandles()) {
            return -1;
        } 
        return 0;
    }

    /**
     * Highlighter which sets the preferredWidth of the renderer relative to the 
     * target component's width. Very special requirement for tree rendering: 
     * extend the coloring all the way from the node to the boundary of the tree.
     * An alternative would be to us a renderer which is layout in such
     * a way so by default.
     */
    public static class ExtendToWidthHighlighter extends AbstractHighlighter {
        
        private int indent;
        private int depthOffset;
        public ExtendToWidthHighlighter(HighlightPredicate predicate, int indent, int depthOffset) {
            super(predicate);
            this.indent = indent;
            this.depthOffset = depthOffset;
        }
        
        public void setDepthOffset(int offset) {
            if (offset == this.depthOffset) return;
            this.depthOffset = offset;
            fireStateChanged();
        }
        
        @Override
        protected Component doHighlight(Component component,
                ComponentAdapter adapter) {
            Dimension dim = component.getPreferredSize();
            int width = adapter.getComponent().getWidth() 
                - (adapter.getDepth() + depthOffset) * indent ;
            dim.width = Math.max(dim.width, width);
            component.setPreferredSize(dim);
            return component;
        }
        
    }

    
    public void interactiveExpandWithHighlighters() {
        JXTree tree = new JXTree();
        Highlighter searchHighlighter = new ColorHighlighter(new SearchPredicate("\\Qe\\E"), null, 
                Color.RED);
        tree.addHighlighter(searchHighlighter);
        showWithScrollingInFrame(tree, "NPE on tree expand with highlighter");

    }
    
    /**
     * visually check if invokesStopCellEditing jumps in on focusLost.
     *
     */
    public void interactiveToggleEditProperties() {
        final JXTree table = new JXTree();
        table.setEditable(true);
        DefaultTreeCellEditor editor = new DefaultTreeCellEditor(null, null) {

            @Override
            public boolean stopCellEditing() {
                String value = String.valueOf(getCellEditorValue());
                if (value.startsWith("s")) {
                    return false;
                }
                return super.stopCellEditing();
            }
            
        };
        table.setCellEditor(editor);
        JXFrame frame = wrapWithScrollingInFrame(table, new JButton("something to focus"), 
                "JXTree: toggle invokesStopEditing ");
        Action toggleTerminate = new AbstractAction("toggleInvokesStop") {

            public void actionPerformed(ActionEvent e) {
                table.setInvokesStopCellEditing(!table.getInvokesStopCellEditing());
                
            }
            
        };
        addAction(frame, toggleTerminate);
        frame.setVisible(true);
        
    }

    /**
     * visualize editing of the hierarchical column, both
     * in a tree and a xTree switching CO.
     * using DefaultXTreeCellEditor.
     */
    public void interactiveXTreeEditingRToL() {
        JTree tree =  new JTree(); 
        tree.setEditable(true);
        JXTree xTree = new JXTree();
        xTree.setCellRenderer(new DefaultTreeRenderer());
        xTree.setEditable(true);
        final JXFrame frame = wrapWithScrollingInFrame(xTree, tree, "XEditing: compare JXTree vs. JTree");
        addComponentOrientationToggle(frame);
        addMessage(frame, "JXTree configured with SwingX renderer/treeEditor");
        show(frame);
        
    }



    /**
     * Issue ??: Background highlighters not working on JXTree.
     *
     */
    public void interactiveUnselectedFocusedBackground() {
        JXTree xtree = new JXTree(treeTableModel);
        xtree.setCellRenderer(new DefaultTreeRenderer());
        xtree.setBackground(new Color(0xF5, 0xFF, 0xF5));
        JTree tree = new JTree(treeTableModel);
        tree.setBackground(new Color(0xF5, 0xFF, 0xF5));
        JXFrame frame = wrapWithScrollingInFrame(xtree, tree, "Unselected focused background: JXTree/JTree" );
        addMessage(frame, "xtree uses swingx renderer");
    }

    /**
     * Issue #503-swingx: JXList rolloverEnabled disables custom cursor.
     * 
     * Sanity test for JXTree (looks okay).
     *
     */
    public void interactiveTestRolloverHighlightCustomCursor() {
        JXTree tree = new JXTree(treeTableModel);
        tree.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        tree.setRolloverEnabled(true);
        tree.setHighlighters(createRolloverHighlighter(true));
        showWithScrollingInFrame(tree, "foreground rollover, custom cursor " );
    }


    public void interactiveTestDepthHighlighter() {
        JXTree tree = new JXTree(treeTableModel);
        tree.setHighlighters(createDepthHighlighters());
        showWithScrollingInFrame(tree, "Depth highlighter" );
    }
    
    public void interactiveTestEditabilityHighlighter() {
        JXTree tree = new JXTree(treeTableModel);
        tree.setEditable(true);
        tree.setHighlighters(new ColorHighlighter(HighlightPredicate.EDITABLE, Color.WHITE, Color.RED));
        showWithScrollingInFrame(tree, "Editability highlighter" );
    }
    
    /**
     * Issue ??: Background highlighters not working on JXTree.
     *
     * Works with SwingX renderer
     */
    public void interactiveTestRolloverHighlightBackground() {
        JXTree tree = new JXTree(treeTableModel);
        tree.setRolloverEnabled(true);
        tree.setCellRenderer(new DefaultTreeRenderer());
        tree.setHighlighters(createRolloverHighlighter(false));
        JXFrame frame = wrapWithScrollingInFrame(tree, "Rollover - background " );
        addMessage(frame, "here we use a SwingX renderer - backgound okay");
        show(frame);
    }
    
    private Highlighter createRolloverHighlighter(boolean useForeground) {
        Color color = new Color(0xF0, 0xF0, 0xE0); //LegacyHighlighter.ledgerBackground.getBackground();
        Highlighter highlighter = new ColorHighlighter(
                HighlightPredicate.ROLLOVER_ROW, useForeground ? null : color, 
                        useForeground ? color.darker() : null);
        return highlighter;
    }
    
    private Highlighter[] createDepthHighlighters() {
        Highlighter[] highlighters = new Highlighter[2];
        
        highlighters[0] = new ColorHighlighter(new DepthHighlightPredicate(1), Color.WHITE, Color.RED);
        highlighters[1] = new ColorHighlighter(new DepthHighlightPredicate(2), Color.WHITE, Color.BLUE);
        
        return highlighters;
    }
    
    /**
     * Issue ??: Background highlighters not working on JXTree.
     * 
     * It's a problem of core DefaultTreeCellRenderer. SwingX renderers are okay.
     *
     */
    public void interactiveTestHighlighters() {
        JXTree tree = new JXTree(treeTableModel);
        String pattern = "o";
        tree.setHighlighters(new ColorHighlighter(new PatternPredicate(pattern, 0), 
                Color.YELLOW, 
                Color.RED),
                HighlighterFactory.createSimpleStriping(HighlighterFactory.LINE_PRINTER));
        JXFrame frame = wrapWithScrollingInFrame(tree, "Foreground/background Highlighter: " + pattern);
        addMessage(frame, "here we use a core default renderer - background highlighter not working!");
        show(frame);
    }
    
    
    
    public void interactiveTestToolTips() {
        JXTree tree = new JXTree(treeTableModel);
        // JW: don't use this idiom - Stackoverflow...
        // multiple delegation - need to solve or discourage
        tree.setCellRenderer(createRenderer());
        // JW: JTree does not automatically register itself
        // should JXTree? 
        ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
        toolTipManager.registerComponent(tree);
        showWithScrollingInFrame(tree, "tooltips");
    }
    
    
    private TreeCellRenderer createRenderer() {
        final TreeCellRenderer delegate = new DefaultTreeCellRenderer();
        TreeCellRenderer renderer = new TreeCellRenderer() {

            public Component getTreeCellRendererComponent(JTree tree, Object value, 
                    boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                Component result = delegate.getTreeCellRendererComponent(tree, value, 
                        selected, expanded, leaf, row, hasFocus);
                ((JComponent) result).setToolTipText(String.valueOf(tree.getPathForRow(row)));
                 return result;
            }
            
        };
        return renderer;
    }

    /**
     * test if lineStyle client property is respected by JXTree.
     * Note that some LFs don't respect anyway (WinLF f.i.)
     */
    public void interactiveTestLineStyle() {
        JXTree tree = new JXTree(treeTableModel);
        tree.setDragEnabled(true);
        tree.putClientProperty("JTree.lineStyle", "None");
        showWithScrollingInFrame(tree, "LineStyle Test - none");
    }

    /**    
     * setting tree properties: JXTree is updated properly.
     */    
    public void interactiveTestTreeProperties() {
        final JXTree treeTable = new JXTree(treeTableModel);
        Action toggleHandles = new AbstractAction("Toggle Handles") {

            public void actionPerformed(ActionEvent e) {
                treeTable.setShowsRootHandles(!treeTable.getShowsRootHandles());
                
            }
            
        };
        Action toggleRoot = new AbstractAction("Toggle Root") {

            public void actionPerformed(ActionEvent e) {
                treeTable.setRootVisible(!treeTable.isRootVisible());
                
            }
            
        };
        treeTable.setRowHeight(22);
        JXFrame frame = wrapWithScrollingInFrame(treeTable,
                "Toggle Tree properties ");
        addAction(frame, toggleRoot);
        addAction(frame, toggleHandles);
        show(frame);
    }
    

    
    /**
     * Ensure that the root node is expanded if invisible and child added.
     */
    public void interactiveRootExpansionTest() {
        final DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
        final DefaultTreeModel model = new DefaultTreeModel(root);
        final JXTree tree = new JXTree(model);
        tree.setRootVisible(false);
        assertFalse(tree.isExpanded(new TreePath(root)));
        
        Action addChild = new AbstractAction("Add Root Child") {
            private int counter = 0;
            
            public void actionPerformed(ActionEvent e) {
                root.add(new DefaultMutableTreeNode("Child " + (counter + 1)));
                model.nodesWereInserted(root, new int[]{counter});
                counter++;
                
                assertTrue(tree.isExpanded(new TreePath(root)));
            }
        };
 
        JXFrame frame = wrapWithScrollingInFrame(tree, "Root Node Expansion Test");
        addAction(frame, addChild);
        show(frame);
    }
}
