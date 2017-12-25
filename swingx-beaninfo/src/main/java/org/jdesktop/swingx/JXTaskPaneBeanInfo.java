/*
 * $Id: JXTaskPaneBeanInfo.java 3846 2010-10-21 15:49:30Z kschaefe $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx;

import java.beans.BeanDescriptor;

import org.jdesktop.beans.BeanInfoSupport;

/**
 * BeanInfo class for JXTaskPane.
 * 
 * @author rbair, Jan Stola
 */
public class JXTaskPaneBeanInfo extends BeanInfoSupport {
    
    /** Constructor for the JXTaskPaneBeanInfo object */
    public JXTaskPaneBeanInfo() {
        super(JXTaskPane.class);
    }
    
    @Override
    protected void initialize() {
        BeanDescriptor bd = getBeanDescriptor();
        
        // setup bean descriptor in constructor.
        bd.setName("JXTaskPane");
        bd.setShortDescription("JXTaskPane is a container for tasks and other arbitrary components.");
        bd.setValue("isContainer", Boolean.TRUE);
        bd.setValue("containerDelegate", "getContentPane");
        
        setPreferred(true, "title", "icon", "special");
        setPreferred(true, "animated", "scrollOnExpand", "collapsed", "font");
        setBound(true, "title", "icon", "special", "scrollOnExpand", "collapsed");
        setPreferred(false, "border");
    }
}
