/*
 * $Id: XLabelDemo.java 4097 2011-11-30 19:22:13Z kschaefe $
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
 */
package org.jdesktop.swingx.demos.xlabel;

import static org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXLabel.TextAlignment;
import org.jdesktop.swingx.combobox.EnumComboBoxModel;
import org.jdesktop.swingx.renderer.DefaultListRenderer;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.renderer.StringValues;
import org.jdesktop.swingxset.DefaultDemoPanel;

import com.sun.swingset3.DemoProperties;

/**
 * A demo for the {@code JXLabel}.
 *
 * @author Karl George Schaefer
 * @author rah003 (original JXLabelDemo)
 * @author Richard Bair (original JXLabelDemo)
 */
@DemoProperties(
    value = "JXLabel Demo",
    category = "Controls",
    description = "Demonstrates JXLabel, a Painter-enabled multiline label component.",
    sourceFiles = {
        "org/jdesktop/swingx/demos/xlabel/XLabelDemo.java",
        "org/jdesktop/swingx/demos/xlabel/resources/XLabelDemo.properties",
        "org/jdesktop/swingx/demos/xlabel/resources/XLabelDemo.html",
        "org/jdesktop/swingx/demos/xlabel/resources/images/XLabelDemo.png",
        "org/jdesktop/swingx/demos/xlabel/resources/images/exit.png"
    }
)
@SuppressWarnings("serial")
public class XLabelDemo extends DefaultDemoPanel {
    private JXLabel label;
    private JCheckBox lineWrap;
    private JComboBox alignments;
    private JButton rotate;
    
    /**
     * main method allows us to run as a standalone demo.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame(XLabelDemo.class.getAnnotation(DemoProperties.class).value());
                
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(new XLabelDemo());
                frame.setPreferredSize(new Dimension(800, 600));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    protected void createDemo() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        
        label = new JXLabel();
        label.setName("contents");
        add(label);
        
        JPanel p = new JPanel();
        add(p, BorderLayout.SOUTH);
        
        lineWrap = new JCheckBox();
        lineWrap.setName("lineWrap");
        p.add(lineWrap);
        
        alignments = new JComboBox(new EnumComboBoxModel<TextAlignment>(TextAlignment.class));
        alignments.setRenderer(new DefaultListRenderer(new StringValue() {
            public String getString(Object value) {
                String s = StringValues.TO_STRING.getString(value);
                
                if (s.length() > 1) {
                    String lc = s.toLowerCase();
                    s = s.charAt(0) + lc.substring(1);
                }
                
                return s;
            }
        }));
        p.add(alignments);
        
        rotate = new JButton();
        rotate.setName("rotate");
        p.add(rotate);
    }
    
    /**
     * {@inheritDoc}
     */
    protected void bind() {
        Bindings.createAutoBinding(READ, lineWrap, BeanProperty.create("selected"),
                label, BeanProperty.create("lineWrap")).bind();
        Bindings.createAutoBinding(READ, alignments, BeanProperty.create("selectedItem"),
                label, BeanProperty.create("textAlignment")).bind();
        //TODO build a converter to handle this via BeanBinding
        rotate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                label.setTextRotation((label.getTextRotation() + Math.PI / 16) % (2 * Math.PI));
            }
        });
    }
}
