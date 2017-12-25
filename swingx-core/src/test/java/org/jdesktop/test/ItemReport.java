/*
 * $Id: ItemReport.java 3336 2009-04-28 11:02:58Z kleopatra $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */


package org.jdesktop.test;

import java.awt.ItemSelectable;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A ItemListener that stores the received ItemEvents.
 * 
 */
public class ItemReport implements ItemListener {
    
    /**
     * Holds a list of all selected events.
     */
    protected List<ItemEvent> selectedEvents = Collections.synchronizedList(new LinkedList<ItemEvent>());
    /**
     * Holds a list of all deselected events.
     */
    protected List<ItemEvent> deselectedEvents = Collections.synchronizedList(new LinkedList<ItemEvent>());
    /**
     * Holds a list of all events.
     */
    protected List<ItemEvent> allEvents = Collections.synchronizedList(new LinkedList<ItemEvent>());
    
    /**
     * Instantiates a report.
     */
    public ItemReport() {
       this(null); 
    }
    
    /**
     * Instantiates a report and registers itself as listener to the given model, 
     * if it is not null.
     * 
     * @param model the model to register as listener to
     */
    public ItemReport(ItemSelectable model) {
       if (model != null) {
           model.addItemListener(this);
       }
    }
    
//------------------------ implement CellEditorListener

    public void itemStateChanged(ItemEvent e) {
        allEvents.add(e);
        if (ItemEvent.SELECTED == e.getStateChange()) {
            selectedEvents.add(e);
        } else if (ItemEvent.DESELECTED == e.getStateChange()){
            deselectedEvents.add(e);
        } else {
            // ItemEvent doesn't guard against illegal states, can't do anything
        }
        
    }
    
//----------------------- utility methods to access all events    
    public void clear() {
        selectedEvents.clear();
        deselectedEvents.clear();
        allEvents.clear();
    }

    public int getEventCount() {
        return allEvents.size();
    }
     
    public boolean hasEvents() {
        return !allEvents.isEmpty();
    }
 
    public ItemEvent getLastEvent() {
        return allEvents.isEmpty()
            ? null
            : allEvents.get(0);
    }

//------------------ access canceled events
    public int getSelectedEventCount() {
        return selectedEvents.size();
    }
    
    public boolean hasSelectedEvents() {
        return !selectedEvents.isEmpty();
    }
    
    public ItemEvent getLastSelectedEvent() {
        return selectedEvents.isEmpty()
        ? null
                : selectedEvents.get(0);
    }

//----------------- access stopped events
    
    public int getDeselectedEventCount() {
        return deselectedEvents.size();
    }
    
    public boolean hasDeselectedEvents() {
        return !deselectedEvents.isEmpty();
    }
    
    public ItemEvent getLastDeselectedEvent() {
        return deselectedEvents.isEmpty()
        ? null
                : deselectedEvents.get(0);
    }
    
    
}
