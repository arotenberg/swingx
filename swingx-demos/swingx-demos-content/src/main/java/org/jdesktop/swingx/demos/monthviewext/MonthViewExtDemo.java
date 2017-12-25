/*
 * $Id: MonthViewExtDemo.java 4097 2011-11-30 19:22:13Z kschaefe $
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
package org.jdesktop.swingx.demos.monthviewext;

import static org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ;
import static org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.util.Calendar;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.beans.AbstractBean;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.swingx.JXCollapsiblePane;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXMonthView;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledSeparator;
import org.jdesktop.swingx.binding.DisplayInfo;
import org.jdesktop.swingx.binding.DisplayInfoConverter;
import org.jdesktop.swingx.binding.LabelHandler;
import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.demos.monthviewext.MonthViewExtDemoUtils.DayOfWeekHighlightPredicate;
import org.jdesktop.swingx.painter.ImagePainter;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.plaf.basic.CalendarHeaderHandler;
import org.jdesktop.swingx.plaf.basic.CalendarRenderingHandler;
import org.jdesktop.swingx.plaf.basic.DemoCalendarRenderingHandler;
import org.jdesktop.swingx.plaf.basic.DemoMonthViewUI;
import org.jdesktop.swingx.plaf.basic.SpinningCalendarHeaderHandler;
import org.jdesktop.swingx.renderer.DefaultListRenderer;
import org.jdesktop.swingxset.util.DemoUtils;
import org.jdesktop.swingxset.util.DisplayValues;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jhlabs.image.OpacityFilter;
import com.sun.swingset3.DemoProperties;

/**
 * A demo for the {@code JXMonthView}.
 *
 * @author Karl George Schaefer
 * @author Joshua Outwater (original JXMonthViewDemoPanel)
 */
@DemoProperties(
    value = "JXMonthView (extended)",
    category = "Controls",
    description = "Demonstrates extended JXMonthView features (not yet public)",
    sourceFiles = {
        "org/jdesktop/swingx/demos/monthviewext/MonthViewExtDemo.java",
        "org/jdesktop/swingx/plaf/basic/DemoMonthViewUI.java",
        "org/jdesktop/swingx/plaf/basic/DemoCalendarRenderingHandler.java",
        "org/jdesktop/swingx/demos/monthviewext/MonthViewExtDemoUtils.java",
        "org/jdesktop/swingx/demos/monthviewext/resources/MonthViewExtDemo.properties"
    }
)
@SuppressWarnings("serial")
public class MonthViewExtDemo extends JPanel {
    
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(MonthViewExtDemo.class
            .getName());
    
    private JXMonthView monthView;

    private MonthViewDemoControl monthViewDemoControl;

    private JXFrame calendarFrame;
    private JComponent calendarBox;
    private JComponent zoomableBox;

    private JComboBox customHeaderBox;

    
//---------------------- bind
    
    @SuppressWarnings("unchecked")
    private void bind() {
        monthViewDemoControl = new MonthViewDemoControl();
        
        // PENDING JW: re-visit distribution of binding control ...
        // this is quite arbitrary (time of coding ;-)
        BindingGroup group = new BindingGroup();
        group.bind();
        
    }
    
    /**
     * 
     */
    public void hideCalendarFrame() {
        if (calendarFrame != null) {
            DemoUtils.fadeOutAndDispose(calendarFrame, 1000);
        }
        calendarFrame = null;
        monthViewDemoControl.setCalendarVisible(false);
    }

    /**
     * 
     */
    public void showCalendarFrame() {
        calendarFrame = new JXFrame("Calendar 2010");
        calendarFrame.setName("calendar2010");
        calendarFrame.setDefaultCloseOperation(JXFrame.DO_NOTHING_ON_CLOSE);
        WindowListener l = new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                calendarFrame.removeWindowListener(this);
                hideCalendarFrame();
            }
            
        };
        calendarFrame.addWindowListener(l);
        JXPanel calendar = new JXPanel();
        calendar.setBackground(Color.WHITE);
        Painter<?> painter = createBackgroundPainter();
        calendar.setBackgroundPainter(painter);

        JXMonthView monthView = new JXMonthView();
        Calendar cal = monthView.getCalendar();
        cal.set(Calendar.YEAR, 2010);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        monthView.setFirstDisplayedDay(cal.getTime());
        monthView.setOpaque(false);
        monthView.setPreferredColumnCount(3);
        monthView.setPreferredRowCount(4);
        // old style: set visual property with JXMonthView api
        monthView.setDayForeground(Calendar.SUNDAY, Color.MAGENTA);
        // <snip> Custom CalendarRenderingHandler
        // new style: install a custom renderingHandler 
        // (as client property, because no public api support yet)
        // which allows to add Highlighters
        monthView.putClientProperty(DemoMonthViewUI.RENDERING_HANDLER_KEY, 
                createRenderingHandler());
        // </snip>
        DemoUtils.setSnippet("Custom CalendarRenderingHandler", monthView);
        calendar.add(monthView);
        calendarFrame.add(calendar);
        Application application = Application.getInstance(Application.class);
        if (application instanceof SingleFrameApplication) {
            ((SingleFrameApplication) application).show(calendarFrame);
        } else {
            calendarFrame.pack();
            calendarFrame.setLocationRelativeTo(this);
            calendarFrame.setVisible(true);
        }
    }


    /**
     * @return
     */
    private Painter<?> createBackgroundPainter() {
        ImagePainter painter = null;
        try {
            BufferedImage img = ImageIO.read(getClass().
                    getResourceAsStream("resources/images/demo_duke.png"));
            painter = new ImagePainter(img);
            painter.setFilters(new OpacityFilter(10));
            painter.setHorizontalRepeat(true);
            painter.setVerticalRepeat(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return painter;
    }


    
    /**
     * Creates and returns a RenderingHandler which supports adding
     * Highlighters.
     *  
     * @return
     */
    private CalendarRenderingHandler createRenderingHandler() {
        // <snip> Custom CalendarRenderingHandler
        // use a RenderingHandler which supports adding Highlighters.
        DemoCalendarRenderingHandler handler = new DemoCalendarRenderingHandler();
        // new style: use highlighter for color config
        handler.addHighlighters(new ColorHighlighter(
                new DayOfWeekHighlightPredicate(Calendar.SATURDAY), 
                null, Color.BLUE));
        // highlight property is setting opacity to true
        Highlighter transparent = new AbstractHighlighter(MonthViewExtDemoUtils.SELECTED) {
            
            @Override
            public Component highlight(Component component,
                    ComponentAdapter adapter) {
                // opacity is not one of the properties which are
                // guaranteed to be reset, so we have to do it here
                ((JComponent) component).setOpaque(adapter.getComponent()
                        .isOpaque());
                // call super to apply the highight - which is to
                // set the component's opacity to true
                return super.highlight(component, adapter);
            }
            
            @Override
            protected Component doHighlight(Component component,
                    ComponentAdapter adapter) {
                ((JComponent) component).setOpaque(true);
                return component;
            }
            
        };
        handler.addHighlighters(transparent);
        // </snip>
        return handler;
    }

    private ComboBoxModel createHeaderInfos() {
        // <snip> Custom CalendarHeaderHandler
        // create combo model containing handlers to choose
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        model.addElement(new DisplayInfo<CalendarHeaderHandler>("base (does nothing)", null));
        model.addElement(new DisplayInfo<CalendarHeaderHandler>(
                "default",
                new SpinningCalendarHeaderHandler()));
        model.addElement(new DisplayInfo<CalendarHeaderHandler>(
                "default (customized)",
                new DemoCalendarHeaderHandler(true, true)));
        // </snip>
        return model;
        
    }
//--------------------- MonthViewDemoControl
    public class MonthViewDemoControl extends AbstractBean {
        
        private boolean calendarVisible;
        
        @SuppressWarnings("unchecked")
        public MonthViewDemoControl() {
            DemoUtils.setSnippet("Custom CalendarRenderingHandler", calendarBox);
            
            // <snip> Custom CalendarHeaderHandler
            // configure the comboBox
            customHeaderBox.setModel(createHeaderInfos());
            customHeaderBox.setRenderer(new DefaultListRenderer(
                    DisplayValues.DISPLAY_INFO_DESCRIPTION));
            // </snip>
            
            DemoUtils.setSnippet("Custom CalendarHeaderHandler", customHeaderBox, zoomableBox);
            
            BindingGroup group = new BindingGroup();
            
            group.addBinding(Bindings.createAutoBinding(READ_WRITE, 
                    calendarBox, BeanProperty.create("selected"),
                    this, BeanProperty.create("calendarVisible")));
            
            group.addBinding(Bindings.createAutoBinding(READ, 
                    zoomableBox, BeanProperty.create("selected"),
                    monthView, BeanProperty.create("zoomable")));
            
            // <snip> Custom CalendarHeaderHandler
            // bind the combo box
            group.addBinding(Bindings.createAutoBinding(READ, 
                    monthView, BeanProperty.create("zoomable"),
                    customHeaderBox, BeanProperty.create("enabled")
                    ));
            Binding handlerBinding = Bindings.createAutoBinding(READ,
                    customHeaderBox, BeanProperty.create("selectedItem"),
                    this, BeanProperty.create("calendarHeaderHandler"));
            handlerBinding.setConverter(new DisplayInfoConverter<CalendarHeaderHandler>());
            // </snip>
            group.addBinding(handlerBinding);
            group.bind();
        }
        
        /**
         * @param handler the handler to set
         */
        // <snip> Custom CalendarHeaderHandler
        // wrapper around not yet public api: property on control for binding
        public void setCalendarHeaderHandler(CalendarHeaderHandler handler) {
            Object old = getCalendarHeaderHandler();
            monthView.putClientProperty(CalendarHeaderHandler.uiControllerID, handler);
            firePropertyChange("calendarHeaderHandler", old, getCalendarHeaderHandler());
        }
        // </snip>

        /**
         * @return the handler
         */
        public CalendarHeaderHandler getCalendarHeaderHandler() {
            return (CalendarHeaderHandler) 
                monthView.getClientProperty(CalendarHeaderHandler.uiControllerID);
        }



        /**
         * @param calendarVisible the calendarVisible to set
         */
        public void setCalendarVisible(boolean calendarVisible) {
            boolean old = isCalendarVisible();
            if (old == calendarVisible) return;
            this.calendarVisible = calendarVisible;
            updateCalendar();
            firePropertyChange("calendarVisible", old, isCalendarVisible());
        }
        
        /**
         * @return the calendarVisible
         */
        public boolean isCalendarVisible() {
            return calendarVisible;
        }
        
        /**
         * 
         */
        private void updateCalendar() {
            if (isCalendarVisible()) {
                showCalendarFrame();
            } else {
                hideCalendarFrame();
            }
        }
        
    }

//--------------------- create ui
    

    private void createMonthViewDemo() {
        monthView = new JXMonthView(){

            @Override
            public void setZoomable(boolean zoomable) {
                super.setZoomable(zoomable);
                ((JComponent) getParent()).revalidate();
            }
            
        };
        monthView.setName("monthView");
        monthView.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel monthViewContainer = new JXPanel();
        FormLayout formLayout = new FormLayout(
                "5dlu, c:d:n, l:4dlu:n, f:d:n", // columns
                "c:d:n " +
                ", t:4dlu:n, t:d:n " +
                ", t:4dlu:n, c:d:n" +
                ", t:4dlu:n, c:d:n" +
                ", t:4dlu:n, c:d:n"
        ); // rows
        PanelBuilder builder = new PanelBuilder(formLayout, monthViewContainer);
        builder.setBorder(Borders.DLU4_BORDER);
//        CellConstraints cl = new CellConstraints();
        CellConstraints cc = new CellConstraints();
        
        JXTitledSeparator areaSeparator = new JXTitledSeparator();
        areaSeparator.setName("monthViewSeparator");
        builder.add(areaSeparator, cc.xywh(1, 1, 4, 1));
        builder.add(monthView, cc.xywh(2, 3, 1, 1));
        
        
        add(monthViewContainer, BorderLayout.CENTER);
        
        JComponent monthViewControlPanel = new JXPanel();
        add(monthViewControlPanel, BorderLayout.EAST);
        JComponent extended = createExtendedConfigPanel();
        monthViewControlPanel.add(extended);

    }


    /**
     * @return
     */
    private JComponent createExtendedConfigPanel() {
        JXCollapsiblePane painterControl = new JXCollapsiblePane();
        FormLayout formLayout = new FormLayout(
                "5dlu, r:d:n, l:4dlu:n, f:d:n, l:4dlu:n, f:d:n", // columns
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
        areaSeparator.setName("extendedSeparator");
        builder.add(areaSeparator, cc.xywh(1, 1, 4, 1));
        
        int labelColumn = 2;
        int widgetColumn = labelColumn + 2;
        int currentRow = 3;

        
        calendarBox = new JCheckBox();
        calendarBox.setName("calendarBox");
        builder.add(calendarBox, cc.xywh(labelColumn, currentRow, 3, 1));
        currentRow += 2;
        
        zoomableBox = new JCheckBox();
        zoomableBox.setName("zoomableBox");
        builder.add(zoomableBox, cc.xywh(labelColumn, currentRow, 3, 1));
        currentRow += 2;

        customHeaderBox = new JComboBox();
        customHeaderBox.setName("customHeaderBox");
        JLabel headerBoxLabel = builder.addLabel(
                "", cl.xywh(labelColumn, currentRow, 1, 1),
                customHeaderBox, cc.xywh(widgetColumn, currentRow, 1, 1));
        headerBoxLabel.setName("customHeaderBoxLabel");
        LabelHandler.bindLabelFor(headerBoxLabel, customHeaderBox);
        currentRow += 2;
        
        return painterControl;
    }

    /**
     * main method allows us to run as a standalone demo.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame(MonthViewExtDemo.class.getAnnotation(DemoProperties.class).value());
                
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(new MonthViewExtDemo());
                frame.setPreferredSize(new Dimension(800, 600));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
    
    public MonthViewExtDemo() {
        super(new BorderLayout());
        // <snip> Custom MonthViewUI
        // register a custom monthView ui which provides support for custom
        // CalendarRenderingHandler and CalendarHeaderHandler
        UIManager.put(JXMonthView.uiClassID, "org.jdesktop.swingx.plaf.basic.DemoMonthViewUI");
        // </snip>
        createMonthViewDemo();
        DemoUtils.injectResources(this);
        bind();
    }

    
    
}
