/*
 * Created on 04.10.2010
 *
 */
package org.jdesktop.swingx;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.util.logging.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class JXFrameTest extends InteractiveTestCase {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(JXFrameTest.class
            .getName());
    
    @Test
    public void testGraphicsConfig() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run ui test - headless environment");
            return;
        }
        JXFrame compare = new JXFrame();
        GraphicsConfiguration gc = new DummyGraphicsConfiguration(compare.getGraphicsConfiguration());
        JXFrame frame = new JXFrame(gc);
        assertEquals(gc, frame.getGraphicsConfiguration());
        assertEquals(compare.getDefaultCloseOperation(), frame.getDefaultCloseOperation());
        assertEquals(compare.getTitle(), frame.getTitle());
    }
    
    public static class DummyGraphicsConfiguration extends GraphicsConfiguration {
        
        GraphicsConfiguration delegate;
        
        public DummyGraphicsConfiguration(GraphicsConfiguration delegate) {
            this.delegate = delegate;
        }

        @Override
        public BufferedImage createCompatibleImage(int width, int height) {
            return delegate.createCompatibleImage(width, height);
        }

        @Override
        public Rectangle getBounds() {
            return delegate.getBounds();
        }

        @Override
        public ColorModel getColorModel() {
            return delegate.getColorModel();
        }

        @Override
        public ColorModel getColorModel(int transparency) {
            return delegate.getColorModel(transparency);
        }

        @Override
        public AffineTransform getDefaultTransform() {
            return delegate.getDefaultTransform();
        }

        @Override
        public GraphicsDevice getDevice() {
            return delegate.getDevice();
        }

        @Override
        public AffineTransform getNormalizingTransform() {
            return delegate.getNormalizingTransform();
        }
        
    }
}
