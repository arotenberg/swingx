/*
 * $Id: ColumnModelReport.java 3397 2009-07-22 08:20:13Z kleopatra $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.test;

import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.table.TableColumnModel;

import org.jdesktop.swingx.event.TableColumnModelExtListener;
import org.jdesktop.test.PropertyChangeReport;

/**
 * A TableColumnExtListener that stores the received TableColumnEvents and
 * PropertyChangeEvents originating from the TableColumns contained in the
 * TableColumnModel.
 */
public class ColumnModelReport implements TableColumnModelExtListener {

    /**
     * Holds a list of all received ValueChangeEvents.
     */
    private List<TableColumnModelEvent> removedEvents = Collections.synchronizedList(new LinkedList<TableColumnModelEvent>());
    private List<TableColumnModelEvent> addedEvents = Collections.synchronizedList(new LinkedList<TableColumnModelEvent>());
    private List<TableColumnModelEvent> movedEvents = Collections.synchronizedList(new LinkedList<TableColumnModelEvent>());
    
    private List<ListSelectionEvent> selectionEvents = Collections.synchronizedList(new LinkedList<ListSelectionEvent>());
    private List<ChangeEvent> changeEvents = Collections.synchronizedList(new LinkedList<ChangeEvent>());
    
//    private List<PropertyChangeEvent> columnPropertyEvents = new LinkedList<PropertyChangeEvent>();

    private PropertyChangeReport propertyReport = new PropertyChangeReport();
    
    public ColumnModelReport() {
        this(null);
    }
    
    public ColumnModelReport(TableColumnModel model) {
        if (model != null) {
            model.addColumnModelListener(this);
        }
    }
    
//------------------------ implement TableColumnModelListener    
    public void columnAdded(TableColumnModelEvent e) {
        addedEvents.add(0, e);

    }
    public void columnMarginChanged(ChangeEvent e) {
        changeEvents.add(0, e);

    }
    public void columnMoved(TableColumnModelEvent e) {
        movedEvents.add(0, e);

    }
    public void columnRemoved(TableColumnModelEvent e) {
        removedEvents.add(0, e);

    }
    public void columnSelectionChanged(ListSelectionEvent e) {
        selectionEvents.add(0, e);

    }
    //---------------------- implement TableColumnModelExtListener


    public void columnPropertyChange(PropertyChangeEvent e) {
        propertyReport.propertyChange(e);
        
    }

    public boolean hasEvents() {
        return getEventCount() > 0;
    }


    public void clear() {
        addedEvents.clear();
        removedEvents.clear();
        movedEvents.clear();
        changeEvents.clear();
        selectionEvents.clear();
        propertyReport.clear();
    }

    public int getEventCount() {
        return addedEvents.size() + removedEvents.size() + movedEvents.size() +
          changeEvents.size() + selectionEvents.size() + propertyReport.getEventCount();
    }

    // -------------- access reported TableModelEvents
    
    public boolean hasRemovedEvent() {
        return !removedEvents.isEmpty();
    }
    
    public TableColumnModelEvent getLastRemoveEvent() {
        return removedEvents.isEmpty() ? null : removedEvents.get(0);
     }

    public boolean hasAddedEvent() {
        return !addedEvents.isEmpty();
    }
    
    public TableColumnModelEvent getLastAddEvent() {
        return addedEvents.isEmpty() ? null : addedEvents.get(0);
     }

    public boolean hasChangeEvent() {
        return !changeEvents.isEmpty();
    }
    
    public int getChangeEventCount() {
        return changeEvents.size();
    }
    
    public ChangeEvent getLastChangeEvent() {
        return hasChangeEvent() ? changeEvents.get(0) : null;
    }
    
    public boolean hasSelectionEvent() {
        return !selectionEvents.isEmpty();
    }
    
    public int getSelectionEventCount() {
        return selectionEvents.size();
    }
    
    public ListSelectionEvent getLastSelectionEvent() {
        return hasSelectionEvent() ? selectionEvents.get(0) : null;
    }
    //--------------- access reported propertyChangeEvent
    
    public boolean hasColumnPropertyEvent() {
        return propertyReport.hasEvents();
    }
    
    public int getColumnPropertyEventCount() {
        return propertyReport.getEventCount();
    }
    
    public PropertyChangeEvent getLastColumnPropertyEvent() {
        return propertyReport.getLastEvent();
    }
    
    public int getColumnPropertyEventCount(String property) {
        return propertyReport.getEventCount(property);
    }
    
    public Object getLastColumnPropertyEvent(String property) {
        return propertyReport.getLastEvent(property);
    }
    
    public PropertyChangeReport getPropertyChangeReport() {
        return propertyReport;
    }
}