/*
 * $Id: MessageTest.java 2977 2008-07-07 03:17:53Z kschaefe $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.event;

import java.util.logging.Level;

import junit.framework.TestCase;

/**
 * implementation of the MessageSourceSupport class.
 * A JUnit test case for the Message event set. Mostly this tests the
 */
public class MessageTest extends TestCase {

    private MListener[] listeners;
    private PListener[] plisteners;

    private static final int COUNT = 10;

    protected void setUp() {
	listeners = new MListener[COUNT];
	plisteners = new PListener[COUNT];

	for (int i = 0; i < COUNT; i++) {
	    listeners[i] = new MListener();
	    plisteners[i] = new PListener();
	}
    }

    protected void tearDown() {
	for (int i = 0; i < COUNT; i++) {
	    listeners[i] = null;
	    plisteners[i] = null;
	}
	listeners = null;
	plisteners = null;
    }

    public void testMessageRegistration() {
	MessageSource source = new MessageSourceSupport(this);
	MessageListener[] ls = source.getMessageListeners();

	assertNotNull(ls);
	assertEquals("Number of listeners not 0", 0, ls.length);

	// test add and remove methods.

	// Add listeners. Ensure that they have been added
	for (int i = 0; i < listeners.length; i++) {
	    source.addMessageListener(listeners[i]);
	}

	ls = source.getMessageListeners();
	assertEquals(listeners.length, ls.length);

	// Remove all listenters.
	for (int i = 0; i < listeners.length; i++) {
	    source.removeMessageListener(listeners[i]);
	}

	ls = source.getMessageListeners();
	assertEquals(0, ls.length);

	// test to ensure that null adds and removes have no effect.
	for (int i = 0; i < COUNT; i++) {
	    source.addMessageListener(null);
	    ls = source.getMessageListeners();
	    assertEquals(0, ls.length);
	}

	// repopulate the message source.
	for (int i = 0; i < listeners.length; i++) {
	    source.addMessageListener(listeners[i]);
	}

	for (int i = 0; i < COUNT; i++) {
	    source.removeMessageListener(null);
	    ls = source.getMessageListeners();
	    assertEquals(listeners.length, ls.length);
	}
    }

    /**
     * Test to ensure that messages are dispatched correctly.
     */
    public void testMessageDispatch() {
	MessageSourceSupport source = new MessageSourceSupport(this);
	for (int i = 0; i < listeners.length; i++) {
	    source.addMessageListener(listeners[i]);
	}

	// Test data.
	String message = "This is a simple message";
	Level level = Level.INFO;
	long when = System.currentTimeMillis();
	MessageEvent evt = null;

	// Simple test to ensure that all listeners get the same event.
	source.fireMessage(message);
	for (int i = 0; i < listeners.length; i++) {
	    evt = listeners[i].getLastEvent();
	    assertNotNull(evt);
	    assertEquals(message, evt.getMessage());
	}

	source.fireMessage(message, level);
	for (int i = 0; i < listeners.length; i++) {
	    evt = listeners[i].getLastEvent();
	    assertNotNull(evt);
	    assertEquals(message, evt.getMessage());
	    assertEquals(level, evt.getLevel());
	}

	source.fireMessage(message, level, when);
	for (int i = 0; i < listeners.length; i++) {
	    evt = listeners[i].getLastEvent();
	    assertNotNull(evt);
	    assertEquals(message, evt.getMessage());
	    assertEquals(level, evt.getLevel());
	    assertEquals(when, evt.getWhen());
	}

	evt = new MessageEvent(this, message, level, when);
	source.fireMessage(evt);
	for (int i = 0; i < listeners.length; i++) {
	    assertEquals(evt, listeners[i].getLastEvent());
	}
    }

    /**
     * Tests the exception passing mechanism.
     */
    public void testExceptions() {
	MessageSourceSupport source = new MessageSourceSupport(this);
	for (int i = 0; i < listeners.length; i++) {
	    source.addMessageListener(listeners[i]);
	}

	Exception ex = new Exception("This is a message");
	MessageEvent evt = null;

	source.fireException(ex);
	for (int i = 0; i < listeners.length; i++) {
	    evt = listeners[i].getLastEvent();
	    assertNotNull(evt);
	    // The event message should be the exception message.
	    assertEquals(ex.getMessage(), evt.getMessage());
	    // By default, exceptions should be Level.SEVERE
	    assertEquals(Level.SEVERE, evt.getLevel());
	}
	
	
    }

    /**
     * Test to ensure that progress messages are correctly propagated.
     */
    public void testDeterminteProgress() {
	MessageSourceSupport source = new MessageSourceSupport(this);
	ProgressEvent evt = null;

	// Add listeners.
	for (int i = 0; i < plisteners.length; i++) {
	    source.addProgressListener(plisteners[i]);
	}

	// This is a determinate progress operation
	source.fireProgressStarted(0, 100);
	for (int i = 0; i < plisteners.length; i++) {
	    evt = plisteners[i].getLastEvent();
	    assertFalse(evt.isIndeterminate());
	    assertTrue(plisteners[i].begin);
	    assertFalse(plisteners[i].end);
	}

	source.fireProgressIncremented(50);
	for (int i = 0; i < plisteners.length; i++) {
	    evt = plisteners[i].getLastEvent();
	    assertFalse(evt.isIndeterminate());
	    assertFalse(plisteners[i].begin);
	    assertFalse(plisteners[i].end);
	}

	source.fireProgressEnded();
	for (int i = 0; i < plisteners.length; i++) {
	    evt = plisteners[i].getLastEvent();
	    assertTrue(evt.isIndeterminate());
	    assertFalse(plisteners[i].begin);
	    assertTrue(plisteners[i].end);
	}
    }

    public void testIndeterminteProgress() {
	MessageSourceSupport source = new MessageSourceSupport(this);
	ProgressEvent evt = null;

	// Add listeners.
	for (int i = 0; i < plisteners.length; i++) {
	    source.addProgressListener(plisteners[i]);
	}

	// This is a determinate progress operation
	source.fireProgressStarted(0, 0);
	for (int i = 0; i < plisteners.length; i++) {
	    evt = plisteners[i].getLastEvent();
	    assertTrue(evt.isIndeterminate());
	    assertTrue(plisteners[i].begin);
	    assertFalse(plisteners[i].end);
	}
    }

    /**
     * A listener of MessageEvents.
     */
    private class MListener
        implements MessageListener {

        private MessageEvent evt;

        public void message(MessageEvent evt) {
            this.evt = evt;
        }

        public void exception(MessageEvent evt) {
            this.evt = evt;
        }

        public MessageEvent getLastEvent() {
            return evt;
        }
    }

    private class PListener
        implements ProgressListener {

        private ProgressEvent evt;

        public boolean begin = false;
        public boolean end = false;

        public void progressStarted(ProgressEvent e) {
            this.evt = e;
            begin = true;
            end = false;
        }

        public void progressIncremented(ProgressEvent e) {
            this.evt = e;
            begin = false;
            end = false;
        }

        public void progressEnded(ProgressEvent e) {
            this.evt = e;
            begin = false;
            end = true;
        }

        public void exception(ProgressEvent e) {
            /**@todo what should we do here? */
        }

        public ProgressEvent getLastEvent() {
            return evt;
        }
    }
}
