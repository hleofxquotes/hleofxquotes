package com.hungle.msmoney.core.ofx.xmlbeans;

import java.io.IOException;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class OptionsType.
 */
public class OptionsType {
    
    /** The Constant log. */
    private static final Logger log = Logger.getLogger(OptionsType.class);

    /** The root symbol. */
    private String rootSymbol;
    
    /** The date. */
    private String date;
    
    /** The indicator. */
    private String indicator;
    
    /** The strike price. */
    private double strikePrice;

    // http://biz.yahoo.com/opt/symbol.html
    // The basic parts of new option symbol are:
    // Root symbol + Expiration Year(yy)+ Expiration Month(mm)+ Expiration
    /**
     * Instantiates a new options type.
     *
     * @param string the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    // Day(dd) + Call/Put Indicator (C or P) + Strike price (5 + 3)
    public OptionsType(String string) throws IOException {
        parse(string);
    }

    /**
     * Parses the.
     *
     * @param symbol the symbol
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void parse(String symbol) throws IOException {
        // YHOO100416C00020000
        // yy=20, mm=04, dd=16, indicator=C, strikePrice=20.000
        // 2 + 2 + 2 + 1 + 5 + 3 = 15
        int len = symbol.length();
        if (len <= 15) {
            throw new IOException("Invalid options symbol. Too short. Symbol=" + symbol);
        }
        int breakPoint = (len - 15);
        String left = symbol.substring(0, breakPoint);
        String right = symbol.substring(breakPoint);
        this.rootSymbol = left;

        this.date = right.substring(0, 6);
        this.indicator = right.substring(6, 7);
        if (this.indicator.compareToIgnoreCase("C") == 0) {
            this.indicator = "CALL";
        } else if (this.indicator.compareToIgnoreCase("P") == 0) {
            this.indicator = "PUT";
        } else {
            log.warn("Invalid options indicator=" + indicator);
            this.indicator = "CALL";
        }
        String dollar = right.substring(7, 12);
        String cents = right.substring(12);
        this.strikePrice = Double.valueOf(dollar + "." + cents);
    }

    /**
     * Gets the root symbol.
     *
     * @return the root symbol
     */
    public String getRootSymbol() {
        return rootSymbol;
    }

    /**
     * Sets the root symbol.
     *
     * @param rootSymbol the new root symbol
     */
    public void setRootSymbol(String rootSymbol) {
        this.rootSymbol = rootSymbol;
    }

    /**
     * Gets the date.
     *
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the date.
     *
     * @param date the new date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Gets the indicator.
     *
     * @return the indicator
     */
    public String getIndicator() {
        return indicator;
    }

    /**
     * Sets the indicator.
     *
     * @param indicator the new indicator
     */
    public void setIndicator(String indicator) {
        this.indicator = indicator;
    }

    /**
     * Gets the strike price.
     *
     * @return the strike price
     */
    public double getStrikePrice() {
        return strikePrice;
    }

    /**
     * Sets the strike price.
     *
     * @param strikePrice the new strike price
     */
    public void setStrikePrice(double strikePrice) {
        this.strikePrice = strikePrice;
    }

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        OptionsType optionsType = null;
        String symbol = null;
        try {
            symbol = "YHOO100416C00020000";
            log.info("symbol=" + symbol);
            optionsType = new OptionsType(symbol);
            log.info("indicator=" + optionsType.getIndicator());
            log.info("strikePrice=" + optionsType.getStrikePrice());
        } catch (IOException e) {
            log.error(e, e);
        } finally {
            log.info("> DONE");
        }
    }
}
