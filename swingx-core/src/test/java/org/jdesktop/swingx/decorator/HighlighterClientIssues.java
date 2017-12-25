/*
 * $Id: HighlighterClientIssues.java 3020 2008-08-02 13:41:02Z kleopatra $
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
package org.jdesktop.swingx.decorator;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXTable;

/**
 * Test to exposed known issues of <code>Highlighter</code> client
 * api in collection components.
 * 
 * Ideally, there would be at least one failing test method per open
 * Issue in the issue tracker. Plus additional failing test methods for
 * not fully specified or not yet decided upon features/behaviour.
 * 
 * @author Jeanette Winzenburg
 */
public class HighlighterClientIssues extends InteractiveTestCase {
    public static void main(String[] args) throws Exception {
//      setSystemLF(true);
      HighlighterClientIssues test = new HighlighterClientIssues();
      try {
          test.runInteractiveTests();
//          test.runInteractiveTests("interactive.*Table.*");
//          test.runInteractiveTests("interactive.*List.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        } 
  }

    
    /**
     * Highlighters in JXTable must be kept on moving 
     * the table to different container. This is a sanity test -
     * failed in early stages of fixing #519-swing: memory leak
     * with shared highlighters.
     *
     */
    public void interactiveMemoryLeak() {
        final ColorHighlighter shared = new ColorHighlighter(Color.RED, null);
        JXTable first = new JXTable(10, 3);
        first.addHighlighter(shared);
        final JXTable second = new JXTable(10, 2);
        second.setName("second");
        second.addHighlighter(shared);
        JXFrame firstFrame = wrapWithScrollingInFrame(first, "control");
        
        final JXFrame secondFrame = wrapWithScrollingInFrame(second, "dependent, don't close directly");
        Action close = new AbstractAction("close second") {

            public void actionPerformed(ActionEvent e) {
                secondFrame.dispose();
                setEnabled(false);
            }
            
        };
        Action open = new AbstractAction("open second") {

            public void actionPerformed(ActionEvent e) {
                JXFrame newFrame = wrapWithScrollingInFrame(second, "newly created");
                newFrame.setVisible(true);
            }
            
        };
        Action color = new AbstractAction("toggle color") {

            public void actionPerformed(ActionEvent e) {
                shared.setBackground(Color.YELLOW);
                
            }
            
        };
        addAction(firstFrame, close);
        addAction(firstFrame, open);
        addAction(firstFrame, color);
        firstFrame.setVisible(true);
        secondFrame.setVisible(true);
    }

    /**
     * Dummy to keep the test runner happy.
     */
    public void testDummy() {
        
    }
}
