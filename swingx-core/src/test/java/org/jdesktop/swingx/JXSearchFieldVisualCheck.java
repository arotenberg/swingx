/*
 * $Id: JXButtonVisualCheck.java 4082 2011-11-15 18:39:43Z kschaefe $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx;


/**
 * Visual tests of JXSearchField issues.
 * @author Karl Schaefer
 *
 */
public class JXSearchFieldVisualCheck extends InteractiveTestCase {
    public void interactiveRenderingCheck() {
        JXSearchField search = new JXSearchField();
        
        showInFrame(search, "Painter testing");
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        JXSearchFieldVisualCheck test = new JXSearchFieldVisualCheck();
        try {
            test.runInteractiveTests("interactiveRenderingCheck");
          } catch (Exception e) {
              System.err.println("exception when executing interactive tests:");
              e.printStackTrace();
          }
    }

    /**
     * do nothing test - keep the testrunner happy.
     */
    public void testDummy() {
    }

}
