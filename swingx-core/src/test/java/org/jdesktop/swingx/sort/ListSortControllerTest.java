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

import java.util.logging.Logger;

import javax.swing.ListModel;
import javax.swing.RowFilter;

import org.jdesktop.test.AncientSwingTeam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * TODO add type doc
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class ListSortControllerTest extends AbstractTestSortController<ListSortController<ListModel>, ListModel> {

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(ListSortControllerTest.class.getName());
    /**
     */
    @Test
    public void testUseStringValueProvider() {
        registry.setStringValue(sv, 0);
        controller.setStringValueProvider(registry);
        RowFilter<Object, Object> filter = RowFilter.regexFilter("R/G/B: -2", 0);
        controller.setRowFilter(filter);
        assertTrue("view row count: " + controller.getViewRowCount(), controller.getViewRowCount() > 0);
    }

    @Override
    protected int getColumnCount() {
        return 1;
    }
    
    @Override
    protected ListSortController<ListModel> createDefaultSortController(
            ListModel model) {
        return new ListSortController<ListModel>(model);
    }

    @Override
    protected ListModel createModel() {
        return AncientSwingTeam.createNamedColorListModel();
    }


    @Override
    protected void setupModelDependentState(ListModel model) {
    }

}
