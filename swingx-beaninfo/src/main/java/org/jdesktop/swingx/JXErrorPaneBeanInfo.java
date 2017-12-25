package org.jdesktop.swingx;

import java.beans.BeanDescriptor;

import org.jdesktop.beans.BeanInfoSupport;

/**
 * BeanInfo class for JXErrorPane.
 * 
 * @author Jan Stola
 */
public class JXErrorPaneBeanInfo extends BeanInfoSupport {

    public JXErrorPaneBeanInfo() {
        super(JXErrorPane.class);        
    }

    @Override
    protected void initialize() {
        BeanDescriptor bd = getBeanDescriptor();
        bd.setValue("isContainer", Boolean.FALSE);
        setPreferred(true, "errorInfo", "icon");
        setPreferred(false, "background", "border", "foreground", "toolTipText");
    }

}
