/*
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * California 95054, U.S.A. All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */
package org.jdesktop.swingx;

import java.util.logging.Logger;

/**
 * @author Karl Schaefer
 */
public class JXGraphVisualCheck extends InteractiveTestCase {
    
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(JXGraphVisualCheck.class.getName());
    
    public JXGraphVisualCheck() {
        super("JXGraph Test");
    }

    public static void main(String[] args) throws Exception {
        JXGraphVisualCheck test = new JXGraphVisualCheck();
        
        try {
            test.runInteractiveTests();
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }

    /**
     * SwingX Issue #1279: Ensure tick marks are not drawn when draw axis is off.
     */
    public void interactiveTickMarkTest() {
        final JXGraph graph = new JXGraph();
        graph.setAxisPainted(false);
        
        showInFrame(graph, "Ensure tick marks are not drawn");
    }

    /**
     * Do nothing, make the test runner happy
     * (would output a warning without a test fixture).
     *
     */
    public void testDummy() {
    }
}
