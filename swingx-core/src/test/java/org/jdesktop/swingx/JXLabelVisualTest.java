package org.jdesktop.swingx;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class JXLabelVisualTest extends InteractiveTestCase {

    public static void main(String[] args) throws Exception {
        // setSystemLF(true);
        JXLabelVisualTest test = new JXLabelVisualTest();

        try {
            test.runInteractiveTests();
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }

    /**
     * #swingx-680 Preferred size is not set when label is rotated.
     */
    public static void interactiveJXLabel() {

    JFrame testFrame = new JFrame("JXLabel Test");
    testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JXLabel label = new JXLabel("This is some JXLabel text");
    label.setTextRotation(Math.PI/4);

    testFrame.setContentPane(label);
    testFrame.pack();
    testFrame.setLocationByPlatform(true);
    testFrame.setVisible(true);
    }

    /**
     * swingx-??? The text is wrapped 20px too wide causing horizontal slider of parent scroll pane to appear.
     * It seems that getVisibleRect() (called from renderer.getPreferredSpan) returns rectangle 20px too wide (width of left and right parent border) so it actually returns scroll panes visible rect rather then visible rect of the label embedded in jpanel with the border.
     */
    public void interactiveLabelTextTooWide()
    {
      JFrame frame = new JFrame();
      JPanel p = new JPanel(new BorderLayout());
      p.setBorder(new EmptyBorder(10, 10, 10, 10));

      JXLabel header = new JXLabel();
      header.setLineWrap(true);
      header.setText("Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed " +
                  "do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut " +
                  "enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi" +
                  " ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit " +
                  "in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur" +
                  " sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt" +
                  " mollit anim id est laborum.");
      Icon icon = new Icon(){
        public int getIconHeight()
        {
          return 40;
        }
        public int getIconWidth()
        {
          return 80;
        }
        public void paintIcon(Component c, Graphics g, int x, int y)
        {
          g.setColor(Color.GREEN);
          g.fillRect(x, y, getIconWidth(), getIconHeight());
        }
      };
      header.setIcon(icon);
      header.setOpaque(true);
      p.add(header, BorderLayout.NORTH);
      p.setOpaque(true);
      p.setBackground(Color.red);

      JScrollPane sp = new JScrollPane(p);
      frame.add(sp);

      frame.setTitle(this.getClass().getSimpleName());
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setPreferredSize(new Dimension(400, 300));
      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);
    }


    /**
     * Do nothing, make the test runner happy
     * (would output a warning without a test fixture).
     *
     */
    @Test
    public void testDummy() {

    }
}