/*
 * $Id: TableModelReport.java 2976 2008-07-04 15:21:12Z kleopatra $
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
package org.jdesktop.test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * TODO add type doc
 * 
 * @author Jeanette Winzenburg
 */
public class TableModelReport implements TableModelListener {

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(TableModelReport.class
            .getName());
    
    List<TableModelEvent> allEvents = Collections.synchronizedList(new LinkedList<TableModelEvent>());
    List<TableModelEvent> updateEvents = Collections.synchronizedList(new LinkedList<TableModelEvent>());
    List<TableModelEvent> insertEvents = Collections.synchronizedList(new LinkedList<TableModelEvent>());
    List<TableModelEvent> deleteEvents = Collections.synchronizedList(new LinkedList<TableModelEvent>());
    private boolean verbose;
    
    /**
     * Instantiates a report.
     */
    public TableModelReport() {
        this(null);
    }
    
    /**
     * Instantiates a report and registers to the given model if it is not null. 
     * 
     * @param model the model to register to 
     */
    public TableModelReport(TableModel model) {
        this(model, false);
    }
    
    /**
     * Instantiates a report and registers to the given model if it is not null. 
     * If verbose, prints the event (use for debugging only).
     * 
     * @param model the model to register to 
     */
    public TableModelReport(TableModel model, boolean verbose) {
        if (model != null) {
            model.addTableModelListener(this);
        }
        this.verbose = verbose;
    }
    
//------------------- TableModelListener    
    public void tableChanged(TableModelEvent e) {
        if (verbose) {
           LOG.info(printEvent(e)); 
        }
        allEvents.add(0, e);
        if (isUpdate(e)) {
            updateEvents.add(0, e);
        } else if (isStructureChanged(e)) {
            // this is effectively a test for null event
            // do nothing for now
        } else if (isDataChanged(e)) {
            // do nothing for now
        } else if (TableModelEvent.DELETE == e.getType()) { 
            deleteEvents.add(0, e);
        } else if (TableModelEvent.INSERT == e.getType()) {
            insertEvents.add(0, e);
        }
    
    }
    
//-------------------- all events access
    
    public void clear() {
        updateEvents.clear();
        deleteEvents.clear();
        insertEvents.clear();
        allEvents.clear();
    }
    
    public boolean hasEvents() {
        return !allEvents.isEmpty();
    }

    /**
     * @return
     */
    public int getEventCount() {
        return allEvents.size();
    }
    
    public TableModelEvent getLastEvent() {
        return allEvents.isEmpty() ? null : allEvents.get(0);
    }

//---------------- update events    
    public boolean hasUpdateEvents() {
        return !updateEvents.isEmpty();
    }

    /**
     * @return
     */
    public int getUpdateEventCount() {
        return updateEvents.size();
    }
    
    public TableModelEvent getLastUpdateEvent() {
        return updateEvents.isEmpty() ? null : updateEvents.get(0);
    }

//  ---------------- insert events    
    public boolean hasInsertEvents() {
        return !insertEvents.isEmpty();
    }

    /**
     * @return
     */
    public int getInsertEventCount() {
        return insertEvents.size();
    }
    
    public TableModelEvent getLastInsertEvent() {
        return insertEvents.isEmpty() ? null : insertEvents.get(0);
    }

//  ---------------- delete events    
    public boolean hasDeleteEvents() {
        return !deleteEvents.isEmpty();
    }

    /**
     * @return
     */
    public int getDeleteEventCount() {
        return deleteEvents.size();
    }
    
    public TableModelEvent getLastDeleteEvent() {
        return deleteEvents.isEmpty() ? null : deleteEvents.get(0);
    }

    
//---------------- utility
 
    /**
     * Convenience method to detect dataChanged table event type.
     * 
     * @param e the event to examine. 
     * @return true if the event is of type dataChanged, false else.
     */
    public boolean isDataChanged(TableModelEvent e) {
        if (e == null) return false;
        return e.getType() == TableModelEvent.UPDATE && 
            e.getFirstRow() == 0 &&
            e.getLastRow() == Integer.MAX_VALUE;
    }
    
    /**
     * Convenience method to detect update table event type.
     * 
     * @param e the event to examine. 
     * @return true if the event is of type update and not dataChanged, false else.
     */
    public boolean isUpdate(TableModelEvent e) {
        if (isStructureChanged(e)) return false;
        return e.getType() == TableModelEvent.UPDATE && 
            e.getLastRow() < Integer.MAX_VALUE;
    }

    /**
     * Convenience method to detect a structureChanged table event type.
     * @param e the event to examine.
     * @return true if the event is of type structureChanged or null, false else.
     */
    public boolean isStructureChanged(TableModelEvent e) {
        return e == null || e.getFirstRow() == TableModelEvent.HEADER_ROW;
    }

    public static String printEvent(TableModelEvent e) {
        if (e == null) return "null";
        StringBuffer buffer = new StringBuffer();
        buffer.append("Type: ");
        buffer.append(typeStrings[e.getType() + 1]);
        buffer.append(", firstRow: ");
        buffer.append(e.getFirstRow());
        buffer.append(", lastRow: ");
        buffer.append(e.getLastRow());
        buffer.append(", column: ");
        buffer.append(e.getColumn());
        return buffer.toString();
    }
    
    private static String[] typeStrings = { "DELETE", "UPDATE", "INSERT" };
}
