package org.jdesktop.swingx.renderer;

import java.util.Locale;
import java.util.Map;

import org.jdesktop.swingx.plaf.UIManagerExt;
import org.jdesktop.swingx.util.Contract;

/**
 * A StringValue which looks up localized String representations for objects.
 */
public class LocalizableStringValue implements StringValue {

    private Map<Object, String> lookup;

    private Locale locale;

    private String prefix;

    /**
     * Instantiates a LocaleStringValue which looks up localized String
     * representations for items in the map using the JComponent defaultLocale.
     * 
     * @param lookup a map containing Entries of objects and a string key to
     *        look up its string representation in the UIManager
     */
    public LocalizableStringValue(Map<Object, String> lookup) {
        this(lookup, null, null);
    }

    /**
     * Instantiates a LocaleStringValue which looks up localized String
     * representations for items in the map using the given Locale.
     * 
     * @param lookup a map containing Entries of objects and a string key to
     *        look up its string representation in the UIManager
     * @param locale the locale to lookup the localized strings, may be null to
     *        denote using JComponent.defaultLocale
     */
    public LocalizableStringValue(Map<Object, String> lookup, Locale locale) {
        this(lookup, null, locale);
    }

    /**
     * Instantiates a LocaleStringValue which looks up localized String
     * representations for items in the map using the JComponent defaultLocale.
     * 
     * @param lookup a map containing Entries of objects and a string key to
     *        look up its string representation in the UIManager
     * @param prefix a common prefix for all string keys in the map, may be null
     *    to denote that the keys should be use as are 
     */
    public LocalizableStringValue(Map<Object, String> lookup, String prefix) {
        this(lookup, prefix, null);
    }

    /**
     * Instantiates a LocaleStringValue which looks up localized String
     * representations for items in the map using the given Locale.
     * 
     * @param lookup a map containing Entries of objects and a string key to
     *        look up its string representation in the UIManager
     * @param prefix a common prefix for all string keys in the map, may be null
     *    to denote that the keys should be use as are 
     * @param locale the locale to lookup the localized strings, may be null to
     *        denote using JComponent.defaultLocale
     */
    public LocalizableStringValue(Map<Object, String> lookup, String prefix,
            Locale locale) {
        this.lookup = Contract.asNotNull(lookup, "map must not be null");
        this.prefix = prefix;
        setLocale(locale);
    }

    /**
     * 
     * @inherited <p>
     * 
     * Implemented to lookup the value's localized string
     * representation, if contained in the lookup map. Returns
     * the fallback if not contained.
     * 
     */
    @Override
    public String getString(Object value) {
        String key = getKey(value);
        if (key != null) {
            String text = getLocalizedString(key);
            if (text != null)
                return text;
        }
        return getFallbackString(value);
    }

    /**
     * Returns the key for the given value. The prefix is prepended
     * to the key if both are available.
     * 
     * May be null if not available.
     * 
     * @param value the value to find the key for
     * @return the key or null if not available.
     */
    protected String getKey(Object value) {
        String key = lookup.get(value);
        if (key != null) {
            if (prefix != null) {
                key = prefix + key;
            }
        }
        return key;
    }

    /**
     * Returns the fallback string representation. This method is called
     * when a localized value isn't available be found.<p>
     * 
     * This implementation returns toString.
     * 
     * @param value the object to return a string representation for.
     * @return a string representation for the value
     */
    protected String getFallbackString(Object value) {
        return StringValues.TO_STRING_UI.getString(value);
    }

    /**
     * Returns the localized value for the given key or null if not
     * found. <p>
     * 
     * This implementation queries the UIManagerExt.
     * 
     * @param key the key to search a value for
     * @return the localized value for the given key or null if not
     *    available.
     */
    protected String getLocalizedString(String key) {
        return UIManagerExt.getString(key, getLocale());
    }

    // -------------------- implement Localizable

    /**
     * Sets the Locale to use for lookup of localized string representation.
     * 
     * @param locale the locale to lookup the localized strings, may be null to
     *        denote using Locale's default.
     */
    public final void setLocale(Locale locale) {
        this.locale = locale;
    }

    /**
     * Returns the Locale to use for lookup, guaranteed to be not null. If
     * the initial setting had been null, returns current Locale's default.
     * 
     * @return the Locale used for lookup.
     */
    public Locale getLocale() {
        return locale != null ? locale : Locale.getDefault();
    }
}