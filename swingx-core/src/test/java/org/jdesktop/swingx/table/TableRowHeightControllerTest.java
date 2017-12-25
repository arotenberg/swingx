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

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.renderer.HighlighterClientVisualCheck.FontHighlighter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests for TableRowHeightController.
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class TableRowHeightControllerTest extends InteractiveTestCase {
    
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(TableRowHeightControllerTest.class.getName());

    private static final String BIG = "BIG";
    private static final String SMALL = "small";
    private TableRowHeightController controller;
    private JXTable table;

    public static void main(String[] args) {
        TableRowHeightControllerTest test = new TableRowHeightControllerTest();
        try {
            test.runInteractiveTests();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void interactiveCoreTableRowHeights() {
        final JTable table = createCoreTableWithIndyRowHeights();
        table.setAutoCreateRowSorter(true);
        new TableRowHeightController().install(table);
        JXFrame frame = wrapWithScrollingInFrame(table, "core: row heights");
        Action toggleBig = new AbstractAction("toggleSize") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (table.getSelectionModel().isSelectionEmpty()) return;
                int selected = table.getSelectedRow();
                Object value = BIG.equals(table.getValueAt(selected, 0)) ? SMALL : BIG;
                table.setValueAt(value, selected, 0);
            }
        };
        addAction(frame, toggleBig);
        Action toggleModel = new AbstractAction("toggleModel") {
            boolean evenBig;
            @Override
            public void actionPerformed(ActionEvent e) {
                table.setModel(createTableModel(evenBig));
                evenBig = !evenBig;
            }
        };
        addAction(frame, toggleModel);
        show(frame);
    }
    
    
    public void interactiveRowHeights() {
        final JXTable table = createXTableWithIndyRowHeights();
        new TableRowHeightController().install(table);
        table.packAll();
        JXFrame frame = wrapWithScrollingInFrame(table, "x: row heights");
        Action toggleBig = new AbstractAction("toggleSize") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (table.getSelectionModel().isSelectionEmpty()) return;
                int selected = table.getSelectedRow();
                Object value = BIG.equals(table.getValueAt(selected, 0)) ? SMALL : BIG;
                table.setValueAt(value, selected, 0);
            }
        };
        addAction(frame, toggleBig);
        Action toggleModel = new AbstractAction("toggleModel") {
            boolean evenBig;
            @Override
            public void actionPerformed(ActionEvent e) {
                table.setModel(createTableModel(evenBig));
                evenBig = !evenBig;
            }
        };
        addAction(frame, toggleModel);
        show(frame);
    }
    
    /**
     * @return
     */
    private JTable createCoreTableWithIndyRowHeights() {
        DefaultTableModel model = createTableModel(false);
        JTable table = new JTable(model);
        final Font derivedFont = table.getFont().deriveFont(50f);
        TableCellRenderer renderer = new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean isSelected, boolean hasFocus,
                    int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                        row, column);
                if (BIG.equals(value)) {
                    setFont(derivedFont);
                }
                return comp;
            }
            
        };
        table.setDefaultRenderer(Object.class, renderer);
        return table;
    }
    
    /**
     * Creates and returns a JXTable with model as returned by createTableModel with
     * eventBig == false.
     * Has a FontHighlighter which decorates the renderer with a big font if 
     * the cell value is BIG.
     *  
     * @return a JXTable configured 
     */
    private JXTable createXTableWithIndyRowHeights() {
        DefaultTableModel model = createTableModel(false);
        JXTable table = new JXTable(model);
        HighlightPredicate predicate = new HighlightPredicate() {

            @Override
            public boolean isHighlighted(Component renderer,
                    ComponentAdapter adapter) {
                return BIG.equals(adapter.getValue());
            }
            
        };
        FontHighlighter highlighter = new FontHighlighter(predicate, table.getFont().deriveFont(50f));
        table.addHighlighter(highlighter);
        return table;
    }

    /**
     * Creates and returns a table model with value in first column alternating between
     * SMALL and BIG.
     * 
     * @param evenBig starts with BIG if true, starts with SMALL otherwise
     * @return a DefaultTableModel with alternating SMALL/BIG values in first column.
     */
    private DefaultTableModel createTableModel(boolean evenBig) {
        int compare = evenBig ? 0 : 1;
        DefaultTableModel model = new DefaultTableModel(10, 2) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == getColumnCount() - 1) {
                    return Boolean.class;
                }
                return super.getColumnClass(columnIndex);
            }
            
        };
        for (int i = 0; i < model.getRowCount(); i++) {
            if (i % 2 == compare) {
                model.setValueAt(BIG, i, 0);
            } else {
                model.setValueAt(SMALL, i, 0);
            }
        }
        return model;
    }
    
    @Test
    public void testSetModelRewiresModelListeners() throws InterruptedException, InvocationTargetException {
        final int smallRowHeight = table.getRowHeight(1);
        final AbstractTableModel oldModel = (AbstractTableModel) table.getModel();
        assertEquals("sanity: model listener installed", true, containsModelListener(oldModel));
        final DefaultTableModel model = createTableModel(true);
        table.setModel(model);
        
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                assertEquals("new model must be installed", true, containsModelListener(model));
                assertEquals("oldModel must be released", false, containsModelListener(oldModel));
                assertEquals(smallRowHeight, table.getRowHeight(0));
            }
        });
        
    }

    /**
     * @param model
     * @return
     */
    private boolean containsModelListener(AbstractTableModel model) {
        TableModelListener[] listeners = model.getListeners(TableModelListener.class);
        for (TableModelListener l : listeners) {
            if (l == controller.getTableModelListener()) return true;
        }
        return false;
    }

    @Test
    public void testRowHeightAdjustedOnInsert() throws InterruptedException, InvocationTargetException {
        final int bigRowHeight = table.getRowHeight(1);
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                ((DefaultTableModel) table.getModel()).insertRow(0, new Object[] {BIG});
            }
        });
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                assertEquals(bigRowHeight, table.getRowHeight(0));
            }
        });
        
    }
    
    @Test
    public void testRowHeightAdjustedOnUpdate() throws InterruptedException, InvocationTargetException {
        final int smallRowHeight = table.getRowHeight(0);
        table.setValueAt(table.getValueAt(0, 0), 1, 0);
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                assertEquals(smallRowHeight, table.getRowHeight(1));
            }
        });
     }
    
    @Test
    public void testRowHeightAdjustedWithPrepare() {
        int rowHeight = table.getRowHeight(0);
        int bigRowHeight = table.getRowHeight(1);
        assertEquals(getRendererRowHeight(0, 0), table.getRowHeight(0));
        assertEquals(getRendererRowHeight(1, 0), table.getRowHeight(1));
        assertTrue(" ", 2 * rowHeight < bigRowHeight);
    }
    
    /**
     * @param i
     * @return
     */
    private int getRendererRowHeight(int row, int column) {
        TableCellRenderer renderer = table.getCellRenderer(row, column);
        Component comp = table.prepareRenderer(renderer, row, column);
        return comp.getPreferredSize().height;
    }

    @Override
    @Before
    public void setUp() throws Exception {
        table = createXTableWithIndyRowHeights();
        controller = new TableRowHeightController(table);
//        controller.install(table);
    }
    
    
    
}
