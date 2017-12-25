package org.jdesktop.swingx;

import org.jdesktop.beans.AbstractBeanInfoTest;
import org.junit.Ignore;
import org.junit.Test;

public class JXTipOfTheDayBeanInfoTest extends AbstractBeanInfoTest<JXTipOfTheDay> {
    @Override
    protected JXTipOfTheDay createInstance() {
        return new JXTipOfTheDay();
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
