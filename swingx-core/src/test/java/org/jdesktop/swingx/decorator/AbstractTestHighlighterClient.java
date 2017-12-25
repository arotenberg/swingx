/*
 * $Id: AbstractTestHighlighterClient.java 4086 2011-11-15 21:16:47Z kschaefe $
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
package org.jdesktop.swingx.decorator;

import static org.hamcrest.CoreMatchers.is;
import static org.jdesktop.test.matchers.Matchers.property;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;

import javax.swing.UIManager;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Contains tests around Highlighter client api for collection components.
 * <p>
 * 
 * The common public api:
 * 
 * <pre><code>
 *  void setHighlighters(Highlighter...)
 *  HighLighter[] getHighlighters()
 *  void addHighlighter(Highlighter)
 *  void removeHighlighter(Highlighter)
 *  updateUI()
 * </code></pre>
 * 
 * Subclasses testing concrete implementations must override the createHighlighterClient.
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public abstract class AbstractTestHighlighterClient extends TestCase {

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(AbstractTestHighlighterClient.class.getName());
    
    // ---- HighlighterClient
    

    /**
     * 
     * @return the concrete HighlighterClient to test
     */
    protected abstract HighlighterClient createHighlighterClient();
    
    
    /**
     * Test that the client is messaged on change to a managed Highlighter.
     */
    @Test
    public void testUpdateUI() {
        HighlighterClient client = createHighlighterClient();
        // force loading of striping colors
        ColorHighlighter colorHighlighter = (ColorHighlighter) HighlighterFactory.createSimpleStriping();
        Color uiColor = UIManager.getColor("UIColorHighlighter.stripingBackground");
        if (uiColor == null) {
            LOG.info("cannot run test - no ui striping color");
            return;
        }
        assertSame("sanity", uiColor, colorHighlighter.getBackground());
        client.addHighlighter(colorHighlighter);
        Color changedUIColor = Color.RED;
        UIManager.put("UIColorHighlighter.stripingBackground", changedUIColor);
        client.updateUI();
        try {
            assertSame("support must update ui color", changedUIColor, colorHighlighter.getBackground());
        } finally {
            UIManager.put("UIColorHighlighter.stripingBackground", uiColor);
        }
    }

    /**
     * Test assumption is incorrect if the client has internal Highlighters which
     * are always included!
     */
    @Test
    public void testSetHighlighters() {
        HighlighterClient client = createHighlighterClient();
        Highlighter[] highlighters = new Highlighter[] {new ColorHighlighter(), new ColorHighlighter()};
        client.setHighlighters(highlighters);
        assertSameContent(highlighters, client.getHighlighters());
    }

    /**
     * Test property change event on setHighlighters for JXTable.
     */
    @Test
    public void testSetHighlightersChangeEvent() {
        HighlighterClient client = createHighlighterClient();
        
        PropertyChangeListener pcl = mock(PropertyChangeListener.class);
        client.addPropertyChangeListener(pcl);
        
        Highlighter[] old = client.getHighlighters();
        client.setHighlighters(new ColorHighlighter());
        
        verify(pcl).propertyChange(argThat(is(property("highlighters", old, client.getHighlighters()))));
    }

    /**
     * Sanity: handles empty array.
     */
    @Test
    public void testSetHighlightersEmptyArray() {
        HighlighterClient client = createHighlighterClient();
        int initialCount = client.getHighlighters().length;
        client.setHighlighters(new Highlighter[] {});
        assertEquals(initialCount, client.getHighlighters().length);
    }

    /**
     * 
     * Test that setting zero highlighter removes all.
     */
    @Test
    public void testSetHighlightersNoArgument() {
        HighlighterClient client = createHighlighterClient();
        int initialCount = client.getHighlighters().length;
        client.addHighlighter(new ColorHighlighter());
        // sanity
        assertEquals(initialCount + 1, client.getHighlighters().length);
        client.setHighlighters();
        assertEquals(initialCount + 0, client.getHighlighters().length);
    }

    /**
     * Test strict enforcement of not null allowed in setHighlighters for JXTable.
     * 
     * Here: null highlighter.
     */
    @Test
    public void testSetHighlightersNullHighlighter() {
        try {
            createHighlighterClient().setHighlighters((Highlighter) null);
            fail("illegal to call setHighlighters(null)");
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * Test strict enforcement of not null allowed in setHighlighters for JXTable.
     * 
     * Here: null array
     */
    @Test
    public void testSetHighlightersNullArray() {
        try {
            createHighlighterClient().setHighlighters((Highlighter[]) null);
            fail("illegal to call setHighlighters(null)");
            
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * Test strict enforcement of not null allowed in setHighlighters for JXTable.
     * 
     * Here: null array element.
     */
    @Test
    public void testSetHighlightersArrayNullElement() {
        try {
            createHighlighterClient().setHighlighters(new Highlighter[] {null});
            fail("illegal to call setHighlighters(null)");
            
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * test if removeHighlighter behaves as doc'ed.
     *
     */
    @Test
    public void testRemoveHighlighterTable() {
        HighlighterClient client = createHighlighterClient();
        int initialCount = client.getHighlighters().length;
        // test cope with null
        client.removeHighlighter(null);
        Highlighter presetHighlighter = new ColorHighlighter();
        client.setHighlighters(presetHighlighter);
        Highlighter[] highlighters = client.getHighlighters();
        // sanity
        assertEquals(initialCount + 1, highlighters.length);
        // remove uncontained
        client.removeHighlighter(new ColorHighlighter());
        // assert no change
        assertSameContent(highlighters, client.getHighlighters());
        client.removeHighlighter(presetHighlighter);
        assertEquals(initialCount, client.getHighlighters().length);
    }

    /**
     * Test property change event on removeHighlighter for JXTable.
     */
    @Test
    public void testRemoveHighlightersChangeEvent() {
        HighlighterClient table = createHighlighterClient();
        
        PropertyChangeListener pcl = mock(PropertyChangeListener.class);
        table.addPropertyChangeListener(pcl);
        
        Highlighter highlighter = new ColorHighlighter();
        table.setHighlighters(highlighter);
        Highlighter[] old = table.getHighlighters();
        table.removeHighlighter(highlighter);
        
        verify(pcl).propertyChange(argThat(is(property("highlighters", old, table.getHighlighters()))));
    }

    /**
     * test if addHighlighter behaves as doc'ed for JXTable.
     *
     */
    @Test
    public void testAddHighlighter() {
        HighlighterClient client = createHighlighterClient();
        int initialCount = client.getHighlighters().length;
        Highlighter presetHighlighter = new ColorHighlighter();
        // add the first
        client.addHighlighter(presetHighlighter);
        // assert that it is added
        assertEquals(initialCount + 1, client.getHighlighters().length);
        assertAsLast(client.getHighlighters(), presetHighlighter);
        Highlighter highlighter = new ColorHighlighter();
        // add the second
        client.addHighlighter(highlighter);
        assertEquals(initialCount + 2, client.getHighlighters().length);
        // assert that it is appended
        assertAsLast(client.getHighlighters(), highlighter);
    }

    /**
     * Test property change event on addHighlighter for JXTable.
     */
    @Test
    public void testAddHighlighterChangeEvent() {
        HighlighterClient table = createHighlighterClient();
        
        PropertyChangeListener pcl = mock(PropertyChangeListener.class);
        table.addPropertyChangeListener(pcl);
        
        Highlighter[] old = table.getHighlighters();
        table.addHighlighter(new ColorHighlighter());
        
        verify(pcl).propertyChange(argThat(is(property("highlighters", old, table.getHighlighters()))));
    }

    /**
     * test choking on precondition failure (highlighter must not be null) for JTXTable.
     *
     */
    @Test
    public void testAddNullHighlighter() {
        try {
            createHighlighterClient().addHighlighter(null);
            fail("adding a null highlighter must throw NPE");
        } catch (NullPointerException e) {
            // pass - this is what we expect
        } catch (Exception e) {
            fail("adding a null highlighter throws exception different " +
                        "from the expected NPE \n" + e);
        }
    }


    
    
    /**
     * Same content in both.
     * @param highlighters
     * @param highlighters2
     */
    private void assertSameContent(Highlighter[] highlighters, Highlighter[] highlighters2) {
        assertEquals(highlighters.length, highlighters2.length);
        for (int i = 0; i < highlighters.length; i++) {
            assertSame("must contain same element", highlighters[i], highlighters2[i]);
        }
    }
    
    /**
     * Last in list.
     * 
     * @param highlighters
     * @param highlighter
     */
    private void assertAsLast(Highlighter[] highlighters, Highlighter highlighter) {
        assertTrue("pipeline must not be empty", highlighters.length > 0);
        assertSame("highlighter must be added as last", highlighter, highlighters[highlighters.length - 1]);
    }

    /**
     * This interface defines the common contract of clients which provide
     * support for Highlighters. They must
     * 
     * <ul>
     * <li> have a bound property "highlighters" denoting a collection of
     * Highlighters
     * <li> have methods to modify the collection
     * <li> update the ui of contained Highlighters on LAF changes
     * <li> apply the highlighters as appropriate. This implies that it must
     * listen to highlighter state changes to update itself (or related parties)
     * accordingly.
     * </ul>
     * 
     * The last bullet is a "vague" requirement in that it might vary
     * considerably across client implementations. While JComponents typically
     * will invoke a repaint, a non-component might choose to notify some other
     * listeners. Furthermore, it's not testable, as clients might choose to do
     * the actual update only if really needed, f.i. not if invisible or if
     * there are no external listeners.
     * <p>
     * 
     */
    public static interface HighlighterClient {

        /**
         * Sets the <code>Highlighter</code>s to this client, replacing any old settings.
         * No argument or an empty array removes all <code>Highlighter</code>s. <p>
         * 
         * This is a bound property.
         * 
         * @param highlighters zero or more not null highlighters to use for renderer decoration.
         * @throws NullPointerException if array is null or array contains null values.
         * 
         * @see #getHighlighters()
         * @see #addHighlighter(Highlighter)
         * @see #removeHighlighter(Highlighter)
         * 
         */
        void setHighlighters(Highlighter... highlighters);

        /**
         * Returns the <code>Highlighter</code>s used by this client.
         * Maybe empty, but guarantees to be never null.
         * 
         * @return the Highlighters used by this table, guaranteed to never null.
         * 
         * @see #setHighlighters(Highlighter[])
         */
        Highlighter[] getHighlighters();

        /**
         * Appends a <code>Highlighter</code> to the end of the list of used
         * <code>Highlighter</code>s. The argument must not be null. 
         * <p>
         * 
         * @param highlighter the <code>Highlighter</code> to add, must not be null.
         * @throws NullPointerException if <code>Highlighter</code> is null.
         * 
         * @see #removeHighlighter(Highlighter)
         * @see #setHighlighters(Highlighter[])
         */
        void addHighlighter(Highlighter highlighter);

        /**
         * Removes the given <code>Highlighter</code>. Does nothing if the
         * <code>Highlighter</code> is not contained.
         * 
         * @param highlighter the <code>Highlighter</code> to remove.
         * 
         * @see #addHighlighter(Highlighter)
         * @see #setHighlighters(Highlighter...)
         */
        void removeHighlighter(Highlighter highlighter);

        /**
         * Updates contained Highlighters on LAF changes.
         */
        void updateUI();

        /**
         * Adds a PropertyChangeListener which will be notified on changes of the 
         * "highlighters" property.
         * 
         * @param l the listener to add.
         */
        void addPropertyChangeListener(PropertyChangeListener l);

        /**
         * Removes the <code>PropertyChangeListener</code>. Does nothing if the
         * listener is not contained.
         * 
         * @param l the listener to remove. 
         */
        void removePropertyChangeListener(PropertyChangeListener l);
    }
}
