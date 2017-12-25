/*
 * $Id: PatternModelTest.java 3473 2009-08-27 13:17:10Z kleopatra $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.search;

import java.util.List;

import junit.framework.TestCase;

import org.jdesktop.test.PropertyChangeReport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Testing PatternModel.
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class PatternModelTest extends TestCase {

    final static String startAnchor = "^";
    final static String endAnchor = "$";
    final static String middleStartAnchor = "some" + startAnchor + "one";
    final static String middleEndAnchor = "some" + endAnchor + "one";
    private PropertyChangeReport propertyReport;


    @Before
    public void setUpJ4() throws Exception {
        setUp();
    }
    
    @After
    public void tearDownJ4() throws Exception {
        tearDown();
    }
    
    /**
     * test initial autoAdjust of foundIndex in backward search.
     *
     */
    @Test
    public void testFoundIndexAutoAdjustBackward() {
        PatternModel model = new PatternModel();
        model.setBackwards(true);
        assertTrue("is autoAdjust by default", model.isAutoAdjustFoundIndex());
        model.addPropertyChangeListener(propertyReport);
        int foundIndex = 10;
        model.setFoundIndex(foundIndex);
        // assert changed value
        assertEquals("foundIndex must be set", foundIndex -1, model.getFoundIndex());
        assertTrue("changing foundIndex must have fired foundIndex property", 
                propertyReport.hasEvents("foundIndex"));
    }

    /**
     * test initial autoAdjust of foundIndex in forward search.
     *
     */
    @Test
    public void testFoundIndexAutoAdjustNotFound() {
        PatternModel model = new PatternModel();
        assertTrue("is autoAdjust by default", model.isAutoAdjustFoundIndex());
        model.addPropertyChangeListener(propertyReport);
        int foundIndex = -1;
        model.setFoundIndex(foundIndex);
        // assert changed value
        assertEquals("foundIndex must be set", foundIndex, model.getFoundIndex());
    }


    /**
     * test initial autoAdjust of foundIndex in forward search.
     *
     */
    @Test
    public void testFoundIndexAutoAdjustForward() {
        PatternModel model = new PatternModel();
        assertTrue("is autoAdjust by default", model.isAutoAdjustFoundIndex());
        model.addPropertyChangeListener(propertyReport);
        int foundIndex = 10;
        model.setFoundIndex(foundIndex);
        // assert changed value
        assertEquals("foundIndex must be set", foundIndex + 1, model.getFoundIndex());
        assertTrue("changing foundIndex must have fired foundIndex property", 
                propertyReport.hasEvents("foundIndex"));
    }

    /**
     * test initial value and notification of "foundIndex" property.
     *
     */
    @Test
    public void testFoundIndex() {
        PatternModel model = new PatternModel();
        model.setIncremental(true);
        // assert initial value
        assertEquals("not found on start", -1, model.getFoundIndex());
        model.addPropertyChangeListener(propertyReport);
        model.setFoundIndex(10);
        // assert changed value
        assertEquals("foundIndex must be set", 10, model.getFoundIndex());
        assertTrue("changing foundIndex must have fired foundIndex property", 
                propertyReport.hasEvents("foundIndex"));
    }
    
    @Test
    public void testCaseSensitive() {
        PatternModel model = new PatternModel();
        model.setRawText("tab");
        assertTrue("must find not case sensitive by default", 
                model.getPattern().matcher("JTABLE").find());
        model.addPropertyChangeListener(propertyReport);
        model.setCaseSensitive(true);
        assertTrue("changing case sensitive must fire casesensitive property", 
                propertyReport.hasEvents("caseSensitive"));
        assertTrue("changing case sensitive must fire pattern property", 
                propertyReport.hasEvents("pattern"));
        
    }
    @Test
    public void testAvailableMatchRules() {
        PatternModel model = new PatternModel();
        List<?> rules = model.getMatchRules();
        assertNotNull("rules must not be null", rules);
    }
    
    @Test
    public void testRegexCreator() {
        PatternModel model = new PatternModel();
        model.addPropertyChangeListener(propertyReport);
        model.setRegexCreatorKey(PatternModel.REGEX_UNCHANGED);
        assertEquals("search string mode must be", PatternModel.REGEX_UNCHANGED, model.getRegexCreatorKey());
        assertTrue(propertyReport.hasEvents("regexCreatorKey"));
        
//        model.setSearchStringMode(PatternModel.SEARCH_STRING_ANCHORED);
//        model.setSearchStringMode(PatternModel.SEARCH_STRING_WILDCARD);
//        model.setSearchStringMode(PatternModel.SEARCH_STRING_EXPLICIT);
    }
    
    @Test
    public void testMatchRule() {
        PatternModel model = new PatternModel();
        model.addPropertyChangeListener(propertyReport);
        // default searchStringMode
        assertEquals("search string mode must be", 
                PatternModel.REGEX_MATCH_RULES, model.getRegexCreatorKey());
        // default searchCategory
        assertEquals("search category must be ", 
                PatternModel.MATCH_RULE_CONTAINS, model.getMatchRule());
        // change category and test if property change is fired
        model.setMatchRule(PatternModel.MATCH_RULE_EQUALS);
        assertTrue("model must have fired " + "matchRule ", propertyReport.hasEvents("matchRule"));
    }
    
    @Test
    public void testChangeMatchRule() {
        PatternModel model = new PatternModel();
        String contained = "t";
        model.setRawText(contained);
        String match = "x" + contained + "x";
        assertTrue("pattern must find " + match, model.getPattern().matcher(match).find());
        model.addPropertyChangeListener(propertyReport);
        model.setMatchRule(PatternModel.MATCH_RULE_EQUALS);
        assertTrue("model must have fire pattern change", propertyReport.hasEvents("pattern"));
        assertFalse("pattern must reject " + match, model.getPattern().matcher(match).find());
        model.setMatchRule(PatternModel.MATCH_RULE_STARTSWITH);
        match = "txx";
        assertTrue("pattern must find " + match, model.getPattern().matcher(match).find());
        model.setMatchRule(PatternModel.MATCH_RULE_ENDSWITH);
        match = "xxt";
        assertTrue("pattern must find " + match, model.getPattern().matcher(match).find());
    }
    
    /**
     * test if rawtext is treated as literal.
     *
     */
    @Test
    public void testRawMiddleAnchorTokens() {
        PatternModel patternModel = new PatternModel();
        patternModel.setRawText(startAnchor);
        String literalAnchor = "some" + startAnchor +"one";
        assertTrue("must find literal containing startAnchor " + literalAnchor, patternModel.getPattern().matcher(literalAnchor).find());
        String literal = "someone";
        assertFalse("must reject literal not containing startAnchor " + literal, 
                patternModel.getPattern().matcher(literal).find());
    }
    
    /**
     * test if rawtext is treated as literal.
     *
     */
    @Test
    public void testRawStartAnchor() {
        PatternModel patternModel = new PatternModel();
        String anchored = startAnchor + "hap";
        patternModel.setRawText(anchored);
        String literalAnchor = startAnchor + "happy";
        assertTrue("must find literal containing startAnchor " + literalAnchor, patternModel.getPattern().matcher(literalAnchor).find());
        String literal = "happy";
        assertFalse("must reject literal not containing startAnchor " + literal, 
                patternModel.getPattern().matcher(literal).find());
    }

    @Override
    protected void setUp()  {
        propertyReport = new PropertyChangeReport();
    }
    
    
}
