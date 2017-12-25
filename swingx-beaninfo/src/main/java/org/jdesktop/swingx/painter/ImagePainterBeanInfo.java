package org.jdesktop.swingx.painter;

import org.jdesktop.beans.BeanInfoSupport;
import org.jdesktop.beans.editors.EnumPropertyEditor;
import org.jdesktop.beans.editors.ImageEditor;

/**
 * BeanInfo of ImagePainter.
 *
 * @author joshy, Jan Stola
 */
public class ImagePainterBeanInfo extends BeanInfoSupport {
    
    /** Creates a new instance of ImagePainterBeanInfo */
    public ImagePainterBeanInfo() {
        super(ImagePainter.class);
    }
    
    @Override
    protected void initialize() {
        setPropertyEditor(ImageEditor.class,"image");
        setPropertyEditor(ScaleTypePropertyEditor.class, "scaleType");
        setPreferred(true, "image", "scaleType", "scaleToFit");
    }

    public static final class ScaleTypePropertyEditor extends EnumPropertyEditor<ImagePainter.ScaleType> {
        public ScaleTypePropertyEditor() {
            super(ImagePainter.ScaleType.class);
        }
    }

}
