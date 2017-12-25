/**
 * 
 */
package org.jdesktop.swingx.treetable;

import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 *
 */
@RunWith(JUnit4.class)
public class DefaultMutableTreeTableNodeUnitTest extends TestCase {
    @Test
     public void testAdd() {
         DefaultMutableTreeTableNode root = new DefaultMutableTreeTableNode("root");
         
         assertEquals(root.getChildCount(), 0);
         
         root.add(new DefaultMutableTreeTableNode("a"));
         
         assertEquals(root.getChildCount(), 1);
         
         root.add(new DefaultMutableTreeTableNode("a"));
         
         assertEquals(root.getChildCount(), 2);
     }
     
    @Test
     public void testAddDoesNotDuplicate() {
         DefaultMutableTreeTableNode root = new DefaultMutableTreeTableNode("root");
         
         assertEquals(root.getChildCount(), 0);
         
         DefaultMutableTreeTableNode a = new DefaultMutableTreeTableNode("a");
         root.add(a);
         
         assertEquals(root.getChildCount(), 1);
         
         root.add(a);
         
         assertEquals(root.getChildCount(), 1);
     }
     
    @Test
     public void testChildren() {
         DefaultMutableTreeTableNode root = new DefaultMutableTreeTableNode("root");
         
         Enumeration<? extends MutableTreeTableNode> children = root.children();
         
         while (children.hasMoreElements()) {
             fail("This enumaration should have no elements");
         }
         
         DefaultMutableTreeTableNode a = new DefaultMutableTreeTableNode("a");
         DefaultMutableTreeTableNode b = new DefaultMutableTreeTableNode("b");
         DefaultMutableTreeTableNode c = new DefaultMutableTreeTableNode("c");
         DefaultMutableTreeTableNode d = new DefaultMutableTreeTableNode("d");
         
         root.add(a);
         root.add(b);
         root.add(c);
         root.add(d);
         
         children = root.children();
         
         assertEquals(children.nextElement(), a);
         assertEquals(children.nextElement(), b);
         assertEquals(children.nextElement(), c);
         assertEquals(children.nextElement(), d);
         assertFalse(children.hasMoreElements());
     }
     
    @Test
     public void testAllowsChildren() {
         DefaultMutableTreeTableNode empty = new DefaultMutableTreeTableNode();
         assertTrue(empty.getAllowsChildren());
         empty.setAllowsChildren(!empty.getAllowsChildren());
         assertFalse(empty.getAllowsChildren());
         empty.setAllowsChildren(!empty.getAllowsChildren());
         assertTrue(empty.getAllowsChildren());
         
         DefaultMutableTreeTableNode root = new DefaultMutableTreeTableNode("root");
         assertTrue(root.getAllowsChildren());
         root.setAllowsChildren(!root.getAllowsChildren());
         assertFalse(root.getAllowsChildren());
         root.setAllowsChildren(!root.getAllowsChildren());
         assertTrue(root.getAllowsChildren());
         
         DefaultMutableTreeTableNode a = new DefaultMutableTreeTableNode("a", true);
         assertTrue(a.getAllowsChildren());
         a.setAllowsChildren(!a.getAllowsChildren());
         assertFalse(a.getAllowsChildren());
         a.setAllowsChildren(!a.getAllowsChildren());
         assertTrue(a.getAllowsChildren());
         
         DefaultMutableTreeTableNode b = new DefaultMutableTreeTableNode("b", false);
         assertFalse(b.getAllowsChildren());
         b.setAllowsChildren(!b.getAllowsChildren());
         assertTrue(b.getAllowsChildren());
         b.setAllowsChildren(!b.getAllowsChildren());
         assertFalse(b.getAllowsChildren());
     }
     
    @Test
     public void testGetChildAt() {
         DefaultMutableTreeTableNode root = new DefaultMutableTreeTableNode("root");
         
         try {
             root.getChildAt(-1);
             fail("expected IndexOutOfBoundsException");
         } catch (IndexOutOfBoundsException e) {
             //do nothing
         }
         
         try {
             root.getChildAt(root.getChildCount());
             fail("expected IndexOutOfBoundsException");
         } catch (IndexOutOfBoundsException e) {
             //do nothing
         }
         
         DefaultMutableTreeTableNode a = new DefaultMutableTreeTableNode("a");
         DefaultMutableTreeTableNode b = new DefaultMutableTreeTableNode("b");
         DefaultMutableTreeTableNode c = new DefaultMutableTreeTableNode("c");
         DefaultMutableTreeTableNode d = new DefaultMutableTreeTableNode("d");
         
         root.add(a);
         root.add(b);
         root.add(c);
         root.add(d);

         assertEquals(root.getChildAt(0), a);
         assertEquals(root.getChildAt(1), b);
         assertEquals(root.getChildAt(2), c);
         assertEquals(root.getChildAt(3), d);
         
         try {
             root.getChildAt(root.getChildCount());
             fail("expected IndexOutOfBoundsException");
         } catch (IndexOutOfBoundsException e) {
             //do nothing
         }
     }
     
    @Test
     public void testGetChildCount() {
         DefaultMutableTreeTableNode root = new DefaultMutableTreeTableNode("root");
         
         assertEquals(root.getChildCount(), 0);
         
         DefaultMutableTreeTableNode a = new DefaultMutableTreeTableNode("a");
         root.add(a);
         assertEquals(root.getChildCount(), 1);
         
         DefaultMutableTreeTableNode b = new DefaultMutableTreeTableNode("b");
         root.add(b);
         assertEquals(root.getChildCount(), 2);
         
         DefaultMutableTreeTableNode c = new DefaultMutableTreeTableNode("c");
         root.add(c);
         assertEquals(root.getChildCount(), 3);
         
         DefaultMutableTreeTableNode d = new DefaultMutableTreeTableNode("d");
         root.add(d);
         assertEquals(root.getChildCount(), 4);

         root.remove(a);
         assertEquals(root.getChildCount(), 3);
         
         root.remove(b);
         assertEquals(root.getChildCount(), 2);
         
         root.remove(c);
         assertEquals(root.getChildCount(), 1);
         
         root.remove(d);
         assertEquals(root.getChildCount(), 0);
     }
     
    @Test
     public void testGetIndex() {
         DefaultMutableTreeTableNode root = new DefaultMutableTreeTableNode("root");
         
         assertEquals(root.getIndex(null), -1);
         
         assertEquals(root.getIndex(new DefaultMutableTreeNode()), -1);
         
         assertEquals(root.getIndex(new DefaultMutableTreeTableNode()), -1);
         
         DefaultMutableTreeTableNode a = new DefaultMutableTreeTableNode("a");
         root.add(a);
         assertEquals(root.getIndex(a), 0);
         
         DefaultMutableTreeTableNode b = new DefaultMutableTreeTableNode("b");
         root.add(b);
         assertEquals(root.getIndex(b), 1);
         
         DefaultMutableTreeTableNode c = new DefaultMutableTreeTableNode("c");
         root.add(c);
         assertEquals(root.getIndex(c), 2);
         
         DefaultMutableTreeTableNode d = new DefaultMutableTreeTableNode("d");
         root.add(d);
         assertEquals(root.getIndex(d), 3);
     }
     
    @Test
     public void testGetParent() {
         DefaultMutableTreeTableNode root = new DefaultMutableTreeTableNode("root");

         assertNull(root.getParent());
         
         DefaultMutableTreeTableNode a = new DefaultMutableTreeTableNode("a");
         a.setParent(root);
         
         assertEquals(a.getParent(), root);
         assertEquals(root.getChildAt(0), a);
         
         DefaultMutableTreeTableNode b = new DefaultMutableTreeTableNode("b");
         b.setParent(root);
         
         assertEquals(b.getParent(), root);
         assertEquals(root.getChildAt(1), b);
         
         DefaultMutableTreeTableNode c = new DefaultMutableTreeTableNode("c");
         c.setParent(root);
         
         assertEquals(c.getParent(), root);
         assertEquals(root.getChildAt(2), c);
         
         DefaultMutableTreeTableNode d = new DefaultMutableTreeTableNode("d");
         d.setParent(root);
         
         assertEquals(d.getParent(), root);
         assertEquals(root.getChildAt(3), d);
         
         a.setParent(d);
         
         assertEquals(root.getChildCount(), 3);
         assertEquals(a.getParent(), d);
         assertEquals(d.getChildAt(0), a);
         
         b.setAllowsChildren(false);
         
         try {
             c.setParent(b);
             fail("expected IllegalArgumentException");
         } catch (IllegalArgumentException e) {
             //do nothing
         }
         
         assertEquals(c.getParent(), root);
         assertEquals(b.getChildCount(), 0);
     }
     
    @Test
     public void testSetParent() {
         DefaultMutableTreeTableNode root = new DefaultMutableTreeTableNode("root");
         DefaultMutableTreeTableNode a = new DefaultMutableTreeTableNode("a");
         root.add(a);
         assertEquals(root.getChildCount(), 1);
         
         a.setParent(null);
         assertEquals(root.getChildCount(), 0);
         
         a.setParent(root);
         assertEquals(root.getChildCount(), 1);
         
         a.setParent(null);
         assertEquals(root.getChildCount(), 0);
         
         root.setAllowsChildren(false);
         try {
             a.setParent(root);
             fail("should throw IllegalArgumentException");
         } catch (IllegalArgumentException e) {
             //success
         }
     }
     
    @Test
     public void testUserObject() {
         DefaultMutableTreeTableNode empty = new DefaultMutableTreeTableNode();
         assertNull(empty.getUserObject());
         empty.setUserObject("root");
         assertEquals(empty.getUserObject(), "root");
         
         DefaultMutableTreeTableNode root = new DefaultMutableTreeTableNode("root");
         assertEquals(root.getUserObject(), "root");
//         root.setAllowsChildren(!root.getAllowsChildren());
//         assertFalse(root.getAllowsChildren());
//         root.setAllowsChildren(!root.getAllowsChildren());
//         assertTrue(root.getAllowsChildren());
//         
//         DefaultMutableTreeTableNode a = new DefaultMutableTreeTableNode("a", true);
//         assertTrue(a.getAllowsChildren());
//         a.setAllowsChildren(!a.getAllowsChildren());
//         assertFalse(a.getAllowsChildren());
//         a.setAllowsChildren(!a.getAllowsChildren());
//         assertTrue(a.getAllowsChildren());
//         
//         DefaultMutableTreeTableNode b = new DefaultMutableTreeTableNode("b", false);
//         assertFalse(b.getAllowsChildren());
//         b.setAllowsChildren(!b.getAllowsChildren());
//         assertTrue(b.getAllowsChildren());
//         b.setAllowsChildren(!b.getAllowsChildren());
//         assertFalse(b.getAllowsChildren());
     }
}
