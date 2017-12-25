/*
 * $Id: DatePickerDemo.java 4097 2011-11-30 19:22:13Z kschaefe $
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
package org.jdesktop.swingx.demos.datepicker;

import static org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;

import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledSeparator;
import org.jdesktop.swingx.binding.ComponentOrientationConverter;
import org.jdesktop.swingx.binding.LabelHandler;
import org.jdesktop.swingxset.util.DemoUtils;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.sun.swingset3.DemoProperties;

/**
 * A demo for the {@code JXDatePicker}.
 *
 * @author Karl George Schaefer
 * @author Richard Bair (original JXDatePickerDemoPanel)
 */
@DemoProperties(
    value = "JXDatePicker Demo",
    category = "Controls",
    description = "Demonstrates JXDatePicker, a control which allows the user to select a date",
    sourceFiles = {
        "org/jdesktop/swingx/demos/datepicker/DatePickerDemo.java",
        "org/jdesktop/swingx/demos/datepicker/resources/DatePickerDemo.properties",
        "org/jdesktop/swingx/binding/ComponentOrientationConverter.java"
    }
)
@SuppressWarnings("serial")
public class DatePickerDemo extends JXPanel {
    private JXDatePicker datePicker;
    
    private JCheckBox interactivity;
    private JCheckBox editability;
    private JCheckBox orientation;

    private JFormattedTextField dateEchoField;
    
    
    public DatePickerDemo() {
        initComponents();
        configureComponents();
        DemoUtils.injectResources(this);
        bind();
    }
 
//------------------------ bind    
    
    private void configureComponents() {
        dateEchoField.setEditable(false);
        AbstractFormatter formatter = new DateFormatter(DateFormat.getDateTimeInstance());
        AbstractFormatterFactory tf = new DefaultFormatterFactory(formatter);
        dateEchoField.setFormatterFactory(tf);
    }
    
    @SuppressWarnings("unchecked")
    private void bind() {
        
        BindingGroup group = new BindingGroup();
        group.addBinding(Bindings.createAutoBinding(READ, 
                datePicker, BeanProperty.create("date"),
                dateEchoField, BeanProperty.create("value")
        ));

        group.addBinding(Bindings.createAutoBinding(READ,
            interactivity, BeanProperty.create("selected"),
            datePicker, BeanProperty.create("enabled")
        ));
        
        group.addBinding(Bindings.createAutoBinding(READ,
            editability, BeanProperty.create("selected"),
            datePicker, BeanProperty.create("editable")
        ));
        
        Binding b = Bindings.createAutoBinding(READ,
            orientation, BeanProperty.create("selected"),
            datePicker, BeanProperty.create("componentOrientation"));
        b.setConverter(new ComponentOrientationConverter());
        group.addBinding(b);
        group.bind();
    }

//------------------- inti ui
    
    private void initComponents() {
        setLayout(new BorderLayout());
        

        JPanel monthViewContainer = new JXPanel();
        FormLayout formLayout = new FormLayout(
                "5dlu, r:d:g, l:4dlu:n, f:d:g", // columns
                "c:d:n " +
                ", t:4dlu:n, c:d:n" +
                ", t:4dlu:n, c:d:n" +
                ", t:4dlu:n, c:d:n"
        ); // rows
        PanelBuilder builder = new PanelBuilder(formLayout, monthViewContainer);
        builder.setBorder(Borders.DLU4_BORDER);
        CellConstraints cl = new CellConstraints();
        CellConstraints cc = new CellConstraints();
        
        JXTitledSeparator areaSeparator = new JXTitledSeparator();
        areaSeparator.setName("listSeparator");
        builder.add(areaSeparator, cc.xywh(1, 1, 4, 1));

        int labelColumn = 2;
        int widgetColumn = labelColumn + 2;
        int currentRow = 3;

        //creates a new picker and sets the current date to today
        datePicker = new JXDatePicker(new Date());
        datePicker.setName("datePicker");
        JLabel datePickerLabel = builder.addLabel(
                "", cl.xywh(labelColumn, currentRow, 1, 1),
                datePicker, cc.xywh(widgetColumn, currentRow, 1, 1));
        datePickerLabel.setName("datePickerLabel");
        LabelHandler.bindLabelFor(datePickerLabel, datePicker);
        currentRow += 2;
        
        dateEchoField = new JFormattedTextField();
        dateEchoField.setName("dateEchoField");
        JLabel dateEchoLabel = builder.addLabel(
                "", cl.xywh(labelColumn, currentRow, 1, 1),
                dateEchoField, cc.xywh(widgetColumn, currentRow, 1, 1));
        dateEchoLabel.setName("dateEchoLabel");
        LabelHandler.bindLabelFor(dateEchoLabel, dateEchoField);
        currentRow += 2;
        
        add(monthViewContainer, BorderLayout.CENTER);
        
        JComponent panel = createControlPanel();
        
        add(panel, BorderLayout.SOUTH);
    }

    /**
     * @return
     */
    private JComponent createControlPanel() {
        JPanel painterControl = new JXPanel();

        FormLayout formLayout = new FormLayout(
                "5dlu, r:d:n, l:4dlu:n, f:d:g", // , l:4dlu:n, f:d:g", // columns
                "c:d:n " +
                ", t:4dlu:n, c:d:n " +
                ", t:4dlu:n, c:d:n" +
                ", t:4dlu:n, c:d:n" +
                ", t:4dlu:n, c:d:n" +
                ", t:4dlu:n, c:d:n" +
                ", t:4dlu:n, c:d:n" +
                ", t:4dlu:n, c:d:n"
        ); // rows
        PanelBuilder builder = new PanelBuilder(formLayout, painterControl);
        builder.setBorder(Borders.DLU4_BORDER);
        CellConstraints cl = new CellConstraints();
        CellConstraints cc = new CellConstraints();
        
        JXTitledSeparator areaSeparator = new JXTitledSeparator();
        areaSeparator.setName("propertySeparator");
        builder.add(areaSeparator, cc.xywh(1, 1, 4, 1));
        
        int labelColumn = 2;
        int widgetColumn = labelColumn + 2;
        int currentRow = 3;


        
        interactivity = new JCheckBox();
        interactivity.setName("interactivity");
        builder.add(interactivity, cc.xywh(labelColumn, currentRow, 3, 1));
        currentRow += 2;

//        painterControl.add(interactivity);
        
        editability = new JCheckBox();
        editability.setName("editability");
        builder.add(editability, cc.xywh(labelColumn, currentRow, 3, 1));
        currentRow += 2;
        
//        painterControl.add(editability);
        
        orientation = new JCheckBox();
        orientation.setName("orientation");

        builder.add(orientation, cc.xywh(labelColumn, currentRow, 3, 1));
        currentRow += 2;

        //should be able to set this from properties file
        orientation.setSelected(!datePicker.getComponentOrientation().isLeftToRight());
//        painterControl.add(orientation);
        return painterControl;
    }

    /**
     * main method allows us to run as a standalone demo.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame(DatePickerDemo.class.getAnnotation(DemoProperties.class).value());
                
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(new DatePickerDemo());
                frame.setPreferredSize(new Dimension(800, 600));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

}
