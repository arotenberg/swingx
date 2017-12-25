/*
 * $Id: ComponentAdapterClientTest.java 3458 2009-08-21 09:34:01Z kleopatra $
 *
 * Copyright 2007 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx.decorator;

import java.awt.Color;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.decorator.ComponentAdapterTest.JXTableT;
import org.jdesktop.swingx.decorator.ComponentAdapterTest.JXTreeT;
import org.jdesktop.swingx.decorator.ComponentAdapterTest.JXTreeTableT;
import org.jdesktop.swingx.renderer.DefaultListRenderer;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.DefaultTreeRenderer;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.renderer.StringValues;
import org.jdesktop.swingx.test.ComponentTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.jdesktop.test.AncientSwingTeam;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Testing clients of ComponentAdapter, mainly clients which rely on uniform string 
 * representation across functionality. Not the optimal location, but where would
 * that be? 
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class ComponentAdapterClientTest extends InteractiveTestCase {

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(ComponentAdapterClientTest.class.getName());
    public static void main(String[] args) {
        ComponentAdapterClientTest test = new ComponentAdapterClientTest();
        try {
            test.runInteractiveTests();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * A custom StringValue for Color. Maps to a string composed of the
     * prefix "R/G/B: " and the Color's rgb value.
     */
    private StringValue sv;

    @Before
    public void setUpJ4() throws Exception {
        setUp();
    }
    
    @After
    public void tearDownJ4() throws Exception {
        tearDown();
    }

    /**
     * Issue #1156-swingx: sort must use comparable
     * 
     * visually check that the custom string value for the color column is
     * used for sorting.
     */
    public void interactiveTableSortComparableAndStringValue() {
        JXTable table = new JXTable(new AncientSwingTeam());
        table.setDefaultRenderer(Color.class, new DefaultTableRenderer(sv));
        
        JTable core = new JTable(table.getModel());
        core.setAutoCreateRowSorter(true);
        JXFrame frame = wrapWithScrollingInFrame(table, core, "JXTable <--> JTable: Compare sorting of color column");
        
        show(frame);
    }


    /**
     * Issue #767-swingx: consistent string representation.
     * 
     * used in find/highlight
     */
    public void interactiveTableGetStringUsedInFind() {
        JXTable table = new JXTable(new AncientSwingTeam());
        table.setDefaultRenderer(Color.class, new DefaultTableRenderer(sv));
        HighlightPredicate predicate = new PatternPredicate("R/G/B: -2", 2, 2);
        table.addHighlighter(new ColorHighlighter(predicate, null, Color.RED));
        table.setColumnControlVisible(true);
        
        JXFrame frame = wrapWithScrollingInFrame(table, "Find/Highlight use adapter string value");
        addSearchModeToggle(frame);
        addMessage(frame, "Press ctrl-F to open search widget");
        show(frame);
    }

    /**
     * Issue #767-swingx: consistent string representation.
     * 
     * used in find/highlight
     */
    public void interactiveListGetStringUsedInFind() {
        JXList table = new JXList(AncientSwingTeam.createNamedColorListModel());
        table.setCellRenderer(new DefaultListRenderer(sv));
        HighlightPredicate predicate = new PatternPredicate("R/G/B: -2", 2, 2);
        table.addHighlighter(new ColorHighlighter(predicate, null, Color.RED));
        JXFrame frame = wrapWithScrollingInFrame(table, "Find/Highlight use adapter string value");
        addSearchModeToggle(frame);
        addMessage(frame, "Press ctrl-F to open search widget");
        show(frame);
    }

    /**
     * Issue #767-swingx: consistent string representation.
     * 
     * used in find/highlight
     */
    public void interactiveTreeGetStringUsedInFind() {
        JXTree table = new JXTree(AncientSwingTeam.createNamedColorTreeModel());
        table.setCellRenderer(new DefaultTreeRenderer(sv));
        HighlightPredicate predicate = new PatternPredicate("R/G/B: -2", 2, 2);
        table.addHighlighter(new ColorHighlighter(predicate, null, Color.RED));
        JXFrame frame = wrapWithScrollingInFrame(table, "Find/Highlight use adapter string value");
        addSearchModeToggle(frame);
        addMessage(frame, "Press ctrl-F to open search widget");
        show(frame);
    }
    
//--------------- unit tests

    /**
     * Issue #979-swingx: JXTreeTable broken string rep of hierarchical column
     * 
     * The breakage is visible in models with 
     * (node.toString) != (value for hierarchical column). 
     */
    @Test
    public void testTreeTableGetStringAtClippedTextRenderer() {
        JPanel panel = new JPanel();
        JButton button = new JButton();
        String buttonName = "buttonName";
        button.setName(buttonName);
        panel.add(button);
        TreeTableModel model = new ComponentTreeTableModel(panel);
        JXTreeTableT table = new JXTreeTableT(model);
        table.setRootVisible(true);
        table.expandAll();
        assertEquals("string rep must be button name", table.getValueAt(1, 0),  table.getStringAt(1, 0));
    }
    
    /**
     * Issue #979-swingx: JXTreeTable broken string rep of hierarchical column
     * 
     * here: test search (accidentally passing because node is instanceof NamedColor
     * with its toString the same as the value returned for the hierarchical column)
     */
    @Test
    public void testTreeTableGetStringUsedInSearchClippedTextRenderer() {
        JXTreeTableT table = new JXTreeTableT(AncientSwingTeam.createNamedColorTreeTableModel());
        table.expandAll();
        String text = table.getStringAt(2, 0);
        int matchRow = table.getSearchable().search(text);
        assertEquals(2, matchRow);
    }


    /**
     * Issue #979-swingx: JXTreeTable broken string rep of hierarchical column
     * 
     * here: test highlight (accidentally passing because node is instanceof NamedColor
     * with its toString the same as the value returned for the hierarchical column)
     */
    @Test
    public void testTreeTableGetStringUsedInPatternPredicateClippedTextRenderer() {
        JXTreeTableT table = new JXTreeTableT(AncientSwingTeam.createNamedColorTreeTableModel());
        int matchRow = 2;
        int matchColumn = 0;
        String text = table.getStringAt(matchRow, matchColumn);
        ComponentAdapter adapter = table.getComponentAdapter(matchRow, matchColumn);
        HighlightPredicate predicate = new PatternPredicate(text, matchColumn, PatternPredicate.ALL);
        assertTrue(predicate.isHighlighted(null, adapter));
    }

    /**
     * Issue #821-swingx: JXTreeTable broken string rep of hierarchical column
     * 
     * here: test highlight
     */
    @Test
    public void testTreeTableGetStringUsedInPatternPredicate() {
        JXTreeTableT table = new JXTreeTableT(AncientSwingTeam.createNamedColorTreeTableModel());
        table.setTreeCellRenderer(new DefaultTreeRenderer(sv));
        int matchRow = 2;
        int matchColumn = 0;
        String text = sv.getString(table.getValueAt(matchRow, matchColumn));
        ComponentAdapter adapter = table.getComponentAdapter(matchRow, matchColumn);
        HighlightPredicate predicate = new PatternPredicate(text, matchColumn, PatternPredicate.ALL);
        assertTrue(predicate.isHighlighted(null, adapter));
    }

    /**
     * Issue #821-swingx: JXTreeTable broken string rep of hierarchical column
     * 
     * here: test search
     */
    @Test
    public void testTreeTableGetStringUsedInSearch() {
        JXTreeTableT table = new JXTreeTableT(AncientSwingTeam.createNamedColorTreeTableModel());
        table.setTreeCellRenderer(new DefaultTreeRenderer(sv));
        String text = sv.getString(table.getValueAt(2, 0));
        int matchRow = table.getSearchable().search(text);
        assertEquals(2, matchRow);
    }



    /**
     * Issue #767-swingx: consistent string representation.
     * 
     * Here: test TableSearchable uses getStringXX
     */
    @Test
    public void testTreeGetStringAtUsedInSearch() {
        JXTreeT tree = new JXTreeT(AncientSwingTeam.createNamedColorTreeModel());
        tree.expandAll();
        tree.setCellRenderer(new DefaultTreeRenderer(sv));
        String text = sv.getString(((DefaultMutableTreeNode) tree.getPathForRow(2).getLastPathComponent()).getUserObject());
        int matchRow = tree.getSearchable().search(text);
        assertEquals(2, matchRow);
    }


    /**
     * Issue #767-swingx: consistent string representation.
     * 
     * Here: test TableSearchable uses getStringXX
     */
    @Test
    public void testListGetStringUsedInSearch() {
        JXList table = new JXList(AncientSwingTeam.createNamedColorListModel());
        table.setCellRenderer(new DefaultListRenderer(sv));
        String text = sv.getString(table.getElementAt(2));
        int matchRow = table.getSearchable().search(text);
        assertEquals(2, matchRow);
    }


    
    /**
     * Issue #767-swingx: consistent string representation.
     * 
     * Here: test TableSearchable uses getStringXX
     */
    @Test
    public void testTableGetStringUsedInSearch() {
        JXTable table = new JXTable(new AncientSwingTeam());
        table.setDefaultRenderer(Color.class, new DefaultTableRenderer(sv));
        String text = sv.getString(table.getValueAt(2, 2));
        int matchRow = table.getSearchable().search(text);
        assertEquals(2, matchRow);
    }

 
    /**
     * Issue #767-swingx: consistent string representation.
     * 
     * Here: test SearchPredicate uses getStringXX.
     */
    @Test
    public void testTableGetStringUsedInSearchPredicate() {
        JXTableT table = new JXTableT(new AncientSwingTeam());
        table.setDefaultRenderer(Color.class, new DefaultTableRenderer(sv));
        int matchRow = 3;
        int matchColumn = 2;
        String text = sv.getString(table.getValueAt(matchRow, matchColumn));
        ComponentAdapter adapter = table.getComponentAdapter(matchRow, matchColumn);
        SearchPredicate predicate = new SearchPredicate(text, matchRow, matchColumn);
        assertTrue(predicate.isHighlighted(null, adapter));
    }

    /**
     * Issue #767-swingx: consistent string representation.
     * 
     * Here: test PatternPredicate uses getStringxx().
     */
    @Test
    public void testTableGetStringUsedInPatternPredicate() {
        JXTableT table = new JXTableT(new AncientSwingTeam());
        table.setDefaultRenderer(Color.class, new DefaultTableRenderer(sv));
        int matchRow = 3;
        int matchColumn = 2;
        String text = sv.getString(table.getValueAt(matchRow, matchColumn));
        ComponentAdapter adapter = table.getComponentAdapter(matchRow, matchColumn);
        HighlightPredicate predicate = new PatternPredicate(text, matchColumn, PatternPredicate.ALL);
        assertTrue(predicate.isHighlighted(null, adapter));
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

    @Override
    protected void setUp() throws Exception {
        sv = createColorStringValue();
    }
    
    
}
