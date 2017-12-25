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
package org.jdesktop.swingx;

import java.awt.ComponentOrientation;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * Issues with core JTree.
 * 
 */
public class JTreeIssues extends InteractiveTestCase {

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(JTreeIssues.class
            .getName());
    
    public static void main(String[] args) {
        JTreeIssues test = new JTreeIssues();
        try {
            setLookAndFeel("Nimbus");
            test.runInteractiveTests();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Core Issue: Nimbus Tree Handle Icon not bidi compliant.
     * 
     * several problems in RToL
     * - the closed-icon should point to the left, that is the opposite direction or LToR
     * - handle position is incorrect, overlapping under the node content
     * 
     * Unrelated to Nimbus: editor completely wrecked (node icon jumps to the left)
     */
    public void interactiveNimbusHandleIconRToL() {
        JTree tree = new JTree();
        tree.setEditable(true);
        JXFrame frame = wrapWithScrollingInFrame(tree, "Nimbus handle");
        frame.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        addComponentOrientationToggle(frame);
        show(frame);
    }
    

    /**
     * Core Issue: editor not moved if node added above.
     * 
     * Start editing a node, press button to insert a row above
     * expected: editor sticks to node, that is moves one row down
     * actual: editor sticks to row, now pointing to incorrect node
     * 
     * Leads to data corruption as the editing value now it written to 
     * the newly inserted node.
     */
    public void interactiveEditorSticksToRowOnInsert() {
        final JTree tree = new JTree();
        tree.setEditable(true);
        JXFrame frame = wrapWithScrollingInFrame(tree, "editor not moved on insert");
        Action action = new AbstractAction("insert node") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                TreePath path = tree.getEditingPath();
                if (path == null) return;
                TreePath parent = path.getParentPath();
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)parent.getLastPathComponent();
                int idx = node.getIndex((TreeNode)path.getLastPathComponent());
                ((DefaultTreeModel)tree.getModel()).insertNodeInto(new DefaultMutableTreeNode("inserted " + idx), 
                        node, idx);

            }
        };
        addAction(frame, action);
        show(frame);
        
    }
}
