/*
 * $Id: ListDataReport.java 3373 2009-06-30 14:26:46Z kleopatra $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */


package org.jdesktop.test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * A <code>ListDataListener</code> that stores the received 
 * <code>ListDataEvents</code>.
 * 
 */
public class ListDataReport implements ListDataListener {
    
    /**
     * Holds a list of all canceled events.
     */
    protected List<ListDataEvent> changedEvents = Collections.synchronizedList(new LinkedList<ListDataEvent>());
    protected List<ListDataEvent> addedEvents = Collections.synchronizedList(new LinkedList<ListDataEvent>());
    protected List<ListDataEvent> removedEvents = Collections.synchronizedList(new LinkedList<ListDataEvent>());
    protected List<ListDataEvent> allEvents = Collections.synchronizedList(new LinkedList<ListDataEvent>());

    /**
     * Instantiates a ListDataReport.
     */
    public ListDataReport() {
       this(null); 
    }
    
    /**
     * Instantiates a ListDataReport and registers as listener to the given ListModel.
     */
    public ListDataReport(ListModel model) {
       if (model != null) {
           model.addListDataListener(this);
       }
    }
    
    //  ------------------ implement ListDataListener    

    public void contentsChanged(ListDataEvent e) {
        changedEvents.add(0, e);
        allEvents.add(0, e);
    }


    public void intervalAdded(ListDataEvent e) {
        addedEvents.add(e);
        allEvents.add(0, e);
    }


    public void intervalRemoved(ListDataEvent e) {
        removedEvents.add(e);
        allEvents.add(0, e);
    }
    
    
    
//----------------------- utility methods to access all events    

    public void clear() {
        changedEvents.clear();
        addedEvents.clear();
        removedEvents.clear();
        allEvents.clear();
    }

    public int getEventCount() {
        return allEvents.size();
    }
     
    public boolean hasEvents() {
        return !allEvents.isEmpty();
    }
 
    public ListDataEvent getLastEvent() {
        return allEvents.isEmpty() ? null : allEvents.get(0);
    }

// ------------------ access changed events
    public int getChangedEventCount() {
        return changedEvents.size();
    }
    
    public boolean hasChangedEvents() {
        return !changedEvents.isEmpty();
    }
    
    public ListDataEvent getLastChangedEvent() {
        return changedEvents.isEmpty() ? null : changedEvents.get(0);
    }

// ----------------- access added events
    
    public int getAddedEventCount() {
        return addedEvents.size();
    }
    
    public boolean hasAddedEvents() {
        return !addedEvents.isEmpty();
    }
    
    public ListDataEvent getLastAddedEvent() {
        return addedEvents.isEmpty() ? null : addedEvents.get(0);
    }


// ----------------- removed added events
    
    public int getRemovedEventCount() {
        return removedEvents.size();
    }
    
    public boolean hasRemovedEvents() {
        return !removedEvents.isEmpty();
    }
    
    public ListDataEvent getLastRemovedEvent() {
        return removedEvents.isEmpty() ? null : removedEvents.get(0);
    }

    
}
