/*
 * $Id: ComponentAdapterTest.java 3906 2010-12-15 11:49:23Z kleopatra $
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

import static org.junit.Assert.*;

import java.awt.Color;

import javax.swing.ListModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

import junit.framework.TestCase;

import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.renderer.DefaultListRenderer;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.DefaultTreeRenderer;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.renderer.StringValues;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.jdesktop.test.AncientSwingTeam;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test <code>ComponentAdapter</code>.
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class ComponentAdapterTest extends TestCase {
    
    /**
     * A custom StringValue for Color. Maps to a string composed of the
     * prefix "R/G/B: " and the Color's rgb value.
     */
    private StringValue sv;
    
    @Test
    public void testColumnClassTable() {
        JXTableT table = new JXTableT(new AncientSwingTeam());
        for (int column = 0; column < table.getColumnCount(); column++) {
            ComponentAdapter adapter = table.getComponentAdapter(0, column);
            assertEquals(table.getModel().getColumnClass(column), adapter.getColumnClass());
        }
    }
    
    @Test
    public void testColumnClassByIndexTable() {
        JXTableT table = new JXTableT(new AncientSwingTeam());
        ComponentAdapter adapter = table.getComponentAdapter(0, 0);
        for (int column = 0; column < table.getColumnCount(); column++) {
            assertEquals(table.getModel().getColumnClass(column), adapter.getColumnClass(column));
        }
    }

    @Test
    public void testColumnClass() {
        JXListT list = new JXListT(new Object[] {"one"});
        ComponentAdapter adapter = list.getComponentAdapter(0);
        assertEquals(Object.class, adapter.getColumnClass());
    }
    
    /**
     * Issue #791-swingx: complete coordinate transformation methods - missing id --> index
     * 
     * Tree has no columns/identifiers - implement reasonable default.
     */
    @Test
    public void testColumnIdentifierAtList() {
        JXListT tree = new JXListT(new String[] {"one" });
        ComponentAdapter adapter = tree.getComponentAdapter(0);
        assertEquals(ComponentAdapter.DEFAULT_COLUMN_IDENTIFIER, adapter.getColumnIdentifierAt(0));
    }
    /**
     * Issue #791-swingx: complete coordinate transformation methods - missing id --> index
     * 
     * Here test contract: must throw IllegalArgumentException if invalid index.
     */
    @Test
    public void testColumnIdentifierAtListInvalidIndex() {
        JXListT table =  new JXListT(new String[] {"one" });
        ComponentAdapter adapter = table.getComponentAdapter(0);
        try {
            adapter.getColumnIdentifierAt(adapter.getColumnCount());
            fail("must throw at invalid columnIndex: " + adapter.getColumnCount());
        } catch (ArrayIndexOutOfBoundsException e) {
            // behaves as documented  
        } catch (Exception e) {
            fail("expected ArrayIndexOutOfBounds but was: " + e);
        }
    }
 
    /**
     * Issue #791-swingx: complete coordinate transformation methods - missing id --> index
     * 
     * Here test contract: must throw NPE if nullIdentifier.
     */
    @Test
    public void testColumnIdentifierReverseListNullIdentifier() {
        JXListT table =  new JXListT(new String[] {"one" });
        ComponentAdapter adapter = table.getComponentAdapter(0);
        try {
            adapter.getColumnIndex(null);
            fail("must throw at invalid columnIndex: " + adapter.getColumnCount());
        } catch (NullPointerException e) {
            // behaves as documented  
        } catch (Exception e) {
            fail("expected ArrayIndexOutOfBounds but was: " + e);
        }
    }
    /**
     * Issue #791-swingx: complete coordinate transformation methods - missing id --> index
     * 
     * Tree has no columns/identifiers - implement reasonable default.
     */
    @Test
    public void testColumnIdentifierListReverse() {
        JXListT tree = new JXListT(new String[] {"one" });
        ComponentAdapter adapter = tree.getComponentAdapter(0);
        assertEquals(0, adapter.getColumnIndex(ComponentAdapter.DEFAULT_COLUMN_IDENTIFIER));
    }

    /**
     * Issue #791-swingx: complete coordinate transformation methods - missing id --> index
     * 
     * Tree has no columns/identifiers - implement reasonable default.
     */
    @Test
    public void testColumnIdentifierListNotFound() {
        JXListT tree = new JXListT(new String[] {"one" });
        ComponentAdapter adapter = tree.getComponentAdapter(0);
        assertEquals(-1, adapter.getColumnIndex("unknown"));
    }

    
    /**
     * Issue #791-swingx: complete coordinate transformation methods - missing id --> index
     * 
     * Tree has no columns/identifiers - implement reasonable default.
     */
    @Test
    public void testColumnIdentifierAtTree() {
        JXTreeT tree = new JXTreeT();
        ComponentAdapter adapter = tree.getComponentAdapter(0);
        assertEquals(ComponentAdapter.DEFAULT_COLUMN_IDENTIFIER, adapter.getColumnIdentifierAt(0));
    }
    /**
     * Issue #791-swingx: complete coordinate transformation methods - missing id --> index
     * 
     * Here test contract: must throw IllegalArgumentException if invalid index.
     */
    @Test
    public void testColumnIdentifierAtTreeInvalidIndex() {
        JXTreeT table =  new JXTreeT();
        ComponentAdapter adapter = table.getComponentAdapter(0);
        try {
            adapter.getColumnIdentifierAt(adapter.getColumnCount());
            fail("must throw at invalid columnIndex: " + adapter.getColumnCount());
        } catch (ArrayIndexOutOfBoundsException e) {
            // behaves as documented  
        } catch (Exception e) {
            fail("expected ArrayIndexOutOfBounds but was: " + e);
        }
    }
 
    /**
     * Issue #791-swingx: complete coordinate transformation methods - missing id --> index
     * 
     * Here test contract: must throw NPE if nullIdentifier.
     */
    @Test
    public void testColumnIdentifierReverseTreeNullIdentifier() {
        JXTreeT table =  new JXTreeT();
        ComponentAdapter adapter = table.getComponentAdapter(0);
        try {
            adapter.getColumnIndex(null);
            fail("must throw at invalid columnIndex: " + adapter.getColumnCount());
        } catch (NullPointerException e) {
            // behaves as documented  
        } catch (Exception e) {
            fail("expected ArrayIndexOutOfBounds but was: " + e);
        }
    }
    /**
     * Issue #791-swingx: complete coordinate transformation methods - missing id --> index
     * 
     * Tree has no columns/identifiers - implement reasonable default.
     */
    @Test
    public void testColumnIdentifierTreeReverse() {
        JXTreeT tree = new JXTreeT();
        ComponentAdapter adapter = tree.getComponentAdapter(0);
        assertEquals(0, adapter.getColumnIndex(ComponentAdapter.DEFAULT_COLUMN_IDENTIFIER));
    }

    /**
     * Issue #791-swingx: complete coordinate transformation methods - missing id --> index
     * 
     * Tree has no columns/identifiers - implement reasonable default.
     */
    @Test
    public void testColumnIdentifierTreeNotFound() {
        JXTreeT tree = new JXTreeT();
        ComponentAdapter adapter = tree.getComponentAdapter(0);
        assertEquals(-1, adapter.getColumnIndex("unknown"));
    }
    
    /**
     * Issue #791-swingx: complete coordinate transformation methods - missing id --> index
     * 
     * Here: must return model index for hidden columns
     */
    @Test
    public void testColumnIdentifierReverseHidden() {
        JXTableT table =  new JXTableT(new AncientSwingTeam());
        ComponentAdapter adapter = table.getComponentAdapter(0, 0);
        Object id = new Integer(50);
        table.getColumn(1).setIdentifier(id);
        table.getColumnExt(1).setVisible(false);
        assertEquals(1, adapter.getColumnIndex(id));
    }


    /**
     * Issue #791-swingx: complete coordinate transformation methods - missing id --> index
     * 
     * Here: must return model index
     */
    @Test
    public void testColumnIdentifierReverse() {
        JXTableT table =  new JXTableT(new AncientSwingTeam());
        ComponentAdapter adapter = table.getComponentAdapter(0, 0);
        Object id = new Integer(50);
        table.getColumn(1).setIdentifier(id);
        assertEquals(1, adapter.getColumnIndex(id));
    }

    /**
     * Issue #791-swingx: complete coordinate transformation methods - missing id --> index
     * 
     * Here: must return model index if identifier not known.
     */
    @Test
    public void testColumnIdentifierReverseNotFound() {
        JXTableT table =  new JXTableT(new AncientSwingTeam());
        ComponentAdapter adapter = table.getComponentAdapter(0, 0);
        Object id = new Integer(50);
        table.getColumn(1).setIdentifier(id);
        assertEquals(-1, adapter.getColumnIndex("unknown"));
    }

    /**
     * Issue #791-swingx: complete coordinate transformation methods - missing id --> index
     * 
     * Here test contract: must throw IllegalArgumentException if invalid index.
     */
    @Test
    public void testColumnIdentifierAtInvalidIndex() {
        JXTableT table =  new JXTableT(new AncientSwingTeam());
        ComponentAdapter adapter = table.getComponentAdapter(0, 0);
        try {
            adapter.getColumnIdentifierAt(adapter.getColumnCount());
            fail("must throw at invalid columnIndex: " + adapter.getColumnCount());
        } catch (ArrayIndexOutOfBoundsException e) {
            // behaves as documented  
        } catch (Exception e) {
            fail("expected ArrayIndexOutOfBounds but was: " + e);
        }
    }

    /**
     * Issue #791-swingx: complete coordinate transformation methods - missing id --> index
     * 
     * Here test contract: must throw NPE if Null identifier.
     */
    @Test
    public void testColumnIdentifierReverseNullIdentifier() {
        JXTableT table =  new JXTableT(new AncientSwingTeam());
        ComponentAdapter adapter = table.getComponentAdapter(0, 0);
        try {
            adapter.getColumnIndex(null);
            fail("must throw for null identifier");
        } catch (NullPointerException e) {
            // behaves as documented  
        } catch (Exception e) {
            fail("expected ArrayIndexOutOfBounds but was: " + e);
        }
    }
    /**
     * Issue #791-swingx: complete coordinate transformation methods - missing id --> index
     * 
     * 
     */
    @Test
    public void testColumnIdentifierAt() {
        JXTableT table =  new JXTableT(new AncientSwingTeam());
        ComponentAdapter adapter = table.getComponentAdapter(0, 0);
        Object id = new Integer(50);
        table.getColumn(0).setIdentifier(id);
        assertEquals(table.getColumn(0).getIdentifier(), adapter.getColumnIdentifierAt(0));
        // this fails because the adapter returns a string representation of the identifier
//        assertEquals(table.getColumn(0).getIdentifier(), adapter.getColumnIdentifier(0));
    }

    
    /**
     * Issue #821-swingx: JXTreeTable broken string rep of hierarchical column
     * 
     * here: test ComponentAdapter on hidden hierarchical column
     */
    @Test
    public void testTreeTableGetStringColumnHiddenHierarchicalColumn() {
        JXTreeTableT table = new JXTreeTableT(AncientSwingTeam.createNamedColorTreeTableModel());
        table.setTreeCellRenderer(new DefaultTreeRenderer(sv));
        table.getColumnExt(0).setVisible(false);
        String text = sv.getString(table.getModel().getValueAt(2, 0));
        ComponentAdapter adapter = table.getComponentAdapter(2, 0);
        assertEquals(text, adapter.getString(0));
    }


    /**
     * Issue #821-swingx: JXTreeTable broken string rep of hierarchical column
     * 
     * here: test ComponentAdapter on hidden hierarchical column
     */
    @Test
    public void testTreeTableGetStringAtHiddenHierarchicalColumn() {
        JXTreeTableT table = new JXTreeTableT(AncientSwingTeam.createNamedColorTreeTableModel());
        table.setTreeCellRenderer(new DefaultTreeRenderer(sv));
        table.getColumnExt(0).setVisible(false);
        String text = sv.getString(table.getModel().getValueAt(2, 0));
        ComponentAdapter adapter = table.getComponentAdapter(2, 0);
        assertEquals(text, adapter.getStringAt(2, 0));
    }



    /**
     * Issue #767-swingx: consistent string representation.
     * 
     * Here: test api on tree's ComponentAdapter.
     */
    @Test
    public void testTreeGetStringAt() {
        JXTreeT tree = new JXTreeT(AncientSwingTeam.createNamedColorTreeModel());
        tree.expandAll();
        tree.setCellRenderer(new DefaultTreeRenderer(sv));
        String text = sv.getString(((DefaultMutableTreeNode) tree.getPathForRow(2).getLastPathComponent()).getUserObject());
        ComponentAdapter adapter = tree.getComponentAdapter(2);
        assertEquals(text, adapter.getStringAt(2, 0));
    }

    /**
     * Issue #767-swingx: consistent string representation.
     * 
     * Here: test api on tree's ComponentAdapter.
     */
    @Test
    public void testTreeGetString() {
        JXTreeT tree = new JXTreeT(AncientSwingTeam.createNamedColorTreeModel());
        tree.expandAll();
        tree.setCellRenderer(new DefaultTreeRenderer(sv));
        String text = sv.getString(((DefaultMutableTreeNode) tree.getPathForRow(2).getLastPathComponent()).getUserObject());
        ComponentAdapter adapter = tree.getComponentAdapter(2);
        assertEquals(text, adapter.getString());
    }

    /**
     * Issue #767-swingx: consistent string representation.
     * 
     * Here: test api on tree's ComponentAdapter.
     */
    @Test
    public void testTreeGetFilteredString() {
        JXTreeT tree = new JXTreeT(AncientSwingTeam.createNamedColorTreeModel());
        tree.expandAll();
        tree.setCellRenderer(new DefaultTreeRenderer(sv));
        String text = sv.getString(((DefaultMutableTreeNode) tree.getPathForRow(2).getLastPathComponent()).getUserObject());
        ComponentAdapter adapter = tree.getComponentAdapter(2);
        assertEquals(text, adapter.getFilteredStringAt(2, 0));
    }

    /**
     * Issue #767-swingx: consistent string representation.
     * 
     * Here: test api on tree's ComponentAdapter.
     */
    @Test
    public void testTreeGetStringColumn() {
        JXTreeT tree = new JXTreeT(AncientSwingTeam.createNamedColorTreeModel());
        tree.expandAll();
        tree.setCellRenderer(new DefaultTreeRenderer(sv));
        String text = sv.getString(((DefaultMutableTreeNode) tree.getPathForRow(2).getLastPathComponent()).getUserObject());
        ComponentAdapter adapter = tree.getComponentAdapter(2);
        assertEquals(text, adapter.getString(0));
    }

    /**
     * Subclass to access ComponentAdapter.
     */
    public static class JXTreeT extends JXTree {

        public JXTreeT(TreeModel model) {
            super(model);
        }

        public JXTreeT() {
            super();
        }
        @Override
        public ComponentAdapter getComponentAdapter(int row) {
            return super.getComponentAdapter(row);
        }

    }

    /**
     * Issue #767-swingx: consistent string representation.
     * 
     * Here: test api on list's ComponentAdapter.
     */
    @Test
    public void testListGetStringAtSorted() {
        JXListT list = new JXListT(AncientSwingTeam.createNamedColorListModel(), true);
        list.setCellRenderer(new DefaultListRenderer(sv));
        list.toggleSortOrder();
        String text = sv.getString(list.getModel().getElementAt(2));
        ComponentAdapter adapter = list.getComponentAdapter(2);
        assertEquals(text, adapter.getStringAt(2, 0));
    }

    /**
     * Issue #767-swingx: consistent string representation.
     * 
     * Here: test api on list's ComponentAdapter.
     */
    @Test
    public void testListGetStringAtUnsorted() {
        JXListT list = new JXListT(AncientSwingTeam.createNamedColorListModel());
        list.setCellRenderer(new DefaultListRenderer(sv));
        String text = sv.getString(list.getElementAt(2));
        ComponentAdapter adapter = list.getComponentAdapter(2);
        assertEquals(text, adapter.getStringAt(2, 0));
    }

    /**
     * Issue #767-swingx: consistent string representation.
     * 
     * Here: test api on list's ComponentAdapter.
     */
    @Test
    public void testListGetString() {
        JXListT list = new JXListT(AncientSwingTeam.createNamedColorListModel());
        list.setCellRenderer(new DefaultListRenderer(sv));
        String text = sv.getString(list.getElementAt(2));
        ComponentAdapter adapter = list.getComponentAdapter(2);
        assertEquals(text, adapter.getString());
    }

    /**
     * Issue #767-swingx: consistent string representation.
     * 
     * Here: test api on list's ComponentAdapter.
     */
    @Test
    public void testListGetFilteredString() {
        JXListT list = new JXListT(AncientSwingTeam.createNamedColorListModel());
        list.setCellRenderer(new DefaultListRenderer(sv));
        String text = sv.getString(list.getElementAt(2));
        ComponentAdapter adapter = list.getComponentAdapter(2);
        assertEquals(text, adapter.getFilteredStringAt(2, 0));
    }

    /**
     * Issue #767-swingx: consistent string representation.
     * 
     * Here: test api on list's ComponentAdapter.
     */
    @Test
    public void testListGetStringColumn() {
        JXListT list = new JXListT(AncientSwingTeam.createNamedColorListModel());
        list.setCellRenderer(new DefaultListRenderer(sv));
        String text = sv.getString(list.getElementAt(2));
        ComponentAdapter adapter = list.getComponentAdapter(2);
        assertEquals(text, adapter.getString(0));
    }

    /**
     * Subclass to access ComponentAdapter.
     */
    public static class JXListT extends JXList {

        public JXListT(ListModel model) {
            super(model);
        }
        public JXListT(ListModel model, boolean autoCreateRowSorter) {
            super(model, autoCreateRowSorter);
        }

        public JXListT(Object[] data) {
            super(data);
        }
        @Override
        public ComponentAdapter getComponentAdapter(int row) {
            return super.getComponentAdapter(row);
        }

    }
 
    /**
     * Issue #767-swingx: consistent string representation.
     * 
     * Here: test api on table's ComponentAdapter.
     */
    @Test
    public void testTableGetStringColumnHiddenColumn() {
        JXTableT table = new JXTableT(new AncientSwingTeam());
        table.setDefaultRenderer(Color.class, new DefaultTableRenderer(sv));
        table.getColumnExt(2).setVisible(false);
        String text = sv.getString(table.getModel().getValueAt(2, 2));
        ComponentAdapter adapter = table.getComponentAdapter(2, 2);
        assertEquals(text, adapter.getString(2));
    }


    /**
     * Issue #767-swingx: consistent string representation.
     * 
     * Here: test api on table's ComponentAdapter.
     */
    @Test
    public void testTableGetStringAtHiddenColumn() {
        JXTableT table = new JXTableT(new AncientSwingTeam());
        table.setDefaultRenderer(Color.class, new DefaultTableRenderer(sv));
        table.getColumnExt(2).setVisible(false);
        String text = sv.getString(table.getModel().getValueAt(2, 2));
        ComponentAdapter adapter = table.getComponentAdapter(2, 2);
        assertEquals(text, adapter.getStringAt(2, 2));
    }


    /**
     * Issue #767-swingx: consistent string representation.
     * 
     * Here: test api on table's ComponentAdapter.
     */
    @Test
    public void testTableGetStringAtSorted() {
        JXTableT table = new JXTableT(new AncientSwingTeam());
        table.setDefaultRenderer(Color.class, new DefaultTableRenderer(sv));
        table.toggleSortOrder(2);
        String text = sv.getString(table.getModel().getValueAt(2, 2));
        ComponentAdapter adapter = table.getComponentAdapter(2, 2);
        assertEquals(text, adapter.getStringAt(2, 2));
    }

   
    /**
     * Issue #767-swingx: consistent string representation.
     * 
     * Here: test api on table's ComponentAdapter.
     */
    @Test
    public void testTableGetStringAtUnsorted() {
        JXTableT table = new JXTableT(new AncientSwingTeam());
        table.setDefaultRenderer(Color.class, new DefaultTableRenderer(sv));
        String text = sv.getString(table.getValueAt(2, 2));
        ComponentAdapter adapter = table.getComponentAdapter(2, 2);
        assertEquals(text, adapter.getStringAt(2, 2));
    }

    /**
     * Issue #767-swingx: consistent string representation.
     * 
     * Here: test api on table's ComponentAdapter.
     */
    @Test
    public void testTableGetString() {
        JXTableT table = new JXTableT(new AncientSwingTeam());
        table.setDefaultRenderer(Color.class, new DefaultTableRenderer(sv));
        String text = sv.getString(table.getValueAt(2, 2));
        ComponentAdapter adapter = table.getComponentAdapter(2, 2);
        assertEquals(text, adapter.getString());
    }

    /**
     * Issue #767-swingx: consistent string representation.
     * 
     * Here: test api on table's ComponentAdapter.
     */
    @Test
    public void testTableGetFilteredString() {
        JXTableT table = new JXTableT(new AncientSwingTeam());
        table.setDefaultRenderer(Color.class, new DefaultTableRenderer(sv));
        String text = sv.getString(table.getValueAt(2, 2));
        ComponentAdapter adapter = table.getComponentAdapter(2, 2);
        assertEquals(text, adapter.getFilteredStringAt(2, 2));
    }

    /**
     * Issue #767-swingx: consistent string representation.
     * 
     * Here: test api on table's ComponentAdapter.
     */
    @Test
    public void testTableGetStringColumn() {
        JXTableT table = new JXTableT(new AncientSwingTeam());
        table.setDefaultRenderer(Color.class, new DefaultTableRenderer(sv));
        String text = sv.getString(table.getValueAt(2, 2));
        ComponentAdapter adapter = table.getComponentAdapter(2, 2);
        assertEquals(text, adapter.getString(2));
    }

    /**
     * Overridden to get access to componentAdapter.
     * 
     */
    public static class JXTableT extends JXTable {

        public JXTableT(TableModel model) {
            super(model);
        }

        @Override
        public ComponentAdapter getComponentAdapter(int row, int column) {
            return super.getComponentAdapter(row, column);
        }
        
    }

    
    /**
     * Overridden to get access to componentAdapter.
     * 
     */
    public static class JXTreeTableT extends JXTreeTable {

        public JXTreeTableT(TreeTableModel model) {
            super(model);
        }

        @Override
        public ComponentAdapter getComponentAdapter(int row, int column) {
            return super.getComponentAdapter(row, column);
        }
        
    }


    @SuppressWarnings("unused")
    private DefaultTableModel createAscendingModel(int startRow, int count) {
        DefaultTableModel model = new DefaultTableModel(count, 5);
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(new Integer(startRow++), i, 0);
        }
        return model;
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
    

    @Before
    public void setUpJ4() throws Exception {
        setUp();
    }
    
    @After
    public void tearDownJ4() throws Exception {
        tearDown();
    }
    

}
