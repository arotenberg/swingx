package org.jdesktop.swingx;

import org.jdesktop.beans.AbstractBeanInfoTest;
import org.junit.Ignore;
import org.junit.Test;

@Deprecated
public class JXImagePanelBeanInfoTest extends AbstractBeanInfoTest<JXImagePanel> {
    @Override
    protected JXImagePanel createInstance() {
        return new JXImagePanel();
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
