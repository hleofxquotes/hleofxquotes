package com.hungle.msmoney.core.qif;

import java.util.Date;

import com.hungle.msmoney.core.stockprice.Price;

public class QifBean {
    private String symbol;

    private Price price;
    
    private Date date;
    
    private Price dayHigh;
    
    private Price dayLow;
    
    private Long volume;
    
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Price getDayHigh() {
        return dayHigh;
    }

    public void setDayHigh(Price dayHigh) {
        this.dayHigh = dayHigh;
    }

    public Price getDayLow() {
        return dayLow;
    }

    public void setDayLow(Price dayLow) {
        this.dayLow = dayLow;
    }

    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }
}
