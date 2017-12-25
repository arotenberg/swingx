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
package org.jdesktop.swingx.plaf;

import java.util.logging.Logger;

import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import org.jdesktop.swingx.InteractiveTestCase;

public class ContextLAFCheck extends InteractiveTestCase {

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(ContextLAFCheck.class
            .getName());
    
    public static void main(String[] args) {
        ContextLAFCheck test = new ContextLAFCheck();
        UIManager.addAuxiliaryLookAndFeel(new ContextMenuAuxLF());
        try {
            test.runInteractiveTests();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public void interactiveContextMenu() {
        JTextField field = new JTextField();
        LOG.info("text ui " + field.getUI());
        LOG.info("" + UIManager.getLookAndFeel());
        LookAndFeel lf;
    }
}
