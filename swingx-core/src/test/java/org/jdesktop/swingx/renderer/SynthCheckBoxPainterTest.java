/*
 * Created on 15.02.2013
 *
 */
package org.jdesktop.swingx.renderer;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.synth.SynthLookAndFeel;
import javax.swing.table.AbstractTableModel;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.decorator.PainterHighlighter;
import org.jdesktop.swingx.painter.ImagePainter;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.test.XTestUtils;
import org.jdesktop.swingx.util.PaintUtils;

/**
 * Issue 1546-swingx: striping lost in synth-based LAFs (Regression 1.6.5)
 * 
 * @author Wolfgang Zitzelsberger
 * @author Jeanette Winzenburg
 */
public class SynthCheckBoxPainterTest extends JFrame
{
  private static String synthXml = "<synth>" +
  "  <style id=\"all\">" +
  "    <font name=\"Tahoma\" size=\"11\"/>" +
  "  </style>" +
  "  <bind style=\"all\" type=\"REGION\" key=\".*\"/>" +
  "  <style id=\"checkbox\">" +
  "    <state>" +
  "      <opaque value=\"false\"/>" +
  "      <color type=\"BACKGROUND\" value=\"#FFE0E0\" />" +
  "      <color type=\"FOREGROUND\" value=\"#000000\" />" +
  "      <imagePainter method=\"checkBoxBackground\" path=\"/org/jdesktop/swingx/renderer/checkBoxBackground.png\" sourceInsets=\"5 5 5 5\" paintCenter=\"true\" stretch=\"true\" />" +  
  "    </state>" +
  "    <state value=\"SELECTED\">" +
  "      <color type=\"BACKGROUND\" value=\"#E0FFE0\"/>" + 
  "      <color type=\"TEXT_FOREGROUND\" value=\"#00AA00\"/>" + 
  "    </state>" +   
  "  </style>" +
  "  <bind style=\"checkbox\" type=\"region\" key=\"Checkbox\"/>" +
  "  <style id=\"table\">" +       
  "    <state>" +
  "      <color type=\"BACKGROUND\" value=\"#FFFFF0\" />" +
  "      <color type=\"FOREGROUND\" value=\"#000000\" />" +
  "    </state>" +
  "    <state value=\"SELECTED\">" +
  "      <color type=\"TEXT_BACKGROUND\" value=\"#0070C0\"/>" + 
  "      <color type=\"TEXT_FOREGROUND\" value=\"#FFFFFF\"/>" + 
  "    </state>" +   
  "  </style>" +
  "  <bind style=\"table\" type=\"region\" key=\"Table\"/>" +
  "  <style id=\"cellRenderer\"> " +
  "    <state>" +
  "      <opaque value=\"true\"/>" +
  "    </state>" +
  "  </style>" +  
  "  <bind style=\"cellRenderer\" type=\"name\" key=\"Table.cellRenderer\"/>" +
  "</synth>";
  
  public static void main(String[] args)
  {
    EventQueue.invokeLater(new Runnable(){
      public void run()
      {
        try
        {
          new SynthCheckBoxPainterTest();
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
    });   
  }

  public SynthCheckBoxPainterTest() throws Exception
  {
    InputStream is = new ByteArrayInputStream(synthXml.getBytes("UTF8"));
    SynthLookAndFeel laf = new SynthLookAndFeel();
    laf.load(is, SynthCheckBoxPainterTest.class);
    UIManager.setLookAndFeel(laf); 
    // optional: background image to check fix for 1513- swingx
    // support fully transparent renderers
    JXPanel panel = new JXPanel(new BorderLayout());
    panel.setBackgroundPainter(new ImagePainter(XTestUtils.loadDefaultImage("moon.jpg")));
    setContentPane(panel);
    

    JXTable table = new JXTable(new MyTableModel(100));
    // optional: forcing a semi-transparent background onto an otherwise opaque 
    // renderingComponent
//    DefaultTableRenderer renderer = (DefaultTableRenderer) table.getDefaultRenderer(Boolean.class);
//    ComponentProvider provider = renderer.getComponentProvider();
//    provider.getDefaultVisuals().setBackground(PaintUtils.setAlpha(Color.BLUE, 100));
    // apply striping
    table.addHighlighter(HighlighterFactory.createSimpleStriping(Color.YELLOW));
    // apply swingx painter
    table.addHighlighter(new PainterHighlighter(HighlightPredicate.ROLLOVER_ROW, 
            new MattePainter(PaintUtils.setAlpha(Color.RED, 100))));
//            new BusyPainter()));
    add(new JScrollPane(table));    


    // optional: background image to check fix for 1513- swingx
    // support fully transparent renderers
    // set transparent
//    ((DefaultTableRenderer) table.getDefaultRenderer(Boolean.class))
//        .getComponentProvider().getRendererComponent(null).setOpaque(false);
//    table.setOpaque(false);
//    ((JComponent) table.getParent()).setOpaque(false);
//    ((JComponent) table.getParent().getParent()).setOpaque(false);


    add(new JCheckBox("Regular CheckBox"), BorderLayout.SOUTH);
    
    setTitle("Synth table (transparent/update) rollover " + System.getProperty("java.version"));
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(new Dimension(600, 400));
    setLocationRelativeTo(null);
    setVisible(true);      
  }  
  
  
  private static class MyTableModel extends AbstractTableModel
  {
    private int rows;
    private int cols = 3;
    private ArrayList<Object[]> data;
    private Class<?>[] columns = new Class<?>[] { String.class, Boolean.class, ImageIcon.class};

    public MyTableModel(int rows)
    {
      this.rows = rows;
      this.data = new ArrayList<Object[]>();
      for (int i = 0; i < rows; i++)
      {
        Object[] row = new Object[this.cols];
        this.data.add(row);
        row[0] = "cell " + i + ":" + 0;
        row[1] = new Boolean(i % 2 == 0);
        row[2] = createIcon();
      }
      // for debugging: solid background in row with striping color
      setValueAt(false, 1, 1);
    }

    private Icon createIcon()
    {
      return new Icon()
      {
        private Color color = new Color(new Random().nextInt(0xFFFFFF));

        public int getIconHeight()
        {
          return 16;
        }

        public int getIconWidth()
        {
          return 16;
        }

        public void paintIcon(Component c, Graphics g, int x, int y)
        {
          g.setColor(color);
          g.fillRect(x, y, getIconWidth(), getIconHeight());
        }
      };
    }

    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
      return this.columns[columnIndex];
    }

    public int getColumnCount()
    {
      return this.cols;
    }

    public int getRowCount()
    {
      return this.rows;
    }

    public Object getValueAt(int row, int col)
    {
      return this.data.get(row)[col];
    }

    @Override
    public String getColumnName(int column)
    {
      return this.getColumnClass(column).getSimpleName();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex)
    {
      return columnIndex < 2; //(rowIndex % 2 == 0);
    }

    @Override
    public void setValueAt(Object value, int row, int col)
    {
      this.data.get(row)[col] = value;
      this.fireTableCellUpdated(row, col);
    }
  }

}  
