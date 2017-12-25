/*
 * $Id: PainterDemoUtils.java 4097 2011-11-30 19:22:13Z kschaefe $
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
package org.jdesktop.swingx.demos.painter;

import java.awt.Insets;

import org.jdesktop.beansbinding.Converter;

/**
 * 
 * 
 * @author Jeanette Winzenburg
 */
public class PainterDemoUtils {

    public static final Converter<?, ?> SLIDER_TO_FLOAT = new SliderConverter();
    public static final Converter<?, ?> SLIDER_TO_INSETS = new InsetsConverter();
    
    private static class InsetsConverter extends Converter<Integer, Insets> {
        @Override
        public Insets convertForward(Integer arg0) {
            int inset = arg0.intValue();
            return new Insets(inset, 2 * inset, inset, 2* inset);
        }
        
        @Override
        public Integer convertReverse(Insets arg0) {
            throw new UnsupportedOperationException();
        }
        
    }

    private static class SliderConverter extends Converter<Integer, Float> {

        @Override
        public Float convertForward(Integer value) {
            return Float.valueOf(value.floatValue());
        }

        @Override
        public Integer convertReverse(Float value) {
            return Integer.valueOf(value.intValue());
        }
        
    }
    
    private PainterDemoUtils() {
    }
}
