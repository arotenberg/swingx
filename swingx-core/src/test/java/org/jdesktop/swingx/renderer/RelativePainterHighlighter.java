/*
 * $Id$
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
package org.jdesktop.swingx.renderer;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.logging.Logger;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.PainterHighlighter;
import org.jdesktop.swingx.painter.AbstractLayoutPainter;
import org.jdesktop.swingx.painter.AbstractLayoutPainter.HorizontalAlignment;
import org.jdesktop.swingx.painter.Painter;

@SuppressWarnings("unchecked")
public class RelativePainterHighlighter extends PainterHighlighter {
    // the fixed value to compare against (should be Comparable)
    private Relativizer relativizer;
    
    public RelativePainterHighlighter() {
        this(null);
    }
    
    public RelativePainterHighlighter(Painter delegate) {
        super(delegate);
    }

    public void setHorizontalAlignment(HorizontalAlignment align) {
        getPainter().setHorizontalAlignment(align);
        fireStateChanged();
    }
    
    public HorizontalAlignment getHorizontalAlignment() {
        return getPainter().getHorizontalAlignment();
    }
    

    /**
     * @param maxValue the maxValue to set
     */
    public void setRelativizer(Relativizer relativizer) {
        this.relativizer = relativizer;
        fireStateChanged();
    }

    public Relativizer getRelativizer() {
        return relativizer;
    }
    
    @Override
    protected Component doHighlight(Component component,
            ComponentAdapter adapter) {
        float xPercent = relativizer.getRelativeValue(adapter);
        getPainter().setXFactor(xPercent);
        getPainter().setVisible(xPercent != Relativizer.ZERO);
        return super.doHighlight(component, adapter);
    }
    /**
     * Overridden to wrap a RelativePainter around the given, if not
     * already is of type RelativePainter.
     */
    @Override
    public void setPainter(Painter painter) {
        if (!(painter instanceof RelativePainter)) {
            painter = new RelativePainter(painter); 
        }
        super.setPainter(painter);
    }

    @Override
    public RelativePainter getPainter() {
        return (RelativePainter) super.getPainter();
    }

    @Override
    protected boolean canHighlight(Component component,
            ComponentAdapter adapter) {
        return relativizer != null && super.canHighlight(component, adapter);
    }
    
//------------------- Relativizer
    
    public static interface Relativizer {
        public static final float ZERO = 0.0f;
        public static final float ONE = 1.0f;
        /**
         * Returns a float in the range of 0.0f to 1.0f inclusive which
         * indicates the relative value of the given adapter's value.
         * 
         * @param adapter
         * @return
         */
        public float getRelativeValue(ComponentAdapter adapter);

    }

    

    public static class NumberRelativizer implements Relativizer {

        private Number max;
        private Number current;
        private int valueColumn;
        private boolean spread;
        
        public NumberRelativizer(Number max) {
            this(max, max);
        }

        public NumberRelativizer(Number max, Number current) {
            this(0, max, current);
        }
        
        /**
         * @param i
         * @param max
         * @param current
         */
        public NumberRelativizer(int column, Number max, Number current) {
            this(column, false, max, current);
        }

        public NumberRelativizer(int column, boolean spreadColumns, Number max, Number current) {
            this.current = current;
            this.max = max;
            this.valueColumn = column;
            this.spread = spreadColumns;
        }
        
        @Override
        public float getRelativeValue(ComponentAdapter adapter) {
            if (getNumber(adapter) == null) {
                return ZERO;
            }
            float value = getNumber(adapter).floatValue();
            float limit = Math.min(getCurrent().floatValue(), value);
            if (isZero(limit)) {
                return ZERO;
            }
            float percent = limit / getMax().floatValue();
            
            if (!spread) {
                return percent;
            }
            
            int width = adapter.getComponent().getWidth();
            int pixelLocation = (int) (percent * width);
            int visualColumn = getColumnAt(adapter, pixelLocation);
            if (adapter.column < visualColumn) {
                return ONE;
            } else if (adapter.column > visualColumn) {
                return ZERO;
            }
            
            int visualColumnWidth = getColumnWidth(adapter, visualColumn);
            int startColumn = getColumnLocation(adapter, visualColumn);
            int valueWidth = pixelLocation - startColumn; 
            return (float) valueWidth / (float) visualColumnWidth;
        }

        /**
         * @param adapter
         * @return
         */
        protected Number getNumber(ComponentAdapter adapter) {
            if (adapter.getValue() instanceof Number)
                return (Number) adapter.getValue(valueColumn);
            return null;
        }

        private int getColumnLocation(ComponentAdapter adapter, int visualColumn) {
            if (!(adapter.getComponent() instanceof JXTable)) {
                return 0;
            }
            JXTable table = (JXTable) adapter.getComponent();
            // PENDING JW: guard against null header
            return table.getTableHeader().getHeaderRect(visualColumn).x;
        }

        private int getColumnWidth(ComponentAdapter adapter, int visualColumn) {
            if (!(adapter.getComponent() instanceof JXTable)) {
                return adapter.getComponent().getWidth();
            }
            JXTable table = (JXTable) adapter.getComponent();
            return table.getColumn(visualColumn).getWidth();
        }

        private int getColumnAt(ComponentAdapter adapter, int pixelLocation) {
            if (!(adapter.getComponent() instanceof JXTable)) {
                return 0;
            }
            JXTable table = (JXTable) adapter.getComponent();
            // PENDING JW: guard against null header
            return table.getTableHeader().columnAtPoint(new Point(pixelLocation, 10));
            
        }

        protected Number getCurrent() {
            return current;
        }
        
        protected Number getMax() {
            return max;
        }
        
        protected boolean isZero(float limit) {
            return Math.abs(limit) < 0.002;
        }

        protected int getValueColumn() {
            return valueColumn;
        }
    }

    
    //--------- hack around missing size proportional painters
    
    public static class RelativePainter<T> extends AbstractLayoutPainter<T> {

        private Painter<? super T> painter;
        private double xFactor;
        private double yFactor;
        private boolean visible;

        public RelativePainter() {
            this(null);
        }
        
        
        public RelativePainter(Painter<? super T> delegate) {
            this.painter = delegate;
        }
        
        public RelativePainter(Painter<? super T> delegate, double xPercent) {
            this(delegate);
            xFactor = xPercent;
        }
        public void setPainter(Painter<? super T> painter) {
            Object old = getPainter();
            this.painter = painter;
            firePropertyChange("painter", old, getPainter());
        }
        
        public Painter<? super T> getPainter() {
            return painter;
        }
        
        public void setXFactor(double xPercent) {
            double old = getXFactor();
            this.xFactor = xPercent;
            firePropertyChange("xFactor", old, getXFactor());
        }
        
        /**
         * @return
         */
        public double getXFactor() {
            return xFactor;
        }

        public void setYFactor(double yPercent) {
            this.yFactor = yPercent;
        }
        @Override
        protected void doPaint(Graphics2D g, T object, int width, int height) {
            if (painter == null) return;
            // use epsilon
            if (xFactor != 0.0) {
                int oldWidth = width;
                width = (int) (xFactor * width);
                if (getHorizontalAlignment() == HorizontalAlignment.RIGHT) {
                    g.translate(oldWidth - width, 0);
                }
            }
            if (yFactor != 0.0) {
                int oldHeight = height;
                height = (int) (yFactor * height);
                if (getVerticalAlignment() == VerticalAlignment.BOTTOM) {
                    g.translate(0, oldHeight - height);
                }
            }
            
            painter.paint(g, object, width, height);
        }

        /**
         * Overridden to take over completely: super does strange things with
         * dirty which result in property changes fired during painting.
         */
        @Override
        public boolean isVisible() {
            return visible;
        }


        /**
         * Overridden to take over completely: super does strange things with
         * dirty which result in property changes fired during painting.
         */
        @Override
        public void setVisible(boolean visible) {
            if (isVisible() == visible) return;
            this.visible = visible;
            firePropertyChange("visible", !visible, isVisible());
        }
        
    }

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(RelativePainterHighlighter.class.getName());
}