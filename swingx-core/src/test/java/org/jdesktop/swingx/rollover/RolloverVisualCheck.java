/*
 * Created on 08.12.2010
 *
 */
package org.jdesktop.swingx.rollover;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.TransferHandler;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.CompoundHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.HighlightPredicate.AndHighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.renderer.DefaultListRenderer;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.DefaultTreeRenderer;
import org.jdesktop.swingx.treetable.FileSystemModel;
import org.jdesktop.test.AncientSwingTeam;

public class RolloverVisualCheck extends InteractiveTestCase {

    private TableModel sortableTableModel;
    private Highlighter backgroundHighlighter;
    private Highlighter foregroundHighlighter;
    private ListModel listModel;
    private FileSystemModel treeTableModel;

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(RolloverVisualCheck.class.getName());

    public static void main(String[] args) {
        RolloverVisualCheck test = new RolloverVisualCheck();
        try {
            test.runInteractiveTests();
//            test.runInteractive("Drag");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Issue #1249-swingx: RolloverProducer clears rollover point when inserting child
     * 
     * Happens f.i. when starting an edit.
     * 
     */
    public void interactiveExitToChild() {
        JXTable table = new JXTable(new AncientSwingTeam());
        table.addHighlighter(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, 
                Color.MAGENTA, null, Color.MAGENTA, null));
        JXFrame frame = wrapWithScrollingInFrame(table, "rollover child");
        addStatusMessage(frame, "edit under mouse, move");
        show(frame);
    }
    
    /**
     * Issue #1193-swingx: rollover state not updated on scrolling/mouseWheel
     * 
     * visualize behaviour on 
     * - scrolling (with mouse wheel)
     * - resizing (added custom actions)
     */
    public void interactiveTreeRolloverScroll() {
        final JXTree table = new JXTree(new FileSystemModel());
        table.setCellRenderer(new DefaultTreeRenderer());
        table.setRolloverEnabled(true);
        table.addHighlighter(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, Color.YELLOW, null));
        final JXFrame frame = getResizableFrame(table);
        show(frame);
    }

    /**
     * Issue #1193-swingx: rollover state not updated on scrolling/mouseWheel
     * 
     * visualize behaviour on 
     * - scrolling (with mouse wheel)
     * - resizing (added custom actions)
     */
    public void interactiveListRolloverScroll() {
        final JXList table = new JXList(AncientSwingTeam.createNamedColorListModel());
        table.setRolloverEnabled(true);
        table.addHighlighter(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, Color.YELLOW, null));
        final JXFrame frame = getResizableFrame(table);
        show(frame);
    }
    
    /**
     * Issue #1193-swingx: rollover state not updated on scrolling/mouseWheel
     * 
     * visualize behaviour on 
     * - scrolling (with mouse wheel)
     * - resizing (added custom actions)
     */
    public void interactiveTableRolloverScroll() {
        final JXTable table = new JXTable(new AncientSwingTeam());
        table.setEditable(false);
        table.setHorizontalScrollEnabled(true);
        table.addHighlighter(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, Color.YELLOW, null));
        final JXFrame frame = getResizableFrame(table);
        show(frame);
    }



    /**
     * @param table
     * @return
     */
    private JXFrame getResizableFrame(final JComponent table) {
        final JXFrame frame = wrapWithScrollingInFrame(table, "rollover and wheel");
        Action hd = new AbstractAction("horizontalDecrease") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                Dimension dim = frame.getSize();
                dim.width -= 50;
                frame.setSize(dim);
            }
            
        };
        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
                KeyStroke.getKeyStroke("A"), "horizontalDecrease");
        table.getActionMap().put("horizontalDecrease", hd);
        Action hi = new AbstractAction("horizontalDecrease") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                Dimension dim = frame.getSize();
                dim.width += 50;
                frame.setSize(dim);
            }
            
        };
        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
                KeyStroke.getKeyStroke("D"), "horizontalIncrease");
        table.getActionMap().put("horizontalIncrease", hi);
        Action vd = new AbstractAction("verticalDecrease") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                Dimension dim = frame.getSize();
                dim.height -= 20;
                frame.setSize(dim);
            }
            
        };
        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
                KeyStroke.getKeyStroke("W"), "verticalDecrease");
        table.getActionMap().put("verticalDecrease", vd);
        Action vi = new AbstractAction("verticalIncrease") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                Dimension dim = frame.getSize();
                dim.height += 20;
                frame.setSize(dim);
            }
            
        };
        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
                KeyStroke.getKeyStroke("S"), "verticalIncrease");
        table.getActionMap().put("verticalIncrease", vi);
        addStatusComponent(frame, new JLabel("Horizontal Resize: A <--> D "));
        addStatusComponent(frame, new JLabel("Vertical Resize: W <--> S "));
        return frame;
    }


    
    public void interactiveTableRollover() {
        JXTable table = new JXTable(sortableTableModel);
        final CompoundHighlighter compoundHighlighter = new CompoundHighlighter(foregroundHighlighter);
        table.setHighlighters(compoundHighlighter);
        JXFrame frame = wrapWithScrollingInFrame(table, "Table with rollover");
        Action toggleAction = new AbstractAction("toggle foreground/background") {
            boolean isBackground;
            public void actionPerformed(ActionEvent e) {
                if (isBackground) {
                    compoundHighlighter.addHighlighter(foregroundHighlighter);
                    compoundHighlighter.removeHighlighter(backgroundHighlighter);
                } else {
                    compoundHighlighter.addHighlighter(backgroundHighlighter);
                    compoundHighlighter.removeHighlighter(foregroundHighlighter);
                    
                }
                isBackground = !isBackground;
                
            }
            
        };
        addAction(frame, toggleAction);
        frame.setVisible(true);
    }
    
    
    public void interactiveListRollover() {
        final JXList table = new JXList(listModel);
        table.setRolloverEnabled(true);
        final CompoundHighlighter compoundHighlighter = new CompoundHighlighter(foregroundHighlighter);
        table.setHighlighters(compoundHighlighter);
        JXFrame frame = wrapWithScrollingInFrame(table, "List with rollover");
        Action toggleAction = new AbstractAction("toggle foreground/background") {
            boolean isBackground;
            public void actionPerformed(ActionEvent e) {
                if (isBackground) {
                    compoundHighlighter.addHighlighter(foregroundHighlighter);
                    compoundHighlighter.removeHighlighter(backgroundHighlighter);
                } else {
                    compoundHighlighter.addHighlighter(backgroundHighlighter);
                    compoundHighlighter.removeHighlighter(foregroundHighlighter);
                    
                }
                isBackground = !isBackground;
                
            }
            
        };
        addAction(frame, toggleAction);
        frame.setVisible(true);
    }
    
    /**
     * Issue ??: background highlighter not working in JXTree.
     * Not really an issue, works fine with SwingX renderer.
     */
    public void interactiveTreeRollover() {
        final JXTree table = new JXTree(treeTableModel);
        // Note: we need swingx renderer to make some decorations work reliably.
        table.setCellRenderer(new DefaultTreeRenderer());
        table.setRolloverEnabled(true);
        table.setComponentPopupMenu(createPopup());
        final CompoundHighlighter compoundHighlighter = new CompoundHighlighter(foregroundHighlighter);
        table.setHighlighters(compoundHighlighter);
        JTree tree = new JTree(treeTableModel);
        tree.setComponentPopupMenu(createPopup());
        JXFrame frame = wrapWithScrollingInFrame(table, tree, "JXTree (at left) with rollover");
        Action toggleAction = new AbstractAction("toggle foreground/background") {
            boolean isBackground;
            public void actionPerformed(ActionEvent e) {
                if (isBackground) {
                    compoundHighlighter.addHighlighter(foregroundHighlighter);
                    compoundHighlighter.removeHighlighter(backgroundHighlighter);
                } else {
                    compoundHighlighter.addHighlighter(backgroundHighlighter);
                    compoundHighlighter.removeHighlighter(foregroundHighlighter);
                    
                }
                isBackground = !isBackground;
                
            }
            
        };
        addAction(frame, toggleAction);
        addMessage(frame, "background highlight not working in JXTree (with core renderer)");
        frame.setVisible(true);
    }

    public JPopupMenu createPopup() {
        JPopupMenu popup = new JPopupMenu();
        popup.add("dummy");
        return popup;
    }
    
    public void interactiveTreeTableRollover() {
        final JXTreeTable table = new JXTreeTable(treeTableModel);
        final CompoundHighlighter compoundHighlighter = new CompoundHighlighter(foregroundHighlighter);
        table.setHighlighters(compoundHighlighter);
        JXFrame frame = wrapWithScrollingInFrame(table, "TreeTable with rollover");
        Action toggleAction = new AbstractAction("toggle foreground/background") {
            boolean isBackground;
            public void actionPerformed(ActionEvent e) {
                if (isBackground) {
                    compoundHighlighter.addHighlighter(foregroundHighlighter);
                    compoundHighlighter.removeHighlighter(backgroundHighlighter);
                } else {
                    compoundHighlighter.addHighlighter(backgroundHighlighter);
                    compoundHighlighter.removeHighlighter(foregroundHighlighter);
                    
                }
                isBackground = !isBackground;
                
            }
            
        };
        addAction(frame, toggleAction);
        frame.setVisible(true);
    }

    /**
     * Example for per-cell rollover decoration in JXTreeTable.
     */
    public void interactiveTreeTableRolloverHierarchical() {
        final JXTreeTable table = new JXTreeTable(treeTableModel);
        HighlightPredicate andPredicate = new AndHighlightPredicate(
                new HighlightPredicate.ColumnHighlightPredicate(0),
                HighlightPredicate.ROLLOVER_ROW
                );
        final Highlighter foregroundHighlighter = new ColorHighlighter(andPredicate, null,
                Color.MAGENTA);
        final Highlighter backgroundHighlighter = new ColorHighlighter(andPredicate, Color.YELLOW,
                null);
        table.setHighlighters(foregroundHighlighter);
        JXFrame frame = wrapWithScrollingInFrame(table, "TreeTable with rollover - effect hierarchical column");
        Action toggleAction = new AbstractAction("toggle foreground/background") {
            boolean isBackground;
            public void actionPerformed(ActionEvent e) {
                if (isBackground) {
                    table.setHighlighters(foregroundHighlighter);
                } else {
                    table.setHighlighters(backgroundHighlighter);
                    
                }
                isBackground = !isBackground;
                
            }
            
        };
        addAction(frame, toggleAction);
        frame.setVisible(true);
    }
    

    
    /**
     * Issue #456-swingx: Rollover highlighter not showing while dragging.
     * 
     * Example how to force rollover while dragging (NOT dnd - in that case the visuals
     * are controlled by the dnd mechanism): subclass JXList to install a custom 
     * RolloverProducer
     */
    public void interactiveEnforceRolloverWhileDragging() {
        final JXList list = new JXList(AncientSwingTeam.createNamedColorListModel()) {

            /** 
             * @inherited <p>
             */
            @Override
            protected RolloverProducer createRolloverProducer() {
                ListRolloverProducer producer = new ListRolloverProducer() {

                    /** 
                     * @inherited <p>
                     */
                    @Override
                    public void mouseDragged(MouseEvent e) {
                        super.mouseDragged(e);
                        updateRollover(e, ROLLOVER_KEY, false);
                    }
                    
                };
                return producer;
            }
            
        };
        list.setVisibleRowCount(list.getElementCount());
        list.setRolloverEnabled(true);
        list.addHighlighter(new ColorHighlighter(
                HighlightPredicate.ROLLOVER_ROW, 
                Color.MAGENTA, null, Color.MAGENTA, null));
        
        TransferHandler handler = new TransferHandler() {
            
            /** 
             * @inherited <p>
             */
            @Override
            public boolean canImport(TransferSupport support) {
                return true;
            }
            
        };
        list.setTransferHandler(handler);
        JXFrame frame = wrapWithScrollingInFrame(list, "force Rollover while dragging");
        JTextField textField = new JTextField("just something to drag ...", 40);
        textField.setDragEnabled(true);
        addStatusComponent(frame, textField);
        show(frame);
    }
    
    /**
     * Issue #456-swingx: Rollover highlighter not showing while dragging.
     * 
     * The expected behaviour: 
     * - don't show the xRollover while dragging during a dnd,
     * - update the rollover to the released.
     */
    public void interactiveRolloverWhileDragging() {
        final JXList list = new JXList(AncientSwingTeam.createNamedColorListModel());
        list.setVisibleRowCount(list.getElementCount());
        list.setRolloverEnabled(true);
        list.addHighlighter(new ColorHighlighter(
                HighlightPredicate.ROLLOVER_ROW, 
                Color.MAGENTA, null, Color.MAGENTA, null));

        TransferHandler handler = new TransferHandler() {

            /** 
             * @inherited <p>
             */
            @Override
            public boolean canImport(TransferSupport support) {
                return true;
            }
            
        };
        list.setTransferHandler(handler);
        JXFrame frame = wrapWithScrollingInFrame(list, "rollover and drag-enabled (fake drop)");
        JTextField textField = new JTextField("just something to drag ...", 40);
        textField.setDragEnabled(true);
        addStatusComponent(frame, textField);
        show(frame);
    }
    
    /**
     * Issue #1387-swingx: Rollover click-on-release-after drag.
     * 
     * PENDING JW: what's the expected behaviour?
     */
    public void interactiveRolloverClickAfterDrag() {
        JXList list = new JXList(AncientSwingTeam.createNamedColorListModel());
        list.setVisibleRowCount(list.getElementCount());
        list.setRolloverEnabled(true);
        list.addHighlighter(new ColorHighlighter(
                HighlightPredicate.ROLLOVER_ROW, 
                Color.MAGENTA, null, Color.MAGENTA, null));

        TransferHandler handler = new TransferHandler() {

            /** 
             * @inherited <p>
             */
            @Override
            public boolean canImport(TransferSupport support) {
                return true;
            }
            
        };
        list.setTransferHandler(handler);
        final JXFrame frame = wrapWithScrollingInFrame(list, "release-after-drag must not trigger clicked");
        // rollover-enabled default renderer
        // Note JW: this implicitly changes the cursor to the hand-cursor
        DefaultListRenderer renderer = new DefaultListRenderer() {
            @Override
            public void doClick() {
                JOptionPane.showMessageDialog(frame, "Clicked");
            }
            
            @Override
            public boolean isEnabled() {
                return true;
            }
        };
        list.setCellRenderer(renderer);
        JTextField textField = new JTextField("just something to drag ...", 40);
        textField.setDragEnabled(true);
        addStatusComponent(frame, textField);
        Action action = new AbstractAction("drag into me - am I called on release?") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                LOG.info("triggered..." + ((Component) e.getSource()).getName());
            }
        };
        JButton button = new JButton(action);
        button.setName("first");
        addStatusComponent(frame, button);
        JButton other = new JButton(action);
        other.setName("second");
        addStatusComponent(frame, other);
        show(frame);
    }
    
    /**
     * Issue #1374-swingx: Rollover click must be disabled if target disabled
     * 
     * Visual test for JXTable, adapted from reporter's test case
     */
    public void interactiveRolloverDisabledClickList() {
        JXList table = new JXList(AncientSwingTeam.createNamedColorListModel());
        table.setEnabled(false);
        table.setRolloverEnabled(true);
        final JXFrame frame = showWithScrollingInFrame(table, "disable list must not trigger rollover renderer");
        // rollover-enabled default renderer
        DefaultListRenderer renderer = new DefaultListRenderer() {
            @Override
            public void doClick() {
                JOptionPane.showMessageDialog(frame, "Click");
            }
            
            @Override
            public boolean isEnabled() {
                return true;
            }
        };
        table.setCellRenderer(renderer);
        addEnabledToggle(frame, table);
    }
    
    /**
     * Issue #1374-swingx: Rollover click must be disabled if target disabled
     * 
     * Visual test for JXTable, adapted from reporter's test case
     */
    public void interactiveRolloverDisabledClickTree() {
        JXTree table = new JXTree();
        table.setEnabled(false);
        table.setRolloverEnabled(true);
        final JXFrame frame = showWithScrollingInFrame(table, "disable tree must not trigger rollover renderer");
        // rollover-enabled default renderer
        DefaultTreeRenderer renderer = new DefaultTreeRenderer() {
            @Override
            public void doClick() {
                JOptionPane.showMessageDialog(frame, "Click");
            }
            
            @Override
            public boolean isEnabled() {
                return true;
            }
        };
        table.setCellRenderer(renderer);
        addEnabledToggle(frame, table);
    }
    
    
    /**
     * Issue #1374-swingx: Rollover click must be disabled if target disabled
     * 
     * Visual test for JXTable, adapted from reporter's test case
     */
    public void interactiveRolloverDisabledClickTable() {
        JXTable table = new JXTable(new AncientSwingTeam());
        table.setEditable(false);
        table.setEnabled(false);
        final JXFrame frame = showWithScrollingInFrame(table, "disable table must not trigger rollover renderer");
        // rollover-enabled default renderer
        DefaultTableRenderer renderer = new DefaultTableRenderer() {
            @Override
            public void doClick() {
                JOptionPane.showMessageDialog(frame, "Click");
            }
            
            @Override
            public boolean isEnabled() {
                return true;
            }
        };
        table.setDefaultRenderer(Object.class, renderer);
        addEnabledToggle(frame, table);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        sortableTableModel = new AncientSwingTeam();
        listModel = new AbstractListModel() {

            public int getSize() {
                return sortableTableModel.getRowCount();
            }

            public Object getElementAt(int index) {
                return sortableTableModel.getValueAt(index, 0);
            }
            
        };
        treeTableModel = new FileSystemModel();
        foregroundHighlighter = new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, null,
                Color.MAGENTA);
        backgroundHighlighter = new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, Color.YELLOW,
                null);
     }
    

}
