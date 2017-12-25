/*
 * ShapePropertyEditor.java
 *
 * Created on August 23, 2006, 10:17 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.beans.editors;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.beans.PropertyEditorSupport;


/**
 *
 * @author joshy
 */
public class ShapePropertyEditor extends PropertyEditorSupport {
    ShapeChooser chooser;
    /** Creates a new instance of ShapePropertyEditor */
    public ShapePropertyEditor() {
        chooser = new ShapeChooser();
        chooser.shapeCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                if(chooser.shapeCombo.getSelectedItem().equals("Square")) {
                    setValue(new Rectangle(0,0,100,100));
                } else {
                    setValue(new Ellipse2D.Double(0,0,100,100));
                }
            }
        });
    }
    
    @Override
    public Shape getValue() {
        return (Shape)super.getValue();
    }
    
    @Override
    public void setValue(Object value) {
        super.setValue(value);
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
        return chooser;
    }
}
