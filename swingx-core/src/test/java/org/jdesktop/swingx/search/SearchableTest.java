/*
 * $Id: SearchableTest.java 4181 2012-06-20 09:03:19Z kleopatra $
 *
 * Copyright 2007 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx.search;

import static org.junit.Assert.*;

import java.util.Arrays;

import junit.framework.TestCase;

import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.BorderHighlighter;
import org.jdesktop.swingx.decorator.SearchPredicate;
import org.jdesktop.swingx.search.FindTest.TestListModel;
import org.jdesktop.swingx.search.FindTest.TestTableModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Contains unit tests for Searchable implementations.
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class SearchableTest extends TestCase {
    
    @Test (expected = NullPointerException.class)
    public void testTreeSearchableFailsOnNullTree() {
        new TreeSearchable(null);
    }

    /** 
     * Issue #1209-swingx: SearchPredicate must be updated to last found 
     *   after dynamic setting of a new highlighter.
     *   
     *   
     */
    @Test
    public void testSetMatchHighlighterCleanup() {
        JXTable table = new JXTable(new TestTableModel());
        table.putClientProperty(AbstractSearchable.MATCH_HIGHLIGHTER, Boolean.TRUE);
        // move first column to end
        int firstColumn = 0;
        int row = 39;
        String firstSearchText = table.getStringAt(row, firstColumn);
        PatternModel model = new PatternModel();
        model.setRawText(firstSearchText);
        // initialize searchable to "found state"
        table.getSearchable().search(model.getPattern(), -1);
        AbstractHighlighter hl = ((AbstractSearchable) table.getSearchable()).getMatchHighlighter();
        SearchPredicate predicate = (SearchPredicate) hl.getHighlightPredicate();
        AbstractHighlighter replaceHL = new BorderHighlighter();
        ((AbstractSearchable) table.getSearchable()).setMatchHighlighter(replaceHL);
        assertTrue("replaced highlighter must have searchPredicate ",
                replaceHL.getHighlightPredicate() instanceof SearchPredicate);
        assertEquals("replaced search predicate must be installed with old matching row", 
                predicate.getHighlightRow(), 
                ((SearchPredicate) replaceHL.getHighlightPredicate()).getHighlightRow());
        assertSame("replaced renderer must be added", 
                replaceHL, table.getHighlighters()[table.getHighlighters().length - 1]);
        assertFalse("previous matchhighlighter must be removed", Arrays.asList(table.getHighlighters()).contains(hl));
        
    }

    
    /** 
     * test that the search predicate's highlight column index is in 
     * model coordinates
     *
     */
    @Test
    public void testTableMovedColumn() {
        JXTable table = new JXTable(new TestTableModel());
        table.putClientProperty(AbstractSearchable.MATCH_HIGHLIGHTER, Boolean.TRUE);
        
        // move first column to end
        int firstColumn = table.getColumnCount() - 1;
        table.getColumnModel().moveColumn(0, firstColumn);
        int row = 39;
        String firstSearchText = table.getValueAt(row, firstColumn).toString();
        PatternModel model = new PatternModel();
        model.setRawText(firstSearchText);
        // initialize searchable to "found state"
        table.getSearchable().search(model.getPattern(), -1);
        // column index in view coordinates
        int foundColumn = ((AbstractSearchable) table.getSearchable()).lastSearchResult.foundColumn;
        assertEquals("column must be updated", firstColumn, foundColumn);
        AbstractHighlighter hl = ((AbstractSearchable) table.getSearchable()).getMatchHighlighter();
        assertTrue("searchPredicate", hl.getHighlightPredicate() instanceof SearchPredicate);
        SearchPredicate predicate = (SearchPredicate) hl.getHighlightPredicate();
        assertEquals(table.convertColumnIndexToModel(firstColumn), predicate.getHighlightColumn());
    }

    /** 
     * test that the search predicate's highlight column index is in 
     * model coordinates
     *
     */
    @Test
    public void testTableUseMatchHighlighter() {
        JXTable table = new JXTable(new TestTableModel());
        table.putClientProperty(AbstractSearchable.MATCH_HIGHLIGHTER, Boolean.TRUE);
        assertTrue("use match highlighter", ((AbstractSearchable) table.getSearchable()).markByHighlighter());
        AbstractHighlighter hl = ((AbstractSearchable) table.getSearchable()).getMatchHighlighter();
        assertNotNull(hl);
    }
    
    /** 
     * test that the search predicate's highlight column index is in 
     * model coordinates
     *
     */
    @Test
    public void testListUseMatchHighlighter() {
        JXList table = new JXList(new TestListModel());
        table.putClientProperty(AbstractSearchable.MATCH_HIGHLIGHTER, Boolean.TRUE);
        assertTrue("use match highlighter", ((AbstractSearchable) table.getSearchable()).markByHighlighter());
        AbstractHighlighter hl = ((AbstractSearchable) table.getSearchable()).getMatchHighlighter();
        assertNotNull(hl);
    }
    
    /** 
     * test that the search predicate's highlight column index is in 
     * model coordinates
     *
     */
    @Test
    public void testTreeUseMatchHighlighter() {
        JXTree table = new JXTree();
        table.putClientProperty(AbstractSearchable.MATCH_HIGHLIGHTER, Boolean.TRUE);
        assertTrue("use match highlighter", ((AbstractSearchable) table.getSearchable()).markByHighlighter());
        AbstractHighlighter hl = ((AbstractSearchable) table.getSearchable()).getMatchHighlighter();
        assertNotNull(hl);
    }

    /**
     * Task: new api in AbstractSearchable to support match highlighter.
     */
    @Test
    public void testTargetTable() {
        JXTable table = new JXTable();
        AbstractSearchable searchable = (AbstractSearchable) table.getSearchable();
        assertSame("get target same as table", table, searchable.getTarget());
    }
    
    /**
     * Task: new api in AbstractSearchable to support match highlighter.
     */
    @Test
    public void testTargetList() {
        JXList table = new JXList();
        AbstractSearchable searchable = (AbstractSearchable) table.getSearchable();
        assertSame("get target same as table", table, searchable.getTarget());
    }

    /**
     * Task: new api in AbstractSearchable to support match highlighter.
     */
    @Test
    public void testTargetTree() {
        JXTree table = new JXTree();
        AbstractSearchable searchable = (AbstractSearchable) table.getSearchable();
        assertSame("get target same as table", table, searchable.getTarget());
    }

    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // sanity: new instance for each test
        SearchFactory.setInstance(new SearchFactory());
    }
    

}
