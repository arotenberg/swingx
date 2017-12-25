/*
 * $Id: URLPainter.java 3849 2010-10-21 20:11:28Z kschaefe $
 *
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle,
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


package org.jdesktop.beans.editors;

import java.awt.Graphics2D;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.Painter;

/**
 *
 * @author joshy
 */
public class URLPainter extends CompoundPainter {
    URL url;
    /**
     * Creates a new instance of URLPainter
     */
    public URLPainter() {
        this.url = null;
    }
    
    public URLPainter(URL url) {
        this.url = url;
    }
    
    public URLPainter(File file) {
        try {
            this.url = file.toURI().toURL();
        } catch (MalformedURLException exception) {
            exception.printStackTrace();
            this.url = null;
        }
    }
        
    public URLPainter(String url) {
        try {
            this.url = new URL(url);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            this.url = null;
        }
    }
    
    public URLPainter(Class<?> baseClass, String resource) {
        url = baseClass.getResource(resource);
    }
    
    public void setURL(URL url) {
        URL old = this.url;
        this.url = url;
        firePropertyChange("file", old, this.url);
    }
    
    public URL getURL() {
        return this.url;
    }
    
    private boolean loaded = false;
    
    private void load() {
        try {
            Painter painter = PainterUtil.loadPainter(url);
            this.setPainters(painter);
            loaded = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void doPaint(Graphics2D g, Object component, int width, int height) {
        if(!loaded) {
            load();
        }
        super.doPaint(g, component, width, height);
    }
    
    
}
