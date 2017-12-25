package org.jdesktop.swingx;

import java.awt.Font;

import javax.swing.text.JTextComponent;

import org.jdesktop.beans.BeanInfoSupport;
import org.jdesktop.beans.EnumerationValue;
import org.jdesktop.swingx.prompt.PromptSupport.FocusBehavior;

public abstract class JXPromptBeanInfo extends BeanInfoSupport {
	protected JXPromptBeanInfo(Class<? extends JTextComponent> beanClass) {
		super(beanClass);
	}

	protected void initialize() {
		setPreferred(true, "prompt", "promptForeground", "promptBackground", "focusBehavior",
				"promptFontStyle");
		
		setEnumerationValues(new EnumerationValue[] {
				new EnumerationValue("Default", null, "null"),
				new EnumerationValue("Plain", Font.PLAIN, "java.awt.Font.PLAIN"),
				new EnumerationValue("Bold", Font.BOLD, "java.awt.Font.BOLD"),
				new EnumerationValue("Italic", Font.ITALIC, "java.awt.Font.ITALIC"),
				new EnumerationValue("Bold & Italic", Font.BOLD | Font.ITALIC,
						"java.awt.Font.BOLD | java.awt.Font.ITALIC") }, "promptFontStyle");
		setEnumerationValues(new EnumerationValue[] {
				new EnumerationValue("Show", FocusBehavior.SHOW_PROMPT,
						"org.jdesktop.swingx.prompt.PromptSupport.FocusBehavior.SHOW_PROMPT"),
				new EnumerationValue("Hide", FocusBehavior.HIDE_PROMPT,
						"org.jdesktop.swingx.prompt.PromptSupport.FocusBehavior.HIDE_PROMPT"),
				new EnumerationValue("Highlight", FocusBehavior.HIGHLIGHT_PROMPT,
						"org.jdesktop.swingx.prompt.PromptSupport.FocusBehavior.HIGHLIGHT_PROMPT"), }, "focusBehavior");
	}
}
