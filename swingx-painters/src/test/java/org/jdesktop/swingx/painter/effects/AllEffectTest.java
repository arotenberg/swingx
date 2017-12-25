package org.jdesktop.swingx.painter.effects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.Point;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Basic effect tests to ensure default behavior is not affected by changes in abstract parents.
 * @author had
 *
 */
@RunWith(JUnit4.class)
public class AllEffectTest {

    @Test
    public void testGlowPathEffect() {
        GlowPathEffect effect = new GlowPathEffect();
        assertEquals(Color.WHITE, effect.getBrushColor());
        assertEquals(10, effect.getBrushSteps());
        assertEquals(10, effect.getEffectWidth());
        assertEquals(new Point(0,0), effect.getOffset());
        assertFalse(effect.isRenderInsideShape());
        assertFalse(effect.isShouldFillShape());
    }
    @Test
    public void testInnerGlowPathEffect() {
        InnerGlowPathEffect effect = new InnerGlowPathEffect();
        assertEquals(Color.WHITE, effect.getBrushColor());
        assertEquals(10, effect.getBrushSteps());
        assertEquals(10, effect.getEffectWidth());
        assertEquals(new Point(0,0), effect.getOffset());
        assertTrue(effect.isRenderInsideShape());
        assertFalse(effect.isShouldFillShape());
    }
    @Test
    public void testInnerShadowPathEffect() {
        InnerShadowPathEffect effect = new InnerShadowPathEffect();
        assertEquals(Color.BLACK, effect.getBrushColor());
        assertEquals(10, effect.getBrushSteps());
        assertEquals(8, effect.getEffectWidth());
        assertEquals(new Point(2,2), effect.getOffset());
        assertTrue(effect.isRenderInsideShape());
        assertTrue(effect.isShouldFillShape());
    }
    @Test
    public void testShadowPathEffect() {
        ShadowPathEffect effect = new ShadowPathEffect();
        assertEquals(Color.BLACK, effect.getBrushColor());
        assertEquals(10, effect.getBrushSteps());
        assertEquals(8, effect.getEffectWidth());
        assertEquals(new Point(3,3), effect.getOffset());
        assertFalse(effect.isRenderInsideShape());
        assertTrue(effect.isShouldFillShape());
    }
    @Test
    public void testNeonBorderEffect() {
        NeonBorderEffect effect = new NeonBorderEffect();
        assertEquals(Color.GREEN, effect.getEdgeColor());
        assertEquals(Color.WHITE, effect.getCenterColor());
        assertEquals(10, effect.getBrushSteps());
        assertEquals(10, effect.getEffectWidth());
        assertEquals(new Point(0,0), effect.getOffset());
        assertFalse(effect.isRenderInsideShape());
        assertFalse(effect.isShouldFillShape());
    }
}
