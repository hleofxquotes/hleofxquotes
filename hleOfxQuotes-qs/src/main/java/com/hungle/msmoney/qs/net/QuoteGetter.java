package com.hungle.msmoney.qs.net;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

import com.hungle.msmoney.core.stockprice.AbstractStockPrice;

public interface QuoteGetter extends Closeable {
    List<AbstractStockPrice> getQuotes(List<String> stocks) throws IOException;
}
