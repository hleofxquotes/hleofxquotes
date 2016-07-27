package com.le.tools.moneyutils.ofx.xmlbeans;

import java.io.IOException;

import org.apache.log4j.Logger;

public class OptionsType {
    private static final Logger log = Logger.getLogger(OptionsType.class);

    private String rootSymbol;
    private String date;
    private String indicator;
    private double strikePrice;

    // http://biz.yahoo.com/opt/symbol.html
    // The basic parts of new option symbol are:
    // Root symbol + Expiration Year(yy)+ Expiration Month(mm)+ Expiration
    // Day(dd) + Call/Put Indicator (C or P) + Strike price (5 + 3)
    public OptionsType(String string) throws IOException {
        parse(string);
    }

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

    public String getRootSymbol() {
        return rootSymbol;
    }

    public void setRootSymbol(String rootSymbol) {
        this.rootSymbol = rootSymbol;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIndicator() {
        return indicator;
    }

    public void setIndicator(String indicator) {
        this.indicator = indicator;
    }

    public double getStrikePrice() {
        return strikePrice;
    }

    public void setStrikePrice(double strikePrice) {
        this.strikePrice = strikePrice;
    }

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
