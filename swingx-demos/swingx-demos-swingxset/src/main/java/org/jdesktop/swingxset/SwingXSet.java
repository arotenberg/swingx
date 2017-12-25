/*
 * Copyright 2007 Sun Microsystems, Inc.  All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Sun Microsystems nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.jdesktop.swingxset;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.MultiSplitLayout;
import org.jdesktop.swingx.MultiSplitLayout.Node;
import org.jdesktop.swingx.appframework.AppSearchFactory;
import org.jdesktop.swingx.appframework.SingleXFrameApplication;
import org.jdesktop.swingx.appframework.StyledTextActions;
import org.jdesktop.swingx.search.SearchFactory;
import org.jdesktop.swingx.util.OS;
import org.jdesktop.swingx.util.font.FontPolicies;
import org.jdesktop.swingx.util.font.FontPolicy;
import org.jdesktop.swingx.util.font.FontSet;
import org.jdesktop.swingx.util.font.UIFontUtils;
import org.jdesktop.swingx.util.font.WrapperFontSet;
import org.jdesktop.swingxset.util.DemoUtils;

import com.sun.swingset3.Demo;
import com.sun.swingset3.codeview.CodeViewer;
import com.sun.swingset3.utilities.RoundedBorder;
import com.sun.swingset3.utilities.Utilities;

/**
 *
 * @author Amy Fowler
 * @author Jeanette Winzenburg (SwingX adaption)
 */
public class SwingXSet extends SingleXFrameApplication  {
    public static final Logger logger = Logger.getLogger(SwingXSet.class.getName());
    
    private static final ServiceLoader<LookAndFeel> LOOK_AND_FEEL_LOADER = ServiceLoader.load(LookAndFeel.class); 

    public static final String CONTROL_VERY_LIGHT_SHADOW_KEY = "controlVeryLightShadowColor";
    public static final String CONTROL_LIGHT_SHADOW_KEY = "controlLightShadowColor";
    public static final String CONTROL_MID_SHADOW_KEY = "controlMidShadowColor";
    public static final String CONTROL_VERY_DARK_SHADOW_KEY = "controlVeryDarkShadowColor";
    public static final String CONTROL_DARK_SHADOW_KEY = "controlDarkShadowColor";
    public static final String TITLE_GRADIENT_COLOR1_KEY = "titleGradientColor1";
    public static final String TITLE_GRADIENT_COLOR2_KEY = "titleGradientColor2";
    public static final String TITLE_FOREGROUND_KEY = "titleForegroundColor";
    public static final String CODE_HIGHLIGHT_KEY = "codeHighlightColor";
    public static final String TITLE_FONT_KEY = "titleFont";
    public static final String SUB_PANEL_BACKGROUND_KEY = "subPanelBackgroundColor";

    private static final Border EMPTY_BORDER = new EmptyBorder(0, 0, 0, 0);
    private static final Border PANEL_BORDER = new EmptyBorder(10, 10, 10, 10);
    
    private static final String DEMO_LIST_FILE = "/META-INF/demolist";

    static {
        // Property must be set *early* due to Apple Bug#3909714
        if (System.getProperty("os.name").equals("Mac OS X")) {
            System.setProperty("apple.laf.useScreenMenuBar", "true"); 
        }
        
        // temporary workaround for problem with Nimbus classname
        UIManager.LookAndFeelInfo lafInfo[] = UIManager.getInstalledLookAndFeels();
        for(int i = 0; i < lafInfo.length; i++) {
            if (lafInfo[i].getName().equals("Nimbus")) {
                lafInfo[i] = new UIManager.LookAndFeelInfo("Nimbus",
                        "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
                break;
            }
        }
        UIManager.setInstalledLookAndFeels(lafInfo);
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        UIManager.put("auto-add-separator", Boolean.FALSE);
    }
    
    public static void main(String[] args) {
        launch(SwingXSet.class, args);
    }
    
    public static boolean runningOnMac() {
        return System.getProperty("os.name").equals("Mac OS X");
    } 
    
    public static boolean usingNimbus() {
        return UIManager.getLookAndFeel().getName().equals("Nimbus");
    }
    
    // End of statics
    
    private ResourceMap resourceMap;
    
    // Application models
    private PropertyChangeListener demoPropertyChangeListener;
    private HashMap<String, JComponent> runningDemoCache;
    private DemoSelector demoSelector;
    
    
    // GUI components and controls
    private MultiSplitLayout multiSplitLayout;
    
    private CodeViewer codeViewer;    
    private ButtonGroup lookAndFeelRadioGroup;
    
    private JPopupMenu popup;
    
    // Properties
    private String lookAndFeel;
    private boolean hasBig;


 // -------------------- SwingXSet application/control code

    /**
     * Sets the bigFontSet property which is a flag indicating whether a large
     * FontSet should be used.
     * <p>
     * If true, the LAF FontSet is scaled up by a fixed amount, otherwise it's
     * taken as is.
     * 
     * The default is false.
     * 
     * @param bigFontSet a boolean indicating whether or not the system Fonts
     *        should be scaled up.
     */
    public void setBigFontSet(boolean bigFontSet) {
        boolean old = isBigFontSet();
        if (old == bigFontSet) return;
        this.hasBig = bigFontSet;
        updateFontSet();
        firePropertyChange("bigFontSet", old, isBigFontSet());
    }
     
    /**
     * Returns the bigFontSet property.
     * @return
     */
     public boolean isBigFontSet() {
         return hasBig;
     }

    /**
     * Toggle the bigFontSet property.
     * <p>
     * Note: this is public as an implementation side-effect. DON'T USE!
     */
    @Action(selectedProperty = "bigFontSet", enabledProperty = "windowsOS")
    public void toggleFontSet() {
    }

    public boolean isWindowsOS() {
        return OS.isWindows();
    }
    /**
     * Internal state update after FontSet flag has been changed.
     */
    private void updateFontSet() {
        if (!isBigFontSet()) {
            UIFontUtils.initFontDefaults(UIManager.getDefaults(), null);
        } else {
//            <snip> Install sscaled font
            FontPolicy windowsPolicy = FontPolicies.getDefaultWindowsPolicy();
            FontSet fontSet = windowsPolicy.getFontSet(null, UIManager
                    .getLookAndFeelDefaults());
            WrapperFontSet scaled = new WrapperFontSet(fontSet, 5);
            UIFontUtils.initFontDefaults(UIManager.getDefaults(), scaled);
//            </snip>
        }
        updateLookAndFeel();
    }


    /**
     * Toggles the codeViewerVisible property.
     * <p>
     * Note: this is public as an implementation side-effect. DON'T USE!
     */
    @Action (selectedProperty = "codeViewerVisible")
    public void toggleCodeViewerVisible() {
    }
    
    public void setCodeViewerVisible(boolean selectorVisible) {
        boolean old = isCodeViewerVisible();
//        <snip> MultiSplit toggle visiility by constraint
        multiSplitLayout.displayNode("source", selectorVisible);
        ((JComponent) getMainFrame().getContentPane()).revalidate();
//        </snip>
        firePropertyChange("codeViewerVisible", old, isCodeViewerVisible());
        requestFocusOnDemo();
    }
    
    
    public boolean isCodeViewerVisible() {
        JComponent selector = getComponentByConstraint("source");
        return selector.isVisible();
    }
    
    /**
     * Toggles the selectorVisible property.
     * <p>
     * Note: this is public as an implementation side-effect. DON'T USE!
     * 
     */
    @Action (selectedProperty = "selectorVisible")
    public void toggleSelectorVisible() {
    }
    
    public void setSelectorVisible(boolean selectorVisible) {
        boolean old = isSelectorVisible();
//        <snip> MultiSplit toggle visiility by constraint
        multiSplitLayout.displayNode("selector", selectorVisible);
        ((JComponent) getMainFrame().getContentPane()).revalidate();
//        </snip>
        firePropertyChange("selectorVisible", old, isSelectorVisible());
        requestFocusOnDemo();
    }

    
    public boolean isSelectorVisible() {
        JComponent selector = getComponentByConstraint("selector");
        return selector.isVisible();
    }
    
    /**
     * @param demoContainer
     */
    private void requestFocusOnDemo() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Demo demo = demoSelector.getSelectedDemo();
                if (demo != null) {
                    Component demoComponent = demo.getDemoComponent();
                    if (demoComponent != null) {
                        KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent(demoComponent);
                    }
                }
            }
        });
    }
    
    private void updateFromSelectedDemoChanged() {
        JComponent demoContainer = getComponentByConstraint("demo");
        Demo demo = getDemo();
        if (demo != null) {
            JComponent demoPanel = runningDemoCache.get(demo.getName());
            if (demoPanel == null || demo.getDemoComponent() == null) {
                demo.startInitializing();
                demoPanel = createDemoPanel(demo);
                if (demoPanel != null) {
                    runningDemoCache.put(demo.getName(), demoPanel);
                }
            }
            if (demoPanel != null) {
                demoContainer.removeAll(); //(currentDemoPanel);
                demoContainer.add(demoPanel, BorderLayout.CENTER);
                demoContainer.revalidate();
                demoContainer.repaint();
                multiSplitLayout.layoutByWeight(getMainFrame().getContentPane());
            }
        }

        codeViewer.setSourceFiles(demo != null?
                                  demo.getSourceFiles() : null);
    }
   
    private JComponent createDemoPanel(Demo demo) {
        try {
            return new DemoXPanel(demo);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }  
        
        return null;
    }
    public Demo getDemo() {
        return demoSelector.getSelectedDemo();
    }

    
    private class DemoSelectionListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent event) {
            if (event.getPropertyName().equals("selectedDemo")) {
                updateFromSelectedDemoChanged();
            }
        }
    }
           
    
    // registered on Demo to detect when the demo component is instantiated.
    // we need this because when we embed the demo inside an HTML description pane,
    // we don't have control over the demo component's instantiation
    private class DemoPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            String propertyName = e.getPropertyName();
            if (propertyName.equals("demoComponent")) {
                Demo demo = (Demo)e.getSource();
                JComponent demoComponent = (JComponent)e.getNewValue();
                if (demoComponent != null) {
                    demoComponent.putClientProperty("swingset3.demo", demo);
                    demoComponent.addHierarchyListener(new DemoVisibilityListener());
                    registerPopups(demoComponent);
                }
            } 
        }
    }
    
    private class DemoVisibilityListener implements HierarchyListener {
        public void hierarchyChanged(HierarchyEvent event) {
            if ((event.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) > 0) {
                JComponent component = (JComponent)event.getComponent();
                final Demo demo = (Demo)component.getClientProperty("swingset3.demo");
                if (!component.isShowing()) {
                    demo.stop();
                } else {
                    getComponentByConstraint("demo").revalidate();
                    EventQueue.invokeLater(new Runnable() {
                        public void run() {
                            demo.start();
                        }
                    });
                }
            }            
        }        
    }


    private JComponent getComponentByConstraint(String string) {
        Node node = multiSplitLayout.getNodeForName(string);
        return (JComponent) multiSplitLayout.getComponentForNode(node);
    }

    
    public void setLookAndFeel(String lookAndFeel) throws ClassNotFoundException,
        InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        
        String oldLookAndFeel = this.lookAndFeel;
        
        if (oldLookAndFeel != lookAndFeel) {
            boolean oldBig = isBigFontSet();
            hasBig = false;
            updateFontSet();
            UIManager.setLookAndFeel(lookAndFeel);
            this.lookAndFeel = lookAndFeel;
            updateLookAndFeel();
            setBigFontSet(oldBig);
            firePropertyChange("lookAndFeel", oldLookAndFeel, lookAndFeel);                     
        }
    }
    
    @Action 
    public void setLookAndFeel() {
        ButtonModel model = lookAndFeelRadioGroup.getSelection();
        String lookAndFeelName = model.getActionCommand();
        try {
            setLookAndFeel(lookAndFeelName);
        } catch (Exception ex) {
            displayErrorMessage(resourceMap.getString("error.unableToChangeLookAndFeel") +
                    "to "+lookAndFeelName, ex);
        }
    }
    
    public String getLookAndFeel() {
        return lookAndFeel;
    }

    private void updateLookAndFeel() {
        Window windows[] = Frame.getWindows();

        for(Window window : windows) {
            SwingUtilities.updateComponentTreeUI(window);
            for(JComponent demoPanel : runningDemoCache.values()) {
                SwingUtilities.updateComponentTreeUI(demoPanel);
            }
        }
        
        ((AppSearchFactory) SearchFactory.getInstance()).updateUI();
    }


    // hook used to detect if any components in the demo have registered a
    // code snippet key for the their creation code inside the source
    private void registerPopups(Component component) {
        
        if (component instanceof Container) {
            Component children[] = ((Container)component).getComponents();
            for(Component child: children) {
                if (child instanceof JComponent) {
                    registerPopups(child);
                }
            }
        }
        if (component instanceof JComponent) {
            JComponent jcomponent = (JComponent)component;
            String snippetKey = (String)jcomponent.getClientProperty("snippetKey");
            if (snippetKey != null) {
                jcomponent.setComponentPopupMenu(popup);
            }
        }
    }  
    
    private class ViewCodeSnippetAction extends AbstractAction {
        public ViewCodeSnippetAction() {
            super("View Source Code");
        }
        public void actionPerformed(ActionEvent actionEvent) {
            Container popup = (JComponent)actionEvent.getSource();
            while(popup != null && !(popup instanceof JPopupMenu)) {
                popup = popup.getParent();
            }
            JComponent target = (JComponent)((JPopupMenu)popup).getInvoker();
            
            String snippetKey = (String)target.getClientProperty("snippetKey");
            if (snippetKey != null) {
                codeViewer.highlightSnippetSet(snippetKey);
            } else {
                logger.log(Level.WARNING, "can't find source code snippet for:" + snippetKey);
            }                                    
        }
    }
    
    private static class EditPropertiesAction extends AbstractAction {
        public EditPropertiesAction() {
            super("Edit Properties");
        }
        @Override
        public boolean isEnabled() {
            return false;
        }
        public void actionPerformed(ActionEvent actionEvent) {
            
        }
    } 
    
 //------------------- override/implement Application    
    @Override
    protected void initialize(String args[]) { 
        // following line is for debugging initial layount only, will be removed ..
//        deleteSessionState();
        try {
            // JW: don't want to start up with Nimbus because it misbehaves
            // severely in not cleaning up behind itself ...
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            // not catestrophic
        }
        super.initialize(args);
        resourceMap = getContext().getResourceMap();
        
        runningDemoCache = new HashMap<String, JComponent>();
        
        List<Demo> demos = DemoCreator.getInstance().createDemos(args, DEMO_LIST_FILE);
        for (Demo demo : demos) {
            demo.addPropertyChangeListener(getDemoPropertyChangeListener());
        }
        demoSelector = new DemoSelector(demos);
//        SearchFactory.getInstance().setUseFindBar(true);
    }

    @Override 
    protected void startup() {
        
        configureDefaults();
        
        JXFrame frame = (JXFrame) getMainFrame();
        frame.add(createMainPanel());
        frame.setJMenuBar(createMenuBar());
        applyDefaults();
        installListeners();
        // PENDING JW: moved from start of method to after component creation
        // otherwise throws with bsaf-1.9.1
        // needs tracking - dont know what's happening ...
        StyledTextActions.install(getContext());
        
        show(frame);     
        demoSelector.setDefaultSelectedDemo();
    } 

    
    
    /** 
     * Overridden to apply fade-out hook instead of halting the runtime.
     */
    @Override
    protected void end() {
        DemoUtils.fadeOutAndExit(getMainFrame(), 800);
    }

    @Override
    protected void configureWindow(Window root) {
        super.configureWindow(root);
        if (root == getMainFrame()) {
            // PENDING JW
            // brute force: prevent super from pack()'ing the mainframe
            // initially - actually it's a problem of our overall
            // layout, packing giving horrible results ...
            // we have to do it here (instead of in show as I assumed)
            // because super's injecting properties will invalidate
            root.pack();
            root.setBounds(0, 0, 1024, 748);
            root.validate();
        }
    }

    
    @Override
    protected JXFrame createXFrame() {
//        JXFrame frame = new JXFrame();
//        frame.setRootPane(new JXDemoRootPane());
        return new JXDemoFrame();
    }

    protected void installListeners() {
        UIManager.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getPropertyName().equals("lookAndFeel")) {
                    configureDefaults();
                }
            }
        });
        demoSelector.addPropertyChangeListener(new DemoSelectionListener());
    }
    
    // --------------------------- build components
    
    
    protected JComponent createMainPanel() {
//        <snip> MultiSplit layout declaration
        String layout = 
            "(ROW " +
                 "(LEAF name=selector weight=0.25)" +
                 "(COLUMN weight=0.75 " +
                     "(LEAF name= demo weight=0.7)" +
                     "(LEAF name=source weight=0.3)" +
                 ")" +
            ")"; 
        multiSplitLayout = new MultiSplitLayout(MultiSplitLayout.parseModel(layout));
//        </snip>
        JXMultiSplitPane splitPane = new JXMultiSplitPane();
        splitPane.setLayout(multiSplitLayout);

        // Create demo selector panel on left
        JComponent demoSelectorPanel = demoSelector.getSelectorComponent();
        demoSelectorPanel.setBorder(PANEL_BORDER);
        splitPane.add(demoSelectorPanel, "selector");
        
        // Create panel to contain currently running demo
        JComponent demoContainer = new JXPanel();
        demoContainer.setLayout(new BorderLayout());
        demoContainer.setBorder(PANEL_BORDER);
        splitPane.add(demoContainer, "demo");

        // Create source code pane
        codeViewer = new CodeViewer();
//        Font font = codeViewer.getFont();
//        codeViewer.setFont(font.deriveFont(Font.BOLD, 20.f));
        JComponent codeContainer = new JPanel(new BorderLayout());
        codeContainer.add(codeViewer);
        codeContainer.setBorder(PANEL_BORDER);
        splitPane.add(codeContainer, "source");
        
        // Create shareable popup menu for demo actions
        popup = new JPopupMenu();
        popup.add(new EditPropertiesAction());
        popup.add(new ViewCodeSnippetAction());

        return splitPane;
    }
    
    protected JMenuBar createMenuBar() {
    
        JMenuBar menubar = new JMenuBar();
        menubar.setName("menubar");
        
        // File menu
        JMenu fileMenu = new JMenu();
        fileMenu.setName("file");
        menubar.add(fileMenu);
        
        // File -> Quit
        if (!runningOnMac()) {
            JMenuItem quitItem = new JMenuItem();
            quitItem.setName("quit");
            quitItem.setAction(getAction("quit"));
            fileMenu.add(quitItem);
        }
       
        // View menu
        JMenu viewMenu = new JMenu();
        viewMenu.setName("view");
        // View -> Look and Feel       
        viewMenu.add(createLookAndFeelMenu());
        menubar.add(viewMenu);

        viewMenu.add(new JCheckBoxMenuItem(getAction("toggleFontSet")));
        JMenuItem toggleSelectorVisible = new JCheckBoxMenuItem(getAction("toggleSelectorVisible"));
        // PENDING JW: Action binding to selected - force initial state
        toggleSelectorVisible.setSelected(isSelectorVisible());
        viewMenu.add(toggleSelectorVisible);
        JMenuItem toggleCodeViewerVisible = new JCheckBoxMenuItem(getAction("toggleCodeViewerVisible"));
        // PENDING JW: Action binding to selected - force initial state
        toggleCodeViewerVisible.setSelected(isCodeViewerVisible());
        viewMenu.add(toggleCodeViewerVisible);
        
        // debug
//        viewMenu.add(new JMenuItem(getAction("debugVisible")));
//        JMenuItem toggleCO = new JMenuItem(getAction("toggleCO"));
//        viewMenu.add(toggleCO);
        return menubar;
    }

    @Action
    public void debugVisible() {
        toggleCodeViewerVisible();
    }
    
    @Action
    public void toggleCO() {
        JFrame frame = getMainFrame();
        ComponentOrientation current = frame.getComponentOrientation();
        if (current == ComponentOrientation.LEFT_TO_RIGHT) {
            frame.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        } else {
            frame.applyComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

        }
        frame.getRootPane().revalidate();
        frame.invalidate();
        frame.validate();
        frame.repaint();

    }

    protected JMenu createLookAndFeelMenu() {
        JMenu menu = new JMenu();
        menu.setName("lookAndFeel");
        
        // Look for toolkit look and feels first
        UIManager.LookAndFeelInfo lookAndFeelInfos[] = UIManager.getInstalledLookAndFeels();
        lookAndFeel = UIManager.getLookAndFeel().getClass().getName();
        lookAndFeelRadioGroup = new ButtonGroup();
        for(UIManager.LookAndFeelInfo lafInfo: lookAndFeelInfos) {
            menu.add(createLookAndFeelItem(lafInfo.getName(), lafInfo.getClassName()));
        }  
        // Now load any look and feels defined externally as service via java.util.ServiceLoader
        LOOK_AND_FEEL_LOADER.iterator();
        for (LookAndFeel laf : LOOK_AND_FEEL_LOADER) {           
            menu.add(createLookAndFeelItem(laf.getName(), laf.getClass().getName()));
        }
         
        return menu;
    }
    
    protected JRadioButtonMenuItem createLookAndFeelItem(String lafName, String lafClassName) {
        JRadioButtonMenuItem lafItem = new JRadioButtonMenuItem();

        lafItem.setSelected(lafClassName.equals(lookAndFeel));
        lafItem.setHideActionText(true);
        lafItem.setAction(getAction("setLookAndFeel"));
        lafItem.setText(lafName);
        lafItem.setActionCommand(lafClassName);
        lookAndFeelRadioGroup.add(lafItem);
        
        return lafItem;
    }
    
    private javax.swing.Action getAction(String actionName) {
        return getContext().getActionMap().get(actionName);
    }
       
    // For displaying error messages to user
    protected void displayErrorMessage(String message, Exception ex) {
        JPanel messagePanel = new JPanel(new BorderLayout());       
        JLabel label = new JLabel(message);
        messagePanel.add(label);
        if (ex != null) {
            JComponent panel = new JXPanel(new BorderLayout());
            panel.setBorder(new RoundedBorder());
            
            // remind(aim): provide way to allow user to see exception only if desired
            StringWriter writer = new StringWriter();
            ex.printStackTrace(new PrintWriter(writer));
            JTextArea exceptionText = new JTextArea();
            exceptionText.setText("Cause of error:\n" +
                    writer.getBuffer().toString());
            exceptionText.setBorder(new RoundedBorder());
            exceptionText.setOpaque(false);
            exceptionText.setBackground(
                    Utilities.deriveColorHSB(UIManager.getColor("Panel.background"),
                    0, 0, -.2f));
            JScrollPane scrollpane = new JScrollPane(exceptionText);
            scrollpane.setBorder(EMPTY_BORDER);
            scrollpane.setPreferredSize(new Dimension(600,240));
            panel.add(scrollpane);
            messagePanel.add(panel, BorderLayout.SOUTH);            
        }
        JOptionPane.showMessageDialog(getMainFrame(), messagePanel, 
                resourceMap.getString("error.title"),
                JOptionPane.ERROR_MESSAGE);
                
    }
    
    // ----------------------- init/read/create Demos
    
    
    protected PropertyChangeListener getDemoPropertyChangeListener() {
        if (demoPropertyChangeListener == null) {
            demoPropertyChangeListener = new DemoPropertyChangeListener();
        }
        return demoPropertyChangeListener;
    }
    

    //------------------------ init/configure ui properties
    
    private void configureDefaults() {
        
        // Color palette algorithm courtesy of Jasper Potts
        Color controlColor = UIManager.getColor("control");
        
        UIManager.put(CONTROL_VERY_LIGHT_SHADOW_KEY, 
                Utilities.deriveColorHSB(controlColor, 0, 0, -0.02f));
        UIManager.put(CONTROL_LIGHT_SHADOW_KEY, 
                Utilities.deriveColorHSB(controlColor, 0, 0, -0.06f));
        UIManager.put(CONTROL_MID_SHADOW_KEY, 
                Utilities.deriveColorHSB(controlColor, 0, 0, -0.16f));
        UIManager.put(CONTROL_VERY_DARK_SHADOW_KEY, 
                Utilities.deriveColorHSB(controlColor, 0, 0, -0.5f));
        UIManager.put(CONTROL_DARK_SHADOW_KEY, 
                Utilities.deriveColorHSB(controlColor, 0, 0, -0.32f));
        
        // Calculate gradient colors for title panels
        Color titleColor = UIManager.getColor(usingNimbus()? "nimbusBase" : "activeCaption");

        // Some LAFs (e.g. GTK) don't contain "activeCaption" 
        if (titleColor == null) {
            titleColor = controlColor;
        }
        float hsb[] = Color.RGBtoHSB(
                titleColor.getRed(), titleColor.getGreen(), titleColor.getBlue(), null);
        UIManager.put(TITLE_GRADIENT_COLOR1_KEY, 
                Color.getHSBColor(hsb[0]-.013f, .15f, .85f));
        UIManager.put(TITLE_GRADIENT_COLOR2_KEY, 
                Color.getHSBColor(hsb[0]-.005f, .24f, .80f));
        UIManager.put(TITLE_FOREGROUND_KEY, 
                Color.getHSBColor(hsb[0], .54f, .40f));
        
        // Calculate highlight color for code pane
        UIManager.put(CODE_HIGHLIGHT_KEY,
                Color.getHSBColor(hsb[0]-.005f, .20f, .95f));
       
        Font labelFont = UIManager.getFont("Label.font");
        UIManager.put(TITLE_FONT_KEY, labelFont.deriveFont(Font.BOLD, labelFont.getSize()+4f));        
 
        Color panelColor = UIManager.getColor("Panel.background");
        UIManager.put(SUB_PANEL_BACKGROUND_KEY, 
                Utilities.deriveColorHSB(panelColor, 0, 0, -.06f));
        
        applyDefaults();
        
    } 
    
    protected void applyDefaults() {
        if (codeViewer != null) {
            codeViewer.setHighlightColor(UIManager.getColor(CODE_HIGHLIGHT_KEY));
        }        
    }
    

}
