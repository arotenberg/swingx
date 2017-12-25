/*
 * Created on 14.12.2009
 *
 */
package org.jdesktop.swingxset;

import java.awt.im.InputContext;

import org.jdesktop.swingx.JXRootPane;
import org.jdesktop.swingx.event.DispatchingInputContext;
import org.jdesktop.swingx.event.InputEventDispatcher;

public class JXDemoRootPane extends JXRootPane {

    //---------------- hook for InputEventDispatcher
    
    private DispatchingInputContext dispatchingContext;
    
    public InputEventDispatcher getInputEventDispatcher() {
        return getDispatchingInputContext().getInputEventDispatcher();
    }

    public void setInputEventDispatcher(
            InputEventDispatcher dispatcher) {
        getDispatchingInputContext().setInputEventDispatcher(dispatcher);
    }
    
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
