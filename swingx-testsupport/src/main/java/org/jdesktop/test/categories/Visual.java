package org.jdesktop.test.categories;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A category to use on tests that require visual display. This does not mean that the tests require
 * to be seen, but that {@link java.awt.GraphicsEnvironment#isHeadless()} must return {@code true}.
 * 
 * @author kschaefer
 */
@Retention(RetentionPolicy.SOURCE)
@Target({})
public @interface Visual { }
