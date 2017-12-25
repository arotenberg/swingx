/*
 * Created on 22.06.2007
 *
 */
package org.jdesktop.swingx.binding;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;

/**
 * A collection of binding utilities, providing common bindings.<p>
 * 
 * NOTE JW: if anybody wants some static methods, just add them whereever (even  here, 
 * dont care)-- but DONOT remove the old api and thereby BREAK MY client code! 
 * 
 * @author Jeanette Winzenburg
 * @author Karl Schaefer
 */
public class LabelHandler {
    private static final Object LABEL_FOR_BINDING_KEY = new Object();
    
    public void add(JLabel label, JComponent component) {
        label.setLabelFor(component);
        Binding binding =  Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
                component, BeanProperty.create("enabled"), 
                label, BeanProperty.create("enabled"));
        binding.bind();
    }


    /**
     * Creates a binding for the specified {@code label} that tracks the {@code enabled} state of
     * its {@#link JLabel#setLabelFor(Component) labelFor} property. If no component is
     * associated with the label, the label will be enabled.
     * 
     * @param label
     *            the label to bind
     * @throws NullPointerException
     *             if {@code label} is {@code null}
     */
    public static void bindLabelFor(JLabel label) {
        bindLabelFor(label, null);
    }

    /**
     * <p>
     * Creates a binding for the specified {@code label} that tracks the {@code enabled} and {@code
     * visible} states of its {@#link JLabel#setLabelFor(Component) labelFor} property. If
     * no component is associated with the label or the associated component is removed, the label
     * will retain its current enabled and visible states.
     * </p>
     * <p>
     * If a component is supplied, then this method will also set the label-for association.
     * </p>
     * 
     * @param label
     *            the label to bind
     * @param c
     *            the component to associate with the label; may be {@code null}
     * @throws NullPointerException
     *             if {@code label} is {@code null}
     */
    public static void bindLabelFor(JLabel label, Component c) {
        if (label.getClientProperty(LABEL_FOR_BINDING_KEY) == null) {
            BindingGroup bg = new BindingGroup();
            
            //use the ternary operator to ensure that we always check valid properties
            //we get the same effect using ${labelFor.enabled} but that results in log
            //warnings when the labelFor property is null/empty.
            bg.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, label,
                    ELProperty.create("${empty labelFor ? enabled : labelFor.enabled}"), label,
                    BeanProperty.create("enabled")));
            bg.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, label,
                    ELProperty.create("${empty labelFor ? visible : labelFor.visible}"), label,
                    BeanProperty.create("visible")));
            bg.bind();
            
            label.putClientProperty(LABEL_FOR_BINDING_KEY, bg);
        }
        
        label.setLabelFor(c);
    }

    /**
     * <p>
     * Removes a binding for the specified {@code label} that tracks the its {@#link
     * JLabel#setLabelFor(Component) labelFor} property.
     * </p>
     * 
     * @param label
     *            the label to unbind
     * @throws NullPointerException
     *             if {@code label} is {@code null}
     */
    public static void unbindLabelFor(JLabel label) {
        if (label.getClientProperty(LABEL_FOR_BINDING_KEY) != null) {
            BindingGroup bg = (BindingGroup) label.getClientProperty(LABEL_FOR_BINDING_KEY);
            bg.unbind();
            
            label.putClientProperty(LABEL_FOR_BINDING_KEY, null);
        }
    }
}
