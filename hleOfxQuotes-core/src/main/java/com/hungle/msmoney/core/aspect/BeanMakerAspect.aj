package com.hungle.msmoney.core.aspect;

import java.beans.Introspector;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.apache.commons.beanutils.BeanUtils;

public aspect BeanMakerAspect {
    // package com.hungle.tools.moneyutils.fi.model.bean;
//    declare parents: com.hungle.msmoney.statements.fi.model.bean.* implements BeanSupport;
    declare parents: com.hungle.msmoney.stmt.fi.model.bean.* implements BeanSupport;

    // introduces a member propertyChangeSupport
    private PropertyChangeSupport BeanSupport.propertyChangeSupport;

    // you introduce two new methods addPropertyChangeListener() and
    // removePropertyChangeListener()
    public void BeanSupport.addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void BeanSupport.removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    pointcut beanCreation(BeanSupport bean)
    : initialization(BeanSupport+.new(..)) && this(bean);

    pointcut beanPropertyChange(BeanSupport bean, Object newValue)
    : execution(void BeanSupport+.set*(*))
    && args(newValue) && this(bean);

    after(BeanSupport bean) returning : beanCreation(bean) {
        bean.propertyChangeSupport = new PropertyChangeSupport(bean);
    }

    void around(BeanSupport bean, Object newValue)
    : beanPropertyChange(bean, newValue) {
        String methodName = thisJoinPointStaticPart.getSignature().getName();
        String propertyName = Introspector.decapitalize(methodName.substring(3));
        Object oldValue = getPropertyValue(bean, propertyName);
        proceed(bean, newValue);
        bean.propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    private static Object getPropertyValue(Object bean, String propertyName) {
        try {
            return BeanUtils.getProperty(bean, propertyName);
        } catch (Exception ex) {
            return null;
        }
    }
}
