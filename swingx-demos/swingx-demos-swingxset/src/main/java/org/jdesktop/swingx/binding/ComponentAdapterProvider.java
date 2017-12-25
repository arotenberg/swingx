/*
 * $Id: ComponentAdapterProvider.java 4097 2011-11-30 19:22:13Z kschaefe $
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

import java.awt.Component;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import org.jdesktop.beansbinding.ext.BeanAdapterProvider;
import org.jdesktop.swingbinding.adapters.BeanAdapterBase;

/**
 * @author Karl George Schaefer
 * @author Shannon Hickey (for ideas from adapters package)
 */
public final class ComponentAdapterProvider implements BeanAdapterProvider {

    private static final String VISIBLE_P = "visible";

    public final class Adapter extends BeanAdapterBase {
        private Component comp;

        private Handler handler;

        private boolean visible;

        private Adapter(Component comp, String property) {
            super(property);
            this.comp = comp;
            visible = comp.isVisible();
        }

        public boolean isVisible() {
            return visible;
        }
        
        public void setVisible(boolean visible) {
            comp.setVisible(visible);
        }
        
        @Override
        protected void listeningStarted() {
            handler = new Handler();
            comp.addComponentListener(handler);
        }

        @Override
        protected void listeningStopped() {
            comp.removeComponentListener(handler);
            visible = false;
            handler = null;
        }

        private class Handler implements ComponentListener {
            @Override
            public void componentShown(ComponentEvent e) {
                visible = true;
                firePropertyChange(false, true);
            }
            
            @Override
            public void componentHidden(ComponentEvent e) {
                visible = false;
                firePropertyChange(true, false);
            }

            @Override
            public void componentMoved(ComponentEvent e) { }

            @Override
            public void componentResized(ComponentEvent e) { }
        }
    }

    public boolean providesAdapter(Class<?> type, String property) {
        if (!Component.class.isAssignableFrom(type)) {
            return false;
        }

        property = property.intern();

        return property == VISIBLE_P;
    }

    public Object createAdapter(Object source, String property) {
        if (!providesAdapter(source.getClass(), property)) {
            throw new IllegalArgumentException();
        }

        return new Adapter((Component) source, property);
    }

    public Class<?> getAdapterClass(Class<?> type) {
        return Component.class.isAssignableFrom(type) ? ComponentAdapterProvider.Adapter.class : null;
    }

}
