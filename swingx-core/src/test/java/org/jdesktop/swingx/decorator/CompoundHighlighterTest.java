/*
 * $Id: CompoundHighlighterTest.java 3877 2010-11-03 11:36:33Z kleopatra $
 *
 * Copyright 2007 Sun Microsystems, Inc., 4150 Network Circle,
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
 *
 */
package org.jdesktop.swingx.decorator;

import java.awt.Color;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.UIManager;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.renderer.JRendererLabel;
import org.jdesktop.test.ChangeReport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test CompoundHighlighter.
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class CompoundHighlighterTest extends InteractiveTestCase {
    
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(CompoundHighlighterTest.class.getName());
    
    protected JLabel backgroundNull ;
    protected JLabel foregroundNull;
    protected JLabel allNull;
    protected JRendererLabel allColored;
    
    protected Color background = Color.RED;
    protected Color foreground = Color.BLUE;
    
    protected Color unselectedBackground = Color.CYAN;
    protected Color unselectedForeground = Color.GREEN;
    
    protected Color selectedBackground = Color.LIGHT_GRAY;
    protected Color selectedForeground = Color.MAGENTA;
    
    protected ColorHighlighter emptyHighlighter;
    // flag used in setup to explicitly choose LF
    protected boolean defaultToSystemLF;

    @Override
    protected void setUp() {
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
        // make sure we have the same default for each test
        defaultToSystemLF = false;
        setSystemLF(defaultToSystemLF);
    }


    /**
     * Test that the client is messaged on change to a managed Highlighter.
     */
    @Test
    public void testUpdateUI() {
        CompoundHighlighter support = new CompoundHighlighter();
        // force loading of striping colors
        ColorHighlighter colorHighlighter = (ColorHighlighter) HighlighterFactory.createSimpleStriping();
        Color uiColor = UIManager.getColor("UIColorHighlighter.stripingBackground");
        if (uiColor == null) {
            LOG.info("cannot run test - no ui striping color");
            return;
        }
        assertSame("sanity", uiColor, colorHighlighter.getBackground());
        support.addHighlighter(colorHighlighter);
        Color changedUIColor = Color.RED;
        UIManager.put("UIColorHighlighter.stripingBackground", changedUIColor);
        support.updateUI();
        try {
            assertSame("support must update ui color", changedUIColor, colorHighlighter.getBackground());
        } finally {
            UIManager.put("UIColorHighlighter.stripingBackground", uiColor);
        }
        
    }

    /**
     * 
     * Test that setting zero highlighter removes all.
     */
    @Test
    public void testSetHighlightersReset() {
        CompoundHighlighter support = new CompoundHighlighter();
        support.addHighlighter(new ColorHighlighter());
        // sanity
        assertEquals(1, support.getHighlighters().length);
        support.setHighlighters();
        assertEquals(0, support.getHighlighters().length);
    }

    /**
     * 
     * Test that setting zero highlighter removes all.
     */
    @Test
    public void testSetHighlightersResetRemoveListeners() {
        CompoundHighlighter support = new CompoundHighlighter();
        ColorHighlighter colorHighlighter = new ColorHighlighter();
        support.addHighlighter(colorHighlighter);
        // sanity
        assertEquals(1, support.getHighlighters().length);
        ChangeReport report = new ChangeReport();
        support.addChangeListener(report);
        support.setHighlighters();
        assertEquals(0, support.getHighlighters().length);
        assertEquals("compound must fire on modification", 1, report.getEventCount());
        report.clear();
        colorHighlighter.setBackground(Color.RED);
        assertEquals("compound must have removed listener", 0, report.getEventCount());
    }

    /**
     * 
     * Test that setting zero highlighter removes all.
     */
    @Test
    public void testSetHighlightersResetSingleEvent() {
        ColorHighlighter colorHighlighter = new ColorHighlighter();
        CompoundHighlighter support = new CompoundHighlighter(colorHighlighter, new ColorHighlighter());
        // sanity
        assertEquals(2, support.getHighlighters().length);
        ChangeReport report = new ChangeReport();
        support.addChangeListener(report);
        support.setHighlighters();
        assertEquals(0, support.getHighlighters().length);
        assertEquals("compound must fire on modification", 1, report.getEventCount());
    }
    
    /**
     * 
     * Test that setting zero highlighter removes all.
     */
    @Test
    public void testSetHighlightersSingleEvent() {
        ColorHighlighter colorHighlighter = new ColorHighlighter();
        CompoundHighlighter support = new CompoundHighlighter();
        ChangeReport report = new ChangeReport();
        support.addChangeListener(report);
        support.setHighlighters(colorHighlighter, new ColorHighlighter());
        assertEquals(2, support.getHighlighters().length);
        assertEquals("compound must fire on modification", 1, report.getEventCount());
    }
    
    /**
     * 
     * Test that setting zero Highlighters on empty compound does not fire.
     */
    @Test
    public void testSetHighlightersResetEmptyNoEvent() {
        CompoundHighlighter support = new CompoundHighlighter();
        ChangeReport report = new ChangeReport();
        support.addChangeListener(report);
        support.setHighlighters();
        assertEquals(0, support.getHighlighters().length);
        assertEquals("compound must not fire without modification", 0, report.getEventCount());
    }
    /**
     * Sanity: handles empty array.
     */
    @Test
    public void testSetHighlightersEmptyArray() {
        CompoundHighlighter support = new CompoundHighlighter();
        support.setHighlighters(new Highlighter[] {});
        assertEquals(0, support.getHighlighters().length);
        assertSame(CompoundHighlighter.EMPTY_HIGHLIGHTERS, support.getHighlighters());
    }
    
    /**
     * test if removeHighlighter behaves as doc'ed.
     *
     */
    @Test
    public void testTableRemoveHighlighter() {
        CompoundHighlighter support = new CompoundHighlighter();
        // test cope with null
        support.removeHighlighter(null);
        Highlighter presetHighlighter = new ColorHighlighter();
        support.setHighlighters(presetHighlighter);
        Highlighter[] highlighters = support.getHighlighters();
        // sanity
        assertEquals(1, highlighters.length);
        // remove uncontained
        support.removeHighlighter(new ColorHighlighter());
        // assert no change
        assertSameContent(highlighters, support.getHighlighters());
        support.removeHighlighter(presetHighlighter);
        assertEquals(0, support.getHighlighters().length);
    }


    /**
     * test if addHighlighter behaves as doc'ed.
     *
     */
    @Test
    public void testTableAddHighlighter() {
        CompoundHighlighter support = new CompoundHighlighter();
        Highlighter presetHighlighter = new ColorHighlighter();
        // add the first
        support.addHighlighter(presetHighlighter);
        // assert that it is added
        assertEquals(1, support.getHighlighters().length);
        assertAsLast(support.getHighlighters(), presetHighlighter);
        Highlighter highlighter = new ColorHighlighter();
        // add the second
        support.addHighlighter(highlighter);
        assertEquals(2, support.getHighlighters().length);
        // assert that it is appended
        assertAsLast(support.getHighlighters(), highlighter);
    }

    /**
     * Test strict enforcement of not null allowed in setHighlighters.
     */
    @Test
    public void testSetHighlightersNull() {
        CompoundHighlighter table = new CompoundHighlighter();
        try {
            table.setHighlighters((Highlighter) null);
            fail("illegal to call setHighlighters(null)");
            
        } catch (NullPointerException e) {
            // expected
        }
    }


    /**
     * Test strict enforcement of not null allowed in setHighlighters.
     */
    @Test
    public void testSetHighlightersWithNullArray() {
        CompoundHighlighter table = new CompoundHighlighter();
        try {
            table.setHighlighters((Highlighter[]) null);
            fail("illegal to call setHighlighters(null)");
            
        } catch (NullPointerException e) {
            // expected
        }
    }

    
    /**
     * Test strict enforcement of not null allowed in setHighlighters.
     */
    @Test
    public void testSetHighlightersArrayNullElement() {
        CompoundHighlighter table = new CompoundHighlighter();
        try {
            table.setHighlighters(new Highlighter[] {null});
            fail("illegal to call setHighlighters(null)");
            
        } catch (NullPointerException e) {
            // expected
        }
    }

//-----------------------  CompoundHighlighter
    
    /**
     * there had been exceptions when adding/removing highlighters to/from
     * an initially empty pipeline. 
     */
    @Test
    public void testAddToEmptyCompoundHiglighter() {
        CompoundHighlighter pipeline = new CompoundHighlighter();
        pipeline.addHighlighter(new ColorHighlighter());
    }
    @Test
    public void testRemoveFromEmptyCompoundHighlighter() {
        CompoundHighlighter pipeline = new CompoundHighlighter();
        pipeline.removeHighlighter(new ColorHighlighter());
    }
    @Test
    public void testApplyEmptyCompoundHighlighter() {
        CompoundHighlighter pipeline = new CompoundHighlighter();
        pipeline.highlight(new JLabel(), createComponentAdapter(new JLabel(), false));
    }

    /*
     */
    @Test
    public void testAddRemoveHighlighter() {
        CompoundHighlighter pipeline = new CompoundHighlighter(
                new ColorHighlighter(Color.white, new Color(0xF0, 0xF0, 0xE0)),
                new ColorHighlighter(null, foreground)
                );

        ColorHighlighter hl = new ColorHighlighter(Color.blue, Color.red);
        ColorHighlighter hl2 = new ColorHighlighter(Color.white, Color.black);

        // added highlighter should be appended
        pipeline.addHighlighter(hl);

        Highlighter[] hls = pipeline.getHighlighters();

        assertTrue(hls.length == 3);
        assertTrue(hls[2] == hl);

        // added highlighter should be prepended
        pipeline.addHighlighter(hl2, true);

        hls = pipeline.getHighlighters();

        assertTrue(hls.length == 4);
        assertTrue(hls[0] == hl2);

        // remove highlighter
        pipeline.removeHighlighter(hl);

        hls = pipeline.getHighlighters();
        assertTrue(hls.length == 3);
        for (int i = 0; i < hls.length; i++) {
            assertTrue(hls[i] != hl);
        }

        // remove another highligher
        pipeline.removeHighlighter(hl2);

        hls = pipeline.getHighlighters();
        assertTrue(hls.length == 2);
        for (int i = 0; i < hls.length; i++) {
            assertTrue(hls[i] != hl2);
        }
    }


    //----------------- testing change notification of pipeline
    
    /**
     * @todo - how to handle same highlighter inserted more than once?
     */
    @Test
    public void testCompoundHighlighterWithDuplicates() {
        
    }
    
    /**
     * test doc'ed NPE when adding null Highlighter.
     *
     */
    @Test
    public void testCompoundHighlighterAddNull() {
        CompoundHighlighter pipeline = new CompoundHighlighter();
        try {
            pipeline.addHighlighter(null);
            fail("compoundHighlighter must not accept null highlighter");
        } catch(NullPointerException ex) {
            
        }
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        // was added even with NPE.
        pipeline.highlight(allColored, adapter);
    }
    
    @Test
    public void testCompoundHighlighterChange() {
        ColorHighlighter highlighter = new ColorHighlighter();
        CompoundHighlighter pipeline = new CompoundHighlighter();
        ChangeReport changeReport = new ChangeReport();
        pipeline.addChangeListener(changeReport);
        int count = changeReport.getEventCount();
        pipeline.addHighlighter(highlighter);
        assertEquals("event count must be increased", ++count,  changeReport.getEventCount() );
        assertCompoundHighlighterChange(highlighter, pipeline, changeReport);
    }
    
    @Test
    public void testCompoundHighlighterChangeConstructor() {
        ColorHighlighter highlighter = new ColorHighlighter();
        CompoundHighlighter pipeline = new CompoundHighlighter(highlighter);
        ChangeReport changeReport = new ChangeReport();
        pipeline.addChangeListener(changeReport);
        assertCompoundHighlighterChange(highlighter, pipeline, changeReport);
 
    }
    private void assertCompoundHighlighterChange(ColorHighlighter highlighter, CompoundHighlighter pipeline, ChangeReport changeReport) {
        int count = changeReport.getEventCount();
        highlighter.setBackground(Color.red);
        assertEquals("event count must be increased", ++count,  changeReport.getEventCount() );
        assertEquals("event source must be pipeline", pipeline, changeReport.getLastEvent().getSource());
        pipeline.removeHighlighter(highlighter);
        assertEquals("event count must be increased", ++count,  changeReport.getEventCount() );
        pipeline.removeHighlighter(highlighter);
        assertEquals("event count must not be increased", count,  changeReport.getEventCount() );
        highlighter.setBackground(Color.BLUE);
        assertEquals("event count must not be increased", count,  changeReport.getEventCount() );
    }

    /**
     * Same content in both.
     * @param highlighters
     * @param highlighters2
     */
    private void assertSameContent(Highlighter[] highlighters, Highlighter[] highlighters2) {
        assertEquals(highlighters.length, highlighters2.length);
        for (int i = 0; i < highlighters.length; i++) {
            assertSame("must contain same element", highlighters[i], highlighters2[i]);
        }
    }
    
    /**
     * Last in list.
     * 
     * @param highlighters
     * @param highlighter
     */
    private void assertAsLast(Highlighter[] highlighters, Highlighter highlighter) {
        assertTrue("pipeline must not be empty", highlighters.length > 0);
        assertSame("highlighter must be added as last", highlighter, highlighters[highlighters.length - 1]);
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
 


}
