package com.hungle.msmoney.core.fx;

// TODO: Auto-generated Javadoc
/**
 * The Class FxTableEntry.
 */
public class FxTableEntry {
    
    /** The from currency. */
    private String fromCurrency;
    
    /** The to currency. */
    private String toCurrency;
    
    /** The rate. */
    private String rate;

    public FxTableEntry(String fromCurrency, String toCurrency, String rate) {
        super();
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.rate = rate;
    }

    public FxTableEntry() {
        super();
    }

    @Override
    public String toString() {
        return "FxTableEntry [fromCurrency=" + fromCurrency + ", toCurrency=" + toCurrency + ", rate=" + rate + "]";
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
    public String getRate() {
        return rate;
    }

    /**
     * Sets the rate.
     *
     * @param rate the new rate
     */
    public void setRate(String rate) {
        this.rate = rate;
    }
}
