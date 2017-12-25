/*
 * $Id: DemoMonthViewUI.java 4097 2011-11-30 19:22:13Z kschaefe $
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
 *
 */
package org.jdesktop.swingx.plaf.basic;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;

/**
 * Extension to allow for experimenting with pluggable Rendering- and/or 
 * HeaderHandler.
 * 
 * @author Jeanette Winzenburg
 */
public class DemoMonthViewUI extends BasicMonthViewUI {

    public static final String RENDERING_HANDLER_KEY = "monthView.renderingHandler";
    public static final String RENDERING_HANDLER_CLASS_NAME_KEY = "monthView.renderingHandlerClassName";
    
    /**
     * {@inheritDoc} <p>
     * 
     * Overridden to support lookup of handler/class name and return it, if available.
     * Returns a new intance of type DemoCalendarRenderingHandler otherwise.
     */
    @Override
    protected CalendarRenderingHandler createRenderingHandler() {
        String handlerClass = null;
        // <snip> Custom CalendarRenderingHandler
        // the handler is responsible for providing and configuring
        // components to render the cells of a month box. The mechanism
        // is very similar to renderers in the collection views.
        // Custom implementations can support extended visuals, by
        // adding Highlighters or StringValues.
        if (monthView != null) {
            // lookup per-monthView handler instance
           Object handler = monthView.getClientProperty(RENDERING_HANDLER_KEY);
           if (handler instanceof CalendarRenderingHandler) {
               return (CalendarRenderingHandler) handler;
           }
        }
        // look per-application handler class name
        handlerClass = UIManager.getString(RENDERING_HANDLER_CLASS_NAME_KEY);
        if (handlerClass != null) {
            try {
                Class<?> clazz = Class.forName(handlerClass);
                return (CalendarRenderingHandler) clazz.newInstance();
            } catch (Exception e) {
                // no success - fall back to default
            }
        }
        // </snip>
        // fallback to default
        return new DemoCalendarRenderingHandler();
    }

    
    /**
     * {@inheritDoc} <p>
     * 
     * Overridden to support a per-component header handler.
     */
    @Override
    protected CalendarHeaderHandler createCalendarHeaderHandler() {
        // <snip> Custom CalendarHeaderHandler
        // the HeaderHandler is responsible to provide a component used
        // as header of a month box if the monthView's zoomable property is enabled.
        // This component is added to the monthView box which allows direct
        // user interaction with it and its children.
        if (monthView != null) {
            // lookup per-instance handler
            Object handler = monthView.getClientProperty(CalendarHeaderHandler.uiControllerID);
            if (handler instanceof CalendarHeaderHandler) {
                return (CalendarHeaderHandler) handler;
            }
        }
        // </snip>
        // fallback to super (which does a for a per-application handler class name lookup
        // before falling back to a do-nothing base ;-)
        return super.createCalendarHeaderHandler();
    }

//------------------------ updates on property changes related to handlers    
    
    /**
     * Updates the RenderingHandler if the monthView's client property has changed.
     */
    protected void updateRenderingHandler() {
        // <snip> Custom CalendarRenderingHandler
        setRenderingHandler(createRenderingHandler());
        monthView.revalidate();
        monthView.repaint();
        // </snip>
    }

    /**
     * Updates the header handler if the monthView's zoomable property 
     * has changed.
     */
    protected void updateHeaderHandler() {
        // <snip> Custom CalendarHeaderHandler
        // super is not yet prepared for dynamical setting of header handler
        // so have to take over here: uninstall the old and install the new
        if (monthView.isZoomable()) {
            // cleanup old
            monthView.remove(getCalendarHeaderHandler().getHeaderComponent());
            getCalendarHeaderHandler().uninstall(monthView);
            // create and install new
            setCalendarHeaderHandler(createCalendarHeaderHandler());
            getCalendarHeaderHandler().install(monthView);
            updateZoomable();
        }
        // <snip>
    }
    

    /**
     * {@inheritDoc} <p>
     * 
     * Overridden to update header and rendering handlers on property
     * change of the monthView
     */
    @Override
    protected PropertyChangeListener createPropertyChangeListener() {
        final PropertyChangeListener r = super.createPropertyChangeListener();
        PropertyChangeListener ml = new PropertyChangeListener() {
            
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (RENDERING_HANDLER_KEY.equals(evt.getPropertyName())) {
                    updateRenderingHandler();
                } else if (CalendarHeaderHandler.uiControllerID.equals(evt.getPropertyName())) {
                    updateHeaderHandler();
                } else {
                    r.propertyChange(evt);
                }
                
            }
        };
        return ml;
    }

    
    @SuppressWarnings({"UnusedDeclaration"})
    public static ComponentUI createUI(JComponent c) {
        return new DemoMonthViewUI();
    }

    
}

