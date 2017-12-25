/*
 * $Id: JXHyperlinkTest.java 3484 2009-09-03 15:55:34Z kleopatra $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.util.logging.Logger;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.UIResource;

import junit.framework.TestCase;

import org.jdesktop.swingx.hyperlink.AbstractHyperlinkAction;
import org.jdesktop.swingx.hyperlink.HyperlinkAction;
import org.jdesktop.swingx.plaf.basic.BasicHyperlinkUI.BasicHyperlinkListener;
import org.jdesktop.test.PropertyChangeReport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test of JXHyperlink. Raw usage and as hyperlinkRenderer.
 * <p>
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class JXHyperlinkTest extends TestCase {
    @SuppressWarnings("all")
    private static final Logger LOG = Logger.getLogger(JXHyperlinkTest.class
            .getName());
    
    private PropertyChangeReport report;

    @Before
    public void setUpJ4() throws Exception {
        setUp();
    }
    
    @After
    public void tearDownJ4() throws Exception {
        tearDown();
    }
    
    @Test
    public void testSetURI() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run ui test - headless environment");
            return;
        }
        JXHyperlink hyperlink = new JXHyperlink();
        hyperlink.setURI(null);
        assertNotNull(hyperlink.getAction());
        assertTrue(hyperlink.getAction() instanceof HyperlinkAction);
    }
    
    @Test
    public void testBorderUIResource() {
        JXHyperlink hyperlink = new JXHyperlink();
        if (!(hyperlink.getBorder() instanceof UIResource)) {
            LOG.info("not running test, LAF doesn't install hyperlink border");
            return;
        }
        Border border = new EmptyBorder(1, 2, 3, 4);
        hyperlink.setBorder(border);
        hyperlink.updateUI();
        assertSame("border untouched in updateUI ", border, hyperlink.getBorder());
    }
    
    @Test
    public void testHyperlinkButtonListener() {
        JXHyperlink hyperlink = new JXHyperlink();
        MouseListener[] listeners = hyperlink.getMouseListeners();
        for (MouseListener mouseListener : listeners) {
            if(mouseListener instanceof BasicHyperlinkListener) return;
        }
        fail("hyperlink must have installed a BasicHyperlinkListener");
    }
    
    /**
     * test control of the clicked property.
     * 
     * Default behaviour
     * 
     *
     */
    @Test
    public void testAutoClicked() {
       // no action 
       JXHyperlink hyperlink = new JXHyperlink();
       hyperlink.doClick();
       assertTrue("hyperlink autoClicks if it has no action", hyperlink.isClicked());
       
       AbstractHyperlinkAction<Object> emptyAction = createEmptyLinkAction();
       JXHyperlink hyperlink2 = new JXHyperlink(emptyAction);
       hyperlink2.doClick();
       assertFalse(emptyAction.isVisited());
       assertFalse("hyperlink does nothing if has action", hyperlink2.isClicked());
       
       AbstractHyperlinkAction<?> emptyAction3 = createEmptyLinkAction();
       JXHyperlink hyperlink3 = new JXHyperlink(emptyAction3);
       hyperlink3.setOverrulesActionOnClick(true);
       hyperlink3.doClick();
       assertFalse(emptyAction.isVisited());
       assertTrue("hyperlink overrules action", hyperlink3.isClicked());
       
    }
    
    @Test
    public void testOverrulesActionOnClick() {
        JXHyperlink hyperlink = new JXHyperlink();
        assertFalse(hyperlink.getOverrulesActionOnClick());
        hyperlink.addPropertyChangeListener(report);
        hyperlink.setOverrulesActionOnClick(true);
        assertTrue(hyperlink.getOverrulesActionOnClick()); 
        assertEquals(1, report.getEventCount("overrulesActionOnClick"));
    }
    /**
     * sanity (duplicate of LinkActionTest method) to
     * guarantee that hyperlink is updated as expected.
     *
     */
    @Test
    public void testLinkActionSetTarget() {
        AbstractHyperlinkAction<Object> linkAction = createEmptyLinkAction();
        linkAction.setVisited(true);
        JXHyperlink hyperlink = new JXHyperlink(linkAction);
        Object target = new Object();
        linkAction.setTarget(target);
        assertEquals(linkAction.getName(), hyperlink.getText());
        assertFalse(hyperlink.isClicked());
    }
    /**
     * test that hyperlink.setClicked doesn't change action.isVisited();
     *
     */
    @Test
    public void testSetClickedActionUnchanged() {
        AbstractHyperlinkAction<Object> linkAction = createEmptyLinkAction();
        linkAction.setVisited(true);
        JXHyperlink hyperlink = new JXHyperlink(linkAction);
        // sanity assert..
        assertTrue(hyperlink.isClicked());
        hyperlink.setClicked(false);
        // action state must be unchanged;
        assertTrue(linkAction.isVisited());
        
    }
    /**
     * test hyperlink's clicked property.
     *
     */
    @Test
    public void testClicked() {
        JXHyperlink hyperlink = new JXHyperlink();
        boolean isClicked = hyperlink.isClicked();
        assertFalse(isClicked);
        hyperlink.addPropertyChangeListener(report);
        hyperlink.setClicked(!isClicked);
        assertEquals(1, report.getEventCount("clicked"));
    }
    
    /**
     * JXHyperlink must handle null action gracefully.
     * 
     * Was NPE in configureFromAction
     *
     */
    @Test
    public void testInitNullAction() {
        JXHyperlink hyperlink = new JXHyperlink();
        assertNull(hyperlink.getAction());
        
    }

    /**
     * JXHyperlink must handle null action gracefully.
     * 
     * Was NPE in configureFromAction
     *
     */
    @Test
    public void testSetNullAction() {
        AbstractHyperlinkAction<?> action = createEmptyLinkAction();
        JXHyperlink hyperlink = new JXHyperlink(action);
        assertEquals("hyperlink action must be equal to linkAction", action, hyperlink.getAction());
        hyperlink.setAction(null);
        assertNull(hyperlink.getAction());
    }
    /**
     * JXHyperlink must handle null action gracefully.
     * 
     * Was NPE in configureFromAction
     *
     */
    @Test
    public void testSetAction() {
        JXHyperlink hyperlink = new JXHyperlink();
        AbstractHyperlinkAction<?> action = createEmptyLinkAction();
        hyperlink.setAction(action);
        assertEquals("hyperlink action must be equal to linkAction", 
                action, hyperlink.getAction());
    }

    /**
     * test that JXHyperlink visited state keeps synched 
     * to LinkAction.
     *
     */
    @Test
    public void testListeningVisited() {
       AbstractHyperlinkAction<Object> linkAction = createEmptyLinkAction();
       JXHyperlink hyperlink = new JXHyperlink(linkAction);
       // sanity: both are expected to be false
       assertEquals(linkAction.isVisited(), hyperlink.isClicked());
       assertFalse(linkAction.isVisited());
       linkAction.setVisited(!linkAction.isVisited());
       assertEquals(linkAction.isVisited(), hyperlink.isClicked());
    }
    
    /**
     * test initial visited state in JXHyperlink is synched to
     * linkAction given in constructor.
     * 
     * There was the usual "init" problem with the constructor.
     * Solved by chaining.
     * 
     */
    @Test
    public void testInitialVisitedSynched() {
        AbstractHyperlinkAction<Object> linkAction = createEmptyLinkAction();
       linkAction.setVisited(true);
       // sanity: linkAction is changed to true
       assertTrue(linkAction.isVisited());
       JXHyperlink hyperlink = new JXHyperlink(linkAction);
       assertEquals(linkAction.isVisited(), hyperlink.isClicked());
    }


    public static class Player {
        String name;
        int score;
        public Player(String name, int score) {
            this.name = name;
            this.score = score;
        }
        @Override
        public String toString() {
            return name + " has score: " + score;
        }
    }

    protected AbstractHyperlinkAction<Object> createEmptyLinkAction() {
        AbstractHyperlinkAction<Object> linkAction = new AbstractHyperlinkAction<Object>(null) {

            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                
            }
               
       };
        return linkAction;
    }

    protected AbstractHyperlinkAction<?> createEmptyLinkAction(String name) {
        AbstractHyperlinkAction<?> linkAction = createEmptyLinkAction();
        linkAction.setName(name);
        return linkAction;
    }
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        report = new PropertyChangeReport();
    }

}
