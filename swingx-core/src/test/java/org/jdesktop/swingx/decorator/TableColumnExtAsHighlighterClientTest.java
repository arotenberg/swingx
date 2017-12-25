/*
 * $Id: TableColumnExtAsHighlighterClientTest.java 3473 2009-08-27 13:17:10Z kleopatra $
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

import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.test.PropertyChangeReport;
import org.jdesktop.test.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test JXTable as HighlighterClient.
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class TableColumnExtAsHighlighterClientTest extends AbstractTestHighlighterClient {

    

    @Test
    public void testHighlighterStateChangeNotification() {
        TableColumnExt columnExt = new TableColumnExt();
        ColorHighlighter hl = new ColorHighlighter();
        columnExt.addHighlighter(hl);
        PropertyChangeReport report = new PropertyChangeReport();
        columnExt.addPropertyChangeListener(report);
        hl.setBackground(Color.RED);
        TestUtils.assertPropertyChangeEvent(report, "highlighterStateChanged", false, true);
    }

    @Override
    protected HighlighterClient createHighlighterClient() {
        return createHighlighterClient(new TableColumnExt());
    }

    private HighlighterClient createHighlighterClient(final TableColumnExt table) {
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
