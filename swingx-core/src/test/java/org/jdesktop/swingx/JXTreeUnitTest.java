/*
 * $Id: JXTreeUnitTest.java 4301 2013-07-04 12:36:17Z kleopatra $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.text.Position.Bias;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTree.DelegatingRenderer;
import org.jdesktop.swingx.JXTreeTableUnitTest.InsertTreeTableModel;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.SearchPredicate;
import org.jdesktop.swingx.renderer.DefaultTreeRenderer;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.renderer.StringValues;
import org.jdesktop.swingx.tree.DefaultXTreeCellEditor;
import org.jdesktop.swingx.tree.DefaultXTreeCellRenderer;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.FileSystemModel;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.jdesktop.test.AncientSwingTeam;
import org.jdesktop.test.PropertyChangeReport;
import org.jdesktop.test.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;



/**
 * Unit tests for JXTree.
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class JXTreeUnitTest extends InteractiveTestCase {

    protected TreeTableModel treeTableModel;
        
    public JXTreeUnitTest() {
        super("JXTree Test");
    }

    /**
     * Issue #1563-swingx: find cell that was clicked for componentPopup
     * 
     * Test api and event firing.
     */
    @Test
    public void testPopupTriggerLocationAvailable() {
        JXTree table = new JXTree();
        table.expandAll();
        MouseEvent event = new MouseEvent(table, 0,
                0, 0, 40, 5, 0, false);
        PropertyChangeReport report = new PropertyChangeReport(table);
        table.getPopupLocation(event);
        assertEquals(event.getPoint(), table.getPopupTriggerLocation());
        TestUtils.assertPropertyChangeEvent(report, "popupTriggerLocation", 
                null, event.getPoint());
    }
    
    
    /**
     * Issue #1563-swingx: find cell that was clicked for componentPopup
     * 
     * Test safe return value.
     */
    @Test
    public void testPopupTriggerCopy() {
        JXTree table = new JXTree();
        table.expandAll();
        MouseEvent event = new MouseEvent(table, 0,
                0, 0, 40, 5, 0, false);
        table.getPopupLocation(event);
        assertNotSame("trigger point must not be same", 
                table.getPopupTriggerLocation(), table.getPopupTriggerLocation());
    }
    
    /**
     * Issue #1563-swingx: find cell that was clicked for componentPopup
     * 
     * Test safe handle null.
     */
    @Test
    public void testPopupTriggerKeyboard() {
        JXTree table = new JXTree();
        table.expandAll();
        MouseEvent event = new MouseEvent(table, 0,
                0, 0, 40, 5, 0, false);
        table.getPopupLocation(event);
        PropertyChangeReport report = new PropertyChangeReport(table);
        table.getPopupLocation(null);
        assertNull("trigger must null", 
                table.getPopupTriggerLocation());
        TestUtils.assertPropertyChangeEvent(report, "popupTriggerLocation", 
                event.getPoint(), null);
    }


    /**
     * Issue http://java.net/jira/browse/SWINGX-1483 - nextMatch must respect string rep
     */
    @Test
    public void testNextMatch() {
        JXTree tree = new JXTree(AncientSwingTeam.createNamedColorTreeModel());
        tree.setCellRenderer(new DefaultTreeRenderer(createColorStringValue()));
        tree.expandAll();
        assertEquals("must not find a match for 'b', all start with 'r'", 
                null, tree.getNextMatch("b", 0, Bias.Forward));

    }
    
    /**
     * Issue #1231-swingx: tree cell renderer size problems.
     * 
     * Cache not invalidated on setCellRenderer due to not firing a 
     * propertyChange (it's wrapped). Problem or not is LAF dependent ;-)
     * The not-firing is clearly a bug.
     * 
     */
    @Test
    public void testRendererNotification() {
        JXTree tree = new JXTree();
        TreeCellRenderer renderer = tree.getCellRenderer();
        assertNotNull("sanity: ", renderer);
        PropertyChangeReport report = new PropertyChangeReport(tree);
        tree.setCellRenderer(new DefaultTreeRenderer());
        assertEquals(1, report.getEventCount());
        assertEquals(1, report.getEventCount("cellRenderer"));
        
    }
    /**
     * Issue #1061-swingx: renderer/editor inconsistent on startup
     */
    @Test
    public void testRendererOnInit() {
        TestTree tree = new TestTree();
        assertSame(tree.getSuperRenderer(), tree.getCellRenderer()); 
    }

    /**
     * Issue #1061-swingx: renderer/editor inconsistent on startup
     */
    @Test
    public void testEditorOnInit() {
        TestTree tree = new TestTree();
        assertSame(tree.getSuperEditor(), tree.getCellEditor()); 
    }
    
    /**
     * Issue #1061-swingx: renderer/editor inconsistent on startup
     */
    @Test
    public void testDefaultRendererOnInit() {
        TestTree tree = new TestTree();
        assertNotNull("sanity: default renderer created", tree.getCreatedDefaultRenderer()); 
        assertSame("sanity: default renderer used as wrappee", tree.getCreatedDefaultRenderer(), tree.getWrappedCellRenderer());
    }
    
    /**
     * Issue #1061-swingx: renderer/editor inconsistent on startup.
     * Note: this test will fail once we use an enhanced cellEditor (which can cope with 
     * SwingX default renderers).
     */
    @Test
    public void testDefaultRendererUsedInEditorOnInit() {
        TestTree tree = new TestTree();
        assertTrue("sanity: editor is of type DefaultXTreeCellEditor", tree.getCellEditor() instanceof DefaultXTreeCellEditor);
        assertSame(tree.getWrappedCellRenderer(), ((DefaultXTreeCellEditor) tree.getCellEditor()).getRenderer()); 
    }
    
    
    /**
     * Subclass for testing: access cellRenderer/cellEditor fields 
     */
    public static class TestTree extends JXTree {
        
        private TreeCellRenderer createdDefaultRenderer;

        public TreeCellRenderer getSuperRenderer() {
            return cellRenderer;
        }
        
        public TreeCellEditor getSuperEditor() {
            return cellEditor;
        }

        public TreeCellRenderer getCreatedDefaultRenderer() {
            return createdDefaultRenderer;
        }
        
        @Override
        protected TreeCellRenderer createDefaultCellRenderer() {
            createdDefaultRenderer = super.createDefaultCellRenderer();
            return createdDefaultRenderer;
        }
        
        
    }

    /**
     * Issue #862-swingx: JXTree - add api for selection colors.
     */
    @Test
    public void testSelectionBackground() {
        JXTree tree = new JXTree();
        Color uiColor = UIManager.getColor("Tree.selectionBackground");
        assertEquals(uiColor, tree.getSelectionBackground());
        Color customColor = Color.RED;
        tree.setSelectionBackground(customColor);
        tree.updateUI();
        assertEquals("custom color must not be reset by ui", customColor, tree.getSelectionBackground());
    }
    
    /**
     * Issue #862-swingx: JXTree - add api for selection colors.
     */
    @Test
    public void testSelectionForeground() {
        JXTree tree = new JXTree();
        Color uiColor = UIManager.getColor("Tree.selectionForeground");
        assertEquals(uiColor, tree.getSelectionForeground());
        Color customColor = Color.RED;
        tree.setSelectionForeground(customColor);
        tree.updateUI();
        assertEquals("custom color must not be reset by ui", customColor, tree.getSelectionForeground());
    }
    
    
    /**
     * Issue #862-swingx: JXTree - add api for selection colors.
     */
    @Test
    public void testSelectionBackgroundChange() {
        JXTree tree = new JXTree();
        Color uiColor = tree.getSelectionBackground();
        Color customColor = Color.RED;
        PropertyChangeReport report = new PropertyChangeReport();
        tree.addPropertyChangeListener(report);
        tree.setSelectionBackground(customColor);
        TestUtils.assertPropertyChangeEvent(report, "selectionBackground", uiColor, customColor);
    }
    
    /**
     * Issue #862-swingx: JXTree - add api for selection colors.
     */
    @Test
    public void testSelectionForegroundChange() {
        JXTree tree = new JXTree();
        Color uiColor = tree.getSelectionForeground();
        Color customColor = Color.RED;
        PropertyChangeReport report = new PropertyChangeReport();
        tree.addPropertyChangeListener(report);
        tree.setSelectionForeground(customColor);
        TestUtils.assertPropertyChangeEvent(report, "selectionForeground", uiColor, customColor);
    }
    
    /**
     * Issue #817-swingx: Delegating renderer must create list's default.
     * Consistent api: expose wrappedRenderer the same way as wrappedModel
     */
    @Test
    public void testWrappedRendererDefault() {
        JXTree list = new JXTree();
        DelegatingRenderer renderer = (DelegatingRenderer) list.getCellRenderer();
        assertSame("wrapping renderer must use list's default on null", 
                 renderer.getDelegateRenderer(), list.getWrappedCellRenderer());
    }

    /**
     * Issue #817-swingx: Delegating renderer must create list's default.
     * Consistent api: expose wrappedRenderer the same way as wrappedModel
     */
    @Test
    public void testWrappedRendererCustom() {
        JXTree list = new JXTree();
        DelegatingRenderer renderer = (DelegatingRenderer) list.getCellRenderer();
        TreeCellRenderer custom = new DefaultTreeRenderer();
        list.setCellRenderer(custom);
        assertSame("wrapping renderer must use list's default on null", 
                 renderer.getDelegateRenderer(), list.getWrappedCellRenderer());
    }
    
    /**
     * Issue #817-swingx: Delegating renderer must create list's default.
     * Delegating uses default on null, here: default default.
     */
    @Test
    public void testDelegatingRendererUseDefaultSetNull() {
        JXTree list = new JXTree();
        TreeCellRenderer defaultRenderer = list.createDefaultCellRenderer();
        DelegatingRenderer renderer = (DelegatingRenderer) list.getCellRenderer();
        list.setCellRenderer(null);
        assertEquals("wrapping renderer must use list's default on null", 
                defaultRenderer.getClass(), renderer.getDelegateRenderer().getClass());
    }

    /**
     * Issue #817-swingx: Delegating renderer must create list's default.
     * Delegating has default from list initially, here: default default.
     * 
     * Note: this test has to be changed once we switch to default to DefaultTreeRenderer.
     */
    @Test
    public void testDelegatingRendererUseDefault() {
        JXTree list = new JXTree();
        TreeCellRenderer defaultRenderer = list.createDefaultCellRenderer();
        assertEquals("sanity: creates default", DefaultXTreeCellRenderer.class, 
                defaultRenderer.getClass());
        DelegatingRenderer renderer = (DelegatingRenderer) list.getCellRenderer();
        assertEquals(defaultRenderer.getClass(), renderer.getDelegateRenderer().getClass());
    }
    
    /**
     * Issue #817-swingx: Delegating renderer must create list's default.
     * Delegating has default from list initially, here: custom default.
     */
    @Test
    public void testDelegatingRendererUseCustomDefaultSetNull() {
        JXTree list = new JXTree() {

            @Override
            protected TreeCellRenderer createDefaultCellRenderer() {
                return new CustomDefaultRenderer();
            }
            
        };
        TreeCellRenderer defaultRenderer = list.createDefaultCellRenderer();
        DelegatingRenderer renderer = (DelegatingRenderer) list.getCellRenderer();
        list.setCellRenderer(null);
        assertEquals("wrapping renderer must use list's default on null",
                defaultRenderer.getClass(), renderer.getDelegateRenderer().getClass());
    }
    
    /**
     * Issue #817-swingx: Delegating renderer must create list's default.
     * Delegating has default from list initially, here: custom default.
     */
    @Test
    public void testDelegatingRendererUseCustomDefault() {
        JXTree list = new JXTree() {

            @Override
            protected TreeCellRenderer createDefaultCellRenderer() {
                return new CustomDefaultRenderer();
            }
            
        };
        TreeCellRenderer defaultRenderer = list.createDefaultCellRenderer();
        assertEquals("sanity: creates custom", CustomDefaultRenderer.class, 
                defaultRenderer.getClass());
        DelegatingRenderer renderer = (DelegatingRenderer) list.getCellRenderer();
        assertEquals(defaultRenderer.getClass(), renderer.getDelegateRenderer().getClass());
    }
    /**
     * Dummy extension for testing - does nothing more as super.
     * For tree, we subclass swingx renderer, as the default still it core default.
     */
    public static class CustomDefaultRenderer extends DefaultTreeRenderer {
    }
    
    /**
     * Issue #767-swingx: consistent string representation.
     * 
     * Here: test api on JXTable.
     */
    @Test
    public void testGetStringForRow() {
        JXTree tree = new JXTree(AncientSwingTeam.createNamedColorTreeModel());
        tree.expandAll();
        StringValue sv = new StringValue() {

            public String getString(Object value) {
                if (value instanceof Color) {
                    Color color = (Color) value;
                    return "R/G/B: " + color.getRGB();
                }
                return StringValues.TO_STRING.getString(value);
            }
            
        };
        tree.setCellRenderer(new DefaultTreeRenderer(sv));
        Object value =((DefaultMutableTreeNode) tree.getPathForRow(2).getLastPathComponent()).getUserObject();
        String text = tree.getStringAt(2);
        assertEquals(sv.getString(value), text);
    }

    /**
     * Issue #767-swingx: consistent string representation.
     * 
     * Here: test api on JXTable.
     */
    @Test
    public void testGetStringForPath() {
        JXTree tree = new JXTree(AncientSwingTeam.createNamedColorTreeModel());
        tree.expandAll();
        StringValue sv = new StringValue() {

            public String getString(Object value) {
                if (value instanceof Color) {
                    Color color = (Color) value;
                    return "R/G/B: " + color.getRGB();
                }
                return StringValues.TO_STRING.getString(value);
            }
            
        };
        tree.setCellRenderer(new DefaultTreeRenderer(sv));
        Object value =((DefaultMutableTreeNode) tree.getPathForRow(2).getLastPathComponent()).getUserObject();
        String text = tree.getStringAt(tree.getPathForRow(2));
        assertEquals(sv.getString(value), text);
    }
    /**
     * Issue #769-swingx: setXXIcon on renderer vs setXXIcon on Tree/Table.
     * Characterize tree behaviour.
     * 
     * Here: icon on renderer must be respected if overwrite is false.
     */
    @Test
    public void testIconSetOnRendererFalseOverwrite() {
        JXTree tree = new JXTree();
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setLeafIcon(null);
        tree.setCellRenderer(renderer);
        assertEquals("renderer must have null leaf icon", null, renderer.getLeafIcon());
    }

    /**
     * Issue #769-swingx: setXXIcon on renderer vs setXXIcon on Tree/Table.
     * Characterize tree behaviour.
     * 
     * Here: icon on renderer must be overwritten if overwrite is true.
     */
    @Test
    public void testIconSetOnRendererTrueOverwrite() {
        JXTree tree = new JXTree();
        tree.setLeafIcon(null);
        tree.setOverwriteRendererIcons(true);
        // PENDING: incomplete api - no getter
//        Icon leaf = tree.getLeafIcon();
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        assertNotNull("sanity - the renderer has a leafIcon ", renderer.getLeafIcon());
        tree.setCellRenderer(renderer);
        assertEquals("renderer leaf icon must be overwritten by tree's leaf icon", 
                null, renderer.getLeafIcon());
    }

    /**
     * Issue #769-swingx: setXXIcon on renderer vs setXXIcon on Tree/Table.
     * Characterize tree behaviour.
     * 
     * Here: set icon on tree is passed on to renderer always.
     */
    @Test
    public void testIconSetOnTreeFalseOverwrite() {
        JXTree tree = new JXTree();
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        assertNotNull(renderer.getLeafIcon());
        tree.setCellRenderer(renderer);
        tree.setLeafIcon(null);
        assertEquals("renderer must have null leaf icon", null, renderer.getLeafIcon());
    }

    /**
     * Issue #769-swingx: setXXIcon on renderer vs setXXIcon on Tree/Table.
     * Characterize tree behaviour.
     * 
     * Here: set icon on tree is passed on to renderer always.
     */
    @Test
    public void testIconSetOnTreeTrueOverwrite() {
        JXTree tree = new JXTree();
        tree.setOverwriteRendererIcons(true);
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        assertNotNull(renderer.getLeafIcon());
        tree.setCellRenderer(renderer);
        tree.setLeafIcon(null);
        assertEquals("renderer must have null leaf icon", null, renderer.getLeafIcon());
    }
    
    /**
     * Issue #769-swingx: setXXIcon on renderer vs setXXIcon on Tree/Table.
     * Characterize tree behaviour.
     * 
     * Here: default overwriteOnRenderer is false.
     */
    @Test
    public void testIconOverwriteInitial() {
        JXTree tree = new JXTree();
        assertFalse("initial overwriteRendererIcons must be false", tree.isOverwriteRendererIcons());
    }
    
    
    /**
     * focus issues with popup in editors: tweak with 
     * custom cellEditorListener.
     *
     */
    @Test
    public void testEditorListenerRemovedOnEditorStopped() {
        JXTree tree = createEditingTree();
        DefaultTreeCellEditor cellEditor = (DefaultTreeCellEditor) tree.getCellEditor();
        // core doesn't remove the listener ... dooh
        cellEditor.stopCellEditing();
        // but we do ... 
        assertEquals(1, cellEditor.getCellEditorListeners().length);
    }


    /**
     * focus issues with popup in editors: tweak with 
     * custom cellEditorListener.
     *
     */
    @Test
    public void testEditorListenerRemovedOnEditorCancel() {
        JXTree tree = createEditingTree();
        DefaultTreeCellEditor cellEditor = (DefaultTreeCellEditor) tree.getCellEditor();
        // core doesn't remove the listener ... dooh
        cellEditor.cancelCellEditing();
        // but we do ... 
        assertEquals(1, cellEditor.getCellEditorListeners().length);
    }

    /**
     * focus issues with popup in editors: tweak with 
     * custom cellEditorListener.
     *
     */
    @Test
    public void testEditorListenerRemovedOnTreeCancel() {
        JXTree tree = createEditingTree();
        DefaultTreeCellEditor cellEditor = (DefaultTreeCellEditor) tree.getCellEditor();
        // core doesn't remove the listener ... dooh
        tree.cancelEditing();
        // but we do ... 
        assertEquals(1, cellEditor.getCellEditorListeners().length);
    }
    
    /**
     * focus issues with popup in editors: tweak with 
     * custom cellEditorListener.
     *
     */
    @Test
    public void testEditorListenerRemovedOnTreeStop() {
        JXTree tree = createEditingTree();
        DefaultTreeCellEditor cellEditor = (DefaultTreeCellEditor) tree.getCellEditor();
        // core doesn't remove the listener ... dooh
        tree.stopEditing();
        // but we do ... 
        assertEquals(1, cellEditor.getCellEditorListeners().length);
    }

    /**
     * focus issues with popup in editors: tweak with 
     * custom cellEditorListener.
     *
     */
    @Test
    public void testEditorListenerOnXTree() {
        JTree core = new JTree();
        int coreCount = getListenerCountAfterStartEditing(core);
        JXTree tree = createEditingTree();
        DefaultTreeCellEditor cellEditor = (DefaultTreeCellEditor) tree.getCellEditor();
        assertEquals("need one more listener than core", 
                coreCount + 1, cellEditor.getCellEditorListeners().length);
    }
    
    /**
     * @return a tree with default model and editing started on row 2
     */
    private JXTree createEditingTree() {
        JXTree tree = new JXTree();
        tree.setEditable(true);
        // sanity
        assertTrue(tree.getRowCount() > 2);
        TreePath path = tree.getPathForRow(2);
        tree.startEditingAtPath(path);
        return tree;
    }

    /**
     * characterization: listeners in core tree.
     *
     */
    @Test
    public void testEditorListenerOnCoreTree() {
        JTree tree = new JTree();
        int listenerCount = getListenerCountAfterStartEditing(tree);
        assertEquals(1, listenerCount);
        tree.stopEditing();
        // doesn't remove the listener ... dooh
        assertEquals(1, ((DefaultTreeCellEditor) tree.getCellEditor()).getCellEditorListeners().length);
        
    }


    /**
     * Starts editing on row 2 and returns the cell editor listener count after.
     * 
     * @param tree
     * @return
     */
    private int getListenerCountAfterStartEditing(JTree tree) {
        tree.setEditable(true);
        // sanity
        assertTrue(tree.getRowCount() > 2);
        TreePath path = tree.getPathForRow(2);
        tree.startEditingAtPath(path);
        DefaultTreeCellEditor cellEditor = (DefaultTreeCellEditor) tree.getCellEditor();
        int listenerCount = cellEditor.getCellEditorListeners().length;
        return listenerCount;
    }
    

    
    /**
     * Issue #473-swingx: NPE in JXTree with highlighter.<p>
     * 
     * Renderers are doc'ed to cope with invalid input values.
     * Highlighters can rely on valid ComponentAdapter state. 
     * JXTree delegatingRenderer is the culprit which does set
     * invalid ComponentAdapter state. Negative invalid index.
     *
     */
    @Test
    public void testIllegalNegativeTreeRowIndex() {
        JXTree tree = new JXTree();
        tree.expandAll();
        assertTrue(tree.getRowCount() > 0);
        TreeCellRenderer renderer = tree.getCellRenderer();
        renderer.getTreeCellRendererComponent(tree, "dummy", false, false, false, -1, false);
        SearchPredicate predicate = new SearchPredicate("\\QNode\\E");
        Highlighter searchHighlighter = new ColorHighlighter(predicate, null, Color.RED);
        tree.addHighlighter(searchHighlighter);
        renderer.getTreeCellRendererComponent(tree, "dummy", false, false, false, -1, false);
    }
    
    /**
     * Issue #473-swingx: NPE in JXTree with highlighter.<p>
     * 
     * Renderers are doc'ed to cope with invalid input values.
     * Highlighters can rely on valid ComponentAdapter state. 
     * JXTree delegatingRenderer is the culprit which does set
     * invalid ComponentAdapter state. Invalid index > valid range.
     *
     */
    @Test
    public void testIllegalExceedingTreeRowIndex() {
        JXTree tree = new JXTree();
        tree.expandAll();
        assertTrue(tree.getRowCount() > 0);
        TreeCellRenderer renderer = tree.getCellRenderer();
        renderer.getTreeCellRendererComponent(tree, "dummy", false, false, false, tree.getRowCount(), false);
        SearchPredicate predicate = new SearchPredicate("\\QNode\\E");
        Highlighter searchHighlighter = new ColorHighlighter(predicate, null, Color.RED);
        tree.addHighlighter(searchHighlighter);
        renderer.getTreeCellRendererComponent(tree, "dummy", false, false, false, tree.getRowCount(), false);
    }

    /**
     * test convenience method accessing the configured adapter.
     *
     */
    @Test
    public void testConfiguredComponentAdapter() {
        JXTree list = new JXTree();
        list.expandAll();
        assertTrue(list.getRowCount() > 0);
        ComponentAdapter adapter = list.getComponentAdapter();
        assertEquals(0, adapter.column);
        assertEquals(0, adapter.row);
        assertTrue(adapter.isHierarchical());
        
        adapter.row = 1;
        // corrupt adapter
        adapter.column = 1;
        adapter = list.getComponentAdapter(0);
        assertEquals(0, adapter.column);
        assertEquals(0, adapter.row);
    }

    
    /**
     * Issue #254-swingx: expandAll doesn't expand if root not shown?
     *
     */
    @Test
    public void testExpandAllWithInvisible() {
        final DefaultMutableTreeTableNode root = new DefaultMutableTreeTableNode();
        final InsertTreeTableModel model = new InsertTreeTableModel(root);
        int childCount = 5;
        for (int i = 0; i < childCount; i++) {
            model.addChild(root);
        }
        final JXTree treeTable = new JXTree(model);
        // sanity...
        assertTrue(treeTable.isRootVisible());
        assertEquals("all children visible", childCount + 1, treeTable.getRowCount());
        treeTable.collapseAll();
        assertEquals(" all children invisible", 1, treeTable.getRowCount());
        treeTable.setRootVisible(false);
        assertEquals("no rows with invisible root", 0, treeTable.getRowCount());
        treeTable.expandAll();
        assertTrue(treeTable.getRowCount() > 0);
        
    }

    /**
     * test enhanced getSelectedRows contract: returned 
     * array != null
     *
     */
    @Test
    public void testNotNullGetSelectedRows() {
        JXTree tree = new JXTree(treeTableModel);
        // sanity: no selection
        assertEquals(0, tree.getSelectionCount());
        assertNotNull("getSelectedRows guarantees not null array", tree.getSelectionRows());
    }
    
    /**
     * test enhanced getSelectedRows contract: returned 
     * array != null
     *
     */
    @Test
    public void testNotNullGetSelectedPaths() {
        JXTree tree = new JXTree(treeTableModel);
        // sanity: no selection
        assertEquals(0, tree.getSelectionCount());
        assertNotNull("getSelectedPaths guarantees not null array", tree.getSelectionPaths());
    }
    /**
     * Issue #221-swingx: actionMap not initialized in JXTreeNode constructor.
     * Issue #231-swingx: icons lost in editor, enhanced default editor not installed.
     * 
     * PENDING: test all constructors!
     *
     */
    @Test
    public void testInitInConstructors() {
        assertXTreeInit(new JXTree());
        assertXTreeInit(new JXTree(new Object[] {}));
        assertXTreeInit(new JXTree(new Vector<Object>()));
        assertXTreeInit(new JXTree(new Hashtable<Object, Object>()));
        assertXTreeInit(new JXTree(new DefaultMutableTreeNode("dummy"), false));
        assertXTreeInit(new JXTree(new DefaultMutableTreeNode("dummy")));
        assertXTreeInit(new JXTree(new DefaultTreeModel(new DefaultMutableTreeNode("dummy"))));
    }

    /**
     * @param tree
     */
    private void assertXTreeInit(JXTree tree) {
        assertNotNull("Actions must be initialized", tree.getActionMap().get("find"));
        assertTrue("Editor must be DefaultXTreeCellEditor", 
                tree.getCellEditor() instanceof DefaultXTreeCellEditor);
        // JW: wrong assumption, available for TreeTableModel impl only?
//        assertNotNull("conversionMethod must be initialized", 
//                tree.getValueConversionMethod(tree.getModel()));
//        tree.getValueConversionMethod(tree.getModel());
    }

    /** 
     * JTree allows null model.
     * learning something new every day :-)
     *
     */
    @Test
    public void testNullModel() {
        JXTree tree = new JXTree();
        assertNotNull(tree.getModel());
        tree.setModel(null);
        assertEquals(0, tree.getRowCount());
        // tree.getComponentAdapter().isLeaf();
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
        super.setUp();
        treeTableModel = new FileSystemModel();
    }

}
