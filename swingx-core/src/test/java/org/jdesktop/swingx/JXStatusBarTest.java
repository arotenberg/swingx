/*
 * $Id: JXStatusBarTest.java 3473 2009-08-27 13:17:10Z kleopatra $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 *
 * @author rbair
 */
@RunWith(JUnit4.class)
public class JXStatusBarTest extends junit.framework.TestCase {
    
    /**
     * This test ensures that after the LAF is switched, the Border and
     * LayoutManager are still in valid states.
     */
    @Test
    public void testLAFSwitch() throws Exception {
        //create a status bar with a single child, a label.
        //ensure that after switching look and feels, the layout manager
        //on the status bar still references the label
        JXStatusBar bar = new JXStatusBar();
        JLabel label = new JLabel("Hello");
        bar.add(label);
        Dimension dim = bar.getLayout().preferredLayoutSize(bar);
        toggleLAF();
        toggleLAF();
        assertEquals(dim, bar.getLayout().preferredLayoutSize(bar));
    }

    private void toggleLAF() throws Exception {
        LookAndFeel laf = UIManager.getLookAndFeel();
        if (laf == null || laf.getName().equals(UIManager.getSystemLookAndFeelClassName())) {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } else {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
    }
}
