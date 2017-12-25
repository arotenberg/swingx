/*
 * Created on 14.02.2012
 *
 */
package org.jdesktop.test;

import org.jdesktop.beans.AbstractBean;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Contains unit tests around the xxReport classes.
 * 
 * @author Jeanette Winzenburg, Berlin
 */
public class AllReportsTest extends TestCase {


    /**
     * Multicast events not counted properly.
     */
    @Test
    public void testPropertyEventCount() {
        DummyBean bean = new DummyBean();
        PropertyReport report = new PropertyReport(bean);
        bean.fireMulticastChange();
        bean.fireNameChange("myProperty");
        assertEquals(2, report.getEventCount());
        assertEquals(1, report.getMultiCastEventCount());
        assertEquals(1, report.getNamedEventCount());
    }
    
    @Test
    public void testPropertyMultipleMulticastCount() {
        DummyBean bean = new DummyBean();
        PropertyReport report = new PropertyReport(bean);
        bean.fireMulticastChange();
        bean.fireNameChange("myProperty");
        bean.fireMulticastChange();
        assertEquals(2, report.getMultiCastEventCount());
        assertEquals(1, report.getNamedEventCount());
        
    }
    
    public static class DummyBean extends AbstractBean {
        
        public void fireNameChange(String name) {
            firePropertyChange(name, null, "somevalue");
        }
        
        public void fireMulticastChange() {
            firePropertyChange(null, null, null);
        }
    }
    

}
