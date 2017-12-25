/*
 * Created on 26.02.2007
 *
 */
package org.jdesktop.swingx.appframework;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.RootPaneContainer;

import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.SessionStorage;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.appframework.XProperties.XTableProperty;
import org.jdesktop.swingx.appframework.XProperties.XTaskPaneProperty;
import org.jdesktop.swingx.search.SearchFactory;

public abstract class SingleXFrameApplication extends SingleFrameApplication {
    private static final Logger logger = Logger.getLogger(SingleXFrameApplication.class.getName());

    /**
     * {@inheritDoc} <p>
     * 
     * Overridden to force a JXFrame as main frame and inject SwingX specific
     * session properties.
     * 
     */
    @Override
    protected void initialize(String[] args) {
//        <snip>SwingX special
        injectSessionProperties();
        customizeSwingXContext();
        setMainFrame(createXMainFrame());
//        </snip>
    }

    /**
     * Hook to configure SwingX related global state.
     * Here: sets SearchFactory. <p>
     * PENDING: okay to do on the EDT?
     */
    protected void customizeSwingXContext() {
        SearchFactory.setInstance(new AppSearchFactory());
    }


    
    public void prepareDialog(JDialog c, boolean visible) {
        if (c == null) {
            throw new IllegalArgumentException("null JDialog");
        }
        if (!hadBeenPrepared(c)) {
            prepareWindow(c);
        }
        if (visible) {
            c.setVisible(true);
        }
    }

    /**
     * Checks and returns whether the given RootPaneContainer already has 
     * been prepared. As a side-effect, the container is marked as prepared 
     * (wrong place?)
     * 
     * @param c
     * @return
     */
    private boolean hadBeenPrepared(RootPaneContainer c) {
        JComponent rootPane = c.getRootPane();
        // These initializations are only done once
        Object k = "SingleFrameApplication.initRootPaneContainer";
        boolean prepared = Boolean.TRUE.equals(rootPane.getClientProperty(k));
        if (!prepared) {
            rootPane.putClientProperty(k, Boolean.TRUE);
        }
        return prepared;
    }


    /**
     * Prepares the given window. Injects properties from app context. 
     * Restores session state if appropriate. Registers listeners to try and
     * track session state.
     * 
     * @param root
     */
    protected void prepareWindow(Window root) {
        configureWindow(root);
        // If the window's size doesn't appear to have been set, do it
        if ((root.getWidth() == 0) || (root.getHeight() == 0)) {
            root.pack();
            if (!root.isLocationByPlatform()) {
                Component owner = (root != getMainFrame()) ? getMainFrame()
                        : null;
                root.setLocationRelativeTo(owner); // center the window
            }
        }
        // Restore session state
        String filename = sessionFilename(root);
        if (filename != null) {
            try {
                ApplicationContext ac = getContext();
                ac.getSessionStorage().restore(root, filename);
            } catch (Exception e) {
                logger.log(Level.WARNING, "couldn't restore sesssion", e);
            }
        }
        root.addWindowListener(getDialogListener());
    }

    private WindowListener getDialogListener() {
        return new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveSession(e.getWindow());
            }

            @Override
            public void windowClosed(WindowEvent e) {
                saveSession(e.getWindow());
            }

        };
    }


    /**
     * Save session state for the component hierarchy rooted by the mainFrame.
     * SingleFrameApplication subclasses that override shutdown need to remember
     * call {@code super.shutdown()}.
     */
    @Override
    protected void shutdown() {
        List<Window> windows = new ArrayList<Window>();
        windows.add(getMainFrame());
        for (int i = 0; i < getMainFrame().getOwnedWindows().length; i++) {
            windows.add(getMainFrame().getOwnedWindows()[i]);
        }
        for (Window window : windows) {
            saveSession(window);
        }
    }

    private String sessionFilename(Window window) {
        if (window == null) {
            return null; 
        }
        else {
            String name = window.getName();
            return (name == null) ? null : name + ".session.xml";
        }
    }

    private void saveSession(Window window) {
        String filename = sessionFilename(window);
        if (filename != null) {
            ApplicationContext appContext = getContext();
            try {
                appContext.getSessionStorage().save(window, filename);
            }
            catch (IOException e) {
                logger.log(Level.WARNING, "couldn't save sesssion", e);
            } catch (SecurityException e) {
                logger.log(Level.WARNING, "couldn't save sesssion", e);
            }
        }
    }

    /**
     * Deletes the session state by deleting the file. Useful during development
     * when restoring to old state is not always the desired behaviour.
     * Pending: this is incomplete, deletes the mainframe state only.
     */
    protected void deleteSessionState() {
        ApplicationContext context = getContext();
        try {
            context.getLocalStorage().deleteFile("mainFrame.session.xml");
        } catch (IOException e) {
            logger.log(Level.WARNING, "couldn't delete sesssion", e);
        } catch (SecurityException e) {
            logger.log(Level.WARNING, "couldn't delete sesssion", e);
        }
    }


    protected JXFrame createXMainFrame() {
        JXFrame xFrame = createXFrame();
        xFrame.setName("mainFrame");
        // PENDING JW: how-to set app icon?
        return xFrame;
    }

    protected JXFrame createXFrame() {
        return new JXFrame();
    }

    /**
     * Registers SwingX specific Properties for session storage. <p>
     */
    protected void injectSessionProperties() {
        SessionStorage storage = getContext().getSessionStorage();
        storage.putProperty(JXTable.class, new XTableProperty());
        storage.putProperty(JXTaskPane.class, new XTaskPaneProperty());
        new XProperties().registerPersistenceDelegates();
    }


}
