/**
 * 
 */
package org.jdesktop.test;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
 
/**
 * A test runner for JUnit that ensures that all tests are run on the EDT.
 */
public class EDTRunner extends BlockJUnit4ClassRunner {
    /**
     * Creates a test runner for the specified test class.
     * 
     * @param klass
     *            the class to test
     * @throws InitializationError
     *             if a problem occurs during object construction
     */
    public EDTRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }
 
    /**
     * {@inheritDoc}
     */
    @Override
    public void run(final RunNotifier notifier) {
        if (SwingUtilities.isEventDispatchThread()) {
            super.run(notifier);
        } else {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        EDTRunner.super.run(notifier);
                    }
                });
            } catch (InterruptedException e) {
                notifier.fireTestFailure(new Failure(getDescription(), e));
            } catch (InvocationTargetException e) {
                notifier.fireTestFailure(new Failure(getDescription(), e.getCause()));
            }
        }
    }
}
