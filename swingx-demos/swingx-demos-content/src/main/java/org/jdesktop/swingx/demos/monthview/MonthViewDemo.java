/*
 * $Id: MonthViewDemo.java 4097 2011-11-30 19:22:13Z kschaefe $
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
package org.jdesktop.swingx.demos.monthview;

import static org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.JSpinner.DefaultEditor;

import org.jdesktop.application.Application;
import org.jdesktop.beans.AbstractBean;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.Converter;
import org.jdesktop.swingx.JXCollapsiblePane;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXMonthView;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledSeparator;
import org.jdesktop.swingx.binding.LabelHandler;
import org.jdesktop.swingx.calendar.CalendarUtils;
import org.jdesktop.swingx.calendar.DateSelectionModel.SelectionMode;
import org.jdesktop.swingx.combobox.EnumComboBoxModel;
import org.jdesktop.swingx.demos.monthview.MonthViewDemoUtils.DayOfWeekConverter;
import org.jdesktop.swingx.renderer.DefaultListRenderer;
import org.jdesktop.swingx.renderer.FormatStringValue;
import org.jdesktop.swingxset.util.DisplayValues;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.sun.swingset3.DemoProperties;

/**
 * A demo for the {@code JXMonthView}.
 *
 * @author Karl George Schaefer
 * @author Joshua Outwater (original JXMonthViewDemoPanel)
 */
@DemoProperties(
    value = "JXMonthView (basic)",
    category = "Controls",
    description = "Demonstrates JXMonthView, a monthly calendar display.",
    sourceFiles = {
        "org/jdesktop/swingx/demos/monthview/MonthViewDemo.java",
        "org/jdesktop/swingx/demos/monthview/MonthViewDemoUtils.java",
        "org/jdesktop/swingx/demos/monthview/resources/MonthViewDemo.properties",
        "org/jdesktop/swingx/demos/monthview/resources/MonthViewDemo.html",
        "org/jdesktop/swingx/demos/monthview/resources/images/MonthViewDemo.png"
    }
)
@SuppressWarnings("serial")
public class MonthViewDemo extends JPanel {
    
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(MonthViewDemo.class
            .getName());
    
    private JXMonthView monthView;

    private JComboBox selectionModes;
    private JCheckBox traversable;
    private JComboBox dayOfWeekComboBox;

    private JCheckBox leadingDaysBox;
    private JCheckBox trailingDaysBox;
    private JSpinner prefColumnSlider;
    private JCheckBox weekNumberBox;
    private JSpinner prefRowSlider;
    private JXDatePicker flaggedDates;

    private JXDatePicker unselectableDates;

    private JXDatePicker upperBound;

    private JXDatePicker lowerBound;
    
//---------------------- bind
    
    @SuppressWarnings("unchecked")
    private void bind() {
        new MonthViewDemoControl();
        
        // PENDING JW: re-visit distribution of binding control ...
        // this is quite arbitrary (time of coding ;-)
        BindingGroup group = new BindingGroup();
        group.addBinding(Bindings.createAutoBinding(READ, 
                leadingDaysBox, BeanProperty.create("selected"),
                monthView, BeanProperty.create("showingLeadingDays")));
        group.addBinding(Bindings.createAutoBinding(READ, 
                trailingDaysBox, BeanProperty.create("selected"),
                monthView, BeanProperty.create("showingTrailingDays")));
        
        group.addBinding(Bindings.createAutoBinding(READ, 
                weekNumberBox, BeanProperty.create("selected"),
                monthView, BeanProperty.create("showingWeekNumber")));

        group.addBinding(Bindings.createAutoBinding(READ, 
                prefColumnSlider, BeanProperty.create("value"),
                monthView, BeanProperty.create("preferredColumnCount")));
        
        group.addBinding(Bindings.createAutoBinding(READ, 
                prefRowSlider, BeanProperty.create("value"),
                monthView, BeanProperty.create("preferredRowCount")));
        
        group.bind();
        
    }

//--------------------- MonthViewDemoControl
    public class MonthViewDemoControl extends AbstractBean {
        
        private Date lastFlagged;
        private Date lastUnselectable;
        private Date upper;
        private Date lower;
        
        
        @SuppressWarnings("unchecked")
        public MonthViewDemoControl() {
            selectionModes.setModel(new EnumComboBoxModel<SelectionMode>(SelectionMode.class));
            selectionModes.setRenderer(new DefaultListRenderer(DisplayValues.TITLE_WORDS_UNDERSCORE));

            // PENDING JW: this does not survive a change in Locale - 
            // revisit if we add changing Locale to the demo
            Calendar calendar = monthView.getCalendar();
            // start of week == first day of week in the calendar's coordinate space
            CalendarUtils.startOfWeek(calendar);
            DefaultComboBoxModel model = new DefaultComboBoxModel();
            for (int i = 0; i < 7; i++) {
                model.addElement(calendar.getTime());
                calendar.add(Calendar.DATE, 1);
            }
            dayOfWeekComboBox.setModel(model);
            SimpleDateFormat format = new SimpleDateFormat("EEEE");
            dayOfWeekComboBox.setRenderer(new DefaultListRenderer(
                    new FormatStringValue(format)));
            Converter<?, ?> days = new DayOfWeekConverter(calendar);
            
            BindingGroup group = new BindingGroup();
            group.addBinding(Bindings.createAutoBinding(READ, 
                    selectionModes, BeanProperty.create("selectedItem"),
                    monthView, BeanProperty.create("selectionMode")));
            
            group.addBinding(Bindings.createAutoBinding(READ, 
                    traversable, BeanProperty.create("selected"),
                    monthView, BeanProperty.create("traversable")));
            
            Binding dayOfWeek = Bindings.createAutoBinding(READ, 
                    dayOfWeekComboBox, BeanProperty.create("selectedItem"),
                    monthView, BeanProperty.create("firstDayOfWeek"));
            dayOfWeek.setConverter(days);
            group.addBinding(dayOfWeek);
            
            Binding flagged = Bindings.createAutoBinding(READ, 
                    flaggedDates, BeanProperty.create("date"),
                    this, BeanProperty.create("lastFlagged"));
            group.addBinding(flagged);
            
            Binding unselectable = Bindings.createAutoBinding(READ, 
                    unselectableDates, BeanProperty.create("date"),
                    this, BeanProperty.create("lastUnselectable"));
            group.addBinding(unselectable);
            
            group.addBinding(Bindings.createAutoBinding(READ, 
                    upperBound, BeanProperty.create("date"),
                    this, BeanProperty.create("upperBound")));

            group.addBinding(Bindings.createAutoBinding(READ, 
                    lowerBound, BeanProperty.create("date"),
                    this, BeanProperty.create("lowerBound")));
            
            group.bind();
            
            // PENDING JW: removed the color selection stuff for now
            // future will be to use highlighters anyway - revisit then
        }
        /**
         * @return the lastFlagged
         */
        public Date getLastFlagged() {
            return lastFlagged;
        }
        /**
         * @param lastFlagged the lastFlagged to set
         */
        public void setLastFlagged(Date lastFlagged) {
            Date old = getLastFlagged();
            this.lastFlagged = lastFlagged;
            updateFlaggedDates();
            firePropertyChange("lastFlagged", old, getLastFlagged());
        }
        
        /**
         * 
         */
        private void updateFlaggedDates() {
            // PENDING JW: should be handled by converter
            // not working - "flaggedDates" is not a real property because different
            // types in getter/setter
            if (getLastFlagged() == null) {
                monthView.setFlaggedDates();
                return;
            }
            Set<Date> old = monthView.getFlaggedDates();
            Date[] flagged = new Date[old.size() + 1];
            int index = 0;
            for (Date d : old) {
                flagged[index++] = d;
            }
            flagged[index] = getLastFlagged();
            monthView.setFlaggedDates(flagged);
        }
        
        
        /**
         * @return the lastUnselectable
         */
        public Date getLastUnselectable() {
            return lastUnselectable;
        }
        /**
         * @param lastUnselectable the lastUnselectable to set
         */
        public void setLastUnselectable(Date lastUnselectable) {
            Date old = getLastUnselectable();
            this.lastUnselectable = lastUnselectable;
            updateLastUnselectable();
            firePropertyChange("lastUnselectable", old, getLastUnselectable());
        }
        
        /**
         * 
         */
        private void updateLastUnselectable() {
            // JW: can't bind directly - it's not a property
            if (getLastUnselectable() == null) {
                monthView.setUnselectableDates();
                return;
            }
            Set<Date> old = monthView.getSelectionModel().getUnselectableDates();
            SortedSet<Date> result = new TreeSet<Date>(old);
            result.add(getLastUnselectable());
            monthView.getSelectionModel().setUnselectableDates(result);
        }
        /**
         * @param lower the lower to set
         */
        public void setLowerBound(Date lower) {
            Date old = getLowerBound();
            this.lower = lower;
            monthView.setLowerBound(lower);
            firePropertyChange("lowerBound", old, getLowerBound());
        }
        /**
         * @return the lower
         */
        public Date getLowerBound() {
            return lower;
        }
        /**
         * @param upper the upper to set
         */
        public void setUpperBound(Date upper) {
            Date old = getUpperBound();
            this.upper = upper;
            monthView.setUpperBound(upper);
            firePropertyChange("upperBound", old, getUpperBound());
        }
        /**
         * @return the upper
         */
        public Date getUpperBound() {
            return upper;
        }
        
        
    }

//--------------------- create ui
    

    private void createMonthViewDemo() {
        monthView = new JXMonthView();
        monthView.setName("monthView");
        
        // add to container which doesn't grow the size beyond the pref
        JComponent monthViewContainer = new JXPanel();
        monthViewContainer.add(monthView);
        
        JPanel monthViewControlPanel = new JXPanel();
        add(monthViewControlPanel, BorderLayout.SOUTH);

        FormLayout formLayout = new FormLayout(
                "f:m:g, l:4dlu:n, f:m:g", // columns
                "c:d:g, t:2dlu:n, t:d:n "
        ); // rows
        PanelBuilder builder = new PanelBuilder(formLayout, this);
        builder.setBorder(Borders.DLU4_BORDER);
        CellConstraints cc = new CellConstraints();
        
        builder.add(monthViewContainer, cc.xywh(1, 1, 3, 1));
        
        builder.add(createBoxPropertiesPanel(), cc.xywh(1, 3, 1, 1));
        builder.add(createConfigPanel(), cc.xywh(3, 3, 1, 1));
    }
    
    /**
     * @return
     */
    private JComponent createConfigPanel() {
        JXCollapsiblePane painterControl = new JXCollapsiblePane();
        FormLayout formLayout = new FormLayout(
                "5dlu, r:d:n, l:4dlu:n, f:m:g, l:4dlu:n, f:m:g", // columns
                "c:d:n " +
                ", t:4dlu:n, c:d:n " +
                ", t:4dlu:n, c:d:n" +
                ", t:4dlu:n, c:d:n" +
                ", t:4dlu:n, c:d:n"
        ); // rows
        PanelBuilder builder = new PanelBuilder(formLayout, painterControl);
        builder.setBorder(Borders.DLU4_BORDER);
        CellConstraints cl = new CellConstraints();
        CellConstraints cc = new CellConstraints();
        
        JXTitledSeparator areaSeparator = new JXTitledSeparator();
        areaSeparator.setName("configurationSeparator");
        builder.add(areaSeparator, cc.xywh(1, 1, 6, 1));
        
        int labelColumn = 2;
        int widgetColumn = labelColumn + 2;
        int currentRow = 3;

        dayOfWeekComboBox = new JComboBox();
        JLabel dayOfWeekLabel = builder.addLabel("", cl.xywh(labelColumn, currentRow, 1, 1), 
                dayOfWeekComboBox, cc.xywh(widgetColumn, currentRow, 3, 1));
        dayOfWeekLabel.setName("dayOfWeekLabel");
        LabelHandler.bindLabelFor(dayOfWeekLabel, dayOfWeekComboBox);
        currentRow += 2;

        selectionModes = new JComboBox();
        JLabel insets = builder.addLabel("", cl.xywh(labelColumn, currentRow, 1, 1), 
                selectionModes, cc.xywh(widgetColumn, currentRow, 3, 1));
        currentRow += 2;
        insets.setName("selectionModesLabel");
        LabelHandler.bindLabelFor(insets, selectionModes);

        
        unselectableDates = new JXDatePicker();
        JLabel unselectables = builder.addLabel("", cl.xywh(labelColumn, currentRow, 1, 1), 
                unselectableDates, cc.xywh(widgetColumn, currentRow, 1, 1));
        unselectables.setName("unselectableDatesLabel");
        LabelHandler.bindLabelFor(unselectables, unselectableDates);
        flaggedDates = new JXDatePicker();
        builder.add(flaggedDates, cc.xywh(widgetColumn + 2, currentRow, 1, 1));
        currentRow += 2;

        upperBound = new JXDatePicker();
        lowerBound = new JXDatePicker();
        
        JLabel lower = builder.addLabel("", cl.xywh(labelColumn, currentRow, 1, 1), 
                lowerBound, cc.xywh(widgetColumn, currentRow, 1, 1));
        lower.setName("lowerBoundsLabel");
        LabelHandler.bindLabelFor(lower, lowerBound);
        
        builder.add(upperBound, cc.xywh(widgetColumn + 2, currentRow, 1, 1));
        currentRow += 2;
        
        
        return painterControl;
    }




    /**
     * @return
     */
    private JComponent createBoxPropertiesPanel() {
        JXCollapsiblePane painterControl = new JXCollapsiblePane();
        FormLayout formLayout = new FormLayout(
                "5dlu, r:d:n, l:4dlu:n, f:d:n, l:4dlu:n, f:d:n", // columns
                "c:d:n " +
                ", t:4dlu:n, c:d:n " +
                ", t:4dlu:n, c:d:n " +
                ", t:4dlu:n, c:d:n" +
                ", t:4dlu:n, c:d:n" +
                ", t:4dlu:n, c:d:n"
                ); // rows
        PanelBuilder builder = new PanelBuilder(formLayout, painterControl);
        builder.setBorder(Borders.DLU4_BORDER);
        CellConstraints cl = new CellConstraints();
        CellConstraints cc = new CellConstraints();
        
        JXTitledSeparator areaSeparator = new JXTitledSeparator();
        areaSeparator.setName("monthBoxSeparator");
        builder.add(areaSeparator, cc.xywh(1, 1, 6, 1));
        
        int labelColumn = 2;
        int widgetColumn = labelColumn + 2;
        int currentRow = 3;
        prefColumnSlider = createSpinner(1, 3, 2);
        prefColumnSlider.setName("preferredColumnSlider");
        
        prefRowSlider = createSpinner(1, 2, 1);
        prefRowSlider.setName("preferredRowSlider");
        
        JLabel insets = builder.addLabel("", cl.xywh(labelColumn, currentRow, 1, 1), prefRowSlider,
                cc.xywh(widgetColumn, currentRow, 1, 1));
        insets.setName("preferredColumnLabel");
        LabelHandler.bindLabelFor(insets, prefRowSlider);
        builder.add(prefColumnSlider,
                cc.xywh(widgetColumn + 2, currentRow, 1, 1));
        currentRow += 2;
        
        leadingDaysBox = new JCheckBox();
        leadingDaysBox.setName("leadingDaysBox");
        
        JLabel leadingLabel = builder.addLabel("", cl.xywh(labelColumn, currentRow, 1, 1),
                leadingDaysBox, cc.xywh(widgetColumn, currentRow, 1, 1));
        leadingLabel.setName("leadingDaysLabel");
        LabelHandler.bindLabelFor(leadingLabel, leadingDaysBox);
        
        trailingDaysBox = new JCheckBox();
        trailingDaysBox.setName("trailingDaysBox");
        builder.add(trailingDaysBox, cc.xywh(widgetColumn + 2, currentRow, 1, 1));
        currentRow += 2;
        
        
        weekNumberBox = new JCheckBox();
        weekNumberBox.setName("weekNumberBox");
        builder.add(weekNumberBox, cc.xywh(widgetColumn, currentRow, 3, 1));
        currentRow += 2;

        traversable = new JCheckBox();
        traversable.setName("traversable");
        builder.add(traversable, cc.xywh(widgetColumn, currentRow, 3, 1));
        currentRow += 2;

        return painterControl;
    }

    private JSpinner createSpinner(int min, int max, int value) {
        SpinnerModel model = new SpinnerNumberModel(value, min, max, 1);
        JSpinner spinner = new JSpinner(model);
        ((DefaultEditor) spinner.getEditor()).getTextField().setEditable(false);
        return spinner;
        
    }

    /**
     * main method allows us to run as a standalone demo.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame(MonthViewDemo.class.getAnnotation(DemoProperties.class).value());
                
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(new MonthViewDemo());
                frame.setPreferredSize(new Dimension(800, 600));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
    
    public MonthViewDemo() {
        super(new BorderLayout());
        createMonthViewDemo();
        Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(this);
        bind();
    }

    
    
}
