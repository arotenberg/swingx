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
package org.jdesktop.swingx.plaf;

import java.util.logging.Logger;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.util.OS;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Test related to WindowsLookAndFeelAddons.
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class WindowsLookAndFeelAddonIssues extends InteractiveTestCase {

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(WindowsLookAndFeelAddonIssues.class.getName());
    
    /**
     * Issue #1305-swingx: popup closing must not consume mousePressed.
     * 
     * It's a core issue (#6753637) which is easily fixed by setting 
     * the ui property. Test if it's done. 
     * 
     * Reverted: different native behaviour of combos vs standalone popups. A 
     * single ui property can serve one of those only, core opted for combos.
     * Should be consistent.
     * 
     * @throws UnsupportedLookAndFeelException 
     */
    @Test
    public void testWindowsNotConsumeEventOnClose() throws Exception {
        if (!OS.isWindows()) {
            LOG.fine("not on windows, can run windowsLAF specific test");
            return;
        }
        LookAndFeel old = UIManager.getLookAndFeel();
        assertTrue(LookAndFeelAddons.isTrackingLookAndFeelChanges());
        try {
            setLookAndFeel("Windows");
            assertEquals(Boolean.FALSE, UIManager.get("PopupMenu.consumeEventOnClose"));
            setLookAndFeel("Motif");
            assertEquals(Boolean.TRUE, UIManager.get("PopupMenu.consumeEventOnClose"));
        } finally {
            UIManager.setLookAndFeel(old);
        }
    }
    
    
 
//--------------------- interactive 
 
    
    public static void main(String[] args) {
        WindowsLookAndFeelAddonIssues test = new WindowsLookAndFeelAddonIssues();
        try {
            test.runInteractiveTests();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Issue #1305-swingx: popup closing must not consume mousePressed.
     * 
     * Most visible effect: combo editor in table - open editor, click
     * somewhere else, edit must be terminated (for windows and metal)
     */
    public void interactivePopupNotConsumesOnPressed() {
        JXTable table = new JXTable(10, 6);
        JComboBox box = new JComboBox(new Object[] {1, 2, 3});
        table.getColumn(0).setCellEditor(new DefaultCellEditor(box));
        showWithScrollingInFrame(table, "combo editor in first");
    }
}
