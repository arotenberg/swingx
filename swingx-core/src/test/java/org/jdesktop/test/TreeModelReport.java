/*
 * $Id: TreeModelReport.java 4293 2013-04-24 13:36:19Z kleopatra $
 *
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package org.jdesktop.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;

/**
 * TODO add type doc
 * 
 * @author Jeanette Winzenburg
 */
public class TreeModelReport implements TreeModelListener {

    List<TreeModelEvent> allEvents = Collections.synchronizedList(new LinkedList<TreeModelEvent>());
    List<TreeModelEvent> updateEvents = Collections.synchronizedList(new LinkedList<TreeModelEvent>());
    List<TreeModelEvent> insertEvents = Collections.synchronizedList(new LinkedList<TreeModelEvent>());
    List<TreeModelEvent> deleteEvents = Collections.synchronizedList(new LinkedList<TreeModelEvent>());
    List<TreeModelEvent> structureEvents = Collections.synchronizedList(new LinkedList<TreeModelEvent>());
    
    /**
     * Instantiates a report.
     */
    public TreeModelReport() {
        this(null);
    }
    
    

    /**
     * Instantiates a report and registers itself as listener to the given model, 
     * if it is not null.
     * 
     * @param model the model to register as listener to
     */
    public TreeModelReport(TreeModel model) {
        if (model != null) {
            model.addTreeModelListener(this);
        }
    }


//------------------- TableModelListener    
    public void treeNodesChanged(TreeModelEvent e) {
        allEvents.add(0, e);
        updateEvents.add(0, e);
        
    }

    public void treeNodesInserted(TreeModelEvent e) {
        allEvents.add(0, e);
        insertEvents.add(0, e);
    }

    public void treeNodesRemoved(TreeModelEvent e) {
        allEvents.add(0, e);
        deleteEvents.add(0, e);
    }

    public void treeStructureChanged(TreeModelEvent e) {
        allEvents.add(0, e);
        structureEvents.add(0, e);
    }

    
    
//-------------------- all events access
    
    public void clear() {
        updateEvents.clear();
        deleteEvents.clear();
        insertEvents.clear();
        structureEvents.clear();
        allEvents.clear();
    }
    
    public boolean hasEvents() {
        return !allEvents.isEmpty();
    }

    /**
     * @return
     */
    public int getEventCount() {
        return allEvents.size();
    }
    
    public TreeModelEvent getLastEvent() {
        return allEvents.isEmpty() ? null : allEvents.get(0);
    }

//---------------- update events    
    public boolean hasUpdateEvents() {
        return !updateEvents.isEmpty();
    }

    /**
     * @return
     */
    public int getUpdateEventCount() {
        return updateEvents.size();
    }
    
    public TreeModelEvent getLastUpdateEvent() {
        return updateEvents.isEmpty() ? null : updateEvents.get(0);
    }

//  ---------------- insert events    
    public boolean hasInsertEvents() {
        return !insertEvents.isEmpty();
    }

    /**
     * @return
     */
    public int getInsertEventCount() {
        return insertEvents.size();
    }
    
    public TreeModelEvent getLastInsertEvent() {
        return insertEvents.isEmpty() ? null : insertEvents.get(0);
    }

    /**
     * Returns all insertEvents in the order they were received.
     * 
     * @return
     */
    public List<TreeModelEvent> getInsertEvents() {
        if (!hasInsertEvents()) return Collections.emptyList();
        List<TreeModelEvent> inserted = new ArrayList<TreeModelEvent>();
        for (TreeModelEvent treeModelEvent : insertEvents) {
           inserted.add(0, treeModelEvent);  
        }
        return inserted;
    }
//  ---------------- delete events    
    public boolean hasDeleteEvents() {
        return !deleteEvents.isEmpty();
    }

    /**
     * @return
     */
    public int getDeleteEventCount() {
        return deleteEvents.size();
    }
    
    public TreeModelEvent getLastDeleteEvent() {
        return deleteEvents.isEmpty() ? null : deleteEvents.get(0);
    }

//---------------- structure events
    
    public boolean hasStructureEvents() {
        return !structureEvents.isEmpty();
    }

    /**
     * @return
     */
    public int getStructureEventCount() {
        return structureEvents.size();
    }
    
    public TreeModelEvent getLastStructureEvent() {
        return structureEvents.isEmpty() ? null : structureEvents.get(0);
    }


 
}
