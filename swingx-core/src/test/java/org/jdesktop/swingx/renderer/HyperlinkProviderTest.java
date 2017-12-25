/*
 * $Id: HyperlinkProviderTest.java 3473 2009-08-27 13:17:10Z kleopatra $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.renderer;

import java.awt.Point;
import java.net.URL;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.hyperlink.EditorPaneLinkVisitor;
import org.jdesktop.swingx.hyperlink.LinkModel;
import org.jdesktop.swingx.hyperlink.LinkModelAction;
import org.jdesktop.swingx.rollover.RolloverProducer;
import org.jdesktop.test.AncientSwingTeam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test around hyperlink rendering.
 * 
 * PENDING JW: how does this relate to JXHyperlinkTest? Merge? 
 * Move to renderer package?
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class HyperlinkProviderTest extends InteractiveTestCase {
    private static final Logger LOG = Logger.getLogger(HyperlinkProviderTest.class
            .getName());

    private LinkModel link;

    // flag used in setup to explicitly choose LF
    private boolean defaultToSystemLF;

    /**
     * renderers must cope with type mismatch.
     *
     */
    @Test
    public void testMixedValueTypes() {
        LinkModelAction<?> action = new LinkModelAction<LinkModel>(new EditorPaneLinkVisitor());
        TableCellRenderer linkRenderer = new DefaultTableRenderer(
                new HyperlinkProvider(action, LinkModel.class));
        linkRenderer.getTableCellRendererComponent(null, "stringonly", false, false, -1, -1);

    }

    /**
     * sanity: can cope with subclasses.
     * a side-effect: renderers should cope with null table.
     */
    @Test
    public void testSubclassedValueTypes() {
        LinkModelAction<?> action = new LinkModelAction<LinkModel>(new EditorPaneLinkVisitor());
        TableCellRenderer renderer = new DefaultTableRenderer(
                new HyperlinkProvider(action, LinkModel.class));
        LinkModel link = new LinkModel() {
            
        };
        renderer.getTableCellRendererComponent(null, link, false, false, -1, -1);

    }

    /**
     * Issue #183-swingx. test if the selection background is updated on
     * changing LF.
     * 
     */
    @Test
    public void testSelectionBackground() {
        JXTable table = new JXTable(2, 2);
        TableCellRenderer linkRenderer = new DefaultTableRenderer(new HyperlinkProvider());
        table.getColumnModel().getColumn(0).setCellRenderer(linkRenderer);
        JXHyperlink hyperlink = (JXHyperlink) linkRenderer
                .getTableCellRendererComponent(table, link, true, false, 1, 0);
        // JW: asking any background without knowing transparency state is
        // useless!
        assertTrue("renderer comp must be opaque", hyperlink.isOpaque());
        assertEquals("background must be table selection background", table
                .getSelectionBackground(), hyperlink.getBackground());
        hyperlink = (JXHyperlink) linkRenderer.getTableCellRendererComponent(
                table, link, false, false, 1, 0);
        assertEquals("background must be table background", table
                .getBackground(), hyperlink.getBackground());

    }

    /**
     * Issue #183-swingx. test if the selection background is updated on
     * changing LF.
     * 
     */
    @Test
    public void testRendererComponentPropertiesAfterLFChange() {
        JXTable table = new JXTable(2, 2);
        TableCellRenderer linkRenderer = new DefaultTableRenderer(new HyperlinkProvider());
        table.getColumnModel().getColumn(0).setCellRenderer(linkRenderer);
        // sanity: same as set
        assertSame(linkRenderer, table.getCellRenderer(1, 0));
        JXHyperlink hyperlink = (JXHyperlink) table.prepareRenderer(
                linkRenderer, 1, 0);
        // KEEP to remember
        // - JW: asking for the background really doesn't make sense -
        // will not show if comp isn't opaque!
        // assertEquals("background must be table selection background",
        // selectionBackground, hyperlink.getBackground());
        // need to check the properties which are set differently in
        // LinkRenderer
        assertTrue("renderer comp must be opaque", hyperlink.isOpaque());
        assertTrue("renderer must paint border", hyperlink.isBorderPainted());
        String lf = UIManager.getLookAndFeel().getName();
        // switch LF
        setSystemLF(!defaultToSystemLF);
        if (lf.equals(UIManager.getLookAndFeel().getName())) {
            LOG.info("cannot run rendererComponentPropertiesAfterLFChange - equal LF" + lf);
            return;
        }
        SwingUtilities.updateComponentTreeUI(table);
        // sanity: same as set
        assertSame(linkRenderer, table.getCellRenderer(1, 0));
        hyperlink = (JXHyperlink) table.prepareRenderer(linkRenderer, 1, 0);
        // assert that the changed properties survived the LF switch
        assertTrue("renderer comp must be opaque", hyperlink.isOpaque());
        assertTrue("renderer must paint border", hyperlink.isBorderPainted());

    }

    @Test
    public void testRolloverRecognition() {
        JXTable table = new JXTable(2, 2);
        TableCellRenderer linkRenderer = new DefaultTableRenderer(new HyperlinkProvider());
        table.getColumnModel().getColumn(0).setCellRenderer(linkRenderer);
        JXHyperlink hyperlink = (JXHyperlink) linkRenderer
                .getTableCellRendererComponent(table, link, false, false, 1, 0);
        assertFalse("renderer must not be rollover", hyperlink.getModel()
                .isRollover());
        table.putClientProperty(RolloverProducer.ROLLOVER_KEY, new Point(0, 1));
        hyperlink = (JXHyperlink) linkRenderer.getTableCellRendererComponent(
                table, link, false, false, 1, 0);
        assertTrue("renderer must be rollover", hyperlink.getModel()
                .isRollover());
    }

    /**
     * Issue #183-swingx. visual check if the selection background is updated on
     * changing LF. 
     */
    public void interactiveTableSelectionBackgroundOnLF() {
        final JXTable table = new JXTable(new AncientSwingTeam());
        TableCellRenderer linkRenderer = new DefaultTableRenderer(new HyperlinkProvider());
        table.getColumnModel().getColumn(0).setCellRenderer(linkRenderer);
        table.setRowSelectionInterval(1, 1);
        final JXFrame frame = wrapWithScrollingInFrame(table, "test background must change with LAF");
        frame.setVisible(true);

    }

    public static void main(String[] args) throws Exception {
        // setSystemLF(true);
        HyperlinkProviderTest test = new HyperlinkProviderTest();
        try {
            test.runInteractiveTests();
            // test.runInteractiveTests("interactive.*Table.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        URL url = getClass().getResource("resources/test.html");
        link = new LinkModel("a resource", null, url);
        // make sure we have the same default for each test
        defaultToSystemLF = false;
        setSystemLF(defaultToSystemLF);
    }
}
