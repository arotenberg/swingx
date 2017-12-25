/**
 * @PROJECT.FULLNAME@ @VERSION@ License.
 *
 * Copyright @YEAR@ L2FProd.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jdesktop.swingx;

import java.util.Properties;

import javax.swing.JTree;

import junit.framework.TestCase;

import org.jdesktop.swingx.icon.EmptyIcon;
import org.jdesktop.swingx.tips.DefaultTip;
import org.jdesktop.swingx.tips.DefaultTipOfTheDayModel;
import org.jdesktop.swingx.tips.TipLoader;
import org.jdesktop.swingx.tips.TipOfTheDayModel;
import org.jdesktop.test.PropertyChangeReport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class JXTipOfTheDayTest extends TestCase {

    @Test
  public void testBean() {
    PropertyChangeReport report = new PropertyChangeReport();
    JXTipOfTheDay totd = new JXTipOfTheDay();
    assertEquals(0, totd.getCurrentTip());
    // with an empty model next/previous are no-op
    totd.nextTip();
    totd.previousTip();
    assertFalse(report.hasEvents(JXTipOfTheDay.CURRENT_TIP_CHANGED_KEY));
    
    totd.addPropertyChangeListener(report);
    
    DefaultTipOfTheDayModel model = new DefaultTipOfTheDayModel();
    model.add(new DefaultTip("name1", "description1"));
    model.add(new DefaultTip("name2", "<html>description2"));
    model.add(new DefaultTip("name3", new EmptyIcon()));
    model.add(new DefaultTip("name3", new JTree()));

    totd.setModel(model);
    assertTrue(report.hasEvents("model"));
    
    assertFalse(report.hasEvents(JXTipOfTheDay.CURRENT_TIP_CHANGED_KEY));
    assertEquals(0, totd.getCurrentTip());
    totd.nextTip();
    assertTrue(report.hasEvents(JXTipOfTheDay.CURRENT_TIP_CHANGED_KEY));  
    assertEquals(1, totd.getCurrentTip());
    totd.nextTip();
    assertEquals(2, totd.getCurrentTip());
    totd.nextTip();
    assertEquals(3, totd.getCurrentTip());
    // tips cycle when bounds are reached
    totd.nextTip();
    assertEquals(0, totd.getCurrentTip());
    totd.previousTip();
    assertEquals(3, totd.getCurrentTip());
    
    // do not accept to go outside of tip count bounds
    try {
      totd.setCurrentTip(-1);
      fail("Negative tip!");
    } catch (IllegalArgumentException e) {      
    }
    
    try {
      totd.setCurrentTip(totd.getModel().getTipCount());
      fail("Over the limit!");
    } catch (IllegalArgumentException e) {      
    }

    // do not accept null model
    try {
      totd.setModel(null);
      fail("Should not reach this code");
    } catch (IllegalArgumentException e) {      
    }
    
    
    DefaultTip tip = new DefaultTip();
    assertNull(tip.getTip());
    assertNull(tip.getTipName());
    tip.setTipName("name");
    assertEquals("name", tip.getTipName());
    tip.setTip("tip");
    assertEquals("tip", tip.getTip());
    assertEquals(tip.getTipName(), tip.toString());
  }
  
    @Test
  public void testTipLoader() {
    TipOfTheDayModel model;
    
    Properties props = new Properties();
    
    // empty properties create empty model
    model = TipLoader.load(props);
    assertEquals(0, model.getTipCount());
    
    // one tip with name and description
    props.put("tip.1.name", "name1");
    props.put("tip.1.description", "description1");
    model = TipLoader.load(props);
    assertEquals(1, model.getTipCount());
    assertEquals("name1", model.getTipAt(0).getTipName());
    assertEquals("description1", model.getTipAt(0).getTip());   
    
    // one tip with description only (name is optional)
    props.put("tip.2.description", "description2");
    model = TipLoader.load(props);
    assertEquals(2, model.getTipCount());
    assertEquals("name1", model.getTipAt(0).getTipName());
    assertEquals("description1", model.getTipAt(0).getTip());   
    assertNull(model.getTipAt(1).getTipName());
    assertEquals("description2", model.getTipAt(1).getTip());
    
    // one tip with name only, but description is mandatory!
    props.put("tip.3.name", "name3");
    try {
      model = TipLoader.load(props);
      assertTrue("Must not get here", false);
    } catch (IllegalArgumentException e) {
      // expected
    }
    
    // one missing number in the tip sequence
    props.put("tip.3.description", "desc3");
    props.put("tip.5.description", "desc5");
    model = TipLoader.load(props);
    assertEquals(3, model.getTipCount());
  }
  
    @Test
  public void testAddon() throws Exception {
    // move around all addons
    TestUtilities.cycleAddons(new JXTipOfTheDay());
  }
  
    @Test
  public void testChoice() throws Exception {
    JXTipOfTheDay.ShowOnStartupChoice noshow = new JXTipOfTheDay.ShowOnStartupChoice() {
      public boolean isShowingOnStartup() { return false; }
      public void setShowingOnStartup(boolean showOnStartup) { }
    };
    
    JXTipOfTheDay totd = new JXTipOfTheDay();
    assertFalse(totd.showDialog(null, noshow));
  }
}
