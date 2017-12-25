/*
 * Created on 16.04.2009
 *
 */
package org.jdesktop.swingx.event;

import java.awt.AWTEvent;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import org.jdesktop.beans.AbstractBean;

public abstract class AbstractInputEventDispatcher extends AbstractBean 
    implements InputEventDispatcher {

    /**
     * {@inheritDoc}
     * <p/>
     * This method calls the appropriate
     * {@code process<eventType>Event}
     * method for the given class of event.
     */
    public void dispatchEvent(AWTEvent e) {
        if (e instanceof FocusEvent) {
            processFocusEvent((FocusEvent) e);
        } else if (e instanceof MouseEvent) {
            switch (e.getID()) {
                case MouseEvent.MOUSE_PRESSED:
                case MouseEvent.MOUSE_RELEASED:
                case MouseEvent.MOUSE_CLICKED:
                case MouseEvent.MOUSE_ENTERED:
                case MouseEvent.MOUSE_EXITED:
                    processMouseEvent((MouseEvent) e);
                    break;
                case MouseEvent.MOUSE_MOVED:
                case MouseEvent.MOUSE_DRAGGED:
                    processMouseMotionEvent((MouseEvent) e);
                    break;
                case MouseEvent.MOUSE_WHEEL:
                    processMouseWheelEvent((MouseWheelEvent) e);
                    break;
            }
        } else if (e instanceof KeyEvent) {
            processKeyEvent((KeyEvent) e);
        }
    }

    protected void processFocusEvent(FocusEvent e) {
        // TODO Auto-generated method stub
        
    }

    protected void processMouseEvent(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    protected void processMouseMotionEvent(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    protected void processMouseWheelEvent(MouseWheelEvent e) {
        // TODO Auto-generated method stub
        
    }

    protected void processKeyEvent(KeyEvent e) {
        // TODO Auto-generated method stub
        
    }


}
