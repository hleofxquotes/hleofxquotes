package com.hungle.tools.moneyutils.fi.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractModelObject.
 *
 * @author lobas_av
 */
public abstract class AbstractModelObject {
    
    /** The property change support. */
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * Adds the property change listener.
     *
     * @param listener the listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Adds the property change listener.
     *
     * @param propertyName the property name
     * @param listener the listener
     */
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    /**
     * Removes the property change listener.
     *
     * @param listener the listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    /**
     * Removes the property change listener.
     *
     * @param propertyName the property name
     * @param listener the listener
     */
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
    }

    /**
     * Fire property change.
     *
     * @param propertyName the property name
     * @param oldValue the old value
     * @param newValue the new value
     */
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
}