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
package org.jdesktop.swingx.renderer;

import java.awt.Font;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXTable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * CellContext related tests.<p>
 * 
 * PENDING JW: should move all bare context related tests here - currently they are spread
 * across the rendererxxtests, hard to find.
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class CellContextTest extends InteractiveTestCase {

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(CellContextTest.class
            .getName());
    
    @Test
    public void testNameInContext() {
        assertEquals("cellRenderer", new CellContext().getCellRendererName());
        assertEquals("Table.cellRenderer", new TableCellContext().getCellRendererName());
        assertEquals("Tree.cellRenderer", new TreeCellContext().getCellRendererName());
        assertEquals("List.cellRenderer", new ListCellContext().getCellRendererName());
    }
    
    @Test
    public void testDefaultVisualsSetName() {
        CellContext context = new TableCellContext();
        JLabel label = new JLabel();
        new DefaultVisuals<JLabel>().configureVisuals(label, context);
        assertEquals("DefaultVisuals must set name", "Table.cellRenderer", label.getName());
    }
    
    @Test
    public void testDefaultVisualsUseContextFont() {
        JXTable table = new JXTable();
        final Font font = table.getFont().deriveFont(50f);
        TableCellContext context = new TableCellContext() {

            @Override
            protected Font getFont() {
                return font;
            }
            
        };
        context.installContext(table, -1, -1, -1, false, false, false, false);
        JLabel label = new JLabel();
        new DefaultVisuals<JLabel>().configureVisuals(label, context);
        assertEquals(font, label.getFont());
    }
    
    @Test
    public void testFontWithoutComponent() {
        CellContext context = new CellContext();
        assertNull(context.getFont());
    }
    
    @Test
    public void testFontWithComponent() {
        final JLabel label = new JLabel();
        CellContext context = new CellContext() {

            @Override
            public JComponent getComponent() {
                return label;
            }
            
        };
        assertNotNull("sanity", label.getFont());
        assertEquals(label.getFont(), context.getFont());
    }
    
    /**
     * Issue #1151-swingx: Nimbus border not used in renderer. 
     * @throws Exception 
     */
    @Test
    public void testListContextNoFocusBorder() throws Exception {
        assertNimbusNoFocusBorder(new ListCellContext());
    }
    /**
     * Issue #1151-swingx: Nimbus border not used in renderer. 
     * @throws Exception 
     */
    @Test
    public void testTableContextNoFocusBorder() throws Exception {
        assertNimbusNoFocusBorder(new TableCellContext());
    }
    
    /**
     * Issue #1151-swingx: Nimbus border not used. 
     * @throws Exception 
     */
    private void assertNimbusNoFocusBorder(CellContext context) throws Exception {
        if (!hasLookAndFeel("Nimbus")) {
            LOG.fine("can't run - no Nimbus");
            return;
        }
        LookAndFeel lf = UIManager.getLookAndFeel();
        try {
            setLookAndFeel("Nimbus");
            context.installState(null, -1, -1, false, false, false, false);
            assertEquals(UIManager.getBorder(context.getUIPrefix() + "cellNoFocusBorder"), context.getBorder());
        } finally {
            UIManager.setLookAndFeel(lf);
        }
    }

    @Override
    @Before
    public void setUp() throws Exception {
    }
    
    
}
