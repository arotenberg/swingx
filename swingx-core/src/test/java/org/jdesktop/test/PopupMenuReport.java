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

import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * A <code>ListDataListener</code> that stores the received 
 * <code>ListDataEvents</code>.
 * 
 */
public class PopupMenuReport implements PopupMenuListener {
    
    /**
     * Holds a list of all canceled events.
     */
    protected List<PopupMenuEvent> canceldEvents = Collections.synchronizedList(new LinkedList<PopupMenuEvent>());
    protected List<PopupMenuEvent> visibleEvents = Collections.synchronizedList(new LinkedList<PopupMenuEvent>());
    protected List<PopupMenuEvent> invisibleEvents = Collections.synchronizedList(new LinkedList<PopupMenuEvent>());
    protected List<PopupMenuEvent> allEvents = Collections.synchronizedList(new LinkedList<PopupMenuEvent>());

    /**
     * Instantiates a ListDataReport.
     */
    public PopupMenuReport() {
       this(null); 
    }
    
    /**
     * Instantiates a ListDataReport and registers as listener to the given ListModel.
     */
    public PopupMenuReport(JPopupMenu model) {
       if (model != null) {
           model.addPopupMenuListener(this);
       }
    }
    
    //  ------------------ implement ListDataListener    

    public void popupMenuCanceled(PopupMenuEvent e) {
        canceldEvents.add(0, e);
        allEvents.add(0, e);
    }


    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        visibleEvents.add(e);
        allEvents.add(0, e);
    }


    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        invisibleEvents.add(e);
        allEvents.add(0, e);
    }
    
    
    
//----------------------- utility methods to access all events    

    public void clear() {
        canceldEvents.clear();
        visibleEvents.clear();
        invisibleEvents.clear();
        allEvents.clear();
    }

    public int getEventCount() {
        return allEvents.size();
    }
     
    public boolean hasEvents() {
        return !allEvents.isEmpty();
    }
 
    public PopupMenuEvent getLastEvent() {
        return allEvents.isEmpty() ? null : allEvents.get(0);
    }

// ------------------ access changed events
    public int getCanceledEventCount() {
        return canceldEvents.size();
    }
    
    public boolean hasCanceledEvents() {
        return !canceldEvents.isEmpty();
    }
    
    public PopupMenuEvent getLastCanceledEvent() {
        return canceldEvents.isEmpty() ? null : canceldEvents.get(0);
    }

// ----------------- access added events
    
    public int getVisibleEventCount() {
        return visibleEvents.size();
    }
    
    public boolean hasVisibleEvents() {
        return !visibleEvents.isEmpty();
    }
    
    public PopupMenuEvent getLastVisibleEvent() {
        return visibleEvents.isEmpty() ? null : visibleEvents.get(0);
    }


// ----------------- removed added events
    
    public int getInvisibleEventCount() {
        return invisibleEvents.size();
    }
    
    public boolean hasInvisibleEvents() {
        return !invisibleEvents.isEmpty();
    }
    
    public PopupMenuEvent getLastInvisibleEvent() {
        return invisibleEvents.isEmpty() ? null : invisibleEvents.get(0);
    }

    
}
