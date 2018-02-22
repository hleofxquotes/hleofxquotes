package com.hungle.msmoney.qs;

import java.util.List;

import com.hungle.msmoney.core.stockprice.AbstractStockPrice;

// TODO: Auto-generated Javadoc
/**
 * The Interface QuoteSource.
 */
public interface QuoteSource {

    /**
     * Gets the exchange rates.
     *
     * @return the exchange rates
     */
    List<AbstractStockPrice> getExchangeRates();

    void setHistoricalQuotes(boolean b);

    /**
     * Checks if is historical quotes.
     *
     * @return true, if is historical quotes
     */
    boolean isHistoricalQuotes();
    
    List<String> getNotFoundSymbols();

    void setQuotesResult(QuotesResult quotesResult);
    
    QuotesResult getQuotesResult();
}
