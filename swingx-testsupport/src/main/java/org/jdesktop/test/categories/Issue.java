package org.jdesktop.test.categories;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A category to use on tests that require demonstrate an issue. These tests are expected to fail
 * until the code in question is fixed.
 * <p>
 * {@code Issue} is used in preference to {@link org.junit.Ignore}, so that automated builds can
 * occasionally run these tests to determine if a fix addresses a known issue. This is often the
 * case when two seemingly unrelated bugs have the same source issue. Solving the second bug
 * resolves both, so we need to know when that happens.
 * 
 * @author kschaefer
 */
@Retention(RetentionPolicy.SOURCE)
@Target({})
public @interface Issue { }
