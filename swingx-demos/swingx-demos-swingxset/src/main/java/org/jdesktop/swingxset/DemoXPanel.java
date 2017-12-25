/*
// * Copyright 2007-2008 Sun Microsystems, Inc.  All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Sun Microsystems nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.jdesktop.swingxset;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.Rectangle2D;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.Border;

import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.interpolation.PropertySetter;
import org.jdesktop.animation.timing.triggers.TimingTrigger;
import org.jdesktop.animation.timing.triggers.TimingTriggerEvent;
import org.jdesktop.swingx.JXPanel;

import com.sun.swingset3.Demo;
import com.sun.swingset3.utilities.RoundedBorder;
import com.sun.swingset3.utilities.Utilities;

/**
 *
 * @author aim
 * @author Jeanette Winzenburg (SwingX adaption)
 */
public class DemoXPanel extends JXPanel {
    
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(DemoXPanel.class
            .getName());
    
    static final Border roundedBorder = new RoundedBorder(10);
    
    // insets for description editorpane
    static final Insets margin = new Insets(8, 10, 8, 8);

    private Demo demo;

    public DemoXPanel(Demo demo) throws Exception  {
        LoadedXDemoPanel demoPanel = new LoadedXDemoPanel(demo);
        this.demo = demo;
        setLayout(new BorderLayout());
        // remind(aim): how to access resourceMap?
        //resourceMap = getContext().getResourceMap();

        // JW: adding is done in end of fade-out
        // getting even weirder layout if prematurely adding here ...
//        add(demoPanel);
        LoadAnimationPanel loadAnimationPanel = new LoadAnimationPanel();

        add(loadAnimationPanel);
        loadAnimationPanel.setAnimating(true);

        Animator fadeOutAnimator = null;
        Animator fadeInAnimator = null;
        try {
            loadAnimationPanel.setAnimating(false);
            fadeOutAnimator = new Animator(400,
                    new FadeOut(DemoXPanel.this,
                            loadAnimationPanel, demoPanel));
            fadeOutAnimator.setAcceleration(.2f);
            fadeOutAnimator.setDeceleration(.3f);
            fadeInAnimator = new Animator(400,
                    new PropertySetter(DemoXPanel.this, "alpha", 0.3f, 1.0f));
            TimingTrigger.addTrigger(fadeOutAnimator, fadeInAnimator, TimingTriggerEvent.STOP);
            fadeOutAnimator.start();
        } catch (Exception ignore) {
            if (fadeOutAnimator != null) fadeOutAnimator.stop();
            if (fadeInAnimator != null) fadeInAnimator.stop();
            if (loadAnimationPanel != null)
               loadAnimationPanel.setAnimating(false);
            
            System.err.println(ignore);
            ignore.printStackTrace();
        }
    }

    public Demo getDemo() {
        return demo;
    }

    private static class FadeOut extends PropertySetter {
        private JXPanel parent;
        private JXPanel out;
        private JComponent in;

        public FadeOut(JXPanel parent, JXPanel out, JComponent in) {
            super(out, "alpha", 1.0f, 0.3f);
            this.parent = parent;
            this.out = out;
            this.in = in;
        }

        public void end() {
            parent.setAlpha(0.3f);
            parent.remove(out);
            parent.add(in);
            parent.revalidate();
        }
    } // Fader

    private static class LoadAnimationPanel extends JXPanel {
        private String message;
        private int triState = 0;
        private boolean animating = false;
        private Animator animator;

        public LoadAnimationPanel() {
            setBorder(roundedBorder);
            setBackground(Utilities.deriveColorHSB(
                    UIManager.getColor("Panel.background"), 0, 0, -.06f));

            // remind(aim): get from resource map
            message = "demo loading";

            PropertySetter rotator = new PropertySetter(this, "triState", 0, 3);
            animator = new Animator(500, Animator.INFINITE,
                    Animator.RepeatBehavior.LOOP, rotator);
            // Don't animate gears if loading is quick
            animator.setStartDelay(200);
        }

        public void setAnimating(boolean animating) {
            this.animating = animating;
            if (animating) {
                animator.start();
            } else {
                animator.stop();
            }
        }

        public boolean isAnimating() {
            return animating;
        }

        public void setTriState(int triState) {
            this.triState = triState;
            repaint();
        }

        public int getTriState() {
            return triState;
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            Dimension size = getSize();

            Color textColor = Utilities.deriveColorHSB(getBackground(), 0, 0, -.3f);
            Color dotColor = Utilities.deriveColorHSB(textColor, 0, .2f, -.08f);
            g2.setColor(textColor);
            g2.setFont(UIManager.getFont("Label.font").deriveFont(32f));
            FontMetrics metrics = g2.getFontMetrics();
            Rectangle2D rect = metrics.getStringBounds(message, g2);
            Rectangle2D dotRect = metrics.getStringBounds(".", g2);
            float x = (float) (size.width - (rect.getWidth() + 3 * dotRect.getWidth())) / 2;
            float y = (float) (size.height - rect.getHeight()) / 2;
            g2.drawString(message, x, y);
            int tri = getTriState();
            float dx = 0;
            for (int i = 0; i < 3; i++) {
                g2.setColor(animator.isRunning() && i == tri ?
                        dotColor :
                        textColor);
                g2.drawString(".", x + (float) (rect.getWidth() + dx), y);
                dx += dotRect.getWidth();
            }
        }
    } // LoadAnimationPanel
}
