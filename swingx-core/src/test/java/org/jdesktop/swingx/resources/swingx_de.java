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
public class swingx_de extends ListResourceBundle {
    /**
     * {@inheritDoc}
     */
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                new String[]{"LoginPane.1", "de One"},
                new String[]{"LoginPane.2", "de Two"},
                new String[]{"LoginPane.3", "de Three"},
        };
    }
}