/*
 * $Id: HighlighterTest.java 3989 2011-03-31 12:23:47Z kleopatra $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.decorator;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.plaf.ColorUIResource;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory.UIColorHighlighter;
import org.jdesktop.swingx.painter.AbstractAreaPainter;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.renderer.IconAware;
import org.jdesktop.swingx.renderer.JRendererLabel;
import org.jdesktop.swingx.renderer.StringValues;
import org.jdesktop.swingx.renderer.WrappingIconPanel;
import org.jdesktop.swingx.test.XTestUtils;
import org.jdesktop.test.ChangeReport;
import org.jdesktop.test.PropertyChangeReport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * 
 * Tests for Highlighters after overhaul.
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class HighlighterTest extends InteractiveTestCase {
    private static final Logger LOG = Logger.getLogger(HighlighterTest.class
            .getName());
    
    protected JLabel backgroundNull ;
    protected JLabel foregroundNull;
    protected JLabel allNull;
    protected JRendererLabel allColored;
    
    protected Color background = Color.RED;
    protected Color foreground = Color.BLUE;
    
    // JW: opaque Highlighter colors
    protected Color unselectedBackground = Color.CYAN;
    protected Color unselectedForeground = Color.GREEN;
    
    protected Color selectedBackground = Color.LIGHT_GRAY;
    protected Color selectedForeground = Color.MAGENTA;
    
    
    // JW: use these to expose Issue #1327-swingx:
    // Regression: ColorHighlighter must not blend by default
//    protected Color unselectedBackground = ColorUtil.setAlpha(Color.CYAN, 100);
//    protected Color unselectedForeground = ColorUtil.setAlpha(Color.GREEN, 100);
//    
//    protected Color selectedBackground = ColorUtil.setAlpha(Color.LIGHT_GRAY, 100);
//    protected Color selectedForeground = ColorUtil.setAlpha(Color.MAGENTA, 100);
    
    protected ColorHighlighter emptyHighlighter;
    // flag used in setup to explicitly choose LF
    protected boolean defaultToSystemLF;

    protected Font tableFont;
    @Override
    @Before
       public void setUp() {
        backgroundNull = new JLabel("test");
        backgroundNull.setForeground(foreground);
        backgroundNull.setBackground(null);
        
        foregroundNull = new JLabel("test");
        foregroundNull.setForeground(null);
        foregroundNull.setBackground(background);
        
        allNull = new JLabel("test");
        allNull.setForeground(null);
        allNull.setBackground(null);
        
        allColored = new JRendererLabel();
        allColored.setText("test");
        allColored.setForeground(foreground);
        allColored.setBackground(background);
        allColored.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        
        
        emptyHighlighter = new ColorHighlighter();
        
        tableFont = new JXTable().getFont();
        if (tableFont.equals(allColored.getFont())) {
            tableFont = tableFont.deriveFont(50.f);
        }
        // make sure we have the same default for each test
        defaultToSystemLF = false;
        InteractiveTestCase.setSystemLF(defaultToSystemLF);
    }

//-------------------PainterHighlighter
    
    @Test
     public void testPainterHighlighterConstructors() {
        PainterHighlighter hl = new PainterHighlighter();
        assertEquals(HighlightPredicate.ALWAYS, hl.getHighlightPredicate());
        assertNull(hl.getPainter());
        Painter<?> mattePainter = new MattePainter();
        PainterHighlighter withPainter = new PainterHighlighter(mattePainter);
        assertEquals(HighlightPredicate.ALWAYS, withPainter.getHighlightPredicate());
        assertEquals(mattePainter, withPainter.getPainter());
        PainterHighlighter withPredicate = new PainterHighlighter(HighlightPredicate.NEVER);
        assertEquals(HighlightPredicate.NEVER, withPredicate.getHighlightPredicate());
        assertEquals(null, withPredicate.getPainter());
        PainterHighlighter all = new PainterHighlighter(HighlightPredicate.NEVER, mattePainter);
        assertEquals(HighlightPredicate.NEVER, all.getHighlightPredicate());
        assertEquals(mattePainter, all.getPainter());
    }
    
    @Test
    public void testPainterHighlighterSetPainterChangeNotificatioon() {
        PainterHighlighter hl = new PainterHighlighter();
        ChangeReport report = new ChangeReport();
        hl.addChangeListener(report);
        MattePainter mattePainter = new MattePainter();
        hl.setPainter(mattePainter);
        assertEquals(mattePainter, hl.getPainter());
        assertEquals(1, report.getEventCount());
     }
    
    @Test
    public void testPainterHighlighterSetPainterNoChangeNotificatioon() {
        MattePainter mattePainter = new MattePainter();
        PainterHighlighter hl = new PainterHighlighter(mattePainter);
        ChangeReport report = new ChangeReport();
        hl.addChangeListener(report);
        hl.setPainter(mattePainter);
        assertEquals(0, report.getEventCount());
     }
    /**
     * Issue #851-swingx: Highlighter must notify on painter property change
     */
    @Test
    public void testPainterHighlighterPainterPropertyChangeNotificatioon() {
        PainterHighlighter hl = new PainterHighlighter();
        Color red = Color.RED;
        MattePainter mattePainter = new MattePainter(red);
        assertEquals(red, mattePainter.getFillPaint());
        PropertyChangeReport p = new PropertyChangeReport();
        mattePainter.addPropertyChangeListener(p);
        hl.setPainter(mattePainter);
        assertEquals(mattePainter, hl.getPainter());
        ChangeReport report = new ChangeReport();
        hl.addChangeListener(report);
        mattePainter.setFillPaint(Color.BLUE);
        assertEquals(p.getEventCount(), report.getEventCount());
     }

    /**
     * Issue #851-swingx: Highlighter must notify on painter property change.
     * 
     * Here: the Highlighter must not fire if the painter is changed during
     * the decoration. Supported in base to ease subclassing and keep
     * backward compatibility
     */
    @Test
    public void testPainterHighlighterPainterPropertyNotNotifyInternalChange() {
        Color red = Color.RED;
        MattePainter mattePainter = new MattePainter(red);
        assertEquals(red, mattePainter.getFillPaint());
        PainterHighlighter hl = new PainterHighlighter(mattePainter) {

            @Override
            protected Component doHighlight(Component component,
                    ComponentAdapter adapter) {
                ((AbstractAreaPainter<?>) getPainter()).setFillPaint(Color.BLUE);
                return super.doHighlight(component, adapter);
            }
            
        };
        ChangeReport report = new ChangeReport();
        hl.addChangeListener(report);
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        hl.highlight(allColored, adapter);
        assertEquals(Color.BLUE, mattePainter.getFillPaint());
        assertEquals(0, report.getEventCount());
     }

    @Test
    public void testPainterHighlighterUsePainter() {
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        MattePainter mattePainter = new MattePainter();
        PainterHighlighter hl = new PainterHighlighter(mattePainter);
        hl.highlight(allColored, adapter);
        // sanity 
        assertEquals(mattePainter, allColored.getPainter());
    }
    
    @Test
    public void testPainterHighlighterNotUseNullPainter() {
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        PainterHighlighter hl = new PainterHighlighter();
        MattePainter mattePainter = new MattePainter();
        allColored.setPainter(mattePainter);
        hl.highlight(allColored, adapter);
        // sanity 
        assertEquals(mattePainter, allColored.getPainter());
    }
    
    @Test
    public void testPainterHighlighterIgnoreNotPainterAware() {
        ComponentAdapter adapter = createComponentAdapter(foregroundNull, false);
        MattePainter mattePainter = new MattePainter();
        PainterHighlighter hl = new PainterHighlighter(mattePainter);
        hl.highlight(foregroundNull, adapter);
    }
    
//-------------------- factory
    
    /**
     * highlight every second 
     */
    @Test
    public void testSimpleStriping() {
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        Highlighter h = HighlighterFactory.createSimpleStriping(unselectedBackground);
        h.highlight(allColored, adapter);
        // no effect on first row
        assertEquals(background, allColored.getBackground());
        adapter.row = 1;
        h.highlight(allColored, adapter);
        assertEquals(unselectedBackground, allColored.getBackground());
    }
    

    /**
     * highlight every second 
     */
    @Test
    public void testAlternateStriping() {
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        Highlighter h = HighlighterFactory.createAlternateStriping(unselectedBackground, selectedBackground);
        h.highlight(allColored, adapter);
        // first color on first row
        assertEquals(unselectedBackground, allColored.getBackground());
        adapter.row = 1;
        h.highlight(allColored, adapter);
        // second color on second row
        assertEquals(selectedBackground, allColored.getBackground());
    }

//-------------- IconHighlighter

    @Test
    public void testIconHighlighterWithIconAwareTextField() {
        Icon icon = XTestUtils.loadDefaultIcon();
        IconAwareTextField field = new IconAwareTextField();
        IconHighlighter hl = new IconHighlighter(icon);
        ComponentAdapter adapter = createDummyComponentAdapter(field);
        assertNotNull(hl.highlight(field, adapter));
        assertEquals(icon, field.getIcon());
    }
    
    @Test
    public void testIconHighlighterWithWrappingIconPanel() {
        Icon icon = XTestUtils.loadDefaultIcon();
        WrappingIconPanel field = new WrappingIconPanel();
        IconHighlighter hl = new IconHighlighter(icon);
        ComponentAdapter adapter = createDummyComponentAdapter(field);
        assertNotNull(hl.highlight(field, adapter));
        assertEquals(icon, field.getIcon());
    }
    
    @Test
    public void testIconAwareTextField() {
        // sanity...
        Icon icon = XTestUtils.loadDefaultIcon();
        IconAwareTextField field = new IconAwareTextField();
        assertNull(field.getIcon());
        field.setIcon(icon);
        assertEquals(icon, field.getIcon());
    }
    
    /**
     * Test class implementing IconAware.
     */
    public static class IconAwareTextField extends JTextField implements IconAware {
        private Icon icon;
        
        @Override
        public Icon getIcon() {
            return icon;
        }

        @Override
        public void setIcon(Icon icon) {
            this.icon = icon;
        }
        
    }
    
    
    @Test
    public void testIconHighlighterNotHighlightUnable() {
        Icon icon = XTestUtils.loadDefaultIcon();
        JTextField field = new JTextField();
        IconHighlighter hl = new IconHighlighter(icon);
        ComponentAdapter adapter = createDummyComponentAdapter(field);
        assertNotNull(hl.highlight(field, adapter));
    }


    @Test
    public void testIconHighlighterNotHighlight() {
        Icon icon = XTestUtils.loadDefaultIcon();
        allColored.setIcon(icon);
        IconHighlighter hl = new IconHighlighter();
        assertNotNull(hl.highlight(allColored, createComponentAdapter(allColored)));
        assertEquals(icon, allColored.getIcon());
    }

    @Test
    public void testIconHighlighterHighlight() {
        Icon icon = XTestUtils.loadDefaultIcon();
        IconHighlighter hl = new IconHighlighter(icon);
        assertNotNull(hl.highlight(allColored, createComponentAdapter(allColored)));
        assertEquals(icon, allColored.getIcon());
    }
    
    @Test
    public void testIconHighlightIconChangeNotification() {
        IconHighlighter hl = new IconHighlighter();
        Icon icon = XTestUtils.loadDefaultIcon();
        ChangeReport report = new ChangeReport();
        hl.addChangeListener(report);
        hl.setIcon(icon);
        assertEquals(icon, hl.getIcon());
        assertEquals(1, report.getEventCount());
    }
    
    @Test
    public void testIconHighlightIconNoChangeNotification() {
        Icon icon = XTestUtils.loadDefaultIcon();
        IconHighlighter hl = new IconHighlighter(icon);
        ChangeReport report = new ChangeReport();
        hl.addChangeListener(report);
        hl.setIcon(icon);
        assertEquals(0, report.getEventCount());
    }
    
    @Test
    public void testIconHighlighterConstructors() {
        IconHighlighter empty = new IconHighlighter();
        assertIconHighlighter(empty, HighlightPredicate.ALWAYS, null);
        IconHighlighter never = new IconHighlighter(HighlightPredicate.NEVER);
        assertIconHighlighter(never, HighlightPredicate.NEVER, null);
        Icon icon = XTestUtils.loadDefaultIcon();
        IconHighlighter withIcon = new IconHighlighter(icon);
        assertIconHighlighter(withIcon, HighlightPredicate.ALWAYS, icon);
        IconHighlighter complete = new IconHighlighter(HighlightPredicate.NEVER, icon);
        assertIconHighlighter(complete, HighlightPredicate.NEVER, icon);
    }
    
    private void assertIconHighlighter(IconHighlighter hl, HighlightPredicate predicate, Icon icon) {
        assertEquals(predicate, hl.getHighlightPredicate());
        assertEquals(icon, hl.getIcon());
    }

//-------------- EnabledHighlighter
    
    @Test
    public void testOrientationHighlighterConstructors() {
        ComponentOrientationHighlighter hl = new ComponentOrientationHighlighter();
        assertSame(HighlightPredicate.ALWAYS, hl.getHighlightPredicate());
        assertEquals(true, hl.getComponentOrientation().isLeftToRight());
        hl = new ComponentOrientationHighlighter(ComponentOrientation.RIGHT_TO_LEFT);
        assertSame(HighlightPredicate.ALWAYS, hl.getHighlightPredicate());
        assertEquals(ComponentOrientation.RIGHT_TO_LEFT, hl.getComponentOrientation());
        hl = new ComponentOrientationHighlighter(HighlightPredicate.NEVER);
        assertSame(HighlightPredicate.NEVER, hl.getHighlightPredicate());
        assertEquals(true, hl.getComponentOrientation().isLeftToRight());
        hl = new ComponentOrientationHighlighter(HighlightPredicate.NEVER, ComponentOrientation.RIGHT_TO_LEFT);
        assertSame(HighlightPredicate.NEVER, hl.getHighlightPredicate());
        assertEquals(ComponentOrientation.RIGHT_TO_LEFT, hl.getComponentOrientation());
    }
    
    @Test
    public void testOrientationHighlighterSetCO() {
        ComponentOrientationHighlighter hl = new ComponentOrientationHighlighter();
        ChangeReport report = new ChangeReport();
        hl.addChangeListener(report);
        hl.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        assertEquals(ComponentOrientation.RIGHT_TO_LEFT, hl.getComponentOrientation());
        assertEquals(1, report.getEventCount());
        report.clear();
        hl.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        assertEquals(0, report.getEventCount());
    }
    
    @Test
    public void testOrientationHighlighterDecorate() {
        JSlider box = new JSlider();
        ComponentOrientation orientation = box.getComponentOrientation().isLeftToRight() ? 
                ComponentOrientation.RIGHT_TO_LEFT : ComponentOrientation.LEFT_TO_RIGHT;
        ComponentOrientationHighlighter hl = new ComponentOrientationHighlighter(orientation);
        hl.highlight(box, createDummyComponentAdapter(box));
        assertEquals(orientation, box.getComponentOrientation());
    }
    
//-------------- EnabledHighlighter
    
    @Test
    public void testEnabledHighlighterConstructors() {
        EnabledHighlighter hl = new EnabledHighlighter();
        assertSame(HighlightPredicate.ALWAYS, hl.getHighlightPredicate());
        assertEquals(false, hl.isEnabled());
        hl = new EnabledHighlighter(true);
        assertSame(HighlightPredicate.ALWAYS, hl.getHighlightPredicate());
        assertEquals(true, hl.isEnabled());
        hl = new EnabledHighlighter(HighlightPredicate.NEVER);
        assertSame(HighlightPredicate.NEVER, hl.getHighlightPredicate());
        assertEquals(false, hl.isEnabled());
        hl = new EnabledHighlighter(HighlightPredicate.NEVER, true);
        assertSame(HighlightPredicate.NEVER, hl.getHighlightPredicate());
        assertEquals(true, hl.isEnabled());
    }
    
    @Test
    public void testEnableHighlighterSetEnabled() {
        EnabledHighlighter hl = new EnabledHighlighter();
        ChangeReport report = new ChangeReport();
        hl.addChangeListener(report);
        hl.setEnabled(true);
        assertEquals(true, hl.isEnabled());
        assertEquals(1, report.getEventCount());
        report.clear();
        hl.setEnabled(true);
        assertEquals(0, report.getEventCount());
    }
    
    @Test
    public void testEnabledHighlighterDecorate() {
        EnabledHighlighter hl = new EnabledHighlighter();
        JSlider box = new JSlider();
        hl.highlight(box, createDummyComponentAdapter(box));
        assertEquals(false, box.isEnabled());
    }
   
    
//--------------AlignmentHighlighter  
    
    @Test
    public void testAlignmentHighlighterConstructors() {
        AlignmentHighlighter hl = new AlignmentHighlighter();
        assertSame(HighlightPredicate.ALWAYS, hl.getHighlightPredicate());
        assertEquals(SwingConstants.LEADING, hl.getHorizontalAlignment());
        hl = new AlignmentHighlighter(SwingConstants.TRAILING);
        assertSame(HighlightPredicate.ALWAYS, hl.getHighlightPredicate());
        assertEquals(SwingConstants.TRAILING, hl.getHorizontalAlignment());
        hl = new AlignmentHighlighter(HighlightPredicate.NEVER);
        assertSame(HighlightPredicate.NEVER, hl.getHighlightPredicate());
        assertEquals(SwingConstants.LEADING, hl.getHorizontalAlignment());
        hl = new AlignmentHighlighter(HighlightPredicate.NEVER, SwingConstants.TRAILING);
        assertSame(HighlightPredicate.NEVER, hl.getHighlightPredicate());
        assertEquals(SwingConstants.TRAILING, hl.getHorizontalAlignment());
    }
    
    @Test
    public void testAlignmentHighlighterSetAlignment() {
        AlignmentHighlighter hl = new AlignmentHighlighter();
        ChangeReport report = new ChangeReport();
        hl.addChangeListener(report);
        hl.setHorizontalAlignment(SwingConstants.TRAILING);
        assertEquals(1, report.getEventCount());
        assertEquals(SwingConstants.TRAILING, hl.getHorizontalAlignment());
        report.clear();
        hl.setHorizontalAlignment(SwingConstants.TRAILING);
        assertEquals(0, report.getEventCount());
    }
    
    
    @Test(expected = IllegalArgumentException.class)
    public void testAlignmentHighlighterSetAlignmentInvalid() {
        AlignmentHighlighter hl = new AlignmentHighlighter();
        hl.setHorizontalAlignment(SwingConstants.TOP);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testAlignmentHighlighterConstructorAlignmentInvalid() {
        new AlignmentHighlighter(SwingConstants.TOP);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testAlignmentHighlighterConstructorAlignmentInvalid2() {
        new AlignmentHighlighter(null, SwingConstants.TOP);
    }
    
    
    @Test
    public void testAlignmentHighlighterDecorateLabel() {
        AlignmentHighlighter hl = new AlignmentHighlighter(SwingConstants.TRAILING);
        hl.highlight(allColored, createComponentAdapter(allColored));
        assertEquals(SwingConstants.TRAILING, allColored.getHorizontalAlignment());
    }
    
    @Test
    public void testAlignmentHighlighterDecorateButton() {
        AlignmentHighlighter hl = new AlignmentHighlighter(SwingConstants.TRAILING);
        JCheckBox box = new JCheckBox("dummy");
        hl.highlight(box, createDummyComponentAdapter(box));
        assertEquals(SwingConstants.TRAILING, box.getHorizontalAlignment());
    }
    
    @Test
    public void testAlignmentHighlighterDecorateTextField() {
        AlignmentHighlighter hl = new AlignmentHighlighter(SwingConstants.TRAILING);
        JTextField box = new JTextField("dummy");
        hl.highlight(box, createDummyComponentAdapter(box));
        assertEquals(SwingConstants.TRAILING, box.getHorizontalAlignment());
    }
    
    
//-------------- FontHighlighter
    
    @Test
    public void testFontHighlighterConstructors() {
        FontHighlighter hl = new FontHighlighter();
        assertSame(HighlightPredicate.ALWAYS, hl.getHighlightPredicate());
        assertNull(hl.getFont());
        hl = new FontHighlighter(HighlightPredicate.NEVER);
        assertSame(HighlightPredicate.NEVER, hl.getHighlightPredicate());
        assertNull(hl.getFont());
        hl = new FontHighlighter(tableFont);
        assertSame(HighlightPredicate.ALWAYS, hl.getHighlightPredicate());
        assertNotNull(hl.getFont());
        assertEquals(tableFont, hl.getFont());
        hl = new FontHighlighter(HighlightPredicate.NEVER, tableFont);
        assertSame(HighlightPredicate.NEVER, hl.getHighlightPredicate());
        assertEquals(tableFont, hl.getFont());
    }
    
    @Test
    public void testFontHighlighterSetFont() {
        FontHighlighter hl = new FontHighlighter();
        ChangeReport report = new ChangeReport();
        hl.addChangeListener(report);
        hl.setFont(tableFont);
        assertEquals(1, report.getEventCount());
        assertEquals(tableFont, hl.getFont());
        report.clear();
        hl.setFont(tableFont);
        assertEquals(0, report.getEventCount());
    }
    
    @Test
    public void testFontHighlighterDecorate() {
        FontHighlighter hl = new FontHighlighter(tableFont);
        hl.highlight(allColored, createComponentAdapter(allColored));
        assertEquals(tableFont, allColored.getFont());
    }
    
    @Test
    public void testFontHighlighterDecorateNotWithNullFont() {
        FontHighlighter hl = new FontHighlighter();
        Font old = allColored.getFont();
        hl.highlight(allColored, createComponentAdapter(allColored));
        assertEquals(old, allColored.getFont());
    }
    
//-------------- ToolTipHighlighter
    
    @Test
    public void testToolTipHighlighterConstructors() {
        ToolTipHighlighter hl = new ToolTipHighlighter();
        assertSame(HighlightPredicate.ALWAYS, hl.getHighlightPredicate());
        assertNull(hl.getToolTipValue());
        hl = new ToolTipHighlighter(HighlightPredicate.NEVER);
        assertSame(HighlightPredicate.NEVER, hl.getHighlightPredicate());
        assertNull(hl.getToolTipValue());
        hl = new ToolTipHighlighter(StringValues.TO_STRING);
        assertSame(HighlightPredicate.ALWAYS, hl.getHighlightPredicate());
        assertNotNull(hl.getToolTipValue());
        assertEquals(StringValues.TO_STRING, hl.getToolTipValue());
        hl = new ToolTipHighlighter(HighlightPredicate.NEVER, StringValues.TO_STRING);
        assertSame(HighlightPredicate.NEVER, hl.getHighlightPredicate());
        assertEquals(StringValues.TO_STRING, hl.getToolTipValue());
    }
    
    @Test
    public void testFontHighlighterSetToolTipValue() {
        ToolTipHighlighter hl = new ToolTipHighlighter();
        ChangeReport report = new ChangeReport();
        hl.addChangeListener(report);
        hl.setToolTipValue(StringValues.TO_STRING);
        assertEquals(1, report.getEventCount());
        assertEquals(StringValues.TO_STRING, hl.getToolTipValue());
        report.clear();
        hl.setToolTipValue(StringValues.TO_STRING);
        assertEquals(0, report.getEventCount());
    }
    
    @Test
    public void testToolTipHighlighterDecorate() {
        ToolTipHighlighter hl = new ToolTipHighlighter(StringValues.EMPTY);
        ComponentAdapter adapter = createComponentAdapter(allColored);
        hl.highlight(allColored, adapter);
        assertEquals(StringValues.EMPTY.getString(adapter.getValue()), allColored.getToolTipText());
    }
    
    @Test
    public void testToolTipHighlighterDecorateWithNullStringValue() {
        ToolTipHighlighter hl = new ToolTipHighlighter();
        ComponentAdapter adapter = createComponentAdapter(allColored);
        hl.highlight(allColored, adapter);
        assertEquals(adapter.getString(), allColored.getToolTipText());
    }
    
    
//-------------- BorderHighlighter
    
    /**
     * Test that all properties have setters and fire a changeEvent. 
     */
    @Test
    public void testBorderHighlightSetters() {
        BorderHighlighter hl = new BorderHighlighter();
        ChangeReport report = new ChangeReport();
        hl.addChangeListener(report);
        hl.setInner(!hl.isInner());
        assertEquals(1, report.getEventCount());
        report.clear();
        hl.setCompound(!hl.isCompound());
        assertEquals(1, report.getEventCount());
    }
    
    /**
     * Test setBorder and fire a changeEvent.
     * (setter was missing) 
     */
    @Test
    public void testBorderHighlightSetBorder() {
        BorderHighlighter hl = new BorderHighlighter();
        ChangeReport report = new ChangeReport();
        hl.addChangeListener(report);
        Border padding = BorderFactory.createLineBorder(Color.RED, 3);
        hl.setBorder(padding);
        assertEquals("sanity: border set", padding, hl.getBorder());
        assertEquals("must fire on setting border", 1, report.getEventCount());
        report.clear();
        hl.setBorder(padding);
        assertEquals("must not fire on setting the same border", 0, report.getEventCount());
        hl.setBorder(null);
        assertEquals("must fire on setting border null", 1, report.getEventCount());
        report.clear();
        hl.setBorder(null);
        assertEquals("must not fire setting same null border", 0, report.getEventCount());
    }
    
    
    @Test
    public void testBorderPaddingNull() {
        BorderHighlighter empty = new BorderHighlighter();
        Border border = allColored.getBorder();
        empty.highlight(allColored, createComponentAdapter(allColored));
        assertEquals("borderHighlighter without padding must not change the component", 
                border, allColored.getBorder());
    }

    @Test
    public void testBorderPaddingCompoundNotInner() {
        Border padding = BorderFactory.createLineBorder(Color.RED, 3);
        BorderHighlighter empty = new BorderHighlighter(padding);
        empty.setInner(true);
        Border border = allColored.getBorder();
        empty.highlight(allColored, createComponentAdapter(allColored));
        Border compound = allColored.getBorder();
        assertTrue(compound instanceof CompoundBorder);
        assertEquals("borderHighlighter with padding and outer compound must have outside", 
                border, ((CompoundBorder) compound).getOutsideBorder());
        assertEquals("borderHighlighter with padding and outer compound must have inside", 
                padding, ((CompoundBorder) compound).getInsideBorder());
    }

    @Test
    public void testBorderPaddingCompoundInner() {
        Border padding = BorderFactory.createLineBorder(Color.RED, 3);
        BorderHighlighter empty = new BorderHighlighter(padding);
        Border border = allColored.getBorder();
        empty.highlight(allColored, createComponentAdapter(allColored));
        Border compound = allColored.getBorder();
        assertTrue(compound instanceof CompoundBorder);
        assertEquals("borderHighlighter with padding and outer compound must have outside", 
                padding, ((CompoundBorder) compound).getOutsideBorder());
        assertEquals("borderHighlighter with padding and outer compound must have inside", 
                border, ((CompoundBorder) compound).getInsideBorder());
    }
    
    @Test
    public void testBorderPaddingReplace() {
        Border padding = BorderFactory.createLineBorder(Color.RED, 3);
        BorderHighlighter empty = new BorderHighlighter(null, padding, false);
        empty.highlight(allColored, createComponentAdapter(allColored));
        assertEquals("borderHighlighter padding and not-compound must replace", 
                padding, allColored.getBorder());
    }
    
    @Test
    public void testBorderPaddingSetBorderIfComponentBorderNull() {
        Border padding = BorderFactory.createLineBorder(Color.RED, 3);
        BorderHighlighter empty = new BorderHighlighter(padding);
        allColored.setBorder(null);
        empty.highlight(allColored, createComponentAdapter(allColored));
        assertEquals("borderHighlighter padding and null component border must set", 
                padding, allColored.getBorder());
    }
    @Test
    public void testBorderConstructors() {
        BorderHighlighter empty = new BorderHighlighter();
        assertBorderHLState(empty, HighlightPredicate.ALWAYS, null, true, false);
        BorderHighlighter predicate = new BorderHighlighter(HighlightPredicate.NEVER);
        assertBorderHLState(predicate, HighlightPredicate.NEVER, null, true, false);
        Border padding = BorderFactory.createLineBorder(Color.RED, 3);
        BorderHighlighter paddingOnly = new BorderHighlighter(padding);
        assertBorderHLState(paddingOnly, HighlightPredicate.ALWAYS, padding, true, false);
        BorderHighlighter paddingPred = new BorderHighlighter(HighlightPredicate.NEVER, padding);
        assertBorderHLState(paddingPred, HighlightPredicate.NEVER, padding, true, false);
        BorderHighlighter paddingPredCompound = new BorderHighlighter(HighlightPredicate.NEVER, padding, false);
        assertBorderHLState(paddingPredCompound, HighlightPredicate.NEVER, padding, false, false);
        BorderHighlighter all = new BorderHighlighter(HighlightPredicate.NEVER, padding, false, true);
        assertBorderHLState(all, HighlightPredicate.NEVER, padding, false, true);
    }
    
    private void assertBorderHLState(BorderHighlighter hl, HighlightPredicate predicate, 
            Border border, boolean compound, boolean inner) {
        assertEquals(predicate, hl.getHighlightPredicate());
        assertEquals(border, hl.getBorder());
        assertEquals(compound, hl.isCompound());
        assertEquals(inner, hl.isInner());
    }

//------------------ ShadingColorHighlighter
    
    @Test
    public void testShadingConstructors() {
        ShadingColorHighlighter shading = new ShadingColorHighlighter();
        assertColorsAndPredicate(shading, HighlightPredicate.ALWAYS, null, null, null, null);
        ShadingColorHighlighter never = new ShadingColorHighlighter(HighlightPredicate.NEVER);
        assertColorsAndPredicate(never, HighlightPredicate.NEVER, null, null, null, null);
    }
    
    @Test
    public void testShadingEffect() {
        
    }
//----------------------- Colorhighlighter constructors
 
    /**
     * Test constructor of ColorHighlighter.
     */
    @Test
    public void testConstructors() {
        ColorHighlighter empty = new ColorHighlighter();
        assertColorsAndPredicate(empty, HighlightPredicate.ALWAYS, null, null, null, null);
        ColorHighlighter predicateOnly = new ColorHighlighter(HighlightPredicate.NEVER);
        assertColorsAndPredicate(predicateOnly, HighlightPredicate.NEVER, null, null, null, null);
        ColorHighlighter normal = new ColorHighlighter(background, foreground);
        assertColorsAndPredicate(normal, HighlightPredicate.ALWAYS, background, foreground, null, null);
        ColorHighlighter normalNever = new ColorHighlighter(HighlightPredicate.NEVER, background, 
                foreground);
        assertColorsAndPredicate(normalNever, HighlightPredicate.NEVER, background, foreground, null, null);
        ColorHighlighter full = new ColorHighlighter(background, foreground, 
                selectedBackground , selectedForeground);
        assertColorsAndPredicate(full, HighlightPredicate.ALWAYS, background, foreground, selectedBackground, selectedForeground);
        ColorHighlighter fullNever = new ColorHighlighter(HighlightPredicate.NEVER, background, 
                foreground , selectedBackground, selectedForeground);
        assertColorsAndPredicate(fullNever, HighlightPredicate.NEVER, background, foreground, selectedBackground, selectedForeground);
    }
    
    private void assertColorsAndPredicate(ColorHighlighter highlighter, HighlightPredicate predicate, Color background,
            Color foreground, Color  selectedBackground, Color selectedForeground) {
        assertEquals("background", background, highlighter.getBackground());
        assertEquals("foreground", foreground, highlighter.getForeground());
        assertEquals("selectedbackground", selectedBackground, highlighter.getSelectedBackground());
        assertEquals("selectedForeground", selectedForeground, highlighter.getSelectedForeground());
        assertEquals("predicate", predicate, highlighter.getHighlightPredicate());
    }
//----------------- testing change notification ColorHighlighter

    /**
     * Test ui dependent color is updated.
     */
    @Test
    public void testCustomUIColorHighlighter() {
        UIColorHighlighter h = new UIColorHighlighter();
        Color uiBackground = h.getBackground();
        Color uiColor = UIManager.getColor("UIColorHighlighter.stripingBackground");
        // very unusual ui striping
        Color color = new ColorUIResource(Color.BLACK);
        if (color.equals(uiBackground)) {
            LOG.info("cannot run testUIColorHighlight - ui striping same as test color");
            return;
        }
        UIManager.put("UIColorHighlighter.stripingBackground", color);
        try {
            h.updateUI();
            assertEquals(color, h.getBackground());
        } finally {
            // remove custom setting. DO NOT set the old uiColor manually!
            UIManager.put("UIColorHighlighter.stripingBackground", null);
        }
        // sanity - reset
        h.updateUI();
        assertNotNull(h.getBackground());
        assertNotSame(color.getRGB(), h.getBackground().getRGB());
        // can be generic grey as well (as per HighLighterFactory treatment of cases with no LAF defined)
        boolean found = h.getBackground().equals( uiColor) || h.getBackground().equals(HighlighterFactory.GENERIC_GRAY);
        assertTrue("Found " + h.getBackground() + " while expected " + uiColor, found);
    }

    
    
    @Test
    public void testColorHighlighterChangeNotification() {
        ColorHighlighter highlighter = new ColorHighlighter();
        assertBaseHighlighterChange(highlighter, 1);
    }

    @Test
    public void testColorHighlighterNoChangeNotification() {
        ColorHighlighter highlighter = new ColorHighlighter(HighlightPredicate.NEVER, 
                Color.RED, Color.RED, Color.RED, Color.RED);
        assertBaseHighlighterChange(highlighter, 0);
    }
    
    private void assertBaseHighlighterChange(ColorHighlighter highlighter,  int count ) {
        ChangeReport changeReport = new ChangeReport();
        highlighter.addChangeListener(changeReport);
        highlighter.setBackground(Color.red);
        assertEquals("event count ", count,  changeReport.getEventCount() );
        changeReport.clear();
        highlighter.setForeground(Color.red);
        assertEquals("event count ", count,  changeReport.getEventCount() );
        changeReport.clear();
        highlighter.setSelectedBackground(Color.red);
        assertEquals("event count ", count,  changeReport.getEventCount() );
        changeReport.clear();
        highlighter.setSelectedForeground(Color.red);
        assertEquals("event count ", count,  changeReport.getEventCount() );
        changeReport.clear();
        highlighter.setHighlightPredicate(HighlightPredicate.NEVER);
        assertEquals("event count ", count, changeReport.getEventCount());
    }
    
    /**
     * test that the base implementation is polite and fires only if 
     * the predicate is really changed.
     */
    @Test
    public void testHighlightPredicateNoChangeNotification() {
        AbstractHighlighter hl = new AbstractHighlighter() {

            @Override
            protected Component doHighlight(Component component,
                    ComponentAdapter adapter) {
                return component;
            }
            
        };
        ChangeReport report = new ChangeReport();
        hl.addChangeListener(report);
        hl.setHighlightPredicate(HighlightPredicate.ALWAYS);
        assertEquals("must not fire on setting same predicate", 0, report.getEventCount());
        report.clear();
        hl.setHighlightPredicate(null);
        assertEquals("must not fire on setting same predicate", 0, report.getEventCount());
        
    }
//---------------------- exposing highlighter probs with null component color
    
    @Test
    public void testLabelSanity() {
        assertNull("foreground must be null", foregroundNull.getForeground());
        assertNotNull("background must not be null", foregroundNull.getBackground());
        assertNull("background must be null", backgroundNull.getBackground());
        assertNotNull("foreground must not be null", backgroundNull.getForeground());
        assertNull("foreground must be null", allNull.getForeground());
        assertNull("background must be null", allNull.getBackground());
        assertEquals(background, allColored.getBackground());
        assertEquals(foreground, allColored.getForeground());
    }

    // --------------------- highlightPredicate
    
    /**
     * test predicate defaults to always
     */
    @Test
    public void testDefaultPredicate() {
        ColorHighlighter highlighter = new ColorHighlighter();
        assertSame(HighlightPredicate.ALWAYS, highlighter.getHighlightPredicate());
    }
    
    /**
     * test highlight respects predicate never.
     *
     */
    @Test
    public void testHighlightPredicate() {
        ColorHighlighter highlighter = new ColorHighlighter(unselectedBackground, 
                unselectedForeground);
        highlighter.setHighlightPredicate(HighlightPredicate.NEVER);
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        highlighter.highlight(allColored, adapter);
        // assert unchanged colors
        assertEquals(foreground, allColored.getForeground());
        assertEquals(background, allColored.getBackground());
    }
    //------------------------- test highlight effects ColorHighlighter
    
    /**
     * Test ColorHighlighter decorate unselected cell.
     *
     */
    @Test
    public void testApplyColorHighlighterUnselected() {
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        ColorHighlighter highlighter = new ColorHighlighter(unselectedBackground, 
                unselectedForeground, selectedBackground, selectedForeground);
        assertApplied(highlighter, allColored, adapter);
    }
    
    /**
     * Test ColorHighlighter decorate selected cell.
     *
     */
    @Test
    public void testApplyColorHighlighterSelected() {
        ComponentAdapter adapter = createComponentAdapter(allColored, true);
        ColorHighlighter highlighter = new ColorHighlighter(unselectedBackground, 
                unselectedForeground, selectedBackground, selectedForeground);
        assertApplied(highlighter, allColored, adapter);
    }
    
    /**
     * test that same renderer is returned.
     *
     */
    @Test
    public void testReturnHighlightedComponent() {
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        ColorHighlighter highlighter = new ColorHighlighter(unselectedBackground, 
                unselectedForeground);
        assertSame(allColored, highlighter.highlight(allColored, adapter));
    }
    
    /**
     * Issue #178-swingx: Highlighters always change the selection color.
     * sanity test to see if non-selected colors are unchanged.
     */
    @Test
    public void testUnSelectedDoNothingHighlighter() {
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        assertApplied(emptyHighlighter, allColored, adapter);
    }
    /**
     * Issue #178-swingx: Highlighters always change the selection color.
     */
    @Test
    public void testSelectedDoNothingHighlighter() {
        ComponentAdapter adapter = createComponentAdapter(allColored, true);
        assertApplied(emptyHighlighter, allColored, adapter);
    }

    
    /**
     * running assertion for all highlighter colors, depending on selection of adapter and
     * colors set/not set in highlighter.
     * 
     * @param highlighter
     * @param label
     * @param adapter
     */
    protected void assertApplied(ColorHighlighter highlighter, Component label, ComponentAdapter adapter) {
        Color labelForeground = label.getForeground();
        Color labelBackground = label.getBackground();
        highlighter.highlight(label, adapter);
        if (!adapter.isSelected()) {
            if (highlighter.getBackground() == null) {
                assertEquals("unselected: background must not be changed", labelBackground, label.getBackground());
            } else {
                assertEquals("unselected: background must be changed", highlighter.getBackground(), label.getBackground());
            }
            if (highlighter.getForeground() == null) {
                assertEquals("unselected: forground must not be changed", labelForeground, label.getForeground());
            } else {
                assertEquals("unselected: forground must be changed", highlighter.getForeground(), label.getForeground());
            }
        } else {
            if (highlighter.getSelectedBackground() == null) {
                assertEquals("selected: background must not be changed", labelBackground, label.getBackground());
            } else {
                assertEquals("selected: background must be changed", highlighter.getSelectedBackground(), label.getBackground());
            }
            if (highlighter.getSelectedForeground() == null) {
                assertEquals("selected: forground must not be changed", labelForeground, label.getForeground());
            } else {
                assertEquals("selected: forground must be changed", highlighter.getSelectedForeground(), label.getForeground());
            }
            
        }
    } 

//------------------ UIDependent
    
    /**
     * test that the ui color highlighter comes up with 
     * the ui-setting.
     */
    @Test
    public void testInitialUIColorHighlighter() {
        ColorHighlighter h = new UIColorHighlighter();
        Color uiBackground = h.getBackground();
        Color uiColor = UIManager.getColor("UIColorHighlighter.stripingBackground");
        if (uiColor == null) {
            LOG.info("cannot test initial ui striping color - UIManager has null");
        }
        assertEquals(uiColor, uiBackground);
    }
    
    // --------------------- factory methods
    
    /**
     * Creates and returns a ComponentAdapter on the given 
     * label with the unselected state.
     * 
     * @param label
     * @param selected
     * @return
     */
    protected ComponentAdapter createComponentAdapter(final JLabel label) {
        return createComponentAdapter(label, false);
    }   
    
    /**
     * Creates and returns a ComponentAdapter on the given 
     * label with the specified selection state.
     * 
     * @param label
     * @param selected
     * @return
     */
    protected ComponentAdapter createComponentAdapter(final JLabel label, final boolean selected) {
        ComponentAdapter adapter = new ComponentAdapter(label) {

            @Override
            public Object getValueAt(int row, int column) {
                return label.getText();
            }

            @Override
            public Object getFilteredValueAt(int row, int column) {
                return getValueAt(row, column);
            }

            @Override
            public Object getValue() {
                return getValueAt(row, column);
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean hasFocus() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean isEditable() {
                return false;
            }
            
            @Override
            public boolean isSelected() {
                return selected;
            }

            @Override
            public String getColumnName(int columnIndex) {
                return null;
            }

            
        };
        return adapter;
    }
 
    private ComponentAdapter createDummyComponentAdapter(JComponent allColored) {
        ComponentAdapter adapter = new ComponentAdapter(allColored) {

            @Override
            public Object getFilteredValueAt(int row, int column) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Object getValueAt(int row, int column) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public boolean hasFocus() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean isEditable() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean isSelected() {
                // TODO Auto-generated method stub
                return false;
            }
        };
        return adapter;
    }

}
