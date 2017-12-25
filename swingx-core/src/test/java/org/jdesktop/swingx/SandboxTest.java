/*
 * Created on 31.07.2006
 *
 */
package org.jdesktop.swingx;

import java.util.logging.Logger;

import junit.framework.TestCase;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * "hand test" sandbox restrictions 
 * (here: around Utilities.initNamesAndValue, #353-swingx).
 * 
 * Note: to run this test manually, remove the ignore annotation 
 * can't automatically run tests that install a securityManager 
 * (because I found no way to uninstall it when the test class is done)
 * 
 */
@Ignore
@RunWith(JUnit4.class)
public class SandboxTest extends TestCase {
    private static final Logger LOG = Logger.getLogger(SandboxTest.class
            .getName());
    /**
     * quick test if table doesn't throw securityExceptions.
     * 
     *
     */
    @Test
    public void testSecurityManager() {
        if (System.getSecurityManager() == null) {
            LOG.info("cannot run testSecurityManager - no SecurityManager installed");
            return;
        }
        @SuppressWarnings("unused")
//        JXTable table = new JXTable();
        JXDatePicker picker = new JXDatePicker();
        try {
            System.getProperty("user.home", "not specified");
            fail("Sandbox without security priviledges");
        } catch (SecurityException e) {
            // nothing to do - that's what we expect
        }
        
    }
    
    /**
     * Sanity: make sure the second-time-around is reached!
     *
     */
    @Test
    public void testSecurityManagerAgain() {
        if (System.getSecurityManager() == null) {
            LOG.info("cannot run testSecurityManagerAgain - no SecurityManager installed");
            return;
        }
        try {
            System.getProperty("user.home", "not specified");
            fail("Sandbox without security priviledges");
        } catch (SecurityException e) {
            // nothing to do - that's what we expect
        }
    }
    
    @Override
    @Before
    public void setUp() throws Exception {
//        LookAndFeelAddons.getAddon();
        InteractiveTestCase.setLAF("Win");
        // A - install the default SecurityManager. 
        // Doing so we are not allowed to reverse the install -
        // which makes this testCase to a manual-run-only affair
        // (the securityManager is not uninstalled when running 
        // other test cases - in Eclipse, when running the 
        // bulk "all tests" of a projects. 
        // Same as B (accessing addon throws)
        // the behaviour is similiar as Metal not choosable in
        // webstart .. classLoader prob?
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        // B- if we install a SecurityManager we need to be sure
        // that we are allowed to uninstall it.
        // BUT: with this custom manager on, JXTable instantiation
        // fails with a rather weird stack-trace. Gave up for now...
//        if (System.getSecurityManager() == null) {
//                System.setSecurityManager(new SecurityManager() {
//
//                    @Override
//                    public void checkPermission(Permission perm) {
//                        if ("setSecurityManager".equals(perm.getName())) return;
//                        super.checkPermission(perm);
//                    }
//                    
//                });
//            }        
              
    }
    
//    @After
//    @Override
//    protected void tearDown() throws Exception {
//        super.tearDown();
//    }
    
  
    @AfterClass
    public static void uninstall() {
        // be sure to uninstall the manager
//        System.setSecurityManager(null);
        
    }
    
//    @BeforeClass
//    public static void init() {
//        System.setSecurityManager(new SecurityManager());
//        try {
//            InteractiveTestCase.setLookAndFeel("Win");
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
}
