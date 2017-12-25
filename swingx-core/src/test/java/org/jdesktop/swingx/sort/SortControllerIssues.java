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

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ListModel;
import javax.swing.SortOrder;
import javax.swing.RowSorter.SortKey;

import org.jdesktop.swingx.InteractiveTestCase;
import org.junit.Test;

/**
 * Issues with SortController implementations.
 * 
 * @author Jeanette Winzenburg
 */
public class SortControllerIssues extends InteractiveTestCase {


    /**
     * Issue #1156-swingx: sorter must use comparable if available
     * ListSortController
     */
    @Test
    public void testComparableComparatorList() {
        ListModel model = new DefaultComboBoxModel(new Integer[] {10, 2});
        ListSortController<ListModel> sorter = new ListSortController<ListModel>(model);
        List<SortKey> keys = new ArrayList<SortKey>();
        keys.add(new SortKey(0, SortOrder.ASCENDING));
        sorter.setSortKeys(keys);
        assertEquals(0, sorter.convertRowIndexToModel(1));
    }
}
