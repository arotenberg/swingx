package org.jdesktop.beans.editors;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditorSupport;

/**
 *
 * @author joshy
 */
public class Paint2PropertyEditor extends PropertyEditorSupport {
    Paint paint = new Color(0,128,255);
    PaintPicker picker = new PaintPicker();

    /** Creates a new instance of Paint2PropertyEditor */
    public Paint2PropertyEditor() {
        picker.addPropertyChangeListener("paint",new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                paint = picker.getPaint();
                firePropertyChange();
            }
        });
        
    }
    
    @Override
    public Paint getValue() {
        return paint;
    }

    @Override
    public void setValue(Object object) {
        paint = (Paint)object;
        picker.setPaint(paint);
        super.setValue(object);
    }

       
    @Override
    public String getJavaInitializationString() {
        Paint paint = getValue();
        //TODO!!!
        return paint == null ? "null" : 
            "org.jdesktop.swingx.painter.gradient.LinearGradientPainter.BLACK_STAR";
    }
    
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        // do nothing right now
    }
    
    @Override
    public String getAsText() {
        return "PainterText";
    }
    
    @Override
    public void paintValue(Graphics g, Rectangle box) {
        Graphics2D g2 = (Graphics2D)g;
        //picker.setPaint(getValue());
        g2.setPaint(picker.getDisplayPaint(box));
        g2.fill(box);
    }
    @Override
    public boolean isPaintable() {
        return true;
    }
    

    @Override
    public boolean supportsCustomEditor() {
        return true;
    }

    @Override
    public Component getCustomEditor() {
        return picker;
    }
    
}
