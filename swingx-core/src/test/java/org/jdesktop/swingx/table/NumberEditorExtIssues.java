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
package org.jdesktop.swingx.table;

import java.awt.Point;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Logger;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.text.InternationalFormatter;
import javax.swing.text.NumberFormatter;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.table.DatePickerCellEditor;
import org.jdesktop.swingx.table.NumberEditorExt;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * TODO add type doc
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class NumberEditorExtIssues extends InteractiveTestCase {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(NumberEditorExtIssues.class.getName());
    
    private static final String TOO_BIG_INTEGER = "11111111111111111111111111";
    private static final int INTEGER_COLUMN = 0;
    /** a table with a model which has column class Integer in INTEGER_COLUMN. */
    private JXTable table;
    /** a NumberEditorExt configured with IntegerFormat. */
    private NumberEditorExt cellEditor;

    private NumberEditorExt cellEditorStrict;

    public static void main(String[] args) {
        NumberEditorExtIssues test = new NumberEditorExtIssues();
        try {
            test.runInteractiveTests();
//            test.runInteractiveTests("interactive.*Number.*");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void interactiveNumberEditorKeepsEscape() {
        JFormattedTextField field = new JFormattedTextField(new Date());
        KeyStroke keyStroke = KeyStroke.getKeyStroke("ESCAPE");
        LOG.info("actionForEscape: " + field.getInputMap().get(keyStroke ));
        new NumberEditorExt(true);
        LOG.info("actionForEscape: " + field.getInputMap().get(keyStroke ));
        JXFrame frame = wrapInFrame(field, "textfield escape");
        show(frame, 400, 300);
    }
    /**
     * Issue #1236-swingx: NumberEditorExt only handles columns with type
     *    Number.
     *    
     * Perceived as overly restrictive, fails f.i. in usage in property sheet.   
     * 
     */
//    @Test
    public void interactiveMixedColumnEditors() {
        // mixed types in column
        Object[] values = new Object[] {new Integer(10), new Double(10.5), new Date(),
                new JLabel("dummy"),
                new Point(4, 6)};
        DefaultTableModel model = new DefaultTableModel(10, 2) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == getColumnCount() - 1) {
                    return Point.class;
                }
                return super.getColumnClass(columnIndex);
            }
            
        };
        for (int i = 0; i < values.length; i++) {
            model.setValueAt(values[i], i, 0);
            model.setValueAt(new Point(1, i), i, 1);
        }
        
        JXTable table = new JXTable(model) {

            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                Object value = getValueAt(row, column);
                if (value instanceof Number) {
                    return getDefaultEditor(Number.class);
                }
                if (value instanceof Date) {
                    return getDefaultEditor(Date.class);
                }
                if (value instanceof JLabel) {
                    // pathological: something with a string param constructor
                    return getDefaultEditor(Number.class);
                }
                return super.getCellEditor(row, column);
            }
            
        };
        table.setDefaultEditor(Date.class, new DatePickerCellEditor());
//        table.setDefaultEditor(Number.class, new NumberEditorExt(false));
        JXFrame frame = showWithScrollingInFrame(table, "per-cell number editor? ");
        JFormattedTextField field = new JFormattedTextField(new Date());
        addStatusComponent(frame, field);
    }
    
    /**
     * Issue #1183-swingx: NumberEditorExt throws in getCellEditorValue if
     *   Integer (short, byte..) below/above min/max.
     *   
     * Check IllegalStateException as doc'ed - the strict version doesn't. 
     * Need to check the delegate implementation?
     */
    @Test(expected = IllegalStateException.class)
    public void testEditorStrictValueIllegalState() {
        JFormattedTextField field = (JFormattedTextField) cellEditorStrict
                .getTableCellEditorComponent(table, Integer.MAX_VALUE, false, 0, INTEGER_COLUMN);
        // add valid digit - but exceeds Integer bounds so must not return true
        field.setText(field.getText() + "9");
        cellEditorStrict.getCellEditorValue();
    }
 
    /**
     * Issue ??-swingx: editor with strict number formatter throws on 
     *    committing null value.
     *   
     *  happens only if active editor in table. Use non-strict for comparison.
     *  InternationalFormatter with bounds throws as well. But not using
     *  strict catches this in isValid, that is stopCellEditing returns false.
     */
    @Test
    public void testEditorNullValue() {
        table.getColumn(INTEGER_COLUMN).setCellEditor(cellEditor);
        ((InternationalFormatter) cellEditor.getComponent().getFormatter()).setMinimum(0);
        table.editCellAt(0, INTEGER_COLUMN);
        assertTrue(cellEditor.stopCellEditing());
    }
    

//--------------------- core issues

    /**
     * This is not really an issue: Double values exceeding the bounds are Infinity.
     */
    @Test (expected = ParseException.class)
    public void testNumberFormatterDouble() throws ParseException {
        NumberFormat format = NumberFormat.getInstance();
        // no need to do anything special - parsing of doubles fails if out-off range?
        NumberFormatter formatter = new NumberFormatter(format);
        String text = "9" + new Double(Double.MAX_VALUE).toString();
        Number number = (Number) formatter.stringToValue(text);
        LOG.info("number: " + number);
    }

    /**
     * Formatted text field commit can't handle empty string.
     * 
     * @throws ParseException
     */
    @Test
    public void testTextFieldWithEmptyString() throws ParseException {
        NumberFormat format = NumberFormat.getIntegerInstance();
        JFormattedTextField field = new JFormattedTextField(format);
        field.setText("");
        field.commitEdit();
    }
    
    /**
     * NumberFormatter parsing can't handle null.
     * 
     * @throws ParseException
     */
    @Test
    public void testNumberFormatterEmptyStringValue() throws ParseException {
        NumberFormat format = NumberFormat.getIntegerInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.stringToValue("");
    }
    
    /**
     * NumberFormat parsing cant handle empty string.
     * 
     * @throws ParseException
     */
    @Test
    public void testNumberFormatEmptyStringValue() throws ParseException {
        NumberFormat format = NumberFormat.getIntegerInstance();
        format.parse("");
    }
    
    /**
     * NumberFormatter parsing can't handle null.
     * @throws ParseException
     */
    @Test
    public void testNumberFormatterNullValue() throws ParseException {
        NumberFormat format = NumberFormat.getIntegerInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.stringToValue(null);
    }
    
    /**
     * NumberFormat parse can't handle null.
     * 
     * @throws ParseException
     */
    @Test
    public void testNumberFormatNullValue() throws ParseException {
        NumberFormat format = NumberFormat.getIntegerInstance();
        format.parse(null);
    }
    

    @Test (expected = ParseException.class)
    public void testNumberFormatterMinMax() throws ParseException {
        NumberFormat format = NumberFormat.getIntegerInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setMaximum(Integer.MAX_VALUE);
        formatter.setMinimum(Integer.MIN_VALUE);
        formatter.stringToValue(TOO_BIG_INTEGER);
    }

    @Test
    public void testNumberFormatInteger() throws ParseException {
        NumberFormat format = NumberFormat.getIntegerInstance();
        // this passes - everything fitting into double range is acceptable
        format.parse(TOO_BIG_INTEGER);
        // this blows - must fit into Integer.MIN/MAX
        new Integer(TOO_BIG_INTEGER);
    }
    
    @Before
    @Override
    public void setUp() throws Exception {
        DefaultTableModel model = new DefaultTableModel(5, 1) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == INTEGER_COLUMN)
                    return Integer.class;
                return super.getColumnClass(columnIndex);
            }
            
        };
        table = new JXTable(model);
        cellEditor = new NumberEditorExt(NumberFormat.getIntegerInstance());
        cellEditorStrict = new NumberEditorExt(NumberFormat.getIntegerInstance(), true);
    }


}
