/*
 * $Id$
 *
 * Copyright 2009 Sun Microsystems, Inc., 4150 Network Circle,
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
 *
 */
package org.jdesktop.swingx.plaf.nimbus;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.table.AbstractTableModel;

import org.jdesktop.swingx.SwingXUtilities;


/**
 * Nimbus colors ... evil magic?
 */
public class RawNimbus {

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(RawNimbus.class
            .getName());
    
    private JComponent createContent() {
        JTabbedPane pane = new JTabbedPane();
        JTable table = new JTable(new SomeData());
        Color selectionBackground = table.getSelectionBackground();
        LOG.info("ui resource? " + (selectionBackground instanceof UIResource) + selectionBackground);
        table.setSelectionBackground(new ColorUIResource(selectionBackground));
//        table.setSelectionBackground(new ColorUIResource(Color.RED));
//        table.setSelectionBackground(Color.RED);
        JLabel tableLabel = new JLabel("background from table");
        tableLabel.setOpaque(true);
        tableLabel.setBackground(table.getBackground());
//        final JLabel label = new JLabel("table background");
        final JCheckBox label = new JCheckBox("table background");
        label.setName("Table.background");
//        label.setOpaque(true);
        label.setBackground(UIManager.getColor(label.getName()));
        final JLabel alternate = new JLabel("table alternate row");
        alternate.setName("Table.alternateRowColor");
        alternate.setOpaque(true);
        alternate.setBackground(UIManager.getColor(alternate.getName()));
        JComponent panel =  Box.createVerticalBox();
        panel.setBackground(Color.RED);
        panel.setOpaque(true);
        panel.add(label);
        panel.add(alternate);
        panel.add(table); //new JScrollPane(table));
        panel.add(createButtonBar());
        pane.addTab("JTable" , panel);
        JList list = new JList(new DefaultComboBoxModel(new Object[] {"just", "some", "elements"}));
        pane.addTab("JList", new JScrollPane(list));
        JTree tree = new JTree();
        pane.addTab("JTree", new JScrollPane(tree));

        return pane;
    }
    
    /**
     * @return
     */
    private JComponent createButtonBar() {
        JComponent bar = new JPanel();
        Action laf = new AbstractAction("toggle LAF (metal/nimbus") {
            boolean isMetal = false;

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (isMetal) {
                        setLookAndFeel("Nimbus");
                    } else {
                        setLookAndFeel("Metal");
                    }
                    isMetal = !isMetal;
                    SwingXUtilities.updateAllComponentTreeUIs();
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

            }
        };
        bar.add(new JButton(laf));
        Action remove = new AbstractAction("remove alternate color") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                Object alternate = UIManager.getLookAndFeelDefaults().remove("Table.alternateRowColor");
                LOG.info("removed? got color " + UIManager.getColor("Table.alternateRowColor") + alternate);
                SwingXUtilities.updateAllComponentTreeUIs();
            }
        };
        bar.add(new JButton(remove));
        return bar;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    setLookAndFeel("Nimbus");
                    Object alternate = UIManager.getLookAndFeelDefaults().remove("Table.alternateRowColor");
                    LOG.info("removed? got color " + UIManager.getColor("Table.alternateRowColor") + alternate);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                JFrame frame = new JFrame("");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(new RawNimbus().createContent());
                frame.setLocationRelativeTo(null);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    public static void setLookAndFeel(String nameSnippet) throws Exception {
        LookAndFeelInfo[] plafs = UIManager.getInstalledLookAndFeels();
        for (LookAndFeelInfo info : plafs) {
            if (info.getName().contains(nameSnippet)) {
                UIManager.setLookAndFeel(info.getClassName());
                return;
            }
        }
        throw new UnsupportedLookAndFeelException("no LAF installed with name snippet " + nameSnippet);
    }

    public static class SomeData extends AbstractTableModel {
        protected final String[] names = { "First Name", "Last Name", "Favorite Color",
                "No.", "Vegetarian" };
        protected final Object[][] data = {
                { "Mark", "Andrews", Color.red, new Integer(2), new Boolean(true) },
                { "Tom", "Ball", Color.blue, new Integer(99), new Boolean(false) },
                { "Alan", "Chung", Color.green, new Integer(838), new Boolean(false) },
                { "Jeff", "Dinkins", Color.cyan, new Integer(8), new Boolean(true) },
                { "Amy", "Fowler", Color.yellow, new Integer(3), new Boolean(false) },
                { "Brian", "Gerhold", Color.green, new Integer(0), new Boolean(false) },
                { "James", "Gosling", Color.magenta, new Integer(21), new Boolean(false) },
                { "David", "Karlton", Color.red, new Integer(1), new Boolean(false) },
                { "Dave", "Kloba", Color.yellow, new Integer(14), new Boolean(false) },
                { "Peter", "Korn", Color.orange, new Integer(12), new Boolean(false) },
                { "Phil", "Milne", Color.magenta, new Integer(3), new Boolean(false) },
                { "Dave", "Moore", Color.green, new Integer(88), new Boolean(false) },
                { "Hans", "Muller", Color.red, new Integer(5), new Boolean(false) },

                { "Rick", "Levenson", Color.blue, new Integer(2), new Boolean(false) },
                { "Tim", "Prinzing", Color.blue, new Integer(22), new Boolean(false) },
                { "Chester", "Rose", Color.black, new Integer(0), new Boolean(false) },
                { "Ray", "Ryan", Color.gray, new Integer(77), new Boolean(false) },
                { "Georges", "Saab", Color.red, new Integer(4), new Boolean(false) },
                { "Willie", "Walker", Color.blue, new Integer(4),
                        new Boolean(false) },

                { "Kathy", "Walrath", Color.blue, new Integer(8), new Boolean(false) },
                { "Arnaud", "Weber", Color.green, new Integer(44), new Boolean(false) } };

        public int getColumnCount() {
            return names.length;
        }

        public int getRowCount() {
            return data.length;
        }
        /** everything is editable. */
        @Override
        public boolean isCellEditable(int row, int col) {
            return true;
        }
        
        public Object getValueAt(int row, int col) {
            // following shows only every second value
            // if ((row + col) % 2 == 0) return null;
            return data[row % data.length][col];
        }
        @Override
        public void setValueAt(Object value, int row, int col) {
            data[row % data.length][col] = value;
            fireTableCellUpdated(row, col);
        }
        @Override
        public String getColumnName(int column) {
            return names[column];
        }

        /** returns class of column by asking class of value in first row. */
        @Override
        public Class<?> getColumnClass(int c) {
            Object value = null;
            if (getRowCount() > 0) {
                value = getValueAt(0, c);
            }
            if (value == null) {
                return Object.class;
            }
            return value.getClass();
        }

    }
}
