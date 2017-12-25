/*
 * Created on 16.11.2009
 *
 */
package org.jdesktop.swingxset.util;


import java.awt.GraphicsConfiguration;
import java.awt.Shape;
import java.awt.Window;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reflective wrapper around Window shape and opacity support in sun jdk 1.6u10++.
 * All methods delegate to the corresponding methods in AWTUtilities if available.
 * They fail silently and return reasonable defaults if the jdk doesn't support.
 * 
 * Taken from:
 * http://java.sun.com/developer/technicalArticles/GUI/translucent_shaped_windows/
 * 
 * @author Anthony Petrov
 * @author Jeanette Winzenburg (adaption for SwingX Demos)
 */
public class AWTUtilitiesWrapper {

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(AWTUtilitiesWrapper.class.getName());
            
    public static Object PERPIXEL_TRANSPARENT,  TRANSLUCENT,  PERPIXEL_TRANSLUCENT;
    
    private static Class<?> awtUtilitiesClass;
    private static Class<?> translucencyClass;
    private static Method translucencySupported;
    private static Method  translucencyCapable;
    private static Method setWindowShape;
    private static Method setWindowOpacity;
    private static Method setWindowOpaque;
    private static Method getWindowOpacity;
    private static Method isWindowOpaque;

    private static Method getWindowShape;

    static void init() {
        try {
            awtUtilitiesClass = Class.forName("com.sun.awt.AWTUtilities");
            translucencyClass = Class.forName("com.sun.awt.AWTUtilities$Translucency");
            if (translucencyClass.isEnum()) {
                Object[] kinds = translucencyClass.getEnumConstants();
                if (kinds != null) {
                    PERPIXEL_TRANSPARENT = kinds[0];
                    TRANSLUCENT = kinds[1];
                    PERPIXEL_TRANSLUCENT = kinds[2];
                }
            }
            translucencySupported = awtUtilitiesClass.getMethod("isTranslucencySupported", translucencyClass);
            translucencyCapable = awtUtilitiesClass.getMethod("isTranslucencyCapable", GraphicsConfiguration.class);
            setWindowShape = awtUtilitiesClass.getMethod("setWindowShape", Window.class, Shape.class);
            setWindowOpacity = awtUtilitiesClass.getMethod("setWindowOpacity", Window.class, float.class);
            setWindowOpaque = awtUtilitiesClass.getMethod("setWindowOpaque", Window.class, boolean.class);
            getWindowOpacity = awtUtilitiesClass.getMethod("getWindowOpacity", Window.class);
            isWindowOpaque = awtUtilitiesClass.getMethod("isWindowOpaque", Window.class);
            getWindowShape = awtUtilitiesClass.getMethod("getWindowShape", Window.class);
        } catch (NoSuchMethodException ex) {
            LOG.log(Level.FINE, "Problem initializing AWTUtilitiesWrapper", ex);
        } catch (SecurityException ex) {
            LOG.log(Level.FINE, "Problem initializing AWTUtilitiesWrapper", ex);
        } catch (ClassNotFoundException ex) {
            LOG.log(Level.FINE, "Problem initializing AWTUtilitiesWrapper", ex);
        }
    }

    static {
        init();
    }
    
    public static boolean isTranslucencySupported(Object kind) {
        if (translucencyClass == null) {
            return false;
        }
        return getBoolean(translucencySupported, kind, false);
    }
    
    public static boolean isTranslucencyCapable(GraphicsConfiguration gc) {
        return getBoolean(translucencyCapable, gc, false);
    }
    
    public static float getWindowOpacity(Window window) {
        if (hasMethod(getWindowOpacity)) {
            try {
                return (Float) getWindowOpacity.invoke(null, window);
            } catch (Exception e) {
                LOG.log(Level.FINE, "cant access windowOpacity", e);
            } 
        }
        return 1.0f;
    }
    
    public static void setWindowOpacity(Window window, float opacity) {
        set(setWindowOpacity, window, Float.valueOf(opacity));
    }
    

    
    public static void setWindowOpaque(Window window, boolean opaque) {
        set(setWindowOpaque, window, Boolean.valueOf(opaque));
    }
    
    public static boolean isWindowOpaque(Window window) {
        return getBoolean(isWindowOpaque, window, true);
    }
    
    public static void setWindowShape(Window window, Shape shape) {
        set(setWindowShape, window, shape);
    }
    
    public static Shape getWindowShape(Window window) {
        if (hasMethod(getWindowShape)) {
            try {
                return (Shape) getWindowShape.invoke(null, window);
            } catch (Exception e) {
                LOG.log(Level.FINE, "cant access windowOpacity", e);
            } 
        }
        return window.getBounds();
    }
    
    private static void set(Method method, Window window, Object value) {
        if (!hasMethod(method)) {
            return;
        }
        try {
            method.invoke(null, window, value);
        } catch (Exception ex) {
            LOG.log(Level.FINE, "cant set window property", ex);
        } 
    }

    /**
     * @param method
     * @return
     */
    private static boolean hasMethod(Method method) {
        return awtUtilitiesClass != null && method != null;
    }
    
    
    
    private static boolean getBoolean(Method method, Object kind,
            boolean valueOnFailure) {
        if (hasMethod(method)) {
            try {
                Object ret = method.invoke(null, kind);
                return ((Boolean) ret).booleanValue();
            } catch (Exception ex) {
                LOG.log(Level.FINE, "cant access window property", ex);
            } 
        }
        return valueOnFailure;
    }

}