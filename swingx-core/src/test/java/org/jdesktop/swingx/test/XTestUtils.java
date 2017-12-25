/*
 * $Id: XTestUtils.java 4302 2013-07-05 08:46:39Z Kleopatra $
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
 *
 */
package org.jdesktop.swingx.test;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.calendar.CalendarUtils;

/**
 * Static convenience methods for testing. Note that the resources
 * are available in the test src hierarchy. 
 * 
 * @author Jeanette Winzenburg
 */
public class XTestUtils {
    private static final Logger LOG = Logger.getLogger(XTestUtils.class
            .getName());
    
    private static String IMAGE_DIR = "resources/images/";
    private static Class<?> BASE = JXTable.class; 
    private static String IMAGE_NAME = "kleopatra.jpg";
    
    public static String WRAPPABLE_TEXT = 
        "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has "
        + "been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of "
        + "type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the "
        + "leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the"
        + " release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing "
        + "software like Aldus PageMaker including versions of Lorem Ipsum.";
          
            
    /**
     * 
     * @return the default icon for the swingx testing context.
     */
    public static Icon loadDefaultIcon() {
        return loadDefaultIcon(IMAGE_NAME);
    }


    /**
     * 
     * @param name the name relative to the default image package.
     * @return
     */
    public static Icon loadDefaultIcon(String name) {
        URL url = BASE.getResource(IMAGE_DIR + name);
        return new ImageIcon(url);
    }
    
    
    public static BufferedImage loadDefaultImage() {
        return loadDefaultImage(IMAGE_NAME);
    }


    public static BufferedImage loadDefaultImage(String imageName) {
        try {
            return ImageIO.read(BASE.getResource(IMAGE_DIR + imageName));
        } catch (IOException e) {
            LOG.warning("no reason this should happen .... we are ");
        }
        return null;
    }
    
    /**
     * 
     * @param days offset from today
     * @return the start of the day offset from today by the given value, 
     *  of the calendar default instance.
     */
    public static Date getStartOfToday(int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, days);
        return getStartOfDay(cal);
    }
    /**
     * 
     * @return the start of today of the calendar default instance.
     */
    public static Date getStartOfToday() {
        return getStartOfDay(Calendar.getInstance());
    }
    
    /**
     * Adjusts the given calendar to the start of the 
     * current day and returns its date. 
     * <p>
     * Note that the calendar is 
     * changed! This is a convenience wrapper around CalendarUtils.startOfDay, 
     * might be moved there?
     * 
     * @param cal the calendar to clean
     * @return the calendar's date with all time elements set to 0
     */
    public static Date getStartOfDay(Calendar cal) {
        CalendarUtils.startOfDay(cal);
        return cal.getTime();
    }



}
