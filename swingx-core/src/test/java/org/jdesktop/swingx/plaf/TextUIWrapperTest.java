package org.jdesktop.swingx.plaf;

import static org.junit.Assert.assertEquals;

import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicTextFieldUI;

import org.jdesktop.swingx.JXSearchField;
import org.junit.Before;
import org.junit.Test;


public class TextUIWrapperTest {
	private JTextField txt;
	private TextUIWrapper<? extends PromptTextUI> wrapper;

	@Before
    public void setup() {
        txt = new JTextField();
        wrapper = TextUIWrapper.getDefaultWrapper();
    }
    
	@Test
    public void testWrapUI() throws Exception {
    	assertEquals(BuddyTextFieldUI.class, wrapper.wrapUI(new JTextField()).getClass());
    	assertEquals(PromptTextAreaUI.class, wrapper.wrapUI(new JTextArea()).getClass());
    	assertEquals(SearchFieldUI.class, wrapper.wrapUI(new JXSearchField()).getClass());
	}
	
	
	@Test
    public void testUninstall() {
    	Class<?> defaultUiClass = txt.getUI().getClass();
    	wrapper.install(txt, true);
    	wrapper.uninstall(txt);
    	assertEquals(defaultUiClass, txt.getUI().getClass());
    }
	
	@Test
    public void testInstall() {
    	wrapper.install(txt, false);
    	mustBeInstalled();
	}
    
    @Test
    public void testInstallAndStay() {
    	wrapper.install(txt, true);
    	mustStayInstalled();
	}

	private void mustBeInstalled() {
		assertEquals(BuddyTextFieldUI.class, txt.getUI().getClass());
		txt.setUI(new BasicTextFieldUI());
    	assertEquals(BasicTextFieldUI.class, txt.getUI().getClass());
	}
	
	private void mustStayInstalled() {
		assertEquals(BuddyTextFieldUI.class, txt.getUI().getClass());
    	txt.setUI(new BasicTextFieldUI());
    	assertEquals(BuddyTextFieldUI.class, txt.getUI().getClass());
	}
}
