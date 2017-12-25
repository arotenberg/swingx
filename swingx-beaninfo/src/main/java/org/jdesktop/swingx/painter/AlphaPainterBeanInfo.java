package org.jdesktop.swingx.painter;

import org.jdesktop.beans.BeanInfoSupport;

/**
 * BeanInfo of AlphaPainter.
 *
 * @author Jan Stola
 */
public class AlphaPainterBeanInfo extends BeanInfoSupport {
    
    public AlphaPainterBeanInfo() {
        super(AlphaPainter.class);
    }
    
    @Override
    protected void initialize() {
        setPreferred(true, "alpha");
    }

}
