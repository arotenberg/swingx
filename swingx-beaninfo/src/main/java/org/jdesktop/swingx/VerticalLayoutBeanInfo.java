package org.jdesktop.swingx;

import org.jdesktop.beans.BeanInfoSupport;

/**
 * BeanInfo class for VerticalLayout.
 * 
 * @author Jan Stola
 */
public class VerticalLayoutBeanInfo extends BeanInfoSupport {

    public VerticalLayoutBeanInfo() {
        super(VerticalLayout.class);        
    }
    
    @Override
    protected void initialize() {
        setHidden(true, "class");
    }

}
