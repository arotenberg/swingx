package org.jdesktop.swingx;

import org.jdesktop.beans.AbstractBeanInfoTest;
import org.junit.Ignore;
import org.junit.Test;

public class JXColorSelectionButtonBeanInfoTest extends AbstractBeanInfoTest<JXColorSelectionButton> {
    @Override
    protected JXColorSelectionButton createInstance() {
        return new JXColorSelectionButton();
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
