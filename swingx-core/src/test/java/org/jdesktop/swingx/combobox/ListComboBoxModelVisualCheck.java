/*
 * $Id: ListComboBoxModelVisualCheck.java 2585 2008-01-11 14:01:33Z kleopatra $
 *
 * Copyright 2007 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx.combobox;

import java.awt.event.ActionEvent;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXFrame;

public class ListComboBoxModelVisualCheck extends InteractiveTestCase {
    public static void main(String[] args) throws Exception {
        ListComboBoxModelVisualCheck test = new ListComboBoxModelVisualCheck();
        try {
            test.runInteractiveTests();
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }

    /**
     * Creates a {@code ListComboBoxModel} of {@code Integer}s for use in
     * visual checks.
     * 
     * @return a list of integers from 1 to 10
     */
    protected ListComboBoxModel<Integer> createComboBoxModel() {
        return new ListComboBoxModel<Integer>(Arrays.asList(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}));
    }
    
    /**
     * SwingX issue #670.  Fixed.
     * <p>
     * Ensure that programatically selecting an item from the list correctly updates the display.
     */
    public void interactiveSelectedItem() {
        final ComboBoxModel model = createComboBoxModel();
        JXFrame frame = wrapInFrame(new JComboBox(model), "programatically select item");
        addAction(frame, new AbstractAction("Select 9") {
            public void actionPerformed(ActionEvent e) {
                model.setSelectedItem(9);
            }
        });
        frame.pack();
        frame.setVisible(true);
    }
    
    /**
     * do nothing test - keep the testrunner happy.
     */
    public void testDummy() {
    }

}
