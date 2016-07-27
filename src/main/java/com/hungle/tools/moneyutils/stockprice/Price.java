package com.hungle.tools.moneyutils.stockprice;

import java.text.NumberFormat;

// TODO: Auto-generated Javadoc
/**
 * The Class Price.
 */
public class Price extends Number implements Comparable<Price> {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The price. */
    private Double price;
    
    /** The currency. */
    private String currency;
    
    /** The price formatter. */
    private final NumberFormat priceFormatter;

    /**
     * Instantiates a new price.
     *
     * @param price the price
     */
    public Price(double price) {
        this.price = price;
        this.priceFormatter = NumberFormat.getNumberInstance();
        this.priceFormatter.setGroupingUsed(false);
        this.priceFormatter.setMinimumFractionDigits(4);
        this.priceFormatter.setMaximumFractionDigits(4);
    }

    /**
     * Gets the price.
     *
     * @return the price
     */
    public Double getPrice() {
        return price;
    }

    /**
     * Sets the price.
     *
     * @param price the new price
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (currency == null) {
            return this.priceFormatter.format(this.price);
        } else {
            return this.priceFormatter.format(this.price) + " " + currency;
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Number#doubleValue()
     */
    @Override
    public double doubleValue() {
        return price.doubleValue();
    }

    /* (non-Javadoc)
     * @see java.lang.Number#floatValue()
     */
    @Override
    public float floatValue() {
        return price.floatValue();
    }

    /* (non-Javadoc)
     * @see java.lang.Number#intValue()
     */
    @Override
    public int intValue() {
        return price.intValue();
    }

    /* (non-Javadoc)
     * @see java.lang.Number#longValue()
     */
    @Override
    public long longValue() {
        return price.longValue();
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Price o) {
        return price.compareTo(o.getPrice());
    }

    /**
     * Gets the currency.
     *
     * @return the currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Sets the currency.
     *
     * @param currency the new currency
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * Gets the price formatter.
     *
     * @return the price formatter
     */
    public NumberFormat getPriceFormatter() {
        return priceFormatter;
    }
}
