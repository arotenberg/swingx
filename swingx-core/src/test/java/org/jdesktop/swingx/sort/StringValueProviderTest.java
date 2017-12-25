/*
 * $Id$
 *
 * Copyright 2009 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx.sort;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.renderer.StringValues;
import org.jdesktop.test.AncientSwingTeam;
import org.jdesktop.test.AncientSwingTeam.NamedColor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Units tests for implemenations of StringValueProvider. 
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class StringValueProviderTest extends InteractiveTestCase {
    /**
     * A custom StringValue for Color. Maps to a string composed of the
     * prefix "R/G/B: " and the Color's rgb value.
     */
    private StringValue sv;

    /** writable version */
    private StringValueRegistry registry;
    /** readable version, same as writable - used for interface testing.*/
    private StringValueProvider provider;
    
    private TableModel teamModel;
    private int column;
    
    
    /**
     * Test registry: clear all per-column registrations
     */
    @Test
    public void testClearColumnStringValues() {
        registry.setStringValue(sv, column);
        registry.clearColumnStringValues();
        assertEquals(registry.getStringValue(-1, -1), registry.getStringValue(-1, column));
    }
    /**
     * Test registry: by class - null removes.
     */
    @Test
    public void testPerClassNullRemoves() {
        registry.setStringValue(sv, NamedColor.class);
        registry.setStringValue(null, NamedColor.class);
        registry.setStringValue(null, Number.class);
        assertNull(registry.getStringValue(NamedColor.class));
    }
    /**
     * Test registry: by class (direct class registered)
     */
    @Test
    public void testPerClass() {
        registry.setStringValue(sv, NamedColor.class);
        assertEquals(sv, registry.getStringValue(-1, column));
    }
    /**
     * Test registry: by class (super-class registered)
     */
    @Test
    public void testPerClassSuper() {
        registry.setStringValue(sv, Color.class);
        assertEquals(sv, registry.getStringValue(-1, column));
    }
    
    /**
     * Test registry: per interface class registered, must at least fall back to Object.class 
     *    (JXTable behaviour)
     */
    @Test
    public void testPerClassInterface() {
        installPerClass(registry, Object.class, Icon.class);
        registry.setStringValue(sv, ImageIcon.class);
        registry.setStringValue(StringValues.EMPTY, Object.class);
        assertEquals(StringValues.EMPTY, registry.getStringValue(-1, 1));
    }
    
    /**
     * Test registry: no per-column classes must not throw, fall back to default
     */
    @Test
    public void testPerClassEmptyClasses() {
        registry.setColumnClasses(null);
        registry.setStringValue(sv, Color.class);
        assertEquals(registry.getStringValue(-1, -1), registry.getStringValue(-1, column));
    }
    
    
    /**
     * Test registry: by column
     */
    @Test
    public void testPerColumn() {
        registry.setStringValue(sv, column);
        assertEquals(sv, registry.getStringValue(-1, 2));
    }
    
    
    /**
     * Test contract: return value must not be null.
     */
    @Test
    public void testEmptyClass() {
        StringValue s = provider.getStringValue(0, column);
        assertNotNull("converter must not be null", s);
    }
    
    /**
     * Test contract: return value must not be null.
     */
    @Test
    public void testEmptyPositive() {
        StringValue s = provider.getStringValue(0, column);
        assertNotNull("converter must not be null", s);
    }
    /**
     * Test contract: must accept negative coordinates.
     */
    @Test
    public void testNegative() {
        StringValue s = provider.getStringValue(-1, -1);
        assertNotNull("converter must not be null", s);
    }
    
    /**
     * Creates and returns a StringValue which maps a Color to it's R/G/B rep, 
     * prepending "R/G/B: "
     * 
     * @return the StringValue for color.
     */
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
     * @param registry2
     * @param teamModel2
     */
    private void initColumnClasses(StringValueRegistry registry,
            TableModel model) {
        Map<Integer, Class<?>> classPerColumn = new HashMap<Integer, Class<?>>();
        for (int i = 0; i < model.getColumnCount(); i++) {
            if (!Object.class.equals(model.getColumnClass(i))) {
                classPerColumn.put(i, model.getColumnClass(i));
            }
        }
        registry.setColumnClasses(classPerColumn);
    }

    /**
     * @param registry2
     * @param class1
     */
    private void installPerClass(StringValueRegistry registry,
            Class<?>... clazz ) {
        Map<Integer, Class<?>> classPerColumn = new HashMap<Integer, Class<?>>();
        for (int i = 0; i < clazz.length; i++) {
            classPerColumn.put(i, clazz[i]);
        }
        registry.setColumnClasses(classPerColumn);
    }

    @Before
    @Override
    public void setUp() throws Exception {
        teamModel = new AncientSwingTeam();
        column = 2;
        sv = createColorStringValue();
        registry = new StringValueRegistry();
        initColumnClasses(registry, teamModel);
        provider = registry;
    }

}
