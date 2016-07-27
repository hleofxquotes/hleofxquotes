package com.hungle.tools.moneyutils.stockprice;

import com.hungle.tools.moneyutils.fi.props.PropertiesUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class FxSymbol.
 */
public class FxSymbol {
    
    /** The from currency. */
    private String fromCurrency;
    
    /** The to currency. */
    private String toCurrency;
    
    /** The rate. */
    private double rate;

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
     * @param fromCurrency the new from currency
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
     * @param toCurrency the new to currency
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
     * @param rate the new rate
     */
    public void setRate(double rate) {
        this.rate = rate;
    }

    /**
     * Parses the.
     *
     * @param symbol the symbol
     * @return the fx symbol
     */
    public static FxSymbol parse(String symbol) {
        FxSymbol fxSymbol = null;

        // ALLUSD=X
        if (symbol == null) {
            return null;
        }

        if (PropertiesUtils.isNull(symbol)) {
            return null;
        }

        if (symbol.length() != 8) {
            return null;
        }

        if (!symbol.endsWith("=X")) {
            return null;
        }

        fxSymbol = new FxSymbol();
        fxSymbol.setFromCurrency(symbol.substring(0, 3));
        fxSymbol.setToCurrency(symbol.substring(3, 6));

        return fxSymbol;
    }
}
