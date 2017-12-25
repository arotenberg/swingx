/*
 * $Id: JXEditorPaneVisualCheck.java 3553 2009-11-03 11:19:35Z kleopatra $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JEditorPane;
import javax.swing.JToolBar;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.action.ActionContainerFactory;
import org.jdesktop.swingx.action.ActionFactory;
import org.jdesktop.swingx.action.ActionManager;

/**
 * A unit test for the JXEditorPane
 *
 * @author Mark Davidson
 */
public class JXEditorPaneVisualCheck extends JXEditorPaneTest {
    @SuppressWarnings("all")
    private static final Logger LOG = Logger.getLogger(JXEditorPaneVisualCheck.class
            .getName());
    private static String testText = "This is an example of some text";

    public static void main(String[] args) throws Exception {
//      setSystemLF(true);
      JXEditorPaneVisualCheck test = new JXEditorPaneVisualCheck();
      try {
          test.runInteractiveTests();
//          test.runInteractiveTests("interactive.*Table.*");
//          test.runInteractiveTests("interactive.*List.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        } 
  }
    /**
     * Issue #289-swingx: JXEditorPane actions should be disabled if not
     * applicable.
     * checking action enabled behaviour of core editorpane.
     * Doing nothing to enable/disable depending on editable state?
     * @throws IOException 
     *
     */
    public void interactiveXEditorStyledActions() throws IOException {
        JXEditorPane editor = new JXEditorPane();
        editor.setEditable(false);
        editor.setPreferredSize(new Dimension(600, 400));
        Action[] actions = editor.getActions();
        ActionManager manager = ActionManager.getInstance();
        List<Object> actionNames = new ArrayList<Object>();
        StringBuffer buffer = new StringBuffer("No. of default actions: " + actions.length);
        ActionMap map = editor.getActionMap();
        Object[] keys = map.keys();
        int count = keys != null ? keys.length : 0;
        buffer.append("\n No. of actions in ActionMap: " + count);
        for (int i = 0; i < actions.length; i++) {
            // TODO: are names allowed to be anything else as String?
            // same question in other test methods as well
            Object id = actions[i].getValue(Action.NAME);
            // ?? the id in the actionManager is doc'ed as ACTION_COMMAND?
            // which would imply to be a String (assumption somewhere in core)
            manager.addAction(id, actions[i]);
            actionNames.add(id);
            buffer.append("\n" + actions[i].toString());
        }
        
        
        editor.setText(buffer.toString());
        ActionContainerFactory factory = new ActionContainerFactory(manager);

      JToolBar toolbar = factory.createToolBar(actionNames);
      toolbar.setOrientation(JToolBar.VERTICAL);
      editor.setEditable(false);
      editor.setPreferredSize(new Dimension(600, 400));
      JXList list = new JXList(true);
      list.setModel(createListModel(actionNames));
      
      JXFrame frame = wrapWithScrollingInFrame(editor, list, "Looking at swingx editor default and styled actions (show in editor <--> list)");
      frame.getContentPane().add(toolbar, BorderLayout.WEST);
      frame.setVisible(true);
    }


    private ListModel createListModel(final List<Object> actionNames) {
        ListModel model = new ListModel() {

            public int getSize() {
                return actionNames.size();
            }

            public Object getElementAt(int index) {
                return actionNames.get(index);
            }

            public void addListDataListener(ListDataListener l) {
                // TODO Auto-generated method stub
                
            }

            public void removeListDataListener(ListDataListener l) {
                // TODO Auto-generated method stub
                
            }
            
        };
        return model;
    }

    /**
     * Issue #289-swingx: JXEditorPane actions should be disabled if not
     * applicable.
     * checking action enabled behaviour of core editorpane.
     * Doing nothing to enable/disable depending on editable state?
     *
     */
    public void interactiveXEditorDefaultActions() {
        JXEditorPane editor = new JXEditorPane();
        editor.setEditable(false);
        editor.setPreferredSize(new Dimension(600, 400));
        Action[] actions = editor.getActions();
        ActionManager manager = ActionManager.getInstance();
        List<Object> actionNames = new ArrayList<Object>();
        StringBuffer buffer = new StringBuffer("No. of default actions: " + actions.length);
        ActionMap map = editor.getActionMap();
        Object[] keys = map.keys();
        int count = keys != null ? keys.length : 0;
        buffer.append("\n No. of actions in ActionMap: " + count);
        for (int i = 0; i < actions.length; i++) {
            Object id = actions[i].getValue(Action.NAME);
            manager.addAction(id, actions[i]);
            actionNames.add(id);
            buffer.append("\n" + actions[i].toString());
        }
        
        
        editor.setText(buffer.toString());
        ActionContainerFactory factory = new ActionContainerFactory(manager);

      JToolBar toolbar = factory.createToolBar(actionNames);
      toolbar.setOrientation(JToolBar.VERTICAL);
      editor.setEditable(false);
      editor.setPreferredSize(new Dimension(600, 400));

      JXFrame frame = wrapWithScrollingInFrame(editor, "Looking at swingx editor default actions (show in xeditor)");
      frame.getContentPane().add(toolbar, BorderLayout.WEST);
      frame.setVisible(true);
    }

    /**
     * Issue #289-swingx: JXEditorPane actions should be disabled if not
     * applicable.
     * checking action enabled behaviour of core editorpane.
     * Doing nothing to enable/disable depending on editable state?
     *
     */
    public void interactiveEditorDefaultActions() {
        JEditorPane editor = new JEditorPane();
        editor.setText(testText);
        Action[] actions = editor.getActions();
        ActionManager manager = ActionManager.getInstance();
        List<Object> actionNames = new ArrayList<Object>();
        StringBuffer buffer = new StringBuffer("No. of default actions: " + actions.length);
        ActionMap map = editor.getActionMap();
        Object[] keys = map.keys();
        int count = keys != null ? keys.length : 0;
        buffer.append("\n No. of actions in ActionMap: " + count);
        for (int i = 0; i < actions.length; i++) {
            Object id = actions[i].getValue(Action.NAME);
            manager.addAction(id, actions[i]);
            actionNames.add(id);
            buffer.append("\n" + actions[i].toString());
        }
        editor.setText(buffer.toString());
        ActionContainerFactory factory = new ActionContainerFactory(manager);

      JToolBar toolbar = factory.createToolBar(actionNames);
      toolbar.setOrientation(JToolBar.VERTICAL);
      editor.setEditable(false);
      editor.setPreferredSize(new Dimension(600, 400));

      JXFrame frame = wrapWithScrollingInFrame(editor, "Looking at core default actions (show in core editor)");
      frame.getContentPane().add(toolbar, BorderLayout.WEST);
      frame.setVisible(true);
    }
    /**
     * JW: this is oold - no idea if that's the way to handle actions!.
     *
     */
    public void interactiveXEditorActions() {
        //AbstractActionExt[] actions = new AbstractActionExt[14];
        List<AbstractActionExt> actions = new ArrayList<AbstractActionExt>();
        actions.add(ActionFactory.createTargetableAction("cut-to-clipboard", "Cut", "C"));
        actions.add(ActionFactory.createTargetableAction("copy-to-clipboard", "Copy", "P"));
        actions.add(ActionFactory.createTargetableAction("paste-from-clipboard", "Paste", "T"));

        actions.add(ActionFactory.createTargetableAction("undo", "Undo", "U"));
        actions.add(ActionFactory.createTargetableAction("redo", "Redo", "R"));
        actions.add(ActionFactory.createTargetableAction("insert-break", "LF", "F"));

        actions.add(ActionFactory.createTargetableAction("left-justify", "Left", "L", true,
                                                          "position-group"));
        actions.add(ActionFactory.createTargetableAction("center-justify", "Center", "C", true,
                                                          "position-group"));
        actions.add(ActionFactory.createTargetableAction("right-justify", "Right", "R", true,
                                                          "position-group"));

        actions.add(ActionFactory.createTargetableAction("font-bold", "Bold", "B", true));
        actions.add(ActionFactory.createTargetableAction("font-italic", "Italic", "I", true));
        actions.add(ActionFactory.createTargetableAction("font-underline", "Underline", "U", true));

        actions.add(ActionFactory.createTargetableAction("InsertUnorderedList", "UL", "U", true));
        actions.add(ActionFactory.createTargetableAction("InsertOrderedList", "OL", "O", true));
        actions.add(ActionFactory.createTargetableAction("InsertHR", "HR", "H"));

        ActionManager manager = ActionManager.getInstance();
        List<Object> actionNames = new ArrayList<Object>();
        for (AbstractActionExt ext : actions) {
            manager.addAction(ext);
            actionNames.add(ext.getActionCommand());
            
        }
        
        // Populate the toolbar. Must use the ActionContainerFactory to ensure
        // that toggle actions are supported.
        ActionContainerFactory factory = new ActionContainerFactory(manager);

        JToolBar toolbar = factory.createToolBar(actionNames);
        
        URL url = JXEditorPaneVisualCheck.class.getResource("resources/test.html");
        JXEditorPane editor = null;
        try {
            editor = new JXEditorPane(url);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        editor.setEditable(false);
        editor.setPreferredSize(new Dimension(600, 400));

//        toolbar.add(editor.getParagraphSelector());

        JXFrame frame = wrapWithScrollingInFrame(editor, "Swingx Editor tester");
        frame.getContentPane().add(toolbar, BorderLayout.NORTH);

        frame.pack();
        frame.setVisible(true);
    }
    /**
     * JW: this is oold - no idea if that's the way to handle actions!.
     *
     */
    public void interactiveEditorActions() {
        //AbstractActionExt[] actions = new AbstractActionExt[14];
        List<AbstractActionExt> actions = new ArrayList<AbstractActionExt>();
//        actions.add(ActionFactory.createTargetableAction("cut-to-clipboard", "Cut", "C"));
//        actions.add(ActionFactory.createTargetableAction("copy-to-clipboard", "Copy", "P"));
//        actions.add(ActionFactory.createTargetableAction("paste-from-clipboard", "Paste", "T"));
        actions.add(ActionFactory.createTargetableAction("cut", "Cut", "C"));
        actions.add(ActionFactory.createTargetableAction("copy", "Copy", "P"));
        actions.add(ActionFactory.createTargetableAction("paste", "Paste", "T"));

        actions.add(ActionFactory.createTargetableAction("undo", "Undo", "U"));
        actions.add(ActionFactory.createTargetableAction("redo", "Redo", "R"));
        actions.add(ActionFactory.createTargetableAction("insert-break", "LF", "F"));

        actions.add(ActionFactory.createTargetableAction("left-justify", "Left", "L", true,
                                                          "position-group"));
        actions.add(ActionFactory.createTargetableAction("center-justify", "Center", "C", true,
                                                          "position-group"));
        actions.add(ActionFactory.createTargetableAction("right-justify", "Right", "R", true,
                                                          "position-group"));

        actions.add(ActionFactory.createTargetableAction("font-bold", "Bold", "B", true));
        actions.add(ActionFactory.createTargetableAction("font-italic", "Italic", "I", true));
        actions.add(ActionFactory.createTargetableAction("font-underline", "Underline", "U", true));

        actions.add(ActionFactory.createTargetableAction("InsertUnorderedList", "UL", "U", true));
        actions.add(ActionFactory.createTargetableAction("InsertOrderedList", "OL", "O", true));
        actions.add(ActionFactory.createTargetableAction("InsertHR", "HR", "H"));

        ActionManager manager = ActionManager.getInstance();
        List<Object> actionNames = new ArrayList<Object>();
        for (AbstractActionExt ext : actions) {
            manager.addAction(ext);
            actionNames.add(ext.getActionCommand());
            
        }
        
        // Populate the toolbar. Must use the ActionContainerFactory to ensure
        // that toggle actions are supported.
        ActionContainerFactory factory = new ActionContainerFactory(manager);

        JToolBar toolbar = factory.createToolBar(actionNames);
        
        URL url = JXEditorPaneVisualCheck.class.getResource("resources/test.html");
        JEditorPane editor = null;
        try {
            editor = new JEditorPane(url);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        editor.setEditable(false);
        editor.setPreferredSize(new Dimension(600, 400));

//        toolbar.add(editor.getParagraphSelector());

        JXFrame frame = wrapWithScrollingInFrame(editor, "Core Editor tester");
        frame.getContentPane().add(toolbar, BorderLayout.NORTH);

        frame.pack();
        frame.setVisible(true);
    }
}
