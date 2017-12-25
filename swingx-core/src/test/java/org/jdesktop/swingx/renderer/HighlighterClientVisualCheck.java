/*
 * $Id: HighlighterClientVisualCheck.java 4229 2012-08-08 15:27:09Z kleopatra $
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
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXSearchPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.BorderHighlighter;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.CompoundHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.decorator.PatternPredicate;
import org.jdesktop.swingx.decorator.ShadingColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate.ColumnHighlightPredicate;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.painter.AbstractLayoutPainter.HorizontalAlignment;
import org.jdesktop.swingx.renderer.RelativePainterHighlighter.NumberRelativizer;
import org.jdesktop.swingx.rollover.RolloverProducer;
import org.jdesktop.swingx.search.PatternMatcher;
import org.jdesktop.swingx.treetable.FileSystemModel;
import org.jdesktop.swingx.util.PaintUtils;
import org.jdesktop.test.AncientSwingTeam;

/**
 * visual checks of highlighter clients. Mostly by example of JXTable.
 * 
 * @author Jeanette Winzenburg
 */
public class HighlighterClientVisualCheck extends InteractiveTestCase {
    
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(HighlighterClientVisualCheck.class.getName());
    
    protected TableModel tableModel;
    protected Color background = Color.RED;
    protected Color foreground = Color.BLUE;
    
    
    public static void main(String args[]) {
//        UIManager.put("Nimbus.keepAlternateRowColor", Boolean.TRUE);
//      setSystemLF(true);
      HighlighterClientVisualCheck test = new HighlighterClientVisualCheck();
      try {
//          setLookAndFeel("Nimbus");
         test.runInteractiveTests();
//          test.runInteractive("JP");
//          test.runInteractiveTests(".*Striping.*");
//         test.runInteractiveTests(".*ToolTip.*");
//         test.runInteractiveTests("interactive.*Search.*");
//         test.runInteractiveTests("interactive.*BorderHighlighter.*");
      } catch (Exception e) {
          System.err.println("exception when executing interactive tests:");
          e.printStackTrace();
      }
  }

    /**
     * Issue #1295-swingx: JXTable rolloverController repaint incomplete.
     * Column not repainted.
     */
    public void interactiveBorderHighlighterOnRollover() {
        JXTable table = new JXTable(tableModel) {
            int count;
            @Override
            protected void paintComponent(Graphics g) {
                // crude check if the logic of column/row painting
                // in TableRolloverController is working (looks so)
//                LOG.info("paint clip: " + count++ + g.getClip());
                super.paintComponent(g);
            }
            
        };
        final AbstractHighlighter hlRow = new BorderHighlighter(HighlightPredicate.ROLLOVER_ROW, 
//                BorderFactory.createCompoundBorder(
                new MatteBorder(1, 0, 1, 0, Color.lightGray)
//                , BorderFactory.createEmptyBorder(0, 1, 0, 1))
                , true, false );  
        final AbstractHighlighter hlColumn = new ColorHighlighter(HighlightPredicate.ROLLOVER_COLUMN, 
                Color.LIGHT_GRAY, null);
        table.setHighlighters(hlRow, hlColumn);
        JXFrame frame = wrapWithScrollingInFrame(table, "borderHighlgihter on rollover");
        Action toggleCell = new AbstractAction("toggle cell-rollover") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (hlRow.getHighlightPredicate() == HighlightPredicate.ROLLOVER_ROW) {
                    hlRow.setHighlightPredicate(HighlightPredicate.NEVER);
                    hlColumn.setHighlightPredicate(HighlightPredicate.ROLLOVER_CELL);
                } else {
                    hlRow.setHighlightPredicate(HighlightPredicate.ROLLOVER_ROW);
                    hlColumn.setHighlightPredicate(HighlightPredicate.ROLLOVER_COLUMN);
                }
                
            }
        };
        addAction(frame, toggleCell);
        show(frame);
    }
    /**
     * Show variants of border Highlighters.
     *
     */
    public void interactiveTableBorderHighlighter() {
        JXTable table = new JXTable(tableModel);
        table.setVisibleRowCount(table.getRowCount());
        table.setVisibleColumnCount(7);
        table.packAll();
        table.setColumnControlVisible(true);
        
        BorderHighlighter outer = new BorderHighlighter(new HighlightPredicate.ColumnHighlightPredicate(1),
                BorderFactory.createLineBorder(Color.RED, 3));
        BorderHighlighter inner = new BorderHighlighter(new HighlightPredicate.ColumnHighlightPredicate(2),
                BorderFactory.createLineBorder(Color.RED, 3), true, true);
        BorderHighlighter replace = new BorderHighlighter(new HighlightPredicate.ColumnHighlightPredicate(0),
                BorderFactory.createLineBorder(Color.RED, 3), false, true);
        table.setHighlighters(outer, inner, replace);
        showWithScrollingInFrame(table, "Border Highlighters");
    }
    
    /**
     * Show variants of border Highlighters.
     *
     */
    public void interactiveTreeBorderHighlighter() {
        JXTree tree = new JXTree();
        tree.expandAll();
        tree.setVisibleRowCount(tree.getRowCount());
        // need SwingX highlighter
        tree.setCellRenderer(new DefaultTreeRenderer());
        tree.setHighlighters(new BorderHighlighter(BorderFactory.createLineBorder(Color.GREEN, 1)));
        showWithScrollingInFrame(tree, "Border Highlighters");
    }

    /**
     * Multiple Highlighters (shown as example in Javapolis 2007).
     * 
     */
    public void interactiveTablePatternHighlighterJP() {
        JXTable table = new JXTable(tableModel);
        table.setVisibleRowCount(table.getRowCount());
        table.setVisibleColumnCount(7);
        table.packAll();
        table.setColumnControlVisible(true);

        Font font = table.getFont().deriveFont(Font.BOLD | Font.ITALIC);
        Highlighter simpleStriping = HighlighterFactory.createSimpleStriping();
        PatternPredicate patternPredicate = new PatternPredicate("^M", 1);
        ColorHighlighter magenta = new ColorHighlighter(patternPredicate, null,
                Color.MAGENTA, null, Color.MAGENTA);
        FontHighlighter derivedFont = new FontHighlighter(patternPredicate,
                font);
        AbstractHighlighter gradient = createRelativeGradientHighlighter(HorizontalAlignment.LEFT, AncientSwingTeam.INTEGER_COLUMN);
        gradient.setHighlightPredicate(new HighlightPredicate.ColumnHighlightPredicate(AncientSwingTeam.INTEGER_COLUMN));
        
        LOG.info("" + (table.getValueAt(0, 3) instanceof Number));
        Highlighter shading = new ShadingColorHighlighter(
                new HighlightPredicate.ColumnHighlightPredicate(1));

        table.setHighlighters(simpleStriping, magenta, derivedFont, shading //);
                , gradient);
        showWithScrollingInFrame(table, "Multiple Highlighters");
    }

    /**
     * Simulates table by one-list per column.
     * 
     * NOTE: the only purpose is to demonstrate the similarity
     * of highlighter usage across collection components!
     * (shown as example in Javapolis 2007)
     * 
     * @see #interactiveTablePatternHighlighterJP()
     */
    public void interactiveListPatternHighlighterJP() {
        JXTable source = new JXTable(tableModel);
        source.toggleSortOrder(3);
        Font font = source.getFont().deriveFont(Font.BOLD | Font.ITALIC);
        Highlighter simpleStriping = HighlighterFactory.createSimpleStriping();
        String pattern = "^M";
        PatternPredicate patternPredicate = new PatternPredicate(pattern, 0);
        ColorHighlighter magenta = new ColorHighlighter(patternPredicate, null,
                Color.MAGENTA, null, Color.MAGENTA);
        FontHighlighter derivedFont = new FontHighlighter(patternPredicate,
                font);
        Highlighter gradient = createRelativeGradientHighlighter(
                HorizontalAlignment.LEFT, 0);
        Highlighter shading = new ShadingColorHighlighter(
                new HighlightPredicate.ColumnHighlightPredicate(0));
        // create and configure one JXList per column.
        List<JXList> lists = new ArrayList<JXList>();
        // first name
        JXList list0 = new JXList(createListModel(source, 0));
        list0.setHighlighters(simpleStriping);
        lists.add(list0);
        // last name
        JXList list1 = new JXList(createListModel(source, 1));
        list1.setHighlighters(simpleStriping, magenta, derivedFont, shading);
        lists.add(list1);

        // color
        JXList listc = new JXList(createListModel(source, 2));
        listc.setHighlighters(simpleStriping);
        lists.add(listc);

        // number
        JXList listn = new JXList(createListModel(source, AncientSwingTeam.INTEGER_COLUMN));
        listn.setCellRenderer(new DefaultListRenderer(
                StringValues.NUMBER_TO_STRING, JLabel.RIGHT));
        listn.setHighlighters(simpleStriping, gradient);
        lists.add(listn);

        // boolean
        JXList listb = new JXList(createListModel(source, 4));
        listb.setCellRenderer(new DefaultListRenderer(new CheckBoxProvider()));
        listb.setFixedCellHeight(list0.getPreferredSize().height
                / source.getRowCount());
        listb.setHighlighters(simpleStriping, magenta, derivedFont, gradient);
        lists.add(listb);

        JComponent panel = Box.createHorizontalBox();
        for (JXList l : lists) {
            listb.setVisibleRowCount(source.getRowCount());
            l.setFont(source.getFont());
            panel.add(new JScrollPane(l));
        }
        showInFrame(panel, "Multiple Highlighters");
    }

    /**
     * @param right
     * @return
     */
    private RelativePainterHighlighter createRelativeGradientHighlighter(
            HorizontalAlignment right, int column) {
        Color startColor = PaintUtils.setAlpha(Color.RED, 130);
        Color endColor = PaintUtils.setAlpha(Color.RED.brighter(), 0);
        boolean isRightAligned = HorizontalAlignment.RIGHT == right;
        GradientPaint paint = new GradientPaint(new Point2D.Double(0, 0),
                isRightAligned ? endColor : startColor, 
                new Point2D.Double(100, 0), 
                isRightAligned ? startColor : endColor);
        MattePainter painter = new MattePainter(paint);
        painter.setPaintStretched(true);
        RelativePainterHighlighter p = new RelativePainterHighlighter(painter);
        p.setHorizontalAlignment(right);
        p.setRelativizer(new NumberRelativizer(column, 100, 100));
        return p;
    }
    
    /**
     * @param tableModel2
     * @param i
     * @return
     */
    private ListModel createListModel(final JXTable tableModel, final int i) {
        ListModel listModel = new AbstractListModel(){

            @Override
            public Object getElementAt(int index) {
                return tableModel.getValueAt(index, i);
            }

            @Override
            public int getSize() {
                return tableModel.getRowCount();
            }};
        return listModel ;
    }


    public static class FontHighlighter extends AbstractHighlighter {
        
        private Font font;
        public FontHighlighter(HighlightPredicate predicate, Font font) {
            super(predicate);
            this.font = font;
        }
        @Override
        protected Component doHighlight(Component component,
                ComponentAdapter adapter) {
            component.setFont(font);
            return component;
        }
        
    }
    /**
     * Example from forum requirement: highlight all rows of a given group
     * if mouse if over one of them.
     * 
     * PENDING: need to track row view-model coordinates mapping after
     * filtering/sorting.
     *
     */
    public void interactiveRolloverRowGroup() {
        TableModel model = new AncientSwingTeam();
       final List<Integer> rowSet = new ArrayList<Integer>();
       for (int i = 0; i < model.getRowCount(); i++) {
         if ((Integer)model.getValueAt(i, 3) > 10) {
             rowSet.add(i);
         }
       }
       final HighlightPredicate predicate = new HighlightPredicate() {

        @Override
        public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
            return rowSet.contains(adapter.row);
        }
           
       };
       final ColorHighlighter highlighter = new ColorHighlighter(HighlightPredicate.NEVER, Color.YELLOW, 
               null);
       JXTable table = new JXTable(model);
       table.addHighlighter(highlighter);
       PropertyChangeListener l = new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            Point location = (Point) evt.getNewValue();
            int row = -1;
            if (location != null) {
                row = location.y;
            }
            if (rowSet.contains(row)) {
                highlighter.setHighlightPredicate(predicate);
            } else {
                highlighter.setHighlightPredicate(HighlightPredicate.NEVER);
            }
        }
           
       };
       table.addPropertyChangeListener(RolloverProducer.ROLLOVER_KEY, l);
       showWithScrollingInFrame(table, "rollover highlight of row groups");
    }
    
    /**
     * Example to highlight against a value/color map.
     * Here the control is in predicate. <p>
     * 
     */
    public void interactiveColorValueMappedHighlighterPredicate() {
        JXTable table = new JXTable(new AncientSwingTeam());
        // build a quick color lookup to simulate multi-value value-based
        // coloring
        final int numberColumn = 3;
        table.toggleSortOrder(numberColumn);
        Color[] colors = new Color[] { Color.YELLOW, Color.CYAN, Color.MAGENTA,
                Color.GREEN };
        int rowsPerColor = (table.getRowCount() - 5) / colors.length;
        Map<Color, HighlightPredicate> map = new HashMap<Color, HighlightPredicate>();
        for (int i = 0; i < colors.length; i++) {
            List<Integer> values = new ArrayList<Integer>();
            for (int j = 0; j < rowsPerColor; j++) {
                values.add((Integer) table.getValueAt(i * rowsPerColor + j, numberColumn));
            }
            map.put(colors[i], new ValueMappedHighlightPredicate(values, numberColumn));
        }
        // create one ColorHighlighter for each color/predicate pair and 
        // add to a compoundHighlighter
        CompoundHighlighter chl = new CompoundHighlighter();
        for (Color color : colors) {
            chl.addHighlighter(new ColorHighlighter(map.get(color), color, null));
        }
        table.resetSortOrder();
        table.addHighlighter(chl);
        showWithScrollingInFrame(table,
                "compound highlighter with value-based color mapping predicate");
    }
    
    /**
     * Custom predicate which returns true if the filtered cell value
     * of a given testColumn is contained in a list of values.
     * PENDING: logic similar to search/pattern, enough to abstract?
     */
    public static class ValueMappedHighlightPredicate implements HighlightPredicate {

        private List<?> values;
        private int testColumn;
        public ValueMappedHighlightPredicate(List<?> values, int testColumn) {
            this.values = values;
            this.testColumn = testColumn;
        }
        @Override
        public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
            return values.contains(adapter.getValue(testColumn));
        }
        
    }
    /**
     * Example to highlight against a value/color map. <p>
     * Here the Highlighter takes full control. Which is a bit 
     * on the border line of 
     * the intended distribution of responsibility between
     * Highlighter and HighlighterPredicate.
     */
    public void interactiveColorValueMappedHighlighter() {
        JXTable table = new JXTable(new AncientSwingTeam());
        // build a quick color lookup to simulate multi-value value-based
        // coloring
        final int numberColumn = 3;
        table.toggleSortOrder(numberColumn);
        final Map<Integer, Color> lookup = new HashMap<Integer, Color>();
        Color[] colors = new Color[] { Color.YELLOW, Color.CYAN, Color.MAGENTA,
                Color.GREEN };
        int rowsPerColor = (table.getRowCount() - 5) / colors.length;
        for (int i = 0; i < colors.length; i++) {
            for (int j = 0; j < rowsPerColor; j++) {
                lookup.put((Integer) table.getValueAt(i * rowsPerColor + j,
                        numberColumn), colors[i]);
            }
        }
        table.resetSortOrder();
        // PENDING JW: add and use HighlightPredicate.SELECTED/UN_SELECTED 
        Highlighter hl = new ColorHighlighter() {

            @Override
            protected void applyBackground(Component renderer, ComponentAdapter adapter) {
                if (adapter.isSelected()) return;
                Color background = lookup.get(adapter.getValue(numberColumn));
                if (background != null) {
                    renderer.setBackground(background);
                }
            }
        };
        table.addHighlighter(hl);
        showWithScrollingInFrame(table,
                "conditional highlighter with value-based color mapping");
    }

    /**
     * test to see searchPanel functionality in new Highlighter api
     * 
     */
    public void interactiveSearchPanel() {
        JXTable table = new JXTable(tableModel);
        final ColorHighlighter cl = new ColorHighlighter(new PatternPredicate((Pattern) null, 0), null,
                Color.RED);
        table.addHighlighter(cl);
        JXSearchPanel searchPanel = new JXSearchPanel();
        PatternMatcher patternMatcher = new PatternMatcher() {
            @Override
            public Pattern getPattern() {
                return getPatternPredicate().getPattern();
            }

            @Override
            public void setPattern(Pattern pattern) {
                PatternPredicate old = getPatternPredicate();
                cl.setHighlightPredicate(new PatternPredicate(pattern, old
                        .getTestColumn(), old.getHighlightColumn()));
            }
            
            private PatternPredicate getPatternPredicate() {
                return (PatternPredicate) cl.getHighlightPredicate();
            }

        };
        searchPanel.addPatternMatcher(patternMatcher);
        JXFrame frame = wrapWithScrollingInFrame(table,
                "Pattern highlighting col 0");
        addStatusComponent(frame, searchPanel);
        show(frame);
    }
    
//----------------- custom PatternMatcher
    
    /**
     * columm shading (was: hierarchicalColumnHighlighter)
     *
     */
    public void interactiveColumnShading() {
        JXTreeTable treeTable = new JXTreeTable(new FileSystemModel());
        // simulate hierarchicalColumnHighlighter
        int hierarchicalColumn = 0;
        for (int i = 0; i < treeTable.getColumnCount(); i++) {
            if (treeTable.isHierarchical(i)) {
                hierarchicalColumn = i;
                break;
            }
        }
        treeTable.addHighlighter(new ShadingColorHighlighter(new ColumnHighlightPredicate(hierarchicalColumn)));
        showWithScrollingInFrame(treeTable, "hierarchical column");
        
    }
    
    /**
     * Classic lineprinter striping and hyperlink (LF only, no action).
     * 
     */
    public void interactiveTableAlternateAndHyperlink() {
        JXTable table = new JXTable(tableModel);
        table.setRowHeight(22);
        table.getColumn(1).setCellRenderer(
                new DefaultTableRenderer(new HyperlinkProvider()));
        table.addHighlighter(HighlighterFactory
                .createSimpleStriping(HighlighterFactory.CLASSIC_LINE_PRINTER));
        JFrame frame = wrapWithScrollingInFrame(table,
                "classic lineprinter and hyperlink on column 1");
        frame.setVisible(true);
    }


    /**
     * LinePrinter striping and rollover.
     *
     */
    public void interactiveTableAlternateAndRollover() {
        JXTable table = new JXTable(tableModel);
        table.setRowHeight(22);
        table.setHighlighters(
            HighlighterFactory.createSimpleStriping(HighlighterFactory.LINE_PRINTER),
            new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, Color.YELLOW, null));
        showWithScrollingInFrame(table, "LinePrinter plus yellow rollover");
    }

    /**
     * Foreground highlight on column 1 and 3.
     *
     */
    public void interactiveColumnForeground() {
        JXTable table = new JXTable(tableModel);
        HighlightPredicate predicate = new ColumnHighlightPredicate(1, 3);
        table.addHighlighter(new ColorHighlighter(predicate, null, Color.BLUE));
        showWithScrollingInFrame(table, "Foreground highlight col 1 and 3");
    }


    /**
     * ColorHighlighter with pattern predicate
     *
     */
    public void interactiveTablePatternHighlighter() {
        JXTable table = new JXTable(tableModel);
        table.setColumnControlVisible(true);
        table.addHighlighter(new ColorHighlighter(new PatternPredicate("^M", 1), null, 
                Color.red));
        showWithScrollingInFrame(table, "Pattern: highlight row if ^M col 1");
    }

    /**
     * Issue #258-swingx: Background LegacyHighlighter must not change custom
     * foreground.
     * <p>
     * 
     * Use SwingX extended default renderer.
     */
    public void interactiveTableCustomRendererColor() {
        TableModel model = new AncientSwingTeam();
        JXTable table = new JXTable(model);
        DefaultTableRenderer renderer = new DefaultTableRenderer();
        renderer.setForeground(foreground);
        renderer.setBackground(background);
        table.addHighlighter(HighlighterFactory.createAlternateStriping(Color.WHITE, HighlighterFactory.GENERIC_GRAY));
        table.setDefaultRenderer(Object.class, renderer);
        JXTable nohighlight = new JXTable(model);
        nohighlight.setDefaultRenderer(Object.class, renderer);
        showWithScrollingInFrame(table, nohighlight,
                "ext: custom colored renderer with bg highlighter <--> shared without highl");
    }
    
    /**
     * Requirement from forum: value based color and tooltip.
     * 
     * Here the tooltip is regarded as visual decoration and 
     * set in a specialized Highlighter. 
     *
     */
    public void interactiveValueBasedToolTipAndColorOnHighlighter() {
        ToolTipManager t = ToolTipManager.sharedInstance();
        LOG.info("initial/reshow/dismiss" + t.getInitialDelay() + " / " + t.getReshowDelay()
                + " / " + t.getDismissDelay()
                + " / " + UIManager.getString("ToolTipManager.enableToolTipMode"));
        final JXTable table = new JXTable(new AncientSwingTeam()) {

//            @Override
//            public Point getToolTipLocation(MouseEvent event) {
//                int column = columnAtPoint(event.getPoint());
//                int row = rowAtPoint(event.getPoint());
//                Rectangle cellRect = getCellRect(row, column, false);
//                if (!getComponentOrientation().isLeftToRight()) {
//                    cellRect.translate(cellRect.width, 0);
//                }
//                // PENDING JW: otherwise we get a small (borders only) tooltip for null
//                // core issue? Yeh, the logic in tooltipManager is crooked.
//                // but this here is ehem ... rather arbitrary. 
////                return getValueAt(row, column) == null ? null : cellRect.getLocation();
//                return getToolTipText(event) == null ? null : cellRect.getLocation();
////                return null;
//            }
//            

        };
        t.unregisterComponent(table);
        table.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke('q'), "dummy");
        t.registerComponent(table);
        table.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("F5"), "hideTip");
        LOG.info("hideTip? " + table.getActionMap().get("hideTip"));
        
        PropertyChangeListener l = new PropertyChangeListener() {
            
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                LOG.info("evt.getP" + evt.getPropertyName());
                ToolTipManager.sharedInstance().mousePressed(null);
            }
        };
        table.addPropertyChangeListener(RolloverProducer.ROLLOVER_KEY, l);
//        table.addMouseMotionListener(new MouseMotionAdapter() {
//            int prevRow = -1;
//            int prevCol = -1;
//          
//            public void mouseMoved(MouseEvent e) {
//               int row = table.rowAtPoint(e.getPoint());
//               int col = table.columnAtPoint(e.getPoint());
//               if (row == prevRow && col == prevCol)
//                  return;
//               ToolTipManager.sharedInstance().mousePressed(e);
////               ToolTipManager.sharedInstance().mouseEntered(e);
//               prevRow = row;
//               prevCol = col;
//            }
//         });
        HighlightPredicate predicate = new HighlightPredicate() {

            @Override
            public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
                if (!(adapter.getValue() instanceof Number)) return false;
                return ((Number) adapter.getValue()).intValue() < 10;
            }
            
        };
        ColorHighlighter hl = new ColorHighlighter(
                predicate, null, Color.RED, null, Color.RED);
        // THINK this is possible, but maybe not the correct place 
        // ... more on the what-side of "what vs. how" ?
        Highlighter tl = new AbstractHighlighter(predicate) {

            @Override
            protected Component doHighlight(Component component, ComponentAdapter adapter) {
                String text = "low on luck: " + ((JLabel) component).getText();
                ((JComponent) component).setToolTipText(text);
                return component;
            }
            @Override
            protected boolean canHighlight(Component component,
                    ComponentAdapter adapter) {
                return component instanceof JLabel;
            }
            
        };
        table.setHighlighters(HighlighterFactory.createSimpleStriping(HighlighterFactory.GENERIC_GRAY),
                hl, tl);
        showWithScrollingInFrame(table, "Value-based Tooltip ... on Highlighter");
    }

    /**
     * Requirement from forum: value based color and tooltip.<p>
     * 
     * Here the tooltip is regarded as belonging more to the "what"
     * of rendering and set in a custom provider. The implication
     * is that the logic (whether to show the tooltip or not) is
     * duplicated (in the predicate and the provider.
     * 
     *
     */
    public void interactiveValueBasedToolTipAndColorOnProvider() {
        JXTable table = new JXTable(new AncientSwingTeam());
        HighlightPredicate predicate = new HighlightPredicate() {

            @Override
            public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
                if (!(adapter.getValue() instanceof Number)) return false;
                return ((Number) adapter.getValue()).intValue() < 10;
            }
            
        };
        ColorHighlighter hl = new ColorHighlighter(
                predicate, null, Color.RED, null, Color.RED);
        table.setHighlighters(HighlighterFactory.createSimpleStriping(HighlighterFactory.GENERIC_GRAY),
                hl); //, tl);
        // here: set's value based .. this duplicates logic of 
        // predicate
        LabelProvider provider = new LabelProvider() {

            
            @Override
            protected void configureState(CellContext context) {
                super.configureState(context);
                rendererComponent.setToolTipText(getToolTipText(context));
            }


            private String getToolTipText(CellContext context) {
                if ((context.getValue() instanceof Number))  {
                    int luck = ((Number) context.getValue()).intValue();
                    if (luck < 10) {
                        return "low on luck: " + luck;
                    }
                }
                return null;
            }
            
        };
        provider.setHorizontalAlignment(JLabel.RIGHT);
        table.setDefaultRenderer(Number.class, new DefaultTableRenderer(provider));
        showWithScrollingInFrame(table, "Value-based Tooltip ... on provider");
    }

    /**
     * Example of custom predicate based on the component's value, 
     * (as opposed to on the value of the adapter). 
     * 
     * 
     */
    public void interactiveTableColorBasedOnComponentValue() {
        TableModel model = new AncientSwingTeam();
        JXTable table = new JXTable(model);
        table.setForeground(Color.GREEN);
        HighlightPredicate predicate = new HighlightPredicate() {

            @Override
            public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
                if (!(renderer instanceof JLabel)) return false;
                String text = ((JLabel) renderer).getText();
                 return text.contains("y");
            }
            
        };
        ColorHighlighter hl = new ColorHighlighter(predicate, null, Color.RED);
        table.addHighlighter(HighlighterFactory.createSimpleStriping(HighlighterFactory.GENERIC_GRAY));
        table.addHighlighter(hl);
        showWithScrollingInFrame(table, 
                "component value-based rendering (label text contains y) ");
    }


    //------------------ rollover
    
    /**
     * Issue #503-swingx: custom cursor respected when rollover?
     * Seems okay for table, 
     */
    public void interactiveRolloverHighlightCustomCursor() {
        JXTable table = new JXTable(tableModel);
        table.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        table.addHighlighter(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, Color.YELLOW, null));
        showWithScrollingInFrame(table, "rollover highlight, custom cursor");
    }

    /**
     * Plain RolloverHighlighter. 
     * Issue #513-swingx: no rollover effect for disabled table.
     *
     */
    public void interactiveRolloverHighlight() {
        final JXTable table = new JXTable(tableModel);
        ColorHighlighter colorHighlighter = new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, Color.YELLOW, null);
        table.addHighlighter(colorHighlighter);
        Action action = new AbstractAction("toggle table enabled") {

            @Override
            public void actionPerformed(ActionEvent e) {
                table.setEnabled(!table.isEnabled());
                
            }
            
        };
        JXFrame frame = showWithScrollingInFrame(table, "rollover highlight, enabled/disabled table");
        addAction(frame, action);
        frame.pack();
    }
    

//--------------------- factory    
    /**
     * Simple ui-striping.
     *
     */
    public void interactiveTreeTableSimpleStripingUI() {
        JXTreeTable table = new JXTreeTable(new FileSystemModel());
        table.setVisibleRowCount(table.getRowCount() + 3);
        table.addHighlighter(HighlighterFactory.createSimpleStriping());
        showWithScrollingInFrame(table, "Simple ui striping");
    }
    
    /**
     * shows the effect of a simple striping highlighter on a 
     * colored table.
     *
     */
    public void interactiveTreeTableSimpleStriping() {
        JXTreeTable table = new JXTreeTable(new FileSystemModel());
        table.setVisibleRowCount(table.getRowCount() + 3);
        table.setBackground(new Color(0xC0FFC0));
        table.addHighlighter(HighlighterFactory.createSimpleStriping());
        showWithScrollingInFrame(table, "Simple gray striping");
    }
    /**
     * Simple ui-striping.
     *
     */
    public void interactiveSimpleStripingUI() {
        JXTable table = new JXTable(tableModel);
        table.setVisibleRowCount(table.getRowCount() + 3);
        table.addHighlighter(HighlighterFactory.createSimpleStriping());
        showWithScrollingInFrame(table, "Simple ui striping");
    }

    /**
     * shows the effect of a simple striping highlighter on a 
     * colored table.
     *
     */
    public void interactiveSimpleStriping() {
        JXTable table = new JXTable(tableModel);
        table.setVisibleRowCount(table.getRowCount() + 3);
        table.setBackground(new Color(0xC0FFC0));
        table.addHighlighter(HighlighterFactory.createSimpleStriping());
        Highlighter disabled = new AbstractHighlighter(HighlightPredicate.READ_ONLY) {
            
            @Override
            protected Component doHighlight(Component component,
                    ComponentAdapter adapter) {
                component.setEnabled(false);
                return component;
            }
        };
        table.getColumnExt(0).setEditable(false);
        table.getColumnExt(2).setEditable(false);
        table.getColumnExt(0).setCellRenderer(new DefaultTableRenderer(new TextFieldProvider()));
        table.addHighlighter(disabled);
        showWithScrollingInFrame(table, "Simple gray striping");
    }

    public static class TextFieldProvider extends ComponentProvider<JTextField> {

        @Override
        protected void configureState(CellContext context) {
            // TODO Auto-generated method stub
            
        }

        @Override
        protected JTextField createRendererComponent() {
            JTextField field = new JTextField(20);
            return field;
        }

        @Override
        protected void format(CellContext context) {
            rendererComponent.setText(getValueAsString(context));
        }
        
    }
    /**
     * shows the effect of a simple striping highlighter on a 
     * colored table.
     *
     */
    public void interactiveSimpleStripingGroup() {
        JXTable table = new JXTable(tableModel);
        table.setVisibleRowCount(table.getRowCount() + 3);
        table.setBackground(Color.YELLOW);
        table.addHighlighter(HighlighterFactory.createSimpleStriping(Color.LIGHT_GRAY, 3));
        showWithScrollingInFrame(table, "Simple gray striping, grouped by 3");
    }
    /**
     * shows the effect of a simple striping highlighter on a 
     * colored table.
     *
     */
    public void interactiveAlternateStriping() {
        JXTable table = new JXTable(tableModel);
        table.setVisibleRowCount(table.getRowCount() + 3);
        table.setBackground(Color.YELLOW);
        table.addHighlighter(HighlighterFactory.createAlternateStriping(Color.WHITE, Color.LIGHT_GRAY));
        showWithScrollingInFrame(table, "Alternate white/gray striping");
    }
    
    /**
     * shows the effect of a simple striping highlighter on a 
     * colored table.
     *
     */
    public void interactiveAlternateStripingGroup() {
        JXTable table = new JXTable(tableModel);
        table.setVisibleRowCount(table.getRowCount() + 3);
        table.setBackground(Color.YELLOW);
        table.addHighlighter(HighlighterFactory.createAlternateStriping(Color.WHITE, Color.LIGHT_GRAY, 3));
        showWithScrollingInFrame(table, "Alternate white/gray striping");
    }

    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
         tableModel = new AncientSwingTeam();
     }

    /**
     * Do nothing, make the test runner happy
     * (would output a warning without a test fixture).
     *
     */
    public void testDummy() {
        
    }
}
