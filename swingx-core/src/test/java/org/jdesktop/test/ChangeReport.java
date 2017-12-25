/*
 * $Id: ChangeReport.java 2944 2008-06-13 14:41:39Z kleopatra $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */


package org.jdesktop.test;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A ChangeListener that stores the received ChangeEvents.
 * 
 */
public class ChangeReport implements ChangeListener {
    
    /**
     * Holds a list of all received PropertyChangeEvents.
     */
    protected List<ChangeEvent> events = Collections.synchronizedList(new LinkedList<ChangeEvent>());
    protected Map<Object, ChangeEvent> eventMap = Collections.synchronizedMap(new HashMap<Object, ChangeEvent>());
    
//------------------------ implement PropertyChangeListener
    
    public void stateChanged(ChangeEvent evt) {
        events.add(0, evt);
        if (evt.getSource() != null) {
            eventMap.put(evt.getSource(), evt);
        }
    }
    
    public int getEventCount() {
        return events.size();
    }
 
    public void clear() {
        events.clear();
        eventMap.clear();
    }
    
    public boolean hasEvents() {
        return !events.isEmpty();
    }
 
     public ChangeEvent getLastEvent() {
        return events.isEmpty()
            ? null
            : (ChangeEvent) events.get(0);
    }

     public ChangeEvent getEvent(Object source) {
         return (ChangeEvent) eventMap.get(source);
     }

}
