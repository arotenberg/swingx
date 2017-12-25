/*
 * Created on 17.08.2011
 *
 */
package org.jdesktop.swingx.table;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.table.ColumnControlPopup.ActionGrouper;
import org.jdesktop.test.AncientSwingTeam;

public class ColumnControlButtonVisualCheck extends InteractiveTestCase {

    public static void main(String[] args) {
        ColumnControlButtonVisualCheck test = new ColumnControlButtonVisualCheck();
        try {
//            test.runInteractiveTests();
//            test.runInteractive("ToggleTableModel");
//            test.runInteractive("ColumnAction");
            test.runInteractive("Null");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void interactiveCustomColumnActionGroup() {
        JXTable table = new JXTable(new AncientSwingTeam());
        AbstractActionExt custom = new AbstractActionExt("Custom") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                
            }
        };
        // add a group as value
        custom.putValue(GroupKeyActionGrouper.GROUP_KEY, 0);
        // add to table's action map
        table.getActionMap().put(ColumnControlButton.COLUMN_CONTROL_MARKER + "myCommand", custom);
        final ColumnControlButton control = (ColumnControlButton) table.getColumnControl();
        table.setColumnControlVisible(true);
        JXFrame frame = wrapWithScrollingInFrame(table, "Group actions");
        
        Action toggleGrouper = new AbstractAction("toggleGrouper") {
            
            ActionGrouper current;
            @Override
            public void actionPerformed(ActionEvent e) {
                current = (current == null) ? new GroupKeyActionGrouper() : null;
                control.setActionGrouper(current);
            }
        };
        addAction(frame, toggleGrouper);
        show(frame);
        
    }
    

    /**
     * Implementation of ActionGrouper which groups by action values with
     * key GROUP_KEY.
     */
    public static class GroupKeyActionGrouper implements ActionGrouper {

        /** Marker to support custom grouping of additional actions. (Issue #swingx-968) */
        public static final String GROUP_KEY = "zzzz_group";
        
        @Override
        public <A extends Action> List<List<A>> group(List<A> actions) {
            List<List<A>> result = new ArrayList<List<A>>();
            List<Object> keys = new ArrayList<Object>();
            List<A> noKey = new ArrayList<A>();
            for (A action : actions) {
                Object groupKey = action.getValue(GROUP_KEY);
                if (groupKey != null) {
                    int index = keys.indexOf(groupKey); 
                    if (index < 0) {
                        keys.add(groupKey);
                        index = keys.size() - 1;
                        result.add(new ArrayList<A>());
                    }
                    result.get(index).add(action);
                } else {
                    noKey.add(action);
                }
                
            }
            result.add(0, noKey);
            return result ;
        }
    }



    public void interactiveCustomColumnAction() {
        JXTable table = new JXTable(new AncientSwingTeam());
        ColumnControlButton control = new ColumnControlButton(table) {

            /**
             * @inherited <p>
             */
            @Override
            protected ColumnControlPopup createColumnControlPopup() {
                ColumnControlPopup controlPopup = new DefaultColumnControlPopup() {

                    /**
                     * @inherited <p>
                     */
                    @Override
                    public void addAdditionalActionItems(
                            List<? extends Action> actions) {
                        super.addAdditionalActionItems(actions);
                        String commands = "commands: ";
                        for (Action action : actions) {
                            commands += action
                                    .getValue(Action.ACTION_COMMAND_KEY);
                        }
                        LOG.info(commands);
                    }

                };
                return controlPopup;
            }

        };
        table.setColumnControl(control);
        table.setColumnControlVisible(true);
        showWithScrollingInFrame(table, "custom control");

    }
    /**
     * test if subclasses are allowed to not create a visibility action. This
     * might happen, if they want to exempt certain columns from user
     * interaction.
     * 
     */
    public void interactiveNullVisibilityActionWithHideable() {
        JXTable table = new JXTable();
        ColumnControlButton columnControl = new ColumnControlButton(table) {
            
            @Override
            protected ColumnVisibilityAction createColumnVisibilityAction(
                    TableColumn column) {
                if (column instanceof TableColumnExt
                        && !((TableColumnExt) column).isHideable())
                    return null;
                return super.createColumnVisibilityAction(column);
            }
            
        };
        table.setColumnControl(columnControl);
        table.setColumnControlVisible(true);
        ColumnFactory factory = new ColumnFactory() {
            
            /**
             * @inherited <p>
             */
            @Override
            public void configureTableColumn(TableModel model,
                    TableColumnExt columnExt) {
                super.configureTableColumn(model, columnExt);
                if (columnExt.getModelIndex() == 0) {
                    columnExt.setHideable(false);
                }
            }
            
        };
        table.setColumnFactory(factory);
        table.setModel(new AncientSwingTeam());
        JXFrame frame = wrapWithScrollingInFrame(table,
                "first model column not togglable");
        frame.setVisible(true);
    }
    
    /**
     * Issue #1573-swingx: !hideable column action must be disabled
     * Problem was missing initial synch. 
     */
    public void interactiveNullVisibilityAction() {
        JXTable table = new JXTable(new AncientSwingTeam());
        table.getColumnExt(0).setHideable(false);
        table.setColumnControlVisible(true);
        JXFrame frame = wrapWithScrollingInFrame(table,
                "first model column not hideable - needs custom control");
        frame.setVisible(true);
    }

    /**
     * Issue ??: Column control update on changing table model.
     * Issue ??: add property to control whether or not additional table
     *   actions are included
     */
    public void interactiveTestToggleTableModel() {
        final DefaultTableModel tableModel = new DefaultTableModel(0, 20);
        final JXTable table = new JXTable(tableModel);
        table.setColumnControlVisible(true);
        Action toggleAction = new AbstractAction("Toggle TableModel") {

            @Override
            public void actionPerformed(ActionEvent e) {
                TableModel model = table.getModel();
                table.setModel(model.equals(tableModel) ? new AncientSwingTeam()
                        : tableModel);
            }

        };
        JXFrame frame = wrapWithScrollingInFrame(table,
                "ColumnControl: set tableModel update columns");
        addAction(frame, toggleAction);
        
        Action toggleAdditionalActions = new AbstractAction("toggleAdditionalActions") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                ColumnControlButton button = (ColumnControlButton) table.getColumnControl();
                button.setAdditionalActionsVisible(!button.getAdditionalActionsVisible());
            }
        };
        addAction(frame, toggleAdditionalActions);
        frame.setVisible(true);
    }

    /**
     * Issue ??: Column control on changing column model.
     * 
     */
    public void interactiveTestColumnControlColumnModel() {
        final JXTable table = new JXTable(10, 5);
        table.setColumnControlVisible(true);
        Action toggleAction = new AbstractAction("Set ColumnModel") {

            @Override
            public void actionPerformed(ActionEvent e) {
                table.setColumnModel(new DefaultTableColumnModel());
                table.setModel(new AncientSwingTeam());
                setEnabled(false);
            }

        };
        JXFrame frame = wrapWithScrollingInFrame(table,
                "ColumnControl: set columnModel ext -> core default");
        addAction(frame, toggleAction);
        frame.setVisible(true);
    }

    /**
     * Issue ??: Column control cancontrol update on changing column model.
     * 
     */
    public void interactiveTestColumnControlColumnModelExt() {
        final JXTable table = new JXTable();
        table.setColumnModel(new DefaultTableColumnModel());
        table.setModel(new DefaultTableModel(10, 5));
        table.setColumnControlVisible(true);
        Action toggleAction = new AbstractAction("Set ColumnModelExt") {

            @Override
            public void actionPerformed(ActionEvent e) {
                table.setColumnModel(new DefaultTableColumnModelExt());
                table.setModel(new AncientSwingTeam());
                table.getColumnExt(0).setVisible(false);
                setEnabled(false);
            }

        };
        JXFrame frame = wrapWithScrollingInFrame(table,
                "ColumnControl: set ColumnModel core -> modelExt");
        addAction(frame, toggleAction);
        frame.setVisible(true);
    }

    /**
     * Issue #192: initially invisibility columns are hidden but marked as
     * visible in control.
     * 
     * Issue #38 (swingx): initially invisble columns don't show up in the
     * column control list.
     * 
     * Visual check: first enable column control then set column invisible.
     * 
     */
    public void interactiveTestColumnControlInvisibleColumns() {
        final JXTable table = new JXTable(new AncientSwingTeam());
        table.setColumnControlVisible(true);
        final TableColumnExt firstNameColumn = table.getColumnExt("First Name");
        firstNameColumn.setVisible(false);
        JXFrame frame = wrapWithScrollingInFrame(
                table,
                "ColumnControl (#192, #38-swingx) first enable ColumnControl then column invisible");
        Action toggleHideable = new AbstractActionExt(
                "toggle hideable first Name") {

            @Override
            public void actionPerformed(ActionEvent e) {
                firstNameColumn.setHideable(!firstNameColumn.isHideable());

            }
        };
        addAction(frame, toggleHideable);
        show(frame);
    }

    /**
     * Issue #192: initially invisibility columns are hidden but marked as
     * visible in control.
     * 
     * Issue #38 (swingx): initially invisble columns don't show up in the
     * column control list.
     * 
     * Visual check: first set column invisible then enable column control.
     * 
     */
    public void interactiveTestColumnControlEarlyInvisibleColumns() {
        final JXTable table = new JXTable(new AncientSwingTeam());
        table.getColumnExt("First Name").setVisible(false);
        table.setColumnControlVisible(true);
        JFrame frame = wrapWithScrollingInFrame(
                table,
                "ColumnControl (#192, #38-swingx) first column invisible, the enable columnControl");
        frame.setVisible(true);
    }

    /**
     * Issue #212: programmatically toggle column vis does not work.
     * 
     * Visual check: compare toggle column visibility both via the columnControl
     * and programmatically by button. While the columnControl prevents to hide
     * the very last visible column, developers have full control to do so
     * programatically.
     * 
     * 
     */
    public void interactiveTestColumnControlSetModelToggleInvisibleColumns() {
        final JXTable table = new JXTable();
        table.setColumnControlVisible(true);
        JXFrame frame = wrapWithScrollingInFrame(table,
                "ColumnControl (#212-swingx) setModel and toggle first column invisible");
        frame.setVisible(true);
        table.setModel(new DefaultTableModel(10, 2));
        final TableColumnExt firstNameColumn = table.getColumnExt(1);
        Action action = new AbstractAction("Toggle first name visibility") {

            @Override
            public void actionPerformed(ActionEvent e) {
                firstNameColumn.setVisible(!firstNameColumn.isVisible());

            }

        };
        addAction(frame, action);
    }

    /** 
      * 
      * 
      */
    public void interactiveTestLastVisibleColumn() {
        final JXTable table = new JXTable();
        table.setModel(new DefaultTableModel(10, 2));
        table.setColumnControlVisible(true);

        JFrame frame = wrapWithScrollingInFrame(table,
                "JXTable (#192, #38-swingx) ColumnControl and Visibility of items");
        table.getColumnExt(0).setVisible(false);
        frame.setVisible(true);
    }

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(ColumnControlButtonVisualCheck.class.getName());

}
