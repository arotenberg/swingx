/*
 * Created on 09.06.2006
 *
 */
package org.jdesktop.swingx;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.jdesktop.swingx.decorator.BorderHighlighter;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.test.AncientSwingTeam;

/**
 * Interactive "test" methods for <code>JXTableHeader</code>.
 * 
 * @author Jeanette Winzenburg
 */
public class JXTableHeaderVisualCheck extends InteractiveTestCase {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(JXTableHeaderVisualCheck.class.getName());
    
    public static void main(String args[]) {
        JXTableHeaderVisualCheck test = new JXTableHeaderVisualCheck();
        try {
//          test.runInteractiveTests();
//            test.runInteractiveTests("interactive.*DoubleSort.*");
//            test.runInteractive("ColumnToolTip");
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
        JXTable table = new JXTable(new AncientSwingTeam());
        table.setColumnSelectionAllowed(true);
        JXTableHeader header = (JXTableHeader) table.getTableHeader();
        JPopupMenu popup = new JPopupMenu();
        Action action = new AbstractAction("cell found in actionPerformed") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                JXTableHeader table = SwingXUtilities.getAncestor(JXTableHeader.class, (Component) e.getSource());
                Point trigger = table.getPopupTriggerLocation();
                Point cell = null;
                if (trigger != null) {
                    int column = table.columnAtPoint(trigger);
                    table.getColumnModel().getSelectionModel().setSelectionInterval(column, column);
                    cell = new Point(column, -1);
                } else {
                    table.getColumnModel().getSelectionModel().clearSelection();
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
//                JXTable table = SwingXUtilities.getAncestor(JXTable.class, 
//                        (Component) e.getSource());
                JXTableHeader table = (JXTableHeader) ((JPopupMenu) e.getSource()).getInvoker();
                Point trigger = table.getPopupTriggerLocation();
                Point cell = null;
                if (trigger != null) {
                    int column = table.columnAtPoint(trigger);
                    // here we set the cell focus, just to do a bit differently
                    // from the other action
//                    table.setColumnSelectionInterval(column, column);
//                    table.clearSelection();
                    cell = new Point(column, -1);
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
        header.setComponentPopupMenu(popup);
        showWithScrollingInFrame(table, "PopupTriggerLocation");
    }
    

    
    /**
     * Issue 1560-swingx: column tooltip not working in stand-alone header
     * 
     */
    public void interactiveColumnToolTip() {
        JXTable table = new JXTable(new AncientSwingTeam());
        for (TableColumn column : table.getColumns()) {
            ((TableColumnExt) column).setToolTipText("tip: "
                    + column.getHeaderValue());
            
        }
        JXTableHeader header = new JXTableHeader(table.getColumnModel());
        JXFrame frame = wrapWithScrollingInFrame(table, "columnToolTip");
        frame.add(header, BorderLayout.NORTH);
        show(frame);
    }
    
    
    /**
     * Issue 1558-swingx: support custom column tooltip mechanism
     * 
     * The use-case is a dynamic, tableModel-backed tooltip:
     * needs subclassing of header which should not need duplication
     * of column look-up 
     */
    public void interactiveColumnToolTipCustom() {
        JXTable table = new JXTable(new ModelWithToolTip());
        JXTableHeader header = new JXTableHeader(table.getColumnModel()) {

            @Override
            protected String getColumnToolTipText(TableColumnExt column) {
                TableModel model = getTable().getModel();
                if (model instanceof ModelWithToolTip) {
                    return ((ModelWithToolTip) model).getColumnToolTip(column.getModelIndex());
                }
                return model.getColumnName(column.getModelIndex());
            }
            
        };
        DefaultTableCellRenderer r = new DefaultTableCellRenderer() {

            Border padding = BorderFactory.createEmptyBorder(0, 10, 0, 10);
            @Override
            public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean isSelected, boolean hasFocus,
                    int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                        row, column);
                setBorder(BorderFactory.createCompoundBorder(getBorder(), padding));
                return this;
            }
             
        };
        table.setDefaultRenderer(Object.class, r);
        DefaultTableRenderer renderer = (DefaultTableRenderer) table.getDefaultRenderer(Number.class);
        renderer.getComponentProvider().getRendererComponent(null).setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        BorderHighlighter hl = new BorderHighlighter(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        hl.setInner(true);
        table.addHighlighter(hl);
        table.setTableHeader(header);
        JXFrame frame = wrapWithScrollingInFrame(table, "custom columnToolTip");
        show(frame);
    }

    /**
     * Issue #1558-swing: model-based column tooltip
     */
    public static class ModelWithToolTip extends AncientSwingTeam {
        
        public String getColumnToolTip(int column) {
            return "model-tip: " + getColumnName(column);
        }
    }
    
    /**
     * Issue #271-swingx: optionally support double sort on double click.
     * 
     */
    public void interactiveDoubleSort() {
        JXTable table = new JXTable(30, 5);
        showWithScrollingInFrame(table, "support double sort on double click");
    }
    
    /**
     * Issue #1225-swingx: JXTableHeader throws on rowSorters which are not
     *   of type SortController when resizing columns with mouse.
     */
    public void interactiveHeaderCoreRowSorter() {
        JXTable table = new JXTable(30, 5);
        table.setAutoCreateRowSorter(false);
        table.setRowSorter(new TableRowSorter<TableModel>(table.getModel()));
        showWithScrollingInFrame(table, "core resize columns with mouse");
    }
    /**
     * Issue #683-swingx: Autoscroll if column dragged outside.
     * 
     */
    public void interactiveHeaderAutoScrollRToL() {
        JXTable table = new JXTable(20, 30);
        table.setColumnControlVisible(true);
        table.setVisibleColumnCount(6);
        assertTrue(table.getAutoscrolls());
        table.getTableHeader().setAutoscrolls(true);
        assertTrue(table.getTableHeader().getAutoscrolls());
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JXFrame frame = showWithScrollingInFrame(table, "autoScroll column drag - RToL");
        frame.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
    }

    /**
     * Issue #683-swingx: Autoscroll if column dragged outside.
     * 
     */
    public void interactiveHeaderAutoScroll() {
        JXTable table = new JXTable(20, 30);
        table.setColumnControlVisible(true);
        table.setVisibleColumnCount(6);
        assertTrue(table.getAutoscrolls());
        table.getTableHeader().setAutoscrolls(true);
        assertTrue(table.getTableHeader().getAutoscrolls());
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JXFrame frame = showWithScrollingInFrame(table, "autoScroll column drag - LToR");
        frame.applyComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
    }
    
    /**
     * Issue #683-swingx: Autoscroll if column dragged outside.
     * Plain ol' JTable - core bug 6503981
     */
    public void interactiveHeaderAutoScrollCoreIssue() {
        JTable table = new JXTable(20, 30);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JXFrame frame = showWithScrollingInFrame(table, "core bug 1.6 before u4 - scroll to last column and drag");
        frame.applyComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        frame.setSize(400, 400);
    }
    
    /**
     * Issue #485-swingx: table header disappears if all header values are
     * empty. Compare core <--> JXTable
     * fixed for SwingX.
     *
     */
    public void interactiveHeaderSizeRequirements() {
        
        final String[] alternate = { 
                null, 
                null, 
                };
        final JTable table = new JTable(10, 2);
        table.getColumnModel().getColumn(0).setHeaderValue(alternate[0]);
        table.getColumnModel().getColumn(1).setHeaderValue(alternate[1]);
        
        JXTable xTable = new JXTable(10, 2);
        xTable.getColumn(0).setHeaderValue(alternate[0]);
        xTable.getColumn(1).setHeaderValue(alternate[1]);
        
        JXFrame frame = wrapWithScrollingInFrame(table, xTable, "header height empty (core - xtable)");
        frame.setVisible(true);
        
    }

    /**
     * Issue #390-swingx: JXTableHeader: throws AIOOB on removing dragged column.
     * 
     */
    public void interactiveDraggedColumnRemoved() {
        final JXTable table = new JXTable(10, 5);
        Action deleteColumn = new AbstractAction("deleteCurrentColumn") {

            @Override
            public void actionPerformed(ActionEvent e) {
                TableColumn column = table.getTableHeader().getDraggedColumn();
                if (column == null) return;
                table.getColumnModel().removeColumn(column);
            }
            
        };
        KeyStroke keyStroke = KeyStroke.getKeyStroke("F1");
        table.getInputMap(JTable.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, "f1");
        table.getActionMap().put("f1", deleteColumn);
        JXFrame frame = wrapWithScrollingInFrame(table, "Remove dragged column with F1");
        frame.setVisible(true);
    }
    /**
     * Visual demo that header is always visible.
     */
    public void interactiveHeaderVisible() {
        final JXTable table = new JXTable();
        table.setColumnControlVisible(true);
        JXFrame frame = wrapWithScrollingInFrame(table, "header always visible");
        Action action = new AbstractAction("toggle model") {

            @Override
            public void actionPerformed(ActionEvent e) {
                int columnCount = table.getColumnCount(true);
                table.setModel(columnCount > 0 ?
                        new DefaultTableModel() : new DefaultTableModel(10, 2));
            }
            
        };
        addAction(frame, action);
        frame.setVisible(true);
        
    }

    /**
     * do nothing test - keep the testrunner happy.
     */
    public void testDummy() {
    }

}
