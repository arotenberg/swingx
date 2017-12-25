/*
 * $Id: PropertyChangeReport.java 4164 2012-02-14 11:53:10Z kleopatra $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */


package org.jdesktop.test;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A PropertyChangeListener that stores the received PropertyChangeEvents.
 * 
 * modified ("beanified") from JGoodies PropertyChangeReport.
 * 
 */
@SuppressWarnings("nls")
public class PropertyChangeReport implements PropertyChangeListener {
    
    /**
     * Holds a list of all received PropertyChangeEvents.
     */
    protected List<PropertyChangeEvent> events = Collections.synchronizedList(new LinkedList<PropertyChangeEvent>());
    protected Map<String, PropertyChangeEvent> eventMap = Collections.synchronizedMap(new HashMap<String, PropertyChangeEvent>());

    /**
     * Instantiates a PropertyChangeReport.
     */
    public PropertyChangeReport() {
        this(null);
    }
    
    
    /**
     * Instantiates a PropertyChangeReport and registers itself with the given component
     * if that is not null.
     * 
     * @param component the Component to register itself to.
     */
    public PropertyChangeReport(Component component) {
        if(component != null) {
            component.addPropertyChangeListener(this);
        }
    }
//------------------------ implement PropertyChangeListener
    

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        events.add(0, evt);
        if (evt.getPropertyName() != null) {
            eventMap.put(evt.getPropertyName(), evt);
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
 
    public int getEventCount(String property) {
        if (property == null) return getMultiCastEventCount();
        int count = 0;
        for (Iterator<PropertyChangeEvent> iter = events.iterator(); iter.hasNext();) {
            PropertyChangeEvent event = iter.next();
            if (property.equals(event.getPropertyName())) {
                count++;
            }
        }
        return count;
    }

    public boolean hasEvents(String property) {
        return eventMap.get(property) != null;
    }
    
    public int getMultiCastEventCount() {
        int count = 0;
        for (Iterator<PropertyChangeEvent> i = events.iterator(); i.hasNext();) {
            PropertyChangeEvent event =  i.next();
            if (event.getPropertyName() == null)
                count++;
        }
        return count;
    }
    
    public int getNamedEventCount() {
        return getEventCount() - getMultiCastEventCount();
    }
    
    public PropertyChangeEvent getLastEvent() {
        return events.isEmpty()
            ? null
            : events.get(0);
    }

    public PropertyChangeEvent getLastEvent(String property) {
        return eventMap.get(property);
    }
    
    public Object getLastOldValue() {
        PropertyChangeEvent last = getLastEvent();
        return last != null ? last.getOldValue() : null;
    }
    
    public Object getLastNewValue() {
        PropertyChangeEvent last = getLastEvent();
        return last != null ? last.getNewValue() : null;
    }
 
    /**
     * @return the propertyName of the last event or 
     *    null if !hasEvents().
     */
    public String getLastProperty() {
        PropertyChangeEvent last = getLastEvent();
        return last != null ? last.getPropertyName() : null;
    }
    /**
     * @return the source of the last event
     */
    public Object getLastSource() {
        PropertyChangeEvent last = getLastEvent();
        return last != null ? last.getSource() : null;
    }
    
    public Object getLastOldValue(String property) {
        PropertyChangeEvent last = getLastEvent(property);
        return last != null ? last.getOldValue() : null;
    }
    
    public Object getLastNewValue(String property) {
        PropertyChangeEvent last = getLastEvent(property);
        return last != null ? last.getNewValue() : null;
    }
    
    /**
     * PRE: hasEvents()
     * @return the last old value as a boolean
     */
    public boolean getLastOldBooleanValue() {
        return ((Boolean) getLastOldValue()).booleanValue();
    }

    /**
     * PRE: hasEvents()
     * @return the last new value as a boolean
     */
    public boolean getLastNewBooleanValue() {
        return ((Boolean) getLastNewValue()).booleanValue();
    }

    /**
     * @return a list of all of the event names captured by this reporter
     */
    public String getEventNames() {
        StringBuffer buffer = new StringBuffer();
        for (PropertyChangeEvent event : events) {
            buffer.append(event.getPropertyName());
            buffer.append(" :: ");
        }
        return buffer.toString();
    }


}
