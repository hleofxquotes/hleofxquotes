package com.le.tools.moneyutils.ofx.quotes;

import java.util.List;

import com.le.tools.moneyutils.stockprice.AbstractStockPrice;

public interface QuoteSource {

    List<AbstractStockPrice> getExchangeRates();

    boolean isHistoricalQuotes();

}
