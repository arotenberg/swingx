package org.jdesktop.beans;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Determines if the specified class is a Java Bean. This information is added to the manifest with
 * the annotation processor.
 * 
 * @author kschaefer
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface JavaBean {
    @Documented
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.SOURCE)
    public static @interface Property {
        
    }
    
    boolean value() default true;
}
