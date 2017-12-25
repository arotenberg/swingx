/*
 * Created on 16.04.2009
 *
 */
package org.jdesktop.test;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class FocusEventReport implements FocusListener {

    
    /**
     * Holds a list of all canceled events.
     */
    protected List<FocusEvent> canceledEvents = Collections.synchronizedList(new LinkedList<FocusEvent>());
    protected List<FocusEvent> stoppedEvents = Collections.synchronizedList(new LinkedList<FocusEvent>());
    protected List<FocusEvent> allEvents = Collections.synchronizedList(new LinkedList<FocusEvent>());
    
    
//------------------------ implement FocusListener

    
    public void focusGained(FocusEvent e) {
        canceledEvents.add(0, e);
        allEvents.add(0, e);
    }
    
    
    public void focusLost(FocusEvent e) {
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
 
    public FocusEvent getLastEvent() {
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
    
    public FocusEvent getLastCanceledEvent() {
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
    
    public FocusEvent getLastStoppedEvent() {
        return stoppedEvents.isEmpty()
        ? null
                : stoppedEvents.get(0);
    }

}
