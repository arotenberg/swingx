/*
 * $Id: DemoCreator.java 4097 2011-11-30 19:22:13Z kschaefe $
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
package org.jdesktop.swingxset;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.swingset3.Demo;


/**
 * Reads demo classnames and creates demos. Extracted methods from Swing/X/Set3
 * 
 * @author Jeanette Winzenburg
 */
public class DemoCreator {

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(DemoCreator.class
            .getName());
    private static final DemoCreator INSTANCE = new DemoCreator();
    
    public List<Demo> createDemos(String[] args, String metaSource) {
        List<String> demoNames = getDemoClassNames(args, metaSource);
        return createDemoList(demoNames);
    }
    
    private List<String> readDemoClassNames(Reader reader) throws IOException {
        List<String> demoClassNames = new ArrayList<String>();
        
        BufferedReader breader = new BufferedReader(reader);
        String line;
        while((line = breader.readLine()) != null) {
            if (isClassName(line))
                demoClassNames.add(line);
        }
        breader.close();
        return demoClassNames;
    }

    /**
     * @param line
     * @return
     */
    private boolean isClassName(String line) {
        if ((line == null) 
                || line.startsWith("#")
                || line.trim().length() == 0 
                ) return false;
        return true;
    }

    private List<String>getDemoClassNames(String args[], String metaSource) {
        List<String> demoList = new ArrayList<String>();
        boolean augment = false;
        Exception exception = null; 

        // First look for any demo list files specified on the command-line
        for(String arg : args) {
            if (arg.equals("-a") || arg.equals("-augment")) {
                augment = true;
            } else {
                // process argument as filename containing names of demo classes
                try {
                    demoList.addAll(readDemoClassNames(new FileReader(arg) /*filename*/));
                } catch (IOException ex) {
                    exception = ex;
                    LOG.log(Level.WARNING, "unable to read demo class names from file: "+arg, ex);
                }
            }
        }

        if (demoList.isEmpty() || augment) {
            // Load default Demos
            try {
                demoList.addAll(readDemoClassNames(
                        new InputStreamReader(getClass().getResourceAsStream(metaSource))));
            } catch (IOException ex) {
                exception = ex;
                LOG.log(Level.WARNING, "unable to read resource: " + metaSource, ex);
            }
        }

        if (demoList.isEmpty()) {
//            displayErrorMessage(resourceMap.getString("error.noDemosLoaded"), 
//                    exception);
        }        
        return demoList;
  
    }

    private List<Demo> createDemoList(List<String> demoClassNamesList) {              
        List<Demo> demoList = new ArrayList<Demo>();
        for(String demoClassName: demoClassNamesList) {
             Demo demo = createDemo(demoClassName);
            if (demo != null) {
                demoList.add(demo);
            }
        }
        return demoList;
    }
    
    /**
     */
    private Demo createDemo(String demoClassName) {        
        Class<?> demoClass = null;
        Demo demo = null;
        try {
            demoClass = Class.forName(demoClassName);
            demo = new Demo(demoClass);
        } catch (ClassNotFoundException cnfe) {
            LOG.log(Level.WARNING, "demo class not found:"+ demoClassName);
        }        
        return demo;
    }

    /**
     * @return
     */
    public static DemoCreator getInstance() {
        return INSTANCE;
    }

}
