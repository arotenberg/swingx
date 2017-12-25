/*
 * ShapeEffectBeanInfo.java
 *
 * Created on August 23, 2006, 4:54 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painter.effects;

import org.jdesktop.beans.BeanInfoSupport;
import org.jdesktop.beans.editors.Paint2PropertyEditor;

/**
 *
 * @author joshy
 */
public class AbstractPathEffectBeanInfo extends BeanInfoSupport {
    
    /** Creates a new instance of ShapeEffectBeanInfo */
    public AbstractPathEffectBeanInfo() {
        super(AbstractAreaEffect.class);
    }
    
    @Override
    protected void initialize() {
        setHidden(true, "class");
        setPropertyEditor(Paint2PropertyEditor.class, "brushColor");
    }
    
}
