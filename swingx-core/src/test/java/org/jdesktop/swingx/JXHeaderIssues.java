/*
 * $Id: JXHeaderIssues.java 3487 2009-09-04 10:11:00Z kleopatra $
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
package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.GradientPaint;
import java.util.logging.Logger;

import org.jdesktop.swingx.painter.MattePainter;


/**
 * Test to expose known issues of <code>JXHeader</code>.
 * <p>
 * 
 * Ideally, there would be at least one failing test method per open issue in
 * the issue tracker. Plus additional failing test methods for not fully
 * specified or not yet decided upon features/behaviour.
 * <p>
 * 
 * If an issue is fixed and the corresponding methods are passing, they
 * should be moved over to the XXTest.
 * 
 * @author Jeanette Winzenburg
 */
public class JXHeaderIssues extends InteractiveTestCase {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(JXHeaderIssues.class
            .getName());
    public static void main(String args[]) {
        JXHeaderIssues test = new JXHeaderIssues();
        try {
          test.runInteractiveTests();
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        } 
    }

    /**
     * Issue 1167-swingx: all components with horizontal gradients must be
     * ComponentOrientation aware.
     * 
     */
    public void interactiveHeaderGradient() {
        JXHeader header = new JXHeader();
        JXFrame frame = wrapInFrame(header, "gradient not CO-aware");
        addComponentOrientationToggle(frame);
        show(frame, 500, 500);
    }
    
    /**
     * Issue 1167-swingx: all components with horizontal gradients must be
     * ComponentOrientation aware.
     * 
     */
    public void interactiveLoginGradient() {
        JXLoginPane header = new JXLoginPane();
        JXFrame frame = wrapInFrame(header, "gradient not CO-aware");
        addComponentOrientationToggle(frame);
        show(frame, 500, 500);
    }
    
    /**
     * Issue 1167-swingx: all components with horizontal gradients must be
     * ComponentOrientation aware.
     * 
     */
    public void interactiveGradient() {
        GradientPaint paint = new GradientPaint(0f, 0f, Color.WHITE, 500f, 
                500f, Color.BLUE);
        JXTitledPanel panel = new JXTitledPanel("want a gradient");
        panel.setTitlePainter(new MattePainter(paint, true));
        JXFrame frame = wrapInFrame(panel, "gradient");
        addComponentOrientationToggle(frame);
        show(frame, 500, 500);
    }
    

    @Override
    protected void setUp() throws Exception {
        setSystemLF(true);
        // forcing load of headerAddon
        new JXHeader();
    }
    
    /**
     * Dummy empty test just to keep it from whining.
     */
    public void testDummy() {
        // do nothing
    }
    
}
