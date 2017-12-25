package org.jdesktop.swingx.plaf;

import java.beans.PropertyChangeEvent;

import javax.swing.JTextField;

import org.jdesktop.test.EDTRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EDTRunner.class)
public class AbstractUIChangeHandlerTest {
	JTextField tf = new JTextField();
	AbstractUIChangeHandler ch;
	private boolean changed;
	
	@Before
	public void setUp() throws Exception {
		ch = new AbstractUIChangeHandler(){
			@Override
            public void propertyChange(PropertyChangeEvent evt) {
				changed = true;
			}
		};
	}
	
	@Test
	public void testInstall(){
		ch.install(tf);
		Assert.assertFalse(changed);
		tf.updateUI();
		Assert.assertTrue(changed);
	}
	
	@Test
	public void testUninstall(){
		ch.install(tf);
		ch.uninstall(tf);
		tf.updateUI();
		Assert.assertFalse(changed);
	}
	
	@Test
	public void testDoubleInstall(){
		ch.install(tf);
		ch.install(tf);
		ch.uninstall(tf);
		tf.updateUI();
		Assert.assertFalse(changed);
	}
}
