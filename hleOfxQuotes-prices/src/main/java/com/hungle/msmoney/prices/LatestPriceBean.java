package com.hungle.msmoney.prices;

import java.util.Date;

import com.hungle.sunriise.mnyobject.EnumSecurityPriceSrc;

public class LatestPriceBean {
    private String symbol;
    private String name;
    private Double price;
    private Date date;
    private EnumSecurityPriceSrc source;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public EnumSecurityPriceSrc getSource() {
        return source;
    }

    public void setSource(EnumSecurityPriceSrc source) {
        this.source = source;
    }
}
