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
package org.jdesktop.swingx;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JList;
import javax.swing.ListModel;

/**
 * 
 * Core JList Issues.
 * 
 * 
 * @author Jeanette Winzenburg
 */
public class JListIssues extends InteractiveTestCase {

    public static void main(String[] args)  {
        JListIssues test = new JListIssues();
        setLAF("Nimbus");
        try {
            test.runInteractiveTests();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Issue: Nimbus - Navigation keys not updated on changing ComponentOrientation.
     * Same for JTable.
     * reported with internal review id: 1589811 
     */
    public void interactiveMultiColumn() {
        JList core = new JList(createListModel());
        core.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        JXFrame frame = wrapWithScrollingInFrame(core, "Nimbus: navigation keys not updated on CO change");
        addComponentOrientationToggle(frame);
        show(frame);
    }
    
    protected ListModel createListModel() {
        JList list = new JList();
        return new DefaultComboBoxModel(list.getActionMap().allKeys());
    }

}
