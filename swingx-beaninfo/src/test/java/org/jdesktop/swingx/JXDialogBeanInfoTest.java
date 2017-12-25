package org.jdesktop.swingx;

import org.jdesktop.beans.AbstractBeanInfoTest;
import org.junit.Ignore;

@Ignore
public class JXDialogBeanInfoTest extends AbstractBeanInfoTest<JXDialog> {
    @Override
    protected JXDialog createInstance() {
        //TODO we must have a nullary constructor
        return new JXDialog(null);
    }
}
