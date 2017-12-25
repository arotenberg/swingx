/*
 * Created on 06.10.2005
 *
 */
package org.jdesktop.swingx.rollover;

import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.test.AncientSwingTeam;
import org.jdesktop.test.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class RolloverTest extends InteractiveTestCase {

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(RolloverTest.class
            .getName());
    
    private TableModel sortableTableModel;
//    private Highlighter backgroundHighlighter;
//    private Highlighter foregroundHighlighter;
//    private ListModel listModel;
//    private FileSystemModel treeTableModel;

    
    /**
     * Issue #1193-swingx: fix rollover mouse to cell mapping on scrolling/resizing.
     *  
     */
    @Test
    public void testTableRolloverProducerComponentListener() {
        JXTable table = new JXTable();
        assertComponentListener(table, true);
        table.setRolloverEnabled(false);
        assertComponentListener(table, false);
    }
    /**
     * Issue #1193-swingx: fix rollover mouse to cell mapping on scrolling/resizing.
     *  
     */
    @Test
    public void testTreeRolloverProducerComponentListener() {
        JXTree table = new JXTree();
        assertComponentListener(table, false);
        table.setRolloverEnabled(true);
        assertComponentListener(table, true);
    }
    
    /**
     * Issue #1193-swingx: fix rollover mouse to cell mapping on scrolling/resizing.
     *  
     */
    @Test
    public void testListRolloverProducerComponentListener() {
        JXList table = new JXList();
        assertComponentListener(table, false);
        table.setRolloverEnabled(true);
        assertComponentListener(table, true);
    }
    

    /**
     * @param table
     */
    private void assertComponentListener(JComponent table, boolean expected) {
        TestUtils.assertContainsType(table.getComponentListeners(), 
                RolloverProducer.class, expected ? 1 : 0);
    }
    

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        sortableTableModel = new AncientSwingTeam();
//        listModel = new AbstractListModel() {
//
//            public int getSize() {
//                return sortableTableModel.getRowCount();
//            }
//
//            public Object getElementAt(int index) {
//                return sortableTableModel.getValueAt(index, 0);
//            }
//            
//        };
//        treeTableModel = new FileSystemModel();
//        foregroundHighlighter = new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, null,
//                Color.MAGENTA);
//        backgroundHighlighter = new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, Color.YELLOW,
//                null);
     }
    

    @Test
    public void testXDummy() {
        
    }

}
