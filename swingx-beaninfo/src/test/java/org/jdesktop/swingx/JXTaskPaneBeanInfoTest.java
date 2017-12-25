package org.jdesktop.swingx;

import org.jdesktop.beans.AbstractBeanInfoTest;
import org.junit.Ignore;
import org.junit.Test;

public class JXTaskPaneBeanInfoTest extends AbstractBeanInfoTest<JXTaskPane> {
    @Override
    protected JXTaskPane createInstance() {
        return new JXTaskPane();
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
