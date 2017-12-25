/*
 * $Id: RendererVisualCheck.java 4283 2013-02-26 17:37:02Z kleopatra $
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
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.SwingConstants;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXEditorPaneTest;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.BorderHighlighter;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.decorator.HighlighterFactory.UIColorHighlighter;
import org.jdesktop.swingx.decorator.PainterHighlighter;
import org.jdesktop.swingx.decorator.PatternPredicate;
import org.jdesktop.swingx.hyperlink.EditorPaneLinkVisitor;
import org.jdesktop.swingx.hyperlink.HyperlinkAction;
import org.jdesktop.swingx.hyperlink.LinkModel;
import org.jdesktop.swingx.hyperlink.LinkModelAction;
import org.jdesktop.swingx.painter.BusyPainter;
import org.jdesktop.swingx.painter.ImagePainter;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.rollover.RolloverRenderer;
import org.jdesktop.swingx.table.ColumnControlButton;
import org.jdesktop.swingx.test.ComponentTreeTableModel;
import org.jdesktop.swingx.test.XTestUtils;
import org.jdesktop.swingx.treetable.FileSystemModel;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableNode;
import org.jdesktop.swingx.util.PaintUtils;
import org.jdesktop.test.AncientSwingTeam;

/**
 * Visual check of extended Swingx renderers.
 * 
 * @author Jeanette Winzenburg
 */
public class RendererVisualCheck extends InteractiveTestCase {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(RendererVisualCheck.class.getName());
    
    public static void main(String[] args) {
        // Note JW: to check opacity issue in renderers _do not_ toggle
        // laf during runtime (that's how
        // instead start a new instance of the visual check
        // with another LAF
        setLAF("Nim");
        RendererVisualCheck test = new RendererVisualCheck();
        try {
//            test.runInteractiveTests();
//          test.runInteractiveTests(".*CheckBox.*");
//          test.runInteractiveTests(".*CustomIcons.*");
//          test.runInteractiveTests(".*XLabel.*");
//            test.runInteractiveTests(".*Button.*");
//          test.runInteractiveTests(".*TextArea.*");
//          test.runInteractiveTests(".*Text.*");
//          test.runInteractiveTests(".*Color.*");
//          test.runInteractiveTests("interactive.*ColumnControl.*");
//            test.runInteractive("RowGrouping");
//          test.runInteractive("Link");
            test.runInteractive("Opacity");
            test.runInteractive("CheckBox");
//          test.runInteractive("TreeRenderer");
//          test.runInteractive("URI");
            
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }

    /**
     * Issue swingx-1514: icon background always highlighted. <p>
     * Actually a problem introduced by #3789 version of JXPanel
     * Not special to the hierarchical nature, same for list
     * 
     * Not fixed (1.6.5) for Nimbus (and potentially other synth-based lafs)
     * 
     * @see org.jdesktop.swingx.renderer.RendererVisualCheck#interactiveIconTextAlignmentAndExtendsOpacity
     */
    public void interactiveTreeRendererExtendsOpacity() {
        JXTree table = new JXTree();
        table.expandAll();
        final WrappingProvider wrapper = new WrappingProvider();
        table.setCellRenderer(new DefaultTreeRenderer(wrapper));
        table.addHighlighter(HighlighterFactory.createSimpleStriping());
        JXFrame frame = wrapWithScrollingInFrame(table, "background on icon?");
        addAction(frame, createToggleExtendsOpacityAction(wrapper, table));
        show(frame);
    }
    
    private Action createToggleExtendsOpacityAction(final WrappingProvider provider, final JComponent target) {
        final String text = "toggle extendsOpacity to: ";
        Action a = new AbstractAction(text + !provider.getExtendsComponentOpacity()) {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean old = provider.getExtendsComponentOpacity();
                provider.setExtendsComponentOpacity(!old);
                putValue(Action.NAME, text + old);
                target.repaint();
            }
        };
        return a;
    }


    
    /**
     * Issue #1513-swingx: opacity of JRendererCheckBox can't be effected by 
     * client code.
     * 
     * Stand-alone JRendererCheckBox showing permutations of 
     * not/opaque and with/out painter
     */
    public void interactiveRendererCheckBox() {
        JRendererCheckBox opaque = new JRendererCheckBox();
        opaque.setText("I'm opaque without painter");
        opaque.setBackground(Color.YELLOW);
        
        JRendererCheckBox opaqueWith = new JRendererCheckBox();
        opaqueWith.setText("I'm opaque with painter");
        opaqueWith.setBackground(Color.YELLOW);
        opaqueWith.setPainter(new ImagePainter(XTestUtils.loadDefaultImage()));
        opaqueWith.setForeground(Color.GREEN);
        
        JRendererCheckBox transparent = new JRendererCheckBox();
        transparent.setText("I'm transparent without painter");
        transparent.setBackground(Color.YELLOW);
//        transparent.setContentAreaFilled(false);
        transparent.setOpaque(false);
//        transparent.setPainter(new BusyPainter());
        
        
        JRendererCheckBox transparentWith = new JRendererCheckBox();
        transparentWith.setText("I'm transparent WITH painter");
        transparentWith.setBackground(Color.YELLOW);
//        transparentWith.setContentAreaFilled(false);
        transparentWith.setOpaque(false);
        transparentWith.setPainter(new ImagePainter(XTestUtils.loadDefaultImage()));
        transparentWith.setForeground(Color.GREEN);
        
        
        JCheckBox plain = new JCheckBox("I'm a plain default box");
        plain.setBackground(Color.YELLOW);
//        plain.setOpaque(false);
        
        JPanel content = new JPanel();
        content.setBackground(Color.WHITE);
        content.add(opaque);
        content.add(opaqueWith);
        content.add(transparent);
        content.add(transparentWith);
        content.add(plain);
        JXFrame frame = wrapInFrame(content, "checkRendereres");
        show(frame);
        
    }
    /**
     * Issue #1513-swingx: opacity of JRendererCheckBox can't be effected by 
     * client code.
     * 
     * Here's a use-case: make all components in the stack not-opaque to show
     * a background image. (note: opacity not false for number column)
     */
    public void interactiveCheckBoxRendererOpacity1513() {
        JXPanel panel = new JXPanel(new BorderLayout());
        panel.setBackgroundPainter(new ImagePainter(XTestUtils.loadDefaultImage("moon.jpg")));
        JXTable table = new JXTable(new AncientSwingTeam());
        table.addHighlighter(HighlighterFactory.createSimpleStriping());
        table.addHighlighter(new PainterHighlighter(HighlightPredicate.ROLLOVER_ROW, 
                new MattePainter(PaintUtils.setAlpha(Color.RED, 100))));
//                new BusyPainter()));
        panel.add(new JScrollPane(table));
        table.setOpaque(false);
        JComponent comp = (JComponent) table.prepareRenderer(0, 0);
        comp.setOpaque(false);
        AbstractButton checkBox = (AbstractButton) table.prepareRenderer(0, AncientSwingTeam.BOOLEAN_COLUMN);
        checkBox.setOpaque(false);
        ((JComponent) table.getParent()).setOpaque(false);
        ((JComponent) table.getParent().getParent()).setOpaque(false);
        showInFrame(panel, "Checkbox: set to opacity");
    }

    /**
     * Issue #??-swingx: first click in unselected (in terms of 
     * listSelection, not checkBox) checkbox does not change 
     * the editor background to selected. 
     * 
     * Suspected culprit is the editor: the click starts the 
     * edit, but doesn't update the background of the editor
     * itself. Once selected, the editor will be selected as well.
     * 
     * Core issue: Problem is that at the time of getting the
     * editing component, the row is not yet selected. Based on 
     * shouldSelectedCell, the table's row selection is updated 
     * _after_ installing the editing component.
     * 
     * Options:
     * - override changeSelection to special case the editing comp
     * - tweak the editor to config the renderer with isSelected == true
     *  (or with shouldSelect)
     *  
     */
    public void interactiveCheckBoxEditorSelectBackground() {
        JXTable table = new JXTable(new AncientSwingTeam());
        JTable core = new JTable(table.getModel());
        
        JComponent comp = Box.createHorizontalBox();
        comp.add(new JScrollPane(table));
        comp.add(new JScrollPane(core));
        showInFrame(comp, "compare checkbox editor x <-> core");
    }
    /**
     * Issue #897-swingx: Opacity issues of JRendererCheckBox - striping lost.
     * 
     *  Reported against Nimbus, but similar in other LAFs if combined
     *  with Painter.
     */
    public void interactiveCheckBoxRendererOpacity897() {
        JXTable table = new JXTable(new AncientSwingTeam());
        table.addHighlighter(HighlighterFactory.createSimpleStriping());
        table.addHighlighter(new PainterHighlighter(HighlightPredicate.ROLLOVER_ROW, 
                new MattePainter(PaintUtils.setAlpha(Color.RED, 100))));
//                new BusyPainter()));
        showWithScrollingInFrame(table, "Checkbox: striping lost on rollover");
    }



    
    /**
     * Conditionally hide the renderingComponent
     * 
     * http://stackoverflow.com/questions/9311455/swingx-component-provider-hide-component-on-certain-rows
     */
    public void interactiveButtonProvider() {
        JXTable table = new JXTable(new AncientSwingTeam());
        table.getColumn(0).setCellRenderer(new DefaultTableRenderer(new ButtonProvider()));
        table.getColumn(1).setCellRenderer(new DefaultTableRenderer(
                new WrappingProvider(IconValues.NONE, new ButtonProvider(), false) {

                    @Override
                    protected void configureState(CellContext context) {
                        super.configureState(context);
                        rendererComponent.getComponent().setVisible(true);
                    }

                    @Override
                    public boolean isEnabled() {
                        boolean enabled = super.isEnabled();
                        LOG.info("rollover " + enabled);
                        return enabled;
                    }
                    
                    
                }
        ));
        
        AbstractHighlighter highlighter = new AbstractHighlighter(HighlightPredicate.EVEN) {

            @Override
            protected Component doHighlight(Component component,
                    ComponentAdapter adapter) {
                ((WrappingIconPanel) component).getComponent().setVisible(false);
                return component;
            }

            @Override
            protected boolean canHighlight(Component component,
                    ComponentAdapter adapter) {
                return component instanceof WrappingIconPanel;
            }
            
            
        };
//        table.addHighlighter(highlighter);
        showWithScrollingInFrame(table, "invisible button");
    }
    
    public static class ButtonProvider extends ComponentProvider<JButton> implements 
       RolloverRenderer {

        @Override
        protected void format(CellContext context) {
            rendererComponent.setText(getValueAsString(context));
        }

        @Override
        protected void configureState(CellContext context) {
            rendererComponent.setHorizontalAlignment(getHorizontalAlignment());
        }

        @Override
        protected JButton createRendererComponent() {
            return new JButton("View online");
        }

        @Override
        public boolean isEnabled() {
            return rendererComponent.isVisible();
        }

        @Override
        public void doClick() {
            // TODO Auto-generated method stub
            
        }

        
    }
    

    
    private Object[] columnNames = {
            "Buy/Sell", "Type", "SubType", "Ccy1", "Amount1", "Ccy2", "Amount2", "DealId"};
        private Object[][] data = {
            {"Buy&Sell", "Ccy Swap", "A1", "EUR", new Double(1000000.00), "USD", new Double(1439000.00), 50},
            {"Buy&Sell", "Ccy Swap", "A3", "USD", new Double(1438900.00), "EUR", new Double(1000000.00), 50},
            {"Buy&Sell", "Ccy Swap", "A1", "EUR", new Double(500000.00), "CHF", new Double(550000.00), 350},
            {"Buy&Sell", "Ccy Swap", "A1", "CHF", new Double(549800.00), "EUR", new Double(500000.00), 350},
            {"Sell&Buy", "Ccy Swap", "A3", "USD", new Double(1000000.00), "EUR", new Double(749000.00), 2250},
            {"Sell&Buy", "Ccy Swap", "A1", "EUR", new Double(748900.00), "USD", new Double(1000000.00), 2250},
            {"Buy&Sell", "Ccy Swap", "A1", "GBP", new Double(1000000.00), "USD", new Double(1638100.00), 400},
            {"Buy&Sell", "Ccy Swap", "A3", "USD", new Double(1638200.00), "GBP", new Double(1000000.00), 400},
            {"Sell", "Ccy Spot", "A1", "AUD", new Double(343575.0), "EUR", new Double(250000.0), 11990},
            {"Buy", "Ccy Spot", "A1", "EUR", new Double(100000.00), "JPY", new Double(1099000.00), 259},
            {"Sell", "Ccy Fwd", "A3", "DKK", new Double(74889.00), "EUR", new Double(10000.00), 115439},};

    public void interactiveAlternateRowGrouping() {
        JXTable table = new JXTable(data, columnNames);
        HighlightPredicate predicate =  new HighlightPredicate() {

            @Override
            public boolean isHighlighted(Component renderer,
                    ComponentAdapter adapter) {
                if (adapter.row == 0) return false;
                return isOddValue(adapter);
            }

            private boolean isOddValue(ComponentAdapter adapter) {
                Object previous = adapter.getFilteredValueAt(0, 7);
                boolean odd = false;
                for (int i = 1; i <= adapter.row; i++) {
                    Object current = adapter.getFilteredValueAt(i, 7);
                    if (!previous.equals(current)) {
                        odd = !odd;
                    }
                    previous = current;
                }
                return odd;
            }
            
        };
        table.addHighlighter(new UIColorHighlighter(predicate));
        showWithScrollingInFrame(table, "value-grouped alternate striping");
    }    
    
    
    public void interactiveAlternateRowGroupingRobSecond() {
        JTable table = new JTable(data, columnNames) {

            @Override
            public Component prepareRenderer(TableCellRenderer renderer,
                    int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(getRowBackground(row));
                }

                return c;
            }

            private Color getRowBackground(int row) {
                boolean isDark = true;

                Object previous = getValueAt(0, 7);

                for (int i = 1; i <= row; i++) {
                    Object current = getValueAt(i, 7);

                    if (!current.equals(previous)) {
                        isDark = !isDark;
                        previous = current;
                    }
                }

                return isDark ? Color.ORANGE : Color.YELLOW;
            }

        };
        table.setAutoCreateRowSorter(true);
        showWithScrollingInFrame(table,
                "Rob: value-grouped striping, sort-robust");
    }

    public void interactiveAlternateRowGroupingRob() {
        JTable table = new JTable(data, columnNames) {
            private Map<Object, Color> rowColor = new HashMap<Object, Color>();

            private Color nextColor = Color.ORANGE;

            @Override
            public Component prepareRenderer(TableCellRenderer renderer,
                    int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);

                if (isRowSelected(row))
                    return c;

                Object value = getValueAt(row, 7);
                Color background = rowColor.get(value);

                if (background != null) {
                    c.setBackground(background);
                } else {
                    rowColor.put(value, nextColor);
                    c.setBackground(nextColor);
                    nextColor = (nextColor == Color.ORANGE) ? Color.YELLOW
                            : Color.ORANGE;
                }

                return c;
            }

        };
        table.setAutoCreateRowSorter(true);
        showWithScrollingInFrame(table, "Rob: value-grouped striping");
    }
    /**
     * Issue #1345-swingx: make TableCellContext handle LAF provided alternateRowColor.
     * 
     * Note: running this method has static side-effects, run in isolation or press cleanup.
     */
    public void interactiveOptionalAlternateRowBackground() {
        final Color original = UIManager.getColor("Table.alternateRowColor");
        final JXTable table = new JXTable(new AncientSwingTeam());
        JXFrame frame = wrapWithScrollingInFrame(table, "toggle handle alternateRow");
        Action toggle = new AbstractAction("toggle alternate row as default visual") {
            boolean isHandling;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                isHandling = !isHandling;
                UIManager.put(TableCellContext.HANDLE_ALTERNATE_ROW_BACKGROUND, isHandling);
                if (UIManager.getColor("Table.alternateRowColor") == null) {
                    UIManager.put("Table.alternateRowColor", HighlighterFactory.FLORAL_WHITE);
                }
                table.repaint();
            }
        };
        addAction(frame, toggle);
        
        Action cleanup = new AbstractAction("cleanup alternateRowHandling") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                UIManager.put(TableCellContext.HANDLE_ALTERNATE_ROW_BACKGROUND, false);
                UIManager.put("Table.alternateRowColor", original);
                table.repaint();
            }
        };
        addAction(frame, cleanup);
        show(frame);
    }

    /**
     * Requirement: left aligned icon and right aligned text.
     * http://forums.java.net/jive/thread.jspa?messageID=398448#398448
     * 
     * Issue #1309-swingx: WrappingProvider needs option to "highlight" the icon as well.
     */
    public void interactiveIconTextAlignmentAndExtendsOpacity() {
        ListModel files = createFileListModel();
        final JXList list = new JXList(files);
        ComponentProvider<?> text = new LabelProvider(StringValues.FILE_NAME, JLabel.TRAILING);
        final WrappingProvider wrapper = new WrappingProvider(IconValues.FILE_ICON, text, true);
        wrapper.setExtendsComponentOpacity(true);
        list.setCellRenderer(new DefaultListRenderer(wrapper));
        list.addHighlighter(HighlighterFactory.createSimpleStriping());
        JXFrame frame =showWithScrollingInFrame(list, "alignment in wrappingProvider");
        Action action = new AbstractAction("toggle icon opacity") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                wrapper.setExtendsComponentOpacity(!wrapper.getExtendsComponentOpacity());
                list.repaint();
            }
        };
        addAction(frame, action);
        JLabel label = new JLabel(IconValues.FILE_ICON.getIcon(list.getElementAt(1)));
        addStatusComponent(frame, label);
    }
    
    /**
     * List/tree filled with TreeNodes wrapping a File.
     * 
     */
    public void interactiveTreeNodeCustomIcons() {
        ListModel files = createFileListModel();
        
        JXList table = new JXList(files);
        StringValue sv = new StringValue() {

            @Override
            public String getString(Object value) {
                if (value instanceof File) {
                    return FileSystemView.getFileSystemView().getSystemDisplayName((File) value)
                       + " Type: " 
                       + FileSystemView.getFileSystemView().getSystemTypeDescription((File) value); 
                } 
                return StringValues.TO_STRING.getString(value);
            }
            
        };
        IconValue iv = IconValues.FILE_ICON;
        table.setCellRenderer(new DefaultListRenderer(new MappedValue(sv, iv)));
        final JXTree tree = new JXTree(createFileNodeModel());
        final DefaultTreeRenderer treeRenderer = new DefaultTreeRenderer(iv, sv);
        
        tree.setCellRenderer(treeRenderer);
        // string based. Note: this example is locale dependent
        String folderDescription = ".*ordner.*";
        PatternPredicate predicate = new PatternPredicate(folderDescription, 0, -1);
        Highlighter hl = new ColorHighlighter(predicate, null, Color.RED);
        table.addHighlighter(hl);
        tree.addHighlighter(hl);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);
        final Date lastYear = calendar.getTime();
        // install value based highlighter 
        final HighlightPredicate valueBased = new HighlightPredicate() {

            @Override
            public boolean isHighlighted(Component renderer,
                    ComponentAdapter adapter) {
                if (!(adapter.getValue() instanceof File)) return false;
                File file = (File) adapter.getValue();
                Date date = new Date(file.lastModified());
                return date.after(lastYear);
            }
            
        };
        final ColorHighlighter back =  new ColorHighlighter(valueBased, Color.YELLOW, null);
        table.addHighlighter(back);
        tree.addHighlighter(back);
        tree.setRolloverEnabled(true);
        tree.addHighlighter(new BorderHighlighter(HighlightPredicate.ROLLOVER_ROW, 
                BorderFactory.createLineBorder(Color.BLUE)));
        final HighlightPredicate valueBasedUnwrap = new HighlightPredicate() {

            @Override
            public boolean isHighlighted(Component renderer,
                    ComponentAdapter adapter) {
                File file = getUserObject(adapter.getValue());
                if (file == null) return false;
                Date date = new Date(file.lastModified());
                return date.after(lastYear);
            }

            private File getUserObject(Object value) {
                if (value instanceof File) return (File) value;
                if (value instanceof DefaultMutableTreeNode) {
                    return getUserObject(((DefaultMutableTreeNode) value).getUserObject());
                }
                if (value instanceof TreeTableNode) {
                    return getUserObject(((TreeTableNode) value).getUserObject());
                }
                return null;
            }

            
        };
        JXFrame frame = showWithScrollingInFrame(tree, table, "Tree/List: same string/icon values ");
        Action toggleUnwrap = new AbstractAction("toggle unwrapUserObject") {

            @Override
            public void actionPerformed(ActionEvent e) {
                WrappingProvider provider = (WrappingProvider) treeRenderer.getComponentProvider();
                provider.setUnwrapUserObject(!provider.getUnwrapUserObject());
                tree.revalidate();
                tree.repaint();
            }
            
        };
        addAction(frame, toggleUnwrap);
        Action togglePredicate = new AbstractAction("toggle predicateUnwrap") {

            @Override
            public void actionPerformed(ActionEvent e) {
                HighlightPredicate old = back.getHighlightPredicate();
                back.setHighlightPredicate(old == valueBased ? valueBasedUnwrap : valueBased);
            }
            
        };
        addAction(frame, togglePredicate);
        Action toggleLargeModel = new AbstractAction("toggle largeModel") {

            @Override
            public void actionPerformed(ActionEvent e) {
                tree.setLargeModel(!tree.isLargeModel());
                tree.revalidate();
                tree.repaint();
            }
            
        };
        addAction(frame, toggleLargeModel);
        addStatusMessage(frame, "node is treeNode wrapping file - string/value based Highlighters differ");
        
    }
   
    
    /**
     * @return
     */
    private TreeModel createFileNodeModel() {
        FileSystemModel tree = new FileSystemModel();
        File root = tree.getRoot();
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(root);
        for (int i = 0; i < tree.getChildCount(root); i++) {
            rootNode.add(new DefaultMutableTreeNode(tree.getChild(root, i)));
        }
        
        return new DefaultTreeModel(rootNode);
    }

    /**
     * @return
     */
    private ListModel createFileListModel() {
        FileSystemModel tree = new FileSystemModel();
        DefaultListModel list = new DefaultListModel();
        File root = tree.getRoot();
        list.addElement(root);
        for (int i = 0; i < tree.getChildCount(root); i++) {
            list.addElement(tree.getChild(root, i));
        }
        return list;
    }

    /**
     * example to configure treeTable hierarchical column (same for tree) with
     * custom icon and content mapping. Here the nodes are actually of type File.
     * 
     * PENDING JW:
     * TreeTable string rep is not correctly recognized in the PatternHighlighter,
     *  - will be correct automatically, once we make it to really use the table/model rep
     *  - could explicitly use the treecellrenderer as stringValue in the meantime?
     */
    public void interactiveTreeTableCustomIcons() {
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
        JXTreeTable table = new JXTreeTable(model);
        StringValue sv = new StringValue() {

            @Override
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

            @Override
            public Icon getIcon(Object value) {
                if (value instanceof File) {
                    return  FileSystemView.getFileSystemView().getSystemIcon((File) value);
                } 
                return null;
            }};
        final DefaultTreeRenderer treeRenderer = new DefaultTreeRenderer(iv, sv);
        table.setTreeCellRenderer(treeRenderer);
        final JXTree tree = new JXTree(model);
        tree.setCellRenderer(treeRenderer);
        tree.setRolloverEnabled(true);
        tree.addHighlighter(new BorderHighlighter(HighlightPredicate.ROLLOVER_ROW, 
                BorderFactory.createLineBorder(Color.BLUE)));
        // string based. Note: this example is locale dependent
        String folderDescription = ".*ordner.*";
        PatternPredicate predicate = new PatternPredicate(folderDescription, 0, -1);
        Highlighter hl = new ColorHighlighter(predicate, null, Color.RED);
        table.addHighlighter(hl);
        tree.addHighlighter(hl);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);
        final Date lastYear = calendar.getTime();
        // install value based highlighter 
        HighlightPredicate valueBased = new HighlightPredicate() {

            @Override
            public boolean isHighlighted(Component renderer,
                    ComponentAdapter adapter) {
                if (!(adapter.getValue() instanceof File)) return false;
                File file = (File) adapter.getValue();
                Date date = new Date(file.lastModified());
                return date.after(lastYear);
            }
            
        };
        ColorHighlighter back =  new ColorHighlighter(valueBased, Color.YELLOW, null);
        table.addHighlighter(back);
        tree.addHighlighter(back);
        JXFrame frame =showWithScrollingInFrame(tree, table, "Tree/Table: same string/icon value");
        addStatusMessage(frame, "node is File - string/value based highlighters same");
    }
    
    /**
     * Use formatting from sql date/time classes.
     *
     */
    public void interactiveTableSQLDateTime() {
        Date date = new Date();
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        Timestamp stamp = new Timestamp(date.getTime());
        Time time = new Time(date.getTime());
        DefaultTableModel model = new DefaultTableModel(1, 5) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (getRowCount() > 0) {
                    Object value = getValueAt(0, columnIndex);
                    if (value != null) {
                        return value.getClass();
                    }
                }
                return super.getColumnClass(columnIndex);
            }
            
        };
        model.setColumnIdentifiers(new Object[]{"Date - normal", "SQL Date", "SQL Timestamp", "SQL Time", "Date - as time"});
        model.setValueAt(date, 0, 0);
        model.setValueAt(sqlDate, 0, 1);
        model.setValueAt(stamp, 0, 2);
        model.setValueAt(time, 0, 3);
        model.setValueAt(date, 0, 4);
        JXTable table = new JXTable(model);
        // right align to see the difference to normal date renderer
        DefaultTableRenderer renderer = new DefaultTableRenderer(
                new LabelProvider(SwingConstants.RIGHT));
        table.setDefaultRenderer(Timestamp.class, renderer);
        table.setDefaultRenderer(Time.class, renderer);
        table.setDefaultRenderer(java.sql.Date.class, renderer);
        // format the given Date as short time
        table.getColumnExt(4).setCellRenderer(new DefaultTableRenderer(
                new FormatStringValue(DateFormat.getTimeInstance(DateFormat.SHORT))));
        showWithScrollingInFrame(table, "normal/sql date formatting"); 
    }
    
    /**
     * Quick example of using a JTextArea as rendering component and 
     * dynamically adjust table row height to max pref height of the
     * cells in a row.
     *
     */
    public void interactiveTextAreaRendererTable() {
        DefaultTableModel model = new DefaultTableModel(0, 2);
        model.addRow(new String[]{"some really, maybe really really long text -  "
                + "wrappit .... where needed ", "some really, maybe really really long text -  " +
                "wrappit .... where needed "});
        model.addRow(new String[]{"another really, maybe really really long text -  "
                + "with nothing but junk. wrappit .... where needed", "some really, maybe really really long text -  " +
                "wrappit .... where needed "});


        JXTable table = new JXTable(model);
        table.setColumnControlVisible(true);
        table.setDefaultRenderer(Object.class, new DefaultTableRenderer(new TextAreaProvider()));
        table.addHighlighter(HighlighterFactory.createSimpleStriping());
        installDynamicRowHeights(table);
        showWithScrollingInFrame(table, "textArea as rendering comp");
    }

    /**
     * Configures the table to allow individual row heights based on rendering components
     * pref size and inits the sizes. Installs an extended column model listener to 
     * update the rowHeights on changes to the column widths.
     * 
     * NOTE: this is not complete - real-world methods must 
     * - listen to the TableModel to update the rowHeight 
     * - listen to table's property changes for its Table/ColumnModel to 
     *   re-wire the appropriate listeners.
     * - be prepared to handle performance problems for large models
     * 
     * @param table
     */
    private void installDynamicRowHeights(final JXTable table) {
        updateRowHeight(table);
        TableColumnModelListener columnListener = new TableColumnModelListener() {

            @Override
            public void columnAdded(TableColumnModelEvent e) {
                updateRowHeight(table);
            }

            @Override
            public void columnMarginChanged(ChangeEvent e) {
                // here we rely on an implementation detail of DefaultTableColumnModel:
                // which fires a marginChanged is column widths are changed
                updateRowHeight(table);
            }

            @Override
            public void columnMoved(TableColumnModelEvent e) {
            }

            @Override
            public void columnRemoved(TableColumnModelEvent e) {
                updateRowHeight(table);
            }

            @Override
            public void columnSelectionChanged(ListSelectionEvent e) {
            }
            
        };
        table.getColumnModel().addColumnModelListener(columnListener);
    }
    
    /**
     * Sets per-row rowHeight based on the rendering component's preferred height.
     * 
     * @param table
     */
    private void updateRowHeight(final JXTable table) {
        for (int row = 0; row < table.getRowCount(); row++) {
            int rowHeight = 0;
            for (int column = 0; column < table.getColumnCount(); column++) {
                Component comp = table.prepareRenderer(table
                        .getCellRenderer(row, column), row, column);
                rowHeight = Math.max(rowHeight,
                        comp.getPreferredSize().height);
            }
            table.setRowHeight(row, rowHeight); 
        }
    }

    /**
     * List needs special handling, see
     * http://stackoverflow.com/questions/7306295/swing-jlist-with-multiline-text-and-dynamic-height
     * 
     */
    public void interactiveTextAreaRendererList() {
        final DefaultListModel model = new DefaultListModel();
        model.addElement("Start: This is a short text");
        model.addElement("Start: This is a long text. This is a long text. This is a long text. This is a long text. This is a long text. This is a long text. This is a long text. This is a long text. This is a long text. This is a long text. This is a long text. This is a long text. This is a long text. ");
        model.addElement("Start: This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. ");

        final JXList list = new JXList(model) {

            /** 
             * @inherited <p>
             */
            @Override
            public boolean getScrollableTracksViewportWidth() {
                return true;
            }

            
        };
        list.setCellRenderer(new DefaultListRenderer(new TextAreaProvider()));
        
        ComponentListener l = new java.awt.event.ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                list.invalidateCellSizeCache();
                // for core: force cache invalidation by temporarily setting fixed height
//                list.setFixedCellHeight(10);
//                list.setFixedCellHeight(-1);
            }
            
        };

        list.addComponentListener(l);
        showWithScrollingInFrame(list, "TextAreaPovider in JXList");
        
    }
    /**
     * use a JTextArea as rendering component.
     */
    public static class TextAreaProvider extends ComponentProvider<JTextArea> {

        @Override
        protected void configureState(CellContext context) {
            int columnWidth = getPreferredAreaWidth(context);
            if (columnWidth > 0) {
                rendererComponent.setSize(columnWidth, Short.MAX_VALUE);
            }
        }

        /**
         * @param context
         * @return
         */
        private int getPreferredAreaWidth(CellContext context) {
            if (context.getComponent() instanceof JXTable) {
                JXTable table = (JXTable) context.getComponent();
                return table.getColumn(context.getColumn()).getWidth();
            }
            if (context.getComponent() instanceof JTree) {
                // TODO: implement width and depth based initial width
            }
            if (context.getComponent() != null) {
                // pending: accont for insets
                // doesn't really work for a JList in a scrollPane
                // need to track..
                // for a explanation/hack around see my answer in
                // http://stackoverflow.com/questions/7306295/swing-jlist-with-multiline-text-and-dynamic-height
                return context.getComponent().getWidth();
            }
            // comp is null - not much we can do
            return -1;
        }

        @Override
        protected JTextArea createRendererComponent() {
            JTextArea area = new JTextArea();
            area.setLineWrap(true);
            area.setWrapStyleWord(true);
            area.setOpaque(true);
            return area;
        }

        @Override
        protected void format(CellContext context) {
            rendererComponent.setText(getValueAsString(context));
        }
        
    }
 
    /**
     * Quick example of using a JXLabel as rendering component.
     * Looks funny .. wrapping jumps?
     */
    public void interactiveXLabelRenderer() {
        DefaultTableModel model = new DefaultTableModel(0, 1);
        model.addRow(new String[] {"some really, maybe really really long text -  "
                + "wrappit .... where needed "});
        model.addRow(new String[] {"another really, maybe really really long text -  "
                + "with nothing but junk. wrappit .... where needed"});
        JXTable table = new JXTable(model);
        table.setVisibleRowCount(4);
        table.setVisibleColumnCount(2);
        table.setColumnControlVisible(true);
        table.getColumnExt(0).setCellRenderer(new DefaultTableRenderer(new XLabelProvider()));
        table.addHighlighter(
                HighlighterFactory.createAlternateStriping());
        table.setRowHeight(50);
        showWithScrollingInFrame(table, "textArea as rendering comp");
    }

    /**
     * Use a JXLabel as rendering component.
     */
    public static class XLabelProvider extends ComponentProvider<JXLabel> {

        @Override
        protected void configureState(CellContext context) {
        }

        @Override
        protected JXLabel createRendererComponent() {
            JXLabel label = new JXLabel();
//            label.setOpaque(true);
            label.setLineWrap(true);
            return label;
        }

        @Override
        protected void format(CellContext context) {
            rendererComponent.setText(getValueAsString(context));
        }
        
    }
    /**
     * Check disabled appearance.
     *
     */
    public void interactiveListDisabledIconRenderer() {
        final TableModel model = createTableModelWithDefaultTypes();
        // 
        ListModel listModel = new AbstractListModel() {

            @Override
            public Object getElementAt(int index) {
                if (index == 0) {
                    return "dummy";
                }
                return model.getValueAt(index - 1, 4);
            }

            @Override
            public int getSize() {
                return model.getRowCount() + 1;
            }
            
        };
        final JList standard = new JList(listModel);
        final JList enhanced = new JList(listModel);
        enhanced.setCellRenderer(new DefaultListRenderer());

        AbstractAction action = new AbstractAction("toggle disabled") {

            @Override
            public void actionPerformed(ActionEvent e) {
                standard.setEnabled(!standard.isEnabled());
                enhanced.setEnabled(!enhanced.isEnabled());
            }
            
        };
        JXFrame frame = wrapWithScrollingInFrame(standard, enhanced, "Disabled - compare renderers: default <--> enhanced");
        addAction(frame, action);
        frame.setVisible(true);
    }
    
    /**
     * Check disabled appearance for all renderers.
     *
     */
    public void interactiveTableDefaultRenderers() {
        TableModel model = createTableModelWithDefaultTypes();
        final JTable standard = new JTable(model);
        // used as source for swingx renderers
        final JXTable xTable = new JXTable();
        final JTable enhanced = new JTable(model) {

            @Override
            protected void createDefaultRenderers() {
                defaultRenderersByColumnClass = new UIDefaults();
                setDefaultRenderer(Object.class, xTable.getDefaultRenderer(Object.class));
                setDefaultRenderer(Number.class, xTable.getDefaultRenderer(Number.class));
                setDefaultRenderer(Date.class, xTable.getDefaultRenderer(Date.class));
                setDefaultRenderer(Icon.class, xTable.getDefaultRenderer(Icon.class));
                setDefaultRenderer(ImageIcon.class, xTable.getDefaultRenderer(ImageIcon.class));
                setDefaultRenderer(Boolean.class, xTable.getDefaultRenderer(Boolean.class));
            }
            
        };
        AbstractAction action = new AbstractAction("toggle disabled") {

            @Override
            public void actionPerformed(ActionEvent e) {
                standard.setEnabled(!standard.isEnabled());
                enhanced.setEnabled(!enhanced.isEnabled());
            }
            
        };
        JXFrame frame = wrapWithScrollingInFrame(standard, enhanced, "Disabled Compare renderers: default <--> enhanced");
        addAction(frame, action);
        frame.setVisible(true);
    }
    
    /**
     * @return
     */
    private TableModel createTableModelWithDefaultTypes() {
        String[] names = {"Object", "Number", "Double", "Date", "ImageIcon", "Boolean", "URI"};
        final Class<?>[] types = {Object.class, Number.class, Double.class, Date.class, ImageIcon.class, Boolean.class, URI.class};
        DefaultTableModel model = new DefaultTableModel(names, 0) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            /** 
             * @inherited <p>
             */
            @Override
            public boolean isCellEditable(int row, int column) {
                return getColumnClass(column) != URI.class;
            }
            
            
            
        };
        Date today = new Date();
        Icon icon = XTestUtils.loadDefaultIcon();
        for (int i = 0; i < 10; i++) {
            Object[] values;
            try {
                values = new Object[] {"row " + i, i, Math.random() * 100, 
                        new Date(today.getTime() + i * 1000000), icon, i % 2 == 0, new URI("http://swingx.java.net")};
                model.addRow(values);
            } catch (URISyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return model;
    }

    /**
     * Compare core table using core default renderer vs. swingx default renderer.<p>
     * Unselected background of lead is different for editable/not-editable cells.
     */
    @SuppressWarnings("deprecation")
    public void interactiveTableCompareFocusedCellBackground() {
        TableModel model = new AncientSwingTeam() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }
        };
        
        JTable xtable = new JTable(model);
        xtable.setBackground(HighlighterFactory.NOTEPAD); // ledger
        JTable table = new JTable(model);
        table.setBackground(new Color(0xF5, 0xFF, 0xF5)); // ledger
        TableCellRenderer renderer = new DefaultTableRenderer();
        table.setDefaultRenderer(Object.class, renderer);
        JXFrame frame = wrapWithScrollingInFrame(xtable, table, "JTable: Unselected focused background: core/ext renderer");
        addStatusComponent(frame, new JLabel("background for unselected lead: first column is not-editable"));    
        show(frame);
    }

    /**
     * Compare xtable using core default renderer vs. swingx default renderer.<p>
     * Obsolete - swingx renderers registered by default.
     * 
     * Unselected background of lead is different for editable/not-editable cells.
     * With core renderer: can't because LegacyHighlighter hack jumps in.
     * 
     */
    public void interactiveXTableCompareFocusedCellBackground() {
//        TableModel model = new AncientSwingTeam() {
//            public boolean isCellEditable(int row, int column) {
//                return column != 0;
//            }
//        };
//        
//        JXTable xtable = new JXTable(model);
//        xtable.setBackground(LegacyHighlighter.notePadBackground.getBackground()); // ledger
//        JXTable table = new JXTable(model) {
//
//            @Override
//            protected void createDefaultRenderers() {
//                defaultRenderersByColumnClass = new UIDefaults();
//                setDefaultRenderer(Object.class, new DefaultTableRenderer());
//                LabelProvider controller = new LabelProvider(FormatStringValue.NUMBER_TO_STRING);
//                controller.setHorizontalAlignment(JLabel.RIGHT);
//                setDefaultRenderer(Number.class, new DefaultTableRenderer(controller));
//                setDefaultRenderer(Date.class, new DefaultTableRenderer(
//                        FormatStringValue.DATE_TO_STRING));
//                TableCellRenderer renderer  = new DefaultTableRenderer(new LabelProvider(JLabel.CENTER));
//                setDefaultRenderer(Icon.class, renderer);
//                setDefaultRenderer(ImageIcon.class, renderer);
//                setDefaultRenderer(Boolean.class, new DefaultTableRenderer(new ButtonProvider()));
//            }
//
//        };
//        table.setBackground(xtable.getBackground()); // ledger
//        JXFrame frame = wrapWithScrollingInFrame(xtable, table, "JXTable: Unselected focused background: core/ext renderer");
//        getStatusBar(frame).add(new JLabel("different background for unselected lead: first column is not-editable"));    
//        frame.pack();
//        frame.setVisible(true);
    }

    /**
     * Issue #282-swingx: compare disabled appearance of
     * collection views.
     * Check if extended renderers behave correctly. Still open: header 
     * renderer disabled.
     */
    public void interactiveDisabledCollectionViews() {
        final JXTable table = new JXTable(new AncientSwingTeam());
//        table.setDefaultRenderer(Object.class, new DefaultTableRenderer());
        table.setEnabled(false);
        final JXList list = new JXList(new String[] {"one", "two", "and something longer"});
        list.setEnabled(false);
//        list.setCellRenderer(new DefaultListRenderer());
        final JXTree tree = new JXTree(new FileSystemModel());
        tree.setEnabled(false);
        JComponent box = Box.createHorizontalBox();
        box.add(new JScrollPane(table));
        box.add(new JScrollPane(list));
        box.add(new JScrollPane(tree));
        JXFrame frame = wrapInFrame(box, "disabled collection views");
        AbstractAction action = new AbstractAction("toggle disabled") {

            @Override
            public void actionPerformed(ActionEvent e) {
                table.setEnabled(!table.isEnabled());
                list.setEnabled(!list.isEnabled());
                tree.setEnabled(!tree.isEnabled());
            }
            
        };
        addAction(frame, action);
        frame.setVisible(true);
        
    }

    /**
     * 
     * Example for custom StringValue: bound to bean property.
     * 
     * A column of xtable and the xlist share the same component controller.<p>
     * 
     *  
     */
    public void interactiveTableAndListCustomRenderer() {
        final ListModel players = createPlayerModel();
        TableModel tableModel = new AbstractTableModel() {
            String[] columnNames = {"Name", "Score", "Player.toString"};
            @Override
            public int getColumnCount() {
                return 3;
            }

            @Override
            public int getRowCount() {
                return players.getSize();
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                return players.getElementAt(rowIndex);
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return Player.class;
            }

            @Override
            public String getColumnName(int column) {
                return columnNames[column];
            }
            
            
        };
        JXTable xtable = new JXTable(tableModel);
        PropertyStringValue converter = new PropertyStringValue("name");
        LabelProvider nameController = new LabelProvider(converter);
        xtable.getColumn(0).setCellRenderer(new DefaultTableRenderer(nameController));
        PropertyStringValue scoreConverter = new PropertyStringValue("score");
        xtable.getColumn(1).setCellRenderer(new DefaultTableRenderer(scoreConverter));
        xtable.packAll();
        JXList list = new JXList(players);
        // we share the component controller between table and list
        list.setCellRenderer(new DefaultListRenderer(nameController));
        showWithScrollingInFrame(xtable, list, "JXTable/JXList: Custom property renderer");

    }

  /**
  * Simple example to bind a toStringConverter to a single property of the value.
  */
    public static class PropertyStringValue implements StringValue {
        private String property;

        public PropertyStringValue(String property) {
            this.property = property;
        }

        /**
         * {@inheritDoc} <p>
         * Implemented to return the toString of the named property value.
         */
        @Override
        public String getString(Object value) {
            try {
                PropertyDescriptor desc = getPropertyDescriptor(value.getClass(), property);
                return StringValues.TO_STRING.getString(getValue(value, desc));
            } catch (Exception e) {
                // nothing much we can do here...
                
            }
            return "";
        }
        
    }
    
    /**
     * Use custom converter for Dimension/Point (from demo)
     *
     */
    public void interactiveTableCustomRenderer() {
        JXTable table = new JXTable();
        StringValue converter = new StringValue() {

            @Override
            public String getString(Object value) {
                if (value instanceof Point) {
                    Point p = (Point) value;
                    return createString(p.x, p.y);
                } else if (value instanceof Dimension) {
                    Dimension dim = (Dimension) value;
                    return createString(dim.width, dim.height);
                }
               return "";
            }

            private String createString(int width, int height) {
                return "(" + width + ", " + height + ")";
            }
            
        };
        TableCellRenderer renderer = new DefaultTableRenderer(converter);
        table.setDefaultRenderer(Point.class, renderer);
        table.setDefaultRenderer(Dimension.class, renderer);
        JXFrame frame = showWithScrollingInFrame(table, "custom renderer (from demo) for Point/Dimension");
        ComponentTreeTableModel model = new ComponentTreeTableModel(frame);
        JXTreeTable treeTable = new JXTreeTable(model);
        treeTable.expandAll();
        table.setModel(treeTable.getModel());
        
    }
    
//---------------- hyperlink rendering    
    
    public void interactiveTableURIRenderer() {
        JXTable table = new JXTable(createTableModelWithURI());
        JXFrame frame = wrapWithScrollingInFrame(table, "default renderer: URI");
        addStatusMessage(frame, "Note: hyperlinks only clickable if !editable");
        show(frame);
    }
    /**
     * extended link renderer in table.
     *
     */
    public void interactiveTestTableLinkRenderer() {
        EditorPaneLinkVisitor visitor = new EditorPaneLinkVisitor();
        JXTable table = new JXTable(createModelWithLinks());
        LinkModelAction<LinkModel> action = new LinkModelAction<LinkModel>(visitor);
        ComponentProvider<JXHyperlink> controller = new HyperlinkProvider(action, LinkModel.class);
        table.setDefaultRenderer(LinkModel.class, new DefaultTableRenderer(controller));
        JXFrame frame = wrapWithScrollingInFrame(table, visitor.getOutputComponent(), "show link renderer in table");
        addStatusMessage(frame, "Note: hyperlinks only clickable if !editable");
        frame.setVisible(true);

    }
    
    public void interactiveListURIRenderer() {
        JXList list = new JXList(createListModelWithURI(20));
        list.setRolloverEnabled(true);
        ComponentProvider<?> provider = new HyperlinkProvider(new HyperlinkAction());
        list.setCellRenderer(new DefaultListRenderer(provider));
        showWithScrollingInFrame(list, "list with uri renderer");
    }
    /**
     * extended link renderer in list.
     *
     */
    public void interactiveTestListLinkRenderer() {
        EditorPaneLinkVisitor visitor = new EditorPaneLinkVisitor();
        JXList list = new JXList(createListModelWithLinks(20));
        list.setRolloverEnabled(true);
        LinkModelAction<LinkModel> action = new LinkModelAction<LinkModel>(visitor);
        ComponentProvider<JXHyperlink> context = new HyperlinkProvider(action, LinkModel.class);
        list.setCellRenderer(new DefaultListRenderer(context));
        JFrame frame = wrapWithScrollingInFrame(list, visitor.getOutputComponent(), "show link renderer in list");
        frame.setVisible(true);

    }

    public void interactiveTreeURIRenderer() {
        JXTree tree = new JXTree(createTreeModelWithURI(20));
        tree.setRolloverEnabled(true);
        // PENDING JW: how to have "mixed" user object types? HyperlinkAction throws on f.i. String ..
        ComponentProvider<?> provider = new HyperlinkProvider(new HyperlinkAction());
        tree.setCellRenderer(new DefaultTreeRenderer(new WrappingProvider(provider)));
        showWithScrollingInFrame(tree, "tree with uri renderer");
    }
    /**
     * extended link renderer in tree.
     *
     */
    public void interactiveTestTreeLinkRenderer() {
        EditorPaneLinkVisitor visitor = new EditorPaneLinkVisitor();
        JXTree tree = new JXTree(createTreeModelWithLinks(20));
        tree.setRolloverEnabled(true);
        LinkModelAction<LinkModel> action = new LinkModelAction<LinkModel>(visitor);
        ComponentProvider<JXHyperlink> context = new HyperlinkProvider(action, LinkModel.class);
        tree.setCellRenderer(new DefaultTreeRenderer(new WrappingProvider(context)));
        JFrame frame = wrapWithScrollingInFrame(tree, visitor.getOutputComponent(), "show link renderer in list");
        frame.setVisible(true);
    }
    

    // ----------------------- experiments with "CheckList" fakes

    /**
     * Use a custom button controller to show both checkbox icon and text to
     * render Actions in a JXList.
     */
    public void interactiveCheckListColumnControl() {
        TableModel model = new AncientSwingTeam();
        JXTable table = new JXTable(model);
        table.setColumnControlVisible(true);
        JXList list = new JXList();
        // quick-fill and hook to table columns' visibility state
        configureList(list, table, true);
        StringValue sv = new StringValue() {

            @Override
            public String getString(Object value) {
                if (value instanceof AbstractActionExt) {
                    return ((AbstractActionExt) value).getName();
                }
                return StringValues.TO_STRING.getString(value);
            }

        };
        BooleanValue bv = new BooleanValue() {

            @Override
            public boolean getBoolean(Object value) {
                if (value instanceof AbstractActionExt) {
                    return ((AbstractActionExt) value).isSelected();
                }
                return false;
            }

        };
        CheckBoxProvider provider = new CheckBoxProvider(new MappedValue(sv,
                null, bv), JLabel.LEADING);
        list.setCellRenderer(new DefaultListRenderer(provider));
        JXFrame frame = showWithScrollingInFrame(table, list,
                "rollover checkbox list-renderer");
        addComponentOrientationToggle(frame);
        addStatusMessage(frame,
                "CheckList renderer (not completely active - no hotspot, doubleclick only)");
    }


    /**
     * Fills the list with a collection of actions (as returned from the 
     * table's column control). Binds space and double-click to toggle
     * the action's selected state.
     * 
     * note: this is just an example to show-off the button renderer in a list!
     * ... it's very dirty!!
     * 
     * @param list
     * @param table
     */
    private void configureList(final JXList list, final JXTable table, boolean useRollover) {
        final List<Action> actions = new ArrayList<Action>();
        // quick and dirty access to column visibility actions
        @SuppressWarnings("all")
        ColumnControlButton columnControl = new ColumnControlButton(table) {

            @Override
            protected void addVisibilityActionItems() {
                actions.addAll(Collections
                        .unmodifiableList(getColumnVisibilityActions()));
            }

        };
        list.setModel(createListeningListModel(actions));
        // action toggling selected state of selected list item
        final Action toggleSelected = new AbstractActionExt(
                "toggle column visibility") {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (list.isSelectionEmpty())
                    return;
                AbstractActionExt selectedItem = (AbstractActionExt) list
                        .getSelectedValue();
                selectedItem.setSelected(!selectedItem.isSelected());
            }

        };
        if (useRollover) {
            list.setRolloverEnabled(true);
        } else {
            // bind action to space
            list.getInputMap().put(KeyStroke.getKeyStroke("SPACE"),
                    "toggleSelectedActionState");
        }
        list.getActionMap().put("toggleSelectedActionState", toggleSelected);
        // bind action to double-click
        MouseAdapter adapter = new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    toggleSelected.actionPerformed(null);
                }
            }

        };
        list.addMouseListener(adapter);

    }
    /**
     * Creates and returns a ListModel containing the given actions. 
     * Registers a PropertyChangeListener with each action to get
     * notified and fire ListEvents.
     * 
     * @param actions the actions to add into the model.
     * @return the filled model.
     */
    private ListModel createListeningListModel(final List<Action> actions) {
        final DefaultListModel model = new DefaultListModel() {

            DefaultListModel reallyThis = this;
            @Override
            public void addElement(Object obj) {
                super.addElement(obj);
                ((Action) obj).addPropertyChangeListener(l);
                
            }
            
            PropertyChangeListener l = new PropertyChangeListener() {
                
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    int index = indexOf(evt.getSource());
                    if (index >= 0) {
                        fireContentsChanged(reallyThis, index, index);
                    }
                }
                
            };
        };
        for (Action action : actions) {
            model.addElement(action);
        }
        return model;
    }

//--------------- experiments with color renderer (content directly related to visuals)

    /**
     * Compare xtable using custom color renderer - standard vs. ext.<p>
     * 
     */
    public void interactiveTableCustomColorRenderer() {
        TableModel model = new AncientSwingTeam();
        JXTable xtable = new JXTable(model);
        xtable.setDefaultRenderer(Color.class, new ColorRenderer(true));
        JXTable table = new JXTable(model);
        TableCellRenderer renderer = createColorRendererExt();
        table.setDefaultRenderer(Color.class, renderer);
        showWithScrollingInFrame(xtable, table, "JXTable: Custom color renderer - standard/ext");
    }

    /**
     * Compare xtable using custom color renderer - standard vs. ext.<p>
     * Adds highlighter ... running amok.
     */
    public void interactiveTableCustomColorRendererWithHighlighter() {
        TableModel model = new AncientSwingTeam();
        JXTable xtable = new JXTable(model);
        xtable.addHighlighter(
                HighlighterFactory.createSimpleStriping(HighlighterFactory.GENERIC_GRAY));
        xtable.setDefaultRenderer(Color.class, new ColorRenderer(true));
        JXTable table = new JXTable(model);
        table.addHighlighter(
                HighlighterFactory.createSimpleStriping(HighlighterFactory.GENERIC_GRAY));
        TableCellRenderer renderer = createColorRendererExt();
        table.setDefaultRenderer(Color.class, renderer);
        JXFrame frame = wrapWithScrollingInFrame(xtable, table, "JXTable/highlighter: Custom color renderer - standard/ext");
        addStatusMessage(frame, "Highlighter hide custom color renderer background for unselected");
        frame.setVisible(true);
    }

    /**
     * Compare xtable using custom color renderer - standard vs. ext.<p>
     * Adds highlighter which respects renderer's dont touch.
     */
    public void interactiveTableCustomColorRendererWithHighlighterDontTouch() {
        TableModel model = new AncientSwingTeam();
        JXTable xtable = new JXTable(model);
        Highlighter highlighter = createPropertyRespectingHighlighter(
                HighlighterFactory.createSimpleStriping(HighlighterFactory.GENERIC_GRAY));

        xtable.addHighlighter(highlighter);
        xtable.setDefaultRenderer(Color.class, new ColorRenderer(true));
        JXTable table = new JXTable(model);
        table.addHighlighter(highlighter);
        TableCellRenderer renderer = createColorRendererExt();
        table.setDefaultRenderer(Color.class, renderer);
        JXFrame frame = wrapWithScrollingInFrame(xtable, table, "JXTable/highlighter dont-touch: Custom color renderer - standard/ext");
        addStatusMessage(frame, "Highlighter doesn't touch custom color renderer visual properties");
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Creates and returns a LegacyHighlighter which does nothing if the 
     * rendererComponent has the dont-touch property set. Otherwise
     * delegates highlighting to the delegate.
     * 
     * @param delegate
     * @return
     */
    private Highlighter createPropertyRespectingHighlighter(final Highlighter delegate) {
        HighlightPredicate predicate = new HighlightPredicate() {

            @Override
            public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
                return ((JComponent) renderer).getClientProperty("renderer-dont-touch") == null;
            }
            
        };
        
        Highlighter highlighter = new AbstractHighlighter(predicate) {

            @Override
            public Component doHighlight(Component renderer, ComponentAdapter adapter) {
                return delegate.highlight(renderer, adapter);
            }
            
        };
        return highlighter;
    }



    /**
     * xtable/xlist using the same custom component controller.<p>
     * 
     */
    public void interactiveTableAndListCustomColorRenderingController() {
        TableModel tableModel = new AncientSwingTeam();
        ComponentProvider<JLabel> controller = createColorRenderingLabelController();
        JXTable xtable = new JXTable(tableModel);
        xtable.setDefaultRenderer(Color.class, new DefaultTableRenderer(controller));
        ListModel model = createListColorModel();
        JXList list = new JXList(model);
        ListCellRenderer renderer = new DefaultListRenderer(controller);
        list.setCellRenderer(renderer);
        JXFrame frame = wrapWithScrollingInFrame(xtable, list, "JXTable/JXList: Custom color renderer - sharing the component controller");
        addMessage(frame, "share provider in normally in different comps is okay?");
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * xtable/xlist using the same custom component controller.<p>
     * 
     */
    public void interactiveTableAndTreeCustomColorRenderingController() {
        TableModel tableModel = new AncientSwingTeam();
        ComponentProvider<JLabel> controller = createColorRenderingLabelController();
        JXTable xtable = new JXTable(tableModel);
        xtable.setDefaultRenderer(Color.class, new DefaultTableRenderer(controller));
        TreeModel model = createTreeColorModel();
        JTree tree = new JTree(model);
        ComponentProvider<?> wrapper = new WrappingProvider(controller); //;createColorRenderingLabelController());
        TreeCellRenderer renderer = new DefaultTreeRenderer(wrapper);
        tree.setCellRenderer(renderer);
        JXFrame frame = wrapWithScrollingInFrame(xtable, tree, "JXTable/JXTree: Custom color renderer - sharing the component controller");
        addMessage(frame, "share provider in table and in wrappingProvider does not work?");
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * creates and returns a color ext renderer.
     * @return
     */
    protected TableCellRenderer createColorRendererExt() {
        ComponentProvider<JLabel> context = createColorRenderingLabelController();
        TableCellRenderer renderer = new DefaultTableRenderer(context);
        return renderer;
    }

    /**
     * creates and returns a color ext renderer.
     * @return
     */
    protected ListCellRenderer createListColorRendererExt() {
        ComponentProvider<JLabel> context = createColorRenderingLabelController();
        ListCellRenderer renderer = new DefaultListRenderer(context);
        return renderer;
    }

    /**
     * Creates and returns a component controller specialized on Color values. <p>
     * Note: this implementation set's the tooltip
     * @return
     */
    private ComponentProvider<JLabel> createColorRenderingLabelController() {
        ComponentProvider<JLabel> context = new LabelProvider() {
            Border selectedBorder;
            @Override
            protected void format(CellContext context) {
                super.format(context);
                Object value = context.getValue();
                if (value instanceof Color) {
                    rendererComponent.setBackground((Color) value);
                    rendererComponent.putClientProperty("renderer-dont-touch", "color");
                } else {
                    rendererComponent.putClientProperty("renderer-dont-touch", null);
                }
            }

            /**
             * 
             * @param context
             */
            @Override
            protected void configureState(CellContext context) {
                Object value = context.getValue();
                if (value instanceof Color) {
                    Color newColor = (Color) value;
                    rendererComponent.setToolTipText("RGB value: " + newColor.getRed() + ", "
                            + newColor.getGreen() + ", " + newColor.getBlue());

                } else {
                    rendererComponent.setToolTipText(null);
                }
                if (context.isSelected()) {
                    selectedBorder = BorderFactory.createMatteBorder(2, 5,
                            2, 5, context.getSelectionBackground());
                } else {
                    selectedBorder = BorderFactory.createMatteBorder(2, 5,
                            2, 5, context.getBackground());
                }
                rendererComponent.setBorder(selectedBorder);
            }
            
        };
        return context;
    }

    
//--------------------- utility    
    
    
    private ListModel createListModelWithLinks(int count) {
        DefaultListModel model = new DefaultListModel();
        for (int i = 0; i < count; i++) {
            LinkModel link = createLinkModel(i);
            model.addElement(link);
        }
 
        return model;
    }
    
    private TreeModel createTreeModelWithLinks(int count) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Links");
        for (int i = 0; i < count; i++) {
            LinkModel link = createLinkModel(i);
            root.add(new DefaultMutableTreeNode(link));
        }
        return new DefaultTreeModel(root);
    }
    
    private TableModel createModelWithLinks() {
        String[] columnNames = { "text only", "Link editable",
                "Link not-editable", "Bool editable", "Bool not-editable" };

        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return getValueAt(0, columnIndex).getClass();
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return !getColumnName(column).contains("not");
            }

        };
        for (int i = 0; i < 4; i++) {
            LinkModel link = createLinkModel(i);
            model.addRow(new Object[] { "text only " + i, link, link,
                    Boolean.TRUE, Boolean.TRUE });
        }
        return model;
    }

    private LinkModel createLinkModel(int i) {
        LinkModel link = null;
        try {
            link = new LinkModel("a link text " + i, null,
                    new URL("http://some.dummy.url" + i));
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (i == 1) {
            URL url = JXEditorPaneTest.class
                    .getResource("resources/test.html");

            link = new LinkModel("a link text " + i, null, url);
        }
        return link;
    }
    
    private ListModel createListModelWithURI(int count) {
        DefaultListModel model = new DefaultListModel();
        for (int i = 0; i < count; i++) {
            model.addElement(createURI(i));
        }
        return model;
    }
    
    private TreeModel createTreeModelWithURI(int count) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(null);
        for (int i = 0; i < count; i++) {
            root.add(new DefaultMutableTreeNode(createURI(i)));
        }
        
        return new DefaultTreeModel(root );
    }
    
    private TableModel createTableModelWithURI() {
        String[] columnNames = { "text only", "URI editable",
                "URI not-editable", "Bool editable", "Bool not-editable" };

        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return getValueAt(0, columnIndex).getClass();
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return !getColumnName(column).contains("not");
            }

        };
        for (int i = 0; i < 4; i++) {
            URI link = createURI(i);
            model.addRow(new Object[] { "text only " + i, link, link,
                    Boolean.TRUE, Boolean.TRUE });
        }
        return model;
    }

    private URI createURI(int i) {
        URI link = null;
        try {
            link = new URI("http://some.dummy.url" + i);
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (i == 1) {
            URL url = JXEditorPaneTest.class
                    .getResource("resources/test.html");

            try {
                link = url.toURI();
            } catch (URISyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return link;
    }
    /**
     * 
     * @return a ListModel wrapped around the AncientSwingTeam's Color column.
     */
    private ListModel createListColorModel() {
        AncientSwingTeam tableModel = new AncientSwingTeam();
        int colorColumn = 2;
        DefaultListModel model = new DefaultListModel();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            model.addElement(tableModel.getValueAt(i, colorColumn));
        }
        return model;
    }

    /**
     * @return a TreeModel with tree nodes of type Color.
     */
    private TreeModel createTreeColorModel() {
        final AncientSwingTeam tableModel = new AncientSwingTeam();
        final int colorColumn = 2;
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Colors");
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            root.add(new DefaultMutableTreeNode(tableModel.getValueAt(row, colorColumn)));
        }
        return new DefaultTreeModel(root);
        
    }

    /**
     * copied from sun's tutorial.
     */
    public static class ColorRenderer extends JLabel implements
            TableCellRenderer {
        Border unselectedBorder = null;

        Border selectedBorder = null;

        boolean isBordered = true;

        public ColorRenderer(boolean isBordered) {
            this.isBordered = isBordered;
            setOpaque(true); // MUST do this for background to show up.
            putClientProperty("renderer-dont-touch", "color");
        }

        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object color, boolean isSelected, boolean hasFocus, int row,
                int column) {
            Color newColor = (Color) color;
            setBackground(newColor);
            if (isBordered) {
                if (isSelected) {
                    if (selectedBorder == null) {
                        selectedBorder = BorderFactory.createMatteBorder(2, 5,
                                2, 5, table.getSelectionBackground());
                    }
                    setBorder(selectedBorder);
                } else {
                    if (unselectedBorder == null) {
                        unselectedBorder = BorderFactory.createMatteBorder(2,
                                5, 2, 5, table.getBackground());
                    }
                    setBorder(unselectedBorder);
                }
            }

            setToolTipText("RGB value: " + newColor.getRed() + ", "
                    + newColor.getGreen() + ", " + newColor.getBlue());
            return this;
        }
    }   
    
    public static class Player {
        String name;
        int score;
        public Player(String name, int score) {
            this.name = name;
            this.score = score;
        }
        @Override
        public String toString() {
            return name + " has score: " + score;
        }
        public String getName() {
            return name;
        }
        public int getScore() {
            return score;
        }
        
        
    }

    /**
     * create and returns a ListModel containing Players.
     * @return
     */
    private ListModel createPlayerModel() {
        DefaultListModel model = new DefaultListModel();
        model.addElement(new Player("Henry", 10));
        model.addElement(new Player("Berta", 112));
        model.addElement(new Player("Dave", 20));
        return model;
    }

    
    /**
     * c&p'd from JGoodies BeanUtils.
     * 
     * Looks up and returns a <code>PropertyDescriptor</code> for the
     * given Java Bean class and property name using the standard 
     * Java Bean introspection behavior.
     * 
     * @param beanClass     the type of the bean that holds the property
     * @param propertyName  the name of the Bean property
     * @return the <code>PropertyDescriptor</code> associated with the given
     *     bean and property name as returned by the Bean introspection
     *     
     * @throws IntrospectionException if an exception occurs during
     *     introspection.
     * @throws NullPointerException if the beanClass or propertyName is <code>null</code>
     * 
     * @since 1.1.1
     */
    public static PropertyDescriptor getPropertyDescriptor(
        Class<?> beanClass,
        String propertyName)
        throws IntrospectionException {

        BeanInfo info = Introspector.getBeanInfo(beanClass);
        PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
        for (int i = 0; i < descriptors.length; i++) {
            if (propertyName.equals(descriptors[i].getName()))
                return descriptors[i];
        }
        throw new IntrospectionException(
            "Property '" + propertyName + "' not found in bean " + beanClass);
    }

    /**
     * c&p'd from JGoodies BeanUtils.
     * 
     * Returns the value of the specified property of the given non-null bean.
     * This operation is unsupported if the bean property is read-only.<p>
     * 
     * If the read access fails, a PropertyAccessException is thrown
     * that provides the Throwable that caused the failure.
     * 
     * @param bean                the bean to read the value from
     * @param propertyDescriptor  describes the property to be read
     * @return the bean's property value
     * 
     * @throws NullPointerException           if the bean is <code>null</code>
     * @throws UnsupportedOperationException  if the bean property is write-only
     * @throws PropertyAccessException        if the new value could not be read
     */
    public static Object getValue(Object bean, PropertyDescriptor propertyDescriptor) {
        if (bean == null)
            throw new NullPointerException("The bean must not be null.");
        
        Method getter = propertyDescriptor.getReadMethod();
        if (getter == null) {
            throw new UnsupportedOperationException(
                "The property '" + propertyDescriptor.getName() + "' is write-only.");
        }
        
        try {
            return getter.invoke(bean);
        } catch (Exception e) {
            throw new RuntimeException("can't access property: " + propertyDescriptor.getName());
        }

//    } catch (IllegalAccessException e) {
//        throw PropertyAccessException.createWriteAccessException(
//            bean, newValue, propertyDescriptor, e);
//    } catch (IllegalArgumentException e) {
//        throw PropertyAccessException.createWriteAccessException(
//            bean, newValue, propertyDescriptor, e);
//    }

    }    
    
    /**
     * do-nothing method - suppress warning if there are no other
     * test fixtures to run.
     *
     */
    public void testDummy() {
        
    }

}
