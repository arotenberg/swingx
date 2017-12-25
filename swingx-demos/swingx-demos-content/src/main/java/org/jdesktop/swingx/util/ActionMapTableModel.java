/*
 * $Id: ActionMapTableModel.java 4097 2011-11-30 19:22:13Z kschaefe $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.util;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.table.AbstractTableModel;

/**
 * Convenience TableMap showing all entries of a ActionMap.
 * 
 * @author Jeanette Winzenburg
 */
public class ActionMapTableModel extends AbstractTableModel {

    ActionMap actionMap;

    public ActionMapTableModel(ActionMap actionMap) {
        this.actionMap = actionMap;
    }

    public int getRowCount() {
        return actionMap.allKeys().length;
    }

    public int getColumnCount() {
        return 2;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Object key = actionMap.allKeys()[rowIndex];
        Action action = actionMap.get(key);
        switch (columnIndex) {
        case 0:
            return key;
        case 1:
            return action.getValue(Action.NAME);
        case 2:
            return action.getValue(Action.ACTION_COMMAND_KEY);
        default:
            return null;
        }
    }

    public String getColumnName(int column) {
        switch (column) {
        case 0:
            return "Key Name";
        case 1:
            return "Action Name";
        case 2:
            return "Action Command";
        default:
            return "Column " + column;
        }
    }

}