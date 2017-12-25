package org.jdesktop.swingx;

import org.jdesktop.beans.BeanInfoSupport;
import org.jdesktop.beans.editors.PainterPropertyEditor;

/**
 * BeanInfo class for JXButton.
 * 
 * @author Jan Stola
 */
public class JXButtonBeanInfo extends BeanInfoSupport {

    public JXButtonBeanInfo() {
        super(JXButton.class);        
    }
    
    @Override
    protected void initialize() {
        setPreferred(true, "backgroundPainter", "foregroundPainter");
        setPropertyEditor(PainterPropertyEditor.class, "backgroundPainter");
        setPropertyEditor(PainterPropertyEditor.class, "foregroundPainter");
    }
}
