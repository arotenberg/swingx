/*
 * $Id: TargetableActionTest.java 3475 2009-08-28 08:30:47Z kleopatra $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.action;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import junit.framework.TestCase;

import org.jdesktop.swingx.JXEditorPane;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.JXTreeTable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * A unit test for targetable actions.
 */
@RunWith(JUnit4.class)
public class TargetableActionTest extends TestCase {

    @Test
    public void testDummy() {
    }

    public static void main(String[] args) {
        showActionMaps();
        showUI();
    }

    /**
     * These are cover methods for the same methods in ActionFactory.
     */
    public static TargetableAction createTargetableAction(String id, String name) {
        return createTargetableAction(id, name, null);
    }

    public static TargetableAction createTargetableAction(String id, String name,
                                                          String mnemonic) {
        return createTargetableAction(id, name, mnemonic, false);
    }

    public static TargetableAction createTargetableAction(String id, String name,
                                                          String mnemonic, boolean toggle) {
        return ActionFactory.createTargetableAction(id, name, mnemonic, toggle, null);
    }

    public static TargetableAction createTargetableAction(String id, String name,
                                                          String mnemonic, boolean toggle,
                                                          String group) {
        return ActionFactory.createTargetableAction(id, name, mnemonic, toggle, group);
    }

    public static void showUI() {
        ActionManager manager = new ActionManager();
        ActionManager.setInstance(manager);

        // The action id must map to the action-ids in the component.
        manager.addAction(createTargetableAction("cut-to-clipboard", "Cut", "C"));
        manager.addAction(createTargetableAction("copy-to-clipboard", "Copy", "P"));
        manager.addAction(createTargetableAction("paste-from-clipboard", "Paste", "T"));
        manager.addAction(createTargetableAction("print", "Print", "P"));
        manager.addAction(createTargetableAction("find", "Find", "F"));
        manager.addAction(createTargetableAction("collapse-all", "Collapse", "l"));
        manager.addAction(createTargetableAction("expand-all", "Expand", "x"));
        // More toggle actions for a group
        manager.addAction(createTargetableAction("left-justify", "Left", "L", true,
                                                 "position-group"));
        manager.addAction(createTargetableAction("center-justify", "Center", "C", true,
                                                 "position-group"));
        manager.addAction(createTargetableAction("right-justify", "Right", "R", true,
                                                 "position-group"));

        List<String> list = new ArrayList<String>();
        list.add("cut-to-clipboard");
        list.add("copy-to-clipboard");
        list.add("paste-from-clipboard");
        list.add(null);
        list.add("left-justify");
        list.add("center-justify");
        list.add("right-justify");
        list.add(null);
        list.add("print");
        list.add("find");
        list.add("collapse-all");
        list.add("expand-all");

        // Use the factory to build components from lists.
        ActionContainerFactory factory = new ActionContainerFactory(manager);
        JToolBar toolbar = factory.createToolBar(list);

        JPanel panel = new JPanel(new GridLayout(2,2, 5, 5));
        JXEditorPane editor = new JXEditorPane("text/html", "This is an example of some text");
        panel.add(editor);
        panel.add(new JXEditorPane());
        panel.add(createTabbedPane());
        panel.add(createTree());

        JFrame frame = new JFrame();
        frame.getContentPane().add(toolbar, BorderLayout.NORTH);
        frame.getContentPane().add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public static JComponent createTabbedPane() {
        JTabbedPane pane = new JTabbedPane();
        pane.add(createTable());
        pane.add(createTable());
        pane.add(createTable());
        pane.add(createTable());


        // XXX - this is a way to support setting a target manager.
        // There are some bugs with this approach. Namely, there is no
        // reliable way for the TargetManager to be reset when th
        pane.addFocusListener(new FocusListener() {
                public void focusGained(FocusEvent evt) {
                    //          System.out.println("FocusGained: " + evt);
                    JTabbedPane pane = (JTabbedPane)evt.getSource();
                    Component comp = pane.getSelectedComponent();
                    if (comp instanceof Targetable) {
                        TargetManager.getInstance().setTarget((Targetable)comp);
                    }
                }

                public void focusLost(FocusEvent evt) {
                    //System.out.println("FocusLost: " + evt);
                    if (!evt.isTemporary()) {
                        TargetManager.getInstance().setTarget(null);
                    }
                }
            });

        pane.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent evt) {
                    JTabbedPane pane = (JTabbedPane)evt.getSource();
                    Component comp = pane.getSelectedComponent();
                    if (comp instanceof Targetable) {
                        TargetManager.getInstance().setTarget((Targetable)comp);
                    }
                }
            });


        return pane;
    }

    public static int tableNum = 0;

    public static JComponent createTable() {
        TableModel dataModel = new AbstractTableModel() {
                public int getColumnCount() { return 4; }
                public int getRowCount() { return 4;}
                public Object getValueAt(int row, int col) { return new Integer(row*col); }
                @Override
                public boolean isCellEditable(int row, int col) { return true; }
            };
        JXTable table = new JXTable(dataModel);
        table.setName("Table: " + tableNum++);

        return table;
    }

    public static JComponent createTree() {
        JTree jtree = new JTree();
        JXTree tree = new JXTree(jtree.getModel());
        tree.setEditable(true);

        // Stuff the action map.
        ActionMap map = tree.getActionMap();
        Action action = new MyAction("print", "Print on JTree");
        map.put(action.getValue(Action.NAME), action);
        action = new MyAction("find", "Find on JTree");
        map.put(action.getValue(Action.NAME), action);

        return tree;
    }

    /**
     * Simple action which displays a string when invoked.
     */
    public static class MyAction extends AbstractAction {

        private String description;

        public MyAction(String name, String desc) {
            super(name);
            this.description = desc;
        }

        public void actionPerformed(ActionEvent evt) {
            System.out.println(getValue(Action.NAME) + " has been invoked: " + description);
        }
    }


    public static void showActionMaps() {
        System.out.println("\nActionMap keys for JXTable\n===================");
        showActionMap(new JXTable());

        System.out.println("\nActionMap keys for JXEditorPane\n==================");
        showActionMap(new JXEditorPane());

        System.out.println("\nActionMap keys for an HTML JXEditorPane\n==================");
        showActionMap(new JXEditorPane("text/html", ""));

        System.out.println("\nActionMap keys for JXTree\n====================");
        showActionMap(new JXTree());

        System.out.println("\nActionMap keys for JXTreeTable\n====================");
        showActionMap(new JXTreeTable());
    }

    public static void showActionMap(JComponent comp) {
        ActionMap map = comp.getActionMap();
        Object[] keys = map.allKeys();

        if (keys != null) {
            for (int i = 0; i < keys.length; i++) {
                System.out.println(keys[i]);
            }
        }
    }
}
