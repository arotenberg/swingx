/*
 * $Id: PainterIssues.java 3835 2010-10-08 03:45:30Z kschaefe $
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
package org.jdesktop.swingx.painter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.Border;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.action.AbstractActionExt;

/**
 * Test to exposed known issues of <code>Painter</code>s.
 * 
 * Ideally, there would be at least one failing test method per open
 * Issue in the issue tracker. Plus additional failing test methods for
 * not fully specified or not yet decided upon features/behavior.
 * 
 * 
 * @author Jeanette Winzenburg
 */
public class PainterIssues extends InteractiveTestCase {
    
    static Logger log = Logger.getAnonymousLogger();
    public static void main(String args[]) {
//      setSystemLF(true);
        PainterIssues test = new PainterIssues();
        try {
            test.runInteractiveTests();
//            test.runInteractiveTests(".*Label.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }

    // ------------------ visual tests
  
    
    /**
     * JXLabel default foreground painter - share between labels.
     * Probably illegal :-) 
     * IMHO not illegal and sometimes even desirable (Jan).
     * 
     */
    public void interactiveXLabelSharedDefaultForegroundPainter() {
        JComponent box = Box.createVerticalBox();
        final JXLabel foreground = new JXLabel(
                "setup: compound - default and overlay ");
        ShapePainter shapePainter = new ShapePainter();
        AlphaPainter<?> alpha = new AlphaPainter<Object>();
        alpha.setAlpha(0.2f);
        alpha.setPainters(shapePainter);
        CompoundPainter<?> compound = new CompoundPainter<Object>(alpha, foreground
                .getForegroundPainter());
        foreground.setForegroundPainter(compound);
        box.add(foreground);
        JXLabel shared = new JXLabel(
                "setup: shared compound of first label - this doesn't show up");
        shared.setForegroundPainter(compound);
        box.add(shared);
        showInFrame(box, "shared foreground painters");
    }
    /**
     * 
     * paint doc relieves impl from restoring graphics. Who
     * is responsible for cleanup?
     *
     */
    public void interactiveRestoreGraphics() {
        Border redLine = BorderFactory.createLineBorder(Color.RED, 3);
        final Painter<JComponent> permanentTranslate = new Painter<JComponent>() {

            public void paint(Graphics2D g, JComponent object, int width, int height) {
                g.translate(50, 0); 
            }
            
        };
        JLabel label = new JLabel() {

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D scratch = (Graphics2D) g.create();
                try {
                    permanentTranslate.paint(scratch, this, getWidth(), getHeight());
                    ui.paint(scratch, this);
                } finally {
                    scratch.dispose();
                }
            }
            
        };
        label.setText("Painter: translated Graphics implement Painter");
        label.setBorder(redLine);
        JLabel labelP = new JLabel("setup: painter with translate, no scratch") {

            /**
             * Illegal paintComponent - painter made permanent changes
             */
            @Override
            protected void paintComponent(Graphics g) {
                permanentTranslate.paint((Graphics2D) g, this, getWidth(), getHeight());
                super.paintComponent(g);
            }
            
        };
        labelP.setBorder(redLine);
        final AbstractPainter<JComponent> painterAP = new AbstractPainter<JComponent>() {

            @Override
            protected void doPaint(Graphics2D g, JComponent component, int width, int height) {
                g.translate(50, 0); 
            }
            
        };
        JLabel labelAP = new JLabel() {

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D scratch = (Graphics2D) g.create();
                try {
                    painterAP.paint(scratch, this, getWidth(), getHeight());
                    ui.paint(scratch, this);
                } finally {
                    scratch.dispose();
                }
            }
            
        };
        labelAP.setText("Painter: translated graphics subclass AbstractPainter");
        labelAP.setBorder(redLine);
        JComponent box = Box.createVerticalBox();
        box.add(label);
        box.add(labelAP);
        box.add(labelP);
        showInFrame(box, "unrestored graphics");
    }
    
    /**
     * Style.None - use case? Always invisible?
     */
    public void interactiveRenderingLabel() {
        JComponent box = Box.createVerticalBox();
        final JXLabel label = new JXLabel("setup: ShapePainter with fillstyle none");
        // fixed: NPE with null shape - but has default instead of null?
        final ShapePainter styleNone = new ShapePainter();
        styleNone.setStyle(ShapePainter.Style.NONE);
        label.setBackgroundPainter(styleNone);
        box.add(label);
        final JXLabel label2 = new JXLabel("setup: default ShapePainter");
        final ShapePainter painter = new ShapePainter();
        label2.setBackgroundPainter(painter);
        box.add(label2);
        Action action = new AbstractActionExt("toggle painter visible") {

            public void actionPerformed(ActionEvent e) {
                styleNone.setVisible(!styleNone.isVisible());
                painter.setVisible(!painter.isVisible());
                label.repaint();
                label2.repaint();
            }
            
        };
        JXFrame frame = wrapInFrame(box, "renderer label with shape painter - fillstyle none");
        addAction(frame, action);
        frame.pack();
        frame.setVisible(true);
    }
}
