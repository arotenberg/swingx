/*
 * $Id: HighlighterInfo.java 4097 2011-11-30 19:22:13Z kschaefe $
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

import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.util.Contract;

/**
 * A wrapper object that associates a description with a highlighter.
 * 
 * @author Karl George Schaefer
 */
public final class HighlighterInfo {
    private static class EmptyHighlighter extends AbstractHighlighter {
        protected Component doHighlight(Component component, ComponentAdapter adapter) {
            return component;
        }
    }
    
    private static final Highlighter EMPTY = new EmptyHighlighter();
    
    private final String description;
    private final Highlighter highlighter;
    
    public HighlighterInfo(String description, Highlighter highlighter) {
        this.description = Contract.asNotNull(description,
                "description cannot be null");
        this.highlighter = highlighter == null ? EMPTY : highlighter;
    }
    
    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the highlighter
     */
    public Highlighter getHighlighter() {
        return highlighter;
    }
}
