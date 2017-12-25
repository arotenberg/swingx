/*
 * $Id: RendererIssues.java 4283 2013-02-26 17:37:02Z kleopatra $
 *
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx.renderer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.border.DropShadowBorder;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.decorator.PainterHighlighter;
import org.jdesktop.swingx.decorator.PatternPredicate;
import org.jdesktop.swingx.painter.BusyPainter;
import org.jdesktop.swingx.painter.ImagePainter;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.rollover.RolloverProducer;
import org.jdesktop.swingx.rollover.RolloverRenderer;
import org.jdesktop.swingx.test.ComponentTreeTableModel;
import org.jdesktop.swingx.test.XTestUtils;
import org.jdesktop.swingx.treetable.FileSystemModel;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.jdesktop.swingx.util.PaintUtils;
import org.jdesktop.test.AncientSwingTeam;

import com.sun.java.swing.plaf.motif.MotifLookAndFeel;

/**
 * Test around known issues of SwingX renderers. <p>
 * 
 * Ideally, there would be at least one failing test method per open
 * Issue in the issue tracker. Plus additional failing test methods for
 * not fully specified or not yet decided upon features/behaviour.
 * 
 * @author Jeanette Winzenburg
 */
public class RendererIssues extends InteractiveTestCase {
    private static final Logger LOG = Logger.getLogger(RendererIssues.class
            .getName());

    public static void main(String[] args) {
        RendererIssues test = new RendererIssues();
        setLAF("Nimb");
        try {
//            test.runInteractiveTests();
          test.runInteractiveTests("interactive.*Alpha.*");
//          test.runInteractiveTests(".*XLabel.*");
//          test.runInteractiveTests(".*Color.*");
//          test.runInteractiveTests("interactive.*ColumnControl.*");
//          test.runInteractiveTests("interactive.*ToolTip.*");
//            test.runInteractiveTests("interactive.*TreeRenderer.*");
//            test.runInteractiveTests("interactive.*Opacity.*");
//          test.runInteractive("RendererCheckBox");
//          test.runInteractiveTests("interactive.*Hyperlink.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }
    
    
    
    /**
     * Issue ??-swingx: Boolean renderer background is slightly darker if 
     * background color is part-transparent.
     * 
     * Not only checkbox as renderer - there seem to be subtle differences 
     * when painting buttons, labels, ... plus it's laf dependent.
     * 
     */
    public void interactiveAlphaBackground() {
        Color color = PaintUtils.setAlpha(Color.ORANGE, 60);
        
        JCheckBox check = new JCheckBox("what's my color? - as is");
//        check.setOpaque(true);
//        check.setContentAreaFilled(true);
        check.setBackground(color);
        JLabel label = new JLabel("label: and mine? (forced to opaque)");
        label.setOpaque(true);
        label.setBackground(color );
        JButton button = new JButton("the new kid on the block - as-is");
        button.setBackground(color);
        
        JRadioButton radio = new JRadioButton("radio, raadio .. as-is");
        radio.setBackground(color);
        
        JTextField field = new JTextField(40);
        field.setBackground(color);
        JComponent box = Box.createVerticalBox();
        box.setOpaque(true);
        box.setBackground(Color.WHITE);
        box.add(check);
        box.add(radio);
        box.add(label);
        box.add(button);
        box.add(field);
        JXFrame frame = wrapInFrame(box, "alpha in plain ..", true);
        show(frame, 400, 200);
        
    }

    /**
     * Issue ??-swingx: Boolean renderer background is slightly darker if 
     * background color is part-transparent.
     * 
     */
    public void interactiveCheckBoxAlpha() {
        JXTable table = new JXTable(new org.jdesktop.test.AncientSwingTeam());
        table.setBackground(PaintUtils.setAlpha(Color.YELLOW, 100));
        table.setOpaque(false);
        table.addHighlighter(new RowHighlighter(new HighlightPredicate() {
            public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
                return ((Boolean) adapter.getValue(4)).booleanValue();
            }
        }));
        showWithScrollingInFrame(table, "boolean renderer and alpha background");
        
    }

    static class RowHighlighter extends ColorHighlighter {
        Font BOLD_FONT;

        RowHighlighter(HighlightPredicate predicate) {
            super(predicate, PaintUtils.setAlpha(Color.ORANGE, 60), Color.RED);
            setSelectedForeground(getForeground());
        }

        @Override
        protected Component doHighlight(Component renderer, ComponentAdapter adapter) {
            if (BOLD_FONT == null) {
                BOLD_FONT = renderer.getFont().deriveFont(Font.BOLD);
            }
            renderer.setFont(BOLD_FONT);
            return super.doHighlight(renderer, adapter);
        }
    }

    
    /**
     * example to configure treeTable hierarchical column with
     * custom icon and content mapping. The nodes are actually of type File.
     * 
     * Problem: 
     * painting on resizing tree column sluggish, especially if the PatternHighlighter
     * is on. Reason seems to be the FileSystemView - prepare time increases with
     * number of accesses.
     */
    public void interactiveTreeTableCustomIconsPerformance() {
        // modify the file model to return the file itself for the hierarchical column
        TreeTableModel model = new FileSystemModel() {
            
            @Override
            public Object getValueAt(Object node, int column) {
                if (column == 0) {
                    return node;
                }
                return super.getValueAt(node, column);
            }
            
        };
        final JXTreeTable table = new JXTreeTable(model);
        table.setAutoResizeMode(JXTable.AUTO_RESIZE_OFF);
        StringValue sv = new StringValue() {

            public String getString(Object value) {
                if (value instanceof File) {
                    return FileSystemView.getFileSystemView().getSystemDisplayName((File) value)
                       + " Type: " 
                       + FileSystemView.getFileSystemView().getSystemTypeDescription((File) value)
                    ; 
                } 
                return StringValues.TO_STRING.getString(value);
            }
            
        };
        IconValue iv = new IconValue() {

            public Icon getIcon(Object value) {
                if (value instanceof File) {
                    return  FileSystemView.getFileSystemView().getSystemIcon((File) value);
                } 
                return null;
            }};
        final DefaultTreeRenderer treeRenderer = new DefaultTreeRenderer(iv, sv);
        table.setTreeCellRenderer(treeRenderer);
        // string based. Note: this example is locale dependent
        String folderDescription = ".*ordner.*";
        PatternPredicate predicate = new PatternPredicate(folderDescription, 0, -1);
        final Highlighter hl = new ColorHighlighter(predicate, null, Color.RED);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);
        final Date lastYear = calendar.getTime();
        // install value based highlighter 
        HighlightPredicate valueBased = new HighlightPredicate() {

            public boolean isHighlighted(Component renderer,
                    ComponentAdapter adapter) {
                if (!(adapter.getValue() instanceof File)) return false;
                File file = (File) adapter.getValue();
                Date date = new Date(file.lastModified());
                return date.after(lastYear);
            }
            
        };
        final ColorHighlighter back =  new ColorHighlighter(valueBased, Color.YELLOW, null);
        JXFrame frame =showWithScrollingInFrame(table, "TreeTable: performance bottleneck is FileSystemView");
        Action toggleBack = new AbstractAction("toggleBackHighlighter") {
            boolean hasBack;
            public void actionPerformed(ActionEvent e) {
                if (hasBack) {
                    table.removeHighlighter(back);
                } else {
                    table.addHighlighter(back);
                }
                hasBack = !hasBack;
                
            }
            
        };
        addAction(frame, toggleBack);
        Action togglePattern = new AbstractAction("togglePatternHighlighter") {
            boolean hasBack;
            public void actionPerformed(ActionEvent e) {
                if (hasBack) {
                    table.removeHighlighter(hl);
                } else {
                    table.addHighlighter(hl);
                }
                hasBack = !hasBack;
                
            }
            
        };
        addAction(frame, togglePattern);
        // accessing the FileSystemView is slooooww
        // increasingly costly over time, shows particularly 
        // when the patternHighlighter is on
        // (probably because that increases the number 
        // of queries to the systemView
        final JLabel timeL = new JLabel("stop-watch");
        Action timer = new AbstractAction("start") {
            public void actionPerformed(ActionEvent e) {
                timeL.setText("started");
                long time = System.currentTimeMillis();
                for (int i = 0; i < 2000; i++) {
                   table.prepareRenderer(table.getCellRenderer(0, 0), 0, 0); 
                }
                timeL.setText("stopped: " + (System.currentTimeMillis() - time));
            }
        };
        addAction(frame, timer);
        
        addStatusComponent(frame, timeL);
        addStatusMessage(frame, "node is File - string/value based highlighters same");
    }


    /**
     * Playing with rollover for visual clue if cell editable.
     * The old problem: cursor shoudn't be altered by the rollover
     * controller but by the rollover renderer.
     */
    public void interactiveRolloverEffects() {
        JXTable table = new JXTable(new AncientSwingTeam());
        table.setDefaultRenderer(Boolean.class, new DefaultTableRenderer(new RolloverCheckBox()));
        showWithScrollingInFrame(table, "checkbox rollover effect");
    }
    
    public static class RolloverCheckBox extends CheckBoxProvider 
        implements RolloverRenderer {
        boolean wasEditable;

        
        @Override
        protected void configureState(CellContext context) {
            super.configureState(context);
            if (context.getComponent() !=  null) {
                Point p = (Point) context.getComponent()
                        .getClientProperty(RolloverProducer.ROLLOVER_KEY);
                if (/*hasFocus || */(p != null && (p.x >= 0) && 
                        (p.x == context.getColumn()) && (p.y == context.getRow()))) {
                     rendererComponent.getModel().setRollover(true);
                } else {
                    rendererComponent.getModel().setRollover(false);
                }
            }
        }

        @Override
        protected void format(CellContext context) {
            // TODO Auto-generated method stub
            super.format(context);
            wasEditable = context.isEditable();
        }

        public void doClick() {
        }

        public boolean isEnabled() {
            return wasEditable;
        }
        
    }
    
    public void interactiveToolTipList() {
        final JXTree table = new JXTree(new ComponentTreeTableModel(new JXFrame()));
        table.expandAll();
        // quick model for long values
        ListModel model = new AbstractListModel() {

            public Object getElementAt(int index) {
                return table.getPathForRow(index).getLastPathComponent();
            }

            public int getSize() {
                return table.getRowCount();
            }
            
        };
        JXList list = new JXList(model) {

            @Override
            public String getToolTipText(MouseEvent event) {
                int row = locationToIndex(event.getPoint());
                Rectangle r = getCellBounds(row, row);
                if (r == null) 
                    return super.getToolTipText(event);
                ListCellRenderer renderer = getCellRenderer();
                Component comp = renderer.getListCellRendererComponent(this, getElementAt(row), row, isSelectedIndex(row), false);
                if (comp.getPreferredSize().width <= getVisibleRect().width) return null;
                renderer = ((DelegatingRenderer) renderer).getDelegateRenderer();
                if (renderer instanceof StringValue) {
                    return ((StringValue) renderer).getString(getElementAt(row));
                }
                return null;
                
            }

            @Override
            public Point getToolTipLocation(MouseEvent event) {
                int row = locationToIndex(event.getPoint());
                Rectangle r = getCellBounds(row, row);
                if (r != null) {
                    if (!getComponentOrientation().isLeftToRight()) {
                        r.translate(r.width, 0);
                    }
                    return r.getLocation();
                }
                return super.getToolTipLocation(event);
            }
            
            
            
        };
        JXFrame frame = wrapWithScrollingInFrame(list, "list tooltip");
        addComponentOrientationToggle(frame);
        show(frame, 300, 300);
    }
    
    public void interactiveToolTipTable() {
        JXTreeTable treeTable = new JXTreeTable(new ComponentTreeTableModel(new JXFrame()));
        treeTable.expandAll();
        JXTable table = new JXTable(treeTable.getModel()) {
            @Override
            public String getToolTipText(MouseEvent event) {
                int column = columnAtPoint(event.getPoint());
                int row = rowAtPoint(event.getPoint());
                TableCellRenderer renderer = getCellRenderer(row, column);
                Component comp = prepareRenderer(renderer, row, column);
                if (comp.getPreferredSize().width <= getColumn(column).getWidth()) return null;
                if (renderer instanceof StringValue) {
                    return ((StringValue) renderer).getString(getValueAt(row, column));
                }
                return null;
            }

            @Override
            public Point getToolTipLocation(MouseEvent event) {
                int column = columnAtPoint(event.getPoint());
                int row = rowAtPoint(event.getPoint());
                Rectangle cellRect = getCellRect(row, column, false);
                if (!getComponentOrientation().isLeftToRight()) {
                    cellRect.translate(cellRect.width, 0);
                }
                // PENDING JW: otherwise we get a small (borders only) tooltip for null
                // core issue? Yeh, the logic in tooltipManager is crooked.
                // but this here is ehem ... rather arbitrary, not working if value
                // not null without tooltip. 
                return getValueAt(row, column) == null ? null : cellRect.getLocation();
                // working - might be costly?
//                return getToolTipText(event) == null ? null : cellRect.getLocation();
//                return null;
            }
            
            
            
        };
        table.setColumnControlVisible(true);
        JXFrame frame = wrapWithScrollingInFrame(table, "tooltip");
        addComponentOrientationToggle(frame);
        show(frame);
    }

    public void interactiveToolTipTree() {
        ComponentTreeTableModel model = new ComponentTreeTableModel(new JXFrame());
        JXTree tree = new JXTree(model) {

            @Override
            public String getToolTipText(MouseEvent event) {
                int row = getRowForLocation(event.getX(), event.getY());
                if (row < 0) return null;
                TreeCellRenderer renderer = getCellRenderer();
                TreePath     path = getPathForRow(row);
                Object       lastPath = path.getLastPathComponent();
                Component comp = renderer.getTreeCellRendererComponent(this, 
                        lastPath, isRowSelected(row), isExpanded(row), 
                        getModel().isLeaf(lastPath), row, false);
                int width = getVisibleRect().width;
                if (comp.getPreferredSize().width <= width ) return null;
                if (renderer instanceof JXTree.DelegatingRenderer) {
                    renderer = ((JXTree.DelegatingRenderer) renderer).getDelegateRenderer();
                    if (renderer instanceof StringValue) {
                        return ((StringValue) renderer).getString(lastPath);
                    }
                }
                return null;
            }
            @SuppressWarnings("unused")
            private int getVisibleWidth() {
                int width = getVisibleRect().width;
                int indent =  (((BasicTreeUI)getUI()).getLeftChildIndent() + ((BasicTreeUI)getUI()).getRightChildIndent());
                return width;
            }

            @Override
            public Point getToolTipLocation(MouseEvent event) {
                return null;
            }
            
            
            
        };
        
        tree.expandAll();
        tree.setCellRenderer(new DefaultTreeRenderer());
        // I'm registered to do tool tips so we can draw tips for the renderers
        ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
        toolTipManager.registerComponent(tree);
        JXFrame frame = showWithScrollingInFrame(tree, "tooltip");
        addComponentOrientationToggle(frame);
        show(frame, 400, 400);
    }

    
    /**
     * PENDING JW: really fancify or remove ;-)
     */
    public void interactiveTreeFancyButton() {
        JXTree tree = new JXTree();
        tree.setRowHeight(30);
        MattePainter painter = new MattePainter(Color.YELLOW);
        Highlighter hl = new PainterHighlighter(HighlightPredicate.ROLLOVER_ROW, painter);
        tree.addHighlighter(hl);
        ComponentProvider<?> provider = new NormalButtonProvider(StringValues.TO_STRING, JLabel.LEADING);
        tree.setCellRenderer(new DefaultTreeRenderer(provider));
        tree.setRolloverEnabled(true);
        showWithScrollingInFrame(tree, "Fancy..");
    }
    
    public void interactiveFancyButton() {
        JXButton button = new JXButton("Dummy .... but lonnnnnnngg");
        button.setBorder(BorderFactory.createCompoundBorder(new DropShadowBorder(), button.getBorder()));
        JXPanel panel = new JXPanel();
        panel.add(button);
        showInFrame(panel, "Fancy..");
    }
    
    public static class NormalButtonProvider extends CheckBoxProvider 
        implements RolloverRenderer {

        private Border border;

        /**
         * @param toString
         * @param leading
         */
        public NormalButtonProvider(StringValue toString, int leading) {
            super(toString, leading);
            setBorderPainted(true);
        }

        
        @Override
        protected void configureState(CellContext context) {
            super.configureState(context);
            rendererComponent.setBorder(border);
            Point p = (Point) context.getComponent().getClientProperty(
                    RolloverProducer.ROLLOVER_KEY);
            if (/* hasFocus || */(p != null && (p.x >= 0)
                    && (p.x == context.getColumn()) && (p.y == context.getRow()))) {
                rendererComponent.getModel().setRollover(true);
            } else {
                rendererComponent.getModel().setRollover(false);
            }
        }


        @Override
        protected AbstractButton createRendererComponent() {
            JXButton button = new JXButton();
            border = BorderFactory.createCompoundBorder(
                    new DropShadowBorder(),
                    button.getBorder());
            return button;
        }


        public void doClick() {
            // TODO Auto-generated method stub
            
        }


        public boolean isEnabled() {
            // TODO Auto-generated method stub
            return true;
        }
        
        
        
    }

//--------------- unit tests

    
    /**
     * Issue #794-swingx: tooltip must be reset.
     * 
     * Here: TreeTableCellRenderer (the tree used for rendering the hierarchical 
     * column)
     * 
     */
    public void testToolTipResetTreeTableTreeRenderer() {
        JXTreeTable treeTable = new JXTreeTable(new ComponentTreeTableModel(new JXPanel()));
        JComponent label = (JComponent) treeTable.prepareRenderer(treeTable.getCellRenderer(0, 0), 0, 0);
        String tip = "some tip";
        label.setToolTipText(tip);
        assertEquals("sanity: tooltip must be set", tip, label.getToolTipText());
        // prepare again
        label = (JComponent) treeTable.prepareRenderer(treeTable.getCellRenderer(0, 0), 0, 0);
        assertEquals("tooltip must be reset in each prepare", null, label.getToolTipText());
    }

    /**
     * Issue #774-swingx: support per node-type icons.
     * 
     * postponed to 0.9.x - will break all interface implementors.
     * 
     */
    public void testNodeTypeIcons() {
       TreeCellContext context = new TreeCellContext();
       context.installContext(null, "dummy", -1, -1, false, false, false, true);
       final Icon custom = XTestUtils.loadDefaultIcon();
       IconValue iv = new IconValue() {

        public Icon getIcon(Object value) {
            // TODO Auto-generated method stub
            return custom;
        }
           
       };
       WrappingProvider provider = new WrappingProvider(iv);
       WrappingIconPanel comp = provider.getRendererComponent(context);
       assertEquals(custom, comp.getIcon());
       fail("feature request: per node type iconValue");
    }

    /**
     * base interaction with list: focused, not-selected uses UI border.
     * Moved from ListRendererTest: failes on the new server (what's the default LF there?)
     * TODO: fix and reinstate the test
     * @throws UnsupportedLookAndFeelException 
     */
    public void testListFocusBorder() throws UnsupportedLookAndFeelException {
        LookAndFeel lf = UIManager.getLookAndFeel();
        try {
            UIManager.setLookAndFeel(new MotifLookAndFeel());
            JList list = new JList(new Object[] {1, 2, 3});
            ListCellRenderer coreListRenderer = new DefaultListCellRenderer();
            ListCellRenderer xListRenderer = new DefaultListRenderer();
            // access ui colors
            Border focusBorder = UIManager.getBorder("List.focusCellHighlightBorder");
            // sanity
            assertNotNull(focusBorder);
            // JW: this looks suspicious ... 
            // RAH: line below makes hudson fail the test tho it runs fine locally ...
            assertNotSame(focusBorder, UIManager.getBorder("Table.focusCellHighlightBorder"));
            // need to prepare directly - focus is true only if list is focusowner
            JComponent coreComponent = (JComponent) coreListRenderer.getListCellRendererComponent(list, 
                    null, 0, false, true);
            // sanity: known standard behaviour
            assertEquals(focusBorder, coreComponent.getBorder());
            // prepare extended
            JComponent xComponent = (JComponent) xListRenderer.getListCellRendererComponent(list, 
                    null, 0, false, true);
            // assert behaviour same as standard
            assertEquals(coreComponent.getBorder(), xComponent.getBorder());
        } finally {
            UIManager.setLookAndFeel(lf);
        }
    }

    /**
     * test if renderer properties are updated on LF change. <p>
     * Note: this can be done examplary only. Here: we use the 
     * font of a rendererComponent returned by a HyperlinkProvider for
     * comparison. There's nothing to test if the font are equal
     * in System and crossplattform LF. <p>
     * 
     * There are spurious problems when toggling UI (since when?) 
     * with LinkRenderer
     * "no ComponentUI class for: org.jdesktop.swingx.LinkRenderer$1"
     * that's the inner class JXHyperlink which overrides updateUI.
     * 
     * PENDING: this was moved from tableUnitTest - had been passing with
     * LinkRenderer but with HyperlinkProvider
     * now is failing (on server with defaultToSystem == false, locally win os 
     * with true), probably due to slightly different setup now 
     * in renderer defaultVisuals? It resets the font to table's which
     * LinkRenderer didn't. Think whether to change the provider go back
     * to hyperlink font? 
     */
    public void testUpdateRendererOnLFChange() {
        boolean defaultToSystemLF = true;
        setSystemLF(defaultToSystemLF);
        TableCellRenderer comparison = new DefaultTableRenderer(new HyperlinkProvider());
        TableCellRenderer linkRenderer = new DefaultTableRenderer(new HyperlinkProvider());
        JXTable table = new JXTable(2, 3);
        Component comparisonComponent = comparison.getTableCellRendererComponent(table, null, false, false, 0, 0);
        Font comparisonFont = comparisonComponent.getFont();
        table.getColumnModel().getColumn(0).setCellRenderer(linkRenderer);
        setSystemLF(!defaultToSystemLF);
        SwingUtilities.updateComponentTreeUI(comparisonComponent);
        if (comparisonFont.equals(comparisonComponent.getFont())) {
            LOG.info("cannot run test - equal font " + comparisonFont);
            return;
        }
        SwingUtilities.updateComponentTreeUI(table);
        Component rendererComp = table.prepareRenderer(table.getCellRenderer(0, 0), 0, 0);
        assertEquals("renderer font must be updated", 
                comparisonComponent.getFont(), rendererComp.getFont());
        
    }

    /**
     * RendererLabel NPE with null Graphics. While expected,
     * the exact location is not.
     * NPE in JComponent.paintComponent finally block 
     *
     */
    public void testLabelNPEPaintComponentOpaque() {
        JRendererLabel label = new JRendererLabel();
        label.setOpaque(true);
        label.paintComponent(null);
    }
    
    /**
     * RendererLabel NPE with null Graphics. While expected,
     * the exact location is not.
     * NPE in JComponent.paintComponent finally block 
     *
     */
    public void testLabelNPEPaintComponent() {
        JRendererLabel label = new JRendererLabel();
        label.setOpaque(false);
        label.paintComponent(null);
    }



}
