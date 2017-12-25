/*
 * $Id: TreeRendererIssues.java 2963 2008-06-24 11:11:51Z kleopatra $
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.painter.ImagePainter;
import org.jdesktop.swingx.test.ComponentTreeTableModel;
import org.jdesktop.swingx.test.XTestUtils;

/**
 * Known/open issues with tree renderer.
 * 
 * @author Jeanette Winzenburg
 */
public class TreeRendererIssues extends InteractiveTestCase {
    
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(TreeRendererIssues.class
            .getName());
    
    private JTree tree;
    private DefaultTreeCellRenderer coreTreeRenderer;
    private DefaultTreeRenderer xTreeRenderer;

    // flag used in setup to explicitly choose LF
    private boolean defaultToSystemLF;

    @Override
    protected void setUp() throws Exception {
//        setSystemLF(true);
//        LOG.info("LF: " + UIManager.getLookAndFeel());
//        LOG.info("Theme: " + ((MetalLookAndFeel) UIManager.getLookAndFeel()).getCurrentTheme());
//        UIManager.put("Tree.drawsFocusBorderAroundIcon", Boolean.TRUE);
        // make sure we have the same default for each test
        defaultToSystemLF = false;
        setSystemLF(defaultToSystemLF);
        tree = new JTree();
        coreTreeRenderer = new DefaultTreeCellRenderer();
        xTreeRenderer = new DefaultTreeRenderer();
    }
    public static void main(String[] args) {
        TreeRendererIssues test = new TreeRendererIssues();
        try {
            test.runInteractiveTests();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public void interactiveTransparentRenderer() throws IOException {
        final JXTree tree = new JXTree(new ComponentTreeTableModel(new JXFrame()));
        tree.setEditable(true);
        StringValue sv = new StringValue() {

            public String getString(Object value) {
                if (value instanceof Component) {
                    return ((Component) value).getName();
                }
                return " - no component - ";
            }};
        DefaultTreeRenderer renderer = new DefaultTreeRenderer(sv);
        tree.setCellRenderer(renderer);
        tree.setForeground(Color.WHITE);
        JXPanel panel = new JXPanel(new BorderLayout());
        ImagePainter imagePainter = new ImagePainter(XTestUtils.loadDefaultImage()); 
        imagePainter.setFillHorizontal(true);
        imagePainter.setFillVertical(true);
        panel.setBackgroundPainter(imagePainter);
        panel.add(new JScrollPane(tree));
        
        JXFrame frame = wrapInFrame(panel, "renderer");
        WrappingProvider provider = (WrappingProvider) renderer.getComponentProvider();
        provider.getWrappee().getRendererComponent(null).setOpaque(false);
        tree.setOpaque(false);
        ((JComponent) tree.getParent()).setOpaque(false);
        ((JComponent) tree.getParent().getParent()).setOpaque(false);
        Action edit = new AbstractActionExt("edit") {

            public void actionPerformed(ActionEvent e) {
                if (tree.isSelectionEmpty()) return;
                TreePath path = tree.getSelectionPath();
                Component comp = (Component) path.getLastPathComponent();
                String oldName = comp.getName();
                if (oldName == null) {
                    oldName = "none";
                }
                String changed = oldName.length() > 60 ? oldName.substring(0, 20) :
                    oldName + "+++++++++++++++++++++++++++++++++++++++++++++";
                tree.getModel().valueForPathChanged(path, changed);
            }
            
        };
        addAction(frame, edit);
        show(frame);
    }
    
    /**
     * Sanity: icons updated on LF change.
     */
    public void testTreeIconsUpdateUI() {
        JXTree tree = new JXTree();
        DefaultTreeRenderer renderer = new DefaultTreeRenderer();
        tree.setCellRenderer(renderer);
        WrappingIconPanel before = (WrappingIconPanel) renderer.getTreeCellRendererComponent(tree, "", false, false, true, -1, false);
        Icon leaf = before.getIcon();
        assertNotNull("sanity", leaf);
        assertEquals("sanity", UIManager.getIcon("Tree.leafIcon"), leaf);
        String lf = UIManager.getLookAndFeel().getName();
        setSystemLF(!defaultToSystemLF);
        if (lf.equals(UIManager.getLookAndFeel().getName())) {
            LOG.info("cannot run test - equal LF" + lf);
            return;
        }
        SwingUtilities.updateComponentTreeUI(tree);
        WrappingIconPanel after = (WrappingIconPanel) renderer.getTreeCellRendererComponent(tree, "", false, false, true, -1, false);
        Icon leafAfter = after.getIcon();
        assertNotNull("sanity", leafAfter);
        assertFalse("sanity", leaf.equals(leafAfter));
        assertEquals("icon must be updated", UIManager.getIcon("Tree.leafIcon"), leafAfter);
    }
    
    /**
     * base interaction with list: renderer uses list's unselected  colors
     * 
     * currently, this test fails because the assumptions are wrong! Core
     * renderer behaves slightly unexpected.
     * 
     *
     */
    public void testTreeRendererExtColors() {
        // prepare standard
        Component coreComponent = coreTreeRenderer.getTreeCellRendererComponent(tree, null,
                false, false, false, 0, false);
        // sanity: known standard behaviour
        assertNull(coreComponent.getBackground());
//        assertNull(coreComponent.getForeground());
        assertNull(tree.getForeground());
        Color uiForeground = UIManager.getColor("Tree.textForeground");
        assertEquals(uiForeground, coreComponent.getForeground());
        // prepare extended
        Component xComponent = xTreeRenderer.getTreeCellRendererComponent(tree, null,
                false, false, false, 0, false);
        // assert behaviour same as standard
//        assertEquals(coreComponent.getBackground(), xComponent.getBackground());
        assertEquals(coreComponent.getForeground(), xComponent.getForeground());
    }

    /**
     * base interaction with list: renderer uses list's unselected custom
     * colors.
     * 
     * currently, this test fails because the assumptions are wrong! Core
     * renderer behaves slightly unexpected.
     * 
     */
    public void testTreeRendererExtTreeColors() {
        Color background = Color.MAGENTA;
        Color foreground = Color.YELLOW;
        tree.setBackground(background);
        tree.setForeground(foreground);
        coreTreeRenderer.setBackgroundNonSelectionColor(background);
        coreTreeRenderer.setTextNonSelectionColor(foreground);
        // prepare standard
        Component coreComponent = coreTreeRenderer
                .getTreeCellRendererComponent(tree, null, false, false, false,
                        0, false);
        // sanity: known standard behaviour
        // background is manually painted
        assertEquals(background, coreComponent.getBackground());
        assertEquals(tree.getForeground(), coreComponent.getForeground());
        // prepare extended
        Component xComponent = xTreeRenderer.getTreeCellRendererComponent(tree,
                null, false, false, false, 0, false);
        // assert behaviour same as standard
        assertEquals(background, xComponent.getBackground());
        assertEquals(foreground, xComponent.getForeground());
    }

}
