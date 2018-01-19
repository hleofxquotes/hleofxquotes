package com.hungle.msmoney.gui.md;

import com.hungle.msmoney.core.stockprice.Price;

public class MdCsvBean {
    private Price price;
    
    private String symbol;

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
