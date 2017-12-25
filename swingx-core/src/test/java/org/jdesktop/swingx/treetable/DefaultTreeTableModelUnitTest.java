package org.jdesktop.swingx.treetable;

import java.util.Vector;

import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 *
 */
@RunWith(JUnit4.class)
public class DefaultTreeTableModelUnitTest extends TestCase {
    private DefaultTreeTableModel model;
    private DefaultMutableTreeTableNode root;
    private DefaultMutableTreeTableNode child1;
    private DefaultMutableTreeTableNode child2;
    private DefaultMutableTreeTableNode grandchild1;
    private DefaultMutableTreeTableNode grandchild2;
    private DefaultMutableTreeTableNode grandchild3;
    private DefaultMutableTreeTableNode grandchild4;
    private DefaultMutableTreeTableNode grandchild5;
    private AbstractMutableTreeTableNode grandchild6;
    
    @Before
    public void setUpJ4() throws Exception {
        setUp();
    }
    
    @After
    public void tearDownJ4() throws Exception {
        tearDown();
    }
    
    private TreeTableNode createTree() {
        root = new DefaultMutableTreeTableNode("root");
        
        child1 = new DefaultMutableTreeTableNode("child1");
        grandchild1 = new DefaultMutableTreeTableNode("grandchild1");
        child1.add(grandchild1);
        grandchild2 = new DefaultMutableTreeTableNode("grandchild2");
        child1.add(grandchild2);
        grandchild3 = new DefaultMutableTreeTableNode("grandchild3");
        child1.add(grandchild3);
        root.add(child1);
        
        child2 = new DefaultMutableTreeTableNode("child2");
        grandchild4 = new DefaultMutableTreeTableNode("grandchild4");
        child2.add(grandchild4);
        grandchild5 = new DefaultMutableTreeTableNode("grandchild5");
        child2.add(grandchild5);
        grandchild6 = new AbstractMutableTreeTableNode("grandchild6") {
            public int getColumnCount() {
                return 0;
            }

            public Object getValueAt(int column) {
                return getUserObject();
            }
        };
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
        
        Vector<String> names = new Vector<String>();
        names.add("A");
        
        model = new DefaultTreeTableModel(createTree(), names);
    }
    
    @Test
    public void testGetPathToRoot() {
        TreeNode[] testGroup1 = model.getPathToRoot(grandchild3);
        
        assertEquals(testGroup1[0], root);
        assertEquals(testGroup1[1], child1);
        assertEquals(testGroup1[2], grandchild3);
        
        TreeNode[] testGroup2 = model.getPathToRoot(child2);
        
        assertEquals(testGroup2[0], root);
        assertEquals(testGroup2[1], child2);
        
        TreeNode[] testGroup3 = model.getPathToRoot(root);
        
        assertEquals(testGroup3[0], root);
        
        try {
            model.getPathToRoot(null);
            fail("expected NullPointerException");
        } catch (NullPointerException e) {
            //success
        }
        
        try {
            model.getPathToRoot(new DefaultMutableTreeTableNode("failure"));
            fail("expected NullPointerException");
        } catch (NullPointerException e) {
            //success
        }
    }
    
    @Test
    public void testGetValueAt() {
        //Test expected cases
        assertEquals(model.getValueAt(root, 0), "root");
        
        //Outside TTN count
        assertNull(model.getValueAt(grandchild6, 0));
        
        //Test boundary cases
        try {
            model.getValueAt(child1, model.getColumnCount());
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            //success
        }
        
        try {
            model.getValueAt(grandchild4, -1);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            //success
        }
        
        //Test exceptional cases
        try {
            model.getValueAt(null, 0);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            //success
        }
        
        try {
            model.getValueAt(new Object(), 0);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            //success
        }
    }
    
    @Test
    public void testSetValueAt() {
        //Test expected cases
        model.setValueAt("new", root, 0);
        assertEquals(model.getValueAt(root, 0), "new");
        
        //Test boundary cases
        try {
            model.setValueAt("new", child1, model.getColumnCount());
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            //success
        }
        
        try {
            model.setValueAt("new", grandchild4, -1);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            //success
        }
        
        //Test exceptional cases
        try {
            model.setValueAt("new", null, 0);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            //success
        }
        
        try {
            model.setValueAt("new", new Object(), 0);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            //success
        }
    }
    
    public void testSetValueAtIssues() {
        //TODO what to do in this case?  exception? nothing?
        //Outside TTN count
        model.setValueAt("new", grandchild6, 0);
    }
    
    @Test
    public void testGetChild() {
        assertEquals(model.getChild(root, 0), child1);
        
        //Test boundary cases
        //TODO untestable: outcome depends on how node handles OOB
        //should TTN document an IOOBException?
        
        //Test exceptional cases
        try {
            model.getChild(null, 0);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            //success
        }
        
        try {
            model.getChild(new Object(), 0);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            //success
        }
    }
    
    @Test
    public void testGetChildCount() {
        assertEquals(model.getChildCount(root), 2);
        
        assertEquals(model.getChildCount(child1), 3);
        
        assertEquals(model.getChildCount(grandchild4), 0);
        
        //Test exceptional cases
        try {
            model.getChildCount(null);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            //success
        }
        
        try {
            model.getChildCount(new Object());
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            //success
        }
    }
    
    /**
     * Testing that indexOfChild returns -1 for uncontained parent/child nodes.
     */
    @Test
    public void testGetIndexOfChild() {
        assertEquals(model.getIndexOfChild(root,child1), 0);
        
        assertEquals(model.getIndexOfChild(root,child2), 1);
        
        assertEquals(-1, model.getIndexOfChild(null, child1));
        assertEquals(-1, model.getIndexOfChild(new Object(), child1));
        assertEquals(-1, model.getIndexOfChild(child1, null));
        assertEquals(-1, model.getIndexOfChild(child1, new Object()));

//        //Test exceptional cases
//        try {
//            model.getIndexOfChild(null, child1);
//            fail("expected IllegalArgumentException");
//        } catch (IllegalArgumentException e) {
//            //success
//        }
//        
//        try {
//            model.getIndexOfChild(new Object(), child1);
//            fail("expected IllegalArgumentException");
//        } catch (IllegalArgumentException e) {
//            //success
//        }
//        try {
//            model.getIndexOfChild(child1, null);
//            fail("expected IllegalArgumentException");
//        } catch (IllegalArgumentException e) {
//            //success
//        }
//        
//        try {
//            model.getIndexOfChild(child1, new Object());
//            fail("expected IllegalArgumentException");
//        } catch (IllegalArgumentException e) {
//            //success
//        }
    }
    
    @Test
    public void testIsCellEditable() {
        //Test expected cases
//        assertFalse(model.isLeaf(root));
//        assertFalse(model.isLeaf(child2));
//        assertTrue(model.isLeaf(grandchild3));
        
        //Test boundary cases
        try {
            model.isCellEditable(root, model.getColumnCount());
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            //success
        }
        
        try {
            model.isCellEditable(root, -1);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            //success
        }
        
        //Test exceptional cases
        try {
            model.isCellEditable(null, 0);
            
            fail("IllegalArgumentException is not thrown.");
        } catch (IllegalArgumentException e) {
            //test succeeded
        }
        
        try {
            model.isCellEditable(new Object(), 0);
            
            fail("IllegalArgumentException is not thrown.");
        } catch (IllegalArgumentException e) {
            //test succeeded
        }
    }
    
    @Test
    public void testModelIsLeaf() {
        //Test expected cases
        assertFalse(model.isLeaf(root));
        assertFalse(model.isLeaf(child2));
        assertTrue(model.isLeaf(grandchild3));
        
        //Test exceptional cases
        try {
            model.isLeaf(null);
            
            fail("IllegalArgumentException is not thrown.");
        } catch (IllegalArgumentException e) {
            //test succeeded
        }
        
        try {
            model.isLeaf(new Object());
            
            fail("IllegalArgumentException is not thrown.");
        } catch (IllegalArgumentException e) {
            //test succeeded
        }
    }
    
    @Test
    public void testRemoveFromParent() {
    	try {
    		model.removeNodeFromParent(new DefaultMutableTreeTableNode());
    		fail("Expected IllegalArgumentException");
    	} catch (IllegalArgumentException e) {
    		//success
    	}
    	
    	try {
    		DefaultMutableTreeTableNode p = new DefaultMutableTreeTableNode();
    		DefaultMutableTreeTableNode c = new DefaultMutableTreeTableNode();
    		c.setParent(p);
    		
    		model.removeNodeFromParent(c);
    		
    		fail("Expected NullPointerException");
    	} catch (NullPointerException e) {
    		//success
    		//TODO does not seem like the correct exception
    	}
    	
    	TreeNode parent = grandchild6.getParent();
    	int count = parent.getChildCount();
    	model.removeNodeFromParent(grandchild6);
    	
    	assertNull(grandchild6.getParent());
    	assertEquals(parent.getChildCount(), count - 1);
    	
    	model.removeNodeFromParent(child1);
    	assertNull(child1.getParent());
    	
    	try {
    		model.removeNodeFromParent(root);
    		fail("Expected IllegalArgumentException");
    	} catch (IllegalArgumentException e) {
    		//success
    	}
    	
    	//TODO test removing already removed nodes?
    }
    
    @Test
    public void testSetRoot() {
    	assertEquals(model.getRoot(), root);
    	
    	DefaultMutableTreeTableNode newRoot = new DefaultMutableTreeTableNode("a new root");
    	model.setRoot(newRoot);
    	
    	assertEquals(model.getRoot(), newRoot);
    }
    
    //TODO test "fire" methods and reloads
    
    @Test
    public void testValueForPathChanged() {
        //Test expected cases
        model.valueForPathChanged(new TreePath(root), "a new root");
        assertEquals(root.getUserObject(), "a new root");
        
        //Test exceptional cases
        try {
            model.valueForPathChanged(null, new Object());
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            //success
        }
        
        try {
            model.valueForPathChanged(new TreePath(new Object()), new Object());
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            //success
        }
    }
    
    @Test
    public void testSetUserObject() {
        //Test expected cases
        model.setUserObject(grandchild2, "a new value");
        assertEquals(grandchild2.getUserObject(), "a new value");
        
        //Test exceptional cases
        try {
            model.setUserObject(null, new Object());
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            //success
        }
        
        //TODO fix model to return correct exception
//        try {
//            model.setUserObject(new DefaultMutableTreeTableNode("unmanaged"), new Object());
//            fail("Expected IllegalArgumentException");
//        } catch (IllegalArgumentException e) {
//            //success
//        }
    }
}
