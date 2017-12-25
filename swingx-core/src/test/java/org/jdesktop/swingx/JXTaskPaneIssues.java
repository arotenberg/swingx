/*
 * $Id: JXTaskPaneIssues.java 3650 2010-04-08 15:16:11Z kleopatra $
 *
 * Copyright 2007 Sun Microsystems, Inc., 4150 Network Circle,
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


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.hyperlink.AbstractHyperlinkAction;

/**
 * 
 * @author Jeanette Winzenburg
 */
public class JXTaskPaneIssues extends InteractiveTestCase {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(JXTaskPaneIssues.class
            .getName());
    
    public static void main(String[] args) {
        JXTaskPaneIssues test = new JXTaskPaneIssues();
        try {
            test.runInteractiveTests();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Issue #1190-swingx: hyperlink looses visited visuals in taskpane.
     *  
     */
    public void interactiveVisitedPropertyOnLAFChange() {
        Action task = new AbstractHyperlinkAction<Object>("AddedAsAction: click - visited doesn't survive LAF") {

            @Override
            public void actionPerformed(ActionEvent e) {
                setVisited(true);
            }
        };  
        Action hyperlink = new AbstractHyperlinkAction<Object>("AddedAsHyperlink: click - visited  survives LAF") {

            @Override
            public void actionPerformed(ActionEvent e) {
                setVisited(true);
            }
        };  
        JXTaskPane pane = new JXTaskPane();
        pane.add(task);
        pane.add(new JXHyperlink(hyperlink));
        showInFrame(pane, "visit and laf-change");
    }

    /**
     * Quick check to see if hidden comps receive a keybinding (no).
     */
    public void interactiveDialogWithHidden() {
        JXPanel pane = new JXPanel(new BorderLayout());
        Action action = new AbstractActionExt("something to click - F3 to show log message") {

            public void actionPerformed(ActionEvent e) {
                LOG.info("got me");
            }
            
        };
        final JButton hidable = new JButton(action);
        Object actionKey = "dummy";
        hidable.getActionMap().put(actionKey, action);
        KeyStroke keyStroke = KeyStroke.getKeyStroke("F3");
        hidable.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, actionKey);
        
        Action hide = new AbstractActionExt("hide other, then press F3 - no log message") {

            public void actionPerformed(ActionEvent e) {
                hidable.setVisible(!hidable.isVisible());
            }
            
        };
        pane.add(hidable);
        pane.add(new JButton(hide), BorderLayout.SOUTH);
        final JXDialog dialog = new JXDialog(pane);
        dialog.setTitle("No keybinding on invisible component");
        dialog.pack();
        dialog.setVisible(true);
    }


    /**
     * Empty test method to keep the test runner happy if we have no 
     * open issues.
     */
    public void testDummy() {
        
    }
}
