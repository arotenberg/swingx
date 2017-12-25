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

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.InteractiveTestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests the expose (mis-)behaviour of DefaultSortController. Mainly the aspect
 * that it is not shareable across multiple views (same as core DefaultRowSorter).
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class DefaultSortControllerIssues extends InteractiveTestCase {

    int rows;
    DefaultTableModel model;
    DefaultSortController<TableModel> sorter;

    /**
     * RowSorter not shareable!
     */
    @Test
    public void testShareSorter() {
        sorter.toggleSortOrder(0);
        int last = model.getRowCount() - 1;
        model.removeRow(0);
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
        sorter = new TableSortController<TableModel>(model);
    }

    
}
