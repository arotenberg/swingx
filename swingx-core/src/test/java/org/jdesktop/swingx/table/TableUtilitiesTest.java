/*
 * Created on 25.02.2011
 *
 */
package org.jdesktop.swingx.table;

import static org.jdesktop.swingx.table.TableUtilities.isDataChanged;
import static org.jdesktop.swingx.table.TableUtilities.isInsert;
import static org.jdesktop.swingx.table.TableUtilities.isDelete;
import static org.jdesktop.swingx.table.TableUtilities.isStructureChanged;
import static org.jdesktop.swingx.table.TableUtilities.isUpdate;
import static org.jdesktop.swingx.table.TableUtilities.ordinalsOf;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXTable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
@RunWith(JUnit4.class)
public class TableUtilitiesTest extends InteractiveTestCase {
    
    @Test
    public void testRemoveAllColumns() {
        TableColumnModel model = createTableColumnModel();
        TableUtilities.clear(model, true);
        assertEquals(0, model.getColumnCount());
    }
    
    @Test
    public void testRemoveAllColumnsExt() {
        TableColumnModelExt model = createTableColumnModelExt();
        TableUtilities.clear(model, true);
        assertEquals(0, model.getColumnCount());
    }
    
    @Test
    public void testRemoveAllColumnsExtHiddenIncluded() {
        TableColumnModelExt model = createTableColumnModelExt();
        model.getColumnExt(0).setVisible(false);
        TableUtilities.clear(model, true);
        assertEquals(0, model.getColumnCount(true));
    }
    
    @Test
    public void testRemoveAllColumnsExtHiddenExcluded() {
        TableColumnModelExt model = createTableColumnModelExt();
        model.getColumnExt(0).setVisible(false);
        TableUtilities.clear(model, false);
        assertEquals(1, model.getColumnCount(true));
    }
    
    
    private TableColumnModelExt createTableColumnModelExt() {
        JXTable table = new JXTable(0, 4);
        return (TableColumnModelExt) table.getColumnModel();
    }
    
    private TableColumnModel createTableColumnModel() {
        JTable table = new JTable(0, 4);
        return table.getColumnModel();
    }

    @Test
    public void testOrdinals() {
        int[] ordinals = ordinalsOf(Dummy.THIRD, Dummy.SECOND, Dummy.FIRST);
        for (int i = 0; i < ordinals.length; i++) {
            assertEquals(Dummy.values().length - 1 - i, ordinals[i]);
        }
    }
    
    @Test
    public void testOrdinalsEmpty() {
        int[] ordinals = ordinalsOf();
        assertNotNull(ordinals);
        assertEquals(0, ordinals.length);
    }
    
    enum Dummy {
        FIRST,
        SECOND,
        THIRD
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testPrefRowHeightInvalidRow() {
        JTable table = new JTable(10, 3);
        assertEquals(table.getRowHeight(), TableUtilities.getPreferredRowHeight(table, -1));
    }
    
    @Test
    public void testZeroHeight() {
        JTable table = new JTable();
        assertEquals(table.getRowHeight(), TableUtilities.getPreferredRowHeight(table, -1));
    }
    
    @Test (expected = NullPointerException.class)
    public void testPrefRowHeightMustBarkOnNull() {
        TableUtilities.getPreferredRowHeight(null, -1);
    }
    
    
    /**
     * Issue ??-swingx: NPE if tableChanged is messaged with a null event.
     *
     */
    @Test
    public void testNullTableEventNPE() {
        // don't throw on null events
        assertFalse(isInsert(null));
        assertFalse(isDelete(null));
        assertFalse(isUpdate(null));
        assertFalse(isDataChanged(null));
        assertTrue(isStructureChanged(null));
        
    }

    @Test
    public void testDataChanged() {
        // correct detection of dataChanged
        TableModelEvent dataChanged = new TableModelEvent(getModel());
        assertFalse(isUpdate(dataChanged));
        assertTrue(isDataChanged(dataChanged));
        assertFalse(isStructureChanged(dataChanged));
        assertFalse(isInsert(dataChanged));
        assertFalse(isDelete(dataChanged));
    }

    @Test
    public void testUpdate() {
        // correct detection of update
        TableModelEvent update = new TableModelEvent(getModel(), 0, 10);
        assertTrue(isUpdate(update));
        assertFalse(isDataChanged(update));
        assertFalse(isStructureChanged(update));
        assertFalse(isDelete(update));
        assertFalse(isInsert(update));
    }

    @Test
    public void testInsert() {
        // correct detection of insert/remove
        TableModelEvent insert = new TableModelEvent(
                getModel(), 0, 10, -1, TableModelEvent.INSERT);
        assertFalse(isUpdate(insert));
        assertFalse(isDelete(insert));
        assertFalse(isDataChanged(insert));
        assertFalse(isStructureChanged(insert));
        assertTrue(isInsert(insert));
    }

    @Test
    public void testRemove() {
        // correct detection of insert/remove
        TableModelEvent remove = new TableModelEvent(
                getModel(), 0, 10, -1, TableModelEvent.DELETE);
        assertFalse(isUpdate(remove));
        assertFalse(isInsert(remove));
        assertFalse(isDataChanged(remove));
        assertFalse(isStructureChanged(remove));
        assertTrue(isDelete(remove));
    }

    @Test
    public void testStructureChanged() {
        // correct detection of structureChanged
        TableModelEvent structureChanged = new TableModelEvent(getModel(), -1, -1);
        assertFalse(isInsert(structureChanged));
        assertFalse(isDelete(structureChanged));
        assertFalse(isUpdate(structureChanged));
        assertFalse(isDataChanged(structureChanged));
        assertTrue(isStructureChanged(structureChanged));
    }
    
    private TableModel getModel() {
        return new DefaultTableModel();
    }

    @Override
    @Before
    public void setUp() throws Exception {
    }
    
}
