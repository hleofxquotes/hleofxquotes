package com.hungle.msmoney.gui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.hungle.msmoney.core.stockprice.AbstractStockPrice;
import com.hungle.msmoney.core.stockprice.StockPrice;
import com.hungle.msmoney.qs.QuoteSource;

import ca.odell.glazedlists.EventList;

public class NotFoundPriceListUtils {

    static final void addAllNotFoundPrices(final QuoteSource quoteSource, EventList<AbstractStockPrice> priceList) {
        List<String> symbols = quoteSource.getNotFoundSymbols();
        if ((symbols != null) && (symbols.size() > 0)) {
            addAll(symbols, priceList);
        }
    }

    private static void addAll(List<String> symbols, EventList<AbstractStockPrice> priceList) {
        ArrayList<AbstractStockPrice> stockPrices = new ArrayList<AbstractStockPrice>();
        for (String symbol : symbols) {
            AbstractStockPrice stockPrice = new StockPrice(symbol, new Date(), 0.00);
            stockPrices.add(stockPrice);
        }

        priceList.getReadWriteLock().writeLock().lock();
        try {
            priceList.addAll(stockPrices);
        } finally {
            priceList.getReadWriteLock().writeLock().unlock();
        }
    }

}
