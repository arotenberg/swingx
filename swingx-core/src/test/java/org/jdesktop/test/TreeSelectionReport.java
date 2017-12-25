/*
 * $Id: TreeSelectionReport.java 2944 2008-06-13 14:41:39Z kleopatra $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */


package org.jdesktop.test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

/**
 * A TreeSelectionListener that stores the received TreeSelectionEvents.
 * 
 * 
 */
public class TreeSelectionReport implements TreeSelectionListener {
    
    /**
     * Holds a list of all received PropertyChangeEvents.
     */
    protected List<TreeSelectionEvent> events = Collections.synchronizedList(new LinkedList<TreeSelectionEvent>());
    
//------------------------ implement ListSelectionListener
    
    public void valueChanged(TreeSelectionEvent e) {
        events.add(0, e);
        
    }
    
 
    public void clear() {
        events.clear();
    }
    
    public boolean hasEvents() {
        return !events.isEmpty();
    }
 
    public int getEventCount() {
        return events.size();
    }

    
    public TreeSelectionEvent getLastEvent() {
        return getLastFrom(events);
        
    }
    
    private TreeSelectionEvent getLastFrom(List<TreeSelectionEvent> list) {
        return list.isEmpty()
            ? null : list.get(0);
    }


}
