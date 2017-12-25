package org.jdesktop.test.matchers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.jdesktop.test.SerializableSupport.serialize;
import static org.jdesktop.test.matchers.Matchers.equivalentTo;
import static org.junit.Assert.assertThat;

import javax.swing.JButton;

import org.jdesktop.test.EDTRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EDTRunner.class)
public class EquivalentMatcherTest {
    @Test
    public void ensureEqualIsEquivalent() {
        assertThat(1, is(equalTo(1)));
        assertThat(1, is(equivalentTo(1)));
    }
    
    @Test
    public void testUnequalBaseObjects() {
        JButton button1 = new JButton();
        JButton button2 = new JButton();
        
        assertThat(button1, is(not(equalTo(button2))));
        assertThat(button1, is(equivalentTo(button2)));
    }
    
    @Test
    public void testSerializedObjects() {
        JButton button1 = new JButton();
        JButton button2 = serialize(button1);
        
        assertThat(button1, is(not(equalTo(button2))));
        assertThat(button1, is(equivalentTo(button2)));
    }
}
