/*
 * $Id: TableAsHighlighterClientTest.java 3223 2009-01-27 11:09:16Z kleopatra $
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
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;

import javax.swing.UIManager;
import javax.swing.table.TableColumn;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.table.TableColumnExt;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test JXTable as HighlighterClient.
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class TableAsHighlighterClientTest extends AbstractTestHighlighterClient {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(TableAsHighlighterClientTest.class.getName());
    
    /**
     * Test that columnHighlighters are updated.
     */
    @Test
    public void testUpdateUIColumnHighlighters() {
        // force loading of striping colors
        ColorHighlighter colorHighlighter = (ColorHighlighter) HighlighterFactory.createSimpleStriping();
        Color uiColor = UIManager.getColor("UIColorHighlighter.stripingBackground");
        if (uiColor == null) {
            LOG.info("cannot run test - no ui striping color");
            return;
        }
        assertSame("sanity", uiColor, colorHighlighter.getBackground());
        JXTable client = new JXTable(10, 3);
        for (TableColumn tableColumn : client.getColumns(true)) {
            ((TableColumnExt) tableColumn).addHighlighter(HighlighterFactory.createSimpleStriping());
        }
        Color changedUIColor = Color.RED;
        UIManager.put("UIColorHighlighter.stripingBackground", changedUIColor);
        client.updateUI();
        try {
            for (TableColumn tableColumn : client.getColumns(true)) {
                Highlighter hl = ((TableColumnExt) tableColumn).getHighlighters()[0];
                assertSame("support must update ui color", changedUIColor, 
                        ((ColorHighlighter) hl).getBackground());
            }
            
        } finally {
            UIManager.put("UIColorHighlighter.stripingBackground", uiColor);
        }
    }
    
    @Override
    protected HighlighterClient createHighlighterClient() {
        return createHighlighterClient(new JXTable());
    }

    private HighlighterClient createHighlighterClient(final JXTable table) {
        HighlighterClient client = new HighlighterClient() {

            public void addHighlighter(Highlighter highlighter) {
                table.addHighlighter(highlighter);
            }

            public void addPropertyChangeListener(PropertyChangeListener l) {
                table.addPropertyChangeListener(l);
            }

            public Highlighter[] getHighlighters() {
                return table.getHighlighters();
            }

            public void removeHighlighter(Highlighter highlighter) {
                table.removeHighlighter(highlighter);
            }

            public void removePropertyChangeListener(PropertyChangeListener l) {
                table.removePropertyChangeListener(l);
            }

            public void setHighlighters(Highlighter... highlighters) {
                table.setHighlighters(highlighters);
            }

            public void updateUI() {
                table.updateUI();
            }
            
        };
        return client;
    }

}
