/*
 * Created on 20.06.2005
 *
 */
package org.jdesktop.swingx.util;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Comparator;

/**
 * A custom comparator implementation for Point and Dimension.
 * 
 * @author Jeanette Winzenburg
 */
public class XYComparator implements Comparator {

    public int compare(Object o1, Object o2) {
        if ((o1 instanceof Point) && (o2 instanceof Point)) {
            return comparePoint((Point) o1, (Point) o2);
        } 
        if ((o1 instanceof Dimension) && (o2 instanceof Dimension)) {
            return compareDimension((Dimension) o1, (Dimension) o2);
        }
        return 0; // can't decide
    }

    private int compareDimension(Dimension dim1, Dimension dim2) {
        if (dim1.width == dim2.width) {
            if (dim1.height == dim2.height) return 0;
            return dim1.height < dim2.height ? -1 : 1;
        }
        return dim1.width < dim2.width ? -1 : 1;
    }
    

    private int comparePoint(Point p1, Point p2) {
        if (p1.x == p2.x) {
            if (p1.y == p2.y) return 0;
            return p1.y < p2.y ? -1 : 1;
        }
        return p1.x < p2.x ? -1 : 1;
    }

}
