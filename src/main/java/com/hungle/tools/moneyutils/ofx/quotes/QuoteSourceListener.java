package com.hungle.tools.moneyutils.ofx.quotes;

import java.util.List;

import com.hungle.tools.moneyutils.stockprice.AbstractStockPrice;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving quoteSource events.
 * The class that is interested in processing a quoteSource
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addQuoteSourceListener<code> method. When
 * the quoteSource event occurs, that object's appropriate
 * method is invoked.
 *
 * @see QuoteSourceEvent
 */
public interface QuoteSourceListener {
    
    /**
     * Stock prices lookup started.
     *
     * @param quoteSource the quote source
     */
    void stockPricesLookupStarted(QuoteSource quoteSource);

    /**
     * Stock symbols string received.
     *
     * @param quoteSource the quote source
     * @param lines the lines
     */
    void stockSymbolsStringReceived(QuoteSource quoteSource, String lines);

    /**
     * Stock prices received.
     *
     * @param quoteSource the quote source
     * @param stockPrices the stock prices
     */
    void stockPricesReceived(QuoteSource quoteSource, List<AbstractStockPrice> stockPrices);

}
