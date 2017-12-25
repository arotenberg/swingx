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
package org.jdesktop.swingx.plaf.synth;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.plaf.synth.SynthLookAndFeel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.JXTable;

/**
 * Issue 1188-swingx: background color of JXTable incorrect for SynthLAF
 */
public class SynthTableColors extends JFrame {
    private static String synthXml = "<synth>" + "  <style id=\"all\">"
            + "    <font name=\"Tahoma\" size=\"11\"/>" + "  </style>"
            + "  <bind style=\"all\" type=\"REGION\" key=\".*\"/>"
            + "  <style id=\"table\">" + "    <state>"
            + "      <color type=\"BACKGROUND\" value=\"#FFE0E0\" />"
            + "      <color type=\"FOREGROUND\" value=\"#000000\" />"
            + "    </state>" + "    <state value=\"SELECTED\">"
            + "      <color type=\"TEXT_BACKGROUND\" value=\"#0070C0\"/>"
            + "    <color type=\"TEXT_FOREGROUND\" value=\"#FFFFFF\"/>"
            + "    </state>" + "  </style>"
            + "  <bind style=\"table\" type=\"region\" key=\"Table\"/>"
            + "</synth>";

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new SynthTableColors();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public SynthTableColors() throws Exception {
        setTitle(System.getProperty("java.version"));
        InputStream is = new ByteArrayInputStream(synthXml.getBytes("UTF8"));
        SynthLookAndFeel laf = new SynthLookAndFeel();
        laf.load(is, SynthTableColors.class);
        UIManager.setLookAndFeel(laf);

        JTable table = new JTable();
        configureTable(table);

        JXTable xtable = new JXTable();
        configureTable(xtable);
        add(createTab(table, xtable));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * @param title
     * @param core
     * @param xcomp
     * @return
     */
    private JComponent createTab(JComponent core, JComponent xcomp) {
        JComponent comp = new JPanel(new BorderLayout());
        comp.add(new JScrollPane(core), BorderLayout.WEST);
        comp.add(new JScrollPane(xcomp), BorderLayout.EAST);
        return comp;
    }

    /**
     * @param table
     */
    private void configureTable(JTable table) {
        // model with boolean to see a checkbox as renderer
        TableModel model = new DefaultTableModel(10, 5) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }
                return super.getColumnClass(columnIndex);
            }
            
        };
        table.setModel(model);
        // use a renderer which is not under the control of synth
        table.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer());
        // have a portion of table not painted by renderers
        table.setFillsViewportHeight(true);
        // Issue: background color does not appear because
        // JLabelRenderer#isOpaque
        // returns false - as a result the default table color appears painted
        // by
        // ComponentUI#update(...)
        table.setBackground(Color.ORANGE);

        // very hacky workaround - not recommended - see
        // JLabelRenderer#isOpaque()
        // table.setOpaque(false);

        // another workaround
        // table.setUI(new BasicTableUI());
    }
}  
