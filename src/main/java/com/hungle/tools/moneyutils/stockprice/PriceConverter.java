package com.hungle.tools.moneyutils.stockprice;

import org.apache.commons.beanutils.converters.AbstractConverter;
import org.apache.log4j.Logger;

import com.hungle.tools.moneyutils.ofx.quotes.OfxUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class PriceConverter.
 */
public class PriceConverter extends AbstractConverter {
    
    /** The Constant log. */
    private static final Logger log = Logger.getLogger(PriceConverter.class);

    /**
     * Instantiates a new price converter.
     */
    public PriceConverter() {
        super(new Price(0.0));
    }

    /* (non-Javadoc)
     * @see org.apache.commons.beanutils.converters.AbstractConverter#convertToType(java.lang.Class, java.lang.Object)
     */
    @Override
    protected Object convertToType(@SuppressWarnings("rawtypes") Class targetType, Object value) throws Throwable {
        if (log.isDebugEnabled()) {
            log.debug("> convertToType");
        }

        Price price = new Price(0.0);

        if (value == null) {
            return price;
        }

        String str = value.toString();
        try {
            if (OfxUtils.isNA(str)) {
                price = new Price(0.0);
            } else {
                price = new Price(Double.valueOf(str));
            }
        } catch (NumberFormatException e) {
            if (log.isDebugEnabled()) {
                log.warn("Failed to convert price=" + str);
            }
            price = new Price(0.0);
        }
        return price;

    }

    /* (non-Javadoc)
     * @see org.apache.commons.beanutils.converters.AbstractConverter#getDefaultType()
     */
    @SuppressWarnings("rawtypes")
    @Override
    protected Class getDefaultType() {
        return Price.class;
    }
}
