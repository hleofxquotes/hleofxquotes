package com.le.tools.moneyutils.fi.model.aspect;

import java.beans.Introspector;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public aspect BeanMakerAspect {
    declare parents: le.com.tools.moneyutils.fi.model.bean.* implements BeanSupport;
    private transient PropertyChangeSupport BeanSupport.propertyChangeSupport;

    public void BeanSupport.addPropertyChangeListener(PropertyChangeListener listener) {
        if (propertyChangeSupport == null)
            propertyChangeSupport = new PropertyChangeSupport(this);

        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void BeanSupport.removePropertyChangeListener(PropertyChangeListener listener) {
        if (propertyChangeSupport != null)
            propertyChangeSupport.removePropertyChangeListener(listener);
    }

    pointcut beanPropertyChange(BeanSupport bean, Object newValue)
    : execution(void BeanSupport+.set*(*)) && args(newValue) && this(bean);

    void around(BeanSupport bean, Object newValue) : beanPropertyChange(bean, newValue) {
        if (bean.propertyChangeSupport == null) {
            proceed(bean, newValue);
        } else {
            String methodName = thisJoinPointStaticPart.getSignature().getName();
            String propertyName = Introspector.decapitalize(methodName.substring(3));
            proceed(bean, newValue);
            bean.propertyChangeSupport.firePropertyChange(propertyName, null, newValue);
        }
    }

}
