/*
 * $Id: JXTableUnitTest.java 4301 2013-07-04 12:36:17Z kleopatra $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.ScrollPaneLayout;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.jdesktop.swingx.JXTable.GenericEditor;
import org.jdesktop.swingx.action.BoundAction;
import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.ComponentAdapterTest.JXTableT;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.PatternPredicate;
import org.jdesktop.swingx.hyperlink.LinkModel;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.HyperlinkProvider;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.renderer.StringValues;
import org.jdesktop.swingx.rollover.RolloverController;
import org.jdesktop.swingx.rollover.RolloverProducer;
import org.jdesktop.swingx.rollover.TableRolloverController;
import org.jdesktop.swingx.sort.DefaultSortController;
import org.jdesktop.swingx.sort.RowFilters;
import org.jdesktop.swingx.sort.SortUtils;
import org.jdesktop.swingx.sort.StringValueRegistry;
import org.jdesktop.swingx.sort.TableSortController;
import org.jdesktop.swingx.table.ColumnControlButton;
import org.jdesktop.swingx.table.ColumnFactory;
import org.jdesktop.swingx.table.DefaultTableColumnModelExt;
import org.jdesktop.swingx.table.NumberEditorExt;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.test.AncientSwingTeam;
import org.jdesktop.test.CellEditorReport;
import org.jdesktop.test.PropertyChangeReport;
import org.jdesktop.test.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;


/**
* Tests of <code>JXTable</code>.
* 
* 
* @author Jeanette Winzenburg
*/
@RunWith(JUnit4.class)
public class JXTableUnitTest extends InteractiveTestCase {
    private static final Logger LOG = Logger.getLogger(JXTableUnitTest.class
            .getName());

    protected DynamicTableModel tableModel = null;
    protected TableModel sortableTableModel;

    // flag used in setup to explicitly choose LF
    private boolean defaultToSystemLF;
    // stored ui properties to reset in teardown
    private Object uiTableRowHeight;

    private StringValue sv;

    private JXTable table;
    public JXTableUnitTest() {
        super("JXTable unit test");
    }

    /**
     * Issue #1563-swingx: find cell that was clicked for componentPopup
     * 
     * Test api and event firing.
     */
    @Test
    public void testPopupTriggerLocationAvailable() {
        JXTable table = new JXTable(10, 3);
        MouseEvent event = new MouseEvent(table, 0,
                0, 0, 40, 5, 0, false);
        PropertyChangeReport report = new PropertyChangeReport(table);
        table.getPopupLocation(event);
        assertEquals(event.getPoint(), table.getPopupTriggerLocation());
        TestUtils.assertPropertyChangeEvent(report, "popupTriggerLocation", 
                null, event.getPoint());
    }
    
    
    /**
     * Issue #1563-swingx: find cell that was clicked for componentPopup
     * 
     * Test safe return value.
     */
    @Test
    public void testPopupTriggerCopy() {
        JXTable table = new JXTable(10, 3);
        MouseEvent event = new MouseEvent(table, 0,
                0, 0, 40, 5, 0, false);
        table.getPopupLocation(event);
        assertNotSame("trigger point must not be same", 
                table.getPopupTriggerLocation(), table.getPopupTriggerLocation());
    }
    
    /**
     * Issue #1563-swingx: find cell that was clicked for componentPopup
     * 
     * Test safe handle null.
     */
    @Test
    public void testPopupTriggerKeyboard() {
        JXTable table = new JXTable(10, 3);
        MouseEvent event = new MouseEvent(table, 0,
                0, 0, 40, 5, 0, false);
        table.getPopupLocation(event);
        PropertyChangeReport report = new PropertyChangeReport(table);
        table.getPopupLocation(null);
        assertNull("trigger must null", 
                table.getPopupTriggerLocation());
        TestUtils.assertPropertyChangeEvent(report, "popupTriggerLocation", 
                event.getPoint(), null);
    }

    
    /**
     * Issue #1561-swingx: add api to get TableColumn/Ext at point
     */
    @Test
    public void testGetColumnAtPoint() {
        JXTable table = new JXTable(10, 3);
        int x = table.getColumn(0).getWidth() + 10;
        TableColumn second = table.getColumn(new Point(x, 20));
        assertSame(table.getColumn(1), second);
    }
    
    /**
     * Issue #1561-swingx: add api to get TableColumn/Ext at point
     */
    @Test
    public void testGetColumnExtAtPoint() {
        JXTable table = new JXTable(10, 3);
        int x = table.getColumn(0).getWidth() + 10;
        TableColumn second = table.getColumnExt(new Point(x, 20));
        assertSame(table.getColumnExt(1), second);
    }
    
  //------- start testing Issue #1535-swingx
      
      /**
       * Sanity: initially valid entry without forcing edit is behaving as expected
       */
      @Test
      public void testGenericEditorXValidValue() {
          JTable table = new JXTable(create1535TableModel());
          table.setValueAt(new ThrowingDummy("valid"), 0, throwOnEmpty);
          assertStoppedEventOnValidValue(table, 0, throwOnEmpty, false);
      }
      
      /**
       * Test editor firing when empty value is valid
       */
      @Test
      public void testGenericEditorXValidValueAlways() {
          JTable table = new JXTable(create1535TableModel());
          assertStoppedEventOnValidValue(table, 0, takeEmpty, false);
          assertTrue(table.getValueAt(0, takeEmpty) instanceof TakeItAllDummy);
      }
      
      /**
       * Editing a not-null value with empty text
       */
      @Test
      public void testGenericEditorXEmptyValueInitiallyValid() {
          JTable table = new JXTable(create1535TableModel());
          ThrowingDummy validValue = new ThrowingDummy("valid");
          table.setValueAt(validValue, 0, throwOnEmpty);
          assertNoStoppedEventOnEmptyValue(table, 0, throwOnEmpty, true);
          assertEquals(validValue, table.getValueAt(0, throwOnEmpty));
      }
      
      /**
       * Editing a null value with empty text.
       */
      @Test
      public void testGenericEditorXEmptyValue() {
          JTable table = new JXTable(create1535TableModel());
          assertNoStoppedEventOnEmptyValue(table, 0, throwOnEmpty, false);
          assertEquals(null, table.getValueAt(0, throwOnEmpty));
      }
      
      /**
       * Asserts that stopping an edit with empty value (aka: invalid)
       * 
       * - stopCellEditing returns false
       * - does not fire editing stopped
       * 
       * Starts an edit on the given cell and stop the editor to analyse.
       * 
       *  
       * @param table
       * @param row
       * @param column
       * @param forceEmpty if true, set the editing textField's text property
       *    to empty string before stopping
       */
      protected static void assertNoStoppedEventOnEmptyValue(JTable table, int row, int column, boolean forceEmpty) {
          table.editCellAt(row, column);
          CellEditorReport report = new CellEditorReport();
          DefaultCellEditor cellEditor = (DefaultCellEditor) table.getCellEditor();
          if (forceEmpty) {
              ((JTextField) cellEditor.getComponent()).setText("");
          }
          cellEditor.addCellEditorListener(report);
          assertFalse("empty value is invalid, refuse stop", cellEditor.stopCellEditing());
          assertEquals("was invalid edit, must not fire stoppedEvent", 
                  0, report.getStoppedEventCount());
      }
      
      protected static void assertStoppedEventOnValidValue(JTable table, int row, int column, boolean forceEmpty) {
          table.editCellAt(row, column);
          CellEditorReport report = new CellEditorReport();
          DefaultCellEditor cellEditor = (DefaultCellEditor) table.getCellEditor();
          if (forceEmpty) {
              ((JTextField) cellEditor.getComponent()).setText("");
          }
          cellEditor.addCellEditorListener(report);
          assertTrue("empty value is valid", cellEditor.stopCellEditing());
          assertEquals("was valid edit, must  fire single stoppedEvent", 
                  1, report.getStoppedEventCount());
      }

      protected static DefaultTableModel create1535TableModel() {
          DefaultTableModel model = new DefaultTableModel(10, 2) {
              
              @Override
              public Class<?> getColumnClass(int columnIndex) {
                  return columnIndex == 0 ? SilentDummy.class : 
                      columnIndex == 1 ? ThrowingDummy.class : TakeItAllDummy.class;
              }
              
          };
          model.setColumnIdentifiers(new Object[]{"silently refusing edits", 
                  "throwing on empty", "take all"});
          return model;
      }
      protected static final int refuseEdit = 0;
      protected static final int throwOnEmpty = 1;
      protected static final int takeEmpty = 2;
      /**
       * No constructor with String param: not editable.
       */
      public static class SilentDummy {
          public String value;
      }
      
      /**
       * Constructor with String param which takes any string.
       */
      public static class TakeItAllDummy {
          public String value;
          public TakeItAllDummy(String value) {
              this.value = value;
          }
      }
      
      /**
       * Constructor with string param which must not be empty: 
       * fires editingStopped even if empty (aka: not valid)
       */
      public static class ThrowingDummy {
          public ThrowingDummy(String value) {
              if (value == null || "".equals(value)) {
                  throw new IllegalArgumentException("don't feed me air!!");
              }
          }
      }
//------------------- end testing #1535-swingx

    
    @Test
    public void testHyperlinkDefaultRenderer() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run ui test - headless environment: URI-renderer not registered");
            return;
        }
        
        assertHyperlinkProvider(URI.class);
//        assertHyperlinkProvider(URL.class);
    }

    private void assertHyperlinkProvider(Class<?> clazz) {
        DefaultTableRenderer renderer = (DefaultTableRenderer) table.getDefaultRenderer(clazz);
        assertTrue("expected hyperlinkProvider but was:" + renderer.getComponentProvider(),
               renderer.getComponentProvider() instanceof HyperlinkProvider );
    }
    
    /**
     * Issue #1422-swingx: setColumnSequence works incorrectly
     */
    @Test
    public void testSetColumnSequence() {
        int numColumns = 5;
        JXTable table = new JXTable(10, numColumns);
        //hide first column
        TableColumnExt columnExt = table.getColumnExt(0);
        columnExt.setVisible(false);
        List<TableColumn> allColumns = table.getColumns(true);
        List<Object> identifiers = new ArrayList<Object>();
        for (TableColumn tableColumn : allColumns) {
            identifiers.add(tableColumn.getIdentifier());
        }
        Collections.reverse(identifiers);
        table.setColumnSequence(identifiers.toArray());
        assertEquals(numColumns, table.getColumnCount(true));
        assertEquals(false, columnExt.isVisible());
        assertEquals(numColumns -1, table.getColumnCount());
    }

    /**
     * Issue #1392-swingx: ColumnControl lost on change of CO and LAF
     */
    @Test
    public void testColumnControlOnUpdateCO() {
        JXTable table = new JXTable(10, 2);
        JScrollPane scrollPane = new JScrollPane(table);
        table.setColumnControlVisible(true);
        toggleComponentOrientation(scrollPane);
        //        scrollPane.setLayout(new ScrollPaneLayout());
        assertSame("sanity: column control in trailing corner", 
                table.getColumnControl(), scrollPane.getCorner(JScrollPane.UPPER_TRAILING_CORNER));
        assertNull("column control must not be in leading corner", 
                scrollPane.getCorner(JScrollPane.UPPER_LEADING_CORNER));
        
    }
    
        /**
         * Issue #1392-swingx: ColumnControl lost on change of CO and LAF
         */
        @Test
        public void testColumnControlOnUpdateUI() {
            JXTable table = new JXTable(10, 2);
            JScrollPane scrollPane = new JScrollPane(table);
            table.setColumnControlVisible(true);
            scrollPane.setLayout(new ScrollPaneLayout());
            assertSame("sanity: column control survives setLayout", 
                    table.getColumnControl(), scrollPane.getCorner(JScrollPane.UPPER_TRAILING_CORNER));
            toggleComponentOrientation(scrollPane);
    //        scrollPane.setLayout(new ScrollPaneLayout());
            assertSame("sanity: column control in trailing corner", 
                    table.getColumnControl(), scrollPane.getCorner(JScrollPane.UPPER_TRAILING_CORNER));
            assertNull("column control must not be in leading corner", 
                    scrollPane.getCorner(JScrollPane.UPPER_LEADING_CORNER));
            
        }
        
    @Test
    public void testPrepareRenderer() {
        table.setModel(sortableTableModel);
        TableCellRenderer renderer = table.getCellRenderer(0, AncientSwingTeam.INTEGER_COLUMN);
        Component comp = table.prepareRenderer(renderer, 0, AncientSwingTeam.INTEGER_COLUMN);
        assertSame(comp, table.prepareRenderer(0, AncientSwingTeam.INTEGER_COLUMN));
    }
    
    @Test
    public void testSortedColumnIndex() {
        table.setModel(sortableTableModel);
        assertEquals(-1, table.getSortedColumnIndex());
        table.toggleSortOrder(0);
        assertEquals(0, table.getSortedColumnIndex());
        TableColumnExt columnExt = table.getColumnExt(0);
        columnExt.setVisible(false);
        assertEquals(-1, table.getSortedColumnIndex());
        columnExt.setVisible(true);
    }

    /**
     * Issue #1173-swingx: clarify table's responsibities in sorter configuration.
     */
    @Test
    public void testHasSortController() {
        assertTrue(table.hasSortController());
        table.setRowSorter(new TableRowSorter<TableModel>());
        assertFalse(table.hasSortController());
    }

    /**
     * Issue #1173-swingx: clarify table's responsibities in sorter configuration.
     */
    @Test
    public void testControlsSorterPropertiesOnSettingSorterTrue() {
        assertControlsSorterPropertiesTrue(true);
    }
    
    /**
     * Issue #1173-swingx: clarify table's responsibities in sorter configuration.
     */
    @Test
    public void testControlsSorterPropertiesOnSettingPropertyTrue() {
        assertControlsSorterPropertiesTrue(false);
    }
    /**
     * Issue #1173-swingx: clarify table's responsibities in sorter configuration.
     */
    @Test
    public void testControlsSorterPropertiesOnSettingSorterFalse() {
        assertControlsSorterPropertiesFalse(true);
    }
    
    /**
     * Issue #1173-swingx: clarify table's responsibities in sorter configuration.
     */
    @Test
    public void testControlsSorterPropertiesOnSettingPropertyFalse() {
        assertControlsSorterPropertiesFalse(false);
    }
    
    /**
     * RowSorter properties not touched if getControlsSorterProperties false.
     */
    private void assertControlsSorterPropertiesFalse(boolean setSorter) {
        table.setAutoCreateRowSorter(false);
        SortOrder[] cycle = new SortOrder[] {SortOrder.DESCENDING, SortOrder.UNSORTED};
        table.setSortOrderCycle(cycle);
        table.setSortsOnUpdates(!table.getSortsOnUpdates());
        table.setSortable(!table.isSortable());
        if (setSorter) {
            table.setRowSorter(new TableSortController<TableModel>(table.getModel()));
            assertFalse("StringValueProvider propagated to controller", 
                    table.getStringValueRegistry().equals(getSortController(table).getStringValueProvider()));
        }
        assertEquals("sortsOnUpdates propagated to controller", 
                !table.getSortsOnUpdates(), getSortController(table).getSortsOnUpdates());
        assertEquals("sortable propagated to controller",
                !table.isSortable(), getSortController(table).isSortable());
        assertFalse("sortOrderCycle propagated to controller",
                Arrays.equals(table.getSortOrderCycle(), 
                        getSortController(table).getSortOrderCycle()));
    }
    /**
     * RowSorter properties updated on getControlsSorterProperties true.
     */
    private void assertControlsSorterPropertiesTrue(boolean setSorter) {
        SortOrder[] cycle = new SortOrder[] {SortOrder.DESCENDING, SortOrder.UNSORTED};
        table.setSortOrderCycle(cycle);
        table.setSortsOnUpdates(!table.getSortsOnUpdates());
        table.setSortable(!table.isSortable());
        if (setSorter) {
            table.setRowSorter(new TableSortController<TableModel>(table.getModel()));
        }
        assertEquals("sortsOnUpdates propagated to controller", 
                table.getSortsOnUpdates(), getSortController(table).getSortsOnUpdates());
        assertEquals("sortable propagated to controller",
                table.isSortable(), getSortController(table).isSortable());
        assertTrue("sortOrderCycle propagated to controller",
                Arrays.equals(table.getSortOrderCycle(), 
                        getSortController(table).getSortOrderCycle()));
        assertEquals("StringValueProvider propagated to controller", 
                table.getStringValueRegistry(), getSortController(table).getStringValueProvider());
    }

  //-------------- sort-related properties on table


    
    /**
     * Issue #1122-swingx: re-introduce JXTable sort api.
     * Test sortsOnUpdate property.
     * Here: test default value and change notification.
     */
    @Test
    public void testSortsOnUpdateChangeNotification() {
        assertEquals("initial sortsOnUpdate", true, table.getSortsOnUpdates());
        PropertyChangeReport report = new PropertyChangeReport(table);
        table.setSortsOnUpdates(false);
        TestUtils.assertPropertyChangeEvent(report, "sortsOnUpdates", true, false);
    }
    
    /**
     * Setting table's sortable property updates controller.
     */
    @Test
    public void testSortOrderCycleNotification() {
        SortOrder[] cycle = new SortOrder[] {SortOrder.DESCENDING, SortOrder.UNSORTED};
        PropertyChangeReport report = new PropertyChangeReport(table);
        table.setSortOrderCycle(cycle);
        TestUtils.assertPropertyChangeEvent(report, "sortOrderCycle", 
                    DefaultSortController.getDefaultSortOrderCycle(), table.getSortOrderCycle());
    }

    

 // --------- add usage of StringValueRegistry into JXTable

    

    /**
     * Issue #1145-swingx: re-enable filtering with single-string-representation.
     * was: Issue #767-swingx: consistent string representation.
     * 
     * Here: test PatternFilter uses getStringXX
     */
    @Test
    public void testTableGetStringUsedInPatternFilter() {
        JXTableT table = new JXTableT(new AncientSwingTeam());
        table.setDefaultRenderer(Color.class, new DefaultTableRenderer(sv));
        RowFilter<Object, Integer> filter = RowFilter.regexFilter("R/G/B: -2.*", 2);
        table.setRowFilter(filter);
        assertTrue(table.getRowCount() > 0);
        assertEquals(sv.getString(table.getValueAt(0, 2)), table.getStringAt(0, 2));
    }
    
    /**
     * Issue 1145-swingx: re-enable filter to use string representation.
     * Here: test that cell-location StringValue look up setColumnModel.
     * 
     */
    @Test
    public void testStringValueRegistryFromSetColumnModel() {
        JXTable table = new JXTable();
        table.setAutoCreateColumnsFromModel(false);
        table.setModel(createModelDefaultColumnClasses(4));
        final int column = 2;
        // custom column factory which sets per-column renderer
        ColumnFactory factory = new ColumnFactory() {
            
            @Override
            public void configureTableColumn(TableModel model,
                    TableColumnExt columnExt) {
                super.configureTableColumn(model, columnExt);
                if (columnExt.getModelIndex() == column)
                    columnExt.setCellRenderer(new DefaultTableRenderer());
            }
            
        };
        DefaultTableColumnModelExt model = new DefaultTableColumnModelExt();
        for (int i = 0; i < table.getModel().getColumnCount(); i++) {
            model.addColumn(factory.createAndConfigureTableColumn(table.getModel(), i));
        }
        table.setColumnModel(model);
        StringValueRegistry provider = table.getStringValueRegistry();
        assertEquals(table.getCellRenderer(0, column), provider.getStringValue(0, column));
    }
    /**
     * Issue 1145-swingx: re-enable filter to use string representation.
     * Here: test that cell-location StringValue look up initial per-column renderer
     * 
     */
    @Test
    public void testStringValueRegistryFromColumnFactory() {
        JXTable table = new JXTable();
        final int column = 2;
        // custom column factory which sets per-column renderer
        ColumnFactory factory = new ColumnFactory() {

            @Override
            public void configureTableColumn(TableModel model,
                    TableColumnExt columnExt) {
                super.configureTableColumn(model, columnExt);
                if (columnExt.getModelIndex() == column)
                    columnExt.setCellRenderer(new DefaultTableRenderer());
            }
            
        };
        table.setColumnFactory(factory);
        table.setModel(createModelDefaultColumnClasses(4));
        StringValueRegistry provider = table.getStringValueRegistry();
        assertEquals(table.getCellRenderer(0, column), provider.getStringValue(0, column));
    }
    
    /**
     * Issue 1145-swingx: re-enable filter to use string representation.
     * Here: test that cell-location StringValue look up updates per-column renderer
     * on setting on TableColumn.
     */
    @Test
    public void testStringValueRegistryFromColumnRendererChange() {
        JXTable table = new JXTable(createModelDefaultColumnClasses(4));
        int column = 2;
        table.getColumn(column).setCellRenderer(new DefaultTableRenderer());
        StringValueRegistry provider = table.getStringValueRegistry();
        assertEquals(table.getCellRenderer(0, column), provider.getStringValue(0, column));
    }
    
    /**
     * Issue 1145-swingx: re-enable filter to use string representation.
     * Here: test that cell-location StringValue look up is updated with model in setModel.
     */
    @Test
    public void testStringValueRegistryWithModelSet() {
        table.setModel(createModelDefaultColumnClasses(4));
        StringValueRegistry provider = table.getStringValueRegistry();
        for (int i = 0; i < table.getColumnCount(); i++) {
            assertEquals("stringValue must be same as renderer for class: " + table.getColumnClass(i),
                    table.getDefaultRenderer(table.getColumnClass(i)),
                    provider.getStringValue(0, i));
        }
    }
    /**
     * Issue 1145-swingx: re-enable filter to use string representation.
     * Here: test that cell-location StringValue look up is updated with model initially.
     */
    @Test
    public void testStringValueRegistryWithModelInitial() {
        JXTable table = new JXTable(createModelDefaultColumnClasses(4));
        StringValueRegistry provider = table.getStringValueRegistry();
        for (int i = 0; i < table.getColumnCount(); i++) {
            assertEquals("stringValue must be same as renderer for class: " + table.getColumnClass(i),
                    table.getDefaultRenderer(table.getColumnClass(i)),
                    provider.getStringValue(0, i));
        }
    }
    
    /**
     * Issue 1145-swingx: re-enable filter to use string representation.
     * Here: test that default per-class string values are removed if not 
     *   of type StringValue.
     */
    @Test
    public void testStringValueRegistryUpdatedRemoved() {
        table.setDefaultRenderer(Number.class, new DefaultTableCellRenderer());
        assertEquals(null, 
                table.getStringValueRegistry().getStringValue(Number.class));
    }
    /**
     * Issue 1145-swingx: re-enable filter to use string representation.
     * Here: test that default per-class string values are updated.
     */
    @Test
    public void testStringValueRegistryUpdated() {
        table.setDefaultRenderer(Component.class, new DefaultTableRenderer());
        assertEquals(table.getDefaultRenderer(Component.class), 
                table.getStringValueRegistry().getStringValue(Component.class));
    }
    
    /**
     * Issue 1145-swingx: re-enable filter to use string representation.
     * Here: test that default per-class string values are registered initially.
     */
    @Test
    public void testStringValueRegistryInitial() {
        StringValueRegistry provider = table.getStringValueRegistry();
        for (int i = 0; i < DEFAULT_COLUMN_TYPES.length; i++) {
            assertEquals("stringValue must be same as renderer for class: " + DEFAULT_COLUMN_NAMES[i],
                    table.getDefaultRenderer(DEFAULT_COLUMN_TYPES[i]),
                    provider.getStringValue(DEFAULT_COLUMN_TYPES[i]));
        }
    }


    /**
     * Creates and returns a TableModel with default column classes, as
     * defined by DEFAULT_COLUMN_TYPES.
     * @return
     */
    private TableModel createModelDefaultColumnClasses(int rows) {
        DefaultTableModel model = new DefaultTableModel(DEFAULT_COLUMN_NAMES, rows) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return DEFAULT_COLUMN_TYPES[columnIndex];
            }
            
        };
        return model;
    }
    
    private static String[] DEFAULT_COLUMN_NAMES = { 
        "Object", "Number", "Date", "Icon", "ImageIcon", "Boolean"};
    private static Class<?>[] DEFAULT_COLUMN_TYPES = new Class[] {
        Object.class, Number.class, Date.class, Icon.class, ImageIcon.class, Boolean.class
    };

    
    
//--------------------- sort state on modifications
    
    /**
     * For comparison ...
     * 
     * Issue #1132-swingx: JXTable - define and implement behaviour on remove column
     */
    @Test
    public void testSortStateAfterRemoveColumn() {
        JTable table = new JTable();
        table.setAutoCreateRowSorter(true);
        table.setModel(new AncientSwingTeam());
        table.getRowSorter().toggleSortOrder(0);
        List<? extends SortKey> sortKeys = table.getRowSorter().getSortKeys();
        table.removeColumn(table.getColumnModel().getColumn(0));
        assertEquals(sortKeys, table.getRowSorter().getSortKeys());
    }

    /**
     * 
     * Issue #1132-swingx: JXTable - define and implement behaviour on remove column
     * 
     * was: Issue #53-swingx: interactive sorter not removed if column removed.
     *
     */
    @Test
    public void testSorterAfterColumnRemoved() {
        JXTable table = new JXTable(sortableTableModel);
        TableColumnExt columnX = table.getColumnExt(0);
        table.toggleSortOrder(0);
        table.removeColumn(columnX);
        // this is trivially true, as only contained columns are found ....
        assertNull("sorter must be removed when column removed", 
                table.getSortedColumn());
        // check the sort keys instead
        assertEquals("consistency with core: sortKeys untouched after remove column", 
                1, table.getRowSorter().getSortKeys().size());
    }
    
    /**
     * Interactive sorter must be active if column is hidden.
     */
    @Test
    public void testSorterAfterColumnHidden() {
        JXTable table = new JXTable(sortableTableModel);
        TableColumnExt columnX = table.getColumnExt(0);
        table.toggleSortOrder(0);
        columnX.setVisible(false);
        assertEquals("interactive sorter must be same as sorter in column", 
                columnX, table.getSortedColumn());
    }
    
    
 // --------------- sortable/comparator property 

    
    /**
     * JXTable has responsibility to guarantee usage of 
     * TableColumnExt comparator.
     * 
     */
    @Test
    public void testComparatorToController() {
        JXTable table = new JXTable(new AncientSwingTeam());
        TableColumnExt columnX = table.getColumnExt(0);
        columnX.setComparator(Collator.getInstance());
        assertSame(columnX.getComparator(), getSortController(table).getComparator(columnX.getModelIndex()));
    }
    
    /**
     * JXTable has responsibility to guarantee usage of 
     * TableColumnExt comparator.
     * 
     */
    @Test
    public void testComparatorToControllerInSetSorter() {
        JXTable table = new JXTable(new AncientSwingTeam());
        TableColumnExt columnX = table.getColumnExt(0);
        columnX.setComparator(Collator.getInstance());
        table.setRowSorter(new TableSortController<TableModel>(table.getModel()));
        assertSame(columnX.getComparator(), getSortController(table).getComparator(columnX.getModelIndex()));
    }


    /**
     * Issue 1131-swingx: JXTable must guarantee to pass column sortable property
     * always.
     * 
     * Here: test that all columns in sortController are updated after structureChanged event.
     * with a real structureChanged (more columns)
     */
    @Test
    public void testSortableColumnPropertyOnStructureChangedRemoveColumn() {
        SortableTestFactory factory = new SortableTestFactory();
        table.setColumnFactory(factory);
        // quick access to fire a structure change
        DefaultTableModel model = new DefaultTableModel(10, 5);
        table.setModel(model);
        // trigger structureChanged 
        model.setColumnCount(model.getColumnCount() - 2);
        factory.assertSortableColumnState(table);
    }
    
    /**
     * Issue 1131-swingx: JXTable must guarantee to pass column sortable property
     * always.
     * 
     * Here: test that all columns in sortController are updated after structureChanged event.
     * with a real structureChanged (more columns)
     */
    @Test
    public void testSortableColumnPropertyOnStructureChangedAddColumn() {
        SortableTestFactory factory = new SortableTestFactory();
        table.setColumnFactory(factory);
        // quick access to fire a structure change
        DefaultTableModel model = new DefaultTableModel(10, 5);
        table.setModel(model);
        // trigger structureChanged 
        model.setColumnCount(model.getColumnCount() + 2);
        factory.assertSortableColumnState(table);
    }

    /**
     * Issue 1131-swingx: JXTable must guarantee to pass column sortable property
     * always.
     * 
     * Here: test that all columns in sortController are updated after setColumnModel.
     */
    @Test
    public void testSortableSetColumnModel() {
        SortableTestFactory factory = new SortableTestFactory();
        table.setAutoCreateColumnsFromModel(false);
        table.setModel(sortableTableModel);
        DefaultTableColumnModelExt columnModel = new DefaultTableColumnModelExt();
        // add two columns, one sortable, one not sortable
        columnModel.addColumn(factory.createAndConfigureTableColumn(sortableTableModel, 0));
        columnModel.addColumn(factory.createAndConfigureTableColumn(sortableTableModel, 1));
        // hide unsortable column
        columnModel.getColumnExt(1).setVisible(false);
        table.setColumnModel(columnModel);
        factory.assertSortableColumnState(table);
    }
    
    
    /**
     * Issue 1131-swingx: JXTable must guarantee to pass column sortable property
     * always.
     * 
     * Here: test that all columns in sortController are updated after addColumn.
     */
    @Test
    public void testSortableAddColumn() {
        SortableTestFactory factory = new SortableTestFactory();
        table.setAutoCreateColumnsFromModel(false);
        table.setModel(sortableTableModel);
        // add two columns, one sortable, one not sortable
        table.addColumn(factory.createAndConfigureTableColumn(sortableTableModel, 0));
        table.addColumn(factory.createAndConfigureTableColumn(sortableTableModel, 1));
        factory.assertSortableColumnState(table);
    }
    

    /**
     * Issue 1131-swingx: JXTable must guarantee to pass column sortable property
     * always.
     * 
     * Here: test that all columns in sortController are updated after setModel.
     */
    @Test
    public void testSortableColumnPropertyOnSetModel() {
        SortableTestFactory factory = new SortableTestFactory();
        table.setColumnFactory(factory);
        table.setModel(sortableTableModel);
        factory.assertSortableColumnState(table);
    }
    
 
    /**
     * Convenience factory for testing. Configures every odd column as not sortable
     * and has a method to assert that the sortable state is as configured.
     */
    public static class SortableTestFactory extends ColumnFactory {
        
        @Override
        public void configureTableColumn(TableModel model,
                TableColumnExt columnExt) {
            super.configureTableColumn(model, columnExt);
            if (columnExt.getModelIndex() % 2 != 0) {
                // make odd columns not-sortable
                columnExt.setSortable(false);
            } else {
                // set per-column comparator for even columns
                columnExt.setComparator(Collator.getInstance());
            }
        }
        
        public void assertSortableColumnState(JXTable table) {
            List<TableColumn> columns = table.getColumns(true);
            for (TableColumn tableColumn : columns) {
                int i = tableColumn.getModelIndex();
                assertEquals("odd/even columns must be not/-sortable: " + i, i % 2 == 0, 
                        getSortController(table).isSortable(i));
                if (tableColumn instanceof TableColumnExt) {
                    Comparator<?> comparator = ((TableColumnExt) tableColumn).getComparator();
                    // JW: need to check against null because sorter might have its own
                    // ideas about default comparators
                    if (comparator != null) {
                        assertSame("comparator must be same: " + i, comparator, 
                                getSortController(table).getComparator(i));
                    }
                }
                
            }
        }
    }
    
    
    /**
     * Issue 1131-swingx: JXTable must guarantee to pass column sortable property
     * always.
     * 
     * Here: test that table's sortable property is propagated on setModel.
     */
    @Test
    public void testSortableTablePropertyOnSetModel() {
        table.setSortable(false);
        table.setModel(sortableTableModel);
        assertEquals(false, getSortController(table).isSortable());
    }

    /**
     * Issue 1131-swingx: table must unsort column on sortable change.
     * 
     * Invalid as of switch to core sorting. On the contrary, the table must
     * not go clever under the feat of the controller.
     * 
     */
    @Test
    public void testTableUnsortedColumnOnColumnSortableChange() {
        JXTable table = new JXTable(10, 2);
        TableColumnExt columnExt = table.getColumnExt(0);
        table.toggleSortOrder(0);
        assertTrue("sanity: sorted", SortUtils.isSorted(table.getSortOrder(0)));
        columnExt.setSortable(false);
        assertTrue("changing sortability must not change sort state (with default controller)", 
                SortUtils.isSorted(table.getSortOrder(0)));
    }



    /**
     * JXTable has responsibility to respect TableColumnExt
     * sortable property.
     * 
     */
    @Test
    public void testSetSortOrderByIdentifierColumnNotSortable() {
        JXTable table = new JXTable(sortableTableModel);
        Object identifier = "Last Name";
        TableColumnExt columnX = table.getColumnExt(identifier);
        //  make column not sortable.
        columnX.setSortable(false);
        table.setSortOrder(identifier, SortOrder.ASCENDING);
        assertEquals("unsortable column must be unsorted", SortOrder.UNSORTED, table.getSortOrder(identifier));
    }

    /**
     * JXTable has responsibility to respect TableColumnExt
     * sortable property.
     * 
     */
    @Test
    public void testToggleSortOrderByIdentifierColumnNotSortable() {
        JXTable table = new JXTable(sortableTableModel);
        Object identifier = "Last Name";
        TableColumnExt columnX = table.getColumnExt(identifier);
        // old way: make column not sortable.
        columnX.setSortable(false);
        table.toggleSortOrder(identifier);
        assertEquals("unsortable column must be unsorted", SortOrder.UNSORTED, table.getSortOrder(identifier));
    }


    /**
     * JXTable has responsibility to respect TableColumnExt
     * sortable property.
     * 
     */
    @Test
    public void testSetSortOrderColumnNotSortable() {
        JXTable table = new JXTable(sortableTableModel);
        TableColumnExt columnX = table.getColumnExt(0);
        // old way: make column not sortable.
        columnX.setSortable(false);
        table.setSortOrder(0, SortOrder.ASCENDING);
        assertEquals("unsortable column must be unsorted", SortOrder.UNSORTED, table.getSortOrder(0));
       
    }

    /**
     * JXTable has responsibility to respect TableColumnExt
     * sortable property.
     * 
     */
    @Test
    public void testToggleSortOrderColumnNotSortable() {
        JXTable table = new JXTable(sortableTableModel);
        TableColumnExt columnX = table.getColumnExt(0);
        // old way: make column not sortable.
        columnX.setSortable(false);
        table.toggleSortOrder(0);
        assertEquals("unsortable column must be unsorted", SortOrder.UNSORTED, table.getSortOrder(0));
    }
   
    
    /**
     * Setting table's sortable property updates controller.
     */
    @Test
    public void testTableRowFilterSynchedToController() {
        RowFilter<Object, Object> filter = RowFilters.regexFilter(".*");
        table.setRowFilter(filter);
        assertEquals(filter, getSortController(table).getRowFilter());
        assertEquals(filter, table.getRowFilter());
    }
    

//----------------- table sort api
/* NOTE: if applicable we test the api on columns which are not of type TableColumnExt
 * Doing so because the pre-Mustang implementation worked on columns of extended type only.
 *    
 */
    /**
     * add api to access the sorted column.
     *
     */
    @Test
    public void testSortedColumn() {
        JXTable table = createTableWithCoreColumns();
        table.toggleSortOrder(1);
        assertEquals(table.getColumn(1), table.getSortedColumn());
        table.toggleSortOrder(2);
        assertEquals(table.getColumn(2), table.getSortedColumn());
    }

    /**
     * Create and return a table with core column type.
     * 
     * @return
     */
    private JXTable createTableWithCoreColumns() {
        JXTable table = new JXTable();
        table.setAutoCreateColumnsFromModel(false);
        table.setModel(sortableTableModel);
        TableColumnModel columnModel = new DefaultTableColumnModelExt();
        for (int i = 0; i < sortableTableModel.getColumnCount(); i++) {
            TableColumn column = new TableColumn(i);
            column.setHeaderValue(sortableTableModel.getColumnName(i));
            column.setIdentifier(sortableTableModel.getColumnName(i));
            columnModel.addColumn(column);
        }
        table.setColumnModel(columnModel);
        return table;
    }
    /**
     * Unsetting sortOrder of one column must not remove sorts in other columns.
     * 
     * Previously we had single column sort only, so remove that single was same
     * as reset all.
     */
    @Test
    public void testSetUnsortedSortOrder() {
        JXTable table = createTableWithCoreColumns();
        // prepare: two columns sorted
        table.setSortOrder(0, SortOrder.ASCENDING);
        table.setSortOrder(1, SortOrder.ASCENDING);
        assertEquals("sanity: really multiple columns sorted", 2, table.getRowSorter().getSortKeys().size());
        // unsort primary sort column
        table.setSortOrder(1, SortOrder.UNSORTED);
        assertEquals("secondary sort column must still be sorted", SortOrder.ASCENDING, table.getSortOrder(0));
    }
    /**
     * Unsetting sortOrder of one column must not remove sorts in other columns.
     * 
     * Previously we had single column sort only, so remove that single was same
     * as reset all.
     */
    @Test
    public void testSetUnsortedSortOrderByIdentifier() {
        JXTable table = createTableWithCoreColumns();
        // prepare: two columns sorted
        table.setSortOrder("First Name", SortOrder.ASCENDING);
        table.setSortOrder("Last Name", SortOrder.ASCENDING);
        assertEquals("sanity: really multiple columns sorted", 2, table.getRowSorter().getSortKeys().size());
        // unsort primary sort column
        table.setSortOrder("Last Name",  SortOrder.UNSORTED);
        assertEquals("secondary sort column must still be sorted", SortOrder.ASCENDING, table.getSortOrder("First Name"));
    }
    
    /**
     * Programmatic sorting of hidden column (through table api).
     * 
     */
    @Test
    public void testSetSortOrderHiddenColumn() {
        // hidden only defined for TableColumnExt
        JXTable table = new JXTable(sortableTableModel);
        Object identifier = "Last Name";
        TableColumnExt columnExt = table.getColumnExt(identifier);
        columnExt.setVisible(false);
        table.setSortOrder(identifier, SortOrder.ASCENDING);
        assertEquals("sorted column must be at " + identifier, columnExt, table.getSortedColumn());
        assertEquals("column must be sorted after setting sortOrder on " + identifier, SortOrder.ASCENDING, table.getSortOrder(identifier));
    }
    
    

    /**
     * added xtable.setSortOrder(Object, SortOrder)
     * 
     */
    @Test
    public void testSetSortOrderByIdentifier() {
        JXTable table = createTableWithCoreColumns();
        Object identifier = "Last Name";
        TableColumn columnExt = table.getColumn(identifier);
        table.setSortOrder(identifier, SortOrder.ASCENDING);
        assertEquals("sorted column must be at " + identifier, columnExt, table.getSortedColumn());
        assertEquals("column must be sorted after setting sortOrder on " + identifier, SortOrder.ASCENDING, table.getSortOrder(identifier));
    }

    /**
     * testing new sorter api: 
     * getSortOrder(int), toggleSortOrder(int).
     *
     */
    @Test
    public void testToggleSortOrder() {
        JXTable table = createTableWithCoreColumns();
        assertSame(SortOrder.UNSORTED, table.getSortOrder(0));
        table.toggleSortOrder(0);
        assertSame(SortOrder.ASCENDING, table.getSortOrder(0));
        // sanity: other columns uneffected
        assertSame(SortOrder.UNSORTED, table.getSortOrder(1));
        table.toggleSortOrder(0);
        assertSame(SortOrder.DESCENDING, table.getSortOrder(0));
        table.resetSortOrder();
        assertSame(SortOrder.UNSORTED, table.getSortOrder(0));
    }
    

    /**
     * testing new sorter api: 
     * toggleSortOrder(Object), resetSortOrder.
     *
     */
    @Test
    public void testToggleSortOrderByIdentifier() {
        JXTable table = createTableWithCoreColumns();
        Object firstColumn = "First Name";
        Object secondColumn = "Last Name";
        assertSame(SortOrder.UNSORTED, table.getSortOrder(secondColumn));
        table.toggleSortOrder(firstColumn);
        assertSame(SortOrder.ASCENDING, table.getSortOrder(firstColumn));
        // sanity: other columns uneffected
        assertSame(SortOrder.UNSORTED, table.getSortOrder(secondColumn));
        table.toggleSortOrder(firstColumn);
        assertSame(SortOrder.DESCENDING, table.getSortOrder(firstColumn));
        table.resetSortOrder();
        assertSame(SortOrder.UNSORTED, table.getSortOrder(firstColumn));
    }

    
    /**
     * added xtable.setSortOrder(int, SortOrder)
     * 
     */
    @Test
    public void testSetSortOrder() {
        JXTable table = createTableWithCoreColumns();
        int col = 0;
        TableColumn columnExt = table.getColumn(col);
        table.setSortOrder(col, SortOrder.ASCENDING);
        assertEquals("sorted column must be at " + col, columnExt, table.getSortedColumn());
        assertEquals("column must be sorted after setting sortOrder on " + col, SortOrder.ASCENDING, table.getSortOrder(col));
    }

    /**
     * resetSortOrders didn't check for tableHeader != null.
     * Didn't show up before new sorter api because method was protected and 
     * only called from JXTableHeader.
     *
     */
    @Test
    public void testResetSortOrderNPE() {
        JXTable table = new JXTable(sortableTableModel);
        table.setTableHeader(null);
        table.resetSortOrder();
    }


// --------------------- SortController/RowSorter
    
    /**
     * Test that JXTable uses TableSortController by default.
     */
    @Test
    public void testSortController() {
        assertTrue("default sorter expected TableRowSorter, but was:" + table.getRowSorter(),
                table.getRowSorter() instanceof TableSortController<?>);
    }
    /**
     * core issue: rowSorter replaced on setAutoCreateRowSorter even without change to flag.
     */
    @Test
    public void testSetAutoCreateRowSorter() {
        JXTable table = new JXTable(new AncientSwingTeam());
        RowSorter<?> sorter = table.getRowSorter();
        assertNotNull("sanity: core rowSorter is created", sorter);
        table.setAutoCreateRowSorter(true);
        assertSame(sorter, table.getRowSorter());
    }

    /**
     * Sanity test: we are tricksing super to think that auto-create is off, but
     * only super. At all other times the property must be as set. 
     * 
     */
    @Test
    public void testAutoCreateRowSorterGetterSetterSynched() {
        table.setAutoCreateRowSorter(false);
        assertEquals(false, table.getAutoCreateRowSorter());
        table.setAutoCreateRowSorter(true);
        assertEquals(true, table.getAutoCreateRowSorter());
    }
    /**
     * Sanity test: we are tricksing super to think that auto-create is off, but
     * only super. Here we test if setModel does reset the property to its old state.
     * 
     */
    @Test
    public void testSetModelKeepsCreateRowSorterFlagTrue() {
        assertSetModelKeepsCreateSorterFlag(table, true);
    }
    /**
     * Sanity test: we are tricksing super to think that auto-create is off, but
     * only super. Here we test if setModel does reset the property to its old state.
     * 
     */
    @Test
    public void testSetModelKeepsCreateRowSorterFlagFalse() {
        assertSetModelKeepsCreateSorterFlag(table, false);
    }

    /**
     * @param table
     * @param flag
     */
    private void assertSetModelKeepsCreateSorterFlag(JTable table, boolean flag) {
        table.setAutoCreateRowSorter(flag);
        table.setModel(new DefaultTableModel());
        assertEquals(flag, table.getAutoCreateRowSorter());
    }

    
    /**
     * Test that setModel uses factory method to create rowSorter.
     */
    @Test
    public void testSetModelCreateDefaultRowSorter() {
        JXTable table = new JXRTable();
        table.setModel(new AncientSwingTeam());
        assertTrue("table must install default rowSorter, but was: " + table.getRowSorter().getClass(), 
                table.getRowSorter() instanceof XTableRowSorter<?>);
        assertSame("default RowSorter must be configured with table model", 
                table.getModel(), table.getRowSorter().getModel() );
    }
    
    /**
     * Test that JXTable uses factroy method when auto-creating a rowSorter.
     */
    @Test
    public void testCreateDefaultRowSorterOverridden() {
        JXTable table = new JXRTable();
        assertTrue("table must install default rowSorter, but was: " + table.getRowSorter().getClass(), 
                table.getRowSorter() instanceof XTableRowSorter<?>);
        assertSame("default RowSorter must be configured with table model", 
                table.getModel(), table.getRowSorter().getModel() );
    }
    
    public static class JXRTable extends JXTable {
        @Override
        protected RowSorter<? extends TableModel> createDefaultRowSorter() {
            XTableRowSorter<TableModel> sorter = new XTableRowSorter<TableModel>();
            sorter.setModel(getModel());
            return sorter;
        }
    }
    
    /** 
     * quick class to check that JXTable uses its factory method to auto-create
     */
    public static class XTableRowSorter<M extends TableModel> extends TableRowSorter<M> {
        
    }
    
    /**
     * Sanity: default rowSorter has same model as table.
     */
    @Test
    public void testAutoCreateRowSorterModelInstalled() {
        assertSame("model must be same", table.getModel(), table.getRowSorter().getModel());
    }
    /**
     * JXTable auto-create rowsorter is true by default.
     */
    @Test
    public void testAutoCreateRowSorter() {
        assertEquals("table must have default auto-create rowsorter true", true, table.getAutoCreateRowSorter());
    }
//------------------- ComponentAdapter
    
    /**
     * Issue #??- ComponentAdapter's default implementation does not
     *    return the value at the adapter's view state.
     * 
     * Clarified the documentation: the assumption for the base implementation
     * is that model coordinates == view coordinates, that is it's up to 
     * subclasses to implement the model correctly if they support different
     * coordinate systems.
     *
     */
    @Test
    public void testComponentAdapterCoordinatesDocumentation() {
        final JXTable table = new JXTable(createAscendingModel(0, 10));
        Object originalFirstRowValue = table.getValueAt(0,0);
        Object originalLastRowValue = table.getValueAt(table.getRowCount() - 1, 0);
        assertEquals("view row coordinate equals model row coordinate", 
                table.getModel().getValueAt(0, 0), originalFirstRowValue);
        // sort first column - actually does not change anything order 
        table.toggleSortOrder(0);
        // sanity assert
        assertEquals("view order must be unchanged ", 
                table.getValueAt(0, 0), originalFirstRowValue);
        // invert sort
        table.toggleSortOrder(0);
        // sanity assert
        assertEquals("view order must be reversed changed ", 
                table.getValueAt(0, 0), originalLastRowValue);
        ComponentAdapter adapter = new ComponentAdapter(table) {

            @Override
            public Object getColumnIdentifierAt(int columnIndex) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public int getColumnIndex(Object identifier) {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public String getColumnName(int columnIndex) {
                return null;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public Object getFilteredValueAt(int row, int column) {
                return getValueAt(table.convertRowIndexToModel(row), column);
            }

            @Override
            public Object getValueAt(int row, int column) {
                return table.getModel().getValueAt(row, column);
            }
            
            @Override
            public Object getValue() {
                return getValueAt(table.convertRowIndexToModel(row), convertColumnIndexToModel(column));
            }

            @Override
            public boolean hasFocus() {
                return false;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public boolean isEditable() {
                return false;
            }
            
            @Override
            public boolean isSelected() {
                return false;
            }
        };
        assertEquals("adapter filteredValue expects row view coordinates", 
                table.getValueAt(0, 0), adapter.getFilteredValueAt(0, 0));
        // adapter coordinates are view coordinates
        adapter.row = 0;
        adapter.column = 0;
        assertEquals("adapter.getValue must return value at adapter coordinates", 
                table.getValueAt(0, 0), adapter.getValue());
        
        assertEquals(adapter.getFilteredValueAt(0, adapter.getColumnCount() -1), 
                adapter.getValue(adapter.getColumnCount()-1));
        
        
    }
    /**
     * Test assumptions of accessing table model/view values through
     * the table's componentAdapter.
     * 
     * PENDING JW: revisit - diff from above method?
     */
    @Test
    public void testComponentAdapterCoordinates() {
        JXTable table = new JXTable(createAscendingModel(0, 10));
        Object originalFirstRowValue = table.getValueAt(0,0);
        Object originalLastRowValue = table.getValueAt(table.getRowCount() - 1, 0);
        assertEquals("view row coordinate equals model row coordinate", 
                table.getModel().getValueAt(0, 0), originalFirstRowValue);
        // sort first column - actually does not change anything order 
        table.toggleSortOrder(0);
        // sanity asssert
        assertEquals("view order must be unchanged ", 
                table.getValueAt(0, 0), originalFirstRowValue);
        // invert sort
        table.toggleSortOrder(0);
        // sanity assert
        assertEquals("view order must be reversed changed ", 
                table.getValueAt(0, 0), originalLastRowValue);
        ComponentAdapter adapter = table.getComponentAdapter();
        assertEquals("adapter filteredValue expects row view coordinates", 
                table.getValueAt(0, 0), adapter.getFilteredValueAt(0, 0));
        // adapter coordinates are view coordinates
        adapter.row = 0;
        adapter.column = 0;
        assertEquals("adapter.getValue must return value at adapter coordinates", 
                table.getValueAt(0, 0), adapter.getValue());
        assertEquals("adapter.getValue must return value at adapter coordinates", 
                table.getValueAt(0, 0), adapter.getValue(0));
    }


//------------------ end of sort-related tests

    
    
    /**
     * Issue #847-swingx: JXTable respect custom corner if columnControl not visible
     * 
     *  LAF provided corners are handled in core since jdk6u10. 
     */
    @Test
    public void testCornerRespectLAF() {
        Object corner = UIManager.get("Table.scrollPaneCornerComponent");
        if (!(corner instanceof Class<?>)) {
            LOG.fine("cannont run - LAF doesn't provide corner component");
            return;
        }
        final JXTable table = new JXTable(10, 2);
        final JScrollPane scrollPane = new JScrollPane(table);
        table.addNotify();
        assertNotNull(scrollPane.getCorner(JScrollPane.UPPER_TRAILING_CORNER));
        assertEquals(corner, scrollPane.getCorner(JScrollPane.UPPER_TRAILING_CORNER).getClass());
    }

    

    /**
     * Issue #550-swingx: xtable must not reset columns' pref/size on 
     * structureChanged if autocreate is false.
     * 
     *  here: width (was no problem, default columnFactory only sets pref)
     */
    @Test
    public void testInitializeColumnWidth() {
        JXTable table = new JXTable(10, 2);
        table.setAutoResizeMode(JXTable.AUTO_RESIZE_OFF);
        table.setAutoCreateColumnsFromModel(false);
        int width = table.getColumn(0).getWidth() + 2;
        table.getColumn(0).setWidth(width);
        assertEquals("sanity: ", width, table.getColumn(0).getWidth());
        table.tableChanged(null);
        assertEquals("structure changed must not resize column", 
                width, table.getColumn(0).getWidth() );
    }
    
    /**
     * Issue #550-swingx: xtable must not reset columns' pref/width on 
     * structureChanged if autocreate is false.
     * 
     * here: prefWidth 
     */
    @Test
    public void testInitializeColumnPrefWidth() {
        JXTable table = new JXTable(10, 2);
        table.setAutoResizeMode(JXTable.AUTO_RESIZE_OFF);
        table.setAutoCreateColumnsFromModel(false);
        int width = table.getColumn(0).getPreferredWidth() + 2;
        table.getColumn(0).setPreferredWidth(width);
        assertEquals("sanity: ", width, table.getColumn(0).getPreferredWidth());
        table.tableChanged(null);
        assertEquals("structure changed must not resize column", width, 
                table.getColumn(0).getPreferredWidth() );
    }
    
    
    /**
     * Issue #847-swingx: JXTable respect custom corner if columnControl not visible
     * 
     * Test correct un-/config on toggling the controlVisible property
     */
    @Test
    public void testColumnControlVisible() {
        JXTable table = new JXTable(10, 2);
        JScrollPane scrollPane = new JScrollPane(table);
        table.setColumnControlVisible(true);
        assertSame("sanity: column control set", table.getColumnControl(), scrollPane.getCorner(JScrollPane.UPPER_TRAILING_CORNER));
        table.setColumnControlVisible(false);
        assertEquals("columnControl must be removed from corner if not visible", 
                null, scrollPane.getCorner(JScrollPane.UPPER_TRAILING_CORNER));
    }
    
    /**
     * Issue #847-swingx: JXTable respect custom corner if columnControl not visible
     * 
     * @throws Exception
     */
    @Test
    public void testCornerRespectCustom() throws Exception {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run testCornerNPE - headless environment");
            return;
        }
        
        final JXTable table = new JXTable(10, 2);
        final JScrollPane scrollPane = new JScrollPane(table);
        final JFrame frame = new JFrame();
        frame.add(scrollPane);
        frame.pack();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                JPanel panel = new JPanel();
                scrollPane.setCorner(JScrollPane.UPPER_TRAILING_CORNER, panel);
                assertEquals("sanity ...", panel, scrollPane.getCorner(JScrollPane.UPPER_TRAILING_CORNER));
                frame.remove(scrollPane);
                frame.add(scrollPane);
                if (table.isColumnControlVisible()) {
                    assertEquals(table.getColumnControl(), scrollPane.getCorner(JScrollPane.UPPER_TRAILING_CORNER));
                } else {
                    assertEquals("xTable respects custom corner if columnControl invisible", 
                            panel,
                        scrollPane.getCorner(JScrollPane.UPPER_TRAILING_CORNER));
                }
            }
        });
    }

    /**
     * Issue #844-swingx: JXTable throws NPE with custom corner.
     * 
     * @throws Exception
     */
    @Test
    public void testCornerNPE() throws Exception {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run testCornerNPE - headless environment");
            return;
        }
        
        JXTable table = new JXTable(10, 2);
        final JScrollPane scrollPane = new JScrollPane(table);
        final JFrame frame = new JFrame();
        frame.add(scrollPane);
        frame.pack();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                scrollPane.setCorner(JScrollPane.UPPER_TRAILING_CORNER, new JPanel());
                assertNotNull("sanity ...", scrollPane.getCorner(JScrollPane.UPPER_TRAILING_CORNER));
                frame.remove(scrollPane);
                frame.add(scrollPane);
            }
        });
    }
    /**
     * Issue #844-swingx: JXTable throws NPE with custom corner.
     * Regression testing (Issue #155-swingx) - scrollpane policy must be respected.
     * @throws Exception
     */
    @Test
    public void testCornerNPEVerticalSPPolicy() throws Exception {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run testCornerNPE - headless environment");
            return;
        }
        
        final JXTable table = new JXTable(10, 2);
        final JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        table.setColumnControlVisible(true);
        final JFrame frame = new JFrame();
        frame.add(scrollPane);
        frame.pack();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.remove(scrollPane);
                frame.add(scrollPane);
                assertSame(table.getColumnControl(), scrollPane.getCorner(JScrollPane.UPPER_TRAILING_CORNER));
                table.setColumnControlVisible(false);
                assertEquals(JScrollPane.VERTICAL_SCROLLBAR_NEVER, scrollPane.getVerticalScrollBarPolicy());
            }
        });
    }

    /**
     * Issue #844-swingx: JXTable throws NPE with custom corner.
     * Regression testing (Issue #155-swingx) - scrollpane policy must be respected.
     */
    @Test
    public void testCornerNPEVerticalSPOnUpdateUI(){
        final JXTable table = new JXTable(10, 2);
        final JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        table.setColumnControlVisible(true);
        table.updateUI();
        table.setColumnControlVisible(false);
        assertEquals(JScrollPane.VERTICAL_SCROLLBAR_NEVER, scrollPane.getVerticalScrollBarPolicy());
    }

    /**
     * Issue #838-swingx: table.prepareRenderer adds bogey listener to column highlighter.
     * 
     */
    @Test
    public void testColumnHighlighterListener() {
        JXTable table = new JXTable(10, 2);
        ColorHighlighter highlighter = new ColorHighlighter();
        table.getColumnExt(0).addHighlighter(highlighter);
        int listenerCount = highlighter.getChangeListeners().length;
        assertEquals(1, listenerCount);
        table.prepareRenderer(table.getCellRenderer(0, 0), 0, 0);
        assertEquals(listenerCount, highlighter.getChangeListeners().length);
    }
    
    /**
     * Issue #767-swingx: consistent string representation.
     * 
     * Here: test api on JXTable.
     */
    @Test
    public void testGetString() {
        JXTable table = new JXTable(new AncientSwingTeam());
        StringValue sv = new StringValue() {

            @Override
            public String getString(Object value) {
                if (value instanceof Color) {
                    Color color = (Color) value;
                    return "R/G/B: " + color.getRGB();
                }
                return StringValues.TO_STRING.getString(value);
            }
            
        };
        table.setDefaultRenderer(Color.class, new DefaultTableRenderer(sv));
        String text = table.getStringAt(0, 2);
        assertEquals(sv.getString(table.getValueAt(0, 2)), text);
    }
    
    
    @Test
    public void testCancelEditEnabled() {
        JXTable table = new JXTable(10, 3);
        table.editCellAt(0, 0);
        // sanity
        assertTrue(table.isEditing());
        assertEquals(table.isEditing(), table.getActionMap().get("cancel").isEnabled());
    }

    @Test
    public void testCancelEditDisabled() {
        JXTable table = new JXTable(10, 3);
        // sanity
        assertFalse(table.isEditing());
        assertEquals(table.isEditing(), table.getActionMap().get("cancel").isEnabled());
    }

    /**
     * NPE if Generic editor barks about constructor. Hacked around ...
     * 
     *  PENDING JW: too verbose ... strip down to essentials
     */
    @Test
    public void testGenericEditorNPE() {
        Date date = new Date();
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        Timestamp stamp = new Timestamp(date.getTime());
        Time time = new Time(date.getTime());
        DefaultTableModel model = new DefaultTableModel(1, 5) {

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
        model.setColumnIdentifiers(new Object[]{"Date - normal", "SQL Date", "SQL Timestamp", "SQL Time", "Date - as time"});
        model.setValueAt(date, 0, 0);
        model.setValueAt(sqlDate, 0, 1);
        model.setValueAt(stamp, 0, 2);
        model.setValueAt(time, 0, 3);
        model.setValueAt(date, 0, 4);
        JXTable table = new JXTable(model);
        table.editCellAt(0, 1);
    }

    /**
     * test that transferFocus methods try to stop edit.
     * 
     * Here: do nothing if !isTerminateEditOnFocusLost.
     *
     */
    @Test
    public void testFocusTransferBackwardTerminateEditFalse() {
        JXTable table = new JXTable(10, 2);
        table.setTerminateEditOnFocusLost(false);
        DefaultCellEditor editor = new DefaultCellEditor(new JTextField());        // need to replace generic editor - which fires twice
        table.setDefaultEditor(Object.class, editor);
        table.editCellAt(0, 0);
        // sanity
        assertTrue(table.isEditing());
        CellEditorReport report = new CellEditorReport();
        table.getCellEditor().addCellEditorListener(report);
        table.transferFocusBackward();
        assertTrue("table must be editing", table.isEditing());
        assertEquals("", 0, report.getEventCount());
    }
   
    /**
     * test that transferFocus methods try to stop edit.
     * 
     * Here: do nothing if !isTerminateEditOnFocusLost.
     *
     */
    @Test
    public void testFocusTransferForwardTerminateEditFalse() {
        JXTable table = new JXTable(10, 2);
        table.setTerminateEditOnFocusLost(false);
        DefaultCellEditor editor = new DefaultCellEditor(new JTextField());        // need to replace generic editor - which fires twice
        table.setDefaultEditor(Object.class, editor);
        table.editCellAt(0, 0);
        // sanity
        assertTrue(table.isEditing());
        CellEditorReport report = new CellEditorReport();
        table.getCellEditor().addCellEditorListener(report);
        table.transferFocus();
        assertTrue("table must be editing", table.isEditing());
        assertEquals("", 0, report.getEventCount());
    }

    /**
     * test that transferFocus methods try to stop edit.
     * 
     * Here: respect false on backward.
     *
     */
    @Test
    public void testFocusTransferBackwardStopEditingFalse() {
        JXTable table = new JXTable(10, 2);
        DefaultCellEditor editor = new DefaultCellEditor(new JTextField()){

            @Override
            public boolean stopCellEditing() {
                return false;
            }
            
        };
        // need to replace generic editor - which fires twice
        table.setDefaultEditor(Object.class, editor);
        table.editCellAt(0, 0);
        // sanity
        assertTrue(table.isEditing());
        CellEditorReport report = new CellEditorReport();
        table.getCellEditor().addCellEditorListener(report);
        table.transferFocusBackward();
        assertTrue("table must be editing", table.isEditing());
        assertEquals("", 0, report.getEventCount());
    }
    
    
    /**
     * test that transferFocus methods try to stop edit.
     * 
     * Here: respect editor false on forward.
     *
     */
    @Test
    public void testFocusTransferForwardStopEditingFalse() {
        JXTable table = new JXTable(10, 2);
        DefaultCellEditor editor = new DefaultCellEditor(new JTextField()){

            @Override
            public boolean stopCellEditing() {
                return false;
            }
            
        };
        // need to replace generic editor - which fires twice
        table.setDefaultEditor(Object.class, editor);
        table.editCellAt(0, 0);
        // sanity
        assertTrue(table.isEditing());
        CellEditorReport report = new CellEditorReport();
        table.getCellEditor().addCellEditorListener(report);
        table.transferFocus();
        assertTrue("table must be editing", table.isEditing());
        assertEquals("", 0, report.getEventCount());
    }
    

    /**
     * test that transferFocus methods try to stop edit.
     * 
     * Here: edit stopped and editor fires on backward.
     *
     */
    @Test
    public void testFocusTransferBackwardStopEditing() {
        JXTable table = new JXTable(10, 2);
        // need to replace generic editor - which fires twice
        table.setDefaultEditor(Object.class, new DefaultCellEditor(new JTextField()));
        table.editCellAt(0, 0);
        // sanity
        assertTrue(table.isEditing());
        CellEditorReport report = new CellEditorReport();
        table.getCellEditor().addCellEditorListener(report);
        table.transferFocusBackward();
        assertFalse("table must not be editing", table.isEditing());
        assertEquals("", 1, report.getEventCount());
        assertEquals("", 1, report.getStoppedEventCount());
    }
    

    /**
     * test that transferFocus methods try to stop edit.
     * 
     * Here: edit stopped and editor fired. 
     *
     */
    @Test
    public void testFocusTransferForwardStopEditing() {
        JXTable table = new JXTable(10, 2);
        // need to replace generic editor - which fires twice
        table.setDefaultEditor(Object.class, new DefaultCellEditor(new JTextField()));
        table.editCellAt(0, 0);
        // sanity
        assertTrue(table.isEditing());
        CellEditorReport report = new CellEditorReport();
        table.getCellEditor().addCellEditorListener(report);
        table.transferFocus();
        assertFalse("table must not be editing", table.isEditing());
        assertEquals("", 1, report.getEventCount());
        assertEquals("", 1, report.getStoppedEventCount());
    }
    
    

    /**
     * test that we have actions registered for forwared/backward
     * focus transfer.
     *
     */
    @Test
    public void testFocusTransferActions() {
        assertNotNull("must have forward action",
                table.getActionMap().get(JXTable.FOCUS_NEXT_COMPONENT));
        assertNotNull("must have backward action",
                table.getActionMap().get(JXTable.FOCUS_PREVIOUS_COMPONENT));
    }

    /**
     * test that we have bindings for forward/backward 
     * focusTransfer.
     *
     */
    @Test
    public void testFocusTransferKeyBinding() {
        JTable core = new JTable();
        Set<?> forwardKeys = core.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
        Set<?> backwardKeys = core.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS);
        for (Object key : forwardKeys) {
            InputMap map = table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
            assertNotNull("must have binding for forward focus transfer " + key, 
                    map.get((KeyStroke) key));
        }
        for (Object key : backwardKeys) {
            InputMap map = table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
            assertNotNull("must have binding for backward focus transfer " + key, 
                    map.get((KeyStroke) key));
        }
    }
    
    /**
     * test that we have no focusTransfer keys set.
     *
     */
    @Test
    public void testFocusTransferNoDefaultKeys() {
        assertTrue(table.getFocusTraversalKeys(
                KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS).isEmpty());
        assertTrue(table.getFocusTraversalKeys(
                KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS).isEmpty());
    }
    /**
     * test that pref scrollable width is updated after structure changed.
     *
     */
    @Test
    public void testPrefScrollableUpdatedOnStructureChanged() {
        JXTable compare = new JXTable(new AncientSwingTeam());
        Dimension compareDim = compare.getPreferredScrollableViewportSize();
        JXTable table = new JXTable(10, 6);
        Dimension initialDim = table.getPreferredScrollableViewportSize();
        assertFalse("configured must be different from default width", 
                compareDim.width == initialDim.width);
        table.setModel(compare.getModel());
        assertEquals(compareDim.width, table.getPreferredScrollableViewportSize().width);
    }
    /**
     * Issue #508-swingx: cleanup scrollable support.
     * 
     */
    @Test
    public void testVisibleRowCountUpdateSize() {
        JXTable table = new JXTable(10, 6);
        Dimension dim = table.getPreferredScrollableViewportSize();
        table.setVisibleRowCount(table.getVisibleRowCount() * 2);
        // change the pref width of a column, the pref scrollable width must not
        // be changed. This is testing the table internal reset code.
        TableColumnExt columnExt = table.getColumnExt(0);
        columnExt.setPreferredWidth(columnExt.getPreferredWidth() * 2);
        assertEquals(dim.height * 2, table.getPreferredScrollableViewportSize().height);
        assertEquals(dim.width, table.getPreferredScrollableViewportSize().width);
    }
    
    /**
     * Issue #508-swingx: cleanup scrollable support
     *
     */
    @Test
    public void testVisibleColumnCountUpdateSize() {
        JXTable table = new JXTable(10, 14);
        table.setVisibleColumnCount(6);
        Dimension dim = table.getPreferredScrollableViewportSize();
        table.setVisibleColumnCount(table.getVisibleColumnCount() * 2);
        assertEquals(dim.width * 2, table.getPreferredScrollableViewportSize().width);
        assertEquals(dim.height, table.getPreferredScrollableViewportSize().height);
    }
    
    /**
     * Issue #508-swingx: cleanup pref scrollable size.
     * test preference of explicit setting (over calculated).
     *
     */
    @Test
    public void testPrefScrollableSetPreference() {
        JXTable table = new JXTable(10, 6);
        Dimension dim = table.getPreferredScrollableViewportSize();
        Dimension other = new Dimension(dim.width + 20, dim.height + 20);
        table.setPreferredScrollableViewportSize(other);
        assertEquals(other, table.getPreferredScrollableViewportSize());
    }
    
    /**
     * Issue #508-swingx: cleanup pref scrollable size.
     * test that max number of columns used for the preferred 
     * scrollable width i getVisibleColumnCount 
     *
     */
    @Test
    public void testPrefScrollableWidthMoreColumns() {
        JXTable table = new JXTable(10, 7);
        table.setVisibleColumnCount(6);
        Dimension dim = table.getPreferredScrollableViewportSize();
        // sanity
        assertEquals(table.getVisibleColumnCount() + 1, table.getColumnCount());
        int width = 0;
        for (int i = 0; i < table.getVisibleColumnCount(); i++) {
            width += table.getColumn(i).getPreferredWidth();
        }
        assertEquals(width, dim.width);
    }
    
    /**
     * Issue #508-swingx: cleanup pref scrollable size.
     * test that max number of columns used for the preferred 
     * scrollable width i getVisibleColumnCount 
     *
     */
    @Test
    public void testPrefScrollableWidthLessColumns() {
        JXTable table = new JXTable(10, 5);
        table.setVisibleColumnCount(6);
        Dimension dim = table.getPreferredScrollableViewportSize();
        // sanity
        assertEquals(table.getVisibleColumnCount() - 1, table.getColumnCount());
        int width = 0;
        for (int i = 0; i < table.getColumnCount(); i++) {
            width += table.getColumn(i).getPreferredWidth();
        }
        width += 75;
        assertEquals(width, dim.width);
    }
    
    /**
     * test change back to default sizing (use-all)
     * 
     */
    @Test
    public void testVisibleColumnCountNegative() {
        JXTable table = new JXTable(10, 7);
        Dimension dim = table.getPreferredScrollableViewportSize();
        int visibleCount = table.getVisibleColumnCount();
        // custom
        table.setVisibleColumnCount(4);
        // change back to default
        table.setVisibleColumnCount(visibleCount);
        assertEquals(dim, table.getPreferredScrollableViewportSize());
    }
    
    /**
     * test default sizing: use all visible columns.
     *
     */
    @Test
    public void testPrefScrollableWidthDefault() {
       JXTable table = new JXTable(10, 7);
       Dimension dim = table.getPreferredScrollableViewportSize();
       assertEquals("default must use all visible columns", 
               table.getColumnCount() * 75, dim.width);
    }
    
    /**
     * Issue #508-swingx: cleanup pref scrollable size.
     * Sanity: test initial visible column count.
     *
     */
    @Test
    public void testDefaultVisibleColumnCount() {
        JXTable table = new JXTable(10, 6);
        assertEquals(-1, table.getVisibleColumnCount());
    }
    /**
     * Issue #508-swingx: cleanup pref scrollable size.
     * test custom setting of visible column count.
     * 
     */
    @Test
    public void testVisibleColumnCount() {
        JXTable table = new JXTable(30, 10);
        int visibleColumns = 7;
        table.setVisibleColumnCount(visibleColumns);
        assertEquals(visibleColumns, table.getVisibleColumnCount());
        Dimension dim = table.getPreferredScrollableViewportSize();
        assertEquals(visibleColumns * 75, dim.width);
    }
    
    
    /**
     * Issue #508-swingx: cleanup pref scrollable size.
     * test that column widths are configured after setModel.
     *
     */    
    @Test
    public void testPrefColumnSetModel() {
        JXTable compare = new JXTable(new AncientSwingTeam());
        // make sure the init is called
        compare.getPreferredScrollableViewportSize();
        // table with arbitrary model
        JXTable table = new JXTable(30, 7);
        // make sure the init is called
        table.getPreferredScrollableViewportSize();
        // following should init the column width ...
        table.setModel(compare.getModel());
        for (int i = 0; i < table.getColumnCount(); i++) {
            assertEquals("prefwidths must be same at index " + i, 
                    compare.getColumnExt(i).getPreferredWidth(),
                    table.getColumnExt(i).getPreferredWidth());
        }
    }

    /**
     * Test bound property visibleRowCount.
     *
     */
    @Test
    public void testVisibleRowCountProperty() {
        JXTable table = new JXTable(10, 7);
        int count = table.getVisibleRowCount();
        PropertyChangeReport report = new PropertyChangeReport();
        table.addPropertyChangeListener(report);
        table.setVisibleRowCount(count + 1);
        TestUtils.assertPropertyChangeEvent(report, "visibleRowCount", count, count+1);
    }
    /**
     * Test bound property visibleColumnCount.
     *
     */
    @Test
    public void testVisibleColumnCountProperty() {
        JXTable table = new JXTable(10, 7);
        int count = table.getVisibleColumnCount();
        PropertyChangeReport report = new PropertyChangeReport();
        table.addPropertyChangeListener(report);
        table.setVisibleColumnCount(count + 1);
        TestUtils.assertPropertyChangeEvent(report, "visibleColumnCount", count, count+1);
    }
    
    /**
     * test doc'ed behaviour: set visible row count must
     * throw on negative row.
     *
     */
    @Test
    public void testVisibleRowCountNegative() {
        JXTable table = new JXTable(10, 7);
        try {
            table.setVisibleRowCount(-2);
            fail("negative count must throw IllegalArgument");
        } catch (IllegalArgumentException e) {
            // expected exception
        }        
    }
    

    /**
     * Issue #547-swingx: return copy of pref scrollable size
     * 
     */
    @Test
    public void testPrefScrollableSafeCalculatedDim() {
        JXTable table = new JXTable(10, 6);
        // sanity: compare the normal dim returns
        assertNotSame("pref size must not be the same", 
                table.getPreferredSize(), table.getPreferredSize());
        assertNotSame("pref scrollable dim must not be the same", 
                table.getPreferredScrollableViewportSize(), 
                table.getPreferredScrollableViewportSize());
    }

    /**
     * Issue #547-swingx: return copy of pref scrollable size
     * This is a super prob - does use the dim as set.
     */
    @Test
    public void testPrefScrollableSafeFixedDim() {
        JXTable table = new JXTable(10, 6);
        Dimension dim = new Dimension(200, 400);
        // sanity: compare to super prf size when set
        table.setPreferredSize(dim);
        assertEquals(dim, table.getPreferredSize());
        assertNotSame(dim, table.getPreferredSize());
        table.setPreferredScrollableViewportSize(dim);
        assertNotSame("pref scrollable dim must not be the same", 
                dim, table.getPreferredScrollableViewportSize());
    }
    
    /**
     * Issue #547-swingx: pref scrollable height included header.
     *
     */
    @Test
    public void testPrefScrollableHeight() {
        JXTable table = new JXTable(10, 6);
        Dimension dim = table.getPreferredScrollableViewportSize();
        assertNotNull("pref scrollable must not be null", dim);
        assertEquals("scrollable height must no include header", 
                table.getVisibleRowCount() * table.getRowHeight(), dim.height);
    }     
    
    /**
     * Sanity: default visible row count == 20.
     *
     */
    @Test
    public void testDefaultVisibleRowCount() {
        JXTable table = new JXTable(10, 6);
        assertEquals(20, table.getVisibleRowCount());
    }
    /**
     * Issue #547-swingx: NPE in ColumnFactory configureColumnWidth 
     *    for hidden column
     *
     */
    @Test
    public void testPrefHiddenColumnNPE() {
        JXTable table = new JXTable(new AncientSwingTeam());
        TableColumnExt columnExt = table.getColumnExt(0);
        columnExt.setPrototypeValue("Jessesmariaandjosefsapperlottodundteufel");
        columnExt.setVisible(false);
        // NPE
        table.getColumnFactory().configureColumnWidths(table, columnExt);
    }
    
    /**
     * Issue #547-swingx: NPE in ColumnFactory configureColumnWidth 
     *    for empty table .. no: doesn't because 
     *    table.getCellRenderer(row,column) does not use the row
     *    coordinate - so the illegal argument doesn't hurt.
     *
     */
    @Test
    public void testPrefEmptyTableNPE() {
        JXTable table = new JXTable(0, 4);
        TableColumnExt columnExt = table.getColumnExt(0);
        columnExt.setPrototypeValue("Jessesmariaandjosefsapperlottodundteufel");
        // NPE
        table.getColumnFactory().configureColumnWidths(table, columnExt);
    }
    
    /**
     * Issue #547-swingx: hidden columns' pref width not initialized.
     *
     * PENDING: the default initialize is working as expected only
     *  if the config is done before setting the model, that is 
     *  in the ColumnFactory. Need public api to programatically
     *  trigger the init after the fact? 
     */
    @Test
    public void testPrefHiddenColumn() {
        JXTable table = new JXTable(new AncientSwingTeam());
        TableColumnExt columnExt = table.getColumnExt(0);
      columnExt.setPrototypeValue("Jessesmariaandjosefsapperlottodundteufel");
        TableCellRenderer renderer = table.getCellRenderer(0, 0);
        Component comp = renderer.getTableCellRendererComponent(null, columnExt.getPrototypeValue(), false, false, -1, -1);
        columnExt.setVisible(false);
        // make sure the column pref is initialized
        table.initializeColumnWidths();
        assertEquals("hidden column's pref must be set", 
                comp.getPreferredSize().width + table.getColumnMargin(), columnExt.getPreferredWidth());
    }

    /**
     * Issue #547-swingx: columns' pref width - header not taken 
     * if no prototype
     * 
     */
    @Test
    public void testPrefColumnTitle() {
        JXTable table = new JXTable(new AncientSwingTeam());
        TableColumnExt columnExt = table.getColumnExt(0);
        columnExt.setHeaderValue("Jessesmariaandjosefsapperlottodundteufel");
        TableCellRenderer renderer = table.getTableHeader().getDefaultRenderer();
        Component comp = renderer.getTableCellRendererComponent(table, columnExt.getHeaderValue(), false, false, -1, -1);
        // need to store the pref - header renderer is used during initialize!
        Dimension prefSize = comp.getPreferredSize();
        // make sure the column pref is initialized
        table.initializeColumnWidths();
        assertEquals("header must be measured", 
                prefSize.width + table.getColumnMargin(), columnExt.getPreferredWidth());
    }

    /**
     * Issue #547-swingx: columns' pref width - without
     * prototype the pref is minimally the (magic) standard if
     * the header doesn't exceed it.
     *
     */
    @Test
    public void testPrefStandardMinWithoutPrototype() {
        JXTable table = new JXTable(10, 6);
        TableColumnExt columnExt = table.getColumnExt(0);
        int standardWidth = columnExt.getPreferredWidth();
        // make sure the column pref is initialized
        table.getPreferredScrollableViewportSize();
        assertEquals("column pref width must be unchanged", 
                standardWidth, columnExt.getPreferredWidth());
    }
    
    /**
     * Issue #547-swingx: columns' pref width - added margin twice
     * if has prototype.
     * 
     * PENDING: the default initialize is working as expected only
     *  if the config is done before setting the model, that is 
     *  in the ColumnFactory. Need public api to programatically
     *  trigger the init after the fact? 
     */
    @Test
    public void testPrefColumnsDuplicateMargin() {
        JXTable table = new JXTable(new AncientSwingTeam());
        TableColumnExt columnExt = table.getColumnExt(0);
        // force the prototype longer than the title
        // to avoid that header measuring is triggered
        // header renderer can have bigger fonts
        columnExt.setPrototypeValue(columnExt.getTitle() + "longer");
        TableCellRenderer renderer = table.getCellRenderer(0, 0);
        Component comp = renderer.getTableCellRendererComponent(null, columnExt.getPrototypeValue(), false, false, -1, -1);
        // make sure the column pref is initialized
        table.initializeColumnWidths();
        assertEquals("column margin must be added once", table.getColumnMargin(), 
                columnExt.getPreferredWidth() - comp.getPreferredSize().width);
    }


    /**
     * core issue: JTable cannot cope with null selection background.
     *
     */
    @Test
    public void testSetSelectionBackground() {
        PropertyChangeReport report = new PropertyChangeReport();
        table.addPropertyChangeListener(report);
        Color oldBackground = table.getSelectionBackground();
        Color color = Color.RED;
        table.setSelectionBackground(color);
        assertFalse(oldBackground.equals(table.getSelectionBackground()));
        assertEquals(color, table.getSelectionBackground());
        TestUtils.assertPropertyChangeEvent(report, "selectionBackground", oldBackground, color);
    }
    
    /**
     * core issue: JTable cannot cope with null selection background.
     *
     */
    @Test
    public void testNullSelectionBackground() {
        table.setSelectionBackground(null);
    }

    /**
     * core issue: JTable cannot cope with null selection background.
     *
     */
    @Test
    public void testSetSelectionForeground() {
        PropertyChangeReport report = new PropertyChangeReport();
        table.addPropertyChangeListener(report);
        Color oldForeground = table.getSelectionForeground();
        Color color = Color.RED;
        table.setSelectionForeground(color);
        assertFalse(oldForeground.equals(table.getSelectionForeground()));
        assertEquals(color, table.getSelectionForeground());
        TestUtils.assertPropertyChangeEvent(report, "selectionForeground", oldForeground, color);
    }
    /**
     * core issue: JTable cannot cope with null selection background.
     *
     */
    @Test
    public void testNullSelectionForeground() {
        table.setSelectionForeground(null);
    }

    /**
     * Test default behaviour: hack around DefaultTableCellRenderer 
     * color memory is on. 
     *
     */
    @Test
    public void testDTCRendererHackEnabled() {
        JXTable table = new JXTable(10, 2);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        table.setDefaultRenderer(Object.class, renderer);
        table.prepareRenderer(renderer, 0, 0);
        assertEquals(Boolean.TRUE, table.getClientProperty(JXTable.USE_DTCR_COLORMEMORY_HACK));
        assertNotNull(renderer.getClientProperty("rendererColorMemory.background"));
        assertNotNull(renderer.getClientProperty("rendererColorMemory.foreground"));
    }

    /**
     * Test custom behaviour: hack around DefaultTableCellRenderer 
     * color memory is disabled.
     *
     */
    @Test
    public void testDTCRendererHackDisabled() {
        JXTable table = new JXTable(10, 2);
        table.putClientProperty(JXTable.USE_DTCR_COLORMEMORY_HACK, null);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        table.setDefaultRenderer(Object.class, renderer);
        table.prepareRenderer(renderer, 0, 0);
        assertNull(renderer.getClientProperty("rendererColorMemory.background"));
        assertNull(renderer.getClientProperty("rendererColorMemory.foreground"));
    }

    /**
     * Issue #282-swingx: compare disabled appearance of
     * collection views.
     *
     */
    @Test
    public void testDisabledRenderer() {
        JXList list = new JXList(new Object[] {"one", "two"});
        list.setEnabled(false);
        // sanity
        assertFalse(list.isEnabled());
        Component comp = list.getCellRenderer().getListCellRendererComponent(list, "some", 0, false, false);
        assertEquals(list.isEnabled(), comp.isEnabled());
        JXTable table = new JXTable(10, 2);
        table.setEnabled(false);
        // sanity
        assertFalse(table.isEnabled());
        comp = table.prepareRenderer(table.getCellRenderer(0, 0), 0, 0);
        assertEquals(table.isEnabled(), comp.isEnabled());
    }

    
    /**
     * Issue 372-swingx: table must cancel edit if column property 
     *   changes to not editable.
     * Here we test if the table actually canceled the edit.
     */
    @Test
    public void testTableCanceledEditOnColumnEditableChange() {
        JXTable table = new JXTable(10, 2);
        TableColumnExt columnExt = table.getColumnExt(0);
        table.editCellAt(0, 0);
        // sanity
        assertTrue(table.isEditing());
        assertEquals(0, table.getEditingColumn());
        TableCellEditor editor = table.getCellEditor();
        CellEditorReport report = new CellEditorReport();
        editor.addCellEditorListener(report);
        columnExt.setEditable(false);
        // sanity
        assertFalse(table.isCellEditable(0, 0));
        assertEquals("editor must have fired canceled", 1, report.getCanceledEventCount());
        assertEquals("editor must not have fired stopped",0, report.getStoppedEventCount());
    }
    
    
    /**
     * Issue 372-swingx: table must cancel edit if column property 
     *   changes to not editable.
     * Here we test if the table is not editing after editable property
     * of the currently edited column is changed to false.
     */
    @Test
    public void testTableNotEditingOnColumnEditableChange() {
        JXTable table = new JXTable(10, 2);
        TableColumnExt columnExt = table.getColumnExt(0);
        table.editCellAt(0, 0);
        // sanity
        assertTrue(table.isEditing());
        assertEquals(0, table.getEditingColumn());
        columnExt.setEditable(false);
        assertFalse(table.isCellEditable(0, 0));
        assertFalse("table must have terminated edit",table.isEditing());
    }
    
    /**
     * Issue 372-swingx: table must cancel edit if column property 
     *   changes to not editable.
     * Here we test if the table is still editing after the editability 
     * change of a non-edited column.
     * 
     */
    @Test
    public void testTableEditingOnNotEditingColumnEditableChange() {
        JXTable table = new JXTable(10, 2);
        int notEditingColumn = 1;
        TableColumnExt columnExt = table.getColumnExt(notEditingColumn);
        table.editCellAt(0, 0);
        // sanity
        assertTrue(table.isEditing());
        assertEquals(0, table.getEditingColumn());
        columnExt.setEditable(false);
        assertFalse(table.isCellEditable(0, notEditingColumn));
        assertTrue("table must still be editing", table.isEditing());
    }
    
    /**
     * Issue 372-swingx: table must cancel edit if column property 
     *   changes to not editable.
     * Here we test if the table is still editing after the editability 
     * change of a non-edited column, special case of hidden column. <p>
     * NOTE: doesn't really test, the columnModel doesn't
     * fire propertyChanges for hidden columns (see Issue #??-swingx)
     * 
     */
    @Test
    public void testTableEditingOnHiddenColumnEditableChange() {
        JXTable table = new JXTable(10, 2);
        int hiddenNotEditingColumn = 1;
        TableColumnExt columnExt = table.getColumnExt(hiddenNotEditingColumn);
        columnExt.setVisible(false);
        table.editCellAt(0, 0);
        // sanity
        assertTrue(table.isEditing());
        assertEquals(0, table.getEditingColumn());
        columnExt.setEditable(false);
        assertTrue("table must still be editing", table.isEditing());
    }

    /**
     * Test if default column creation and configuration is 
     * controlled completely by ColumnFactory.
     *
     */
    @Test
    public void testColumnConfigControlledByFactory() {
        ColumnFactory factory = new ColumnFactory() {

            @Override
            public void configureTableColumn(TableModel model, TableColumnExt columnExt) {
                assertNull(columnExt.getHeaderValue());
            }
            
        };
        table.setColumnFactory(factory);
        table.setModel(new DefaultTableModel(10, 2));
        assertEquals(null, table.getColumn(0).getHeaderValue());
    }
    /**
     * Sanity test for cleanup of createDefaultColumns.
     *
     */
    @Test
    public void testColumnFactory() {
        JXTable table = new JXTable(sortableTableModel);
        List<TableColumn> columns = table.getColumns();
        // for all model columns and in same order..
        assertEquals(sortableTableModel.getColumnCount(), columns.size());
        for (int i = 0; i < sortableTableModel.getColumnCount(); i++) {
            // there must have been inserted a TableColumnExt with 
            // title == headerValue == column name in model
            assertTrue(columns.get(i) instanceof TableColumnExt);
            assertEquals(sortableTableModel.getColumnName(i), 
                    String.valueOf(columns.get(i).getHeaderValue()));
        }
    }
    /**
     * Tests per-table ColumnFactory: bound property, reset to shared.
     * 
     */
    @Test
    public void testSetColumnFactory() {
        PropertyChangeReport report = new PropertyChangeReport();
        table.addPropertyChangeListener(report);
        ColumnFactory factory = createCustomColumnFactory();
        table.setColumnFactory(factory);
        assertEquals(1, report.getEventCount());
        assertTrue(report.hasEvents("columnFactory"));
        assertSame(factory, report.getLastNewValue("columnFactory"));
        assertSame(ColumnFactory.getInstance(), report.getLastOldValue("columnFactory"));
        report.clear();
        table.setColumnFactory(null);
        assertEquals(1, report.getEventCount());
        assertTrue(report.hasEvents("columnFactory"));
        assertSame(factory, report.getLastOldValue("columnFactory"));
        assertSame(ColumnFactory.getInstance(), report.getLastNewValue("columnFactory"));
    }
    
    /**
     * Tests per-table ColumnFactory: use individual.
     * 
     */
    @Test
    public void testUseCustomColumnFactory() {
        PropertyChangeReport report = new PropertyChangeReport();
        table.addPropertyChangeListener(report);
        ColumnFactory factory = createCustomColumnFactory();
        table.setColumnFactory(factory);
        // sanity...
        assertSame(factory, report.getLastNewValue("columnFactory"));
        table.setModel(new DefaultTableModel(2, 5));
        assertEquals(String.valueOf(0), table.getColumnExt(0).getTitle());
    }
    
    /**
     * Creates and returns a custom columnFactory for testing. 
     * Sets column title to modelIndex.
     * 
     * @return the custom ColumnFactory.
     */
    protected ColumnFactory createCustomColumnFactory() {
        ColumnFactory factory = new ColumnFactory() {

            @Override
            public void configureTableColumn(TableModel model,
                    TableColumnExt columnExt) {
                super.configureTableColumn(model, columnExt);
                columnExt.setTitle(String.valueOf(columnExt.getModelIndex()));
            }

        };
        return factory;
        
    }
    
    /**
     * Issue #4614616: renderer lookup broken for interface types.
     * 
     */
    @Test
    public void testNPERendererForInterface() {
        DefaultTableModel model = new DefaultTableModel(10, 2) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return Comparable.class;
            }
            
        };
        JXTable table = new JXTable(model);
        table.prepareRenderer(table.getCellRenderer(0, 0), 0, 0);
    }


    /**
     * Issue #366-swingx: enhance generic editor to take custom
     * textfield as argument.
     * 
     */
    @Test
    public void testGenericEditor() {
        JTextField textField = new JTextField(20);
        GenericEditor editor = new GenericEditor(textField);
        assertEquals("Table.editor", textField.getName());
        // sanity
        assertSame(textField, editor.getComponent());
    }
    
    /**
     * test default rowHeight calculation with default font.
     * Beware: the default height is the font's height + 2, but 
     * bounded by a "magic" minimum of 18.
     */
    @Test
    public void testRowHeightFromFont() {
        // sanity
        assertNull("no ui rowheight", UIManager.get("JXTable.rowHeight"));
        JXTable table = new JXTable();
        // wrong assumption: there's a "magic" minimum of 18!           
        int fontDerivedHeight = table.getFontMetrics(table.getFont()).getHeight() + 2;
        assertEquals("default rowHeight based on fontMetrics height " +
                        "plus top plus bottom border (== 2)", 
                        Math.max(18, fontDerivedHeight), 
                        table.getRowHeight());
    }
    
    
    /**
     * test default rowHeight calculation with bigger font.
     * Issue #1149-swingx: this test failed on Mac.
     * 
     * "expected 17, was 18" - running into the magic number (for
     * whatever reason), which wasn't accounted for. Changed to backout
     * if font not really changed to big.
     * 
     */
    @Test
    public void testRowHeightFromBigFont() {
        // sanity
        assertNull("no ui rowheight", UIManager.get("JXTable.rowHeight"));
        JXTable table = new JXTable();
        table.setFont(table.getFont().deriveFont(table.getFont().getSize() * 2f));
        table.updateUI();
        int bigRowHeight = table.getFontMetrics(table.getFont()).getHeight() + 2;
        if (bigRowHeight <= 18) {
            LOG.info("can't test - font not changed to big as expected but still " + bigRowHeight);
            return;
        }
        assertEquals("default rowHeight based on fontMetrics height " +
                        "plus top plus bottom border (== 2)", 
                        bigRowHeight, 
                        table.getRowHeight());
    }
    
    
    /**
     * Issue #359-swingx: table doesn't respect ui-setting of rowheight.
     * 
     * lower bound is enforced to "magic number", 18
     *
     */
    @Test
    public void testUIRowHeightLowerBound() {
        int tinyRowHeight = 5;
        UIManager.put("JXTable.rowHeight", tinyRowHeight);
        JXTable table = new JXTable();
        assertEquals("table must respect ui rowheight", tinyRowHeight, table.getRowHeight());
        
    }


    /**
     * Issue #359-swingx: table doesn't respect ui-setting of rowheight.
     * 
     * upper bound is taken correctly.
     */
    @Test
    public void testUIRowHeightUpperBound() {
        int monsterRowHeight = 50;
        UIManager.put("JXTable.rowHeight", monsterRowHeight);
        JXTable table = new JXTable();
        assertEquals("table must respect ui rowheight", monsterRowHeight, table.getRowHeight());
        
    }

    /**
     * Issue #342-swingx: default margins in JXTreeTable.
     * 
     * This is not only a treeTable issue: the coupling of 
     * margins to showing gridlines (and still get a "nice" 
     * looking selection) is not overly obvious in JTable as
     * well. Added convenience method to adjust margins to 
     * 0/1 if hiding/showing grid lines.
     *
     */
    @Test
    public void testShowGrid() {
        JXTable table = new JXTable(10, 3);
        // sanity: initial margins are (1, 1), grid on
        assertEquals(1, table.getRowMargin());
        assertTrue(table.getShowHorizontalLines());
        assertEquals(1, table.getColumnMargin());
        assertTrue(table.getShowVerticalLines());
        // hide grid
        boolean show = false;
        table.setShowGrid(show, show);
        assertEquals(0, table.getRowMargin());
        assertEquals(show, table.getShowHorizontalLines());
        assertEquals(0, table.getColumnMargin());
        assertEquals(show, table.getShowVerticalLines());
        
    }
    
    /**
     * Issue ??-swingx: NPE if tableChanged is messaged with a null event.
     *
     */
    @Test
    public void testNullTableEventNPE() {
        // don't throw null events
        table.tableChanged(null);
        assertFalse(table.isUpdate(null));
        assertFalse(table.isDataChanged(null));
        assertTrue(table.isStructureChanged(null));
        // correct detection of structureChanged
        TableModelEvent structureChanged = new TableModelEvent(table.getModel(), -1, -1);
        assertFalse(table.isUpdate(structureChanged));
        assertFalse(table.isDataChanged(structureChanged));
        assertTrue(table.isStructureChanged(structureChanged));
        // correct detection of insert/remove
        TableModelEvent insert = new TableModelEvent(table.getModel(), 0, 10, -1, TableModelEvent.INSERT);
        assertFalse(table.isUpdate(insert));
        assertFalse(table.isDataChanged(insert));
        assertFalse(table.isStructureChanged(insert));
        // correct detection of update
        TableModelEvent update = new TableModelEvent(table.getModel(), 0, 10);
        assertTrue(table.isUpdate(update));
        assertFalse(table.isDataChanged(update));
        assertFalse(table.isStructureChanged(update));
        // correct detection of dataChanged
        TableModelEvent dataChanged = new TableModelEvent(table.getModel());
        assertFalse(table.isUpdate(dataChanged));
        assertTrue(table.isDataChanged(dataChanged));
        assertFalse(table.isStructureChanged(dataChanged));
        
    }

    /**
     * test new mutable columnControl api.
     *
     */
    @Test
    public void testSetColumnControl() {
        JComponent columnControl = table.getColumnControl();
        assertTrue(columnControl instanceof ColumnControlButton);
        JComponent newControl = new JButton();
        PropertyChangeReport report = new PropertyChangeReport();
        table.addPropertyChangeListener(report);
        table.setColumnControl(newControl);
        assertSame(newControl, table.getColumnControl());
        assertEquals(1, report.getEventCount());
        assertEquals(1, report.getEventCount("columnControl"));
        assertSame(newControl, report.getLastNewValue("columnControl"));
    }
    
    /**
     * characterization tests: constructors and exceptions.
     *
     */
    @Test
    public void testConstructorsWithNullArguments() {
        try {
            new JXTable((Object[][]) null, (Object[]) null);
            fail("null arrays must throw NPE");
        } catch (NullPointerException e) {
            // nothing to do - expected
        } catch (Exception e) {
            fail("unexpected exception type (expected NPE)" + e);
        }
        try {
            new JXTable((Object[][]) null, new Object[] {  });
            fail("null arrays must throw NPE");
        } catch (NullPointerException e) {
            // nothing to do - expected
        } catch (Exception e) {
            fail("unexpected exception type (expected NPE)" + e);
        }
        try {
            new JXTable(new Object[][] {{ }, { }}, (Object[]) null);
            fail("null arrays throw NPE");
            
        } catch (NullPointerException e) {
            // nothing to do - expected
            
        } catch (Exception e) {
            fail("unexpected exception type (expected NPE)" + e);
        }
    }

    /**
     * expose JTable.autoStartsEdit.
     *
     */
    @Test
    public void testAutoStartEdit() {
        JXTable table = new JXTable(10, 2);
        assertTrue(table.isAutoStartEditOnKeyStroke());
        PropertyChangeReport report = new PropertyChangeReport();
        table.addPropertyChangeListener(report);
        table.setAutoStartEditOnKeyStroke(false);
        assertFalse("autoStart must be toggled to false", table.isAutoStartEditOnKeyStroke());
        // the following assumption is wrong because the old client property key is
        // different from the method name, leading to two events fired.
        // assertEquals(1, report.getEventCount());
        assertEquals(1, report.getEventCount("autoStartEditOnKeyStroke"));
    }
    
    /**
     * add editable property.
     *
     */
    @Test
    public void testEditable() {
        JXTable table = new JXTable(10, 2);
        assertTrue("default editable must be true", table.isEditable());
        PropertyChangeReport report = new PropertyChangeReport();
        table.addPropertyChangeListener(report);
        table.setEditable(!table.isEditable());
        assertFalse("editable must be toggled to false", table.isEditable());
        assertEquals(1, report.getEventCount());
        assertEquals(1, report.getEventCount("editable"));
    }
    
    /**
     * test effect of editable on cell editing.
     *
     */
    @Test
    public void testCellEditable() {
        JXTable table = new JXTable(10, 2);
        assertTrue("default table editable must be true", table.isEditable());
        assertTrue("default cell editable must be true", table.isCellEditable(0, 0));
        table.setEditable(!table.isEditable());
        assertFalse("editable must be toggled to false", table.isEditable());
        assertFalse("each cell must be not editable", table.isCellEditable(0, 0));
    }
    
    /**
     * 
     */
    @Test
    public void testSetValueCellNotEditable() {
        JXTable table = new JXTable(10, 2);
        Object value = table.getValueAt(0, 0);
        table.setEditable(false);
        // sanity...
        assertFalse("each cell must be not editable", table.isCellEditable(0, 0));
        table.setValueAt("wrong", 0, 0);
        assertEquals("cell value must not be changed", value, table.getValueAt(0, 0));
        
    }

    /**
     * Issue #262-swingx: expose terminateEditOnFocusLost as property.
     * 
     * setting client property is reflected in getter and results in event firing.
     *
     */
    @Test
    public void testGetTerminateEditOnFocusLost() {
       // sanity assert: setting client property set's property
       PropertyChangeReport report = new PropertyChangeReport();
       table.addPropertyChangeListener(report);
       table.putClientProperty("terminateEditOnFocusLost", !table.isTerminateEditOnFocusLost());
       assertEquals(table.getClientProperty("terminateEditOnFocusLost"), table.isTerminateEditOnFocusLost());
       assertEquals(1, report.getEventCount());
       assertEquals(1, report.getEventCount("terminateEditOnFocusLost"));
    }
    

    /**
     * Issue #262-swingx: expose terminateEditOnFocusLost as property.
     * 
     * default value is true.
     * 
     */
    @Test
    public void testInitialTerminateEditOnFocusLost() {
       assertTrue("terminate edit must be on by default", table.isTerminateEditOnFocusLost());
    }

    /**
     * Issue #262-swingx: expose terminateEditOnFocusLost as property.
     * 
     * setter is same as setting client property and results in event firing.
     */
    @Test
    public void testSetTerminateEditOnFocusLost() {
       // sanity assert: setting client property set's property
       PropertyChangeReport report = new PropertyChangeReport();
       table.addPropertyChangeListener(report);
       table.setTerminateEditOnFocusLost(!table.isTerminateEditOnFocusLost());
       assertEquals(table.getClientProperty("terminateEditOnFocusLost"), table.isTerminateEditOnFocusLost());
       assertEquals(1, report.getEventCount());
       assertEquals(1, report.getEventCount("terminateEditOnFocusLost"));
       assertEquals(Boolean.FALSE, report.getLastNewValue("terminateEditOnFocusLost"));
    }

    /**
     * sanity test while cleaning up: 
     * getColumns() should return the exact same
     * ordering as getColumns(false);
     *
     */
    @Test
    public void testColumnSequence() {
        JXTable table = new JXTable(10, 20);
        table.getColumnExt(5).setVisible(false);
        table.getColumnModel().moveColumn(table.getColumnCount() - 1, 0);
        assertEquals(table.getColumns(), table.getColumns(false));
    }

    
    /**
     * Issue #256-swingx: added fillsViewportHeight property.
     * 
     * check "fillsViewportHeight" property change fires event.
     *
     */
    @Test
    public void testDefaultFillsViewport() {
        JXTable table = new JXTable(10, 1);
        boolean fill = table.getFillsViewportHeight();
        assertTrue("fillsViewport is on by default", fill);
    }
    
    
    
    /**
     * Issue #256-swingX: viewport - don't change background
     * in configureEnclosingScrollPane.
     * 
     * 
     */
    @Test
    public void testUnchangedViewportBackground() {
        JXTable table = new JXTable(10, 2);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setSize(500, 500);
        Color viewportColor = scrollPane.getViewport().getBackground();
        Color tableColor = table.getBackground();
        if ((viewportColor != null) && viewportColor.equals(tableColor)) {
            LOG.info("cannot run test unchanged viewport background because \n" +
                        "viewport has same color as table. \n" +
                        "viewport: " + viewportColor + 
                        "\n table: " + tableColor);
            return;
        }
        scrollPane.setViewportView(table);
        table.configureEnclosingScrollPane();
        assertEquals("viewport background must be unchanged", 
                viewportColor, scrollPane.getViewport().getBackground());
        
        
    }

    /**
     * Issue #214-swingX: improved auto-resize.
     * 
     * 
     */
    @Test
    public void testTrackViewportWidth() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run trackViewportWidth - headless environment");
            return;
        }
        JXTable table = new JXTable(10, 2);
        table.setHorizontalScrollEnabled(true);
        Dimension tablePrefSize = table.getPreferredSize();
        JScrollPane scrollPane = new JScrollPane(table);
        JXFrame frame = wrapInFrame(scrollPane, "");
        frame.setSize(tablePrefSize.width * 2, tablePrefSize.height);
        frame.setVisible(true);
        assertEquals("table width must be equal to viewport", 
                table.getWidth(), scrollPane.getViewport().getWidth());
     }

    /**
     * Issue #214-swingX: improved auto-resize.
     * 
     *
     */
    @Test
    public void testSetHorizontalEnabled() {
        JXTable table = new JXTable(10, 2);
        table.setHorizontalScrollEnabled(true);
        assertTrue("enhanced resize property must be enabled", 
                table.isHorizontalScrollEnabled());
        assertHorizontalActionSelected(table, true);
    }

    private void assertHorizontalActionSelected(JXTable table, boolean selected) {
        Action showHorizontal = table.getActionMap().get(
                JXTable.HORIZONTALSCROLL_ACTION_COMMAND);
        assertEquals("horizontAction must be selected"  , selected, 
                ((BoundAction) showHorizontal).isSelected());
    }
 
    /**
     * Issue #214-swingX: improved auto-resize.
     * test autoResizeOff != intelliResizeOff 
     * after sequence a) set intelli, b) setAutoResize
     * 
     */
    @Test
    public void testNotTrackViewportWidth() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run trackViewportWidth - headless environment");
            return;
        }
        JXTable table = new JXTable(10, 2);
        table.setHorizontalScrollEnabled(true);
        table.setAutoResizeMode(JXTable.AUTO_RESIZE_OFF);
        Dimension tablePrefSize = table.getPreferredSize();
        JScrollPane scrollPane = new JScrollPane(table);
        JXFrame frame = wrapInFrame(scrollPane, "");
        frame.setSize(tablePrefSize.width * 2, tablePrefSize.height);
        frame.setVisible(true);
        assertEquals("table width must not be equal to viewport", 
               table.getPreferredSize().width, table.getWidth());
     }
 
    /**
     * Issue #214-swingX: improved auto-resize.
     * test autoResizeOff != intelliResizeOff
     * 
     */
    @Test
    public void testAutoResizeOffNotHorizontalScrollEnabled() {
        JXTable table = new JXTable(10, 2);
        table.setAutoResizeMode(JXTable.AUTO_RESIZE_OFF);
        // sanity: horizontal action must be selected
        assertHorizontalActionSelected(table, false);
        assertFalse("autoResizeOff must not enable enhanced resize", 
                table.isHorizontalScrollEnabled());
     }
 
    /**
     * Issue #214-swingX: improved auto-resize.
     * 
     * testing doc'd behaviour: horizscrollenabled toggles between
     * enhanced resizeOff and the resizeOn mode which had been active 
     * when toggling on. 
     * 
     */
    @Test
    public void testOldAutoResizeOn() {
        JXTable table = new JXTable(10, 2);
        int oldAutoResize = table.getAutoResizeMode();
        table.setHorizontalScrollEnabled(true);
        table.setHorizontalScrollEnabled(false);
        assertEquals("old on-mode must be restored", oldAutoResize, table.getAutoResizeMode());
       }
    
    /**
     * Issue #214-swingX: improved auto-resize.
     * 
     * testing doc'd behaviour: horizscrollenabled toggles between
     * enhanced resizeOff and the resizeOn mode which had been active 
     * when toggling on. Must not restore raw resizeOff mode.
     * 
     * 
     */
    @Test
    public void testNotOldAutoResizeOff() {
        JXTable table = new JXTable(10, 2);
        int oldAutoResize = table.getAutoResizeMode();
        table.setAutoResizeMode(JXTable.AUTO_RESIZE_OFF);
        table.setHorizontalScrollEnabled(true);
        table.setHorizontalScrollEnabled(false);
        assertEquals("old on-mode must be restored", oldAutoResize, table.getAutoResizeMode());
       }

    /**
     * Issue #214-swingX: improved auto-resize.
     * test autoResizeOff != intelliResizeOff 
     * after sequence a) set intelli, b) setAutoResize
     * 
     */
    @Test
    public void testAutoResizeOffAfterHorizontalScrollEnabled() {
        JXTable table = new JXTable(10, 2);
        table.setHorizontalScrollEnabled(true);
        // sanity: intelliResizeOff enabled
        assertTrue(table.isHorizontalScrollEnabled());
        // sanity: horizontal action must be selected
        assertHorizontalActionSelected(table, true);
        table.setAutoResizeMode(JXTable.AUTO_RESIZE_OFF);
        assertFalse("autoResizeOff must not enable enhanced resize", 
                table.isHorizontalScrollEnabled());
        // sanity: horizontal action must be selected
        assertHorizontalActionSelected(table, false);
     }

    /**
     * Issue 252-swingx: getColumnExt throws ClassCastException if tableColumn
     * is not of type TableColumnExt.
     *
     */
    @Test
    public void testTableColumnType() {
        table.setAutoCreateColumnsFromModel(false);
        table.setModel(new DefaultTableModel(2, 1));
        TableColumnModel columnModel = new DefaultTableColumnModel();
        columnModel.addColumn(new TableColumn(0));
        table.setColumnModel(columnModel);
        // valid column index must not throw exception
        TableColumnExt tableColumnExt = table.getColumnExt(0);
        assertNull("getColumnExt must return null on type mismatch", tableColumnExt);
    }


    /**
     * test contract: getColumnExt(int) throws ArrayIndexOutofBounds with 
     * invalid column index.
     *
     */
    @Test
    public void testTableColumnExtOffRange() {
        JXTable table = new JXTable(2, 1);
        try {
            table.getColumnExt(1);
            fail("accessing invalid column index must throw ArrayIndexOutofBoundExc");
        } catch (ArrayIndexOutOfBoundsException e) {
            // do nothing: contracted runtime exception
        } catch (Exception e) {
           fail("unexpected exception: " + e + "\n" +
              "accessing invalid column index must throw ArrayIndexOutofBoundExc");
        }
    }

    /**
     * test contract: getColumn(int) throws ArrayIndexOutofBounds with 
     * invalid column index.<p>
     *
     * Subtle autoboxing issue:  
     * JTable has convenience method getColumn(Object) to access by 
     * identifier, but doesn't have delegate method to columnModel.getColumn(int)
     * Clients assuming the existence of a direct delegate no longer get a
     * compile-time error message in 1.5 due to autoboxing. 
     * Furthermore, the runtime exception is unexpected (IllegalArgument
     * instead of AIOOB). <p>
     * 
     * Added getColumn(int) to JXTable api to solve.
     * 
     */
    @Test
    public void testTableColumnOffRange() {
        JXTable table = new JXTable(2, 1);
        try {
            table.getColumn(1);
            fail("accessing invalid column index must throw ArrayIndexOutofBoundExc");
        } catch (ArrayIndexOutOfBoundsException e) {
            // do nothing: contracted runtime exception
        } catch (Exception e) {
           fail("unexpected exception: " + e + "\n" +
              "accessing invalid column index must throw ArrayIndexOutofBoundExc");
        }
    }


    /**
     * Issue #251-swingx: JXTable doesn't respect TableColumn editability.
     * report, test and fix by nicfagn (Nicola Fagnani),
     *
     */
    @Test
    public void testTableColumnEditable() {
        DefaultTableModel model = new DefaultTableModel( 2, 2 );
        JXTable table = new JXTable( model );

        // DefaultTableModel allows to edit its cells.
        for( int i = 0; i < model.getRowCount(); i++ ) {
            for( int j = 0; j < model.getRowCount(); j++ ) {
                assertEquals(
                    "cell (" + i + "," + j + ") must be editable", 
                    true, table.isCellEditable( i, j ) );
            }
        }        

        // First column not editable.
        int column = 0;
        table.getColumnExt( column ).setEditable( false );
        for( int i = 0; i < model.getRowCount(); i++ ) {
            for( int j = 0; j < model.getRowCount(); j++ ) {
                assertEquals(
                    "cell (" + i + "," + j + ") must " +
                    (j == column ? "not" : "") + " be editable", 
                    !(j == column), table.isCellEditable( i, j ) );
            }
        }        
        table.getColumnExt( column ).setEditable( true );
                
        // Second column not editable.
        column = 1;
        table.getColumnExt( column ).setEditable( false );
        for( int i = 0; i < model.getRowCount(); i++ ) {
            for( int j = 0; j < model.getRowCount(); j++ ) {
                assertEquals(
                    "cell (" + i + "," + j + ") must " +
                    (j == column ? "not" : "") + " be editable", 
                    !(j == column), table.isCellEditable( i, j ) );
            }
        }        
        table.getColumnExt( column ).setEditable( true );
        
    }


    /**
     * test if LinkController/executeButtonAction is properly registered/unregistered on
     * setRolloverEnabled.
     *
     */
    @Test
    public void testLinkControllerListening() {
        JXTable table = new JXTable();
        table.setRolloverEnabled(true);
        assertNotNull("LinkController must be listening", getLinkControllerAsPropertyChangeListener(table, RolloverProducer.CLICKED_KEY));
        assertNotNull("LinkController must be listening", getLinkControllerAsPropertyChangeListener(table, RolloverProducer.ROLLOVER_KEY));
        assertNotNull("execute button action must be registered", table.getActionMap().get(RolloverController.EXECUTE_BUTTON_ACTIONCOMMAND));
        table.setRolloverEnabled(false);
        assertNull("LinkController must not be listening", getLinkControllerAsPropertyChangeListener(table, RolloverProducer.CLICKED_KEY ));
        assertNull("LinkController must be listening", getLinkControllerAsPropertyChangeListener(table, RolloverProducer.ROLLOVER_KEY));
        assertNull("execute button action must be de-registered", table.getActionMap().get(RolloverController.EXECUTE_BUTTON_ACTIONCOMMAND));
    }
    
    private PropertyChangeListener getLinkControllerAsPropertyChangeListener(JXTable table, String propertyName) {
        PropertyChangeListener[] listeners = table.getPropertyChangeListeners(propertyName);
        for (int i = 0; i < listeners.length; i++) {
            if (listeners[i] instanceof TableRolloverController<?>) {
                return (TableRolloverController<?>) listeners[i];
            }
        }
        return null;
    }
    
    

    /**
     * Issue #180-swingx: outOfBoundsEx if testColumn is hidden.
     *
     */
    @Test
    public void testHighlighterHiddenTestColumn() {
        JXTable table = new JXTable(sortableTableModel);
        table.getColumnExt(0).setVisible(false);
        Highlighter highlighter = new ColorHighlighter(new PatternPredicate("a", 0), null,
                Color.RED);
        ComponentAdapter adapter = table.getComponentAdapter();
        adapter.row = 0;
        adapter.column = 0;
        highlighter.highlight(new JLabel(), adapter);
    }
    
    /**
     * Issue #54: hidden columns not removed on setModel.
     *
     */
    @Test
    public void testRemoveAllColumsAfterModelChanged() {
        JXTable table = new JXTable(sortableTableModel);
        TableColumnExt columnX = table.getColumnExt(0);
        columnX.setVisible(false);
        table.setModel(new DefaultTableModel());
        assertEquals("all columns must have been removed", 0, table.getColumnCount(true));
        assertEquals("all columns must have been removed", 
                table.getColumnCount(), table.getColumnCount(true));
    }
    

    /**
     * Issue #165-swingx: IllegalArgumentException when
     * hiding/reshowing columns "at end" of column model.
     *
     */
    @Test
    public void testHideShowLastColumns() {
        JXTable table = new JXTable(10, 3);
        TableColumnExt ext = table.getColumnExt(2);
        for (int i = table.getModel().getColumnCount() - 1; i > 0; i--) {
           table.getColumnExt(i).setVisible(false); 
        }
        ext.setVisible(true);
    }

    /**
     * Issue #155-swingx: lost setting of initial scrollBarPolicy.
     *
     */
    @Test
    public void testConserveVerticalScrollBarPolicy() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run conserveVerticalScrollBarPolicy - headless environment");
            return;
        }
        JXTable table = new JXTable(0, 3);
        JScrollPane scrollPane1 = new JScrollPane(table);
        scrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        JXFrame frame = new JXFrame();
        frame.add(scrollPane1);
        frame.setSize(500, 400);
        frame.setVisible(true);
        assertEquals("vertical scrollbar policy must be always", 
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                scrollPane1.getVerticalScrollBarPolicy());
    }


    /**
     * Issue #197: JXTable pattern search differs from 
     * PatternHighlighter/Filter.
     * 
     */
    @Test
    public void testRespectPatternInSearch() {
        JXTable table = new JXTable(createAscendingModel(0, 11));
        int row = 1;
        String lastName = table.getValueAt(row, 0).toString();
        Pattern strict = Pattern.compile("^" + lastName + "$");
        int found = table.getSearchable().search(strict, -1, false);
        assertEquals("found must be equal to row", row, found);
        found = table.getSearchable().search(strict, found, false);
        assertEquals("search must fail", -1, found);
    }

    /**
     * Issue #54-swingx: hidden columns not removed.
     *
     */
    @Test
    public void testRemoveAllColumns() {
        JXTable table = new JXTable(sortableTableModel);
        TableColumnExt columnX = table.getColumnExt(0);
        columnX.setVisible(false);
        // set empty model
        table.setModel(new DefaultTableModel(0, 0));
        assertEquals("all columns must have been removed", 
                table.getColumnCount(), table.getColumnCount(true));
    }
    
    
    /**
     * testing contract of getColumnExt.
     *
     */
    @Test
    public void testColumnExt() {
        JXTable table = new JXTable(sortableTableModel);
        /// arrgghhh... autoboxing ?
//        Object zeroName = table.getColumn(0).getIdentifier();
        Object zeroName = table.getColumnModel().getColumn(0).getIdentifier();
        Object oneName = table.getColumnModel().getColumn(1).getIdentifier();
        TableColumn column = table.getColumn(zeroName);
        ((TableColumnExt) column).setVisible(false);
        try {
            // access the invisible column by the inherited method
            table.getColumn(zeroName);
            fail("table.getColumn(identifier) guarantees to fail if identifier "
                    + "is unknown or column is hidden");
        } catch (Exception e) {
            // this is what we expect
        }
        // access the invisible column by new method
        TableColumnExt columnZero = table.getColumnExt(zeroName);
        // sanity..
        assertNotNull(columnZero);
        int viewIndexZero = table.convertColumnIndexToView(columnZero
                .getModelIndex());
        assertTrue("viewIndex must be negative for invisible", viewIndexZero < 0);
        // a different way to state the same
        assertEquals(columnZero.isVisible(), viewIndexZero >= 0);
        TableColumnExt columnOne = table.getColumnExt(oneName);
        // sanity..
        assertNotNull(columnOne);
        int viewIndexOne = table.convertColumnIndexToView(columnOne
                .getModelIndex());
        assertTrue("viewIndex must be positive for visible", viewIndexOne >= 0);
        assertEquals(columnOne.isVisible(), viewIndexOne >= 0);
    }
    /**
     * Issue #189, #214: Sorter fails if content is comparable with mixed types
     * 
     */
    @Test
    public void testMixedComparableTypes() {
        
        Object[][] rowData = new Object[][] {
                new Object[] { Boolean.TRUE, new Integer(2) },
                new Object[] { Boolean.TRUE, "BC" } };
        String[] columnNames = new String[] { "Critical", "Task" };
        DefaultTableModel model =  new DefaultTableModel(rowData, columnNames);
        final JXTable table = new JXTable(model);
        table.toggleSortOrder(1);
    }   
    
    /**
     * Issue #189, #214: Sorter fails if content is 
     * mixed comparable/not comparable
     *
     */
    @Test
    public void testMixedComparableTypesWithNonComparable() {
        
        Object[][] rowData = new Object[][] {
                new Object[] { Boolean.TRUE, new Integer(2) },
                new Object[] { Boolean.TRUE, new Object() } };
        String[] columnNames = new String[] { "Critical", "Task" };
        DefaultTableModel model =  new DefaultTableModel(rowData, columnNames);
        final JXTable table = new JXTable(model);
        table.toggleSortOrder(1);
    }   

    @Test
    public void testIncrementalSearch() {
        JXTable table = new JXTable(createAscendingModel(10, 10));
        int row = 0;
        String ten = table.getValueAt(row, 0).toString();
        // sanity assert
        assertEquals("10", ten);
        int found = table.getSearchable().search("1", -1);
        assertEquals("must have found first row", row, found);
        int second = table.getSearchable().search("10", found);
        assertEquals("must have found incrementally at same position", found, second);
    }
    
    /**
     * Issue #196: backward search broken.
     *
     */
    @Test
    public void testBackwardSearch() {
        JXTable table = new JXTable(createAscendingModel(0, 10));
        int row = 1;
        String lastName = table.getValueAt(row, 0).toString();
        int found = table.getSearchable().search(Pattern.compile(lastName), -1, true);
        assertEquals(row, found);
    }

    /**
     * Issue #174: componentAdapter.hasFocus() looks for anchor instead of lead.
     * @throws InvocationTargetException 
     * @throws InterruptedException 
     *
     */
    @Test
    public void testLeadFocusCell() throws InterruptedException, InvocationTargetException {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run leadFocusCell - headless environment");
            return;
        }
        final JXTable table = new JXTable();
        table.setModel(createAscendingModel(0, 10));
        final JXFrame frame = new JXFrame();
        frame.add(table);
        frame.pack();
        frame.setVisible(true);
        table.requestFocus();
        table.addRowSelectionInterval(table.getRowCount() - 2, table.getRowCount() - 1);
        final int leadRow = table.getSelectionModel().getLeadSelectionIndex();
        int anchorRow = table.getSelectionModel().getAnchorSelectionIndex();
        table.addColumnSelectionInterval(0, 0);
        final int leadColumn = table.getColumnModel().getSelectionModel().getLeadSelectionIndex();
        int anchorColumn = table.getColumnModel().getSelectionModel().getAnchorSelectionIndex();
        assertEquals("lead must be last row", table.getRowCount() - 1, leadRow);
        assertEquals("anchor must be second last row", table.getRowCount() - 2, anchorRow);
        assertEquals("lead must be first column", 0, leadColumn);
        assertEquals("anchor must be first column", 0, anchorColumn);
        // take a nap to make sure we are created before testing for focus on linux
        // w/o this the test will intermittently fail on systems using kernel 2.6 and sun java 6
        Thread.sleep(500);
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                ComponentAdapter adapter = table.getComponentAdapter();
                adapter.row = leadRow;
                adapter.column = leadColumn;
                // difficult to test - hasFocus() implies that the table isFocusOwner()
                try {
                    assertTrue("adapter must have focus for leadRow/Column: " + adapter.row + "/" + adapter.column, 
                            adapter.hasFocus());
                } finally {
                    frame.dispose();
                }

            }
        });
    }
    

    /**
     * Test default editors types as expected.
     *
     */
    @Test
    public void testLazyEditorsByClass() {
        JXTable table = new JXTable();
        assertEquals("default Boolean editor", JXTable.BooleanEditor.class, table.getDefaultEditor(Boolean.class).getClass());
        assertEquals("default Number editor", NumberEditorExt.class, table.getDefaultEditor(Number.class).getClass());
        assertEquals("default Double editor", NumberEditorExt.class, table.getDefaultEditor(Double.class).getClass());
    }

    
    /**
     * Convenience: type cast of default rowSorter.
     * @param table
     * @return
     */
    private static TableSortController<? extends TableModel> getSortController(JXTable table) {
        return (TableSortController<? extends TableModel>) table.getRowSorter();
    }

//---------------------------- factory, convenience models, set-up ...
    
    /**
     * returns a tableModel with count rows filled with
     * ascending integers in first column
     * starting from startRow.
     * @param startRow the value of the first row
     * @param count the number of rows
     * @return
     */
    protected DefaultTableModel createAscendingModel(int startRow, int count) {
        DefaultTableModel model = new DefaultTableModel(count, 4) {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 0 ? Integer.class : super.getColumnClass(column);
            }
        };
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(new Integer(startRow++), i, 0);
        }
        return model;
    }

    
    /**
     * returns a tableModel with count rows filled with
     * ascending integers in first/last column depending on fillLast
     * starting from startRow.
     * with columnCount columns
     * @param startRow the value of the first row
     * @param rowCount the number of rows
     * @param columnCount the number of columns
     * @param fillLast boolean to indicate whether to ill the value in the first
     *   or last column
     * @return a configured DefaultTableModel.
     */
    protected DefaultTableModel createAscendingModel(int startRow, final int rowCount, 
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
    

//----------------------- test data for exposing #171 (Tim Dilks)
    

    /**
     * test object to map in test table model.
     */
    static class RowObject {

        private String data1;
        private boolean editable;

        public RowObject(String data1, boolean editable) {
            this.data1 = data1;
            this.editable = editable;
        }

        public String getData1() {
            return data1;
        }
      
        public boolean isEditable() {
            return editable;
        }

    }

    /** 
     *  test TableModel wrapping RowObject.
     */
    static class RowObjectTableModel extends AbstractTableModel {

        List<?> data;

        public RowObjectTableModel(List<?> data) {
            this.data = data;
        }

        public RowObject getRowObject(int row) {
            return (RowObject) data.get(row);
        }
        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public int getRowCount() {
            return data.size();
        }

        @Override
        public Object getValueAt(int row, int col) {
            RowObject object = getRowObject(row);
            switch (col) {
                case 0 :
                    return object.getData1();
                case 1 :
                    return object.isEditable() ? "EDITABLE" : "NOT EDITABLE";
                default :
                    return null;
            }
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return getRowObject(row).isEditable();
        }
    }

    

    /**
     * Creates and returns a StringValue which maps a Color to it's R/G/B rep, 
     * prepending "R/G/B: "
     * 
     * @return the StringValue for color.
     */
    private StringValue createColorStringValue() {
        StringValue sv = new StringValue() {

            @Override
            public String getString(Object value) {
                if (value instanceof Color) {
                    Color color = (Color) value;
                    return "R/G/B: " + color.getRGB();
                }
                return StringValues.TO_STRING.getString(value);
            }
            
        };
        return sv;
    }
    

    public static class DynamicTableModel extends AbstractTableModel {
        private Object columnSamples[];
        private Object columnSamples2[];
        public URL linkURL;

        public static final int IDX_COL_LINK = 6;

        public DynamicTableModel() {
            try {
                linkURL = new URL("http://www.sun.com");
            }
            catch (MalformedURLException ex) {
                throw new RuntimeException(ex);
            }

            columnSamples = new Object[12];
            columnSamples[0] = new Integer(0);
            columnSamples[1] = "Simple String Value";
            columnSamples[2] = new Integer(1000);
            columnSamples[3] = Boolean.TRUE;
            columnSamples[4] = new Date(100);
            columnSamples[5] = new Float(1.5);
            columnSamples[IDX_COL_LINK] = new LinkModel("Sun Micro", "_blank", linkURL);
            columnSamples[7] = new Integer(3023);
            columnSamples[8] = "John Doh";
            columnSamples[9] = "23434 Testcase St";
            columnSamples[10] = new Integer(33333);
            columnSamples[11] = Boolean.FALSE;

            columnSamples2 = new Object[12];
            columnSamples2[0] = new Integer(0);
            columnSamples2[1] = "Another String Value";
            columnSamples2[2] = new Integer(999);
            columnSamples2[3] = Boolean.FALSE;
            columnSamples2[4] = new Date(333);
            columnSamples2[5] = new Float(22.22);
            columnSamples2[IDX_COL_LINK] = new LinkModel("Sun Web", "new_frame", linkURL);
            columnSamples[7] = new Integer(5503);
            columnSamples[8] = "Jane Smith";
            columnSamples[9] = "2343 Table Blvd.";
            columnSamples[10] = new Integer(2);
            columnSamples[11] = Boolean.TRUE;

        }

        public DynamicTableModel(Object columnSamples[]) {
            this.columnSamples = columnSamples;
        }

        @Override
        public Class<?> getColumnClass(int column) {
            return columnSamples[column].getClass();
        }

        @Override
        public int getRowCount() {
            return 1000;
        }

        @Override
        public int getColumnCount() {
            return columnSamples.length;
        }

        @Override
        public Object getValueAt(int row, int column) {
            Object value;
            if (row % 3 == 0) {
                value = columnSamples[column];
            }
            else {
                value = columnSamples2[column];
            }
            return column == 0 ? new Integer(row >> 3) :
                column == 3 ? new Boolean(row % 2 == 0) : value;
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return (column == 1);
        }

        @Override
        public void setValueAt(Object aValue, int row, int column) {
            if (column == 1) {
                if (row % 3 == 0) {
                    columnSamples[column] = aValue;
                }
                else {
                    columnSamples2[column] = aValue;
                }
            }
            this.fireTableDataChanged();
        }
    }
    
    // test per-column highlighting
    private static class TestingHighlighter extends AbstractHighlighter {
        private List<Highlighter> events;
        
        public TestingHighlighter(List<Highlighter> events) {
            this.events = events;
        }
        
        @Override
        protected Component doHighlight(Component component, ComponentAdapter adapter) {
            events.add(this);
            return component;
        }
    }
    
    @Test
    public void testColumnHighlighting() {
        JXTable table = new JXTable(tableModel);
        List<Highlighter> events = new ArrayList<Highlighter>();
        
        Highlighter tableHighlighter = new TestingHighlighter(events);
        Highlighter columnHighlighter = new TestingHighlighter(events);
        
        //sanity check
        assertEquals(0, events.size());
        
        table.addHighlighter(tableHighlighter);
        table.getColumnExt(0).addHighlighter(columnHighlighter);
        
        //explicity prepare the renderer
        table.prepareRenderer(new DefaultTableCellRenderer(), 0, 0);
        
        assertEquals(2, events.size());
        assertSame(events.get(0), tableHighlighter);
        assertSame(events.get(1), columnHighlighter);
        
        events.clear();
        
        //explicity prepare the renderer
        table.prepareRenderer(new DefaultTableCellRenderer(), 0, 1);
        
        assertEquals(1, events.size());
        assertSame(events.get(0), tableHighlighter);
    }
    
    @Before
    public void setUpJu4() throws Exception {
        // just a little conflict between ant and maven builds
        // junit4 @before methods needs to be public, while
        // junit3 setUp() inherited from super is protected
        this.setUp();
    }
    
    @Override
    protected void setUp() throws Exception {
       super.setUp();
        // set loader priority to normal
        if (tableModel == null) {
            tableModel = new DynamicTableModel();
        }
        sortableTableModel = new AncientSwingTeam();
        // make sure we have the same default for each test
        defaultToSystemLF = true;
        setSystemLF(defaultToSystemLF);
        uiTableRowHeight = UIManager.get("JXTable.rowHeight");
        sv = createColorStringValue();
        table = new JXTable();
    }

    
    @Override
    @After
    public void tearDown() throws Exception {
        UIManager.put("JXTable.rowHeight", uiTableRowHeight);
        super.tearDown();
    }

}
