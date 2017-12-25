/*
 * Created on 28.06.2006
 *
 */
package org.jdesktop.swingx;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SortOrder;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.plaf.metal.MetalBorders;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.table.DefaultTableColumnModelExt;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.test.AncientSwingTeam;

public class JXTableHeaderIssues extends JXTableHeaderTest {
    @SuppressWarnings("all")
    private static final Logger LOG = Logger
            .getLogger(JXTableHeaderIssues.class.getName());
    public static void main(String args[]) {
        
        JXTableHeaderIssues test = new JXTableHeaderIssues();
//        setSystemLF(true);
        try {
          test.runInteractiveTests();
         //   test.runInteractiveTests("interactive.*Siz.*");
         //   test.runInteractiveTests("interactive.*Render.*");
//            test.runInteractiveTests("interactive.*Auto.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        } 
    }
    
    
    /**
     * Issue #683-swingx: autoscroll if columns are dragged outside.
     * 
     * Enabled if autoscroll property is true. Changed default to true.
     * Temporarily disabled - clashes with table's horizontalScrollEnabled.
     */
    public void testAutoscrollsDefaultEmptyConstructor() {
        JXTableHeader header = new JXTableHeader();
        assertTrue(header.getAutoscrolls());
    }
    
    /**
     * Issue #1154-swingx: Regression after switching to Mustang.
     * was: Issue #31 (swingx): clicking header must not sort if table !enabled.
     *
     * OOOKayyyy ... this is a core issue: mouse listener in BasicTableHeaderUI
     * don't respect the header's enabled property (reported with review id 1605216).
     * It's possible to drag, resize, sort even if disabled. The only "normal"
     * behaviour is that the resizing cursor isn't showing.
     */
    public void interactiveTestDisabledTableSorting() {
        final JXTable table = new JXTable(new AncientSwingTeam());
        table.setEnabled(false);
        table.setColumnControlVisible(true);
        Action toggleAction = new AbstractAction("Toggle Enabled") {

            public void actionPerformed(ActionEvent e) {
                table.setEnabled(!table.isEnabled());
                
            }
            
        };
        JXFrame frame = wrapWithScrollingInFrame(table, "Disabled tabled: no sorting");
        addAction(frame, toggleAction);
        frame.setVisible(true);  
    }
    
    /**
     * Quick proof-of-concept: JXTableHeader can go dirty and
     * suppress moving of "docked" column.
     *
     */
    public void interactiveDockColumn() {
        final JXTableHeader header = new JXTableHeader() {

            private int oldDistance;


            @Override
            public void setDraggedDistance(int distance) {
                oldDistance = getDraggedDistance();
                if (isDocked(getDraggedColumn())) {
                    distance = 0;
                } 
                if (isDraggedOverDocked(distance)) {
                    distance = 0;
                    setDraggedColumn(null);
                }
                super.setDraggedDistance(distance);
            }

            private boolean isDraggedOverDocked(int distance) {
                if (getDraggedColumn() == null) return false;
                int dragPosition = getDragX(distance);
                int columnUnder = columnAtPoint(new Point(dragPosition, 0));
                if (columnUnder >= 0) {
                    return  isDocked(getColumnModel().getColumn(columnUnder));
                }
                return false;
            }
            
            private int getDragX(int distance) {
                DefaultTableColumnModelExt columnModel = (DefaultTableColumnModelExt) getColumnModel();
                List<TableColumn> columns = columnModel.getColumns(false);
                int startX = 0;
                for (int i = 0; i < columns.size(); i++) {
                    if (columns.get(i) == getDraggedColumn()) {
                        // at the wrong column the very moment 
                        // before the dock neighbour is 
                        // actually replaced with the second next dock neighbour
                        // need to add the stop-column width as well
                        if (distance * oldDistance < 0) {
                            startX += columns.get(i).getWidth();
                        }
                         break;
                    }
                    startX += columns.get(i).getWidth();
                }
                return startX + distance;
            }

            
            private boolean isDocked(TableColumn column) {
                if (column instanceof TableColumnExt) {
                    return Boolean.TRUE.equals(((TableColumnExt) column).getClientProperty("docked"));
                }
                return false;
            }
        };
        final TableColumnModel cModel = new DefaultTableColumnModelExt() {

            @Override
            public void moveColumn(int columnIndex, int newIndex) {
                if (isDocked(columnIndex) || isDocked(newIndex)) return;
                super.moveColumn(columnIndex, newIndex);
            }

            private boolean isDocked(int columnIndex) {
                TableColumnExt column = getColumnExt(columnIndex);
                if (column != null) {
                    return Boolean.TRUE.equals(column.getClientProperty("docked"));
                }
                return false;
            }
            
        };
        JXTable table = new JXTable(10, 5) {

            @Override
            protected JTableHeader createDefaultTableHeader() {
                header.setColumnModel(columnModel);
                return header;
            }

            @Override
            protected TableColumnModel createDefaultColumnModel() {
                return cModel;
            }
            
            
        };
        table.getColumnExt(0).putClientProperty("docked", Boolean.TRUE);
        showWithScrollingInFrame(table, "block dragging");
    }

    /**
     * Issue #281-swingx, Issue #334-swing: 
     * header should be auto-repainted on changes to
     * header title, value. Must update size if appropriate.
     * 
     * still not solved: core #4292511 - autowrapping is weird,
     * even with the swingx fix 
     *
     */
    public void interactiveUpdateHeaderAndSizeRequirements() {
        
        final String[] alternate = { 
//                "simple", 
                "<html><b>This is a test of a large label to see if it wraps </font></b>",
                "simple", 
                //                "<html><center>Line 1<br>Line 2</center></html>" 
                };
        final JXTable table = new JXTable(10, 2);
        table.getColumn(0).setHeaderValue(alternate[0]);
        table.getColumn(1).setHeaderValue(alternate[1]);
        
        JXFrame frame = wrapWithScrollingInFrame(table, "update header");
        Action action = new AbstractAction("update headervalue") {
            boolean first;
            public void actionPerformed(ActionEvent e) {
                table.getColumn(1).setHeaderValue(first ? alternate[0] : alternate[1]);
                first = !first;
                
            }
            
        };
        addAction(frame, action);
        frame.setVisible(true);
        
    }

    /**
     * Issue 337-swingx: header heigth depends on sort icon (for ocean only?) 
     * Looks like a problem in MetalBorders.TableHeaderBorder: extends AbstractBorder but
     * does not override getBorderInsets(comp, insets) which is used by the labelUI getPrefSize
     * to determine the insets and calc the view rect.
     * 
     */
    @SuppressWarnings("unused")
    public void interactiveSortedPreferredHeight() {
        final JXTable table = new JXTable(10, 1);
        table.getColumnExt(0).setPreferredWidth(200);
        final JTable other = new JTable(10, 1);
//        other.setAutoCreateRowSorter(true);
        other.getColumnModel().getColumn(0).setPreferredWidth(200);
        final DefaultTableCellRenderer renderer =(DefaultTableCellRenderer) other.getTableHeader().getDefaultRenderer(); 
        Component comp = renderer.getTableCellRendererComponent(other, "A", false, false, -1, 0);
        final Border border = renderer.getBorder();
        final Border emptyBorder = BorderFactory.createEmptyBorder(0, 0, 0, 20);
        TableCellRenderer wrapper = new TableCellRenderer() {

            public Component getTableCellRendererComponent(JTable mtable,
                    Object value, boolean isSelected, boolean hasFocus,
                    int row, int column) {
                JComponent comp = (JComponent) renderer.getTableCellRendererComponent(mtable, value, 
                        isSelected, hasFocus, row, column);
                if (table.getSortedColumn() == null) {
                    comp.setBorder(border);
                } else {
                    comp.setBorder(new CompoundBorder(border, emptyBorder));
                }
                return comp;
            }
            
        };
        other.getTableHeader().setDefaultRenderer(wrapper);
        Action action = new AbstractActionExt("toggle sorter order") {

            public void actionPerformed(ActionEvent e) {
                if (table.getSortedColumn() == null) {
                    table.toggleSortOrder(0);
                } else {
                    table.resetSortOrder();
                }
                table.getTableHeader().revalidate();
                other.getTableHeader().revalidate();
                
            }
        };
        JXFrame frame = wrapWithScrollingInFrame(table, other, "xheader <--> header border height");
        addAction(frame, action);
        frame.setVisible(true);
    }


    /**
     * Issue #271-swingx: make sort triggering mouseEvents
     * customizable.
     * 
     * added SortGestureRecognizer.
     * 
     * No longer supported - need to re-think (maybe could push core addition?)
     * Anyway, re-opened the old issue.
     *
     */
//    public void interactiveSortGestureRecognizer() {
//        final JXTable table = new JXTable(10, 2);
//        JXFrame frame = wrapWithScrollingInFrame(table, "Sort Gesture customization");
//        Action action = new AbstractAction("toggle default/custom recognizer") {
//            boolean hasCustom;
//            public void actionPerformed(ActionEvent e) {
//                SortGestureRecognizer recognizer = null;
//                if (!hasCustom) {
//                    hasCustom = !hasCustom;
//                    recognizer = new SortGestureRecognizer() {
//                        /**
//                         * allow double clicks to trigger a sort.
//                         */
//                        @Override
//                        public boolean isSortOrderGesture(MouseEvent e) {
//                            return e.getClickCount() <= 2;
//                        }
//
//                        /**
//                         * Disable reset gesture.
//                         */
//                        @Override
//                        public boolean isResetSortOrderGesture(MouseEvent e) {
//                            return false;
//                        }
//
//                        /**
//                         * ignore modifiers.
//                         */
//                        @Override
//                        public boolean isToggleSortOrderGesture(MouseEvent e) {
//                            return isSortOrderGesture(e);
//                        }
//                        
//                        
//                        
//                    };
//                }
//                ((JXTableHeader) table.getTableHeader()).setSortGestureRecognizer(recognizer);
//                
//            }
//            
//        };
//        addAction(frame, action);
//        frame.setVisible(true);
//        
//    }

    

    /**
     * Issue 337-swingx: header heigth depends on sort icon (for ocean only?) 
     * Looks like a problem in MetalBorders.TableHeaderBorder: extends AbstractBorder but
     * does not override getBorderInsets(comp, insets) which is used by the labelUI getPrefSize
     * to determine the insets and calc the view rect.
     * 
     */
    public void testMetalBorderInsets() {
        JLabel label = new JLabel("sometext");
        AbstractBorder metalBorder = new MetalBorders.TableHeaderBorder();
        assertEquals(metalBorder.getBorderInsets(label), 
                metalBorder.getBorderInsets(label, new Insets(0,0,0,0)));
    }
    
    /**
     * Issue 337-swingx: header heigth depends on sort icon (for ocean only?) 
     * Looks like a problem in MetalBorders.TableHeaderBorder: extends AbstractBorder but
     * does not override getBorderInsets(comp, insets) which is used by the labelUI getPrefSize
     * to determine the insets and calc the view rect.
     * 
     * Here we compound the default metal border
     */
    public void testMetalBorderInsetsHack() {
        JLabel label = new JLabel("sometext");
        AbstractBorder metalBorder = new MetalBorders.TableHeaderBorder();
        CompoundBorder compound = new CompoundBorder(metalBorder, BorderFactory.createEmptyBorder());
        assertEquals(compound.getBorderInsets(label), 
                compound.getBorderInsets(label, new Insets(0,0,0,0)));
    }
    
    /**
     * Issue 337-swingx: header heigth depends on sort icon (for ocean only?) 
     * NOTE: this seems to be independent of the tweaks to xTableHeaders
     *   prefSize.
     */
    public void testSortedPreferredHeight() {
        JXTable table = new JXTable(10, 2);
        JXTableHeader tableHeader = (JXTableHeader) table.getTableHeader();
        Dimension dim = tableHeader.getPreferredSize();
        table.setSortOrder(0, SortOrder.ASCENDING);
        assertEquals("Header pref height must be unchanged if sorted",
                dim.height, tableHeader.getPreferredSize().height);
    }

}
