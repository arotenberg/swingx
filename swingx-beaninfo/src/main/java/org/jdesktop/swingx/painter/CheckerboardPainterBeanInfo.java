package org.jdesktop.swingx.painter;

import org.jdesktop.beans.BeanInfoSupport;
import org.jdesktop.beans.editors.PaintPropertyEditor;

/**
 * BeanInfo of CheckerboardPainter.
 *
 * @author Richard, Jan Stola
 */
public class CheckerboardPainterBeanInfo extends BeanInfoSupport {
    
    /** Creates a new instance of CheckerboardPainterBeanInfo */
    public CheckerboardPainterBeanInfo() {
        super(CheckerboardPainter.class);
    }

    @Override
    protected void initialize() {
        setPreferred(true, "darkPaint", "lightPaint", "squareSize");
        setPropertyEditor(PaintPropertyEditor.class, "darkPaint", "lightPaint");
    }
}
