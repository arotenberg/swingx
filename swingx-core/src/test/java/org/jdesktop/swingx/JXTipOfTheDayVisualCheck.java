/*
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * California 95054, U.S.A. All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */
package org.jdesktop.swingx;

import java.awt.event.ActionEvent;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JTree;

import org.jdesktop.swingx.plaf.basic.BasicTipOfTheDayUI;
import org.jdesktop.swingx.tips.DefaultTip;
import org.jdesktop.swingx.tips.DefaultTipOfTheDayModel;
import org.jdesktop.swingx.tips.TipOfTheDayModel;

/**
 * Simple tests to ensure that the {@code JXDatePicker} can be instantiated and
 * displayed.
 * 
 * @author Karl Schaefer
 */
public class JXTipOfTheDayVisualCheck extends InteractiveTestCase {
    public JXTipOfTheDayVisualCheck() {
        super("JXLoginPane Test");
    }

    public static void main(String[] args) throws Exception {
        // setSystemLF(true);
        JXTipOfTheDayVisualCheck test = new JXTipOfTheDayVisualCheck();
        
        try {
            test.runInteractiveTests();
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }
    
    
    /**
     * Issue #538-swingx Failure to set locale at runtime
     *
     */
    public void interactiveDisplay() {
        JXTipOfTheDay tip = new JXTipOfTheDay(createTipOfTheDayModel());
        tip.showDialog(new JFrame());
//        showInFrame(new JXTipOfTheDay(), "tip");
    }
    
    private TipOfTheDayModel createTipOfTheDayModel() {
        // Create a tip model with some tips
        DefaultTipOfTheDayModel tips = new DefaultTipOfTheDayModel();
        
        // plain text
        tips
          .add(new DefaultTip(
            "tip1",
            "This is the first tip " +
            "This is the first tip " +
            "This is the first tip " +
            "This is the first tip " +
            "This is the first tip " +
            "This is the first tip\n" +
            "This is the first tip " +
            "This is the first tip"));
        
        // html text
        tips
          .add(new DefaultTip(
            "tip2",
            "<html>This is an html <b>TIP</b><br><center>" +
            "<table border=\"1\">" +
            "<tr><td>1</td><td>entry 1</td></tr>" +
            "<tr><td>2</td><td>entry 2</td></tr>" +
            "<tr><td>3</td><td>entry 3</td></tr>" +
            "</table>"));
        
        // a Component
        tips.add(new DefaultTip("tip3", new JTree()));

        // an Icon
        tips
          .add(new DefaultTip(
            "tip 4",
            new ImageIcon(BasicTipOfTheDayUI.class.getResource("resources/TipOfTheDay24.gif"))));

        return tips;
    }

    @Override
    protected void createAndAddMenus(JMenuBar menuBar, final JComponent component) {
        super.createAndAddMenus(menuBar, component);
        JMenu menu = new JMenu("Locales");
        menu.add(new AbstractAction("Change Locale") {

            public void actionPerformed(ActionEvent e) {
                if (component.getLocale() == Locale.FRANCE) {
                    component.setLocale(Locale.ENGLISH);
                } else {
                    component.setLocale(Locale.FRANCE);
                }
            }});
        menuBar.add(menu);
    }
    

    /**
     * Do nothing, make the test runner happy
     * (would output a warning without a test fixture).
     *
     */
    public void testDummy() {
        
    }

}
