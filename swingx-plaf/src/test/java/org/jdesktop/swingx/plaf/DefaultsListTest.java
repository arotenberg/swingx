/*
 * $Id: DefaultsListTest.java 4039 2011-07-19 17:57:29Z kschaefe $
 *
 * Copyright 2007 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jdesktop.swingx.plaf;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * This test ensures that all values that should be {@code UIResouce}s are.
 * 
 * @author Karl George Schaefer
 */
@SuppressWarnings("nls")
public class DefaultsListTest {
    /**
     * Ensure that "adding" a key with a {@code null} value removes the value
     * from the list.
     */
    @Test
    public void testRemoval() {
        DefaultsList kvList = new DefaultsList();
        kvList.add("a", "b");
        
        Object[] arr = kvList.toArray();
        assertThat(arr.length, is(2));
        assertThat((String) arr[0], is("a"));
        assertThat((String) arr[1], is("b"));
        
        kvList.add("a", null);
        assertThat(kvList.toArray().length, is(0));
    }
}
