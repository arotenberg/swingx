/*
 * $Id: JXMultiSplitPaneBeanInfo.java 4111 2012-01-20 15:32:16Z kschaefe $
 *
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle,
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
import org.jdesktop.beans.editors.PainterPropertyEditor;

/**
 * Bean info for {@link org.jdesktop.swingx.JXMultiSplitPane} component.
 *
 * @author Hans Muller <Hans.Muller@Sun.COM>
 */
public class JXMultiSplitPaneBeanInfo extends BeanInfoSupport {
    public JXMultiSplitPaneBeanInfo() {
        super(JXMultiSplitPane.class);
    }

    // model, dividerSize, continuousLayout, dividerPainter
    @Override
    protected void initialize() {
        setPreferred(true, "backgroundPainter");
        setPropertyEditor(PainterPropertyEditor.class, "backgroundPainter");

        BeanDescriptor bd = getBeanDescriptor();
        bd.setName("JXMultiSplitPane");
        bd.setShortDescription("JXMultiSplitPane is a container with multiple areas divided by splitters.");
        bd.setValue("isContainer", Boolean.TRUE);

        setCategory("MultiSplitPane Layout, Appearance", "model", "dividerSize", 
                    "continuousLayout", "dividerPainter");
    }
}
