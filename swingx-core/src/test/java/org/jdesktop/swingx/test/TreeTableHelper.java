/*
 * Created on 25.08.2011
 *
 */
package org.jdesktop.swingx.test;

import java.lang.reflect.Constructor;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Random;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import org.jdesktop.swingx.treetable.MutableTreeTableNode;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableNode;

/**
 * This is a quick temporary addition to make the JXTreeTableVisualCheck compilable - most
 * of it is done properly in TreeUtilities but not yet used in the test. Will be removed
 * again as soon as the test uses the proper swingx support.
 * 
 * @author Jeanette Winzenburg, Berlin
 */
public class TreeTableHelper {

    public static class PostOrder implements Enumeration {
        protected TreeModel model;
        protected Object node;
        protected Enumeration children;
        protected Enumeration subtree;
        
        public PostOrder(TreeModel model, Object node) {
            this.model = model;
            this.node = node;
            children = new SingleNodeEnumeration(model, node);
            subtree = EMPTY_ENUMERATION;
        }
        
        @Override
        public boolean hasMoreElements() {
            return node != null;
        }

        @Override
        public Object nextElement() {
            if (subtree.hasMoreElements()) {
                return subtree.nextElement();
            }
            if (children.hasMoreElements()) {
                subtree = new PostOrder(model, children.nextElement());
                return subtree.nextElement();
            }
            Object result = node;
            node = null;
            return result;
        }
        
    }
    @SuppressWarnings("rawtypes")
    public static Enumeration EMPTY_ENUMERATION = new Enumeration() {

        @Override
        public boolean hasMoreElements() {
            return false;
        }

        @Override
        public Object nextElement() {
            throw new NoSuchElementException("single node emumerator");
        }
        
    };
    public static class SingleNodeEnumeration implements Enumeration {

        private Object node;
        private TreeModel model;
        int counter = 0;
        
        public SingleNodeEnumeration(TreeModel model, Object node) {
            this.model = model;
            this.node = node;
        }
        
        @Override
        public boolean hasMoreElements() {
            if (model.isLeaf(node)) return false;
            return counter < model.getChildCount(node);
        }

        @Override
        public Object nextElement() {
            if (counter < model.getChildCount(node)) {
                return model.getChild(node, counter++);
            }
            throw new NoSuchElementException("single node emumerator");
        }
        
    }
    public static class PostorderEnumeration implements Enumeration<TreeNode> {
        protected TreeNode root;
        protected Enumeration<TreeNode> children;
        protected Enumeration<TreeNode> subtree;

        @SuppressWarnings("unchecked")
        public PostorderEnumeration(TreeNode rootNode) {
            super();
            root = rootNode;
            children = root.children();
            subtree = DefaultMutableTreeNode.EMPTY_ENUMERATION;
        }

        public boolean hasMoreElements() {
            return root != null;
        }

        public TreeNode nextElement() {
            TreeNode retval;

            if (subtree.hasMoreElements()) {
                retval = subtree.nextElement();
            } else if (children.hasMoreElements()) {
                subtree = new PostorderEnumeration(
                                (TreeNode)children.nextElement());
                retval = subtree.nextElement();
            } else {
                retval = root;
                root = null;
            }

            return retval;
        }

    }  // End of class PostorderEnumeration



    public static Enumeration<TreeNode> createEnumeration(String className, TreeNode root) {
        try {
            Class clazz = Class.forName("javax.swing.tree.DefaultMutableTreeNode$" + className);
            Constructor constructor = clazz.getDeclaredConstructor(DefaultMutableTreeNode.class, TreeNode.class);
            constructor.setAccessible(true);
            return (Enumeration<TreeNode>) constructor.newInstance(new DefaultMutableTreeNode(), root);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        return null;
    }
    

    static Random random = new Random();
    static int ROWS = 200;
    static int COLUMNS = 20;
    

    public static TreeTableModel createTreeTableModel(int rows, int columns, int depth) {
        ROWS = rows;
        COLUMNS = columns;
            return new Model(createTreeTableNode(depth));
    }
    
    static Node createTreeTableNode(int depth) {
        Object[] data = new Object[COLUMNS];
        for (int i = COLUMNS; --i >= 0;)
            data[i] = createCellValue();
        Node node = new Node(data);
        if (--depth >= 0)
            for (int i = ROWS;
//                    = random.nextInt((depth / 2 + 1)
//                    * Math.round(ROWS / 20f)) + 1; 
                --i >= 0;)
                node.insert((MutableTreeNode) createTreeTableNode(depth),
                        node.getChildCount());
        return node;

    }
    
    static Integer createCellValue() {
            int d = random.nextInt(10)+1;
            d = d * d * d * d * d;
            return random.nextInt(Integer.MAX_VALUE)/d;             
    }
    

    public static class Model extends DefaultTreeModel implements TreeTableModel {

        public Model(TreeNode root) {
                super(root);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
                return Integer.class;
        }

        @Override
        public int getColumnCount() {
                return COLUMNS;
        }

        @Override
        public String getColumnName(int column) {
                return Integer.toString(column+1);
        }

        @Override
        public int getHierarchicalColumn() {
                return 0;
        }

        @Override
        public Object getValueAt(Object node, int column) {
                return ((TreeTableNode)node).getValueAt(column);
        }

        @Override
        public boolean isCellEditable(Object node, int column) {
                return true;
        }

        @Override
        public void setValueAt(Object value, Object node, int column) {
                ((MutableTreeTableNode)node).setValueAt(value, column);
        }
        
        
}

    public static class Node extends DefaultMutableTreeNode implements
            MutableTreeTableNode {

        Node(Object[] d) {
            super(d);
            data = d;
        }

        Object[] data;

        @Override
        public void insert(MutableTreeTableNode child, int index) {
            super.insert((MutableTreeNode) child, index);
        }

        @Override
        public void remove(MutableTreeTableNode node) {
            super.remove((MutableTreeNode) node);

        }

        @Override
        public void setParent(MutableTreeTableNode newParent) {
            super.setParent((MutableTreeNode) newParent);
        }

        @Override
        public int getColumnCount() {
            return data.length;
        }

        @Override
        public Object getValueAt(int column) {
            return data[column];
        }

        @Override
        public boolean isEditable(int column) {
            return true;
        }

        @Override
        public void setValueAt(Object aValue, int column) {
            data[column] = aValue;
        }

        @Override
        public Node getParent() {
            return (Node) super.getParent();
        }

        @Override
        public Node getChildAt(int index) {
            return (Node) super.getChildAt(index);
        }

    }

}
