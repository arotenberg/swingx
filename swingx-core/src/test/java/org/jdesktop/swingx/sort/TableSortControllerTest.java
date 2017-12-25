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
package org.jdesktop.swingx.sort;

import static org.junit.Assert.*;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.RowFilter;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.jdesktop.test.AncientSwingTeam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Unit test of TableSortController.
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class TableSortControllerTest extends
        AbstractTestSortController<TableSortController<TableModel>, TableModel> {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(TableSortControllerTest.class.getName());

    public static void main(String[] args) {
        TableSortControllerTest test = new TableSortControllerTest();
        try {
            test.runInteractiveTests();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Sanity: cachedModelRowCount updated if null model set
     */
    @Test
    public void testSetModelCleanup() {
        controller.setModel(null);
        assertEquals(0, controller.getModelRowCount());
        assertEquals(0, controller.getViewRowCount());
    }
    
    // --------------- start of #1291-swingx related tests
    /**
     * Core Issue http://forums.sun.com/thread.jspa?messageID=10939199#10939199
     * 
     * Table looses selection of last row if deleted before last.
     * 
     * Here: DefaultRowSorter conversion throws too much if not sorted.
     */
    @Test
    public void testViewRowCountAfterRemoveNotSorted() {
        int rows = controller.getModelRowCount();
        ((AncientSwingTeam) controller.getModel()).removeRows(0, 1);
        assertEquals(rows, controller.getViewRowCount());
    }

    /**
     * Core Issue http://forums.sun.com/thread.jspa?messageID=10939199#10939199
     * 
     * Table looses selection of last row if deleted before last.
     * 
     * Here: DefaultRowSorter conversion throws too much if not sorted.
     */
    @Test
    public void testViewRowCountAfterInsertNotSorted() {
        int rows = controller.getModelRowCount();
        ((AncientSwingTeam) controller.getModel()).insertRows(0, 1);
        assertEquals(rows, controller.getViewRowCount());
    }

    /**
     * Core Issue http://forums.sun.com/thread.jspa?messageID=10939199#10939199
     * 
     * Table looses selection of last row if deleted before last.
     * 
     * Here: DefaultRowSorter conversion throws too much if not sorted.
     */
    @Test
    public void testModelRowCountAfterRemoveNotSorted() {
        int rows = controller.getModelRowCount();
        ((AncientSwingTeam) controller.getModel()).removeRows(0, 1);
        assertEquals(rows, controller.getModelRowCount());
    }

    /**
     * Core Issue http://forums.sun.com/thread.jspa?messageID=10939199#10939199
     * 
     * Table looses selection of last row if deleted before last.
     * 
     * Here: DefaultRowSorter conversion throws too much if not sorted.
     */
    @Test
    public void testModelRowCountAfterInsertNotSorted() {
        int rows = controller.getModelRowCount();
        ((AncientSwingTeam) controller.getModel()).insertRows(0, 1);
        assertEquals(rows, controller.getModelRowCount());
    }

    // --------------- end of #1291 related tests

    /**
     * Issue #1156-swingx: sorter must use comparable if available
     * TableSortController
     */
    @Test
    public void testComparableComparatorTable() {
        TableModel model = new DefaultTableModel(2, 1) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return Integer.class;
            }

        };
        model.setValueAt(10, 0, 0);
        model.setValueAt(2, 1, 0);
        TableSortController<TableModel> sorter = new TableSortController<TableModel>(
                model);
        sorter.setSortOrder(0, SortOrder.ASCENDING);
        assertEquals(0, sorter.convertRowIndexToModel(1));
    }

    /**
     * Issue #1156-swingx: sorter must use comparable if available fix for that
     * (comparator update falls back to collator) introduced a
     * ClassCastException - useString must be implemented the same way
     */
    @Test
    public void testUseString() {
        controller.setSortOrder(2, SortOrder.ASCENDING);
    }

    /**
     * 
      */
    @Test
    public void testUseStringValueProvider() {
        registry.setStringValue(sv, Color.class);
        controller.setStringValueProvider(registry);
        RowFilter<Object, Object> filter = RowFilter
                .regexFilter("R/G/B: -2", 2);
        controller.setRowFilter(filter);
        assertTrue("view row count: " + controller.getViewRowCount(),
                controller.getViewRowCount() > 0);
    }

    @Override
    protected int getColumnCount() {
        return ((TableModel) controller.getModel()).getColumnCount();
    }

    // -------------------- utility methods and setup

    /**
     * @param registry2
     * @param teamModel2
     */
    private void initColumnClasses(StringValueRegistry registry,
            TableModel model) {
        Map<Integer, Class<?>> classPerColumn = new HashMap<Integer, Class<?>>();
        for (int i = 0; i < model.getColumnCount(); i++) {
            if (!Object.class.equals(model.getColumnClass(i))) {
                classPerColumn.put(i, model.getColumnClass(i));
            }
        }
        registry.setColumnClasses(classPerColumn);
    }

    /**
     * @param registry2
     * @param class1
     */
    @SuppressWarnings("unused")
    private void installPerClass(StringValueRegistry registry,
            Class<?>... clazz) {
        Map<Integer, Class<?>> classPerColumn = new HashMap<Integer, Class<?>>();
        for (int i = 0; i < clazz.length; i++) {
            classPerColumn.put(i, clazz[i]);
        }
        registry.setColumnClasses(classPerColumn);
    }

    @Override
    protected TableSortController<TableModel> createDefaultSortController(
            TableModel model) {
        return new TableSortController<TableModel>(model);
    }

    @Override
    protected TableModel createModel() {
        return new AncientSwingTeam();
    }

    @Override
    protected void setupModelDependentState(TableModel model) {
        initColumnClasses(registry, model);

    }

}
