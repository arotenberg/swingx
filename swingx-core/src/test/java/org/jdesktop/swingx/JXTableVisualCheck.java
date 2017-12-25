/*
 * $Id: JXTableVisualCheck.java 4301 2013-07-04 12:36:17Z kleopatra $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.RowSorter.SortKey;
import javax.swing.ScrollPaneLayout;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterEvent.Type;
import javax.swing.event.RowSorterListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.hyperlink.AbstractHyperlinkAction;
import org.jdesktop.swingx.hyperlink.LinkModel;
import org.jdesktop.swingx.hyperlink.LinkModelAction;
import org.jdesktop.swingx.renderer.CheckBoxProvider;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.HyperlinkProvider;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.search.SearchFactory;
import org.jdesktop.swingx.sort.DefaultSortController;
import org.jdesktop.swingx.sort.SortUtils;
import org.jdesktop.swingx.table.ColumnFactory;
import org.jdesktop.swingx.table.DatePickerCellEditor;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.treetable.FileSystemModel;
import org.jdesktop.test.AncientSwingTeam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Split from old JXTableUnitTest - contains "interactive"
 * methods only. <p>
 * 
 * PENDING: too many frames to fit all on screen - either split into different
 * tests or change positioning algo to start on top again if hidden. <p>
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class JXTableVisualCheck extends JXTableUnitTest {
    private static final Logger LOG = Logger.getLogger(JXTableVisualCheck.class
            .getName());
    public static void main(String args[]) {
      JXTableVisualCheck test = new JXTableVisualCheck();
      try {
//        test.runInteractiveTests();
//          test.runInteractiveTests("interactive.*FloatingPoint.*");
//          test.runInteractiveTests("interactive.*Disable.*");
//          test.runInteractiveTests("interactive.*ColumnControl.*");
//          test.runInteractiveTests("interactive.*RowHeight.*");
//          test.runInteractiveTests("interactive.*Remove.*");
//          test.runInteractiveTests("interactive.*ColumnProp.*");
//          test.runInteractiveTests("interactive.*Multiple.*");
//          test.runInteractiveTests("interactive.*RToL.*");
//          test.runInteractiveTests("interactive.*Scrollable.*");
//          test.runInteractiveTests("interactive.*isable.*");
//          test.runInteractive("EditorNull");
          test.runInteractive("PopupTrigger");
          
//          test.runInteractiveTests("interactive.*Policy.*");
//        test.runInteractiveTests("interactive.*Rollover.*");
//        test.runInteractiveTests("interactive.*Revalidate.*");
//        test.runInteractiveTests("interactive.*UpdateUI.*");
//        test.runInteractiveTests("interactiveColumnHighlighting");
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
        table.setCellSelectionEnabled(true);
        JPopupMenu popup = new JPopupMenu();
        Action action = new AbstractAction("cell found in actionPerformed") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                JXTable table = SwingXUtilities.getAncestor(JXTable.class, (Component) e.getSource());
                Point trigger = table.getPopupTriggerLocation();
                Point cell = null;
                if (trigger != null) {
                    int row = table.rowAtPoint(trigger);
                    int column = table.columnAtPoint(trigger);
                    table.setRowSelectionInterval(row, row);
                    table.setColumnSelectionInterval(column, column);
                    cell = new Point(column, row);
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
//                JXTable table = SwingXUtilities.getAncestor(JXTable.class, 
//                        (Component) e.getSource());
                JXTable table = (JXTable) ((JPopupMenu) e.getSource()).getInvoker();
                Point trigger = table.getPopupTriggerLocation();
                Point cell = null;
                if (trigger != null) {
                    int row = table.rowAtPoint(trigger);
                    int column = table.columnAtPoint(trigger);
                    // here we set the cell focus, just to do a bit differently
                    // from the other action
                    table.setRowSelectionInterval(row, row);
                    table.setColumnSelectionInterval(column, column);
                    table.clearSelection();
                    cell = new Point(column, row);
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
     * Issue #1535-swingx: GenericEditor fires editingStopped  even if 
     * value invalid (= empty). Leads to NPE in columnMoved (jdk7) or when
     * external code calls the stop/cancel sequence.
     * Core issue but c&p'd to JXTable.
     * 
     * 
     * 1. doesn't allow editing if the column type doesn't have a 
     *    single parameter constructor of type String
     *    (fails silently in this case) - unrelated to this issue
     *    
     * 2. throws NPE on moving column while editing, the editingValue
     *    is empty and the class cannot handle empty strings. The NPE
     *    is triggered in column moved, because the GenericEditor
     *    fires twice. http://stackoverflow.com/q/13524519/203657
     *    Visible only when run with jdk7 because the inappropriate 
     *    removeEditor is fixed :-)
     */
    public void interactiveEditorNull() {
        final JTable table = new JTable(create1535TableModel());
        Action action = new AbstractAction("terminateEditing") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (table.isEditing() && !table.getCellEditor().stopCellEditing() ) {
                    table.getCellEditor().cancelCellEditing();
                }
            }
        };
        JXFrame frame = wrapWithScrollingInFrame(table, "Core: NPE on stopping edit");
        addAction(frame, action);
        show(frame);
    }

    public void interactiveEditorNullX() {
        final JTable table = new JXTable(create1535TableModel());
        Action action = new AbstractAction("terminateEditing") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (table.isEditing() && !table.getCellEditor().stopCellEditing() ) {
                    table.getCellEditor().cancelCellEditing();
                }
            }
        };
        JXFrame frame = wrapWithScrollingInFrame(table, "xTable: NPE on stopping edit");
        addAction(frame, action);
        show(frame);
    }

    /**
     * Issue #1392- swingx: columnControl lost on toggle CO and LAF
     * 
     * Not easily reproducible - happens
     * if the scrollPane's layout is re-created on updateUI.
     */
    public void interactiveColumnControlLAF() {
        JXTable table = new JXTable(new AncientSwingTeam());
        table.setColumnControlVisible(true);
        JScrollPane scrollPane = new JScrollPane(table) {

            /** 
             * @inherited <p>
             */
            @Override
            public void updateUI() {
                super.updateUI();
                setLayout(new ScrollPaneLayout());
            }
            
        };
        JTable rowHeader = new JTable(10, 1);
        scrollPane.setRowHeaderView(rowHeader);
        JLabel label = new JLabel("rowHeader");
        scrollPane.setCorner(JScrollPane.UPPER_LEADING_CORNER, label);
        JXFrame frame = wrapInFrame(scrollPane, "xTable, coreScrollPane");
        addComponentOrientationToggle(frame);
        show(frame);
    }
    

    /**
     * Issue #1195-swingx: keep selection on remove
     * Basically, this is a core issue - fixed in DefaultSortController during
     * last cleanup round.
     * 
     */
    public void interactiveRemoveAndSelected() {
        final JXTable table = new JXTable(10, 5);
        JXFrame frame = wrapWithScrollingInFrame(table, "Issue #1195-swingx - keep selection on remove");
        Action action = new AbstractAction("remove row before selected") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                int selected = table.getSelectedRow();
                if (selected < 1) return;
                ((DefaultTableModel) table.getModel()).removeRow(selected - 1);
                
            }
        };
        addAction(frame, action);
        show(frame);
    }
    /**
     * Issue #35-swingx: visual indicators of secondary sort columns
     * 
     * Trick by David Hall: use unicode char 
     * http://forums.java.net/jive/thread.jspa?threadID=71090
     * 
     * As he already noted: it's not necessarily pretty looking ;-)
     */
    public void interactiveHeaderSecondarySortIndicator() {
        DefaultTableModel model = new DefaultTableModel(0, 3) {

            /** 
             * @inherited <p>
             */
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex < getColumnCount() - 1) {
                    return Integer.class;
                }
                return super.getColumnClass(columnIndex);
            }
            
        };
        int min = hexToInt("25A0");
        int max = hexToInt("25FF");
        for (int i = min; i <= max; i++) {
            Object[] row = new Object[3];
            row[0] = i;
            row[1] = i;
            row[2] = (char) i + ""; 
            model.addRow(row);
        }
        final JXTable table = new JXTable();
        ColumnFactory factory = new ColumnFactory() {

            /** 
             * @inherited <p>
             */
            @Override
            public TableColumnExt createTableColumn(int modelIndex) {
                // TODO Auto-generated method stub
                TableColumnExt tableColumn = new SortAwareTableColumnExt();
                tableColumn.setModelIndex(modelIndex);
                return tableColumn;
            }
            
        };
        table.setColumnControlVisible(true);
        table.setColumnFactory(factory);
        table.setModel(model);
        RowSorterListener l = new RowSorterListener() {
            
            @SuppressWarnings("unchecked")
            @Override
            public void sorterChanged(RowSorterEvent e) {
                if (e.getType() == Type.SORT_ORDER_CHANGED) {
                    List<TableColumn> list = table.getColumns(true);
                    List<? extends SortKey> sortKeys = new ArrayList<SortKey>(e.getSource().getSortKeys());
                    // remove primary
                    List<? extends SortKey> secondary = sortKeys.subList(1, sortKeys.size());
                    for (TableColumn tableColumn : list) {
                        if (tableColumn instanceof TableColumnExt) {
                            SortKey key = SortUtils.getFirstSortKeyForColumn(secondary, tableColumn.getModelIndex());
                            Object property = null;
                            if (key != null && SortUtils.isSorted(key.getSortOrder())) {
                                property = key.getSortOrder();
                            }
                            ((TableColumnExt) tableColumn).putClientProperty(SortAwareTableColumnExt.SORT_ORDER_KEY, property);
                        }
                    }
                }
                
            }
        };
        table.getRowSorter().addRowSorterListener(l);
        StringValue sv = new StringValue() {

            @Override
            public String getString(Object value) {
                return Integer.toHexString(((Integer) value).intValue());
            }
            
        };
        table.getColumn(1).setCellRenderer(new DefaultTableRenderer(sv, JLabel.RIGHT));
        JXFrame frame = showWithScrollingInFrame(table, "Geometric shapes");
        addComponentOrientationToggle(frame);
    }
    
    public static class SortAwareTableColumnExt extends TableColumnExt {
        
        public final static String DESCENDING_CHAR = " \u25bf";
        public final static String ASCENDING_CHAR = " \u25b5";
        
        public final static String SORT_ORDER_KEY = "columnExt.SortOrder";

        /**
         * @inherited <p>
         */
        @Override
        public Object getHeaderValue() {
            Object header = super.getHeaderValue();
            Object sortOrder = getClientProperty(SORT_ORDER_KEY);
            if (SortOrder.ASCENDING == sortOrder) {
                header = header + ASCENDING_CHAR;
            } else if (SortOrder.DESCENDING == sortOrder){
                header = header + DESCENDING_CHAR;
            }
            return header;
        }
        
        
    }
    
    private int hexToInt(String s) {
        return Integer.parseInt(s, 16);
    }

    /**
     * Issue #1254-swingx: JXTable not revalidated on update if filter.
     * 
     * Core JTable issue 
     * Problem is that the update might change the visible row count.
     */
    public void interactiveRevalidateOnUpdateWithFilter() {
        String data[][] = { { "satuAA", "Satu", "SATU", "1" },
                { "duaAAB", "Dua", "DUA", "2" },
                { "tigaBAA", "Tiga", "TIGA", "3" },
                { "empatBBA", "Empat", "EMPAT", "4" } };
        String cols[] = { "col1", "col2", "col3", "col4" };

        final JXTable table = new JXTable(data, cols);
        RowFilter<TableModel, Integer> tm = RowFilter.regexFilter(
                ".*AA.*", 0);
        table.setRowFilter(tm);
        JXFrame frame = wrapWithScrollingInFrame(table, "Update with RowFilter");
        Action action = new AbstractAction("filter first row") {
            
            boolean hasAA = true;
            @Override
            public void actionPerformed(ActionEvent e) {
                String newValue = hasAA ? "BB" : "AA";
                hasAA = !hasAA;
                table.getModel().setValueAt(newValue, 0, 0);
            }
        };
        addAction(frame, action);
        show(frame);
    }
    
    /**
     * Trying to make null biggest value.
     * 
     * Can't do - nulls don't reach the comparator.
     */
    public void interactiveSortWithNull() {
        JXTable table = new JXTable(createAscendingModel(1, 20));
        for (int i = 0; i < table.getRowCount(); i+=2) {
            table.setValueAt(null, i, 0);
        }
        Comparator<?> comparator = new Comparator<Object>() {

            @Override
            public int compare(Object o1, Object o2) {
                if (o1 == null) {
                    if (o2 == null) return 0;
                    return 1;
                }
                if (o2 == null) {
                    return -1;
                }
                return ((Integer) o1).compareTo((Integer) o2);
            }
            
        };
        table.getColumnExt(0).setComparator(comparator);
        showWithScrollingInFrame(table, "nulls");
    }
    /**
     * Quick check of sort order cycle (including pathologicals)
     */
    public void interactiveSortOrderCycle() {
        final JXTable table = new JXTable(new AncientSwingTeam());
        JXFrame frame = wrapWithScrollingInFrame(table, new JTable(table.getModel()), "sort cycles");
        Action three = new AbstractAction("three-cylce") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                table.setSortOrderCycle(SortOrder.ASCENDING, SortOrder.DESCENDING, SortOrder.UNSORTED);
            }
        };
        addAction(frame, three);
        Action two = new AbstractAction("two-cylce") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                table.setSortOrderCycle(SortOrder.ASCENDING, SortOrder.DESCENDING);
            }
        };
        addAction(frame, two);
        Action one = new AbstractAction("one-cylce") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                table.setSortOrderCycle(SortOrder.DESCENDING);
            }
        };
        addAction(frame, one);
        Action none = new AbstractAction("empty-cylce") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                table.setSortOrderCycle();
            }
        };
        addAction(frame, none);
        show(frame);
    }

    public void interactiveMultiColumnSort() {
        DefaultTableModel model = createMultiSortModel();
        JXTable table = new JXTable(model);
        table.setVisibleRowCount(model.getRowCount());
        JXFrame frame = wrapWithScrollingInFrame(table, "multi-column-sort");
        final DefaultSortController<?> rowSorter = (DefaultSortController<?>) table.getRowSorter();
        final List<SortKey> sortKeys = new ArrayList<SortKey>();
        for (int i = 0; i < rowSorter.getMaxSortKeys(); i++) {
            sortKeys.add(new SortKey(i, SortOrder.ASCENDING));
        }
        Action setSortKeys = new AbstractAction("sortKeys") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                rowSorter.setSortKeys(sortKeys);
            }
        };
        addAction(frame, setSortKeys);
        Action reset = new AbstractAction("resetSort") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                rowSorter.setSortKeys(null);
                
            }
        };
        rowSorter.setSortable(0, false);
        addAction(frame, reset);
        show(frame);
    }


    /**
     * @return
     */
    private DefaultTableModel createMultiSortModel() {
        String[] first = { "animal", "plant" };
        String[] second = {"insect", "mammal", "spider" };
        String[] third = {"red", "green", "yellow", "blue" };
        Integer[] age = { 1, 5, 12, 20, 100 };
        Object[][] rows = new Object[][] { first, second, third, age };
        DefaultTableModel model = new DefaultTableModel(20, 4) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == getColumnCount() - 1 ? 
                        Integer.class : super.getColumnClass(columnIndex);
            }
            
        };
        for (int i = 0; i < rows.length; i++) {
            setValues(model, rows[i], i);
        }
        return model;
    }
    /**
     * @param model
     * @param first
     * @param i
     */
    private void setValues(DefaultTableModel model, Object[] first, int column) {
        Random seed = new Random();
        for (int row = 0; row < model.getRowCount(); row++) {
            int random = seed.nextInt(first.length);
            model.setValueAt(first[random], row, column);
        }
    }

    /**
     * Issue #908-swingx: move updateUI responsibility into column.
     * 
     */
    public void interactiveUpdateUIEditors() {
        DefaultTableModel model = new DefaultTableModel(5, 5) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (getValueAt(0, columnIndex) == null)
                    return super.getColumnClass(columnIndex);
                return getValueAt(0, columnIndex).getClass();
            }
            
        };
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(new Date(), i, 0);
            model.setValueAt(true, i, 1);
        }
        JXTable table = new JXTable(model);
        TableCellEditor editor = new DatePickerCellEditor();
        table.getColumn(0).setCellEditor(editor);
        table.getColumn(4).setCellRenderer(new DefaultTableRenderer(new CheckBoxProvider()));
        showWithScrollingInFrame(table, "toggle ui - must update editors/renderers");
    }
    /**
     * Issue #550-swingx: xtable must not reset columns' pref/size on 
     * structureChanged if autocreate is false.
     * 
     *  
     */
    public void interactiveColumnWidthOnStructureChanged() {
        final JXTable table = new JXTable(new AncientSwingTeam());
        table.setAutoCreateColumnsFromModel(false);
        table.setAutoResizeMode(JXTable.AUTO_RESIZE_OFF);
        table.setColumnControlVisible(true);
        // min/max is respected
//        mini.setMaxWidth(5);
//        mini.setMinWidth(5);
        Action structureChanged = new AbstractAction("fire structure changed") {

            @Override
            public void actionPerformed(ActionEvent e) {
                table.tableChanged(null);
            }
            
        };
        JXFrame frame = showWithScrollingInFrame(table, "structure change must not re-size columns");
        addAction(frame, structureChanged);
        show(frame);
    }
    

    /**
     * Issue #675-swingx: esc doesn't reach rootpane.
     * 
     * Verify that the escape is intercepted only if editing.
     * BUT: (core behaviour) starts editing in table processKeyBinding. So every
     * second is not passed on.
     */
    public void interactiveDialogCancelOnEscape() {
        Action cancel = new AbstractActionExt("cancel") {

            @Override
            public void actionPerformed(ActionEvent e) {
                LOG.info("performed: cancel action");
                
            }
            
        };
        final JButton field = new JButton(cancel);
        JXTable xTable = new JXTable(10, 3);
        JTable table = new JTable(xTable.getModel());
        JXFrame frame = wrapWithScrollingInFrame(xTable, table, "escape passed to rootpane (if editing)");
        frame.setCancelButton(field);
        frame.add(field, BorderLayout.SOUTH);
        frame.setVisible(true);
    }
    

    
    /**
     * Issue #508/547-swingx: clean up of pref scrollable.
     * Visual check: column init on model change.
     *
     */
     public void interactivePrefScrollable() {
        final DefaultTableModel tableModel = new DefaultTableModel(30, 7);
        final AncientSwingTeam ancientSwingTeam = new AncientSwingTeam();
        final JXTable table = new JXTable(tableModel);
        table.setColumnControlVisible(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        final JXFrame frame = showWithScrollingInFrame(table, "initial sizing");
        addMessage(frame, "initial size: " + table.getPreferredScrollableViewportSize());
        Action action = new AbstractActionExt("toggle model") {

            @Override
            public void actionPerformed(ActionEvent e) {
                table.setModel(table.getModel() == tableModel ? ancientSwingTeam : tableModel);
                frame.pack();
            }
            
        };
        addAction(frame, action);
        frame.pack();
    }

     /**
     * Issue #508/547-swingx: clean up of pref scrollable.
      * Visual check: dynamic logical scroll sizes
      * Toggle visual row/column count.
      */
     public void interactivePrefScrollableDynamic() {
         final AncientSwingTeam ancientSwingTeam = new AncientSwingTeam();
         final JXTable table = new JXTable(ancientSwingTeam);
         table.setColumnControlVisible(true);
         table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
         final JXFrame frame = wrapWithScrollingInFrame(table, "Dynamic pref scrollable");
         Action action = new AbstractActionExt("vis row") {
             
             @Override
            public void actionPerformed(ActionEvent e) {
                 int visRowCount = table.getVisibleRowCount() + 5;
                 if (visRowCount > 30) {
                     visRowCount = 10;
                 }
                 table.setVisibleRowCount(visRowCount);
                 frame.pack();
             }
             
         };
         addAction(frame, action);
         Action columnAction = new AbstractActionExt("vis column") {
             
             @Override
            public void actionPerformed(ActionEvent e) {
                 int visColumnCount = table.getVisibleColumnCount();
                 if (visColumnCount > 8) {
                     visColumnCount = -1;
                 } else if (visColumnCount < 0 ) {
                     visColumnCount = 2;
                 } else {
                     visColumnCount += 2;
                 }
                 table.setVisibleColumnCount(visColumnCount);
                 frame.pack();
             }
             
         };
         addAction(frame, columnAction);
         frame.setVisible(true);
         frame.pack();
     }


 
    /**
     * Issue #417-swingx: disable default find.
     *
     * Possible alternative to introducing disable api as suggested in the
     * issue report: disable the action? Move the action up the hierarchy to
     * the parent actionmap? Maybe JX specific parent?
     *  
     */
    public void interactiveDisableFind() {
        final JXTable table = new JXTable(sortableTableModel);
        Action findAction = new AbstractActionExt() {

            @Override
            public void actionPerformed(ActionEvent e) {
                SearchFactory.getInstance().showFindDialog(table, table.getSearchable());
                
            }
            
            @Override
            public boolean isEnabled() {
                return false;
            }
            
        };
        table.getActionMap().put("find", findAction);
        showWithScrollingInFrame(table, "disable finding");
    }
    
    /**
     * visually check if we can bind the CCB's action to a keystroke.
     * 
     * Working, but there's a visual glitch if opened by keystroke: 
     * the popup is not trailing aligned to the CCB. And the 
     * CCB must be visible, otherwise there's an IllegalStateException
     * because the popup tries to position itself relative to the CCB.
     *
     */
    public void interactiveKeybindingColumnControl() {
        JXTable table = new JXTable(sortableTableModel);
        // JW: currently the CCB must be visible
        table.setColumnControlVisible(true);
        Action openCCPopup = ((AbstractButton) table.getColumnControl()).getAction();
        String actionKey = "popupColumnControl";
        table.getActionMap().put(actionKey, openCCPopup);
        KeyStroke keyStroke = KeyStroke.getKeyStroke("F2");
        table.getInputMap(JXTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(keyStroke, actionKey);
        showWithScrollingInFrame(table, "Press F2 to open column control");
    }
    /**
     * calculate reasonable table rowHeight withouth "white pixel" in editor.
     * Compare table and xtable
     */
    public void interactiveCompareRowHeight() {
        JXTable xtable = new JXTable(sortableTableModel);
        xtable.setColumnControlVisible(true);
        xtable.setShowGrid(false, false);
        JTable table = new JTable(sortableTableModel);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        table.setRowMargin(0);
        table.getColumnModel().setColumnMargin(0);
        JXFrame frame = wrapWithScrollingInFrame(xtable, table, "compare default rowheight of xtable vs. table");
        frame.setVisible(true);
    }
    
    /**
     * visually check if terminateEditOnFocusLost, autoStartEdit
     * work as expected.
     *
     */
    public void interactiveToggleEditProperties() {
        final JXTable table = new JXTable(10, 2);
        JXFrame frame = wrapWithScrollingInFrame(table, new JButton("something to focus"), 
                "JXTable: toggle terminate/autoStart on left (right is dummy) ");
        Action toggleTerminate = new AbstractAction("toggleTerminate") {

            @Override
            public void actionPerformed(ActionEvent e) {
                table.setTerminateEditOnFocusLost(!table.isTerminateEditOnFocusLost());
                
            }
            
        };
        addAction(frame, toggleTerminate);
        Action toggleAutoStart = new AbstractAction("toggleAutoStart") {

            @Override
            public void actionPerformed(ActionEvent e) {
                table.setAutoStartEditOnKeyStroke(!table.isAutoStartEditOnKeyStroke());
                
            }
            
        };
        addAction(frame, toggleAutoStart);
        frame.setVisible(true);
        
    }

    /**
     * Expose sorted column. 
     * Example how to guarantee one column sorted at all times.
     */
    public void interactiveAlwaysSorted() {
        final JXTable table = new JXTable(sortableTableModel) {

            @Override
            public void columnRemoved(TableColumnModelEvent e) {
                super.columnRemoved(e);
                if (!hasVisibleSortedColumn()) {
                    toggleSortOrder(0);
                }
            }

            private boolean hasVisibleSortedColumn() {
                TableColumn column = getSortedColumn();
                if (column instanceof TableColumnExt) {
                    return ((TableColumnExt) column).isVisible();
                }
                // JW: this path is not tested, don't really expect
                // non-ext column types, though JXTable must 
                // cope with them
                return column != null;
            }

            
        };
        table.setColumnControlVisible(true);
        JXFrame frame = wrapWithScrollingInFrame(table, "Always sorted");
        frame.setVisible(true);
        
    }
   
    /**
     * Issue #282-swingx: compare disabled appearance of
     * collection views.
     * 
     * Issue #1374-swingx: rollover effects on disabled collection views
     *
     */
    public void interactiveDisabledCollectionViews() {
        final JXTable table = new JXTable(new AncientSwingTeam());
        AbstractHyperlinkAction<Object> hyperlink = new AbstractHyperlinkAction<Object>() {

            @Override
            public void actionPerformed(ActionEvent e) {
               LOG.info("pressed link");
            }
        };
        table.getColumnExt(0).setCellRenderer(new DefaultTableRenderer(new HyperlinkProvider(hyperlink)));
        table.getColumnExt(0).setEditable(false);
        table.addHighlighter(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, Color.MAGENTA, null));
        table.setEnabled(false);
        final JXList list = new JXList(new String[] {"one", "two", "and something longer"});
        list.setEnabled(false);
        list.setToolTipText("myText ... showing when disnabled?");
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
        JLabel label = new JLabel("disable label");
        label.setEnabled(false);
        label.setToolTipText("tooltip of disabled label");
        addStatusComponent(frame, label);
        show(frame);
    }

    /**
     * Issue #281-swingx: header should be auto-repainted on changes to
     * header title, value.
     * 
     *
     */
    public void interactiveUpdateHeader() {
        final JXTable table = new JXTable(10, 2);
        JXFrame frame = wrapWithScrollingInFrame(table, "update header");
        Action action = new AbstractAction("update headervalue") {
            int count;
            @Override
            public void actionPerformed(ActionEvent e) {
                table.getColumn(0).setHeaderValue("A" + count++);
                
            }
            
        };
        addAction(frame, action);
        action = new AbstractAction("update column title") {
            int count;
            @Override
            public void actionPerformed(ActionEvent e) {
                table.getColumnExt(0).setTitle("A" + count++);
                
            }
            
        };
        addAction(frame, action);
        frame.setVisible(true);
        
    }
    
    /**
     * Issue #281-swingx, Issue #334-swing: 
     * header should be auto-repainted on changes to
     * header title, value. Must update size if appropriate.
     * 
     * still open: core #4292511 - autowrap not really working
     *
     */
    public void interactiveUpdateHeaderAndSizeRequirements() {
        
        final String[] alternate = { 
                "simple", 
//                "<html><b>This is a test of a large label to see if it wraps </font></b>",
//                "simple", 
                 "<html><center>Line 1<br>Line 2</center></html>" 
                };
        final JXTable table = new JXTable(10, 2);
        
        JXFrame frame = wrapWithScrollingInFrame(table, "update header");
        Action action = new AbstractAction("update headervalue") {
            boolean first;
            @Override
            public void actionPerformed(ActionEvent e) {
                table.getColumn(1).setHeaderValue(first ? alternate[0] : alternate[1]);
                first = !first;
                
            }
            
        };
        addAction(frame, action);
        frame.setVisible(true);
        
    }
    
    /**
     * Issue #??-swingx: column auto-sizing support.
     *
     */
    public void interactiveTestExpandsToViewportWidth() {
        final JXTable table = new JXTable();
        ColumnFactory factory = new ColumnFactory() {
            @Override
            public void configureTableColumn(TableModel model, TableColumnExt columnExt) {
                 super.configureTableColumn(model, columnExt);
                 if (model.getColumnClass(columnExt.getModelIndex()) == Integer.class) {
                     // to see the effect: excess width is distributed relative
                     // to the difference between maxSize and prefSize
                     columnExt.setMaxWidth(200);
                 } else {
                 
                     columnExt.setMaxWidth(1024);
                 }
            }
            
        };
        table.setColumnFactory(factory);
        table.setColumnControlVisible(true);
        table.setModel(sortableTableModel);
        table.setHorizontalScrollEnabled(true);
        table.packAll();
        JXFrame frame = wrapWithScrollingInFrame(table, "expand to width");
        Action toggleModel = new AbstractAction("toggle model") {

            @Override
            public void actionPerformed(ActionEvent e) {
                table.setModel(table.getModel() == sortableTableModel ? 
                        new DefaultTableModel(20, 4) : sortableTableModel);
                
            }
            
        };
        addAction(frame, toggleModel);
        frame.setSize(table.getPreferredSize().width - 50, 300);
        frame.setVisible(true);
        LOG.info("table: " + table.getWidth());
        LOG.info("Viewport: " + table.getParent().getWidth());
    }
    
    /** 
     * Issue ??: Anchor lost after receiving a structure changed.
     * Lead/anchor no longer automatically initialized - no visual clue
     * if table is focused. 
     *
     */
    public void interactiveTestToggleTableModelU6() {
        final DefaultTableModel tableModel = createAscendingModel(0, 20);
        final JTable table = new JTable(tableModel);
        // JW: need to explicitly set _both_ anchor and lead to >= 0
        // need to set anchor first
        table.getSelectionModel().setAnchorSelectionIndex(0);
        table.getSelectionModel().setLeadSelectionIndex(0);
        table.getColumnModel().getSelectionModel().setAnchorSelectionIndex(0);
        table.getColumnModel().getSelectionModel().setLeadSelectionIndex(0);
        Action toggleAction = new AbstractAction("Toggle TableModel") {

            @Override
            public void actionPerformed(ActionEvent e) {
                TableModel model = table.getModel();
                table.setModel(model.equals(tableModel) ? sortableTableModel : tableModel);
            }
            
        };
        JXFrame frame = wrapWithScrollingInFrame(table, "JTable - anchor lost after structure changed");
        addAction(frame, toggleAction);
        frame.setVisible(true);
        frame.pack();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // sanity - focus is on table
                LOG.info("isFocused? " + table.hasFocus());
                LOG.info("who has focus? " + KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner());
            }
        });
    }

    /**
     * Issue #186-swingxProblem with lead/selection and buttons as editors:
     * - move focus (using arrow keys) to first editable boolean  
     * - press space to toggle boolean
     * - move focus to next row (same column)
     * - press space to toggle boolean
     * - move back to first row (same column)
     * - press space: boolean is toggled and (that's the problem) 
     *  lead selection is moved to next row.
     *  No problem in JTable.
     *
     */
    public void interactiveTestCompareTableBoolean() {
        JXTable xtable = new JXTable(createModelWithBooleans());
        JTable table = new JTable(createModelWithBooleans()); 
        JXFrame frame = wrapWithScrollingInFrame(xtable, table, "Compare boolean renderer JXTable <--> JTable");
        frame.setVisible(true);
    }

    private TableModel createModelWithBooleans() {
        String[] columnNames = { "text only", "Bool editable", "Bool not-editable" };
        
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return getValueAt(0, column).getClass();
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                    return !getColumnName(column).contains("not");
            }
            
        };
        for (int i = 0; i < 4; i++) {
                model.addRow(new Object[] {"text only " + i, Boolean.TRUE, Boolean.TRUE });
        }
        return model;
    }


    /**
     * Issue #89-swingx: ColumnControl not updated with ComponentOrientation.
     *
     */
    public void interactiveRToLTableWithColumnControl() {
        final JXTable table = new JXTable(createAscendingModel(0, 20));
         JScrollPane pane = new JScrollPane(table);
        final JXFrame frame = wrapInFrame(pane, "RToLScrollPane");
        addComponentOrientationToggle(frame);
        Action toggleColumnControl = new AbstractAction("toggle column control") {

            @Override
            public void actionPerformed(ActionEvent e) {
                table.setColumnControlVisible(!table.isColumnControlVisible());
                
            }
            
        };
        addAction(frame, toggleColumnControl);
        frame.setVisible(true);
    }
    

    
    
    /**
     * Issue #179: Sorter does not use collator if cell content is
     *  a String.
     *
     */
    public void interactiveTestLocaleSorter() {
        
        Object[][] rowData = new Object[][] {
                new Object[] { Boolean.TRUE, "aa" },
                new Object[] { Boolean.FALSE, "AB" },
                new Object[] { Boolean.FALSE, "AC" },
                new Object[] { Boolean.TRUE, "BA" },
                new Object[] { Boolean.FALSE, "BB" },
                new Object[] { Boolean.TRUE, "BC" } };
        String[] columnNames = new String[] { "Critical", "Task" };
        DefaultTableModel model =  new DefaultTableModel(rowData, columnNames);
//        {
//            public Class getColumnClass(int column) {
//                return column == 1 ? String.class : super.getColumnClass(column);
//            }
//        };
        final JXTable table = new JXTable(model);
        table.toggleSortOrder(1);
        JFrame frame = wrapWithScrollingInFrame(table, "locale sorting");
        frame.setVisible(true);
    }   
    

    /** 
     * Issue #155-swingx: vertical scrollbar policy lost.
     *
     */
    public void interactiveTestColumnControlConserveVerticalScrollBarPolicyAlways() {
        final JXTable table = new JXTable();
        Action toggleAction = new AbstractAction("Toggle Control") {

            @Override
            public void actionPerformed(ActionEvent e) {
                table.setColumnControlVisible(!table.isColumnControlVisible());
                
            }
            
        };
        table.setModel(new DefaultTableModel(10, 5));
        // initial state of column control visibility doesn't seem to matter
//      table.setColumnControlVisible(true);
        final JScrollPane scrollPane1 = new JScrollPane(table);
        scrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        final JXFrame frame = wrapInFrame(scrollPane1, "JXTable Vertical ScrollBar Policy - always");
        addAction(frame, toggleAction);
        Action packAction = new AbstractAction("Pack frame") {

            @Override
            public void actionPerformed(ActionEvent e) {
                frame.remove(scrollPane1);
                frame.add(scrollPane1);
            }
            
        };
        addAction(frame, packAction);
        frame.setVisible(true);
    }


    /** 
     * Issue #155-swingx: vertical scrollbar policy lost.
     *
     */
    public void interactiveTestColumnControlConserveVerticalScrollBarPolicyNever() {
        final JXTable table = new JXTable();
        Action toggleAction = new AbstractAction("Toggle Control") {

            @Override
            public void actionPerformed(ActionEvent e) {
                table.setColumnControlVisible(!table.isColumnControlVisible());
                
            }
            
        };
        table.setModel(new DefaultTableModel(10, 5));
        // initial state of column control visibility doesn't seem to matter
//        table.setColumnControlVisible(true);
        final JScrollPane scrollPane1 = new JScrollPane(table);
        scrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        
        final JXFrame frame = wrapInFrame(scrollPane1, "JXTable Vertical ScrollBar Policy - never");
        addAction(frame, toggleAction);
        Action packAction = new AbstractAction("Pack frame") {

            @Override
            public void actionPerformed(ActionEvent e) {
                frame.remove(scrollPane1);
                frame.add(scrollPane1);
            }
            
        };
        addAction(frame, packAction);
        frame.setVisible(true);
    }

    /** 
     * Issue #11: Column control not showing with few rows.
     *
     */
    public void interactiveTestColumnControlFewRows() {
        final JXTable table = new JXTable();
        Action toggleAction = new AbstractAction("Toggle Control") {

            @Override
            public void actionPerformed(ActionEvent e) {
                table.setColumnControlVisible(!table.isColumnControlVisible());
                
            }
            
        };
        table.setModel(new DefaultTableModel(10, 5));
        table.setColumnControlVisible(true);
        JXFrame frame = wrapWithScrollingInFrame(table, "JXTable ColumnControl with few rows");
        addAction(frame, toggleAction);
        frame.setVisible(true);
    }

    /** 
     * check behaviour outside scrollPane
     *
     */
    public void interactiveTestColumnControlWithoutScrollPane() {
        final JXTable table = new JXTable();
        Action toggleAction = new AbstractAction("Toggle Control") {

            @Override
            public void actionPerformed(ActionEvent e) {
                table.setColumnControlVisible(!table.isColumnControlVisible());
                
            }
            
        };
        toggleAction.putValue(Action.SHORT_DESCRIPTION, "does nothing visible - no scrollpane");
        table.setModel(new DefaultTableModel(10, 5));
        table.setColumnControlVisible(true);
        JXFrame frame = wrapInFrame(table, "JXTable: Toggle ColumnControl outside ScrollPane");
        addAction(frame, toggleAction);
        frame.setVisible(true);
    }

    /** 
     * check behaviour of moving into/out of scrollpane.
     *
     */
    public void interactiveTestToggleScrollPaneWithColumnControlOn() {
        final JXTable table = new JXTable();
        table.setModel(new DefaultTableModel(10, 5));
        table.setColumnControlVisible(true);
        final JXFrame frame = wrapInFrame(table, "JXTable: Toggle ScrollPane with Columncontrol on");
        Action toggleAction = new AbstractAction("Toggle ScrollPane") {

            @Override
            public void actionPerformed(ActionEvent e) {
                Container parent = table.getParent();
                boolean inScrollPane = parent instanceof JViewport;
                if (inScrollPane) {
                    JScrollPane scrollPane = (JScrollPane) table.getParent().getParent();
                    frame.getContentPane().remove(scrollPane);
                    frame.getContentPane().add(table);
                } else {
                  parent.remove(table);
                  parent.add(new JScrollPane(table));
                }
                frame.pack();
                              
            }
            
        };
        addAction(frame, toggleAction);
        frame.setVisible(true);
    }

    /** 
     *  TableColumnExt: user friendly resizable  
     * 
     */
    public void interactiveTestColumnResizable() {
        final JXTable table = new JXTable(sortableTableModel);
        table.setColumnControlVisible(true);
        final TableColumnExt priorityColumn = table.getColumnExt("First Name");
        JXFrame frame = wrapWithScrollingInFrame(table, "JXTable: Column with Min=Max not resizable");
        Action action = new AbstractAction("Toggle MinMax of FirstName") {

            @Override
            public void actionPerformed(ActionEvent e) {
                // user-friendly resizable flag
                if (priorityColumn.getMinWidth() == priorityColumn.getMaxWidth()) {
                    priorityColumn.setMinWidth(50);
                    priorityColumn.setMaxWidth(150);
                } else {
                    priorityColumn.setMinWidth(100);
                    priorityColumn.setMaxWidth(100);
                }
            }
            
        };
        addAction(frame, action);
        frame.setVisible(true);
    }
    
    /**
     */
    public void interactiveTestToggleSortable() {
        final JXTable table = new JXTable(sortableTableModel);
        table.setColumnControlVisible(true);
        Action toggleSortableAction = new AbstractAction("Toggle Sortable") {

            @Override
            public void actionPerformed(ActionEvent e) {
                table.setSortable(!table.isSortable());
                
            }
            
        };
        JXFrame frame = wrapWithScrollingInFrame(table, "ToggleSortingEnabled Test");
        addAction(frame, toggleSortableAction);
        frame.setVisible(true);  
        
    }
    public void interactiveTestTableSizing1() {
        JXTable table = new JXTable();
        table.setAutoCreateColumnsFromModel(false);
        table.setModel(tableModel);
        installLinkRenderer(table);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        TableColumnExt columns[] = new TableColumnExt[tableModel.getColumnCount()];
        for (int i = 0; i < columns.length; i++) {
            columns[i] = new TableColumnExt(i);
            table.addColumn(columns[i]);
        }
        columns[0].setPrototypeValue(new Integer(0));
        columns[1].setPrototypeValue("Simple String Value");
        columns[2].setPrototypeValue(new Integer(1000));
        columns[3].setPrototypeValue(Boolean.TRUE);
        columns[4].setPrototypeValue(new Date(100));
        columns[5].setPrototypeValue(new Float(1.5));
        columns[6].setPrototypeValue(new LinkModel("Sun Micro", "_blank",
                                              tableModel.linkURL));
        columns[7].setPrototypeValue(new Integer(3023));
        columns[8].setPrototypeValue("John Doh");
        columns[9].setPrototypeValue("23434 Testcase St");
        columns[10].setPrototypeValue(new Integer(33333));
        columns[11].setPrototypeValue(Boolean.FALSE);

        table.setVisibleRowCount(12);

        JFrame frame = wrapWithScrollingInFrame(table, "TableSizing1 Test");
        frame.setVisible(true);
    }


    private void installLinkRenderer(JXTable table) {
        LinkModelAction<?> action = new LinkModelAction<LinkModel>() {

            @Override
            public void actionPerformed(ActionEvent e) {
                LOG.info("activated link: " + getTarget());
            }
             
        };
        TableCellRenderer linkRenderer = new DefaultTableRenderer(
                new HyperlinkProvider(action, LinkModel.class));
        table.setDefaultRenderer(LinkModel.class, linkRenderer);
    }

    public void interactiveTestEmptyTableSizing() {
        JXTable table = new JXTable(0, 5);
        table.setColumnControlVisible(true);
        JFrame frame = wrapWithScrollingInFrame(table, "Empty Table (0 rows)");
        frame.setVisible(true);
        
    }
    public void interactiveTestTableSizing2() {
        JXTable table = new JXTable();
        table.setAutoCreateColumnsFromModel(false);
        table.setModel(tableModel);
        installLinkRenderer(table);

        TableColumnExt columns[] = new TableColumnExt[6];
        int viewIndex = 0;
        for (int i = columns.length - 1; i >= 0; i--) {
            columns[viewIndex] = new TableColumnExt(i);
            table.addColumn(columns[viewIndex++]);
        }
        columns[5].setHeaderValue("String Value");
        columns[5].setPrototypeValue("9999");
        columns[4].setHeaderValue("String Value");
        columns[4].setPrototypeValue("Simple String Value");
        columns[3].setHeaderValue("Int Value");
        columns[3].setPrototypeValue(new Integer(1000));
        columns[2].setHeaderValue("Bool");
        columns[2].setPrototypeValue(Boolean.FALSE);
        //columns[2].setSortable(false);
        columns[1].setHeaderValue("Date");
        columns[1].setPrototypeValue(new Date(0));
        //columns[1].setSortable(false);
        columns[0].setHeaderValue("Float");
        columns[0].setPrototypeValue(new Float(5.5));

        table.setRowHeight(24);
        table.setRowMargin(2);
        JFrame frame = wrapWithScrollingInFrame(table, "TableSizing2 Test");
        frame.setVisible(true);
    }


    public void interactiveTestFocusedCellBackground() {
        TableModel model = new AncientSwingTeam() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }
        };
        JXTable xtable = new JXTable(model);
        xtable.setBackground(HighlighterFactory.NOTEPAD);
        JTable table = new JTable(model);
        table.setBackground(new Color(0xF5, 0xFF, 0xF5)); // ledger
        JFrame frame = wrapWithScrollingInFrame(xtable, table, "Unselected focused background: JXTable/JTable");
        frame.setVisible(true);
    }


    public void interactiveTestTableViewProperties() {
        JXTable table = new JXTable(tableModel);
        installLinkRenderer(table);
        table.setIntercellSpacing(new Dimension(15, 15));
        table.setRowHeight(48);
        JFrame frame = wrapWithScrollingInFrame(table, "TableViewProperties Test");
        frame.setVisible(true);
    }

    
    public void interactiveColumnHighlighting() {
        final JXTable table = new JXTable(new AncientSwingTeam());
        
        table.getColumnExt("Favorite Color").setHighlighters(new AbstractHighlighter() {
            @Override
            protected Component doHighlight(Component renderer, ComponentAdapter adapter) {
                Color color = (Color) adapter.getValue();
                
                if (renderer instanceof JComponent) {
                    ((JComponent) renderer).setBorder(BorderFactory.createLineBorder(color));
                }
                
                return renderer;
            }
        });
        
        JFrame frame = wrapWithScrollingInFrame(table, "Column Highlighter Test");
        JToolBar bar = new JToolBar();
        bar.add(new AbstractAction("Toggle") {
            boolean state = false;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (state) {
                    table.getColumnExt("No.").setHighlighters(new Highlighter[0]);
                    state = false;
                } else {
                    table.getColumnExt("No.").addHighlighter(
                        new AbstractHighlighter(new HighlightPredicate() {
                            @Override
                            public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
                                return adapter.getValue().toString().contains("8");
                            }
                        }) {
                            @Override
                            protected Component doHighlight(Component renderer, ComponentAdapter adapter) {
                                Font f = renderer.getFont().deriveFont(Font.ITALIC);
                                renderer.setFont(f);
                                
                                return renderer;
                            }
                        }
                    );
                    state = true;
                }
            }
        });
        frame.add(bar, BorderLayout.NORTH);
        frame.setVisible(true);
    }
 

    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // super has LF specific tests...
        setSystemLF(true);
//        setLookAndFeel("Nimbus");
    }


    /**
     * dummy
     */
    @Test
    public void testDummy() {
    }   

}
