package org.jdesktop.swingx.geom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.Point2D;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Basic tests for Morphing2D
 * @author had
 *
 */
@RunWith(JUnit4.class)
public class Morphing2DTest {

    Morphing2D morph ;

    @Before
    public void setUp() {
        morph= new Morphing2D(new Rectangle(0,0,50,50), new Ellipse2D.Double(50,50,50,50));

    }
    @Test
    public void testGetBounds() {
        assertEquals(new Rectangle(0,0,50,50), morph.getBounds());
        morph.setMorphing(.5);
        assertEquals(new Rectangle(25,25,50,50), morph.getBounds());
        morph.setMorphing(1);
        assertEquals(new Rectangle(50,50,50,50), morph.getBounds());
    }
    @Test
    public void testContains() {
        try {
            morph.contains(new Point2D.Double(5,5));
            fail("when you implement this method, don't forget to add tests as well");
        } catch (InternalError e) {
            // expected
        }
        try {
            morph.contains(new Rectangle(5,5,10,10));
            fail("when you implement this method, don't forget to add tests as well");
        } catch (InternalError e) {
            // expected
        }
        try {
            morph.contains(5,5,10,10);
            fail("when you implement this method, don't forget to add tests as well");
        } catch (InternalError e) {
            // expected
        }
        try {
            morph.contains(5,5);
            fail("when you implement this method, don't forget to add tests as well");
        } catch (InternalError e) {
            // expected
        }
    }

    @Test
    public void testIntersects() {
        try {
            morph.intersects(new Rectangle(5,5,10,10));
            fail("when you implement this method, don't forget to add tests as well");
        } catch (InternalError e) {
            // expected
        }
        try {
            morph.intersects(5,5,10,10);
            fail("when you implement this method, don't forget to add tests as well");
        } catch (InternalError e) {
            // expected
        }
    }

    @Test
    public void testGetPathIterator() {
        assertFalse(morph.getPathIterator(AffineTransform.getRotateInstance(.5)) instanceof FlatteningPathIterator);
        assertTrue(morph.getPathIterator(AffineTransform.getRotateInstance(.5), .7) instanceof FlatteningPathIterator);
    }

}
