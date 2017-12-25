/**
 * 
 */
package org.jdesktop.swingx.binding;

import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingListener;
import org.jdesktop.beansbinding.PropertyStateEvent;
import org.jdesktop.beansbinding.Binding.SyncFailure;

/**
 * @author Karl George Schaefer
 *
 */
public class BindingAdapter implements BindingListener {

    /**
     * {@inheritDoc}
     */
    public void bindingBecameBound(Binding binding) { }

    /**
     * {@inheritDoc}
     */
    public void bindingBecameUnbound(Binding binding) { }

    /**
     * {@inheritDoc}
     */
    public void sourceChanged(Binding binding, PropertyStateEvent event) { }

    /**
     * {@inheritDoc}
     */
    public void syncFailed(Binding binding, SyncFailure failure) { }

    /**
     * {@inheritDoc}
     */
    public void synced(Binding binding) { }

    /**
     * {@inheritDoc}
     */
    public void targetChanged(Binding binding, PropertyStateEvent event) { }

}
