package org.jdesktop.swingx.plaf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.text.JTextComponent;

import org.jdesktop.swingx.prompt.PromptSupport;
import org.jdesktop.swingx.util.JVM;
import org.junit.Before;
import org.junit.Test;

public abstract class PromptTextUITest_Base {
    protected JTextComponent textComponent;
    protected PromptTextUI ui;

    @Before
    public void setUI() {
        setup();
        TextUIWrapper.getDefaultWrapper().install(textComponent, true);
        ui = (PromptTextUI) textComponent.getUI();
    }

    public abstract void setup();
    
    @Test
    public void testGetBaseLine() {
    	int baseline = ui.getBaseline(textComponent, textComponent.getWidth(), textComponent.getHeight());
		if(JVM.current().isOrLater(JVM.JDK1_6)){
    		assertNotSame(-2, baseline);
    	}else{
    		assertSame(-2, baseline);
    	}
    }

    @Test
    public void testInstallUI() {
        textComponent.setUI(ui);
        //ui should already be installed.
        assertTrue(Arrays.asList(textComponent.getFocusListeners()).contains(PromptTextUI.focusHandler));
    }

    @Test
    public void testUninstallUI() {
        ui.uninstallUI(textComponent);
        assertFalse(Arrays.asList(textComponent.getFocusListeners()).contains(PromptTextUI.focusHandler));
    }

    @Test
    public void testShouldPaintLabel() {
        assertTrue(ui.shouldPaintPrompt(textComponent));
        textComponent.setText("");
        assertTrue(ui.shouldPaintPrompt(textComponent));
        textComponent.setText("test");
        assertFalse(ui.shouldPaintPrompt(textComponent));
    }
    
    @Test
    public void testCreateLabelComponent() {
        assertNotNull(ui.createPromptComponent());
    }

    @Test
    public void testGetLabelComponent() {
        PromptSupport.setPrompt("test", textComponent);
        PromptSupport.setForeground(Color.BLACK, textComponent);
        PromptSupport.setBackground(Color.RED, textComponent);
        
        textComponent.setBorder(BorderFactory.createBevelBorder(1));
        textComponent.setEnabled(false);
        textComponent.setEditable(false);
        textComponent.setOpaque(false);
        textComponent.setBounds(new Rectangle(1,1));
        textComponent.setBackground(Color.BLACK);
        textComponent.setFont(textComponent.getFont().deriveFont(Font.ITALIC, 20));
        textComponent.setSelectedTextColor(Color.BLACK);
        textComponent.setSelectionColor(Color.BLACK);
        textComponent.setMargin(new Insets(1,1,1,1));
        JTextComponent lbl = ui.getPromptComponent(textComponent);
        
        assertEquals(PromptSupport.getPrompt(textComponent), lbl.getText());
        assertEquals(PromptSupport.getForeground(textComponent), lbl.getForeground());
        assertEquals(PromptSupport.getBackground(textComponent), lbl.getBackground());
        
        assertEquals(textComponent.getBorder().getBorderInsets(textComponent),
                lbl.getBorder().getBorderInsets(lbl));
        assertEquals(textComponent.isEnabled(), lbl.isEnabled());
        assertEquals(textComponent.isEditable(), lbl.isEditable());
        assertEquals(textComponent.isOpaque(), lbl.isOpaque());
        assertEquals(textComponent.getBounds(), lbl.getBounds());
        assertEquals(textComponent.getFont(), lbl.getFont());
        assertEquals(textComponent.getSelectedTextColor(), lbl.getSelectedTextColor());
        assertEquals(textComponent.getSelectionColor(), lbl.getSelectionColor());
        assertEquals(textComponent.getMargin(), lbl.getMargin());
        
        PromptSupport.setFontStyle(Font.BOLD, textComponent);
        lbl = ui.getPromptComponent(textComponent);
        assertEquals(textComponent.getFont().deriveFont(Font.BOLD), lbl.getFont());
    }

    @Test
    public void testGetPreferredSize() {
        textComponent.setText("label text");
        PromptSupport.setPrompt("label text", textComponent);
        assertEquals(textComponent.getPreferredSize(), ui.getPromptComponent(textComponent).getPreferredSize());
        
        textComponent.setText("text");
        assertFalse(textComponent.getPreferredSize().equals(ui.getPromptComponent(textComponent).getPreferredSize()));
    }
    
    @Test
    public void testPromptSupportStaysInstalledOnUIChange() {
    	assertTrue(textComponent.getUI() instanceof PromptTextUI);
    	textComponent.updateUI();
    	assertTrue(textComponent.getUI() instanceof PromptTextUI);
    }
}
