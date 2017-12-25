/*
 * $Id: DatePickerCellEditorTest.java 3393 2009-07-20 11:02:27Z kleopatra $
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
 */
package org.jdesktop.swingx.table;

import java.text.ParseException;
import java.util.Date;

import javax.swing.tree.DefaultMutableTreeNode;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.test.CellEditorReport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class DatePickerCellEditorTest extends InteractiveTestCase {
    public static void main(String[] args) {
        setSystemLF(true);
        DatePickerCellEditorTest test = new DatePickerCellEditorTest();
        try {
            test.runInteractiveTests();
//          test.runInteractiveTests(".*Text.*");
//          test.runInteractiveTests(".*XLabel.*");
//          test.runInteractiveTests(".*Table.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }
 
    /**
     * Test Picker's value --> date mapping strategy.
     * @throws ParseException 
     * 
     *
     */
    @Test
    public void testDateEditorValueAsDate() throws ParseException  {
        DatePickerCellEditor editor = new DatePickerCellEditor();
        Date input = new Date();
        assertEquals("the input date must be unchanged", input, editor.getValueAsDate(input));
        assertEquals("input as long must be same", input, editor.getValueAsDate(input.getTime()));
        // PENDING: fails on server, moved out off the way into visual check for now
//        String dateString = editor.getFormats()[0].format(input);
//        Date fullCycle = editor.getFormats()[0].parse(dateString);
//        assertEquals("the formatted input date string must be same", fullCycle, editor.getValueAsDate(dateString));
        String nonsenseString = "invalid";
        assertNull("invalid string maps to null", editor.getValueAsDate(nonsenseString));
        assertNull("empty String maps to null", editor.getValueAsDate(""));
        // same with date/string wrapped into TreeNode
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(input);
        assertEquals("date must be user-object", input, editor.getValueAsDate(node));
        // same with date/string wrapped into a TreeTableNode
        DefaultMutableTreeTableNode tableNode = new DefaultMutableTreeTableNode(input);
        assertEquals("date must be user-object", input, editor.getValueAsDate(tableNode));
    }


    /**
     * test fire stopped after accept in monthview.
     * 
     *
     */
    @Test
    public void testDateEditorFireStopMonthAccept()  {
        DatePickerCellEditor editor = new DatePickerCellEditor();
        final CellEditorReport report = new CellEditorReport();
        editor.addCellEditorListener(report);
        JXDatePicker picker = (JXDatePicker) editor.getTableCellEditorComponent
            (null, new Date(), false, -1, -1);
        picker.getMonthView().commitSelection();
        assertEquals(1, report.getEventCount());
        assertEquals(1, report.getStoppedEventCount());
    }

    /**
     * test fire stopped after accept in monthview.
     * 
     *
     */
    @Test
    public void testDateEditorFireStopMonthCancel()  {
        DatePickerCellEditor editor = new DatePickerCellEditor();
        final CellEditorReport report = new CellEditorReport();
        editor.addCellEditorListener(report);
        JXDatePicker picker = (JXDatePicker) editor.getTableCellEditorComponent
            (null, new Date(), false, -1, -1);
        picker.getMonthView().cancelSelection();
        assertEquals(1, report.getEventCount());
        assertEquals(1, report.getCanceledEventCount());
    }

    /**
     * test fire stopped after commit picker.editor.
     * @throws ParseException 
     *
     */
    @Test
    public void testDateEditorFireStopPickerCommit() throws ParseException {
        DatePickerCellEditor editor = new DatePickerCellEditor();
        final CellEditorReport report = new CellEditorReport();
        editor.addCellEditorListener(report);
        JXDatePicker picker = (JXDatePicker) editor.getTableCellEditorComponent
            (null, null, false, -1, -1);
        picker.commitEdit();
        assertEquals(1, report.getEventCount());
        assertEquals(1, report.getStoppedEventCount());
    }

    /**
     * test fire cancel after cancel picker.editor.
     * 
     * @throws ParseException
     * 
     */
    @Test
    public void testDateEditorFireCancelPickerCancel() throws ParseException {
        DatePickerCellEditor editor = new DatePickerCellEditor();
        final CellEditorReport report = new CellEditorReport();
        editor.addCellEditorListener(report);
        JXDatePicker picker = (JXDatePicker) editor
                .getTableCellEditorComponent(null, new Date(), false, -1, -1);
        picker.cancelEdit();
        assertEquals(1, report.getEventCount());
        assertEquals(1, report.getCanceledEventCount());
    }

    /**
     * test fire stopped after stopCellEditing.
     * stop commits, triggers value change in formatted, triggers
     * BasicDatePickerUI to post action event which triggers
     * stop editing again ... that's twice!
     */
    @Test
    public void testDateEditorFireStop() {
        DatePickerCellEditor editor = new DatePickerCellEditor();
        CellEditorReport report = new CellEditorReport();
        editor.addCellEditorListener(report);
        editor.getTableCellEditorComponent(null, new Date(), false, -1, -1);
        editor.stopCellEditing();
        assertEquals(1, report.getEventCount());
        assertEquals(1, report.getStoppedEventCount());
    }
    
   /**
    * test fire cancel after cancelCellEditing
    *
    */ 
    @Test
   public void testDateEditorFireCancel() {
        DatePickerCellEditor editor = new DatePickerCellEditor();
        CellEditorReport report = new CellEditorReport();
        editor.addCellEditorListener(report);
        editor.getTableCellEditorComponent(null, null, false, -1, -1);
        editor.cancelCellEditing();
        assertEquals(1, report.getEventCount());
        assertEquals(1, report.getCanceledEventCount());
    }
        
     /**
      * Editor must not fire on getXXComponent()
      *
      */
    @Test
     public void testDateEditorNotFire() {
        DatePickerCellEditor editor = new DatePickerCellEditor();
        CellEditorReport report = new CellEditorReport();
        editor.addCellEditorListener(report);
        editor.getTableCellEditorComponent(null, null, false, -1, -1);
        assertEquals(0, report.getEventCount());
    }

}
