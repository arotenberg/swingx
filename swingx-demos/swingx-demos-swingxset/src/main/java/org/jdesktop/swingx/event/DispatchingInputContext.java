/*
 * $Id: DispatchingInputContext.java 4097 2011-11-30 19:22:13Z kschaefe $
 *
 * Copyright 2007 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx.event;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.im.InputContext;
import java.util.Locale;

/**
 * An implementation of InputContext which allows to plug-in a custom 
 * InputEventDispatcher to get notified of all Input- and FocusEvents below
 * the root of the container hierarchy this is installed to. This context
 * is enabled (automatically) if it has both a delegate InputContext and 
 * a dispatcher. If so, it will dispatch the received events to the dispatcher before
 * messaging the delegate. All other methods delegate to the wrapped input context 
 * without additional processing. <p>
 * 
 * Typically, application code need not be aware of this class. JComponents
 * which support InputEventDispatcher will install this transparently. 
 * 
 * @see InputEventDispatcher
 * @see org.jdesktop.swingx.JXDatePicker
 * 
 * @author Jeanette Winzenburg
 * 
 * 
 */
public class DispatchingInputContext extends InputContext {

    private InputEventDispatcher dispatcher;
    private InputContext delegate;

    /**
     * Sets the InputEventDispatcher to use.
     * @param dispatcher
     */
    public void setInputEventDispatcher(InputEventDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    
    public InputEventDispatcher getInputEventDispatcher() {
        return dispatcher;
    }

    /**
     * Returns the InputContext. The returned context depends on
     * whether or not this instance has a EventDispatcher. If so,
     * it returns this
     * 
     * @param delegate the InputContext to delegate all methods to.
     * @return the this or the delegate, if we have a dispatcher or not, 
     *   respectively.
     */
    public InputContext getInputContext(InputContext delegate) {
        if (!hasDispatcher()) {
            resetDelegate();
            return delegate;
        }
        setDelegate(delegate);
        return hasDelegate() ? this : delegate;
    }

//-------------------- super overrides to delegate
    /**
     * {@inheritDoc} <p>
     * 
     * Overridden to dispatch the event to the dispatcher before
     * passing it to the original InputContext if available, does nothing otherwise.
     */
    @Override
    public void dispatchEvent(AWTEvent event) {
        if(!hasDelegate()) {
            return;
        }
        if (hasDispatcher()) {
            getInputEventDispatcher().dispatchEvent(event);
        }
        getDelegate().dispatchEvent(event);
    }

    /**
     * {@inheritDoc} <p>
     * 
     * Overridden to delegate to original InputContext if available, does nothing otherwise.
     */
    @Override
    public void dispose() {
        if(getDelegate() == null) {
            return;
        }
        getDelegate().dispose();
    }

    /**
     * {@inheritDoc} <p>
     * 
     * Overridden to delegate to original InputContext if available, does nothing otherwise.
     */
    @Override
    public void endComposition() {
        if(getDelegate() == null) {
            return;
        }
        getDelegate().endComposition();
    }

    /**
     * {@inheritDoc} <p>
     * 
     * Overridden to delegate to original InputContext if available, does nothing otherwise.
     */
    @Override
    public Object getInputMethodControlObject() {
        if(getDelegate() == null) {
            return null;
        }
        return getDelegate().getInputMethodControlObject();
    }

    /**
     * {@inheritDoc} <p>
     * 
     * Overridden to delegate to original InputContext if available, does nothing otherwise.
     */
    @Override
    public Locale getLocale() {
        if(getDelegate() == null) {
            return null;
        }
        return getDelegate().getLocale();
    }

    /**
     * {@inheritDoc} <p>
     * 
     * Overridden to delegate to original InputContext if available, does nothing otherwise.
     */
    @Override
    public boolean isCompositionEnabled() {
        if(getDelegate() == null) {
            return false;
        }
        return getDelegate().isCompositionEnabled();
    }

    /**
     * {@inheritDoc} <p>
     * 
     * Overridden to delegate to original InputContext if available, does nothing otherwise.
     */
    @Override
    public void reconvert() {
        if(getDelegate() == null) {
            return;
        }
        getDelegate().reconvert();
    }

    /**
     * {@inheritDoc} <p>
     * 
     * Overridden to delegate to original InputContext if available, does nothing otherwise.
     */
    @Override
    public void removeNotify(Component client) {
        if(getDelegate() == null) {
            return;
        }
        getDelegate().removeNotify(client);
    }

    /**
     * {@inheritDoc} <p>
     * 
     * Overridden to delegate to original InputContext if available, does nothing otherwise.
     */
    @Override
    public boolean selectInputMethod(Locale locale) {
        if(getDelegate() == null) {
            return false;
        }
        return getDelegate().selectInputMethod(locale);
    }

    /**
     * {@inheritDoc} <p>
     * 
     * Overridden to delegate to original InputContext if available, does nothing otherwise.
     */
    @Override
    public void setCharacterSubsets(Character.Subset[] subsets) {
        if(getDelegate() == null) {
            return;
        }
        getDelegate().setCharacterSubsets(subsets);
    }

    /**
     * {@inheritDoc} <p>
     * 
     * Overridden to delegate to original InputContext if available, does nothing otherwise.
     */
    @Override
    public void setCompositionEnabled(boolean enable) {
        if(getDelegate() == null) {
            return;
        }
        getDelegate().setCompositionEnabled(enable);
    }

    
//------------------ internals    
    
    private boolean hasDispatcher() {
        return getInputEventDispatcher() != null;
    }

    
    private boolean hasDelegate() {
        return getDelegate() != null;
    }

    private void setDelegate(InputContext delegate) {
        this.delegate = delegate;
    }

    private void resetDelegate() {
        this.delegate = null;
    }

    protected InputContext getDelegate() {
        return delegate;
    }
    
    
}
