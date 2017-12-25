/*
 * Created on 23.02.2007
 *
 */
package org.jdesktop.swingx;

import java.util.Locale;

import org.jdesktop.swingx.plaf.UIManagerExt;
import org.jdesktop.swingx.resources.swingx;

/**
 * Standalone example for problems with application resourceBundle. <p>
 * 
 * Run in jdk5: the found resource value is always in the system's default
 * locale. <p>
 * 
 * Run in jdk6: the found resource value is correctly localized.
 * 
 * @author Karl Schaefer
 */
public class UIDefaultsResourceBundleCheck {
    /**
     * @param args
     *                unused
     */
    public static void main(String[] args) {
        UIManagerExt.addResourceBundle(
                swingx.class.getName());
        System.out.println(UIManagerExt.getString("LoginPane.1"));
        System.out.println(UIManagerExt.getString("LoginPane.1", Locale.US));
        System.out.println(UIManagerExt.getString("LoginPane.1", Locale.GERMAN));
        System.out.println(UIManagerExt.getString("LoginPane.1", Locale.GERMANY));
        System.out.println(UIManagerExt.getString("LoginPane.1", Locale.FRENCH));
        System.out.println(UIManagerExt.getString("LoginPane.1", Locale.FRANCE));
        System.out.println(UIManagerExt.getString("LoginPane.1", Locale.CANADA_FRENCH));
        System.out.println(UIManagerExt.getString("LoginPane.1", new Locale("pt")));
        System.out.println(UIManagerExt.getString("LoginPane.1", new Locale("pt", "BR")));
        System.out.println(UIManagerExt.getString("LoginPane.1", Locale.KOREAN));
    }
}
