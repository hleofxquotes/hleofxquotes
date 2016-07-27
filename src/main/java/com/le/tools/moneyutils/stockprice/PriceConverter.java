package com.le.tools.moneyutils.stockprice;

import org.apache.commons.beanutils.converters.AbstractConverter;
import org.apache.log4j.Logger;

import com.le.tools.moneyutils.ofx.quotes.OfxUtils;

public class PriceConverter extends AbstractConverter {
    private static final Logger log = Logger.getLogger(PriceConverter.class);

    public PriceConverter() {
        super(new Price(0.0));
    }

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

    @SuppressWarnings("rawtypes")
    @Override
    protected Class getDefaultType() {
        return Price.class;
    }
}
