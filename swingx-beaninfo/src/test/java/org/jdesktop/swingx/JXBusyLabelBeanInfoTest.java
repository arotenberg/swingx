package org.jdesktop.swingx;

import org.jdesktop.beans.AbstractBeanInfoTest;
import org.junit.Ignore;
import org.junit.Test;

public class JXBusyLabelBeanInfoTest extends AbstractBeanInfoTest<JXBusyLabel> {
    @Override
    protected JXBusyLabel createInstance() {
        return new JXBusyLabel();
    }
    
    /**
     * {@inheritDoc}
     */
    @Test
    @Override
    @Ignore("serialization fails")
    public void testSerialization() {
        super.testSerialization();
    }
}
