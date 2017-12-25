/*
 * $Id: BlendCompositeDemo.java 4097 2011-11-30 19:22:13Z kschaefe $
 *
 * Copyright 2009 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx.demos.blendcomposite;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.graphics.BlendComposite;
import org.jdesktop.swingx.graphics.BlendComposite.BlendingMode;
import org.jdesktop.swingx.util.GraphicsUtilities;

import com.sun.swingset3.DemoProperties;

/**
 * A demo for the {@code BlendComposite}.
 *
 * @author Karl George Schaefer
 * @author Romain Guy <romain.guy@mac.com> (original version)
 */
@DemoProperties(
    value = "BlendComposite Demo",
    category = "Graphics",
    description = "Demonstrates BlendComposite, a Composite for defining various blending effects.",
    sourceFiles = {
        "org/jdesktop/swingx/demos/blendcomposite/BlendCompositeDemo.java",
        "org/jdesktop/swingx/demos/blendcomposite/resources/BlendCompositeDemo.properties",
        "org/jdesktop/swingx/demos/blendcomposite/resources/BlendCompositeDemo.html",
        "org/jdesktop/swingx/demos/blendcomposite/resources/images/A.jpg",
        "org/jdesktop/swingx/demos/blendcomposite/resources/images/B.jpg"
    }
)
@SuppressWarnings("serial")
public class BlendCompositeDemo extends JXPanel {
    private static class CompositeTestPanel extends JPanel {
        private BufferedImage image = null;
        private Composite composite = AlphaComposite.Src;
        private BufferedImage imageA;
        private BufferedImage imageB;
        private boolean repaint = false;

        public CompositeTestPanel() {
            setOpaque(false);
            try {
                imageA = GraphicsUtilities.loadCompatibleImage(getClass().getResource("resources/images/A.jpg"));
                imageB = GraphicsUtilities.loadCompatibleImage(getClass().getResource("resources/images/B.jpg"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(imageA.getWidth(), imageA.getHeight());
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (image == null) {
                image = new BufferedImage(imageA.getWidth(),
                                          imageA.getHeight(),
                                          BufferedImage.TYPE_INT_ARGB);
                repaint = true;
            }

            if (repaint) {
                Graphics2D g2 = image.createGraphics();
                g2.setComposite(AlphaComposite.Clear);
                g2.fillRect(0, 0, image.getWidth(), image.getHeight());

                g2.setComposite(AlphaComposite.Src);
                g2.drawImage(imageA, 0, 0, null);
                g2.setComposite(getComposite());
                g2.drawImage(imageB, 0, 0, null);
                g2.dispose();

                repaint = false;
            }

            int x = (getWidth() - image.getWidth()) / 2;
            int y = (getHeight() - image.getHeight()) / 2;
            g.drawImage(image, x, y, null);
        }

        public void setComposite(Composite composite) {
            if (composite != null) {
                this.composite = composite;
                this.repaint = true;
                repaint();
            }
        }

        public Composite getComposite() {
            return this.composite;
        }
    }
    
    private CompositeTestPanel compositeTestPanel;
    private JComboBox combo;
    private JSlider slider;
    
    public BlendCompositeDemo() {
        setLayout(new BorderLayout());
        compositeTestPanel = new CompositeTestPanel();
        compositeTestPanel.setComposite(BlendComposite.Average);
        add(new JScrollPane(compositeTestPanel));

        combo = new JComboBox(BlendComposite.BlendingMode.values());
        combo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                compositeTestPanel.setComposite(
                    BlendComposite.getInstance(
                        (BlendingMode) combo.getSelectedItem(),
                        slider.getValue() / 100.0f
                    ));
            }
        });

        slider = new JSlider(0, 100, 100);
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                BlendComposite blend = (BlendComposite) compositeTestPanel.getComposite();
                blend = blend.derive(slider.getValue() / 100.0f);
                compositeTestPanel.setComposite(blend);
            }
        });
        Dictionary<Integer, JComponent> labels = new Hashtable<Integer, JComponent>();
        //TODO can we fill these labels from the properties file?
        labels.put(0, new JLabel("0%"));
        labels.put(100, new JLabel("100%"));
        slider.setLabelTable(labels);
        slider.setPaintLabels(true);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(combo);
        controls.add(slider);
        add(controls, BorderLayout.SOUTH);
    }
    
    /**
     * main method allows us to run as a standalone demo.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame(BlendCompositeDemo.class.getAnnotation(DemoProperties.class).value());
                
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(new BlendCompositeDemo());
                frame.setPreferredSize(new Dimension(800, 600));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}
