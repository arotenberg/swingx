/*
 * $Id: HighlightPredicateTest.java 3907 2010-12-15 12:21:26Z kleopatra $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.decorator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlightPredicate.AndHighlightPredicate;
import org.jdesktop.swingx.decorator.HighlightPredicate.ColumnHighlightPredicate;
import org.jdesktop.swingx.decorator.HighlightPredicate.ColumnTypeHighlightPredicate;
import org.jdesktop.swingx.decorator.HighlightPredicate.DepthHighlightPredicate;
import org.jdesktop.swingx.decorator.HighlightPredicate.EqualsHighlightPredicate;
import org.jdesktop.swingx.decorator.HighlightPredicate.IdentifierHighlightPredicate;
import org.jdesktop.swingx.decorator.HighlightPredicate.NotHighlightPredicate;
import org.jdesktop.swingx.decorator.HighlightPredicate.OrHighlightPredicate;
import org.jdesktop.swingx.decorator.HighlightPredicate.RowGroupHighlightPredicate;
import org.jdesktop.swingx.decorator.HighlightPredicate.TypeHighlightPredicate;
import org.jdesktop.swingx.rollover.RolloverProducer;
import org.jdesktop.test.AncientSwingTeam;
import org.junit.After;
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
public class HighlightPredicateTest extends InteractiveTestCase {

    
    protected JLabel backgroundNull ;
    protected JLabel foregroundNull;
    protected JLabel allNull;
    protected JLabel allColored;
    
    protected Color background = Color.RED;
    protected Color foreground = Color.BLUE;
    
    protected Color unselectedBackground = Color.CYAN;
    protected Color unselectedForeground = Color.GREEN;
    
    protected Color selectedBackground = Color.LIGHT_GRAY;
    protected Color selectedForeground = Color.MAGENTA;
    
    protected ColorHighlighter emptyHighlighter;

    @Before
    public void setUpJ4() throws Exception {
        setUp();
    }
    
    @After
    public void tearDownJ4() throws Exception {
        tearDown();
    }
    
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
        
        allColored = new JLabel("test");
        allColored.setForeground(foreground);
        allColored.setBackground(background);
        
        emptyHighlighter = new ColorHighlighter();
    }

    // ---------------- predefined predicate
    
    /**
     * Issue #858-swingx: predefined focus predicate. 
     * 
     * Test the IS_SELECTED predicate
     */
    @Test
    public void testIsSelected() {
        ComponentAdapter adapter = createComponentAdapter(allColored, true);
        assertTrue("sanity", adapter.isSelected());
        assertEquals(adapter.isSelected(), HighlightPredicate.IS_SELECTED.isHighlighted(allColored, adapter));
    }
    
    /**
     * Issue #858-swingx: predefined focus predicate. 
     * 
     * Test the IS_SELECTED predicate
     */
    @Test
    public void testNotIsSelected() {
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        assertFalse("sanity", adapter.isSelected());
        assertEquals(adapter.isSelected(), HighlightPredicate.IS_SELECTED.isHighlighted(allColored, adapter));
    }
    
    /**
     * Issue #858-swingx: predefined focus predicate. 
     * 
     * Test the HAS_FOCUS predicate
     */
    @Test
    public void testHasFocus() {
        ComponentAdapter adapter = createComponentAdapter(allColored, true, true);
        assertTrue("sanity", adapter.hasFocus());
        assertEquals(adapter.hasFocus(), HighlightPredicate.HAS_FOCUS.isHighlighted(allColored, adapter));
     }
 
    /**
     * Issue #858-swingx: predefined focus predicate. 
     * 
     * Test the HAS_FOCUS predicate
     */
    @Test
    public void testNotHasFocus() {
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        assertFalse("sanity", adapter.hasFocus());
        assertEquals(adapter.hasFocus(), HighlightPredicate.HAS_FOCUS.isHighlighted(allColored, adapter));
     }

    /**
     * Issue #1371-swingx: IS_TEXT_TRUNCATED must respect insets
     */
    @Test
    public void testIsTextTruncatedRespectsBorder() {
        allColored.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        Insets insets = allColored.getBorder().getBorderInsets(allColored);
        Dimension preferredSize = allColored.getPreferredSize();
        preferredSize.width -= insets.left + insets.right;
        preferredSize.height -= insets.top + insets.bottom;
        allColored.setSize(preferredSize);
        ComponentAdapter adapter = createComponentAdapter(allColored, true, true);
        assertTrue(HighlightPredicate.IS_TEXT_TRUNCATED.isHighlighted(allColored, adapter));
    }

    /**
     * Issue #1314: predefined truncated text predicate. 
     * 
     * Test the IS_TEXT_TRUNCATED predicate
     */
    @Test
    public void testIsTextTruncated() {
        allColored.setSize(1, 1); //ensure the size is too small to display full String
        ComponentAdapter adapter = createComponentAdapter(allColored, true, true);
        assertTrue(HighlightPredicate.IS_TEXT_TRUNCATED.isHighlighted(allColored, adapter));
    }
    
    /**
     * Issue #1314: predefined truncated text predicate. 
     * 
     * Test the IS_TEXT_TRUNCATED predicate
     */
    @Test
    public void testNotIsTextTruncated() {
        allColored.setSize(allColored.getPreferredSize()); // ensure enough space to display text
        ComponentAdapter adapter = createComponentAdapter(allColored, true, true);
        assertFalse(HighlightPredicate.IS_TEXT_TRUNCATED.isHighlighted(allColored, adapter));
    }
    
    /**
     * test the IS_FOLDER predicate.
     *
     */
    @Test
    public void testFolder() {
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        assertTrue(HighlightPredicate.IS_FOLDER.isHighlighted(allColored, adapter));
    }
    
    /**
     * test the IS_FOLDER predicate.
     *
     */
    @Test
    public void testNotFolder() {
        ComponentAdapter adapter = createComponentAdapter(allColored, true);
        assertFalse(HighlightPredicate.IS_FOLDER.isHighlighted(allColored, adapter));
    }

    /**
     * test the IS_LEAF predicate.
     *
     */
    @Test
    public void testLeaf() {
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        assertFalse(HighlightPredicate.IS_LEAF.isHighlighted(allColored, adapter));
    }
    
    /**
     * test the IS_LEAF predicate.
     *
     */
    @Test
    public void testNotLeaf() {
        ComponentAdapter adapter = createComponentAdapter(allColored, true);
        assertTrue(HighlightPredicate.IS_LEAF.isHighlighted(allColored, adapter));
    }
    
    /**
     * Can't really test the unconditional predicates.
     *
     */
    @Test
    public void testAlways() {
        ComponentAdapter adapter = createComponentAdapter(allColored, true);
        assertTrue(HighlightPredicate.ALWAYS.isHighlighted(allColored, adapter));
    }
    
    /**
     * Can't really test the unconditional predicates.
     *
     */
    @Test
    public void testNever() {
        ComponentAdapter adapter = createComponentAdapter(allColored, true);
        assertFalse(HighlightPredicate.NEVER.isHighlighted(allColored, adapter));
    }
    
    /**
     * test the Editable predicate.
     *
     */
    @Test
    public void testEditable() {
        ComponentAdapter adapter = createComponentAdapter(allColored, false, true);
        assertTrue(HighlightPredicate.EDITABLE.isHighlighted(allColored, adapter));
        assertFalse(HighlightPredicate.READ_ONLY.isHighlighted(allColored, adapter));
    }

    /**
     * test the ReadOnly predicate.
     *
     */
    @Test
    public void testNotEditable() {
        ComponentAdapter adapter = createComponentAdapter(allColored, false, false);
        assertFalse(HighlightPredicate.EDITABLE.isHighlighted(allColored, adapter));
        assertTrue(HighlightPredicate.READ_ONLY.isHighlighted(allColored, adapter));
    }
    
    /**
     * test access the contained predicates
     */
    @Test
    public void testNotProperty() {
        HighlightPredicate inputPredicates = new IdentifierHighlightPredicate("t");
        NotHighlightPredicate predicate = new NotHighlightPredicate(inputPredicates);
        HighlightPredicate predicates = predicate.getHighlightPredicate();
        assertSame(inputPredicates, predicates);
    }
    
    /**
     * test the NOT predicate.
     *
     */
    @Test
    public void testNot() {
        ComponentAdapter adapter = createComponentAdapter(allColored, true);
        HighlightPredicate notNever = new NotHighlightPredicate(HighlightPredicate.NEVER);
        assertTrue(notNever.isHighlighted(allColored, adapter));
        HighlightPredicate notAlways = new NotHighlightPredicate(HighlightPredicate.ALWAYS);
        assertFalse(notAlways.isHighlighted(allColored, adapter));
    }

    /**
     * test access the contained predicates
     */
    @Test
    public void testOrProperty() {
        List<HighlightPredicate> inputPredicates = new ArrayList<HighlightPredicate>();
        inputPredicates.add(HighlightPredicate.NEVER);
        inputPredicates.add(new IdentifierHighlightPredicate("t"));
        OrHighlightPredicate predicate = new OrHighlightPredicate(inputPredicates);
        HighlightPredicate[] predicates = predicate.getHighlightPredicates();
        assertEquals(inputPredicates.size(), predicates.length);
        for (int i = 0; i < predicates.length; i++) {
            assertSame(inputPredicates.get(i), predicates[i]);
        }
    }
    
    /**
     * test that getPredicates return empty array if nothing contained.
     */
    @Test
    public void testOrEmptyProperty() {
        OrHighlightPredicate predicate = new OrHighlightPredicate();
        HighlightPredicate[] predicates = predicate.getHighlightPredicates();
        assertNotNull("array must not be null", predicates);
        assertEquals(0, predicates.length);
    }

    /**
     * test that empty array doesn't highlight. 
     */
    @Test
    public void testOrEmpty() {
        ComponentAdapter adapter = createComponentAdapter(allColored, true);
        HighlightPredicate emptyArray = new OrHighlightPredicate();
        assertFalse(emptyArray.isHighlighted(allColored, adapter));
        HighlightPredicate emptyList = new OrHighlightPredicate(new ArrayList<HighlightPredicate>());
        assertFalse(emptyList.isHighlighted(allColored, adapter));
    }
    /**
     * test the OR predicate array constructor. Boring as it is, is it complete?
     *
     */
    @Test
    public void testOr() {
        ComponentAdapter adapter = createComponentAdapter(allColored, true);
        HighlightPredicate oneTrue = new OrHighlightPredicate(HighlightPredicate.ALWAYS);
        assertTrue(oneTrue.isHighlighted(allColored, adapter));
        HighlightPredicate oneFalse = new OrHighlightPredicate(HighlightPredicate.NEVER);
        assertFalse(oneFalse.isHighlighted(allColored, adapter));
        HighlightPredicate oneFalseOneTrue = new OrHighlightPredicate(
                HighlightPredicate.NEVER, HighlightPredicate.ALWAYS);
        assertTrue(oneFalseOneTrue.isHighlighted(allColored, adapter));
        HighlightPredicate oneTrueOneFalse = new OrHighlightPredicate(
                HighlightPredicate.ALWAYS, HighlightPredicate.NEVER);
        assertTrue(oneTrueOneFalse.isHighlighted(allColored, adapter));
    }
    
    /**
     * test the OR predicate collection constructor. Boring as it is, is it complete?
     *
     */
    @Test
    public void testOrCollectionConstructor() {
        ComponentAdapter adapter = createComponentAdapter(allColored, true);
        List<HighlightPredicate> containsOneTrue = new ArrayList<HighlightPredicate>();
        containsOneTrue.add(HighlightPredicate.ALWAYS);
        HighlightPredicate oneTrue = new OrHighlightPredicate(containsOneTrue);
        assertTrue(oneTrue.isHighlighted(allColored, adapter));
        
        List<HighlightPredicate> containsOneFalse = new ArrayList<HighlightPredicate>();
        containsOneFalse.add(HighlightPredicate.NEVER);
        HighlightPredicate oneFalse = new OrHighlightPredicate(containsOneFalse);
        assertFalse(oneFalse.isHighlighted(allColored, adapter));
        
        List<HighlightPredicate> containsOneFalseOneTrue = new ArrayList<HighlightPredicate>();
        containsOneFalseOneTrue.add(HighlightPredicate.NEVER);
        containsOneFalseOneTrue.add(HighlightPredicate.ALWAYS);
        HighlightPredicate oneFalseOneTrue = new OrHighlightPredicate(containsOneFalseOneTrue);
        assertTrue(oneFalseOneTrue.isHighlighted(allColored, adapter));

        List<HighlightPredicate> containsOneTrueOneFalse = new ArrayList<HighlightPredicate>();
        containsOneTrueOneFalse.add(HighlightPredicate.ALWAYS);
        containsOneTrueOneFalse.add(HighlightPredicate.NEVER);
        HighlightPredicate oneTrueOneFalse = new OrHighlightPredicate(containsOneTrueOneFalse);
        assertTrue(oneTrueOneFalse.isHighlighted(allColored, adapter));
    }
    
    /**
     * Issue #520-swingx: OrPredicate must throw if any of the parameters
     *   is null.
     *
     */
    @Test
    public void testOrThrowsOnNullPredicates() {
        try {
            new OrHighlightPredicate((HighlightPredicate[]) null);
            fail("orPredicate constructor must throw NullPointerException on null predicate");
            
        } catch (NullPointerException ex) {
            // do nothing - the doc'ed exception
        } catch (Exception ex) {
            fail("unexpected exception: " + ex);
        }

        try {
            new OrHighlightPredicate(HighlightPredicate.ALWAYS, null);
            fail("orPredicate constructor must throw NullPointerException on null predicate");
            
        } catch (NullPointerException ex) {
            // do nothing - the doc'ed exception
        } catch (Exception ex) {
            fail("unexpected exception: " + ex);
        }
            

        try {
            new OrHighlightPredicate((HighlightPredicate) null);
            fail("orPredicate constructor must throw NullPointerException on null predicate");
            
        } catch (NullPointerException ex) {
            // do nothing - the doc'ed exception
        } catch (Exception ex) {
            fail("unexpected exception: " + ex);
        }
            
        try {
            new OrHighlightPredicate((Collection<HighlightPredicate>) null);
            fail("orPredicate constructor must throw NullPointerException on null predicate");
            
        } catch (NullPointerException ex) {
            // do nothing - the doc'ed exception
        } catch (Exception ex) {
            fail("unexpected exception: " + ex);
        }
    }

    /**
     * Issue #520-swingx: OrPredicate must throw if any of the parameters
     *   is null.
     *
     */
    @Test
    public void testAndThrowsOnNullPredicates() {
        try {
            new AndHighlightPredicate((HighlightPredicate[]) null);
            fail("AndPredicate constructAnd must throw NullPointerException on null predicate");
            
        } catch (NullPointerException ex) {
            // do nothing - the doc'ed exception
        } catch (Exception ex) {
            fail("unexpected exception: " + ex);
        }

        try {
            new AndHighlightPredicate(HighlightPredicate.ALWAYS, null);
            fail("AndPredicate constructAnd must throw NullPointerException on null predicate");
            
        } catch (NullPointerException ex) {
            // do nothing - the doc'ed exception
        } catch (Exception ex) {
            fail("unexpected exception: " + ex);
        }
            

        try {
            new AndHighlightPredicate((HighlightPredicate) null);
            fail("AndPredicate constructAnd must throw NullPointerException on null predicate");
            
        } catch (NullPointerException ex) {
            // do nothing - the doc'ed exception
        } catch (Exception ex) {
            fail("unexpected exception: " + ex);
        }

        try {
            new AndHighlightPredicate((Collection<HighlightPredicate>) null);
            fail("orPredicate constructor must throw NullPointerException on null predicate");
            
        } catch (NullPointerException ex) {
            // do nothing - the doc'ed exception
        } catch (Exception ex) {
            fail("unexpected exception: " + ex);
        }

    }

    /**
     * test access the contained predicates
     */
    @Test
    public void testAndProperty() {
        List<HighlightPredicate> inputPredicates = new ArrayList<HighlightPredicate>();
        inputPredicates.add(HighlightPredicate.NEVER);
        inputPredicates.add(new IdentifierHighlightPredicate("t"));
        AndHighlightPredicate predicate = new AndHighlightPredicate(inputPredicates);
        HighlightPredicate[] predicates = predicate.getHighlightPredicates();
        assertEquals(inputPredicates.size(), predicates.length);
        for (int i = 0; i < predicates.length; i++) {
            assertSame(inputPredicates.get(i), predicates[i]);
        }
    }
    
    /**
     * test that getPredicates return empty array if nothing contained.
     */
    @Test
    public void testAndEmptyProperty() {
        AndHighlightPredicate predicate = new AndHighlightPredicate();
        HighlightPredicate[] predicates = predicate.getHighlightPredicates();
        assertNotNull("array must not be null", predicates);
        assertEquals(0, predicates.length);
    }
    
    /**
     * test that empty array doesn't highlight. 
     */
    @Test
    public void testAndEmpty() {
        ComponentAdapter adapter = createComponentAdapter(allColored, true);
        HighlightPredicate emptyArray = new AndHighlightPredicate();
        assertFalse(emptyArray.isHighlighted(allColored, adapter));
        HighlightPredicate emptyList = new AndHighlightPredicate(new ArrayList<HighlightPredicate>());
        assertFalse(emptyList.isHighlighted(allColored, adapter));
    }

    /**
     * test the AND predicate array constructor. Boring as it is, is it complete?
     *
     */
    @Test
    public void testAnd() {
        ComponentAdapter adapter = createComponentAdapter(allColored, true);
        HighlightPredicate oneTrue = new AndHighlightPredicate(HighlightPredicate.ALWAYS);
        assertTrue(oneTrue.isHighlighted(allColored, adapter));
        HighlightPredicate oneFalse = new AndHighlightPredicate(HighlightPredicate.NEVER);
        assertFalse(oneFalse.isHighlighted(allColored, adapter));
        HighlightPredicate oneFalseOneTrue = new AndHighlightPredicate(
                HighlightPredicate.NEVER, HighlightPredicate.ALWAYS);
        assertFalse(oneFalseOneTrue.isHighlighted(allColored, adapter));
        HighlightPredicate oneTrueOneFalse = new AndHighlightPredicate(
                HighlightPredicate.ALWAYS, HighlightPredicate.NEVER);
        assertFalse(oneTrueOneFalse.isHighlighted(allColored, adapter));
    }

    /**
     * test the AND predicate collection constructor. Boring as it is, is it complete?
     *
     */
    @Test
    public void testAndCollectionConstructor() {
        ComponentAdapter adapter = createComponentAdapter(allColored, true);
        List<HighlightPredicate> containsOneTrue = new ArrayList<HighlightPredicate>();
        containsOneTrue.add(HighlightPredicate.ALWAYS);
        HighlightPredicate oneTrue = new AndHighlightPredicate(containsOneTrue);
        assertTrue(oneTrue.isHighlighted(allColored, adapter));
        
        List<HighlightPredicate> containsOneFalse = new ArrayList<HighlightPredicate>();
        containsOneFalse.add(HighlightPredicate.NEVER);
        HighlightPredicate oneFalse = new AndHighlightPredicate(containsOneFalse);
        assertFalse(oneFalse.isHighlighted(allColored, adapter));
        
        List<HighlightPredicate> containsOneFalseOneTrue = new ArrayList<HighlightPredicate>();
        containsOneFalseOneTrue.add(HighlightPredicate.NEVER);
        containsOneFalseOneTrue.add(HighlightPredicate.ALWAYS);
        HighlightPredicate oneFalseOneTrue = new AndHighlightPredicate(containsOneFalseOneTrue);
        assertFalse(oneFalseOneTrue.isHighlighted(allColored, adapter));
        
        List<HighlightPredicate> containsOneTrueOneFalse = new ArrayList<HighlightPredicate>();
        containsOneTrueOneFalse.add(HighlightPredicate.ALWAYS);
        containsOneTrueOneFalse.add(HighlightPredicate.NEVER);
        HighlightPredicate oneTrueOneFalse = new AndHighlightPredicate(containsOneTrueOneFalse);
        assertFalse(oneTrueOneFalse.isHighlighted(allColored, adapter));
    }
    

    /**
     * test ROLLOVER_CELL
     *
     */
    @Test
    public void testRolloverCell() {
        ComponentAdapter adapter = createComponentAdapter(allColored, true);
        // rollover and adapter at 0, 0
        int row = 0; 
        int col = 0;
        allColored.putClientProperty(RolloverProducer.ROLLOVER_KEY, new Point(row, col));
        assertTrue(HighlightPredicate.ROLLOVER_CELL.isHighlighted(allColored, adapter));
        // move adapter row in same row
        adapter.row = 3;
        assertFalse(HighlightPredicate.ROLLOVER_CELL.isHighlighted(allColored, adapter));
        // move adapter row 
        adapter.column = 1;
        assertFalse(HighlightPredicate.ROLLOVER_CELL.isHighlighted(allColored, adapter));
    }
    
    /**
     * test ROLLOVER_COLUMN
     *
     */
    @Test
    public void testRolloverColumn() {
        ComponentAdapter adapter = createComponentAdapter(allColored, true);
        // rollover and adapter at 0, 0
        int row = 0; 
        int col = 0;
        allColored.putClientProperty(RolloverProducer.ROLLOVER_KEY, new Point(row, col));
        assertTrue(HighlightPredicate.ROLLOVER_COLUMN.isHighlighted(allColored, adapter));
        // move adapter row in same row
        adapter.row = 3;
        assertTrue(HighlightPredicate.ROLLOVER_COLUMN.isHighlighted(allColored, adapter));
        // move adapter row 
        adapter.column = 1;
        assertFalse(HighlightPredicate.ROLLOVER_COLUMN.isHighlighted(allColored, adapter));
    }
    
    /**
     * test ROLLOVER_ROW
     *
     */
    @Test
    public void testRolloverRow() {
        ComponentAdapter adapter = createComponentAdapter(allColored, true);
        // rollover and adapter at 0, 0
        int row = 0; 
        int col = 0;
        allColored.putClientProperty(RolloverProducer.ROLLOVER_KEY, new Point(row, col));
        assertTrue(HighlightPredicate.ROLLOVER_ROW.isHighlighted(allColored, adapter));
        // move adapter column in same row
        adapter.column = 3;
        assertTrue(HighlightPredicate.ROLLOVER_ROW.isHighlighted(allColored, adapter));
        // move adapter row 
        adapter.row = 1;
        assertFalse(HighlightPredicate.ROLLOVER_ROW.isHighlighted(allColored, adapter));
    }
    
    /**
     * Issue #513-swingx: no rollover highlight for disabled component
     * 
     */
    @Test
    public void testRolloverRowDisabledComponent() {
        ComponentAdapter adapter = createComponentAdapter(allColored, true);
        // rollover and adapter at 0, 0
        int row = 0; 
        int col = 0;
        allColored.putClientProperty(RolloverProducer.ROLLOVER_KEY, new Point(row, col));
        // sanity
        assertTrue(HighlightPredicate.ROLLOVER_ROW.isHighlighted(allColored, adapter));
        allColored.setEnabled(false);
        assertFalse(HighlightPredicate.ROLLOVER_ROW.isHighlighted(allColored, adapter));
    }

    /**
     * test the BIG_DECIMAL_NEGATIVE predicate.
     *
     */
    @Test
    public void testBigDecimalNegative() {
        ComponentAdapter negative = createBigDecimalComponentAdapter(new JLabel("-50.00"));
        assertTrue(HighlightPredicate.BIG_DECIMAL_NEGATIVE.isHighlighted(negative.getComponent(), negative));
        ComponentAdapter positive = createBigDecimalComponentAdapter(new JLabel("50.00"));
        assertFalse(HighlightPredicate.BIG_DECIMAL_NEGATIVE.isHighlighted(positive.getComponent(), positive));
        ComponentAdapter zero = createBigDecimalComponentAdapter(new JLabel(BigDecimal.ZERO.toString()));
        // sanity
        assertEquals(BigDecimal.ZERO, zero.getValue());
        assertFalse(HighlightPredicate.BIG_DECIMAL_NEGATIVE.isHighlighted(zero.getComponent(), zero));
        
    }

//---------------- special predicates
    /**
     * test doc'ed behaviour
     */
    @Test
    public void testRowGroupConstructor() {
        try {
            new RowGroupHighlightPredicate(0);
            fail("RowGroupHighlight must throw IllegalArgumentException for lines < 1");
        } catch (IllegalArgumentException ex) {
            // expected behaviour
        } 
    }
    
    @Test
    public void testRowGroupProperty() {
        int lines = 5;
        RowGroupHighlightPredicate predicate = new RowGroupHighlightPredicate(5);
        assertEquals(lines, predicate.getLinesPerGroup());
    }
    
    /**
     * test equals predicate with null value.
     */
    @Test
    public void testEqualsNull() {
        HighlightPredicate predicate = new EqualsHighlightPredicate();
        allColored.setText(null);
        assertNull(allColored.getText());
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        assertTrue(predicate.isHighlighted(allColored, adapter));
        String text = "test";
        allColored.setText(text);
        assertEquals(text, allColored.getText());
        assertFalse(predicate.isHighlighted(allColored, adapter));
    }
    
    /**
     * test equals predicate with not null value.
     *
     */
    @Test
    public void testEqualsNotNull() {
        HighlightPredicate predicate = new EqualsHighlightPredicate(allColored
                .getText());
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        assertTrue(predicate.isHighlighted(allColored, adapter));
        allColored.setText(null);
        assertFalse(predicate.isHighlighted(allColored, adapter));
    }
    
    /**
     * test equals predicate with not null value.
     *
     */
    @Test
    public void testEqualProperty() {
        Object text = new Object();
        EqualsHighlightPredicate predicate = new EqualsHighlightPredicate(text);
        assertEquals(text, predicate.getCompareValue());
    }
    
    @Test
    public void testColumnProperty() {
        int[] columns = new int[] {2, 7};
        ColumnHighlightPredicate predicate = new ColumnHighlightPredicate(columns);
        Integer[] output = predicate.getColumns();
        assertEquals(columns.length, output.length);
        for (int i = 0; i < output.length; i++) {
            assertEquals(columns[i], output[i].intValue());
        }
    }

    @Test
    public void testColumnEmptyProperty() {
        ColumnHighlightPredicate predicate = new ColumnHighlightPredicate();
        Integer[] output = predicate.getColumns();
        assertEquals("output must be empty array", 0, output.length);
    }

    @Test
    public void testColumn() {
        HighlightPredicate predicate = new ColumnHighlightPredicate(1);
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        assertFalse("column 0 must not be highlighted", predicate.isHighlighted(allColored, adapter));
        adapter.column = 1;
        assertTrue("column 1 must be highlighted", predicate.isHighlighted(allColored, adapter));
    }


    @Test
    public void testDepthProperty() {
        int[] columns = new int[] {2, 7};
        DepthHighlightPredicate predicate = new DepthHighlightPredicate(columns);
        Integer[] output = predicate.getDepths();
        assertEquals(columns.length, output.length);
        for (int i = 0; i < output.length; i++) {
            assertEquals(columns[i], output[i].intValue());
        }
    }

    @Test
    public void testDepthEmptyProperty() {
        DepthHighlightPredicate predicate = new DepthHighlightPredicate();
        Integer[] output = predicate.getDepths();
        assertEquals("output must be empty array", 0, output.length);
    }

    @Test
    public void testIdentifierProperty() {
        Object[] identifiers = new Object[] { "one", "two" };
        IdentifierHighlightPredicate predicate = new IdentifierHighlightPredicate(identifiers);
        Object[] output = predicate.getIdentifiers();
        assertEquals(identifiers.length, output.length);
        for (int i = 0; i < output.length; i++) {
            assertEquals(identifiers[i], output[i]);
        }
    }

    @Test
    public void testIdentifierEmptyProperty() {
        IdentifierHighlightPredicate predicate = new IdentifierHighlightPredicate();
        Object[] output = predicate.getIdentifiers();
        assertEquals("output must be empty array", 0, output.length);
    }

    @Test
    public void testIdentifierDefault() {
        HighlightPredicate predicate = new IdentifierHighlightPredicate(ComponentAdapter.DEFAULT_COLUMN_IDENTIFIER);
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        assertTrue("default (single) column must be highlighted", predicate.isHighlighted(allColored, adapter));
    }

    @Test
    public void testIdentifierFalse() {
        HighlightPredicate predicate = new IdentifierHighlightPredicate("unknown");
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        assertFalse("unknown identifier must not highlight", predicate.isHighlighted(allColored, adapter));
    }

//--------------------- TypeHighlightPredicate
 
    @Test (expected = NullPointerException.class)
    public void testTypeNull() {
        new TypeHighlightPredicate(null);
    }
    

    @Test
    public void testTypeProperty() {
        Class<?> clazz = String.class;
        TypeHighlightPredicate predicate = new TypeHighlightPredicate(clazz);
        assertEquals(clazz, predicate.getType());
    }
    
    @Test
    public void testTypeExact() {
        HighlightPredicate predicate = new TypeHighlightPredicate(String.class);
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        assertTrue("string class must be highlighted", predicate.isHighlighted(allColored, adapter));
    }
    
    @Test
    public void testTypeSubclass() {
        HighlightPredicate predicate = new TypeHighlightPredicate();
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        assertTrue("string class must be highlighted", predicate.isHighlighted(allColored, adapter));
    }
    
    @Test
    public void testTypeFalse() {
        HighlightPredicate predicate = new TypeHighlightPredicate(Integer.class);
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        assertFalse("string class must be highlighted", predicate.isHighlighted(allColored, adapter));
    }
    
//----------------------- ColumnTypePredicate
    
    @Test (expected = NullPointerException.class)
    public void testColumnTypeNull() {
        new ColumnTypeHighlightPredicate(null);
    }
    
    @Test
    public void testColumnTypeProperty() {
        Class<?> clazz = String.class;
        ColumnTypeHighlightPredicate predicate = new ColumnTypeHighlightPredicate(clazz);
        assertEquals(clazz, predicate.getType());
    }
    
    @Test
    public void testColumnTypeExact() {
        HighlightPredicate predicate = new ColumnTypeHighlightPredicate(String.class);
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        assertTrue("string class must be highlighted", predicate.isHighlighted(allColored, adapter));
    }

    @Test
    public void testColumnTypeSubclass() {
        HighlightPredicate predicate = new ColumnTypeHighlightPredicate();
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        assertTrue("string class must be highlighted", predicate.isHighlighted(allColored, adapter));
    }

    @Test
    public void testColumnTypeFalse() {
        HighlightPredicate predicate = new ColumnTypeHighlightPredicate(Integer.class);
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        assertFalse("string class must be highlighted", predicate.isHighlighted(allColored, adapter));
    }
    
// ------ stand-alone predicates    
    
    
    /**
     * Issue #1317-swingx: PatternPredicate causes exception with testColumn -1 (ALL)
     * 
     */
    @Test
    public void testPatternPredicateAllColumns() {
        JXTable table = new JXTable(new AncientSwingTeam());
        PatternPredicate predicate = new PatternPredicate(".*e.*");
        table.addHighlighter(new ColorHighlighter(predicate, Color.BLUE, null));
        for (int i = 0; i < table.getRowCount(); i++) {
            for (int j = 0; j < table.getColumnCount(); j++) {
                TableCellRenderer renderer = table.getCellRenderer(i, j);
                table.prepareRenderer(renderer, i, j);
            }
        }
    }



    @Test
    public void testPattern() {
        // start with "t"
        Pattern pattern = Pattern.compile("^t", 0);
        int testColumn = 0;
        int decorateColumn = 0;
        HighlightPredicate predicate = new PatternPredicate(pattern, testColumn, decorateColumn);
        ComponentAdapter adapter = createComponentAdapter(allColored, true);
        assertEquals("predicate must have same result as matcher", pattern.matcher(allColored.getText()).find(), 
                  predicate.isHighlighted(allColored, adapter));
    }
    
    @Test
    public void testPatternWithString() {
        // start with "t"
        String regex = "^t";
        int testColumn = 0;
        int decorateColumn = 0;
        PatternPredicate predicate = new PatternPredicate(regex, testColumn, decorateColumn);
        Pattern pattern = Pattern.compile(regex);
        assertEquals(pattern.pattern(), predicate.getPattern().pattern());
        ComponentAdapter adapter = createComponentAdapter(allColored, true);
        assertEquals("predicate must have same result as matcher", pattern.matcher(allColored.getText()).find(), 
                  predicate.isHighlighted(allColored, adapter));
    }
    
    @Test
    public void testPatternTwoParamConstructorWithString() {
        // start with "t"
        String regex = "^t";
        int testColumn = 0;
        HighlightPredicate predicate = new PatternPredicate(regex, testColumn);
        Pattern pattern = Pattern.compile(regex);
        ComponentAdapter adapter = createComponentAdapter(allColored, true);
        assertEquals("predicate must have same result as matcher", pattern.matcher(allColored.getText()).find(), 
                  predicate.isHighlighted(allColored, adapter));
    }
 
    @Test
    public void testSearchHighlightConstructorOne() {
        Pattern pattern = Pattern.compile("^t");
        SearchPredicate predicate = new SearchPredicate(pattern);
        assertEquals(pattern, predicate.getPattern());
        assertEquals(SearchPredicate.ALL, predicate.getHighlightColumn());
        assertEquals(SearchPredicate.ALL, predicate.getHighlightRow());
    }

    @Test
    public void testSearchHighlightConstructorTwo() {
        Pattern pattern = Pattern.compile("^t");
        int column = 10;
        SearchPredicate predicate = new SearchPredicate(pattern, column);
        assertEquals(pattern, predicate.getPattern());
        assertEquals(column, predicate.getHighlightColumn());
        assertEquals(SearchPredicate.ALL, predicate.getHighlightRow());
    }
    
    @Test
    public void testSearchHighlightConstructorFull() {
        Pattern pattern = Pattern.compile("^t");
        int row = 5;
        int column = 10;
        SearchPredicate predicate = new SearchPredicate(pattern, row, column);
        assertEquals(pattern, predicate.getPattern());
        assertEquals(column, predicate.getHighlightColumn());
        assertEquals(row, predicate.getHighlightRow());
    }
    /**
     * test match in all cells.
     *
     */
    @Test
    public void testSearchHighlightAllMatches() {
        // start with "t"
        Pattern pattern = Pattern.compile("^t");
        HighlightPredicate predicate = new SearchPredicate(pattern);
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        assertEquals("predicate must have same result as matcher", pattern.matcher(allColored.getText()).find(), 
                  predicate.isHighlighted(allColored, adapter));
        adapter.row = 5;
        adapter.column = 10;
        assertEquals("predicate must have same result as matcher", pattern.matcher(allColored.getText()).find(), 
                predicate.isHighlighted(allColored, adapter));
    }
    
    /**
     * test match limited by column.
     *
     */
    @Test
    public void testSearchHighlightColumn() {
        // start with "t"
        Pattern pattern = Pattern.compile("^t");
        int column = 2;
        HighlightPredicate predicate = new SearchPredicate(pattern, column);
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        assertFalse("predicate must not match", 
                  predicate.isHighlighted(allColored, adapter));
        adapter.column = column;
        assertTrue("predicate must match", 
                predicate.isHighlighted(allColored, adapter));
    }
    
    /**
     * test match limited by row.
     *
     */
    @Test
    public void testSearchHighlightRow() {
        // start with "t"
        Pattern pattern = Pattern.compile("^t");
        int row = 2;
        HighlightPredicate predicate = new SearchPredicate(pattern, row, SearchPredicate.ALL);
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        assertFalse("predicate must not match", 
                  predicate.isHighlighted(allColored, adapter));
        adapter.row = row;
        assertTrue("predicate must match", 
                predicate.isHighlighted(allColored, adapter));
    }
    
    /**
     * test match limited by row and column.
     *
     */
    @Test
    public void testSearchHighlightRowAndColumn() {
        // start with "t"
        Pattern pattern = Pattern.compile("^t");
        int row = 2;
        int column = 2;
        HighlightPredicate predicate = new SearchPredicate(pattern, row, column);
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        assertFalse("predicate must not match", 
                  predicate.isHighlighted(allColored, adapter));
        adapter.row = row;
        adapter.column = column;
        assertTrue("predicate must match", 
                predicate.isHighlighted(allColored, adapter));
    }
    
    /**
     * test match in all cells using regex constructor.
     *
     */
    @Test
    public void testSearchHighlightAllMatchesWithString() {
        // start with "t"
        String regex = "^t";
        HighlightPredicate predicate = new SearchPredicate(regex);
        Pattern pattern = Pattern.compile(regex);
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        assertEquals("predicate must have same result as matcher", pattern.matcher(allColored.getText()).find(), 
                  predicate.isHighlighted(allColored, adapter));
        adapter.row = 5;
        adapter.column = 10;
        assertEquals("predicate must have same result as matcher", pattern.matcher(allColored.getText()).find(), 
                predicate.isHighlighted(allColored, adapter));
    }
    
    /**
     * test match limited by column using regex constructor.
     *
     */
    @Test
    public void testSearchHighlightColumnWithString() {
        // start with "t"
        String regex = "^t";
        int column = 2;
        HighlightPredicate predicate = new SearchPredicate(regex, column);
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        assertFalse("predicate must not match", 
                  predicate.isHighlighted(allColored, adapter));
        adapter.column = column;
        assertTrue("predicate must match", 
                predicate.isHighlighted(allColored, adapter));
    }
    
    /**
     * test match limited by row using regex constructor.
     *
     */
    @Test
    public void testSearchHighlightRowWithString() {
        // start with "t"
        String regex = "^t";
        int row = 2;
        HighlightPredicate predicate = new SearchPredicate(regex, row, SearchPredicate.ALL);
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        assertFalse("predicate must not match", 
                  predicate.isHighlighted(allColored, adapter));
        adapter.row = row;
        assertTrue("predicate must match", 
                predicate.isHighlighted(allColored, adapter));
    }

    @Test
    public void testSearchHighlightNullString() {
        // start with "t"
        String regex = null;
        HighlightPredicate predicate = new SearchPredicate(regex);
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        assertEquals("null regex must be no match", false, 
                  predicate.isHighlighted(allColored, adapter));
    }
    
    @Test
    public void testSearchHighlightEmptyString() {
        // start with "t"
        String regex = "";
        HighlightPredicate predicate = new SearchPredicate(regex);
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        assertEquals("empty regex must be no match", false, 
                  predicate.isHighlighted(allColored, adapter));
    }
    
    @Test
    public void testSearchHighlightNullPattern() {
        Pattern pattern = null;
        HighlightPredicate predicate = new SearchPredicate(pattern);
        ComponentAdapter adapter = createComponentAdapter(allColored, false);
        assertEquals("null pattern must be no match", false, 
                  predicate.isHighlighted(allColored, adapter));
    }
    
    
    // --------------------- factory methods
    /**
     * Creates and returns a ComponentAdapter on the given 
     * label with the specified selection state and non-editable.
     * 
     * @param label the adapter's component
     * @param selected the adapter's selected property
     * @return a ComponentAdapter of the label with the given properties.
     */
    protected ComponentAdapter createComponentAdapter(final JLabel label, final boolean selected) {
        return createComponentAdapter(label, selected, false);
    }

    /**
     * Creates and returns a ComponentAdapter on the given 
     * label with the specified selection and editable state. IsLeaf returns
     * the same value as selected. HasFocus returns selected && editable.
     * 
     * @param label the adapter's component
     * @param selected the adapter's selected property
     * @param editable the adapter's editable property
     * @return a ComponentAdapter of the label with the given properties.
     */
    protected ComponentAdapter createComponentAdapter(final JLabel label,
            final boolean selected, final boolean editable) {
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
                return isEditable();
            }

            @Override
            public boolean hasFocus() {
                return isSelected() && isEditable();
            }
            
            @Override
            public boolean isEditable() {
                return editable;
            }

            @Override
            public boolean isSelected() {
                return selected;
            }

            
            @Override
            public boolean isLeaf() {
                return isSelected();
            }

            @Override
            public String getColumnName(int columnIndex) {
                return null;
            }

            /** 
             * @inherited <p>
             */
            @Override
            public Class<?> getColumnClass(int column) {
                return String.class;
            }

            
        };
        return adapter;
    }
    
    /**
     * Creates and returns a ComponentAdapter on the given 
     * label. The labels text is parsed as BigDecimal.
     * 
     * @param label
     * @return
     */
    protected ComponentAdapter createBigDecimalComponentAdapter(final JLabel label) {
        ComponentAdapter adapter = new ComponentAdapter(label) {

            @Override
            public Object getValueAt(int row, int column) {
                return new BigDecimal(label.getText());
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
                return false;
            }

            @Override
            public String getColumnName(int columnIndex) {
                return null;
            }

            
        };
        return adapter;
    }
}
