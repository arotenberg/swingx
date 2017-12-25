/*
 * $Id: FindTest.java 3119 2008-12-01 14:54:56Z kleopatra $
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
package org.jdesktop.swingx;

import java.net.URL;
import java.util.regex.Pattern;

import org.jdesktop.swingx.JXEditorPane.DocumentSearchable;
import org.jdesktop.swingx.search.SearchFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Contains unit test for search related classes which have references to 
 * swingx package internals (namely JXEditorPane.DocumentSearchable).
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class FindTest extends InteractiveTestCase {

    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // sanity: new instance for each test
        SearchFactory.setInstance(new SearchFactory());
    }

    @Test
    public void testWrapFindBar() {
        JXFindBar findBar = new JXFindBar();
        assertTrue("findbar must auto-wrap", findBar.getPatternModel().isWrapping());
    }
    
    /**
     * test if internal state is reset when search triggered through 
     * methods taking a string. 
     * 
     *
     */
    @Test
    public void testEditorResetStateOnEmptySearchString() {
        JXEditorPane editor = new JXEditorPane();
        String text = "fou four";
        editor.setText(text);
        // initialize to found state
        int foundIndex = editor.getSearchable().search("ou");
        // sanity
        assertEquals(1, foundIndex);
        // search with null searchString
        int notFoundIndex = editor.getSearchable().search("", foundIndex);
        // sanity: nothing found
        assertEquals(-1, notFoundIndex);
        assertEquals(notFoundIndex, ((DocumentSearchable) editor.getSearchable()).lastFoundIndex);
    }

    /**
     * test if internal state is reset when search triggered through 
     * methods taking a string. 
     * 
     *
     */
    @Test
    public void testEditorResetStateOnNullSearchString() {
        JXEditorPane editor = new JXEditorPane();
        String text = "fou four";
        editor.setText(text);
        // initialize to found state
        int foundIndex = editor.getSearchable().search("ou");
        // sanity
        assertEquals(1, foundIndex);
        // search with null searchString
        int notFoundIndex = editor.getSearchable().search((String) null, foundIndex);
        // sanity: nothing found
        assertEquals(-1, notFoundIndex);
        assertEquals(notFoundIndex, ((DocumentSearchable) editor.getSearchable()).lastFoundIndex);
    }
    /**
     * test if internal state is reset when search triggered through 
     * methods taking a string. 
     * 
     *
     */
    @Test
    public void testEditorResetStateOnNullPattern() {
        JXEditorPane editor = new JXEditorPane();
        String text = "fou four";
        editor.setText(text);
        // initialize to found state
        int foundIndex = editor.getSearchable().search("ou");
        // sanity
        assertEquals(1, foundIndex);
        // search with null searchString
        int notFoundIndex = editor.getSearchable().search((Pattern) null, foundIndex);
        // sanity: nothing found
        assertEquals(-1, notFoundIndex);
        assertEquals(notFoundIndex, ((DocumentSearchable) editor.getSearchable()).lastFoundIndex);
    }


    @Test
    public void testEditor() {
        URL url = FindTest.class.getResource("resources/test.txt");
        try {
            JXEditorPane editor = new JXEditorPane(url);

            // There are 9 instances of "four" in the test document
            int useIndex = -1;
            int lastIndex = -1;
            for (int i = 0; i < 9; i++) {
                lastIndex = editor.getSearchable().search("four", useIndex);
                assertTrue(lastIndex != -1);
                assertTrue(lastIndex != useIndex);

                assertEquals("Error text selection is incorrect", "four", editor.getSelectedText());

                useIndex = lastIndex;
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error finding resource for JXEditorPane", ex);
        }
    }


}
