/*
 * $Id: JXErrorPaneIssues.java 4170 2012-02-21 14:27:15Z kleopatra $
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

import org.jdesktop.swingx.error.ErrorInfo;


/**
 * Test to expose known issues around <code>JXError*</code>.
 * 
 * Ideally, there would be at least one failing test method per open
 * Issue in the issue tracker. Plus additional failing test methods for
 * not fully specified or not yet decided upon features/behaviour.
 * 
 * 
 * @author Jeanette Winzenburg
 */
public class JXErrorPaneIssues extends InteractiveTestCase {
    public static void main(String[] args) {
//      setSystemLF(true);
      JXErrorPaneIssues test = new JXErrorPaneIssues();
      try {
        test.runInteractiveTests();
      } catch (Exception e) {
          System.err.println("exception when executing interactive tests:");
          e.printStackTrace();
      }

    }
    
    /**
     * Issue #1486-swingx: errorInfo/pane doesn't substitute system properties
     * (as documented)
     * 
     * Fixed by removing the doc example.
     * 
     */
    public void interactiveSubstitute() {
        String message = "An error occured on ${os.name} version ${os.version}";
        //auto creates the state map, populated with all System properties.
        //Sandboxed apps can't read System properties.
        ErrorInfo info = new ErrorInfo("Error", message, message, null, new NullPointerException(), null, null);
        JXErrorPane.showDialog(null, info);
    }

    /**
     * 
     * reported in forum - details not visible, details close on focus change.
     * Worksforme on XP, maybe OS dependent?
     *
     */
    public void interactiveNoDetails() {
        JXErrorPane.showDialog(new NullPointerException("something to show"));
    }

    /**
     * do-nothing method - suppress warning if there are no other
     * test fixtures to run.
     *
     */
    public void testDummy() {
    }

}
