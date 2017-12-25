/*
 * Created on 31.07.2006
 *
 */
package org.jdesktop.swingx.plaf;

import java.util.logging.Logger;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import junit.framework.TestCase;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Testing LookAndFeelAddons properties/behaviour that might be effected
 * in a sandbox. This here is the test without SecurityManager, the 
 * subclass actually installs a manager.
 */
@RunWith(JUnit4.class)
public class LookAndFeelAddonsSandboxTest extends TestCase {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(LookAndFeelAddonsSandboxTest.class
            .getName());
    
    
    /**
     * Issue #1567-swingx: addon lookup doesn't work in security
     * restricted contexts.
     * 
     * Here we test that the addon is changed to match a newly 
     * set LAF (Nimbus), 
     * assuming that the ui is initially set to system.
     */
    @Test
    public void testMatchingAddon() throws Exception {
        LookAndFeel old = UIManager.getLookAndFeel();
        try {
            assertTrue("sanity: addon is configured to update on LAF change", 
                    LookAndFeelAddons.isTrackingLookAndFeelChanges());
            setLookAndFeel("Nimbus");
            LookAndFeelAddons addon = LookAndFeelAddons.getAddon();
            assertTrue("addon must match Nimbus, but was: " + addon, addon.matches());
            
        } finally {
            UIManager.setLookAndFeel(old);
        }
    }
    
    /**
     * Issue #1567-swingx: addon lookup doesn't work in security
     * restricted contexts.
     * 
     * Here we test for systemLAF, 
     * assuming that the ui is set to system.
     */
    @Test
    public void testSystemAddon() {
        LookAndFeelAddons addon = LookAndFeelAddons.getAddon();
        assertTrue("addon must be system addon, but was: " + addon, addon.isSystemAddon());
    }
    
    
    /**
     * Sets the default LAF to system. 
     * 
     */
    @BeforeClass
    public static void install() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Fix issue #1578-swingx: remove dependency on core module.
     * 
     * C&p'd from InteractiveTestCase: accessing that class here 
     * will break maven builds by introducing a cyclic dependency.
     * 
     * @param nameSnippet
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws UnsupportedLookAndFeelException
     */
    public static void setLookAndFeel(final String nameSnippet)
            throws ClassNotFoundException, InstantiationException,
            IllegalAccessException, UnsupportedLookAndFeelException {
        final String laf = getLookAndFeelClassName(nameSnippet);
        if (laf != null) {
            UIManager.setLookAndFeel(laf);
            return;
        }
        throw new UnsupportedLookAndFeelException(
                "no LAF installed with name snippet " + nameSnippet);
    }

    public static String getLookAndFeelClassName(final String nameSnippet) {
        final UIManager.LookAndFeelInfo[] plafs = UIManager
                .getInstalledLookAndFeels();
        for (final UIManager.LookAndFeelInfo info : plafs) {
            if (info.getName().contains(nameSnippet)) {
                return info.getClassName();
            }
        }
        return null;
    }
}
