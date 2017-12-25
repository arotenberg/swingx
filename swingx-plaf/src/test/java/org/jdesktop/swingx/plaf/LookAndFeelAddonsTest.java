/*
 * $Id: LookAndFeelAddonsTest.java 4303 2013-07-05 10:00:17Z kleopatra $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.plaf;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.basic.BasicLookAndFeel;

import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.plaf.metal.MetalLookAndFeelAddons;
import org.jdesktop.test.EDTRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EDTRunner.class)
@SuppressWarnings("nls")
public class LookAndFeelAddonsTest {
    
    
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(LookAndFeelAddonsTest.class.getName());
    
    /**
     * PENDING JW: something fishy here - not trackingLAF changes? This test
     * passes but a test related to WindowsLookAndFeelAddons fails with
     * incomplete initialize?
     * Moved the failing test into its own class - where it passes ...
     */
    @Test
    public void testTrackingTrue() {
        assertTrue(LookAndFeelAddons.isTrackingLookAndFeelChanges());
    }
    
    /**
     * Ensure that an exception is thrown when a component is passed in that does
     * not contain get/setBackgroundPainter.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInstallBackgroundPainterWithoutPainterCapable() {
        LookAndFeelAddons.installBackgroundPainter(new JPanel(), null);
    }
    
    /**
     * Ensure that an exception is thrown when a {@code null} key is passed in.
     */
    @Test(expected = NullPointerException.class)
    public void testInstallBackgroundPainterWithNullComponent() {
        LookAndFeelAddons.installBackgroundPainter(null, "test.painter");
    }
    
    /**
     * Ensure that an exception is thrown when a {@code null} key is passed in.
     */
    @Test(expected = NullPointerException.class)
    public void testInstallBackgroundPainterWithNullPainterKey() {
        LookAndFeelAddons.installBackgroundPainter(new BackgroundPainterTestingComponent(), null);
    }

    /**
     * Ensure that installBackgroundPainter does not overwrite a user-specified
     * painter.
     */
    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testInstallBackgroundPainter() {
        Painter plafPainter = new PainterUIResource(null);
        UIManager.put("test.painter", plafPainter);
        
        BackgroundPainterTestingComponent panel = new BackgroundPainterTestingComponent();
        //overwrite null painter
        LookAndFeelAddons.installBackgroundPainter(panel, "test.painter");
        
        assertThat(panel.getBackgroundPainter(), is(sameInstance(plafPainter)));
        
        panel.setBackgroundPainter(new PainterUIResource(null));
        
        //overwrite uiresource painter
        LookAndFeelAddons.installBackgroundPainter(panel, "test.painter");
        
        assertThat(panel.getBackgroundPainter(), is(sameInstance(plafPainter)));
        
        Painter userPainter = new MattePainter();
        panel.setBackgroundPainter(userPainter);
        
        //do not overwrite user painter
        LookAndFeelAddons.installBackgroundPainter(panel, "test.painter");
        
        assertThat(panel.getBackgroundPainter(), is(sameInstance(userPainter)));
    }

   /**
     * Issue #784-swingx: Frequent NPE in getUI of (new) SwingX components.
     * 
     * Here: test reload if same instance of LAF is set (example from
     * bug report)
     * @throws UnsupportedLookAndFeelException 
     */
    @Test
    public void testReloadAddons() throws UnsupportedLookAndFeelException {
        // load the addon for a new component
        new BackgroundPainterTestingComponent();
        // reset laf
         UIManager.setLookAndFeel(UIManager.getLookAndFeel());
         // check that the addon is still available
        new BackgroundPainterTestingComponent();

    }
  /**
   * A look and feel can't override SwingX defaults
   */
    @Test
  public void testIssue293() throws Exception {
	  class CustomLF extends BasicLookAndFeel {
	    @Override
	    public String getDescription() {
	      return "custom";
	    }
		  @Override
		  public String getID() {
		    return "custom";
		  }
		  @Override
		  public String getName() {
		    return "custom";
		  }
		  @Override
		  public boolean isNativeLookAndFeel() {
		    return false;
		  }
		  @Override
		  public boolean isSupportedLookAndFeel() {
		    return true;
		  }
      @Override
      protected void initComponentDefaults(UIDefaults table) {
        super.initComponentDefaults(table);
        table.put("CustomProperty", "CustomValue");
      }
	  };
    
    LookAndFeelAddons.setTrackingLookAndFeelChanges(true);
    
    // without addons, the prop is not overriden
    LookAndFeel lf = new CustomLF();
    UIManager.setLookAndFeel(lf);
    assertEquals("CustomValue", UIManager.get("CustomProperty"));
    
    // with an addon, the prop is overriden
    ComponentAddon myAddon = new AbstractComponentAddon("myAddon") {
      @Override
      protected void addBasicDefaults(LookAndFeelAddons addon, DefaultsList defaults) {
        defaults.add("CustomProperty", "customAddonValue");
        defaults.add("AddonProperty", "addonValue");
      }
    };
    LookAndFeelAddons.contribute(myAddon);
    // the addon property was not registered as overriden by the l&f
    assertEquals("CustomValue", UIManager.get("CustomProperty"));
    assertEquals("addonValue", UIManager.get("AddonProperty"));
    
    // now revert to a standard look and feel
    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    // now the addon properties are used 
    assertEquals("customAddonValue", UIManager.get("CustomProperty"));
    assertEquals("addonValue", UIManager.get("AddonProperty"));    
  }
  
  /**
   * LookAndFeelAddons override entries manually added to UIManager
   */
    @Test
  public void testIssue144() throws Exception {
    UIManager.put("Addon.title", "customized");
    UIManager.put("Addon.border", new LineBorder(Color.blue));
    
    Addon addon = new Addon();
    LookAndFeelAddons.contribute(addon);
    
    assertEquals("customized", UIManager.get("Addon.title"));
    assertEquals("my subtitle", UIManager.get("Addon.subtitle"));
    assertTrue(UIManager.get("Addon.border") instanceof LineBorder);
    
    UIManager.put("Addon.title", null);
    assertEquals("my title", UIManager.get("Addon.title"));
  }

    @Test
  public void testContribute() throws Exception {
    Addon addon = new Addon();
    LookAndFeelAddons.contribute(addon);
    // a ComponentAddon is initialized when it is contributed
    assertTrue(addon.initialized);
    // and uninitialized when "uncontributed"
    LookAndFeelAddons.uncontribute(addon);
    assertTrue(addon.uninitialized);

    // re-contribute the ComponentAddon
    LookAndFeelAddons.contribute(addon);
    // reset its state
    addon.initialized = false;
    addon.uninitialized = false;

    // when addon is changed, the ComponentAddon is uninitialized with the
    // previous addon, then initialized with the new
    LookAndFeelAddons oldLFAddon = LookAndFeelAddons.getAddon();
    LookAndFeelAddons.setAddon(MetalLookAndFeelAddons.class);
    LookAndFeelAddons newLFAddon = LookAndFeelAddons.getAddon();

    assertTrue(addon.uninitialized);
    assertEquals(oldLFAddon, addon.uninitializedWith);

    assertTrue(addon.initialized);
    assertEquals(newLFAddon, addon.initializedWith);
  }

    @Test
    public void testUpdateUIForReferredProperties() throws Exception {
        LookAndFeelAddons.contribute(new Addon());
    	
        Color panelBackground = UIManager.getColor("Panel.background");
    	assertThat(UIManager.getColor("Addon.panelBackground"), is(panelBackground));
    	
    	UIManager.put("Panel.background", new ColorUIResource(panelBackground.darker()));
    	
    	assertThat(UIManager.getColor("Addon.panelBackground"), is(panelBackground));
    }
}

@SuppressWarnings("nls")
class Addon extends AbstractComponentAddon {
    boolean initialized;
    LookAndFeelAddons initializedWith;
    
    boolean uninitialized;
    LookAndFeelAddons uninitializedWith;
    
    public Addon() {
        super("Addon");
    }
    @Override
    public void initialize(LookAndFeelAddons addon) {
        initialized = true;
        initializedWith = addon;
        addon.loadDefaults(getDefaults());
    }
    @Override
    public void uninitialize(LookAndFeelAddons addon) {
        uninitialized = true;
        uninitializedWith = addon;
        addon.unloadDefaults(getDefaults());
    }
    protected Object[] getDefaults() {
        return new Object[] {
                "Addon.title", "my title",
                "Addon.subtitle", "my subtitle",
                "Addon.border", new BorderUIResource(new EmptyBorder(0, 0, 0, 0)),
                "Addon.color", new ColorUIResource(Color.blue),
                "Addon.panelBackground", UIManager.getColor("Panel.background")};
    }
}

class BackgroundPainterTestingComponent extends JComponent {
    static {
        LookAndFeelAddons.contribute(new Addon());
    }
    
    private Painter<?> backgroundPainter;
    
    public Painter<?> getBackgroundPainter() {
        return backgroundPainter;
    }
    
    public void setBackgroundPainter(Painter<?> backgroundPainter) {
        this.backgroundPainter = backgroundPainter;
    }
}