/*
 * Created on 10.06.2006
 *
 */
package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.DefaultRowSorter;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.RowFilter.Entry;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.UIResource;

import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.decorator.PatternPredicate;
import org.jdesktop.swingx.hyperlink.EditorPaneLinkVisitor;
import org.jdesktop.swingx.hyperlink.LinkModel;
import org.jdesktop.swingx.hyperlink.LinkModelAction;
import org.jdesktop.swingx.renderer.DefaultListRenderer;
import org.jdesktop.swingx.renderer.HyperlinkProvider;
import org.jdesktop.swingx.renderer.HighlighterClientVisualCheck.FontHighlighter;
import org.jdesktop.swingx.sort.RowFilters;
import org.jdesktop.test.AncientSwingTeam;
import org.junit.After;
import org.junit.Before;

import sun.font.CreatedFontTracker;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class JXListVisualCheck extends InteractiveTestCase { //JXListTest {
    @SuppressWarnings("all")
    private static final Logger LOG = Logger.getLogger(JXListVisualCheck.class
            .getName());
    public static void main(String[] args) {
        setSystemLF(true);
//        LookAndFeel l;
//        SynthConstants s;
        JXListVisualCheck test = new JXListVisualCheck();
        try {
//            NimbusLookAndFeel n;
//            Region my = XRegion.XLIST;
//            setLookAndFeel("Nimbus");
//            new XRegion("XList", "XListUI", false);
//          test.runInteractiveTests();
//            test.runInteractive("RowFilter");
//            test.runInteractive("Remove");
            test.runInteractive("PopupTrigger");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }

    /**
     * Issue #1563-swingx: find cell that was clicked for componentPopup
     * 
     * Example of how to use:
     * - in actionPerformed
     * - in popupMenuWillBecomeVisible
     */
    public void interactivePopupTriggerLocation() {
        JXList table = new JXList(createListModel());
        JPopupMenu popup = new JPopupMenu();
        Action action = new AbstractAction("cell found in actionPerformed") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                JXList table = SwingXUtilities.getAncestor(JXList.class, (Component) e.getSource());
                Point trigger = table.getPopupTriggerLocation();
                Point cell = null;
                if (trigger != null) {
                    int row = table.locationToIndex(trigger);
                    table.setSelectedIndex(row);
                    cell = new Point(0, row);
                } else {
                    table.clearSelection();
                }
                LOG.info("popupTrigger/cell " + trigger + "/" + cell);
            }
        };
        popup.add(action);
        
        final Action onShowing = new AbstractAction("dynamic: ") {

            @Override
            public void actionPerformed(ActionEvent e) {
                LOG.info("" + getValue(NAME));
            }
            
        };
        popup.add(onShowing);
        
        PopupMenuListener l = new PopupMenuListener() {
            
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                // doesn't work: popup itself cannot be used as
                // starting component, bug?
//                JXList list = SwingXUtilities.getAncestor(JXList.class, 
//                        (Component) e.getSource());
                JXList list = (JXList) ((JPopupMenu) e.getSource()).getInvoker();
                Point trigger = list.getPopupTriggerLocation();
                Point cell = null;
                if (trigger != null) {
                    int row = list.locationToIndex(trigger);
                    list.setSelectedIndex(row);
                    // here we set the cell focus, just to do a bit differently
                    // from the other action
                    list.clearSelection();
                    cell = new Point(0, row);
                }
                onShowing.putValue(Action.NAME, "popupTrigger/cell " + trigger + "/" + cell);
            }
            
            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }
            
            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        };
        popup.addPopupMenuListener(l);
        table.setComponentPopupMenu(popup);
        showWithScrollingInFrame(table, "PopupTriggerLocation");
    }
    

    
    /**
     * Issue #1536-swingx: AIOOB on restoring selection with filter
     * Reopened: overfixed - the removeIndexInterval _does_ take 
     * the endIndex instead of length.
     */
    public void interactiveSortUIRemove() {
        final DefaultListModel model = createAscendingListModel(0, 10);
        final JXList list = new JXList(model, true);
        final RowFilter filter = new RowFilters.GeneralFilter() {

            List excludes = Arrays.asList(0);
            @Override
            protected boolean include(
                    Entry<? extends Object, ? extends Object> entry,
                    int index) {
                return !excludes.contains(entry.getIdentifier());
            }
            
        };
        JXFrame frame = wrapWithScrollingInFrame(list, "removeIndexSelectionInterval");
        Action toggleSort = new AbstractAction("toggleSort") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                list.toggleSortOrder();
            }
        };
        addAction(frame, toggleSort);
        Action toggleFilter = new AbstractAction("filter") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                list.setRowFilter(list.getRowFilter() != null ?
                        null : filter
                        );
            }
        };
        addAction(frame, toggleFilter);
        Action remove = new AbstractAction("remove") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                model.remove(model.getSize() - 1);
            }
        };
        addAction(frame, remove);
        show(frame);
        
    }
    
    public void interactiveRowFilter() {
        final JXList list = new JXList(AncientSwingTeam.createNamedColorListModel(), true);
        JXFrame frame = wrapWithScrollingInFrame(list, "filter");
        Action toggleFilter = new AbstractAction("toggleFilter") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (list.getRowFilter() == null) {

                    list.setRowFilter(new RowFilter<ListModel, Integer>() {

                        @Override
                        public boolean include(
                                Entry<? extends ListModel, ? extends Integer> entry) {
                            boolean include = entry
                                    .getStringValue(entry.getIdentifier())
                                    .toLowerCase().contains("o");
                            return include;
                        }

                    });
                } else {
                    list.setRowFilter(null);
                }
            }
        };
        addAction(frame, toggleFilter);
        show(frame, 300, 600);
    }
    
    /**
     * Issue #1261-swingx: list goes blank after setting model and filter.
     * 
     * To reproduce:
     * - click on setModel: resets the model and turns on filter (expected)
     * - click on filterOff: clears the list (unexpected - the expected behaviour
     *   is to show all entries
     * 
     * Workaround:
     * - click on invalidate to explicitly invalidated the cell size cache 
     *  (should be done automatically) - fixed to now do in ListSortUI.sorterChanged
     * 
     * example adjusted from reporter
     */
    public void interactiveRowFilterAfterSetModel() {
        final JXList list = new JXList(true);
        JXFrame frame = wrapWithScrollingInFrame(list, "filter after model");
        
        final Action filterOnAction = new AbstractAction("filter on") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                list.setRowFilter(new RowFilter<ListModel, Integer>() {

                    @Override
                    public boolean include(
                            Entry<? extends ListModel, ? extends Integer> entry) {
                        boolean include = entry.getStringValue(entry.getIdentifier())
                                .toLowerCase().contains("o");
                        return include;
                    }

                });
            }
        };
        
//        addAction(frame, filterOnAction);
        
        Action modelAction = new AbstractAction("setModel") {
            int count;
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultListModel model = new DefaultListModel();
                model.addElement("One" + count++);
                model.addElement("Two");
                model.addElement("Three");
                model.addElement("Four");
                model.addElement("Five");
                list.setModel(model);
                filterOnAction.actionPerformed(e);
            }
        };
        addAction(frame, modelAction);
        
        Action filterOffAction = new AbstractAction("filter off") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                list.setRowFilter(new RowFilter<ListModel, Integer>() {

                    @Override
                    public boolean include(
                            Entry<? extends ListModel, ? extends Integer> entry) {
                        return true;
                    }

                });
            }
        };
        
        addAction(frame, filterOffAction);
        
        Action invalidateCacheAction = new AbstractAction("invalidateCache") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                // this shouldn't be necessary
                list.invalidateCellSizeCache();
            }
        };
        addAction(frame, invalidateCacheAction);
        addStatusMessage(frame, "setModel == new Model + rowFilter on; filterOff = rowFilter off (should show all)");
        show(frame, 500, 300);
    }
    
    /**
     * Issue #1255-swingx: enhance dynamic row sizing.
     */
    public void interactiveDynamicCellHeight() {
        final JXList list = new JXList(AncientSwingTeam.createNamedColorListModel(), true);
        ListSelectionListener l = new ListSelectionListener() {
            
            @Override
            public void valueChanged(final ListSelectionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        list.invalidateCellSizeCache();
                    }
                });
            }
            
        };
        list.addListSelectionListener(l);
        HighlightPredicate selected = new HighlightPredicate() {
            
            @Override
            public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
                return adapter.isSelected();
            }
        };
        Highlighter hl = new FontHighlighter(selected, list.getFont().deriveFont(50f));
        list.addHighlighter(hl);
        JXFrame frame = wrapWithScrollingInFrame(list, "big font on focus");
        Action toggleSort = new AbstractAction("toggle sort") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                list.toggleSortOrder();
            }
        };
        addAction(frame, toggleSort);
        show(frame);
    }

    /**
     * Issue 1161-swingx: JXList not completely updated on setRowFilter
     * Issue 1162-swingx: JXList getNextMatch access model directly
     */
    public void interactiveNextMatch() {
        JList core = new JList(AncientSwingTeam.createNamedColorListModel());
        final JXList list = new JXList(core.getModel(), true);
//        list.toggleSortOrder();
        JXFrame frame = showWithScrollingInFrame(list, core, "x <-> core: nextMatch");
        Action toggleFilter = new AbstractAction("toggleFilter") {
            @Override
            public void actionPerformed(ActionEvent e) {
                RowFilter<Object, Object> filter = RowFilters.regexFilter(Pattern.CASE_INSENSITIVE, "^b");
                list.setRowFilter(list.getRowFilter() == null ? filter : null);
            }
        };
        addAction(frame, toggleFilter);
        Action toggleSort = new AbstractAction("toggleSortOrder") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                list.toggleSortOrder();
            }
        };
        addAction(frame, toggleSort);
    }
    
    public void interactiveTestCompareFocusedCellBackground() {
        final JXList xlist = new JXList(listModel);
        LOG.info("xlist ui" + xlist.getUI());
        final Color bg = new Color(0xF5, 0xFF, 0xF5);
        final JList list = new JList(listModel);
//        xlist.setBackground(bg);
//        list.setBackground(bg);
        JXFrame frame = wrapWithScrollingInFrame(xlist, list, 
                "unselectedd focused background: JXList/JList");
        Action toggle = new AbstractAction("toggle background") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                Color old = xlist.getBackground();
                Color back = ((old == null) || (old instanceof UIResource)) ? bg : null;
                xlist.setBackground(back);
                list.setBackground(back);
                if (back == null) {
                    // force ui default background
                    list.updateUI();
                    xlist.updateUI();
                }
                
            }
        };
        addAction(frame, toggle);
        show(frame);
    }

    public void interactiveTestTablePatternFilter5() {
        JXList list = new JXList(listModel);
        String pattern = "Row";
        list.setHighlighters(new ColorHighlighter(// columns not really important, ListAdapter.getXXValue
        // uses row only
        new PatternPredicate(pattern, 0), null, 
                Color.red));
        showWithScrollingInFrame(list, "PatternHighlighter: " + pattern);
    }

    public void interactiveTestTableAlternateHighlighter1() {
        JXList list = new JXList(listModel);
        list.addHighlighter(
                HighlighterFactory.createSimpleStriping(HighlighterFactory.LINE_PRINTER));

        showWithScrollingInFrame(list, "AlternateRowHighlighter - lineprinter");
    }

    /**
     * Plain rollover highlight, had been repaint issues.
     *
     */
    public void interactiveTestRolloverHighlight() {
        JXList list = new JXList(listModel);
        list.setRolloverEnabled(true);
        ColorHighlighter rollover = new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, new Color(0xF0, 0xF0, 0xE0), 
                        null);
        list.addHighlighter(rollover);
        showWithScrollingInFrame(list, "rollover highlight");
    }

    /**
     * Plain rollover highlight in multi-column layout, had been repaint issues.
     *
     */
    public void interactiveTestRolloverHighlightMultiColumn() {
        LOG.info("rtol-map? " + UIManager.get("List.focusInputMap.RightToLeft"));
        LOG.info("ancestor maps? " + UIManager.get("Table.ancestorInputMap"));
        LOG.info("ancestor rtol maps? " + UIManager.get("Table.ancestorInputMap.RightToLeft"));
        
        JXList list = new JXList(listModel);
        list.setRolloverEnabled(true);
        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        list.addHighlighter(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, new Color(0xF0, 0xF0, 0xE0), 
                null));
        JList core = new JList(listModel);
        core.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        JComponent box = Box.createVerticalBox();
        box.add(new JScrollPane(list));
        box.add(new JScrollPane(core));
        JXFrame frame = wrapInFrame(box, "rollover highlight - horz. Wrap");
        addComponentOrientationToggle(frame);
        show(frame);
    }
    /**
     * Issue #503-swingx: rolloverEnabled disables custom cursor
     *
     */
    public void interactiveTestRolloverHighlightCustomCursor() {
        JXList list = new JXList(listModel);
        list.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        list.setRolloverEnabled(true);
        list.addHighlighter(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, new Color(0xF0, 0xF0, 0xE0), 
                null));
        showWithScrollingInFrame(list, "rollover highlight - custom cursor");
    }
    /**
     * Issue #20: Highlighters and LinkRenderers don't work together
     * fixed with overhaul of SwingX renderers?
     */
    public void interactiveTestRolloverHighlightAndLink() {
        JXList list = new JXList(createListModelWithLinks());
        EditorPaneLinkVisitor editorPaneLinkVisitor = new EditorPaneLinkVisitor();
        LinkModelAction<?> action = new LinkModelAction<LinkModel>(editorPaneLinkVisitor);
        HyperlinkProvider h = new HyperlinkProvider(action, LinkModel.class);
        list.setCellRenderer(new DefaultListRenderer(h));
        list.setRolloverEnabled(true);
        list.addHighlighter(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, new Color(0xF0, 0xF0, 0xE0), 
                null));
        showWithScrollingInFrame(list, editorPaneLinkVisitor.getOutputComponent(), "rollover highlight with links");
    }

    

    protected ListModel createListModel() {
        JList list = new JList();
        return new DefaultComboBoxModel(list.getActionMap().allKeys());
    }

    protected DefaultListModel createAscendingListModel(int startRow, int count) {
        DefaultListModel l = new DefaultListModel();
        for (int row = startRow; row < startRow  + count; row++) {
            l.addElement(new Integer(row));
        }
        return l;
    }
    protected DefaultListModel createListModelWithLinks() {
        DefaultListModel model = new DefaultListModel();
        for (int i = 0; i < 20; i++) {
            try {
                LinkModel link = new LinkModel("a link text " + i, null, new URL("http://some.dummy.url" + i));
                if (i == 1) {
                    URL url = JXEditorPaneTest.class.getResource("resources/test.html");

                    link = new LinkModel("a resource", null, url);
                }
                model.addElement(link);
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
 
        return model;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        listModel = createListModel();
        ascendingListModel = createAscendingListModel(0, 20);
    }

    protected ListModel listModel;
    protected DefaultListModel ascendingListModel;

    
    @Before
    public void setUpJ4() throws Exception {
        setUp();
    }
    
    @After
    public void tearDownJ4() throws Exception {
        tearDown();
    }

}
