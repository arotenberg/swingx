package org.jdesktop.swingx;

import org.jdesktop.beans.EnumerationValue;
import org.jdesktop.swingx.JXSearchField.LayoutStyle;
import org.jdesktop.swingx.JXSearchField.SearchMode;

public class JXSearchFieldBeanInfo extends JXTextFieldBeanInfo {
	public JXSearchFieldBeanInfo() {
		super(JXSearchField.class);
	}

	@Override
    protected void initialize() {
		super.initialize();
		setPreferred(true, "layoutStyle", "searchMode", "instantSearchDelay", "findPopupMenu", "useNativeSearchFieldIfPossible", "recentSearchesSaveKey");

		setEnumerationValues(
				new EnumerationValue[] {
						new EnumerationValue("Mac", LayoutStyle.MAC,
								"org.jdesktop.swingx.JXSearchField.LayoutStyle.MAC"),
						new EnumerationValue("Vista", LayoutStyle.VISTA,
								"org.jdesktop.swingx.JXSearchField.LayoutStyle.VISTA") },
				"layoutStyle");
		setEnumerationValues(
				new EnumerationValue[] {
						new EnumerationValue("Instant", SearchMode.INSTANT,
								"org.jdesktop.swingx.JXSearchField.SearchMode.INSTANT"),
						new EnumerationValue("Regular", SearchMode.REGULAR,
								"org.jdesktop.swingx.JXSearchField.SearchMode.REGULAR") },
				"searchMode");
	}
}
