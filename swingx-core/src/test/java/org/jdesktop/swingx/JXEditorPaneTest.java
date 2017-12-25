/*
 * $Id: JXEditorPaneTest.java 3473 2009-08-27 13:17:10Z kleopatra $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JEditorPane;
import javax.swing.text.html.HTMLDocument;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * A unit test for the JXEditorPane
 *
 * @author Mark Davidson
 */
@RunWith(JUnit4.class)
public class JXEditorPaneTest extends InteractiveTestCase {
    @SuppressWarnings("all")
    private static final Logger LOG = Logger.getLogger(JXEditorPaneTest.class
            .getName());
    private static String testText = "This is an example of some text";

    /**
     * Issue #289-swingx: JXEditorPane actions should be disabled if not
     * applicable.
     * undo is enabled if there is selected text and editor editable
     * disabled otherwise.
     * 
     */
    @Test
    public void testXDisabledUndoAction() {
        JXEditorPane editor = new JXEditorPane();
        editor.setEditable(false);
        editor.setText(testText);
        assertEquals(testText, editor.getText());
        Action action = editor.getActionMap().get("undo");
        assertFalse("undo must not be enabled", action.isEnabled());
    }

    /**
     * Issue #289-swingx: JXEditorPane actions should be disabled if not
     * applicable.
     * cut is enabled if there is selected text and editor editable
     * disabled otherwise.
     * 
     */
    @Test
    public void testXDisabledCutActionUnselected() {
        JXEditorPane editor = new JXEditorPane();
        editor.setText("some");
        editor.setEditable(true);
        Action action = editor.getActionMap().get("cut");
        assertFalse("cut of unselected must not be enabled", action.isEnabled());
        
    }
    
    /**
     * Issue #289-swingx: JXEditorPane actions should be disabled if not
     * applicable.
     * cut is enabled if there is selected text and editor editable
     * disabled otherwise.
     */
    @Test
    public void testXDisabledCutActionOnNotEditable() {
        JXEditorPane editor = new JXEditorPane();
        editor.setText("some");
        editor.selectAll();
        editor.setEditable(false);
        Action action = editor.getActionMap().get("cut");
        assertFalse("cut of uneditable editor must not be enabled", action.isEnabled());
        
    }
    /**
     * Issue #289-swingx: JXEditorPane actions should be disabled if not
     * applicable.
     * 
    *  paste is enabled if the editor is editable and the clipboard isn't empty,
    *  disabled otherwise.
     * 
     */
    @Test
    public void testXDisabledPasteActionOnNotEditable() {
        JXEditorPane editor = new JXEditorPane();
        editor.setEditable(false);
        Action action = editor.getActionMap().get("paste");
        assertFalse("paste of uneditable editor must not be enabled", action.isEnabled());
        
    }
    
    /**
     * Issue #289-swingx: JXEditorPane actions should be disabled if not
     * applicable.
    * 
    *  paste is enabled if the editor is editable the clipboard isn't empty.
    *  can't really test ... don't want to clear the clipboard as a test side-effect. 
     */
    @Test
    public void testXDisabledPasteEmptyClipboard() {
        JXEditorPane editor = new JXEditorPane();
        editor.setEditable(true);
        // can't really test - want to erase system clipboard?
//        Action action = editor.getActionMap().get("paste");
//        assertFalse("paste with empty clipboard must not be enabled", action.isEnabled());
        
    }
    
    @Test
    public void testInitialization() throws IOException {
        URL url = JXEditorPaneTest.class.getResource("resources/test.html");
        JXEditorPane editor = new JXEditorPane();
        editor.setContentType("text/html");
        editor.setPage(url);

        assertTrue(editor.getDocument() instanceof HTMLDocument);
        assertNotNull(editor.getCaretListener());
        assertNotNull(editor.getUndoableEditListener());

        editor = new JXEditorPane("text/html", "");
        editor.setPage(url);
        assertTrue(editor.getDocument() instanceof HTMLDocument);
        assertNotNull(editor.getCaretListener());
        assertNotNull(editor.getUndoableEditListener());

        editor = new JXEditorPane();
        assertFalse(editor.getDocument() instanceof HTMLDocument);
        assertNull(editor.getCaretListener());
    }

    @Test
    public void testRegistration() {

    }

    @Test
    public void testCutPastePlain() {
        JXEditorPane editor = new JXEditorPane("text/plain", testText);
        editorCutPaste(editor);
    }

    /**
     * XXX currently the html cut and paste is broken. A work around has been
     * implemented to use only plain text on the clip. This test will fail
     * if the text contains some markup like &lt;b&gt;foo&lt;/b&gt;
     */
    @Test
    public void testCutPasteHtml() {
        JXEditorPane editor = new JXEditorPane("text/html", testText);
        editorCutPaste(editor);
    }

    public void editorCutPaste(JEditorPane editor) {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }

        // set selection markers
        editor.select(4, 15);

        // get cut and paste actions and execute them simultaniously

        ActionMap map = editor.getActionMap();
        Action cut = map.get("cut-to-clipboard");
        Action paste = map.get("paste-from-clipboard");

        assertNotNull(cut);
        assertNotNull(paste);

        String before = editor.getText();

        cut.actionPerformed(new ActionEvent(editor, 0,
                                            (String)cut.getValue(Action.ACTION_COMMAND_KEY)));

        // XXX caret position should be moved in the cut operation
        editor.setCaretPosition(4);

        paste.actionPerformed(new ActionEvent(editor, 0,
                                              (String)paste.getValue(Action.ACTION_COMMAND_KEY)));
        assertEquals(before, editor.getText());
    }
}
