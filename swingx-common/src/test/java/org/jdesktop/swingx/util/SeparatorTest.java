package org.jdesktop.swingx.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("nls")
public class SeparatorTest {
    private Separator<String> sep;
    private List<String> values;
    
    @Before
    public void setUp() {
        sep = new Separator<String>("", ",");
        values = Arrays.asList("1", "2", "3");
    }
    
    @Test
    public void testOneElement() {
        StringBuilder sb = new StringBuilder();
        
        for (String s : values.subList(0, 1)) {
            sb.append(sep.get()).append(s);
        }
        
        assertThat(sb.toString(), is("1"));
    }
    
    @Test
    public void testTwoElements() {
        StringBuilder sb = new StringBuilder();
        
        for (String s : values.subList(0, 2)) {
            sb.append(sep.get()).append(s);
        }
        
        assertThat(sb.toString(), is("1,2"));
    }
    
    @Test
    public void testThreeElements() {
        StringBuilder sb = new StringBuilder();
        
        for (String s : values) {
            sb.append(sep.get()).append(s);
        }
        
        assertThat(sb.toString(), is("1,2,3"));
    }
}
