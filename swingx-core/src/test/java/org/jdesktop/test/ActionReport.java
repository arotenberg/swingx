/*
 * $Id: ActionReport.java 3299 2009-03-16 10:09:00Z kleopatra $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */


package org.jdesktop.test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A ChangeListener that stores the received ChangeEvents.
 * 
 */
public class ActionReport implements ActionListener {
    
    /**
     * Holds a list of all received PropertyChangeEvents.
     */
    protected List<ActionEvent> events = Collections.synchronizedList(new LinkedList<ActionEvent>());
    protected Map<Object, ActionEvent> eventMap = Collections.synchronizedMap(new HashMap<Object, ActionEvent>());
    
//------------------------ implement PropertyChangeListener
    
    public void actionPerformed(ActionEvent evt) {
        events.add(0, evt);
        if (evt != null && evt.getSource() != null) {
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
 
     public ActionEvent getLastEvent() {
        return hasEvents() ?  events.get(0) : null;
    }

     public ActionEvent getEvent(Object source) {
         return eventMap.get(source);
     }

    /**
     * @return the action command of the last event or null 
     *   if no events received.
     */
    public Object getLastActionCommand() {
        return hasEvents() ?  events.get(0).getActionCommand() : null;
    }


}
