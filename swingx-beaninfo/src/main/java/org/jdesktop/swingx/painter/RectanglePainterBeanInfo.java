package org.jdesktop.swingx.painter;

import org.jdesktop.beans.BeanInfoSupport;

/**
 * BeanInfo of RectanglePainter.
 *
 * @author joshy, Jan Stola
 */
public class RectanglePainterBeanInfo extends BeanInfoSupport {
    
    /** Creates a new instance of RectanglePainterBeanInfo */
    public RectanglePainterBeanInfo() {
        super(RectanglePainter.class);
    }
    
    @Override
    protected void initialize() {
        setPreferred(true, "roundHeight", "roundWidth", "rounded");
    }

}
