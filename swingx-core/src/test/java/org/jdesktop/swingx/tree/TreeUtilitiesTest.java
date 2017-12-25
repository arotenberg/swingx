/*
 * Created on 17.02.2012
 *
 */
package org.jdesktop.swingx.tree;

import static org.jdesktop.swingx.tree.TreeUtilities.EMPTY_ENUMERATION;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.logging.Logger;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.tree.TreeUtilities.BreadthFirstModelEnumeration;
import org.jdesktop.swingx.tree.TreeUtilities.BreadthFirstNodeEnumeration;
import org.jdesktop.swingx.tree.TreeUtilities.PostorderModelEnumeration;
import org.jdesktop.swingx.tree.TreeUtilities.PostorderNodeEnumeration;
import org.jdesktop.swingx.tree.TreeUtilities.PreorderModelEnumeration;
import org.jdesktop.swingx.tree.TreeUtilities.PreorderNodeEnumeration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Test of TreeUtilities.
 * 
 * @author Jeanette Winzenburg, Berlin
 */
@RunWith(JUnit4.class)
public class TreeUtilitiesTest extends InteractiveTestCase {

// traversal tests
// all tests are against the corresponding traversals in DefaultMutableTreeNode
// --> implicit assumption is that core has it correct    
    
    private TreeModel model;
    private DefaultMutableTreeNode root;
    @Test
    public void testBreadthFirstNode() {
        Enumeration<?> coreEnum = root.breadthFirstEnumeration();
        Enumeration<?> xEnum = new BreadthFirstNodeEnumeration<DefaultMutableTreeNode>(root);
        assertSameEnumeration("BreadthFirstNode", coreEnum, xEnum);
    }
    
    @Test
    public void testPostorderNode() {
        Enumeration<?> coreEnum = root.postorderEnumeration();
        Enumeration<?> xEnum = new PostorderNodeEnumeration<DefaultMutableTreeNode>(root);
        assertSameEnumeration("PostOrderNode", coreEnum, xEnum);
    }
    
    @Test
    public void testPreorderNode() {
        Enumeration<?> coreEnum = root.preorderEnumeration();
        Enumeration<?> xEnum = new PreorderNodeEnumeration<TreeNode>(root);
        assertSameEnumeration("PreorderModel", coreEnum, xEnum);
    }

    
    @Test
    public void testPostorderModel() {
        Enumeration<?> coreEnum = root.postorderEnumeration();
        Enumeration<?> xEnum = new PostorderModelEnumeration(model,root);
        assertSameEnumeration("PostOrderModel", coreEnum, xEnum);
    }
    
    @Test
    public void testPostorderModelConstructor() {
        Enumeration<?> coreEnum = root.postorderEnumeration();
        Enumeration<?> xEnum = new PostorderModelEnumeration(model);
        assertSameEnumeration("PostOrderModel", coreEnum, xEnum);
    }
    
    @Test
    public void testBreadthFirstModel() {
        Enumeration<?> coreEnum = root.breadthFirstEnumeration();
        Enumeration<?> xEnum = new BreadthFirstModelEnumeration(model);
        assertSameEnumeration("BreadthFirstModel", coreEnum, xEnum);
    }
    
    @Test
    public void testPreorderModel() {
        Enumeration<?> coreEnum = root.preorderEnumeration();
        Enumeration<?> xEnum = new PreorderModelEnumeration(model);
        assertSameEnumeration("PreorderModel", coreEnum, xEnum);
    }
    
    @Test
    public void testPreorderModelWithRoot() {
        Enumeration<?> coreEnum = root.preorderEnumeration();
        Enumeration<?> xEnum = new PreorderModelEnumeration(model, root);
        assertSameEnumeration("PreorderModel", coreEnum, xEnum);
    }
    
    @Test
    public void testPreorderModelWithRootPath() {
        Enumeration<?> coreEnum = root.preorderEnumeration();
        Enumeration<?> xEnum = new PreorderModelEnumeration(model, new TreePath(root));
        assertSameEnumeration("PreorderModel", coreEnum, xEnum);
    }
    
    
    @Test
    public void testPreorderModelWithPath() {
        TreePath path = new TreePath(root);
        // add first child and first grandChild
        path = path.pathByAddingChild(root.getChildAt(0));
        path = path.pathByAddingChild(root.getChildAt(0).getChildAt(0));
        Enumeration<?> coreEnum = root.preorderEnumeration();
        // build a starting tree path 
        for (int i = 0; i < 2; i++) {
            // move coreEnum so that next == path.lastPathComponent
            coreEnum.nextElement();
        }
        Enumeration<?> xEnum = new PreorderModelEnumeration(model, path);
        assertSameEnumeration("PreorderModel", coreEnum, xEnum);
    }
    
    @Test
    public void testPreorderModelSubtree() {
        Enumeration<?> coreEnum = ((DefaultMutableTreeNode) root.getChildAt(0)).preorderEnumeration();
        Enumeration<?> xEnum = new PreorderModelEnumeration(model, root.getChildAt(0));
        assertSameEnumeration("PreorderModel", coreEnum, xEnum);
    }
    
    /**
     * Asserts that xEnum returns the same elements as the coreEnum.
     *  
     * @param message identifier used in failure output
     * @param coreEnum an enum we think is correct
     * @param xEnum an enum to test for correctness
     */
    private void assertSameEnumeration(String message, Enumeration<?> coreEnum,
            Enumeration<?> xEnum) {
        while(coreEnum.hasMoreElements()) {
            assertTrue(message + " must have more elements", xEnum.hasMoreElements());
            assertSame(message, coreEnum.nextElement(), xEnum.nextElement());
        }
        assertFalse(message + " must not have more elements", xEnum.hasMoreElements());
    }

    @Test(expected = NoSuchElementException.class)
    public void testEmptyEnumeration() {
        assertFalse(EMPTY_ENUMERATION.hasMoreElements());
        EMPTY_ENUMERATION.nextElement();
    }

//------------------
    
    
    @Override
    @Before
    public void setUp() throws Exception {
        JXTree tree = new JXTree();
        model = tree.getModel();
        root = (DefaultMutableTreeNode) model.getRoot();
    }
    
    
//----------------- random generation of trees consisting of DefaultMutableTreeNodes
    
    private static Random random = new Random();
    /**
     * Creates a random tree of nodes of type MutableTreeNode 
     * (actually: DefaultMutableTreeNode). Copied from
     * test.aephyr.swing.TreeSort
     * <p>
     * 
     * PENDING JW: add option to initialize with the same random sequence.
     * 
     * @param text the text of the root
     * @param depth the depth of the tree
     * @return a tree of random nodes
     */
    public static MutableTreeNode createTreeNode(String text, int depth) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(text);
        if (--depth >= 0)
            for (int i = bellCurve(); --i >= 0;)
                node.add(createTreeNode(createCellValue(), depth));
        return node;
    }
    
    private static int bellCurve() {
        int i = Integer.bitCount(random.nextInt());
        if (i < 13)
                return i;
        return i-12;
    }
    private static String createCellValue() {
        char[] c = new char[bellCurve() + 2];
        for (int i = c.length; --i >= 0;)
            c[i] = (char) (random.nextInt(26) + 'a');
        c[0] = Character.toUpperCase(c[0]);
        return new String(c);
    }


    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(TreeUtilitiesTest.class
            .getName());
}
