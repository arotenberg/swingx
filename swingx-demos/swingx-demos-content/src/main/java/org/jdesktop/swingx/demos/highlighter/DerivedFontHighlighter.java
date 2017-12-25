/*
 * $Id: DerivedFontHighlighter.java 4097 2011-11-30 19:22:13Z kschaefe $
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
 */
package org.jdesktop.swingx.demos.highlighter;

import java.awt.Component;
import java.awt.Font;

import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;

/**
 * @author Karl George Schaefer
 *
 */
public class DerivedFontHighlighter extends AbstractHighlighter {
    public static final int NON_DERIVED_STYLE = -1;
    public static final float NON_DERIVED_SIZE = -1f;
    
    private int style;
    private float size;
    
    public DerivedFontHighlighter() {
        this((HighlightPredicate) null);
    }
    
    public DerivedFontHighlighter(int style) {
        this(null, style);
    }
    
    public DerivedFontHighlighter(float size) {
        this(null, size);
    }
    
    public DerivedFontHighlighter(HighlightPredicate predicate) {
        this(predicate, -1);
    }
    
    public DerivedFontHighlighter(HighlightPredicate predicate, int style) {
        this(predicate, style, -1f);
    }
    
    public DerivedFontHighlighter(HighlightPredicate predicate, float size) {
        this(predicate, -1, size);
    }
    
    public DerivedFontHighlighter(HighlightPredicate predicate, int style, float size) {
        super(predicate);
        
        this.style = style;
        this.size = size;
    }
    
    private int getStyle(Font f) {
        if (style == -1) {
            return f.getStyle();
        }
        
        return style;
    }
    
    private float getSize(Font f) {
        if (size < 0) {
            return f.getSize2D();
        }
        
        return size;
    }
    
    public int getStyle() {
        return style;
    }
    
    public void setStyle(int style) {
        this.style = (style & ~0x03) == 0 ? style : -1;
        
        fireStateChanged();
    }
    
    public float getSize() {
        return size;
    }
    
    public void setSize(float size) {
        this.size = size;
        
        fireStateChanged();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected Component doHighlight(Component component, ComponentAdapter adapter) {
        Font current = component.getFont();
        Font f = component.getFont().deriveFont(getStyle(current), getSize(current));
        component.setFont(f);
        
        return component;
    }
}
