/*
 * $Id: PainterDemo.java 4097 2011-11-30 19:22:13Z kschaefe $
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
package org.jdesktop.swingx.demos.painter;

import static org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;

import org.jdesktop.application.Application;
import org.jdesktop.beans.AbstractBean;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.Converter;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.swingx.JXCollapsiblePane;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledSeparator;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.binding.DisplayInfo;
import org.jdesktop.swingx.binding.DisplayInfoArrayConverter;
import org.jdesktop.swingx.binding.DisplayInfoConverter;
import org.jdesktop.swingx.binding.LabelHandler;
import org.jdesktop.swingx.combobox.EnumComboBoxModel;
import org.jdesktop.swingx.painter.AbstractAreaPainter;
import org.jdesktop.swingx.painter.AbstractAreaPainter.Style;
import org.jdesktop.swingx.painter.AbstractLayoutPainter;
import org.jdesktop.swingx.painter.AbstractLayoutPainter.HorizontalAlignment;
import org.jdesktop.swingx.painter.AbstractLayoutPainter.VerticalAlignment;
import org.jdesktop.swingx.painter.AbstractPainter;
import org.jdesktop.swingx.painter.AbstractPainter.Interpolation;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.GlossPainter;
import org.jdesktop.swingx.painter.GlossPainter.GlossPosition;
import org.jdesktop.swingx.painter.ImagePainter;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.painter.PinstripePainter;
import org.jdesktop.swingx.painter.RectanglePainter;
import org.jdesktop.swingx.painter.ShapePainter;
import org.jdesktop.swingx.painter.TextPainter;
import org.jdesktop.swingx.painter.effects.AreaEffect;
import org.jdesktop.swingx.painter.effects.GlowPathEffect;
import org.jdesktop.swingx.painter.effects.InnerGlowPathEffect;
import org.jdesktop.swingx.painter.effects.InnerShadowPathEffect;
import org.jdesktop.swingx.painter.effects.NeonBorderEffect;
import org.jdesktop.swingx.painter.effects.ShadowPathEffect;
import org.jdesktop.swingx.renderer.DefaultListRenderer;
import org.jdesktop.swingx.renderer.DefaultTreeRenderer;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.util.PaintUtils;
import org.jdesktop.swingx.util.ShapeUtils;
import org.jdesktop.swingxset.util.DisplayValues;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jhlabs.image.AverageFilter;
import com.jhlabs.image.BlockFilter;
import com.jhlabs.image.BlurFilter;
import com.jhlabs.image.BorderFilter;
import com.jhlabs.image.BoxBlurFilter;
import com.jhlabs.image.BumpFilter;
import com.jhlabs.image.ChromeFilter;
import com.jhlabs.image.ContrastFilter;
import com.jhlabs.image.CrystallizeFilter;
import com.jhlabs.image.DespeckleFilter;
import com.jhlabs.image.DiffuseFilter;
import com.jhlabs.image.DiffusionFilter;
import com.jhlabs.image.DilateFilter;
import com.jhlabs.image.DisplaceFilter;
import com.jhlabs.image.DitherFilter;
import com.jhlabs.image.EdgeFilter;
import com.jhlabs.image.EmbossFilter;
import com.jhlabs.image.EqualizeFilter;
import com.jhlabs.image.ErodeFilter;
import com.jhlabs.image.FlareFilter;
import com.jhlabs.image.GainFilter;
import com.jhlabs.image.GammaFilter;
import com.jhlabs.image.GaussianFilter;
import com.jhlabs.image.GlintFilter;
import com.jhlabs.image.GlowFilter;
import com.jhlabs.image.GrayFilter;
import com.jhlabs.image.InvertAlphaFilter;
import com.jhlabs.image.InvertFilter;
import com.jhlabs.image.LensBlurFilter;
import com.jhlabs.image.LevelsFilter;
import com.jhlabs.image.LifeFilter;
import com.jhlabs.image.MarbleFilter;
import com.jhlabs.image.MarbleTexFilter;
import com.jhlabs.image.MedianFilter;
import com.jhlabs.image.MinimumFilter;
import com.jhlabs.image.MirrorFilter;
import com.jhlabs.image.MotionBlurFilter;
import com.jhlabs.image.NoiseFilter;
import com.jhlabs.image.OilFilter;
import com.jhlabs.image.OpacityFilter;
import com.jhlabs.image.OutlineFilter;
import com.jhlabs.image.PointillizeFilter;
import com.jhlabs.image.PosterizeFilter;
import com.jhlabs.image.QuantizeFilter;
import com.jhlabs.image.RaysFilter;
import com.jhlabs.image.ReduceNoiseFilter;
import com.jhlabs.image.RippleFilter;
import com.jhlabs.image.ShadowFilter;
import com.jhlabs.image.SharpenFilter;
import com.jhlabs.image.SkeletonFilter;
import com.jhlabs.image.SmearFilter;
import com.jhlabs.image.SolarizeFilter;
import com.jhlabs.image.SparkleFilter;
import com.jhlabs.image.SphereFilter;
import com.jhlabs.image.SwimFilter;
import com.jhlabs.image.ThresholdFilter;
import com.jhlabs.image.TwirlFilter;
import com.jhlabs.image.UnsharpFilter;
import com.jhlabs.image.WaterFilter;
import com.sun.swingset3.DemoProperties;

/**
 * A demo for {@code Painter}.
 *
 * @author Karl George Schaefer
 * @author joshy (original PainterDemoSet)
 */
@DemoProperties(
    value = "Painter Demo",
    category = "Decorators",
    description = "Demonstrates Painters, a painting delegate.",
    sourceFiles = {
        "org/jdesktop/swingx/demos/painter/PainterDemo.java",
        "org/jdesktop/swingx/demos/painter/PainterDemoUtils.java",
        "org/jdesktop/swingx/binding/DisplayInfo.java",
        "org/jdesktop/swingx/binding/DisplayInfoConverter.java",
        "org/jdesktop/swingx/demos/painter/resources/PainterDemo.properties"
    }
)
@SuppressWarnings("serial")
public class PainterDemo extends JPanel {
    
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(PainterDemo.class
            .getName());
    
    private JXTree painterDemos;
    private JXPanel painterDisplay;
    private JComboBox horizontalAlignmentBox;
    private JComboBox verticalAlignmentBox;
    private PainterControl painterControl;

    private JSlider insetSlider;

    private JCheckBox fillHorizontal;

    private JCheckBox fillVertical;

    private JXCollapsiblePane layoutPainterControlPanel;

    private JXCollapsiblePane basePainterControlPanel;

//    private JComponent painterControlPanel;
    
    private JComboBox filterBox;
    
    private JComboBox interpolationBox;

    private JCheckBox visibleBox;

    private JCheckBox antialiasBox;

    private JXTitledSeparator layoutSeparator;

    private JXTitledSeparator baseSeparator;

    private JComponent areaSeparator;

    private JComboBox styleBox;

    private JXCollapsiblePane areaPainterControlPanel;

    private JComboBox effectBox;

    private JCheckBox paintStretchedBox;

    private JSlider borderWidthSlider;
    
    /**
     * main method allows us to run as a standalone demo.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame(PainterDemo.class.getAnnotation(DemoProperties.class).value());
                
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(new PainterDemo());
                frame.setPreferredSize(new Dimension(800, 600));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
    
    public PainterDemo() {
        super(new BorderLayout());
        createPainterDemo();
        Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(this);
        bind();
    }
    
//---------------------------- Painters

    /**
     * @return
     */
    private TreeModel createPainters() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        root.add(createImagePainterDemos());
        root.add(createShapePainterDemos());
        root.add(createTextPainterDemos());
        root.add(createRectanglePainterDemos());
        root.add(createTransformPainterDemos());
        root.add(createGlossPainterDemos());
        root.add(createPinstripePainterDemos());
        root.add(createMattePainterDemos());
        root.add(createCompoundPainterDemos());
        return new DefaultTreeModel(root);
    }

    
    
    @SuppressWarnings("unchecked")
    private DefaultMutableTreeNode createInfoNode(String desc, Painter painter) {
        DefaultMutableTreeNode pin = new DefaultMutableTreeNode(
                new DisplayInfo<Painter>(desc, painter));
        return pin;
    }
    
    //TODO all this ImageIO stuff should be off EDT
    private MutableTreeNode createImagePainterDemos() {
        DefaultMutableTreeNode node = createInfoNode("Image Painter Demos", null);
        
        try {
            BufferedImage img = ImageIO.read(getClass().getResourceAsStream("border.gif"));
            node.add(createInfoNode("small image", new ImagePainter(img)));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        try {
            BufferedImage img = ImageIO.read(getClass().getResourceAsStream("moon.jpg"));
            node.add(createInfoNode("big image", new ImagePainter(img)));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        try {
            BufferedImage img = ImageIO.read(getClass().getResourceAsStream("border.gif"));
            ImagePainter ip = new ImagePainter(img);
            ip.setHorizontalRepeat(true);
            node.add(createInfoNode("horizontal repeat", ip));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        try {
            BufferedImage img = ImageIO.read(getClass().getResourceAsStream("border.gif"));
            ImagePainter ip = new ImagePainter(img);
            ip.setVerticalRepeat(true);
            node.add(createInfoNode("vertical repeat", ip));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        try {
            BufferedImage img = ImageIO.read(getClass().getResourceAsStream("border.gif"));
            ImagePainter ip = new ImagePainter(img);
            ip.setBorderPaint(Color.BLACK);
            node.add(createInfoNode("image with black border", ip));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        try {
            BufferedImage img = ImageIO.read(getClass().getResourceAsStream("a-glyph.png"));
            node.add(createInfoNode("An image of 'A' with transparent parts", 
                    new ImagePainter(img)));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        try {
            BufferedImage img = ImageIO.read(getClass().getResourceAsStream("a-glyph.png"));
            ImagePainter ip = new ImagePainter(img);
            ip.setFillPaint(Color.RED);
            node.add(createInfoNode("red background visible through transparent parts", ip));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        //FIXME need a way to background load the image, cf. SwingX #1099
//        try {
//            ImagePainter ip = new ImagePainter();
//            ip.setImageString("http://java.sun.com/developer/techDocs/hi/repository/graphicsRepository/toolbarButtonGraphics/general/Delete24.gif");
//            
//            DefaultMutableTreeNode pin = createInfoNode("image loaded from remote URL", ip);
//            node.add(pin);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
        
        return node;
    }
    
    private MutableTreeNode createShapePainterDemos() {
        DefaultMutableTreeNode node = createInfoNode("Shape Painter Demos", null);
        
        // build a star shape with 5 points and 30 degree angles
        Shape starShape = ShapeUtils.generatePolygon(5,30,15,true);
        ShapePainter sp = new ShapePainter(starShape, Color.RED);
        node.add(createInfoNode("Star style", sp));
        
        return node;
    }
    
    private MutableTreeNode createTextPainterDemos() {
        DefaultMutableTreeNode node = createInfoNode("Text Painter Demos", null);
        Font font = new Font("SansSerif", Font.BOLD, 80);
        
        TextPainter tp = new TextPainter("Neon", font, Color.RED);
        node.add(createInfoNode("Text", tp));
        
        tp = new TextPainter("Neon",font,Color.BLACK);
        tp.setFillPaint(PaintUtils.AERITH);
        node.add(createInfoNode("Text with gradient", tp));
        
        return node;
    }
    
    private MutableTreeNode createRectanglePainterDemos() {
        DefaultMutableTreeNode node = createInfoNode("Rectangle Painter Demos", null);
        
        RectanglePainter rp = new RectanglePainter(20, 20, 20, 20, 30, 30, true,
                Color.GREEN, 3, Color.GREEN.darker());
        node.add(createInfoNode("Rectangle, green on gray", rp));
        
        rp = new RectanglePainter(50, 50, 30, Color.GREEN);
        rp.setBorderPaint(Color.GREEN.darker());
        node.add(createInfoNode("Rectangle, 100x100", rp));
       
        rp = new RectanglePainter(5, 5, 5, 5, 10, 10, true, Color.RED, 3, Color.BLACK);
        node.add(createInfoNode("Red Rectangle w/  black border", rp));

        rp = new RectanglePainter(Color.BLACK,Color.RED);
        rp.setRoundHeight(10);
        rp.setRoundWidth(10);
        rp.setRounded(true);
        node.add(createInfoNode("Plain round rect for sizing bugs", rp));
        
        return node;
    }
    
    //TODO some transforms moving painter outside of visible region
    private MutableTreeNode createTransformPainterDemos() {
        DefaultMutableTreeNode node = createInfoNode("Compound Painter Transform Demos", null);
        Font font = new Font("SansSerif", Font.BOLD, 80);
        
        TextPainter tp = new TextPainter("Text", font);
        CompoundPainter<Object> cp = new CompoundPainter<Object>(tp);
        node.add(createInfoNode("Normal Text", cp));
        
        tp = new TextPainter("Text", font);
        cp = new CompoundPainter<Object>(tp);
        cp.setTransform(AffineTransform.getRotateInstance(-Math.PI * 2 / 8, 100, 100));
        node.add(createInfoNode("Rotated Text", cp));
        
        tp = new TextPainter("Text", font);
        cp = new CompoundPainter<Object>(tp);
        cp.setTransform(AffineTransform.getShearInstance(-0.2, 0));
        node.add(createInfoNode("Sheared Text", cp));
        
        tp = new TextPainter("Text", font);
        cp = new CompoundPainter<Object>(tp);
        cp.setTransform(AffineTransform.getScaleInstance(2, 2));
        node.add(createInfoNode("Scaled Text", cp));
        
        RectanglePainter rp = new RectanglePainter(30, 30, 30, 30, 30, 30,
                true, Color.RED, 4f, Color.RED.darker());
        tp = new TextPainter("Text", font);
        cp = new CompoundPainter<Object>(rp, tp);
        cp.setTransform(AffineTransform.getRotateInstance(-Math.PI * 2 / 8, 100, 100));
        node.add(createInfoNode("Rotated Text w/ effects on rect", cp));
        
        return node;
    }
    
    private MutableTreeNode createGlossPainterDemos() {
        DefaultMutableTreeNode node = createInfoNode("Gloss Painter Demos",
                null);

        RectanglePainter rp = new RectanglePainter(20, 20, 20,
                20, 20, 20);
        rp.setFillPaint(Color.RED);
        rp.setBorderPaint(Color.RED.darker());
        rp.setStyle(RectanglePainter.Style.BOTH);
        rp.setBorderWidth(5);
        rp.setAntialiasing(true);
        CompoundPainter<Object> cp = new CompoundPainter<Object>(rp, new GlossPainter());
        node.add(createInfoNode("Gloss on top of rectangle", cp));

        rp = new RectanglePainter(20, 20, 20, 20, 20, 20, true,
                Color.RED, 5f, Color.RED.darker());
        rp.setAntialiasing(true);
        cp = new CompoundPainter<Object>(rp, new GlossPainter(GlossPosition.BOTTOM));
        node.add(createInfoNode("Gloss on bottom of rectangle", cp));

        return node;
    }
    
    private MutableTreeNode createPinstripePainterDemos() {
        DefaultMutableTreeNode node = createInfoNode("Pinstripe Painter Demos", null);
        MattePainter black = new MattePainter(Color.BLACK);
        
        PinstripePainter pp = new PinstripePainter(Color.WHITE, 45, 1, 10);
        CompoundPainter<Object> cp = new CompoundPainter<Object>(black, pp);
        node.add(createInfoNode("45deg white pinstripe on black", cp));
        
        pp = new PinstripePainter(Color.WHITE, 0, 1, 10);
        pp.setAntialiasing(true);
        cp = new CompoundPainter<Object>(black, pp);
        node.add(createInfoNode("vertical white pinstripe on black", cp));
        
        pp = new PinstripePainter(Color.WHITE, 90, 1, 10);
        pp.setAntialiasing(true);
        cp = new CompoundPainter<Object>(black, pp);
        node.add(createInfoNode("horizontal white pinstripe on black", cp));
        
        pp = new PinstripePainter(Color.WHITE, 45, 3, 10);
        pp.setAntialiasing(true);
        cp = new CompoundPainter<Object>(black, pp);
        node.add(createInfoNode("3px wide white pinstripe on black", cp));
        
        pp = new PinstripePainter(Color.WHITE, 45, 10, 2);
        pp.setAntialiasing(true);
        cp = new CompoundPainter<Object>(black, pp);
        node.add(createInfoNode("10px wide pinstripe w/ 2px spacing on black", cp));
        
        pp = new PinstripePainter(Color.WHITE, 45, 3, 15);
        pp.setAntialiasing(true);
        pp.setPaint(
                new GradientPaint(new Point(0, 0), Color.WHITE, new Point(10, 10), Color.BLACK));
        cp = new CompoundPainter<Object>(black, pp);
        node.add(createInfoNode("pinstripe w/ 10px gradient", cp));
        
        pp = new PinstripePainter(Color.WHITE, 45, 3, 15);
        pp.setAntialiasing(true);
        pp.setPaint(
                new GradientPaint(new Point(0, 0), Color.WHITE, new Point(200, 200), Color.BLACK));
        cp = new CompoundPainter<Object>(black, pp);
        node.add(createInfoNode("pinstripe w/ 200px gradient", cp));
        
        return node;
    }
    
    private MutableTreeNode createMattePainterDemos() {
        DefaultMutableTreeNode node = createInfoNode("Matte Painter Gradient Demos", null);
        
        Paint gradient = new GradientPaint(new Point(30, 30), Color.RED,
                new Point(80, 30),  Color.GREEN);
        MattePainter mp = new MattePainter(gradient);
        node.add(createInfoNode("single stop, horiz", mp));
        
        gradient = new GradientPaint(new Point(30, 30), Color.RED, 
                new Point(30, 80), Color.GREEN);
        node.add(createInfoNode("single stop, vert", new MattePainter(gradient)));
        
        gradient = new GradientPaint(new Point(30, 30), Color.RED, 
                new Point(80, 80), Color.GREEN);
        node.add(createInfoNode("single stop, diag", new MattePainter(gradient)));
        
        gradient = new LinearGradientPaint(new Point(30,30), new Point(80,30), 
                new float[] {0f, 0.5f, 1f},
                new Color[] {Color.RED, Color.GREEN, Color.BLUE} );
        node.add(createInfoNode("multi stop horiz", new MattePainter(gradient)));
        
        gradient = new LinearGradientPaint(new Point(30,30), new Point(30,80), 
                new float[] {0f, 0.5f, 1f},
                new Color[] {Color.RED, Color.GREEN, Color.BLUE} );
        node.add(createInfoNode("multi stop vert", new MattePainter(gradient)));
        
        gradient = new LinearGradientPaint(new Point(30,30), new Point(30,80), 
                new float[] {0f, 0.5f, 1f},
                new Color[] {Color.RED, Color.GREEN, Color.BLUE} );
        node.add(createInfoNode("multi stop diag", new MattePainter(gradient)));
        
        return node;
    }
    
    private MutableTreeNode createCompoundPainterDemos() {
        DefaultMutableTreeNode node = createInfoNode("Compound Painter Gradient Demos", null);
        
        MattePainter mp = new MattePainter(Color.GREEN);
        PinstripePainter pp = new PinstripePainter(Color.BLUE);
        CompoundPainter<Object> cp = new CompoundPainter<Object>(mp, pp);
        node.add(createInfoNode("panel w/ blue pinstripe fg, green matte bg", cp));
        
        mp = new MattePainter(Color.GREEN);
        RectanglePainter rp = new RectanglePainter(new Insets(20, 20, 20, 20), 50,
                50, 10, 10, true, Color.RED, 5, Color.RED.darker());
        pp = new PinstripePainter(Color.BLUE);
        cp = new CompoundPainter<Object>(mp, rp, pp);
        node.add(createInfoNode("panel, blue stripe fg, green bg, red rect comp", cp));
                
        rp = new RectanglePainter(20, 20, 5, Color.BLUE);
        TextPainter tp = new TextPainter("Some Text");
        cp = new CompoundPainter<Object>(rp, tp);
        node.add(createInfoNode("text on blue", cp));
        
        mp = new MattePainter(Color.BLACK);
        tp = new TextPainter("Neon");
        tp.setFont(new Font("SansSerif", Font.BOLD, 100));
        tp.setFillPaint(Color.BLACK);
        NeonBorderEffect effect = new NeonBorderEffect(Color.BLACK, Color.RED.brighter(), 10);
        effect.setBorderPosition(NeonBorderEffect.BorderPosition.Centered);
        tp.setAreaEffects(effect);
        cp = new CompoundPainter<Object>(mp, tp);
        node.add(createInfoNode("A Cool Logo", cp));
        
        mp = new MattePainter(Color.GRAY);
        ShapePainter sp = new ShapePainter(
                ShapeUtils.generatePolygon(30, 50, 45, true), Color.RED);
        sp.setStyle(ShapePainter.Style.FILLED);
        sp.setBorderPaint(Color.BLUE);
        ShadowPathEffect starShadow = new ShadowPathEffect();
        starShadow.setOffset(new Point(1,1));
        starShadow.setEffectWidth(5);
        sp.setAreaEffects(starShadow);
        tp = new TextPainter("Coming Soon!", new Font("SansSerif", Font.PLAIN, 12), Color.WHITE);
        cp = new CompoundPainter<Object>(mp, sp, tp);
        node.add(createInfoNode("Coming Soon badge", cp));

        return node;
    }

    /**
     * Create and return model for filter selection combobox.
     * 
     * @return
     */
    private ComboBoxModel createFilterList() {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        model.addElement(new DisplayInfo<BufferedImageOp>("- none - ", null));
        model.addElement(new DisplayInfo<BufferedImageOp>(new AverageFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new BlockFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new BlurFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new BorderFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new BoxBlurFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new BumpFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new ChromeFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new ContrastFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new CrystallizeFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new DespeckleFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new DiffuseFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new DiffusionFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new DilateFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new DisplaceFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new DitherFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new EdgeFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new EmbossFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new EqualizeFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new ErodeFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new FlareFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new GainFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new GammaFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new GaussianFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new GlintFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new GlowFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new GrayFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new InvertFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new InvertAlphaFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new LensBlurFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new LevelsFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new LifeFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new MarbleFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new MarbleTexFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new MedianFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new MinimumFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new MirrorFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new MotionBlurFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new NoiseFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new OilFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new OpacityFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new OutlineFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new PointillizeFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new PosterizeFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new QuantizeFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new RaysFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new ReduceNoiseFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new RippleFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new ShadowFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new SharpenFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new SkeletonFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new SmearFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new SolarizeFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new SparkleFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new SphereFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new SwimFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new ThresholdFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new TwirlFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new UnsharpFilter()));
        model.addElement(new DisplayInfo<BufferedImageOp>(new WaterFilter()));
        return model;
    }

    /**
     * Create and returns model for effects selection combobox
     * @return
     */
    private ComboBoxModel createAreaEffectsList() {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        model.addElement(new DisplayInfo<AreaEffect>(" - none -", null));
        model.addElement(new DisplayInfo<AreaEffect>(new GlowPathEffect()));
        model.addElement(new DisplayInfo<AreaEffect>(new InnerGlowPathEffect()));
        model.addElement(new DisplayInfo<AreaEffect>(new InnerShadowPathEffect()));
        model.addElement(new DisplayInfo<AreaEffect>(new NeonBorderEffect()));
        model.addElement(new DisplayInfo<AreaEffect>(new ShadowPathEffect()));
        return model;
    }
    
//------------------------------- Binding
    
    @SuppressWarnings("unchecked")
    private void bind() {
        // fill and configure painter tree
        painterDemos.setModel(createPainters());
        // create the painter property controller
        painterControl = new PainterControl();
        
        // bind selection of node
        bindSelection(painterDisplay, painterControl);
        
        // bindEnabled
        bindEnabled("areaEnabled", areaSeparator, styleBox, effectBox, 
                borderWidthSlider, paintStretchedBox);
        bindEnabled("alignEnabled", layoutSeparator, horizontalAlignmentBox, 
                verticalAlignmentBox, insetSlider, fillHorizontal, fillVertical);
        bindEnabled("baseEnabled", baseSeparator, interpolationBox, 
                visibleBox, antialiasBox);
        
        // bind collapsed
        bindInversCollapsed("baseEnabled", layoutPainterControlPanel,
                basePainterControlPanel, areaPainterControlPanel);
   }

    @SuppressWarnings("unchecked")
    private void bindSelection(Object... components) {
        painterDemos.setCellRenderer(new DefaultTreeRenderer(
                DisplayValues.DISPLAY_INFO_DESCRIPTION)); 
        Converter<?, ?> painterConverter = new DisplayInfoConverter<Painter>();
        BindingGroup group = new BindingGroup();
        for (int i = 0; i < components.length; i++) {
            BeanProperty p = BeanProperty.create(
                    i == 0 ? "backgroundPainter" : "painter");
            Binding b = Bindings.createAutoBinding(READ,
                    painterDemos, BeanProperty.create("selectedElement_UNWRAP_NODE"),
                    components[i], p);
            b.setConverter(painterConverter);
            group.addBinding(b);
            
        }
        group.bind();
    }
    
    /**
     * @param property
     */
    private void bindInversCollapsed(String property, JComponent... components ) {
        BindingGroup group = new BindingGroup();
        for (JComponent comp : components) {
            group.addBinding(Bindings.createAutoBinding(READ,
                    painterControl, ELProperty.create("${!" + property + "}"),
                    comp, BeanProperty.create("collapsed")));
        }
        group.bind();
    }

    /**
     * @param string
     */
    private void bindEnabled(String property, JComponent... components ) {
        BindingGroup group = new BindingGroup();
        for (JComponent comp : components) {
            group.addBinding(Bindings.createAutoBinding(READ,
                    painterControl, BeanProperty.create(property),
                    comp, BeanProperty.create("enabled")));
            
        }
        group.bind();
    }

    /**
     * Controller of bindings to Painter properties.
     */
    public class PainterControl extends AbstractBean {
        private Painter<?> painter;
        private boolean alignEnabled;
        private boolean baseEnabled;
        private boolean areaEnabled;
        private BindingGroup alignGroup;
        private BindingGroup baseGroup;
        private BindingGroup areaGroup;

        public PainterControl() {
            StringValue effectInfo = DisplayValues.DISPLAY_INFO_DESCRIPTION;
            
            // effects
            effectBox.setRenderer(new DefaultListRenderer(effectInfo));
            effectBox.setModel(createAreaEffectsList());
            styleBox.setModel(
                    new EnumComboBoxModel<Style>(Style.class));
            
            // base
            interpolationBox.setModel(
                    new EnumComboBoxModel<Interpolation>(Interpolation.class));
            filterBox.setModel(createFilterList());
            filterBox.setRenderer(new DefaultListRenderer(effectInfo));
            
            // layout data
            horizontalAlignmentBox.setModel(
                    new EnumComboBoxModel<HorizontalAlignment>(HorizontalAlignment.class));
            verticalAlignmentBox.setModel(
                    new EnumComboBoxModel<VerticalAlignment>(VerticalAlignment.class));

        }
        
        /**
         * @return the areaEnabled
         */
        public boolean isAreaEnabled() {
            return areaEnabled;
        }
        
        /**
         * @param areaEnabled the areaEnabled to set
         */
        public void setAreaEnabled(boolean areaEnabled) {
            boolean old = isAreaEnabled();
            this.areaEnabled = areaEnabled;
            updateAreaBindings();
            firePropertyChange("areaEnabled", old, isAreaEnabled());
        }


        public boolean isBaseEnabled() {
            return baseEnabled;
        }
        
        public void setBaseEnabled(boolean baseEnabled) {
            boolean old = isBaseEnabled();
            this.baseEnabled = baseEnabled;
            updateBaseBindings();
            firePropertyChange("baseEnabled", old, isBaseEnabled());
        }
        

        public boolean isAlignEnabled() {
            return alignEnabled;
        }

        public void setAlignEnabled(boolean alignEnabled) {
            boolean old = isAlignEnabled();
            this.alignEnabled = alignEnabled;
            updateAlignBindings();
            firePropertyChange("alignEnabled", old, isAlignEnabled());
        }

        @SuppressWarnings("unchecked")
        private void updateAreaBindings() {
            if (areaEnabled) {
                areaGroup = new BindingGroup();
                areaGroup.addBinding(Bindings.createAutoBinding(READ, 
                        paintStretchedBox, BeanProperty.create("selected"),
                        painter, BeanProperty.create("paintStretched")));
                areaGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ, 
                        styleBox, BeanProperty.create("selectedItem"),
                        painter, BeanProperty.create("style")));
                Binding effectsBinding = (Bindings.createAutoBinding(READ, 
                        effectBox, BeanProperty.create("selectedItem"),
                        painter, BeanProperty.create("areaEffects")));
                effectsBinding.setConverter(new DisplayInfoArrayConverter(AreaEffect.class));
                areaGroup.addBinding(effectsBinding);
                Binding borderWidthBinding = (Bindings.createAutoBinding(READ, 
                        borderWidthSlider, BeanProperty.create("value"),
                        painter, BeanProperty.create("borderWidth")));
                borderWidthBinding.setConverter(PainterDemoUtils.SLIDER_TO_FLOAT);
                areaGroup.addBinding(borderWidthBinding);
                areaGroup.bind();
            }
        }
        /**
         * 
         */
        @SuppressWarnings("unchecked")
        private void updateBaseBindings() {
            if (baseEnabled) {
                baseGroup = new BindingGroup();
                baseGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ, 
                        interpolationBox, BeanProperty.create("selectedItem"),
                        painter, BeanProperty.create("interpolation")));
                baseGroup.addBinding(Bindings.createAutoBinding(READ, 
                        visibleBox, BeanProperty.create("selected"),
                        painter, BeanProperty.create("visible")));
                baseGroup.addBinding(Bindings.createAutoBinding(READ, 
                        antialiasBox, BeanProperty.create("selected"),
                        painter, BeanProperty.create("antialiasing")));
                Binding filterBinding = (Bindings.createAutoBinding(READ, 
                        filterBox, BeanProperty.create("selectedItem"),
                        painter, BeanProperty.create("filters")));
                filterBinding.setConverter(new DisplayInfoArrayConverter(BufferedImageOp.class));
                baseGroup.addBinding(filterBinding);
                baseGroup.bind();
                
            }
        }
        
        /**
         * 
         */
        @SuppressWarnings("unchecked")
        private void updateAlignBindings() {
            if (alignEnabled) {
                alignGroup = new BindingGroup();
                alignGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ, 
                        horizontalAlignmentBox, BeanProperty.create("selectedItem"),
                        painter, BeanProperty.create("horizontalAlignment")));
                alignGroup.addBinding(Bindings.createAutoBinding(READ, 
                        verticalAlignmentBox, BeanProperty.create("selectedItem"),
                        painter, BeanProperty.create("verticalAlignment")));
                alignGroup.addBinding(Bindings.createAutoBinding(READ, 
                        fillHorizontal, BeanProperty.create("selected"),
                        painter, BeanProperty.create("fillHorizontal")));
                alignGroup.addBinding(Bindings.createAutoBinding(READ, 
                        fillVertical, BeanProperty.create("selected"),
                        painter, BeanProperty.create("fillVertical")));
                Binding insetBinding = (Bindings.createAutoBinding(READ, 
                        insetSlider, BeanProperty.create("value"),
                        painter, BeanProperty.create("insets")));
                insetBinding.setConverter(PainterDemoUtils.SLIDER_TO_INSETS);
                alignGroup.addBinding(insetBinding);
                
                alignGroup.bind();
            }
        }

        public Painter<?> getPainter() {
            return painter;
        }

        public void setPainter(Painter<?> painter) {
            releaseBaseBindings();
            releaseAlignBindings();
            releaseAreaBindings();
            this.painter = painter;
            setBaseEnabled(painter instanceof AbstractPainter<?>);
            setAlignEnabled(painter instanceof AbstractLayoutPainter<?>);
            setAreaEnabled(painter instanceof AbstractAreaPainter<?>);
        }

        /**
         * 
         */
        private void releaseAreaBindings() {
            if (areaGroup !=  null) {
                areaGroup.unbind();
            }
            styleBox.setSelectedItem(Style.BOTH);
            effectBox.setSelectedIndex(0);
            paintStretchedBox.setSelected(false);
        }

        /**
         * 
         */
        private void releaseBaseBindings() {
            if (baseGroup != null) {
                baseGroup.unbind();
            }
            interpolationBox.setSelectedItem(Interpolation.NearestNeighbor);
            filterBox.setSelectedIndex(0);
            visibleBox.setSelected(true);
            antialiasBox.setSelected(true);
        }

        /**
         * 
         */
        private void releaseAlignBindings() {
            if (alignGroup != null) {
                alignGroup.unbind();
            }
            // PENDING JW: reset defaults ... wrong place here?
            verticalAlignmentBox.setSelectedItem(VerticalAlignment.CENTER);
            horizontalAlignmentBox.setSelectedItem(HorizontalAlignment.CENTER);
            insetSlider.setValue(0);
            fillHorizontal.setSelected(false);
            fillHorizontal.setSelected(false);
        }

        
        
    }
    
//------------------------------ Create UI
    
    private void createPainterDemo() {
        painterDisplay = createPainterDisplay();
        
        JComponent painterControlPanel =new JXPanel(new BorderLayout());
        painterControlPanel.add(painterDisplay);
        painterControlPanel.add(createPainterPropertiesPanel(), BorderLayout.SOUTH);
        
        painterDemos = new JXTree();
        painterDemos.setRootVisible(false);
        // PENDING JW: weird sizing in splitpane
        JSplitPane contents = new JSplitPane();
        contents.setDividerLocation(240);
        contents.setContinuousLayout(true);
        contents.setRightComponent(painterControlPanel);
        contents.setLeftComponent(new JScrollPane(painterDemos));
        add(contents);
    }

    /**
     * @param painterControlPanel
     * @return
     */
    private JXPanel createPainterPropertiesPanel() {
        basePainterControlPanel = createBasePainterControlPanel();
        layoutPainterControlPanel = createLayoutPainterControl();
        areaPainterControlPanel = createAreaPainterControlPanel();
        JXPanel properties = new JXPanel();
        FormLayout formLayout = new FormLayout(
                " f:d:n, l:4dlu:n, f:d:n, l:4dlu:n, f:d:n ", // columns
                "t:d:n " 
        ); // rows
        
        PanelBuilder builder = new PanelBuilder(formLayout, properties);
        builder.setBorder(Borders.DLU4_BORDER);
        CellConstraints cl = new CellConstraints();
//        CellConstraints cc = new CellConstraints();
        int currentColumn = 1;
        builder.add(basePainterControlPanel, cl.xy(currentColumn, 1));
        currentColumn +=2;
        builder.add(layoutPainterControlPanel, cl.xy(currentColumn, 1));
        currentColumn +=2;
        builder.add(areaPainterControlPanel, cl.xy(currentColumn, 1));
        currentColumn +=2;
        return properties;
    }


    /**
     * @return
     */
    private JXCollapsiblePane createAreaPainterControlPanel() {
        JXCollapsiblePane painterControl = new JXCollapsiblePane();
        FormLayout formLayout = new FormLayout(
                "5dlu, r:d:n, l:4dlu:n, f:d:n", // columns
                "c:d:n " +
                ", t:4dlu:n, c:d:n " +
                ", t:4dlu:n, c:d:n" +
                ", t:4dlu:n, c:d:n" +
                ", t:4dlu:n, c:d:n"
        ); // rows
        PanelBuilder builder = new PanelBuilder(formLayout, painterControl);
        builder.setBorder(Borders.DLU4_BORDER);
        CellConstraints cl = new CellConstraints();
        CellConstraints cc = new CellConstraints();
        
        areaSeparator = new JXTitledSeparator();
        areaSeparator.setName("areaPainterSeparator");
        builder.add(areaSeparator, cc.xywh(1, 1, 4, 1));
        
        int labelColumn = 2;
        int widgetColumn = labelColumn + 2;
        int currentRow = 3;
        styleBox = new JComboBox();
        styleBox.setName("styleBox");
        
        JLabel styleBoxLabel = builder.addLabel("", cl.xywh(labelColumn, currentRow, 1, 1),
                styleBox, cc.xywh(widgetColumn, currentRow, 1, 1));
        currentRow += 2;
        styleBoxLabel.setName("styleBoxLabel");
        LabelHandler.bindLabelFor(styleBoxLabel, styleBox);
        
        effectBox = new JComboBox();
        effectBox.setName("areaEffectBox");
        
        JLabel effectLabel = builder.addLabel("", cl.xywh(labelColumn, currentRow, 1, 1),
                effectBox, cc.xywh(widgetColumn, currentRow, 1, 1));
        currentRow += 2;
        effectLabel.setName("effectLabel");
        LabelHandler.bindLabelFor(effectLabel, effectBox);
        
        borderWidthSlider = new JSlider(0, 100, 0);
        borderWidthSlider.setPaintLabels(true);
        borderWidthSlider.setPaintTicks(true);
        borderWidthSlider.setMajorTickSpacing(50);
        
        JLabel insets = builder.addLabel("", cl.xywh(labelColumn, currentRow, 1, 1), borderWidthSlider,
                cc.xywh(widgetColumn, currentRow, 1, 1));
        insets.setName("borderWidthLabel");
        LabelHandler.bindLabelFor(insets, borderWidthSlider);
        currentRow += 2;
        
        paintStretchedBox = new JCheckBox();
        paintStretchedBox.setName("paintStretchedBox");
        builder.add(paintStretchedBox, cc.xywh(widgetColumn, currentRow, 1, 1));
        currentRow += 2;
        
        return painterControl;
    }
    
    /**
     * @return
     */
    private JXCollapsiblePane createBasePainterControlPanel() {
        JXCollapsiblePane painterControl = new JXCollapsiblePane();
        FormLayout formLayout = new FormLayout(
                "5dlu, r:d:n, l:4dlu:n, f:d:n", // columns
                "c:d:n " +
                ", t:4dlu:n, c:d:n " +
                ", t:4dlu:n, c:d:n" +
                ", t:4dlu:n, c:d:n" +
                ", t:4dlu:n, c:d:n"
                ); // rows
        PanelBuilder builder = new PanelBuilder(formLayout, painterControl);
        builder.setBorder(Borders.DLU4_BORDER);
        CellConstraints cl = new CellConstraints();
        CellConstraints cc = new CellConstraints();
        
        baseSeparator = new JXTitledSeparator();
        baseSeparator.setName("basePainterSeparator");
        builder.add(baseSeparator, cc.xywh(1, 1, 4, 1));
        
        int labelColumn = 2;
        int widgetColumn = labelColumn + 2;
        int currentRow = 3;
        interpolationBox = new JComboBox();
        interpolationBox.setName("interpolationBox");
        
        JLabel interpolationLabel = builder.addLabel("", cl.xywh(labelColumn, currentRow, 1, 1),
                interpolationBox, cc.xywh(widgetColumn, currentRow, 1, 1));
        currentRow += 2;
        interpolationLabel.setName("interpolationLabel");
        LabelHandler.bindLabelFor(interpolationLabel, interpolationBox);
        
        filterBox = new JComboBox();
        filterBox.setName("filterBox");
        
        JLabel filterLabel = builder.addLabel("", cl.xywh(labelColumn, currentRow, 1, 1),
                filterBox, cc.xywh(widgetColumn, currentRow, 1, 1));
        currentRow += 2;
        filterLabel.setName("filterLabel");
        LabelHandler.bindLabelFor(filterLabel, filterBox);
        
        
        visibleBox = new JCheckBox();
        visibleBox.setName("visibleBox");
        
        antialiasBox = new JCheckBox();
        antialiasBox.setName("antialiasBox");
        
        builder.add(visibleBox, cc.xywh(widgetColumn, currentRow, 1, 1));
        currentRow += 2;
        builder.add(antialiasBox, cc.xywh(widgetColumn, currentRow, 1, 1));
        currentRow +=2;
        
        return painterControl;
    }

    /**
     * @return
     */
    private JXCollapsiblePane createLayoutPainterControl() {
        JXCollapsiblePane painterControl = new JXCollapsiblePane();//new JXPanel();

        FormLayout formLayout = new FormLayout(
                "5dlu, r:d:n, l:4dlu:n, f:d:n, l:4dlu:n, f:d:n", // columns
                "c:d:n " +
                ", t:4dlu:n, c:d:n " +
                ", t:4dlu:n, c:d:n " +
                ", t:4dlu:n, c:d:n" +
                ", t:4dlu:n, c:d:n" +
                ", t:4dlu:n, c:d:n"
                ); // rows
        PanelBuilder builder = new PanelBuilder(formLayout, painterControl);
        builder.setBorder(Borders.DLU4_BORDER);
        CellConstraints cl = new CellConstraints();
        CellConstraints cc = new CellConstraints();

        
        layoutSeparator = new JXTitledSeparator();
        layoutSeparator.setName("layoutPainterSeparator");
        builder.add(layoutSeparator, cc.xywh(1, 1, 6, 1));

        int labelColumn = 2;
        int widgetColumn = labelColumn + 2;
        int currentRow = 3;
        
        horizontalAlignmentBox = new JComboBox();
        verticalAlignmentBox = new JComboBox();

        insetSlider = new JSlider(0, 100, 0);
        insetSlider.setPaintLabels(true);
        insetSlider.setPaintTicks(true);
        insetSlider.setMajorTickSpacing(20);
        
        fillHorizontal = new JCheckBox();
        fillHorizontal.setName("fillHorizontalBox");
        
        fillVertical = new JCheckBox();
        fillVertical.setName("fillVerticalBox");

        JLabel horAlign = builder.addLabel("", cl.xywh(labelColumn, currentRow, 1, 1),
                horizontalAlignmentBox, cc.xywh(widgetColumn, currentRow, 3, 1));
        currentRow += 2;
        horAlign.setName("horizontalAlignmentLabel");
        LabelHandler.bindLabelFor(horAlign, horizontalAlignmentBox);

        JLabel vertAlign = builder.addLabel("", cl.xywh(labelColumn, currentRow, 1, 1),
                verticalAlignmentBox, cc.xywh(widgetColumn, currentRow, 3, 1));
        currentRow += 2;
        vertAlign.setName("verticalAlignmentLabel");
        LabelHandler.bindLabelFor(vertAlign, verticalAlignmentBox);

        JLabel insets = builder.addLabel("", cl.xywh(labelColumn, currentRow, 1, 1), insetSlider,
                cc.xywh(widgetColumn, currentRow, 3, 1));
        currentRow += 2;
        insets.setName("insetsLabel");
        LabelHandler.bindLabelFor(insets, insetSlider);
        
        JLabel fillLabel = builder.addLabel("", cl.xywh(labelColumn, currentRow, 1, 1),
                fillHorizontal, cc.xywh(widgetColumn, currentRow, 1, 1));
        fillLabel.setName("fillLabel");
        LabelHandler.bindLabelFor(fillLabel, fillHorizontal);
        builder.add(fillVertical, cc.xywh(widgetColumn + 2, currentRow, 1, 1));
        currentRow += 2;
        return painterControl;
    }

    /**
     */
    private JXPanel createPainterDisplay() {
        JXPanel painterDisplay = new JXPanel(new BorderLayout());
        painterDisplay.setBorder(BorderFactory.createEtchedBorder());
        painterDisplay.setPaintBorderInsets(false);
        return painterDisplay;
    }


}
