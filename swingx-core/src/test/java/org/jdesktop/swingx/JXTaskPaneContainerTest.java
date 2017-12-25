/*
 * $Id: JXTaskPaneContainerTest.java 4226 2012-08-07 16:09:19Z kschaefe $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import javax.swing.plaf.UIResource;

import org.jdesktop.test.EDTRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EDTRunner.class)
public class JXTaskPaneContainerTest {

    @Test
    public void testAddon() throws Exception {
        // move around all addons
        TestUtilities.cycleAddons(new JXTaskPaneContainer());
    }

    @Test
    public void testScrollableTracks() {
        JXTaskPaneContainer container = new JXTaskPaneContainer();
        assertTrue(container.getScrollableTracksViewportWidth());
        assertEquals(ScrollableSizeHint.FIT, container.getScrollableWidthHint());
        // no parent, no tracking
        assertFalse(container.getScrollableTracksViewportHeight());
        assertEquals(ScrollableSizeHint.PREFERRED_STRETCH, container.getScrollableHeightHint());
    }

    /**
     * Issue #843-swingx: BasicTaskPaneContainerUI must respect custom Layout.
     */
    @Test
    public void testRespectCustomLayoutGap() {
        JXTaskPaneContainer container = new JXTaskPaneContainer();
        VerticalLayout layout = (VerticalLayout) container.getLayout();
        VerticalLayout custom = new VerticalLayout(layout.getGap() + 10);
        container.setLayout(custom);
        container.updateUI();
        assertEquals(custom.getGap(), ((VerticalLayout) container.getLayout()).getGap());
    }

    /**
     * Issue #843-swingx: BasicTaskPaneContainerUI must respect custom Layout.
     */
    @Test
    public void testRespectCustomLayout() {
        JXTaskPaneContainer container = new JXTaskPaneContainer();
        VerticalLayout layout = (VerticalLayout) container.getLayout();
        VerticalLayout custom = new VerticalLayout(layout.getGap() + 10);
        container.setLayout(custom);
        container.updateUI();
        assertSame(custom, container.getLayout());
    }

    /**
     * Issue #843-swingx: BasicTaskPaneContainerUI must respect custom Layout.
     */
    @Test
    public void testLayoutUIResource() {
        JXTaskPaneContainer container = new JXTaskPaneContainer();
        assertTrue(container.getLayout() instanceof UIResource);
    }
}
