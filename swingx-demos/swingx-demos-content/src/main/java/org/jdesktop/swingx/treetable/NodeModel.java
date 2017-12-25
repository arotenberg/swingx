/*
 * Created on 14.07.2008
 *
 */
package org.jdesktop.swingx.treetable;

/**
 * A model for any columnar information of a tree node. It encapsulates
 * the methods which a TreeTableModel must implement in addition to
 * those of a TreeModel. It is used by a TreeTableModelAdapter to
 * expose a TreeModel as a TreeTableModel. <p>
 * 
 * @see org.jdesktop.swingx.treetable.TreeTableModel
 */
public interface NodeModel {

    /**
     * Returns the most specific superclass for all the cell values in the
     * column. This is used by the {@code JXTreeTable} to set up a default
     * renderer and editor for the column.
     * 
     * @param columnIndex
     *            the index of the column
     * @return the common ancestor class of the object values in the model.
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    public Class<?> getColumnClass(int column);

    /**
     * Returns the number of columns in the model. A {@code JXTreeTable} uses
     * this method to determine how many columns it should create and display by
     * default.
     * 
     * @return the number of columns in the model
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount();

    /**
     * Returns the name of the column at {@code columnIndex}. This is used to
     * initialize the table's column header name. Note: this name does not need
     * to be unique; two columns in a table can have the same name.
     * 
     * @param column
     *            the index of the column
     * @return the name of the column
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    public String getColumnName(int column);

    /**
     * Returns the column that is the "tree" column. While it is not required,
     * most implementations will default the first column to be the hierarchical
     * one.
     * 
     * @return the index of the hierarchical column or -1 if no column is the
     *         hierarchical column.
     */
    public int getHierarchicalColumn();
    
    /**
     * Returns the value for the {@code node} at {@code columnIndex}. The
     * {@code node} must be managed by this model. Unamanaged nodes should throw
     * an {@code IllegalArgumentException}.
     * 
     * @param node
     *            the node whose value is to be queried
     * @param column
     *            the column whose value is to be queried
     * @return the value Object at the specified cell
     * @throws IllegalArgumentException
     *             if {@code node} is not managed by this model.
     * @see #setValueAt
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(Object node, int column);

    /**
     * Returns true if the cell for the {@code node} at {@code columnIndex} is
     * editable. Otherwise, {@code setValueAt} on the cell will not change the
     * value of that cell. The {@code node} must be managed by this model.
     * Unamanaged nodes should throw an {@code IllegalArgumentException}.
     * 
     * @param node
     *            the node whose value to be queried
     * @param column
     *            the column whose value to be queried
     * @return true if the cell is editable
     * @throws IllegalArgumentException
     *             if {@code node} is not managed by this model.
     * @see #setValueAt
     * @see javax.swing.table.TableModel#isCellEditable(int, int)
     */
    public boolean isCellEditable(Object node, int column);

    /**
     * Sets the value for the {@code node} at {@code columnIndex} to
     * {@code value}. The {@code node} must be managed by this model.
     * Unamanaged nodes should throw an {@code IllegalArgumentException}.
     * 
     * 
     * @param value
     *            the new value
     * @param node
     *            the node whose value is to be changed
     * @param column
     *            the column whose value is to be changed
     * @throws IllegalArgumentException
     *             if {@code node} is not managed by this model.
     * @see #getValueAt
     * @see #isCellEditable
     * @see javax.swing.table.TableModel#setValueAt(Object, int, int)
     */
    public void setValueAt(Object value, Object node, int column);

}
