/*
 * Created on 08.05.2008
 *
 */
package org.jdesktop.swingx.binding;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.jdesktop.beans.AbstractBean;

public class TableRowCountAdapter extends AbstractBean {

    
    private JTable table;
    private TableModelListener tableModelListener;
    private PropertyChangeListener tableListener;
    
    public TableRowCountAdapter(JTable table) {
        this.table = table;
        table.getModel().addTableModelListener(getTableModelListener());
        table.addPropertyChangeListener(getTableListener());
    }

    private PropertyChangeListener getTableListener() {
        if (tableListener == null) {
            tableListener = new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent evt) {
                    if (!"model".equals(evt.getPropertyName())) return;
                    reinstallTableModelListener((TableModel) evt.getOldValue());
                }
                
            };
        }
        return tableListener;
    }

    protected void reinstallTableModelListener(TableModel oldValue) {
        oldValue.removeTableModelListener(getTableModelListener());
        table.getModel().addTableModelListener(getTableModelListener());
        
    }

    public int getRowCount() {
        return table.getRowCount();
    }
    
    private TableModelListener getTableModelListener() {
        if (tableModelListener == null) {
            tableModelListener = new TableModelListener() {

                public void tableChanged(TableModelEvent e) {
                    if (isModification(e)) {
                        firePropertyChange("rowCount", -1, table.getRowCount());
                    }
                    
                }

                private boolean isModification(TableModelEvent e) {
                    if (isStructureChanged(e) || (e.getType() == TableModelEvent.INSERT)
                            || (e.getType() == TableModelEvent.DELETE)) {
                        return true;
                    }
                    return false;
                }

                private boolean isStructureChanged(TableModelEvent e) {
                    return e == null || e.getFirstRow() == TableModelEvent.HEADER_ROW;
                }
                
            };
        }
        return tableModelListener;
    }

}
