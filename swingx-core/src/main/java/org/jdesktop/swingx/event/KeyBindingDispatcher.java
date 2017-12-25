/*
 * Created on 22.03.2013
 *
 */
package org.jdesktop.swingx.event;

import java.awt.KeyEventDispatcher;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;

import org.jdesktop.swingx.util.Contract;

/**
 * KeyEventDispatcher for application-wide KeyBindings.
 * <p>
 * 
 * The mechanism is the same as for component bindings, that is using a pair
 * <code>InputMap/ActionMap</code>. The maps can be accessed directly to bind
 * KeyStrokes and Actions individually. Additionally, the implementation provides
 * convenience methods
 * to bind pairs of <code>KeyStroke/Action</code> easily.
 * Once the dispatcher is added to the KeyboardFocusManager, all KeyEvents will
 * be served first to the bindings (modulo other dispatchers earlier in the chain), 
 * and consumed if an action is available, enabled and performed.
 * <p>
 * 
 * Usage:
 * <pre>
 * <code>
 *     KeyBindingDispatcher d = new KeyBindingDispatcher();
 *     d.bind(myKeyStroke, myAction);
 *     KeyboardFocusManager.getCurrentKeyboardFocusManager()
 *         .addKeyEventDispatcher(d);
 * </code>
 * </pre>
 * 
 * @author Jeanette Winzenburg, Berlin
 */
public class KeyBindingDispatcher implements KeyEventDispatcher {

    private InputMap inputMap;

    private ActionMap actionMap;

    /**
     * Implemented to process the key binding for the event. If a binding is
     * available, the bound action is performed and the event is consumed. Does
     * nothing if the event already is consumed. The returned value is false
     * always to allow other dispatchers to handle the event as well
     * <p>
     * 
     * Subclasses may re-implement to return true and end the dispatch here.
     * <p>
     * 
     * @param e the keyEvent to dispatch
     * @return unconditionally false
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (!e.isConsumed()) {
            if (processKeyBinding(e)) {
                e.consume();
            }
        }
        return false;
    }

    /**
     * Processes the keyBinding for the keyEvent and returns a boolean
     * indicating whether a bound action was performed.
     * 
     * @param e the KeyEvent to handle
     * @return true if a bound action was available, enabled and performed,
     *         false otherwise
     */
    protected boolean processKeyBinding(KeyEvent e) {
        Action action = getAction(e);
        if (action != null && action.isEnabled()) {
            ActionEvent actionEvent = createActionEvent(e, action);
            action.actionPerformed(actionEvent);
            return true;
        }
        return false;
    }

    /**
     * Returns the Action bound to the KeyStroke that corresponds to the
     * keyEvent, or null if none available.
     * 
     * @param e the keyEvent to find a binding for
     * @return the action bound to the keyStroke that corresponds to the
     *         keyEvent or null if not available
     */
    protected Action getAction(KeyEvent e) {
        KeyStroke stroke = KeyStroke.getKeyStrokeForEvent(e);
        Object actionKey = getInputMap().get(stroke);
        if (actionKey != null) {
            return getActionMap().get(actionKey);
        }
        return null;
    }

    /**
     * Creates and returns an ActionEvent that can be passed into the action's
     * actionPerformed. Similar configuration as SwingUtilities.notifyAction,
     * except ignoring standIn actions.
     * 
     * @param e the KeyEvent bound to the action
     * @param action the bound action
     * @return the actionEvent that will be passed into the action's
     *         actionPerformed.
     * 
     * @see javax.swing.SwingUtilities#notifyAction(Action, KeyStroke, KeyEvent,
     *      Object, int)
     */
    protected ActionEvent createActionEvent(KeyEvent e, Action action) {
        Object command0 = action.getValue(Action.ACTION_COMMAND_KEY);
        String command = null;
        if (command0 != null) {
            command = command0.toString();
        } else if (e.getKeyChar() != KeyEvent.CHAR_UNDEFINED) {
            command = String.valueOf(e.getKeyChar());
        }
        ActionEvent actionEvent = new ActionEvent(e.getSource(),
                ActionEvent.ACTION_PERFORMED, command, e.getWhen(),
                e.getModifiers());
        return actionEvent;
    }

    /**
     * Binds the keyStroke and action, using the given key which must not be
     * null.
     * <p>
     * 
     * @param keyStroke the keyStroke to bind
     * @param action the action to bind.
     * @param key the key used for binding, must not be null.
     */
    public void bind(KeyStroke keyStroke, Action action, Object key) {
        Contract.asNotNull(key, "binding key must not be null");
        put(keyStroke, key);
        put(key, action);
    }

    /**
     * Returns the ActionMap used for key bindings, lazily created if not yet
     * done.
     * 
     * @return the ActionMap used for key bindings
     */
    public ActionMap getActionMap() {
        if (actionMap == null) {
            actionMap = new ActionMap();
        }
        return actionMap;
    }

    /**
     * Returns the InputMap used for key bindings, lazily created if not yet
     * done.
     * 
     * @return the InputMap used for key bindings.
     */
    public InputMap getInputMap() {
        if (inputMap == null) {
            inputMap = new InputMap();
        }
        return inputMap;
    }

    /**
     * Binds the keyStroke and action, a key is auto-created.
     * 
     * @param keyStroke the keyStroke to bind
     * @param action the action to bind.
     */
    public void bind(KeyStroke keyStroke, Action action) {
        bind(keyStroke, action, createAutoKey());
    }

    /**
     * Creates and returns a unique actionMapKey.
     * 
     * @return a unique key used for binding in input/actionMap.
     */
    protected Object createAutoKey() {
        long millis = System.currentTimeMillis();
        String key = "auto" + millis;
        while (get(key) != null) {
            key = "auto" + millis--;
        }
        return key;
    }

    /**
     * Registers a binding for <code>keyStroke</code> to
     * <code>actionMapKey</code>. Semantics are the same as the corresponding
     * method in InputMap.
     * 
     * @param keyStroke the keyStroke to bind
     * @param key the key used for binding.
     * 
     * @see javax.swing.InputMap#put(KeyStroke, Object)
     */
    public void put(KeyStroke keyStroke, Object actionMapKey) {
        getInputMap().put(keyStroke, actionMapKey);
    }

    /**
     * Returns the actionMapKey registerd for the keyStroke.
     * 
     * @param keyStroke the keyStroke to bind
     * @return the actionMapKey for the keyStroke or null if unbound.
     */
    public Object get(KeyStroke keyStroke) {
        return getInputMap().get(keyStroke);
    }

    /**
     * Registers a binding for <code>key</code> to <code>action</code>.
     * Semantics are the same as that of the corresponding method in ActionMap.
     * 
     * @param key the key used for binding.
     * @param action the action to bind.
     * 
     * @see javax.swing.ActionMap#put(Object, Action)
     */
    public void put(Object key, Action action) {
        getActionMap().put(key, action);
    }

    /**
     * Returns the Action registered for key or null if none.
     * 
     * @param key the key used for binding.
     * @return the action registered for key or null it none available.
     */
    public Action get(Object key) {
        return getActionMap().get(key);
    }

}
