package org.jdesktop.beans;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import org.kohsuke.MetaInfServices;

/**
 * An annotation processor that creates or updates a manifest with Java-Bean information.
 * 
 * @author kschaefer
 */
@SuppressWarnings("nls")
@MetaInfServices(Processor.class)
public class JavaBeanProcessor extends AbstractProcessor {
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(JavaBean.class.getName());
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return false;
        }

        Set<String> beans = new TreeSet<String>();

        //JavaBean is a type only annotation, so cast to TypeElement is safe
        for (TypeElement type : (Set<TypeElement>) roundEnv.getElementsAnnotatedWith(JavaBean.class)) {
            beans.add(type.getQualifiedName().toString());
        }
        
        // remove any existing values; we append to the file
        Filer filer = processingEnv.getFiler();
        
        FileObject manifest = null;
        
        try {
            processingEnv.getMessager().printMessage(Kind.NOTE, "Attempting to open manifest...");
            
            manifest = filer.getResource(StandardLocation.SOURCE_PATH, "", "META-INF/MANIFEST.MF");
            
            if (manifest != null) {
                processingEnv.getMessager().printMessage(Kind.NOTE, "Succeeded: " + manifest.getName());
                
                BufferedReader r = null;
                
                try {
                    processingEnv.getMessager().printMessage(Kind.NOTE, "Attempting to find previously defined Java beans");
                    
                    r = new BufferedReader(new InputStreamReader(manifest.openInputStream(), "UTF-8"));
                    
                    String possibleBean = null;
                    
                    for (String line = r.readLine(); line != null; line = r.readLine()) {
                        if (possibleBean == null) {
                            if (line.startsWith("Name: ") && line.endsWith(".class")) {
                                possibleBean = line.substring("Name: ".length(), line.length() - ".class".length()).replace('/', '.');
                                
                                try {
                                    Class.forName(possibleBean);
                                } catch (ClassNotFoundException notABean) {
                                    possibleBean = null;
                                }
                            }
                        } else {
                            if (line.equals("Java-Bean: True")) {
                                processingEnv.getMessager().printMessage(Kind.NOTE,  possibleBean + " already defined");
                                beans.remove(possibleBean);
                            }
                            
                            possibleBean = null;
                        }
                    }
                    
                    r.close();
                    
                } catch (FileNotFoundException ignore) {
                    processingEnv.getMessager().printMessage(Kind.NOTE, "Manifest not found");
                } catch (IOException e) {
                    throw new RuntimeException("Failed to read current Java-Bean information", e);
                } finally {
                    if (r != null) {
                        try {
                            r.close();
                        } catch (IOException ignore) { }
                    }
                }
            }
        } catch (FileNotFoundException ignore) {
            // no file to process
            processingEnv.getMessager().printMessage(Kind.NOTE, "Manifest does not exist...");
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Kind.ERROR, "Failed to load existing manifest for Java-Bean processing:\n" + e);
            
            return false;
        }
        
        try {
            processingEnv.getMessager().printMessage(Kind.NOTE, "Attempting to create output manifest...");
            
            manifest = filer.createResource(StandardLocation.SOURCE_OUTPUT, "", "META-INF/MANIFEST.MF");
            
            processingEnv.getMessager().printMessage(Kind.NOTE, "Succeeded: " + manifest.getName());
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Kind.ERROR, "Cannot create manifest for Java-Bean processing:\n" + e);
            
            return false;
        }

        processingEnv.getMessager().printMessage(Kind.NOTE, "Appending Java-Beans to MANIFEST.MF");
        processingEnv.getMessager().printMessage(Kind.NOTE, beans.toString());
        
        PrintWriter pw = null;
        
        try {
            pw = new PrintWriter(new OutputStreamWriter(manifest.openOutputStream(), "UTF-8"));
            
            pw.println();
            
            for (String value : beans) {
                pw.println("Name: " + value + ".class");
                pw.println("Java-Bean: True");
                pw.println();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write Java-Bean information", e);
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
        
        return false;
    }
}
