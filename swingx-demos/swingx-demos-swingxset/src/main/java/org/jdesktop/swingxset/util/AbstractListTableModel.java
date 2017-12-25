/*
 * $Id: AbstractListTableModel.java 4097 2011-11-30 19:22:13Z kschaefe $
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
package org.jdesktop.swingxset.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 * <p>
 * A table model for displaying a list of items. The items are typically data classes. In such
 * cases, the table columns represent data fields from the item.
 * </p>
 * 
 * <pre>
 * public class SimpleExampleModel extends AbstractListTableModel&lt;ExampleItem&gt; {
 *     public int getColumnCount() {
 *         return numberOfDataFields;
 *     }
 *     
 *     public Object getValueAt(int rowIndex, int columnIndex) {
 *         ExampleItem item = getRow(rowIndex);
 *         Object result = null;
 *         
 *         switch (columnIndex) {
 *         case 0:
 *             result = item.getSomeField();
 *             break;
 *         case 1:
 *             result = item.getSomeOtherField();
 *             break;
 *             ...
 *         default:
 *             //does nothing
 *             break;
 *         }
 *         
 *         return result;
 *     }
 * }
 * </pre>
 * 
 * @param <T>
 *            the type for the rows of the table.
 */
@SuppressWarnings("serial")
public abstract class AbstractListTableModel<T> extends AbstractTableModel {
    /**
     * The row data for this table model.
     */
    protected List<T> data;

    /**
     * Creates an {@code AbstractListTableModel}.
     */
    public AbstractListTableModel() {
        this.data = new ArrayList<T>();
    }
    
    /**
     * {@inheritDoc}
     */
    public final int getRowCount() {
        return data.size();
    }

    /**
     * Inserts a row at the specified index.
     * 
     * @param rowIndex
     *            the position to place the new row.
     * @param row
     *            the row data to insert.
     */
    public void insertRow(int rowIndex, T row) {
        data.add(rowIndex, row);
    
        fireTableRowsInserted(rowIndex, rowIndex);
    }

    /**
     * Inserts a group of rows at the specified index.
     * 
     * @param rowIndex
     *            the position to place the new row.
     * @param rows
     *            the row data to insert.
     */
    public void insertRows(int rowIndex, Collection<T> rows) {
        data.addAll(rowIndex, rows);
    
        fireTableRowsInserted(rowIndex, rowIndex + rows.size() - 1);
    }

    /**
     * Appends a row to the model.
     * 
     * @param row
     *            the row to add.
     */
    public void addRow(T row) {
        int currentSize = data.size();
        
        data.add(row);
    
        fireTableRowsInserted(currentSize, currentSize);
    }

    /**
     * Appends a group of rows to the end of the table.
     * 
     * @param rows
     *            the rows to add.
     */
    public void addRows(Collection<T> rows) {
        int currentSize = data.size();
        
        data.addAll(rows);
    
        fireTableRowsInserted(currentSize, data.size() - 1);
    }

    /**
     * Removes the row at the specified index.
     * 
     * @param rowIndex
     *            the position of the row to delete.
     */
    public void removeRow(int rowIndex) {
        //notify listeners before delete, so they can query the model
        fireTableRowsDeleted(rowIndex, rowIndex);
        
        data.remove(rowIndex);
    }

    /**
     * Remove a range of rows from {@code beginIndex} inclusive to
     * {@code endIndex} exclusive}.
     * 
     * @param beginIndex
     *            the position of the first row to remove.
     * @param endIndex
     *            the position of the last row to remove + 1.
     */
    public void removeRows(int beginIndex, int endIndex) {
        //notify listeners before delete, so they can query the model
        fireTableRowsDeleted(beginIndex, endIndex - 1);
        
        for (int i = endIndex - 1; i >= beginIndex; i--) {
            data.remove(i);
        }
    }

    /**
     * Remove all rows from the table model.
     * <p>
     * This implementation defers to {@code removeRows(int, int)}.
     */
    public void removeAllRows() {
        if (data.size() > 0) {
            removeRows(0, data.size());
        }
    }

    /**
     * Obtains the row index of the specified item. If the item is not managed by this model
     * {@code -1} is returned.
     * 
     * @param item
     *            the item to find in the model
     * @return the row index of the item, or {@code -1} if the item is not found
     */
    public final int indexOf(T item) {
        return data.indexOf(item);
    }

    /**
     * Gives access to a specific model entry based on row index.
     * <p>
     * When retrieving this, the {@code JTable.convertRowIndexToModel} should be
     * used to accommodate sorting and filtering.
     * </p>
     * 
     * @param rowIndex
     *            the row index
     * @return instance of data model type corresponding to row index
     * @throws IndexOutOfBoundsException
     *             if {@code column < 0 || column >= getColumnCount()}
     */
    public final T getRow(int rowIndex) {
        return data.get(rowIndex);
    }
}
