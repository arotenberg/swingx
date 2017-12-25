/*
 * Created on 13.02.2007
 *
 */
package org.jdesktop.swingx.appframework;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Window;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.swingx.JXDialog;
import org.jdesktop.swingx.search.AbstractSearchable;
import org.jdesktop.swingx.search.SearchFactory;
import org.jdesktop.swingx.search.Searchable;

public class AppSearchFactory extends SearchFactory {
    private static final Logger LOG = Logger.getLogger(AppSearchFactory.class
            .getName());
 
 
    /**
     * Size and locate the dialog, respecting user preferences. 
     * Locates the dialog at location if not null. Otherwise tries
     * to read stored session state from the ApplicationContext. As a last
     * fallback, centers the dialog relative to its owner.
     * 
     * @param findDialog the dialog to size and locate
     * @param location the internally stored location, may be null.
     */
    private void locateDialog(JXDialog findDialog, Point location) {
        Window owner = findDialog.getOwner();
        findDialog.pack();
        if (location == null) {
            // the first time creation in this app's life-cycle
            findDialog.setLocationRelativeTo(owner);
            Application application = Application.getInstance(Application.class);
            ApplicationContext appContext = application.getContext();
            try {
                if (application instanceof SingleXFrameApplication) {
                    ((SingleXFrameApplication) application).prepareDialog(findDialog, false);
                } else {
                    appContext.getSessionStorage().restore(findDialog, "findDialog.session.xml");
                }
            }
            catch (IOException e) {
                LOG.log(Level.WARNING, "couldn't restore sesssion", e);
            } catch (SecurityException e) {
                LOG.log(Level.WARNING, "couldn't restore sesssion", e);
            }
        } else {
            // all other times - swingx internals take over re-location
            findDialog.setLocation(location);
        }
    }

    @Override
    public void showFindDialog(JComponent target, Searchable searchable) {
        Window frame = null; //JOptionPane.getRootFrame();
        if (target != null) {
            target.putClientProperty(AbstractSearchable.MATCH_HIGHLIGHTER, Boolean.FALSE);
            frame = SwingUtilities.getWindowAncestor(target);
//            if (window instanceof Frame) {
//                frame = (Frame) window;
//            }
        }
        JXDialog topLevel = getDialogForSharedFilePanel();
        JXDialog findDialog;
        if ((topLevel != null) && (topLevel.getOwner().equals(frame))) {
            findDialog = topLevel;
            KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent(findDialog);
        } else {
            Point location = hideSharedFindPanel(true);
            if (frame instanceof Frame) {
                findDialog = new JXDialog((Frame) frame, getSharedFindPanel());
            } else if (frame instanceof Dialog) {
                // fix #215-swingx: had problems with secondary modal dialogs.
                findDialog = new JXDialog((Dialog) frame, getSharedFindPanel());
            } else {
                findDialog = new JXDialog(JOptionPane.getRootFrame(), getSharedFindPanel());
            }
            findDialog.setName("findDialog");
            locateDialog(findDialog, location);
        } 
        
        findDialog.setVisible(true);
        installFindRemover(target, findDialog);
        getSharedFindPanel().setSearchable(searchable);
    }


    private JXDialog getDialogForSharedFilePanel() {
        if (findPanel == null) return null;
        Window window = SwingUtilities.getWindowAncestor(findPanel);
        return (window instanceof JXDialog) ? (JXDialog) window : null;
    }
 
    
    public void updateUI() {
       if (findPanel != null) {
           SwingUtilities.updateComponentTreeUI(findPanel);
       }
       if (findBar != null) {
           SwingUtilities.updateComponentTreeUI(findBar);
       }
    }
}
