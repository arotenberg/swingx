/*
 * $Id: HighlighterClientVisualCheck.java 4229 2012-08-08 15:27:09Z kleopatra $
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
import java.awt.ComponentOrientation;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.decorator.HighlightPredicate.ColumnHighlightPredicate;
import org.jdesktop.swingx.decorator.HighlighterFactory.UIColorHighlighter;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.IconValue;
import org.jdesktop.swingx.renderer.StringValues;
import org.jdesktop.swingx.test.XTestUtils;
import org.jdesktop.swingx.treetable.FileSystemModel;
import org.jdesktop.test.AncientSwingTeam;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * TODO add type doc
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class HighlighterClientVisualCheck extends InteractiveTestCase  {
    
    public static void main(String[] args) {
        HighlighterClientVisualCheck test = new HighlighterClientVisualCheck();
        try {
            test.runInteractiveTests();
//            test.runInteractiveTests("interactive.*ToolTip.*");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void interactiveComponentOrientation() {
        JXTable table = new JXTable(new AncientSwingTeam());
        ComponentOrientation orientation = table.getComponentOrientation().isLeftToRight() ? 
                ComponentOrientation.RIGHT_TO_LEFT : ComponentOrientation.LEFT_TO_RIGHT;
        table.getColumnExt(0).addHighlighter(new ComponentOrientationHighlighter(orientation ));
        showWithScrollingInFrame(table, "Alternative CO in first column");
    }

    /**
     * Issue #1317-swingx: PatternPredicate throws exception if testColumn -1 (== all)
     */
    public void interactivePatternPredicate() {
        JXTable table = new JXTable(new AncientSwingTeam());
        final ColorHighlighter hl = new ColorHighlighter(HighlightPredicate.NEVER, Color.LIGHT_GRAY, null);
        table.addHighlighter(hl);
        JXFrame frame = wrapWithScrollingInFrame(table, "use pattern predicate");
        Action action = new AbstractActionExt("togglePredicate") {
            
            PatternPredicate testFirstHighlightAll = new PatternPredicate(".*a.*", 0, PatternPredicate.ALL);
            PatternPredicate testFirstHighlightFirst = new PatternPredicate(".*a.*", 0, 0);
            PatternPredicate testFirstHighlightSecond = new PatternPredicate(".*a.*", 0, 1);
            PatternPredicate testAllHighlightFirst = new PatternPredicate(".*a.*", PatternPredicate.ALL, 0);
            PatternPredicate testAllHighlightAll = new PatternPredicate(".*a.*", PatternPredicate.ALL, PatternPredicate.ALL);
            int current;
            
            HighlightPredicate[] predicates = new HighlightPredicate[] {
               HighlightPredicate.NEVER,
               testFirstHighlightAll,
               testFirstHighlightFirst,
               testFirstHighlightSecond,
               testAllHighlightFirst,
               testAllHighlightAll,
            };
            
            @Override
            public void actionPerformed(ActionEvent e) {
                current++;
                if (current == predicates.length) {
                    current = 0;
                }
                hl.setHighlightPredicate(predicates[current]);
            }
        };
        addAction(frame, action);
        show(frame);
    }
    
    

    /**
     * Regression Issue ?? swingx: column highlighter change must update view.
     */
    public void interactiveColumnHighlighterChange() {
        final ColorHighlighter hl = new ColorHighlighter(HighlightPredicate.ODD, Color.RED, Color.BLACK);
        JXTable table = new JXTable(new AncientSwingTeam());
        table.getColumnExt(0).addHighlighter(hl);
        Action action = new AbstractAction("toggle column color") {

            @Override
            public void actionPerformed(ActionEvent e) {
                Color old = hl.getBackground();
                hl.setBackground(old == Color.red ? Color.ORANGE : Color.RED);
               
            }
            
        };
        table.addHighlighter(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, null, Color.RED));
        JXFrame frame = wrapWithScrollingInFrame(table, "column highlighter update");
        addAction(frame, action);
        addMessage(frame, "toggle column color between orange/red must update immediately");
        show(frame);
    }


    
    /**
     * UI-dependent Column highlighter must updated on updateUI.
     */
    public void interactiveColumnHighlighterUpdateUI() {
        JXTable table = new JXTable(new AncientSwingTeam());
        table.getColumnExt(2).addHighlighter(new UIColorHighlighter());
        table.addHighlighter(new UIColorHighlighter(new ColumnHighlightPredicate(1)));
        showWithScrollingInFrame(table, "UpdateUI - table highlighter in second, column highlighter in third");
    }

    /**
     * Issue 1314: visual interaction of truncated text and tool tip highlighter.
     */
    public void interactiveToolTipOnTruncatedText() {
        JXTable table = new JXTable(new AncientSwingTeam());
        final Icon icon = XTestUtils.loadDefaultIcon();
        IconValue iv = new IconValue() {
            
            @Override
            public Icon getIcon(Object value) {
                return icon;
            }
        };
        table.getColumn(AncientSwingTeam.COLOR_COLUMN).setCellRenderer(new DefaultTableRenderer(StringValues.TO_STRING, iv));
        table.addHighlighter(new ToolTipHighlighter(HighlightPredicate.IS_TEXT_TRUNCATED));
        JXFrame frame = showWithScrollingInFrame(table, "ToolTip on truncated text");
        addComponentOrientationToggle(frame);
    }
}
