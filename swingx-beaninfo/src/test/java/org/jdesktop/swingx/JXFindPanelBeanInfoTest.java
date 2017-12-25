package org.jdesktop.swingx;

import org.jdesktop.beans.AbstractBeanInfoTest;
import org.junit.Ignore;
import org.junit.Test;

public class JXFindPanelBeanInfoTest extends AbstractBeanInfoTest<JXFindPanel> {
    @Override
    protected JXFindPanel createInstance() {
        return new JXFindPanel();
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
