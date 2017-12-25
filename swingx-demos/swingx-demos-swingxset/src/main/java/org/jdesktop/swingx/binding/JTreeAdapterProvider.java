/*
 * $Id: JTreeAdapterProvider.java 4097 2011-11-30 19:22:13Z kschaefe $
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
package org.jdesktop.swingx.binding;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeSelectionModel;

import org.jdesktop.beansbinding.ext.BeanAdapterProvider;
import org.jdesktop.swingbinding.adapters.BeanAdapterBase;

/**
 * @author Karl George Schaefer
 * @author Shannon Hickey (for ideas from adapters package)
 */
public final class JTreeAdapterProvider implements BeanAdapterProvider {

    private static final String SELECTED_ELEMENT_P = "selectedElement";
    private static final String SELECTED_ELEMENTS_P = "selectedElements";
    private static final String SELECTED_ELEMENT_UN_P = SELECTED_ELEMENT_P + "_UNWRAP_NODE";
    private static final String SELECTED_ELEMENTS_UN_P = SELECTED_ELEMENTS_P + "_UNWRAP_NODE";

    public final class Adapter extends BeanAdapterBase {
        private JTree tree;

        private Handler handler;

        private Object cachedElementOrElements;

        private Adapter(JTree tree, String property) {
            super(property);
            this.tree = tree;
        }

        private boolean isPlural() {
            return property == SELECTED_ELEMENTS_P;
        }

        public Object getSelectedElement() {
            return JTreeAdapterProvider.getSelectedElement(tree);
        }

        public Object getSelectedElement_UNWRAP_NODE() {
            Object o = JTreeAdapterProvider.getSelectedElement(tree);
            
            return unwrappedObject(o);
        }
        
        public List<Object> getSelectedElements() {
            return JTreeAdapterProvider.getSelectedElements(tree);
        }

        public List<Object> getSelectedElements_UNWRAP_NODE() {
            List<Object> objects = JTreeAdapterProvider.getSelectedElements(tree);
            List<Object> unwrapped = new ArrayList<Object>(objects.size());
            
            for (Object o : objects) {
                unwrapped.add(unwrappedObject(o));
            }
            
            return unwrapped;
        }
        
        //why oh why is there no getUserObject method in the interface?!
        private Object unwrappedObject(Object o) {
            //possible to have null element
            if (o == null) {
                return o;
            }
            
            try {
                Method m = o.getClass().getMethod("getUserObject");
                
                return m.invoke(o);
            } catch (SecurityException ignore) {
                //just return o
            } catch (NoSuchMethodException ignore) {
                //just return o
            } catch (IllegalAccessException ignore) {
                //just return o
            } catch (InvocationTargetException ignore) {
                //just return o
            }
            
            return o;
        }
        
        protected void listeningStarted() {
            handler = new Handler();
            cachedElementOrElements = isPlural() ? getSelectedElements() : JTreeAdapterProvider
                    .getSelectedElement(tree);
            tree.addPropertyChangeListener("selectionModel", handler);
            tree.getSelectionModel().addTreeSelectionListener(handler);
        }

        protected void listeningStopped() {
            tree.getSelectionModel().removeTreeSelectionListener(handler);
            tree.removePropertyChangeListener("selectionModel", handler);
            cachedElementOrElements = null;
            handler = null;
        }

        private class Handler implements TreeSelectionListener, PropertyChangeListener {
            private void treeSelectionChanged() {
                Object oldElementOrElements = cachedElementOrElements;
                cachedElementOrElements = getSelectedElements();
                firePropertyChange(oldElementOrElements, cachedElementOrElements);
            }

            public void valueChanged(TreeSelectionEvent e) {
                treeSelectionChanged();
            }

            public void propertyChange(PropertyChangeEvent pce) {
                ((TreeSelectionModel) pce.getOldValue()).removeTreeSelectionListener(handler);
                ((TreeSelectionModel) pce.getNewValue()).addTreeSelectionListener(handler);
                treeSelectionChanged();
            }
        }
    }

    private static List<Object> getSelectedElements(JTree tree) {
        assert tree != null;

        TreeSelectionModel selectionModel = tree.getSelectionModel();
        int min = selectionModel.getMinSelectionRow();
        int max = selectionModel.getMaxSelectionRow();

        if (min < 0 || max < 0) {
            return new ArrayList<Object>(0);
        }

        ArrayList<Object> elements = new ArrayList<Object>(max - min + 1);

        for (int i = min; i <= max; i++) {
            if (selectionModel.isRowSelected(i)) {
                elements.add(getElement(tree, i));
            }
        }

        return elements;
    }

    private static Object getSelectedElement(JTree tree) {
        assert tree != null;

        int index = tree.getSelectionModel().getLeadSelectionRow();
        index = tree.getSelectionModel().isRowSelected(index) ? index : tree.getSelectionModel()
                .getMinSelectionRow();

        if (index == -1) {
            return null;
        }

        return getElement(tree, index);
    }

    private static Object getElement(JTree tree, int index) {
        return tree.getPathForRow(index).getLastPathComponent();
    }

    public boolean providesAdapter(Class<?> type, String property) {
        if (!JTree.class.isAssignableFrom(type)) {
            return false;
        }

        property = property.intern();

        return property == SELECTED_ELEMENT_P
                || property == SELECTED_ELEMENT_UN_P
                || property == SELECTED_ELEMENTS_P
                || property == SELECTED_ELEMENTS_UN_P;
    }

    public Object createAdapter(Object source, String property) {
        if (!providesAdapter(source.getClass(), property)) {
            throw new IllegalArgumentException();
        }

        return new Adapter((JTree) source, property);
    }

    public Class<?> getAdapterClass(Class<?> type) {
        return JTree.class.isAssignableFrom(type) ? JTreeAdapterProvider.Adapter.class : null;
    }

}
