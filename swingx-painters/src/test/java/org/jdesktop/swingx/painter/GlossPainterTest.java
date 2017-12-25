/*
 * $Id: GlossPainterTest.java 3899 2010-11-29 21:39:37Z kschaefe $
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

import java.awt.Color;
import java.awt.Paint;

import org.hamcrest.CoreMatchers;
import org.junit.Test;


/**
 * Test for GlossPainter.
 */
public class GlossPainterTest extends AbstractPainterTest {
    /**
     * {@inheritDoc}
     */
    @Override
    protected GlossPainter createTestingPainter() {
        return new GlossPainter();
    }
    
    /**
     * TODO remove when the compound painter does not start dirty 
     */
    private void copyOfSuper_testDefaultsWithCorrectedValues() {
        assertThat(p.getFilters().length, is(0));
        assertThat(p.getInterpolation(), is(AbstractPainter.Interpolation.NearestNeighbor));
        assertThat(p.isAntialiasing(), is(true));
        assertThat(p.isCacheable(), is(false));
        assertThat(p.isCacheCleared(), is(true));
        //TODO this is because the constructor calls the setters
        assertThat(p.isDirty(), is(true));
        assertThat(p.isInPaintContext(), is(false));
        assertThat(p.isVisible(), is(true));
        assertThat(p.shouldUseCache(), is(false));
    }
    
    /**
     * {@inheritDoc}
     * <p>
     * Overridden for GlossPainter defaults.
     */
    @Test
    @Override
    public void testDefaults() {
        //TODO replace with super.testDefaults() when corrected
        copyOfSuper_testDefaultsWithCorrectedValues();
//        super.testDefaults();
        
        GlossPainter gp = (GlossPainter) p;
        assertThat(gp.getPaint(), CoreMatchers.<Paint>is(new Color(1f, 1f, 1f, .2f)));
        assertThat(gp.getPosition(), is(GlossPainter.GlossPosition.TOP));
    }
}
