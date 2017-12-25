/*
 * Created on 26.06.2008
 *
 */
package org.jdesktop.swingx.renderer;

import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.util.logging.Logger;

import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.test.ComponentTreeTableModel;
import org.jdesktop.test.AncientSwingTeam;

/**
 * 
 * 
 * @author Jeanette Winzenburg
 */
public class DNDRendererVisualCheck extends InteractiveTestCase {

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(DNDRendererVisualCheck.class.getName());
    public static void main(String[] args) {
        DNDRendererVisualCheck test = new DNDRendererVisualCheck();
        try {
            test.runInteractiveTests();
//            test.runInteractiveTests("interactive.*DropOnVisuals.*");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Issue #766-swingx: drop image is blinking over hierarchical column.
     * 
     * Core bug: <a
     * href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6700748">#6700748</a>
     * 
     * Hacks around, use one or other: 
     * - the usual performance optimization (override validate etc to do nothing) 
     * - rendering component return false on isVisible
     */
    public void interactiveDropOnHierachicalColumnBlinks() {
        JXTreeTable xTable = new JXTreeTable(new ComponentTreeTableModel(
                new JXFrame()));
        installTransferHandler(xTable);
        xTable.putClientProperty(JXTreeTable.DROP_HACK_FLAG_KEY, Boolean.TRUE);
        xTable.expandAll();
        xTable.setVisibleColumnCount(10);
        JXFrame frame = wrapWithScrollingInFrame(xTable,
                "TreeTable hierarchical: blink on drop over?");
        JTextField textField = new JTextField(
                "drag me over hierarchical column ..");
        textField.setDragEnabled(true);
        addStatusComponent(frame, textField);
        frame.setVisible(true);
    }

    /**
     * Issue #766-swingx: drop image is blinking over hierarchical column.
     * 
     * Check WrappingIconPanel in tree: blinking. Can't override the validate
     * because it's needed to layout the component correctly.
     */
    public void interactiveDropOnWrappingIconPanelTree() {
        JXTree xTable = new JXTree(new ComponentTreeTableModel(new JXFrame()));
        installTransferHandler(xTable);
        xTable.setCellRenderer(new DefaultTreeRenderer());
        xTable.expandAll();
        JXFrame frame = wrapWithScrollingInFrame(xTable,
                "Wrapping renderer: blink on drop over?");
        JTextField textField = new JTextField("drag me over tree ....");
        textField.setDragEnabled(true);
        addStatusComponent(frame, textField);
        frame.setVisible(true);
    }

    /**
     * Issue #766-swingx: drop image is blinking over hierarchical column.
     * 
     * Check Hyperlink in Table: okay, because it has the validate et al methods
     * overridden to do nothing.
     */
    public void interactiveDropOnHyperlinkTableBlinks() {
        JXTable xTable = new JXTable(new AncientSwingTeam());
        installTransferHandler(xTable);
        xTable.getColumn(0).setCellRenderer(
                new DefaultTableRenderer(new HyperlinkProvider()));
        JXFrame frame = wrapWithScrollingInFrame(xTable,
                "Hyperlink as renderer: blink on drop over?");
        JTextField textField = new JTextField("drag me over hyperlink....");
        textField.setDragEnabled(true);
        addStatusComponent(frame, textField);
        frame.setVisible(true);
    }

    /**
     * Issue #766-swingx: drop image is blinking over hierarchical column.
     * 
     * Test WrappingIconPanel in Table.
     */
    public void interactiveDropOnWrappingIconPanelTableBlinks() {
        JXTable xTable = new JXTable(new AncientSwingTeam());
        installTransferHandler(xTable);
        xTable.getColumn(0).setCellRenderer(
                new DefaultTableRenderer(new WrappingProvider()));
        JXFrame frame = wrapWithScrollingInFrame(xTable,
                "WrappingIconPanel as renderer: blink on drop over?");
        JTextField textField = new JTextField("drag me over first column....");
        textField.setDragEnabled(true);
        addStatusComponent(frame, textField);
        frame.setVisible(true);
    }

    /**
     * Issue #766-swingx: drop image is blinking over hierarchical column.
     * 
     * Check plain JLabel as renderer in Table.
     */
    public void interactiveDropOnPlainLabelTableBlinks() {
        JXTable xTable = new JXTable(new AncientSwingTeam());
        installTransferHandler(xTable);
        TableCellRenderer renderer = new TableCellRenderer() {
            JLabel label = new JLabel();
            public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean isSelected, boolean hasFocus,
                    int row, int column) {
                label.setText(String.valueOf(value));
                return label;
            }
            
        };
        xTable.getColumn(0).setCellRenderer(renderer);
        JXFrame frame = wrapWithScrollingInFrame(xTable,
        "Plain label as renderer: blink on drop over?");
        JTextField textField = new JTextField("drag me over first column....");
        textField.setDragEnabled(true);
        addStatusComponent(frame, textField);
        frame.setVisible(true);
    }
    /**
     * Issue #370-swingx: drop-on not shown correctly.
     * 
     * Check core default renderer as renderer in Table.
     */
    public void interactiveDropOnVisualsTable() {
        JXTable xTable = new JXTable(new AncientSwingTeam());
        installTransferHandler(xTable);
        xTable.getColumn(0).setCellRenderer(new DefaultTableCellRenderer());
        JTable table = new JTable(new AncientSwingTeam());
        installTransferHandler(table);
//        xTable.setDropMode(DropMode.USE_SELECTION);
//        table.setDropMode(DropMode.USE_SELECTION);
        xTable.setDropMode(DropMode.ON_OR_INSERT);
        table.setDropMode(DropMode.ON_OR_INSERT);
        JXFrame frame = wrapWithScrollingInFrame(xTable, table,
                "JXTable (first column core renderer) <-> core table: drop-on appearance");
        JTextField textField = new JTextField("drag me over first column....");
        textField.setDragEnabled(true);
        addStatusComponent(frame, textField);
        show(frame);
    }
    
    /**
     * Issue #370-swingx: drop-on not shown correctly.
     * 
     * Check core default renderer as renderer in Table.
     */
    public void interactiveDropOnVisualsList() {
        JXList xTable = new JXList(AncientSwingTeam.createNamedColorListModel());
        installTransferHandler(xTable);
        JList table = new JList(xTable.getModel());
        installTransferHandler(table);
//        xTable.setDropMode(DropMode.USE_SELECTION);
//        table.setDropMode(DropMode.USE_SELECTION);
        xTable.setDropMode(DropMode.ON_OR_INSERT);
        table.setDropMode(DropMode.ON_OR_INSERT);
        JXFrame frame = wrapWithScrollingInFrame(xTable, table,
                "JXList <-> core list: drop-on appearance");
        JTextField textField = new JTextField("drag me over first column....");
        textField.setDragEnabled(true);
        addStatusComponent(frame, textField);
        show(frame);
    }
    
    /**
     * Issue #370-swingx: drop-on not shown correctly.
     * 
     * Check core default renderer as renderer in Table.
     */
    public void interactiveDropOnVisualsTree() {
        JXTree xTable = new JXTree(AncientSwingTeam.createNamedColorTreeModel());
        installTransferHandler(xTable);
        xTable.setCellRenderer(new DefaultTreeRenderer());
        JTree table = new JTree(xTable.getModel());
        installTransferHandler(table);
//        xTable.setDropMode(DropMode.USE_SELECTION);
//        table.setDropMode(DropMode.USE_SELECTION);
        xTable.setDropMode(DropMode.ON_OR_INSERT);
        table.setDropMode(DropMode.ON_OR_INSERT);
        JXFrame frame = wrapWithScrollingInFrame(xTable, table,
                "JXTree <-> core tree: drop-on appearance");
        JTextField textField = new JTextField("drag me over first column....");
        textField.setDragEnabled(true);
        addStatusComponent(frame, textField);
        show(frame);
    }
    

    private void installTransferHandler(JComponent xTable) {
        TransferHandler tableTransfer = new TransferHandler() {
            @Override
            public boolean canImport(JComponent comp,
                    DataFlavor[] transferFlavors) {
                return true;
            }

        };

        xTable.setTransferHandler(tableTransfer);
    }
    
    public void testDummy() {
        // do nothing - placeholder for empty issues
    }

}
