/*
 * Created on 17.07.2008
 *
 */
package org.jdesktop.swingx.treetable;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import org.jdesktop.swingx.util.Contract;

/**
 * Helper to trigger tree event notification on a TreeModel after 
 * a node content has changed in some way. 
 */
public interface NodeChangedMediator {
    
    /**
     * Mediates node change Notification for a DefaultTreeModel.
     */
    public final static NodeChangedMediator DEFAULT = new NodeChangedMediator() {

        public void nodeChanged(TreeModel model, Object node) {
            Contract.asNotNull(node, "tree node must not be null");
            Contract.asNotNull(model, "model must not be null");
            if (!(model instanceof DefaultTreeModel)) 
                throw new IllegalArgumentException("expected model of type DefaultTreeModel instead of " 
                        + model.getClass().getName());
            DefaultTreeModel treeModel = (DefaultTreeModel) model;
            treeModel.nodeChanged((TreeNode) node);
        }
        
    };

    /**
     * Guarantees to trigger a nodeChanged event from the given TreeModel.
     * 
     * @param model the TreeModel which contains the node, must not be null.
     * @param node the node which has changed. Must be contained in the model, 
     *    must not be null.
     * @throws NullPointerException if model or node is null
     * @throws IllegalArgumentException if it can't guarantee the notification
     */
    public void nodeChanged(TreeModel model, Object node);
}
