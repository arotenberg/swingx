/*
 * $Id: MattePainterVisualCheck.java 3801 2010-10-01 15:33:45Z kschaefe $
 *
 * Copyright 2010 Sun Microsystems, Inc., 4150 Network Circle,
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
 */
package org.jdesktop.swingx.painter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXPanel;

/**
 *
 * @author kschaefer
 */
public class MattePainterVisualCheck extends InteractiveTestCase {
    /**
     * Do nothing, make the test runner happy
     * (would output a warning without a test fixture).
     *
     */
    public void testDummy() {
        
    }

    public static void main(String[] args) throws Exception {
         MattePainterVisualCheck test = new MattePainterVisualCheck();
         
         try {
             test.runInteractiveTests();
         } catch (Exception e) {
             System.err.println("exception when executing interactive tests:");
             e.printStackTrace();
         }
     }
    
    public void interactiveCyclicGradientCheck() {
        JXPanel panel = new JXPanel();
        panel.setPreferredSize(new Dimension(200, 200));
        GradientPaint paint = new GradientPaint(0f, 0f, Color.RED, .1f, .1f, Color.BLACK, true);
        panel.setBackgroundPainter(new MattePainter(paint, true));
        
        showInFrame(panel, "Cyclic Gradient Check");
    }
}
