/*
 * $Id: StyledTextActions.java 4097 2011-11-30 19:22:13Z kschaefe $
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
package org.jdesktop.swingx.appframework;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.Caret;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.jdesktop.application.Action;
import org.jdesktop.application.ApplicationActionMap;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ProxyActions;
import org.jdesktop.beans.AbstractBean;

/**
 */
public class StyledTextActions extends AbstractBean {
    private static boolean installed;
    
    private final ApplicationContext context;
    private final CaretListener textComponentCaretListener;
    private final PropertyChangeListener textComponentPCL;
    private final String markerActionKey = "StyledTextActions.markerAction";
    private final javax.swing.Action markerAction;
    
    private boolean boldEnabled = false;
    private boolean boldSelected = false;
    private boolean italicEnabled = false;
    private boolean italicSelected = false;
    private boolean underlineEnabled = false;
    private boolean underlineSelected = false;

    //TODO justification actions
//    private boolean leftJustifyEnabled = false;
//    private boolean centerJustifyEnabled = false;
//    private boolean rightJustifyEnabled = false;
//    private boolean fullJustifyEnabled = false;
    
    @SuppressWarnings("serial")
    StyledTextActions(ApplicationContext context) {
        this.context = context;
        markerAction = new javax.swing.AbstractAction() {
            public void actionPerformed(ActionEvent e) {
            }
        };
        textComponentCaretListener = new TextComponentCaretListener();
        textComponentPCL = new TextComponentPCL();
        
        installProxyActions();
    }

    public static synchronized void install(final ApplicationContext context) {
        //TODO this should be one per context
        if (!installed) {
            context.addPropertyChangeListener("focusOwner", new PropertyChangeListener() {
                private StyledTextActions textActions = new StyledTextActions(context);

                public void propertyChange(PropertyChangeEvent evt) {
                    textActions.updateFocusOwner((JComponent) evt.getOldValue(),
                            (JComponent) evt.getNewValue());
                }

            });
            
            installed = true;
        }
    }
    
    private ApplicationContext getContext() {
        return context;
    }

    /* Called by the KeyboardFocus PropertyChangeListener in ApplicationContext,
     * before any other focus-change related work is done.
     */
    public void updateFocusOwner(JComponent oldOwner, JComponent newOwner) {
        if (oldOwner instanceof JTextComponent) {
            JTextComponent text = (JTextComponent) oldOwner;
            text.removeCaretListener(textComponentCaretListener);
            text.removePropertyChangeListener(textComponentPCL);
        }
        if (newOwner instanceof JTextComponent) {
            JTextComponent text = (JTextComponent) newOwner;
            maybeInstallTextActions(text);
            updateTextActions(text);
            text.addCaretListener(textComponentCaretListener);
            text.addPropertyChangeListener(textComponentPCL);
        } else if (newOwner == null) {
            setBoldEnabled(false);
            setBoldSelected(false);
        }
    }

    private final class TextComponentCaretListener implements CaretListener {
        public void caretUpdate(CaretEvent e) {
            updateTextActions((JTextComponent) (e.getSource()));
        }
    }

    private final class TextComponentPCL implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            String propertyName = e.getPropertyName();
            if ((propertyName == null) || "editable".equals(propertyName)) {
                updateTextActions((JTextComponent) (e.getSource()));
            }
        }
    }
    
    @ProxyActions({"bold", "italic", "underline"})
    public static final class ProxyActionsContainer {
        //exists to provide proxy action cover for the actions in the outer class
    }

    //TODO hack around loosing actions from map
    private ApplicationActionMap main;
    
    private void installProxyActions() {
        ProxyActionsContainer pac = new ProxyActionsContainer();
        ApplicationActionMap aam = getContext().getActionMap(pac);
        main = getContext().getActionMap();
        
        for (Object key : aam.allKeys()) {
            main.put(key, aam.get(key));
        }
    }
    
    private void updateTextActions(JTextComponent text) {
        if (text.getDocument() instanceof StyledDocument) {
            StyledDocument document = (StyledDocument) text.getDocument();
            boolean editable = text.isEditable();
            
            Caret caret = text.getCaret();
            int dot = caret.getDot();
            dot = dot > 0 ? dot - 1 : dot;
            Element elem = document.getCharacterElement(dot);
            AttributeSet set = elem.getAttributes();
            
            setBoldEnabled(editable);
            setBoldSelected(StyleConstants.isBold(set));
            setItalicEnabled(editable);
            setItalicSelected(StyleConstants.isItalic(set));
            setUnderlineEnabled(editable);
            setUnderlineSelected(StyleConstants.isUnderline(set));
        } else {
            setBoldEnabled(false);
            setBoldSelected(false);
            setItalicEnabled(false);
            setItalicSelected(false);
            setUnderlineEnabled(false);
            setUnderlineSelected(false);
        }
        
    }

    // TBD: what if text.getActionMap is null, or if it's parent isn't the UI-installed actionMap
    private void maybeInstallTextActions(JTextComponent text) {
        ActionMap actionMap = text.getActionMap();
        if (actionMap.get(markerActionKey) == null) {
            actionMap.put(markerActionKey, markerAction);
            ActionMap textActions = getContext().getActionMap(getClass(), this);
            for (Object key : textActions.keys()) {
                actionMap.put(key, textActions.get(key));
            }
        }
    }

    /* This method lifted from JTextComponent.java 
     */
    private int getCurrentEventModifiers() {
        int modifiers = 0;
        AWTEvent currentEvent = EventQueue.getCurrentEvent();
        if (currentEvent instanceof InputEvent) {
            modifiers = ((InputEvent) currentEvent).getModifiers();
        } else if (currentEvent instanceof ActionEvent) {
            modifiers = ((ActionEvent) currentEvent).getModifiers();
        }
        return modifiers;
    }

    private void invokeTextAction(JTextComponent text, String actionName) {
        ActionMap actionMap = text.getActionMap().getParent();
        long eventTime = EventQueue.getMostRecentEventTime();
        int eventMods = getCurrentEventModifiers();
        ActionEvent actionEvent =
                new ActionEvent(text, ActionEvent.ACTION_PERFORMED, actionName, eventTime, eventMods);
        actionMap.get(actionName).actionPerformed(actionEvent);
    }

    /**
     * @return the boldEnabled
     */
    public boolean isBoldEnabled() {
        return boldEnabled;
    }

    /**
     * @param boldEnabled the boldEnabled to set
     */
    public void setBoldEnabled(boolean boldEnabled) {
        boolean oldValue = isBoldEnabled();
        this.boldEnabled = boldEnabled;
        firePropertyChange("boldEnabled", oldValue, isBoldEnabled());
    }

    /**
     * @return the boldSelected
     */
    public boolean isBoldSelected() {
        return boldSelected;
    }

    /**
     * @param boldSelected the boldSelected to set
     */
    public void setBoldSelected(boolean boldSelected) {
        boolean oldValue = isBoldSelected();
        this.boldSelected = boldSelected;
        firePropertyChange("boldSelected", oldValue, isBoldSelected());
    }

    @Action(enabledProperty = "boldEnabled", selectedProperty = "boldSelected")
    public void bold(ActionEvent e) {
        Object src = e.getSource();
        if (src instanceof JTextComponent) {
            invokeTextAction((JTextComponent) src, "font-bold");
        }
    }
    
    /**
     * @return the boldEnabled
     */
    public boolean isItalicEnabled() {
        return italicEnabled;
    }
    
    /**
     * @param italicEnabled the boldEnabled to set
     */
    public void setItalicEnabled(boolean italicEnabled) {
        boolean oldValue = isItalicEnabled();
        this.italicEnabled = italicEnabled;
        firePropertyChange("italicEnabled", oldValue, isItalicEnabled());
    }
    
    /**
     * @return the boldSelected
     */
    public boolean isItalicSelected() {
        return italicSelected;
    }
    
    /**
     * @param italicSelected the boldSelected to set
     */
    public void setItalicSelected(boolean italicSelected) {
        boolean oldValue = isItalicSelected();
        this.italicSelected = italicSelected;
        firePropertyChange("italicSelected", oldValue, isItalicSelected());
    }
    
    @Action(enabledProperty = "italicEnabled", selectedProperty = "italicSelected")
    public void italic(ActionEvent e) {
        Object src = e.getSource();
        if (src instanceof JTextComponent) {
            invokeTextAction((JTextComponent) src, "font-italic");
        }
    }
    
    /**
     * @return the underlineEnabled
     */
    public boolean isUnderlineEnabled() {
        return underlineEnabled;
    }
    
    /**
     * @param underlineEnabled the underlineEnabled to set
     */
    public void setUnderlineEnabled(boolean underlineEnabled) {
        boolean oldValue = isUnderlineEnabled();
        this.underlineEnabled = underlineEnabled;
        firePropertyChange("underlineEnabled", oldValue, isUnderlineEnabled());
    }
    
    /**
     * @return the underlineSelected
     */
    public boolean isUnderlineSelected() {
        return underlineSelected;
    }
    
    /**
     * @param underlineSelected the underlineSelected to set
     */
    public void setUnderlineSelected(boolean underlineSelected) {
        boolean oldValue = isUnderlineSelected();
        this.underlineSelected = underlineSelected;
        firePropertyChange("underlineSelected", oldValue, isUnderlineSelected());
    }
    
    @Action(enabledProperty = "underlineEnabled", selectedProperty = "underlineSelected")
    public void underline(ActionEvent e) {
        Object src = e.getSource();
        if (src instanceof JTextComponent) {
            invokeTextAction((JTextComponent) src, "font-underline");
        }
    }
}
