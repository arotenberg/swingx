package org.jdesktop.swingx.geom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import javax.swing.JFrame;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class Star2DTest {

    Star2D star;

    @Before
    public void setUp() {
        star = new Star2D(50,50,10,15,4);
    }
    
    
    /**
     * Visual check. Should paint star fully visibly at the right bottom corner.
     * @param args
     */
    //TODO fix this visual check
    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        JXPanel p = new JXPanel();
//        p.setPreferredSize(new Dimension(65,65));
//        p.setBackgroundPainter(new ShapePainter(new Star2D(50,50,10,15,16), Color.RED));
//        f.add(p);
        f.pack();
        f.setVisible(true);
    }

    @Test
    public void testGetBounds() {
        // [50,50] are the center coordinates. In no way they can be also x,y coords of the star bounds
        assertFalse(star.getBounds().x == 50);
        assertFalse(star.getBounds().y == 50);
        // 4 branch star with branches pointing to the corner of the bounding rect
        //assertEquals(new Rectangle(39,39,22,22), star.getBounds());
        // 4 branch star with branches pointing horizontally and vertically 
        assertEquals(new Rectangle(35,35,30,30), star.getBounds());
        
    }
    @Test
    public void testContains() {
        assertTrue(star.contains(new Point2D.Double(50,50)));
        assertTrue(star.contains(new Rectangle(45,45,10,10)));
        assertTrue(star.contains(45,45,10,10));
        assertTrue(star.contains(55,55));
    }

    @Test
    public void testIntersects() {
        assertTrue(star.intersects(new Rectangle(25,25,50,50)));
        assertFalse(star.intersects(5,5,10,10));
    }

    @Test
    public void testGetPathIterator() {
        assertNotNull(star.getPathIterator(AffineTransform.getRotateInstance(.5)));
        assertNotNull(star.getPathIterator(AffineTransform.getRotateInstance(.5), .7));
    }

    @Test
    public void testGetters() {
        assertEquals(4, star.getBranchesCount());
        assertEquals(10d, star.getInnerRadius(), .1);
        assertEquals(15d, star.getOuterRadius(), .1);
        
    }
    
}
