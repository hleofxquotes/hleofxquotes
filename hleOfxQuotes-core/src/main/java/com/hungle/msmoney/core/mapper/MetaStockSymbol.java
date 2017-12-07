package com.hungle.msmoney.core.mapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class MetaStockSymbol {
    private static final Logger LOGGER = Logger.getLogger(SymbolMapper.class);

    private static final String DEFAULT_FIELD_SEPARATOR = "/";

    private String symbol;
    private String qsSymbol;
    private String qsCurrency;
    private String currency;
    private boolean noop = false;

    private String originalSymbol;

    private String fieldSeparator = DEFAULT_FIELD_SEPARATOR;

    public MetaStockSymbol(String stockSymbol) throws IOException {
        parse(stockSymbol);
    }

    @Override
    public String toString() {
        return "MetaStockSymbol [symbol=" + symbol + ", qsSymbol=" + qsSymbol + ", qsCurrency=" + qsCurrency
                + ", currency=" + currency + "]";
    }

    private void parse(String stockSymbol) throws IOException {
        // GB0033772517____________ no manipulation needed
        // ALO:PAR_________________ no manipulation needed
        // GB00BTLX1Q39,USD,GBP____ converts the returns currency from USD to
        // GBP
        // GB00BTLX1Q39,USD,GBX____ converts the returns currency from USD to
        // Pence
        // AAPL____________________ no manipulation needed
        // AAPL,USD,GBP____________ converts the returns currency from USD to
        // GBP
        // GB00B2PLJJ36,GBX,GBP__ converts pence into pounds
        // XMRC:LSE:GBX____________ no manipulation needed
        // XMRC:LSE:GBX,GBX,GBP____ converts pence into pounds
        if (stockSymbol == null) {
            return;
        }

        stockSymbol = stockSymbol.trim();
        String[] tokens = stockSymbol.split(fieldSeparator);
        if (tokens == null) {
            return;
        }
        
        this.originalSymbol = stockSymbol;

        if (tokens.length == 1) {
            this.symbol = tokens[0].trim();
            this.qsSymbol = symbol;
            this.qsCurrency = null;
            this.currency = qsCurrency;
            this.noop = true;
        } else if (tokens.length == 2) {
            this.symbol = tokens[0].trim();
            this.qsSymbol = tokens[1].trim();
            this.qsCurrency = null;
            this.currency = qsCurrency;
        } else if (tokens.length == 3) {
            this.symbol = tokens[0].trim();
            this.qsSymbol = symbol;
            this.qsCurrency = tokens[1].trim();
            this.currency = tokens[2].trim();
        } else if (tokens.length == 4) {
            this.symbol = tokens[0].trim();
            this.qsSymbol = tokens[1].trim();
            this.qsCurrency = tokens[2].trim();
            this.currency = tokens[3].trim();
        } else {
            throw new IOException("Invalid symbol=" + symbol);
        }
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getQsSymbol() {
        return qsSymbol;
    }

    public void setQsSymbol(String qsSymbol) {
        this.qsSymbol = qsSymbol;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getQsCurrency() {
        return qsCurrency;
    }

    public void setQsCurrency(String qsCurrency) {
        this.qsCurrency = qsCurrency;
    }

    public boolean isNoop() {
        return noop;
    }

    public void setNoop(boolean noop) {
        this.noop = noop;
    }

    static final List<MetaStockSymbol> parse(List<String> stockSymbols) {
        List<MetaStockSymbol> metaStockSymbols = new ArrayList<MetaStockSymbol>();
        for (String stockSymbol : stockSymbols) {
            try {
                MetaStockSymbol metaStockSymbol = new MetaStockSymbol(stockSymbol);
                if (!metaStockSymbol.isNoop()) {
                    metaStockSymbols.add(metaStockSymbol);
                }
            } catch (IOException e) {
                LOGGER.warn(e);
            }
        }
        return metaStockSymbols;
    }

    public String getOriginalSymbol() {
        return originalSymbol;
    }

    public void setOriginalSymbol(String originalSymbol) {
        this.originalSymbol = originalSymbol;
    }

}
