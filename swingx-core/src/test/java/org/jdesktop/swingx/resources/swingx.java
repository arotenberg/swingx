/**
 * 
 */
package org.jdesktop.swingx.resources;

import java.util.ListResourceBundle;

/**
 * Part of the {@code UIDefaultsResourceBundleTest}.
 * 
 * @author Karl George Schaefer
 */
public class swingx extends ListResourceBundle {
    /**
     * {@inheritDoc}
     */
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
            new String[]{"LoginPane.1", "One"},
            new String[]{"LoginPane.2", "Two"},
            new String[]{"LoginPane.3", "Three"},
        };
    }
}
