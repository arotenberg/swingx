package org.jdesktop.beans.editors;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * A demo program to showcase {@link PaintPicker}.
 * 
 * @author Karl Schaefer
 */
@SuppressWarnings("all")
public class ImagePickerDemo extends JFrame {
    /**
     * {@inheritDoc}
     */
    @Override
    protected void frameInit() {
        super.frameInit();
        
        setTitle("ImagePicker Demo");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        add(new ImagePicker());
        
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
                new ImagePickerDemo().setVisible(true);
            }
        });
    }
}
