/*
 * Created on 14.12.2009
 *
 */
package org.jdesktop.swingx.demos.tree;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.jdesktop.swingx.icon.PainterIcon;
import org.jdesktop.swingx.painter.ImagePainter;
import org.jdesktop.swingx.renderer.IconValue;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.util.GraphicsUtilities;

import com.jhlabs.image.InvertFilter;

/**
 * Custom icon values used in TreeDemo.
 */
public class TreeDemoIconValues {
    
    /**
     * An IconValue which maps cell value to an icon. The value is mapped
     * to a filename using a StringValue. The icons are loaded lazyly.
     */
    public static class LazyLoadingIconValue implements IconValue {
        private Class<?> baseClass;
        private StringValue keyToFileName;
        private Map<Object, Icon> iconCache;
        private Icon fallbackIcon;
        
        public LazyLoadingIconValue(Class<?> baseClass, StringValue sv, String fallbackName) {
           this.baseClass = baseClass;
           iconCache = new HashMap<Object, Icon>(); 
           this.keyToFileName = sv;
           fallbackIcon = loadFromResource(fallbackName);
        }
        
        // <snip> JXTree rendering
        // IconValue based on node value
        /**
         * {@inheritDoc} <p>
         * 
         * Implemented to return a Icon appropriate for the given node value. The icon is
         * loaded (and later cached) as a resource, using a lookup key created by a StringValue. 
         * 
         */
        @Override
        public Icon getIcon(Object value) {
            String key = keyToFileName.getString(value);
            Icon icon = iconCache.get(key);
            if (icon ==  null) {
                icon = loadIcon(key);
             }
            if (icon == null) {
                icon = fallbackIcon;
            }
            return icon;
        }
// </snip>
        
        private Icon loadIcon(String key) {
            Icon icon = loadFromResource(key);
            if (icon != null) {
                iconCache.put(key, icon);
            }
            return icon;
        }
        

        protected Icon loadFromResource(String name) {
            URL url = baseClass.getResource("resources/images/" + name );
            if (url == null) return null;
            try {
                BufferedImage image = ImageIO.read(url);
                if (image.getHeight() > 30) {
                    image = GraphicsUtilities.createThumbnail(image, 16);
                }
                return new ImageIcon(image);
            } catch (IOException e) {
            }
            return null;
        }

        
    }

    /**
     * A IconValue which delegates icon lookup to another IconValue and returns
     * a manipulated icon.
     */
    public static class FilteredIconValue implements IconValue {

        private IconValue delegate;

        private Map<Object, Icon> iconCache;

        public FilteredIconValue(IconValue delegate) {
            iconCache = new HashMap<Object, Icon>();
            this.delegate = delegate;
        }

        /**
         * {@inheritDoc} <p>
         * 
         * Looks up the default icon in the delegate and 
         * returns a manipulated version.
         */
        @Override
        public Icon getIcon(Object value) {
            Icon icon = delegate.getIcon(value);
            Icon xicon = iconCache.get(icon);
            if (xicon == null) {
                xicon = manipulatedIcon(icon);
                iconCache.put(icon, xicon);
            }
            return xicon;
        }

        // <snip> JXTree rollover
        // wraps the given icon into an ImagePainter with a filter effect
        private Icon manipulatedIcon(Icon icon) {
            PainterIcon painterIcon = new PainterIcon(new Dimension(icon
                    .getIconWidth(), icon.getIconHeight()));
            BufferedImage image = (BufferedImage) ((ImageIcon) icon).getImage();
            ImagePainter delegate = new ImagePainter(image);
            delegate.setFilters(new InvertFilter());
            painterIcon.setPainter(delegate);
            return painterIcon;
        }
        // </snip>

    }
    
}
