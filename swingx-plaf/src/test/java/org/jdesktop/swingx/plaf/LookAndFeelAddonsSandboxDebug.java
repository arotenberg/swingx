/*
 * Created on 05.07.2013
 *
 */
package org.jdesktop.swingx.plaf;

import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ServiceLoader;
import java.util.logging.Logger;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Contains failing tests used for debugging the reason for sandbox
 * failures. We understand the reasons now, but let's keep the 
 * tests to not forget.<p>
 * 
 * Note: to run this test manually, remove the ignore annotation 
 * can't automatically run tests that install a securityManager 
 * (because I found no way to uninstall it when the test class is done)
 * 
 * @author Jeanette Winzenburg, Berlin
 */
@Ignore
@RunWith(JUnit4.class)
public class LookAndFeelAddonsSandboxDebug extends
        LookAndFeelAddonsSandboxOnTest {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(LookAndFeelAddonsSandboxDebug.class.getName());

    /**
     * Testing privileged access to the ServiceLoader.
     * Bare grabbing inside the priviledgeAction doesn't work, 
     * probably because the classes are
     * loaded lazily
     */
    @Test
    public void testServiceLoaderPrivileged() {
        ServiceLoader<LookAndFeelAddons> loader =
                AccessController.doPrivileged(new PrivilegedAction<ServiceLoader<LookAndFeelAddons>>() {
                    @Override
                    public ServiceLoader<LookAndFeelAddons> run() {
                        return ServiceLoader.load(LookAndFeelAddons.class);
                    }
                });  
        assertTrue("loader must have addons: fails here because the loading happens lazily", loader.iterator().hasNext());
    }

    /**
     * Issue #1567-swingx: addon lookup doesn't work in security
     * restricted contexts.
     * 
     * Here test if we have access to the serviceProvider resource.
     * Not like this - any way to do in the sandbox?  
     */
    @Test
    public void testAccessMetaInf() {
        Class<?> clazz = LookAndFeelAddons.class;
        // JW: just a reminder (to myself)
        // class.getResource interprets path as relative without 
        // leading slash
        // classloader.getResource always absolute
        String services = "META-INF/services/" + clazz.getName();
        // using the classloader (just as ServiceLoader does)
        // absolute path always
        URL url = clazz.getClassLoader().getResource(services);
        assertNotNull("services must be found: fails in security restricted contexts because the loader " +
        		"has no access to the provider configuration file", 
        		url);
    }

    /**
     * Issue #1567-swingx: addon lookup doesn't work in security
     * restricted contexts.
     * 
     * The core of the issue seems to be that 
     * ServiceLoader doesn't read the provided addons - expected
     * or not? 
     */
    @Test
    public void testServiceLoader() {
        ServiceLoader<LookAndFeelAddons> loader = ServiceLoader.load(LookAndFeelAddons.class);
        assertTrue("loader must have addons: fails in security restricted contexts because the loader " +
        		"has no access to the provider configuration file", 
        		loader.iterator().hasNext());
    }

}
