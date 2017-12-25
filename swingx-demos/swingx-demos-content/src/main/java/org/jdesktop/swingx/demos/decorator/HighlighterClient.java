/*
 * Created on 08.12.2008
 *
 */
package org.jdesktop.swingx.demos.decorator;

import java.beans.PropertyChangeListener;

import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.plaf.UIDependent;

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
public interface HighlighterClient extends UIDependent {

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
