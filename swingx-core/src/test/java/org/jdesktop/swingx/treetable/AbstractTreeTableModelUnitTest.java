/*
 * $Id: AbstractTreeTableModelUnitTest.java 3473 2009-08-27 13:17:10Z kleopatra $
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
package org.jdesktop.swingx.treetable;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class AbstractTreeTableModelUnitTest extends TestCase {
    private static class DummyTreeTableModel extends AbstractTreeTableModel {
        public DummyTreeTableModel(Object root) {
            super(root);
        }
        
        //these methods are effectively no-ops
        public int getColumnCount() {
            return 0;
        }

        public Object getValueAt(Object node, int column) {
            return null;
        }

        public Object getChild(Object parent, int index) {
            return null;
        }

        public int getChildCount(Object parent) {
            return 0;
        }

        public int getIndexOfChild(Object parent, Object child) {
            return 0;
        }

        @Override
        public boolean isLeaf(Object node) {
            return false;
        }
    }
    
    private static class FailingTreeModelListener implements TreeModelListener {

        public void treeNodesChanged(TreeModelEvent e) {
            fail(e.toString());
        }

        public void treeNodesInserted(TreeModelEvent e) {
            fail(e.toString());
        }

        public void treeNodesRemoved(TreeModelEvent e) {
            fail(e.toString());
        }

        public void treeStructureChanged(TreeModelEvent e) {
            fail(e.toString());
        }
    }
    
    private DummyTreeTableModel dummyModel;
    private DummyTreeTableModel nullModel;
    
    private DefaultMutableTreeNode root;
    private DefaultMutableTreeNode child1;
    private DefaultMutableTreeNode child2;
    private DefaultMutableTreeNode grandchild1;
    private DefaultMutableTreeNode grandchild2;
    private DefaultMutableTreeNode grandchild3;
    private DefaultMutableTreeNode grandchild4;
    private DefaultMutableTreeNode grandchild5;
    private DefaultMutableTreeNode grandchild6;
    
    @Before
    public void setUpJ4() throws Exception {
        setUp();
    }
    
    @After
    public void tearDownJ4() throws Exception {
        tearDown();
    }
    
    private TreeNode createTree() {
        root = new DefaultMutableTreeNode("root");
        
        child1 = new DefaultMutableTreeNode("child1");
        grandchild1 = new DefaultMutableTreeNode("grandchild1");
        child1.add(grandchild1);
        grandchild2 = new DefaultMutableTreeNode("grandchild2");
        child1.add(grandchild2);
        grandchild3 = new DefaultMutableTreeNode("grandchild3");
        child1.add(grandchild3);
        root.add(child1);
        
        child2 = new DefaultMutableTreeNode("child2");
        grandchild4 = new DefaultMutableTreeNode("grandchild4");
        child2.add(grandchild4);
        grandchild5 = new DefaultMutableTreeNode("grandchild5");
        child2.add(grandchild5);
        grandchild6 = new DefaultMutableTreeNode("grandchild6");
        child2.add(grandchild6);
        root.add(child2);
        
        return root;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        dummyModel = new DummyTreeTableModel(createTree());
        nullModel = new DummyTreeTableModel(null);
    }
    
    @Test
    public void testModelGetColumnClass() {
        // method returns a constant, but test is useful for ensuring that the
        // method returns the same values over time.
        assertEquals(dummyModel.getColumnClass(Integer.MIN_VALUE), Object.class);
        assertEquals(dummyModel.getColumnClass(-1), Object.class);
        assertEquals(dummyModel.getColumnClass(0), Object.class);
        assertEquals(dummyModel.getColumnClass(1), Object.class);
        assertEquals(dummyModel.getColumnClass(Integer.MAX_VALUE), Object.class);
        
        //demonstrate getColumnClass unaffected by root value
        assertEquals(nullModel.getColumnClass(Integer.MIN_VALUE), Object.class);
        assertEquals(nullModel.getColumnClass(-1), Object.class);
        assertEquals(nullModel.getColumnClass(0), Object.class);
        assertEquals(nullModel.getColumnClass(1), Object.class);
        assertEquals(nullModel.getColumnClass(Integer.MAX_VALUE), Object.class);
    }
    
    @Test
    public void testModelGetColumnName() {
        assertEquals(dummyModel.getColumnName(Integer.MIN_VALUE), "");
        assertEquals(dummyModel.getColumnName(-1), "");
        assertEquals(dummyModel.getColumnName(0), "A");
        assertEquals(dummyModel.getColumnName(1), "B");
        assertEquals(dummyModel.getColumnName(26), "AA");
        assertEquals(dummyModel.getColumnName(702), "AAA");
        assertEquals(dummyModel.getColumnName(Integer.MAX_VALUE), "FXSHRXX");
        
        //demonstrate getColumnName unaffected by root value
        assertEquals(nullModel.getColumnName(Integer.MIN_VALUE), "");
        assertEquals(nullModel.getColumnName(-1), "");
        assertEquals(nullModel.getColumnName(0), "A");
        assertEquals(nullModel.getColumnName(1), "B");
        assertEquals(nullModel.getColumnName(26), "AA");
        assertEquals(nullModel.getColumnName(702), "AAA");
        assertEquals(nullModel.getColumnName(Integer.MAX_VALUE), "FXSHRXX");
    }
    
    @Test
    public void testModelGetHierarchicalColumn() {
        // method returns a constant, but test is useful for ensuring that the
        // method returns the same values over time.
        assertEquals(dummyModel.getHierarchicalColumn(), -1);
        
        //demonstrate getHierarchicalColumn unaffected by root value
        assertEquals(nullModel.getHierarchicalColumn(), -1);
    }
    
    @Test
    public void testModelGetRoot() {
        // method returns a constant, but test is useful for ensuring that the
        // method returns the same values over time.
        assertEquals(dummyModel.getRoot(), root);
        
        //demonstrate getRoot unaffected by root value
        assertNull(nullModel.getRoot());
    }
    
    @Test
    public void testModelIsCellEditable() {
        // method returns a constant, but test is useful for ensuring that the
        // method returns the same values over time.
        assertFalse(dummyModel.isCellEditable(Integer.MIN_VALUE, Integer.MIN_VALUE));
        assertFalse(dummyModel.isCellEditable(Integer.MIN_VALUE, -1));
        assertFalse(dummyModel.isCellEditable(Integer.MIN_VALUE, 0));
        assertFalse(dummyModel.isCellEditable(Integer.MIN_VALUE, 1));
        assertFalse(dummyModel.isCellEditable(Integer.MIN_VALUE, Integer.MAX_VALUE));
        assertFalse(dummyModel.isCellEditable(-1, Integer.MAX_VALUE));
        assertFalse(dummyModel.isCellEditable(-1, -1));
        assertFalse(dummyModel.isCellEditable(-1, 0));
        assertFalse(dummyModel.isCellEditable(-1, 1));
        assertFalse(dummyModel.isCellEditable(-1, Integer.MAX_VALUE));
        assertFalse(dummyModel.isCellEditable(0, Integer.MIN_VALUE));
        assertFalse(dummyModel.isCellEditable(0, -1));
        assertFalse(dummyModel.isCellEditable(0, 0));
        assertFalse(dummyModel.isCellEditable(0, 1));
        assertFalse(dummyModel.isCellEditable(0, Integer.MAX_VALUE));
        assertFalse(dummyModel.isCellEditable(1, Integer.MIN_VALUE));
        assertFalse(dummyModel.isCellEditable(1, -1));
        assertFalse(dummyModel.isCellEditable(1, 0));
        assertFalse(dummyModel.isCellEditable(1, 1));
        assertFalse(dummyModel.isCellEditable(1, Integer.MAX_VALUE));
        assertFalse(dummyModel.isCellEditable(Integer.MAX_VALUE, Integer.MIN_VALUE));
        assertFalse(dummyModel.isCellEditable(Integer.MAX_VALUE, -1));
        assertFalse(dummyModel.isCellEditable(Integer.MAX_VALUE, 0));
        assertFalse(dummyModel.isCellEditable(Integer.MAX_VALUE, 1));
        assertFalse(dummyModel.isCellEditable(Integer.MAX_VALUE, Integer.MAX_VALUE));
        
        //demonstrate isCellEditable unaffected by root value
        assertFalse(nullModel.isCellEditable(Integer.MIN_VALUE, Integer.MIN_VALUE));
        assertFalse(nullModel.isCellEditable(Integer.MIN_VALUE, -1));
        assertFalse(nullModel.isCellEditable(Integer.MIN_VALUE, 0));
        assertFalse(nullModel.isCellEditable(Integer.MIN_VALUE, 1));
        assertFalse(nullModel.isCellEditable(Integer.MIN_VALUE, Integer.MAX_VALUE));
        assertFalse(nullModel.isCellEditable(-1, Integer.MAX_VALUE));
        assertFalse(nullModel.isCellEditable(-1, -1));
        assertFalse(nullModel.isCellEditable(-1, 0));
        assertFalse(nullModel.isCellEditable(-1, 1));
        assertFalse(nullModel.isCellEditable(-1, Integer.MAX_VALUE));
        assertFalse(nullModel.isCellEditable(0, Integer.MIN_VALUE));
        assertFalse(nullModel.isCellEditable(0, -1));
        assertFalse(nullModel.isCellEditable(0, 0));
        assertFalse(nullModel.isCellEditable(0, 1));
        assertFalse(nullModel.isCellEditable(0, Integer.MAX_VALUE));
        assertFalse(nullModel.isCellEditable(1, Integer.MIN_VALUE));
        assertFalse(nullModel.isCellEditable(1, -1));
        assertFalse(nullModel.isCellEditable(1, 0));
        assertFalse(nullModel.isCellEditable(1, 1));
        assertFalse(nullModel.isCellEditable(1, Integer.MAX_VALUE));
        assertFalse(nullModel.isCellEditable(Integer.MAX_VALUE, Integer.MIN_VALUE));
        assertFalse(nullModel.isCellEditable(Integer.MAX_VALUE, -1));
        assertFalse(nullModel.isCellEditable(Integer.MAX_VALUE, 0));
        assertFalse(nullModel.isCellEditable(Integer.MAX_VALUE, 1));
        assertFalse(nullModel.isCellEditable(Integer.MAX_VALUE, Integer.MAX_VALUE));
    }
    
    //Will only fail if an exception is thrown
    @Test
    public void testModelListenerMethods() {
        FailingTreeModelListener l = new FailingTreeModelListener();
        
        dummyModel.addTreeModelListener(l);
        dummyModel.addTreeModelListener(null);
        
        nullModel.addTreeModelListener(l);
        nullModel.addTreeModelListener(null);
        
        dummyModel.removeTreeModelListener(l);
        dummyModel.removeTreeModelListener(null);
        
        nullModel.removeTreeModelListener(l);
        nullModel.removeTreeModelListener(null);
    }
    
    //TODO test fire methods
    
    //Will only fail if valueForPathChanged ever fire events
    @Test
    public void testModelValueForPathChanged() {
        FailingTreeModelListener l = new FailingTreeModelListener();
        
        dummyModel.addTreeModelListener(l);
        dummyModel.valueForPathChanged(null, null);
        dummyModel.valueForPathChanged(null, new Object());
        dummyModel.valueForPathChanged(new TreePath(new Object()), null);
        dummyModel.valueForPathChanged(new TreePath(new Object()), new Object());
        
        //demonstrate valueForPathChanged unaffected by root value
        nullModel.addTreeModelListener(l);
        nullModel.valueForPathChanged(null, null);
        nullModel.valueForPathChanged(null, new Object());
        nullModel.valueForPathChanged(new TreePath(new Object()), null);
        nullModel.valueForPathChanged(new TreePath(new Object()), new Object());
    }
}
