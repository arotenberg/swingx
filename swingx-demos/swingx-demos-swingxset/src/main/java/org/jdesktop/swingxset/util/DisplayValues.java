/*
 * $Id: DisplayValues.java 4097 2011-11-30 19:22:13Z kschaefe $
 *
 * Copyright 2009 Sun Microsystems, Inc., 4150 Network Circle,
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
 *
 */
package org.jdesktop.swingxset.util;

import org.jdesktop.swingx.binding.DisplayInfo;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.renderer.StringValues;
/**
 * Convenience display converters used in Demos.
 * 
 * @author Jeanette Winzenburg
 */
public class DisplayValues {

    public static final StringValue TITLE_WORDS_UNDERSCORE = new TitleWords("_");
    
    public static final StringValue DISPLAY_INFO_DESCRIPTION = new StringValue() {

        @Override
        public String getString(Object value) {
            if (value instanceof DisplayInfo<?>) {
                return ((DisplayInfo<?>) value).getDescription();
            }
            
            return StringValues.EMPTY.getString(value);
        }
        
    };
    
  
    public static class TitleWords implements StringValue {

        private String breakRegex;
        
        public TitleWords() {
            this(null);
        }
        
        public TitleWords(String breakRegex) {
            this.breakRegex = breakRegex != null ? breakRegex : "\\s"; 
        }
        
        @Override
        public String getString(Object value) {
            String s = StringValues.TO_STRING.getString(value).toLowerCase();
            
            if (s.length() > 0) {
                // PENDING JW: pretty sure there's a more elegant way ;-)
                String[] words = s.split(breakRegex);
                s = "";
                for (int i = 0; i < words.length; i++) {
                    words[i] = words[i].substring(0, 1).toUpperCase() + words[i].substring(1);
                    if (i != 0) {
                        words[i] = " " + words[i];
                    }
                    s += words[i]; 
                }
            }
            
            return s;
        }
        
    }
}
