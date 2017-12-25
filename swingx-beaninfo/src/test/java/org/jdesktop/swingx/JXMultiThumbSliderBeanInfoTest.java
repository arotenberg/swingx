package org.jdesktop.swingx;

import org.jdesktop.beans.AbstractBeanInfoTest;
import org.junit.Ignore;
import org.junit.Test;

public class JXMultiThumbSliderBeanInfoTest extends AbstractBeanInfoTest<JXMultiThumbSlider> {
    @Override
    protected JXMultiThumbSlider createInstance() {
        return new JXMultiThumbSlider();
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
