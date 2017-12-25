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

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Simple tests to ensure that the {@code JXTaskPane} can be instantiated and
 * displayed.
 * 
 * @author rah003
 */
public class JXTaskPaneVisualCheck extends InteractiveTestCase {
    
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(JXTaskPaneVisualCheck.class.getName());
    
    public JXTaskPaneVisualCheck() {
        super("JXLoginPane Test");
    }

    public static void main(String[] args) throws Exception {
        JXTaskPaneVisualCheck test = new JXTaskPaneVisualCheck();
        
        try {
//            test.runInteractiveTests();
//            test.runInteractiveTests("interactiveDisplay");
//            test.runInteractiveTests("interactiveMnemonic");
//            test.runInteractiveTests("interactiveEnablingTest");
            test.runInteractive("Scrolls");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }

    /**
     * Issue #1572-swingx: taskPane scrollsOnExpand has no effect Regression:
     * reported to work in 1.6.3, broken in 1.6.5-1
     * 
     * Reason is outdated logic in BasicTaskPaneUI (somehow survived the expanded vs.
     * collapsed property change and animated).
     * 
     * Quick fix in application code is to register a custom listener.
     *  
     */
    public void interactiveScrollsOnExpand() {
        JXTaskPane pane = new JXTaskPane();
        pane.add(new JLabel("just me!"));
        pane.add(new JLabel("just me!"));
        pane.add(new JLabel("just me!"));
        pane.add(new JLabel("just me!"));
        pane.add(new JLabel("just me!"));
        pane.add(new JLabel("just me!"));
        JXTaskPane other = new JXTaskPane();
        other.setCollapsed(true);
        other.setScrollOnExpand(true);
        other.add(new JButton("lazy me!"));
        other.add(new JButton("lazy me!"));
        other.add(new JButton("lazy me!"));
        other.add(new JButton("lazy me!"));
        other.add(new JButton("lazy me!"));
        other.add(new JButton("lazy me!"));
        other.add(new JButton("lazy me!"));
        other.add(new JButton("lazy me!"));
        other.add(new JButton("lazy me!"));
        other.add(new JButton("lazy me!"));
        other.add(new JButton("lazy me!"));
        other.add(new JButton("lazy me!"));
        other.add(new JButton("lazy me!"));
        other.add(new JButton("lazy me!"));
        other.add(new JButton("lazy me!"));
        other.add(new JButton("lazy me!"));
        other.add(new JButton("lazy me!"));
        // hot fix: add propertyChangeListener that triggers the scrolling
        PropertyChangeListener l = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                final JXTaskPane pane = (JXTaskPane) evt.getSource();
                if (!pane.isCollapsed() && pane.isScrollOnExpand()) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            pane.scrollRectToVisible(pane.getBounds());

                        }
                    });
                }
            }
        };
        other.addPropertyChangeListener("collapsed", l);
        JXTaskPane yetAnother = new JXTaskPane();
        yetAnother.add(new JScrollPane(new JXTable(20, 3)));
        yetAnother.setCollapsed(true);
        yetAnother.setScrollOnExpand(true);
        JXTaskPaneContainer container = new JXTaskPaneContainer();
        container.add(pane);
        container.add(other);
        container.add(yetAnother);
        showWithScrollingInFrame(container, "scrollOnExpand");
    }

    public void interactiveMnemonic() {
        JXTaskPane pane = new JXTaskPane();
        pane.setTitle("Use Me");
        pane.setMnemonic(KeyEvent.VK_U);
        pane.setForeground(Color.RED);
        pane.setBackground(Color.YELLOW);
        pane.add(new JLabel("another"));
        pane.add(new JButton("wow!!"));
        JXTaskPaneContainer container = new JXTaskPaneContainer();
        container.add(pane);
        showInFrame(container, "Mnemonic Test", true);
    }
    
    public void interactiveColors() {
        JXTaskPane pane = new JXTaskPane();
        pane.setTitle("just something....");
        pane.setForeground(Color.RED);
        pane.setBackground(Color.YELLOW);
        pane.add(new JLabel("another"));
        pane.add(new JButton("wow!!"));
        JXTaskPaneContainer container = new JXTaskPaneContainer();
        container.add(pane);
        showInFrame(container, "background", true);
    }
    
    /**
     * Issue #249-swingx JXTaskPane looks ugly under different default LaFs
     *
     */
    public void interactiveDisplay() {
        // PENDING JW: removed while fixing #1186-swingx (no dependency on sun packages)
        // revisit: why do we do this at all?
//        sun.awt.AppContext.getAppContext().put("JComponent.defaultLocale", Locale.FRANCE);
        JXTaskPane panel = new JXTaskPane();
        panel.setTitle("Hi there");
        panel.setForeground(Color.RED);
        panel.setFont(new Font("tahoma",Font.BOLD, 72));
        panel.add(new JLabel("Hi there again"));
        showInFrame(panel, "JXTaskPane interactive", true);
    }
    
    /**
     * Ensure that resizing scroll pane's properly enables/disables scrollbars.
     * <p>
     * SwingX issue #740
     */
    public void interactiveJScrollPaneTest() {
        JXTaskPane panel = new JXTaskPane();
        JXTable table = new JXTable(10, 15);
        table.setHorizontalScrollEnabled(true);
        panel.add(new JScrollPane(table));
        showInFrame(panel, "Ensure scrolling works");
    }

    /**
     * Add enabled/disabled state to JXTaskPane.
     * <p>
     * SwingX issue #400
     */
    public void interactiveEnablingTest() {
        final JXTaskPane pane = new JXTaskPane();
        pane.add(new AbstractAction("Disable") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pane.setEnabled(false);
                System.out.println(pane.isEnabled());
            }
        });
        pane.add(new JButton("wow!!"));
        JXTaskPaneContainer container = new JXTaskPaneContainer();
        container.add(pane);
        showInFrame(container, "Ensure enabling/disabling updates");
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
    
    // continue to ignore, until the test is fully converted to visual
    @Test @Ignore
  public void testAnimationListeners() throws Exception {
    JXTaskPane taskPane = new JXTaskPane();
    // start with a not expanded or animated taskPane
    taskPane.setAnimated(false);
    taskPane.setCollapsed(true);
    assertTrue(taskPane.isCollapsed());

    class ListenForEvents implements PropertyChangeListener {
      private boolean expandedEventReceived;
      private boolean collapsedEventReceived;
      private int animationStart;
      
      public void propertyChange(java.beans.PropertyChangeEvent evt) {
        if ("expanded".equals(evt.getNewValue())) {
          expandedEventReceived = true;
        } else if ("collapsed".equals(evt.getNewValue())) {
          collapsedEventReceived = true;
        } else if ("reinit".equals(evt.getNewValue())) {
          animationStart++;
        }
      }
    }

    ListenForEvents listener = new ListenForEvents();

    // register a listener on the animation
    taskPane.addPropertyChangeListener("collapsed",
      listener);
    taskPane.setAnimated(true);
    
    // expand the taskPane and...
    taskPane.setCollapsed(false);
    // ...wait until listener has been notified
    while (!listener.expandedEventReceived) { Thread.sleep(100); }
    
    // collapse the taskPane and...
    // ...wait until listener has been notified
    taskPane.setCollapsed(true);
    while (!listener.collapsedEventReceived) { Thread.sleep(100); }
    
    assertEquals(2, listener.animationStart);
  }

    /**
     * Do nothing, make the test runner happy
     * (would output a warning without a test fixture).
     *
     */
    public void testDummy() {
        
    }

}
