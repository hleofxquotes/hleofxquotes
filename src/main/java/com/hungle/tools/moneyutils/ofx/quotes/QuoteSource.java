package com.hungle.tools.moneyutils.ofx.quotes;

import java.util.List;

import com.hungle.tools.moneyutils.stockprice.AbstractStockPrice;

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

    /**
     * Checks if is historical quotes.
     *
     * @return true, if is historical quotes
     */
    boolean isHistoricalQuotes();

}
