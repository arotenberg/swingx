/*
 * $Id: RootPaneTest.java 3473 2009-08-27 13:17:10Z kleopatra $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * There are several commented out portions of this file. They should be moved
 * or changed once a "status bean" is implemented that provides the functionality
 * originally envisioned for the JXRootPane/JXStatusBar coupling
 */
@RunWith(JUnit4.class)
public class RootPaneTest extends TestCase {

    private Action[] actions;
    private JLabel[] comps;

    private static final int HEAD = 0;
    private static final int BODY = 1;
    private static final int ARMS = 2;
    private static final int LEGS = 3;

    @Before
    public void setUpJ4() throws Exception {
        setUp();
    }
    
    @After
    public void tearDownJ4() throws Exception {
        tearDown();
    }
    
    @Override
    protected void setUp() {
	actions =  new Action[4];
	actions[0] = new TestAction("New", 'N', "Create a new item");
	actions[1] = new TestAction("Open", 'O', "Opens an item");
	actions[2] = new TestAction("Save", 'S', "Saves an item");
	actions[3] = new TestAction("Exit", 'X', "Exits the application");

	comps = new JLabel[4];
	comps[0] = new JLabel("Head");
	comps[1] = new JLabel("Body");
	comps[2] = new JLabel("Arms");
	comps[3] = new JLabel("Legs");
    }

    @Override
    protected void tearDown() {
	for (int i = 0; i < actions.length; i++) {
	    actions[i] = null;
	    comps[i] = null;
	}
	actions = null;
	comps = null;
    }

    /**
     * Simple test to ensure that components are added/removed
     * and registered/unregistered correctly.
     */
    public void JPanelRegistration() {
	JXRootPane rootPane = new JXRootPane();
	JXStatusBar statusBar = new JXStatusBar();
	rootPane.setStatusBar(statusBar);

	for (int i = 0; i < comps.length; i++) {
	    rootPane.getContentPane().add(comps[i]);
	}

	Component[] cs = rootPane.getContentPane().getComponents();
	assertEquals(cs.length, comps.length);

//	// Ensure that messages are passed to the
//	// status bar. The PERSISTENT message is sent to the
//	// trailing message location.
//	for (int i = 0; i < comps.length; i++) {
//	    JPanel comp = comps[i];
//	    comp.sendMessage();
//	    assertEquals(comp.getMessage(), statusBar.getTrailingMessage());
//	}
//
	// Remove all components.
	for (int i = 0; i < comps.length; i++) {
	    rootPane.getContentPane().remove(comps[i]);
	}
	cs = rootPane.getContentPane().getComponents();
	assertEquals(cs.length, 0);

//	// Ensure that the status bar has been unregistered.
//	statusBar.setTrailingMessage("");
//	for (int i = 0; i < comps.length; i++) {
//	    JPanel comp = comps[i];
//	    comp.sendMessage();
//	    assertEquals("", statusBar.getTrailingMessage());
//	}
    }

    /**
     * Test to ensure that all MessageSources in a containment hierarchy
     * are registered.
     */
    @Test
    public void testAggregateContainerRegistration() {
	// Create an aggregate.
	comps[HEAD].add(comps[BODY]);
	comps[BODY].add(comps[ARMS]);
	comps[BODY].add(comps[LEGS]);

	JXRootPane rootPane = new JXRootPane();
	JXStatusBar statusBar = new JXStatusBar();
	rootPane.setStatusBar(statusBar);

	rootPane.getContentPane().add(comps[HEAD]);

	Component[] cs = rootPane.getContentPane().getComponents();
	assertEquals(1, cs.length);

//	// The status bar should get all messages send to all
//	// components.
//	for (int i = 0; i < comps.length; i++) {
//	    JPanel comp = comps[i];
//	    comp.sendMessage();
//	    assertEquals(comp.getMessage(), statusBar.getTrailingMessage());
//	}

	// Remove the head. There shouldn't be any components.
	rootPane.getContentPane().remove(comps[HEAD]);
	cs = rootPane.getContentPane().getComponents();
	assertEquals(0, cs.length);

//	// Ensure that the status bar has been unregistered.
//	statusBar.setTrailingMessage("");
//	for (int i = 0; i < comps.length; i++) {
//	    JPanel comp = comps[i];
//	    comp.sendMessage();
//	    assertEquals("", statusBar.getTrailingMessage());
//	}
    }

    /**
     * A test to ensure that the status bar is correctly registered with
     * existing components and will be unregistered when removed.
     */
    @Test
    public void testStatusBar() {
	JXRootPane rootPane = new JXRootPane();
	for (int i = 0; i < comps.length; i++) {
	    rootPane.add(comps[i]);
	}

	JXStatusBar statusBar = new JXStatusBar();
	rootPane.setStatusBar(statusBar);

//	// The status bar should get all messages send to all
//	// components.
//	for (int i = 0; i < comps.length; i++) {
//	    JPanel comp = comps[i];
//	    comp.sendMessage();
//	    assertEquals(comp.getMessage(), statusBar.getTrailingMessage());
//	}

	// Change the status bar. Reset the old status bar. It shouldn't get
	// any messages.
	rootPane.setStatusBar(new JXStatusBar());
	assertNotSame(statusBar, rootPane.getStatusBar());

//	// Ensure that the status bar has been unregistered.
//	statusBar.setTrailingMessage("");
//	for (int i = 0; i < comps.length; i++) {
//	    JPanel comp = comps[i];
//	    comp.sendMessage();
//	    assertEquals("", statusBar.getTrailingMessage());
//	}
    }

    /**
     * Given a root pane with a status bar, a toolbar with components
     * should get mouse listeners added to the components when added.
     */
    @Test
    public void testToolBar() {
	JXRootPane rootPane = new JXRootPane();
	rootPane.setStatusBar(new JXStatusBar());

	JToolBar toolBar = new JToolBar();
	for (int i = 0; i < actions.length; i++) {
	    toolBar.add(actions[i]);
	}

	// set the baseline number of mouse listeners
	Component[] comps = toolBar.getComponents();
	int[] original = new int[comps.length];
	for (int i = 0; i < comps.length; i++) {
	    MouseListener[] listeners = comps[i].getMouseListeners();
	    original[i] = listeners.length;
	}

	// Add the toolbar and mouse listeners should be registered
	rootPane.setToolBar(toolBar);

	comps = toolBar.getComponents();
//	for (int i = 0; i < comps.length; i++) {
//	    MouseListener[] listeners = comps[i].getMouseListeners();
//	    assertEquals(original[i] + 1, listeners.length);
//	}

	// the toolbar is replaces. MouseListeners should be unregistered
	rootPane.setToolBar(new JToolBar());

	comps = toolBar.getComponents();
	for (int i = 0; i < comps.length; i++) {
	    MouseListener[] listeners = comps[i].getMouseListeners();
	    assertEquals(original[i], listeners.length);
	}
    }

    @Test
    public void testMenuBar() {
	JXRootPane rootPane = new JXRootPane();
	rootPane.setStatusBar(new JXStatusBar());

	JMenuBar menuBar = new JMenuBar();
	JMenu menu = new JMenu("File");

	for (int i = 0; i < actions.length; i++) {
	    menu.add(actions[i]);
	}
	menuBar.add(menu);

	// set the baseline number of mouse listeners
	Component[] comps = menu.getComponents();
	int[] original = new int[comps.length];
	for (int i = 0; i < comps.length; i++) {
	    MouseListener[] listeners = comps[i].getMouseListeners();
	    original[i] = listeners.length;
	}

	// Add the toolbar and mouse listeners should be registered
	rootPane.setJMenuBar(menuBar);

	comps = menu.getComponents();
	for (int i = 0; i < comps.length; i++) {
	    MouseListener[] listeners = comps[i].getMouseListeners();
	    assertEquals(original[i] + 1, listeners.length);
	}

	// the toolbar is replaces. MouseListeners should be unregistered
	rootPane.setJMenuBar(new JMenuBar());

	for (int i = 0; i < comps.length; i++) {
	    MouseListener[] listeners = comps[i].getMouseListeners();
	    original[i] = listeners.length;
	}
    }

    public static void main(String[] args) {
	Action[] actions =  new Action[4];
	actions[0] = new TestAction("New", 'N', "Create a new item");
	actions[1] = new TestAction("Open", 'O', "Opens an item");
	actions[2] = new TestAction("Save", 'S', "Saves an item");
	actions[3] = new TestAction("Exit", 'X', "Exits the application");

	JToolBar toolBar = new JToolBar();
	JMenuBar menuBar = new JMenuBar();
	JMenu menu = new JMenu("File");

	for (int i = 0; i < actions.length; i++) {
	    toolBar.add(actions[i]);
	    menu.add(actions[i]);
	}
	menuBar.add(menu);

	JXRootPane rootPane = new JXRootPane();

	Component[] comps = new Component[4];
	comps[0] = new JLabel("Head");
	comps[1] = new JLabel("Body");
	comps[2] = new JLabel("Arms");
	comps[3] = new JLabel("Legs");

	rootPane.setStatusBar(new JXStatusBar());
	rootPane.setToolBar(toolBar);
	rootPane.setJMenuBar(menuBar);

	for (int i = 0; i < comps.length; i++) {
	    rootPane.add(comps[i]);
	}
	rootPane.add(new JPanel());

	JXFrame frame = new JXFrame();
	frame.setRootPane(rootPane);
	frame.setVisible(true);
    }

    /**
     * A simple action which can be used for creating components.
     */
    public static class TestAction extends AbstractAction {

	public TestAction(String name, int mnemonic,
			  String description) {
	    super(name);
	    putValue(Action.MNEMONIC_KEY, new Integer(mnemonic));
	    putValue(Action.LONG_DESCRIPTION, description);
	}
	public void actionPerformed(ActionEvent evt) {}
    }

}
