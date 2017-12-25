/*
 * Created on 24.03.2013
 *
 */
package org.jdesktop.swingx.event;

import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.util.Date;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXDatePicker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Unit and visual test for KeyBindingDispatcher.
 * 
 * @author Jeanette Winzenburg, Berlin
 */
@RunWith(JUnit4.class)
public class KeyBindingDispatcherTest extends InteractiveTestCase {
    
    
    public static void main(String[] args) {
        KeyBindingDispatcherTest test = new KeyBindingDispatcherTest();
        try {
            test.runInteractiveTests();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void interactiveGlobalBinding() {
        final JComponent content = new JPanel();
        Action showOptionPaneAction = new AbstractAction("showOption") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showInputDialog(content, "dummy input");
            }
        };
        content.add(new JButton(showOptionPaneAction));
        content.add(new JTextField(30));
        JComboBox combo = new JComboBox(new Object[]{"one", "otherlongish"});
        combo.setEditable(true);
        content.add(combo);
        content.add(new JXDatePicker(new Date()));
        
        KeyBindingDispatcher d = new KeyBindingDispatcher();
        Action help = new AbstractAction("help") {

            @Override
            public void actionPerformed(ActionEvent e) {
                LOG.info("hit help on: " + e.getSource());
            }
            
        };
        KeyStroke helpStroke = KeyStroke.getKeyStroke("F1");
        d.bind(helpStroke, help);
        
        Action sink = new AbstractAction("catchA") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("sink: " + e);
            }
        };
        KeyStroke sinkStroke = KeyStroke.getKeyStroke("shift typed A");
        d.bind(sinkStroke, sink);
        LOG.info("help/sink key" + d.get(helpStroke) + "/" + d.get(sinkStroke));
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(d);
        showInFrame(content, "global binding");
    }

    private KeyBindingDispatcher dispatcher;
    private Action action;
    private KeyStroke enter;

    @Test
    public void testBindKeyStroke() {
        Object key = "global-enter";
        dispatcher.put(enter, key );
        assertEquals(key, dispatcher.get(enter));
    }

    @Test
    public void testBindAction() {
        Object key = "global-enter";
        dispatcher.put(key, action);
        assertEquals(action, dispatcher.get(key));
    }
    
    @Test
    public void testBindKeyStrokeAction() {
        Object key = "global-enter";
        dispatcher.bind(enter, action, key);
        assertEquals(key, dispatcher.get(enter));
        assertEquals(action, dispatcher.get(key));
    }
    
    @Test
    public void testBindKeyStrokeActionAutoKey() {
        dispatcher.bind(enter, action);
        Object key = dispatcher.get(enter);
        assertNotNull("auto-key must be created", key);
        assertEquals(action, dispatcher.get(key));
    }
    
    @Test
    public void testBindKeyStrokeActionAutoKeyUnique() {
        Action dummy = new AbstractAction() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        };
        dispatcher.bind(enter, action);
        KeyStroke f1 = KeyStroke.getKeyStroke("F1");
        dispatcher.bind(f1 , dummy);
        assertFalse("auto-keys must be unique " + dispatcher.get(f1) + dispatcher.get(enter), 
                dispatcher.get(f1).equals(dispatcher.get(enter)));
    }
    
    @Test(expected= NullPointerException.class)
    public void testBindKeyStrokeActionNullKey() {
        dispatcher.bind(enter,  action, null);
    }
    
    @Override
    @Before
    public void setUp() throws Exception {
        dispatcher = new KeyBindingDispatcher();
        enter = KeyStroke.getKeyStroke("ENTER");
        action = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
            }
            
        };
    }
    
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(KeyBindingDispatcherTest.class.getName());
}
