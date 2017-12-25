package org.jdesktop.beans.editors;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * A demo program to showcase {@link PaintPicker}.
 * 
 * @author Karl Schaefer
 */
@SuppressWarnings("all")
public class PaintPickerDemo extends JFrame {
    /**
     * {@inheritDoc}
     */
    @Override
    protected void frameInit() {
        super.frameInit();
        
        setTitle("PaintPicker Demo");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        add(new PaintPicker());
        
        pack();
    }
    
    /**
     * Demo entry point.
     * 
     * @param args
     *            unused
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new PaintPickerDemo().setVisible(true);
            }
        });
    }
}
