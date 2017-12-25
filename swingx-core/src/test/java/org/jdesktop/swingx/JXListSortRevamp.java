/*
 * $Id: JXListTest.java 3199 2009-01-21 18:37:28Z kschaefe $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListModel;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.jdesktop.swingx.hyperlink.LinkModel;
import org.jdesktop.swingx.sort.ListSortController;
import org.jdesktop.swingx.sort.RowFilters;
import org.jdesktop.swingx.sort.RowSorterWrapper;
import org.jdesktop.swingx.sort.TableSortController;
import org.jdesktop.test.ListSelectionReport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Subset of tests for currently disabled sorting. Obviously, they are failing.
 * Once re-added support, we'll need analogous tests.
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class JXListSortRevamp extends InteractiveTestCase {

    protected ListModel listModel;
    protected DefaultListModelF ascendingListModel;
    private ListSortController<ListModel> controller;
    private JXList list;

    public static void main(String[] args) {
        JXListSortRevamp test = new JXListSortRevamp();
        try {
//            test.runInteractiveTests();
            test.runInteractiveTests("interactive.*RowSorter.*");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    @Test
    public void testSelectionEventListX() {
        JXList list = new JXList(ascendingListModel);
        list.setSelectedIndex(0);
        ListSelectionReport report = new ListSelectionReport();
        list.addListSelectionListener(report);
        list.addSelectionInterval(1, 1);
        assertEquals(1, report.getEventCount(true));
        assertEquals(1, report.getLastEvent(true).getLastIndex());
        assertEquals(1, report.getLastEvent(true).getFirstIndex());
    }
    

    @Test
    public void testSelectionEventList() {
        JList list = new JList(ascendingListModel);
        list.setSelectedIndex(0);
        ListSelectionReport report = new ListSelectionReport();
        list.addListSelectionListener(report);
        list.addSelectionInterval(1, 1);
        assertEquals(1, report.getEventCount(true));
        assertEquals(1, report.getLastEvent(true).getLastIndex());
        assertEquals(1, report.getLastEvent(true).getFirstIndex());
    }
    
    @Test
    public void testSelectionEvent() {
        DefaultListSelectionModel list = new DefaultListSelectionModel();
        list.setLeadAnchorNotificationEnabled(false);
        list.setSelectionInterval(0, 0);
        ListSelectionReport report = new ListSelectionReport();
        list.addListSelectionListener(report);
        list.addSelectionInterval(1, 1);
        assertEquals(1, report.getEventCount(true));
        assertEquals(1, report.getLastEvent(true).getLastIndex());
        assertEquals(1, report.getLastEvent(true).getFirstIndex());
    }
    
    
//------------------ re-enable
    
    public void interactiveRowSorter() {
        final JXList list = new JXList(ascendingListModel);
        final DefaultTableModel tableModel = new DefaultTableModel(list.getElementCount(), 1) {
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return Integer.class;
            }
            
        };
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.setValueAt(i, i, 0);
        }
        final JXTable table = new JXTable(tableModel);
        final ListSortController<ListModel> controller = new ListSortController<ListModel>(list.getModel());
        list.setRowSorter(controller);
        controller.setComparator(0, TableSortController.COMPARABLE_COMPARATOR);
        Action sort = new AbstractAction("toggle sort") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                list.toggleSortOrder();
                table.toggleSortOrder(0);
                
            }
        };
        Action reset = new AbstractAction("reset") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.resetSortOrders();
                table.resetSortOrder();
                
            }
        };
        Action toggleFilter = new AbstractAction("toggle filter") {
            boolean hasFilter;
            @Override
            public void actionPerformed(ActionEvent e) {
                if (hasFilter) {
                    RowFilter<Object, Integer> filter = RowFilters.regexFilter("0", 0);
                    list.setRowFilter(filter);
                    table.setRowFilter(filter);
                } else {
                    list.setRowFilter(null);
                    table.setRowFilter(null);
                }
                hasFilter = !hasFilter;
            }
        };
        Action removeFirst = new AbstractAction("remove firstM") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                ascendingListModel.remove(0);
                tableModel.removeRow(0);
                
            }
        };
        Action addFirst = new AbstractAction("add firstM") {
            int counter;
            @Override
            public void actionPerformed(ActionEvent e) {
                ascendingListModel.add(0, 10 * ++counter);
                tableModel.insertRow(0, new Object[] {ascendingListModel.getElementAt(0)});
                
            }
        };
        Action fireAllChanged = new AbstractAction("fireDataChanged") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                ascendingListModel.fireContentsChanged();
                tableModel.fireTableDataChanged();
            }
            
        };
        JXFrame frame = showWithScrollingInFrame(list, table, "sort in rowSorter");
        addAction(frame, sort);
        addAction(frame, reset);
        addAction(frame, toggleFilter);
        addAction(frame, removeFirst);
        addAction(frame, addFirst);
        addAction(frame, fireAllChanged);
        show(frame);
    }
    
    /**
     * Compare behaviour of selection in xtable with RowSorterWrapper after
     * 1. model changes
     * 2. filter changes
     * 3. shared selectionModel (starts with shared)
     */
    @SuppressWarnings("unchecked")
    public void interactiveRowSorterWrapperSharedXTable() {
        final DefaultTableModel tableModel = new DefaultTableModel(list.getElementCount(), 2) {
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return Integer.class;
            }
            
        };
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.setValueAt(i, i, 0);
            tableModel.setValueAt(tableModel.getRowCount() - i, i, 1);
        }
        final JXTable master = new JXTable(tableModel);
        final TableSortController<TableModel> rowSorter = (TableSortController<TableModel>) master.getRowSorter();
        master.removeColumn(master.getColumn(0));
        final JXTable rowHeader = new JXTable(master.getModel());
        rowHeader.setAutoCreateRowSorter(false);
        rowHeader.removeColumn(rowHeader.getColumn(1));
        rowHeader.setRowSorter(new RowSorterWrapper<TableModel>(rowSorter));
        rowHeader.setSelectionModel(master.getSelectionModel());
        // need to disable selection update on one of the table's 
        // otherwise the selection is not kept in model coordinates
        rowHeader.setUpdateSelectionOnSort(false);
        JScrollPane scrollPane = new JScrollPane(master);
        scrollPane.setRowHeaderView(rowHeader);
        JXFrame frame = showInFrame(scrollPane, "xtables (wrapped sortController): shared model/selection");
        Action fireAllChanged = new AbstractAction("fireDataChanged") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                tableModel.fireTableDataChanged();
            }
            
        };
        addAction(frame, fireAllChanged);
        Action removeFirst = new AbstractAction("remove firstM") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                tableModel.removeRow(0);
                
            }
        };
        addAction(frame, removeFirst);
        Action removeLast = new AbstractAction("remove lastM") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                tableModel.removeRow(tableModel.getRowCount() - 1);
                
            }
        };
        addAction(frame, removeLast);
        Action filter = new AbstractAction("toggle filter") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                RowFilter filter = rowSorter.getRowFilter();
                if (filter == null) {
                    rowSorter.setRowFilter(RowFilter.regexFilter("^1", 1));
                } else {
                    rowSorter.setRowFilter(null);
                }
                
            }
        };
        addAction(frame, filter);
        addStatusMessage(frame, "row header example with RowSorterWrapper");
        show(frame);
    }
    
    /**
     * Compare behaviour of selection in xtable with shared TableSortController after
     * 1. model changes
     * 2. filter changes
     * 3. shared selectionModel (starts with shared)
     */
    @SuppressWarnings("unchecked")
    public void interactiveXRowSorterSharedXTable() {
        final DefaultTableModel tableModel = new DefaultTableModel(list.getElementCount(), 1) {
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return Integer.class;
            }
            
        };
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.setValueAt(i, i, 0);
        }
        final JTable table = new JXTable(tableModel);
        table.setAutoCreateRowSorter(false);
        final JTable core = new JXTable(table.getModel());
        core.setAutoCreateRowSorter(false);
        final TableSortController<TableModel> rowSorter = new TableSortController<TableModel>(table.getModel());
        table.setRowSorter(rowSorter);
        core.setRowSorter(rowSorter);
        core.setSelectionModel(table.getSelectionModel());
        // need to disable selection update on one of the table's 
        // otherwise the selection is not kept in model coordinates
        core.setUpdateSelectionOnSort(false);
        JXFrame frame = showWithScrollingInFrame(table, core, "xtables (shared sortController): shared model/sorter/selection");
        Action fireAllChanged = new AbstractAction("fireDataChanged") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                tableModel.fireTableDataChanged();
            }
            
        };
        addAction(frame, fireAllChanged);
        Action removeFirst = new AbstractAction("remove firstM") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                tableModel.removeRow(0);
                
            }
        };
        addAction(frame, removeFirst);
        Action removeLast = new AbstractAction("remove lastM") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                tableModel.removeRow(tableModel.getRowCount() - 1);
                
            }
        };
        addAction(frame, removeLast);
        Action filter = new AbstractAction("toggle filter") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                RowFilter filter = rowSorter.getRowFilter();
                if (filter == null) {
                    rowSorter.setRowFilter(RowFilter.regexFilter("^1", 0));
                } else {
                    rowSorter.setRowFilter(null);
                }
                
            }
        };
        addAction(frame, filter);
        Action shareSelection = new AbstractAction("toggle selection share") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean oldSelectionShared = table.getSelectionModel() == core.getSelectionModel();
                if (oldSelectionShared) {
                    core.setSelectionModel(new DefaultListSelectionModel());
                } else {
                    core.setSelectionModel(table.getSelectionModel());
                }
                core.setUpdateSelectionOnSort(!oldSelectionShared);
            }
        };
        addAction(frame, shareSelection);
        addStatusMessage(frame, "here: updateSelectionOnSort is false if selection shared!");
        show(frame);
    }
    
    /**
     * Compare behaviour of selection in core table with shared TableSortController after
     * 1. model changes
     * 2. filter changes
     * 3. shared selectionModel (starts with shared)
     */
    @SuppressWarnings("unchecked")
    public void interactiveXRowSorterShared() {
        final DefaultTableModel tableModel = new DefaultTableModel(list.getElementCount(), 1) {
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return Integer.class;
            }
            
        };
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.setValueAt(i, i, 0);
        }
        final JTable table = new JTable(tableModel);
        final JTable core = new JTable(table.getModel());
        final TableSortController<TableModel> rowSorter = new TableSortController<TableModel>(table.getModel());
        table.setRowSorter(rowSorter);
        core.setRowSorter(rowSorter);
        core.setSelectionModel(table.getSelectionModel());
        JXFrame frame = showWithScrollingInFrame(table, core, "core tables (shared sortController): shared model/sorter/selection");
        Action fireAllChanged = new AbstractAction("fireDataChanged") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                tableModel.fireTableDataChanged();
            }
            
        };
        addAction(frame, fireAllChanged);
        Action removeFirst = new AbstractAction("remove firstM") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                tableModel.removeRow(0);
                
            }
        };
        addAction(frame, removeFirst);
        Action removeLast = new AbstractAction("remove lastM") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                tableModel.removeRow(tableModel.getRowCount() - 1);
                
            }
        };
        addAction(frame, removeLast);
       Action filter = new AbstractAction("toggle filter") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                RowFilter filter = rowSorter.getRowFilter();
                if (filter == null) {
                    rowSorter.setRowFilter(RowFilter.regexFilter("^1", 0));
                } else {
                    rowSorter.setRowFilter(null);
                }
                
            }
        };
        addAction(frame, filter);
        Action shareSelection = new AbstractAction("toggle selection share") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (table.getSelectionModel() == core.getSelectionModel()) {
                    core.setSelectionModel(new DefaultListSelectionModel());
                } else {
                    core.setSelectionModel(table.getSelectionModel());
                }
            }
        };
        addAction(frame, shareSelection);
        show(frame);
    }
    
    
    /**
     * Compare behaviour of selection in core table with shared TableRowSorter after
     * 1. model changes
     * 2. filter changes
     * 3. shared selectionModel (starts with not shared)
     */
    @SuppressWarnings("unchecked")
    public void interactiveRowSorterShared() {
        final DefaultTableModel tableModel = new DefaultTableModel(list.getElementCount(), 1) {
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return Integer.class;
            }
            
        };
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.setValueAt(i, i, 0);
        }
        final JTable table = new JTable(tableModel);
        final JTable core = new JTable(table.getModel());
        final TableRowSorter<TableModel> rowSorter = new TableRowSorter<TableModel>(table.getModel());
        table.setRowSorter(rowSorter);
        core.setRowSorter(rowSorter);
        core.setSelectionModel(table.getSelectionModel());
        JXFrame frame = showWithScrollingInFrame(table, core, "core tables: shared model/sorter/selection");
        Action fireAllChanged = new AbstractAction("fireDataChanged") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                tableModel.fireTableDataChanged();
            }
            
        };
        addAction(frame, fireAllChanged);
        Action removeFirst = new AbstractAction("remove firstM") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                tableModel.removeRow(0);
                
            }
        };
        addAction(frame, removeFirst);
        Action removeLast = new AbstractAction("remove lastM") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                tableModel.removeRow(tableModel.getRowCount() - 1);
                
            }
        };
        addAction(frame, removeLast);
        Action filter = new AbstractAction("toggle filter") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                RowFilter filter = rowSorter.getRowFilter();
                if (filter == null) {
                    rowSorter.setRowFilter(RowFilter.regexFilter("^1", 0));
                } else {
                    rowSorter.setRowFilter(null);
                }
                
            }
        };
        addAction(frame, filter);
        Action shareSelection = new AbstractAction("toggle selection share") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (table.getSelectionModel() == core.getSelectionModel()) {
                    core.setSelectionModel(new DefaultListSelectionModel());
                } else {
                    core.setSelectionModel(table.getSelectionModel());
                }
            }
        };
        addAction(frame, shareSelection);
        show(frame);
    }
    
    /**
     * Compare behaviour of selection with (unrelated) TableRowSorter after
     * 1. model changes
     * 2. filter changes
     * 3. shared selectionModel (starts with not shared)
     */
    @SuppressWarnings("unchecked")
    public void interactiveRowSorterCore() {
        final DefaultTableModel tableModel = new DefaultTableModel(list.getElementCount(), 1) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return Integer.class;
            }
            
        };
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.setValueAt(i, i, 0);
        }
        final JXTable table = new JXTable(tableModel);
        final JTable core = new JTable(table.getModel());
        final TableRowSorter<TableModel> rowSorter = new TableRowSorter<TableModel>(table.getModel());
        core.setRowSorter(rowSorter);
        JXFrame frame = showWithScrollingInFrame(table, core, "xTable <> core");
        Action fireAllChanged = new AbstractAction("fireDataChanged") {

            @Override
            public void actionPerformed(ActionEvent e) {
                tableModel.fireTableDataChanged();
            }
            
        };
        addAction(frame, fireAllChanged);
        Action removeFirst = new AbstractAction("remove firstM") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                tableModel.removeRow(0);
                
            }
        };
        addAction(frame, removeFirst);
        Action removeLast = new AbstractAction("remove lastM") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                tableModel.removeRow(tableModel.getRowCount() - 1);
                
            }
        };
        addAction(frame, removeLast);
        Action filter = new AbstractAction("toggle filter") {
            RowFilter regex = RowFilter.regexFilter("^1", 0);
            @Override
            public void actionPerformed(ActionEvent e) {
                RowFilter filter = rowSorter.getRowFilter();
                if (filter == null) {
                    rowSorter.setRowFilter(regex);
                    table.setRowFilter(regex);
                } else {
                    rowSorter.setRowFilter(null);
                    table.setRowFilter(null);
                }
                
            }
        };
        addAction(frame, filter);
        Action shareSelection = new AbstractAction("toggle selection share") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (table.getSelectionModel() == core.getSelectionModel()) {
                    core.setSelectionModel(new DefaultListSelectionModel());
                } else {
                    core.setSelectionModel(table.getSelectionModel());
                }
            }
        };
        addAction(frame, shareSelection);
        show(frame);
    }

    
    //----------------- re-enabled functionality
//--------------------- interactive
    

    /**
     * Issue #377-swingx: JXList (it's wrapping model) fires incorrect events.
     * 
     * 
     */
    public void interactiveFilterMutateModel() {
        final DefaultListModel model = createAscendingListModel(0, 5);
        // PENDING: currently not useful, sort disabled
        final JXList list = new JXList(model, true);
//        list.setFilters(new FilterPipeline(new PatternFilter()));
        JXFrame frame = wrapWithScrollingInFrame(list, "Mutate model with filter");
        Action addItem = new AbstractAction("add item") {

            public void actionPerformed(ActionEvent e) {
                int selected = list.getSelectedIndex();
                if (selected >= 0) {
                    selected = list.convertIndexToModel(selected);
                }
                if (selected > 0) {
                    model.add(selected - 1, model.getSize());
                } else {
                    model.addElement(model.getSize());
                }
                
            }
            
        };
        addAction(frame, addItem);
        Action removeItem = new AbstractAction("remove item") {

            public void actionPerformed(ActionEvent e) {
                int selected = list.getSelectedIndex();
                if (selected >= 0) {
                    selected = list.convertIndexToModel(selected);
                }
                if (selected > 0) {
                    model.remove(selected - 1);
                } 
                
            }
            
        };
        addAction(frame, removeItem);
        Action changeItem = new AbstractAction("change item") {

            public void actionPerformed(ActionEvent e) {
                int selected = list.getSelectedIndex();
                if (selected >= 0) {
                    selected = list.convertIndexToModel(selected);
                }
                if (selected > 0) {
                    int newValue = ((Integer) model.getElementAt(selected - 1)).intValue() + 10;
                    model.set(selected - 1, newValue);
                } 
                
            }
            
        };
        addAction(frame, changeItem);
        Action flush = new AbstractAction("toggle sort") {

            public void actionPerformed(ActionEvent e) {
                list.toggleSortOrder();
            }
            
        };
        addAction(frame, flush);
        show(frame);
    }
    

    public void interactiveTestSort() {
        // PENDING: currently not useful, sort disabled
        final JXList list = new JXList(listModel, true);
        JXFrame frame = wrapWithScrollingInFrame(list, "Toggle sorter");
        Action toggleSortOrder = new AbstractAction("Toggle Sort Order") {

            public void actionPerformed(ActionEvent e) {
                list.toggleSortOrder();
                
            }
            
        };
        addAction(frame, toggleSortOrder);
        Action resetSortOrder = new AbstractAction("Reset Sort Order") {

            public void actionPerformed(ActionEvent e) {
                list.resetSortOrder();
                
            }
            
        };
        addAction(frame, resetSortOrder);
        frame.setVisible(true);
        
    }

    
//-------------------- factory methods, setup    
    protected ListModel createListModel() {
        JXList list = new JXList();
        return new DefaultComboBoxModel(list.getActionMap().allKeys());
    }

    public static class DefaultListModelF extends DefaultListModel {
        /**
         * Fires a contentsChanged with -1, -1. 
         */
        public void fireContentsChanged() {
            fireContentsChanged(this, -1, -1);
        }
    };
    protected DefaultListModelF createAscendingListModel(int startRow, int count) {
        DefaultListModelF l = new DefaultListModelF();
        for (int row = startRow; row < startRow  + count; row++) {
            l.addElement(new Integer(row));
        }
        return l;
    }
    protected DefaultListModel createListModelWithLinks() {
        DefaultListModel model = new DefaultListModel();
        for (int i = 0; i < 20; i++) {
            try {
                LinkModel link = new LinkModel("a link text " + i, null, new URL("http://some.dummy.url" + i));
                if (i == 1) {
                    URL url = JXEditorPaneTest.class.getResource("resources/test.html");

                    link = new LinkModel("a resource", null, url);
                }
                model.addElement(link);
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
 
        return model;
    }

    /**
     * Creates and returns a number filter, passing values which are numbers and
     * have int values inside or outside of the bounds (included), depending on the given 
     * flag.
     * 
     * @param lowerBound
     * @param upperBound
     * @param inside 
     * @return
     */
//    protected Filter createNumberFilter(final int lowerBound, final int upperBound, final boolean inside) {
//        PatternFilter f = new PatternFilter() {
//
//            @Override
//            public boolean test(int row) {
//                Object value = getInputValue(row, getColumnIndex());
//                if (!(value instanceof Number)) return false;
//                boolean isInside = ((Number) value).intValue() >= lowerBound 
//                    && ((Number) value).intValue() <= upperBound;
//                return inside ? isInside : !isInside;
//            }
//            
//        };
//        return f;
//    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        listModel = createListModel();
        ascendingListModel = createAscendingListModel(0, 22);
        list = new JXList(ascendingListModel);
        controller = new ListSortController<ListModel>(list.getModel());
        controller.setComparator(0, TableSortController.COMPARABLE_COMPARATOR);
        list.setRowSorter(controller);

    }
    public JXListSortRevamp() {
        super("JXList Tests");
    }

    
    @Before
    public void setUpJ4() throws Exception {
        setUp();
    }
    
    @After
    public void tearDownJ4() throws Exception {
        tearDown();
    }

}
