package com.hungle.msmoney.qs;

import java.util.ArrayList;
import java.util.List;

import com.hungle.msmoney.core.stockprice.AbstractStockPrice;

// TODO: Auto-generated Javadoc
/**
 * The Class DefaultQuoteSource.
 */
public class DefaultQuoteSource implements QuoteSource {
    
    /** The exchange rates. */
    private List<AbstractStockPrice> exchangeRates = new ArrayList<AbstractStockPrice>();

    /** The historical quotes. */
    private boolean historicalQuotes = false;
    
    private List<String> notFoundSymbols = new ArrayList<String>();
    
    /* (non-Javadoc)
     * @see com.hungle.tools.moneyutils.ofx.quotes.QuoteSource#getExchangeRates()
     */
    @Override
    public List<AbstractStockPrice> getExchangeRates() {
        return exchangeRates;
    }

    /* (non-Javadoc)
     * @see com.hungle.tools.moneyutils.ofx.quotes.QuoteSource#isHistoricalQuotes()
     */
    @Override
    public boolean isHistoricalQuotes() {
        return historicalQuotes;
    }

    /**
     * Sets the historical quotes.
     *
     * @param historicalQuotes the new historical quotes
     */
    public void setHistoricalQuotes(boolean historicalQuotes) {
        this.historicalQuotes = historicalQuotes;
    }

    public List<String> getNotFoundSymbols() {
        return notFoundSymbols;
    }

    public void setNotFoundSymbols(List<String> notFoundSymbols) {
        this.notFoundSymbols = notFoundSymbols;
    }

}
