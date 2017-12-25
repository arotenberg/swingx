package org.jdesktop.swingx.plaf;

import static org.junit.Assert.assertEquals;

import javax.swing.JTextArea;

import org.jdesktop.swingx.JXTextArea;
import org.junit.Test;

public class PromptTextAreaUITest extends PromptTextUITest_Base {
    JTextArea textArea;
    
    @Override
    public void setup() {
        textComponent = textArea = new JXTextArea();
    }
    
    @Override
    @Test
    public void testGetLabelComponent() {
        super.testGetLabelComponent();
        
        textArea.setRows(5);
        textArea.setColumns(5);
        JTextArea lbl = (JTextArea) ui.getPromptComponent(textArea);
        
        assertEquals(textArea.getRows(), lbl.getRows());
        assertEquals(textArea.getColumns(), lbl.getColumns());
    }
}
