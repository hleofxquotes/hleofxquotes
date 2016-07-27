package com.le.tools.moneyutils.scholarshare;

import java.util.Date;

class TIAACREFPriceInfo {
    private Date date;
    private String portfolioName;
    private String fundName;
    private String symbol;
    private double price;

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(symbol);

        sb.append(", ");
        sb.append(portfolioName);

        sb.append(", ");
        sb.append(fundName);

        sb.append(", ");
        sb.append(price);

        return sb.toString();
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}