/*
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * California 95054, U.S.A. All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */
package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.JTextComponent;

import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.calendar.DateSelectionModel;
import org.jdesktop.swingx.calendar.DaySelectionModel;
import org.jdesktop.swingx.calendar.SingleDaySelectionModel;
import org.jdesktop.swingx.calendar.DateSelectionModel.SelectionMode;
import org.jdesktop.swingx.plaf.LookAndFeelAddons;
import org.jdesktop.test.VerticalLayoutPref;

/**
 * Simple tests to ensure that the {@code JXDatePicker} can be instantiated and
 * displayed.<p>
 * 
 * JW: being lazy - added visuals for <code>JXMonthView</code> as well.
 * 
 * @author Karl Schaefer
 */
public class JXDatePickerVisualCheck extends InteractiveTestCase {
    @SuppressWarnings("all")
    private static final Logger LOG = Logger
            .getLogger(JXDatePickerVisualCheck.class.getName());
    @SuppressWarnings("unused")
    private Calendar calendar;
    /** flag to decide if the menubar should be created */
    private boolean showMenu;

    public JXDatePickerVisualCheck() {
        super("JXDatePicker Test");
    }

    public static void main(String[] args) throws Exception {
       UIManager.put("JXDatePicker.forceZoomable", Boolean.TRUE);
//         setSystemLF(true);
        JXDatePickerVisualCheck test = new JXDatePickerVisualCheck();
        setLAF("Win");
        try {
//            test.runInteractiveTests();
//            test.runInteractiveTests("interactive.*PrefSize.*");
//            test.runInteractiveTests("interactive.*Keep.*");
//          test.runInteractiveTests("interactive.*Multiple.*");
//            test.runInteractiveTests("interactive.*Editable.*");
//            test.runInteractiveTests("interactive.*Enable.*");
//            test.runInteractiveTests("interactive.*Popup.*");
            test.runInteractiveTests("interactive.*NullDate.*");
//            test.runInteractiveTests("interactive.*Event.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }

    /**
     * Quick check if DatePicker behaves correctly in vertical max
     * (yes, thougnh I don't know why: max size isn't overriden?)
     */
    public void interactivePickerMax() {
        JXDatePicker picker = new JXDatePicker();
        picker.setBorder(BorderFactory.createLineBorder(Color.RED));
        JComponent content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
        content.add(picker);
//        content.add(new JFormattedTextField(new Date()));
        JXFrame frame = showInFrame(content, "max size?");
        frame.setSize(frame.getWidth() * 2, frame.getHeight() * 4);
    }

    /**
     * Issue #1393-swingx: inherited popup not shown on editor/arrow button.
     * Issue #1427-swingx: right click on arrow must not open monthView
     * Issue ??: tooltip on children - okay (JW, Mar 2011).
     * Issue #1329-swingx: editor border different from textfield border in win7
     */
    public void interactiveParentPopup() {
        final JXDatePicker picker = new JXDatePicker(new Date());
        picker.setToolTipText("picker toolTip");
        picker.setInheritsPopupMenu(true);
        JComboBox compareBox = new JComboBox(new Object[]{"some", "items", "are ", "better ....."});
        compareBox.setToolTipText("combo toolTip");
        compareBox.setInheritsPopupMenu(true);
        JComboBox compareBoxEditable = new JComboBox(new Object[]{"some", "items", "are ", "better ....."});
        compareBoxEditable.setToolTipText("editable combo toolTip");
        compareBoxEditable.setEditable(true);
        compareBoxEditable.setInheritsPopupMenu(true);
        JTextField compareField = new JTextField("just for comparing");
        compareField.setInheritsPopupMenu(true);
        JPanel panel = new JPanel();
        JPopupMenu menu = new JPopupMenu();
        menu.add("dummy");
        panel.setComponentPopupMenu(menu);
        panel.add(picker);
        panel.add(compareBoxEditable);
        panel.add(compareBox);
        panel.add(compareField);
        JXFrame frame = wrapInFrame(panel, "inherited popup");
        Action printBorder = new AbstractAction("print borders ") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = "picker border: " + picker.getBorder() 
                + "\n editor border: " + picker.getEditor().getBorder()
                    + "\n FormattedTextField border: " + new JFormattedTextField().getBorder()
                ;    
                LOG.info(text);
            }
        };
        addAction(frame, printBorder);
        addEnabledToggleWithChildren(frame, panel);
        addStatusMessage(frame, "right click in components to show parent popup");
        show(frame);
        LOG.info("addon " + LookAndFeelAddons.getAddon());
        
    }
    public void interactivePopupMenuListener() {
        JXDatePicker picker = new JXDatePicker();
        PopupMenuListener l = new PopupMenuListener() {
            
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                LOG.info("visible");
            }
            
            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                LOG.info("invisible");
            }
            
            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                LOG.info("canceled");
            }
        };
        picker.addPopupMenuListener(l);
        showInFrame(picker, "picker: notification?");
    }
    
    public void interactivePopupMenuListenerCompare() {
        JTextArea textArea = new JTextArea(10, 20);
        JPopupMenu picker = new JPopupMenu();
        picker.add("just something");
        PopupMenuListener l = new PopupMenuListener() {
            
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                LOG.info("visible");
            }
            
            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                LOG.info("invisible");
            }
            
            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                LOG.info("canceled");
            }
        };
        textArea.setComponentPopupMenu(picker);
        picker.addPopupMenuListener(l);
        showInFrame(textArea, "plain Popup: notification");
    }
    /**
     * Issue #940-swingx: support multiple selection in picker.
     * Nothing out-off-the-box, trying to manipulate the mouseEvent.
     * 
     * No success: no way to unselect
     */
    public void interactiveMultipleSelectionWithoutCtrl() {
        JXMonthView monthView = new JXMonthView(new Date()) {

            @Override
            protected void processMouseEvent(MouseEvent e) {
                // change the modifiers to believe that control key is down
//                int modifiers = e.getModifiers() | InputEvent.CTRL_DOWN_MASK;
                int modifiers = e.getModifiers() | InputEvent.CTRL_MASK;
                
                MouseEvent myME = new MouseEvent(
                    (Component) e.getSource(), 
                    e.getID(), 
                    e.getWhen(), 
                    modifiers, // my changed modifier
                    e.getX(), 
                    e.getY(), 
                    e.getXOnScreen(), 
                    e.getYOnScreen(), 
                    e.getClickCount(), 
                    e.isPopupTrigger(), 
                    e.getButton());
                super.processMouseEvent(myME);
//                super.processMouseEvent(e);
            }
            
        };
        monthView.setSelectionMode(SelectionMode.MULTIPLE_INTERVAL_SELECTION);
        JXDatePicker picker = new JXDatePicker();
        picker.setMonthView(monthView);
        JXFrame frame = showInFrame(picker, "multiple selection without ctrl");
        show(frame);
    }
    
    /**
     * Issue #940-swingx: support multiple selection in picker.
     * Nothing out-off-the-box, trying to implement custom 
     * dateSelectionModel with overridden setSelectionInterval. 
     * Looks halfway okay for mouse-interaction, completely
     * unusable with keyboard interaction.
     */
    public void interactiveMultipleSelectionTogglingModel() {
        DateSelectionModel model = new ToggleSelectionModel();
        model.setSelectionMode(SelectionMode.MULTIPLE_INTERVAL_SELECTION);
        JXMonthView monthView = new JXMonthView(new Date(), model);
        JXDatePicker picker = new JXDatePicker();
        picker.setMonthView(monthView);
        JXFrame frame = showInFrame(picker, "multiple selection - toggle");
        show(frame);
    }
    
    /**
     * Trying to interfere in setSelectionInterval. Idea from Sunacle dev forum:
     * <a href=http://forums.sun.com/thread.jspa?threadID=5431797&tstart=0> 
     * MultiSelect JList without ctrl
     * </a>
     */
    public static class ToggleSelectionModel extends DaySelectionModel {

        @Override
        public void setSelectionInterval(Date startDate, Date endDate) {
            if (!isSameDay(startDate, endDate)) {
                super.setSelectionInterval(startDate, endDate);
                return;
            }
            if (isSelected(startDate)) {
                removeSelectionInterval(startDate, startDate);
            } else {
                addSelectionInterval(startDate, startDate);
            }
        }
        
        
    }
    
    /**
     * Issue #940-swingx: support multiple selection in picker.
     * Nothing out-off-the-box, trying to implement custom 
     * dateSelectionModel. No success.
     */
    public void interactiveMultipleSelectionAddingModel() {
        DateSelectionModel model = new AddingDaySelectionModel();
        model.setSelectionMode(SelectionMode.MULTIPLE_INTERVAL_SELECTION);
        JXMonthView monthView = new JXMonthView(new Date(), model) {

            @Override
            public Date getSelectionDate() {
                if (getSelectionModel() instanceof AddingDaySelectionModel) {
                    return ((AddingDaySelectionModel) getSelectionModel()).getLastAddedDate();
                }
                return super.getSelectionDate();
            }
            
        };
        JXDatePicker picker = new JXDatePicker();
        picker.setMonthView(monthView);
        JXFrame frame = showInFrame(picker, "multiple selection");
        show(frame);
    }
    
    /**
     * Custom model which always adds selection dates.
     */
    public static class AddingDaySelectionModel extends DaySelectionModel {
        
        Date lastAdded;
        
        @Override
        public void setSelectionInterval(Date startDate, Date endDate) {
            addSelectionInterval(startDate, endDate);
        }
        
        @Override
        public void addSelectionInterval(Date startDate, Date endDate) {
            if (endDate.before(startDate)) return;
            super.addSelectionInterval(startDate, endDate);
            // PENDING: need to do better to cope with unselectables
            if (isSelected(endDate)) {
              lastAdded = endDate;
            } else if (isSelected(startDate)) {
                lastAdded = startDate;
            } else {
                lastAdded = null;
            }
        }
        
        
        @Override
        public void clearSelection() {
            lastAdded = null;
            super.clearSelection();
        }

        public Date getLastAddedDate() {
            return lastAdded;
        }
    }
    /**
     * Issue #1196-swingx: must not allow edits if disabled.
     * 
     */
    public void interactiveNotEnabled() {
        final JXDatePicker picker = new JXDatePicker();
        Calendar cal = picker.getMonthView().getCalendar();
        cal.add(Calendar.MONTH, 5);
        picker.setDate(cal.getTime());
        JXFrame frame = wrapInFrame(picker, "enabled?");
        Action enabled = new AbstractAction("toggle enabled") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                picker.setEnabled(!picker.isEnabled());
                
            }
        };
        addAction(frame, enabled);
        show(frame);
    }
    
    /**
     * Issue #910-swingx: commitToday must not be allowed if field not editable.
     * 
     */
    public void interactiveNotEditableCommitToday() {
        JXDatePicker picker = new JXDatePicker();
        Calendar cal = picker.getMonthView().getCalendar();
        cal.add(Calendar.MONTH, 5);
        picker.setDate(cal.getTime());
        picker.setEditable(false);
        showInFrame(picker, "not editable ");
    }
    
    /**
     * Issue #910-swingx: commitToday must not be allowed if field not editable.
     * 
     */
    public void interactiveNotEditableCompareTextField() {
        final JXDatePicker picker = new JXDatePicker(new Date());
        final JFormattedTextField field = new JFormattedTextField(picker.getDate());
        field.setEditable(false);
        PropertyChangeListener l = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("date".equals(evt.getPropertyName())) {
                    if (picker.getDate() != null)
                     field.setValue(picker.getDate());
                }
            }
            
        };
        picker.addPropertyChangeListener(l);
        JXPanel panel = new JXPanel();
        panel.add(picker);
        panel.add(field);
        showInFrame(panel, "not editable field ");
    }
    /**
     * From forum: have spinner with the same timezone.
     */
    public void interactiveTimeZoneFormat() {
        final JSpinner spinner = new JSpinner(new SpinnerDateModel());
        final JXDatePicker picker = new JXDatePicker();
        picker.setTimeZone(TimeZone.getTimeZone("GMT-10"));
        picker.setDate(new Date());
        spinner.setValue(picker.getDate());
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", picker.getLocale());
        format.setTimeZone(picker.getTimeZone());
        JFormattedTextField textField = ((DefaultEditor) spinner.getEditor()).getTextField();
        textField.setFormatterFactory(
                new DefaultFormatterFactory(new DateFormatter(format)));
        PropertyChangeListener l = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("date".equals(evt.getPropertyName())) {
                    if (picker.getDate() != null)
                     spinner.setValue(picker.getDate());
                }
            }
            
        };
        picker.addPropertyChangeListener(l);
        JXPanel panel = new JXPanel();
        panel.add(picker);
        panel.add(spinner);
        showInFrame(panel, "formats in picker and spinner");
    }
    /**
     * Issue #565-swingx: popup not closed if open and 
     * clicking into other focus-tricksing component (like
     * picker, combo).
     * 
     * Issue #1011-swingx: popup of first picker opened on click on second
     * (mac only?)
     * 
     * And: Compare picker and combo behaviour on toggle lf.
     * 
     * Not really comparable: the combo has complete control over the popup, re-creates
     * both popup and content on install.
     * 
     */
    public void interactivePopupPickerCompareCombo() {
//      Trace14.keyboardFocusManager(true);
       final JXDatePicker picker = new JXDatePicker();
       picker.getEditor().setName("first DateField");
        JComboBox box = new JComboBox(new Object[] {"one", "twooooooo", "threeeeeeeeeeee", "GOOO!"});
        box.setEditable(true);
        JComponent comp = new JPanel();
        comp.add(picker);
        comp.add(new JXDatePicker(new Date()));
        comp.add(box);
        JXFrame frame = wrapInFrame(comp, "compare combo <-> picker, picker <-> picker", true);
        addMessage(frame, "open popup, click in other picker/combo - popup must close and other focused");
        show(frame);
    }
    
    /**
     * Issue #1011-swingx: popup of first picker opened on click on second
     * (mac only?)
     * 
     */
    public void interactivePopupTwoPickers() {
//      Trace14.keyboardFocusManager(true);
       final JXDatePicker picker = new JXDatePicker();
       picker.getEditor().setName("first DateField");
        JComponent comp = new JPanel();
        comp.add(picker);
        JXDatePicker other = new JXDatePicker(new Date());
        comp.add(other);
//        other.setFocusable(false);
//        picker.setFocusable(false);
        JXFrame frame = wrapInFrame(comp, "popup: picker <-> picker", true);
        addMessage(frame, "click on second opens popup of first (mac only?)");
        show(frame, 400, 400);
    }

    
    /**
     * Visually characterize focus behaviour.
     * 
     * Issue #577-swingx: JXDatePicker focus cleanup.
     * After commit/cancel in popup: picker's editor should be focused.
     * 
     * 
     * Issue #757-swingx: JXDatePicker inconsistent focusLost firing.
     * 
     * JXDatePicker must not fire focusLost, the picker's editor should.
     * 
     * New (?) problem: after closing focused popup by clicking into 
     * another the focus is in the picker's editor and can't be moved
     * with tab
     * - open popup, 
     * - focus popup (by clicking next month, no keyboard, nor commit/cancel)
     * - click into textfield: popup closed, picker editor has focus
     *  
     * Independent of forcing focus into picker itself or its editor on open. 
     * Looks dependent on heavyweight popup: okay on resizing the frame so 
     * that the popup fits in.
     * 
     * 
     */
    public void interactiveFocusEventOnTogglePopup() {
        JXDatePicker picker = new JXDatePicker();
        final Action togglePopup = picker.getActionMap().get("TOGGLE_POPUP");
        JComboBox box = new JComboBox(new String[] {"one", "twos"});
        box.setEditable(true);
        FocusListener l = new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                if (e.isTemporary()) return;
                String source = e.getSource().getClass().getSimpleName();
                LOG.info("focus gained from: " + source);
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (e.isTemporary()) return;
                String source = e.getSource().getClass().getSimpleName();
                LOG.info("focus lost from: " + source);
            }};
        picker.getEditor().addFocusListener(l); 
        picker.addFocusListener(l);
        box.addFocusListener(l);
        box.getEditor().getEditorComponent().addFocusListener(l);
        JComponent panel = new JPanel();
        panel.add(box);
        panel.add(picker);
        panel.add(new JTextField("something to focus"));
        JXFrame frame = showInFrame(panel, "E: FocusEvents on editor");
        addAction(frame, togglePopup);
        frame.pack();
    }



    /**
     * Issue #568-swingx: DatePicker must not reset time fields.
     * 
     * Behaviour defined by selection model of monthView. While the default 
     * (DaySelectionModel) normalizes the dates to the start of the day in the
     * model's calendar coordinates, a SingleDaySelectionModel keeps the date as-is.
     * For now, need to explicitly set. <p>
     * 
     * Note: picking a date in the monthView still resets. And may lead to 
     * inconsistent fields (picker.date different from editor/model selected) the
     * latter being normalized? Not reproducible here - in the reported example 
     * the time-part was hard-set when getting an actionEvent from the monthView.
     */
    public void interactiveKeepTimeFields() {
        final JXDatePicker picker = new JXDatePicker();
        DateSelectionModel selectionModel = new SingleDaySelectionModel();
        picker.getMonthView().setSelectionModel(selectionModel);
        picker.setDate(new Date());
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.FULL);
        picker.setFormats(format);
        final JFormattedTextField field = new JFormattedTextField(format);
        field.setValue(picker.getDate());
        PropertyChangeListener l = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("date".equals(evt.getPropertyName())) {
                    field.setValue(evt.getNewValue());
                    LOG.info("picker/editor/monthView: " + picker.getDate() + "/" 
                           + picker.getEditor().getValue() + "/"
                           + picker.getMonthView().getSelectionDate());
                }
                
            }
            
        };
        picker.addPropertyChangeListener(l);
        Action setDate = new AbstractActionExt("set date") {

            @Override
            public void actionPerformed(ActionEvent e) {
                picker.setDate(new Date());
                
            }
            
        };
        JComponent box = Box.createHorizontalBox();
        box.add(picker);
        box.add(field);
        JXFrame frame = wrapInFrame(box, "time fields");
        addAction(frame, setDate);
        frame.pack();
        frame.setVisible(true);
        
    }

    /**
     * Issue #568-swingx: DatePicker must not reset time fields.
     * 
     * Behaviour defined by selection model of monthView. While the default 
     * (DaySelectionModel) normalizes the dates to the start of the day in the
     * model's calendar coordinates, a SingleDaySelectionModel keeps the date as-is.
     * For now, need to explicitly set. <p>
     * 
     * Note: picking a date in the monthView still resets. And may lead to 
     * inconsistent fields (picker.date different from editor/model selected) the
     * latter being normalized? Not reproducible here - in the reported example 
     * the time-part was hard-set when getting an actionEvent from the monthView.
     */
    public void interactiveKeepEndOfDay() {
        final JXDatePicker picker = new JXDatePicker();
        SingleDaySelectionModel selectionModel = new SingleDaySelectionModel() {

            @Override
            public Date getNormalizedDate(Date date) {
                return endOfDay(date);
            }
            
        };
        picker.getMonthView().setSelectionModel(selectionModel);
        picker.setDate(new Date());
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.FULL);
        picker.setFormats(format);
        final JFormattedTextField field = new JFormattedTextField(format);
        field.setValue(picker.getDate());
        PropertyChangeListener l = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("date".equals(evt.getPropertyName())) {
                    field.setValue(evt.getNewValue());
                    LOG.info("picker/editor/monthView: " + picker.getDate() + "/" 
                           + picker.getEditor().getValue() + "/"
                           + picker.getMonthView().getSelectionDate());
                }
                
            }
            
        };
        picker.addPropertyChangeListener(l);
        Action setDate = new AbstractActionExt("set date") {

            @Override
            public void actionPerformed(ActionEvent e) {
                picker.setDate(new Date());
                
            }
            
        };
        JComponent box = Box.createHorizontalBox();
        box.add(picker);
        box.add(field);
        JXFrame frame = wrapInFrame(box, "end of day");
        addAction(frame, setDate);
        frame.pack();
        frame.setVisible(true);
        
    }
    /**
     * Issue #706-swingx: picker doesn't update monthView.
     * 
     */
    public void interactiveUpdateUIPickerMonthView() {
        final JXDatePicker picker = new JXDatePicker();
        JXFrame frame = showInFrame(picker, "picker update ui");
        Action action = new AbstractActionExt("toggleUI") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String uiClass = (String) UIManager.get(JXMonthView.uiClassID);
                boolean custom = uiClass.indexOf("Custom") > 0;
                if (!custom) {
                    UIManager.put(JXMonthView.uiClassID, "org.jdesktop.swingx.test.CustomMonthViewUI");
                } else {
                    UIManager.put(JXMonthView.uiClassID, null);
                }
                picker.updateUI();
                custom = !custom;
            }
            
        };
        addAction(frame, action);
        frame.pack();
    };
    

    /**
     * Issue #1292-swingx: prefsize growing on inserting text into empty editor.
     * 
     * PrefSize should be independent of empty/filled picker. 
     * If not, the initial size might appear kind of collapsed.
     *  
     * adapted code example from bug report: revalidate on text insert/remove  
     * Note: this simulation doesn't survive a LAF change - picker's editor is recreated
     * and listener not re-wired. 
     */
    public void interactivePrefSizeOnInsert() {
        final JPanel panel = new JPanel();
        final JXDatePicker picker = new JXDatePicker();
        final JFormattedTextField field = new JFormattedTextField();
        DocumentListener l = new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                revalidate();
                
            }
            /**
             * @param panel
             */
            private void revalidate() {
                panel.revalidate();
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {
                revalidate();
            }


            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        };
        picker.getEditor().getDocument().addDocumentListener(l);
        field.getDocument().addDocumentListener(l);
        
        JComboBox box = new JComboBox(new Object[] {});
        box.setEditable(true);
        ((JTextComponent) box.getEditor().getEditorComponent()).getDocument().addDocumentListener(l);
        panel.add(field);
        panel.add(picker);
        panel.add(box);
        JXFrame frame = wrapInFrame(panel, "compare prefSize on insert");
        addMessage(frame, "type text and compare formatted/picker/combo");
        show(frame);
    }
    
    /**
     * Issue #764-swingx: JXDatePicker sizing.
     * 
     * Compare pref size with/-out date initially. 
     * - null date is slightly narrower than not null
     * - formats using the day of week are cut a bit (for "long" day names like wed)
     * - a formatted text field is slightly off, by the width of the caret
     */
    public void interactiveLocalePrefSize() {
        // wednesday - has width problems
        calendar.set(2008, Calendar.FEBRUARY, 20);
        Date date = calendar.getTime();
        String formatString = "EEE MM/dd/yyyy";
        LayoutManager layout = new VerticalLayoutPref();
        JComponent fieldsNull = new JPanel(layout);
        addFormattedTextField(fieldsNull, Locale.US, null, formatString);
        addFormattedTextField(fieldsNull, Locale.UK, null, formatString);
        addFormattedTextField(fieldsNull, Locale.GERMAN, null, formatString);
        addFormattedTextField(fieldsNull, Locale.ITALIAN, null, formatString);
        JComponent fields = new JPanel(layout);
        addFormattedTextField(fields, Locale.US, date, formatString);
        addFormattedTextField(fields, Locale.UK, date, formatString);
        addFormattedTextField(fields, Locale.GERMAN, date, formatString);
        addFormattedTextField(fields, Locale.ITALIAN, date, formatString);
        JComponent other = new JPanel(layout);
        addDatePickerWithLocaleSet(other, Locale.US, date, formatString);
        addDatePickerWithLocaleSet(other, Locale.UK, date, formatString);
        addDatePickerWithLocaleSet(other, Locale.GERMAN, date, formatString);
        addDatePickerWithLocaleSet(other, Locale.ITALIAN, date, formatString);
        JComponent comp = new JPanel(layout);
        addDatePickerWithLocaleSet(comp, Locale.US, null, formatString);
        addDatePickerWithLocaleSet(comp, Locale.UK, null, formatString);
        addDatePickerWithLocaleSet(comp, Locale.GERMAN, null, formatString);
        addDatePickerWithLocaleSet(comp, Locale.ITALIAN, null, formatString);
        JComponent outer = Box.createHorizontalBox();
        outer.add(other);
        outer.add(comp);
        outer.add(fields);
        outer.add(fieldsNull);
        JXFrame frame = wrapInFrame(outer, "Sizing DatePicker");
        addMessage(frame, "rows: locales, columns: picker/formatted field");
        show(frame);
    }
    
    /**
     * Issue #764-swingx: JXDatePicker sizing.
     * 
     * Compare pref size with/-out date initially. 
     * - null date is slightly narrower than not null
     * - formats using the day of week are cut a bit (for "long" day names like wed)
     * - a formatted text field is slightly off, by the width of the caret
     */
    public void interactiveLocalePrefSize2() {
        // wednesday - has width problems
        calendar.set(2008, Calendar.FEBRUARY, 20);
        Date date = calendar.getTime();
        String formatString = "EEE MM/dd/yyyy";
        LayoutManager layout = new VerticalLayoutPref();
        
        final JComponent german = new JPanel(layout);
        addFormattedTextField(german, Locale.GERMAN, date, formatString);
        addDatePickerWithLocaleSet(german, Locale.GERMAN, date, formatString);
        addDatePickerWithLocaleSet(german, Locale.GERMAN, null, formatString);
        addFormattedTextField(german, Locale.GERMAN, null, formatString);
        
        JComponent italian = new JPanel(layout);
        addFormattedTextField(italian, Locale.ITALIAN, date, formatString);
        addDatePickerWithLocaleSet(italian, Locale.ITALIAN, date, formatString);
        addDatePickerWithLocaleSet(italian, Locale.ITALIAN, null, formatString);
        addFormattedTextField(italian, Locale.ITALIAN, null, formatString);
        
        JComponent uk = new JPanel(layout);
        addFormattedTextField(uk, Locale.UK, date, formatString);
        addDatePickerWithLocaleSet(uk, Locale.UK, date, formatString);
        addDatePickerWithLocaleSet(uk, Locale.UK, null, formatString);
        addFormattedTextField(uk, Locale.UK, null, formatString);
        
        JComponent us = new JPanel(layout);
        addFormattedTextField(us, Locale.US, date, formatString);
        addDatePickerWithLocaleSet(us, Locale.US, date, formatString);
        addDatePickerWithLocaleSet(us, Locale.US, null, formatString);
        addFormattedTextField(us, Locale.US, null, formatString);
        
        JComponent outer = Box.createHorizontalBox();
        outer.add(us);
        outer.add(uk);
        outer.add(german);
        outer.add(italian);
        final JXFrame frame = wrapInFrame(outer, "Sizing DatePicker");
        Action pack = new AbstractAction("pack") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                JFormattedTextField field = ((JXDatePicker) german.getComponent(1)).getEditor();
                LOG.info("pref " + field.getValue() + field.getText() + field.getPreferredSize());
                frame.pack();
            }
        };
        addAction(frame, pack);
        addMessage(frame, "rows: picker/formatted field, columns: locales");
        show(frame);
    }

    
    

    /**
     * Instantiates a datePicker using the default constructor, set
     * its locale to the given and adds it to the comp.
     * @param comp the container to add the picker to
     * @param uk the locale to use.
     */
    private void addDatePickerWithLocaleSet(JComponent comp, Locale uk, Date date, String formatString) {
        JXDatePicker datePicker = new JXDatePicker(date);
        datePicker.setLocale(uk);
        if (formatString != null) {
            DateFormat format = new SimpleDateFormat(formatString, uk);
            datePicker.setFormats(format);
        }
        comp.add(datePicker);
    }

    /**
     * Instantiates a datePicker using the default constructor, set
     * its locale to the given and adds it to the comp.
     * @param comp the container to add the picker to
     * @param uk the locale to use.
     */
    private void addFormattedTextField(JComponent comp, Locale uk, Date date, String formatString) {
        JFormattedTextField datePicker;
        if (formatString != null) {
            DateFormat format = new SimpleDateFormat(formatString, uk);
            datePicker = new JFormattedTextField(format);
        } else {
            datePicker = new JFormattedTextField();
        }
        datePicker.setValue(date);
        comp.add(datePicker);
    }


    /**
     * Issue #665-swingx: make JXDatePicker Locale-aware.
     * 
     * Here: instantiate the picker with a non-default locale. 
     * Check that the dates in LinkPanel and editor 
     * are formatted as appropriate for the Locale 
     */
    public void interactiveLocaleConstructor() {
        JComponent other = new JPanel();
        // wednesday - has width problems
        calendar.set(2008, Calendar.FEBRUARY, 20);
        Date date = calendar.getTime();
        addDatePickerWithLocaleConstructor(other, Locale.US, date);
        addDatePickerWithLocaleConstructor(other, Locale.UK, date);
        addDatePickerWithLocaleConstructor(other, Locale.GERMAN, date);
        addDatePickerWithLocaleConstructor(other, Locale.ITALIAN, date);
        JComponent comp = new JPanel();
        addDatePickerWithLocaleConstructor(comp, Locale.US, null);
        addDatePickerWithLocaleConstructor(comp, Locale.UK, null);
        addDatePickerWithLocaleConstructor(comp, Locale.GERMAN, null);
        addDatePickerWithLocaleConstructor(comp, Locale.ITALIAN, null);
        JComponent outer = Box.createVerticalBox();
        outer.add(other);
        outer.add(comp);
        showInFrame(outer, "Localized DatePicker: constructor");
    }

    /**
     * Instantiates a datePicker using the constructor with the given locale and
     * adds it to the comp.
     * @param comp the container to add the picker to
     * @param uk the locale to use.
     */
    private void addDatePickerWithLocaleConstructor(JComponent comp, Locale uk, Date date) {
        JXDatePicker datePicker = new JXDatePicker(uk);
        datePicker.setDate(date);
        comp.add(new JLabel(uk.getDisplayName()));
        comp.add(datePicker);
    }

    /**
     * Issue #665-swingx: make JXDatePicker Locale-aware.
     * 
     * Tests reaction to default locales set via both JComponent.setDefault and
     * Locale.setDefault. Going that way, catches the locales fine.
     * 
     * Also Issue #681-swingx - the first row of days in the monthview
     * overlaps with the day names for locales which have the monday as the 
     * first day of week. 
     */
    public void interactiveLocaleDefault() {
        JComponent comp = new JPanel();
        Locale old = addDatePickerWithLocale(comp, Locale.US);
        addDatePickerWithLocale(comp, Locale.UK);
        addDatePickerWithLocale(comp, Locale.GERMAN);
        addDatePickerWithLocale(comp, Locale.ITALIAN);
        showInFrame(comp, "DatePicker takes default Locale");
        setLocale(old);
    }

    /**
     * Sets the default Locale to the given, instantiates a JXDatePicker with
     * default Locale and adds it to the given component. Returns the previous 
     * default Locale.
     *  
     * @param comp the container to add the picker to
     * @param uk the new default Locale
     *  
     * @return the previous default Locale
     */
    private Locale addDatePickerWithLocale(JComponent comp, Locale uk) {
        Locale old = setLocale(uk);
        JXDatePicker datePicker = new JXDatePicker();
        comp.add(new JLabel(uk.getDisplayName()));
        comp.add(datePicker);
        return old;
    }

    /**
     * Sets default Locale (on Locale and JComponent) to the given Locale and
     * returns the previous default.
     * 
     * @param locale the default Locale to set.
     * @return the previous default.
     */
    private Locale setLocale(Locale locale) {
        Locale old = JComponent.getDefaultLocale();
        JComponent.setDefaultLocale(locale);
        Locale.setDefault(locale);
        return old;
    }


    /**
     * Issue #566-swingx: JXRootPane eats picker's popup esc.
     * to reproduce: open the picker's popup the press esc -
     * not closed. Same with combo is working.
     *
     */
    public void interactiveXRootPaneEatsEscape() {
        JXDatePicker picker = new JXDatePicker();
        JComboBox box = new JComboBox(new String[] {"one", "twos"});
        box.setEditable(true);
        JComponent panel = new JPanel();
        panel.add(picker);
        panel.add(box);
        showInFrame(panel, "Escape key");
    }
    

    /**
     * visual check that toggling the panel adds/removes it
     * and installs the keybindings.
     *
     */
    public void interactiveLinkPanelSet() {
        final JXDatePicker picker = new JXDatePicker();
        final JPanel panel = picker.getLinkPanel();
        // initial null okay
        JXFrame frame = showInFrame(picker, "null panel");
        Action toggleLinkPanel = new AbstractAction("toggleLinkPanel <-> null") {

            @Override
            public void actionPerformed(ActionEvent e) {
                boolean hasLinkPanel = picker.getLinkPanel() != null;
                picker.setLinkPanel(hasLinkPanel ? null : panel);
            }
            
        };
        addAction(frame, toggleLinkPanel);
        frame.pack();
    }
  
    /**
     * Checking PropertyChangeEvent: report in forum that no event for date fired.
     * here not reproducible.. problem of WizardPage?
     */
    public void interactivePCEvent() {
        JXDatePicker picker = new JXDatePicker(new Date());
        JFormattedTextField textField = new JFormattedTextField(DateFormat.getDateInstance());
        textField.setValue(new Date());
        
        PropertyChangeListener l = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent e) {
                LOG.info("got pce from: " + e.getSource().getClass().getSimpleName() + 
                        "\n" + e.getPropertyName() + e.getNewValue());
                
            }
            
        };
        textField.addPropertyChangeListener(l);
        picker.addPropertyChangeListener(l);
//        picker.getMonthView().addActionListener(l);
        JPanel panel = new JPanel();
        panel.add(textField);
        panel.add(picker);
        JTabbedPane tab = new JTabbedPane();
        tab.add("fields", panel);
        tab.add("dummy", new JTextField("dummy"));
        JXFrame frame = showInFrame(tab, "Compare propertChange events: keyboard/mouse");
        frame.pack();
        
    }
    

    /**
     * Issue #235-swingx: action events
     * 
     * Compare textfield, formatted, picker, combo after keyboard.
     * 
     * TextField
     * - simple field fires on enter always
     * - formatted fire on enter if value had been edited
     *
     * ComboBox
     * - fires on enter always
     * - fires on click in dropdown
     * 
     * Calendar widgets after cleanup: 
     * 
     * Picker
     * - fires "datePickerCommit" on click (actually released) into monthView
     * - fires "datePickerCommit"/-"Cancel" on enter/escape, both in input field
     * and if popup is open
     * 
     * MonthView
     * - fires "monthViewCommit" on click (actually released)
     * - fires "monthViewCommit"/-"Cancel" on enter/esc 
     * 
     * 
     */
    public void interactiveActionEventSetAction() {
        JXDatePicker picker = new JXDatePicker();
        JTextField simpleField = new JTextField("simple field");
        JFormattedTextField textField = new JFormattedTextField(DateFormat.getDateInstance());
        textField.setValue(new Date());
        JComboBox box = new JComboBox(new Object[] {"one", "two", "three"});
        box.setEditable(true);
        JComboBox nonEditableBox = new JComboBox(new Object[] {"one", "two", "three"});
        final Action l = new AbstractActionExt("recived") {

            @Override
            public void actionPerformed(ActionEvent e) {
                LOG.info("got action from: " + e.getSource().getClass().getName() + 
                        "\n" + e);
            }
            
        };
        simpleField.setAction(l);
        textField.setAction(l);
        // picker doesn't have action-related api
        picker.addActionListener(l);
//        picker.getMonthView().addActionListener(l);
        box.setAction(l);
        nonEditableBox.setAction(l);
        
        JPanel panel = new JPanel();
        panel.add(simpleField);
        panel.add(textField);
        panel.add(picker);
        panel.add(box);
        panel.add(new JCheckBox(l));
        panel.add(nonEditableBox);
        JXFrame frame = wrapInFrame(panel, "SetAction - Compare action events: keyboard/mouse");
        Action toggleEnabled = new AbstractActionExt("toggleEnabledAction") {

            @Override
            public void actionPerformed(ActionEvent e) {
                l.setEnabled(!l.isEnabled());
            }
            
        };
        addAction(frame, toggleEnabled);
        show(frame);
    }

    /**
     * Issue #235-swingx: action events
     * 
     * Compare textfield, formatted, picker and combo: programatic change.
     * - only combo fires
     * 
     */
    public void interactiveActionEventSetValue() {
        final JXDatePicker picker = new JXDatePicker();
        final JTextField simpleField = new JTextField("simple field");
        final JFormattedTextField textField = new JFormattedTextField(DateFormat.getDateInstance());
        textField.setValue(new Date());
        final JComboBox box = new JComboBox(new Object[] {"one", "two", "three"});
        box.setEditable(true);
        
        ActionListener l = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                LOG.info("got action from: " + e.getSource().getClass().getName() + 
                        "\n" + e);
            }
            
        };
        simpleField.addActionListener(l);
        textField.addActionListener(l);
        picker.addActionListener(l);
        picker.getMonthView().addActionListener(l);
        box.addActionListener(l);
        Action action = new AbstractAction("set new value") {
            int dayToAdd = 1;
            @Override
            public void actionPerformed(ActionEvent e) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_MONTH, dayToAdd++);
                Date date = cal.getTime();
                String text = DateFormat.getDateInstance().format(date);
                simpleField.setText(text);
                textField.setValue(date);
                picker.setDate(date);
                box.setSelectedItem(text);
            }
            
        };
        
        JPanel panel = new JPanel();
        panel.add(simpleField);
        panel.add(textField);
        panel.add(picker);
        panel.add(box);
        
        JXFrame frame = showInFrame(panel, "Compare action events: programmatic change");
        addAction(frame, action);
        frame.pack();
    }


    /**
     * Issue #99-swingx: null date and opening popup forces selection.
     * Status? Looks fixed..
     * 
     * Sizing issue if init with null date
     */
    public void interactiveNullDate() {
        JXDatePicker picker = new JXDatePicker();
        showInFrame(picker, "null date in picker");
    }
    
    
    /**
     * Issue #??-swingx: not possible to clear the date.
     * reported on SO: http://stackoverflow.com/q/15133981/203657
     * 
     * not reproducible (maybe was a coding error, missed api)
     * 
     */
    public void interactiveSetNullDate() {
        final JXDatePicker picker = new JXDatePicker(new Date());
        JXFrame frame = wrapInFrame(picker, "null date in picker");
        Action clearDate = new AbstractAction("clear") {

            @Override
            public void actionPerformed(ActionEvent e) {
                // set the date to null
                picker.setDate(null);
                // more model related (if a handle to the model is available
                // clear the selection on the monthView
//                DateSelectionModel model = picker.getMonthView().getSelectionModel();
//                model.clearSelection();
                // don't: the null will not be committed!
//                picker.getEditor().setText("");
//                System.out.println(picker.getDate());
            }
            
        };
        addAction(frame, clearDate);
        show(frame);
    }

    
    /**
     * something weird's going on: the picker's date must be null
     * after setting a monthView with null selection. It is, until
     * shown?
     * Looks fixed during synch control cleanup in datePicker.
     */
    public void interactiveShowPickerSetMonthNull() {
        JXDatePicker picker = new JXDatePicker();
        JXMonthView intervalForPicker = new JXMonthView();
        intervalForPicker.setSelectionMode(SelectionMode.SINGLE_INTERVAL_SELECTION);
        picker.setMonthView(intervalForPicker);
        assertNull(picker.getDate());
        showInFrame(picker, "empty selection in monthView");
        assertNull(picker.getDate());
    }
    
    public void interactiveDatePickerDisplay() {
        JXDatePicker datePicker = new JXDatePicker();
        showInFrame(datePicker, "show date picker");
    }
    

    
    @Override
    protected void setUp() throws Exception {
        calendar = Calendar.getInstance();
    }

    private static class SetPlafAction extends AbstractAction {
        private String plaf;
        
        public SetPlafAction(String name, String plaf) {
            super(name);
            this.plaf = plaf;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Component c = (Component) e.getSource();
                Window w = null;
                
                for (Container p = c.getParent(); p != null; p = p instanceof JPopupMenu ? (Container) ((JPopupMenu) p)
                        .getInvoker() : p.getParent()) {
                    if (p instanceof Window) {
                        w = (Window) p;
                    }
                }
                
                UIManager.setLookAndFeel(plaf);
                SwingUtilities.updateComponentTreeUI(w);
                w.pack();
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            } catch (InstantiationException e1) {
                e1.printStackTrace();
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            } catch (UnsupportedLookAndFeelException e1) {
                e1.printStackTrace();
            }
        }
    }
    
    private JMenuBar createMenuBar() {
        LookAndFeelInfo[] plafs = UIManager.getInstalledLookAndFeels();
        JMenuBar bar = new JMenuBar();
        JMenu menu = new JMenu("Set L&F");
        
        for (LookAndFeelInfo info : plafs) {
            menu.add(new SetPlafAction(info.getName(), info.getClassName()));
        }
        
        bar.add(menu);
        
        return bar;
    }
    
    @Override
    public JXFrame wrapInFrame(JComponent component, String title) {
        JXFrame frame = super.wrapInFrame(component, title);
        if (showMenu) {
            frame.setJMenuBar(createMenuBar());
        }
        return frame;
    }
    


    /**
     * Do nothing, make the test runner happy
     * (would output a warning without a test fixture).
     *
     */
    public void testDummy() {
        
    }
    
}
