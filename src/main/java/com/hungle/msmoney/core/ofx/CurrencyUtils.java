package com.hungle.msmoney.core.ofx;

import java.util.Map;
import java.util.TreeMap;

// TODO: Auto-generated Javadoc
/**
 * The Class CurrencyUtils.
 */
public class CurrencyUtils {
    
    /** The Constant CURRENCIES. */
    public static final Map<String, String> CURRENCIES = new TreeMap<String, String>();

    static {
        CurrencyUtils.CURRENCIES.put("United States of America, Dollars", "USD");
        // ARS Argentina, Pesos
        CurrencyUtils.CURRENCIES.put("Argentina, Pesos", "ARS");
        // AUD Australia, Dollars
        CurrencyUtils.CURRENCIES.put("Australia, Dollars", "AUD");
        // CAD Canada, Dollars
        CurrencyUtils.CURRENCIES.put("Canada, Dollars", "CAD");
        // CNY China, Yuan Renminbi
        CurrencyUtils.CURRENCIES.put("China, Yuan Renminbi", "CNY");
        // EUR Euro Member Countries, Euro
        CurrencyUtils.CURRENCIES.put("Euro Member Countries, Euro", "EUR");
        // GBP United Kingdom, Pounds
        CurrencyUtils.CURRENCIES.put("United Kingdom, Pounds", "GBP");
        // HKD Hong Kong, Dollars
        CurrencyUtils.CURRENCIES.put("Hong Kong, Dollars", "HKD");
        // INR India, Rupees
        CurrencyUtils.CURRENCIES.put("India, Rupees", "INR");
        // JPY Japan, Yen
        CurrencyUtils.CURRENCIES.put("Japan, Yen", "JPY");
    }
}
