/*
 * $Id: JXComboBoxVisualCheck.java 3876 2010-11-03 10:53:27Z kleopatra $
 *
 * Copyright 2010 Sun Microsystems, Inc., 4150 Network Circle,
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
 */
package org.jdesktop.swingx;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.decorator.PainterHighlighter;
import org.jdesktop.swingx.painter.ImagePainter;
import org.jdesktop.swingx.test.XTestUtils;

/**
 *
 * @author kschaefer
 */
public class JXComboBoxIssues extends InteractiveTestCase {
    
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(JXComboBoxIssues.class.getName());
    
    private ComboBoxModel model;
    
    public static void main(String[] args) {
        JXComboBoxIssues test = new JXComboBoxIssues();
        
        try {
          test.runInteractiveTests();
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }


    
    
    public void interactiveHighlightSelectedItemNotInList() {
        final JXComboBox combo = new JXComboBox(createComboBoxModel());
        combo.getModel().setSelectedItem("not-in-list");
        PainterHighlighter hl = new PainterHighlighter(new ImagePainter(XTestUtils.loadDefaultImage()));
        combo.addHighlighter(hl);
        JComponent panel = new JXPanel();
        panel.add(new JButton("something to focus"));
        panel.add(combo);
        panel.add(new JComboBox(combo.getModel()));
        showInFrame(panel, "Painter");
    }
    
    public void interactiveComboBoxHighlighterNotEditable() {
        final JXComboBox combo = new JXComboBox(createComboBoxModel());
        combo.addHighlighter(HighlighterFactory.createSimpleStriping(HighlighterFactory.LINE_PRINTER));
        
        JComponent panel = new JXPanel();
        panel.add(new JButton("something to focus"));
        panel.add(combo);
        panel.add(new JComboBox(combo.getModel()));
        JXFrame frame = wrapInFrame(panel, "Highlighter - not editable");
        Action action = new AbstractAction("toggle useHighlighterOnCurrent") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                combo.setUseHighlightersForCurrentValue(!combo.isUseHighlightersForCurrentValue());
            }
        };
        addAction(frame, action);
        addStatusMessage(frame, "incorrect not-editable xcombo appearance");
        show(frame);
    }
    
    public void interactiveComboBoxHighlighterEditable() {
        JXComboBox combo = new JXComboBox(createComboBoxModel());
        combo.setEditable(true);
        combo.addHighlighter(HighlighterFactory.createSimpleStriping(HighlighterFactory.LINE_PRINTER));

        JComponent panel = new JXPanel();
        panel.add(new JButton("something to focus"));
        panel.add(combo);
        JComboBox plain = new JComboBox(createComboBoxModel());
        plain.setEditable(true);
        panel.add(plain);
        JXFrame frame = showInFrame(panel, "Highlighter - editable");
        addStatusMessage(frame, "editable xcombo appearance looks okay in Win/Nimbus");
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setUp() {
        model = createComboBoxModel();
    }
    
    private DefaultComboBoxModel createComboBoxModel() {
        return new DefaultComboBoxModel(new JComboBox().getActionMap().allKeys());
    }
    
    public void testDummy() { }
}
