/*
 * $Id: CompoundPainterTest.java 3940 2011-03-03 18:29:01Z kschaefe $
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
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.hamcrest.CoreMatchers;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


/**
 * @author rbair
 */
@SuppressWarnings({"nls", "unchecked", "rawtypes"})
public class CompoundPainterTest extends AbstractPainterTest {
    /**
     * {@inheritDoc}
     */
    @Override
    protected CompoundPainter createTestingPainter() {
        return new CompoundPainter();
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
        //TODO why does CompoundPainter start dirty?
        assertThat(p.isDirty(), is(true));
        assertThat(p.isInPaintContext(), is(false));
        assertThat(p.isVisible(), is(true));
        assertThat(p.shouldUseCache(), is(false));
    }
    
    /**
     * {@inheritDoc}
     * <p>
     * Overridden for CompoundPainter defaults.
     */
    @Test
    @Override
    public void testDefaults() {
        //TODO replace with super.testDefaults() when corrected
        copyOfSuper_testDefaultsWithCorrectedValues();
//        super.testDefaults();
        
        CompoundPainter cp = (CompoundPainter) p;
        assertThat(cp.getPainters(), is(new Painter[0]));
        assertThat(cp.getTransform(), is(nullValue()));
        assertThat(cp.isCheckingDirtyChildPainters(), is(true));
        assertThat(cp.isClipPreserved(), is(false));
    }

    /**
     * Issue #497-swingx: setPainters can't cope with null.
     * 
     */
    @Test
    public void testSetNullPainters() {
        CompoundPainter<Object> painter = new CompoundPainter<Object>();
        // changed to cast to Painter, since uncasted it is equivalent to
        // Painter[], which is checked in the next test
        painter.setPainters((Painter<?>) null);
    }
    
    /**
     * Issue #497-swingx: setPainters can't cope with null.
     *
     */
    @Test
    public void testSetEmptyPainters() {
        CompoundPainter<Object> painter = new CompoundPainter<Object>();
        // okay
        painter.setPainters();
        // fails
        painter.setPainters((Painter[]) null);
    }
    
    @Test
    public void testSetttingOnePainterDoesNotEnableCache() {
        ((CompoundPainter) p).setPainters(mock(Painter.class));
        
        assertThat(p.shouldUseCache(), is(false));
    }
    
    @Test
    @Ignore("not sure this is the right thing to do")
    public void testSettingMoreThanOnePainterEnablesCache() {
        ((CompoundPainter) p).setPainters(mock(Painter.class), mock(Painter.class));
        
        assertThat(p.shouldUseCache(), is(true));
    }
    
    /**
     * Issue #1218-swingx: must fire property change if contained painter
     *    changed.
     */
    public void testDirtyNotification() {
        AbstractPainter<Object> child = spy(new DummyPainter());
        ((CompoundPainter<?>) p).setPainters(child);
        
        assertThat(p.isDirty(), is(true));
        verify(child, never()).setDirty(true);
        
        p.paint(g, null, 10, 10);
        
        assertThat(p.isDirty(), is(false));
        
        PropertyChangeListener pcl = mock(PropertyChangeListener.class);
        p.addPropertyChangeListener(pcl);
        
        child.setDirty(true);
        
        assertThat(p.isDirty(), is(true));
        
        ArgumentCaptor<PropertyChangeEvent> captor = ArgumentCaptor.forClass(PropertyChangeEvent.class);
        verify(pcl).propertyChange(captor.capture());
        
        assertThat(captor.getValue().getSource(), CoreMatchers.<Object>is(sameInstance(p)));
        assertThat(captor.getValue().getPropertyName(), is("dirty"));
        assertThat(captor.getAllValues().size(), is(1));
    }
}
