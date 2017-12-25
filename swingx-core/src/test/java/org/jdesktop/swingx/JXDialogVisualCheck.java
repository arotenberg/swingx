/*
 * $Id: JXDialogVisualCheck.java 2959 2008-06-20 11:21:29Z kleopatra $
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

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.action.AbstractActionExt;

/**
 * TODO add type doc
 * 
 * @author Jeanette Winzenburg
 */
public class JXDialogVisualCheck extends InteractiveTestCase {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(JXDialogVisualCheck.class.getName());
    
    public static void main(String[] args) {
//      setSystemLF(true);
      JXDialogVisualCheck  test = new JXDialogVisualCheck();
      try {
          test.runInteractiveTests();
      } catch (Exception e) {
          System.err.println("exception when executing interactive tests:");
          e.printStackTrace();
      }
  }
    
    /**
     * 
     * example of how to bind custom actions to a JXDialog.
     * 
     * Requirement from forum:
     * - custom execute
     * - custom close (text should be "Cancel")
     * 
     */
    public void interactiveXDialogButtons() {
        final JComponent content = new JPanel();
        content.add(new JLabel("A label:"));
        content.add(new JTextField("something to play with"));
        Action myExecuteAction = new AbstractActionExt("Execute") {

            public void actionPerformed(ActionEvent e) {
                LOG.info("got an execute");
            }
            
        };
        content.getActionMap().put(JXDialog.EXECUTE_ACTION_COMMAND, myExecuteAction);
        Action myCancelAction = new AbstractActionExt("Cancel") {

            public void actionPerformed(ActionEvent e) {
                LOG.info("got a close");
                Window window = SwingUtilities.windowForComponent(content);
                if (window instanceof JXDialog) {
                    ((JXDialog) window).doClose();
                }
            }
            
        };
        content.getActionMap().put(JXDialog.CLOSE_ACTION_COMMAND, myCancelAction);
        final JXDialog dialog = new JXDialog(content);
        dialog.pack();
        
        JComponent comp = new JXPanel();
        Action openDialog = new AbstractActionExt("open dialog") {

            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(true);
                
            }
            
        };
        JButton button = new JButton(openDialog);
        comp.add(button);
        showWithScrollingInFrame(comp, "JXDialog custom actions");
     }

    /** 
     * Do nothing, just to make the runner happy if there are no 
     * issues.
     *
     */
    public void testDummy() {
        
    }

}
