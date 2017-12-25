/*
 * Copyright 2007 Sun Microsystems, Inc.  All Rights Reserved.
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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.interpolation.PropertySetter;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.AbstractLayoutPainter.HorizontalAlignment;
import org.jdesktop.swingx.painter.AbstractLayoutPainter.VerticalAlignment;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.ImagePainter;
import org.jdesktop.swingx.util.GraphicsUtilities;

import com.sun.swingset3.DemoProperties;

/**
 *
 * @author Amy Fowler
 */
@DemoProperties(
        value = "IntroSplash",
        category = "Intro",
        description = "Demonstrates an in-application splash with animation effect.",
        sourceFiles = {                
                "org/jdesktop/swingxset/IntroPanelDemo.java",
                "org/jdesktop/swingxset/SwingXSet.java",
                "org/jdesktop/swingx/appframework/SingleXFrameApplication.java", 
                "org/jdesktop/swingx/appframework/XProperties.java"
                
                }
)
/**
 * Intro panel which uses compound, animated painters to show the app image.
 */
public class IntroPanelDemo extends JXPanel {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(IntroPanelDemo.class
            .getName());

    private SlidingPainter textImagePainter;

    private ImagePainter introImagePainter;
    
    public IntroPanelDemo() {
        setName("introPanel");
        
        // <snip> ImagePainters for intro
        introImagePainter = new ImagePainter();
        introImagePainter.setFillHorizontal(true);
        introImagePainter.setVerticalAlignment(VerticalAlignment.TOP);
        
        textImagePainter = new SlidingPainter();
        textImagePainter.setVisible(false);
        textImagePainter.setHorizontalAlignment(HorizontalAlignment.LEFT);
        textImagePainter.setVerticalAlignment(VerticalAlignment.TOP);
        
        setBackgroundPainter(new CompoundPainter<Object>(introImagePainter, textImagePainter));
        // </snip>
        

        Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(this);

        bind();
        
    }

    /**
     * 
     */
    private void bind() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                slideTextIn();
            }
        });
    }
    
    /**
     * Configures the intro image painter with the given image, converting
     * to a BufferedImage if necessary.
     * 
     * @param image
     */
    public void setIntroImage(Image image) {
        introImagePainter.setImage(image instanceof BufferedImage ? 
                (BufferedImage) image : GraphicsUtilities.convertToBufferedImage(image));
    }
    
    /**
     * Configures the text image painter with the given image, converting
     * to a BufferedImage if necessary.
     *  
     * @param image
     */
    // <snip> ImagePainters for intro
    // the image is loaded via resource injection
    public void setTextImage(Image image) {
        textImagePainter.setImage(image instanceof BufferedImage ? 
                (BufferedImage) image : GraphicsUtilities.convertToBufferedImage(image));
    }
    // </snip>
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        // <snip> ImagePainters for intro
        // start animation of text
        if (!textImagePainter.isVisible()) {
            slideTextIn();
            textImagePainter.setVisible(true);
        }
    }
    
    // create, configure and start an animator on the painter's
    // horizontal location
    @Action
    public void slideTextIn() {
        Animator animator = new Animator(800, 
                new PropertySetter(textImagePainter, "x", getWidth(), 30));
        animator.setStartDelay(800);
        animator.setAcceleration(.2f);
        animator.setDeceleration(.5f);
        animator.start();
        // </snip>
    }
    
    public void slideTextOut() {
        Animator animator = new Animator(600, 
                new PropertySetter(textImagePainter, "x", textImagePainter.getX(), -getWidth()));
        animator.setStartDelay(10);
        animator.setAcceleration(.5f);
        animator.setDeceleration(.2f);
        animator.start();        
    }
    
    public class SlidingPainter extends ImagePainter {
        public SlidingPainter(BufferedImage image) {
            super(image);
            // move out of the way ;-)
            setX(2000);
        }
        
        public SlidingPainter() {
            this(null);
        }

        public void setX(int x) {
            setInsets(new Insets(110, x, 0, 0));
            // hack around an open issue in swingx:
            // CompoundPainter doesn't propagate property changes of contained
            // painters
            repaint();
        }
        
        public int getX() {
            return getInsets() != null ? getInsets().left : 0;
        }
    }

    /**
     * main method allows us to run as a standalone demo.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame(IntroPanelDemo.class.getAnnotation(DemoProperties.class).value());
                
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(new IntroPanelDemo());
                frame.setPreferredSize(new Dimension(800, 600));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

}
