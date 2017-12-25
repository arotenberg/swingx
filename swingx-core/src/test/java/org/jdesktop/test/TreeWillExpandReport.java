/*
 * $Id: TreeWillExpandReport.java 3473 2009-08-27 13:17:10Z kleopatra $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */


package org.jdesktop.test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;

/**
 * A CellEditorListener that stores the received ChangeEvents.
 * 
 */
public class TreeWillExpandReport implements TreeWillExpandListener {
    
    /**
     * Holds a list of all canceled events.
     */
    protected List<TreeExpansionEvent> expandedEvents = Collections.synchronizedList(new LinkedList<TreeExpansionEvent>());
    protected List<TreeExpansionEvent> collapsedEvents = Collections.synchronizedList(new LinkedList<TreeExpansionEvent>());
    protected List<TreeExpansionEvent> allEvents = Collections.synchronizedList(new LinkedList<TreeExpansionEvent>());
    private boolean veto;
    
    public TreeWillExpandReport() {
        this(false);
    }
    
/**
     * @param b
     */
    public TreeWillExpandReport(boolean veto) {
        this.veto = veto;
    }

//------------------------ implement TreeWillExpandListener

    
    public void treeWillExpand(TreeExpansionEvent e) throws ExpandVetoException {
        expandedEvents.add(0, e);
        allEvents.add(0, e);
        if (veto) throw new ExpandVetoException(e, "vetoed expand");
    }
    
    
    public void treeWillCollapse(TreeExpansionEvent e) throws ExpandVetoException {
        collapsedEvents.add(0, e);
        allEvents.add(0, e);
        if (veto) throw new ExpandVetoException(e, "vetoed collapse");
        
    }
    
//----------------------- utility methods to access all events    
    public void clear() {
        expandedEvents.clear();
        collapsedEvents.clear();
        allEvents.clear();
    }

    public int getEventCount() {
        return allEvents.size();
    }
     
    public boolean hasEvents() {
        return !allEvents.isEmpty();
    }
 
    public TreeExpansionEvent getLastEvent() {
        return allEvents.isEmpty()
            ? null
            : allEvents.get(0);
    }

//------------------ access canceled events
    public int getExpandedEventCount() {
        return expandedEvents.size();
    }
    
    public boolean hasExpandedEvents() {
        return !expandedEvents.isEmpty();
    }
    
    public TreeExpansionEvent getLastExpandedEvent() {
        return expandedEvents.isEmpty()
        ? null
                : expandedEvents.get(0);
    }

//----------------- access stopped events
    
    public int getCollapsedEventCount() {
        return collapsedEvents.size();
    }
    
    public boolean hasCollapsedEvents() {
        return !collapsedEvents.isEmpty();
    }
    
    public TreeExpansionEvent getLastCollapsedEvent() {
        return collapsedEvents.isEmpty()
        ? null
                : collapsedEvents.get(0);
    }
    
    
}
