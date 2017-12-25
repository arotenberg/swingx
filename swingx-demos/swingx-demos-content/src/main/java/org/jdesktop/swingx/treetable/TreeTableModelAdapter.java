/*
 * Created on 14.07.2008
 *
 */
package org.jdesktop.swingx.treetable;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.tree.TreeModelSupport;
import org.jdesktop.swingx.treetable.TreeTableModel;

/**
 * Adapts a TreeModel to a TreeTableModel. The per-node columnar data is
 * provided by a NodeModel. If the columnar data should be editable, a
 * NodeChangedMediator is required to handle the change notification in the
 * adapted TreeModel.
 * <p>
 * 
 * Example usage: Assuming a default tree model with build from treeNodes which
 * have Person objects as userObject, developers can implement a PersonNodeModel
 * for the columnar data
 * 
 * <pre><code>
 * public class PersonNodeModel implements NodeModel {
 *      public Class&lt;?&gt; getColumnClass(int column) {
 *      if (column == 2) return Date.class;
 *      return String.class;
 *  }
 * 
 *  public int getColumnCount() {
 *      return 3;
 *  }
 * 
 *  public String getColumnName(int column) {
 *      switch(column) {
 *              case 0:
 *                      return &quot;Name&quot;;
 *              case 1:
 *                      return &quot;First Name&quot;;
 *              case 2:
 *                      return &quot;Birth Date&quot;;
 *              default:
 *                      return null;
 *      }
 *  }
 * 
 *  public int getHierarchicalColumn() {
 *      return 0;
 *  }
 * 
 *  public Object getValueAt(Object node, int column) {
 *      Person person = getPerson(node);
 *      if (person == null) return note;
 *      switch (column) {
 *      case 0:
 *              return person.getFamilyName();
 *      case 1:
 *              return person.getFirstName();
 *      case 2:
 *              return person.getBirthDate();
 *      }
 *      return null;
 *  }
 * 
 *  private Person getPerson(Object node) {
 *      if (node instanceof DefaultMutableTreeNode) {
 *              node = ((DefaultMutableTreeNode) node).getUserObject();
 *      }
 *      if (node instanceof Person) return (Person) node;
 *      return null;
 *  }
 * 
 *  public boolean isCellEditable(Object node, int column) {
 *      return true;
 *  }
 * 
 *  public void setValueAt(Object value, Object node, int column) {
 *      Person person = getPerson(node);
 *      if (note == null) return;
 *      switch(column) {
 *      case 0:
 *              person.setFamilyName(String.valueOf(value));
 *              break;
 *      case 1:
 *              person.setFirstName((String) value);
 *              break;
 *      case 2:
 *              person.setBirthDate((Date) value);
 * 
 *      }
 * 
 *  }
 * 
 * 
 * }
 * </code></pre>
 * 
 * For usage in a read-only JXTreeTable:
 * 
 * 
 * <pre><code>
 * TreeTableModel treeTableModel = new TreeTableModelAdapter(personTreeModel,
 *         new PersonNodeModel());
 * treeTable.setModel(treeTableModel);
 * </code></pre>
 * 
 * For usage in an editable JXTreeTable, you'll need to configure the adapter
 * with an appropriate NodeChangedMediator which takes care of change
 * notification by the adapted TreeModel. For TreeModels of type
 * DefaultTreeModel, there's a pre-defined default mediator (for custom types
 * you'll have to implement a custom mediator which knows how to trigger changed events on 
 * behalf of the TreeModel):
 * 
 * <pre><code>
 * treeTableModel.setNodeChangedMediator(NodeChangedMediator.DEFAULT);
 * </code></pre>
 */
public class TreeTableModelAdapter implements TreeTableModel {

    /** The adapted treeModel. */
    private TreeModel treeModel;

    /** The columnar data. */
    private NodeModel nodeModel;
    /** The treeModelsupport which manages listener notification. */
    private TreeModelSupport treeModelSupport;

    /** Translator of events coming from the adapted TreeModel. */
    private Broadcaster broadcaster;

    /** The mediator responsible for node changed notification after edits.*/
    private NodeChangedMediator mediator;

    /**
     * Instantiates a TreeTableModelAdapter from the given TreeModel 
     * and NodeModel. The NodeChangedMediator is set to null.
     * 
     * @param treeModel the TreeModel to adapt.
     * @param nodeModel  the NodelModel which decorates the columnar properties.
     */
    public TreeTableModelAdapter(TreeModel treeModel, NodeModel nodeModel) {
        this(treeModel, nodeModel, null);
    }

    /**
     * Instantiates a TreeTableModelAdapter from the given TreeModel, 
     * NodeModel and NodeChangedMediator. If the mediator is null, 
     * this adapter is not editable.
     * 
     * @param treeModel the TreeModel to adapt.
     * @param nodeModel  the NodelModel which decorates the columnar properties.
     * @param mediator the NodeChangedMediator responsible for nodeChanged notification.
     */
    public TreeTableModelAdapter(TreeModel treeModel, NodeModel nodeModel,
            NodeChangedMediator mediator) {
        this.nodeModel = nodeModel;
        this.treeModel = treeModel;
        treeModelSupport = new TreeModelSupport(this);
        broadcaster = new Broadcaster();
        treeModel.addTreeModelListener(broadcaster);
        setNodeChangedMediator(mediator);
    }

    /**
     * Sets the NodeChangedMediator. 
     * 
     * @param mediator the mediator responsible for node changed notification
     * on the adapted TreeModel.
     */
    public void setNodeChangedMediator(NodeChangedMediator mediator) {
        this.mediator = mediator;
    }

    // ---------------- TreeTableModel

    /**
     * {@inheritDoc} <p>
     * 
     * Implemented to delegate to the associated NodeModel.
     */
    public Class<?> getColumnClass(int columnIndex) {
        return nodeModel.getColumnClass(columnIndex);
    }

    /**
     * {@inheritDoc} <p>
     * 
     * Implemented to delegate to the associated NodeModel or return
     * 0 if NodeModel is null.
     */
    public int getColumnCount() {
        return nodeModel != null ? nodeModel.getColumnCount() : 0;
    }

    /**
     * {@inheritDoc} <p>
     * 
     * Implemented to delegate to the associated NodeModel.
     */
    public String getColumnName(int column) {
        return nodeModel.getColumnName(column);
    }

    /**
     * {@inheritDoc} <p>
     * 
     * Implemented to delegate to the associated NodeModel or return -1 
     * if nodeModel is null.
     */
    public int getHierarchicalColumn() {
        return nodeModel != null ? nodeModel.getHierarchicalColumn() : -1;
    }

    /**
     * {@inheritDoc} <p>
     * 
     * Implemented to delegate to the associated NodeModel.
     */
    public Object getValueAt(Object node, int column) {
        return nodeModel.getValueAt(node, column);
    }

    /**
     * Returns the adapter's editability. If true, it must
     * guarantee to fire appropriate change notification on the
     * adapted TreeModel. <p>
     * 
     * This implementation returns true if it has a NodeChangeMediator, false otherwise.<p>
     * 
     * PENDING: not flexible enough if has to fire on "adapted TreeModel"?
     * Custom models might have their own ideas as to when they want to fire. This
     * adapter relies on getting change notification back from the model, does
     * nothing on its own to serve its notification responsibility. If we allow
     * models to not fire (f.i. if properties which they consider to not constitute
     * a nodeChanged) we'll need an enhanced mediator which provides a treePath to
     * the root so we can fire a changed on our own behalf.
     * 
     * @return the adapter's editable.
     */
    protected boolean isEditable() {
        return mediator != null;
    }

    /**
     * {@inheritDoc} <p>
     * 
     * Implemented to delegate to the associated NodeModel if the adapter is
     * editable, returns false if the adapter is not editable.
     * 
     * @see #isEditable()
     */
    public boolean isCellEditable(Object node, int column) {
        return isEditable() && nodeModel.isCellEditable(node, column);
    }

    /**
     * {@inheritDoc} <p>
     * 
     * Implemented to delegate to the associated NodeModel if the cell
     * is editable. Does nothing if the cell is not editable.
     */
    public void setValueAt(Object value, Object node, int column) {
        if (!isCellEditable(node, column))
            return;
        nodeModel.setValueAt(value, node, column);
        mediator.nodeChanged(treeModel, node);
    }

    // ----------------- TreeModel

    /**
     * {@inheritDoc} <p>
     * 
     * Implemented to delegate to the adapted TreeModel.
     */
    public Object getChild(Object parent, int index) {
        return treeModel.getChild(parent, index);
    }

    /**
     * {@inheritDoc} <p>
     * 
     * Implemented to delegate to the adapted TreeModel.
     */
    public int getChildCount(Object parent) {
        return treeModel.getChildCount(parent);
    }

    /**
     * {@inheritDoc} <p>
     * 
     * Implemented to delegate to the adapted TreeModel.
     */
    public int getIndexOfChild(Object parent, Object child) {
        return treeModel.getIndexOfChild(parent, child);
    }

    /**
     * {@inheritDoc} <p>
     * 
     * Implemented to delegate to the adapted TreeModel if available, or
     * returns null if the TreeModel is null.
     */
    public Object getRoot() {
        return treeModel != null ? treeModel.getRoot() : null;
    }

    /**
     * {@inheritDoc} <p>
     * 
     * Implemented to delegate to the adapted TreeModel.
     */
    public boolean isLeaf(Object node) {
        return treeModel.isLeaf(node);
    }

    /**
     * {@inheritDoc} <p>
     * 
     * Implemented to delegate to the adapted TreeModel.
     */
    public void valueForPathChanged(TreePath path, Object newValue) {
        treeModel.valueForPathChanged(path, newValue);
    }

    /**
     * {@inheritDoc} <p>
     * 
     */
    public void addTreeModelListener(TreeModelListener l) {
        treeModelSupport.addTreeModelListener(l);
    }

    /**
     * {@inheritDoc} <p>
     * 
     */
    public void removeTreeModelListener(TreeModelListener l) {
        treeModelSupport.removeTreeModelListener(l);
    }

    /**
     * The class responsible to translate TreeEvents received from the adapted
     * TreeModel to events as appropriate for listeners of this adapter. All
     * event state is taken as is, except for the source which is changed to
     * this.
     */
    private class Broadcaster implements TreeModelListener {

        /**
         * {@inheritDoc} <p>
         * 
         */
        public void treeNodesChanged(TreeModelEvent e) {
            treeModelSupport.fireChildrenChanged(e.getTreePath(), e
                    .getChildIndices(), e.getChildren());
        }

        /**
         * {@inheritDoc} <p>
         * 
         */
        public void treeNodesInserted(TreeModelEvent e) {
            treeModelSupport.fireChildrenAdded(e.getTreePath(), e
                    .getChildIndices(), e.getChildren());
        }

        /**
         * {@inheritDoc} <p>
         * 
         */
        public void treeNodesRemoved(TreeModelEvent e) {
            treeModelSupport.fireChildrenRemoved(e.getTreePath(), e
                    .getChildIndices(), e.getChildren());
        }

        /**
         * {@inheritDoc} <p>
         * 
         */
        public void treeStructureChanged(TreeModelEvent e) {
            treeModelSupport.fireTreeStructureChanged(e.getTreePath());
        }

    }

}
