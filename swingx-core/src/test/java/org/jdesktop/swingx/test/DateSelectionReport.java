/*
 * $Id: DateSelectionReport.java 2944 2008-06-13 14:41:39Z kleopatra $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */


package org.jdesktop.swingx.test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jdesktop.swingx.calendar.DateSelectionModel;
import org.jdesktop.swingx.event.DateSelectionEvent;
import org.jdesktop.swingx.event.DateSelectionListener;
import org.jdesktop.swingx.event.DateSelectionEvent.EventType;

/**
 * A ChangeListener that stores the received ChangeEvents.
 * 
 */
public class DateSelectionReport implements DateSelectionListener {
    
    /**
     * Holds a list of all received DateSelectionEvents.
     */
    protected List<DateSelectionEvent> events = Collections.synchronizedList(new LinkedList<DateSelectionEvent>());
    
    /**
     * Instantiates a DateSelectionReport.
     */
    public DateSelectionReport() {}

    /**
     * Instantiates a DateSelectionReport and registers itself to the given model.
     * @param model the DateSelectionModel to listen to, must not be null.
     */
    public DateSelectionReport(DateSelectionModel model) {
        model.addDateSelectionListener(this);
    }
    
//------------------------ implement DateSelectionListener
    

    public void valueChanged(DateSelectionEvent evt) {
        events.add(0, evt);
    }

// ------------------- accessors
    
    public int getEventCount() {
        return events.size();
    }
 
    public void clear() {
        events.clear();
    }
    
    public boolean hasEvents() {
        return !events.isEmpty();
    }
 
     public DateSelectionEvent getLastEvent() {
        return hasEvents() ? events.get(0) : null;
    }

    /**
     * @return the EventType of the last event or null if no event received. 
     */
    public EventType getLastEventType() {
        return hasEvents() ? getLastEvent().getEventType() : null;
    }

    public boolean hasEvent(EventType type) {
        for (DateSelectionEvent ev : events) {
            if (ev.getEventType().equals(type)) {
                return true;
            }
        }
        return false;
    }
    
    public int getEventCount(EventType type) {
        int count = 0;
        for (DateSelectionEvent ev : events) {
            if (ev.getEventType().equals(type)) {
                count++;
            }
        }
        return count;
    }
}
