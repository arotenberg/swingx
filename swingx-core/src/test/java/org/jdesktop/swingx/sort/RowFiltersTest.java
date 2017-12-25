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
import java.util.regex.Pattern;

import javax.swing.RowFilter;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.renderer.StringValues;
import org.jdesktop.test.AncientSwingTeam;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Unit and visual tests for RowFilters.
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class RowFiltersTest extends InteractiveTestCase {
    
    private AncientSwingTeam teamModel;
    private int colorColumn;
    private StringValue sv;
    private FilterEntry entry;
    
    public static void main(String[] args) {
        RowFiltersTest test = new RowFiltersTest();
        try {
            test.runInteractiveTests();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testIllegalArgumentOnNegative() {
        RowFilters.regexFilter(0, "some", -1);
    }
    
    @Test(expected = NullPointerException.class)
    public void testNPEOnNullRegex() {
        RowFilters.regexFilter(0, null);
    }
    
    @Test(expected = NullPointerException.class)
    public void testNPEOnNullPattern() {
        RowFilters.regexFilter((Pattern) null);
    }
    
    @Test
    public void testCaseFlagAndColumn() {
        RowFilter<TableModel, Integer> filter = RowFilters.regexFilter(Pattern.CASE_INSENSITIVE, "jeff", 0);
        entry.modelIndex = 3;
        assertTrue(filter.include(entry));
    }
    @Test
    public void testCaseFlagAndColumnNoMatch() {
        RowFilter<TableModel, Integer> filter = RowFilters.regexFilter(Pattern.CASE_INSENSITIVE, "jeff", 1);
        entry.modelIndex = 3;
        assertFalse(filter.include(entry));
    }
    @Test
    public void testCaseFlag() {
        RowFilter<TableModel, Integer> filter = RowFilters.regexFilter(Pattern.CASE_INSENSITIVE, "jeff");
        entry.modelIndex = 3;
        assertTrue(filter.include(entry));
    }
    
    
    @Test
    public void testDefaultCaseNoMatch() {
        RowFilter<TableModel, Integer> filter = RowFilters.regexFilter(0, "jeff");
        entry.modelIndex = 3;
        assertFalse(filter.include(entry));
    }
    
    @Test
    public void testDefaultCaseMatch() {
        RowFilter<TableModel, Integer> filter = RowFilters.regexFilter("Jeff");
        entry.modelIndex = 3;
        assertTrue(filter.include(entry));
    }
    
    @Test
    public void testExist() {
        RowFilters.regexFilter("sometext");
        RowFilters.regexFilter("some", 0, 1, 3);
        RowFilters.regexFilter(Pattern.compile("text"), 0, 5);
        RowFilters.regexFilter(Pattern.compile("soem"));
        RowFilters.regexFilter(0, "some");
        RowFilters.regexFilter(0, "some", 1);
    }

    public void interactiveDebug() {
        JXTable table = new JXTable(teamModel);
        table.getColumn(colorColumn).setCellRenderer(new DefaultTableRenderer(sv));
        JXFrame frame = showWithScrollingInFrame(table, "row filters");
        show(frame);
    }
    
    private class FilterEntry extends RowFilter.Entry<TableModel, Integer> {
        /**
         * The index into the model, set in getFilterEntry
         */
        int modelIndex;

        @Override
        public TableModel getModel() {
            return teamModel;
        }

        @Override
        public int getValueCount() {
            return getModel().getColumnCount();
        }

        @Override
        public Object getValue(int index) {
            return getModel().getValueAt(modelIndex, index);
        }

        @Override
        public String getStringValue(int index) {
            Object value = getValue(index);
            if (index == colorColumn) {
                return sv.getString(value);
            }
            return value != null ? value.toString() : "";
        }

        @Override
        public Integer getIdentifier() {
            return modelIndex;
        }
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

    @Before
    @Override
    public void setUp() throws Exception {
        teamModel = new AncientSwingTeam();
        colorColumn = 2;
        sv = createColorStringValue();
        entry = new FilterEntry();
    }

}
