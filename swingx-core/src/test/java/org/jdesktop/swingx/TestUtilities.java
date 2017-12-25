/*
 * $Id: TestUtilities.java 1434 2006-09-27 01:55:10Z dmouse $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;
import javax.swing.plaf.metal.OceanTheme;

import org.jdesktop.swingx.plaf.LookAndFeelAddons;
import org.jdesktop.swingx.plaf.macosx.MacOSXLookAndFeelAddons;
import org.jdesktop.swingx.plaf.metal.MetalLookAndFeelAddons;
import org.jdesktop.swingx.plaf.motif.MotifLookAndFeelAddons;
import org.jdesktop.swingx.plaf.windows.WindowsClassicLookAndFeelAddons;
import org.jdesktop.swingx.plaf.windows.WindowsLookAndFeelAddons;

/**
 * Provides helper methods to test SwingX components .<br>
 */
public class TestUtilities {

  /**
   * Go through all existing LookAndFeelAddons. This leads all registered
   * {@link org.jdesktop.swingx.plaf.ComponentAddon} to initialize/uninitialize
   * themselves.
   */
  public static void cycleAddons(JComponent component) throws Exception {
    LookAndFeelAddons.setAddon(MacOSXLookAndFeelAddons.class.getName());
    component.updateUI();

    MetalTheme oldTheme = MetalLookAndFeel.getCurrentTheme();
    try {
      MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
      LookAndFeelAddons.setAddon(MetalLookAndFeelAddons.class.getName());
      component.updateUI();

      MetalLookAndFeel.setCurrentTheme(new OceanTheme());
      LookAndFeelAddons.setAddon(MetalLookAndFeelAddons.class.getName());
      component.updateUI();
    } finally {
      MetalLookAndFeel.setCurrentTheme(oldTheme);
    }

    LookAndFeelAddons.setAddon(MotifLookAndFeelAddons.class.getName());
    component.updateUI();

    LookAndFeelAddons.setAddon(WindowsLookAndFeelAddons.class.getName());
    component.updateUI();

    String property = UIManager.getString("win.xpstyle.name");
    try {
      UIManager.put("win.xpstyle.name",
        WindowsLookAndFeelAddons.HOMESTEAD_VISUAL_STYLE);
      LookAndFeelAddons.setAddon(WindowsClassicLookAndFeelAddons.class
        .getName());
      component.updateUI();

      UIManager.put("win.xpstyle.name",
        WindowsLookAndFeelAddons.SILVER_VISUAL_STYLE);
      LookAndFeelAddons.setAddon(WindowsClassicLookAndFeelAddons.class
        .getName());
      component.updateUI();

      UIManager.put("win.xpstyle.name", null);
      LookAndFeelAddons.setAddon(WindowsClassicLookAndFeelAddons.class
        .getName());
      component.updateUI();

    } finally {
      UIManager.put("win.xpstyle.name", property);
    }
  }

}
