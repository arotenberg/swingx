/*
 * $Id: SelectionIssues.java 3084 2008-10-06 13:54:59Z kleopatra $
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
package org.jdesktop.swingx;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.SortedSet;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.calendar.CalendarUtils;
import org.jdesktop.swingx.calendar.DateSelectionModel.SelectionMode;
import org.jdesktop.swingx.combobox.ListComboBoxModel;

/**
 * Cross-component selection support.
 * 
 * 
 * 
 * @author Jeanette Winzenburg
 */
public class SelectionIssues extends InteractiveTestCase {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(SelectionIssues.class
            .getName());

    public static void main(String[] args) {
        setSystemLF(true);
        SelectionIssues  test = new SelectionIssues();
        try {
            test.runInteractiveTests();
//          test.runInteractiveTests("interactive.*Locale.*");
//            test.runInteractiveTests("interactive.*AutoScroll.*");
//          test.runInteractiveTests("interactive.*UpdateUI.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }
      @SuppressWarnings("unused")
      private Calendar calendar;

    /**
     * #703-swingx: set date to first of next doesn't update the view.
     * #712-swingx: support optional auto-scroll on selection.
     * Behaviour is consistent with core components. Except that it is doing 
     * too much: revalidate most probably shouldn't change the scrolling state?
     * 
     */
    public void interactiveAutoScrollOnSelectionMonthView() {
        final JXMonthView us = new JXMonthView();
        us.setSelectionMode(SelectionMode.SINGLE_INTERVAL_SELECTION);
        final Calendar today = Calendar.getInstance();
        CalendarUtils.endOfMonth(today);
        today.add(Calendar.DAY_OF_MONTH, 1);
        us.setSelectionDate(today.getTime());
        JXFrame frame = wrapInFrame(us, "first day of next month");
        Action nextMonthInterval = new AbstractActionExt("next month interval") {

            public void actionPerformed(ActionEvent e) {
                if (us.isSelectionEmpty()) return;
                today.setTime(us.getSelectionDate());
                today.add(Calendar.DAY_OF_MONTH, -20);
                Date start = today.getTime();
                today.add(Calendar.DAY_OF_MONTH, +40);
                us.setSelectionInterval(start, today.getTime());
                // shouldn't effect scrolling state
                us.revalidate();
                // client code must trigger 
//                us.ensureDateVisible(start.getTime());
            }
            
        };
        addAction(frame, nextMonthInterval);
        Action next = new AbstractActionExt("next month") {

            public void actionPerformed(ActionEvent e) {
                if (us.isSelectionEmpty()) return;
                if (!CalendarUtils.isEndOfMonth(today)) {
                    CalendarUtils.endOfMonth(today);
                    
                }
                today.add(Calendar.DAY_OF_MONTH, 1);
                us.setSelectionDate(today.getTime());
                LOG.info("firstDisplayed before: " + us.getFirstDisplayedDay());
                
                // shouldn't effect scrolling state
                us.revalidate();
                LOG.info("firstDisplayed: " + us.getFirstDisplayedDay());
            }
            
        };
        addAction(frame, next);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * #703-swingx: select date doesn't ensure visibility of selected.
     * #712-swingx: support optional auto-scroll on selection.
     * 
     * compare with core list: doesn't scroll as well.
     * 
     */
    public void interactiveAutoScrollOnSelectionList() {
        // add hoc model
        SortedSet<Date> dates = getDates();
        
        final JXList us = new JXList(new ListComboBoxModel<Date>(new ArrayList<Date>(dates)));
        us.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        JXFrame frame = wrapWithScrollingInFrame(us, "list - autoscroll on selection");
        Action next = new AbstractActionExt("select last + 1") {

            public void actionPerformed(ActionEvent e) {
                int last = us.getLastVisibleIndex();
                us.setSelectedIndex(last + 1);
                // shouldn't effect scrolling state
                us.revalidate();
                // client code must trigger 
//                us.ensureIndexIsVisible(last+1);
            }
            
        };
        addAction(frame, next);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * #703-swingx: set date to first of next doesn't "scroll".
     * #712-swingx: support optional auto-scroll on selection.
     * 
     * compare with core tree: doesn't scroll as well.
     * 
     */
    public void interactiveAutoScrollOnSelectionTree() {
        // add hoc model
        SortedSet<Date> dates = getDates();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("dates");
        for (Date date : dates) {
            root.add(new DefaultMutableTreeNode(date));
        }
        
        final JXTree us = new JXTree(root);
        JXFrame frame = wrapWithScrollingInFrame(us, "tree - autoscroll on selection");
        Action next = new AbstractActionExt("select last + 1") {

            public void actionPerformed(ActionEvent e) {
                int last = us.getLeadSelectionRow();
                us.setSelectionRow(last + 1);
                // shouldn't effect scrolling state
                us.revalidate();
                // client code must trigger 
//                us.scrollRowToVisible(last + 1);
            }
            
        };
        addAction(frame, next);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * #703-swingx: set date to first of next doesn't "scroll".
     * #712-swingx: support optional auto-scroll on selection.
     * 
     * compare with core tree: doesn't scroll as well.
     * 
     */
    public void interactiveAutoScrollOnSelectionTable() {
        // add hoc model
        SortedSet<Date> dates = getDates();
        DefaultTableModel model = new DefaultTableModel(0, 1);
        for (Date date : dates) {
            model.addRow(new Object[] {date});
        }
        
        final JXTable us = new JXTable(model);
        JXFrame frame = wrapWithScrollingInFrame(us, "table - autoscroll on selection");
        Action next = new AbstractActionExt("select last + 1") {

            public void actionPerformed(ActionEvent e) {
                int last = us.getSelectedRow();
                us.setRowSelectionInterval(last + 1, last + 1);
                // shouldn't effect scrolling state
                us.revalidate();
                // client code must trigger 
//                us.scrollRowToVisible(last + 1);
            }
            
        };
        addAction(frame, next);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Convenience to get a bunch of dates.
     * 
     * @return
     */
    private SortedSet<Date> getDates() {
        JXMonthView source = new JXMonthView();
        source.setSelectionMode(SelectionMode.SINGLE_INTERVAL_SELECTION);
        final Calendar today = Calendar.getInstance();
        Date start = today.getTime();
        today.add(Calendar.DAY_OF_MONTH, +40);
        source.setSelectionInterval(start, today.getTime());
        SortedSet<Date> dates = source.getSelection();
        return dates;
    }
    
    /**
     * Keep test runner happy.
     */
    public void testDummy() {
        
    }
}
