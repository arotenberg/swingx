/*
 * $Id: InputEventDispatcher.java 4097 2011-11-30 19:22:13Z kschaefe $
 *
 * Copyright 2007 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx.event;

import java.awt.AWTEvent;

/**
 * A InputEventDispatcher cooperates with a component's InputContext in the
 * targeting and dispatching of all Input- and FocusEvents. InputEventDispatcher
 * can be registered on a dispatcher-aware component. It  
 * receive Input- and FocusEvents before they are
 * dispatched to the component's InputContext if the latter is enabled. (default?)
 * <p>
 * 
 * 
 * 
 * @author Jeanette Winzenburg
 */
public interface InputEventDispatcher {

    /**
     * This method is called by the component's  InputContext requesting
     * that this InputEventDispatcher dispatch the specified event on its behalf.
     * <p>
     * 
     * NOTE: only Input- and FocusEvents will be received here 
     * (same as the received by a InputContext).
     * . 
     *
     * @param e the Input- or FocusEvent to dispatch
     */
    void dispatchEvent(AWTEvent e);
}
