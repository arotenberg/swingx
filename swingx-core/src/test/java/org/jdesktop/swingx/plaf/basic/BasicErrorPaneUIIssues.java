/*
 * $Id$
 *
 * Copyright 2009 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx.plaf.basic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.error.ErrorInfo;

/**
 * 
 * @author Jeanette Winzenburg
 */
public class BasicErrorPaneUIIssues extends InteractiveTestCase {

    public static void main(String[] args) {
        BasicErrorPaneUIIssues test = new BasicErrorPaneUIIssues();
        try {
            test.runInteractiveTests();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * first frame - 
     */
    public void interactiveDummy() {
        showWithScrollingInFrame(new JXTable(20, 6), "just a dummy - #1212 is virulent only if not app exited");
    }
    /**
     * Issue #1212-swingx: NullPointerException in BasicErrorPaneUI.initWindow listener
     */
    public void interactiveNPEAfterChangeLAF() {
        JComponent pane = new JPanel();
        JButton showErrorPane = new JButton("show error pane");
        pane.add(showErrorPane);
        final JXFrame frame = showInFrame(showErrorPane, "NPE on closing");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        showErrorPane.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JXErrorPane.showDialog(frame, new ErrorInfo("Title",
                        "Message", "Details", null, null, null, null));
            }
        });
        addStatusMessage(frame, "1. show error, 2. change laf, 3. close frame - NPE");
        show(frame);
    }
}
