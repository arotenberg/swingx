package org.jdesktop.swingx;

import org.jdesktop.beans.AbstractBeanInfoTest;
import org.junit.Ignore;
import org.junit.Test;

public class JXTitledPanelBeanInfoTest extends AbstractBeanInfoTest<JXTitledPanel> {
    @Override
    protected JXTitledPanel createInstance() {
        return new JXTitledPanel();
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
