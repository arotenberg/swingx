/*
 * Created on 14.12.2009
 *
 */
package org.jdesktop.swingxset;

import java.awt.im.InputContext;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.event.DispatchingInputContext;
import org.jdesktop.swingx.event.InputEventDispatcher;

/**
 * JXFrame with support for custom InputEventDispatcher which will receive 
 * all Input- and FocusEvents for all components in the container hierarchy below
 * the frame. <p>
 * 
 * @see InputEventDispatcher
 * @see AbstractInputEventDispatcher
 */
public class JXDemoFrame extends JXFrame {
    
    
    
    public JXDemoFrame() {
        super();
    }
    
    //---------------- hook for InputEventDispatcher

    private DispatchingInputContext dispatchingContext;
    
    // <snip> Input-/FocusEvent notification 
    // access event dispatcher 
    public InputEventDispatcher getInputEventDispatcher() {
        return getDispatchingInputContext().getInputEventDispatcher();
    }

    public void setInputEventDispatcher(
            InputEventDispatcher dispatcher) {
        getDispatchingInputContext().setInputEventDispatcher(dispatcher);
    }
    // </snip>
    
    @Override
    public InputContext getInputContext() {
        return getDispatchingInputContext().getInputContext(super.getInputContext());
    }

    /**
     * @return the dispatchingContext
     */
    private DispatchingInputContext getDispatchingInputContext() {
        if (dispatchingContext == null) {
            dispatchingContext = new DispatchingInputContext();
        }
        return dispatchingContext;
    }


}
