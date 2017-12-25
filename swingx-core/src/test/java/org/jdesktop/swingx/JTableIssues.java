/*
 * $Id: JTableIssues.java 4310 2013-08-27 10:38:06Z kleopatra $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.DefaultRowSorter;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.jdesktop.swingx.JXTableUnitTest.TakeItAllDummy;
import org.jdesktop.swingx.JXTableUnitTest.ThrowingDummy;
import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.sort.RowFilters;
import org.jdesktop.swingx.test.XTestUtils;
import org.jdesktop.test.AncientSwingTeam;
import org.jdesktop.test.CellEditorReport;
import org.jdesktop.test.ListSelectionReport;
import org.jdesktop.test.PropertyChangeReport;
import org.jdesktop.test.TestUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

import static org.jdesktop.swingx.JXTableUnitTest.*;

/**
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
@SuppressWarnings({ "rawtypes", "unchecked" })
public class JTableIssues extends InteractiveTestCase {
    /**
     * 
     */
    private static final String ALTERNATE_ROW_COLOR = "Table.alternateRowColor";
    private static final Logger LOG = Logger.getLogger(JTableIssues.class
            .getName());
    
    public static void main(String args[]) throws Exception {
        setLAF("Nimbus");
      JTableIssues test = new JTableIssues();
      try {
//        test.runInteractiveTests();
//          test.runInteractiveTests("interactive.*ColumnControl.*");
//          test.runInteractiveTests("interactive.*Edit.*");
//          test.runInteractiveTests("interactive.*Sort.*");
//          test.runInteractiveTests("interactive.*EditOnFocusLost.*");
//          test.runInteractive("SortModelSelection");
          test.runInteractive("NimbusCheckBox");
      } catch (Exception e) {
          System.err.println("exception when executing interactive tests:");
          e.printStackTrace();
      }
  }
    
    /**
     * Quick check: Nimbus default striping doesn't stripe checkbox
     */
    public void interactiveNimbusCheckBox() {
        TableModel model = new DefaultTableModel(2, 2) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : super.getColumnClass(columnIndex);
            }
          
            
        };
        // JW: sequence matters - first load addon removes the alternate from
        // normal handling
        JTable table = new JTable(model);
//        JXTable xtable = new JXTable(model);
        showWithScrollingInFrame(table, "core");
    }
    
    /**
     * Issue #1536-swingx: AIOOB on restoring selection with filter
     * 
     */
    @Test
    public void testSelectionWithFilterXTable() {
        DefaultTableModel model = new DefaultTableModel(0, 1);
        // a model with 3 elements is the minimum where to demonstrate
        // the bug
        int last = 2;
        for (int i = 0; i <= last; i++) {
            model.addRow(new Object[]{i});
        }
        JTable table = new JXTable(model);
//        table.setAutoCreateRowSorter(true);
        // set selection at the end
        table.setRowSelectionInterval(last, last);
        // exclude rows based on identifier
        final RowFilter filter = new RowFilters.GeneralFilter() {
            
            List excludes = Arrays.asList(0);
            @Override
            protected boolean include(
                    Entry<? extends Object, ? extends Object> entry,
                    int index) {
                return !excludes.contains(entry.getIdentifier());
            }
            
        };
        ((DefaultRowSorter) table.getRowSorter()).setRowFilter(filter);
        // insertRow _before or at_ selected model index, such that
        // endIndex (in event) > 1
        model.insertRow( 2, new Object[]{"x"});
    }
    
    /**
     * Issue #1536-swingx: AIOOB on restoring selection with filter
     * 
     */
    @Test
    public void testSelectionWithFilterTable() {
        DefaultTableModel model = new DefaultTableModel(0, 1);
        // a model with 3 elements is the minimum where to demonstrate
        // the bug
        int last = 2;
        for (int i = 0; i <= last; i++) {
            model.addRow(new Object[]{i});
        }
        JTable table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        // set selection at the end
        table.setRowSelectionInterval(last, last);
        // exclude rows based on identifier
        final RowFilter filter = new RowFilters.GeneralFilter() {

            List excludes = Arrays.asList(0);
            @Override
            protected boolean include(
                    Entry<? extends Object, ? extends Object> entry,
                    int index) {
                return !excludes.contains(entry.getIdentifier());
            }
            
        };
        ((DefaultRowSorter) table.getRowSorter()).setRowFilter(filter);
        // insertRow _before or at_ selected model index, such that
        // endIndex (in event) > 1
        model.insertRow( 2, new Object[]{"x"});
    }
    
    /**
     * Issue #1536-swingx: AIOOB on restoring selection with filter
     * This is a core issue, sneaked into ListSortUI by c&p
     * 
     * Analyzed by reporter to incorrect method usage in SortManager
     * cacheSelection: selectionModel.insert/removeIndexInterval length of range 
     * but gets last index of range.
     */
    public void interactiveSortModelSelection() {
        final BulkTableModel model = new BulkTableModel(0, 1) {
            
        };
        for (int i = 0; i < 10; i++) {
            model.addRow(new Object[]{i});
        }
        final JTable table = new JTable(model);
//        table.setSortOrderCycle(SortOrder.ASCENDING, SortOrder.DESCENDING, SortOrder.UNSORTED);
        table.setAutoCreateRowSorter(true);
        JXFrame frame = wrapWithScrollingInFrame(table, "sort bug");
        Action add = new AbstractAction("add") {
            int count = model.getRowCount();
            @Override
            public void actionPerformed(ActionEvent e) {
                int selected = table.getSelectedRow();
                if (true) {//(selected < 0) {
                    selected = model.getRowCount() - 3;
                }
                model.insertRowsAt(selected 
                        , new Object[] {count++}
                        , new Object[] {count++}
                        , new Object[] {count++}
                        );
            }
        };
        addAction(frame, add);
        final RowFilter filter = new RowFilters.GeneralFilter() {

            List excludes = Arrays.asList(4, 5, 6);
            @Override
            protected boolean include(
                    Entry<? extends Object, ? extends Object> entry,
                    int index) {
                return !excludes.contains(entry.getIdentifier());
            }
            
        };
        Action toggleFilter = new AbstractAction("filter") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultRowSorter sorter = (DefaultRowSorter) table.getRowSorter();
                sorter.setRowFilter(sorter.getRowFilter() != null ?
                        null : filter
                        );
            }
        };
        addAction(frame, toggleFilter);
        
        Action unsort = new AbstractAction("unsort") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultRowSorter sorter = (DefaultRowSorter) table.getRowSorter();
                sorter.setSortKeys(null);
            }
            
        };
        addAction(frame, unsort);
        show(frame);
    }
    
    public static class BulkTableModel extends DefaultTableModel {
        
        public BulkTableModel(int rows, int columns) {
            super(rows, columns);
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        public void insertRowsAt(int row, Object[]... rows) {
            List toInsert = new ArrayList();
            for (Object[] data : rows) {
                Vector rowData = convertToVector(data);
                toInsert.add(rowData);
            }
            dataVector.addAll(row, toInsert);
//            justifyRows(row, row + toInsert.size());
            fireTableRowsInserted(row, row + toInsert.size() - 1);
        }
        
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) {
                return Integer.class;
            }
            return super.getColumnClass(columnIndex);
        }
        
        private void justifyRows(int from, int to) { 
            // Sometimes the DefaultTableModel is subclassed 
            // instead of the AbstractTableModel by mistake. 
            // Set the number of rows for the case when getRowCount 
            // is overridden. 
            dataVector.setSize(getRowCount()); 

            for (int i = from; i < to; i++) { 
                if (dataVector.elementAt(i) == null) { 
                    dataVector.setElementAt(new Vector(), i); 
                }
                ((Vector)dataVector.elementAt(i)).setSize(getColumnCount());
            }
        }

    }
//------- start testing Issue #1535-swingx

    /**
     * Sanity: initially valid entry without forcing edit is behaving as expected
     */
    @Test
    public void testGenericEditorValidValue() {
        JTable table = new JTable(create1535TableModel());
        table.setValueAt(new ThrowingDummy("valid"), 0, throwOnEmpty);
        assertStoppedEventOnValidValue(table, 0, throwOnEmpty, false);
    }

    /**
     * Test editor firing when empty value is valid
     */
    @Test
    public void testGenericEditorValidValueAlways() {
        JTable table = new JTable(create1535TableModel());
        assertStoppedEventOnValidValue(table, 0, takeEmpty, false);
        assertTrue(table.getValueAt(0, takeEmpty) instanceof TakeItAllDummy);
    }
    
    /**
     * Editing a not-null value with empty text
     */
    @Test
    public void testGenericEditorEmptyValueInitiallyValid() {
        JTable table = new JTable(create1535TableModel());
        ThrowingDummy validValue = new ThrowingDummy("valid");
        table.setValueAt(validValue, 0, throwOnEmpty);
        assertNoStoppedEventOnEmptyValue(table, 0, throwOnEmpty, true);
        assertEquals(validValue, table.getValueAt(0, throwOnEmpty));
    }
    
    /**
     * Editing a null value with empty text.
     */
    @Test
    public void testGenericEditorEmptyValue() {
        JTable table = new JTable(create1535TableModel());
        assertNoStoppedEventOnEmptyValue(table, 0, throwOnEmpty, false);
        assertEquals(null, table.getValueAt(0, throwOnEmpty));
    }
 

  //------------------- end testing #1535-swingx
    
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        setLookAndFeel("Nimbus");
    }
    
    
    @Test
    public void testSelectionInsertBefore() {
        ListSelectionModel model = new DefaultListSelectionModel();
        model.setSelectionInterval(1, 1);
        model.insertIndexInterval(1, 1, true);
        assertTrue(model.isSelectedIndex(1));
        assertTrue(model.isSelectedIndex(2));
    }
    
    @Test
    public void testSelectionInsertAfter() {
        ListSelectionModel model = new DefaultListSelectionModel();
        model.setSelectionInterval(1, 1);
        model.insertIndexInterval(1, 1, false);
        assertTrue(model.isSelectedIndex(1));
        assertTrue(model.isSelectedIndex(2));
    }
    
    /**
     * Core issue: editing not terminated on setModel.
     * 
     * reported in sun forum:
     * http://forums.sun.com/thread.jspa?threadID=5422547&tstart=15
     */
    @Test
    public void testTerminateEditOnStructurChanged() {
        JTable table = new JTable(10, 2);
        table.setAutoCreateColumnsFromModel(false);
        table.editCellAt(0, 0);
        assertTrue("sanity: editing", table.isEditing());
        ((AbstractTableModel) table.getModel()).fireTableStructureChanged();
        assertEquals("editing must be terminated on setModel", false, table.isEditing());
    }  
    
    /**
     * Core issue: editing not terminated on setModel.
     * 
     * reported in sun forum:
     * http://forums.sun.com/thread.jspa?threadID=5422547&tstart=15
     */
    @Test
    public void testTerminateEditOnSetModel() {
        JTable table = new JTable(10, 2);
        table.setAutoCreateColumnsFromModel(false);
        table.editCellAt(0, 0);
        assertTrue("sanity: editing", table.isEditing());
        table.setModel(new DefaultTableModel(10, 2));
        assertEquals("editing must be terminated on setModel", false, table.isEditing());
    }  
    

    //------------ Nimbus
    
    public void interactiveNimbusLabelBackground() throws Exception {
        final JLabel uiLabel = new JLabel("use ColorUIResource");
        uiLabel.setOpaque(true);
        uiLabel.setBackground(new ColorUIResource(Color.WHITE));
        final JLabel label = new JLabel("use plain Color");
        label.setOpaque(true);
        label.setBackground(Color.WHITE);
        JComponent panel = new JPanel();
        panel.setOpaque(true);
        panel.setBackground(Color.RED);
        panel.add(uiLabel);
        panel.add(label);
        JXFrame frame = wrapInFrame(panel, "ColorUIResource - Color");
        Action action = new AbstractAction("setWhite") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                uiLabel.setBackground(new ColorUIResource(Color.WHITE));
                label.setBackground(Color.WHITE);
            }
        };
        addAction(frame, action);
        show(frame);
    }
    
    
    public void interactiveNimbusTableColor() {
        final JLabel label = new JLabel("background");
        label.setName("Table.Background");
        printComponentProperties(label);
        label.setOpaque(true);
        label.setBackground(UIManager.getColor("Table.background"));
        label.setBackground(new ColorUIResource(Color.WHITE));
//        label.setBackground(Color.WHITE);
        final JLabel alternate = new JLabel("alternate");
        alternate.setName(ALTERNATE_ROW_COLOR);
        alternate.setOpaque(true);
        alternate.setBackground(UIManager.getColor(ALTERNATE_ROW_COLOR));
//        assertTrue(UIManager.getColor(ALTERNATE_ROW_COLOR) instanceof UIResource);
        JComponent panel = new JPanel();
        panel.add(label);
        panel.add(alternate);
        JXFrame frame = wrapInFrame(panel, "normal - alternate");
        Action print = new AbstractAction("print") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                printComponentProperties(label);
                printComponentProperties(alternate);
            }
        };
        addAction(frame, print);
        show(frame);
    }
    
    private void printComponentProperties(JLabel label) {
        LOG.info("LAF/back/alternate " + UIManager.getLookAndFeel() + 
                "\n class " + label.getClass() +
                "\n  name " + label.getName() +
                "\n back " + label.getBackground() + 
                "\n opaque " + label.isOpaque());
    }
    
    /**
     * Issue #6594663: gridlines not settable
     */
    public void interactiveNimbusProperties() {
        final JTable core = new JTable(new AncientSwingTeam());
        final JXTable table = new JXTable(core.getModel());
        JXFrame frame = wrapWithScrollingInFrame(core, table, "core <--> xtable: Gridlines?");
        JLabel label = new JLabel("got something, just a label opaque ...");
        label.setOpaque(true);
        frame.add(label, BorderLayout.SOUTH);
        Action action = new AbstractAction("toggle grid") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (core.getRowMargin() == 0) {
                    core.setIntercellSpacing(new Dimension(1, 1));
                    core.setShowGrid(true);
                    table.setShowGrid(true, true);
                } else {
                    
                }
            }
        };
        addAction(frame, action);
        final Action updateUI = new AbstractAction("updateUI") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                Color color = UIManager.getColor(ALTERNATE_ROW_COLOR);
                LOG.info("alternate uiresource? " + (color instanceof UIResource) + color);
                core.updateUI();
                table.updateUI();
                core.repaint();
                table.repaint();
            }
        };
        addAction(frame, updateUI);
//        NimbusDefaults d;
        Action alternate = new AbstractAction("alternate back") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                Color color = UIManager.getColor(ALTERNATE_ROW_COLOR);
                LOG.info("alternate uiresource? " + (color instanceof UIResource) + color);
                UIManager.put(ALTERNATE_ROW_COLOR, 
                        new ColorUIResource(color != null ? color : Color.RED));
//               UIManager.put(ALTERNATE_ROW_COLOR, Color.RED);
               updateUI.actionPerformed(null);
            }
        };
        addAction(frame, alternate);
        show(frame);
    }
    /**
     * Core issues: throws NPE for interface column classes
     * Problem with Nimbus: installs LAF renderers, but doesn't for duplicate 
     * for Icon?
     * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6830678
     */
    public void interactiveNimbusIconCore() {
        TableModel model = new DefaultTableModel(10, 2) {
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Icon.class;
                }
                return super.getColumnClass(columnIndex);
            }
            
        };
        model.setValueAt(XTestUtils.loadDefaultIcon(), 0, 0);
        JTable core = new JTable(model);
        showWithScrollingInFrame(core, "core: NPE with Icon?");
    }
   

    public void interactiveNimbusIconX() {
        TableModel model = new DefaultTableModel(10, 2) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Icon.class;
                }
                return super.getColumnClass(columnIndex);
            }
             
        };
        model.setValueAt(XTestUtils.loadDefaultIcon(), 0, 0);
        JXTable table = new JXTable(model);
        showWithScrollingInFrame(table, "xtable: NPE with Icon?");
    }
    
  //---------------- core sorting 
    
  //------------------ testing core    
      
      @Test
      public void testSetRowSorterChangeNotification() {
          JTable table = new JTable(new AncientSwingTeam());
          PropertyChangeReport report = new PropertyChangeReport();
          table.addPropertyChangeListener(report);
          table.setRowSorter(new TableRowSorter<TableModel>(table.getModel()));
          TestUtils.assertPropertyChangeEvent(report, "rowSorter", null, table.getRowSorter());
      }
      
      /**
       * core issue: rowSorter replaced on setAutoCreateRowSorter even without change to flag.
       */
      @Test
      public void testSetAutoCreateRowSorter() {
          JTable table = new JTable();
          assertEquals("sanity: core table autoCreate off initially", false, table.getAutoCreateRowSorter());
          assertNull("sanity: core rowSorter is not created", table.getRowSorter());
          table.setAutoCreateRowSorter(true);
          assertNotNull("sanity: core rowSorter is created", table.getRowSorter());
          TableModel model = new AncientSwingTeam();
          table.setModel(model);
          RowSorter<?> sorter = table.getRowSorter();
          table.setAutoCreateRowSorter(true);
          assertSame(sorter, table.getRowSorter());
      }

    /**
     * If autoCreate if off, the control of updating a sorter's model is left
     * completely to the client. Corner case: if had been autoCreated, then
     * turned off, then set a new model, the autoCreated still points to the old
     * model.
     */
    @Test
    public void testRowSorterModelUpdated() {
        JTable table = new JTable();
        table.setAutoCreateRowSorter(true);
        table.setAutoCreateRowSorter(false);
        TableModel old = table.getModel();
        table.setModel(new DefaultTableModel());
        assertSame("tend to not extpect: rowsorter still old? ", old, table.getRowSorter().getModel());
        assertSame("expect rowSorter's model updated?", table.getModel(), table.getRowSorter().getModel());
    }

    /**
     * If autoCreate if off, the control of updating a sorter's model is left
     * completely to the client. Corner case: if had been autoCreated, then
     * turned off, then set a new model, the autoCreated still points to the old
     * model.
     */
    @Test
    public void testRowSorterNulled() {
        JTable table = new JTable();
        table.setAutoCreateRowSorter(true);
        table.setAutoCreateRowSorter(false);
        assertNull("expect auto-created rowsorter nulled?", table.getRowSorter());
    }
    
  //----------------------- interactive

    /**
     * Issue #1489-swingx: terminateEditOnFocusLost leads to unexpected focus behaviour 
     * in internalFrame.
     * 
     * This is a core-issue which shows up in SwingX because JxTable has the property
     * set to true by default, while core has not.
     * 
     */
    public void interactiveInternalFrameTerminateEditOnFocusLost() {
        JDesktopPane jDesktopPane = new JDesktopPane();
        JInternalFrame jInternalFrame = new JInternalFrame();
        jDesktopPane.add(jInternalFrame);
        jInternalFrame.getContentPane().add(createPanel(true));
        JXFrame embddingFrame = wrapInFrame(jDesktopPane, "");
        try {
            jInternalFrame.setMaximum(true);
        } catch (PropertyVetoException ex) {
        }
        jInternalFrame.setVisible(true);
        show(embddingFrame, 400, 400);
        
        PropertyChangeListener pcl = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                Object oldValue = evt.getOldValue();
                Object newValue = evt.getNewValue();
                if (newValue == null || ! JFrame.class.equals(newValue.getClass())) {
                    int i = 1;
                }
                System.out.println(evt.getPropertyName() + " from "
                        + (oldValue == null ? null : oldValue.getClass().getCanonicalName() + oldValue.hashCode())
                        + " to "
                        + (newValue == null ? null : newValue.getClass().getCanonicalName() + newValue.hashCode()));
            }
        };

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("permanentFocusOwner", pcl);
    }
    
    private static TableCellEditor createComboCellEditor() {
        return new DefaultCellEditor(new JComboBox(
                new Object[] {"Value1", "Value2", "Value3"}
                ));
    }

    private static TableModel createTableModel(String prefix) {
        String[] columns = new String[3];
        for (int i = 0; i < 3; i++) {
            columns[i] = prefix + " " + (i + 1);
        }
        return new DefaultTableModel(columns, 3);
    }

    private static JPanel createPanel(boolean terminate) {
        JPanel panel = new JPanel();

        JXTable jXTable = new JXTable(createTableModel("JXTable"));
        JTable jTable = new JTable(createTableModel("JTable"));
//        jXTable.setTerminateEditOnFocusLost(terminate);
//        jTable.putClientProperty("terminateEditOnFocusLost", terminate);

        jXTable.setDefaultEditor(Object.class, createComboCellEditor());
        jTable.setDefaultEditor(Object.class, createComboCellEditor());

        JScrollPane scrollPane1 = new JScrollPane();
        JScrollPane scrollPane2 = new JScrollPane();
        scrollPane1.setViewportView(jXTable);
        scrollPane2.setViewportView(jTable);


        panel.add(scrollPane1);
        panel.add(scrollPane2);

        jXTable.setPreferredScrollableViewportSize(new Dimension(300, 100));
        jTable.setPreferredScrollableViewportSize(new Dimension(300, 100));

        return panel;
    }

    /**
     * Core issue: terminateEditOnFocusLost weird behaviour if in InternalFrame
     * see: 
     * http://forums.java.net/jive/thread.jspa?threadID=64281
     * 
     * To reproduce: edit first column (combo dropdown), click in textfield, click again
     * in first column: editing not started again.
     * 
     * Same in JXTable, but there always: the terminateEditOnFocusLost is true by default.
     * 
     * The thread is no longer available, reported as #1489 against JXTable.
     */
    public void interactiveTerminateEditInInternalFrame() {
        JTable table = new JTable(new AncientSwingTeam());
        table.putClientProperty("terminateEditOnFocusLost", true);
        JComboBox box = new JComboBox(new Object[]{"one", "two"});
        table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(box));
        JComponent panel = Box.createVerticalBox();
        panel.add(new JScrollPane(table));
        panel.add(new JTextField("something to focus outside table"));
        
        JInternalFrame internal = new JInternalFrame();
        internal.setContentPane(panel);
        JDesktopPane desktop = new JDesktopPane();
        desktop.add(internal);
        JXFrame frame = wrapInFrame(desktop, "terminate editing when in internal frame (combo)");
        internal.pack();
        internal.setLocation(50, 30);
        internal.setVisible(true);
        show(frame, 600, 600);
    }
   

    
    public void interactiveAutoRowSorter() {
        // mimic a table coming out of a component factory,
        // which makes it autoCreate always
        final JTable table = new JTable();
        table.setAutoCreateRowSorter(true);
        JXFrame frame = wrapWithScrollingInFrame(table, "autoCreateFalse keeps rowSorter");
        Action toggle = new AbstractAction("new model") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                // mimic client code - special case
                table.setAutoCreateRowSorter(false);
                // silently allows error which fails at runtime when clicking header
                table.setModel(new DefaultTableModel(20, table.getColumnCount() +1 ));
            }
        };
        addAction(frame, toggle);
        show(frame);
    }
    
      /**
       * Core Issue: the calculation of the repaint region after update is completely broken.
       * Nevertheless, the cell is updated correctly. Seems like someplace, the complete table
       * is marked as dirty? <p>
       * 
       * Reason is that always the complete table is repainted if we have individual rowheights.
       * slight dirt in the update code: even if already painted, the would be dirty-region
       * for same rowheights is calculated and repainted (repaintManager folds them into one
       * repaint request, though). 
       */
      public void interactiveRepaintIndiRowHeight() {
          DefaultTableModel model = new DefaultTableModel(20, 3) {
              /**
               * Overridden to reach fire rowUpdated (instead of cellUpdated)
               */
              @SuppressWarnings("unchecked")
              @Override
              public void setValueAt(Object aValue, int row, int column) {
                  Vector<Object> rowVector = (Vector<Object>)dataVector.elementAt(row);
                  rowVector.setElementAt(aValue, column);
                  fireTableRowsUpdated(row, row);
              }
              
          };
          final JTable table = new JTable(model) ;
         for (int i = 4; i < table.getRowCount(); i++) {
              table.setRowHeight(i, table.getRowHeight() + i *4);
          }
          JXFrame frame = showInFrame(table, "repaint after update");
          Action action = new AbstractAction("update focused") {
              
              @Override
              public void actionPerformed(ActionEvent e) {
                  int selected = table.getRowCount() / 2;
                  if (selected < 0) return;
                  table.setValueAt("XX" + table.getValueAt(selected, 0), selected, 0);
              }
              
          };
          addAction(frame, action);
      }
      
     
      /**
       * Core issue #6539455: table not properly repainted on update (from model).
       * 
       * Happens if the update is not triggered by an edit in the table itself. If
       * so, all is well (repaint called for all of the table). If not, repaint is
       * limited to the cell that has been updated (not even the whole row is
       * painted) - correct would be to repaint all rows between the old and new
       * row view index, inclusively.
       */
      public void interactiveSortOnUpdateNotEditing() {
          final JTable table = new JTable(new AncientSwingTeam());
          table.setAutoCreateRowSorter(true);
          ((TableRowSorter<?>) table.getRowSorter()).setSortsOnUpdates(true);
          table.getRowSorter().toggleSortOrder(0);
          JXFrame frame = showWithScrollingInFrame(table,
                  "updates and repaint");
          Action edit = new AbstractAction("update first visible") {

              @Override
              public void actionPerformed(ActionEvent e) {
                  table.setValueAt("XXX" + table.getValueAt(0, 0), 0, 0);

              }
          };
          addAction(frame, edit);
      }
      
      /**
       * Core issue #6539455: table not properly repainted on update (from model).
       * 
       * Happens if the update is not triggered by an edit in the table itself. If
       * so, all is well (repaint called for all of the table). If not, repaint is
       * limited to the cell that has been updated (not even the whole row is
       * painted) - correct would be to repaint all rows between the old and new
       * row view index, inclusively.
       */
      public void interactiveSortOnUpdateNotEditingHack() {
          final JTable table = new JTableRepaintOnUpdate();
          table.setModel(new AncientSwingTeam());
          table.setAutoCreateRowSorter(true);
          ((TableRowSorter<?>) table.getRowSorter()).setSortsOnUpdates(true);
          table.getRowSorter().toggleSortOrder(0);
          JXFrame frame = showWithScrollingInFrame(table,
                  "updates and repaint (hacked)");
          Action edit = new AbstractAction("update first visible") {

              @Override
              public void actionPerformed(ActionEvent e) {
                  table.setValueAt("XXX" + table.getValueAt(0, 0), 0, 0);

              }
          };
          addAction(frame, edit);
      }
      
      
      public static class JTableRepaintOnUpdate extends JTable {

          private UpdateHandler beforeSort;
          
          
          @Override
          public void sorterChanged(RowSorterEvent e) {
              super.sorterChanged(e);
              maybeRepaintOnSorterChanged(e);
          } 

          private void beforeUpdate(TableModelEvent e) {
              if (!isSorted()) return;
              beforeSort = new UpdateHandler(e);
          }
          
          /**
           * 
           */
          private void afterUpdate() {
              beforeSort = null;
          }
          
          
          /**
           * 
           */
          private void maybeRepaintOnSorterChanged(RowSorterEvent e) {
              if (beforeSort == null) return;
              if ((e == null) || (e.getType() != RowSorterEvent.Type.SORTED)) return;
              UpdateHandler afterSort = new UpdateHandler(beforeSort);
              if (afterSort.allHidden(beforeSort)) {
                  return;
              } else if (afterSort.complex(beforeSort)) {
                  repaint();
                  return;
              }
              int firstRow = afterSort.getFirstCombined(beforeSort);
              int lastRow = afterSort.getLastCombined(beforeSort);
              Rectangle first = getCellRect(firstRow, 0, false);
              first.width = getWidth();
              Rectangle last = getCellRect(lastRow, 0, false);
              repaint(first.union(last));
          }
          
          private class UpdateHandler {
              private int firstModelRow;
              private int lastModelRow;
              private int viewRow;
              private boolean allHidden;
              
              public UpdateHandler(TableModelEvent e) {
                  firstModelRow = e.getFirstRow();
                  lastModelRow = e.getLastRow();
                  convert();
              }
              
              public UpdateHandler(UpdateHandler e) {
                  firstModelRow = e.firstModelRow;
                  lastModelRow = e.lastModelRow;
                  convert();
              }
              
              public boolean allHidden(UpdateHandler e) {
                  return this.allHidden && e.allHidden;
              }
              
              public boolean complex(UpdateHandler e) {
                  return (firstModelRow != lastModelRow);
              }
              
              public int getFirstCombined(UpdateHandler e) {
                  if (allHidden) return e.viewRow;
                  if (e.allHidden) return viewRow;
                  return Math.min(viewRow, e.viewRow);
              }
              
              public int getLastCombined(UpdateHandler e) {
                  if (allHidden || e.allHidden) return getRowCount() - 1;
                  return Math.max(viewRow, e.viewRow);
                  
              }
              /**
               * @param e
               */
              private void convert() {
                  // multiple updates
                  if (firstModelRow != lastModelRow) {
                      // don't bother too much - calculation not guaranteed to do anything good
                      // just check if the all changed indices are hidden
                      allHidden = true;
                      for (int i = firstModelRow; i <= lastModelRow; i++) {
                          if (convertRowIndexToView(i) >= 0) {
                              allHidden = false;
                              break;
                          }
                      }
                      viewRow = -1;
                      return;
                  }
                  // single update
                  viewRow = convertRowIndexToView(firstModelRow);
                  allHidden = viewRow < 0;
              }
              
          }
          /**
           * @return
           */
          private boolean isSorted() {
              // JW: not good enough - need a way to decide if there are any sortkeys which
              // constitute a sort or any effective filters  
              return getRowSorter() != null;
          }



          @Override
          public void tableChanged(TableModelEvent e) {
              if (isUpdate(e)) {
                  beforeUpdate(e);
              }
              try {
                  super.tableChanged(e);
              } finally {
                  afterUpdate();
              }
          }
          

          /**
           * Convenience method to detect dataChanged table event type.
           * 
           * @param e the event to examine. 
           * @return true if the event is of type dataChanged, false else.
           */
          protected boolean isDataChanged(TableModelEvent e) {
              if (e == null) return false;
              return e.getType() == TableModelEvent.UPDATE && 
                  e.getFirstRow() == 0 &&
                  e.getLastRow() == Integer.MAX_VALUE;
          }
          
          /**
           * Convenience method to detect update table event type.
           * 
           * @param e the event to examine. 
           * @return true if the event is of type update and not dataChanged, false else.
           */
          protected boolean isUpdate(TableModelEvent e) {
              if (isStructureChanged(e)) return false;
              return e.getType() == TableModelEvent.UPDATE && 
                  e.getLastRow() < Integer.MAX_VALUE;
          }

          /**
           * Convenience method to detect a structureChanged table event type.
           * @param e the event to examine.
           * @return true if the event is of type structureChanged or null, false else.
           */
          protected boolean isStructureChanged(TableModelEvent e) {
              return e == null || e.getFirstRow() == TableModelEvent.HEADER_ROW;
          }


      }
      /**
       * Core Issue ??: must not sort if mouse in resize region 
       */
      public void interactiveSortOnResize() {
          JTable table = new JTable(new AncientSwingTeam());
          table.setAutoCreateRowSorter(true);
          showWithScrollingInFrame(table, "must not sort in resize");
      }

      /**
       * Core issue #6539455: table not properly repainted on update (from model).
       * 
       * This setup differs from the examples (assuming we would add a second table, arggh)
       * above in that the sorter is shared as well as the model. In this case the repaint is
       * okay, as the second table receives the event from the sorter outside of its 
       * tableChanged, that is ignoreSort is false.
       */
      public void interactiveSharedRowSorter() {
          TableModel model = new AncientSwingTeam();
          final JTable one = new JTable();
          one.setDragEnabled(true);
          one.setAutoCreateRowSorter(true);
          one.setModel(model);
          JTable other = new JTable(model);
          other.setRowSorter(one.getRowSorter());
          JXFrame frame = showWithScrollingInFrame(one, other, "shared model and rowsorter");
          Action editFirst = new AbstractAction("prefix X on first") {

              @Override
              public void actionPerformed(ActionEvent e) {
                  Object old = one.getValueAt(0, 0);
                  one.setValueAt("X" + old, 0, 0);
              }
              
          };
          addAction(frame, editFirst);
          Action toggleSortOnUpdate = new AbstractAction("toggleSortsOnUpdate") {

              @Override
              public void actionPerformed(ActionEvent e) {
                  DefaultRowSorter<?, ?> sorter = (DefaultRowSorter<?, ?>) one.getRowSorter();
                  sorter.setSortsOnUpdates(!sorter.getSortsOnUpdates());
              } };
          addAction(frame, toggleSortOnUpdate);
      }

   //---------------- end core sorting   

    public void testFormatDefaultRenderer() {
        DefaultTableModel model = new DefaultTableModel(1, 1) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return Date.class;
            }
            
        };
        model.setValueAt("definitely not a date", 0, 0);
        JTable table = new JTable(model);
        TableCellRenderer renderer = table.getCellRenderer(0, 0);
        table.prepareRenderer(renderer , 0, 0);
    }
    
    /**
     * test that all transferFocus methods stop edits and 
     * fire one stopped event.
     *
     * Hmm .. unexpected: we get two stopped?
     */
    public void testStopEditingCoreTable() {
        JTable table = new JTable(10, 2);
        table.editCellAt(0, 0);
        // sanity
        assertTrue(table.isEditing());
        CellEditorReport report = new CellEditorReport();
        table.getCellEditor().addCellEditorListener(report);
        // sanity
        assertFalse(report.hasEvents());
        table.getCellEditor().stopCellEditing();
        assertFalse("table must not be editing", table.isEditing());
        assertEquals("", 1, report.getEventCount());
        assertEquals("", 1, report.getStoppedEventCount());
    }

    /**
     * test that all transferFocus methods stop edits and 
     * fire one stopped event.
     *
     * Hmm .. unexpected: we get two stopped? 
     * Here: let the table prepare the editor (but not install)
     * 
     *  in this case the generic.stopCellEditing calls super 
     *  twice!
     */
    public void testStopEditingTableGenericPrepared() {
        JTable table = new JTable(10, 2);
        TableCellEditor direct = table.getDefaultEditor(Object.class);
        CellEditorReport report = new CellEditorReport();
        direct.addCellEditorListener(report);
        TableCellEditor editor = table.getCellEditor(0, 0);
        // sanity:
        assertSame(direct, editor);
        assertFalse(report.hasEvents());
        table.prepareEditor(editor, 0, 0);
        // sanity: prepare did not fire ..
        assertFalse(report.hasEvents());
        editor.stopCellEditing();
        assertEquals("prepared - must have fired exactly one event", 1, report.getEventCount());
        assertEquals("", 1, report.getStoppedEventCount());
    }

    /**
     * test that all transferFocus methods stop edits and 
     * fire one stopped event.
     *
     * Hmm .. unexpected: we get two stopped? 
     * Here: get the table's editor and prepare manually.
     * this test passes ... what is in the prepare which 
     * fires?
     * In this case it calls super.stop once only ... 
     * 
     */
    public void testStopEditingTableGenericGetComp() {
        JTable table = new JTable(10, 2);
        TableCellEditor editor = table.getCellEditor(0, 0);
        CellEditorReport report = new CellEditorReport();
        editor.addCellEditorListener(report);
        editor.getTableCellEditorComponent(table, "something", false, 0, 0);
        editor.stopCellEditing();
        assertEquals("", 1, report.getEventCount());
        assertEquals("", 1, report.getStoppedEventCount());
    }

    /**
     * test that all transferFocus methods stop edits and 
     * fire one stopped event.
     * 
     * Core issue: 
     * Table's generic editor must not return a null component.
     */
    public void testTableGenericEditorNullTable() {
        JTable table = new JTable(10, 2);
        TableCellEditor editor = table.getCellEditor(0, 0);
        Component comp = editor.getTableCellEditorComponent(
                null, "something", false, 0, 0);
        assertNotNull("editor must not return null component", comp);
    }
    
    /**
     * test that all transferFocus methods stop edits and 
     * fire one stopped event.
     *
     * Hmm .. unexpected: we get two stopped? 
     * 
     * Here: Must not throw NPE if calling stopCellEditing without previous 
     *   getXXComponent.
     */
    public void testTableGenericEditorIsolatedNPE() {
        JTable table = new JTable(10, 2);
        TableCellEditor editor = table.getCellEditor(0, 0);
        editor.stopCellEditing();
    }
    
    /**
     * test that all transferFocus methods stop edits and 
     * fire one stopped event.
     *
     * Hmm .. unexpected: we get two stopped? Test 
     * DefaultCellEditor - okay.
     */
    public void testStopEditingDefaultCellEditor() {
        TableCellEditor editor = new DefaultCellEditor(new JTextField());
        CellEditorReport report = new CellEditorReport();
        editor.addCellEditorListener(report);
        editor.stopCellEditing();
        assertEquals("", 1, report.getEventCount());
        assertEquals("", 1, report.getStoppedEventCount());
    }


    /**
     * core issue: JTable cannot cope with null selection background.
     *
     */
    public void testNullGridColor() {
        JTable table = new JTable();
//        assertNotNull(UIManager.getColor("Table.gridColor"));
        assertNotNull(table.getGridColor());
        assertEquals(UIManager.getColor("Table.gridColor"), table.getGridColor());
        table.setGridColor(null);
    }

    /**
     * core issue: JTable cannot cope with null selection background.
     *
     */
    public void testNullSelectionBackground() {
        JTable table = new JTable();
        assertNotNull(table.getSelectionBackground());
        table.setSelectionBackground(null);
    }
    
    /**
     * core issue: JTable cannot cope with null selection background.
     *
     */
    public void testNullSelectionForeground() {
        JTable table = new JTable();
        table.setSelectionForeground(null);
    }
    /**
     * Issue #282-swingx: compare disabled appearance of
     * collection views.
     *
     */
    public void testDisabledRenderer() {
        JList list = new JList(new Object[] {"one", "two"});
        list.setEnabled(false);
        // sanity
        assertFalse(list.isEnabled());
        Component comp = list.getCellRenderer().getListCellRendererComponent(list, "some", 0, false, false);
        assertEquals(list.isEnabled(), comp.isEnabled());
        JTable table = new JTable(10, 2);
        table.setEnabled(false);
        // sanity
        assertFalse(table.isEnabled());
        comp = table.prepareRenderer(table.getCellRenderer(0, 0), 0, 0);
        assertEquals(table.isEnabled(), comp.isEnabled());
    }

    /**
     * Characterization method: table.addColumn and invalid modelIndex.
     * 
     * Doesn't blow up because DefaultTableModel.getColumnName is lenient,
     * that is has no precondition on the index.
     *
     */
    public void testAddColumn() {
        JTable table = new JTable(0, 0);
        table.addColumn(new TableColumn(1));
    }

    public void interactiveAutoStartsEdit() {
        final String autoKey = "JTable.autoStartsEdit";
        final JTable table = new JTable(new AncientSwingTeam());
        table.putClientProperty(autoKey, Boolean.TRUE);
        final String autoStartName = "toggle AutoStart ";
        boolean isAuto = Boolean.TRUE.equals(table.getClientProperty(autoKey));
        Action autoStart = new AbstractActionExt(autoStartName + isAuto) {

            public void actionPerformed(ActionEvent e) {
                boolean isAuto = Boolean.TRUE.equals(table.getClientProperty(autoKey));
                table.putClientProperty(autoKey, isAuto ? Boolean.FALSE : Boolean.TRUE);
                setName(autoStartName + !isAuto);
            }
            
        };
        JXFrame frame = wrapWithScrollingInFrame(table, "autostart-edit behaviour");
        addAction(frame, autoStart);
        show(frame);
    }
    
    /**
     * Core Issue ??: standalone table header throws NPE on mouse
     * events.
     * 
     * Base reason is that the ui assume a not-null table at varying
     * places in their code.
     * 
     * Reason is an unsafe implementation of viewIndexForColumn. Unconditionally
     * queries the table for index conversion.
     */
    public void interactiveNPEStandaloneHeader() {
        JXTable table = new JXTable(new AncientSwingTeam());
        JXTableHeader header = new JXTableHeader(table.getColumnModel());
        JXFrame frame = showWithScrollingInFrame(header, "Standalone header: NPE on mouse gestures");
        addMessage(frame, "exact place/gesture is LAF dependent. Base error is to assume header.getTable() != null");
    }

    /**
     * 
     * 
     * 
     */
    public void interactiveToolTipOverEmptyCell() {
        final DefaultTableModel model = new DefaultTableModel(50, 2);
        model.setValueAt("not empty", 0, 0);
        final JTable table = new JTable(model) {
            
            @Override
            public String getToolTipText(MouseEvent event) {
                int column = columnAtPoint(event.getPoint());
                if (column == 0) {
                    return "first column";
                }
                return null;
            }

            @Override
            public Point getToolTipLocation(MouseEvent event) {
                int column = columnAtPoint(event.getPoint());
                int row = rowAtPoint(event.getPoint());
                Rectangle cellRect = getCellRect(row, column, false);
                if (!getComponentOrientation().isLeftToRight()) {
                    cellRect.translate(cellRect.width, 0);
                }
                // PENDING JW: otherwise we get a small (borders only) tooltip for null
                // core issue? yeah ... probably
//                return getValueAt(row, column) == null ? null : cellRect.getLocation();
                return cellRect.getLocation();
            }
            

        };
        JXFrame frame = wrapWithScrollingInFrame(table, "Tooltip over empty");
        show(frame);
    }
    
    /**
     * forum: table does not scroll after setRowSelectionInterval?
     * 
     * 
     */
    public void interactiveAutoScroll() {
        final DefaultTableModel model = new DefaultTableModel(50, 2);
        final JTable table = new JTable(model);
        table.setAutoscrolls(true);
        Action action = new AbstractAction("select last row: scrolling?") {

            public void actionPerformed(ActionEvent e) {
                
                int selected = table.getRowCount() - 1;
                if (selected >= 0) {
                    table.setRowSelectionInterval(selected, selected);
                }
            }
            
        };
        JXFrame frame = wrapWithScrollingInFrame(table, "insert at selection");
        addAction(frame, action);
        frame.setVisible(true);
    }
    
    /**
     * Issue #272-swingx: inserted row is selected.
     * Not a bug: documented behaviour of DefaultListSelectionModel.
     *
     */
    public void interactiveInsertAboveSelection() {
        final DefaultTableModel model = new DefaultTableModel(10, 2);
        final JTable table = new JTable(model);
        Action action = new AbstractAction("insertRow") {

            public void actionPerformed(ActionEvent e) {
                
                int selected = table.getSelectedRow();
                if (selected < 0) return;
                model.insertRow(selected, new Object[2]);
            }
            
        };
        JXFrame frame = wrapWithScrollingInFrame(table, "insert at selection");
        addAction(frame, action);
        frame.setVisible(true);
    }

    
    
    public void interactiveLeadAnchor() {
        final JTable table = new JTable(10, 3) {

            @Override
            public void tableChanged(TableModelEvent e) {
                super.tableChanged(e);
                if (isDataChanged(e) || isStructureChanged(e)) {
                    focusFirstCell();
                }
            }

            private void focusFirstCell() {
                if (getColumnCount() > 0) {
                    getColumnModel().getSelectionModel()
                            .removeSelectionInterval(0, 0);
                }
                if (getRowCount() > 0) {
                    getSelectionModel().removeSelectionInterval(0, 0);
                }

            }

            private boolean isDataChanged(TableModelEvent e) {
                return e.getType() == TableModelEvent.UPDATE
                        && e.getFirstRow() == 0
                        && e.getLastRow() == Integer.MAX_VALUE;
            }

            private boolean isStructureChanged(TableModelEvent e) {
                return e == null
                        || e.getFirstRow() == TableModelEvent.HEADER_ROW;
            }

        };
        JXFrame frame = wrapWithScrollingInFrame(table, "auto-lead - force in table subclass");
        Action toggleAction = new AbstractAction("Toggle TableModel") {

            public void actionPerformed(ActionEvent e) {
                if (table.getRowCount() > 0) {
                    table.setModel(new DefaultTableModel());
                } else {
                    table.setModel(new DefaultTableModel(10, 3));
                }
            }

        };
        addAction(frame, toggleAction);
        frame.setVisible(true);
    }

//---------------------- unit tests 
    
    /**
     * Issue #4614616: renderer lookup broken for interface types.
     * 
     */
    public void testNPERendererForInterface() {
        DefaultTableModel model = new DefaultTableModel(10, 2) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return Comparable.class;
            }
            
        };
        JTable table = new JTable(model);
        table.prepareRenderer(table.getCellRenderer(0, 0), 0, 0);
    }

    /**
     * Issue #4614616: editor lookup broken for interface types.
     * 
     */
    public void testNPEEditorForInterface() {
        DefaultTableModel model = new DefaultTableModel(10, 2) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return Comparable.class;
            }
            
        };
        JTable table = new JTable(model);
        table.prepareEditor(table.getCellEditor(0, 0), 0, 0);
    }

    /**
     * isCellEditable is doc'ed as: if false, setValueAt 
     * will have no effect.
     * 
     * 
     */
    public void testSetValueDoNothing() {
        JTable table = new JTable(10, 3) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
        };
        Object value = table.getValueAt(0, 0);
        // sanity...
        assertFalse(table.isCellEditable(0, 0));
        table.setValueAt("wrong", 0, 0);
        assertEquals("value must not be changed", value, table.getValueAt(0, 0));
    }
    
    /**
     * Issue #272-swingx: inserted row is selected.
     * Not a bug: documented behaviour of DefaultListSelectionModel.
     *
     */
    public void testInsertBeforeSelected() {
        DefaultTableModel model = new DefaultTableModel(10, 2);
        JTable table = new JTable(model);
        table.setRowSelectionInterval(3, 3);
        model.insertRow(3, new Object[2]);
        int[] selected = table.getSelectedRows();
        assertEquals(1, selected.length);
    }

    /**
     * Issue #272-swingx: inserted row is selected.
     * Not a bug: documented behaviour of DefaultListSelectionModel.
     */
    public void testInsertBeforeSelectedSM() {
        DefaultListSelectionModel model = new DefaultListSelectionModel();
        model.setSelectionInterval(3, 3);
        model.insertIndexInterval(3, 1, true);
        int max = model.getMaxSelectionIndex();
        int min = model.getMinSelectionIndex();
        assertEquals(max, min);
    }

    /**
     * test contract: getColumn(int) throws ArrayIndexOutofBounds with 
     * invalid column index.
     * 
     * Subtle autoboxing issue: 
     * JTable has convenience method getColumn(Object) to access by 
     * identifier, but doesn't have delegate method to columnModel.getColumn(int)
     * Clients assuming the existence of a direct delegate no longer get a
     * compile-time error message in 1.5 due to autoboxing. 
     * Furthermore, the runtime exception is unexpected (IllegalArgument
     * instead of AIOOB).
     *
     */
    public void testTableColumnOffRange() {
        JTable table = new JTable(2, 1);
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

    
    public void testTableRowAtNegativePoint() {
        JTable treeTable = new JTable(1, 4);
        int negativeYRowHeight = - treeTable.getRowHeight();
        int negativeYRowHeightPlusOne = negativeYRowHeight + 1;
        int negativeYMinimal = -1;
        // just outside of negative row before first row
        assertEquals("negative y location rowheight " + negativeYRowHeight + " must return row -1", 
                -1,  treeTable.rowAtPoint(new Point(-1, negativeYRowHeight)));
        // just inside of negative row before first row
        assertEquals("negative y location " + negativeYRowHeightPlusOne +" must return row -1", 
                -1,  treeTable.rowAtPoint(new Point(-1, negativeYRowHeightPlusOne)));
        // just outside of first row
        assertEquals("minimal negative y location must return row -1", 
                -1,  treeTable.rowAtPoint(new Point(-1, negativeYMinimal)));
        
    }

    public void testLeadSelectionAfterStructureChanged() {
        DefaultTableModel model = new DefaultTableModel(10, 2) {

            @Override
            public void fireTableRowsDeleted(int firstRow, int lastRow) {
                fireTableStructureChanged();
            }
            
            
        };
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(i, i, 0);
        }
        JTable table = new JTable(model);
        int rowIndex = table.getRowCount() - 1;
        table.addRowSelectionInterval(rowIndex, rowIndex);
        model.removeRow(rowIndex);
        // JW: this was pre-1.5u5 (?), changed (1.5u6?) to return - 1
//        assertEquals("", rowIndex, table.getSelectionModel().getAnchorSelectionIndex());
        assertEquals("", -1, table.getSelectionModel().getAnchorSelectionIndex());
        ListSelectionReport report = new ListSelectionReport();
        table.getSelectionModel().addListSelectionListener(report);
    }

    /**
     * as of jdk1.5u6 the lead/anchor is no longer automatically set.
     * before (last code I saw is jdk1.5u4) - tableChanged would call
     * checkLeadAnchor after structureChanged. 
     * CheckLeadAnchor did set the lead/anchor
     * to the first row if count > 0.
     * 
     * Always: BasicTableUI repaints the lead-cell in focusGained.
     * 
     * Now: need to explicitly set _both_ anchor and lead to >= 0
     * need to set anchor first. Need to do so for both row/column selection model.
     * @throws InvocationTargetException 
     * @throws InterruptedException 
     * 
     */
    public void testInitialLeadAnchor() throws InterruptedException, InvocationTargetException {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run testLeadAnchorOnFocusGained - headless environment");
            return;
        }
        DefaultTableModel model = new DefaultTableModel(10, 2) {

            @Override
            public void fireTableRowsDeleted(int firstRow, int lastRow) {
                fireTableStructureChanged();
            }
            
            
        };
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(i, i, 0);
        }
        final JTable table = new JTable(model);
        JFrame frame = new JFrame("anchor on focus");
        frame.add(new JScrollPane(table));
        frame.setVisible(true);
        // JW: need to explicitly set _both_ anchor and lead to >= 0
        // need to set anchor first
//        table.getSelectionModel().setAnchorSelectionIndex(0);
//        table.getSelectionModel().setLeadSelectionIndex(0);
//        table.getColumnModel().getSelectionModel().setAnchorSelectionIndex(0);
//        table.getColumnModel().getSelectionModel().setLeadSelectionIndex(0);

        table.requestFocus();
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                assertTrue("table is focused ", table.hasFocus());
                assertEquals("anchor must be 0", 0, table.getSelectionModel().getAnchorSelectionIndex());
                assertEquals("lead must be 0", 0, table.getSelectionModel().getLeadSelectionIndex());

            }
        });
    }

    /**
     * as of jdk1.5u6 the lead/anchor is no longer automatically set.
     * before (last code I saw is jdk1.5u4) - tableChanged would call
     * checkLeadAnchor after structureChanged. CheckLeadAnchor did set the lead/anchor
     * to the first row if count > 0.
     * 
     * Always: BasicTableUI repaints the lead-cell in focusGained.
     * 
     * Now: need to explicitly set _both_ anchor and lead to >= 0
     * need to set anchor first. Need to do so for both row/column selection model.
     * 
     */
    public void testLeadAnchorAfterStructureChanged() {
        final JTable table = new JTable(10, 2);
        // JW: need to explicitly set _both_ anchor and lead to >= 0
        // need to set anchor first
        table.getSelectionModel().setAnchorSelectionIndex(0);
        table.getSelectionModel().setLeadSelectionIndex(0);
        table.getColumnModel().getSelectionModel().setAnchorSelectionIndex(0);
        table.getColumnModel().getSelectionModel().setLeadSelectionIndex(0);
        // sanity...
        assertEquals("anchor must be 0", 0, table.getSelectionModel().getAnchorSelectionIndex());
        assertEquals("lead must be 0", 0, table.getSelectionModel().getLeadSelectionIndex());
        table.setModel(new DefaultTableModel(20, 3));
        // regression: lead/anchor unconditionally reset to -1 
        assertEquals("anchor must be 0", 0, table.getSelectionModel().getAnchorSelectionIndex());
        assertEquals("lead must be 0", 0, table.getSelectionModel().getLeadSelectionIndex());
        
    }
//------------- from incubator ... PENDING: cleanup/remove
    

}
