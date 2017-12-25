package org.jdesktop.swingx;

import org.jdesktop.beans.BeanInfoSupport;

/**
 * BeanInfo class for HorizontalLayout.
 * 
 * @author Jan Stola
 */
public class HorizontalLayoutBeanInfo extends BeanInfoSupport {

    public HorizontalLayoutBeanInfo() {
        super(HorizontalLayout.class);        
    }
    
    @Override
    protected void initialize() {
        setHidden(true, "class");
    }

}
