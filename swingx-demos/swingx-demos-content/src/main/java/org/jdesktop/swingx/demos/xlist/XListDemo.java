/*
 * $Id: XListDemo.java 4185 2012-06-22 13:39:48Z kschaefe $
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
package org.jdesktop.swingx.demos.xlist;

import static org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Comparator;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;

import org.jdesktop.application.Action;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.swingx.JXCollapsiblePane;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledSeparator;
import org.jdesktop.swingx.binding.DisplayInfo;
import org.jdesktop.swingx.binding.DisplayInfoConverter;
import org.jdesktop.swingx.binding.LabelHandler;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.PainterHighlighter;
import org.jdesktop.swingx.demos.search.Contributor;
import org.jdesktop.swingx.demos.search.Contributors;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.renderer.DefaultListRenderer;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.renderer.StringValues;
import org.jdesktop.swingx.rollover.RolloverProducer;
import org.jdesktop.swingx.sort.DefaultSortController;
import org.jdesktop.swingx.util.PaintUtils;
import org.jdesktop.swingxset.util.DemoUtils;
import org.jdesktop.swingxset.util.DisplayValues;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.sun.swingset3.DemoProperties;

/**
 * A demo for the {@code JXList}.
 *
 * @author Karl George Schaefer
 */
//TODO implement
@DemoProperties(
    value = "JXList Demo",
    category = "Data",
    description = "Demonstrates JXList, an enhanced list component.",
    sourceFiles = {
        "org/jdesktop/swingx/demos/xlist/XListDemo.java",
        "org/jdesktop/swingx/demos/xlist/resources/XListDemo.properties"
    }
)
@SuppressWarnings("serial")
public class XListDemo extends JXPanel {
    
    private JXList list;
    private JComboBox comparatorCombo;
    private JButton toggleSortOrder;
    private JButton resetSortOrder;
    private JCheckBox rolloverEnabledBox;
    private JComboBox highlighterCombo;

    public XListDemo() {
        super(new BorderLayout());
        initComponents();
        configureComponents();
        DemoUtils.injectResources(this);
        bind();
    }

//---------------- public api for Binding/Action control
    
    @Action
    // <snip> JXList sorting
    //  api to toggle sorts
    public void toggleSortOrder() {
        list.toggleSortOrder();
    }
    // </snip>

    @Action
    public void resetSortOrder() {
        list.resetSortOrder();
    }
    
    public void setComparator(Comparator<?> comparator) {
        // <snip> JXList sorting
        //  configure the comparator to use in sorting
        list.setComparator(comparator);
        if (list.getSortOrder() != SortOrder.UNSORTED) {
            // PENDING missing refresh api?
            ((DefaultSortController<?>) list.getRowSorter()).sort();
        }
        // </snip>
    }
    
    
    public void setRolloverHighlighter(Highlighter hl) {
        list.setHighlighters(hl);
    }
 
    public void setRolloverEnabled(boolean enabled) {
        list.setRolloverEnabled(enabled);
        list.setToolTipText(list.isRolloverEnabled() ? 
                DemoUtils.getResourceString(getClass(), "stickyRolloverToolTip") : null);
    }
//------------------- ui configuration    
    private void configureComponents() {
        
        // <snip> JXList rendering
        // custom String representation: concat various element fields
        StringValue sv = new StringValue() {

            @Override
            public String getString(Object value) {
                if (value instanceof Contributor) {
                    Contributor c = (Contributor) value;
                    return c.getFirstName() + " " + c.getLastName() + " (" + c.getMerits() + ")";
                }
                return StringValues.TO_STRING.getString(value);
            }
            
        };
        // PENDING JW: add icon (see demos in swingx)
        // set a renderer configured with the custom string converter
        list.setCellRenderer(new DefaultListRenderer(sv));
        // </snip>
        
        // PENDING JW: add visual clue to currentl sortorder
        toggleSortOrder.setAction(DemoUtils.getAction(this, "toggleSortOrder"));
        resetSortOrder.setAction(DemoUtils.getAction(this, "resetSortOrder"));
        
        comparatorCombo.setRenderer(
                new DefaultListRenderer(DisplayValues.DISPLAY_INFO_DESCRIPTION));
        highlighterCombo.setRenderer(
                new DefaultListRenderer(DisplayValues.DISPLAY_INFO_DESCRIPTION));
        
        // demo specific config
        DemoUtils.setSnippet("JXList sorting", toggleSortOrder, resetSortOrder, comparatorCombo);
        DemoUtils.setSnippet("JXList rollover support", rolloverEnabledBox, highlighterCombo);
        DemoUtils.setSnippet("JXList rendering", list);
    }

    @SuppressWarnings("unchecked")
    private void bind() {
        // list properties
        // <snip> JXlist sorting
        // enable auto-create RowSorter
        list.setAutoCreateRowSorter(true);
        list.setModel(Contributors.getContributorListModel());
        //</snip>
        
        // control combos
        comparatorCombo.setModel(createComparators());
        highlighterCombo.setModel(createRolloverHighlighters());
        
        BindingGroup group = new BindingGroup();
        group.addBinding(Bindings.createAutoBinding(READ, 
                rolloverEnabledBox, BeanProperty.create("selected"),
                this, BeanProperty.create("rolloverEnabled")));
        Binding comparatorBinding = Bindings.createAutoBinding(READ, 
                comparatorCombo, BeanProperty.create("selectedItem"),
                this, BeanProperty.create("comparator"));
        comparatorBinding.setConverter(new DisplayInfoConverter<Comparator<?>>());
        group.addBinding(comparatorBinding);
    
        Binding rolloverBinding = Bindings.createAutoBinding(READ, 
                highlighterCombo, BeanProperty.create("selectedItem"),
                this, BeanProperty.create("rolloverHighlighter"));
        rolloverBinding.setConverter(new DisplayInfoConverter<Highlighter>());
        group.addBinding(rolloverBinding);
        
        group.bind();
    }

    private ComboBoxModel createRolloverHighlighters() {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        // <snip> JXList rollover support
        // simple decorations of rollover row 
        model.addElement(new DisplayInfo<Highlighter>("Background Color",
                new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, Color.MAGENTA, null)));
        model.addElement(new DisplayInfo<Highlighter>("Foreground Color",
                new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, null, Color.MAGENTA)));
        // </snip>
        model.addElement(new DisplayInfo<Highlighter>("Related Merit", 
                createExtendedRolloverDecoration()));
        return model;
    }

    private Highlighter createExtendedRolloverDecoration() {
        Color color = PaintUtils.setAlpha(Color.YELLOW, 100);
        final PainterHighlighter hl = new PainterHighlighter(HighlightPredicate.NEVER, 
                new MattePainter(color));
        // <snip> JXList rollover support
        // listen to changes of cell-rollover property
        // and set a Highlighters custom predicate accordingly
        PropertyChangeListener l = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                Point location = (Point) evt.getNewValue();
                int row = -1;
                if (location != null) {
                    row = location.y;
                }
                hl.setHighlightPredicate(new MeritRangeHighlightPredicate(
                        row < 0 ? null : list.getElementAt(row))); 
            }
            
        };
        list.addPropertyChangeListener(RolloverProducer.ROLLOVER_KEY, l);
        // </snip>
        return hl;
    }

    public static class MeritRangeHighlightPredicate implements HighlightPredicate {

        private Contributor compare;

        public MeritRangeHighlightPredicate(Object object) {
            this.compare = object instanceof Contributor ? (Contributor) object : null;
        }

        @Override
        // <snip> JXList rollover support
        // custom HighlightPredicate which compare the current value
        // against a fixed value and returns true if "near"
        public boolean isHighlighted(Component renderer,
                ComponentAdapter adapter) {
            if (compare == null) return false;
            if (!(adapter.getValue() instanceof Contributor)) return false;
            Contributor contributor = (Contributor) adapter.getValue();
            return contributor.getMerits() >= compare.getMerits() - 5 && 
                contributor.getMerits() <= compare.getMerits() + 5;
        }
        // </snip>
        
    }
    
    private ComboBoxModel createComparators() {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        // <snip> JXList sorting
        //  null comparator defaults to comparing by the display string
        model.addElement(new DisplayInfo<Comparator<?>>("None (by display string)", 
                null));
        // compare by Comparable as implemented by the elements
        model.addElement(new DisplayInfo<Comparator<?>>("Comparable (by lastname)", 
                DefaultSortController.COMPARABLE_COMPARATOR));
        // custom comparator
        Comparator<Contributor> meritComparator = new Comparator<Contributor>() {

            @Override
            public int compare(Contributor o1, Contributor o2) {
                return o1.getMerits() - o2.getMerits();
            }
        };
        // </snip>
        model.addElement(new DisplayInfo<Comparator<?>>("Custom (by merits)", meritComparator));
        return model;
    }

//-------------------- init ui
    
    private void initComponents() {
        list = new JXList();
        list.setName("list");

        JPanel monthViewContainer = new JXPanel();
        FormLayout formLayout = new FormLayout(
                "5dlu, f:d:g ", // l:4dlu:n, f:d:g", // columns
                "c:d:n " +
                ", t:4dlu:n, f:d:g " +
                ", t:4dlu:n, c:d:n" +
                ", t:4dlu:n, c:d:n" +
                ", t:4dlu:n, c:d:n"
        ); // rows
        PanelBuilder builder = new PanelBuilder(formLayout, monthViewContainer);
        builder.setBorder(Borders.DLU4_BORDER);
//        CellConstraints cl = new CellConstraints();
        CellConstraints cc = new CellConstraints();
        
        JXTitledSeparator areaSeparator = new JXTitledSeparator();
        areaSeparator.setName("listSeparator");
        builder.add(areaSeparator, cc.xywh(1, 1, 2, 1));
        builder.add(new JScrollPane(list), cc.xywh(2, 3, 1, 1));
        
        
        add(monthViewContainer, BorderLayout.CENTER);
        
        JComponent extended = createExtendedConfigPanel();
        add(extended, BorderLayout.EAST);
    }

    private JComponent createExtendedConfigPanel() {
        JXCollapsiblePane painterControl = new JXCollapsiblePane();
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
        areaSeparator.setName("extendedSeparator");
        builder.add(areaSeparator, cc.xywh(1, 1, 4, 1));
        
        int labelColumn = 2;
        int widgetColumn = labelColumn + 2;
        int currentRow = 3;

        toggleSortOrder = new JButton();
        toggleSortOrder.setName("toggleSortOrder");
        builder.add(toggleSortOrder, cc.xywh(labelColumn, currentRow, 3, 1));
        currentRow += 2;
        
        resetSortOrder = new JButton();
        resetSortOrder.setName("resetSortOrder");
        builder.add(resetSortOrder, cc.xywh(labelColumn, currentRow, 3, 1));
        currentRow += 2;
        
        comparatorCombo = new JComboBox();
        comparatorCombo.setName("comparatorCombo");
        JLabel comparatorComboLabel = builder.addLabel(
                "", cl.xywh(labelColumn, currentRow, 1, 1),
                comparatorCombo, cc.xywh(widgetColumn, currentRow, 1, 1));
        comparatorComboLabel.setName("comparatorComboLabel");
        LabelHandler.bindLabelFor(comparatorComboLabel, comparatorCombo);
        currentRow += 2;
        
        currentRow += 2;
        JXTitledSeparator rolloverSeparator = new JXTitledSeparator();
        rolloverSeparator.setName("rolloverSeparator");
        builder.add(rolloverSeparator, cc.xywh(1, currentRow, 4, 1));
        currentRow += 2;

          rolloverEnabledBox = new JCheckBox();
          rolloverEnabledBox.setName("rolloverBox");
          builder.add(rolloverEnabledBox, cc.xywh(labelColumn, currentRow, 3, 1));
          currentRow += 2;
          
          highlighterCombo = new JComboBox();
          highlighterCombo.setName("highlighterCombo");
          JLabel highlighterComboLabel = builder.addLabel(
                  "", cl.xywh(labelColumn, currentRow, 1, 1),
                  highlighterCombo, cc.xywh(widgetColumn, currentRow, 1, 1));
          highlighterComboLabel.setName("highlighterComboLabel");
          LabelHandler.bindLabelFor(highlighterComboLabel, highlighterCombo);
          currentRow += 2;

        return painterControl;
    }

    /**
     * main method allows us to run as a standalone demo.
     */
    /**
     * main method allows us to run as a standalone demo.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame(XListDemo.class.getAnnotation(DemoProperties.class).value());
                
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(new XListDemo());
                frame.setPreferredSize(new Dimension(800, 600));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

//--------------------- dummy api (to keep bbb happy)
    
    public Comparator<?> getComparator() {
        return null;
    }
    
    public Highlighter getRolloverHighlighter() {
        return null;
    }
    
    public boolean isRolloverEnabeld() {
        return list.isRolloverEnabled();
    }
}
