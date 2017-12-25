/*
 * $Id: JXLabelVisualCheck.java 3965 2011-03-17 19:14:56Z kschaefe $
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

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JComponent;

import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.painter.AbstractPainter;
import org.jdesktop.swingx.painter.AlphaPainter;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.painter.ShapePainter;

import com.jhlabs.image.BlurFilter;

/**
 * Base test class for JXLabel related code and issues.
 * 
 * @author rah003
 */
@SuppressWarnings("nls")
public class JXLabelVisualCheck extends InteractiveTestCase {
    
    static Logger log = Logger.getAnonymousLogger();
    
    public static void main(String[] args) {
        JXLabelVisualCheck test = new JXLabelVisualCheck();
        try {
            test.runInteractiveTests("interactiveUnderlinedFontWithWrapping");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Example of how-to apply filters to the label's foreground.
     */
    @SuppressWarnings("unchecked")
    public void interactiveFancyFilter() {
        JXLabel label = new JXLabel("that's the real text");
        label.setFont(new Font("SansSerif", Font.BOLD, 80));
        AbstractPainter<?> fg = new MattePainter(Color.RED);
        fg.setFilters(new BlurFilter());
        label.setForegroundPainter(fg);
        JXFrame frame = wrapInFrame(label, "fancy filter");
        show(frame,400, 400);
    }
    
    /**
     * Issue #??-swingx: default foreground painter not guaranteed after change.
     *
     * JXLabel restore default foreground painter.
     * Sequence: 
     *   compose the default with a transparent overlay
     *   try to reset to default
     *   try to compose the overlay again.
     */
    public void interactiveRestoreDefaultForegroundPainter() {
        JComponent box = Box.createVerticalBox();
        final JXLabel foreground = new JXLabel(
                "setup: compound - default and overlay ");
        ShapePainter shapePainter = new ShapePainter();
        final AlphaPainter<?> alpha = new AlphaPainter<Object>();
        alpha.setAlpha(0.2f);
        alpha.setPainters(shapePainter);
        CompoundPainter<?> compound = new CompoundPainter<Object>(foreground
                .getForegroundPainter(), alpha);
        foreground.setForegroundPainter(compound);
        box.add(foreground);
        Action action = new AbstractActionExt("reset default foreground") {
            boolean reset;
            public void actionPerformed(ActionEvent e) {
                if (reset) {
                    CompoundPainter<?> painter = new CompoundPainter<Object>(alpha, foreground.getForegroundPainter());
                    foreground.setForegroundPainter(painter);
                } else {
                  // try to reset to default
                    foreground.setForegroundPainter(null);
                }
                reset = !reset;

            }

        };
        JXFrame frame = wrapInFrame(box, "foreground painters");
        addAction(frame, action);
        frame.pack();
        frame.setVisible(true);
    }
    
    /**
     * Issue #1330-swingx: underlined font does not retain underline during wrapping.
     */
    public void interactiveUnderlinedFontWithWrapping() {
        final JXLabel label = new JXLabel("A really long sentence to display the text wrapping features of JXLabel.");
        // when lineWrap is true, can't see underline effects 
        // when lineWrap is false, underline is ok
        label.setLineWrap(true);
        label.setBounds(31, 48, 91, 18);
        // set font underline
        Map<TextAttribute, Integer> map = new HashMap<TextAttribute, Integer>();
        map.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        label.setFont(label.getFont().deriveFont(map));
        
        final JXFrame frame = wrapInFrame(label, "Underlined Font with wrapping");
        addAction(frame, new AbstractAction("Toggle wrapping") {
            @Override
            public void actionPerformed(ActionEvent e) {
                label.setLineWrap(!label.isLineWrap());
                frame.repaint();
            }
        });
        frame.pack();
        frame.setVisible(true);
    }
    
    /**
     * Issue #978: Setting background color has no effect
     */
    public void interactiveBackgroundColorSetting() {
        final JXLabel label = new JXLabel("A simple label.");
        label.setOpaque(true);
        label.setBackground(Color.CYAN);
        
        showInFrame(label, "Background Color Check");
    }
    
    /**
     * Ensure background painter is always painted.
     */
    public void interactiveBackgroundPainter() {
        JComponent box = Box.createVerticalBox();
        ShapePainter shapePainter = new ShapePainter();
        JXLabel opaqueTrue = new JXLabel("setup: backgroundPainter, opaque = true");
        opaqueTrue.setOpaque(true);
        opaqueTrue.setBackgroundPainter(shapePainter);
        box.add(opaqueTrue);
        JXLabel opaqueFalse = new JXLabel("setup: backgroundPainter, opaque = false");
        opaqueFalse.setOpaque(false);
        opaqueFalse.setBackgroundPainter(shapePainter);
        box.add(opaqueFalse);
        JXLabel opaqueUnchanged = new JXLabel("setup: backgroundPainter, opaque = unchanged");
        opaqueUnchanged.setBackgroundPainter(shapePainter);
        box.add(opaqueUnchanged);
        showInFrame(box, "background painters");
    }
}
