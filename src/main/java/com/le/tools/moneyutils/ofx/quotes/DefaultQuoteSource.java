package com.le.tools.moneyutils.ofx.quotes;

import java.util.ArrayList;
import java.util.List;

import com.le.tools.moneyutils.stockprice.AbstractStockPrice;

public class DefaultQuoteSource implements QuoteSource {
    private List<AbstractStockPrice> exchangeRates = new ArrayList<AbstractStockPrice>();

    private boolean historicalQuotes = false;
    
    @Override
    public List<AbstractStockPrice> getExchangeRates() {
        return exchangeRates;
    }

    @Override
    public boolean isHistoricalQuotes() {
        return historicalQuotes;
    }

    public void setHistoricalQuotes(boolean historicalQuotes) {
        this.historicalQuotes = historicalQuotes;
    }

}
