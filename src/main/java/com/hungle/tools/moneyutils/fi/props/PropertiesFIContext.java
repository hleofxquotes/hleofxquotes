package com.le.tools.moneyutils.fi.props;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.log4j.Logger;

import com.le.tools.moneyutils.fi.AbstractFiContext;

public class PropertiesFIContext extends AbstractFiContext {
    private static final Logger log = Logger.getLogger(PropertiesFIContext.class);
    private BeanUtilsBean beanUtilsBean;

    public PropertiesFIContext(Properties props) {
        super();
        ConvertUtilsBean convertUtilsBean = new ConvertUtilsBean();

        // For type Price, user converter=PriceConverter
        // convertUtilsBean.deregister(Price.class);
        // convertUtilsBean.register(new PriceConverter(), Price.class);

        beanUtilsBean = new BeanUtilsBean(convertUtilsBean, new PropertyUtilsBean());

        for (Object key : props.keySet()) {
            String name = (String) key;
            String value = props.getProperty(name);
            if (value == null) {
                continue;
            }
            if (value.length() <= 0) {
                continue;
            }
            try {
                beanUtilsBean.setProperty(this, name, value);
            } catch (IllegalAccessException e) {
                log.warn(e);
            } catch (InvocationTargetException e) {
                log.warn(e);
            }
        }
    }

}
