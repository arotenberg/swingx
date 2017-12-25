/*
 * $Id$
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
 *
 */
package org.jdesktop.swingx.plaf.nimbus;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JCheckBox;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.test.PropertyChangeReport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class RawNimbusIssues extends InteractiveTestCase {

    /**
     * 
     */
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(RawNimbusIssues.class
            .getName());
    
    private static final String ALTERNATE_ROW_COLOR = "Table.alternateRowColor";
    private static final String TABLE_BACKGROUND = "Table.background";
    
    @Test
    public void testBackground() {
        LOG.info("back color? " + UIManager.get(TABLE_BACKGROUND));
        JCheckBox box = new JCheckBox();
        LOG.info(" checkbox? " + box.getBackground());
    }
    /**
     * Core Issue ??: Nimbus install must be complete on propertyChangeNotification
     *   from UIManager.
     *   
     * The error is to "derive" the color only after firing, that is when receiving
     * the notification of the change to itself (how weird is that?) and 
     * changing the color value without notifying listeners to the defaults.
     * 
     * Here we test the alternative: if the color is not yet completely installed
     * at the time of firing a lookAndFeel property change, then at least the 
     * color must fire a property change on derive (as is documented in the 
     * api doc, but not done)
     * 
     * @throws Exception
     */
    @Test
    public void testColorInCompleteFireChange() throws Exception {
        setLookAndFeel("Metal");
        final List<Color> colors = new ArrayList<Color>();
        final List<Integer> rgb = new ArrayList<Integer>();
        final PropertyChangeReport report = new PropertyChangeReport();
        PropertyChangeListener l = new PropertyChangeListener() {
            
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                UIManager.getLookAndFeelDefaults().addPropertyChangeListener(report);
                colors.add(UIManager.getColor(ALTERNATE_ROW_COLOR));
                rgb.add(colors.get(0).getRGB());
                
            }
        };
        try {
            
            UIManager.addPropertyChangeListener(l);
            setLookAndFeel("Nimbus");
            int rgbOrg = rgb.get(0);
            if (colors.get(0).getRGB() != rgbOrg) {
                assertTrue("lookandFeelDefaults must have fired color change notification", 
                        report.hasEvents());
            }
        } finally {
            removeListeners(l, report);
        }
    }
    /**
     * Core Issue ??: Nimbus install must be complete on propertyChangeNotification
     *   from UIManager.
     *   
     * The error is to "derive" the color only after firing, that is when receiving
     * the notification of the change to itself (how weird is that?) and 
     * changing the color value without notifying listeners to the defaults.
     * 
     * Here we test that the color is the same instance as when the lookAndFeel
     * property change and that it is unchanged if no event fired from the 
     * lookandfeelDefaults
     *   
     * @throws Exception
     */
    @Test
    public void testColorCompleteInstalled() throws Exception {
        setLookAndFeel("Metal");
        final List<Color> colors = new ArrayList<Color>();
        final List<Integer> rgb = new ArrayList<Integer>();
        final PropertyChangeReport report = new PropertyChangeReport();
        PropertyChangeListener l = new PropertyChangeListener() {
            
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                UIManager.getLookAndFeelDefaults().addPropertyChangeListener(report);
                colors.add(UIManager.getColor(ALTERNATE_ROW_COLOR));
                rgb.add(colors.get(0).getRGB());
               
            }
        };
        try {
            
            UIManager.addPropertyChangeListener(l);
            setLookAndFeel("Nimbus");
            assertSame("Color installed when firing property change", 
                    colors.get(0), UIManager.getColor(ALTERNATE_ROW_COLOR));
            if (!report.hasEvents()) {
                int rgbOrg = rgb.get(0);
                assertEquals("Color must be unchanged compared to original", 
                        rgbOrg, colors.get(0).getRGB());
            }
        } finally {
            removeListeners(l, report);
        }
    }

    /**
     * Core issue: UIDefaults not firing on remove.
     * 
     * Reported with review id: 1610127
     */
    @Test
    public void testUIDefaultsNotificationRemove() {
        UIDefaults properties = new UIDefaults();
        PropertyChangeReport report = new PropertyChangeReport();
        properties.addPropertyChangeListener(report);
        Object value = new Object();
        properties.put("somevalue", value);
        assertEquals(1, report.getEventCount("somevalue"));
        report.clear();
        properties.remove("somevalue");
        assertNull("sanity: value removed", properties.get("somevalue"));
        assertEquals("uidefaults must fire (here: remove)", 1, report.getEventCount("somevalue"));
    }
    
    /**
     * Core issue: UIDefaults not firing on remove.
     * 
     * Reported with review id: 1610127
     * 
     * This tests the workaround: don't use remove but put(key, null) instead.
     */
    @Test
    public void testUIDefaultsNotificationPutNull() {
        UIDefaults properties = new UIDefaults();
        Object value = new Object();
        properties.put("somevalue", value);
        PropertyChangeReport report = new PropertyChangeReport();
        properties.addPropertyChangeListener(report);
        properties.put("somevalue", null);
        assertEquals("uidefaults must fire (here: put(.. null))", 1, report.getEventCount("somevalue"));
    }
    
    
    /**
     * Removes listeners installed on UIManager and lookAndFeelDefaults.
     * 
     * @param managerListener the listener the remove from the UIManager
     * @param lookAndFeelDefaultsListener the listener to remove from the 
     *    lookAndFeelDefaults
     */
    private void removeListeners(PropertyChangeListener managerListener, 
            PropertyChangeListener lookAndFeelDefaultsListener) {
        UIManager.removePropertyChangeListener(managerListener);
        UIManager.getLookAndFeelDefaults().removePropertyChangeListener(lookAndFeelDefaultsListener);
        
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        // force a new instance for each round
        setLookAndFeel("Nimbus");
        assertEquals("sanity: nothing loaded", null, UIManager.get("DatePickerUI"));
    }

}
