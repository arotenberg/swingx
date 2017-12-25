/*
 * Created on 20.06.2005
 *
 */
package org.jdesktop.swingx.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;
import javax.swing.tree.TreeModel;

/**
 * A static snapshot of a container hierarchy.
 * 
 * NOTE: does not listen to any property changes of
 * contained components - cell updates are arbitrary on
 * repaint only!
 * 
 * @author Jeanette Winzenburg
 */
public class ComponentTableModel extends AbstractTableModel {


    public int getRowCount() {
        return components.size();
    }

    public Class getColumnClass(int column) {
        switch (column) {
        case 0:
            return String.class;
        case 1:
            return Point.class;
        case 2:
            return Dimension.class;
        default:
            return Object.class;
        }
    }
    
    public int getColumnCount() {
        return 3;
    }
    public String getColumnName(int column) {
        switch (column) {
        case 0:
            return "Type";
        case 1:
            return "Location";
        case 2:
            return "Size";
        default:
            return "Column " + column;
        }
    }
    
    public Object getValueAt(int row, int column) {
        Component comp = (Component) components.get(row);
        switch (column) {
        case 0:
            return convertValueToText(comp);
        case 1:
            return comp.getLocation();
        case 2:
            return comp.getSize();
        default:
            return null;
        }
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }


    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
      
    }

    private List components = new ArrayList();
    
    public void updateComponentList(TreeModel model) {
        components = new ArrayList();
        Enumeration enumeration = new PreorderEnumeration(model);
        while (enumeration.hasMoreElements()) {
            components.add(enumeration.nextElement());
        }
        fireTableDataChanged();
        
    }
    public String convertValueToText(Object node) {
        String className = node.getClass().getName();
        int lastDot = className.lastIndexOf(".");
        String lastElement = className.substring(lastDot + 1);
        return lastElement;
    }

//  -------------------- helper classes for traversing the tree
//  -------------------- c&p from Santhosh Kumar
//  --------------------http://jroller.com/page/santhosh/20050607#tree_inside_jcombobox    
      
      class ChildrenEnumeration implements Enumeration{ 
          TreeModel treeModel; 
          Object node; 
          int index = -1; 
       
          public ChildrenEnumeration(TreeModel treeModel, Object node){ 
              this.treeModel = treeModel; 
              this.node = node; 
          } 
       
          public boolean hasMoreElements(){ 
              return index<treeModel.getChildCount(node)-1; 
          } 
       
          public Object nextElement(){ 
              return treeModel.getChild(node, ++index); 
          } 
      } 

      class PreorderEnumeration implements Enumeration{ 
          private TreeModel treeModel; 
          protected Stack stack; 
       
          public PreorderEnumeration(TreeModel treeModel){ 
              this.treeModel = treeModel; 
              Vector v = new Vector(1); 
              v.addElement(treeModel.getRoot()); 
              stack = new Stack(); 
              stack.push(v.elements()); 
          } 
       
          public boolean hasMoreElements(){ 
              return (!stack.empty() && 
                      ((Enumeration)stack.peek()).hasMoreElements()); 
          } 
       
          public Object nextElement(){ 
              Enumeration enumer = (Enumeration)stack.peek(); 
              Object node = enumer.nextElement(); 
              if(!enumer.hasMoreElements()) 
                  stack.pop(); 
              Enumeration children = new ChildrenEnumeration(treeModel, node); 
              if(children.hasMoreElements()) 
                  stack.push(children); 
              return node; 
          } 
      } 

}
