package org.jdesktop.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import javax.swing.SwingUtilities;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EDTRunner.class)
public class EDTRunnerTest {
    /**
     * Ensure that the EDTRunner is using the EventDispatchThread.
     */
    @Test
    public void testForSwingThread() {
        assertThat(SwingUtilities.isEventDispatchThread(), is(true));
    }
}
