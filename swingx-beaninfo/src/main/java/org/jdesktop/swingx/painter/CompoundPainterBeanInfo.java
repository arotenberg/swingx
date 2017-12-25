package org.jdesktop.swingx.painter;

import org.jdesktop.beans.BeanInfoSupport;

/**
 * BeanInfo of CompoundPainter.
 *
 * @author Richard, Jan Stola
 */
public class CompoundPainterBeanInfo extends BeanInfoSupport {
    
    /** Creates a new instance of CompoundPainterBeanInfo */
    public CompoundPainterBeanInfo() {
        super(CompoundPainter.class);
    }

    @Override
    protected void initialize() {
        setPreferred(true, "painters");
    }
}
