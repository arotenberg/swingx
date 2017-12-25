/*
 * $Id$
 *
 * Copyright 2009 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx.plaf.basic.core;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.DefaultRowSorter;
import javax.swing.ListModel;
import javax.swing.RowFilter;
import javax.swing.SortOrder;
import javax.swing.RowFilter.Entry;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXEditorPaneTest;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXListSortRevamp.DefaultListModelF;
import org.jdesktop.swingx.hyperlink.LinkModel;
import org.jdesktop.swingx.plaf.basic.core.ListSortUI.ModelChange;
import org.jdesktop.swingx.sort.ListSortController;
import org.jdesktop.swingx.sort.RowFilters;
import org.jdesktop.swingx.sort.TableSortController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for SortManager (mainly to understand).
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ListSortUITest extends InteractiveTestCase {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(ListSortUITest.class
            .getName());
    
    protected DefaultListModelF ascendingListModel;
    private JXList list;
    private ListSortController<ListModel> controller;
//    private ListSortUI sortUI;
    private int testRow;

    
    /**
     * Issue #1536-swingx: AIOOB on restoring selection with filter
     * Reopened: overfixed - the removeIndexInterval _does_ take 
     * the endIndex instead of length.
     * 
     */
    @Test
    public void testSelectionWithFilterXListRemove() {
        JXList table = new JXList(ascendingListModel, true);
        // set selection somewhere below the filtered (which is 0)
        int selected = 1;
        table.setSelectionInterval(selected, selected);
        // exclude rows based on identifier (here: first item
        final RowFilter filter = new RowFilters.GeneralFilter() {
            
            List excludes = Arrays.asList(0);
            @Override
            protected boolean include(
                    Entry<? extends Object, ? extends Object> entry,
                    int index) {
                return !excludes.contains(entry.getIdentifier());
            }
            
        };
        table.setRowFilter(filter);
        assertEquals("sanity: filtered selection", selected - 1, table.getSelectedIndex());
        // remove last row
        ascendingListModel.remove(ascendingListModel.getSize() - 1);
        assertEquals("filtered selection unchanged", selected - 1, table.getSelectedIndex());
        assertFalse(table.isSelectionEmpty());
    }
    /**
     * Issue #1536-swingx: AIOOB on restoring selection with filter
     * This is a core issue, sneaked into ListSortUI by c&p
     * 
     */
    @Test
    public void testSelectionWithFilterXListInsert() {
        DefaultListModel model = new DefaultListModel();
        // a model with 3 elements is the minimum size to demonstrate
        // the bug
        int last = 2;
        for (int i = 0; i <= last; i++) {
            model.addElement(i);
        }
        JXList table = new JXList(model, true);
        // set selection to the end
        table.setSelectionInterval(last, last);
        // exclude rows based on identifier
        final RowFilter filter = new RowFilters.GeneralFilter() {
            
            List excludes = Arrays.asList(0);
            @Override
            protected boolean include(
                    Entry<? extends Object, ? extends Object> entry,
                    int index) {
                return !excludes.contains(entry.getIdentifier());
            }
            
        };
        table.setRowFilter(filter);
        // insertRow _before or at_ selected model index, such that
        // endIndex (in event) > 1
        model.add(2, "x");
    }

    @Test(expected = IllegalStateException.class)
    public void testConstructorDifferentSorter() {
        new ListSortUI(new JXList(true), new ListSortController<ListModel>(ascendingListModel));
    }
    
    @Test(expected = NullPointerException.class)
    public void testConstructorNullSorter() {
        new ListSortUI(new JXList(), null);
    }
    
    @Test(expected = NullPointerException.class)
    public void testConstructorNullList() {
        new ListSortUI(null, new ListSortController<ListModel>(ascendingListModel));
    }
    
    @Test
    public void testSetFilterKeepsSelection() {
        int selection = 0;
        list.setSelectedIndex(selection);
        RowFilter<Object, Integer> filter = RowFilters.regexFilter(".*", 0);
        controller.setRowFilter(filter);
        assertEquals("setting filters must keep selection", selection, list.getSelectedIndex());
    }


    /**
     * Issue #223
     * test if selection is updated on add row above selection.
     *
     */
    @Test
    public void testAddRowAboveSelectionInvertedOrder() {
        // select the last row in view coordinates
        int selectedRow = list.getElementCount() - 2;
        list.setSelectedIndex(selectedRow);
        // revert order 
        list.setSortOrder(SortOrder.DESCENDING);
        assertEquals("second row must be selected", 1, list.getSelectedIndex());
        // add row in model coordinates
        // insert high value
        Object row = new Integer(100);
        ascendingListModel.addElement(row);
        assertEquals(row, list.getElementAt(0));
        // selection must be moved one below
        assertEquals("selection must be incremented by one ", 2, list.getSelectedIndex());
    }
    
    /**
     * Issue #855-swingx: throws AIOOB on repeated remove/add.
     * Reason is that the lead/anchor is not removed in removeIndexInterval
     */
    @Test
    public void testAddRemoveSelect() {
        list.setSortOrder(SortOrder.ASCENDING);
        list.setSelectedIndex(0);
        ascendingListModel.remove(0);
        assertTrue("sanity - empty selection after remove", list.isSelectionEmpty());
        ascendingListModel.addElement(-1);
        assertTrue("sanity - empty selection re-adding", list.isSelectionEmpty());
        list.setSelectedIndex(0);
    }
    
    
    /**
     * 
     * Issue #173-swingx.
     * 
     * table.setFilters() leads to selectionListener
     * notification while internal table state not yet stable.
     * 
     * example (second one, from Nicola):
     * http://www.javadesktop.org/forums/thread.jspa?messageID=117814
     *
     */
    @Test
    public void testSelectionListenerNotification() {
        assertEquals(20, list.getElementCount());
        final int modelRow = 0;
        // set a selection 
        list.setSelectedIndex(modelRow);
        ListSelectionListener l = new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) return;
                int viewRow = list.getSelectedIndex(); 
                assertEquals("view index visible", 0, viewRow);
                // JW: the following checks if the reverse conversion succeeds
                list.convertIndexToModel(viewRow);
                
            }
            
        };
        list.getSelectionModel().addListSelectionListener(l);
        RowFilter<ListModel, Integer> filter = RowFilters.regexFilter("0", 0);
        ((ListSortController<? extends ListModel>) list.getRowSorter()).setRowFilter(filter);
        assertEquals(2, list.getElementCount());
    }


    @Test
    public void testSelectionAfterSort() {
        // use the 2 to be sure the comparable is used
        list.setSelectedIndex(testRow);
        list.setSortOrder(SortOrder.DESCENDING);
        int index = list.getSelectedIndex();
        assertEquals("last row must be selected after sorting", 
                ascendingListModel.getSize() - (testRow + 1) , index);
        
    }

    /**
     * Issue #477-swingx:
     * 
     * Selection must be cleared after setModel. This is from
     * super's contract.
     *
     */
    @Test
    public void testSetModelEmptySelection() {
        final JXList list = new JXList(new DefaultListModel(), true);
        int selection = 0;
        list.setSelectedIndex(selection);
        list.setModel(ascendingListModel);
        assertTrue("setting model must clear selectioon", list.isSelectionEmpty());
        assertEquals(ascendingListModel.getSize(), list.getElementCount());
    }
    /**
     * Selection must be cleared after dataChanged. 
     *
     */
    @Test
    public void testDataChangedEmptySelection() {
        final JXList list = new JXList(ascendingListModel, true);
        int selection = 0;
        list.setSelectedIndex(selection);
        ascendingListModel.fireContentsChanged();
        assertTrue("dataChanged must clear selection", list.isSelectionEmpty());
    }
    /**
     * Selection must be cleared after dataChanged. 
     *
     */
    @Test
    public void testDataChangedSortedEmptySelection() {
        final JXList list = new JXList(ascendingListModel, true);
        int selection = 0;
        list.setSelectedIndex(selection);
        list.setSortOrder(SortOrder.DESCENDING);
        ascendingListModel.fireContentsChanged();
        assertTrue("dataChanged must clear selection", list.isSelectionEmpty());
    }
    
    /**
     * test if selection is kept after deleting a row above the
     * selected.
     * 
     * This fails because the ui-delegate has its hands in removing
     * selection after removed, that is they are doubly removed.
     *
     */
    @Test
    public void testSelectionAfterAddAbove() {
        // selecte second row
        list.setSelectedIndex(1);
        // remove first 
        ascendingListModel.insertElementAt(5, 0);
        assertEquals("selected must have moved after adding at start", 
                2, list.getSelectedIndex());
    }
    
    /**
     * test if selection is kept after deleting a row above the
     * selected.
     * 
     * This fails because the ui-delegate has its hands in removing
     * selection after removed, that is they are doubly removed.
     *
     */
    @Test
    public void testSelectionAfterDeleteAbove() {
        // selecte second row
        list.setSelectedIndex(1);
        // remove first 
        ascendingListModel.remove(0);
        assertEquals("first row must be selected removing old first", 
                0, list.getSelectedIndex());
    }
    


//------------------- ModelChange - temporary ...    
    @Test
    public void testModelAdded() {
        int first = 3;
        int last = 5;
        ListDataEvent e = new ListDataEvent(new DefaultListModel(), ListDataEvent.INTERVAL_ADDED, last, first);
        ModelChange change = new ModelChange(e);
        assertEquals(ListDataEvent.INTERVAL_ADDED, change.type);
        assertFalse(change.allRowsChanged);
        assertEquals(last-first +1, change.length);
        assertEquals(first, change.startModelIndex);
        assertEquals(last, change.endModelIndex);
    }
    @Test
    public void testModelRemoved() {
        int first = 3;
        int last = 5;
        ListDataEvent e = new ListDataEvent(new DefaultListModel(), ListDataEvent.INTERVAL_REMOVED, last, first);
        ModelChange change = new ModelChange(e);
        assertEquals(ListDataEvent.INTERVAL_REMOVED, change.type);
        assertFalse(change.allRowsChanged);
        assertEquals(last-first +1, change.length);
        assertEquals(first, change.startModelIndex);
        assertEquals(last, change.endModelIndex);
    }
    @Test
    public void testModelChanged() {
        int first = 3;
        int last = 5;
        ListDataEvent e = new ListDataEvent(new DefaultListModel(), ListDataEvent.CONTENTS_CHANGED, last, first);
        ModelChange change = new ModelChange(e);
        assertEquals(ListDataEvent.CONTENTS_CHANGED, change.type);
        assertFalse(change.allRowsChanged);
        assertEquals(last-first +1, change.length);
        assertEquals(first, change.startModelIndex);
        assertEquals(last, change.endModelIndex);
    }
    @Test
    public void testModelAllChanged() {
        int first = -1;
        int last = -1;
        ListDataEvent e = new ListDataEvent(new DefaultListModel(), ListDataEvent.CONTENTS_CHANGED, last, first);
        ModelChange change = new ModelChange(e);
        assertEquals(ListDataEvent.CONTENTS_CHANGED, change.type);
        assertTrue(change.allRowsChanged);
        assertEquals(-1, change.startModelIndex);
        assertEquals(-1, change.endModelIndex);
        assertEquals(-1, change.length);
    }
    
  //-------------------- factory methods, setup    
    protected ListModel createListModel() {
        JXList list = new JXList();
        return new DefaultComboBoxModel(list.getActionMap().allKeys());
    }

    protected DefaultListModelF createAscendingListModel(int startRow, int count) {
        DefaultListModelF l = new DefaultListModelF();
        for (int row = startRow; row < startRow  + count; row++) {
            l.addElement(new Integer(row));
        }
        return l;
    }
    protected DefaultListModel createListModelWithLinks() {
        DefaultListModel model = new DefaultListModel();
        for (int i = 0; i < 20; i++) {
            try {
                LinkModel link = new LinkModel("a link text " + i, null, new URL("http://some.dummy.url" + i));
                if (i == 1) {
                    URL url = JXEditorPaneTest.class.getResource("resources/test.html");

                    link = new LinkModel("a resource", null, url);
                }
                model.addElement(link);
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
 
        return model;
    }

    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ascendingListModel = createAscendingListModel(0, 20);
        list = new JXList(ascendingListModel, true);
        controller = new ListSortController<ListModel>(list.getModel());
        list.setComparator(TableSortController.COMPARABLE_COMPARATOR);
        list.setRowSorter(controller);
        testRow = 2;
    }
    
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @Before
    public void setUpJ4() throws Exception {
        setUp();
    }
    
    @After
    public void tearDownJ4() throws Exception {
        tearDown();
    }

}
