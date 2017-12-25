/*
 * $Id: JXTitledPanelTest.java 4082 2011-11-15 18:39:43Z kschaefe $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.util.PaintUtils;
import org.jdesktop.test.PropertyChangeReport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class JXTitledPanelTest extends InteractiveTestCase {
    private static final Logger LOG = Logger.getLogger(JXTitledPanelTest.class
            .getName());
    
    // flag used in setup to explicitly choose LF
    private boolean defaultToSystemLF;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // make sure we have the same default for each test
        defaultToSystemLF = false;
        setSystemLF(defaultToSystemLF);
    }

    public JXTitledPanelTest() {
        super("JXTitledPane interactive test");
    }

    /**
     * Sanity: changed ui-delegate to install the opaqueness via LookAndFeel. Doing so
     * is the equivalent of UIResource for primitive types.
     */
    @Test
    public void testOpaque() {
        JXTitledPanel titledPanel = new JXTitledPanel();
        boolean opaque = titledPanel.isOpaque(); 
        titledPanel.setOpaque(!opaque);
        assertEquals("sanity: opaqueness toggled: ", !opaque, titledPanel.isOpaque());
        titledPanel.updateUI();
        assertEquals("ui must not overwrite custom setting: ", !opaque, titledPanel.isOpaque());
    }
    /**
     * Issue #1063-swingx: JXTitledPanel must not overwrite custom border
     */
    @Test
    public void testBorderOnLFChange() {
        JXTitledPanel titledPanel = new JXTitledPanel();
        assertTrue("sanity: titledPanels default border must be ui-installable " + titledPanel.getBorder(), 
                SwingXUtilities.isUIInstallable(titledPanel.getBorder()));
        LineBorder border = new LineBorder(titledPanel.getBackground());
        titledPanel.setBorder(border);
        assertEquals("sanity: border set", border, titledPanel.getBorder());
        titledPanel.updateUI();
        assertEquals("border untouched ", border, titledPanel.getBorder());
    }
    
    @Test
    public void testLayoutOnLFChange() {
        JXTitledPanel titledPanel = new JXTitledPanel();
        assertNotNull(titledPanel.getContentContainer());
        titledPanel.getContentContainer().setLayout(new BorderLayout());
        String lf = UIManager.getLookAndFeel().getName();
        setSystemLF(!defaultToSystemLF);
        if (lf.equals(UIManager.getLookAndFeel().getName())) {
            LOG.info("cannot run layoutOnLFChange - equal LF" + lf);
            return;
        }
        SwingUtilities.updateComponentTreeUI(titledPanel);
        assertTrue(titledPanel.getContentContainer().getLayout() instanceof BorderLayout);
    }
    
    /**
     * incorrect propertyChangeEvent on setTitle(null).
     *
     */
    @Test
    public void testTitleNotifyPropertyValue() {
        String title = "starting title";
        final JXTitledPanel panel = new JXTitledPanel(title);
        PropertyChangeReport report = new PropertyChangeReport();
        panel.addPropertyChangeListener( "title", report);
        panel.setTitle(null);
        assertTrue("panel must have fired propertyChange", report.hasEvents());
        assertEquals("new property value must be equal to getTitle", panel.getTitle(),
                report.getLastNewValue("title"));
        
    }

//--------------------- interactive tests
 
    
    /**
     * Issue #157-swingx: JXTitledPanel not fully bidi-compliant.
     * 
     * Last issue: gradient not adjusted - should be handled by 
     * the painter. See Issue #1163-swingx
     */
    public void interactiveGradient() {
        GradientPaint paint = new GradientPaint(0f, 0f, Color.WHITE, 500f, 
                500f, Color.BLUE);
        JXTitledPanel panel = new JXTitledPanel("want a gradient");
        panel.setTitlePainter(new MattePainter(paint, true));
//        ImagePainter p = new ImagePainter(XTestUtils.loadDefaultImage());
//        p.setScaleType(ScaleType.OutsideFit);
//        p.setHorizontalAlignment(HorizontalAlignment.LEFT);
//        panel.setTitlePainter(p);
        JXFrame frame = wrapInFrame(panel, "gradient");
        addComponentOrientationToggle(frame);
        show(frame, 500, 500);
    }

    /**
     * Issue #1063-swingx: JXTitledPanel must respect custom border.
     */
    public void interactiveBorderOnUpdateUI() {
        final JXTitledPanel titledPanel = new JXTitledPanel("Search");
        titledPanel.setBorder(new LineBorder(titledPanel.getBackground()));
        showInFrame(titledPanel, "border on laf change");
    }

    public  void interactiveRToL() {
        String title = "starting title";
        JXTitledPanel titledPane = new JXTitledPanel(title);
        titledPane.setLeftDecoration(new JLabel("Leading"));
        titledPane.setRightDecoration(new JLabel("Trailing"));
//        panel.getContentContainer().setLayout(new BoxLayout(panel.getContentContainer(), BoxLayout.PAGE_AXIS));
        Icon icon = new ImageIcon(getClass().getResource("resources/images/wellBottom.gif"));
        final JLabel label = new JLabel(title);
        label.setIcon(icon);
        final JPanel panel = new JPanel(new BorderLayout());
        panel.add(titledPane, BorderLayout.NORTH);
        panel.add(label);
        JXFrame frame = wrapInFrame(panel, "toggle Title");
        Action toggleCO = new AbstractAction("toggle orientation") {


                public void actionPerformed(ActionEvent e) {
                    ComponentOrientation current = panel.getComponentOrientation();
                    if (current == ComponentOrientation.LEFT_TO_RIGHT) {
                        panel.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                        label.setText("RightToLeft");
                    } else {
                        panel.applyComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
                        label.setText("LeftToRight");

                    }

                }
                
            
        };
        addAction(frame, toggleCO);
        frame.pack();
        frame.setVisible(true);

    }
    

    public  void interactiveIconAndHtmlTest() {
        String title = "<html><u>starting title </u></html>";
        final JXTitledPanel panel = new JXTitledPanel(title);
        Icon icon = new ImageIcon(getClass().getResource("resources/images/wellBottom.gif"));
        panel.setLeftDecoration(new JLabel(icon));
        panel.getContentContainer().setLayout(new BoxLayout(panel.getContentContainer(), BoxLayout.Y_AXIS));
        panel.getContentContainer().add(new JLabel(title));
        JXFrame frame = wrapInFrame(panel, "toggle Title");
        frame.setVisible(true);

    }
    
    /**
     * trying to set divers TitledPanel properties interactively.
     * can't set titlefont.
     */
    public void interactiveTitleTest() {
        String title = "starting title";
        final JXTitledPanel panel = new JXTitledPanel(title);
        panel.getContentContainer().setLayout(new BoxLayout(panel.getContentContainer(), BoxLayout.Y_AXIS));
        Action toggleLight = new AbstractAction("toggle lightBackground") {
            public void actionPerformed(ActionEvent e) {
                LOG.info("JXTitledPanelText must be updated to new api");

//                Color oldFont = panel.getTitleLightBackground();
//                panel.setTitleLightBackground(oldFont.darker());
            }
            
        };

        panel.getContentContainer().add(new JButton(toggleLight));
        panel.getContentContainer().setLayout(new BoxLayout(panel.getContentContainer(), BoxLayout.Y_AXIS));
        Action toggleDark = new AbstractAction("toggle darkbackground") {
            public void actionPerformed(ActionEvent e) {
                LOG.info("JXTitledPanelText must be updated to new api");
//                Color oldFont = panel.getTitleDarkBackground();
//                panel.setTitleDarkBackground(oldFont.darker());
                
            }
            
        };

        panel.getContentContainer().add(new JButton(toggleDark));
        Action toggleForeground = new AbstractAction("toggle Foreground") {
            public void actionPerformed(ActionEvent e) {
                Color oldColor = panel.getTitleForeground();
                panel.setTitleForeground(oldColor.darker());
                
            }
            
        };

        panel.getContentContainer().add(new JButton(toggleForeground));
        Action toggleFont = new AbstractAction("toggle Font") {
            public void actionPerformed(ActionEvent e) {
                Font oldFont = panel.getTitleFont();
                System.out.println("oldfont size: " + oldFont.getSize());
                panel.setTitleFont(oldFont.deriveFont(oldFont.getSize()*2.f));
                
            }
            
        };

        panel.getContentContainer().add(new JButton(toggleFont));
        Action toggleTitle = new AbstractAction("toggle title") {
            int count = 0;
            public void actionPerformed(ActionEvent e) {
                panel.setTitle(" * " + count++ + " title");
                
            }
            
        };

        panel.getContentContainer().add(new JButton(toggleTitle));
        JFrame frame = wrapInFrame(panel, "toggle Title");
        frame.setVisible(true);
    }
 
    /**
     * Issue #1223-swingx: title looks ugly with rounded border.
     * 
     */
    public void interactiveRoundedBorder() {
        final JXTitledPanel titledPanel = new JXTitledPanel("Search");
        titledPanel.setBorder(new RoundedBorder(50));
        JXFrame frame = wrapInFrame(titledPanel, "Rounded border?");
        show(frame, 400, 400);
    }

    /**
     * This is the implementation from swinglabs demos (originally 
     * swingset3): while it be extreme in declaring all the vertical
     * extend of the complete corner as border insets, the general 
     * problem remains in every distribution. Fitting a square
     * titleBar into round corners isn't easily possible ;-)
     * 
     * see forum thread:
     * http://forums.java.net/jive/thread.jspa?threadID=69440&tstart=0
     */
    public static class RoundedBorder implements Border {    
        private int cornerRadius;
        
        public RoundedBorder() {
            this(10);
        }
        
        public RoundedBorder(int cornerRadius) {
            this.cornerRadius = cornerRadius;
        }

        public Insets getBorderInsets(Component c) {
            return getBorderInsets(c, new Insets(0,0,0,0));
        }

        public Insets getBorderInsets(Component c, Insets insets) {
            insets.top = insets.bottom = cornerRadius/2; 
            insets.left = insets.right = 1;
            return insets;
        }

        public boolean isBorderOpaque() {
            return false;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                    RenderingHints.VALUE_ANTIALIAS_ON);
            
            Color color = Color.RED;
            g2.setColor(PaintUtils.setAlpha(color, 40));        
            g2.drawRoundRect(x, y + 2, width - 1, height - 3, cornerRadius, cornerRadius);
            g2.setColor(PaintUtils.setAlpha(color, 90));        
            g2.drawRoundRect(x, y + 1, width - 1, height - 2, cornerRadius, cornerRadius); 
            g2.setColor(PaintUtils.setAlpha(color, 255));        
            g2.drawRoundRect(x, y, width - 1, height - 1, cornerRadius, cornerRadius);

            g2.dispose();            
        }
    }

    public static void main(String args[]) {
        JXTitledPanelTest test = new JXTitledPanelTest();
        try {
          test.runInteractiveTests("interactiveRoundedBorder");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        } 
    }
}
