package org.jdesktop.swingx;

import java.beans.BeanDescriptor;

import org.jdesktop.beans.BeanInfoSupport;

/**
 * BeanInfo class for JXFindBar.
 * 
 * @author Jan Stola
 */
public class JXFindBarBeanInfo extends BeanInfoSupport {

    public JXFindBarBeanInfo() {
        super(JXFindBar.class);
    }

    @Override
    protected void initialize() {
        BeanDescriptor bd = getBeanDescriptor();
        bd.setValue("isContainer", Boolean.FALSE);        
    }

}
