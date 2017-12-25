/*
 * $Id: RenderingTest.java 4222 2012-08-07 10:23:08Z kleopatra $
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
package org.jdesktop.swingx.renderer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.util.logging.Logger;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.hyperlink.AbstractHyperlinkAction;
import org.jdesktop.swingx.icon.EmptyIcon;
import org.jdesktop.swingx.painter.ShapePainter;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.test.ComponentTreeTableModel;
import org.jdesktop.swingx.test.XTestUtils;
import org.jdesktop.test.PropertyChangeReport;
import org.jdesktop.test.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests swingx rendering infrastructure: ComponentProvider, CellContext, 
 * ..
 * 
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class RenderingTest extends InteractiveTestCase {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(RenderingTest.class
            .getName());

    /**
     * Issue #1339-swingx: set name of rendering component for Synth-based LAFs.
     * 
     * @throws Exception
     */
    @Test
    public void testRenderingComponentNames() throws Exception {
        ComponentProvider<?> provider = new LabelProvider();
        assertEquals("default name expected null", null, provider
                .getRendererComponent(null).getName());
        // use the same provider for all types of renderers
        DefaultTableRenderer rendererTable = new DefaultTableRenderer(provider);
        DefaultListRenderer rendererList = new DefaultListRenderer(provider);
        DefaultTreeRenderer rendererTree = new DefaultTreeRenderer(provider);
        LookAndFeel old = UIManager.getLookAndFeel();
        try {
            InteractiveTestCase.setLookAndFeel("Nimbus");
            // table
            JTable table = new JTable(4, 3);
            TableCellRenderer tableT = table.getDefaultRenderer(Object.class);
            String nameT = tableT.getTableCellRendererComponent(table, null, false, false, 0, 0).getName();
            assertEquals("sanity: checking default name", "Table.cellRenderer", nameT);
            assertEquals(nameT, rendererTable
                    .getTableCellRendererComponent(null, null, false, false, 0,
                            0).getName());
            // list
            JList list = new JList();
            ListCellRenderer listR = list.getCellRenderer();
            String nameL =
                     listR.getListCellRendererComponent(list, null, 0, false,
                            false).getName();
            assertEquals("sanity: checking default name", "List.cellRenderer", nameL);
            assertEquals(nameL, rendererList.
                    getListCellRendererComponent(list, null, 0, false, false).getName());
            // tree
            JTree tree = new JTree();
            TreeCellRenderer treeR = tree.getCellRenderer();
            String nameTree =
                     treeR.getTreeCellRendererComponent(tree, null, false,
                            false, false, 1, false).getName();
            assertEquals("sanity: checking default name", "Tree.cellRenderer", nameTree);
            assertEquals(nameTree, rendererTree.
                    getTreeCellRendererComponent(tree, null, false, false, false, 0, false).getName());
            
        } finally {
            UIManager.setLookAndFeel(old);
        }
    }
    /**
     * Issue #766-swingx: flickering cursor on drop over.
     * 
     * test hack configuration (arrrggg ...)
     */
    @Test
    public void testWrappingIconPanelDropHackDefault() {
        JXPanel sanity = new JXPanel();
        assertEquals("visible by default", true, sanity.isVisible());
        WrappingIconPanel panel = new WrappingIconPanel();
        assertEquals("invisible by default", false, panel.isVisible());
        panel.setDropHackEnabled(false);
        assertEquals("disabled hack", true, panel.isVisible());
    }
    
    /**
     * Issue #766-swingx: flickering cursor on drop over.
     * 
     * test hack configuration (arrrggg ...)
     */
    @Test
    public void testWrappingIconPanelDropHackConstructor() {
        JXPanel sanity = new JXPanel();
        assertEquals("visible by default", true, sanity.isVisible());
        WrappingIconPanel panel = new WrappingIconPanel(false);
        assertEquals("invisible by default", true, panel.isVisible());
        panel.setDropHackEnabled(true);
        assertEquals("disabled hack", false, panel.isVisible());
    }
    
    /**
     * Issue #863-swingx: SwingX renderering components must be PainterAware.
     * Here: test Hyperlink
     */
    @Test
    public void testHyperlinkPainterAware() {
        HyperlinkProvider provider = new HyperlinkProvider();
        assertTrue("hyperlink as rendering comp must be PainterAware", provider.getRendererComponent(null) instanceof PainterAware);
    }

    
    /**
     * Issue #863-swingx: SwingX renderering components must be PainterAware.
     * Here: test WrappingIconPanel
     */
    @Test
    public void testWrappingIconPanelPainterAware() {
        DefaultTreeRenderer renderer = new DefaultTreeRenderer();
        JComponent rendererComponent = renderer.getComponentProvider().getRendererComponent(null);
        assertTrue("wrappingIconPanel as rendering comp must be PainterAware", rendererComponent instanceof PainterAware);
    }
    
    /**
     * Issue #840-swingx: hyperlink foreground unreadable for dark selection colors.
     * 
     * Test that selected foreground is same as table. 
     * 
     */
    @Test
    public void testHyperlinkProviderForeground() {
        JXTable table = new JXTable(20, 2);
        HyperlinkProvider provider = new HyperlinkProvider();
        JXHyperlink hyperlink = provider.getRendererComponent(null);
        Color unclicked = hyperlink.getUnclickedColor();
        table.setDefaultRenderer(Object.class, new DefaultTableRenderer(provider));
        table.prepareRenderer(table.getCellRenderer(0, 0), 0, 0);
        assertEquals("hyperlink foreground set to unclicked", unclicked, hyperlink.getForeground());
        table.setRowSelectionInterval(0, 0);
        // JW: had been failing before because I forgot to re-prepare :-)
        table.prepareRenderer(table.getCellRenderer(0, 0), 0, 0);
        assertEquals("hyperlink foreground set to table's selection foreground", 
                table.getSelectionForeground(), hyperlink.getForeground());
    }


    /**
     * Issue #842-swingx: HyperlinkProvider getString doesn't serve its contract.
     * 
     */
    @Test
    public void testHyperlinkStringValue() {
        AbstractHyperlinkAction<?> linkAction = new AbstractHyperlinkAction<Object>() {

            public void actionPerformed(ActionEvent e) {
                // nothing
            }

            @Override
            protected void installTarget() {
                setName("pref" + String.valueOf(target));
            }
            
            
        };
        HyperlinkProvider provider = new HyperlinkProvider(linkAction, Object.class);
        Object target = null;
        assertEquals("pref" + String.valueOf(target), provider.getString(target));
    }
    

    
    /**
     * Issue ?? swingx: support to configure the auto-unwrap of tree/table/xx/nodes.
     * 
     * Initial default is true.
     */
    @Test
    public void testWrappingProviderUnwrapContructor() {
        LabelProvider delegate = new LabelProvider();
        WrappingProvider provider = new WrappingProvider(delegate, false);
        assertWrappingProviderState(provider, null, null, delegate, false);
    }
    /**
     * Issue ?? swingx: support to configure the auto-unwrap of tree/table/xx/nodes.
     * 
     * Initial default is true.
     */
    @Test
    public void testWrappingProviderUserObjectUnwrapInitial() {
        WrappingProvider provider = new WrappingProvider();
        assertEquals(true, provider.getUnwrapUserObject());
    }

    /**
     * Issue ?? swingx: support to configure the auto-unwrap of tree/table/xx/nodes.
     * setter sets
     */
    @Test
    public void testWrappingProviderUserObjectUnwrapSet() {
        WrappingProvider provider = new WrappingProvider();
        boolean unwrap = !provider.getUnwrapUserObject();
        provider.setUnwrapUserObject(unwrap);
        assertEquals(unwrap, provider.getUnwrapUserObject());
    }
    
    /**
     * Issue ?? swingx: support to configure the auto-unwrap of tree/table/xx/nodes.
     * Respect unwrap flag in getString.
     */
    @Test
    public void testWrappingProviderUserObjectUnwrapRespectString() {
        StringValue sv = new StringValue() {

            public String getString(Object value) {
                if (value instanceof Point) {
                    return "x of Point: " + ((Point) value).x;
                }
                return StringValues.TO_STRING.getString(value);
            }
            
        };
        CellContext context =  new TableCellContext();
        Point p = new Point(10, 20);
        context.replaceValue(new DefaultMutableTreeNode(p));
        WrappingProvider provider = new WrappingProvider(sv);
        provider.setUnwrapUserObject(false);
        assertEquals("must not unwrap the user object", 
                sv.getString(context.getValue()), provider.getString(context.getValue()));
    }
    
    /**
     * Issue ?? swingx: support to configure the auto-unwrap of tree/table/xx/nodes.
     * Respect unwrap flag in configuring a renderer.
     */
    @Test
    public void testWrappingProviderUserObjectUnwrapRespectRenderer() {
        StringValue sv = new StringValue() {

            public String getString(Object value) {
                if (value instanceof Point) {
                    return "x of Point: " + ((Point) value).x;
                }
                return StringValues.TO_STRING.getString(value);
            }
            
        };
        CellContext context =  new TableCellContext();
        Point p = new Point(10, 20);
        context.replaceValue(new DefaultMutableTreeNode(p));
        WrappingProvider provider = new WrappingProvider(sv);
        provider.setUnwrapUserObject(false);
        LabelProvider wrappee = (LabelProvider) provider.getWrappee();
        // configure 
        provider.getRendererComponent(context);
        assertEquals("must not unwrap the user object", 
                sv.getString(context.getValue()), 
                wrappee.rendererComponent.getText());
    }
    

    /**
     * Issue #790-swingx: rendering comps must not be registered with the tooltip manager.
     * 
     * Here: TreeTableCellRenderer (the tree used for rendering the hierarchical 
     * column)
     * 
     */
    @Test
    public void testToolTipManagerTreeTableTreeRenderer() {
        JXTreeTable treeTable = new JXTreeTable(new ComponentTreeTableModel(new JXPanel()));
        // core row conversion is lenient than ol' swingx
        // PENDING JW: raise an issue about JXTreeTable not being sortable 
        // (remove the default auto-createRowSorter)
        treeTable.setRootVisible(true);
        JComponent label = (JComponent) treeTable.prepareRenderer(treeTable.getCellRenderer(0, 0), 0, 0);
        String tip = "some tip";
        PropertyChangeReport report = new PropertyChangeReport();
        label.addPropertyChangeListener(report);
        label.setToolTipText(tip);
        TestUtils.assertPropertyChangeEvent(report, JComponent.TOOL_TIP_TEXT_KEY, null, tip);
        assertToolTipManagerNotRegistered(label);
    }

    /**
     * Issue #790-swingx: rendering comps must not be registered with the
     * tooltip manager.
     * 
     * Here: Hyperlink in provider
     */
    @Test
    public void testToolTipManagerHyperlinkProvider() {
        AbstractHyperlinkAction<?> linkAction = new AbstractHyperlinkAction<Object>() {

            public void actionPerformed(ActionEvent e) {
                // do nothing
            }

            @Override
            protected void installTarget() {
                super.installTarget();
                setShortDescription(getName());
            }

        };
        HyperlinkProvider provider = new HyperlinkProvider(linkAction,
                Object.class);
        CellContext context = new TableCellContext();
        context.replaceValue("dummy");
        JXHyperlink label = provider.getRendererComponent(context);
        assertEquals("sanity - tooltip is set to value", "dummy", label
                .getToolTipText());
        assertToolTipManagerNotRegistered(label);
    }


   /**
     * Issue #790-swingx: rendering comps must not be registered with the tooltip manager.
     * 
     * Here: JRendererCheckBox-
     */
    @Test
    public void testToolTipManagerRendererCheckBox() {
       JRendererCheckBox label = new JRendererCheckBox();
       assertNull("sanity - no tooltip", label.getToolTipText());
       String tip = "some tip";
       PropertyChangeReport report = new PropertyChangeReport();
       label.addPropertyChangeListener(report);
       label.setToolTipText(tip);
       // JRendererCheckBox is optimized to not fire any property changes
       assertEquals(0, report.getEventCount());
       // nevertheless, we want the tip set ;-)
       assertEquals(tip, label.getToolTipText());
       assertToolTipManagerNotRegistered(label);
    }

    /**
     * Issue #790-swingx: rendering comps must not be registered with the tooltip manager.
     * 
     * Here: JRendererLabel.
     * 
     */
    @Test
    public void testToolTipManagerRendererLabel() {
       JRendererLabel label = new JRendererLabel();
       assertNull("sanity - no tooltip", label.getToolTipText());
       String tip = "some tip";
       PropertyChangeReport report = new PropertyChangeReport();
       label.addPropertyChangeListener(report);
       label.setToolTipText(tip);
       // JRendererLabel is optimized to not fire any property changes
       assertEquals(0, report.getEventCount());
       // nevertheless, we want the tip set ;-)
       assertEquals(tip, label.getToolTipText());
       assertToolTipManagerNotRegistered(label);
    }

    /**
     * Issue #790-swingx: rendering comps must not be registered with the tooltip manager.
     * 
     * Sanity: cross-check the "normal" effect of setToolTipText which is that the
     * ToolTipManager registers itself as mouseListener with the component.
     * 
     */
    @Test
    public void testToolTipManagerJLabel() {
       JLabel label = new JLabel();
       assertNull("sanity - no tooltip", label.getToolTipText());
       String tip = "some tip";
       PropertyChangeReport report = new PropertyChangeReport();
       label.addPropertyChangeListener(report);
       label.setToolTipText(tip);
       TestUtils.assertPropertyChangeEvent(report, JComponent.TOOL_TIP_TEXT_KEY, null, tip);
       assertToolTipManagerRegistered(label);
    }
    
    /**
     * Asserts that the component is not registered with the ToolTipManager.
     * There's no direct api to check, so we loop through the mouseListeners and
     * assert that none of them is a ToolTipManager. <p>
     * 
     * This is inherently unsafe, as we rely on the implementation detail that
     * the manager registers itself as listener (instead of delegating). That's why 
     * we cross-check a "normal" component's behaviour - if that fails one day, the
     * implementation detail changed.
     * 
     * @param label the component to assert.
     */
    private void assertToolTipManagerNotRegistered(JComponent label) {
        MouseListener[] listeners = label.getMouseListeners();
        for (MouseListener l : listeners) {
            assertEquals("registered with tooltipManager", false,
                    l instanceof ToolTipManager);
        }
    }
    
    /**
     * Asserts that the component is registered with the ToolTipManager.
     * There's no direct api to check, so we loop through the mouseListeners and
     * assert that none of them is a ToolTipManager. <p>
     * 
     * This is inherently unsafe, as we rely on the implementation detail that
     * the manager registers itself as listener (instead of delegating). That's why 
     * we cross-check a "normal" component's behaviour - if that fails one day, the
     * implementation detail changed.
     * 
     * @param label the component to assert.
     */
    private void assertToolTipManagerRegistered(JComponent label) {
        MouseListener[] listeners = label.getMouseListeners();
        int managerCount = 0;
        for (MouseListener l : listeners) {
            if (l instanceof ToolTipManager) {
                managerCount++;
            }
        }
        assertEquals("registered with tooltipManager", 1, managerCount);
    }
    
    /**
     * Issue #768-swingx: cleanup access to string representation of provider.
     * 
     */
    @Test
    public void testLabelProviderGetString() {
        StringValue sv = new StringValue() {

            public String getString(Object value) {
                return "funnyconstant ... haha";
            }
            
        };
        CellContext context =  new TableCellContext();
        ComponentProvider<JLabel> provider = new LabelProvider(sv);
        JLabel label = provider.getRendererComponent(context);
        assertEquals(sv.getString(context.getValue()), label.getText());
        assertEquals(sv.getString(context.getValue()), provider.getString(context.getValue()));
    }
    
    /**
     * Issue #768-swingx: cleanup access to string representation of provider.
     * 
     */
    @Test
    public void testButtonProviderGetString() {
        StringValue sv = new StringValue() {

            public String getString(Object value) {
                return "funnyconstant ... haha";
            }
            
        };
        CellContext context =  new TableCellContext();
        ComponentProvider<AbstractButton> provider = new CheckBoxProvider(sv);
        AbstractButton label = provider.getRendererComponent(context);
        assertEquals(sv.getString(context.getValue()), label.getText());
        assertEquals(sv.getString(context.getValue()), provider.getString(context.getValue()));
    }
    
    /**
     * Issue #768-swingx: cleanup access to string representation of provider.
     * 
     */
    @Test
    public void testWrappingProviderGetString() {
        StringValue sv = new StringValue() {

            public String getString(Object value) {
                return "funnyconstant ... haha";
            }
            
        };
        CellContext context =  new TableCellContext();
        ComponentProvider<WrappingIconPanel> provider = new WrappingProvider(sv);
        assertEquals(sv.getString(context.getValue()), provider.getString(context.getValue()));
    }
    
    
    /**
     * Issue #768-swingx: cleanup access to string representation of provider.
     * 
     * WrappingProvider must do the same "unwrapping" magic in getString as in 
     * getRendereringComponent.
     */
    @Test
    public void testWrappingProviderGetStringFromNode() {
        StringValue sv = new StringValue() {

            public String getString(Object value) {
                if (value instanceof Point) {
                    return "x of Point: " + ((Point) value).x;
                }
                return StringValues.TO_STRING.getString(value);
            }
            
        };
        CellContext context =  new TableCellContext();
        Point p = new Point(10, 20);
        context.value = new DefaultMutableTreeNode(p);
        WrappingProvider provider = new WrappingProvider(sv);
        assertEquals(sv.getString(p), provider.getString(context.getValue()));
    }

    /**
     * Issue #768-swingx: cleanup access to string representation of provider.
     * 
     */
    @Test
    public void testWrappingProviderGetStringNotNullValue() {
        StringValue sv = new StringValue() {

            public String getString(Object value) {
                return String.valueOf(value) + "added ... ";
            }
            
        };
        CellContext context =  new TableCellContext();
        context.value = "dummy";
        ComponentProvider<WrappingIconPanel> provider = new WrappingProvider(sv);
        assertEquals(sv.getString(context.getValue()), provider.getString(context.getValue()));
    }
    
    /**
     * Issue #769-swingx: support null icons.
     * 
     * enhance WrappingProvider to allow real null values. 
     * 
     */
    @Test
    public void testWrappingProviderNullIcon() {
       CellContext context = new TreeCellContext();
       WrappingProvider provider = new WrappingProvider(IconValues.NONE);
       WrappingIconPanel comp = provider.getRendererComponent(context);
       assertEquals(null, comp.getIcon());
    }
    

    /**
     * Added pref/min/max size to list of properties which 
     * must be reset by the DefaultVisuals.
     */
    @Test
    public void testResetPreferredSize() {
        DefaultVisuals<JComponent> visuals = new DefaultVisuals<JComponent>();
        JComponent label = new  JLabel("somevalue");
        visuals.configureVisuals(label, new TableCellContext());
        Dimension prefSize = label.getPreferredSize();
        Dimension newPrefSize = new Dimension(prefSize.width + 100, prefSize.height + 100);
        label.setPreferredSize(newPrefSize);
        visuals.configureVisuals(label, new TableCellContext());
        assertEquals("default visual config must reset prefSize", prefSize, label.getPreferredSize());
    }
    
    /**
     * Test provider respect converter. 
     * 
     * Here: must show the
     * description instead of setting the icon.
     *
     * PENDING JW: revisit .. Icon.EMPTY vs. Icon.NONE: what exactly is the expected 
     * behaviour?
     */
    @Test
    public void testLabelProviderRespectStringValueNoIcon() {
        ImageIcon icon = (ImageIcon) XTestUtils.loadDefaultIcon();
        icon.setDescription("description");
        LabelProvider provider = new LabelProvider(
                new MappedValue(StringValues.TO_STRING, IconValues.NONE));
        TableCellContext context = new TableCellContext();
        context.value = icon;
        JLabel label = provider.getRendererComponent(context);
        Icon i = label.getIcon();
        assertTrue("icon must be empty", i instanceof EmptyIcon);
        assertEquals("icon must have no width", 0, i.getIconWidth());
        assertEquals("icon must have no height", 0, i.getIconHeight());
        assertEquals("label text must be default to-string", StringValues.TO_STRING.getString(icon), label.getText());
    }
    
    /**
     * Test provider respect converter. 
     * 
     * Here: must show the icon and empty text.
     *
     */
    @Test
    public void testLabelProviderRespectIconValueNoString() {
        ImageIcon icon = (ImageIcon) XTestUtils.loadDefaultIcon();
        icon.setDescription("description");
        LabelProvider provider = new LabelProvider(
                new MappedValue(StringValues.EMPTY, IconValues.ICON));
        TableCellContext context = new TableCellContext();
        context.value = icon;
        JLabel label = provider.getRendererComponent(context);
        assertEquals(icon, label.getIcon());
        assertEquals("label text must be empty", StringValues.EMPTY.getString(icon), label.getText());
    }
    
    /**
     * Test provider respect converter. 
     * 
     * Here: must show both description and icon.
     *
     */
    @Test
    public void testLabelProviderRespectStringIconValueBoth() {
        ImageIcon icon = (ImageIcon) XTestUtils.loadDefaultIcon();
        icon.setDescription("description");
        LabelProvider provider = new LabelProvider(
                new MappedValue(StringValues.TO_STRING, IconValues.ICON));
        TableCellContext context = new TableCellContext();
        context.value = icon;
        JLabel label = provider.getRendererComponent(context);
        assertEquals(icon, label.getIcon());
        assertEquals(StringValues.TO_STRING.getString(icon), label.getText());
    }

    /**
     * WrappingProvider: test custom icon
     */
    @Test
    public void testWrappingProviderIcon() {
        final Icon icon = XTestUtils.loadDefaultIcon();
        IconValue iv = new IconValue() {
            public Icon getIcon(Object value) {
                return icon;
            }};
        WrappingProvider provider = new WrappingProvider(iv);
        CellContext context = new TreeCellContext();
        WrappingIconPanel iconPanel = provider.getRendererComponent(context);
        assertEquals(icon, iconPanel.getIcon());
        // NOTE: do not test type of wrapper's stringValue - it's a mappedValue if icon present!
        assertEquals("", provider.getStringValue().getString(icon));
    }
    
    /**
     * WrappingProvider: test custom icon
     */
    @Test
    public void testWrappingProviderIconAndContent() {
        final Icon icon = XTestUtils.loadDefaultIcon();
        IconValue iv = new IconValue() {
            public Icon getIcon(Object value) {
                return icon;
            }};
            
        WrappingProvider provider = new WrappingProvider(iv, StringValues.DATE_TO_STRING);
        CellContext context = new TreeCellContext();
        WrappingIconPanel iconPanel = provider.getRendererComponent(context);
        assertEquals(icon, iconPanel.getIcon());
        // NOTE: do not test type of wrapper's stringValue - it's a mappedValue if icon present!
        assertEquals("", provider.getStringValue().getString(icon));
    }
    
    /**
     * WrappingProvider: 
     * test wrappee and its state after instantiation.
     */
    @Test
    public void testWrappingProviderWrappeeConstructors() {
        IconValue iv = IconValues.FILE_ICON;
        LabelProvider delegate = new LabelProvider(StringValues.DATE_TO_STRING);
        assertWrappingProviderState(new WrappingProvider(delegate),
                null, null, delegate, true);
        assertWrappingProviderState(new WrappingProvider(delegate, false), 
                null, null, delegate, false);
        assertWrappingProviderState(new WrappingProvider(iv, delegate, false), 
                iv, null, delegate, false);
        assertWrappingProviderState(new WrappingProvider(iv, (ComponentProvider<?>) null, false), 
                iv, null, null, false);
        assertWrappingProviderState(new WrappingProvider(null, (ComponentProvider<?>) null, false), 
                null, null, null, false);
    }
   
    /**
     * WrappingProvider: 
     * test wrappee and its state after instantiation.
     */
    @Test
    public void testWrappingProviderWrappeeStringValueConstructors() {
        IconValue iv = IconValues.FILE_ICON;
        StringValue sv = StringValues.DATE_TO_STRING;
        assertWrappingProviderState(new WrappingProvider(iv),
                iv, null, null, true);
        assertWrappingProviderState(new WrappingProvider(iv, sv), 
                iv, sv, null, true);
        assertWrappingProviderState(new WrappingProvider(sv), 
                null, sv, null, true);
        assertWrappingProviderState(new WrappingProvider(null, sv), 
                null, sv, null, true);
        assertWrappingProviderState(new WrappingProvider(iv, null), 
                iv, null, null, true);
        assertWrappingProviderState(new WrappingProvider(null, null), 
                null, null, null, true);
//        assertWrappingProviderState(new WrappingProvider(false, iv), 
//                iv, null, null, false);
    }
    
    /**
     * WrappingProvider: 
     * test provider's state after instantiation with empty constructor.
     */
    @Test
    public void testWrappingProviderEmptyConstructor() {
        assertWrappingProviderState(new WrappingProvider(), 
                null, null, null, true);
//        assertWrappingProviderState(new WrappingProvider(false), 
//                null, null, null, false);
    }
    
    private void assertWrappingProviderState(WrappingProvider provider, 
            IconValue iv, StringValue sv, ComponentProvider<?> delegate, boolean unwrap) {
        if (iv == null) {
            assertEquals("default StringValue must be empty", StringValues.EMPTY, 
                    provider.getStringValue());
        } else {
            assertTrue("provider's StringValue must be a MappedValue containing the IconValue, but" +
            		"was " + provider.getStringValue().getClass(),
                    provider.getStringValue() instanceof MappedValue);
            // can't access MappedValue's delegates - usually don't need to
//            assertEquals(iv, ((MappedValue) provider.getStringValue()).iconDelegate);
        }
        if (delegate == null) {
            assertTrue("default wrappee must be LabelProvider but was " + 
                    provider.getWrappee().getClass(), 
               provider.getWrappee() instanceof LabelProvider);
        } else {
            assertEquals("wrappee must be set", delegate, provider.getWrappee());
        }
        if (sv != null) {
            assertEquals("wrappee's StringValue must be configured to given", 
                    sv, provider.getWrappee().getStringValue());
        }
        assertEquals(unwrap, provider.getUnwrapUserObject());
    }
    /**
     * Test text and boolean taken from MappedValue
     */
    @Test
    public void testButtonProviderCustomValue() {
        // some object to map
        String identifier = "dummyID";
        final TableColumnExt column = new TableColumnExt();
        column.setTitle(identifier);
        BooleanValue bv = new BooleanValue(){

            public boolean getBoolean(Object value) {
                return column.isVisible();
            }
            
        };
        StringValue sv = new StringValue() {

            public String getString(Object value) {
                return column.getTitle();
            }
            
        };
        CheckBoxProvider provider = new CheckBoxProvider(new MappedValue(sv, null, bv));
        TableCellContext context = new TableCellContext();
        context.value = column;
        AbstractButton button = provider.getRendererComponent(context);
        assertEquals(column.isVisible(), button.isSelected());
        assertEquals(column.getTitle(), button.getText());
    }
    
    /**
     * safety net for addition of BooleanValue. Defaults to
     * selected from boolean value, text empty.
     * 
     * here: constructor with null stringValue and align
     *
     */
    @Test
    public void testButtonProviderDefaultsTwoConstructor() {
        CheckBoxProvider provider = new CheckBoxProvider(null, JLabel.RIGHT);
        TableCellContext context = new TableCellContext();
        AbstractButton button = provider.getRendererComponent(context);
        // empty context
        assertFalse(button.isSelected());
        assertEquals("", button.getText());
        // boolean true
        context.value = true;
        provider.getRendererComponent(context);
        assertEquals(context.value, button.isSelected());
        assertEquals("", button.getText());
        // non-boolean
        context.value = "dummy";
        provider.getRendererComponent(context);
        assertFalse(button.isSelected());
        assertEquals("", button.getText());
    }
    

    /**
     * safety net for addition of BooleanValue. Defaults to
     * selected from boolean value, text empty.
     * 
     * here: constructor with null stringValue
     *
     */
    @Test
    public void testButtonProviderDefaultsOneConstructor() {
        CheckBoxProvider provider = new CheckBoxProvider(null);
        TableCellContext context = new TableCellContext();
        AbstractButton button = provider.getRendererComponent(context);
        // empty context
        assertFalse(button.isSelected());
        assertEquals("", button.getText());
        // boolean true
        context.value = true;
        provider.getRendererComponent(context);
        assertEquals(context.value, button.isSelected());
        assertEquals("", button.getText());
        // non-boolean
        context.value = "dummy";
        provider.getRendererComponent(context);
        assertFalse(button.isSelected());
        assertEquals("", button.getText());
    }
    

    /**
     * safety net for addition of BooleanValue. Defaults to
     * selected from boolean value, text empty.
     * 
     * here: parameterless constructor
     *
     */
    @Test
    public void testButtonProviderDefaultsEmptyConstructor() {
        CheckBoxProvider provider = new CheckBoxProvider();
        TableCellContext context = new TableCellContext();
        AbstractButton button = provider.getRendererComponent(context);
        // empty context
        assertFalse(button.isSelected());
        assertEquals("", button.getText());
        // boolean true
        context.value = true;
        provider.getRendererComponent(context);
        assertEquals(context.value, button.isSelected());
        assertEquals("", button.getText());
        // non-boolean
        context.value = "dummy";
        provider.getRendererComponent(context);
        assertFalse(button.isSelected());
        assertEquals("", button.getText());
    }

    /**
     * test ButtonProvider default constructor and properties.
     *
     */
    @Test
    public void testButtonProviderConstructor() {
        ComponentProvider<?> provider = new CheckBoxProvider();
        assertEquals(JLabel.CENTER, provider.getHorizontalAlignment());
        assertEquals(StringValues.EMPTY, provider.getStringValue());
       
    }

    /**
     * Test provider property reset: borderPainted.
     *
     */
    @Test
    public void testButtonProviderBorderPainted() {
        CheckBoxProvider provider = new CheckBoxProvider();
        TableCellContext context = new TableCellContext();
        AbstractButton button = provider.getRendererComponent(context);
        assertEquals(provider.isBorderPainted(), button.isBorderPainted());
        button.setBorderPainted(!provider.isBorderPainted());
        provider.getRendererComponent(context);
        assertEquals(provider.isBorderPainted(), button.isBorderPainted());
    }
    /**
     * Test provider property reset: horizontal.
     *
     */
    @Test
    public void testButtonProviderHorizontalAlignment() {
        CheckBoxProvider provider = new CheckBoxProvider();
        CellContext context = new TableCellContext();
        AbstractButton button = provider.getRendererComponent(context);
        assertEquals(provider.getHorizontalAlignment(), button.getHorizontalAlignment());
        button.setHorizontalAlignment(JLabel.TRAILING);
        provider.getRendererComponent(context);
        assertEquals(provider.getHorizontalAlignment(), button.getHorizontalAlignment());
    }
   /**
     * use convenience constructor where appropriate: 
     * test clients code (default renderers in JXTable).
     * 
     *
     */
    @Test
    public void testConstructorClients() {
        JXTable table = new JXTable();
        // Number
        DefaultTableRenderer numberRenderer = (DefaultTableRenderer) table.getDefaultRenderer(Number.class);
        JLabel label = (JLabel) numberRenderer.getTableCellRendererComponent(table, null, false, false, 0, 0);
        assertEquals(JLabel.RIGHT, label.getHorizontalAlignment());
        assertEquals(StringValues.NUMBER_TO_STRING, numberRenderer.componentController.getStringValue());
        // icon
        DefaultTableRenderer iconRenderer = (DefaultTableRenderer) table.getDefaultRenderer(Icon.class);
        JLabel iconLabel = (JLabel) iconRenderer.getTableCellRendererComponent(table, null, false, false, 0, 0);
        assertEquals(JLabel.CENTER, iconLabel.getHorizontalAlignment());
        // JW: wrong assumption after starting to fix #590-swingx
        // LabelProvider should respect formatter
//        assertEquals(StringValue.TO_STRING, iconRenderer.componentController.getToStringConverter());
    }
    
    /**
     * Test constructors: convenience constructor.
     */
    @Test
    public void testConstructorConvenience() {
        FormatStringValue sv = new FormatStringValue(DateFormat.getTimeInstance());
        int align = JLabel.RIGHT;
        LabelProvider provider = new LabelProvider(sv, align);
        assertEquals(align, provider.getHorizontalAlignment());
        assertEquals(sv, provider.getStringValue());
    }
    
    /**
     * Test constructors: parameterless.
     */
    @Test
    public void testConstructorDefault() {
        LabelProvider provider = new LabelProvider();
        assertEquals(JLabel.LEADING, provider.getHorizontalAlignment());
        assertEquals(StringValues.TO_STRING, provider.getStringValue());
    }
    
    /**
     * Test constructors: convenience constructor.
     */
    @Test
    public void testConstructorAlignment() {
        int align = JLabel.RIGHT;
        LabelProvider provider = new LabelProvider(align);
        assertEquals(align, provider.getHorizontalAlignment());
        assertEquals(StringValues.TO_STRING, provider.getStringValue());
    }
    
    /**
     * Test constructors: convenience constructor.
     */
    @Test
    public void testConstructorStringValue() {
        FormatStringValue sv = new FormatStringValue(DateFormat.getTimeInstance());
        LabelProvider provider = new LabelProvider(sv);
        assertEquals(JLabel.LEADING, provider.getHorizontalAlignment());
        assertEquals(sv, provider.getStringValue());
    }

    /**
     * test that default visual config clears the tooltip.
     *
     */
    @Test
    public void testResetTooltip() {
        DefaultVisuals<JComponent> visuals = new DefaultVisuals<JComponent>();
        JComponent label = new  JLabel("somevalue");
        label.setToolTipText("tooltip");
        visuals.configureVisuals(label, new TableCellContext());
        assertNull("default visual config must clear tooltiptext", label.getToolTipText());
    }
    
    /**
     * Test if all collaborators can cope with null component on CellContext.
     *
     */
    @Test
    public void testEmptyContext() {
        // test LabelProvider
        // same for list and table
        assertEmptyContext(new LabelProvider());
        assertEmptyContext(new CheckBoxProvider());
        assertEmptyContext(new HyperlinkProvider());
    }
    
    private void assertEmptyContext(ComponentProvider<?> provider) {
        DefaultListRenderer renderer = new DefaultListRenderer(provider);
        renderer.getListCellRendererComponent(null, null, -1, false, false);
        // treeRenderer - use the same provider, can't do in real life, 
        // the providers component is added to the wrapping provider's component.
        DefaultTreeRenderer treeRenderer = new DefaultTreeRenderer(provider);
        treeRenderer.getTreeCellRendererComponent(null, null, false, false, false, -1, false);
        // had an NPE in TreeCellContext focus border 
        treeRenderer.getTreeCellRendererComponent(null, null, false, false, false, -1, true);
        // random test - the input parameters don't map to a legal state
        treeRenderer.getTreeCellRendererComponent(null, new Object(), false, true, false, 2, true);
    }
    /**
     * Test doc'ed constructor behaviour of default tree renderer.
     *
     */
    @Test
    public void testDefaultTreeRendererConstructors() {
        DefaultTreeRenderer renderer = new DefaultTreeRenderer();
        assertTrue(renderer.componentController instanceof WrappingProvider);
        renderer = new DefaultTreeRenderer(StringValues.DATE_TO_STRING);
        assertTrue(renderer.componentController instanceof WrappingProvider);
        // wrong assumption - we are wrapping...
//        assertSame(FormatStringValue.DATE_TO_STRING, renderer.componentController.formatter);
        assertSame(StringValues.DATE_TO_STRING, ((WrappingProvider) renderer.componentController).wrappee.formatter);
        ComponentProvider<?> controller = new CheckBoxProvider();
        renderer = new DefaultTreeRenderer(controller);
        assertSame(controller, renderer.componentController);
    }

    /**
     * Test doc'ed constructor behaviour of default list renderer.
     *
     */
    @Test
    public void testDefaultListRendererConstructors() {
        DefaultListRenderer renderer = new DefaultListRenderer();
        assertTrue(renderer.componentController instanceof LabelProvider);
        renderer = new DefaultListRenderer(StringValues.DATE_TO_STRING);
        assertTrue(renderer.componentController instanceof LabelProvider);
        assertSame(StringValues.DATE_TO_STRING, renderer.componentController.formatter);
        ComponentProvider<?> controller = new CheckBoxProvider();
        renderer = new DefaultListRenderer(controller);
        assertSame(controller, renderer.componentController);
    }

    /**
     * Issue 970-swingx: add convenience constructors with IconValues
     * Test doc'ed constructor behaviour of default list renderer.
     *
     */
    @Test
    public void testDefaultListRendererConstructorWithIconValue() {
        DefaultListRenderer renderer = new DefaultListRenderer(StringValues.TO_STRING, IconValues.ICON);
        assertTrue(renderer.componentController instanceof LabelProvider);
        LabelProvider provider = (LabelProvider) renderer.componentController;
        ImageIcon icon = (ImageIcon) XTestUtils.loadDefaultIcon();
        icon.setDescription("description");
        TableCellContext context = new TableCellContext();
        context.value = icon;
        JLabel label = provider.getRendererComponent(context);
        assertEquals(icon, label.getIcon());
        assertEquals(StringValues.TO_STRING.getString(icon), label.getText());
    }

    /**
     * Issue #970-swingx: add convenience constructors with IconValues
     * Test doc'ed constructor behaviour of default list renderer.
     *
     */
    @Test
    public void testDefaultListRendererConstructorWithIconValueAndAlign() {
        DefaultListRenderer renderer = new DefaultListRenderer(StringValues.TO_STRING, IconValues.ICON, JLabel.TRAILING);
        assertTrue(renderer.componentController instanceof LabelProvider);
        LabelProvider provider = (LabelProvider) renderer.componentController;
        ImageIcon icon = (ImageIcon) XTestUtils.loadDefaultIcon();
        icon.setDescription("description");
        TableCellContext context = new TableCellContext();
        context.value = icon;
        JLabel label = provider.getRendererComponent(context);
        assertEquals(icon, label.getIcon());
        assertEquals(StringValues.TO_STRING.getString(icon), label.getText());
        assertEquals(JLabel.TRAILING, label.getHorizontalAlignment());
    }
    /**
     * Test doc'ed constructor behaviour of default table renderer.
     *
     */
    @Test
    public void testDefaultTableRendererConstructors() {
        DefaultTableRenderer renderer = new DefaultTableRenderer();
        assertTrue(renderer.componentController instanceof LabelProvider);
        renderer = new DefaultTableRenderer(StringValues.DATE_TO_STRING);
        assertTrue(renderer.componentController instanceof LabelProvider);
        assertSame(StringValues.DATE_TO_STRING, renderer.componentController.formatter);
        ComponentProvider<?> controller = new CheckBoxProvider();
        renderer = new DefaultTableRenderer(controller);
        assertSame(controller, renderer.componentController);
    }

    /**
     * Issue #970-swingx: text constructors with IconValues
     * Test doc'ed constructor behaviour of default table renderer.
     *
     */
    @Test
    public void testDefaultTableRendererConstructorWithIconValue() {
        DefaultTableRenderer renderer = new DefaultTableRenderer(StringValues.TO_STRING, IconValues.ICON);
        assertTrue(renderer.componentController instanceof LabelProvider);
        LabelProvider provider = (LabelProvider) renderer.componentController;
        ImageIcon icon = (ImageIcon) XTestUtils.loadDefaultIcon();
        icon.setDescription("description");
        TableCellContext context = new TableCellContext();
        context.value = icon;
        JLabel label = provider.getRendererComponent(context);
        assertEquals(icon, label.getIcon());
        assertEquals(StringValues.TO_STRING.getString(icon), label.getText());
    }

    /**
     * Issue #970-swingx: text constructors with IconValues
     * Test doc'ed constructor behaviour of default table renderer.
     *
     */
    @Test
    public void testDefaultTableRendererConstructorWithIconValueAndAlign() {
        DefaultTableRenderer renderer = new DefaultTableRenderer(StringValues.TO_STRING, IconValues.ICON, JLabel.TRAILING);
        assertTrue(renderer.componentController instanceof LabelProvider);
        LabelProvider provider = (LabelProvider) renderer.componentController;
        ImageIcon icon = (ImageIcon) XTestUtils.loadDefaultIcon();
        icon.setDescription("description");
        TableCellContext context = new TableCellContext();
        context.value = icon;
        JLabel label = provider.getRendererComponent(context);
        assertEquals(icon, label.getIcon());
        assertEquals(StringValues.TO_STRING.getString(icon), label.getText());
        assertEquals(JLabel.TRAILING, label.getHorizontalAlignment());
    }

    /**
     * public methods of <code>ComponentProvider</code> must cope
     * with null context. Here: test getRenderingComponent in WrappingProvider.
     *
     */
    @Test
    public void testGetWrappingComponentNullContext() {
        WrappingProvider provider = new WrappingProvider();
        assertEquals(provider.rendererComponent, provider.getRendererComponent(null));
    }

    /**
     * public methods of <code>ComponentProvider</code> must cope
     * with null context. Here: test getRenderingComponent in LabelProvider.
     */
    @Test
    public void testGetComponentNullContext() {
        ComponentProvider<?> controller = new LabelProvider();
        assertEquals(controller.rendererComponent, controller.getRendererComponent(null));
    }
    
    /**
     * test doc'ed behaviour on defaultVisuals configure:
     * NPE on null context.
     *
     */
    @Test
    public void testConfigureVisualsNullContext() {
        DefaultVisuals<JLabel> controller = new DefaultVisuals<JLabel>();
        try {
            controller.configureVisuals(new JLabel(), null);
            fail("renderer controller must throw NPE on null context");
        } catch (NullPointerException e) {
            // this is what we expect
        } catch (Exception e) {
            fail("renderer controller must throw NPE on null context - instead: " + e);
        }
    }
    /**
     * test doc'ed behaviour on defaultVisuals configure:
     * NPE on null component.
     *
     */
    @Test
    public void testConfigureVisualsNullComponent() {
        DefaultVisuals<JLabel> controller = new DefaultVisuals<JLabel>();
        try {
            controller.configureVisuals(null, new TableCellContext());
            fail("renderer controller must throw NPE on null component");
        } catch (NullPointerException e) {
            // this is what we expect
        } catch (Exception e) {
            fail("renderer controller must throw NPE on null component - instead: " + e);
        }
    }
    
    /**
     * RendererLabel NPE with null Graphics. 
     * Fail-fast NPE in label.paintComponentWithPainter.
     *
     */
    @Test
    public void testLabelNPEPaintComponentOpaqueWithPainter() {
        JRendererLabel label = new JRendererLabel();
        label.setOpaque(true);
        label.setPainter(new ShapePainter());
        try {
            label.paintComponent(null);
            fail("invoke paintComponent with null graphics must throw NPE");
        } catch (NullPointerException e) {
            // basically the right thing - but how to test the fail-fast?
        } catch (Exception e) {
            fail("unexpected exception invoke paintcomponent with null" + e);
        }
    }
    /**
     * RendererLabel NPE with null Graphics. 
     * Fail-fast NPE in paintPainter.
     *
     */
    @Test
    public void testLabelNPEPaintComponentWithPainter() {
        JRendererLabel label = new JRendererLabel();
        label.setOpaque(false);
        label.setPainter(new ShapePainter());
        try {
            label.paintComponent(null);
            fail("invoke paintComponent with null graphics must throw NPE");
        } catch (NullPointerException e) {
            // basically the right thing - but how to test the fail-fast?
        } catch (Exception e) {
            fail("unexpected exception invoke paintcomponent with null" + e);
        }
    }

    /**
     * RendererLabel NPE with null Graphics. 
     * NPE in label.paintComponentWithPainter finally block.
     *
     */
    @Test
    public void testButtonNPEPaintComponentOpaqueWithPainter() {
        JRendererCheckBox  checkBox = new JRendererCheckBox();
        checkBox.setOpaque(true);
        checkBox.setPainter(new ShapePainter());
        try {
            checkBox.paintComponent(null);
            fail("invoke paintComponent with null graphics must throw NPE");
        } catch (NullPointerException e) {
            // basically the right thing - but how to test the fail-fast?
        } catch (Exception e) {
            fail("unexpected exception invoke paintcomponent with null" + e);
        }
    }

    /**
     * RendererCheckBox NPE with null Graphics. NPE in
     * label.paintComponentWithPainter finally block.
     * 
     */
    @Test
    public void testButtonNPEPaintComponentWithPainter() {
        JRendererCheckBox checkBox = new JRendererCheckBox();
        checkBox.setOpaque(false);
        checkBox.setPainter(new ShapePainter());
        try {
            checkBox.paintComponent(null);
            fail("invoke paintComponent with null graphics must throw NPE");
        } catch (NullPointerException e) {
            // basically the right thing - but how to test the fail-fast?
        } catch (Exception e) {
            fail("unexpected exception invoke paintcomponent with null" + e);
        }
    }

}
