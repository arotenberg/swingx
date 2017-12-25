package org.jdesktop.swingx.search;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JTextField;

import junit.framework.Assert;

import org.junit.Test;


public class NativeSearchFieldSupportTest implements PropertyChangeListener {
	private JTextField tf = new JTextField();
	private boolean eventFired;
	
	@Test
	public void testSearchFieldPropertyChangeEvent() throws Exception {
		tf.addPropertyChangeListener(NativeSearchFieldSupport.MAC_TEXT_FIELD_VARIANT_PROPERTY, this);
		NativeSearchFieldSupport.setSearchField(tf, true);
		Assert.assertTrue(eventFired);
		eventFired = false;
		NativeSearchFieldSupport.setSearchField(tf, true);
		Assert.assertTrue(eventFired);
	}
	
	@Test
	public void testFindActionPropertyChangeEvent() throws Exception {
		tf.addPropertyChangeListener(NativeSearchFieldSupport.FIND_ACTION_PROPERTY, this);
		NativeSearchFieldSupport.setFindAction(tf, new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				eventFired = true;
			}
		});
		Assert.assertTrue(eventFired);
		eventFired = false;
		NativeSearchFieldSupport.setFindAction(tf, null);
		Assert.assertTrue(eventFired);
	}
	
	@Test
	public void testCancelActionPropertyChangeEvent() throws Exception {
		tf.addPropertyChangeListener(NativeSearchFieldSupport.CANCEL_ACTION_PROPERTY, this);
		NativeSearchFieldSupport.setCancelAction(tf, new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				eventFired = true;
			}
		});
		Assert.assertTrue(eventFired);
		eventFired = false;
		NativeSearchFieldSupport.setCancelAction(tf, null);
		Assert.assertTrue(eventFired);
	}
	
	@Test
	public void testSearchFieldUIChange() throws Exception {
		NativeSearchFieldSupport.setSearchField(tf, true);
		tf.addPropertyChangeListener(NativeSearchFieldSupport.MAC_TEXT_FIELD_VARIANT_PROPERTY, this);
		tf.updateUI();
		Assert.assertTrue(eventFired);
		
		NativeSearchFieldSupport.setSearchField(tf, false);
		eventFired = false;
		tf.updateUI();
		Assert.assertFalse(eventFired);
	}
	
	@Test
	public void testIsSearchField() throws Exception {
		NativeSearchFieldSupport.setSearchField(tf, true);
		Assert.assertTrue(NativeSearchFieldSupport.isSearchField(tf));
		
		NativeSearchFieldSupport.setSearchField(tf, false);
		Assert.assertFalse(NativeSearchFieldSupport.isSearchField(tf));
	}

	public void propertyChange(PropertyChangeEvent evt) {
		eventFired = true;
	}
}
