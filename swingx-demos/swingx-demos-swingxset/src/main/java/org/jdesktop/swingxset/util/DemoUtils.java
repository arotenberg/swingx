/*
 * Created on 16.11.2009
 *
 */
package org.jdesktop.swingxset.util;

import java.awt.Component;
import java.awt.Window;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.jnlp.ClipboardService;
import javax.jnlp.ServiceManager;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.text.DefaultEditorKit;

import org.jdesktop.application.Application;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.TimelineState;
import org.pushingpixels.trident.callback.UIThreadTimelineCallbackAdapter;

/**
 * Misc. convenience methods ;-) 
 */
public class DemoUtils {

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(DemoUtils.class
            .getName());
    
    /**
     * Replaces the editor's default copy action in security restricted
     * environments with one messaging the ClipboardService. Does nothing 
     * if not restricted.
     * 
     * @param editor the editor to replace 
     */
    public static void replaceCopyAction(final JEditorPane editor) {
        if (!isRestricted()) return;
        Action safeCopy = new AbstractAction() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ClipboardService cs = (ClipboardService)ServiceManager.lookup
                        ("javax.jnlp.ClipboardService");
                    StringSelection transferable = new StringSelection(editor.getSelectedText());
                    cs.setContents(transferable);
                } catch (Exception e1) {
                    // do nothing
                }
            }
        };
        editor.getActionMap().put(DefaultEditorKit.copyAction, safeCopy);
    }
    
    private static boolean isRestricted() {
        SecurityManager manager = System.getSecurityManager();
        if (manager == null) return false;
        try {
            manager.checkSystemClipboardAccess();
            return false;
        } catch (SecurityException e) {
            // nothing to do - not allowed to access
        }
        return true;
    }
//--------------- Application-related convenience methods
    
    /**
     * Injects the resources into the given component instance from
     * the ResourceMap of the component's class.
     */
    public static void injectResources(Component comp) {
        Application.getInstance().getContext().getResourceMap(comp.getClass()).injectComponents(comp);
    }

    /**
     * Injects the resources into the given component instance from
     * the ResourceMap of the parent's class.
     */
    public static void injectResources(Object parent, Component child) {
        Application.getInstance().getContext().getResourceMap(parent.getClass())
            .injectComponents(child);
    }
    
    /**
     * Returns an Icon stored with the given key using the resourceMap of 
     * the baseClass, or null if none is found.
     * 
     */
    public static Icon getResourceIcon(Class<?> baseClass, String key) {
        return Application.getInstance().getContext()
            .getResourceMap(baseClass).getIcon(key);
    }

    /**
     * Returns an String stored with the given key using the resourceMap of 
     * the baseClass, or null if none is found.
     * 
     */
    public static String getResourceString(Class<?> baseClass, String key) {
        return Application.getInstance().getContext()
            .getResourceMap(baseClass).getString(key);
    }

    public static Action getAction(Object actionProvider, String key) {
        return Application.getInstance().getContext().getActionMap(actionProvider).get(key);        
    }
    
//--------------------- swingset specials
    
    public static void setSnippet(String snippet, JComponent... comps) {
        for (JComponent comp : comps) {
            comp.putClientProperty("snippetKey", snippet);
        }
    }

//----------------------- Window transparency convenience support
    
    public static void fadeOutAndDispose(final Window window,
            int fadeOutDuration) {
        fadeOutAndEnd(window, fadeOutDuration, false);
    }

    public static void fadeOutAndExit(Window window, int fadeOutDuration) {
        fadeOutAndEnd(window, fadeOutDuration, true);
    }
    /**
     * @param window
     * @param fadeOutDuration
     */
    private static void fadeOutAndEnd(final Window window, int fadeOutDuration, 
            final boolean exit) {
        Timeline dispose = new Timeline(new WindowFader(window));
        dispose.addPropertyToInterpolate("opacity", 1.0f,
//                AWTUtilitiesWrapper.getWindowOpacity(window), 
                0.0f);
        dispose.addCallback(new UIThreadTimelineCallbackAdapter() {
            @Override
            public void onTimelineStateChanged(TimelineState oldState,
                    TimelineState newState, float durationFraction,
                    float timelinePosition) {
                if (newState == TimelineState.DONE) {
                    if (exit) {
                        Runtime.getRuntime().exit(0);
                    } else {
                        window.dispose();
                    }
                }
            }
        });
        dispose.setDuration(fadeOutDuration);
        dispose.play();
    }
    
    public static class WindowFader {
        private Window window;
        
        public WindowFader(Window window) {
            this.window = window;
        }
        
        public void setOpacity(float opacity) {
            AWTUtilitiesWrapper.setWindowOpacity(window, opacity);
        }
    }

    private DemoUtils() {}
    
    
}
