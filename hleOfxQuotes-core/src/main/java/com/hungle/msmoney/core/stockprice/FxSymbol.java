package com.hungle.msmoney.core.stockprice;

import java.util.Currency;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.hungle.msmoney.core.misc.CheckNullUtils;

/**
 * The Class FxSymbol.
 */
public class FxSymbol {
    private static final Logger LOGGER = Logger.getLogger(FxSymbol.class);

    private static final String PENNY_STERLING = "GBX";

    /** The from currency. */
    private String fromCurrency;

    /** The to currency. */
    private String toCurrency;

    /** The rate. */
    private double rate;

    private static final Set<Currency> availableCurrencies = Currency.getAvailableCurrencies();

    @Override
    public String toString() {
        return "FxSymbol [fromCurrency=" + fromCurrency + ", toCurrency=" + toCurrency + ", rate=" + rate + "]";
    }

    /**
     * Gets the from currency.
     *
     * @return the from currency
     */
    public String getFromCurrency() {
        return fromCurrency;
    }

    /**
     * Sets the from currency.
     *
     * @param fromCurrency
     *            the new from currency
     */
    public void setFromCurrency(String fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    /**
     * Gets the to currency.
     *
     * @return the to currency
     */
    public String getToCurrency() {
        return toCurrency;
    }

    /**
     * Sets the to currency.
     *
     * @param toCurrency
     *            the new to currency
     */
    public void setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency;
    }

    /**
     * Gets the rate.
     *
     * @return the rate
     */
    public double getRate() {
        return rate;
    }

    /**
     * Sets the rate.
     *
     * @param rate
     *            the new rate
     */
    public void setRate(double rate) {
        this.rate = rate;
    }

    /**
     * Parses the.
     *
     * @param symbol
     *            the symbol
     * @return the fx symbol
     */
    public static FxSymbol parse(String symbol) {
        // ALLUSD=X
        // EURUSD=X
        if (symbol == null) {
            return null;
        }

        if (CheckNullUtils.isEmpty(symbol)) {
            return null;
        }

        if (symbol.length() == 8) {
            if (!symbol.endsWith("=X")) {
                return null;
            }
        } else if (symbol.length() == 6) {
            // OK
        } else {
            return null;
        }

        String from = symbol.substring(0, 3);
        if (!isValidCurrencyCode(from)) {
            return null;
        }
        String to = symbol.substring(3, 6);
        if (!isValidCurrencyCode(to)) {
            return null;
        }

        FxSymbol fxSymbol = new FxSymbol();
        fxSymbol.setFromCurrency(from);
        fxSymbol.setToCurrency(to);

        return fxSymbol;
    }

    private static boolean isValidCurrencyCode(String currencyCode) {
        if (StringUtils.isEmpty(currencyCode)) {
            return false;
        }
        
        // handle special cases
        if (currencyCode.compareToIgnoreCase(PENNY_STERLING) == 0) {
            return true;
        }

        Currency currency = null;
        try {
            currency = Currency.getInstance(currencyCode);
        } catch (Exception e) {
            LOGGER.warn("currencyCode=" + currencyCode + ", e=" + e.getClass().getName() + " " + e.getMessage());
        }

        if (currency == null) {
            return false;
        }
        return availableCurrencies.contains(currency);
    }
}
