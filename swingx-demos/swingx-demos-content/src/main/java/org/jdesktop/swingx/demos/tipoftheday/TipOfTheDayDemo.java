/*
 * $Id: TipOfTheDayDemo.java 4097 2011-11-30 19:22:13Z kschaefe $
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
 */
package org.jdesktop.swingx.demos.tipoftheday;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.SwingUtilities;

import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationActionMap;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTipOfTheDay;
import org.jdesktop.swingx.VerticalLayout;
import org.jdesktop.swingx.plaf.basic.BasicTipOfTheDayUI;
import org.jdesktop.swingx.tips.DefaultTip;
import org.jdesktop.swingx.tips.DefaultTipOfTheDayModel;
import org.jdesktop.swingx.tips.TipOfTheDayModel;

import com.sun.swingset3.DemoProperties;

/**
 * A demo for the {@code JXTipOfTheDay}.
 * 
 * @author Karl George Schaefer
 * @author l2fprod (original JXTipOfTheDayDemoPanel)
 */
@DemoProperties(
    value = "JXTipOfTheDay Demo",
    category = "Containers",
    description = "Demonstrates JXTipOfTheDay, a container for tips and other arbitrary components.",
    sourceFiles = {
        "org/jdesktop/swingx/demos/tipoftheday/TipOfTheDayDemo.java",
        "org/jdesktop/swingx/demos/tipoftheday/resources/TipOfTheDayDemo.properties",
        "org/jdesktop/swingx/demos/tipoftheday/resources/TipOfTheDayDemo.html",
        "org/jdesktop/swingx/demos/tipoftheday/resources/images/TipOfTheDayDemo.png",
        "org/jdesktop/swingx/plaf/basic/resources/TipOfTheDay24.gif"
})
@SuppressWarnings("serial")
public class TipOfTheDayDemo extends JPanel {
    private TipOfTheDayModel model;
    private JXTipOfTheDay totd;
    private JXHyperlink nextTipLink;
    private JXHyperlink dialogLink;

    /**
     * main method allows us to run as a standalone demo.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame(TipOfTheDayDemo.class.getAnnotation(DemoProperties.class).value());

                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(new TipOfTheDayDemo());
                frame.setPreferredSize(new Dimension(800, 600));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    public TipOfTheDayDemo() {
        super(new BorderLayout());

        createTipOfTheDayDemo();

        Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(this);

        bind();
    }

    private void createTipOfTheDayDemo() {
        model = createTipOfTheDayModel();

        JXPanel panel = new JXPanel(new VerticalLayout());
        totd = new JXTipOfTheDay(model);
        totd.setName("totd");
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(totd);

        nextTipLink = new JXHyperlink();
        nextTipLink.setName("nextTipLink");
        panel.add(nextTipLink, BorderLayout.SOUTH);
        add(panel);
        
        dialogLink = new JXHyperlink();
        dialogLink.setName("dialogLink");
        add(dialogLink, BorderLayout.SOUTH);
    }

    protected TipOfTheDayModel createTipOfTheDayModel() {
        // Create a tip model with some tips
        DefaultTipOfTheDayModel tips = new DefaultTipOfTheDayModel();

        // plain text
        tips.add(new DefaultTip("Plain Text Tip", "This is the first tip " + "This is the first tip "
                + "This is the first tip " + "This is the first tip " + "This is the first tip "
                + "This is the first tip\n" + "This is the first tip " + "This is the first tip"));

        // html text
        tips.add(new DefaultTip("HTML Text Tip", "<html>This is an html <b>TIP</b><br><center>"
                + "<table border=\"1\">" + "<tr><td>1</td><td>entry 1</td></tr>"
                + "<tr><td>2</td><td>entry 2</td></tr>" + "<tr><td>3</td><td>entry 3</td></tr>"
                + "</table>"));

        // a Component
        tips.add(new DefaultTip("Component Tip", new JTree()));

        // an Icon
        tips.add(new DefaultTip("Icon Tip", new ImageIcon(BasicTipOfTheDayUI.class
                .getResource("resources/TipOfTheDay24.gif"))));

        return tips;
    }
    
    private void bind() {
        ApplicationActionMap map = Application.getInstance().getContext().getActionMap(this);

        nextTipLink.addActionListener(totd.getActionMap().get("nextTip"));
        dialogLink.addActionListener(map.get("displayTipDialog"));
    }

    @org.jdesktop.application.Action
    public void displayTipDialog() {
        JXTipOfTheDay totd = new JXTipOfTheDay(model);
        totd.setCurrentTip(0);
        totd.showDialog(this);
    }
}
