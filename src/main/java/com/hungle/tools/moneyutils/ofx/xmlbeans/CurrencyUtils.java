package com.hungle.tools.moneyutils.ofx.xmlbeans;

import net.ofx.types.x2003.x04.CurrencyEnum;

// TODO: Auto-generated Javadoc
/**
 * The Class CurrencyUtils.
 */
public class CurrencyUtils {
    
    /**
     * Gets the default currency.
     *
     * @return the default currency
     */
    public static String getDefaultCurrency() {
        return CurrencyEnum.USD.toString();
    }
}
