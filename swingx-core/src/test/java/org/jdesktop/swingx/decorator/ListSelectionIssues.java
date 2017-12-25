/*
 * $Id: ListSelectionIssues.java 3473 2009-08-27 13:17:10Z kleopatra $
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
package org.jdesktop.swingx.decorator;

import javax.swing.DefaultListSelectionModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;

import junit.framework.TestCase;

import org.jdesktop.test.ListSelectionReport;

/**
 * Test to understand behaviour/expose known issues of 
 * <code>ListSelectionModel</code>.
 * 
 * 
 * @author Jeanette Winzenburg
 */
public class ListSelectionIssues extends TestCase {
    /**
     * sanity: understand DefaultListSelectionModel behaviour.
     * 
     * Is it allowed that event.getFirstIndex < 0? This happens in 
     * table.clearLeadAnchor
     *
     */
    public void testEventsONLeadAnchorAfterClearSelection() {
        DefaultListSelectionModel selectionModel = new DefaultListSelectionModel();
        int selected = 5;
        selectionModel.setSelectionInterval(selected, selected);
        assertEquals(selected, selectionModel.getAnchorSelectionIndex());
        assertEquals(selected, selectionModel.getLeadSelectionIndex());
//        selectionModel.setLeadAnchorNotificationEnabled(false);
        ListSelectionReport report = new ListSelectionReport();
        selectionModel.addListSelectionListener(report);
        // following lines are copied from table.clearLeadAnchor()
//        selectionModel.setValueIsAdjusting(true);
        selectionModel.clearSelection();
        assertEquals(1, report.getEventCount());
        assertTrue(report.getLastEvent(false).getFirstIndex() >= 0);
        report.clear();
        selectionModel.setAnchorSelectionIndex(-1);
        assertEquals(1, report.getEventCount());
        assertTrue(report.getLastEvent(false).getFirstIndex() >= 0);
        report.clear();
        
        selectionModel.setLeadSelectionIndex(-1);
        assertEquals(1, report.getEventCount());
        assertTrue(report.getLastEvent(false).getFirstIndex() >= 0);
        report.clear();
    }


    /**
     * sanity: understand DefaultListSelectionModel behaviour.
     * 
     * Is it allowed that event.getFirstIndex < 0? This happens in 
     * table.clearLeadAnchor
     *
     */
    public void testEventONLeadAnchorAfterClearSelection() {
        DefaultListSelectionModel selectionModel = new DefaultListSelectionModel();
        int selected = 5;
        selectionModel.setSelectionInterval(selected, selected);
        assertEquals(selected, selectionModel.getAnchorSelectionIndex());
        assertEquals(selected, selectionModel.getLeadSelectionIndex());
        selectionModel.setLeadAnchorNotificationEnabled(false);
        ListSelectionReport report = new ListSelectionReport();
        selectionModel.addListSelectionListener(report);
        // following lines are copied from table.clearLeadAnchor()
        selectionModel.setValueIsAdjusting(true);
        selectionModel.clearSelection();
        selectionModel.setAnchorSelectionIndex(-1);
        selectionModel.setLeadSelectionIndex(-1);
        assertEquals("", 0, report.getEventCount(true));
        selectionModel.setValueIsAdjusting(false);
        ListSelectionEvent event = report.getLastEvent(true);  
        assertEquals(5, event.getFirstIndex());
    }

    /**
     * sanity: understand DefaultListSelectionModel behaviour.
     * 
     * behaviour for a single selected and removal of that single selected: 
     * if "last" selected (==lead/anchor) and removed then the
     * selection is empty but lead/anchor are on the new "last" row.
     * 
     * behaviour for single selected and removal of larger interval: 
     * anchor/lead moved down by the amount of the range removed.
     * 
     *
     */
    public void testLeadAnchorAfterClearSelection() {
        ListSelectionModel viewSelectionModel = new DefaultListSelectionModel();
        int selected = 5;
        viewSelectionModel.setSelectionInterval(selected, selected);
        assertEquals(selected, viewSelectionModel.getAnchorSelectionIndex());
        assertEquals(selected, viewSelectionModel.getLeadSelectionIndex());
        viewSelectionModel.clearSelection();
        int anchor = selected;
        assertTrue(viewSelectionModel.isSelectionEmpty());
        assertEquals(anchor, viewSelectionModel.getAnchorSelectionIndex());
        assertEquals(anchor, viewSelectionModel.getLeadSelectionIndex());
        
    }

    /**
     * sanity: understand DefaultListSelectionModel behaviour.
     * 
     * Here: select index 5, remove 5..5 -> lead == 4
     *
     */
    public void testLeadAnchorAfterRemove() {
        ListSelectionModel viewSelectionModel = new DefaultListSelectionModel();
        assertLeadAnchorAfterRemoveAll(viewSelectionModel, 5, 5);
    }

    /**
     * sanity: understand DefaultListSelectionModel behaviour.
     * 
     * 
     * Here: select index 5, remove 0..5 -> lead == -1
     */
    public void testLeadAnchorAfterRemoveAll() {
        ListSelectionModel viewSelectionModel = new DefaultListSelectionModel();
        assertLeadAnchorAfterRemoveAll(viewSelectionModel, 5, 0);
    }

    /**
     * sanity: understand DefaultListSelectionModel behaviour.
     * 
     * 
     * Here: select 0, remove 0..0 --> lead == 0 (expected -1)
     */
    public void testLeadAnchorAfterRemoveAll0() {
        ListSelectionModel viewSelectionModel = new DefaultListSelectionModel();
        assertLeadAnchorAfterRemoveAll(viewSelectionModel, 0, 0);
    }

    /**
     * Asserts lead/anchor after removeIndexInterval.
     * The setup is to select selectedIndex and removes the index interval first..selected.
     *  
     * @param viewSelectionModel
     * @param selected
     * @param firstOfRemoveInterval
     */
    private void assertLeadAnchorAfterRemoveAll(
            ListSelectionModel viewSelectionModel, int selected, int firstOfRemoveInterval) {
        viewSelectionModel.setSelectionInterval(selected, selected);
        assertEquals(selected, viewSelectionModel.getAnchorSelectionIndex());
        assertEquals(selected, viewSelectionModel.getLeadSelectionIndex());
        int length = selected - firstOfRemoveInterval + 1;
        viewSelectionModel.removeIndexInterval(firstOfRemoveInterval, selected);
        int anchor = selected - length;
        assertTrue(viewSelectionModel.isSelectionEmpty());
        assertEquals(anchor, viewSelectionModel.getAnchorSelectionIndex());
        assertEquals(anchor, viewSelectionModel.getLeadSelectionIndex());
    }
    
}
