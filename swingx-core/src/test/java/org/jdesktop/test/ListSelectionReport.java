/*
 * $Id: ListSelectionReport.java 2944 2008-06-13 14:41:39Z kleopatra $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */


package org.jdesktop.test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * A PropertyChangeListener that stores the received PropertyChangeEvents.
 * 
 * modified ("beanified") from JGoodies PropertyChangeReport.
 * 
 */
public class ListSelectionReport implements ListSelectionListener {
    
    /**
     * Holds a list of all received PropertyChangeEvents.
     */
    protected List<ListSelectionEvent> events = Collections.synchronizedList(new LinkedList<ListSelectionEvent>());
    protected List<ListSelectionEvent> notAdjustingEvents = Collections.synchronizedList(new LinkedList<ListSelectionEvent>());
    
//------------------------ implement ListSelectionListener
    
    public void valueChanged(ListSelectionEvent e) {
        events.add(0, e);
        if (!e.getValueIsAdjusting()) {
            notAdjustingEvents.add(0, e);
        }
        
    }
    
    public int getEventCount() {
        return getEventCount(false);
    }
 
    public void clear() {
        events.clear();
        notAdjustingEvents.clear();
    }
    
    public boolean hasEvents() {
        return !events.isEmpty();
    }
 
    public int getEventCount(boolean notAdjusting) {
        if (notAdjusting) {
            return notAdjustingEvents.size();
        }
        return events.size();
    }

    
    public ListSelectionEvent getLastEvent(boolean notAdjusting) {
        if (notAdjusting) {
            return getLastFrom(events);
        }
        return getLastFrom(notAdjustingEvents);
        
    }
    
    private ListSelectionEvent getLastFrom(List<ListSelectionEvent> list) {
        return list.isEmpty()
            ? null : list.get(0);
    }


}
