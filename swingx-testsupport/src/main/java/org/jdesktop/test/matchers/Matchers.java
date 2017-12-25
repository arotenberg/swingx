package org.jdesktop.test.matchers;

import static org.hamcrest.CoreMatchers.any;

import java.beans.PropertyChangeEvent;

import org.hamcrest.Matcher;

/**
 * A collection of matchers useful for testing.
 * 
 * @author kschaefer
 */
public final class Matchers {
    /**
     * A matcher for any property change.
     * 
     * @return {@code true} if any property can be matched; {@code false} otherwise
     */
    public static Matcher<PropertyChangeEvent> anyProperty() {
        return any(PropertyChangeEvent.class);
    }

    /**
     * A matcher for a specified property.
     * 
     * @param propertyName
     *            the name of the property to match
     * @return {@code true} if the property exists and has a name that matches the specified name;
     *         {@code false} otherwise
     */
    public static Matcher<PropertyChangeEvent> propertyWithName(String propertyName) {
        return new PropertyChangeEventMatcher(propertyName, null, null);
    }

    /**
     * A matcher for a specified property.
     * 
     * @param propertyName
     *            the name of the property to match
     * @param oldValue
     *            the old property value
     * @param newValue
     *            the new property value
     * @return {@code true} if the property exists and all parameters match the specified values;
     *         {@code false} otherwise
     */
    public static Matcher<PropertyChangeEvent> property(String propertyName, Object oldValue, Object newValue) {
        return new PropertyChangeEventMatcher(propertyName, oldValue, newValue);
    }

    /**
     * This matcher returns {@code true} when the arguments are equivalent.
     * <p>
     * For purposes of this matcher equivalence is as follows:
     * <ol>
     * <li>the objects are equal</li>
     * <li>the objects contain the same contents, insofar as the {@link java.beans.BeanInfo class
     * info} for each object instance returns equivalent values</li>
     * </ol>
     * 
     * @param <T>
     *            the type of the matcher
     * @param object
     *            the object to test
     * @return {@code true} if the object is equivalent; {@code false} otherwise
     */
    public static <T> Matcher<T> equivalentTo(T object) {
        return new EquivalentMatcher<T>(object);
    }
}
