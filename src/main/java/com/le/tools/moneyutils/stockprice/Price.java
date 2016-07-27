package com.le.tools.moneyutils.stockprice;

import java.text.NumberFormat;

public class Price extends Number implements Comparable<Price> {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Double price;
    private String currency;
    private final NumberFormat priceFormatter;

    public Price(double price) {
        this.price = price;
        this.priceFormatter = NumberFormat.getNumberInstance();
        this.priceFormatter.setGroupingUsed(false);
        this.priceFormatter.setMinimumFractionDigits(4);
        this.priceFormatter.setMaximumFractionDigits(4);
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        if (currency == null) {
            return this.priceFormatter.format(this.price);
        } else {
            return this.priceFormatter.format(this.price) + " " + currency;
        }
    }

    @Override
    public double doubleValue() {
        return price.doubleValue();
    }

    @Override
    public float floatValue() {
        return price.floatValue();
    }

    @Override
    public int intValue() {
        return price.intValue();
    }

    @Override
    public long longValue() {
        return price.longValue();
    }

    @Override
    public int compareTo(Price o) {
        return price.compareTo(o.getPrice());
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public NumberFormat getPriceFormatter() {
        return priceFormatter;
    }
}
