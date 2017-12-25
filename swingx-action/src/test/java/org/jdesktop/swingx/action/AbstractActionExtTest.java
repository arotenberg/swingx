/*
 * Created on 27.01.2006
 *
 */
package org.jdesktop.swingx.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToggleButton;

import org.jdesktop.test.EDTRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EDTRunner.class)
@SuppressWarnings("nls")
public class AbstractActionExtTest {
    @SuppressWarnings("all")
    private static final Logger LOG = Logger.getLogger(AbstractActionExtTest.class.getName());
    
    protected ActionContainerFactory factory;
    
    @Before
    public void setUp() throws Exception {
        factory = new ActionContainerFactory(null);
    }

    /**
     * Issue #255-swingx: probs in synch selectable button <--> action. 
     * 
     * test that configured button is kept in synch with
     *  action selected state and the other way round. This uses a 
     *  custom configure via the ActionContainerFactory.configureSelectableButton.
     * The direction from button to action is broken.
     */
    @Test
    public void testButtonSelectedSynchAction() {
        AbstractActionExt extAction = createStateAction();
        boolean actionSelected = true;
        extAction.setSelected(actionSelected);
        JToggleButton button = new JToggleButton();
        factory.configureSelectableButton(button, extAction, null);
        // invert action selected and assert that the change is taken up
        // by the button
        extAction.setSelected(!actionSelected);
        assertEquals("button selected must be synched to action", 
                !actionSelected, button.isSelected());
        // reset button 
        button.setSelected(actionSelected);
        // sanity: the button did take the direct selection change
        assertEquals(actionSelected, button.isSelected());
        // assert that changed selected is taken up by action
        assertEquals("action selected must be synched to button", 
                actionSelected, extAction.isSelected());
    }


    /**
     * Issue #255-swingx: probs in synch selectable button <--> action. 
     * 
     * test that configured button is kept in synch with
     *  action selected state and the other way round. This uses the 
     *  ActionContainerFactory.createButton().
     * The direction from button to action is broken.
     */
    @Test
    public void testCreateButtonSelectedSynchAction() {
        AbstractActionExt extAction = createStateAction();
        boolean actionSelected = true;
        extAction.setSelected(actionSelected);
        JToggleButton button = (JToggleButton) factory.createButton(extAction);
        // invert action selected and assert that the change is taken up
        // by the button
        extAction.setSelected(!actionSelected);
        assertEquals("button selected must be synched to action", 
                !actionSelected, button.isSelected());
        // reset button 
        button.setSelected(actionSelected);
        // sanity: the button did take the direct selection change
        assertEquals(actionSelected, button.isSelected());
        // assert that changed selected is taken up by action
        assertEquals("action selected must be synched to button", 
                actionSelected, extAction.isSelected());
    }



    /**
     * Issue #255-swingx: probs in synch selectable button <--> action. 
     *  
     * test that the button always has mostly one
     * Action registered as itemListener and that
     * this registered listener is the same as the buttons
     * action.
     *
     */
    @Test
    public void testButtonOneActionAsItemListener() {
        AbstractActionExt extAction = createStateAction();
        JToggleButton button = new JToggleButton();
        factory.configureSelectableButton(button, extAction, null);
        assertCountAsItemListener(button, extAction, 1);
        factory.configureSelectableButton(button, null, null);
        // assert that the previous action is removed as itemListener
        assertCountAsItemListener(button, extAction, 0);
    }
    /**
     * Issue #255-swingx: probs in synch selectable button <--> action. 
     * test that configured button is kept in synch with
     *  maximal one action's selected state
     */
    @Test
    public void testButtonSelectedNullAction() {
        AbstractActionExt extAction = createStateAction();
        JToggleButton button = new JToggleButton();
        factory.configureSelectableButton(button, extAction, null);
        // we are sure that the button selected is true (has dedicated test)
        // now configure it with a different action, unselected
        AbstractActionExt extActionB = createStateAction();
        factory.configureSelectableButton(button, extActionB, null);
        // invert the old action selected and assert that the change 
        // does not effect the taken up by the button
        extAction.setSelected(!extAction.isSelected());
        assertEquals("button selected must be uneffected by old action",
                extActionB.isSelected(), button.isSelected());
    }

    /**
     * Issue #255-swingx: probs in synch selectable button <--> action. 
     * test that PCLs related to a previous button are 
     * unregistered from the Action after release.
     *
     */
    @Test
    public void testButtonReleaseActionReleasePCL() {
        AbstractActionExt extAction = createStateAction();
        JToggleButton button = new JToggleButton();
        factory.configureSelectableButton(button, extAction, null);
        // sanity: expect it to be 2 - one is the menuitem itself, another 
        // the TogglePCL registered by the ActionContainerFacory
        assertEquals(2, extAction.getPropertyChangeListeners().length);
        // set the button's action to null
        factory.configureSelectableButton(button, null, null);
        // assert that button related PCLs are removed from the action's listener list
        assertEquals(0, extAction.getPropertyChangeListeners().length);
    }

    /**
     * Issue #255-swingx: probs in synch selectable button <--> action. 
     * test that configured button is no longer kept in
     * synch after setting the action to null.
     */
    @Test
    public void testButtonSelectedReleasedSynchAction() {
        AbstractActionExt extAction = createStateAction();
        JToggleButton button = new JToggleButton();
        factory.configureSelectableButton(button, extAction, null);
        // now we unconfigure it with a null action
        factory.configureSelectableButton(button, null, null);
        // invert the old action selected and assert that the change 
        // does not effect the taken up by the button
        boolean oldSelected = button.isSelected();
        extAction.setSelected(!extAction.isSelected());
        assertEquals("button selected must be uneffected by old action",
                oldSelected, button.isSelected());
    }

    /**
     * Issue #255-swingx: probs in synch selectable button <--> action. 
     * test that configured button is kept in synch with
     *  maximal one action's selected state
     */
    @Test
    public void testButtonSelectedMaxOneSynchAction() {
        AbstractActionExt extAction = createStateAction();
        boolean actionSelected = true;
        extAction.setSelected(actionSelected);
        JToggleButton button = new JToggleButton();
        factory.configureSelectableButton(button, extAction, null);
        // we are sure that the button selected is true (has dedicated test)
        // now configure it with a different action, unselected
        AbstractActionExt extActionB = createStateAction();
        factory.configureSelectableButton(button, extActionB, null);
        // sanity: the new action is not effected by the old
        // currently this may accidentally pass because the back direction isn't
        // synched!! 
        assertFalse(extActionB.isSelected());
        assertEquals("button selected must be initialized to new action",
                extActionB.isSelected(), button.isSelected());
        // invert the old action selected and assert that the change 
        // does not effect the taken up by the button
        extAction.setSelected(!actionSelected);
        // need to be done twice, the first toggle produces 
        extAction.setSelected(actionSelected);
        assertEquals("button selected must be uneffected by old action",
                extActionB.isSelected(), button.isSelected());
    }

    /**
     * Issue #255-swingx: probs in synch selectable button <--> action. 
     * test that button is configured with initial action selected state.
     *
     */
    @Test
    public void testButtonSelectedInitialSynchAction() {
        AbstractActionExt extAction = createStateAction();
        boolean actionSelected = true;
        extAction.setSelected(actionSelected);
        JToggleButton button = new JToggleButton();
        boolean buttonSelected = button.isSelected();
        // sanity: different selected state
        assertTrue(actionSelected != buttonSelected);
        factory.configureSelectableButton(button, extAction, null);
        assertEquals("action selection must be unchanged", actionSelected, extAction.isSelected());
        assertEquals("button selected must be initialized", actionSelected, button.isSelected());
    }

    /**
     * test method contract: configureSelectable must throw runtime exception
     * for non-state action.
     *
     */
    @Test
    public void testExceptionOnNonStateAction() {
        AbstractActionExt actionExt = createStateAction();
        actionExt.setStateAction(false);
        JToggleButton button = new JToggleButton();
        try {
           factory.configureSelectableButton(button, actionExt, null);
           fail("configureSelectable didn't throw IllegalArgument for non-state action ");
        } catch (IllegalArgumentException e) {
            // nothing todo - this is what we expect
        } catch (Exception e) {
            fail("caught unexpected exception " + e);
        }
        
    }

    /**
     * Issue #229-swingx: increasing listener list in column actions.
     * 
     * sub-issue: ActionContainerFactory doesn't check if the action is
     * already synchronizing to the same button. 
     */
    @Test
    public void testToggleButtonConfigure() {
        // this should pass after giving the gc "reasonable" chance to
        // have collected the unreachable...
//        assertToggleButtonConfigure(new JToggleButton(), new JToggleButton());

    }

    /**
     * Issue #229-swingx: increasing listener list in column actions.
     * 
     * sub-issue: ActionContainerFactory doesn't check if the action is
     * already synchronizing to the same button. 
     */
    @Test
    public void testToggleButtonConfigureToggleWithSame() {
        assertToggleButtonConfigureWithSame(new JToggleButton());
        assertToggleButtonConfigureWithSame(new JRadioButton());
        assertToggleButtonConfigureWithSame(new JCheckBox());
        assertToggleButtonConfigureWithSame(new JRadioButtonMenuItem());
        assertToggleButtonConfigureWithSame(new JCheckBoxMenuItem());

    }
    
    private void assertToggleButtonConfigureWithSame(AbstractButton button) {
        AbstractActionExt extAction = createStateAction();
        assertEquals(0, extAction.getPropertyChangeListeners().length);
        factory.configureSelectableButton(button, extAction, null);
        // sanity: expect it to be 2 - one is the menuitem itself, another 
        // the TogglePCL registered by the ActionContainerFacory
        
        assertEquals(2, extAction.getPropertyChangeListeners().length);
        factory.configureSelectableButton(button, extAction, null);
        // JW: wrong assumption!! Valid only if first == second, for
        // different buttons we actually expect the listener count to be increased!
        // Note to myself: remove this comment after correcting the method call
        // sequence here in the test ...
        assertEquals(2, extAction.getPropertyChangeListeners().length);
        
    }

    /**
     * Issue #229-swingx: increasing listener list in column actions.
     * 
     * sub-issue: ActionContainerFactory registers the action multiple times to
     * the same button as ItemListener
     */
    @Test
    public void testToggleButtonAddItemListenerToSame() {
        assertAddItemListenerToSame(new JToggleButton());
        assertAddItemListenerToSame(new JRadioButton());
        assertAddItemListenerToSame(new JCheckBox());
        assertAddItemListenerToSame(new JRadioButtonMenuItem());
        assertAddItemListenerToSame(new JCheckBoxMenuItem());
    }
    
    private void assertAddItemListenerToSame(AbstractButton checkBoxItem) {
        AbstractActionExt extAction = createStateAction();
        factory.configureSelectableButton(checkBoxItem, extAction, null);
        assertCountAsItemListener(checkBoxItem, extAction, 1 );
        factory.configureSelectableButton(checkBoxItem, extAction, null);
        assertCountAsItemListener(checkBoxItem, extAction, 1 );
      
    }

    protected AbstractActionExt createStateAction() {
        AbstractActionExt extAction = new AbstractActionExt("dummy") {

            public void actionPerformed(ActionEvent e) {
            }
            
        };
        extAction.setStateAction();
        return extAction;
    }

    /**
     * assert that the given itemListener is registered exactly
     * expectedCount times to the given button.
     * @param checkBoxItem
     * @param extAction
     * @param expectedCount
     */
    protected void assertCountAsItemListener(AbstractButton checkBoxItem, ItemListener extAction, int expectedCount) {
        int count = 0;
        ItemListener[] itemListeners = checkBoxItem.getItemListeners();
        for (int j = 0; j < itemListeners.length; j++) {
            if (extAction == itemListeners[j]) {
                count++;
            }
        }
        assertEquals("ItemListener registration count", expectedCount, count);
        
    }

    /**
     * Issue #4-swinglabs: infinite loop when setting long destricption.
     *
     */
    @Test
    public void testLongDescriptionLoop() {
        AbstractActionExt action = createStateAction();
        action.setLongDescription("some");
    }

    /**
     * core issue: 
     * set enabled via putValue leads to inconsistent state.
     * fixed in jdk6
     */
    @Test
    public void testFireEnabled() {
        Action action = new AbstractAction("dummy") {

            public void actionPerformed(ActionEvent e) {
                // nothing to do
                
            }
            
        };
        PropertyChangeListener l = new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if ("enabled".equals(evt.getPropertyName())) {
                    assertEquals(evt.getNewValue(), ((Action) evt.getSource()).isEnabled());
                }
                
            }
            
        };
        action.addPropertyChangeListener(l);
        action.putValue("enabled", false);
        
    }
}
