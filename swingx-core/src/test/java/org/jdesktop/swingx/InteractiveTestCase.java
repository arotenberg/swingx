/*
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;

import org.jdesktop.swingx.search.SearchFactory;

/**
 * Base class for supporting inclusion of interactive tests into a JUnit test case.
 * Note that the interactive tests are NOT executed by the JUnit framework and
 * are not automated.  They are typically used for visual inspection of features
 * during development. It is convenient to include the interactive tests along with
 * the automated JUnit tests since they may share resources and it keeps tests
 * focused in a single place.
 * <p>
 * All interactive test methods should be prefixed with &quot;interactive&quot;,
 * e.g.  interactiveTestTableSorting().</p>
 * <p>
 * The test class's <code>main</code> method should be used to control which
 * interactive tests should run.  Use <code>runInteractiveTests()</code> method
 * to run all interactive tests in the class.</p>
 * <p>
 * Ultimately we need to investigate moving to a mechanism which can help automate
 * interactive tests.  JFCUnit is being investigated.  In the meantime, this
 * is quick and dirty and cheap.
 * </p>
 * @author Amy Fowler
 * @version 1.0
 */
public abstract class InteractiveTestCase extends junit.framework.TestCase {
    private static final Logger LOG = Logger
            .getLogger(InteractiveTestCase.class.getName());
    protected Point frameLocation = new Point(0,0);
    protected boolean systemLF;
    
    public InteractiveTestCase() {
        super();
        String className = getClass().getName();
        int lastDot = className.lastIndexOf(".");
        String lastElement = className.substring(lastDot + 1);
        setName(lastElement);
    }
    
    public InteractiveTestCase(String testTitle) {
        super(testTitle);
    }

//----------------- run
    
    /**
     * Runs all tests which are prefixed by "interactive" and contain the given snippet.
     * @throws Exception 
     */
    public void runInteractive(String snippet) throws Exception {
        runInteractiveTests("interactive.*" + snippet + ".*");
    }
    
    /**
     * Runs all tests whose method names match the specified regex pattern.
     * @param regexPattern regular expression pattern used to match test method names
     * @throws java.lang.Exception
     */
    public void runInteractiveTests(String regexPattern)  throws java.lang.Exception {
        setUp();
        Class<?> testClass = getClass();
        Method methods[] = testClass.getMethods();

        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().matches(regexPattern)) {
                try {
                    methods[i].invoke(this);
                }
                catch (Exception e) {
                    System.out.println("could not run interactive test: " +
                                       methods[i].getName());
                    e.printStackTrace();
                }
            }
        }
        if (methods.length == 0) {
            System.out.println("no test methods found matching the pattern: "+
                               regexPattern);
        }
        tearDown();
    }

    /**
     * Runs all test methods which are prefixed with &quot;interactive&quot;.
     * @throws java.lang.Exception
     */
    public void runInteractiveTests() throws java.lang.Exception {
        runInteractiveTests("interactive.*");
    }

//------------------------ creating/showing frame
    
    /**
     * Creates and returns a JXFrame with the specified title, containing
     * the component wrapped into a JScrollPane.
     * 
     * @param component the JComponent to wrap
     * @param title the title to show in the frame
     * @return a configured, packed and located JXFrame.
     */
    public JXFrame wrapWithScrollingInFrame(JComponent component, String title) {
        JScrollPane scroller = new JScrollPane(component);
        return wrapInFrame(scroller, title);
    }

    /**
     * Creates and returns a JXFrame with the specified title, containing
     * two components individually wrapped into a JScrollPane.
     * 
     * @param leftComp the left JComponent to wrap
     * @param rightComp the right JComponent to wrap
     * @param title the title to show in the frame
     * @return a configured, packed and located JXFrame
     */
    public JXFrame wrapWithScrollingInFrame(JComponent leftComp, JComponent rightComp, String title) {
        JComponent comp = Box.createHorizontalBox();
        comp.add(new JScrollPane(leftComp));
        comp.add(new JScrollPane(rightComp));
        JXFrame frame = wrapInFrame(comp, title);
        return frame;
    }

    /**
     * Creates and returns a JXFrame with the specified title, containing the
     * component. First frame get's the menubar as defined by
     * createAndFillMenuBar. Closing the first frame will exit.
     * 
     * @param component the JComponent to wrap
     * @param title the title to show in the frame
     * @return a configured, packed and located JXFrame.
     */
    public JXFrame wrapInFrame(JComponent component, String title) {
        JXFrame frame = new JXFrame(title, false);
        JToolBar toolbar = new JToolBar();
        frame.getRootPaneExt().setToolBar(toolbar);
        frame.getContentPane().add(BorderLayout.CENTER, component);
        frame.setLocation(frameLocation);
        if (frameLocation.x == 0) {
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setTitle(title + "  [close me and all tests will close]");
            frame.setJMenuBar(createAndFillMenuBar(component));
        }
        frameLocation.x += 30;
        frameLocation.y += 30;
        frame.pack();
        return frame;
    }

    /**
     * Creates, shows and returns a JXFrame with the specified title, containing
     * the component wrapped into a JScrollPane.
     * 
     * @param component the JComponent to wrap
     * @param title the title to show in the frame
     * @return a configured, packed and located JXFrame.
     * @see #wrapWithScrollingInFrame(JComponent, String)
     */
    public JXFrame showWithScrollingInFrame(JComponent component, String title) {
        JXFrame frame = wrapWithScrollingInFrame(component, title);
        frame.setVisible(true);
        return frame;
    }

    /**
     * Creates and returns a JXFrame with the specified title, containing
     * two components individually wrapped into a JScrollPane.
     * 
     * @param leftComp the left JComponent to wrap
     * @param rightComp the right JComponent to wrap
     * @param title the title to show in the frame
     * @return a configured, packed and located JXFrame
     */
    public JXFrame showWithScrollingInFrame(JComponent leftComp, JComponent rightComp, String title) {
        JXFrame frame = wrapWithScrollingInFrame(leftComp, rightComp, title);
        frame.setVisible(true);
        return frame;
    }
    /**
     * Creates, shows and returns a JXFrame with the specified title, containing
     * the component.
     * 
     * @param component the JComponent to wrap
     * @param title the title to show in the frame
     * @return a configured, packed and located JXFrame.
     */
    public JXFrame showInFrame(JComponent component, String title) {
        return showInFrame(component, title, false);
    }

    /**
     * Creates, shows and returns a JXFrame with the specified title, containing
     * the component. Creates and adds a Menubar if showMenu is true.
     * 
     * @param component the JComponent to wrap
     * @param title the title to show in the frame
     * @param showMenu flag to indicate whether a JMenuBar should be added
     * @return a configured, packed and located JXFrame.
     */
    public JXFrame showInFrame(JComponent component, String title, boolean showMenu) {
        JXFrame frame = wrapInFrame(component, title, showMenu);
        frame.setVisible(true);
        return frame;
    }
    public JXFrame wrapInFrame(JComponent component, String title, boolean showMenu) {
        JXFrame frame = wrapInFrame(component, title);
        if (showMenu) {
            frame.setJMenuBar(createAndFillMenuBar(component));
        }
        frame.pack();
        return frame;
    }

    /**
     * Packs and shows the frame.
     * 
     * @param frame
     */
    public void show(final JXFrame frame) {
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Packs, sizes and shows the frame.
     * 
     * @param frame
     */
    public void show(final JXFrame frame, int width, int height) {
        frame.pack();
        frame.setSize(width, height);
        frame.setVisible(true);
    }

//---  toolbar, statusbar
    
    public AbstractButton addAction(JXFrame frame, Action action) {
        JToolBar toolbar = frame.getRootPaneExt().getToolBar();
        if (toolbar != null) {
            AbstractButton button = toolbar.add(action);
            button.setFocusable(false);
            return button;
        }
        return null;
    }

    /**
     * Creates and adds a button toggling the frame's componentOrientation.
     * @param frame
     */
    public void addComponentOrientationToggle(final JXFrame frame) {
        Action toggleComponentOrientation = new AbstractAction("toggle orientation") {

            @Override
            public void actionPerformed(ActionEvent e) {
                ComponentOrientation current = frame.getComponentOrientation();
                if (current.isLeftToRight()) {
                    frame.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                } else {
                    frame.applyComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

                }
                frame.getRootPane().revalidate();
                frame.invalidate();
                frame.validate();
                frame.repaint();
            }

        };
        addAction(frame, toggleComponentOrientation);
    }
    
    public void toggleComponentOrientation(JComponent frame) {
        ComponentOrientation current = frame.getComponentOrientation();
        if (current.isLeftToRight()) {
            frame.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        } else {
            frame.applyComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        }
        frame.revalidate();
        frame.repaint();
    }
    
    /**
     * Creates and adds a button toggling the frame's componentOrientation.
     * @param frame
     */
    public void addSearchModeToggle(final JXFrame frame) {
        Action action = new AbstractAction("toggle batch/incremental"){
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean useFindBar = !SearchFactory.getInstance().isUseFindBar(null, null);
                SearchFactory.getInstance().setUseFindBar(useFindBar);
            }
            
        };
        addAction(frame, action);
    }

    /**
     * Creates and adds a button toggling the target's enabled property.
     * 
     * @param frame
     * @param target
     */
    public void addEnabledToggle(JXFrame frame, final JComponent target) {
        Action action = new AbstractAction("toggle enabled") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                target.setEnabled(!target.isEnabled());
            }
        };
        addAction(frame, action);
    }
    
    /**
     * Creates and adds a button toggling the target's and its direct children's
     * enabled property.
     * 
     * @param frame
     * @param target
     */
    public void addEnabledToggleWithChildren(JXFrame frame, final JComponent target) {
        Action action = new AbstractAction("toggle enabled (with children)") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                target.setEnabled(!target.isEnabled());
                for (int i = 0; i < target.getComponentCount(); i++) {
                    target.getComponent(i).setEnabled(target.isEnabled());
                }
            }
        };
        addAction(frame, action);
    }
    

    public void addMessage(JXFrame frame, String message) {
        JXStatusBar statusBar = getStatusBar(frame);
        statusBar.add(new JLabel(message), JXStatusBar.Constraint.ResizeBehavior.FILL);
    }

    /**
     * Returns the <code>JXFrame</code>'s status bar. Lazily creates and 
     * sets an instance if necessary.
     * @param frame the target frame
     * @return the frame's statusbar
     */
    public JXStatusBar getStatusBar(JXFrame frame) {
        JXStatusBar statusBar = frame.getRootPaneExt().getStatusBar();
        if (statusBar == null) {
            statusBar = new JXStatusBar();
            frame.setStatusBar(statusBar);
        }
        return statusBar;
    }

    /**
     * Adds the component to the statusbar of the frame.  
     * 
     * @param frame
     * @param component
     */
    public void addStatusComponent(JXFrame frame, JComponent component) {
        getStatusBar(frame).add(component);
        frame.pack();
    }
    
    /**
     * @param frame
     * @param string
     */
    public void addStatusMessage(JXFrame frame, String message) {
        JXStatusBar bar = getStatusBar(frame);
        bar.add(new JLabel(message));
        frame.pack();
    }

//------------------------ menu
    
    public JMenuBar getMenuBar(JXFrame frame) {
        JMenuBar bar = frame.getJMenuBar();
        if (bar == null) {
            bar = new JMenuBar();
            frame.setJMenuBar(bar);
        }
        return bar;
    }
    
    /**
     * Creates, fills and returns a JMenuBar. 
     * 
     * @param component the component that was added to the frame.
     * @return a menu bar filled with actions as defined in createAndAddMenus
     * 
     * @see #createAndAddMenus
     */
    protected JMenuBar createAndFillMenuBar(JComponent component) {
        JMenuBar bar = new JMenuBar();
        createAndAddMenus(bar, component);
        
        return bar;
    }

    /**
     * Creates menus and adds them to the given menu bar.<p>
     * 
     * This implementation adds a menu to choose the LF.
     * 
     * @param bar the menubar to fill
     * @param component the component that was added to the frame.
     * 
     */
    protected void createAndAddMenus(JMenuBar bar, JComponent component) {
        bar.add(createPlafMenu());
    }

    /**
     * @return
     */
    protected JMenu createPlafMenu() {
        return createPlafMenu(null);
    }

    /**
     * Creates a menu filled with one SetPlafAction for each of the currently
     * installed LFs.
     * 
     * @param target the toplevel window to update, maybe null to indicate update
     *   of all application windows
     * @return the menu to use for plaf switching.
     */
    protected JMenu createPlafMenu(Window target) {
        LookAndFeelInfo[] plafs = UIManager.getInstalledLookAndFeels();
        JMenu menu = new JMenu("Set L&F");
        
        for (LookAndFeelInfo info : plafs) {
            menu.add(new SetPlafAction(info.getName(), info.getClassName(), target));
        }
        return menu;
    }

    // ---------------------- laf

    /**
     * 
     * Sets the lookAndFeel which has a name containing the given snippet.
     * Does not update any component/-tree, just bare
     * setting. May fail silently (Logging with level FINE) if there is no
     * installed LAF with the name or the setting fails for other reasons.
     * 
     * @param nameSnippet part of the name as published by the LAF.
     */
    public static void setLAF(String nameSnippet) {
        String laf = getLookAndFeelClassName(nameSnippet);
        if (laf != null) {
            try {
                UIManager.setLookAndFeel(laf);
            } catch (Exception e) {
                LOG.log(Level.FINE, "problem in setting laf: " + laf, e);
            }
        }
    }
    /**
     * 
     * Sets the lookAndFeel which has a name containing the given snippet. Throws
     * if none installed. Does not update any component/-tree, just bare setting.
     * 
     * @param nameSnippet part of the name as published by the LAF.
     * 
     * @throws UnsupportedLookAndFeelException 
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     * @throws ClassNotFoundException 
     * 
     */
    public static void setLookAndFeel(String nameSnippet) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        String laf = getLookAndFeelClassName(nameSnippet);
        if (laf != null) {
            UIManager.setLookAndFeel(laf);
            return;
        }
        throw new UnsupportedLookAndFeelException("no LAF installed with name snippet " + nameSnippet);
    }
    
    /**
     * Returns a boolean indicating whether or not the UI has a LAF with a name
     * containing the name snippet installed.
     * 
     * @param nameSnippet part of the name as published by the LAF.
     * @return a boolean indicating whether there's installed LAF with
     *   a name containing the name snippet.
     */
    public static boolean hasLookAndFeel(String nameSnippet) {
        return getLookAndFeelClassName(nameSnippet) != null;
    }
    
    /**
     * Returns the class name of the installed LookAndFeel with a name
     * containing the name snippet or null if none found.
     * 
     * @param nameSnippet
     * @return
     */
    public static String getLookAndFeelClassName(String nameSnippet) {
        LookAndFeelInfo[] plafs = UIManager.getInstalledLookAndFeels();
        for (LookAndFeelInfo info : plafs) {
            if (info.getName().contains(nameSnippet)) {
                return info.getClassName();
            }
        }
        return null;
    }
    /**
     * Action to toggle plaf and update all toplevel windows of the
     * current application. Used to setup the plaf-menu.
     */
    private static class SetPlafAction extends AbstractAction {
        private String plaf;
        private Window toplevel;
        
        
        @SuppressWarnings("unused")
        public SetPlafAction(String name, String plaf) {
            this(name, plaf, null);
        }
        
        /**
         * Instantiates an action which updates the toplevel window to
         * the given LAF. 
         * 
         * @param name the name of the action
         * @param plaf the class name of the LAF to set
         * @param toplevel the window to update, may be null to indicate
         *   update of all application windows
         */
        public SetPlafAction(String name, String plaf, Window toplevel) {
            super(name);
            this.plaf = plaf;
            this.toplevel = toplevel;
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                UIManager.setLookAndFeel(plaf);
                if (toplevel != null) {
                    SwingUtilities.updateComponentTreeUI(toplevel);
                } else {
                    SwingXUtilities.updateAllComponentTreeUIs();
                }
                
            } catch (Exception e1) {
                e1.printStackTrace();
                LOG.log(Level.FINE, "problem in setting laf: " + plaf, e1);
            } 
        }

    }
    

    /**
     * PENDING: JW - this is about toggling the LF, does nothing to update the
     * UI. Check all tests using this method to see if they make sense!
     * 
     * 
     * @param system
     */
    public static void setSystemLF(boolean system) {
        String lfName = system ? UIManager.getSystemLookAndFeelClassName()
                : UIManager.getCrossPlatformLookAndFeelClassName();
        try {
            UIManager.setLookAndFeel(lfName);
//            systemLF = system;
        } catch (Exception e1) {
            LOG.info("exception when setting LF to " + lfName);
            LOG.log(Level.FINE, "caused by ", e1);
        }
    }

    /**
     * Returns whether the current lf is the system lf. It assumes that the
     * lf is either cross-platform or system. Not really safe 
     */
    public static boolean isSystemLF() {
        LookAndFeel lf = UIManager.getLookAndFeel();
        return UIManager.getSystemLookAndFeelClassName().equals(lf.getClass().getName());
    }

    /**
     * Returns whether the current lf is the cross-platform lf. It assumes that the
     * lf is either cross-platform or system. Not really safe 
     */
    public static boolean isCrossPlatformLF() {
        LookAndFeel lf = UIManager.getLookAndFeel();
        return UIManager.getCrossPlatformLookAndFeelClassName().equals(lf.getClass().getName());
    }
    
    
  
}
