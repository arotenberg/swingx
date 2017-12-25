/*
 * $Id: AutoCompleteDocumentTest.java 3475 2009-08-28 08:30:47Z kleopatra $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
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

package org.jdesktop.swingx.autocomplete;

import java.util.Arrays;

import javax.swing.JTextField;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class AutoCompleteDocumentTest extends TestCase {

    @Override
    @Before
       public void setUp() {
    }

    @Override
    @After
       public void tearDown() {
    }

    @Test
    public void testPreferExactMatchOverCurrentlySelected() throws Exception {
        String[] items = new String[]{"exact", "exacter", "exactest"};

        JTextComponent textComponent = new JTextField();
        TextComponentAdaptor adaptor = new TextComponentAdaptor(textComponent, Arrays.asList(items));
        Document document = new AutoCompleteDocument(adaptor, true);
        textComponent.setDocument(document);
        
        textComponent.setText("exacter");
        assertTrue(adaptor.getSelectedItem().equals("exacter"));
        
        document.remove(4, 3);
        assertTrue(adaptor.getSelectedItem().equals("exacter"));
        
        document.insertString(4, "t", null);
        assertTrue(adaptor.getSelectedItem().equals("exact"));
    }
}
