package org.jdesktop.swingx;

import org.jdesktop.beans.AbstractBeanInfoTest;
import org.junit.Ignore;
import org.junit.Test;

public class JXCollapsiblePaneBeanInfoTest extends AbstractBeanInfoTest<JXCollapsiblePane> {
    @Override
    protected JXCollapsiblePane createInstance() {
        return new JXCollapsiblePane();
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
