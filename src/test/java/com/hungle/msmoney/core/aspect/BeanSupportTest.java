package com.hungle.msmoney.core.aspect;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

import com.hungle.msmoney.stmt.fi.model.bean.FiInfoBean;

// TODO: Auto-generated Javadoc
/**
 * The Class BeanSupportTest.
 */
public class BeanSupportTest {
    
    /**
     * Value change notifications.
     */
    @Test
    public void valueChangeNotifications() {
        FiInfoBean testCustomer = new FiInfoBean();
        testCustomer.setName("oldName");

        final AtomicInteger counter = new AtomicInteger();

        testCustomer.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                Assert.assertEquals("name", evt.getPropertyName());
                Assert.assertEquals("oldName", evt.getOldValue());
                Assert.assertEquals("newName", evt.getNewValue());
                counter.incrementAndGet();
            }
        });

        testCustomer.setName("newName");
        Assert.assertEquals(1, counter.get());
    }

}
