/*
 * $Id: ServerActionTest.java 3974 2011-03-21 14:35:05Z kschaefe $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.action;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ServerActionTest extends TestCase {

    /**
     * Issue #206-swingx: NPE in addHeader.
     *
     */
    @Test
    public void testNPEAddHeader() {
       ServerAction action = new ServerAction();
       action.addHeader("key", "value");
    }
}
