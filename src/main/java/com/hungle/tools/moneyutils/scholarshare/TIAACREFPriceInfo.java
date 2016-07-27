package com.hungle.tools.moneyutils.scholarshare;

import java.util.Date;

// TODO: Auto-generated Javadoc
/**
 * The Class TIAACREFPriceInfo.
 */
class TIAACREFPriceInfo {
    
    /** The date. */
    private Date date;
    
    /** The portfolio name. */
    private String portfolioName;
    
    /** The fund name. */
    private String fundName;
    
    /** The symbol. */
    private String symbol;
    
    /** The price. */
    private double price;

    /**
     * Gets the portfolio name.
     *
     * @return the portfolio name
     */
    public String getPortfolioName() {
        return portfolioName;
    }

    /**
     * Sets the portfolio name.
     *
     * @param portfolioName the new portfolio name
     */
    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    /**
     * Gets the fund name.
     *
     * @return the fund name
     */
    public String getFundName() {
        return fundName;
    }

    /**
     * Sets the fund name.
     *
     * @param fundName the new fund name
     */
    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    /**
     * Gets the price.
     *
     * @return the price
     */
    public double getPrice() {
        return price;
    }

    /**
     * Sets the price.
     *
     * @param price the new price
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
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

    /**
     * Gets the symbol.
     *
     * @return the symbol
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Sets the symbol.
     *
     * @param symbol the new symbol
     */
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Gets the date.
     *
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Sets the date.
     *
     * @param date the new date
     */
    public void setDate(Date date) {
        this.date = date;
    }
}