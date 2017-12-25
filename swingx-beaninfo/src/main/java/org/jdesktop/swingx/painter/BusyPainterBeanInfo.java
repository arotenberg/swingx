package org.jdesktop.swingx.painter;

import org.jdesktop.beans.BeanInfoSupport;
import org.jdesktop.beans.editors.EnumPropertyEditor;
import org.jdesktop.beans.editors.ShapePropertyEditor;
import org.jdesktop.swingx.painter.GlossPainterBeanInfo.GlossPositionPropertyEditor;

public class BusyPainterBeanInfo extends BeanInfoSupport {
    public BusyPainterBeanInfo() {
        super(BusyPainter.class);
    }

    @Override
    protected void initialize() {
        setPropertyEditor(GlossPositionPropertyEditor.class, "direction");
        setPropertyEditor(ShapePropertyEditor.class, "pointShape");
        setPropertyEditor(ShapePropertyEditor.class, "trajectory");
    }

    public static final class DirectionPropertyEditor extends EnumPropertyEditor<BusyPainter.Direction> {
        public DirectionPropertyEditor() {
            super(BusyPainter.Direction.class);
        }
    }
}
