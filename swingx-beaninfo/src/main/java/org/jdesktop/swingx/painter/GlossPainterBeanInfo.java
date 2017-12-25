package org.jdesktop.swingx.painter;

import org.jdesktop.beans.BeanInfoSupport;
import org.jdesktop.beans.editors.EnumPropertyEditor;
import org.jdesktop.beans.editors.Paint2PropertyEditor;

/**
 * BeanInfo of GlossPainter.
 *
 * @author joshy
 */
public class GlossPainterBeanInfo extends BeanInfoSupport {
    
    /** Creates a new instance of GlossPainterBeanInfo */
    public GlossPainterBeanInfo() {
        super(GlossPainter.class);
    }
    
    @Override
    protected void initialize() {
        setPropertyEditor(Paint2PropertyEditor.class,"paint");
        setPropertyEditor(GlossPositionPropertyEditor.class, "position");
    }

    public static final class GlossPositionPropertyEditor extends EnumPropertyEditor<GlossPainter.GlossPosition> {
        public GlossPositionPropertyEditor() {
            super(GlossPainter.GlossPosition.class);
        }
    }

}
