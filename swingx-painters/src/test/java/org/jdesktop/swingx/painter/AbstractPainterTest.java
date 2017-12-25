/*
 * $Id: AbstractPainterTest.java 4077 2011-11-11 21:28:11Z kschaefe $
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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Graphics2D;
import java.awt.image.BufferedImageOp;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

/**
 * Test for AbstractPainter
 */
@SuppressWarnings({"nls", "unchecked", "rawtypes"})
public class AbstractPainterTest {
    protected AbstractPainter p;
    protected Graphics2D g;

    @Before
    public void setUp() {
        p = spy(createTestingPainter());
        g = mock(Graphics2D.class);
    }

    protected AbstractPainter createTestingPainter() {
        return new DummyPainter();
    }
    
    @Test
    public void testDefaults() {
        assertThat(p.getFilters().length, is(0));
        assertThat(p.getInterpolation(), is(AbstractPainter.Interpolation.NearestNeighbor));
        assertThat(p.isAntialiasing(), is(true));
        assertThat(p.isCacheable(), is(false));
        assertThat(p.isCacheCleared(), is(true));
        assertThat(p.isDirty(), is(false));
        assertThat(p.isInPaintContext(), is(false));
        assertThat(p.isVisible(), is(true));
        assertThat(p.shouldUseCache(), is(p.isCacheable()));
    }
    
    /**
     * Ensure a {@link NullPointerException} is thrown with a {@code null} graphics object.
     */
    @Test(expected=NullPointerException.class)
    public void testPaintWithNullGraphics() {
        p.paint(null, null, 10, 10);
    }

    /**
     * {@link AbstractPainter} will pass {@code null} objects to
     * {@link AbstractPainter#doPaint(Graphics2D, Object, int, int) doPaint}.
     */
    @Test
    public void testPaintWithNullObject() {
        p.paint(g, null, 10, 10);
        
        if (p.isCacheable()) {
            verify(p).doPaint(any(Graphics2D.class), isNull(), eq(10), eq(10));
        } else {
            verify(p).doPaint(g, null, 10, 10);
        }
    }
    
    /**
     * {@link AbstractPainter} will pass any object to
     * {@link AbstractPainter#doPaint(Graphics2D, Object, int, int) doPaint}.
     */
    @Test
    public void testPaintWithAnyObject() {
        p.paint(g, "yo", 10, 10);
        
        if (p.isCacheable()) {
            verify(p).doPaint(any(Graphics2D.class), eq("yo"), eq(10), eq(10));
        } else {
            verify(p).doPaint(g, "yo", 10, 10);
        }
        
        p.clearCache();
        p.paint(g, 1f, 10, 10);
        
        if (p.isCacheable()) {
            verify(p).doPaint(any(Graphics2D.class), eq(1f), eq(10), eq(10));
        } else {
            verify(p).doPaint(g, 1f, 10, 10);
        }
    }
    
    /**
     * Ensure that no painting occurs if width and/or height is <= 0.
     */
    @Test
    public void testNoPaintWithNonPositiveDimension() {
        p.paint(g, null, 0, 10);
        p.paint(g, null, 10, 0);
        p.paint(g, null, -1, 10);
        p.paint(g, null, 10, -1);
        p.paint(g, null, 0, 0);
        
        verify(p, never()).doPaint(any(Graphics2D.class), any(Object.class), anyInt(), anyInt());
    }
    
    /**
     * Ensure that visibility settings correctly alter painting behavior.
     */
    @Test
    public void testPaintAndVisibility() {
        p.setVisible(false);
        p.paint(g, null, 10, 10);
        verify(p, never()).doPaint(g, null, 10, 10);
        
        p.setVisible(true);
        testPaintWithNullObject();
    }
    
    /**
     * Ensure that paint orders calls correctly.
     */
    @Test
    public void testInOrderPaintCallsWithoutCaching() {
        when(p.shouldUseCache()).thenReturn(false);
        
        InOrder orderedCalls = inOrder(p);
        p.paint(g, null, 10, 10);
        
        orderedCalls.verify(p).configureGraphics(g);
        orderedCalls.verify(p, times(0)).validate(null);
        orderedCalls.verify(p).doPaint(g, null, 10, 10);
    }
    
    /**
     * Ensure that paint orders calls correctly.
     */
    @Test
    public void testInOrderPaintCallsWithCaching() {
        when(p.shouldUseCache()).thenReturn(true);
        
        InOrder orderedCalls = inOrder(p);
        p.paint(g, null, 10, 10);
        
        orderedCalls.verify(p).configureGraphics(g);
        orderedCalls.verify(p).validate(null);
        //when caching we get a different graphics object
        verify(p).doPaint(any(Graphics2D.class), isNull(), eq(10), eq(10));
    }

    /**
     * Issue #??-swingx: clearCache has no detectable effect. Test was poorly designed. It has had
     * an effect for a long time, but the member is not bound, so the test was failing erroneously.
     */
    @Test
    public void testClearCacheDetectable() {
        p.setCacheable(true);
        p.paint(g, null, 10, 10);
        
        // sanity
        //when caching we get a different graphics object
        verify(p).doPaint(any(Graphics2D.class), isNull(), eq(10), eq(10));
        assertThat("clean after paint", false, is(p.isDirty()));
        assertThat("cacheable is enabled", true, is(p.isCacheable()));
        assertThat("has a cached image", false, is(p.isCacheCleared()));
        
        p.clearCache();
        
        assertThat("has a cached image", true, is(p.isCacheCleared()));
    }
    
    /**
     * Ensures that setting cacheable makes shouldUseCache return true.
     */
    @Test
    public void testSetCacheableEnablesCache() {
        p.setCacheable(true);
        
        assertThat(p.shouldUseCache(), is(true));
    }
    
    /**
     * Ensures that setting filters makes shouldUseCache return true.
     */
    @Test
    public void testFiltersEnableCache() {
        p.setFilters(mock(BufferedImageOp.class));
        
        assertThat(p.shouldUseCache(), is(true));
    }

    /**
     * Ensure that shouldUseCache forces the use of the cache.
     */
    @Test
    public void testShouldUseCacheRepaintsWithCachedCopy() {
        when(p.shouldUseCache()).thenReturn(true);
        
        p.paint(g, null, 10, 10);
        
        //when caching we get a different graphics object
        verify(p, times(1)).doPaint(any(Graphics2D.class), isNull(), eq(10), eq(10));
        
        p.paint(g, null, 10, 10);
        p.paint(g, null, 10, 10);
        p.paint(g, null, 10, 10);
        p.paint(g, null, 10, 10);
        
        //we do not invoke doPaint a subsequent calls
        verify(p, times(1)).doPaint(any(Graphics2D.class), isNull(), eq(10), eq(10));
    }
}
