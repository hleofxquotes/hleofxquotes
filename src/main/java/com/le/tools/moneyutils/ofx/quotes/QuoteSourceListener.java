package com.le.tools.moneyutils.ofx.quotes;

import java.util.List;

import com.le.tools.moneyutils.stockprice.AbstractStockPrice;

public interface QuoteSourceListener {
    void stockPricesLookupStarted(QuoteSource quoteSource);

    void stockSymbolsStringReceived(QuoteSource quoteSource, String lines);

    void stockPricesReceived(QuoteSource quoteSource, List<AbstractStockPrice> stockPrices);

}
