/*
 * $Id: JXPanelVisualCheck.java 4289 2013-03-01 16:36:59Z kleopatra $
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


import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.painter.ImagePainter;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.test.XTestUtils;
import org.jdesktop.swingx.util.PaintUtils;

import com.sun.java.swing.Painter;

/**
 * Contains methods to visually test JXPanel.
 * 
 * @author Jeanette Winzenburg
 */
public class JXPanelVisualCheck extends InteractiveTestCase {

    public static void main(String args[]) {
        final JXPanelVisualCheck test = new JXPanelVisualCheck();
        setLAF("Nimbus");
//        UIManager.put("JXPanel.patch", Boolean.TRUE);
        try {

//             test.runInteractiveTests();
            test.runInteractiveTests("interactiveBackgroundAlphaToggleOpaque");
//            test.runInteractive("PanelPainter");
//             test.runInteractive("BackgroundAndAlphaCheck");
            // test.runInteractive("FrameArtefacts");

        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }

    /**
     * Issue #1515-swingx: painting artefacts in a JXFrame (which uses JXPanel
     * as rootPane)
     * 
     * If the upper frame contains a JXPanel with alpha, the lower background is
     * painted only below the area of the upper frame. All fine without alpha on
     * the upper. The exact outcome is erractic (sometimes half or even fully
     * painted). On/off EDT doesn't seem to make a difference.
     */
    public void interactiveXPanelInFrameArtefacts() {
        JPanel panel = new JPanel();
        Color red = Color.RED;
        panel.setBackground(red);
        JXFrame frame = wrapInFrame(panel,
                "plain panel added to contentPane of a xframe");
        show(frame, 400, 400);

        JXPanel other = new JXPanel();
        other.setAlpha(.5f);
        JXFrame otherFrame = wrapInFrame(other,
                "xpanel with alpha, showing on top of first");
        show(otherFrame, 100, 100);
    }

    /**
     * Issue #1516-swingx: need feature to _not_ paint the background if alpha
     * and not-opaque
     * 
     * 
     */
    public void interactiveBackgroundAlphaToggleOpaque() {
        JPanel container = new JPanel(new GridLayout(0, 3));
        Color base = Color.RED;
        Color alpha = PaintUtils.setAlpha(base, 100);

        final JPanel p = new JPanel();
        p.setBorder(BorderFactory.createTitledBorder("core (alpha back)"));
        p.setBackground(alpha);
        final JLabel coreLabel = new JLabel("is-opaque: " + p.isOpaque());
        p.add(coreLabel);

        final JXPanel xp1 = new JXPanel();
        xp1.setBorder(BorderFactory.createTitledBorder("x (alpha back)"));
        xp1.setBackground(alpha);
        final JLabel xbackAlpha = new JLabel("is-opaque: " + xp1.isOpaque());
        xp1.add(xbackAlpha);

        final JXPanel xp2 = new JXPanel();
        xp2.setBorder(BorderFactory.createTitledBorder("x (alpha container)"));
        xp2.setBackground(base);
        xp2.setAlpha(100f / 255f);
        final JLabel xcontainerAlpha = new JLabel("is-opaque: "
                + xp2.isOpaque());
        xp2.add(xcontainerAlpha);

        container.add(p);
        container.add(xp1);
        container.add(xp2);

        JXFrame frame = wrapInFrame(container,
                "opaque with alpha " + System.getProperty("java.version") + " "
                        + UIManager.get("JXPanel.patch"));
        Action action = new AbstractAction("toggle opaque (is on)") {
            boolean realOpaque = true;

            @Override
            public void actionPerformed(ActionEvent e) {
                realOpaque = !realOpaque;

                putValue(Action.NAME, realOpaque ? "toggle opaque (is on)"
                        : "toggle opaque (is off)");

                p.setOpaque(realOpaque);
                xp1.setOpaque(realOpaque);
                xp2.setOpaque(realOpaque);

                coreLabel.setText("is-opaque: " + p.isOpaque());
                xbackAlpha.setText("is-opaque: " + xbackAlpha.isOpaque());
                xcontainerAlpha.setText("is-opaque: "
                        + xcontainerAlpha.isOpaque());
                p.repaint();
                xp1.repaint();
                xp2.repaint();
            }
        };
        addAction(frame, action);
        show(frame, 400, 400);
    }

    /**
     * Issue 1517-swingx: JXPanel - background color depends on opaqueness prior
     * to setting
     */
    public void interactiveBackgroundAlphaColorDependsOnOpaque() {
        JPanel container = new JPanel(new GridLayout(0, 2));
        Color alpha = PaintUtils.setAlpha(Color.RED, 100);

        JXPanel opaque = new JXPanel();
        opaque.setBorder(BorderFactory
                .createTitledBorder("xpanel (alpha color, real opaque)"));
        opaque.setBackground(alpha);

        JXPanel nonOpaque = new JXPanel();
        nonOpaque.setOpaque(false);
        nonOpaque.setBorder(BorderFactory
                .createTitledBorder("xpanel (alpha color, real !opaque)"));
        nonOpaque.setBackground(alpha);

        container.add(opaque);
        container.add(nonOpaque);
        show(wrapInFrame(container,
                "compare alpha background: opaque <-> not opaque"), 400, 400);
    }

    /**
     * Issue ??-swingx: support core painters in synth-based LAFs
     * 
     * In synth-based LAFs (core) painters are applied on top of the background
     * fill and below the foreground (== content) painting. SwingX painters are
     * applied on top of the background but below the core painter.
     * 
     * The example below has:
     * 
     * - yellowish background color - SwingX ImagePainter - core painter which
     * paints a diagonal line (top left to bottom right) - a label as content
     * 
     * With Nimbus, all three "background" layers should be visible
     * 
     * Actions:
     * 
     * - toggle opaqueness: if false, the background color must not be painted 
     * - toggle alpha of background color: must not show any artefacts 
     * - toggle alpha of containter: must not show any artefacts, must respect the
     * background filling or not (opaqueness on/off), background alpha must be
     * unchanged
     */
    public void interactiveCustomPanelPainter() {
        final JXPanel panel = new JXPanel(new BorderLayout());
        final Color background = Color.YELLOW;
        final Color backgroundTrans = PaintUtils.setAlpha(background, 100);
        panel.setBackground(background);
        panel.setBackgroundPainter(new ImagePainter(XTestUtils
                .loadDefaultImage("moon.jpg")));
        JLabel label = new JLabel(" seeing something");
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setFont(label.getFont().deriveFont(55f));
        panel.add(label);
        UIDefaults defaults = new UIDefaults();
        defaults.put("Panel.backgroundPainter", new CrossPainter());
        panel.putClientProperty("Nimbus.Overrides", defaults);
        panel.putClientProperty("Nimbus.Overrides.InheritDefaults", true);
        JXFrame frame = wrapInFrame(panel, "background");

        Action opaque = new AbstractAction("toggle opaque (is: true)") {
            boolean isOpaque = true;

            @Override
            public void actionPerformed(ActionEvent e) {
                isOpaque = !isOpaque;
                panel.setOpaque(isOpaque);
                putValue(NAME, "toggle opaque (is: " + isOpaque);
                panel.repaint();
            }
        };
        addAction(frame, opaque);

        Action containerAlpha = new AbstractAction("toggle container alpha") {

            @Override
            public void actionPerformed(ActionEvent e) {
                float alpha = panel.getAlpha();
                panel.setAlpha(alpha < 1f ? 1f : 0.5f);
            }
        };
        addAction(frame, containerAlpha);
        Action backgroundAlpha = new AbstractAction("toggle background alpha") {

            @Override
            public void actionPerformed(ActionEvent e) {
                Color back = panel.getBackground();
                panel.setBackground(back.getAlpha() < 255 ? background
                        : backgroundTrans);

            }
        };
        addAction(frame, backgroundAlpha);
        show(frame, 800, 1000);
    }

    public static class CrossPainter implements Painter<JComponent> {

        BasicStroke stroke = new BasicStroke(20f);

        @Override
        public void paint(Graphics2D g, JComponent comp, int width, int height) {
            g.setColor(Color.MAGENTA);
            g.setStroke(stroke);
            g.drawLine(0, 0, comp.getWidth(), comp.getHeight());
        }

    }

    /**
     * Issue #1199-swingx: JXPanel - must repaint on changes to background
     * painter.
     */
    public void interactivePainterUpdate() {
        JXPanel panel = new JXPanel();
        final MattePainter painter = new MattePainter(Color.RED);
        panel.setBackgroundPainter(painter);
        JXFrame frame = wrapInFrame(panel, "background");
        Action toggleColor = new AbstractAction("toggle background") {

            @Override
            public void actionPerformed(ActionEvent e) {
                Paint old = painter.getFillPaint();
                painter.setFillPaint(old == Color.RED ? Color.YELLOW
                        : Color.RED);

            }
        };
        addAction(frame, toggleColor);
        show(frame, 200, 200);
    }

    /**
     * Issue #1187-swingx: default scrollable tracks property prevents
     * scrolling. Problem are the implementations of scrollableTracks: they
     * unconditionally return true, so forcing the viewport/scrollpane to the
     * size of the contained component.
     */
    public void interactiveScrolling() {
        final JXPanel panel = new JXPanel(new BorderLayout());
        panel.add(createScrollableContent(20));
        JXFrame frame = wrapWithScrollingInFrame(panel,
                "scrollbar must be showing");
        Action toggleHeightTrack = new AbstractActionExt("track height: "
                + panel.getScrollableHeightHint()) {

            ScrollableSizeHint[] tracks = new ScrollableSizeHint[] {
                    ScrollableSizeHint.FIT, ScrollableSizeHint.NONE,
                    ScrollableSizeHint.PREFERRED_STRETCH };

            int position;

            @Override
            public void actionPerformed(ActionEvent e) {
                position++;
                if (position >= tracks.length)
                    position = 0;
                panel.setScrollableHeightHint(tracks[position]);
                setName("track height: " + tracks[position]);
            }
        };
        addAction(frame, toggleHeightTrack);
        show(frame, 400, 400);
    }

    /**
     * @return
     */
    private JComponent createScrollableContent(int rows) {
        JPanel component = new JPanel();
        component.setPreferredSize(new Dimension(400, 400));
        component.setMinimumSize(new Dimension(200, 200));
        component.setMaximumSize(new Dimension(600, 600));
        component.setBorder(BorderFactory.createLineBorder(Color.RED, 5));

        // JButton component = new JButton("gotcha!");
        // JXTable table = new JXTable(rows, 6);
        // for (int i = 0; i < table.getRowCount(); i++) {
        // table.setValueAt("row: " + i, i, 0);
        // }
        return component;
    }

    /**
     * See a painter at work.
     * 
     * Plus Issue ??-swingx: setting opaqueness _after_ the painter leads to
     * painting artefacts.
     * 
     * Here: run this check with several tests active so that the frame create
     * here is overlapped by others, the click on its title to move to front.
     * The background is not uniform: the formerly covered part is grey, the
     * formerly exposed part is white.
     * 
     * Update: probably similar to #1515
     * 
     * @throws Exception
     */
    public void interactiveIconPainter() throws Exception {
        ImagePainter imagePainter = new ImagePainter(ImageIO.read(JXPanel.class
                .getResource("resources/images/kleopatra.jpg")));
        JXPanel panel = new JXPanel();
        panel.setBackgroundPainter(imagePainter);
        // uncomment to see the painting glitch. Move to before setting the
        // background painter and
        // all is fine.
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(200, 200));
        showWithScrollingInFrame(panel, "icon painter in jxpanel");
    }

    public void interactiveAlphaCheck() {
        JXPanel xp = new JXPanel();
        xp.setBackgroundPainter(new MattePainter(PaintUtils.AERITH, true));
        xp.setAlpha(.5f);
        xp.add(new JButton("Press Me"));

        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.BLACK);
        p.add(xp);

        show(wrapInFrame(p, "Alpha Check"), 200, 200);
    }

    public void interactiveBackgroundAndAlphaCheck() {
        final JXPanel xp = new JXPanel();
        xp.setBackground(Color.RED);
        xp.setAlpha(.5f);
        xp.add(new JButton("Press Me"));

        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.BLACK);
        p.add(xp);

        show(wrapInFrame(p, "Alpha Check"), 200, 200);
    }

    /**
     * do-nothing method - suppress warning if there are no other test fixtures
     * to run.
     * 
     */
    public void testDummy() {

    }

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(JXPanelVisualCheck.class
            .getName());
}
