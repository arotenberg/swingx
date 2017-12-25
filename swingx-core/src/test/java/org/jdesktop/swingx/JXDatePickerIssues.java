/*
 * $Id: JXDatePickerIssues.java 4290 2013-03-08 10:27:30Z kleopatra $
 *
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFormattedTextField;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerDateModel;
import javax.swing.plaf.UIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.calendar.CalendarUtils;
import org.jdesktop.swingx.calendar.DatePickerFormatter;
import org.jdesktop.swingx.table.DatePickerCellEditor;
import org.junit.Test;

/**
 * Known issues of <code>JXDatePicker</code> and picker related 
 * formats.
 * 
 * @author Jeanette Winzenburg
 */
public class JXDatePickerIssues extends InteractiveTestCase {
    @SuppressWarnings("all")
    private static final Logger LOG = Logger.getLogger(JXDatePickerIssues.class
            .getName());
    public static void main(String[] args) {
//        setSystemLF(true);
//        Trace14.keyboardFocusManager(false);
        JXDatePickerIssues  test = new JXDatePickerIssues();
        try {
//            test.runInteractiveTests();
//          test.runInteractiveTests("interactive.*UpdateUI.*");
//          test.runInteractiveTests("interactive.*Symbols.*");
          test.runInteractiveTests("interactive.*AsEditor.*");
          
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }

    /**
     * Issue #1372-swingx: DatePickerCellEditor on 2 different tables with clickCountToStart = 1 
     * throws IllegalComponentStateException
     * 
     * Metal only?
     * Same behaviour for JTable (and terminateEditOnFocusLost) and JXTable
     * hot fix: backing out if parent null - leaves arrowButton in pressed?
     */
    public void interactiveIllegalComponentStateAsEditor() {
        TableModel model = new DefaultTableModel(10, 2) {

            /** 
             * @inherited <p>
             */
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return Date.class;
            }
             
        };
        JTable one = createTableWithEditor(model, "picker 1 ");
        JTable other = createTableWithEditor(model, "picker 2");
        showWithScrollingInFrame(one, other, "click to left of cell");
    }
    
    private JTable createTableWithEditor(TableModel model, String name) {
        JTable table = new JXTable(model);
//        table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        DatePickerCellEditor editor = new DatePickerCellEditor();
        editor.getTableCellEditorComponent(table, null, false, 0, 0).setName(name);
        editor.setClickCountToStart(1);
        table.setDefaultEditor(Date.class, editor);
        return table;
    }

    public void interactiveDateSymbols() {
        for (Locale locale : Locale.getAvailableLocales()) {
            LOG.info("locale " + locale + locale.getDisplayCountry());
        }
        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance(new Locale("ar", "KW"));
        DecimalFormatSymbols dfs = new DecimalFormatSymbols(new Locale("ar", "KW"));//df.getDecimalFormatSymbols();
        LOG.info("" + df + " zero " + dfs.getZeroDigit());
        LOG.info("" + dfs);
        df.setDecimalFormatSymbols(dfs);
        @SuppressWarnings("unused")
        DateFormatSymbols dateSymbols = null;
        // set the beginning of the range to Arabic digits
//        dfs.setZeroDigit('\u0660');
//        df.setDecimalFormatSymbols(dfs);
        JXDatePicker picker = new JXDatePicker(new Locale("ar", "KW"));
        JLabel label1 = new JLabel(df.format(1));
        JLabel label2 = new JLabel(df.format(2));
        JComponent component = new JPanel();
        component.add(label1);
        component.add(label2);
        component.add(picker);
        showInFrame(component, "Symbos");
        System.out.println(df.format(1));
        System.out.println(df.format(2));
    }

    /**
     * Issue #??-swingx: first week of year in year-week format invalid?
     * 
     * Same problem for formattedTextField.
     */
    public void interactiveYearWeekFormat() {
        JComponent box = Box.createVerticalBox();
        calendar.set(2009, Calendar.JANUARY, 1);
        JXDatePicker picker = new JXDatePicker(calendar.getTime());
        SimpleDateFormat format = new SimpleDateFormat("yyww");
        format.setLenient(false);
        picker.setFormats(format);
        picker.getMonthView().setShowingWeekNumber(true);
        box.add(picker);
        JFormattedTextField field = new JFormattedTextField(format);
        field.setValue(picker.getDate());
        box.add(field);
        PropertyChangeListener l = new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                LOG.info(evt.getPropertyName() + evt.getSource().getClass().getSimpleName() + evt.getNewValue());
                
            }};
        field.addPropertyChangeListener(l);
        picker.addPropertyChangeListener(l);
        showInFrame(box, "yearweek format");
    }
    
    /**
     * Issue #955-swingx: editor should have same visual props as picker
     */
    public void interactivePickerToEditorVisuals() {
        JXDatePicker picker = new JXDatePicker(calendar.getTime());
        picker.setBackground(Color.RED);
        picker.setForeground(Color.BLUE);
        picker.setToolTipText("Colored editor");
        JComboBox combo = new JComboBox(new Object[] {"just something", "and something else"});
        combo.setEditable(true);
        combo.setBackground(Color.RED);
        combo.setForeground(Color.BLUE);
        combo.setToolTipText("Colored combo editor");
        JComponent box = new JXPanel();
        box.add(picker);
        box.add(combo);
        showInFrame(box, "Colored editor");
    }
    
    /**
     * Issue #913-swingx: Datepicker looks bad in some LFs (f.i. Nimbus)
     * 
     * not only Nimbus ...
     * - Metal: starts with different height than textfield, shrinks when coming
     *   back from another LF
     * - motif: button is shrunkenn always
     * - win: halfway okay, but should have buttons "nearer/integrated" to the field
     *   as spinner, combo does
     * - Vista: button should visually "merge" into field until rollover (as combo does,
     *   Spinner not
     * - Can't remember the reason why combo's button isn't re-used?
     *       
     */
    public void interactiveDatePickerNimbus() {
        JXDatePicker picker = new JXDatePicker(new Date());
        JFormattedTextField field = new JFormattedTextField();
        field.setValue(picker.getDate());
        JSpinner spinner = new JSpinner(new SpinnerDateModel());
        spinner.setValue(picker.getDate());
        JComboBox box = new JComboBox(new Object[] {picker.getDate()});
        box.setEditable(true);
        JComponent panel = new JXPanel();
        panel.add(field);
        panel.add(picker);
        panel.add(spinner);
        panel.add(box);
        showInFrame(panel, "Nimbus and picker");
    }

    /**
     * Compare behaviour of notifyAction: DatePicker vs. core components.
     * 
     * The requirement (from forum) is to trigger the default button on enter.
     * DatePicker: always eats - BasicDatePickerUI commit action always enabled
     * JComboBox: always pass-through
     * JFormattedDateField: pass-through if unchanged, eats if changed.
     * JTextField: eats if has actionListeners, pass-through if none.
     */
    public void interactiveDialogFocus() {
        final JComponent content = Box.createVerticalBox();
        final JXDatePicker picker = new JXDatePicker(calendar.getTime());
        final Action original = picker.getActionMap().get(JXDatePicker.COMMIT_KEY);
        Action wrapper = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                original.actionPerformed(e);
                
            }

            @Override
            public boolean isEnabled() {
                JFormattedTextField editor = picker.getEditor();
                Action action = editor.getActionMap().get(JTextField.notifyAction);
                return action.isEnabled();
            }
            
        };
        picker.getActionMap().put(JXDatePicker.COMMIT_KEY, wrapper);
        JComboBox combo = new JComboBox(new Object[] {calendar.getTime()});
        combo.setEditable(true);
        JFormattedTextField field = new JFormattedTextField();
        field.setValue(calendar.getTime());
        JTextField simple = new JTextField(calendar.getTime().toString());
        ActionListener l = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                LOG.info("action from fields: " + e.getSource().getClass());
                
            }
            
        };
        combo.addActionListener(l);
        field.addActionListener(l);
        simple.addActionListener(l);
        content.add(picker);
        content.add(combo);
        content.add(field);
        content.add(simple);
        Action open =  new AbstractAction("open") {

            public void actionPerformed(ActionEvent e) {
                JXDialog dialog = new JXDialog(content);
                dialog.pack();
                dialog.setVisible(true);
                
            }
            
        };
        
        showInFrame(new JButton(open), "dialog close");
    }
    


    
    /**
     * Issue #709-swingx: DatePicker should have empty constructor which doesn't select.
     * 
     * Plus deprecate constructors with long - replace by Date parameters.
     * Deprecate other methods taking long.
     */
    public void interactiveNullDate() {
        JComponent comp = Box.createVerticalBox();
        comp.add(new JLabel("setDate(null)"));
        JXDatePicker picker = new JXDatePicker();
        comp.add(picker);
        comp.add(new JTextField("initial -1"));
        showInFrame(comp, "null date");
    }

    /**
     * Issue #606-swingx: keybindings in monthView popup not working 
     * in InternalFrame.
     * 
     * Looks like a problem with componentInputMaps in internalFrame:
     * the registered keys are eaten somewhere (f.i. bind f1 for left is okay)
     *  
     */
    public void interactiveKeyBindingInternalFrame() {
        JXDatePicker picker = new JXDatePicker();
        JInternalFrame iFrame = new JInternalFrame("iFrame", true, true, true, true) {

            @Override
            protected boolean processKeyBinding(KeyStroke ks, KeyEvent e,
                    int condition, boolean pressed) {
                // hook for debugging
                return super.processKeyBinding(ks, e, condition, pressed);
            }
            
        };
        JComponent box = Box.createVerticalBox();
        box.add(picker);
        box.add(new JLabel("where????"));
        iFrame.getContentPane().add(box);
        iFrame.pack();
        iFrame.setVisible(true);
        JDesktopPane desktop = new JDesktopPane() {

            @Override
            protected boolean processKeyBinding(KeyStroke ks, KeyEvent e,
                    int condition, boolean pressed) {
                // hook for debugging
                return super.processKeyBinding(ks, e, condition, pressed);
            }
            
        };
        desktop.add(iFrame);
        JInternalFrame other = new JInternalFrame("other", true, true, true, true);
        other.add(new JLabel("Dummy .... we want it!"));
        other.pack();
        other.setVisible(true);
        desktop.add(other);
        
        JXFrame frame = wrapInFrame(desktop, "InternalFrame keybinding");
        frame.setSize(400, 300);
        frame.setVisible(true);
    }
    

    /**
     * Issue #725-swingx: review linkPanel/linkDate requirements.
     * 
     * link panel commit gesture (keystroke F5, double-click) must 
     * commit the date. 
     * 
     * Update the linkDate must 
     * - show in the LinkPanel. 
     * - open the popup in the month of the link date?
     *
     */
    public void interactiveCommitLinkPanelAction() {
        final JXDatePicker picker = new JXDatePicker();
        ActionListener l = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                LOG.info("received: " + e + 
                        "\n adjusting must be false: " 
                        + picker.getMonthView().getSelectionModel().isAdjusting());
                
            }
            
        };
        picker.addActionListener(l);
        JXFrame frame = showInFrame(picker, "double-click on linkpanel must commit");
        Action nextDate = new AbstractAction("change linkdate") {

            public void actionPerformed(ActionEvent e) {
                setLinkDateNextMonth(picker);
                
            }
            
        };
        addAction(frame, nextDate);
        frame.pack();
    }
    
    /**
     * Issue #572-swingx: monthView must show linkDate on empty selection.
     *
     * add month to linkDate, popup must show month containing link date.
     * Note: client code using the link date for something different than today
     * must take care to update the message format (PENDING: is that possible?)
     */
    public void interactiveLinkDate() {
        final JXDatePicker picker = new JXDatePicker();
        setLinkDateNextMonth(picker);
        Action action = new AbstractAction("next linkdate month") {

            public void actionPerformed(ActionEvent e) {
                setLinkDateNextMonth(picker);
            }
            
        };
        JXFrame frame = wrapInFrame(picker, "show linkdate if unselected");
        addAction(frame, action);
        addMessage(frame, "incr linkDate and open popup: must show new linkMonth");
        frame.pack();
        frame.setVisible(true);
    }


    protected void setLinkDateNextMonth(final JXDatePicker picker) {
        Date linkDate = picker.getLinkDay();
        // add a months and set as new link date
        calendar.setTime(linkDate);
        calendar.add(Calendar.MONTH, 1);
        picker.setLinkDay(calendar.getTime());
    }

    /**
     * Issue #565-swingx: occasionally, the popup isn't closed. 
     * to reproduce: open the picker's popup then click into
     * the comboBox. All is well if click into the textfield.
     *
     */
    public void interactiveClosePopup() {
        final JXDatePicker picker = new JXDatePicker();
        JComboBox box = new JComboBox(new String[] {"one", "twos"});
        box.setEditable(true);
        ActionListener l = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                LOG.info("received: " + e + 
                        "\n and not adjusting: " 
                        + picker.getMonthView().getSelectionModel().isAdjusting());
                
            }
            
        };
        picker.addActionListener(l);
        box.addActionListener(l);
        JComponent panel = new JPanel();
        panel.add(picker);
        panel.add(box);
        panel.add(new JTextField("textfield - click into with open popup"));
        JXFrame frame = showInFrame(panel, "closed?");
        // check if it's a lightweight vs. heavyweight problem
        frame.setSize(new Dimension(frame.getSize().width, 400));
    }
    
    /**
     * compare JFormattedTextField and JXDatePicker pref.
     * date is slightly cut. Looks like an issue 
     * of the formattedTextField.
     */
    public void interactivePrefSize() {
        JXDatePicker picker = new JXDatePicker(new Date());
        JFormattedTextField field = new JFormattedTextField(new DatePickerFormatter());
        field.setValue(picker.getDate());
        JComponent panel = new JPanel();
        panel.add(picker);
        panel.add(field);
        showInFrame(panel, "compare pref width");
    }

    /**
     * visual testing of selection constraints: upper/lower bounds.
     * 
     * Issue #567-swingx:
     * clicking into a unselectable in the popup clears the
     * selection - should revert to the last valid selection.
     * PENDING: better control the bounds ... 
     */
    public void interactiveBoundsDatePickerClickUnselectable() {
        JXDatePicker picker = new JXDatePicker();
        calendar.add(Calendar.DAY_OF_MONTH, 10);
        picker.getMonthView().setUpperBound(calendar.getTime());
        calendar.add(Calendar.DAY_OF_MONTH, - 20);
        picker.getMonthView().setLowerBound(calendar.getTime());
        showInFrame(picker, "click unselectable clears date");
    }




    /**
     * Sanity during fix #705-swingx: JXMonthView must not scroll in layoutContainer.
     * 
     * Here we have a selected date way into the future, to be sure it had to scroll.
     * Open question: should the monthView be scrolled to the selected always on open or
     *   remember the last month shown
     *   
     * To reproduce  
     * - open popup with toolbar button 
     * - click next month
     * - close (without commit/cancel)
     * - open popup again with toolbar button
     * - ?? on old navigated or current picker date?
     */
    public void interactiveVisibleMonth() {
        calendar.add(Calendar.YEAR, 1);
        final JXDatePicker picker = new JXDatePicker(calendar.getTime());
        JXFrame frame = wrapInFrame(picker, "sanity - monthview shows selected");
        Action toggleWrapper = new AbstractAction("open popup") {

            public void actionPerformed(ActionEvent e) {
                Action togglePopup = picker.getActionMap().get("TOGGLE_POPUP");
                togglePopup.actionPerformed(null);
                CalendarUtils.startOfMonth(calendar);
                assertEquals(calendar.getTime(), picker.getMonthView().getFirstDisplayedDay());

                
            }
            
        };
        addAction(frame, toggleWrapper);
        frame.pack();
        frame.setVisible(true);
    }
//-------------------- unit tests
    
    
    /**
     * Issue #1551-swingx: column setting lost in updateUI
     */
    @Test
    public void testSynchColumsPicker() {
        JXDatePicker picker = new JXDatePicker();
        int columns = 17;
        picker.getEditor().setColumns(columns);
        picker.updateUI();
        assertEquals("columns after updateUI", columns, picker.getEditor().getColumns());
    }
    
    /**
     * Issue #1551-swingx: column setting lost in updateUI
     * 
     * Compare JCombobBox behaviour
     */
    @Test
    public void testSynchColumsCombo() {
        JComboBox combo = new JComboBox();
        combo.setEditable(true);
        int columns = 17;
        ((JTextField) combo.getEditor().getEditorComponent()).setColumns(columns);
        combo.updateUI();
        assertEquals("columns after updateUI", columns, 
                ((JTextField) combo.getEditor().getEditorComponent()).getColumns());
    }
    
    /**
     * Issue #1551-swingx: column setting lost in updateUI
     * 
     * Sanity: columns kept in custom editor
     */
    @Test
    public void testSynchColumsCustomEditorPicker() {
        JXDatePicker picker = new JXDatePicker();
        JFormattedTextField field = new JFormattedTextField();
        picker.setEditor(field);
        int columns = 17;
        picker.getEditor().setColumns(columns);
        picker.updateUI();
        assertEquals("columns after updateUI", columns, picker.getEditor().getColumns());
    }
    
    /**
     * Issue #??-swingx: font setting lost in updateUI
     */
    @Test
    public void testSynchFontEditorPicker() {
        JXDatePicker picker = new JXDatePicker();
        Font derived = picker.getFont().deriveFont(40f);
        assertFalse("sanity: derived font must not be uiresource", (derived instanceof UIResource));
        picker.setFont(derived);
        picker.updateUI();
        assertEquals("font after updateUI", derived, picker.getFont());
    }
    
    /**
     * Issue #??-swingx: font setting lost in updateUI
     * 
     * Sanity test: font of custom editor kept.
     */
    @Test
    public void testSynchFontCustomEditorPicker() {
        JXDatePicker picker = new JXDatePicker();
        JFormattedTextField field = new JFormattedTextField();
        picker.setEditor(field);
        Font derived = picker.getFont().deriveFont(40f);
        assertFalse("sanity: derived font must not be uiresource", (derived instanceof UIResource));
        picker.setFont(derived);
        picker.updateUI();
        assertEquals("font after updateUI", derived, picker.getFont());
    }

    /**
     * Issue #955-swingx: editor should have same visual props as picker
     * for comparison: combobox.
     */
    @Test
    public void testSynchEditorFontInitialCombo() {
        JComboBox combo = new JComboBox();
        // make sure we have an editor
        combo.setEditable(true);
        assertEquals("combo fonts initially synched",
                combo.getEditor().getEditorComponent().getFont(), combo.getFont());
    }
    
    /**
     * Issue #955-swingx: editor should have same visual props as picker
     * for comparison: combobox.
     */
    @Test
    public void testSynchEditorFontCombo() {
        JComboBox combo = new JComboBox();
        // make sure we have an editor
        combo.setEditable(true);
        Font font = combo.getFont();
        Font derived = font.deriveFont(40f);
        combo.setFont(derived);
        assertEquals("combo fonts synched after setting",
                combo.getEditor().getEditorComponent().getFont(), combo.getFont());
        combo.updateUI();
        assertEquals("combo fonts synched after updateUI",
                combo.getEditor().getEditorComponent().getFont(), combo.getFont());
        
    }


    /**
     * Issue #955-swingx: editor should have same visual props as picker
     */
    @Test
    public void testSynchEditorColorsInitialPicker() {
        JXDatePicker picker = new JXDatePicker();
        assertEquals(picker.getForeground(), picker.getEditor().getForeground());
        assertEquals(picker.getBackground(), picker.getEditor().getBackground());
    }
    
    /**
     * Issue #955-swingx: editor should have same visual props as picker
     * for comparison: combobox.
     */
    @Test
    public void testSynchEditorColorsInitialCombo() {
        JComboBox picker = new JComboBox();
        // make sure we have an editor
        picker.setEditable(true);
        assertEquals(picker.getForeground(), picker.getEditor().getEditorComponent().getForeground());
        assertEquals(picker.getBackground(), picker.getEditor().getEditorComponent().getBackground());
    }
    
    /**
     * Issue #955-swingx: editor should have same visual props as picker
     */
    @Test
    public void testSynchEditorColorsPicker() {
        JXDatePicker picker = new JXDatePicker();
        picker.setBackground(Color.RED);
        picker.setForeground(Color.BLUE);
        assertEquals(picker.getForeground(), picker.getEditor().getForeground());
        assertEquals(picker.getBackground(), picker.getEditor().getBackground());
    }
    
    /**
     * Issue #955-swingx: editor should have same visual props as picker
     * for comparison: combobox.
     */
    @Test
    public void testSynchEditorColorsCombo() {
        JComboBox picker = new JComboBox();
        // make sure we have an editor
        picker.setEditable(true);
        picker.setBackground(Color.RED);
        picker.setForeground(Color.BLUE);
        assertEquals(picker.getForeground(), picker.getEditor().getEditorComponent().getForeground());
        assertEquals(picker.getBackground(), picker.getEditor().getEditorComponent().getBackground());
    }

    
    /**
     * Issue #764-swingx: picker prefsize too narrow
     */
    public void testPrefSizeGerman() {
        // wednesday - has width problems
        calendar.set(2008, Calendar.FEBRUARY, 20);
        JXDatePicker nullDate = new JXDatePicker(Locale.GERMAN);
        JXDatePicker notNull = new JXDatePicker(calendar.getTime(), Locale.GERMAN);
        assertEquals(notNull.getPreferredSize(), nullDate.getPreferredSize());
    }


    /**
     * Issue #764-swingx: picker prefsize too narrow
     */
    public void testPrefSizeUS() {
        // wednesday - has width problems
        calendar.set(2008, Calendar.FEBRUARY, 20);
         JXDatePicker nullDate = new JXDatePicker(Locale.US);
        JXDatePicker notNull = new JXDatePicker(calendar.getTime(), Locale.US);
        assertEquals(notNull.getPreferredSize(), nullDate.getPreferredSize());
    }
    
    /**
     * Issue #764-swingx: picker prefsize too narrow
     */
    public void testPrefSizeUSEditor() {
        // wednesday - has width problems
        calendar.set(2008, Calendar.FEBRUARY, 20);
         JXDatePicker nullDate = new JXDatePicker(Locale.US);
        JXDatePicker notNull = new JXDatePicker(calendar.getTime(), Locale.US);
        assertEquals(notNull.getEditor().getPreferredSize(), nullDate.getEditor().getPreferredSize());
    }

    
//    DateFormat longFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
    /**
     * Issue #572-swingx: monthView must show linkDate on empty selection.
     *
     * Definition of picker.linkDate vs. monthView.todayInMillis missing.
     * PENDING: back out - say linkDate == today, not mutable by client code
     * but fixed to system?
     */
    public void testLinkDate() {
        JXDatePicker picker = new JXDatePicker();
        Date today = picker.getLinkDay();
        Date firstDisplayedDate = picker.getMonthView().getFirstDisplayedDay();
        assertSameMonth(today, firstDisplayedDate);
        calendar.setTime(today);
        calendar.add(Calendar.MONTH, 2);
        picker.setLinkDay(calendar.getTime());
        assertSameMonth(calendar.getTime(), 
                picker.getMonthView().getFirstDisplayedDay());
    }
    /**
     * @param linkDate
     * @param firstDisplayedDate
     */
    private void assertSameMonth(Date linkDate, Date firstDisplayedDate) {
        calendar.setTime(linkDate);
        int linkMonth = calendar.get(Calendar.MONTH);
        calendar.setTime(firstDisplayedDate);
        assertEquals(linkMonth, calendar.get(Calendar.MONTH));
        
    }

    /**
     * Returns a timezone different from the given.
     * @param defaultZone
     * @return
     */
    @SuppressWarnings("unused")
    private TimeZone getSafeAlternativeTimeZone(TimeZone defaultZone) {
        TimeZone alternative = TimeZone.getTimeZone("GMT-6");
        // sanity
        assertNotNull(alternative);
        if (alternative.equals(defaultZone)) {
            alternative = TimeZone.getTimeZone("GMT-7");
            // paranoid ... but shit happens
            assertNotNull(alternative);
            assertFalse(alternative.equals(defaultZone));
        }
        return alternative;
    }




    private Calendar calendar;
    

    @Override
    protected void setUp() throws Exception {
        calendar = Calendar.getInstance();
    }

    
    
}
