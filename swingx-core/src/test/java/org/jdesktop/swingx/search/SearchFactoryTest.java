/*
 * $Id: SearchFactoryTest.java 3727 2010-07-14 16:56:18Z kschaefe $
 *
 * Copyright 2010 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx.search;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import javax.swing.UIManager;

import org.jdesktop.swingx.JXFindBar;
import org.jdesktop.swingx.JXFindPanel;
import org.jdesktop.test.EDTRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author kschaefer
 */
@RunWith(EDTRunner.class)
public class SearchFactoryTest {
    @Test
    public void testUpdateUI() throws Exception {
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        
        final boolean[] updated = new boolean[] {false, false};
        
        SearchFactory.setInstance(new SearchFactory() {
            /**
             * {@inheritDoc}
             */
            @Override
            public JXFindBar createFindBar() {
                return new JXFindBar() {
                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public void updateUI() {
                        updated[0] = true;
                        super.updateUI();
                    }
                };
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public JXFindPanel createFindPanel() {
                return new JXFindPanel() {
                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public void updateUI() {
                        updated[1] = true;
                        super.updateUI();
                    }
                };
            }
        });
        
        JXFindBar bar = SearchFactory.getInstance().getSharedFindBar();
        JXFindPanel panel = SearchFactory.getInstance().getSharedFindPanel();
        
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        
        assertThat(bar, is(sameInstance(SearchFactory.getInstance().getSharedFindBar())));
        assertThat(updated[0], is(true));
        assertThat(panel, is(sameInstance(SearchFactory.getInstance().getSharedFindPanel())));
        assertThat(updated[1], is(true));
    }
}
