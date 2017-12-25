/*
 * $Id: ComponentAdapterIssues.java 3650 2010-04-08 15:16:11Z kleopatra $
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
package org.jdesktop.swingx.decorator;

import java.awt.Color;
import java.util.logging.Logger;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.renderer.StringValues;
import org.jdesktop.swingx.search.SearchFactory;
import org.jdesktop.swingx.test.ComponentTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.jdesktop.test.AncientSwingTeam;

/**
 * Test to exposed known issues of <code>ComponentAdapter</code>.
 * 
 * Ideally, there would be at least one failing test method per open
 * Issue in the issue tracker. Plus additional failing test methods for
 * not fully specified or not yet decided upon features/behaviour.
 * 
 * 
 * @author Jeanette Winzenburg
 */
public class ComponentAdapterIssues extends InteractiveTestCase {

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(ComponentAdapterIssues.class.getName());
    public static void main(String[] args) {
        SearchFactory.getInstance().setUseFindBar(true);
        ComponentAdapterIssues test = new ComponentAdapterIssues();
        try {
            test.runInteractiveTests();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    private StringValue sv;
    
    /**
     * Issue ??-swingx: TreeTable doesn't return correct string value for hierarchical column.
     * 
     * This is probably a variant of not using the table's renderer (at least I expect it 
     * to be solved then at the latest). In the meantime, might want to do something special
     * for the hierarchical column?
     */
    public void interactiveTreeTableStringValue() {
        TreeTableModel model = AncientSwingTeam.createNamedColorTreeTableModel();
        JXTreeTable treeTable = new JXTreeTable(model);
        treeTable.setRootVisible(true);
//        treeTable.setTreeCellRenderer(new DefaultTreeRenderer(sv));
        JXTree tree =  new JXTree(model);
//        tree.setCellRenderer(new DefaultTreeRenderer(sv));
        HighlightPredicate predicate = new PatternPredicate("R/G/B: -2", 0, PatternPredicate.ALL);
        ColorHighlighter hl = new ColorHighlighter(predicate, null, Color.RED);
        treeTable.addHighlighter(hl);
        tree.addHighlighter(hl);
        JXFrame frame = wrapWithScrollingInFrame(tree, treeTable, "string rep in hierarchical column");
        show(frame);
    }

    /**
     * Issue ??-swingx: TreeTable doesn't return correct string value for hierarchical column.
     * 
     * This is probably a variant of not using the table's renderer (at least I expect it 
     * to be solved then at the latest). In the meantime, might want to do something special
     * for the hierarchical column?
     */
    public void interactiveTreeTableStringValueComponent() {
        TreeTableModel model = new ComponentTreeTableModel(new JXFrame());
        JXTreeTable treeTable = new JXTreeTable(model);
        treeTable.setRootVisible(true);
        treeTable.expandAll();
        LOG.info(treeTable.getStringAt(3, 0));
//        treeTable.setTreeCellRenderer(new DefaultTreeRenderer(sv));
        JXTree tree =  new JXTree(model);
//        tree.setCellRenderer(new DefaultTreeRenderer(sv));
        HighlightPredicate predicate = new PatternPredicate("null", 0, PatternPredicate.ALL);
        ColorHighlighter hl = new ColorHighlighter(predicate, null, Color.RED);
        treeTable.addHighlighter(hl);
        tree.addHighlighter(hl);
        JXFrame frame = wrapWithScrollingInFrame( treeTable, "string rep in hierarchical column");
        show(frame);
    }

    /**
     * Issue ??-swingx: TreeTable doesn't return correct string value for hierarchical column.
     * 
     * This is probably a variant of not using the table's renderer (at least I expect it 
     * to be solved then at the latest). In the meantime, might want to do something special
     * for the hierarchical column?
     */
    public void interactiveTreeStringValueComponent() {
        TreeTableModel model = new ComponentTreeTableModel(new JXFrame());
        JXTreeTable treeTable = new JXTreeTable(model);
        treeTable.setRootVisible(true);
        treeTable.expandAll();
        LOG.info(treeTable.getStringAt(3, 0));
//        treeTable.setTreeCellRenderer(new DefaultTreeRenderer(sv));
        JXTree tree =  new JXTree(model);
//        tree.setCellRenderer(new DefaultTreeRenderer(sv));
        HighlightPredicate predicate = new PatternPredicate("null", 0, PatternPredicate.ALL);
        ColorHighlighter hl = new ColorHighlighter(predicate, null, Color.RED);
        treeTable.addHighlighter(hl);
        tree.addHighlighter(hl);
        JXFrame frame = wrapWithScrollingInFrame( tree, "string rep in hierarchical column");
        show(frame);
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

    public void testDummy() {
        
    }

}
