/*
 * $Id: SerializableIssues.java 4017 2011-05-10 21:00:48Z kschaefe $
 *
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package org.jdesktop.swingx;

import javax.swing.JPanel;

import org.jdesktop.test.SerializableSupport;

/**
 * Test to exposed known issues of serializable.
 * 
 * @author Jeanette Winzenburg
 */
public class SerializableIssues extends InteractiveTestCase {


    /**
     * Issue #423-swingx: all descendants of JComponent must be 
     * serializable. <p>
     * 
     * First blow: RolloverController
     * 
     */
    public void testTreeTable() {
        JXTreeTable component = new JXTreeTable();
        try {
            SerializableSupport.serialize(component);
        } catch (Exception e) {
            fail("not serializable " + e);
        } 
    }


    /**
     * Issue #423-swingx: all descendants of JComponent must be 
     * serializable. <p>
     * 
     * First blow: TreeAdapter
     * 
     */
    public void testTree() {
        JXTree component = new JXTree();
        try {
            SerializableSupport.serialize(component);
        } catch (Exception e) {
            fail("not serializable " + e);
        } 
    }


    /**
     * Issue #423-swingx: all descendants of JComponent must be 
     * serializable. <p>
     * 
     * First blow: PainterUIResource
     * 
     */
    public void testTitledPanel() {
        JXTitledPanel component = new JXTitledPanel();
        try {
            SerializableSupport.serialize(component);
        } catch (Exception e) {
            fail("not serializable " + e);
        } 
    }


    /**
     * Issue #423-swingx: all descendants of JComponent must be 
     * serializable. <p>
     * 
     * First blow: ui delegate
     * 
     */
    public void testTipOfTheDay() {
        JXTipOfTheDay component = new JXTipOfTheDay();
        try {
            SerializableSupport.serialize(component);
        } catch (Exception e) {
            fail("not serializable " + e);
        } 
    }


    /**
     * Issue #423-swingx: all descendants of JComponent must be 
     * serializable. <p>
     * 
     * First blow: VerticalLayout
     * 
     */
    public void testTaskPaneContainer() {
        JXTaskPaneContainer component = new JXTaskPaneContainer();
        try {
            SerializableSupport.serialize(component);
        } catch (Exception e) {
            fail("not serializable " + e);
        } 
    }

    
    /**
     * Issue #423-swingx: all descendants of JComponent must be 
     * serializable. <p>
     * 
     * First blow: VerticalLayout
     * 
     */
    public void testTaskPane() {
        JXTaskPane component = new JXTaskPane();
        try {
            SerializableSupport.serialize(component);
        } catch (Exception e) {
            fail("not serializable " + e);
        } 
    }

    /**
     * Issue #423-swingx: all descendants of JComponent must be 
     * serializable. <p>
     * 
     * First blow: rolloverController
     * 
     */
    public void testTable() {
        JXTable component = new JXTable();
        try {
            SerializableSupport.serialize(component);
        } catch (Exception e) {
            fail("not serializable " + e);
        } 
    }

    /**
     * Issue #423-swingx: all descendants of JComponent must be 
     * serializable. <p>
     * 
     * First blow: ui-delegate
     * 
     */
    public void testStatusBar() {
        JXStatusBar component = new JXStatusBar();
        try {
            SerializableSupport.serialize(component);
        } catch (Exception e) {
            fail("not serializable " + e);
        } 
    }

    /**
     * Issue #423-swingx: all descendants of JComponent must be 
     * serializable. <p>
     * 
     * First blow: EventHandler
     * 
     */
    public void testSearchPanel() {
        JXSearchPanel component = new JXSearchPanel();
        try {
            SerializableSupport.serialize(component);
        } catch (Exception e) {
            fail("not serializable " + e);
        } 
    }

    
    /**
     * Issue #423-swingx: all descendants of JComponent must be 
     * serializable. <p>
     * 
     * First blow: DefaultMultiThumbModel.
     * 
     */
    @SuppressWarnings("unchecked")
    public void testMultiThumbSlider() {
        JXMultiThumbSlider component = new JXMultiThumbSlider();
        try {
            SerializableSupport.serialize(component);
        } catch (Exception e) {
            fail("not serializable " + e);
        } 
    }

    /**
     * Issue #423-swingx: all descendants of JComponent must be 
     * serializable.<p>
     * 
     * First blow: MultiSplitPaneLayout.
     * 
     */
    public void testMultiSplitPane() {
        JXMultiSplitPane component = new JXMultiSplitPane();
        try {
            SerializableSupport.serialize(component);
        } catch (Exception e) {
            fail("not serializable " + e);
        } 
    }

    /**
     * Issue #423-swingx: all descendants of JComponent must be 
     * serializable.<p>
     * 
     * First blow: BufferedImage.
     * 
     */
    public void testLoginPanel() {
        JXLoginPane component = new JXLoginPane();
        try {
            SerializableSupport.serialize(component);
        } catch (Exception e) {
            fail("not serializable " + e);
        } 
    }

    /**
     * Issue #423-swingx: all descendants of JComponent must be 
     * serializable.<p>
     * 
     * First blow: DelegatingRenderer.
     * 
     */
    public void testList() {
        JXList component = new JXList();
        try {
            SerializableSupport.serialize(component);
        } catch (Exception e) {
            fail("not serializable " + e);
        } 
    }

    /**
     * Issue #423-swingx: all descendants of JComponent must be 
     * serializable.<p>
     * 
     * first blow: PainterUIResource
     */
    public void testHeader() {
        JXHeader component = new JXHeader();
        try {
            SerializableSupport.serialize(component);
        } catch (Exception e) {
            fail("not serializable " + e);
        } 
    }

    /**
     * Issue #423-swingx: all descendants of JComponent must be 
     * serializable.<p>
     * 
     * Frist blow: Rectangle inner class
     */
    public void testGraph() {
        JXGraph component = new JXGraph();
        try {
            SerializableSupport.serialize(component);
        } catch (Exception e) {
            fail("not serializable " + e);
        } 
    }

    /**
     * Issue #423-swingx: all descendants of JComponent must be 
     * serializable.<p>
     * 
     * First blow: BufferedImage
     */
    public void testGradientChooser() {
        JXGradientChooser component = new JXGradientChooser();
        try {
            SerializableSupport.serialize(component);
        } catch (Exception e) {
            fail("not serializable " + e);
        } 
    }


    /**
     * Issue #423-swingx: all descendants of JComponent must be 
     * serializable.<p>
     * 
     * First blow: inner class?.
     *
     */
    public void testFrame() {
        JXFrame component = new JXFrame();
        try {
            SerializableSupport.serialize(component);
        } catch (Exception e) {
            fail("not serializable " + e);
        } 
    }

    /**
     * Issue #423-swingx: all descendants of JComponent must be 
     * serializable.<p>
     * 
     * First blow: EventHandler
     */
    public void testFindPanel() {
        JXFindPanel component = new JXFindPanel();
        try {
            SerializableSupport.serialize(component);
        } catch (Exception e) {
            fail("not serializable " + e);
        } 
    }

    /**
     * Issue #423-swingx: all descendants of JComponent must be 
     * serializable.<p>
     * 
     * First blow: EventHandler
     */
    public void testFindBar() {
        JXFindBar component = new JXFindBar();
        try {
            SerializableSupport.serialize(component);
        } catch (Exception e) {
            fail("not serializable " + e);
        } 
    }

    /**
     * Issue #423-swingx: all descendants of JComponent must be 
     * serializable.<p>
     * 
     * First blow: ui-delegate 
     */
    public void testErrorPane() {
        JXErrorPane component = new JXErrorPane();
        try {
            SerializableSupport.serialize(component);
        } catch (Exception e) {
            fail("not serializable " + e);
        } 
    }

    /**
     * Issue #423-swingx: all descendants of JComponent must be 
     * serializable.<p>
     * 
     *
     */
    public void testEditorPane() {
        JXEditorPane component = new JXEditorPane();
        try {
            SerializableSupport.serialize(component);
        } catch (Exception e) {
            fail("not serializable " + e);
        } 
    }

    /**
     * Issue #423-swingx: all descendants of JComponent must be 
     * serializable.<p>
     * 
     *
     */
    public void testDialog() {
        JXDialog component = new JXDialog(new JPanel());
        try {
            SerializableSupport.serialize(component);
        } catch (Exception e) {
            fail("not serializable " + e);
        } 
    }

    
    /**
     * Issue #423-swingx: all descendants of JComponent must be 
     * serializable.<p>
     * 
     *
     */
    public void testDatePicker() {
        JXDatePicker component = new JXDatePicker();
        try {
            SerializableSupport.serialize(component);
        } catch (Exception e) {
            fail("not serializable " + e);
        } 
    }

    /**
     * Issue #423-swingx: all descendants of JComponent must be 
     * serializable.<p>
     * 
     *
     */
    public void testColorSelectionButton() {
        JXColorSelectionButton component = new JXColorSelectionButton();
        try {
            SerializableSupport.serialize(component);
        } catch (Exception e) {
            fail("not serializable " + e);
        } 
    }

    /**
     * Issue #423-swingx: all descendants of JComponent must be 
     * serializable.<p>
     * 
     *
     */
    public void testCollapsiblePane() {
        JXCollapsiblePane component = new JXCollapsiblePane();
        try {
            SerializableSupport.serialize(component);
        } catch (Exception e) {
            fail("not serializable " + e);
        } 
    }

}
