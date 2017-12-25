package org.jdesktop.swingx.plaf.basic;

import static org.junit.Assert.assertSame;

import javax.swing.UIManager;
import javax.swing.text.AbstractDocument;

import org.jdesktop.swingx.JXSearchField;
import org.jdesktop.swingx.plaf.SearchFieldUI;
import org.jdesktop.swingx.search.NativeSearchFieldSupport;
import org.junit.Before;
import org.junit.Test;


public class BasicSearchFieldUITest {
	private JXSearchField sf;
	private SearchFieldUI ui;
	
	@Before
	public void setUp() {
		UIManager.put("SearchField.useSeperatePopupButton", Boolean.FALSE);
		sf = new JXSearchField();
		sf.setUseNativeSearchFieldIfPossible(false);
		ui = (SearchFieldUI) sf.getUI();
	}
	
	@Test
	public void testChildComponents() throws Exception {
		assertSame(3, sf.getComponentCount());
		ui.uninstallUI(sf);
		assertSame(0, sf.getComponentCount());
	}
	
	@Test
	public void testChildComponentsNative() throws Exception {
		if(!NativeSearchFieldSupport.isNativeSearchFieldSupported()){
			return;
		}
		
		sf.setUseNativeSearchFieldIfPossible(true);
		
		//search and clear buttons installed by AquaLnF
		assertSame(2, sf.getComponentCount());
	}

	@Test
	public void testDocumentListener() throws Exception {
		int listenerCount = ((AbstractDocument)sf.getDocument()).getDocumentListeners().length;
		ui.uninstallUI(sf);
		assertSame(listenerCount-3, ((AbstractDocument)sf.getDocument()).getDocumentListeners().length);
	}
	
	@Test
	public void testPopupListener() throws Exception {
		assertSame(1, sf.getPopupButton().getActionListeners().length);
		ui.uninstallUI(sf);
		assertSame(0, sf.getPopupButton().getActionListeners().length);
	}
}
