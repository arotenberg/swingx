/*
 * $Id: AbstractLayoutPainterTest.java 3899 2010-11-29 21:39:37Z kschaefe $
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
package org.jdesktop.swingx.painter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.awt.Insets;

import org.junit.Test;


/**
 * Test for AbstractLayoutPainter.
 */
@SuppressWarnings({"rawtypes"})
public class AbstractLayoutPainterTest extends AbstractPainterTest {
    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractLayoutPainter createTestingPainter() {
        return new DummyLayoutPainter();
    }
    
    @Test
    @Override
    public void testDefaults() {
        super.testDefaults();
        
        AbstractLayoutPainter alp = (AbstractLayoutPainter) p;
        assertThat(alp.getHorizontalAlignment(), is(AbstractLayoutPainter.HorizontalAlignment.CENTER));
        assertThat(alp.getInsets(), is(new Insets(0, 0, 0, 0)));
        assertThat(alp.getVerticalAlignment(), is(AbstractLayoutPainter.VerticalAlignment.CENTER));
        assertThat(alp.isFillHorizontal(), is(false));
        assertThat(alp.isFillVertical(), is(false));
    }
}
