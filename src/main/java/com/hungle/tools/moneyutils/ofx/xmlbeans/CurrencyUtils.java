package com.le.tools.moneyutils.ofx.xmlbeans;

import net.ofx.types.x2003.x04.CurrencyEnum;

public class CurrencyUtils {
    public static String getDefaultCurrency() {
        return CurrencyEnum.USD.toString();
    }
}
