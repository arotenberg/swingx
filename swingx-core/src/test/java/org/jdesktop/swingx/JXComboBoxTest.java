/*
 * $Id: JXComboBoxTest.java 4002 2011-05-02 19:47:20Z kschaefe $
 *
 * Copyright 2010 Sun Microsystems, Inc., 4150 Network Circle,
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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.awt.Color;

import javax.swing.ListCellRenderer;

import org.jdesktop.swingx.JXComboBox.DelegatingRenderer;
import org.jdesktop.swingx.JXListTest.CustomDefaultRenderer;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.ComponentAdapterTest.JXListT;
import org.jdesktop.swingx.renderer.DefaultListRenderer;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.renderer.StringValues;
import org.jdesktop.swingx.sort.StringValueRegistry;
import org.jdesktop.test.AncientSwingTeam;
import org.jdesktop.test.EDTRunner;
import org.jdesktop.test.PropertyChangeReport;
import org.jdesktop.test.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Unit tests for JXComboBox.
 * 
 * @author kschaefer
 */
@RunWith(EDTRunner.class)
public class JXComboBoxTest {
    /**
     * test convenience method accessing the configured adapter.
     */
    @Test
    public void testConfiguredComponentAdapter() {
        JXComboBox combo = new JXComboBox(new Object[] {1, 2, 3});
        ComponentAdapter adapter = combo.getComponentAdapter();
        
        assertThat(adapter.column, is(0));
        assertThat(adapter.row, is(0));
        
        // corrupt adapter
        adapter.row = 1;
        adapter.column = 1;
        
        adapter = combo.getComponentAdapter(0);
        
        assertThat(adapter.column, is(0));
        assertThat(adapter.row, is(0));
    }

    /**
     * test that swingx renderer is used by default.
     */
    @Test
    public void testDefaultListRenderer() {
        JXComboBox combo = new JXComboBox();
        
        ListCellRenderer renderer = ((DelegatingRenderer) combo.getRenderer()).getDelegateRenderer();
        assertThat(renderer, is(instanceOf(DefaultListRenderer.class)));
    }

    /**
     * Delegating renderer must create combo box's default.
     * <p>
     * Delegating has default from combo box initially, here: default default.
     */
    @Test
    public void testDelegatingRendererUseDefault() {
        JXComboBox combo = new JXComboBox();
        
        ListCellRenderer defaultRenderer = combo.createDefaultCellRenderer();
        assertThat(defaultRenderer, is(instanceOf(DefaultListRenderer.class)));
        
        DelegatingRenderer renderer = (DelegatingRenderer) combo.getRenderer();
        assertThat(renderer.getDelegateRenderer(), is(instanceOf(defaultRenderer.getClass())));
    }

    /**
     * Delegating renderer must create combo box's default.
     * <p>
     * Delegating has default from combo box initially, here: custom default.
     */
    @Test
    public void testDelegatingRendererUseCustomDefault() {
        JXComboBox combo = new JXComboBox() {
            @Override
            protected ListCellRenderer createDefaultCellRenderer() {
                return new CustomDefaultRenderer();
            }
        };
        
        ListCellRenderer defaultRenderer = combo.createDefaultCellRenderer();
        assertThat(defaultRenderer, is(instanceOf(CustomDefaultRenderer.class)));
        
        ListCellRenderer renderer = ((DelegatingRenderer) combo.getRenderer()).getDelegateRenderer();
        assertThat(renderer, is(instanceOf(CustomDefaultRenderer.class)));
    }
    
    /**
     * Delegating renderer must create combo box's default.
     * <p>
     * Delegating has default from combo box initially, here: default default.
     */
    @Test
    public void testDelegatingRendererUseDefaultSetNull() {
        JXComboBox combo = new JXComboBox();
        
        ListCellRenderer defaultRenderer = combo.createDefaultCellRenderer();
        DelegatingRenderer renderer = (DelegatingRenderer) combo.getRenderer();
        combo.setRenderer(null);
        
        assertThat(renderer.getDelegateRenderer(), is(instanceOf(defaultRenderer.getClass())));
    }

    /**
     * Delegating renderer must create combo box's default.
     * <p>
     * Delegating has default from list initially, here: custom default.
     */
    @Test
    public void testDelegatingRendererUseCustomDefaultSetNull() {
        JXComboBox combo = new JXComboBox() {
            @Override
            protected ListCellRenderer createDefaultCellRenderer() {
                return new CustomDefaultRenderer();
            }
        };
        
        ListCellRenderer defaultRenderer = combo.createDefaultCellRenderer();
        DelegatingRenderer renderer = (DelegatingRenderer) combo.getRenderer();
        combo.setRenderer(null);
        
        assertThat(renderer.getDelegateRenderer(), is(instanceOf(defaultRenderer.getClass())));
    }

    /**
     * JXComboBox must fire property change on setRenderer.
     */
    @Test
    public void testRendererNotification() {
        JXComboBox combo = new JXComboBox();
        
        assertThat(combo.getRenderer(), is(notNullValue()));

        PropertyChangeReport report = new PropertyChangeReport(combo);
        
        // very first setting: fires twice ... a bit annoying but ... waiting for complaints ;-)
        combo.setRenderer(new DefaultListRenderer());
        TestUtils.assertPropertyChangeEvent(report, "renderer", null, combo.getRenderer());
    }
    
    /**
     * Delegating renderer must create list's default.
     * <p>
     * Consistent API: expose wrappedRenderer the same way as wrappedModel
     */
    @Test
    public void testWrappedRendererDefault() {
        JXComboBox combo = new JXComboBox();
        
        DelegatingRenderer renderer = (DelegatingRenderer) combo.getRenderer();
        assertThat(renderer.getDelegateRenderer(), is(sameInstance(combo.getWrappedRenderer())));
    }

    /**
     * Delegating renderer must create list's default.
     * <p>
     * Consistent API: expose wrappedRenderer the same way as wrappedModel
     */
    @Test
    public void testWrappedRendererCustom() {
        JXComboBox combo = new JXComboBox();
        
        ListCellRenderer custom = new DefaultListRenderer();
        combo.setRenderer(custom);
        
        assertThat(custom, is(sameInstance(combo.getWrappedRenderer())));
    }
    
    private StringValue createColorStringValue() {
        StringValue sv = new StringValue() {

            public String getString(Object value) {
                if (value instanceof Color) {
                    Color color = (Color) value;
                    return "R/G/B: " + color.getRGB();
                }
                return StringValues.TO_STRING.getString(value);
            }
            
        };
        return sv;
    }
    
    /**
     * Issue #1382-swingx: use StringValueRegistry and supply getStringAt.
     * <p>
     * Here: test updates on renderer change.
     */
    @Test
    public void testStringValueRegistryFromRendererChange() {
        JXComboBox combo = new JXComboBox(AncientSwingTeam.createNamedColorComboBoxModel());
        StringValueRegistry provider = combo.getStringValueRegistry();
        combo.setRenderer(new DefaultListRenderer(createColorStringValue()));
        assertEquals(combo.getWrappedRenderer(), provider.getStringValue(0, 0));
    }

    /**
     * Issue #1382-swingx: use StringValueRegistry and supply getStringAt.
     * 
     * Here: test getStringAt use provider (sanity, trying to pull the rag failed
     * during re-enable)
     */
    @Test
    public void testStringAtUseProvider() {
        JXComboBox combo = new JXComboBox(AncientSwingTeam.createNamedColorComboBoxModel());
        combo.setRenderer(new DefaultListRenderer(createColorStringValue()));
        combo.getStringValueRegistry().setStringValue(StringValues.TO_STRING, 0);
        assertEquals(StringValues.TO_STRING.getString(combo.getItemAt(0)),
                combo.getStringAt(0));
        
    }
    /**
     * Issue #1382-swingx: use StringValueRegistry and supply getStringAt.
     * 
     * Here: test getStringAt of ComponentAdapter use provider 
     * (sanity, trying to pull the rag failed during re-enable)
     */
    @Test
    public void testStringAtComponentAdapterUseProvider() {
        JXComboBox combo = new JXComboBox(AncientSwingTeam.createNamedColorComboBoxModel());
        combo.setRenderer(new DefaultListRenderer(createColorStringValue()));
        combo.getStringValueRegistry().setStringValue(StringValues.TO_STRING, 0);
        ComponentAdapter adapter = combo.getComponentAdapter();
        assertEquals(StringValues.TO_STRING.getString(combo.getItemAt(0)),
                adapter.getStringAt(0, 0));
        
    }
}
