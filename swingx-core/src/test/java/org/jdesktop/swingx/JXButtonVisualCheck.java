/*
 * $Id: JXButtonVisualCheck.java 4217 2012-08-03 18:28:12Z kschaefe $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.jdesktop.swingx.image.FastBlurFilter;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.util.PaintUtils;

/**
 * Visual tests of JXButton issues.
 * @author rah003
 *
 */
@SuppressWarnings("nls")
public class JXButtonVisualCheck extends InteractiveTestCase {

    /**
     * Test for issue #761.
     */
    public void interactiveButton() {
        final JFrame f = new JFrame();
    	JPanel control = new JPanel();
        JButton b = new JButton("Start");
        control.add(b);
        f.add(control, BorderLayout.SOUTH);
        f.setPreferredSize(new Dimension(400, 400));
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setVisible(true);
    }

    /**
     * Test for issue 849
     */
    public void interactiveActionButton() {
        AbstractAction action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //do nothing
            }
        };
        action.putValue(Action.NAME, "My Action");
        action.setEnabled(true);
        final JFrame f = new JFrame();
        f.setSize(300, 200);
        JPanel jContentPane = new JPanel();
        jContentPane.setLayout(new BorderLayout());
        jContentPane.add(new JButton(action), BorderLayout.WEST); // Generated
        jContentPane.add(new JXButton(action), BorderLayout.EAST);
        f.setContentPane(jContentPane);
        f.setTitle("JFrame");
        f.setVisible(true);
    }
    
    /**
     * SwingX Issue 1158
     */
    public void interactiveStatusBarCheck() {
        final JXButton button = new JXButton("Sample");
        MattePainter p = new MattePainter(PaintUtils.BLUE_EXPERIENCE, true);
        button.setForegroundPainter(p);
        BufferedImage im;
        try {
            im = ImageIO.read(JXButton.class.getResource("plaf/basic/resources/error16.png"));
        } catch (IOException ignore) {
            System.out.println(ignore);
            im = null;
        }
        button.setIcon(new ImageIcon(im));
        
        JXFrame frame = wrapInFrame(button, "Painter testing");
        frame.setStatusBar(new JXStatusBar());
        show(frame);
    }
    
    public void interactiveForegroundCheck() {
        final JXButton button = new JXButton("Sample");
//        MattePainter p = new MattePainter(PaintUtils.AERITH, true);
        final MattePainter p = new MattePainter(PaintUtils.BLUE_EXPERIENCE, true);
        p.setFilters(new FastBlurFilter());
        button.setForegroundPainter(p);
        button.addActionListener(new ActionListener(){
            private String[] values = new String[] {"Hello", "Goodbye", "SwingLabs", "Turkey Bowl"};
            private int index = 1;
            @Override
            public void actionPerformed(ActionEvent ae) {
                button.setText(values[index]);
                index++;
                if (index >= values.length) {
                    index = 0;
                }
            }
        });
        BufferedImage im;
        try {
            im = ImageIO.read(JXButton.class.getResource("plaf/basic/resources/error16.png"));
        } catch (IOException ignore) {
            System.out.println(ignore);
            im = null;
        }
        button.setIcon(new ImageIcon(im));
        button.addMouseListener(new MouseAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void mouseEntered(MouseEvent e) {
                p.setFilters((BufferedImageOp[]) null);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void mouseExited(MouseEvent e) {
                p.setFilters(new FastBlurFilter());
            }
            
        });
        
        showInFrame(button, "Painter testing");
    }

    public void interactiveBackgroundCheck() {
        final JXButton button = new JXButton("Sample");
        MattePainter p = new MattePainter(PaintUtils.AERITH, true);
        button.setBackgroundPainter(p);
        button.addActionListener(new ActionListener(){
            private String[] values = new String[] {"Hello", "Goodbye", "SwingLabs", "Turkey Bowl"};
            private int index = 1;
            @Override
            public void actionPerformed(ActionEvent ae) {
                button.setText(values[index]);
                index++;
                if (index >= values.length) {
                    index = 0;
                }
            }
        });
        BufferedImage im;
        try {
            im = ImageIO.read(JXButton.class.getResource("plaf/basic/resources/error16.png"));
        } catch (IOException ignore) {
            System.out.println(ignore);
            im = null;
        }
        button.setIcon(new ImageIcon(im));
        
        showInFrame(button, "Painter testing");
    }
    
    /**
     * SWINGX-1449: Ensure that the font displays correctly when the background or background painter is set.
     */
    public void interactiveFontAndBackgroundCheck() {
        Font font = Font.decode("Arial-BOLDITALIC-14");
        Color background = Color.LIGHT_GRAY;
        Painter<?> backgroundPainter = new MattePainter(background);

        JButton button1 = new JButton("Default");
        JButton button2 = new JButton("Font changed");
        button2.setFont(font);
        JButton button3 = new JButton("Background changed");
        button3.setBackground(background);
        JButton button4 = new JButton("Background changed");
        button4.setBackground(background);
//        button4.setBackgroundPainter(backgroundPainter);
        JButton button5 = new JButton("Font and Background changed");
        button5.setFont(font);
        button5.setBackground(background);
        JButton button6 = new JButton("Font and Background changed");
        button6.setFont(font);
        button6.setBackground(background);
//        button6.setBackgroundPainter(backgroundPainter);

        JXButton xbutton1 = new JXButton("Default");
        JXButton xbutton2 = new JXButton("Font changed");
        xbutton2.setFont(font);
        JXButton xbutton3 = new JXButton("Background changed");
        xbutton3.setBackground(background);
        JXButton xbutton4 = new JXButton("BackgroundPainter changed");
        xbutton4.setBackgroundPainter(backgroundPainter);
        JXButton xbutton5 = new JXButton("Font and Background changed");
        xbutton5.setFont(font);
        xbutton5.setBackground(background);
        JXButton xbutton6 = new JXButton("Font and BackgroundPainter changed");
        xbutton6.setFont(font);
        xbutton6.setBackgroundPainter(backgroundPainter);

        JPanel panel = new JPanel(new GridLayout(7, 2, 1, 1));
        panel.add(new JXLabel(JButton.class.getSimpleName(), SwingConstants.CENTER));
        panel.add(new JXLabel(JXButton.class.getSimpleName(), SwingConstants.CENTER));
        panel.add(button1);
        panel.add(xbutton1);
        panel.add(button2);
        panel.add(xbutton2);
        panel.add(button3);
        panel.add(xbutton3);
        panel.add(button4);
        panel.add(xbutton4);
        panel.add(button5);
        panel.add(xbutton5);
        panel.add(button6);
        panel.add(xbutton6);

        showInFrame(panel, "Font and Background Check");
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        JXButtonVisualCheck test = new JXButtonVisualCheck();
        try {
            test.runInteractiveTests("interactiveFontAndBackgroundCheck");
          } catch (Exception e) {
              System.err.println("exception when executing interactive tests:");
              e.printStackTrace();
          }
    }

    /**
     * do nothing test - keep the testrunner happy.
     */
    public void testDummy() {
    }

}
