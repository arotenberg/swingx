/*
 * Created on 09.06.2006
 *
 */
package org.jdesktop.swingx;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.test.PropertyChangeReport;
import org.jdesktop.test.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class JXTableHeaderTest extends InteractiveTestCase {
    private static final Logger LOG = Logger.getLogger(JXTableHeaderTest.class
            .getName());
    
    /**
     * the table used to instantiate a TableColumnModel used in the header.
     */
    private JXTable table;
    /**
     * A header instantiated with the same TableColumnModel as the table.
     * NOTE: this is a standalone header, except for the model unrelated
     * to the table.
     */
    private JXTableHeader header;

    @Test
    public void testSortsOnDoubleClick() {
        assertFalse(header.getResortsOnDoubleClick());
        header.setResortsOnDoubleClick(true);
        assertEquals(header.getXTable() != null, header.getResortsOnDoubleClick());
        header.setTable(table);
        assertEquals(header.getXTable() != null, header.getResortsOnDoubleClick());
        
    }

    /**
     * Issue #1563-swingx: find cell that was clicked for componentPopup
     * 
     * Test api and event firing.
     */
    @Test
    public void testPopupTriggerLocationAvailable() {
        JXTableHeader table = (JXTableHeader) new JXTable(10, 3).getTableHeader();
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
        JXTableHeader table = (JXTableHeader) new JXTable(10, 3).getTableHeader();
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
        JXTableHeader table = (JXTableHeader) new JXTable(10, 3).getTableHeader();
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
     * Issue #1341-swingx: resizing/dragged/column/distance bound properties.
     */
    @Test
    public void testNotifyResizingColumn() {
        assertNull(header.getResizingColumn());
        PropertyChangeReport report = new PropertyChangeReport(header);
        TableColumn tableColumn = header.getColumnModel().getColumn(0);
        header.setResizingColumn(tableColumn);
        TestUtils.assertPropertyChangeEvent(report, "resizingColumn", null, tableColumn);
        
    }
    
    /**
     * Issue #1341-swingx: resizing/dragged/column/distance bound properties.
     */
    @Test
    public void testNotifyDraggedDistance() {
        PropertyChangeReport report = new PropertyChangeReport(header);
        header.setDraggedDistance(10);
        TestUtils.assertPropertyChangeEvent(report, "draggedDistance", 0, 10);
    }
    
    /**
     * Issue #1341-swingx: resizing/dragged/column/distance bound properties.
     */
    @Test
    public void testNotifyDraggedColumn() {
        assertNull(header.getDraggedColumn());
        PropertyChangeReport report = new PropertyChangeReport(header);
        TableColumn tableColumn = header.getColumnModel().getColumn(0);
        header.setDraggedColumn(tableColumn);
        TestUtils.assertPropertyChangeEvent(report, "draggedColumn", null, tableColumn);
    }
    

    /**
     * Issue #1154-swingx: Regression after switching to Mustang.
     * was: Issue #31 (swingx): clicking header must not sort if table !enabled.
     * 
     * Actually, an older debate - to disable or not to disable the header
     * if table disabled. Decided to synch header disabled (via listener in
     * JXTableHeader. This is consistent with ColumnControlButton.
     *
     */
    @Test
    public void testEnabledSynched() {
        // test synch on propertyChange
        JXTable table = new JXTable();
        table.setEnabled(false);
        assertEquals(table.isEnabled(), table.getTableHeader().isEnabled());
        // test synch on setTable
        JXTableHeader header = new JXTableHeader();
        header.setTable(table);
        assertEquals(table.isEnabled(), header.isEnabled());
        // test true if table null
        header.setTable(null);
        assertEquals(true, header.isEnabled());
    }
    
    

    /**
     * Issue #485-swingx: table header disappears if all header values are
     * empty. 
     * 
     * fixed for SwingX.
     *
     */
    @Test
    public void testHeaderSizeEmptyStringHeaderValue() {
        final String[] alternate = { 
                "", 
                "", 
                };
        JXTable xTable = new JXTable(10, 2);
        xTable.getColumn(0).setHeaderValue(alternate[0]);
        xTable.getColumn(1).setHeaderValue(alternate[1]);
        assertTrue("header height must be > 0", xTable.getTableHeader().getPreferredSize().height > 0);
    }

    /**
     * Issue #485-swingx: table header disappears if all header values are
     * empty. 
     * 
     * fixed for Swingx.
     *
     */
    @Test
    public void testHeaderSizeNullHeaderValue() {
        final String[] alternate = { 
                null, 
                null, 
                };
        JXTable xTable = new JXTable(10, 2);
        xTable.getColumn(0).setHeaderValue(alternate[0]);
        xTable.getColumn(1).setHeaderValue(alternate[1]);
        assertTrue("header height must be > 0", xTable.getTableHeader().getPreferredSize().height > 0);
    }
    /**
     * Issue #390-swingx: JXTableHeader: throws AIOOB on removing dragged column.
     * Test that getDraggedColumn is null if removed.
     * 
     * Problem was reported on mac:
     * http://forums.java.net/jive/thread.jspa?threadID=18368&tstart=0
     * when hiding column while drag(?) is in process.
     * 
     *
     */
    @Test
    public void testDraggedColumnRemoved() {
        JXTable table = new JXTable(10, 2);
        TableColumnExt columnExt = table.getColumnExt(0);
        table.getTableHeader().setDraggedColumn(columnExt);
        // sanity assert
        assertEquals(columnExt, table.getTableHeader().getDraggedColumn());
        table.getColumnModel().removeColumn(columnExt);
        assertNull("draggedColumn must be null if removed", table.getTableHeader().getDraggedColumn());
    }
    
    /**
     * Issue #390-swingx: JXTableHeader: throws AIOOB on removing dragged column.
     * Test that getDraggedColumn is visible or null.
     * 
     * Problem was reported on mac:
     * http://forums.java.net/jive/thread.jspa?threadID=18368&tstart=0
     * when hiding column while drag(?) is in process.
     */
    @Test
    public void testDraggedColumnVisible() {
        JXTable table = new JXTable(10, 2);
        TableColumnExt columnExt = table.getColumnExt(0);
        table.getTableHeader().setDraggedColumn(columnExt);
        // sanity assert
        assertEquals(columnExt, table.getTableHeader().getDraggedColumn());
        columnExt.setVisible(false);
        assertNull("dragged column must visible or null", table.getTableHeader().getDraggedColumn());
    }
    
    
    /**
     * Issue 334-swingx: BasicTableHeaderUI.getPrefSize doesn't respect 
     *   all renderere's size requirements.
     *
     */
    @Test
    public void testPreferredHeight() {
        JXTable table = new JXTable(10, 2);
        TableColumnExt columnExt = table.getColumnExt(1);
        columnExt.setTitle("<html><center>Line 1<br>Line 2</center></html>");
        JXTableHeader tableHeader = (JXTableHeader) table.getTableHeader();
        TableCellRenderer renderer = tableHeader.getCellRenderer(1);
        Component comp = renderer.getTableCellRendererComponent(table, 
                columnExt.getHeaderValue(), false, false, -1, 1);
        Dimension prefRendererSize = comp.getPreferredSize();
        assertEquals("Header pref height must respect renderer",
                prefRendererSize.height, tableHeader.getPreferredSize().height);
    }
    

    /**
     * test doc'ed xheader.getToolTipText(MouseEvent) behaviour.
     *
     */
    @Test
    public void testColumnToolTip() {
        JXTable table = new JXTable(10, 2);
        TableColumnExt columnExt = table.getColumnExt(0);
        JXTableHeader tableHeader = (JXTableHeader) table.getTableHeader();
        MouseEvent event = new MouseEvent(tableHeader, 0,
                0, 0, 40, 5, 0, false);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        String rendererToolTip = "rendererToolTip";
        renderer.setToolTipText(rendererToolTip);
        columnExt.setHeaderRenderer(renderer);
        assertEquals(rendererToolTip, tableHeader.getToolTipText(event));
        String columnToolTip = "columnToolTip";
        columnExt.setToolTipText(columnToolTip);
        assertEquals(columnToolTip, tableHeader.getToolTipText(event));
        
    }
    
    /**
     * Issue #1560-swingx: column tooltip not working for stand-alone header.
     */
    @Test
    public void testColumnToolTipStandAlone() {
        JXTable table = new JXTable(10, 2);
        TableColumnExt columnExt = table.getColumnExt(0);
        JXTableHeader tableHeader = new JXTableHeader(table.getColumnModel());
        MouseEvent event = new MouseEvent(tableHeader, 0,
                  0, 0, 40, 5, 0, false);
        String columnToolTip = "columnToolTip";
        columnExt.setToolTipText(columnToolTip);
        assertEquals(columnToolTip, tableHeader.getToolTipText(event));

    }
    
    /**
     * #212-swingx: second last column cannot be set to invisible programatically.
     * 
     * One reason for the "trick" of reselecting the last is that 
     * the header and with it the columnControl vanishes if there is 
     * no visible column.
     * 
     * 
     *
     */
    @Test
    public void testHeaderVisibleWithoutColumns() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run headerVisible - headless environment");
            return;
        }
        JXTable table = new JXTable();
        table.setColumnControlVisible(true);
        wrapWithScrollingInFrame(table, "");
        assertTrue("headerHeight must be > 0", table.getTableHeader().getHeight() > 0);
        assertEquals("headerWidth must be table width", 
                table.getWidth(), table.getTableHeader().getWidth());
        
    }
    
    /**
     * #212-swingx: second last column cannot be set to invisible programatically.
     * 
     * One reason for the "trick" of reselecting the last is that 
     * the header and with it the columnControl vanishes if there is 
     * no visible column.
     * 
     * 
     *
     */
    @Test
    public void testHeaderVisibleWithColumns() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run headerVisible - headless environment");
            return;
        }
        JXTable table = new JXTable(10, 2);
        table.setColumnControlVisible(true);
        wrapWithScrollingInFrame(table, "");
        assertTrue("headerHeight must be > 0", table.getTableHeader().getHeight() > 0);
        table.setModel(new DefaultTableModel());
        assertTrue("headerHeight must be > 0", table.getTableHeader().getHeight() > 0);
        
    }
    

//--------------------------------- visual checks
    /**
     * Issue #485-swingx: table header disappears if all header values are
     * empty. Compare core <--> JXTable
     * fixed for SwingX.
     *
     */
    public void interactiveHeaderSizeRequirements() {
        
        final String[] alternate = { 
                null, 
                null, 
                };
        final JTable table = new JTable(10, 2);
        table.getColumnModel().getColumn(0).setHeaderValue(alternate[0]);
        table.getColumnModel().getColumn(1).setHeaderValue(alternate[1]);
        
        JXTable xTable = new JXTable(10, 2);
        xTable.getColumn(0).setHeaderValue(alternate[0]);
        xTable.getColumn(1).setHeaderValue(alternate[1]);
        
        JXFrame frame = wrapWithScrollingInFrame(table, xTable, "header height empty (core - xtable)");
        frame.setVisible(true);
        
    }

    /**
     * Issue #390-swingx: JXTableHeader: throws AIOOB on removing dragged column.
     * 
     */
    public void interactiveDraggedColumnRemoved() {
        final JXTable table = new JXTable(10, 5);
        Action deleteColumn = new AbstractAction("deleteCurrentColumn") {

            public void actionPerformed(ActionEvent e) {
                TableColumn column = table.getTableHeader().getDraggedColumn();
                if (column == null) return;
                table.getColumnModel().removeColumn(column);
            }
            
        };
        KeyStroke keyStroke = KeyStroke.getKeyStroke("F1");
        table.getInputMap(JTable.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, "f1");
        table.getActionMap().put("f1", deleteColumn);
        JXFrame frame = wrapWithScrollingInFrame(table, "Remove dragged column with F1");
        frame.setVisible(true);
    }
    /**
     * Visual demo that header is always visible.
     */
    public void interactiveHeaderVisible() {
        final JXTable table = new JXTable();
        table.setColumnControlVisible(true);
        JXFrame frame = wrapWithScrollingInFrame(table, "header always visible");
        Action action = new AbstractAction("toggle model") {

            public void actionPerformed(ActionEvent e) {
                int columnCount = table.getColumnCount(true);
                table.setModel(columnCount > 0 ?
                        new DefaultTableModel() : new DefaultTableModel(10, 2));
                
            }
            
        };
        addAction(frame, action);
        frame.setVisible(true);
        
    }
    public static void main(String args[]) {
        JXTableHeaderTest test = new JXTableHeaderTest();
        try {
          test.runInteractiveTests();
         //   test.runInteractiveTests("interactive.*Siz.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        } 
    }

    
    @Override
    @Before
    public void setUp() {
        table = new JXTable(10, 5); 
        header = new JXTableHeader(table.getColumnModel());
    }


    
    @After
    public void tearDownJ4() throws Exception {
        tearDown();
    }
    


}
