/*
 * $Id: ComponentModels.java 4097 2011-11-30 19:22:13Z kschaefe $
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
 */
package org.jdesktop.swingxset.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.util.Collections;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.ListModel;
import javax.swing.table.TableModel;
import javax.swing.tree.TreeModel;

import org.jdesktop.swingx.combobox.ListComboBoxModel;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.jdesktop.swingx.util.WindowUtils;

/**
 * A collection of models for the data display classes. These models are backed by the component
 * hierarchy. This means that they should always contain something and that they should not require
 * any special permissions (such as file system access). The models are ideal for demos that may be
 * shown via Web Start, where signing is a concern.
 * 
 * @author Karl George Schaefer
 */
//TODO consider refactoring the common models pieces (like getValueAt switch) into reusable methods
public final class ComponentModels {
    //cover for broken getAllComponents method, which should take Component and not Container
    private static List<Component> getComponents(Component root) {
        List<Component> components;
        
        if (root instanceof Container) {
            components = WindowUtils.getAllComponents((Container) root);
        } else {
            components = Collections.singletonList(root);
        }
        
        return components;
    }
    
    public static ListModel getListModel(Component root) {
        return getComboBoxModel(root);
    }
    
    public static ComboBoxModel getComboBoxModel(Component root) {
        return new ListComboBoxModel<Component>(getComponents(root));
    }
    
    //TODO implement column names?
    public static TableModel getTableModel(Component root) {
        AbstractListTableModel<Component> tm = new AbstractListTableModel<Component>() {
            public int getColumnCount() {
                return 4;
            }

            public Object getValueAt(int rowIndex, int columnIndex) {
                Component c = getRow(rowIndex);
                Object o = null;
                
                switch (columnIndex) {
                case 0:
                    o = c;
                    break;
                case 1:
                    o = c.getName();
                    break;
                case 2:
                    if (c.isShowing()) {
                        o = c.getLocationOnScreen();
                    }
                    break;
                case 3:
                    o = c.getSize();
                    break;
                default:
                    //does nothing
                    break;
                }
                
                return o;
            }
        };
        tm.addRows(getComponents(root));
        
        return tm;
    }
    
    public static TreeModel getTreeModel(Component root) {
        return getTreeTableModel(root);
    }
    
    //TODO implement column names?
    public static TreeTableModel getTreeTableModel(Component root) {
        return new AbstractTreeTableModel(root) {

            public int getColumnCount() {
                return 4;
            }

            public Object getValueAt(Object node, int column) {
                Component c = (Component) node;
                Object o = null;
                
                switch (column) {
                case 0:
                    o = c;
                    break;
                case 1:
                    o = c.getName();
                    break;
                case 2:
                    if (c.isShowing()) {
                        o = c.getLocationOnScreen();
                    }
                    break;
                case 3:
                    o = c.getSize();
                    break;
                default:
                    //does nothing
                    break;
                }
                
                return o;
            }

            
            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                case 0:
                    return Component.class;
                case 1:
                    return String.class;
                case 2:
                    return Point.class;
                case 3:
                    return Dimension.class;
                }    
                return super.getColumnClass(column);
            }

            public Object getChild(Object parent, int index) {
                return ((Container) parent).getComponent(index);
            }

            public int getChildCount(Object parent) {
                return parent instanceof Container ? ((Container) parent).getComponentCount() : 0;
            }

            public int getIndexOfChild(Object parent, Object child) {
                Component[] children = ((Container) parent).getComponents();
                
                for (int i = 0, len = children.length; i < len; i++) {
                    if (child == children[i]) {
                        return i;
                    }
                }
                
                return -1;
            }
            
        };
    }
}
