package org.jdesktop.swingx;

import java.beans.BeanDescriptor;

import org.jdesktop.beans.BeanInfoSupport;

/**
 * BeanInfo class for JXTitledPanel.
 *
 * @author Richard, Jan Stola
 */
public class JXTitledPanelBeanInfo extends BeanInfoSupport {

    public JXTitledPanelBeanInfo() {
        super(JXTitledPanel.class);
    }
    
    @Override
    protected void initialize() {
        BeanDescriptor bd = getBeanDescriptor();
        bd.setName("JXTitledPanel");
        bd.setShortDescription("A special type of Panel that has a Title section and a Content section.");
        bd.setValue("isContainer", Boolean.TRUE);
        bd.setValue("containerDelegate", "getContentContainer");
        setPreferred(true, "title", "titleFont", "titleForeground", "titlePainter");
        setPreferred(true, "leftDecoration", "rightDecoration");
        setPreferred(false, "alpha", "border", "inheritAlpha");
    }
}
