/*
 * Created on 03.12.2008
 *
 */
package org.jdesktop.swingx.util;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

/**
 * Quick stateful subclass: not shareable. With convenience 
 * card navigation methods.
 * 
 */
public class XCardLayout extends CardLayout {

    List<String> constraints;
    int current;
    Container parent;
    
    public XCardLayout(Container parent) {
        super();
        constraints = new ArrayList<String>();
        parent.setLayout(this);
        this.parent = parent;
    }
    
    
    @Override
    public void addLayoutComponent(String name, Component comp) {
        constraints.add(name);
        super.addLayoutComponent(name, comp);
    }


    public void next() {
        if (constraints.size() == 0) {
            return;
        }
        current++;
        if (current > (constraints.size() -1)) {
            current = 0;
        }
        show(parent, constraints.get(current));
    }
    
    public void previous() {
        
    }


    public void revalidate(String string) {
        ((JComponent) parent).revalidate();
        parent.repaint();
        
    }
}
