/*
 * $Id: GraphicsUtilitiesVisualCheck.java 4082 2011-11-15 18:39:43Z kschaefe $
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
package org.jdesktop.swingx.graphics;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.jdesktop.swingx.JXImageView;
import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.util.GraphicsUtilities;

/**
 * GraphicsUtilities visual checks.
 * 
 * @author rah003
 */
public class GraphicsUtilitiesVisualCheck extends InteractiveTestCase {

    public static void main(String[] args) {
        GraphicsUtilitiesVisualCheck test = new GraphicsUtilitiesVisualCheck();
        try {
            test.runInteractiveTests();
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }

    /**
     * Issue #524-swingx: Thumbnails not generated correctly.
     * 
     */
    public void interactiveMistargetedKeyStrokes() throws Exception {
        BufferedImage im = ImageIO.read(getClass().getResourceAsStream(
                "/org/jdesktop/swingx/resources/images/500by500.png"));
        System.out.println("size:" + im.getWidth() + ", " + im.getHeight());
        BufferedImage im2 = GraphicsUtilities.createThumbnail(im, 100);
        JXImageView ipa = new JXImageView();
        ipa.setImage(im2);
        showInFrame(ipa, "default - for debugging only");
    }

    /**
     * do nothing test - keep the test runner happy.
     */
    public void testDummy() {
    }

}
