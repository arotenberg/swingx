/*
 * $Id: AutoCompleteDemo.java 4097 2011-11-30 19:22:13Z kschaefe $
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
package org.jdesktop.swingx.demos.autocomplete;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.jdesktop.application.Application;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.jdesktop.swingx.combobox.ListComboBoxModel;
import org.jdesktop.swingxset.DefaultDemoPanel;

import com.sun.swingset3.DemoProperties;

/**
 * A demo for the {@code AutoCompleteDecorator}.
 * 
 * @author Karl George Schaefer
 * @author  Thomas Bierhance (original AutoCompleteDemoPanel)
 */
@DemoProperties(
    value = "AutoComplete Demo",
    category = "Functionality",
    description = "Demonstrates AutoComplete, a decorator that automatically completes selections",
    sourceFiles = {
        "org/jdesktop/swingx/demos/autocomplete/AutoCompleteDemo.java",
        "org/jdesktop/swingx/demos/autocomplete/Airport.java",
        "org/jdesktop/swingx/demos/autocomplete/Airports.java",
        "org/jdesktop/swingx/demos/autocomplete/AirportConverter.java",
        "org/jdesktop/swingx/demos/autocomplete/resources/AutoCompleteDemo.properties",
        "org/jdesktop/swingx/demos/autocomplete/resources/AutoCompleteDemo.html"
    }
)
@SuppressWarnings("serial")
public class AutoCompleteDemo extends DefaultDemoPanel {
    
    private List<String> names;
    
    private JComboBox airportComboBox;
    private JList list;
    private JComboBox nonStrictComboBox;
    private JTextField nonStrictTextField;
    private JComboBox strictComboBox;
    private JTextField strictTextField;
    private JTextField textFieldForList;
    
    /**
     * main method allows us to run as a standalone demo.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame(AutoCompleteDemo.class.getAnnotation(DemoProperties.class).value());
                
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(new AutoCompleteDemo());
                frame.setPreferredSize(new Dimension(800, 600));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
    
    
    public AutoCompleteDemo() {
        decorate();
    }
    
    /**
     * {@inheritDoc}
     */
    protected void createDemo() {
        setLayout(new GridBagLayout());
        
        GridBagConstraints gridBagConstraints;

        strictComboBox = new JComboBox();
        nonStrictComboBox = new JComboBox();
        strictTextField = new JTextField();
        nonStrictTextField = new JTextField();
        textFieldForList = new JTextField();
        list = new JList();
        airportComboBox = new JComboBox();

        JLabel strictComboBoxLabel = new JLabel();
        strictComboBoxLabel.setName("strictComboBoxLabel");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(3, 3, 3, 3);
        add(strictComboBoxLabel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new Insets(3, 3, 3, 3);
        add(strictComboBox, gridBagConstraints);

        JLabel nonStrictComboBoxLabel = new JLabel();
        nonStrictComboBoxLabel.setName("nonStrictComboBoxLabel");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(3, 3, 3, 3);
        add(nonStrictComboBoxLabel, gridBagConstraints);

        nonStrictComboBox.setEditable(true);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new Insets(3, 3, 3, 3);
        add(nonStrictComboBox, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new Insets(10, 0, 10, 0);
        add(new JSeparator(), gridBagConstraints);

        JLabel strictTextFieldLabel = new JLabel();
        strictTextFieldLabel.setName("strictTextFieldLabel");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(3, 3, 3, 3);
        add(strictTextFieldLabel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new Insets(3, 3, 3, 3);
        add(strictTextField, gridBagConstraints);

        JLabel nonStrictTextFieldLabel = new JLabel();
        nonStrictTextFieldLabel.setName("nonStrictTextFieldLabel");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(3, 3, 3, 3);
        add(nonStrictTextFieldLabel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new Insets(3, 3, 3, 3);
        add(nonStrictTextField, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new Insets(10, 0, 10, 0);
        add(new JSeparator(), gridBagConstraints);

        JLabel textFieldForListLabel = new JLabel();
        textFieldForListLabel.setName("textFieldForListLabel");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(3, 3, 3, 3);
        add(textFieldForListLabel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new Insets(3, 3, 3, 3);
        add(textFieldForList, gridBagConstraints);

        JLabel listLabel = new JLabel();
        listLabel.setName("listLabel");
        
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(3, 3, 3, 3);
        add(listLabel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new Insets(3, 3, 3, 3);
        add(new JScrollPane(list), gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new Insets(10, 0, 10, 0);
        add(new JSeparator(), gridBagConstraints);

        JLabel airportLabel = new JLabel();
        airportLabel.setName("airportLabel");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(3, 3, 3, 3);
        add(airportLabel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new Insets(3, 3, 3, 3);
        add(airportComboBox, gridBagConstraints);
    }
    
    protected void injectResources() {
        super.injectResources();
        
        String s = Application.getInstance().getContext().getResourceMap(getClass()).getString("names");
        //prevent changes; we're sharing the list among several models
        names = Collections.unmodifiableList(Arrays.asList(s.split(",")));
    }
    
    /**
     * {@inheritDoc}
     */
    protected void bind() {
        strictComboBox.setModel(new ListComboBoxModel<String>(names));
        nonStrictComboBox.setModel(new ListComboBoxModel<String>(names));
        airportComboBox.setModel(new ListComboBoxModel<Airport>(Airports.ALL_AIRPORTS));
        //use the combo box model because it's SwingX
        list.setModel(new ListComboBoxModel<String>(names));
    }
    
    private void decorate() {
        AutoCompleteDecorator.decorate(strictComboBox);
        AutoCompleteDecorator.decorate(nonStrictComboBox);
        
        AutoCompleteDecorator.decorate(strictTextField, names, true);
        AutoCompleteDecorator.decorate(nonStrictTextField, names, false);
        
        AutoCompleteDecorator.decorate(list, textFieldForList);
        
        AutoCompleteDecorator.decorate(airportComboBox, new AirportConverter());
    }
}
