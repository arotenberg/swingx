/*
 * $Id: ActionManagerTest.java 3974 2011-03-21 14:35:05Z kschaefe $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.action;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.beans.Statement;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.JButton;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Unit test driver for the ActionManager
 *
 * TODO: Should test TargetableActions
 */
@RunWith(JUnit4.class)
public class ActionManagerTest extends TestCase {

    private ActionManager manager;

    @Before
    public void setUpJ4() throws Exception {
        setUp();
    }
    
    @After
    public void tearDownJ4() throws Exception {
        tearDown();
    }
    
    // TODO: Add more attributes which represent actions and types.
    @Override
    protected void setUp() {
        // JW: changed on reorg to remove reference to Application 
        //        manager = Application.getInstance().getActionManager();
        manager = ActionManager.getInstance();
        // Simple commands
        manager.addAction(createBoundAction("simple-command", "Simple", "S"));
        manager.addAction(createBoundAction("simple2-command", "Simple 2", "2"));
        manager.addAction(createBoundAction("simple3-command", "Simple 3", "3"));

        // Toggle action
        manager.addAction(createBoundAction("toggle-command", "Toggle",
                                            "T", true));
        // More toggle actions for a group
        manager.addAction(createBoundAction("left-command", "Left", "L", true,
                                            "position-group"));
        manager.addAction(createBoundAction("center-command", "Center", "C", true,
                                            "position-group"));
        manager.addAction(createBoundAction("right-command", "Right", "R", true,
                                            "position-group"));

        // Composite action
        CompositeAction action = ActionFactory.createCompositeAction("composite-command",
                                                                     "Composite", "C");
        action.addAction("simple-command");
        action.addAction("simple2-command");
        manager.addAction(action);

        // Server action
        ServerAction saction = ActionFactory.createServerAction("namefinder-command",
                                                                "NameFinder", "N");
        saction.setURL("http://namefinder.sfbay/NameFinder");
        saction.addParam("nfquery", "Mark Davidson");
        manager.addAction(saction);

        // XXX This doesn't work since google doesn't allow this.
        saction = ActionFactory.createServerAction("server-command", "Google", "G");
        saction.setURL("http://www.google.com/search");
        saction.addParam("q", "Zaphod+Beeblebrox");
        manager.addAction(saction);
    }

    public BoundAction createBoundAction(String id, String name,
                                         String mnemonic) {
        return createBoundAction(id, name, mnemonic, false);
    }

    public BoundAction createBoundAction(String id, String name,
                                         String mnemonic, boolean toggle) {
        return createBoundAction(id, name, mnemonic, toggle, null);
    }

    public BoundAction createBoundAction(String id, String name,
                                         String mnemonic, boolean toggle,
                                         String group) {
        return ActionFactory.createBoundAction(id, name, mnemonic, toggle, group);
    }
    /**
     * Test to see if the types of actions that are created map correctly.
     */
    @Test
    public void testActionTypes() {

        assertTrue(manager.isBoundAction("simple-command"));
        assertTrue(manager.isBoundAction("simple2-command"));
        assertTrue(manager.isBoundAction("simple3-command"));

        assertTrue(manager.isBoundAction("toggle-command"));
        assertTrue(manager.isBoundAction("left-command"));
        assertTrue(manager.isBoundAction("right-command"));
        assertTrue(manager.isBoundAction("center-command"));

        assertTrue(manager.isCompositeAction("composite-command"));

        assertTrue(manager.isServerAction("namefinder-command"));
        assertTrue(manager.isServerAction("server-command"));

        // state types

        assertTrue(!manager.isStateAction("simple-command"));
        assertTrue(!manager.isStateAction("simple2-command"));
        assertTrue(!manager.isStateAction("simple3-command"));

        assertTrue(manager.isStateAction("toggle-command"));
        assertTrue(manager.isStateAction("left-command"));
        assertTrue(manager.isStateAction("right-command"));
        assertTrue(manager.isStateAction("center-command"));

        assertTrue(!manager.isStateAction("composite-command"));

        assertTrue(!manager.isStateAction("namefinder-command"));
        assertTrue(!manager.isStateAction("server-command"));
    }


    /**
     * A test which registers all the actions with a controller,
     * invokes the actions to see if the registration was correct.
     */
    @Test
    public void testRegisterMethod() {
        Controller controller = new Controller();

        // Register the action on the controller.
        Iterator<?> iter = manager.getActionIDs().iterator();
        while (iter.hasNext()) {
            manager.registerCallback(iter.next(), controller, "action");
        }

        // Invoke all the actions.
        Action action;

        // dummy ItemSelectable used for forging ItemEvents.
        java.awt.ItemSelectable dummy = new JButton("Dummy");

        iter = manager.getActionIDs().iterator();
        while (iter.hasNext()) {
            controller.reset();

            Object id = iter.next();

            action = manager.getAction(id);
            if (manager.isBoundAction(id)) {
                if (manager.isStateAction(id)) {
                    // Use reflection to fake the ItemEvent.
                    ItemEvent evt = new ItemEvent(dummy, 666, "test",
                                                  ItemEvent.SELECTED);
                    Statement statement = new Statement(action,
                                                        "itemStateChanged",
                                                        new Object[] { evt });
                    try {
                        statement.execute();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    assertTrue(controller.isInvoked());
                } else {
                    // Simple command action.
                    action.actionPerformed(new ActionEvent(action, 666, "test"));
                    assertTrue("ERROR: " + manager.getBoundAction(id).toString(),
                               controller.isInvoked());
                }


            }
        }
    }

    /**
     * Test the composite action. Two simple commands have registered methods.
     * these methods should be executed in the composite action invokation.
     */
    @Test
    public void testCompositeAction() {
        Controller controller = new Controller();

        manager.registerCallback("simple-command", controller, "doNew");
        manager.registerCallback("simple2-command", controller, "doSave");

        Action action = manager.getAction("composite-command");
        action.actionPerformed(new ActionEvent(action, 666, "test"));

        assertTrue("ERROR: Controller was not invoked", controller.isInvoked());
        assertTrue("ERROR: Controller should have been invoked twice",
                   controller.getNumInvoked() == 2);
    }

    /**
     * Test the server action. The server action should send an http post.
     * with the params and not throw an exception.
     * 
     * TODO: It's difficult to test the server action in a firewall/non-controlled
     * network environment. Enable this test when working specifically with ServerActions.
    @Test
    public void testServerAction() {
        ServerAction action = manager.getServerAction("namefinder-command");
        try {
            // Determine if we are behind a firewall. Set
            // set the web proxy if we are
            URL url = new URL(action.getURL());
            URLConnection uc = url.openConnection();
            uc.connect();
        } catch (Exception ex) {
            // Set proxy since we are behind the firewall.
            System.setProperty("http.proxyHost", "scaweb1.sfbay");
            System.setProperty("http.proxyPort", "8080");
        }
        action.actionPerformed(new ActionEvent(action, 666, "test"));
    }
    */

    @Test
    public void testEnabled() {
        boolean[] values = new boolean[] { true, false, true, true, false, false };

        Iterator<?> iter;
        for (int i = 0; i < values.length; i++) {

            // Test for actions enabled by disabling actions.
            iter = manager.getActionIDs().iterator();
            while (iter.hasNext()) {
                manager.setEnabled(iter.next(), values[i]);
            }

            iter = manager.getActionIDs().iterator();
            while (iter.hasNext()) {
                assertTrue(manager.isEnabled(iter.next()) == values[i]);
            }
        }
    }


    @Test
    public void testSelected() {
        boolean[] values = new boolean[] { true, false, true, true, false, false };

        Iterator<?> iter;
        for (int i = 0; i < values.length; i++) {

            // Test for actions enabled by disabling actions.
            iter = manager.getActionIDs().iterator();
            while (iter.hasNext()) {
                manager.setSelected(iter.next(), values[i]);
            }

            iter = manager.getActionIDs().iterator();
            while (iter.hasNext()) {
                Object a = iter.next();
                if (manager.isStateAction(a)) {
                    assertTrue("Action: " + a + " selected state not " + values[i],
                               manager.isSelected(a) == values[i]);
                } else {
                    // Non StateActions will always return false.
                    assertFalse(manager.isSelected(a));
                }
            }
        }
    }


    /**
     * A simple controller callback for ActionManager registration test.
     */
    public class Controller {
        private boolean invoked = false;
        private int numInvoked = 0;

        public void action() {
            invoked = true;
            numInvoked++;
        }

        public void doNew() {
            action();
        }

        public void doSave() {
            action();
        }

        public void action(boolean state) {
            action();
        }

        public void reset() {
            invoked = false;
            numInvoked = 0;
        }

        public int getNumInvoked() {
            return numInvoked;
        }

        public boolean isInvoked() {
            return invoked;
        }
    }
}
