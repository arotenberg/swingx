/*
 * $Id: ListRendererTest.java 3440 2009-08-11 10:13:59Z kleopatra $
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
import java.util.logging.Logger;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;

import org.jdesktop.swingx.InteractiveTestCase;
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
public class ListRendererTest extends InteractiveTestCase {

    private static final Logger LOG = Logger.getLogger(ListRendererTest.class
            .getName());
    

    private DefaultListCellRenderer coreListRenderer;

    private DefaultListRenderer xListRenderer;

    private JList list;
    
    @Before
    public void setUpJ4() throws Exception {
        setUp();
    }
    
    @After
    public void tearDownJ4() throws Exception {
        tearDown();
    }
    
    @Override
    protected void setUp() throws Exception {
        list = new JList(new Object[] {1, 2, 3});
        coreListRenderer = new DefaultListCellRenderer();
        xListRenderer = new DefaultListRenderer();
    }

    /**
     * Test constructors: here convenience with alignment and converter
     *
     */
    @Test
    public void testConstructor() {
        FormatStringValue sv = new FormatStringValue(DateFormat.getTimeInstance());
        int align = JLabel.RIGHT;
        DefaultListRenderer renderer = new DefaultListRenderer(sv, align);
        assertEquals(sv, renderer.componentController.getStringValue());
        assertEquals(align, renderer.componentController.getHorizontalAlignment());
    }

    /**
     * test if default icon/text handling in DefaultListRenderer 
     * is the same as core default. 
      *
     */
    @Test
    public void testIcon() {
        Icon icon = XTestUtils.loadDefaultIcon();
        String text = "dummy";
        JList list = new JList(new Object[] {icon, text});
        coreListRenderer.getListCellRendererComponent(list, icon, 0, false, false);
        JLabel label = (JLabel) xListRenderer.getListCellRendererComponent(null, icon, 0, false, false);
        assertEquals("sanity: core shows icon", icon, coreListRenderer.getIcon());
        assertEquals("sanity: core shows empty string", "", coreListRenderer.getText());
        assertEquals("swingx renderer same icon as core", coreListRenderer.getIcon(), label.getIcon());
        assertEquals("swingx renderer same text as core", coreListRenderer.getText(), label.getText());
        coreListRenderer.getListCellRendererComponent(list, text, 1, false, false);
        label = (JLabel) xListRenderer.getListCellRendererComponent(null, text, 1, false, false);
        assertNull("sanity: core has null icon", coreListRenderer.getIcon());
        assertEquals("sanity: core shows text", text, coreListRenderer.getText());
        assertEquals("swingx renderer same same icon as core", coreListRenderer.getIcon(), label.getIcon());
        assertEquals("swingx renderer same text as core", coreListRenderer.getText(), label.getText());
    }
 
    /**
     * test serializable of default renderer.
     * 
     */
    @Test
    public void testSerializeListRenderer() {
        ListCellRenderer xListRenderer = new DefaultListRenderer();
        try {
            SerializableSupport.serialize(xListRenderer);
        } catch (Exception e) {
            fail("not serializable " + e);
        }
    }


    /**
     * base interaction with list: focused, not-selected uses UI border.
     */
    @Test
    public void testListFocusSelectedBorder() {
        // access ui border
        Border selectedFocusBorder = getFocusBorder(true);
        // sanity
        if (selectedFocusBorder == null) {
            LOG.info("cannot run focusSelectedBorder - UI has no selected focus border");
            return;
            
        }
        // need to prepare directly - focus is true only if list is focusowner
        JComponent coreComponent = (JComponent) coreListRenderer.getListCellRendererComponent(list, 
                null, 0, true, true);
        // sanity: known standard behaviour
        assertEquals(selectedFocusBorder, coreComponent.getBorder());
        // prepare extended
        JComponent xComponent = (JComponent) xListRenderer.getListCellRendererComponent(list, 
                null, 0, true, true);
        // assert behaviour same as standard
        assertEquals(coreComponent.getBorder(), xComponent.getBorder());
    }


    /**
     * Returns the focus border as registered in the UIManager or null no border found.
     * 
     * @param lookup if true, tries fallback with more common key.
     * @return focusBorder as registered in the UIManagar, or null if no border found.
     */
    private Border getFocusBorder(boolean lookup) {
        Border selectedFocusBorder = UIManager.getBorder("List.focusSelectedCellHighlightBorder");
        if (lookup && (selectedFocusBorder == null)) {
            selectedFocusBorder = UIManager.getBorder("List.focusCellHighlightBorder");
        }
        return selectedFocusBorder;
    }

    /**
     * base interaction with list: focused, not-selected uses UI border.
     * 
     */
    @Test
    public void testListFocusBorder() {
        // access ui colors
        Border focusBorder = UIManager.getBorder("List.focusCellHighlightBorder");
        if (focusBorder == null) {
            LOG.info("cannot run test - ui has no special border for focused list cell");
            return;
        }
        // need to prepare directly - focus is true only if list is focusowner
        JComponent coreComponent = (JComponent) coreListRenderer.getListCellRendererComponent(list, 
                null, 0, false, true);
        // sanity: known standard behaviour
        assertEquals(focusBorder, coreComponent.getBorder());
        // prepare extended
        JComponent xComponent = (JComponent) xListRenderer.getListCellRendererComponent(list, 
                null, 0, false, true);
        // assert behaviour same as standard
        assertEquals(coreComponent.getBorder(), xComponent.getBorder());
    }

    /**
     * base interaction with table: custom color of renderer precedes
     * table color.
     *
     */
    @Test
    public void testListRendererExtCustomColor() {
        Color background = Color.MAGENTA;
        Color foreground = Color.YELLOW;
        
//        // prepare standard - not applicable for core default list renderer
//        coreListRenderer.setBackground(background);
//        coreListRenderer.setForeground(foreground);
//        Component coreComponent = coreListRenderer.getListCellRendererComponent(list, 
//                null, 0, false, false);

        // prepare extended
        xListRenderer.setBackground(background);
        xListRenderer.setForeground(foreground);
        Component xComponent = xListRenderer.getListCellRendererComponent(list, 
                null, 0, false, false);
        // assert behaviour same as standard
        assertEquals(background, xComponent.getBackground());
        assertEquals(foreground, xComponent.getForeground());
    }

    
    /**
     * base interaction with list: renderer uses list's selection color.
     *
     */
    @Test
    public void testListRendererExtSelectedColors() {
        // select first row
        list.setSelectedIndex(0);
        // prepare standard
        Component coreComponent = coreListRenderer.getListCellRendererComponent(list, 
                null, 0, true, false);
        // sanity: known standard behaviour
        assertEquals(list.getSelectionBackground(), coreComponent.getBackground());
        assertEquals(list.getSelectionForeground(), coreComponent.getForeground());
        // prepare extended
        Component xComponent = xListRenderer.getListCellRendererComponent(list, 
                null, 0, true, false);
        // assert behaviour same as standard
        assertEquals(coreComponent.getBackground(), xComponent.getBackground());
        assertEquals(coreComponent.getForeground(), xComponent.getForeground());
    }
    
    /**
     * base interaction with list: renderer uses list's custom selection color.
     *
     */
    @Test
    public void testListRendererExtListSelectedColors() {
        Color background = Color.MAGENTA;
        Color foreground = Color.YELLOW;
        list.setSelectionBackground(background);
        list.setSelectionForeground(foreground);
        // select first row
        list.setSelectedIndex(0);
        // prepare standard
        Component coreComponent = coreListRenderer.getListCellRendererComponent(list, 
                null, 0, true, false);
        // sanity: known standard behaviour
        assertEquals(list.getSelectionBackground(), coreComponent.getBackground());
        assertEquals(list.getSelectionForeground(), coreComponent.getForeground());
        // prepare extended
        Component xComponent = xListRenderer.getListCellRendererComponent(list, 
                null, 0, true, false);
        // assert behaviour same as standard
        assertEquals(coreComponent.getBackground(), xComponent.getBackground());
        assertEquals(coreComponent.getForeground(), xComponent.getForeground());
    }


    /**
     * base interaction with list: renderer uses list's unselected custom colors
     * 
     *
     */
    @Test
    public void testListRendererExtListColors() {
        Color background = Color.MAGENTA;
        Color foreground = Color.YELLOW;
        list.setBackground(background);
        list.setForeground(foreground);
        // prepare standard
        Component coreComponent = coreListRenderer.getListCellRendererComponent(list, 
                null, 0, false, false);
        // sanity: known standard behaviour
        assertEquals(list.getBackground(), coreComponent.getBackground());
        assertEquals(list.getForeground(), coreComponent.getForeground());
        // prepare extended
        Component xComponent = xListRenderer.getListCellRendererComponent(list, 
                null, 0, false, false);
        // assert behaviour same as standard
        assertEquals(coreComponent.getBackground(), xComponent.getBackground());
        assertEquals(coreComponent.getForeground(), xComponent.getForeground());
        
    }
    
    /**
     * base interaction with list: renderer uses list's unselected  colors
     * 
     *
     */
    @Test
    public void testListRendererExtColors() {
        // prepare standard
        Component coreComponent = coreListRenderer.getListCellRendererComponent(list, 
                null, 0, false, false);
        // sanity: known standard behaviour
        assertEquals(list.getBackground(), coreComponent.getBackground());
        assertEquals(list.getForeground(), coreComponent.getForeground());
        // prepare extended
        Component xComponent = xListRenderer.getListCellRendererComponent(list, 
                null, 0, false, false);
        // assert behaviour same as standard
        assertEquals(coreComponent.getBackground(), xComponent.getBackground());
        assertEquals(coreComponent.getForeground(), xComponent.getForeground());
    }
    /**
     * characterize opaqueness of rendering components.
     *
     */
    @Test
    public void testListOpaqueRenderer() {
        // sanity
        assertFalse(new JLabel().isOpaque());
        assertTrue(coreListRenderer.isOpaque());
//        assertTrue(xListRenderer.getRendererComponent().isOpaque());
    }
   
    /**
     * base existence/type tests while adding DefaultTableCellRendererExt.
     *
     */
    @Test
    public void testListRendererExt() {
        DefaultListRenderer renderer = new DefaultListRenderer();
        assertTrue(renderer instanceof ListCellRenderer);
        assertTrue(renderer instanceof Serializable);
        
    }
    
}
