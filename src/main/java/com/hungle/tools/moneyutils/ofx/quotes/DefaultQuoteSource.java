package com.hungle.tools.moneyutils.ofx.quotes;

import java.util.ArrayList;
import java.util.List;

import com.hungle.tools.moneyutils.stockprice.AbstractStockPrice;

// TODO: Auto-generated Javadoc
/**
 * The Class DefaultQuoteSource.
 */
public class DefaultQuoteSource implements QuoteSource {
    
    /** The exchange rates. */
    private List<AbstractStockPrice> exchangeRates = new ArrayList<AbstractStockPrice>();

    /** The historical quotes. */
    private boolean historicalQuotes = false;
    
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

}
