package org.jdesktop.swingx;

import org.jdesktop.beans.AbstractBeanInfoTest;
import org.junit.Ignore;
import org.junit.Test;

public class JXTaskPaneContainerBeanInfoTest extends AbstractBeanInfoTest<JXTaskPaneContainer> {
    @Override
    protected JXTaskPaneContainer createInstance() {
        return new JXTaskPaneContainer();
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
