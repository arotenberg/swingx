/*
 * Created on 27.02.2013
 *
 */
package org.jdesktop.swingx;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.plaf.synth.SynthLookAndFeel;

import org.jdesktop.swingx.painter.ImagePainter;
import org.jdesktop.swingx.test.XTestUtils;

public class SynthPanelPainterTest extends JFrame
{
  private static String synthXml = "<synth>" +
  "  <style id=\"all\">" +
  "    <font name=\"Tahoma\" size=\"11\"/>" +
  "  </style>" +
  "  <bind style=\"all\" type=\"REGION\" key=\".*\"/>" +
  "  <style id=\"panel\">" +
  "    <state>" +
  "      <opaque value=\"true\"/>" +
  "      <color type=\"BACKGROUND\" value=\"#FFFFFF\" />" +
  "      <color type=\"FOREGROUND\" value=\"#000000\" />" +
  "      <imagePainter method=\"panelBackground\" path=\"/org/jdesktop/swingx/panelBackground.png\" sourceInsets=\"5 5 5 5\" paintCenter=\"true\" stretch=\"true\" />" +  
  "    </state>" +
  "  </style>" +
  "  <bind style=\"panel\" type=\"region\" key=\"Panel\"/>" +
  "</synth>";
  
  public static void main(String[] args)
  {
    EventQueue.invokeLater(new Runnable(){
      public void run()
      {
//        UIManager.put("JXPanel.patch", Boolean.TRUE);
        try
        {
          new SynthPanelPainterTest();
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
    });   
  }

  public SynthPanelPainterTest() throws Exception
  {
    InputStream is = new ByteArrayInputStream(synthXml.getBytes("UTF8"));
    SynthLookAndFeel laf = new SynthLookAndFeel();
    laf.load(is, SynthPanelPainterTest.class);
    UIManager.setLookAndFeel(laf);    

    setLayout(new BoxLayout(getContentPane(), BoxLayout.LINE_AXIS));

    JPanel p = new JPanel(new BorderLayout());
//    p.setBorder(new LineBorder(Color.GREEN));
    p.add(new JLabel("in core panel"));
    add(p);    
    
    JXPanel xp = new JXPanel(new BorderLayout());
    
    JLabel label = new JLabel("happily living in x panel");
    label.setForeground(Color.GREEN);
    label.setHorizontalAlignment(JLabel.CENTER);
    xp.add(label);
    xp.setBackgroundPainter(new ImagePainter(XTestUtils.loadDefaultImage()));
//    xp.setBorder(new LineBorder(Color.RED));
    add(xp);    
    
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(new Dimension(600, 400));
    setLocationRelativeTo(null);
    setVisible(true);      
  }  
}  
