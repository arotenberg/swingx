/*
 * Created on 14.10.2005
 *
 */
package org.jdesktop.swingx.decorator;

import java.awt.Color;
import java.awt.Component;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import javax.swing.DefaultListModel;
import javax.swing.JTable;
import javax.swing.ListModel;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.JXEditorPaneTest;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.decorator.HighlighterFactory.UIColorHighlighter;
import org.jdesktop.swingx.hyperlink.LinkModel;
import org.jdesktop.swingx.renderer.DefaultTreeRenderer;
import org.jdesktop.test.AncientSwingTeam;


public class HighlighterIssues extends org.jdesktop.swingx.InteractiveTestCase {
    private static final Logger LOG = Logger.getLogger(HighlighterIssues.class
            .getName());
    
    protected Color ledger = new Color(0xF5, 0xFF, 0xF5);
    protected Color background = Color.RED;
    protected Color foreground = Color.BLUE;

    protected ColorHighlighter emptyHighlighter;
    // flag used in setup to explicitly choose LF
    protected boolean defaultToSystemLF;
    
    //---------------- uidependent
    

    /**
     * test if background changes with LF.
     * 
     * PENDING: this is not entirely correct, might fail because
     *   both LFs fall back to GenericGray.
     */
    public void testLookupUIColor() {
        UIColorHighlighter hl = new UIColorHighlighter();
        Color color = hl.getBackground();
        String lf = UIManager.getLookAndFeel().getName();
        Color uiColor = UIManager.getColor("UIColorHighlighter.stripingBackground");
        assertNotNull(uiColor);
        // switch LF
        setSystemLF(!defaultToSystemLF);
        if (lf.equals(UIManager.getLookAndFeel().getName())) {
            LOG.info("cannot run lookupUIColor - same LF" + lf);
            return;
        }
        Color uiColor2 = UIManager.getColor("UIColorHighlighter.stripingBackground");
        assertNotNull(uiColor2);
        // hmm ... how to force the reloading in the addon?
        LOG.info("color must be different " + uiColor + "/" + uiColor2);
        assertFalse("color must be different " + uiColor + "/" + uiColor2 , uiColor2.equals(uiColor));
        hl.updateUI();
        assertFalse("highlighter background must be changed", 
                color.equals(hl.getBackground()));
    }
    /**
     * test if background changes with LF.
     * 
     * PENDING: this is not entirely correct, might fail because
     *   both LFs fall back to GenericGray.
     */
    public void testLookupUIColorInCompound() {
        UIColorHighlighter hl = new UIColorHighlighter();
        Color color = hl.getBackground();
        CompoundHighlighter compound = new CompoundHighlighter(hl);
        String lf = UIManager.getLookAndFeel().getName();
        // switch LF
        setSystemLF(!defaultToSystemLF);
        if (lf.equals(UIManager.getLookAndFeel().getName())) {
            LOG.info("cannot run lookupUIColor - same LF" + lf);
            return;
        }
        compound.updateUI();
        assertFalse("highlighter background must be changed", 
                color.equals(hl.getBackground()));
    }

    /**
     * Issue #258-swingx: DefaultTableCellRenderer has memory. 
     * How to formulate as test?
     * this is testing the hack (reset the memory in ResetDTCR to null), not
     * any highlighter!
     */
    public void testTableUnSelectedDoNothingHighlighter() {
        JXTable table = new JXTable(10, 2);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setForeground(foreground);
        table.setHighlighters(new ColorHighlighter());
        Component comp = table.prepareRenderer(renderer, 0, 0);
        assertEquals("do nothing highlighter must not change foreground", foreground, comp.getForeground());
        fail("testing the hack around DefaultTableCellRenderer memory - not the memory itself");
    }


    //---------------
 
    public static void main(String args[]) {
//      setSystemLF(true);
      HighlighterIssues test = new HighlighterIssues();
      try {
//         test.runInteractiveTests();
         test.runInteractiveTests("interactive.*Predicate.*");
      } catch (Exception e) {
          System.err.println("exception when executing interactive tests:");
          e.printStackTrace();
      }
  }

    /**
     * Issue #258-swingx: Background LegacyHighlighter must not change custom
     * foreground.
     * <p>
     * 
     * Visualizing effect of hack: table-internally, a ResetDTCRColorHighlighter
     * tries to neutralize DefaultTableCellRenderer's color memory.
     * 
     * <ul>
     * <li> a DTCR subclass with value-based custom foreground
     * <li> the renderer is shared between a table with background highlighter
     * (alternateRowHighlighter) and a table without highlighter
     * <li> the custom value-based foreground must show in both
     * (AlternateRowHighlighter overwrite both striped and unstriped back)
     * </ul>
     * 
     * This behaves as expected after moving the hack to _before_ calling 
     * super.prepareRenderer.
     */
    public void interactiveTableCustomCoreRendererColorBasedOnValue() {
        TableModel model = new AncientSwingTeam();
        JXTable table = new JXTable(model);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean isSelected, boolean hasFocus,
                    int row, int column) {
                // TODO Auto-generated method stub
                super.getTableCellRendererComponent(table, value, isSelected,
                        hasFocus, row, column);
                if (!isSelected) {
                    if (getText().contains("y")) {
                        setForeground(Color.RED);
                    } else {
                        setForeground(Color.GREEN);
                    }
                }
                return this;
            }

        };
        table.addHighlighter(
                HighlighterFactory.createSimpleStriping(HighlighterFactory.GENERIC_GRAY));

        table.setDefaultRenderer(Object.class, renderer);
        JXTable nohighlight = new JXTable(model);
        nohighlight.setDefaultRenderer(Object.class, renderer);
        showWithScrollingInFrame(table, nohighlight,
                "core - value-based fg renderer with bg highlighter <--> shared without highl");
    }
    

    
    /**
     * Issue #258-swingx: Background LegacyHighlighter must not change custom
     * foreground.
     * <p>
     * 
     * Visualizing effect of hack: table-internally, a ResetDTCRColorHighlighter
     * tries to neutralize DefaultTableCellRenderer's color memory.
     * 
     * <ul>
     * <li> a DTCR with custom foreground and custom background
     * <li> the renderer is shared between a table with background highlighter
     * (alternateRowHighlighter) and a table without highlighter
     * <li> the custom foreground must show in both
     * <li> the custom background must show in the table without highlighter
     * <li> the custom background must not show in the table with highlighter
     * (AlternateRowHighlighter overwrite both striped and unstriped back)
     * </ul>
     * 
     */
    public void interactiveTableCustomCoreRendererColor() {
        TableModel model = new AncientSwingTeam();
        JXTable table = new JXTable(model);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setForeground(foreground);
        renderer.setBackground(background);
        table.addHighlighter(
                HighlighterFactory.createSimpleStriping(HighlighterFactory.GENERIC_GRAY));

        table.setDefaultRenderer(Object.class, renderer);
        JXTable nohighlight = new JXTable(model);
        nohighlight.setDefaultRenderer(Object.class, renderer);
        showWithScrollingInFrame(table, nohighlight,
                "core: custom colored renderer with bg highlighter <--> shared without highl");
    }
    
    
    /**
     * Issue #258-swingx: Background LegacyHighlighter must not change custom
     * foreground.
     * <p>
     * 
     * Visualizing effect of hack: table-internally, a ResetDTCRColorHighlighter
     * tries to neutralize DefaultTableCellRenderer's color memory.
     * 
     * Problem: if pre-hack-highlighting and selected on first rendering,
     * the renderer always uses the selection color. Bug in hacking highlighter?
     * Not much it can do about it: the hacking does not store the color if
     * the adapter isSelected - so in the next round the previously configured
     * color - which was for selected state - is stored as to color to restore to.
     * <p>
     * Arrggghhh...
     * 
     */
    public void interactiveTableCoreRendererFirstSelected() {
        TableModel model = new AncientSwingTeam();
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
//        JXTable table = new JXTable(model);
//        table.addHighlighter(AlternateRowHighlighter.genericGrey);
//        table.setDefaultRenderer(Object.class, renderer);
        JXTable nohighlight = new JXTable(model);
        nohighlight.setDefaultRenderer(Object.class, renderer);
        nohighlight.setRowSelectionInterval(0, 0);
//        showWithScrollingInFrame(table, nohighlight,
        showWithScrollingInFrame(nohighlight,
                "core: very first match is against selected");
    }
    
    
    /**
     * UIHighlighter: check if highlighter is updated when toggling LF.
     */
    public void interactiveUITableWithAlternateRow() {
        JXTable table = new JXTable(10, 2);
        table.setBackground(ledger);
        table.setHighlighters(HighlighterFactory.createSimpleStriping());
        JXTable nohighlight = new JXTable(10, 2);
        nohighlight.setBackground(ledger);
       showWithScrollingInFrame(table, nohighlight, "colored table with ui highlighter <--> without highlighter");
    }

    /**
     * Effect of background highlighters on list with custom background.
     * 
     */
    public void interactiveColoredListWithAlternateRow() {
        JXList list = new JXList(createListModel());
        list.setBackground(ledger);
        list.addHighlighter(
                HighlighterFactory.createSimpleStriping(HighlighterFactory.GENERIC_GRAY));

        JXList nohighlight = new JXList(createListModel());
        nohighlight.setBackground(ledger);
        showWithScrollingInFrame(list, nohighlight, "colored list with striping <--> without ");
    }

    /**
     * 
     * Effect of background highlighters on tree with custom background. Note:
     * background highlighters don't work at all with DefaultTreeCellRenderers.
     */
    public void interactiveColoredTreeWithAlternateRow() {
        JXTree coreRendering = new JXTree();
        coreRendering.setHighlighters(HighlighterFactory.createSimpleStriping(HighlighterFactory.GENERIC_GRAY));
        coreRendering.setBackground(ledger);
        JXTree tree = new JXTree();
        tree.setCellRenderer(new DefaultTreeRenderer()); 
        tree.setHighlighters(HighlighterFactory.createSimpleStriping(HighlighterFactory.GENERIC_GRAY));
        tree.setBackground(ledger);
        showWithScrollingInFrame(tree, coreRendering, "colored tree with striping: swingx <--> core");
    }
    

    @SuppressWarnings("all")
    private TableModel createTableModelWithLinks() {
        String[] columnNames = { "text only", "Link editable", "Link not-editable", "Bool editable", "Bool not-editable" };
        
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            public Class<?> getColumnClass(int column) {
                return getValueAt(0, column).getClass();
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                    return !getColumnName(column).contains("not");
            }
            
        };
        for (int i = 0; i < 4; i++) {
            try {
                LinkModel link = new LinkModel("a link text " + i, null, new URL("http://some.dummy.url" + i));
                if (i == 1) {
                    URL url = JXEditorPaneTest.class.getResource("resources/test.html");

                    link = new LinkModel("a link text " + i, null, url);
                }
                model.addRow(new Object[] {"text only " + i, link, link, Boolean.TRUE, Boolean.TRUE });
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return model;
    }

    
    /**
     * Issue #178-swingx: Highlighters must not change the selection color if selection colors 
     * not set.
     * 
     * Compare table with empty highlighter to table without highlighter.
     */
    public void interactiveTableWithDoNothingHighlighter() {
        JXTable table = new JXTable(new AncientSwingTeam());
        table.addHighlighter(emptyHighlighter);
        showWithScrollingInFrame(table, new JXTable(table.getModel()), "selection: empty highlighter <--> no highlighter");
        
    }
    

//------------------ helpers
    
    private ListModel createListModel() {
        DefaultListModel model = new DefaultListModel();
        for (int i = 0; i < 10; i++) {
            model.add(i, i);
        }
        return model;
    }


}
