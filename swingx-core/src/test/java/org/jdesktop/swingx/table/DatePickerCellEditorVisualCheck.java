/*
 * $Id: DatePickerCellEditorVisualCheck.java 2991 2008-07-29 12:52:45Z kleopatra $
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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.tree.DefaultXTreeCellEditor;

public class DatePickerCellEditorVisualCheck extends InteractiveTestCase {
    public static void main(String[] args) {
//        setSystemLF(true);
        DatePickerCellEditorVisualCheck test = new DatePickerCellEditorVisualCheck();
        try {
            test.runInteractiveTests();
//          test.runInteractiveTests(".*Text.*");
//          test.runInteractiveTests(".*XTree.*");
//          test.runInteractiveTests(".*Table.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }
 
    /**
     * visualize datepicker editing of the hierarchical column, both
     * in a tree and a xTree switching CO.
     *
     * standard editor
     */
    public void interactiveTreeEditingRToLDatePicker() {
        TreeModel model = createTreeModelWithDates();
        JTree tree =  new JTree(model); 
        tree.setEditable(true);
        tree.setCellEditor(new DefaultTreeCellEditor(tree, null, new DatePickerCellEditor()));
        JXTree xTree = new JXTree(model);
        xTree.setEditable(true);
        xTree.setCellEditor(new DefaultTreeCellEditor(tree, null, new DatePickerCellEditor()));
        final JXFrame frame = wrapWithScrollingInFrame(tree, xTree, "standard Editing (DatePicker): compare tree and xtree");
        addComponentOrientationToggle(frame);
        show(frame);
    }

    /**
     * @return
     */
    private TreeModel createTreeModelWithDates() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(new Date());
        root.add(new DefaultMutableTreeNode(new Date()));
        TreeModel model = new DefaultTreeModel(root);
        return model;
    }

    /**
     * visualize date picker editing of the hierarchical column, both
     * in a tree and a xTree switching CO.
     * using DefaultXTreeCellEditor.
     */
    public void interactiveXTreeEditingRToLDatePicker() {
        TreeModel model = createTreeModelWithDates();
        JTree tree =  new JTree(model); 
        tree.setEditable(true);
        tree.setCellEditor(new DefaultXTreeCellEditor(tree, null, new DatePickerCellEditor()));
        JXTree xTree = new JXTree(model);
        xTree.setEditable(true);
        xTree.setCellEditor(new DefaultXTreeCellEditor(xTree, null, new DatePickerCellEditor()));
        final JXFrame frame = wrapWithScrollingInFrame(tree, xTree, "XEditing(DatePicker): compare tree and xtree");
        addComponentOrientationToggle(frame);
        show(frame);
    }


    /**
     * Issue ??-swingx: picker cell editor popup commit/cancel 
     * transfers focus out-off the table (1.5)
     * 
     * Looks like a core issue - editable combo misbehaves as well.
     * Here we use a JXTable.
     */
    public void interactiveDatePickerCellEditorXTable() {
        final JXTable table = new JXTable(createTableModel(2));
        table.setVisibleColumnCount(6);
//        table.setSurrendersFocusOnKeystroke(true);
        installEditors(table);
        Action action = new AbstractAction("toggle terminate") {

            public void actionPerformed(ActionEvent e) {
                table.setTerminateEditOnFocusLost(!table.isTerminateEditOnFocusLost());
                
            }
            
        };
        JXFrame frame = wrapWithScrollingInFrame(table, "JXTable - date picker cell editor");
        addAction(frame, action);
        frame.add(new JXDatePicker(), BorderLayout.SOUTH);
        show(frame);
    }


    /**
     * Issue ??-swingx: picker cell editor popup commit/cancel 
     * transfers focus out-off the tree (1.5)
     * 
     * 
     * a) commit by keyboard - commit okay but focus moved where?
     * b) click into popup to commit - did not commit. 
     *    Now fixed (CellEditorRemover copes with popup) okay committed, 
     *    but focus where (as in a)?
     * c) click on month navigation - editing canceled fixed. 
     * d) click in month navigation, then cancel by keyboard - 
     *    focus where?
     *    
     * focus handling is done in BasicTreeUI.completeEditing - and
     * does not cope with popup. All termination is done in uidelegate,
     * no central method in the tree called.
     *      
     * independent on stopCellEditing flag - semantic in tree is
     * different from semantic in table (first is "how" to terminate, 
     * second is "if" to terminate)  
     *    
     */
    public void interactiveDatePickerCellEditorXTree() {
        final JXTree tree = new JXTree(createTreeModelWithDates());
        tree.setEditable(true);
        tree.setCellEditor(new DefaultXTreeCellEditor(tree, null, new DatePickerCellEditor()));
        Action action = new AbstractAction("toggle terminate") {

            public void actionPerformed(ActionEvent e) {
                tree.setInvokesStopCellEditing(!tree.getInvokesStopCellEditing());
                
            }
            
        };
        JXFrame frame = wrapWithScrollingInFrame(tree, "JXTree - date picker cell editor");
        addAction(frame, action);
        frame.add(new JTextField("yet another thing to focus"), BorderLayout.SOUTH);
        show(frame);
    }


        
    /**
     * Issue ??-swingx: picker cell editor popup commit/cancel 
     * transfers focus out-off the table (1.5)
     * 
     * Looks like a core issue - editable combo misbehaves as well.
     * Here we use a core table.
     */
    public void interactiveDatePickerCellEditorTable() {
        JTable table = new JTable(createTableModel(2));
        table.putClientProperty("terminateEditOnFocusLost", true);
        installEditors(table);
        JXFrame frame = wrapWithScrollingInFrame(table, "JTable - date picker cell editor");
        frame.add(new JTextField("yet another thing to focus"), BorderLayout.SOUTH);
        show(frame);
    }



    /**
     * @param table
     */
    private void installEditors(JTable table) {
        table.setDefaultEditor(Date.class, 
                new DatePickerCellEditor(DateFormat.getDateInstance()));
        JComboBox box = new JComboBox(new String[] {"item1", "item2", "item3"});
        box.setEditable(true);
        table.getColumnModel().getColumn(1).setCellEditor(
                new DefaultCellEditor(box));
    }

    /**
     * @return
     */
    private DefaultTableModel createTableModel(int rows) {
        Object[] columns = new Object[]{"Date", "editable combo", "simple field"};
        DefaultTableModel model = new DefaultTableModel(rows, columns.length) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (getRowCount() > 0) {
                    Object value = getValueAt(0, columnIndex);
                    if (value != null) {
                        return value.getClass();
                    }
                }
                return super.getColumnClass(columnIndex);
            }
            
        };
        model.setColumnIdentifiers(columns);
        Date date = new Date();
        model.setValueAt(date, 0, 0);
        model.setValueAt("selectedItem", 0, 1);
        return model;
    }

    /**
     * Test Picker's value --> date mapping strategy.
     * @throws ParseException 
     *  failing on server due to parseException (format issue?)
     *
     */
    public void testDateEditorValueAsDate() throws ParseException  {
        DatePickerCellEditor editor = new DatePickerCellEditor();
        Date input = new Date();
        String dateString = editor.getFormats()[0].format(input);
        Date fullCycle = editor.getFormats()[0].parse(dateString);
        assertEquals("the formatted input date string must be same", fullCycle, editor.getValueAsDate(dateString));
    }



}
