/*
 * $Id: JXLabelTest.java 3483 2009-09-02 12:33:21Z kleopatra $
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

import java.util.logging.Logger;

import org.jdesktop.swingx.painter.AbstractPainter;
import org.jdesktop.swingx.painter.Painter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Base test class for JXLabel related code and issues.
 * 
 * @author rah003
 */
@RunWith(JUnit4.class)
public class JXLabelTest extends InteractiveTestCase {
    
    static Logger log = Logger.getAnonymousLogger();

    /**
     * Issue #1164-swingx: do not interfere with core antialiased handling.
     */
    @Test
    public void testDefaultForegroundNotAntialiasing() {
        JXLabel label = new JXLabel();
        AbstractPainter<?> painter = (AbstractPainter<?>) label.getForegroundPainter();
        assertFalse("foreground painter must not be antialiasing", painter.isAntialiasing());
    }
    
    /**
     * Issue #??-swingx: default foreground painter not guaranteed after change.
     *
     */
    @Test
    public void testDefaultForegroundPainter() {
        JXLabel label =  new JXLabel();
        Painter<?> defaultForeground = label.getForegroundPainter();
        // sanity
        assertNotNull(defaultForeground);
        label.setForegroundPainter(null);
        assertEquals(defaultForeground, label.getForegroundPainter());
    }
}
