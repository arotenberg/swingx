/*
 * $Id: TableRendererTest.java 3877 2010-11-03 11:36:33Z kleopatra $
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
 *
 */
package org.jdesktop.swingx.renderer;

import java.awt.Color;
import java.awt.Component;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.test.XTestUtils;
import org.jdesktop.test.SerializableSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests behaviour of SwingX renderers. Currently: mostly characterization to
 * guarantee that they behave similar to the standard.
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class TableRendererTest extends InteractiveTestCase {

    private static final Logger LOG = Logger.getLogger(TableRendererTest.class
            .getName());
    
    private JTable table;
    private int coreColumn;
    private DefaultTableCellRenderer coreTableRenderer;
    private int xColumn;
    private DefaultTableRenderer xTableRenderer;

    
    @Before
    public void setUpJ4() throws Exception {
        setUp();
    }
    
    @After
    public void tearDownJ4() throws Exception {
        tearDown();
    }
    
    /**
     * Issue #1345-swingx: TableCellContext optional handling of LAF provided alternateRowColor 
     */
    @Test
    public void testNotHandleAlternateColor() {
        TableCellRenderer renderer = new DefaultTableRenderer();
        Color oldColor = UIManager.getColor("Table.alternateRowColor");
        Object oldHandle = UIManager.get(TableCellContext.HANDLE_ALTERNATE_ROW_BACKGROUND);
        try {
            // alternate color
            UIManager.put("Table.alternateRowColor", Color.MAGENTA);
            // remove handle flag
            UIManager.put(TableCellContext.HANDLE_ALTERNATE_ROW_BACKGROUND, null);
            JTable table = new JTable(10, 2);
            Component comp = renderer.getTableCellRendererComponent(table, null, false, false, 0, -1);
            // even row has table background
            assertEquals(table.getBackground(), comp.getBackground());
            // odd row has alternate background if handle active - here it is not, so expect 
            // stable background
            renderer.getTableCellRendererComponent(table, null, false, false, 1, -1);
            assertEquals(table.getBackground(), comp.getBackground());
        } finally {
            UIManager.put("Table.alternateRowColor", oldColor);
            UIManager.put(TableCellContext.HANDLE_ALTERNATE_ROW_BACKGROUND, oldHandle);
        }
    }
    
    /**
     * Issue #1345-swingx: TableCellContext optional handling of LAF provided alternateRowColor 
     */
    @Test
    public void testAlternateColor() {
        TableCellRenderer renderer = new DefaultTableRenderer();
        Color oldColor = UIManager.getColor("Table.alternateRowColor");
        Object oldHandle = UIManager.get(TableCellContext.HANDLE_ALTERNATE_ROW_BACKGROUND);
        try {
            
            UIManager.put("Table.alternateRowColor", Color.MAGENTA);
            UIManager.put(TableCellContext.HANDLE_ALTERNATE_ROW_BACKGROUND, Boolean.TRUE);
            JTable table = new JTable(10, 2);
            Component comp = renderer.getTableCellRendererComponent(table, null, false, false, 0, -1);
            // even row has table background
            assertEquals(table.getBackground(), comp.getBackground());
            // odd row has alternate background
            renderer.getTableCellRendererComponent(table, null, false, false, 1, -1);
            assertEquals(Color.MAGENTA, comp.getBackground());
        } finally {
            UIManager.put("Table.alternateRowColor", oldColor);
            UIManager.put(TableCellContext.HANDLE_ALTERNATE_ROW_BACKGROUND, oldHandle);
        }
    }
    
    @Test
    public void testUIPropertyHandleAlternateRowColor() {
        assertNull(UIManager.get(TableCellContext.HANDLE_ALTERNATE_ROW_BACKGROUND));
    }
    
    @Override
    protected void setUp() throws Exception {
        // setup table
        table = new JTable(10, 2);
        coreColumn = 0; 
        coreTableRenderer = new DefaultTableCellRenderer();
        table.getColumnModel().getColumn(coreColumn).setCellRenderer(coreTableRenderer);
        xColumn = 1;
        xTableRenderer = new DefaultTableRenderer();
        table.getColumnModel().getColumn(xColumn).setCellRenderer(xTableRenderer);
    }
    
    /**
     * Issue #484-swingx: dnd on color not showing.
     * Can't really test - can't mock a drop-on only bare context methods.
     */
    @Test
    public void testCellContextDropOn() {
        TableCellContext cellContext = new TableCellContext();
        cellContext.installContext(null, "whatever", 0, 0, false, true, false, false);
        assertFalse("context must cope with null component", cellContext.isDropOn());
        // fake ... and test internals
        cellContext.dropOn = true;
        assertTrue("context must use dropOn flag ... dooh", cellContext.isDropOn());
    }
    /**
     * Issue #484-swingx: dnd on color not showing.
     * Can't really test - can't mock a drop-on only bare context methods.
     */
    @Test
    public void testCellContextDropOnColors() {
        TableCellContext cellContext = new TableCellContext();
        Color dropBackground = UIManager.getColor("Table.dropCellBackground");
        LOG.info("background " + dropBackground);
        if (dropBackground != null) {
            assertEquals(dropBackground, cellContext.getDropCellBackground());
        }
        Color dropForeground = UIManager.getColor("Table.dropCellForeground");
        LOG.info("foreground " + dropForeground);
        if (dropForeground != null) {
            assertEquals(dropForeground, cellContext.getDropCellForeground());
        }
    }
    /**
     * Issue ??-swingx: getEditable throws if cell coordinates invalid
     */
    @Test
    public void testCellContextEditable() {
        TableCellContext cellContext = new TableCellContext();
        cellContext.installContext(null, "whatever", 0, 0, false, true, false, false);
        assertFalse("context must cope with null component", cellContext.isEditable());
        JXTable table = new JXTable();
        cellContext.installContext(table, "whatever", 0, 0, false, true, false, false);
        // KEEP commented lines is real-world example that'll blow
//        DefaultVisuals visuals = new DefaultVisuals();
//        visuals.configureVisuals(new JLabel(), cellContext);
        assertFalse("context must cope with invalid coordinates", cellContext.isEditable());
    }
    
    /**
     * Test constructors: here convenience with alignment and converter
     *
     */
    @Test
    public void testConstructor() {
        FormatStringValue sv = new FormatStringValue(DateFormat.getTimeInstance());
        int align = JLabel.RIGHT;
        DefaultTableRenderer renderer = new DefaultTableRenderer(sv, align);
        assertEquals(sv, renderer.componentController.getStringValue());
        assertEquals(align, renderer.componentController.getHorizontalAlignment());
    }
    /**
     * test if icon handling is the same for core default and
     * swingx.
     *
     */
    @Test
    public void testIcon() {
        TableModel model = createTableModelWithDefaultTypes();
        int iconColumn = 4;
        // sanity
        assertTrue(Icon.class.isAssignableFrom(model.getColumnClass(iconColumn)));
        Icon icon = (Icon) model.getValueAt(0, iconColumn);
        // default uses a different class for icon rendering
        DefaultTableCellRenderer coreIconRenderer = (DefaultTableCellRenderer) table.getDefaultRenderer(Icon.class);
        // core default can't cope with null component - can't really compare behaviour
        coreIconRenderer.getTableCellRendererComponent(table, icon, false, false, -1, -1);
        assertEquals(icon, coreIconRenderer.getIcon());
        assertEquals("", coreIconRenderer.getText());
        JXTable xTable = new JXTable();
        TableCellRenderer xIconRenderer = xTable.getDefaultRenderer(Icon.class);
        JLabel label = (JLabel) xIconRenderer.getTableCellRendererComponent(null, icon, false, false, -1, -1);
        assertEquals(icon, label.getIcon());
        assertEquals("", label.getText());
        // wrong assumption after fix of #591-swingx - default icon renderer
        // no longer tries to be clever
//        label = (JLabel) xIconRenderer.getTableCellRendererComponent(null, "dummy", false, false, -1, -1);
//        assertNull(label.getIcon());
//        assertEquals("dummy", label.getText());        
    }
        /**
     * @return
     */
    private TableModel createTableModelWithDefaultTypes() {
        String[] names = {"Object", "Number", "Double", "Date", "ImageIcon", "Boolean"};
        final Class<?>[] types = {Object.class, Number.class, Double.class, Date.class, ImageIcon.class, Boolean.class};
        DefaultTableModel model = new DefaultTableModel(names, 0) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
            
        };
        Date today = new Date();
        Icon icon = XTestUtils.loadDefaultIcon();
        for (int i = 0; i < 10; i++) {
            Object[] values = new Object[] {"row " + i, i, Math.random() * 100, 
                    new Date(today.getTime() + i * 1000000), icon, i % 2 == 0};
            model.addRow(values);
        }
        return model;
    }

 
    
    /**
     * test serializable of default renderer.
     * 
     */
    @Test
    public void testSerializeTableRenderer() {
        TableCellRenderer xListRenderer = new DefaultTableRenderer();
        try {
            SerializableSupport.serialize(xListRenderer);
        } catch (Exception e) {
            fail("not serializable " + e);
        } 
    }
 

    /**
     * base interaction with table: focused, selected uses UI border.
     */
    @Test
    public void testTableFocusSelectedBorder() {
        // sanity to see test test validity
//        UIManager.put("Table.focusSelectedCellHighlightBorder", new LineBorder(Color.red));
        // access ui colors
        Border selectedFocusBorder = getFocusBorder(true);
        // sanity
        if (selectedFocusBorder == null) {
            LOG.info("cannot run focusSelectedBorder - UI has no selected focus border");
            return;
            
        }
        // need to prepare directly - focus is true only if table is focusowner
        JComponent coreComponent = (JComponent) coreTableRenderer.getTableCellRendererComponent(table, 
                null, true, true, 0, coreColumn);
        // sanity: known standard behaviour
        assertEquals(selectedFocusBorder, coreComponent.getBorder());
        // prepare extended
        JComponent xComponent = (JComponent) xTableRenderer.getTableCellRendererComponent(table, 
                null, true, true, 0, xColumn);
        // assert behaviour same as standard
        assertEquals(coreComponent.getBorder(), xComponent.getBorder());
    }

    private Border getFocusBorder(boolean lookup) {
        Border selectedFocusBorder = UIManager.getBorder("Table.focusSelectedCellHighlightBorder");
        if (lookup && (selectedFocusBorder == null)) {
            selectedFocusBorder = UIManager.getBorder("Table.focusCellHighlightBorder");
        }
        return selectedFocusBorder;
    }

    /**
     * base interaction with table: focused, not-selected uses UI border.
     * 
     *
     */
    @Test
    public void testTableFocusBorder() {
        // access ui colors
        Border focusBorder = UIManager.getBorder("Table.focusCellHighlightBorder");
//        Border selectedFocusBorder = UIManager.getBorder("Table.focusSelectedCellHighlightBorder");
        // sanity
        assertNotNull(focusBorder);
        // need to prepare directly - focus is true only if table is focusowner
        JComponent coreComponent = (JComponent) coreTableRenderer.getTableCellRendererComponent(table, 
                null, false, true, 0, coreColumn);
        // sanity: known standard behaviour
        assertEquals(focusBorder, coreComponent.getBorder());
        // prepare extended
        JComponent xComponent = (JComponent) xTableRenderer.getTableCellRendererComponent(table, 
                null, false, true, 0, xColumn);
        // assert behaviour same as standard
        assertEquals(coreComponent.getBorder(), xComponent.getBorder());
    }
    /**
     * base interaction with table: focused, not-selected and editable 
     * uses UI colors.
     *
     */
    @Test
    public void testTableRendererExtFocusedNotSelectedEditable() {
        // sanity
        assertTrue(table.isCellEditable(0, coreColumn));
        // access ui colors
        Color uiForeground = UIManager.getColor("Table.focusCellForeground");
        Color uiBackground = UIManager.getColor("Table.focusCellBackground");
        // sanity
        assertNotNull(uiForeground);
        assertNotNull(uiBackground);
        Color background = Color.MAGENTA;
        Color foreground = Color.YELLOW;
        // prepare standard
        coreTableRenderer.setBackground(background);
        coreTableRenderer.setForeground(foreground);
        // need to prepare directly - focus is true only if table is focusowner
        Component coreComponent = coreTableRenderer.getTableCellRendererComponent(table, 
                null, false, true, 0, coreColumn);
        // sanity: known standard behaviour
        assertEquals(uiBackground, coreComponent.getBackground());
        assertEquals(uiForeground, coreComponent.getForeground());
        // prepare extended
        xTableRenderer.setBackground(background);
        xTableRenderer.setForeground(foreground);
        Component xComponent = xTableRenderer.getTableCellRendererComponent(table, 
                null, false, true, 0, xColumn);
        // assert behaviour same as standard
        assertEquals(coreComponent.getBackground(), xComponent.getBackground());
        assertEquals(coreComponent.getForeground(), xComponent.getForeground());
    }
    
    /**
     * base interaction with table: custom color of renderer precedes
     * table color.
     *
     */
    @Test
    public void testTableRendererExtCustomColor() {
        Color background = Color.MAGENTA;
        Color foreground = Color.YELLOW;
        // prepare standard
        coreTableRenderer.setBackground(background);
        coreTableRenderer.setForeground(foreground);
        Component coreComponent = table.prepareRenderer(coreTableRenderer, 0, coreColumn);
        // sanity: known standard behaviour
        assertEquals(background, coreComponent.getBackground());
        assertEquals(foreground, coreComponent.getForeground());
        // prepare extended
        xTableRenderer.setBackground(background);
        xTableRenderer.setForeground(foreground);
        Component xComponent = table.prepareRenderer(xTableRenderer, 0, xColumn);
        // assert behaviour same as standard
        assertEquals(coreComponent.getBackground(), xComponent.getBackground());
        assertEquals(coreComponent.getForeground(), xComponent.getForeground());
    }

    /**
     * base interaction with table: renderer uses table's selection color.
     *
     */
    @Test
    public void testTableRendererExtSelectedColors() {
        // select first row
        table.setRowSelectionInterval(0, 0);
        // prepare standard
        Component coreComponent = table.prepareRenderer(coreTableRenderer, 0, coreColumn);
        // sanity: known standard behaviour
        assertEquals(table.getSelectionBackground(), coreComponent.getBackground());
        assertEquals(table.getSelectionForeground(), coreComponent.getForeground());
        // prepare extended
        Component xComponent = table.prepareRenderer(xTableRenderer, 0, xColumn);
        // assert behaviour same as standard
        assertEquals(coreComponent.getBackground(), xComponent.getBackground());
        assertEquals(coreComponent.getForeground(), xComponent.getForeground());
    }
    
    /**
     * base interaction with table: renderer uses table's custom selection color.
     *
     */
    @Test
    public void testTableRendererExtTableSelectedColors() {
        Color background = Color.MAGENTA;
        Color foreground = Color.YELLOW;
        table.setSelectionBackground(background);
        table.setSelectionForeground(foreground);
        // select first row
        table.setRowSelectionInterval(0, 0);
        // prepare standard
        Component coreComponent = table.prepareRenderer(coreTableRenderer, 0, coreColumn);
        // sanity: known standard behaviour
        assertEquals(table.getSelectionBackground(), coreComponent.getBackground());
        assertEquals(table.getSelectionForeground(), coreComponent.getForeground());
        // prepare extended
        Component xComponent = table.prepareRenderer(xTableRenderer, 0, xColumn);
        // assert behaviour same as standard
        assertEquals(coreComponent.getBackground(), xComponent.getBackground());
        assertEquals(coreComponent.getForeground(), xComponent.getForeground());
    }

    /**
     * base interaction with table: renderer uses table's unselected colors.
     *
     */
    @Test
    public void testTableRendererExtColors() {
        // prepare standard
        Component coreComponent = table.prepareRenderer(coreTableRenderer, 0, coreColumn);
        // sanity: known standard behaviour
        assertEquals(table.getBackground(), coreComponent.getBackground());
        assertEquals(table.getForeground(), coreComponent.getForeground());
        // prepare extended
        Component xComponent = table.prepareRenderer(xTableRenderer, 0, xColumn);
        // assert behaviour same as standard
        assertEquals(coreComponent.getBackground(), xComponent.getBackground());
        assertEquals(coreComponent.getForeground(), xComponent.getForeground());
    }
    
    /**
     * base interaction with table: renderer uses table's unselected custom colors
     * 
     *
     */
    @Test
    public void testTableRendererExtTableColors() {
        Color background = Color.MAGENTA;
        Color foreground = Color.YELLOW;
        table.setBackground(background);
        table.setForeground(foreground);
        // prepare standard
        Component coreComponent = table.prepareRenderer(coreTableRenderer, 0, coreColumn);
        // sanity: known standard behaviour
        assertEquals(table.getBackground(), coreComponent.getBackground());
        assertEquals(table.getForeground(), coreComponent.getForeground());
        // prepare extended
        Component xComponent = table.prepareRenderer(xTableRenderer, 0, xColumn);
        // assert behaviour same as standard
        assertEquals(coreComponent.getBackground(), xComponent.getBackground());
        assertEquals(coreComponent.getForeground(), xComponent.getForeground());
    }

    /**
     * characterize opaqueness of rendering components.
     *
     */
    @Test
    public void testTableOpaqueRenderer() {
        // sanity
        assertFalse(new JLabel().isOpaque());
        assertTrue(coreTableRenderer.isOpaque());
//        assertTrue(xTableRenderer.getRendererComponent().isOpaque());
    }
    
    /**
     * characterize opaqueness of rendering components.
     * 
     * that's useless: the opaque magic only applies if parent != null
     */
    @Test
    public void testTableOpaqueRendererComponent() {
        // sanity
        assertFalse(new JLabel().isOpaque());
        Component coreComponent = table.prepareRenderer(coreTableRenderer, 0, coreColumn);
        // prepare extended
        assertTrue(coreComponent.isOpaque());
        Component xComponent = table.prepareRenderer(xTableRenderer, 0, xColumn);
        assertTrue(xComponent.isOpaque());
    }


    /**
     * base existence/type tests while adding DefaultTableCellRendererExt.
     *
     */
    @Test
    public void testTableRendererExt() {
        DefaultTableRenderer renderer = new DefaultTableRenderer();
        assertTrue(renderer instanceof TableCellRenderer);
        assertTrue(renderer instanceof Serializable);
        
    }
}
