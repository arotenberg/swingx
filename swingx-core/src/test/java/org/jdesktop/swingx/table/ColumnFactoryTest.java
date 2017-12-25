/*
 * Created on 02.06.2006
 *
 */
package org.jdesktop.swingx.table;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.BorderHighlighter;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.test.AncientSwingTeam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Contains unit tests for <code>ColumnFactory</code>.
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class ColumnFactoryTest extends InteractiveTestCase {
    
    /**
     * Issue #1215-swingx: ColumnFactory must pack with prepareRenderer.
     * Otherwise, doesn't catch sizing effective highlighters, like f.i. font.
     */
    @Test
    public void testPackWithPrepareRenderer() {
        JXTable table = new JXTable(1, 1);
        table.setValueAt("just a long string something utterly meaningless", 0, 0);
        table.packColumn(0, 0);
        assertEquals("sanity: no highlighter", table.prepareRenderer(
                table.getCellRenderer(0, 0), 0, 0).getPreferredSize().width,
                table.getColumn(0).getPreferredWidth());
        Highlighter hl = new BorderHighlighter(BorderFactory.createEmptyBorder(0, 50, 0, 50));
        table.addHighlighter(hl);
        table.packColumn(0, 0);
        assertEquals("highlighter which adds 100 px width", table.prepareRenderer(
                table.getCellRenderer(0, 0), 0, 0).getPreferredSize().width,
                table.getColumn(0).getPreferredWidth());
    }

    /**
     * Issue #564-swingx: allow custom factories to return null column.
     * Here: test that table can cope with null columns on create.
     */
    @Test
    public void testTableCopeWithCreateNullColumn() {
        // factory returns null on create
        ColumnFactory factory = new ColumnFactory() {

            @Override
            public TableColumnExt createTableColumn(int modelIndex) {
                return modelIndex > 0 ? super.createTableColumn(modelIndex) : null;
            }
            
            
        };
        JXTable table = new JXTable();
        table.setColumnFactory(factory);
        TableModel model = new DefaultTableModel(0, 10);
        table.setModel(model);
        assertEquals("factory must have created one less than model columns", 
                model.getColumnCount() - 1, table.getColumnCount());
    }

    /**
     * Issue #564-swingx: allow custom factories to return null column.
     * Here: test that ColumnFactory can handle null creation.
     */
    @Test
    public void testCreateNullColumn() {
        // factory returns null on create
        ColumnFactory factory = new ColumnFactory() {

            @Override
            public TableColumnExt createTableColumn(int modelIndex) {
                return null;
            }
            
            
        };
        TableModel model = new DefaultTableModel(0, 10);
        factory.createAndConfigureTableColumn(model, 0);
        
    }

    /**
     * Issue #564-swingx: allow custom factories to return null column.
     * Here: check how to implement model-based decision.
     * 
     * PENDING: need additional api? Left to subclasses for now. 
     */
    @Test
    public void testCreateNullColumnFromModelProperty() {
        // factory returns null on create
        ColumnFactory factory = new ColumnFactory() {

            @Override
            public TableColumnExt createAndConfigureTableColumn(
                    TableModel model, int modelIndex) {
                if ("A".equals(model.getColumnName(modelIndex))) return null;
                return super.createAndConfigureTableColumn(model, modelIndex);
            }

            
            
        };
        TableModel model = new DefaultTableModel(0, 10);
        TableColumnExt column = factory.createAndConfigureTableColumn(model, 0);
        assertNull("", column);
        
    }

    /**
     * Issue ??: NPE in pack for null table header.
     *
     */
    @Test
    public void testPackColumnNullHeader() {
        JXTable table = new JXTable(new AncientSwingTeam());
        table.setTableHeader(null);
        table.packAll();
    }
    /**
     * test if max parameter is respected.
     *
     */
    @Test
    public void testPackColumnWithMax() {
        JXTable table = new JXTable(new AncientSwingTeam());
        TableColumnExt columnExt = table.getColumnExt(0);
        table.getColumnFactory().packColumn(table, columnExt, -1, -1);
        int prefWidth = columnExt.getPreferredWidth();
        assertTrue("sanity: ", prefWidth > 10);
        int max = prefWidth - 5;
        table.getColumnFactory().packColumn(table, columnExt, -1, max);
        assertEquals("pref width must be bounded by max", 
                max, columnExt.getPreferredWidth());
    }
    /**
     * packColumn can't handle hidden columns.
     *
     */
    @Test
    public void testPackHiddenColumn() {
        JXTable table = new JXTable(10, 4);
        TableColumnExt columnExt = table.getColumnExt(0);
        columnExt.setVisible(false);
        try {
            table.getColumnFactory().packColumn(table, columnExt, -1, -1);
            fail("packColumn is doc'ed to not handle hidden columns");
        } catch (IllegalStateException e) {
            // expected
        }        
    }
    
    /**
     * test that configure throws exceptions as doc'ed.
     * Here: model index == negative
     *
     */
    @Test
    public void testConfigureTableColumnDoc() {
        TableModel model = new DefaultTableModel(0, 4);
        TableColumnExt columnExt = new TableColumnExt(-1);
        try {
            ColumnFactory.getInstance().configureTableColumn(model, columnExt);
            fail("factory must throw on illegal column model index " + columnExt.getModelIndex());
        } catch (IllegalStateException e) {
            // nothing to do - that's the doc'ed behaviour
        }        
    }
    /**
     * test that configure throws exceptions as doc'ed.
     * Here: model index == getColumnCount
     *
     */
    @Test
    public void testConfigureTableColumnExcessModelIndex() {
        TableModel model = new DefaultTableModel(0, 4);
        TableColumnExt columnExt = new TableColumnExt(model.getColumnCount());
        try {
            ColumnFactory.getInstance().configureTableColumn(model, columnExt);
            fail("factory must throw on illegal column model index " + columnExt.getModelIndex());
        } catch (IllegalStateException e) {
            // nothing to do - that's the doc'ed behaviour
        }        
    }
    /**
     * For completeness: formally test that app-wide factory 
     * is used by JXTable.
     *
     */
    @Test
    public void testSetColumnFactory() {
        ColumnFactory myFactory = new ColumnFactory();
        ColumnFactory.setInstance(myFactory);
        JXTable table = new JXTable();
        assertSame(myFactory, table.getColumnFactory());
    }
    /**
     * Issue #470-swingx: added getRowCount(table)
     *
     */
    @Test
    public void testEncapsulateRowCount() {
        final int cutOffRowCount = 10;
        ColumnFactory factory = new ColumnFactory() {
           @Override
           protected int getRowCount(JXTable table) {
               return cutOffRowCount;
           }
        };
        DefaultTableModel model = new DefaultTableModel(cutOffRowCount * 2, 2) {

            @Override
            public Object getValueAt(int row, int column) {
                if (row >= cutOffRowCount) {
                    throw new IllegalArgumentException("Illegal access to cutoff rows");
                }
                return super.getValueAt(row, column);
            }
             
        };
        JXTable table = new JXTable();
        table.setColumnFactory(factory);
        
        table.setModel(model);
        factory.packColumn(table, table.getColumnExt(0), -1, -1);
        
        
    }
    /**
     * Issue #315-swingx: pack doesn't pass column index to headerRenderer.
     * This bug shows only if the renderer relies on the column index (default doesn't). 
     */
    @Test
    public void testPackColumnIndexToHeaderRenderer() {
        final int special = 1;
        TableCellRenderer renderer = new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (column == special) {
                    value = "exxxxttteeeed" + value;
                }
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                        row, column);
                return this;
            }
            
        };
        JXTable table = new JXTable(1, 2);
        table.getTableHeader().setDefaultRenderer(renderer);
        TableColumnExt columnExt = table.getColumnExt(special);
        table.packAll();
        int realPrefWidth = 2 * 4 // magic value - JXTable's default margin, 
                                  //  needs to be made configurable, see Issue ?? 
            + renderer.getTableCellRendererComponent(table, 
                columnExt.getHeaderValue(), false, false, -1, special).getPreferredSize().width;
        assertEquals(realPrefWidth, columnExt.getPreferredWidth());
        
    }

    
    /**
     * Issue #266-swingx: support customization of pack margin.
     * 
     * added property to ColumnFactory. 
     *  
     */
    @Test
    public void testPackMargin() {
        final int special = 1;
        JXTable table = new JXTable(1, 2);
        ColumnFactory factory = new ColumnFactory();
        table.setColumnFactory(factory);
        table.setValueAt("something that's wider than 75", 0, special);
        TableColumnExt columnExt = table.getColumnExt(special);
        table.packAll();
        TableCellRenderer renderer = table.getCellRenderer(0, special);
        Component comp = table.prepareRenderer(renderer, 0, special);
        int realPrefWidth = 2 * factory.getDefaultPackMargin() // magic value - JXTable's default margin, 
                                  //  needs to be made configurable, see Issue 266 
            + comp.getPreferredSize().width;
        // sanity - default margin kicks in
        assertEquals(realPrefWidth, columnExt.getPreferredWidth());
        
        int margin = 10;
        factory.setDefaultPackMargin(margin);
        table.packAll();
        table.prepareRenderer(renderer, 0, special);
        int otherPrefWidth = 2 * margin + comp.getPreferredSize().width;
        assertEquals(otherPrefWidth, columnExt.getPreferredWidth());
        
        
    }

}
