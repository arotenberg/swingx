/*
 * $Id: JXTableIssues.java 4032 2011-07-08 09:20:19Z kleopatra $
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
package org.jdesktop.swingx;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.CellEditor;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.action.BoundAction;
import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.test.AncientSwingTeam;
import org.jdesktop.test.CellEditorReport;
import org.jdesktop.test.PropertyChangeReport;
import org.jdesktop.test.SerializableSupport;

/**
 * Test to exposed known issues of <code>JXTable</code>.
 * 
 * Ideally, there would be at least one failing test method per open
 * Issue in the issue tracker. Plus additional failing test methods for
 * not fully specified or not yet decided upon features/behaviour.
 * 
 * @author Jeanette Winzenburg
 */
public class JXTableIssues extends InteractiveTestCase {
    private static final Logger LOG = Logger.getLogger(JXTableIssues.class
            .getName());

    public static void main(String args[]) {
        JXTableIssues test = new JXTableIssues();
        setSystemLF(true);
        try {
//          test.runInteractiveTests();
            test.runInteractiveTests("interactive.*Scroll.*");
         //   test.runInteractiveTests("interactive.*Render.*");
//          test.runInteractiveTests("interactive.*ExtendOnRemoveAdd.*");
//            test.runInteractiveTests("interactive.*Extend.*");
//            test.runInteractiveTests("interactive.*Repaint.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        } 
    }

    /**
     * Quick check for a forum report:
     * getValueAt called on init for each cell (even the invisible).
     * 
     * Looks okay: getValueAt called for visible cells only.
     * @throws Exception 
     * 
     */
    public void testGetValueOnInit() throws Exception {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run test - headless environment");
            return;
        }
        final List<Integer> set = new ArrayList<Integer>();
        final JXTable table = new JXTable() {

            @Override
            public Object getValueAt(int row, int column) {
                set.add(row);
                return super.getValueAt(row, column);
            }
            
        };
        showWithScrollingInFrame(table, "");
        table.setModel(new DefaultTableModel(100, 5));
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                // failing - one row off?
                assertEquals(table.getColumnCount() * table.getVisibleRowCount(), set.size());
                
            }
        });

    }


    /**
     * Issue #4614616: editor lookup broken for interface types.
     * With editors (vs. renderers), the solution is not obvious -
     * interfaces can't be instantiated. As a consequence, the
     * GenericEditor can't cope (returns null as component which
     * it must not but that's another issue).
     *  
     */
    public void testNPEEditorForInterface() {
        DefaultTableModel model = new DefaultTableModel(10, 2) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return Comparable.class;
            }
            
        };
        JXTable table = new JXTable(model);
        table.prepareEditor(table.getCellEditor(0, 0), 0, 0);
    }

    /**
     * Not defined: what should happen if the edited column is hidden? 
     * For sure, editing must be terminated - but canceled or stopped?
     * 
     * Here we test if the table is not editing after editable property
     * of the currently edited column is changed to false.
     */
    public void testTableNotEditingOnColumnVisibleChange() {
        JXTable table = new JXTable(10, 2);
        TableColumnExt columnExt = table.getColumnExt(0);
        table.editCellAt(0, 0);
        // sanity
        assertTrue(table.isEditing());
        assertEquals(0, table.getEditingColumn());
        columnExt.setVisible(false);
        assertFalse("table must have terminated edit",table.isEditing());
        fail("forcing a fail - cancel editing is a side-effect of removal notification");
    }
   
    
    /**
     * Issue #359-swing: find suitable rowHeight.
     * 
     * Text selection in textfield has row of metrics.getHeight.
     * Suitable rowHeight should should take border into account:
     * for a textfield that's the metrics height plus 2.
     * 
     * PENDING: this passes locally, fails on server
     */
    public void testRowHeightFontMetrics() {
        JXTable table = new JXTable(10, 2);
        TableCellEditor editor = table.getCellEditor(1, 1);
        Component comp = table.prepareEditor(editor, 1, 1);
        assertEquals(comp.getPreferredSize().height, table.getRowHeight());
    }
    
    /**
     * a quick sanity test: reporting okay?. 
     * (doesn't belong here, should test the tools 
     * somewhere else)
     *
     */
    public void testCellEditorFired() {
        JXTable table = new JXTable(10, 2);
        table.editCellAt(0, 0);
        CellEditorReport report = new CellEditorReport();
        TableCellEditor editor = table.getCellEditor();
        editor.addCellEditorListener(report);
        editor.cancelCellEditing();
        assertEquals("total count must be equals to canceled",
                report.getCanceledEventCount(), report.getEventCount());
        assertEquals("editor must have fired canceled", 1, report.getCanceledEventCount());
        assertEquals("editor must not have fired stopped", 0, report.getStoppedEventCount());
        report.clear();
        assertEquals("canceled cleared", 0, report.getCanceledEventCount());
        assertEquals("total cleared", 0, report.getStoppedEventCount());
        // same cell, same editor
        table.editCellAt(0, 0);
        editor.stopCellEditing();
        assertEquals("total count must be equals to stopped",
                report.getStoppedEventCount(), report.getEventCount());
        assertEquals("editor must not have fired canceled", 0, report.getCanceledEventCount());
        // JW: surprising... it really fires twice?
        assertEquals("editor must have fired stopped", 1, report.getStoppedEventCount());
        
    }
    /**
     * Issue #349-swingx: table not serializable
     * 
     * Part of the problem is in TableRolloverController.
     *
     */
    public void testSerializationRollover() {
        JXTable table = new JXTable();
        SerializableSupport.serialize(table);
    }

    /**
     * Issue #349-swingx: table not serializable
     * 
     * Part of it seems to be in BoundAction. 
     *
     */
    public void testSerializationRolloverFalse() {
        JXTable table = new JXTable();
        table.setRolloverEnabled(false);
        ActionMap actionMap = table.getActionMap();
        Object[] keys = actionMap.keys();
        for (int i = 0; i < keys.length; i++) {
            if (actionMap.get(keys[i]) instanceof BoundAction) {
                actionMap.remove(keys[i]);
            }
        }
        SerializableSupport.serialize(table);
    }


    /**
     * Issue??-swingx: turn off scrollbar doesn't work if the
     *   table was initially in autoResizeOff mode.
     *   
     * Problem with state management.  
     *
     */
    public void testHorizontalScrollEnabled() {
        JXTable table = new JXTable();
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        assertEquals("horizontalScroll must be on", true, table.isHorizontalScrollEnabled());
        table.setHorizontalScrollEnabled(false);
        assertEquals("horizontalScroll must be off", false, table.isHorizontalScrollEnabled());
    }
    
    /**
     * we have a slight inconsistency in event values: setting the
     * client property to null means "false" but the event fired
     * has the newValue null.
     *
     * The way out is to _not_ set the client prop manually, always go
     * through the property setter.
     */
    public void testClientPropertyNull() {
        JXTable table = new JXTable();
        // sanity assert: setting client property set's property
        PropertyChangeReport report = new PropertyChangeReport();
        table.addPropertyChangeListener(report);
        table.putClientProperty("terminateEditOnFocusLost", null);
        assertFalse(table.isTerminateEditOnFocusLost());
        assertEquals(1, report.getEventCount());
        assertEquals(1, report.getEventCount("terminateEditOnFocusLost"));
        assertEquals(false, report.getLastNewValue("terminateEditOnFocusLost"));
    }



//----------------- interactive

    /**
     * Issue: scrollpane not sized to fit visibleRowCount exactly
     * 
     * PENDING JW: report!
     */
    public void interactiveVisibleRowCount() {
        JXTable table = new JXTable(new AncientSwingTeam());
        table.setTableHeader(null);
        table.setVisibleRowCount(table.getRowCount());
        table.setHighlighters(HighlighterFactory.createAlternateStriping(2));
        showWithScrollingInFrame(table, "visible rowCount jxtable - must fit");
        assertEquals(table.getRowCount() * table.getRowHeight(), table.getPreferredScrollableViewportSize().height);
    }
    
    public void interactiveVisibleRowCountCore() {
        JTable table = new JTable(new AncientSwingTeam()) {

            /** 
             * @inherited <p>
             */
            @Override
            public Dimension getPreferredScrollableViewportSize() {
                Dimension dim = super.getPreferredScrollableViewportSize();
                dim.height = getRowHeight() * getRowCount();
                return dim;
            }
            
        };
        table.setTableHeader(null);
        showWithScrollingInFrame(table, "core ");
        
    }


    /**
     * Use-case: null always at bottom, independent of sort order.
     * Unsupported by core default. Can SwingX? No ...
     */
    public void interactiveSortIgnoreNull() {
        TableModel model = new DefaultTableModel(10, 3);
        for (int i = 0; i < 3; i++) {
            model.setValueAt(i+1, i, 0);
        }
        JTable table = new JTable();
        table.setAutoCreateRowSorter(true);
        table.setModel(model);
        JXTable xtable = new JXTable(model);
        showWithScrollingInFrame(xtable, table, "JXTable <-> JTable: ignore null in sort");
    }

    /**
     * After moving to Mustang, this might be vieweda as a bug ;-)
     * Was: Unconditional repaint on cell update (through the default
     * identify filter). 
     * Is: only the updated cell is repainted, unrelated (from model perspective)
     * cells (like others in row) are not repainted. 
     */
    public void interactiveRepaintOnUpdateSingleCell() {
        JXTable table =  new JXTable(10, 5);
        // highlight complete row if first cell starts with a
        HighlightPredicate predicate = new HighlightPredicate() {

            public boolean isHighlighted(Component renderer,
                    ComponentAdapter adapter) {
                return adapter.getString(0).startsWith("a");
            }
            
        };
        ColorHighlighter highlighter = new ColorHighlighter(predicate, Color.MAGENTA, null, Color.MAGENTA, null);
        table.addHighlighter(highlighter);
        JXTable other = new JXTable(table.getModel());
        other.addHighlighter(highlighter);
        JXFrame frame = wrapWithScrollingInFrame(table, other, "repaint on update in first");
        addMessage(frame, "edit first cell in left table (start with/out a)");
        show(frame);
    }
    

    /**
     * Issue #610-swingx: Cancel editing via Escape doesn't fire editingCanceled.
     * 
     * Reported against ComboBoxCellEditor in the autoComplete package, but actually
     * a JTable _never_ fires a editingCanceled for any editor. Reason is that the
     * cancel Action registered in BasisTableUI incorrectly calls table.removeEditor
     * instead of getCelleditor.cancelEditing.
     * 
     * Quick hack around that: JXTable registers its own cancel action.
     * 
     * Still open: esc when popup is open will only close the popup, not cancel the
     * edit (which requires a second esc). 
     *  
     */
    public void interactiveEditingCanceledOnEscape() {
        final JTextField field = new JTextField();
        JXTable xTable = new JXTable(10, 3);
        CellEditor editor = xTable.getDefaultEditor(Object.class);
        CellEditorListener l =  new CellEditorListener() {

            public void editingCanceled(ChangeEvent e) {
                field.setText("canceled");
                
            }

            public void editingStopped(ChangeEvent e) {
                field.setText("stopped");
                
            }};
        editor.addCellEditorListener(l);
        JTable table = new JTable(xTable.getModel());
        CellEditor coreEditor = table.getDefaultEditor(Object.class);
        coreEditor.addCellEditorListener(l);
        JXFrame frame = wrapWithScrollingInFrame(xTable, table, "#610-swingx: escape doesn't fire editing canceled");
        frame.add(field, BorderLayout.SOUTH);
        frame.setVisible(true);
    }


    /**
     * Match highlighter fails to display correctly if column-based highlighter alters background
     * color.
     */
    public void interactiveColumnHighlightingWithSearch() {
        JXTable table = new JXTable(new AncientSwingTeam());
        
        table.getColumnExt("Favorite Color").setHighlighters(new AbstractHighlighter() {
            @Override
            protected Component doHighlight(Component renderer, ComponentAdapter adapter) {
                Color color = (Color) adapter.getValue();
                
                if (renderer instanceof JComponent) {
                    ((JComponent) renderer).setBorder(BorderFactory.createLineBorder(color));
                }
                
                return renderer;
            }
        });
        
        table.getColumnExt(0).addHighlighter(
                new ColorHighlighter(new HighlightPredicate() {
                    public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
                        return adapter.getValue().toString().contains("e");
                    }
                }, Color.GREEN, null));
        
        JFrame frame = wrapWithScrollingInFrame(table, "Column Highlighter with Search Test");
        table.putClientProperty(JXTable.MATCH_HIGHLIGHTER, true);
        //should highlight Jeff with Yellow
        table.getSearchable().search("e", 3);
        frame.setVisible(true);
    }
    
  //-------------------- adapted jesse wilson: #223


    /**
     * Enhancement: modifying (= filtering by resetting the content) should keep 
     * selection
     * 
     */
    public void testModifyTableContentAndSelection() {
        CompareTableBehaviour compare = new CompareTableBehaviour(new Object[] { "A", "B", "C", "D", "E", "F", "G", "H", "I" });
        compare.table.getSelectionModel().setSelectionInterval(2, 5);
        Object[] selectedObjects = new Object[] { "C", "D", "E", "F" };
        assertSelection(compare.tableModel, compare.table.getSelectionModel(), selectedObjects);
        compare.tableModel.setContents(new Object[] { "B", "C", "D", "F", "G", "H" });
        Object[] selectedObjectsAfterModify = (new Object[] { "C", "D", "F" });
        assertSelection(compare.tableModel, compare.table.getSelectionModel(), selectedObjectsAfterModify);
    }
    
    /**
     * Enhancement: modifying (= filtering by resetting the content) should keep 
     * selection
     */
    public void testModifyXTableContentAndSelection() {
        CompareTableBehaviour compare = new CompareTableBehaviour(new Object[] { "A", "B", "C", "D", "E", "F", "G", "H", "I" });
        compare.xTable.getSelectionModel().setSelectionInterval(2, 5);
        Object[] selectedObjects = new Object[] { "C", "D", "E", "F" };
        assertSelection(compare.tableModel, compare.xTable.getSelectionModel(), selectedObjects);
        compare.tableModel.setContents(new Object[] { "B", "C", "D", "F", "G", "H" });
        Object[] selectedObjectsAfterModify = (new Object[] { "C", "D", "F" });
        assertSelection(compare.tableModel, compare.xTable.getSelectionModel(), selectedObjectsAfterModify);
    }
   
    
    
     
    private void assertSelection(TableModel tableModel, ListSelectionModel selectionModel, Object[] expected) {
        List<Object> selected = new ArrayList<Object>();
        for(int r = 0; r < tableModel.getRowCount(); r++) {
            if(selectionModel.isSelectedIndex(r)) selected.add(tableModel.getValueAt(r, 0));
        }
        
        List<?> expectedList = Arrays.asList(expected);
        assertEquals("selected Objects must be as expected", expectedList, selected);
    
    }
    public static class CompareTableBehaviour {
      
        public ReallySimpleTableModel tableModel;
        public JTable table;
        public JXTable xTable;

        public CompareTableBehaviour(Object[] model) {
            tableModel = new ReallySimpleTableModel();
            tableModel.setContents(model);
 
            table = new JTable(tableModel);
            xTable = new JXTable(tableModel);
          table.getColumnModel().getColumn(0).setHeaderValue("JTable");
          xTable.getColumnModel().getColumn(0).setHeaderValue("JXTable");
        }
    };
    
    /**
     * A one column table model where all the data is in an Object[] array.
     */
    static class ReallySimpleTableModel extends AbstractTableModel {
        private List<Object> contents = new ArrayList<Object>();
        
        public void setContents(List<Object> contents) {
            this.contents.clear();
            this.contents.addAll(contents);
            fireTableDataChanged();
        }
        public void setContents(Object[] contents) {
            setContents(Arrays.asList(contents));
        }
        public void removeRow(int row) {
            contents.remove(row);
            fireTableRowsDeleted(row, row);
        }
        public int getRowCount() {
            return contents.size();
        }
        public int getColumnCount() {
            return 1;
        }
        public Object getValueAt(int row, int column) {
            if(column != 0) throw new IllegalArgumentException();
            return contents.get(row);
        }
    }

    
    
//--------------------    
    /**
     * returns a tableModel with count rows filled with
     * ascending integers in first column
     * starting from startRow.
     * @param startRow the value of the first row
     * @param rowCount the number of rows
     * @return
     */
    @SuppressWarnings("unused")
    private DefaultTableModel createAscendingModel(int startRow, final int rowCount, 
            final int columnCount, boolean fillLast) {
        DefaultTableModel model = new DefaultTableModel(rowCount, columnCount) {
            @Override
            public Class<?> getColumnClass(int column) {
                Object value = rowCount > 0 ? getValueAt(0, column) : null;
                return value != null ? value.getClass() : super.getColumnClass(column);
            }
        };
        int filledColumn = fillLast ? columnCount - 1 : 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(new Integer(startRow++), i, filledColumn);
        }
        return model;
    }
    
    
    @SuppressWarnings("unused")
    private DefaultTableModel createAscendingModel(int startRow, int count) {
        DefaultTableModel model = new DefaultTableModel(count, 5);
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(new Integer(startRow++), i, 0);
        }
        return model;
    }
    

}
