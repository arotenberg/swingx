/*
 * $Id: CellEditorReport.java 2944 2008-06-13 14:41:39Z kleopatra $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */


package org.jdesktop.test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;

/**
 * A CellEditorListener that stores the received ChangeEvents.
 * 
 */
public class CellEditorReport implements CellEditorListener {
    
    /**
     * Holds a list of all canceled events.
     */
    protected List<ChangeEvent> canceledEvents = Collections.synchronizedList(new LinkedList<ChangeEvent>());
    protected List<ChangeEvent> stoppedEvents = Collections.synchronizedList(new LinkedList<ChangeEvent>());
    protected List<ChangeEvent> allEvents = Collections.synchronizedList(new LinkedList<ChangeEvent>());
    
    
//------------------------ implement CellEditorListener

    
    public void editingCanceled(ChangeEvent e) {
        canceledEvents.add(0, e);
        allEvents.add(0, e);
    }
    
    
    public void editingStopped(ChangeEvent e) {
        stoppedEvents.add(0, e);
        allEvents.add(0, e);
        
    }
    
//----------------------- utility methods to access all events    
    public void clear() {
        canceledEvents.clear();
        stoppedEvents.clear();
        allEvents.clear();
    }

    public int getEventCount() {
        return allEvents.size();
    }
     
    public boolean hasEvents() {
        return !allEvents.isEmpty();
    }
 
    public ChangeEvent getLastEvent() {
        return allEvents.isEmpty()
            ? null
            : allEvents.get(0);
    }

//------------------ access canceled events
    public int getCanceledEventCount() {
        return canceledEvents.size();
    }
    
    public boolean hasCanceledEvents() {
        return !canceledEvents.isEmpty();
    }
    
    public ChangeEvent getLastCanceledEvent() {
        return canceledEvents.isEmpty()
        ? null
                : canceledEvents.get(0);
    }

//----------------- access stopped events
    
    public int getStoppedEventCount() {
        return stoppedEvents.size();
    }
    
    public boolean hasStoppedEvents() {
        return !stoppedEvents.isEmpty();
    }
    
    public ChangeEvent getLastStoppedEvent() {
        return stoppedEvents.isEmpty()
        ? null
                : stoppedEvents.get(0);
    }
    
    
}
