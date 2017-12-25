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
package org.jdesktop.swingx.plaf.basic.core;

import java.util.logging.Logger;

import javax.swing.JList;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.synth.SynthContext;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.plaf.synth.SynthUI;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Issues around ListUI replacement.<p>
 * 
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class XListUIIssues extends InteractiveTestCase {

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(XListUIIssues.class
            .getName());
    /**
     * getContext(component) should throw if given component is not ours (for
     * 1:1 delegate implemenations). 
     * 
     * PENDING JW: moved out of XListUITest because server doesn't know Nimbus
     * back again if jdk version updated to u10+. Or: can we have conditions
     * in the expected clause?
     * 
     * @throws Exception 
     */
    @Test (expected=IllegalArgumentException.class)
    public void testSynthUIX() throws Exception {
        LookAndFeel lf = UIManager.getLookAndFeel();
        try {
            setLookAndFeel("Nimbus");
            JXList list = new JXList();
            SynthUI ui = (SynthUI) list.getUI();
            ui.getContext(new JXList());
        } finally {
            UIManager.setLookAndFeel(lf);
        }
        
    }


    /**
     * getContext(component) should throw if given component is not ours (for
     * 1:1 delegate implemenations). 
     * @throws Exception 
     */
    @Test
    public void testSynthUICore() throws Exception {
        LookAndFeel lf = UIManager.getLookAndFeel();
        try {
            setLookAndFeel("Nimbus");
            JList list = new JList();
            SynthUI ui = (SynthUI) list.getUI();
            SynthContext context = ui.getContext(new JList());
            assertEquals(list, context.getComponent());
            setLookAndFeel("Metal");
            list.updateUI();
            setLookAndFeel("Nimbus");
            list.updateUI();
        } finally {
            UIManager.setLookAndFeel(lf);
        }
        
    }
    /**
     * sanity: expect to pass if reverted to not use xlistui
     */
    @Test
    public void testDisabledExtendedClassID() {
        JXList list = new JXList();
        assertSame(new JList().getUIClassID(), list.getUIClassID());
    }

    /**
     * sanity: expect to pass if reverted to not use xlistui
     */
    @Test
    public void testDisabledExtendedUI() {
        JXList list = new JXList();
        assertFalse("xlist must have BasicXListUI instead of " + list.getUI().getClass().getSimpleName(),
                list.getUI() instanceof BasicXListUI);
        
    }

}
