package com.hungle.msmoney.stmt.fi.props;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.log4j.Logger;

import com.hungle.msmoney.stmt.fi.AbstractFiContext;

// TODO: Auto-generated Javadoc
/**
 * The Class PropertiesFIContext.
 */
public class PropertiesFIContext extends AbstractFiContext {
    
    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(PropertiesFIContext.class);
    
    /** The bean utils bean. */
    private BeanUtilsBean beanUtilsBean;

    /**
     * Instantiates a new properties FI context.
     *
     * @param props the props
     */
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
                LOGGER.warn(e);
            } catch (InvocationTargetException e) {
                LOGGER.warn(e);
            }
        }
    }

}
