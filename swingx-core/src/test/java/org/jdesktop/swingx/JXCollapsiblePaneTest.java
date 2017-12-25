/*
 * $Id: JXCollapsiblePaneTest.java 3950 2011-03-14 20:17:23Z kschaefe $
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
 *
 */
package org.jdesktop.swingx;


import static org.hamcrest.CoreMatchers.is;
import static org.jdesktop.test.matchers.Matchers.property;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.beans.PropertyChangeListener;

import javax.swing.JLabel;

import org.jdesktop.test.EDTRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Unit test for <code>JXCollapsiblePane</code>.
 * <p>
 * 
 * All test methods in this class are expected to pass. 
 * 
 * @author Karl Schaefer
 */
@RunWith(EDTRunner.class)
public class JXCollapsiblePaneTest {
    /**
     * SWINGX-1412: Ensure that we do not animate when not showing.
     */
    @Test
    public void testCollapsingNonShowingPane() {
        JXCollapsiblePane pane = new JXCollapsiblePane();
        pane.add(new JLabel("some content"));
        
        PropertyChangeListener pcl = mock(PropertyChangeListener.class);
        pane.addPropertyChangeListener("collapsed", pcl);
        
        pane.setCollapsed(true);
        
        // this fails if we animate because the property change
        // will enqueue on the EDT after this check
        verify(pcl).propertyChange(argThat(is(property("collapsed", false, true))));
    }
 }
