/*
 * $Id: ColumnControlButtonTest.java 4315 2013-09-17 08:41:53Z kleopatra $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.table;

import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.action.ActionContainerFactory;
import org.jdesktop.swingx.icon.EmptyIcon;
import org.jdesktop.swingx.plaf.ColumnControlButtonAddon;
import org.jdesktop.swingx.plaf.LookAndFeelAddons;
import org.jdesktop.swingx.table.ColumnControlButton.ColumnVisibilityAction;
import org.jdesktop.swingx.table.ColumnControlButton.DefaultColumnControlPopup;
import org.jdesktop.swingx.table.ColumnControlButtonVisualCheck.GroupKeyActionGrouper;
import org.jdesktop.test.AncientSwingTeam;
import org.jdesktop.test.PropertyChangeReport;
import org.jdesktop.test.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;


/**
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class ColumnControlButtonTest extends InteractiveTestCase {
    private static final Logger LOG = Logger
            .getLogger(ColumnControlButtonTest.class.getName());
    
    protected TableModel sortableTableModel;
    
    @Before
    public void setUpJ4() throws Exception {
        setUp();
    }
    
    @After
    public void tearDownJ4() throws Exception {
        tearDown();
    }
    
    /**
     * Issue: add support for custom grouping of additional actions 
     * http://java.net/jira/browse/SWINGX-968
     */
    @Test
    public void testAdditonalActionGrouping() {
        JXTable table = new JXTable(10, 4);
        AbstractActionExt custom = new AbstractActionExt("Custom") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                
            }
        };
        custom.putValue(GroupKeyActionGrouper.GROUP_KEY, 0);
        table.getActionMap().put(ColumnControlButton.COLUMN_CONTROL_MARKER + "myCommand", custom);
        ColumnControlButton button = new ColumnControlButton(table);
        button.setActionGrouper(new GroupKeyActionGrouper());
        DefaultColumnControlPopup popup = (DefaultColumnControlPopup) button.getColumnControlPopup();
        assertEquals("additional actions visible, component count expected ", 
                table.getColumnCount() 
                    + 1 /* separator */ + 3 /*default actions with column. prefix*/
                    + 1 /* separator custom group */ + 1 /* custom action */, 
                popup.getPopupMenu().getComponentCount());
    }

    /**
     * Issue http://java.net/jira/browse/SWINGX-1466: add control to show/hide
     *   additional actions
     */
    @Test
    public void testAdditionalActionVisible() {
        JXTable table = new JXTable(10, 4);
        ColumnControlButton button = new ColumnControlButton(table);
        assertEquals(true, button.getAdditionalActionsVisible());
        DefaultColumnControlPopup popup = (DefaultColumnControlPopup) button.getColumnControlPopup();
        assertEquals("additional actions visible, component count expected ", 
                table.getColumnCount() + 1 /* separator */ + 3 /* actions with column. prefix*/, 
                popup.getPopupMenu().getComponentCount());
        PropertyChangeReport report = new PropertyChangeReport(button);
        button.setAdditionalActionsVisible(false);
        TestUtils.assertPropertyChangeEvent(report, "additionalActionsVisible", true, false);
        assertEquals(false, button.getAdditionalActionsVisible());
        assertEquals("additional actions hidden, component count expected ", 
                table.getColumnCount(),
                popup.getPopupMenu().getComponentCount());
        
    }
    
    /**
     * Issue #1573-swingx: !hideable column action must be disabled
     * Problem: isn't initially
     */
    @Test
    public void testColumnVisibilityActionOnHideableInitial() {
        JXTable table = new JXTable(10, 3);
        // set the first column !hideable
        table.getColumnExt(0).setHideable(false);
        table.setColumnControlVisible(true);
        ColumnControlButton columnControl = (ColumnControlButton) table.getColumnControl();
        ColumnVisibilityAction action = columnControl.getColumnVisibilityActions().get(0);
        assertFalse("action must be disabled initially", action.isEnabled());
    }
    
    @Test
    public void testColumnVisibilityActionOnHideable() {
        JXTable table = new JXTable(10, 3);
        table.setColumnControlVisible(true);
        ColumnControlButton columnControl = (ColumnControlButton) table.getColumnControl();
        ColumnVisibilityAction action = columnControl.getColumnVisibilityActions().get(0);
        TableColumnExt columnExt = table.getColumnExt(0);
        // visible property is false
        columnExt.setVisible(false);
        columnExt.setHideable(false);
        assertTrue("visibility action must be selected if not hideable", action.isSelected());
        assertFalse("action must be disabled", action.isEnabled());
        columnExt.setHideable(true);
        assertFalse("visibility action must be unselected if hideable", action.isSelected());
    }

    /**
     * Issue #404-swingx: load column control margin from ui.
     * Test that column control configures itself with the icon from the ui.
     */
    @Test
    public void testColumnControlInitialUpdateInsetsUIResource() {
      ColumnControlButton control = new ColumnControlButton(new JXTable());
      // PENDING JW: why not same? insets can be shared - or not?
      // probably setMargin interferes - is doing some things ... 
      assertEquals("columnControl must have margin from ui", 
                UIManager.getInsets(ColumnControlButton.COLUMN_CONTROL_BUTTON_MARGIN_KEY),
                control.getMargin());
    }

    /**
     * Issue #404-swingx: load column control margin from ui.
     * Test that column control loads the margin.
     */
    @SuppressWarnings("unused")
    @Test
    public void testColumnControlLoadsMargin() {
        // force loading by instantiating a column control
      ColumnControlButton control = new ColumnControlButton(new JXTable());
        assertNotNull("columnControl must load lf-specific icon", 
                UIManager.getInsets(ColumnControlButton.COLUMN_CONTROL_BUTTON_MARGIN_KEY));
    }

    /**
     * Issue #404-swingx: load column control margin from ui.
     * Test that addon loads the margin.
     */
    @Test
    public void testColumnControlAddonLoadsMargin() {
        // direct loading of addon
        LookAndFeelAddons.contribute(new ColumnControlButtonAddon());
        assertNotNull("addon must load lf-specific icon", 
                UIManager.getInsets(ColumnControlButton.COLUMN_CONTROL_BUTTON_MARGIN_KEY));
    }
    
    
    /**
     * Issue #404-swingx: load column control icon from ui.
     * Test that table instantiates the column control with ui icon.
     */
    @Test
    public void testColumnControlInXTable() {
      JXTable table = new JXTable();
      ColumnControlButton control = (ColumnControlButton) table.getColumnControl();
      assertSame("columnControl must have icon from ui", 
                UIManager.getIcon(ColumnControlButton.COLUMN_CONTROL_BUTTON_ICON_KEY),
                control.getIcon());
    }
    
    /**
     * Issue #404-swingx: load column control icon from ui.
     * Test that icon is not updated on updateUI if not uiResource
     */
    @Test
    public void testColumnControlIconNotUpdateNonActionUIResource() {
        ColumnControlButton control = new ColumnControlButton(new JXTable(), new EmptyIcon());
        Icon icon = control.getIcon();
        String lf = UIManager.getLookAndFeel().getName();
        setSystemLF(!defaultToSystemLF);
        if (lf.equals(UIManager.getLookAndFeel().getName())) {
            LOG.info("cannot run layoutOnLFChange - equal LF" + lf);
            return;
        }
        SwingUtilities.updateComponentTreeUI(control);
        assertSame("icon must not be updated on LF change if not UIResource: ", 
                icon, control.getIcon());
    }

    /**
     * Issue #404-swingx: load column control icon from ui.
     * Test that is updated on updateUI
     */
    @Test
    public void testColumnControlIconUpdateActionUIResource() {
        ColumnControlButton control = new ColumnControlButton(new JXTable());
        Icon icon = control.getIcon();
        String lf = UIManager.getLookAndFeel().getName();
        setSystemLF(!defaultToSystemLF);
        if (lf.equals(UIManager.getLookAndFeel().getName())) {
            LOG.info("cannot run layoutOnLFChange - equal LF" + lf);
            return;
        }
        SwingUtilities.updateComponentTreeUI(control);
        assertNotSame("sanity: ui did reload icon: ", icon, 
                UIManager.getIcon(ColumnControlButton.COLUMN_CONTROL_BUTTON_ICON_KEY));

        assertNotSame("icon must be updated on LF change: ", icon, control.getIcon());

    }
    /**
     * Issue #404-swingx: load column control icon from ui.
     * Test that column control configures itself with the icon from the ui.
     */
    @Test
    public void testColumnControlInitialUpdateActionUIResource() {
      ColumnControlButton control = new ColumnControlButton(new JXTable());
      assertSame("columnControl must have icon from ui", 
                UIManager.getIcon(ColumnControlButton.COLUMN_CONTROL_BUTTON_ICON_KEY),
                control.getIcon());
    }

    /**
     * Issue #404-swingx: load column control icon from ui.
     * Test that column control loads the icon.
     */
    @SuppressWarnings("unused")
    @Test
    public void testColumnControlLoadsIcon() {
        // force loading by instantiating a column control
      ColumnControlButton control = new ColumnControlButton(new JXTable());
        assertNotNull("columnControl must load lf-specific icon", 
                UIManager.getIcon(ColumnControlButton.COLUMN_CONTROL_BUTTON_ICON_KEY));
    }

    /**
     * Issue #404-swingx: load column control icon from ui.
     * Test that addon loads the icon.
     */
    @Test
    public void testColumnControlAddonLoadsIcon() {
        // direct loading of addon
        LookAndFeelAddons.contribute(new ColumnControlButtonAddon());
        assertNotNull("addon must load lf-specific icon", 
                UIManager.getIcon(ColumnControlButton.COLUMN_CONTROL_BUTTON_ICON_KEY));
    }
    
    /**
     * Issue #429-swingx: ClassCastException if column identifiers are not
     * String type.
     *
     */
    @Test
    public void testNonStringIdentifier() {
        JXTable table = new JXTable(0, 2);
        table.getColumn(0).setIdentifier(new Object());
        table.setColumnControlVisible(true);
        table.getColumnControl();
    }
    
    @Test
    public void testNotNullColumnModelListener() {
        JXTable table = new JXTable(0, 2);
        table.setColumnControlVisible(true);
        assertNotNull(((ColumnControlButton)table.getColumnControl()).columnModelListener);
    }
    /**
     * Tests if subclasses are allowed to not create a visibility action.
     * This might happen if they want to exempt certain columns from 
     * user interaction.
     *
     */
    @Test
    public void testNullVisibilityAction() {
        JXTable table = new JXTable();
        ColumnControlButton columnControl = new ColumnControlButton(table) {

                    @Override
                    protected ColumnVisibilityAction createColumnVisibilityAction(TableColumn column) {
                        if (column.getModelIndex() == 0) return null;
                        return super.createColumnVisibilityAction(column);
                    }
            
            
        };
        table.setColumnControl(columnControl);
        table.setColumnControlVisible(true);
        table.setModel(sortableTableModel);
    }
    /**
     * test that the actions synch's its own selected property with 
     * the column's visible property. <p>
     * 
     * Looks as if the non-synch of action.setSelected only shows 
     * if the ColumnControlPopup doesn't create a menuitem via ActionFactory: the
     * listeners internally installed via ActionFactory probably take care?
     *  <p>
     * 
     * An analogous test in the incubator (in kleopatra/.../table) did fail
     * for a dialog based custom ColumnControlPopup. For now, changed the visibility
     * action to explicitly update the tableColumn. All tests are passing,
     * but need to further evaluate.
     *
     */
    @Test
    public void testColumnVisibilityAction() {
        JXTable table = new JXTable(10, 3);
        table.setColumnControlVisible(true);
        ColumnControlButton columnControl = (ColumnControlButton) table.getColumnControl();
        ColumnVisibilityAction action = columnControl.getColumnVisibilityActions().get(0);
        TableColumnExt columnExt = table.getColumnExt(0);
        boolean visible = columnExt.isVisible();
        // sanity
        assertTrue(visible);
        assertEquals(columnExt.isVisible(), action.isSelected());
        action.setSelected(!visible);
        // hmmm... here it's working? unexpected
        // synch might be done by the listener's installed by ActionFactor.createMenuItem()?
        assertEquals(!visible, columnExt.isVisible());
    }
 
    /**
     * Tests that enabled property of table and column control is synched dynamically.
     */
    @Test
    public void testDynamicDisabled() {
        JXTable table = new JXTable(10, 3);
        table.setColumnControlVisible(true);
        assertEquals(table.isEnabled(), table.getColumnControl().isEnabled());
        table.setEnabled(!table.isEnabled());
        assertEquals(table.isEnabled(), table.getColumnControl().isEnabled());
    }

    /**
     * suspected: enabled not synched on init. 
     * But is (done in ccb.installTable()). 
     *
     */
    @Test
    public void testInitialDisabled() {
        JXTable table = new JXTable(10, 3);
        table.setEnabled(false);
        table.setColumnControlVisible(true);
        assertEquals(table.isEnabled(), table.getColumnControl().isEnabled());
    }


    /**
     * guarantee that at least one column is always visible.
     *
     */
    @Test
    public void testMinimumColumnCountOne() {
        JXTable table = new JXTable(10, 2);
        table.setColumnControlVisible(true);
        table.getColumnExt(0).setVisible(false);
        assertEquals(1, table.getColumnCount());
    }
    
    /**
     * Issue #229-swingx: increasing listener list in column actions.
     * 
     */
    @Test
    public void testActionListenerCount() {
        JXTable table = new JXTable(10, 1);
        Action action = table.getActionMap().get(JXTable.HORIZONTALSCROLL_ACTION_COMMAND);
        if (!(action instanceof AbstractActionExt)) {
            LOG.info("cannot run testColumnActionListenerCount - action not of type AbstractAction");
            return;
        }
        AbstractActionExt extAction = (AbstractActionExt) action;
        assertTrue(extAction.isStateAction());
        assertEquals(0, extAction.getPropertyChangeListeners().length);
        AbstractButton menuItem = new JCheckBoxMenuItem();
        ActionContainerFactory factory = new ActionContainerFactory(null);
        factory.configureSelectableButton(menuItem, extAction, null);
        // sanity: here the action is bound to a menu item in the columnControl
        // should have one ad
        int initialPCLCount = extAction.getPropertyChangeListeners().length;
        // sanity: expect it to be 2 - one is the menuitem itself, another 
        // the TogglePCL registered by the ActionContainerFacory
        assertEquals(2, initialPCLCount);
        menuItem = new JToggleButton();
        factory.configureSelectableButton(menuItem, extAction, null);
        // 2 menuitems are listening
       assertEquals(2* initialPCLCount, extAction.getPropertyChangeListeners().length);
        
    }
    

    /**
     * Issue #153-swingx: ClassCastException if actionMap key is not a string.
     *
     */
    @Test
    public void testNonStringActionKeys() {
        JXTable table = new JXTable();
        Action l = new AbstractAction("dummy") {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                
            }
            
        };
        table.registerKeyboardAction(l , KeyStroke.getKeyStroke("ESCAPE"), JComponent.WHEN_FOCUSED);
        table.setColumnControlVisible(true);
        table.getColumnControl();
    }
    
    @Test
    public void testColumnControlReleaseAction() {
        final JXTable table = new JXTable(sortableTableModel);
        final TableColumnExt priorityColumn = table.getColumnExt("First Name");
        int listenerCount = priorityColumn.getPropertyChangeListeners().length;
        table.setColumnControlVisible(true);
        // JW: the columnControlButton is created lazily, so we
        // have to access to test if listeners are registered.
        table.getColumnControl();
        assertEquals("numbers of listeners must be increased", listenerCount + 1, 
                priorityColumn.getPropertyChangeListeners().length);
        int totalColumnCount = table.getColumnCount();
        table.removeColumn(priorityColumn);
        assertEquals("number of columns reduced", totalColumnCount - 1, table.getColumnCount());
        assertEquals("all listeners must be removed", 0, 
                priorityColumn.getPropertyChangeListeners().length);
     }

    /**
     * Issue #212-swingx: 
     * 
     * Behaviour change: 
     * <ul>
     * <li>before - guarantee that exactly one column is always visible, 
     *    independent of source of visibiblity change
     * <li> now - this is true for user gesture induced invisible (through 
     *    columnControl, but not for programmatic hiding. It's up to 
     *    developers to not hide all. To alleviate the effects if they 
     *    hide all, the JXTableHeader and ColumnControl is always visible.
     * </ul>
     * This is testing the old behaviour (guarding against regression)
     * Here we directly set the second last visible column to invisible. This 
     * failed if a) column visibility is set after adding the table to a frame
     * and b) model.count = 2.
     *
     */
    @Test
    public void testSetAllColumnsToInvisible() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        final JXTable table = new JXTable(10, 2);
        table.setColumnControlVisible(true);
        wrapWithScrollingInFrame(table, "");
        table.getColumnExt(0).setVisible(false);
        assertEquals(1, table.getColumnCount());
        table.getColumnExt(0).setVisible(false);
        assertEquals(0, table.getColumnCount());
    }
    /**
     * Issue #212-swingx: 
     * 
     * guarantee that exactly one column is always visible if 
     * visibility is toggled via the ColumnControl.
     * 
     * Here we deselect the menuitem.
     * 
     */
    @Test
    public void testSetLastColumnMenuItemToUnselected() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        final JXTable table = new JXTable(10, 1);
        table.setColumnControlVisible(true);
        wrapWithScrollingInFrame(table, "");
        ColumnControlButton columnControl = (ColumnControlButton) table.getColumnControl();
        Component[] items = ((DefaultColumnControlPopup) columnControl.getColumnControlPopup()).getPopupMenu().getComponents();
        ((JMenuItem) items[0]).setSelected(false);
        assertEquals(1, table.getColumnCount());
    }

   /** 
    * Issue #192: initially invisibility columns are hidden
    * but marked as visible in control.
    *
    * Issue #38 (swingx): initially invisble columns don't show up
    * in the column control list.
    * 
    * 
    */
    @Test
   public void testColumnControlInvisibleColumns() {
       final JXTable table = new JXTable(sortableTableModel);
       // columns set to invisible before setting the columnControl
       // will not be inserted into the column control's list
//     table.getColumnExt("Last Name").setVisible(false);
       table.setColumnControlVisible(true);
       int totalColumnCount = table.getColumnCount();
       final TableColumnExt priorityColumn = table.getColumnExt("First Name");
       priorityColumn.setVisible(false);
       ColumnControlButton columnControl = (ColumnControlButton) table.getColumnControl();
       assertNotNull("popup menu not null", columnControl.popup);
       int columnMenuItems = 0;
       Component[] items = ((DefaultColumnControlPopup) columnControl.getColumnControlPopup()).getPopupMenu().getComponents();
       for (int i = 0; i < items.length; i++) {
           if (!(items[i] instanceof JMenuItem)) {
               break;
           }
           columnMenuItems++;
       }
       // wrong assumption - has separator and actions!
       assertEquals("menu items must be equal to columns", totalColumnCount, 
               columnMenuItems);
       JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) ((DefaultColumnControlPopup) columnControl.getColumnControlPopup()).getPopupMenu()
           .getComponent(0);
       // sanit assert
       assertEquals(priorityColumn.getHeaderValue(), menuItem.getText());
       assertEquals("selection of menu must be equal to column visibility", 
               priorityColumn.isVisible(), menuItem.isSelected());
   }

    public ColumnControlButtonTest() {
        super("ColumnControlButtonTest");
    }
    // flag used in setup to explicitly choose LF
    private boolean defaultToSystemLF;

    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // make sure we have the same default for each test
        defaultToSystemLF = false;
        setSystemLF(defaultToSystemLF);
         sortableTableModel = new AncientSwingTeam();
     }

    public static void main(String args[]) {
       setSystemLF(false);
      ColumnControlButtonTest test = new ColumnControlButtonTest();
      try {
        test.runInteractiveTests();
      //    test.runInteractiveTests("interactive.*Column.*");
//          test.runInteractiveTests("interactive.*TableHeader.*");
      //    test.runInteractiveTests("interactive.*SorterP.*");
      //    test.runInteractiveTests("interactive.*Column.*");
      } catch (Exception e) {
          System.err.println("exception when executing interactive tests:");
          e.printStackTrace();
      }
  }
    
}
