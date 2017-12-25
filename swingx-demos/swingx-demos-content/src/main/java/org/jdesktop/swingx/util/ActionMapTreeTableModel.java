/*
 * $Id: ActionMapTreeTableModel.java 4097 2011-11-30 19:22:13Z kschaefe $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.tree.TreeNode;

import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableNode;

/**
 * Convenience TreeTableModel for wrapping an ActionMap hierarchy.
 * 
 * @author Jeanette Winzenburg, Berlin
 */
public class ActionMapTreeTableModel extends DefaultTreeTableModel {

    
    public ActionMapTreeTableModel(JComponent comp) {
        super();
        setRoot(createRootNodeExt(comp));
        Vector names = new Vector();
        names.add("Key Name");
        names.add("Action Name");
        names.add("Action Command");
        setColumnIdentifiers(names);
    }


    private ActionEntryNode createRootNodeExt(JComponent comp) {
        ActionMap map = comp.getActionMap();
        if (map == null)
            throw new IllegalArgumentException("Component must have ActionMap");
        List actionMaps = new ArrayList();
        actionMaps.add(map);
        while ((map = map.getParent()) != null) {
            actionMaps.add(0, map);
        }
        return createActionEntryNodes(actionMaps);
    }

    private ActionEntryNode createActionEntryNodes(List actionMaps) {
        ActionMap topLevel = (ActionMap) actionMaps.get(0);
        ActionEntryNode mapRoot = new ActionEntryNode("topLevel", topLevel, null);
        ActionEntryNode current = mapRoot;
        for (int i = 1; i < actionMaps.size(); i++) {
            current = current.addActionMapAsChild("childMap " + i,
                    (ActionMap) actionMaps.get(i));
        }
        return mapRoot;
    }

    private static class ActionEntryNode implements TreeTableNode {
        ActionEntryNode parent;
        
        Object key;

        Action action;

        ActionMap actionMap;

        List<ActionEntryNode> children;

        public ActionEntryNode(Object key, Action action, ActionEntryNode parent) {
            this.parent = parent;
            this.key = key;
            this.action = action;
            children = Collections.EMPTY_LIST;
        }

        public ActionEntryNode(Object key, ActionMap map, ActionEntryNode parent) {
            this.parent = parent;
            this.key = key;
            this.actionMap = map;
            children = new ArrayList<ActionEntryNode>();
            Object[] keys = map.keys();
            for (int i = 0; i < keys.length; i++) {
                children.add(new ActionEntryNode(keys[i], (Action) map
                        .get(keys[i]), this));
            }
        }

        /**
         * pre: !isLeaf
         * 
         * @param key
         * @param map
         */
        public ActionEntryNode addActionMapAsChild(Object key, ActionMap map) {
            ActionEntryNode actionEntryNode = new ActionEntryNode(key, map, this);
            getChildren().add(0, actionEntryNode);
            return actionEntryNode;
        }

        public List<ActionEntryNode> getChildren() {
            return children;
        }


        public ActionMap getActionMap() {
            return actionMap;
        }

        public Action getAction() {
            return action;
        }


        public String toString() {
            return key.toString();
        }

        // --------------- implement TreeNode
        public boolean isLeaf() {
            return action != null;
        }
        
        public boolean getAllowsChildren() {
            return !isLeaf();
        }

        public int getChildCount() {
            return children.size();
        }

        public int getIndex(TreeNode node) {
            return children.indexOf(node);
        }
        
        
        //------------- implement re-defined methods of TreeNode
        
        public Enumeration<? extends TreeTableNode> children() {
            return Collections.enumeration(children);
        }

        
        public TreeTableNode getChildAt(int childIndex) {
            return children.get(childIndex);
        }

        public TreeTableNode getParent() {
            return parent;
        }

        //---------------- implement TreeTableNode
        
        public int getColumnCount() {
            return 2;
        }
        
        public Object getValueAt(int column) {
            ActionEntryNode actionNode = this;

            switch (column) {
            case 0:
                return actionNode.key;
            case 1:
                if (actionNode.isLeaf())
                    return actionNode.getAction().getValue(Action.NAME);
                return null;
            case 2:
                if (actionNode.isLeaf())
                    return actionNode.getAction().getValue(
                            Action.ACTION_COMMAND_KEY);
            // case 3:
            // return "Modification Date";
            default:
                return null;
            }
        }

        public boolean isEditable(int column) {
            return false;
        }

        public void setValueAt(Object aValue, int column) {
            // do nothing
            
        }


        public Object getUserObject() {
            return getAction();
       }

       public void setUserObject(Object userObject) {
           // TODO Auto-generated method stub
           
       }


    }

}
