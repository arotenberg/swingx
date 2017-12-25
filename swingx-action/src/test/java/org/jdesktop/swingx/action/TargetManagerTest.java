/*
 * $Id: TargetManagerTest.java 3974 2011-03-21 14:35:05Z kschaefe $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.action;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests registration and command execution of the target manager.
 *
 * @author Mark Davidson
 */
@RunWith(JUnit4.class)
public class TargetManagerTest extends TestCase {

    @Before
    public void setUpJ4() throws Exception {
        setUp();
    }
    
    @After
    public void tearDownJ4() throws Exception {
        tearDown();
    }
    
    @Override
    protected void tearDown() {
        TargetManager manager = TargetManager.getInstance();
        manager.reset();
    }

    /**
     * By default, there are no targets.
     */
    @Test
    public void testGetTargets() {
        TargetManager manager = TargetManager.getInstance();

        assertNull(manager.getTarget());
        Targetable[] targets = manager.getTargets();

        assertNotNull(targets);
        assertTrue(targets.length == 0);
    }

    /**
     * Test Target registration methods.
     */
    @Test
    public void testAddRemoveTargets() {
        FooTarget foo = new FooTarget();
        BarTarget bar = new BarTarget();

        TargetManager manager = TargetManager.getInstance();

        // append targets
        manager.addTarget(foo);
        manager.addTarget(bar);

        Targetable[] targets = manager.getTargets();
        assertNotNull(targets);
        assertTrue(targets.length == 2);
        // by default, targets are appended to the list.
        assertTrue(targets[0] == foo);
        assertTrue(targets[1] == bar);

        // Test removal
        manager.removeTarget(foo);
        manager.removeTarget(bar);

        targets = manager.getTargets();
        assertNotNull(targets);
        assertTrue(targets.length == 0);

        // add targets, prepend bar
        manager.addTarget(foo);
        manager.addTarget(bar, true);

        targets = manager.getTargets();
        assertNotNull(targets);
        assertTrue(targets.length == 2);
        assertTrue(targets[0] == bar);
        assertTrue(targets[1] == foo);
    }

    /**
     * Tests doCommand for both explicit targets and the targets in the list.
     */
    @Test
    public void testDoCommand() {
        FooTarget foo = new FooTarget();
        BarTarget bar = new BarTarget();
        TargetManager manager = TargetManager.getInstance();

        // set explicit targets
        manager.setTarget(foo);
        assertTrue(manager.doCommand("foo", null));
        assertFalse(manager.doCommand("bar", null));

        manager.setTarget(bar);
        assertTrue(manager.doCommand("bar", null));
        assertFalse(manager.doCommand("foo", null));

        // Add both of the targets all commands are valid
        manager.setTarget(null);
        manager.addTarget(foo);
        manager.addTarget(bar);

        assertTrue(manager.doCommand("bar", null));
        assertTrue(manager.doCommand("foo", null));
    }

    //
    // Some examples of Targets
    //

    private class FooTarget implements Targetable {

        public String FOO_ACTION = "foo";

        public boolean doCommand(Object command, Object value) {
            return hasCommand(command);
        }

        public boolean hasCommand(Object command) {
            if (command.equals(FOO_ACTION)) {
                return true;
            }
            return false;
        }

        public Object[] getCommands() {
            return new Object[] { FOO_ACTION };
        }
    }

    private class BarTarget implements Targetable {
        public String BAR_ACTION = "bar";

        public boolean doCommand(Object command, Object value) {
            return hasCommand(command);
        }

        public boolean hasCommand(Object command) {
            if (command.equals(BAR_ACTION)) {
                return true;
            }
            return false;
        }

        public Object[] getCommands() {
            return new Object[] { BAR_ACTION };
        }
    }
}
