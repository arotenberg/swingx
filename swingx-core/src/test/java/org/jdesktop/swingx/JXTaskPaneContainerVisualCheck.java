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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.painter.Painter;

/**
 * Simple tests to ensure that the {@code JXTaskPane} can be instantiated and
 * displayed.
 * 
 * @author rah003
 */
public class JXTaskPaneContainerVisualCheck extends InteractiveTestCase {
    public JXTaskPaneContainerVisualCheck() {
        super("JXLoginPane Test");
    }

    public static void main(String[] args) throws Exception {
//        setLookAndFeel("Nimbus");
//        setSystemLF(true);
        JXTaskPaneContainerVisualCheck test = new JXTaskPaneContainerVisualCheck();
        
        try {
            test.runInteractiveTests();
//            test.runInteractiveTests("interactive.*Color.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }
    
    /**
     * Tweak JXTaskPaneContainer into an accordion-like component 
     */
    public void interactiveAccordion() {
        JXTaskPaneContainer container = new JXTaskPaneContainer() {

            private JXTaskPane current;

            private PropertyChangeListener expansionListener;

            /**
             * @inherited <p>
             */
            @Override
            protected void addImpl(Component comp, Object constraints, int index) {
                super.addImpl(comp, constraints, index);
                if (comp instanceof JXTaskPane) {
                    grabExpansionControl((JXTaskPane) comp);
                }
            }

            private void grabExpansionControl(JXTaskPane comp) {
                if (current != null) {
                    comp.setCollapsed(true);
                } else {
                    current = comp;
                    comp.setCollapsed(false);
                }
                comp.addPropertyChangeListener("collapsed",
                        getExpansionListener());
            }

            private void updateCurrentTaskPane(JXTaskPane source) {
                if (source != current) {
                    if (!source.isCollapsed()) {
                        if (current != null) {
                            current.setCollapsed(true);
                        }
                        current = source;
                    }
                }
            }

            private PropertyChangeListener createExpansionListener() {
                PropertyChangeListener l = new PropertyChangeListener() {

                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        // TODO Auto-generated method stub
                        updateCurrentTaskPane((JXTaskPane) evt.getSource());
                    }
                };
                return l;
            }
            

            private PropertyChangeListener getExpansionListener() {
                if (expansionListener == null) {
                    expansionListener = createExpansionListener();
                }
                return expansionListener;
            }


        };
        ((VerticalLayout) container.getLayout()).setGap(0);
        for (int i = 0; i < 5; i++) {
            addTaskPane(container);
        }
        showWithScrollingInFrame(container, "");
    }

    private void addTaskPane(JXTaskPaneContainer container) {
        JXTaskPane first = new JXTaskPane("pane " + container.getComponentCount());
        first.setAnimated(false);
        fillTaskPane(first);
        container.add(first);
    }
    
    /**
     * Requirement: color of parent, that is transparent. 
     * Issue ??-swingx: Not respected in Windows - that's because 
     * the windows addon installs a background painter (container is-a JXPanel).
     * <p>
     * Actually, it was slightly different: use same color as laf-background of
     * other components, what ever that means. Nevertheless, the issue here
     * is that there are two different mechanisms to define background 
     * (plain color or painter) by the laf, should be made consistent. 
     */
    public void interactiveContainerColor() {
        JXTaskPaneContainer container = new JXTaskPaneContainer();
        container.setOpaque(false);
        Painter<?> nullPainter = new Painter<Object>() {
            @Override
            public void paint(Graphics2D g, Object object, int width, int height) {
            }
        };
        container.setBackgroundPainter(nullPainter);
        addTaskPane(container);
        JXTaskPane second = new JXTaskPane();
        fillTaskPane(second);
        showWithScrollingInFrame(container, "transparent");
        container.getParent().setBackground(Color.CYAN);
        assertTrue(container.getParent().isOpaque());
    }
    
    public void interactiveGap() {
        JXTaskPaneContainer container = new JXTaskPaneContainer();
        addTaskPane(container);
        addTaskPane(container);
        showWithScrollingInFrame(container, "custom gap");
    }

    public void interactiveGetUINPE() {
        showWithScrollingInFrame(new AbstractLBContentPanel<Object>("hi") {}, "custom gap");

      }

    private void fillTaskPane(JXTaskPane first) {
        first.add(new AbstractActionExt("some") {

            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub

            }

        });

        first.add(new AbstractActionExt("other") {

            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub

            }

        });
    }

    /**
     * Ensure that removing a task pane properly repaints the container.
     * <p>
     * SwingX issue #719
     */
    public void interactiveRemovalTest() {
        final JXTaskPaneContainer container = new JXTaskPaneContainer();
        final JXTaskPane pane1 = new JXTaskPane();
        pane1.setTitle("First");
        container.add(pane1);
        
        final JXTaskPane pane2 = new JXTaskPane();
        pane2.setTitle("Second");
        container.add(pane2);
        
        JXFrame frame = wrapInFrame(container, "removal test");
        frame.add(new JButton(new AbstractAction("Remove Second") {
            public void actionPerformed(ActionEvent e) {
                container.remove(pane2);
            }
        }), BorderLayout.SOUTH);
        
        frame.pack();
        frame.setVisible(true);
    }
    
    /**
     * Ensure correct painting when in JSplitPane.
     * <p>
     * SwingX issue #434
     */
    public void interactiveSplitPaneTest() {
        final JXTaskPaneContainer container = new JXTaskPaneContainer();
        final JXTaskPane pane1 = new JXTaskPane();
        pane1.setTitle("First");
        pane1.add(new JLabel("1"));
        pane1.add(new JLabel("2"));
        pane1.add(new JLabel("3"));
        container.add(pane1);
        
        final JXTaskPane pane2 = new JXTaskPane();
        class DummyAction extends AbstractAction {
            DummyAction(String name, boolean enabled) {
                super(name);
                
                setEnabled(enabled);
            }
            
            /**
             * {@inheritDoc}
             */
            public void actionPerformed(ActionEvent e) {
                //does nothing
            }
        }
        pane2.setTitle("Second");
        pane2.add(new DummyAction("MMMM", true));
        pane2.add(new DummyAction("MMMM", false));
        pane2.add(new DummyAction("<html>MMMM</html>", false));
        pane2.add(new DummyAction("<html>MMMM</html>", true));
        container.add(pane2);
        
        final JXTaskPane pane3 = new JXTaskPane();
        pane3.setTitle("Third");
        pane3.add(new JLabel("1"));
        pane3.add(new JLabel("2"));
        pane3.add(new JLabel("3"));
        container.add(pane3);
        
        JSplitPane splitter = new JSplitPane();
        splitter.setLeftComponent(container);
        splitter.setContinuousLayout(true);
        
        JXFrame frame = wrapInFrame(splitter, "split pane test");
        addComponentOrientationToggle(frame);
        frame.pack();
        frame.setVisible(true);
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
    public abstract class AbstractLBContentPanel<T extends Object>
    extends JPanel {

    private JXTaskPaneContainer taskPane;
    private JXTaskPaneContainer jxTaskPaneContainer;

    public AbstractLBContentPanel(T content) {
        taskPane = getJXTaskPaneContainer(content);
        this.add(taskPane);
    }

            /**
         * This method initializes JXTaskPaneContainer
         *
         * @return org.jdesktop.swingx.JXTaskPaneContainer
         */
        private JXTaskPaneContainer getJXTaskPaneContainer(T content) {
            if (jxTaskPaneContainer == null) {
                try {
                    JXTaskPane first = new JXTaskPane();
                    fillTaskPane(first);
                    JXTaskPane second = new JXTaskPane();
                    fillTaskPane(second);
                    jxTaskPaneContainer = new JXTaskPaneContainer();
                    jxTaskPaneContainer.setSize(new Dimension(200, 220)); // Generated
                    jxTaskPaneContainer.setOpaque(false);
                    jxTaskPaneContainer.add(first);
                    jxTaskPaneContainer.add(second);
                } catch (java.lang.Throwable e) {
                    e.printStackTrace();
                }
            }
            return jxTaskPaneContainer;
        }
    }

}
