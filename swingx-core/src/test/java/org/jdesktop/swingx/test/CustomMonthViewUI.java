/*
 * Created on 19.12.2007
 *
 */
package org.jdesktop.swingx.test;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;

import org.jdesktop.swingx.plaf.basic.BasicMonthViewUI;

/**
 * 
 * Testing custom ui delegate for JXMonthView.
 * 
 * @author Jeanette Winzenburg
 */
public class CustomMonthViewUI extends BasicMonthViewUI {
    @SuppressWarnings({"UnusedDeclaration"})
    public static ComponentUI createUI(JComponent c) {
        return new CustomMonthViewUI();
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
        monthView.setSelectionBackground(new ColorUIResource(Color.MAGENTA));
    }
    
    
}