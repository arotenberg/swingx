package org.jdesktop.swingx;

import org.jdesktop.beans.AbstractBeanInfoTest;
import org.junit.Ignore;
import org.junit.Test;

public class JXGraphBeanInfoTest extends AbstractBeanInfoTest<JXGraph> {
    @Override
    protected JXGraph createInstance() {
        return new JXGraph();
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
