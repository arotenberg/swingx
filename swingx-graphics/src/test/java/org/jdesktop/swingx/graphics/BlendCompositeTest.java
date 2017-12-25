package org.jdesktop.swingx.graphics;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.jdesktop.swingx.util.GraphicsUtilities.createCompatibleImage;
import static org.jdesktop.swingx.util.GraphicsUtilities.getPixels;
import static org.jdesktop.swingx.util.GraphicsUtilities.loadCompatibleImage;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;

import org.jdesktop.swingx.util.GraphicsUtilities;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This unit test performs blend operations on known image inputs and compares the results to
 * expected image output. All expected images have been visually verified prior to inclusion in this
 * test.
 * 
 * @author kschaefer
 */
@SuppressWarnings("nls")
public class BlendCompositeTest {
    private static BufferedImage VERTICAL_IMAGE;
    private static BufferedImage HORIZONTAL_IMAGE;
    
    @BeforeClass
    public static void initialize() throws Exception {
        VERTICAL_IMAGE = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("vertical.gif"));
        HORIZONTAL_IMAGE = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("horizontal.gif"));
        
        assertThat(VERTICAL_IMAGE.getWidth(), is(HORIZONTAL_IMAGE.getWidth()));
        assertThat(VERTICAL_IMAGE.getHeight(), is(HORIZONTAL_IMAGE.getHeight()));
        
        assumeThat(VERTICAL_IMAGE.getColorModel(), is(instanceOf(DirectColorModel.class)));
        assumeThat(HORIZONTAL_IMAGE.getColorModel(), is(instanceOf(DirectColorModel.class)));
    }
    
    private BufferedImage actual;
    private BufferedImage expected;
    
    @Before
    public void setUp() {
        actual = createCompatibleImage(VERTICAL_IMAGE);
    }
    
    private void blend(BufferedImage src, BlendComposite composite, BufferedImage dst) {
        Graphics2D g2 = actual.createGraphics();
        
        try {
            g2.setComposite(AlphaComposite.Clear);
            g2.fillRect(0, 0, actual.getWidth(), actual.getHeight());
            
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.setComposite(AlphaComposite.Src);
            g2.drawImage(src, 0, 0, null);
            g2.setComposite(composite);
            g2.drawImage(dst, 0, 0, null);
        } finally {
            g2.dispose();
        }
    }
    
    @Test
    public void testDarkenHorizontalOverVertical() throws Exception {
        expected = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("darken.gif"));
        blend(VERTICAL_IMAGE, BlendComposite.Darken, HORIZONTAL_IMAGE);
        
        assertArrayEquals(getPixels(expected, 0, 0, expected.getWidth(), expected.getHeight(), null),
                getPixels(actual, 0, 0, actual.getWidth(), actual.getHeight(), null));
    }
    
    @Test
    public void testDarkenVerticalOverHorizontal() throws Exception {
        expected = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("darken.gif"));
        blend(HORIZONTAL_IMAGE, BlendComposite.Darken, VERTICAL_IMAGE);
        
        assertArrayEquals(getPixels(expected, 0, 0, expected.getWidth(), expected.getHeight(), null),
                getPixels(actual, 0, 0, actual.getWidth(), actual.getHeight(), null));
    }
    
    @Test
    public void testMultiplyHorizontalOverVertical() throws Exception {
        expected = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("multiply.gif"));
        blend(VERTICAL_IMAGE, BlendComposite.Multiply, HORIZONTAL_IMAGE);
        
        assertArrayEquals(getPixels(expected, 0, 0, expected.getWidth(), expected.getHeight(), null),
                getPixels(actual, 0, 0, actual.getWidth(), actual.getHeight(), null));
    }
    
    @Test
    public void testMultiplyVerticalOverHorizontal() throws Exception {
        expected = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("multiply.gif"));
        blend(HORIZONTAL_IMAGE, BlendComposite.Multiply, VERTICAL_IMAGE);
        
        assertArrayEquals(getPixels(expected, 0, 0, expected.getWidth(), expected.getHeight(), null),
                getPixels(actual, 0, 0, actual.getWidth(), actual.getHeight(), null));
    }
    
    @Test
    public void testColorBurnHorizontalOverVertical() throws Exception {
        expected = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("color-burn-hov.gif"));
        blend(VERTICAL_IMAGE, BlendComposite.ColorBurn, HORIZONTAL_IMAGE);

        assertArrayEquals(getPixels(expected, 0, 0, expected.getWidth(), expected.getHeight(), null),
                getPixels(actual, 0, 0, actual.getWidth(), actual.getHeight(), null));
    }
    
    @Test
    public void testColorBurnVerticalOverHorizontal() throws Exception {
        expected = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("color-burn-voh.gif"));
        blend(HORIZONTAL_IMAGE, BlendComposite.ColorBurn, VERTICAL_IMAGE);
        
        assertArrayEquals(getPixels(expected, 0, 0, expected.getWidth(), expected.getHeight(), null),
                getPixels(actual, 0, 0, actual.getWidth(), actual.getHeight(), null));
    }
    
    @Test
    public void testInverseColorBurnHorizontalOverVertical() throws Exception {
        expected = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("color-burn-voh.gif"));
        blend(VERTICAL_IMAGE, BlendComposite.InverseColorBurn, HORIZONTAL_IMAGE);
        
        assertArrayEquals(getPixels(expected, 0, 0, expected.getWidth(), expected.getHeight(), null),
                getPixels(actual, 0, 0, actual.getWidth(), actual.getHeight(), null));
    }
    
    @Test
    public void testInverseColorBurnVerticalOverHorizontal() throws Exception {
        expected = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("color-burn-hov.gif"));
        blend(HORIZONTAL_IMAGE, BlendComposite.InverseColorBurn, VERTICAL_IMAGE);
        
        assertArrayEquals(getPixels(expected, 0, 0, expected.getWidth(), expected.getHeight(), null),
                getPixels(actual, 0, 0, actual.getWidth(), actual.getHeight(), null));
    }
    
    @Test
    public void testSoftBurnHorizontalOverVertical() throws Exception {
        expected = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("soft-burn-hov.gif"));
        blend(VERTICAL_IMAGE, BlendComposite.SoftBurn, HORIZONTAL_IMAGE);
        
        assertArrayEquals(getPixels(expected, 0, 0, expected.getWidth(), expected.getHeight(), null),
                getPixels(actual, 0, 0, actual.getWidth(), actual.getHeight(), null));
    }
    
    @Test
    public void testSoftBurnVerticalOverHorizontal() throws Exception {
        expected = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("soft-burn-voh.gif"));
        blend(HORIZONTAL_IMAGE, BlendComposite.SoftBurn, VERTICAL_IMAGE);
        
        assertArrayEquals(getPixels(expected, 0, 0, expected.getWidth(), expected.getHeight(), null),
                getPixels(actual, 0, 0, actual.getWidth(), actual.getHeight(), null));
    }
    
    @Test
    public void testSubtractHorizontalOverVertical() throws Exception {
        expected = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("subtract.gif"));
        blend(VERTICAL_IMAGE, BlendComposite.Subtract, HORIZONTAL_IMAGE);
        
        assertArrayEquals(getPixels(expected, 0, 0, expected.getWidth(), expected.getHeight(), null),
                getPixels(actual, 0, 0, actual.getWidth(), actual.getHeight(), null));
    }
    
    @Test
    public void testSubtractVerticalOverHorizontal() throws Exception {
        expected = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("subtract.gif"));
        blend(HORIZONTAL_IMAGE, BlendComposite.Subtract, VERTICAL_IMAGE);
        
        assertArrayEquals(getPixels(expected, 0, 0, expected.getWidth(), expected.getHeight(), null),
                getPixels(actual, 0, 0, actual.getWidth(), actual.getHeight(), null));
    }
    
    @Test
    public void testLightenHorizontalOverVertical() throws Exception {
        expected = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("lighten.gif"));
        blend(VERTICAL_IMAGE, BlendComposite.Lighten, HORIZONTAL_IMAGE);
        
        assertArrayEquals(getPixels(expected, 0, 0, expected.getWidth(), expected.getHeight(), null),
                getPixels(actual, 0, 0, actual.getWidth(), actual.getHeight(), null));
    }
    
    @Test
    public void testLightenVerticalOverHorizontal() throws Exception {
        expected = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("lighten.gif"));
        blend(HORIZONTAL_IMAGE, BlendComposite.Lighten, VERTICAL_IMAGE);
        
        assertArrayEquals(getPixels(expected, 0, 0, expected.getWidth(), expected.getHeight(), null),
                getPixels(actual, 0, 0, actual.getWidth(), actual.getHeight(), null));
    }
    
    @Test
    public void testScreenHorizontalOverVertical() throws Exception {
        expected = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("screen.gif"));
        blend(VERTICAL_IMAGE, BlendComposite.Screen, HORIZONTAL_IMAGE);
        
        assertArrayEquals(getPixels(expected, 0, 0, expected.getWidth(), expected.getHeight(), null),
                getPixels(actual, 0, 0, actual.getWidth(), actual.getHeight(), null));
    }
    
    @Test
    public void testScreenVerticalOverHorizontal() throws Exception {
        expected = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("screen.gif"));
        blend(HORIZONTAL_IMAGE, BlendComposite.Screen, VERTICAL_IMAGE);
        
        assertArrayEquals(getPixels(expected, 0, 0, expected.getWidth(), expected.getHeight(), null),
                getPixels(actual, 0, 0, actual.getWidth(), actual.getHeight(), null));
    }
    
    @Test
    public void testColorDodgeHorizontalOverVertical() throws Exception {
        expected = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("color-dodge-hov.gif"));
        blend(VERTICAL_IMAGE, BlendComposite.ColorDodge, HORIZONTAL_IMAGE);
        
        assertArrayEquals(getPixels(expected, 0, 0, expected.getWidth(), expected.getHeight(), null),
                getPixels(actual, 0, 0, actual.getWidth(), actual.getHeight(), null));
    }
    
    @Test
    public void testColorDodgeVerticalOverHorizontal() throws Exception {
        expected = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("color-dodge-voh.gif"));
        blend(HORIZONTAL_IMAGE, BlendComposite.ColorDodge, VERTICAL_IMAGE);
        
        assertArrayEquals(getPixels(expected, 0, 0, expected.getWidth(), expected.getHeight(), null),
                getPixels(actual, 0, 0, actual.getWidth(), actual.getHeight(), null));
    }
    
    @Test
    public void testInverseColorDodgeHorizontalOverVertical() throws Exception {
        expected = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("color-dodge-voh.gif"));
        blend(VERTICAL_IMAGE, BlendComposite.InverseColorDodge, HORIZONTAL_IMAGE);
        
        assertArrayEquals(getPixels(expected, 0, 0, expected.getWidth(), expected.getHeight(), null),
                getPixels(actual, 0, 0, actual.getWidth(), actual.getHeight(), null));
    }
    
    @Test
    public void testInverseColorDodgeVerticalOverHorizontal() throws Exception {
        expected = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("color-dodge-hov.gif"));
        blend(HORIZONTAL_IMAGE, BlendComposite.InverseColorDodge, VERTICAL_IMAGE);
        
        assertArrayEquals(getPixels(expected, 0, 0, expected.getWidth(), expected.getHeight(), null),
                getPixels(actual, 0, 0, actual.getWidth(), actual.getHeight(), null));
    }
    
    @Test
    public void testSoftDodgeHorizontalOverVertical() throws Exception {
        expected = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("soft-burn-voh.gif"));
        blend(VERTICAL_IMAGE, BlendComposite.SoftDodge, HORIZONTAL_IMAGE);
        
        assertArrayEquals(getPixels(expected, 0, 0, expected.getWidth(), expected.getHeight(), null),
                getPixels(actual, 0, 0, actual.getWidth(), actual.getHeight(), null));
    }
    
    @Test
    public void testSoftDodgeVerticalOverHorizontal() throws Exception {
        expected = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("soft-burn-hov.gif"));
        blend(HORIZONTAL_IMAGE, BlendComposite.SoftDodge, VERTICAL_IMAGE);
        
        assertArrayEquals(getPixels(expected, 0, 0, expected.getWidth(), expected.getHeight(), null),
                getPixels(actual, 0, 0, actual.getWidth(), actual.getHeight(), null));
    }
    
    @Test
    public void testAddHorizontalOverVertical() throws Exception {
        expected = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("add.gif"));
        blend(VERTICAL_IMAGE, BlendComposite.Add, HORIZONTAL_IMAGE);
        
        assertArrayEquals(getPixels(expected, 0, 0, expected.getWidth(), expected.getHeight(), null),
                getPixels(actual, 0, 0, actual.getWidth(), actual.getHeight(), null));
    }
    
    @Test
    public void testAddVerticalOverHorizontal() throws Exception {
        expected = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("add.gif"));
        blend(HORIZONTAL_IMAGE, BlendComposite.Add, VERTICAL_IMAGE);
        
        assertArrayEquals(getPixels(expected, 0, 0, expected.getWidth(), expected.getHeight(), null),
                getPixels(actual, 0, 0, actual.getWidth(), actual.getHeight(), null));
    }
    
    @Test
    public void testOverlayHorizontalOverVertical() throws Exception {
        expected = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("overlay-hov.gif"));
        blend(VERTICAL_IMAGE, BlendComposite.Overlay, HORIZONTAL_IMAGE);
        
        assertArrayEquals(getPixels(expected, 0, 0, expected.getWidth(), expected.getHeight(), null),
                getPixels(actual, 0, 0, actual.getWidth(), actual.getHeight(), null));
    }
    
    @Test
    public void testOverlayVerticalOverHorizontal() throws Exception {
        expected = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("overlay-voh.gif"));
        blend(HORIZONTAL_IMAGE, BlendComposite.Overlay, VERTICAL_IMAGE);
        
        assertArrayEquals(getPixels(expected, 0, 0, expected.getWidth(), expected.getHeight(), null),
                getPixels(actual, 0, 0, actual.getWidth(), actual.getHeight(), null));
    }
    
    @Test
    public void testSoftLightHorizontalOverVertical() throws Exception {
        expected = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("soft-light-hov.gif"));
        blend(VERTICAL_IMAGE, BlendComposite.SoftLight, HORIZONTAL_IMAGE);

        assertArrayEquals(getPixels(expected, 0, 0, expected.getWidth(), expected.getHeight(), null),
                getPixels(actual, 0, 0, actual.getWidth(), actual.getHeight(), null));
    }
    
    @Test
    public void testSoftLightVerticalOverHorizontal() throws Exception {
        expected = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("soft-light-voh.gif"));
        blend(HORIZONTAL_IMAGE, BlendComposite.SoftLight, VERTICAL_IMAGE);
        
        assertArrayEquals(getPixels(expected, 0, 0, expected.getWidth(), expected.getHeight(), null),
                getPixels(actual, 0, 0, actual.getWidth(), actual.getHeight(), null));
    }
    
    @Test
    public void testHardLightHorizontalOverVertical() throws Exception {
        expected = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("overlay-voh.gif"));
        blend(VERTICAL_IMAGE, BlendComposite.HardLight, HORIZONTAL_IMAGE);
        
        assertArrayEquals(getPixels(expected, 0, 0, expected.getWidth(), expected.getHeight(), null),
                getPixels(actual, 0, 0, actual.getWidth(), actual.getHeight(), null));
    }
    
    @Test
    public void testHardLightVerticalOverHorizontal() throws Exception {
        expected = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("overlay-hov.gif"));
        blend(HORIZONTAL_IMAGE, BlendComposite.HardLight, VERTICAL_IMAGE);
        
        assertArrayEquals(getPixels(expected, 0, 0, expected.getWidth(), expected.getHeight(), null),
                getPixels(actual, 0, 0, actual.getWidth(), actual.getHeight(), null));
    }
    
    @Test
    public void testVividLightHorizontalOverVertical() throws Exception {
        expected = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("vivid-light-hov.gif"));
        blend(VERTICAL_IMAGE, BlendComposite.VividLight, HORIZONTAL_IMAGE);
        
        assertArrayEquals(getPixels(expected, 0, 0, expected.getWidth(), expected.getHeight(), null),
                getPixels(actual, 0, 0, actual.getWidth(), actual.getHeight(), null));
    }
    
    @Test
    public void testVividLightVerticalOverHorizontal() throws Exception {
        expected = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("vivid-light-voh.gif"));
        blend(HORIZONTAL_IMAGE, BlendComposite.VividLight, VERTICAL_IMAGE);
        
        assertArrayEquals(getPixels(expected, 0, 0, expected.getWidth(), expected.getHeight(), null),
                getPixels(actual, 0, 0, actual.getWidth(), actual.getHeight(), null));
    }
    
    @Test
    public void testLinearLightHorizontalOverVertical() throws Exception {
        expected = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("linear-light-hov.gif"));
        blend(VERTICAL_IMAGE, BlendComposite.LinearLight, HORIZONTAL_IMAGE);
        
        assertArrayEquals(getPixels(expected, 0, 0, expected.getWidth(), expected.getHeight(), null),
                getPixels(actual, 0, 0, actual.getWidth(), actual.getHeight(), null));
    }
    
    @Test
    public void testLinearLightVerticalOverHorizontal() throws Exception {
        expected = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("linear-light-voh.gif"));
        blend(HORIZONTAL_IMAGE, BlendComposite.LinearLight, VERTICAL_IMAGE);
        
        assertArrayEquals(getPixels(expected, 0, 0, expected.getWidth(), expected.getHeight(), null),
                getPixels(actual, 0, 0, actual.getWidth(), actual.getHeight(), null));
    }
    
    @Test
    public void testPinLightHorizontalOverVertical() throws Exception {
        expected = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("pin-light-hov.gif"));
        blend(VERTICAL_IMAGE, BlendComposite.PinLight, HORIZONTAL_IMAGE);
        
        assertArrayEquals(getPixels(expected, 0, 0, expected.getWidth(), expected.getHeight(), null),
                getPixels(actual, 0, 0, actual.getWidth(), actual.getHeight(), null));
    }
    
    @Test
    public void testPinLightVerticalOverHorizontal() throws Exception {
        expected = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("pin-light-voh.gif"));
        blend(HORIZONTAL_IMAGE, BlendComposite.PinLight, VERTICAL_IMAGE);
        
        assertArrayEquals(getPixels(expected, 0, 0, expected.getWidth(), expected.getHeight(), null),
                getPixels(actual, 0, 0, actual.getWidth(), actual.getHeight(), null));
    }
    
    @Test
    public void testHardMixHorizontalOverVertical() throws Exception {
        expected = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("hard-mix.gif"));
        blend(VERTICAL_IMAGE, BlendComposite.HardMix, HORIZONTAL_IMAGE);
        
        assertArrayEquals(getPixels(expected, 0, 0, expected.getWidth(), expected.getHeight(), null),
                getPixels(actual, 0, 0, actual.getWidth(), actual.getHeight(), null));
    }
    
    @Test
    public void testHardMixVerticalOverHorizontal() throws Exception {
        expected = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("hard-mix.gif"));
        blend(HORIZONTAL_IMAGE, BlendComposite.HardMix, VERTICAL_IMAGE);

        assertArrayEquals(getPixels(expected, 0, 0, expected.getWidth(), expected.getHeight(), null),
                getPixels(actual, 0, 0, actual.getWidth(), actual.getHeight(), null));
    }
    
    @Test
    public void testDifferenceHorizontalOverVertical() throws Exception {
        expected = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("difference.gif"));
        blend(VERTICAL_IMAGE, BlendComposite.Difference, HORIZONTAL_IMAGE);
        
        assertArrayEquals(getPixels(expected, 0, 0, expected.getWidth(), expected.getHeight(), null),
                getPixels(actual, 0, 0, actual.getWidth(), actual.getHeight(), null));
    }
    
    @Test
    public void testDifferenceVerticalOverHorizontal() throws Exception {
        expected = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("difference.gif"));
        blend(HORIZONTAL_IMAGE, BlendComposite.Difference, VERTICAL_IMAGE);
        
        assertArrayEquals(getPixels(expected, 0, 0, expected.getWidth(), expected.getHeight(), null),
                getPixels(actual, 0, 0, actual.getWidth(), actual.getHeight(), null));
    }
    
    @Test
    public void testExclusionHorizontalOverVertical() throws Exception {
        expected = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("exclusion.gif"));
        blend(VERTICAL_IMAGE, BlendComposite.Exclusion, HORIZONTAL_IMAGE);
        
        assertArrayEquals(getPixels(expected, 0, 0, expected.getWidth(), expected.getHeight(), null),
                getPixels(actual, 0, 0, actual.getWidth(), actual.getHeight(), null));
    }
    
    @Test
    public void testExclusionVerticalOverHorizontal() throws Exception {
        expected = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("exclusion.gif"));
        blend(HORIZONTAL_IMAGE, BlendComposite.Exclusion, VERTICAL_IMAGE);
        
        assertArrayEquals(getPixels(expected, 0, 0, expected.getWidth(), expected.getHeight(), null),
                getPixels(actual, 0, 0, actual.getWidth(), actual.getHeight(), null));
    }
    
    @Test
    public void testRedOverHorizontal() throws Exception {
        expected = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("red-over-horizontal.gif"));
        
        BufferedImage red = GraphicsUtilities.createCompatibleImage(VERTICAL_IMAGE);
        Graphics2D g = red.createGraphics();
        
        try {
            g.setColor(Color.RED);
            g.fillRect(0, 0, red.getWidth(), red.getHeight());
        } finally {
            g.dispose();
        }
        
        blend(HORIZONTAL_IMAGE, BlendComposite.Red, red);
        
        assertArrayEquals(getPixels(expected, 0, 0, expected.getWidth(), expected.getHeight(), null),
                getPixels(actual, 0, 0, actual.getWidth(), actual.getHeight(), null));
    }
    
    @Test
    public void testGreenOverHorizontal() throws Exception {
        expected = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("green-over-horizontal.gif"));
        
        BufferedImage green = GraphicsUtilities.createCompatibleImage(VERTICAL_IMAGE);
        Graphics2D g = green.createGraphics();
        
        try {
            g.setColor(Color.GREEN);
            g.fillRect(0, 0, green.getWidth(), green.getHeight());
        } finally {
            g.dispose();
        }
        
        blend(HORIZONTAL_IMAGE, BlendComposite.Green, green);
        
        assertArrayEquals(getPixels(expected, 0, 0, expected.getWidth(), expected.getHeight(), null),
                getPixels(actual, 0, 0, actual.getWidth(), actual.getHeight(), null));
    }
    
    @Test
    public void testBlueOverHorizontal() throws Exception {
        expected = loadCompatibleImage(BlendCompositeTest.class.getResourceAsStream("blue-over-horizontal.gif"));
        
        BufferedImage blue = GraphicsUtilities.createCompatibleImage(VERTICAL_IMAGE);
        Graphics2D g = blue.createGraphics();
        
        try {
            g.setColor(Color.BLUE);
            g.fillRect(0, 0, blue.getWidth(), blue.getHeight());
        } finally {
            g.dispose();
        }
        
        blend(HORIZONTAL_IMAGE, BlendComposite.Blue, blue);
        
        assertArrayEquals(getPixels(expected, 0, 0, expected.getWidth(), expected.getHeight(), null),
                getPixels(actual, 0, 0, actual.getWidth(), actual.getHeight(), null));
    }
}
