/*
 * Created on 14.02.2012
 *
 */
package org.jdesktop.test;

import java.awt.Component;

import org.jdesktop.beans.AbstractBean;

/**
 * Subclass as quick-fix for dependency issue.
 * 
 * PENDING JW: need better solution ... 
 *  PropertyChangeReport resides in swingx-testsupport which is stand-alone.
 *  Convenience constructor needs AbstractBean which is in swingx-common.
 * 
 * @author Jeanette Winzenburg, Berlin
 */
public class PropertyReport extends PropertyChangeReport {

    /**
     * Instantiates a PropertyChangeReport.
     */
    public PropertyReport() {
        this((AbstractBean) null);
    }
    
    /**
     * Instantiates a PropertyChangeReport and registers itself with the given bean
     * if that is not null.
     * 
     * @param bean the AbstractBean to register itself to.
     */
    public PropertyReport(AbstractBean bean) {
        if (bean != null) {
            bean.addPropertyChangeListener(this);
        }
    }
    
    /**
     * Instantiates a PropertyChangeReport and registers itself with the given component
     * if that is not null.
     * 
     * @param component the Component to register itself to.
     */
    public PropertyReport(Component component) {
        if(component != null) {
            component.addPropertyChangeListener(this);
        }
    }

}
