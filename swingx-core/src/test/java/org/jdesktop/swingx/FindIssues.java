/*
 * $Id: FindIssues.java 3473 2009-08-27 13:17:10Z kleopatra $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import javax.swing.AbstractListModel;

import org.jdesktop.swingx.search.FindTest;
import org.jdesktop.swingx.search.PatternModel;
import org.jdesktop.swingx.search.Searchable;


/**
 * Exposing open issues in Searchable implementations.
 * PENDING JW: should be in package search - but references swingx package internals. 
 * 
 * @author Jeanette Winzenburg
 */
public class FindIssues extends FindTest {

    public static void main(String args[]) {
        setSystemLF(true);
//        Locale.setDefault(new Locale("es"));
        FindIssues test = new FindIssues();
        try {
//          test.runInteractiveTests();
            test.runInteractiveTests("interactive.*Mark.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }
    

    /**
     * Issue #487-swingx: NPE if setting a not-null Searchable before 
     * showing. Hack around ...
     */
    public void testFindBarNPEComponents() {
        Searchable searchable = new JXTable().getSearchable();
        JXFindBar findBar = new JXFindBar();
        // old hack
//        findBar.addNotify();
        findBar.setSearchable(searchable);
        fail("quick hack ... remove me if really fixed");
    }


    /**
     * Issue #236-swingx: backwards match in first row shows not-found-message.
     * Trackdown from Nicfagn - findPanel.doSearch always returns the next startIndex
     * in backwards search that's -1 which is interpreted as "not-found"
     * 
     */
    public void testFindPanelFirstRowBackwards() {
        JXList list = new JXList( new AbstractListModel() {
            private String[] data = { "a", "b", "c" };
            public Object getElementAt(int index) {
                return data[ index ];
            }
            public int getSize() {
                return data.length;
            }
        });
        JXFindPanel findPanel = new JXFindPanel(list.getSearchable());
        findPanel.init();
        PatternModel patternModel = findPanel.getPatternModel();
        patternModel.setBackwards(true);
        patternModel.setRawText("a");
        int matchIndex = list.getSearchable().search(patternModel.getPattern(),
                patternModel.getFoundIndex(), patternModel.isBackwards());
        assertEquals("found match", matchIndex, findPanel.doSearch());
    }

    // -------------------- interactive tests


    /**
     * #463-swingx: batch find and cellSelection don't play nicely.
     *
     */
    public void interactiveTableBatchWithCellSelectionIssue() {
        JXTable table = new JXTable(new TestTableModel());
        table.setCellSelectionEnabled(true);
        showWithScrollingInFrame(table, "batch find with cell selection");
    }

}
