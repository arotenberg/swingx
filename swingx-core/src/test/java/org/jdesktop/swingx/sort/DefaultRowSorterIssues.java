/*
 * $Id$
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
package org.jdesktop.swingx.sort;

import java.util.logging.Logger;

import javax.swing.DefaultRowSorter;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.jdesktop.swingx.InteractiveTestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Test the DefaultRowSorter, to expose facets of core 
 * <a href=http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6894632>Issue 6894632</a>.
 * <p>
 * Compare with DefaultSortControllerTest to see the effect of the fix in SwingX.
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class DefaultRowSorterIssues extends InteractiveTestCase {

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(DefaultRowSorterIssues.class.getName());
    
    int rows;
    DefaultTableModel model;
    DefaultRowSorter<TableModel, ?> sorter;

    /**
     * RowSorter not shareable!
     */
    @Test
    public void testSimulateShareSorter() {
        sorter.toggleSortOrder(0);
        int last = model.getRowCount() - 1;
        model.removeRow(last);
        // notification by first table
        sorter.rowsDeleted(last, last);
        // notification by second table
        sorter.rowsDeleted(last, last);
        sorter.convertRowIndexToModel(last - 1);
    }
    
    /**
     * RowSorter not shareable!
     * Here: remove last row of model - fails immediately with OOB exception
     */
    @Test
    public void testShareSorterRemoveLastRow() {
        DefaultTableModel model = new DefaultTableModel(10, 1);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(model);
        int last = model.getRowCount() - 1;
        JTable table = new JTable(model);
        table.setRowSorter(sorter);
        JTable other = new JTable(model);
        other.setRowSorter(sorter);
        sorter.toggleSortOrder(0);
        sorter.toggleSortOrder(0);
        model.removeRow(last);
    }
    
    /**
     * RowSorter not shareable!
     * 
     * Here: remove first row of model - no exception, incorrect view row count
     */
    @Test
    public void testShareSorterRemoveFirstRow() {
        DefaultTableModel model = new DefaultTableModel(10, 1);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(model);
        int last = model.getRowCount() - 1;
        JTable table = new JTable(model);
        table.setRowSorter(sorter);
        JTable other = new JTable(model);
        other.setRowSorter(sorter);
        sorter.toggleSortOrder(0);
        sorter.toggleSortOrder(0);
        model.removeRow(0);
        assertEquals(last, table.getRowCount());
    }
    
    /**
     * Selection of last row lost if a row above is removed.
     * 
     * Core issue: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6894632
     */
    @Test
    public void testRemoveSelected() {
        DefaultTableModel model = new DefaultTableModel(10, 1);
        JTable table = new JTable(model);
        // install a rowSorter
        table.setAutoCreateRowSorter(true);
        int last = table.getRowCount() - 1;
        // select that last row
        table.setRowSelectionInterval(last, last);
        assertTrue("sanity: really selected", table.getSelectionModel().isSelectedIndex(last));
        // remove the second last
        model.removeRow(last - 1);
        assertEquals("last row must be still selected", 
                table.getRowCount() - 1, table.getSelectedRow());
    }

    //------------------------ model changes with notification    
//-------------------- conversion methods: to model
    /**
     * Core Issue http://forums.sun.com/thread.jspa?messageID=10939199#10939199
     * 
     * Table looses selection of last row if deleted before last.
     * 
     * Here: after being notified, DefaultRowSorter must throw on invalid index 
     */
    @Test (expected = IndexOutOfBoundsException.class)
    public void testToModelAfterRemoveSorted() {
        sorter.toggleSortOrder(0);
        sorter.toggleSortOrder(0);
        model.removeRow(rows - 1);
        sorter.rowsDeleted(rows - 1, rows - 1);
        sorter.convertRowIndexToModel(rows - 1);
    }
    /**
     * Core Issue http://forums.sun.com/thread.jspa?messageID=10939199#10939199
     * 
     * Table looses selection of last row if deleted before last.
     * 
     * Here: after being notified, DefaultRowSorter must throw on invalid index 
     */
    @Test (expected = IndexOutOfBoundsException.class)
    public void testToModelAfterRemoveNotSorted() {
        model.removeRow(rows - 1);
        sorter.rowsDeleted(rows - 1, rows - 1);
        sorter.convertRowIndexToModel(rows - 1);
    }
    

//------------------ conversion methods: toView
    
    /**
     * Core Issue http://forums.sun.com/thread.jspa?messageID=10939199#10939199
     * 
     * Table looses selection of last row if deleted before last.
     * 
     * Here: after being notified, DefaultRowSorter must throw on invalid index 
     */
    @Test (expected = IndexOutOfBoundsException.class)
    public void testToViewAfterRemoveSorted() {
        sorter.toggleSortOrder(0);
        sorter.toggleSortOrder(0);
        model.removeRow(rows - 1);
        sorter.rowsDeleted(rows - 1, rows - 1);
        sorter.convertRowIndexToView(rows - 1);
    }

    /**
     * Core Issue http://forums.sun.com/thread.jspa?messageID=10939199#10939199
     * 
     * Table looses selection of last row if deleted before last.
     * 
     * Here: after being notified, DefaultRowSorter must throw on invalid index 
     */
    @Test (expected = IndexOutOfBoundsException.class)
    public void testToViewAfterRemoveNotSorted() {
        model.removeRow(rows - 1);
        sorter.rowsDeleted(rows - 1, rows - 1);
        sorter.convertRowIndexToView(rows - 1);
    }
    
    /**
     * Core Issue http://forums.sun.com/thread.jspa?messageID=10939199#10939199
     * 
     * Table looses selection of last row if deleted before last.
     * 
     * Here: after being notified, DefaultRowSorter must throw on invalid index 
     */
    @Test (expected = IndexOutOfBoundsException.class)
    public void testToViewAfterRemoveAllPassFilter() {
        RowFilter<Object, Object> filter = RowFilter.regexFilter(".*");
        sorter.setRowFilter(filter);
        model.removeRow(rows - 1);
        sorter.rowsDeleted(rows - 1, rows - 1);
        sorter.convertRowIndexToView(rows - 1);
    }
    

//----------------------- row counts: model
    
    /**
     * Core Issue http://forums.sun.com/thread.jspa?messageID=10939199#10939199
     * 
     * Table looses selection of last row if deleted before last.
     * 
     * Here: DefaultRowSorter modelRowCount must be unchanged after silent
     *   model insert.
     */
    @Test
    public void testModelRowCountSilentInsert() {
        model.addRow(new Object[] {rows});
        sorter.rowsInserted(rows, rows);
        assertEquals(rows + 1, sorter.getModelRowCount());
    }
    
    /**
     * Core Issue http://forums.sun.com/thread.jspa?messageID=10939199#10939199
     * 
     * Table looses selection of last row if deleted before last.
     * 
     * Here: DefaultRowSorter modelRowCount must be unchanged after silent
     *   model delete.
     */
    @Test
    public void testModelRowCountAfterRemove() {
        model.removeRow(0);
        sorter.rowsDeleted(0, 0);
        assertEquals(rows - 1, sorter.getModelRowCount());
    }

    
//------------------------ silent model changes, that is client fails to notify    
//------------------------ conversion methods: toModel    
    /**
     * Core Issue http://forums.sun.com/thread.jspa?messageID=10939199#10939199
     * 
     * Table looses selection of last row if deleted before last.
     * 
     * Here: DefaultRowSorter does not throw on invalid (removed) model row index
     *    if sorted - that's (my) expected behaviour but inconsistent with 
     *    unsorted.
     */
    @Test
    public void testToModelAfterSilentRemoveSorted() {
        sorter.toggleSortOrder(0);
        sorter.toggleSortOrder(0);
        model.removeRow(rows - 1);
        assertEquals(0, sorter.convertRowIndexToModel(rows - 1));
    }
    /**
     * Core Issue http://forums.sun.com/thread.jspa?messageID=10939199#10939199
     * 
     * Table looses selection of last row if deleted before last.
     * 
     * Here: DefaultRowSorter does not throw on invalid (removed) model row index
     *    if sorted - that's (my) expected behaviour but inconsistent with 
     *    unsorted.
     */
    @Test
    public void testToModelAfterSilentRemoveNotSorted() {
        model.removeRow(rows - 1);
        assertEquals(rows - 1, sorter.convertRowIndexToModel(rows - 1));
    }
    
    /**
     * Core Issue http://forums.sun.com/thread.jspa?messageID=10939199#10939199
     * 
     * Table looses selection of last row if deleted before last.
     * 
     * Here: DefaultRowSorter throws on invalid (silently added) model row index
     *    if sorted - that's expected behaviour because it was not notified about
     *    a model change.
     */
    @Test (expected = IndexOutOfBoundsException.class)
    public void testToModelAfterSilentInsertSorted() {
        sorter.toggleSortOrder(0);
        sorter.toggleSortOrder(0);
        model.addRow(new Object[] {rows});
        sorter.convertRowIndexToModel(rows);
    }
    
    /**
     * Core Issue http://forums.sun.com/thread.jspa?messageID=10939199#10939199
     * 
     * Table looses selection of last row if deleted before last.
     * 
     * Here: DefaultRowSorter does not throw on invalid (silently added) model row index
     *    if not sorted - that's not expected behaviour because it's invalid with
     *    sorted state.
     */
    @Test (expected = IndexOutOfBoundsException.class)
    public void testToModelAfterSilentInsertNotSorted() {
        model.addRow(new Object[] {rows});
        sorter.convertRowIndexToModel(rows);
    }


//------------------ conversion methods: toView
    
    /**
     * Core Issue http://forums.sun.com/thread.jspa?messageID=10939199#10939199
     * 
     * Table looses selection of last row if deleted before last.
     * 
     * Here: DefaultRowSorter does not throw on invalid (removed) model row index
     *    if sorted - that's (my) expected behaviour but inconsistent with 
     *    unsorted.
     */
    @Test
    public void testToViewAfterSilentRemoveSorted() {
        sorter.toggleSortOrder(0);
        sorter.toggleSortOrder(0);
        model.removeRow(rows - 1);
        assertEquals(0, sorter.convertRowIndexToView(rows - 1));
    }

    /**
     * Core Issue http://forums.sun.com/thread.jspa?messageID=10939199#10939199
     * 
     * Table looses selection of last row if deleted before last.
     * 
     * Here: DefaultRowSorter does throw on invalid (removed) model row index
     *    if not sorted - shouldn't because it wasn't notified about the model 
     *    change (responsibility of caller)
     */
    @Test
    public void testToViewAfterSilentRemoveNotSorted() {
        model.removeRow(rows - 1);
        assertEquals(rows - 1, sorter.convertRowIndexToView(rows - 1));
    }
    
    /**
     * Core Issue http://forums.sun.com/thread.jspa?messageID=10939199#10939199
     * 
     * Table looses selection of last row if deleted before last.
     * 
     * Here: DefaultRowSorter does not throw on invalid (removed) model row index
     *    if has filter - that's (my) expected behaviour but inconsistent with 
     *    unsorted.
     */
    @Test
    public void testToViewAfterSilentRemoveAllPassFilter() {
        RowFilter<Object, Object> filter = RowFilter.regexFilter(".*");
        sorter.setRowFilter(filter);
        model.removeRow(rows - 1);
        assertEquals(rows - 1, sorter.convertRowIndexToView(rows - 1));
    }
    
    /**
     * Core Issue http://forums.sun.com/thread.jspa?messageID=10939199#10939199
     * 
     * Table looses selection of last row if deleted before last.
     * 
     * Here: DefaultRowSorter does not throw on invalid (removed) model row index
     *    if has filter - that's (my) expected behaviour but inconsistent with 
     *    unsorted.
     */
    @Test (expected = IndexOutOfBoundsException.class)
    public void testToViewAfterSilentInsertAllPassFilter() {
        RowFilter<Object, Object> filter = RowFilter.regexFilter(".*");
        sorter.setRowFilter(filter);
        model.addRow(new Object[] {rows});
        assertEquals(rows, sorter.convertRowIndexToView(rows));
    }

    /**
     * Core Issue http://forums.sun.com/thread.jspa?messageID=10939199#10939199
     * 
     * Table looses selection of last row if deleted before last.
     * 
     * Here: DefaultRowSorter throws on invalid (silently added) model row index
     *    if sorted - that's expected behaviour because it was not notified about
     *    a model change.
     */
    @Test (expected = IndexOutOfBoundsException.class)
    public void testToViewAfterSilentInsertSorted() {
        sorter.toggleSortOrder(0);
        sorter.toggleSortOrder(0);
        model.addRow(new Object[] {rows});
        sorter.convertRowIndexToView(rows);
    }
    
    /**
     * Core Issue http://forums.sun.com/thread.jspa?messageID=10939199#10939199
     * 
     * Table looses selection of last row if deleted before last.
     * 
     * Here: DefaultRowSorter does not throw on invalid (silently added) model row index
     *    if not sorted - that's not expected behaviour because it's invalid with
     *    sorted state.
     */
    @Test (expected = IndexOutOfBoundsException.class)
    public void testToViewAfterSilentInsertNotSorted() {
        model.addRow(new Object[] {rows});
        sorter.convertRowIndexToView(rows);
    }

//----------------------- row counts: model
    
    /**
     * Core Issue http://forums.sun.com/thread.jspa?messageID=10939199#10939199
     * 
     * Table looses selection of last row if deleted before last.
     * 
     * Here: DefaultRowSorter modelRowCount must be unchanged after silent
     *   model insert.
     */
    @Test
    public void testModelRowCountAfterSilentInsert() {
        model.addRow(new Object[] {rows});
        assertEquals(rows, sorter.getModelRowCount());
    }
    
    /**
     * Core Issue http://forums.sun.com/thread.jspa?messageID=10939199#10939199
     * 
     * Table looses selection of last row if deleted before last.
     * 
     * Here: DefaultRowSorter modelRowCount must be unchanged after silent
     *   model delete.
     */
    @Test
    public void testModelRowCountAfterSilentRemove() {
        model.removeRow(0);
        assertEquals(rows, sorter.getModelRowCount());
    }
    
//-------------------- row counts: view
    
    /**
     * Core Issue http://forums.sun.com/thread.jspa?messageID=10939199#10939199
     * 
     * Table looses selection of last row if deleted before last.
     * 
     * Here: DefaultRowSorter modelRowCount must be unchanged after silent
     *   model insert.
     */
    @Test
    public void testViewRowCountAfterSilentInsert() {
        model.addRow(new Object[] {rows});
        assertEquals(rows, sorter.getViewRowCount());
    }
    
    /**
     * Core Issue http://forums.sun.com/thread.jspa?messageID=10939199#10939199
     * 
     * Table looses selection of last row if deleted before last.
     * 
     * Here: DefaultRowSorter modelRowCount must be unchanged after silent
     *   model delete.
     */
    @Test
    public void testViewRowCountAfterSilentRemove() {
        model.removeRow(0);
        assertEquals(rows, sorter.getViewRowCount());
    }
    
    
    /**
     * Creates and returns a DefaultTableModel with one column of type Integer, filled
     * with ascending values.
     * 
     * @param rows
     * @return
     */
    private DefaultTableModel createAscendingTableModel(int rows) {
        DefaultTableModel model = new DefaultTableModel(rows, 1) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return Integer.class;
            }
            
        };
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(i, i, 0);
        }
        return model;
    }
    
    @Override
    @Before
    public void setUp() throws Exception {
        // TODO Auto-generated method stub
        super.setUp();
        rows = 10;
        model = createAscendingTableModel(rows);
        sorter = new TableRowSorter<TableModel>(model);
    }

    
}
