/*
 * Created on 04.11.2010
 *
 */
package org.jdesktop.swingx;

import java.awt.GraphicsEnvironment;
import java.util.logging.Logger;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.Border;

import org.jdesktop.swingx.plaf.SafeBorder;
import org.jdesktop.test.AncientSwingTeam;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * GTK specific tests. Will be blind on non-linux OS.
 * 
 * @author Jeanette Winzenburg, Berlin
 */
@RunWith(JUnit4.class)
public class GTKTest extends InteractiveTestCase {
    
    String[] borderKeys = {"focusCellHighlightBorder", "focusSelectedCellHighlightBorder", 
            "noFocusBorder"};
    
    static boolean hasGTK; 
    
    @Test
    public void testUninstallBorder() throws Exception {
        if (!hasGTK) {
            LOG.info("couldn't install GTK, is " + UIManager.getLookAndFeel());
            return;
        }
        LookAndFeel old = UIManager.getLookAndFeel();
        try {
            setLookAndFeel("Metal");
            Border border = UIManager.getBorder("Table." + borderKeys[0]);
            assertFalse(border instanceof SafeBorder);
        } finally {
            UIManager.setLookAndFeel(old);
        }
    }
    
    @Test
    public void testSafeBordersInstalledTable() {
        if (!hasGTK) {
            LOG.info("couldn't install GTK, is " + UIManager.getLookAndFeel());
            return;
        }
        for (String borderKey : borderKeys) {
            assertSafeBorder("Table.", borderKey);
        }
    }
    
    @Test
    public void testSafeBordersInstalledList() {
        if (!hasGTK) {
            LOG.info("couldn't install GTK, is " + UIManager.getLookAndFeel());
            return;
        }
        for (String borderKey : borderKeys) {
            assertSafeBorder("List.", borderKey);
        }
    }
    
    private void assertSafeBorder(String string, String borderKey) {
        Border border = UIManager.getBorder(string + borderKey);
        if (border != null) 
            assertTrue("expected border of type SafeBorder but was: " + border.getClass().getName(),
                border instanceof SafeBorder);
    }

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(GTKTest.class.getName());

    public static void main(String[] args) throws Exception {
        beforeClass();
        GTKTest test = new GTKTest();
        try {
            test.runInteractiveTests();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void interactiveBigEditor() {
        JXTable table = new JXTable(new AncientSwingTeam());
         
        showWithScrollingInFrame(table, "Issue #1297-swingx (GTK core issue) - click last column");
    }

    
    
    @BeforeClass
    public static void beforeClass() {
        // This test will not work in a headless configuration.
        // need to check before trying to install GTK (any LAF?)
        if (GraphicsEnvironment.isHeadless()) {
            LOG.fine("cannot run ui test - headless environment");
            return;
        }
        
        LookAndFeelInfo info = new LookAndFeelInfo("GTK plain",
                "com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        try {
            UIManager.installLookAndFeel(info);
            InteractiveTestCase.setLookAndFeel("GTK");
            hasGTK = true;
            // force loading of addons
            new JXTable();
            new JXList();
            
        } catch (Exception e) {
            // nothing to do - tests will be ignored;
        }
    }
}
