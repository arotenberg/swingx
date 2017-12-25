package org.jdesktop.swingx.painter;

import org.jdesktop.beans.BeanInfoSupport;
import org.jdesktop.beans.editors.EnumPropertyEditor;

/**
 * BeanInfo of AbstractPainter.
 *
 * @author Richard, Jan Stola
 */
public class AbstractPainterBeanInfo extends BeanInfoSupport {
    
    /** Creates a new instance of BackgroundPainterBeanInfo */
    public AbstractPainterBeanInfo() {
        super(AbstractPainter.class);
    }
    
    public AbstractPainterBeanInfo(Class clazz) {
        super(clazz);
    }

    @Override
    protected void initialize() {
        setHidden(true, "class", "propertyChangeListeners", "vetoableChangeListeners", "filters");
        setPropertyEditor(InterpolationPropertyEditor.class, "interpolation");
        //move some items into "Appearance" and some into "Behavior"
        //setCategory("Rendering Hints", "antialiasing", "fractionalMetrics", "interpolation");
        setExpert(true, "antialiasing","cacheable","interpolation");
    }
    
    public static final class InterpolationPropertyEditor extends EnumPropertyEditor<AbstractPainter.Interpolation> {
        public InterpolationPropertyEditor() {
            super(AbstractPainter.Interpolation.class);
        }
    }
}
