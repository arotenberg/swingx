/*
 * $Id: RootPaneMessagingTest.java 2977 2008-07-07 03:17:53Z kschaefe $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import junit.framework.TestCase;

import org.jdesktop.swingx.event.MessageListener;
import org.jdesktop.swingx.event.MessageSource;
import org.jdesktop.swingx.event.MessageSourceSupport;
import org.jdesktop.swingx.event.ProgressListener;
import org.jdesktop.swingx.event.ProgressSource;

/**
 * There are several commented out portions of this file. They should be moved
 * or changed once a "status bean" is implemented that provides the functionality
 * originally envisioned for the JXRootPane/JXStatusBar coupling
 */
public class RootPaneMessagingTest extends TestCase {

    private Action[] actions;
    private TestComponent[] comps;

    private static final int HEAD = 0;
    private static final int BODY = 1;
    private static final int ARMS = 2;
    private static final int LEGS = 3;

    @Override
    protected void setUp() {
	actions =  new Action[4];
	actions[0] = new TestAction("New", 'N', "Create a new item");
	actions[1] = new TestAction("Open", 'O', "Opens an item");
	actions[2] = new TestAction("Save", 'S', "Saves an item");
	actions[3] = new TestAction("Exit", 'X', "Exits the application");

	comps = new TestComponent[4];
	comps[0] = new TestComponent("Head");
	comps[1] = new TestComponent("Body");
	comps[2] = new TestComponent("Arms");
	comps[3] = new TestComponent("Legs");
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
    public void testComponentRegistration() {
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
//	    TestComponent comp = comps[i];
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
//	    TestComponent comp = comps[i];
//	    comp.sendMessage();
//	    assertEquals("", statusBar.getTrailingMessage());
//	}
    }

    /**
     * Test to ensure that all MessageSources in a containment hierarchy
     * are registered.
     */
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
//	    TestComponent comp = comps[i];
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
//	    TestComponent comp = comps[i];
//	    comp.sendMessage();
//	    assertEquals("", statusBar.getTrailingMessage());
//	}
    }

    /**
     * A test to ensure that the status bar is correctly registered with
     * existing components and will be unregistered when removed.
     */
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
//	    TestComponent comp = comps[i];
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
//	    TestComponent comp = comps[i];
//	    comp.sendMessage();
//	    assertEquals("", statusBar.getTrailingMessage());
//	}
    }

    /**
     * Given a root pane with a status bar, a toolbar with components
     * should get mouse listeners added to the components when added.
     */
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
	comps[0] = new TestComponent("Head");
	comps[1] = new TestComponent("Body");
	comps[2] = new TestComponent("Arms");
	comps[3] = new TestComponent("Legs");

	rootPane.setStatusBar(new JXStatusBar());
	rootPane.setToolBar(toolBar);
	rootPane.setJMenuBar(menuBar);

	for (int i = 0; i < comps.length; i++) {
	    rootPane.add(comps[i]);
	}
	rootPane.add(new ProgressComponent());

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

    /**
     * A panel with some buttons to fire progress messages.
     */
    public static class ProgressComponent extends TestComponent
	implements ActionListener {

	private JButton start, stop, progress;

	public ProgressComponent() {
	    super();
	}

	@Override
    protected void initUI() {
	    setBorder(BorderFactory.createTitledBorder("Progress Messages"));
	    start = new JButton("Start");
	    start.addActionListener(this);

	    stop = new JButton("Stop");
	    stop.addActionListener(this);

	    progress = new JButton("Progress");
	    progress.addActionListener(this);

	    add(start);
	    add(stop);
	    add(progress);
	}

	public void actionPerformed(ActionEvent evt) {
	    Object source = evt.getSource();
	    if (source == start) {
		support.fireProgressStarted(0,0);
	    } else if (source == stop) {
		support.fireProgressEnded();
	    } else if (source == progress) {
		doLongOperation();
	    }
	}

	/**
	 * Demonstrates how to use the progress bar feature.
	 */
	private void doLongOperation() {
	    final int start = 0;
	    final int stop = 100;

	    // Initialized the progress start.
	    support.fireProgressStarted(start, stop);

	    final Timer timer = new Timer();

	    TimerTask task = new TimerTask() {
		    int progress = start;
		    @Override
            public void run() {
			progress += 10;
			if (progress > stop) {
			    support.fireProgressEnded();
			    timer.cancel();
			} else {
			    support.fireProgressIncremented(progress);
			}
		    }
		};
	    timer.schedule(task, 250L, 250L);


	}

    }


    /**
     * A test component which is a source of messages.
     */
    public static class TestComponent extends JPanel implements MessageSource,
								ProgressSource {

	protected  MessageSourceSupport support;
	private JLabel label;
	private String text;

	public TestComponent(String text) {
	    support = new MessageSourceSupport(this);
	    addMouseListener(new MouseHandler());
	    this.text = text;
	    initUI();
	}

	public TestComponent() {
	    this("TestComponent");
	}

	protected void initUI() {
	    label = new JLabel(text);
	    add(label);
	}

	public void addMessageListener(MessageListener l) {
	    support.addMessageListener(l);
	}

	public void removeMessageListener(MessageListener l) {
	    support.removeMessageListener(l);
	}

	public MessageListener[] getMessageListeners() {
	    return support.getMessageListeners();
	}

	public void addProgressListener(ProgressListener l) {
	    support.addProgressListener(l);
	}

	public void removeProgressListener(ProgressListener l) {
	    support.removeProgressListener(l);
	}

	public ProgressListener[] getProgressListeners() {
	    return support.getProgressListeners();
	}

	// Returns a message identifying this component.
	public String getMessage() {
	    return "I'm a " + text;
	}

	// Sends a persistent message to the any listeners.
	public void sendMessage() {
	    support.fireMessage(getMessage());
	}

	private class MouseHandler extends MouseAdapter {
	    @Override
        public void mouseEntered(MouseEvent evt) {
		sendMessage();
	    }
	}
    }

}
