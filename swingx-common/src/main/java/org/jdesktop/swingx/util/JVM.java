/*
 * $Id: JVM.java 4236 2012-08-13 17:32:35Z kschaefe $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
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
 */
package org.jdesktop.swingx.util;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

/**
 * Deals with the different version of the Java Virtual Machine. <br>
 */
@SuppressWarnings("nls")
public class JVM {

  public final static int JDK1_0 = 1000;
  public final static int JDK1_1 = 1100;
  public final static int JDK1_2 = 1200;
  public final static int JDK1_3 = 1300;
  public final static int JDK1_4 = 1400;
  public final static int JDK1_5 = 1500;
  public final static int JDK1_6 = 1600;
  public final static int JDK1_6N = 1610;
  public final static int JDK1_7 = 1700;

  private static JVM current;
  static {
    current = new JVM();
  }

  /**
   * @return the current JVM object
   */
  public static JVM current() {
    return current;
  }

  private int jdkVersion;

  /**
   * Creates a new JVM data from the <code>java.version</code>
   * System property
   *  
   */
  public JVM() {
    this(System.getProperty("java.version"));
  }

  /**
   * Constructor for the OS object
   */
  public JVM(String p_JavaVersion) {
    if (p_JavaVersion.startsWith("1.7.")) {
      jdkVersion = JDK1_7;
    } else if (p_JavaVersion.startsWith("1.6.")) {
      for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
          if ("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel".equals(info.getClassName())) {
              jdkVersion = JDK1_6N;
              break;
          }
      }
      
      jdkVersion = jdkVersion == 0 ? JDK1_6 : jdkVersion;
    } else if (p_JavaVersion.startsWith("1.5.")) {
      jdkVersion = JDK1_5;
    } else if (p_JavaVersion.startsWith("1.4.")) {
      jdkVersion = JDK1_4;
    } else if (p_JavaVersion.startsWith("1.3.")) {
      jdkVersion = JDK1_3;
    } else if (p_JavaVersion.startsWith("1.2.")) {
      jdkVersion = JDK1_2;
    } else if (p_JavaVersion.startsWith("1.1.")) {
      jdkVersion = JDK1_1;
    } else if (p_JavaVersion.startsWith("1.0.")) {
      jdkVersion = JDK1_0;
    } else {
      // unknown version, assume 1.3
      jdkVersion = JDK1_3;
    }
  }

  public boolean isOrLater(int p_Version) {
    return jdkVersion >= p_Version;
  }

  public boolean isOneDotOne() {
    return jdkVersion == JDK1_1;
  }

  public boolean isOneDotTwo() {
    return jdkVersion == JDK1_2;
  }

  public boolean isOneDotThree() {
    return jdkVersion == JDK1_3;
  }

  public boolean isOneDotFour() {
    return jdkVersion == JDK1_4;
  }

  public boolean isOneDotFive() {
    return jdkVersion == JDK1_5;
  }

  public boolean isOneDotSix() {
    return jdkVersion == JDK1_6 || isOneDotSixUpdateN();
  }

    /**
     * Determines if the version of JDK1_6 has Nimbus Look and Feel installed.
     * 
     * @return {@code true} if Nimbus is available and the version is 1.6;
     *         {@code false} otherwise
     */
  public boolean isOneDotSixUpdateN() {
      return jdkVersion == JDK1_6N;
  }
  
  public boolean isOneDotSeven() {
      return jdkVersion == JDK1_7;
  }

}