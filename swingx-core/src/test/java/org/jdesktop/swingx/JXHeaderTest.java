/*
 * $Id: JXHeaderTest.java 3473 2009-08-27 13:17:10Z kleopatra $
 *
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.Font;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.IconUIResource;

import junit.framework.TestCase;

import org.jdesktop.swingx.test.XTestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit test for <code>JXHeader</code>.
 * <p>
 *
 * All test methods in this class are expected to pass.
 * 
 * PENDING JW: some of the tests are dirty in that they rely on hidden (from the
 * perspective of the JXHeader) implementation
 * details of BasicHeaderUI (like how the different parts are implemented). 
 * What/How else to test that? A minor improvement could be reached by testing
 * the ui delegate directly, as the children are visible to subclasses. So we 
 * would end up testing its contract to extension.  
 *
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class JXHeaderTest extends TestCase {

//---------------- testing icon property, similar to #925
// except that it's not a property normally controlled by the ui defaults.
    
    /**
     * Test that header's icon property set to default value, if any.
     */
    @Test
    public void testIconDefaultA() {
        Icon icon = XTestUtils.loadDefaultIcon();
        assertNotNull("sanity: default icon loaded", icon);
        UIManager.put("Header.defaultIcon", icon);
        JXHeader header = new JXHeader();
        try {
            assertSame(icon, header.getIcon());
        } finally {
            UIManager.put("Header.defaultIcon", null);
        }
    }


    /**
     * Test that header's icon property set to default value, if any.
     */
    @Test
    public void testIconLabelDefaultB() {
        Icon icon = XTestUtils.loadDefaultIcon();
        assertNotNull("sanity: default icon loaded", icon);
        UIManager.put("Header.defaultIcon", icon);
        JXHeader header = new JXHeader();
        try {
            assertSame(icon, getIconLabel(header).getIcon());
        } finally {
            UIManager.put("Header.defaultIcon", null);
        }
    }

    /**
     * Test that header's icon property set to default value, if any.
     */
    @Test
    public void testIconLabelCustomC() {
        Icon icon = XTestUtils.loadDefaultIcon();
        assertNotNull("sanity: default icon loaded", icon);
        JXHeader header = new JXHeader();
        header.setIcon(icon);
        SwingUtilities.updateComponentTreeUI(header);
        assertSame(icon, header.getIcon());
        assertSame(icon, getIconLabel(header).getIcon());
    }

    /**
     * Test that header's icon property set to default value, if any.
     */
    @Test
    public void testIconLabelDefaultUpdateD() {
        Icon uiDefault = UIManager.getIcon("Header.defaultIcon");
        Icon icon = new IconUIResource(XTestUtils.loadDefaultIcon());
        assertNotNull("sanity: default icon loaded", icon);
        JXHeader header = new JXHeader();
        header.setIcon(icon);
        assertSame(icon, getIconLabel(header).getIcon());
        SwingUtilities.updateComponentTreeUI(header);
        assertSame(uiDefault, header.getIcon());
        assertSame(uiDefault, getIconLabel(header).getIcon());
    }
    
/*
 * ----------- start testing #925-swingx
 * 
 * there are several sub-issues: A - property on JXHeader must be set to
 * defaults B - corresponding property on the appropriate child must be set C -
 * (sanity) custom property must be kept on both header and child after laf
 * change D - property on both header and child must be updated to defaults on
 * laf change if not custom
 * 
 * The critical (child) properties are those which have default ui properties
 * controlled by the header, that is font and foreground of description and
 * title label. So we have one group comprised of tests A-D for each of them,
 * denoted by the respective postfix.
 * 
 * PENDING JW: any way to auto-create this tests? The groups are created by c&p
 * and replace ... error-prone and in fact, introduced testing errors. Which
 * passed accidentally because the tested properties had the same default value
 * (f.i. Color.BLACK or so).
 * 
 */    
//-------- property: description font   
    /**
     * Issue #925-swingx: custom properties lost on updateUI.
     * Header property set to uimanager setting.
     */
    @Test
    public void testUpdateUIDescriptionFontA() {
        Font color = UIManager.getFont("JXHeader.descriptionFont");
        assertNotNull("sanity: description font available", color);
        JXHeader header = new JXHeader();
        assertEquals(color, header.getDescriptionFont());
    }

    /**
     * Issue #925-swingx: custom properties lost on updateUI.
     * Description label property set to uimanager setting.
     */
    @Test
    public void testUpdateUIDescriptionLabelFontB() {
        Font color = UIManager.getFont("JXHeader.descriptionFont");
        assertNotNull("sanity: description font available", color);
        JXHeader header = new JXHeader();
        assertEquals(color, getDescriptionLabel(header).getFont());
    }
    
    /**
     * Issue #925-swingx: custom properties lost on updateUI.
     * Description label custom property kept on LAF change.
     */
    @Test
    public void testUpdateUICustomDescriptionLabelFontC() {
        Font color = new Font("serif", Font.BOLD, 36);
        JXHeader header = new JXHeader();
        header.setDescriptionFont(color);
        assertEquals("sanity: description color taken", color, getDescriptionLabel(header).getFont());
        SwingUtilities.updateComponentTreeUI(header);
        assertEquals(color, header.getDescriptionFont());
        assertEquals(color, getDescriptionLabel(header).getFont());
    }
    
    /**
     * Issue #925-swingx: custom properties lost on updateUI.
     * Title label property updated to ui default on laf change.
     */
    @Test
    public void testUpdateUIDefaultDescriptionLabelFontD() {
        Font uiDefault = new FontUIResource("serif", Font.PLAIN, 36);
        UIManager.put("JXHeader.descriptionFont", uiDefault);
        Font color = new FontUIResource("serif", Font.ITALIC, 20);
        JXHeader header = new JXHeader();
        header.setDescriptionFont(color);
        try {
            assertEquals(color, header.getDescriptionFont());
            assertEquals(color, getDescriptionLabel(header).getFont());
            SwingUtilities.updateComponentTreeUI(header);
            
            assertEquals(uiDefault, header.getDescriptionFont());
            assertEquals(uiDefault, getDescriptionLabel(header).getFont());
        } finally {
            // reset custom property
           UIManager.put("JXHeader.descriptionFont", null);   
        }
    }


    
    
//--------------   property: description foreground 
    /**
     * Issue #925-swingx: custom properties lost on updateUI.
     * Header property set to uimanager setting.
     */
    @Test
    public void testUpdateUIDescriptionForegroundA() {
        Color color = UIManager.getColor("JXHeader.descriptionForeground");
        assertNotNull("sanity: description font available", color);
        JXHeader header = new JXHeader();
        assertEquals(color, header.getDescriptionForeground());
    }

    /**
     * Issue #925-swingx: custom properties lost on updateUI.
     * Description label property set to uimanager setting.
     */
    @Test
    public void testUpdateUIDescriptionLabelForegroundB() {
        Color color = UIManager.getColor("JXHeader.descriptionForeground");
        assertNotNull("sanity: description font available", color);
        JXHeader header = new JXHeader();
        assertEquals(color, getDescriptionLabel(header).getForeground());
    }
    
    /**
     * Issue #925-swingx: custom properties lost on updateUI.
     * Description label custom property kept on LAF change.
     */
    @Test
    public void testUpdateUICustomDescriptionLabelForegroundC() {
        Color color = Color.PINK;
        JXHeader header = new JXHeader();
        header.setDescriptionForeground(color);
        assertEquals("sanity: description color taken", color, getDescriptionLabel(header).getForeground());
        SwingUtilities.updateComponentTreeUI(header);
        assertEquals(color, header.getDescriptionForeground());
        assertEquals(color, getDescriptionLabel(header).getForeground());
    }
    
    /**
     * Issue #925-swingx: custom properties lost on updateUI.
     * Description label property updated to ui default on laf change.
     */
    @Test
    public void testUpdateUIDefaultDescriptionLabelForegroundD() {
        Color uiDefault = new ColorUIResource(Color.BLUE);
        UIManager.put("JXHeader.descriptionForeground", uiDefault);
        Color color = new ColorUIResource(Color.PINK);
        JXHeader header = new JXHeader();
        header.setDescriptionForeground(color);
        SwingUtilities.updateComponentTreeUI(header);
        try {
            
            assertEquals(uiDefault, header.getDescriptionForeground());
            assertEquals(uiDefault, getDescriptionLabel(header).getForeground());
        } finally {
            // reset custom property
           UIManager.put("JXHeader.descriptionForeground", null);   
        }
    }


//----------- property title foreground    
    /**
     * Issue #925-swingx: custom properties lost on updateUI.
     * Header property set to uimanager setting.
     */
    @Test
    public void testUpdateUITitleForegroundA() {
        Color color = UIManager.getColor("JXHeader.titleForeground");
        assertNotNull("sanity: title foreground available", color);
        JXHeader header = new JXHeader();
        assertEquals(color, header.getTitleForeground());
    }

    /**
     * Issue #925-swingx: custom properties lost on updateUI.
     * Title label property set to uimanager setting.
     */
    @Test
    public void testUpdateUITitleLabelForegroundB() {
        Color color = UIManager.getColor("JXHeader.titleForeground");
        assertNotNull("sanity: title foreground available", color);
        JXHeader header = new JXHeader();
        assertEquals(color, getTitleLabel(header).getForeground());
    }
    
    /**
     * Issue #925-swingx: custom properties lost on updateUI.
     * Title label custom property kept on LAF change.
     */
    @Test
    public void testUpdateUICustomTitleLabelForegroundC() {
        Color color = Color.PINK;
        JXHeader header = new JXHeader();
        header.setTitleForeground(color);
        assertEquals("sanity: title foreground taken", color, getTitleLabel(header).getForeground());
        SwingUtilities.updateComponentTreeUI(header);
        assertEquals(color, header.getTitleForeground());
        assertEquals(color, getTitleLabel(header).getForeground());
    }
    
    /**
     * Issue #925-swingx: custom properties lost on updateUI.
     * Title label property updated to ui default on laf change.
     */
    @Test
    public void testUpdateUIDefaultTitleLabelForegroundD() {
        Color uiDefault = new ColorUIResource(Color.BLUE);
        UIManager.put("JXHeader.titleForeground", uiDefault);
        Color color = new ColorUIResource(Color.PINK);
        JXHeader header = new JXHeader();
        header.setTitleForeground(color);
        SwingUtilities.updateComponentTreeUI(header);
        try {
            
            assertEquals(uiDefault, header.getTitleForeground());
            assertEquals(uiDefault, getTitleLabel(header).getForeground());
        } finally {
            // reset custom property
           UIManager.put("JXHeader.titleForeground", null);   
        }
    }

//---------- property: title font    
    /**
     * Issue #925-swingx: custom properties lost on updateUI.
     * Header property set to uimanager setting.
     */
    @Test
    public void testUpdateUITitleFontA() {
        Font font = UIManager.getFont("JXHeader.titleFont");
        assertNotNull("sanity: title font available", font);
        JXHeader header = new JXHeader();
        assertEquals(font, header.getTitleFont());
    }

    
    /**
     * Issue #925-swingx: custom properties lost on updateUI.
     * Test title label property set to UIManager prop
     */
    @Test
    public void testUpdateUITitleLabelFontB() {
        Font font = UIManager.getFont("JXHeader.titleFont");
        assertNotNull("sanity: title font available", font);
        JXHeader header = new JXHeader();
        assertEquals(font, getTitleLabel(header).getFont());
    }

    
    /**
     * Issue #925-swingx: custom properties lost on updateUI.
     * Title label custom property kept on LAF change.
     */
    @Test
    public void testUpdateUICustomTitleLabelFontC() {
        Font color = new Font("serif", Font.BOLD, 36);
        JXHeader header = new JXHeader();
        header.setTitleFont(color);
        assertEquals("sanity: title color taken", color, getTitleLabel(header).getFont());
        SwingUtilities.updateComponentTreeUI(header);
        assertEquals(color, header.getTitleFont());
        assertEquals(color, getTitleLabel(header).getFont());
    }
    
    /**
     * Issue #925-swingx: custom properties lost on updateUI.
     * Title label property updated to ui default on laf change.
     */
    @Test
    public void testUpdateUIDefaultTitleLabelFontD() {
        Font uiDefault = new FontUIResource("serif", Font.PLAIN, 36);
        UIManager.put("JXHeader.titleFont", uiDefault);
        Font color = new FontUIResource("serif", Font.ITALIC, 20);
        JXHeader header = new JXHeader();
        header.setTitleFont(color);
        try {
            assertEquals(color, header.getTitleFont());
            assertEquals(color, getTitleLabel(header).getFont());
            SwingUtilities.updateComponentTreeUI(header);
            
            assertEquals(uiDefault, header.getTitleFont());
            assertEquals(uiDefault, getTitleLabel(header).getFont());
        } finally {
            // reset custom property
           UIManager.put("JXHeader.titleFont", null);   
        }
    }


//--------- end testing #925
    
    
    /**
     * Issue #695-swingx: not-null default values break class invariant.
     * Here initial empty constructor.
     */
    @Test
    public void testTitleSynchInitialEmpty() {
        JXHeader header = new JXHeader();
        assertEquals(header.getTitle(), getTitleLabel(header).getText());
    }


    /**
     * Issue #695-swingx: not-null default values break invariant.
     * Here: initial not-null explicitly set to null and updateUI (to
     * simulate LF toggle).
     */
    @Test
    public void testTitleSynchUpdateUI() {
        JXHeader header = new JXHeader("dummy", null);
        header.setTitle(null);
        header.updateUI();
        assertEquals(header.getTitle(), getTitleLabel(header).getText());
    }

    /**
     * Issue #695-swingx: not-null default values break invariant.
     * Here: initial null params constructor.
     */
    @Test
    public void testTitleSynchInitialNull() {
        JXHeader header = new JXHeader(null, null);
        header.setTitle(null);
        assertEquals(header.getTitle(), getTitleLabel(header).getText());
    }


    /**
     * Issue #403-swingx: JXHeader doesn't show custom values.
     *
     */
    @Test
    public void testIconSet() {
        URL url = getClass().getResource("resources/images/wellTop.gif");
        Icon icon = new ImageIcon(url);
        assertNotNull(url);
        JXHeader header = new JXHeader();
        header.setIcon(icon);
        // sanity: the property is set
        assertEquals(icon, header.getIcon());
        // fishing in the internals ... not really safe, there are 2 labels and 1 jxlabel ... indeed not safe!
        assertEquals("the label's text must be equal to the headers title",
                header.getIcon(), getIconLabel(header).getIcon());
    }

    /**
     * Issue #403-swingx: JXHeader doesn't show custom values.
     *
     */
    @Test
    public void testTitleSet() {
        JXHeader header = new JXHeader();
        String title = "customTitle";
        header.setTitle(title);
        // sanity: the property is set
        assertEquals(title, header.getTitle());
        assertEquals("the label's text must be equal to the headers title",
                header.getTitle(), getTitleLabel(header).getText());
    }

    /**
     * Issue #403-swingx: JXHeader doesn't show custom values.
     * <p>
     *
     * Breaking if values are passed in the constructor.
     */
    @Test
    public void testTitleInContructor() {
        String title = "customTitle";
        JXHeader header = new JXHeader(title, null);
        // sanity: the property is set
        assertEquals(title, header.getTitle());
        assertEquals("the label's text must be equal to the headers title",
                header.getTitle(), getTitleLabel(header).getText());
    }

    /**
     * Issue swingx-900 NPE when top level ancestor is not available, while "some" ancestor is.
     */
    @Test
    public void testNPE() {
        JXHeader header = new JXHeader();
        JPanel panel = new JPanel();
        panel.add( header );
        panel.setBounds( 0, 0, 200, 200 );
    }

// ----------------- private helpers (dirty!)    
    /**
     * Returns the label used for painting the title. Implemented to 
     * return the first child of type JLabel. <p>
     * 
     * NOTE: this is fishing in implementation details of BasicHeaderUI!
     * 
     * @return the label used for painting the title.
     */
    private JLabel getTitleLabel(JXHeader header) {
        for (int i = 0; i < header.getComponentCount(); i++) {
            if (header.getComponent(i) instanceof JLabel) {
                return (JLabel) header.getComponent(i);
            }
        }
        return null;
    }

    /**
     * Returns the label used for painting the description. Implemented to 
     * return the first child of type JXLabel. <p>
     * 
     * NOTE: this is fishing in implementation details of BasicHeaderUI!
     * 
     * @return the label used for painting the description.
     */
    private JLabel getDescriptionLabel(JXHeader header) {
        for (int i = 0; i < header.getComponentCount(); i++) {
            if (header.getComponent(i) instanceof JXLabel) {
                return (JLabel) header.getComponent(i);
            }
         }
        return null;
    }

    /**
     * Returns the label used for painting the icon. Implemented to 
     * return the second child of type JLabel and not of type JXLabel. <p>
     * 
     * NOTE: this is fishing in implementation details of BasicHeaderUI!
     * 
     * @return the label used for painting the icon.
     */
    private JLabel getIconLabel(JXHeader header) {
        JLabel label = null;
        for (int i = 0; i < header.getComponentCount(); i++) {
            if (header.getComponent(i) instanceof JLabel && !(header.getComponent(i) instanceof JXLabel)) {
                boolean second = label != null;
                label = (JLabel) header.getComponent(i);
                if (second) {
                    return label;
                }
            }
        }
        return null;
    }

    
    @Override
    protected void setUp() throws Exception {
        // forcing load of headerAddon
        new JXHeader();
    }

}
