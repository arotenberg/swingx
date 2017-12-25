package org.jdesktop.swingx;

import org.jdesktop.beans.AbstractBeanInfoTest;
import org.junit.Ignore;
import org.junit.Test;

public class JXGradientChooserBeanInfoTest extends AbstractBeanInfoTest<JXGradientChooser> {
    @Override
    protected JXGradientChooser createInstance() {
        return new JXGradientChooser();
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
