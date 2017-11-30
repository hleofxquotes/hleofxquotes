package com.hungle.msmoney.qs.yahoo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.hungle.msmoney.core.ofx.OfxUtils;
import com.hungle.msmoney.core.stockprice.AbstractStockPrice;
import com.hungle.msmoney.core.stockprice.Price;

public class YahooSS2HistQuoteGetterTest {
    private static final Logger LOGGER = Logger.getLogger(YahooSS2HistQuoteGetterTest.class);

    @Test
    public void testParseHtmlContent() throws IOException {
        String symbol = "GB00B2PLJJ36.L";
        InputStream stream = OfxUtils.getResource(symbol + ".html", this).openStream();
        YahooSS2HistQuoteGetter getter = new YahooSS2HistQuoteGetter();
        getter.setStockSymbol("GB00B2PLJJ36.L");
        List<AbstractStockPrice> stockPrices = getter.parseInputStream(stream);

        Assert.assertNotNull(stockPrices);

        Assert.assertEquals(1, stockPrices.size());

        AbstractStockPrice stockPrice = stockPrices.get(0);
        Assert.assertNotNull(stockPrice);

        Price price = stockPrice.getLastPrice();
        Assert.assertNotNull(price);

        Assert.assertEquals(2.463399887084961, price.doubleValue(), 0.1);

    }

    @Test
    public void testLiveUrl() throws IOException {

        YahooSS2HistQuoteGetter quoteGetter = null;
        try {
            quoteGetter = new YahooSS2HistQuoteGetter();
            String[] stocks = {
                    "GB00B2PLJJ36.L",
            };
            for (String stock : stocks) {
                List<String> list = new ArrayList<String>();
                list.add(stock);
                List<AbstractStockPrice> stockPrices = quoteGetter.getQuotes(list);

                Assert.assertNotNull(stockPrices);
                Assert.assertTrue(stockPrices.size() == 1);
            }
        } finally {
            if (quoteGetter != null) {
                quoteGetter = null;
            }
        }
    }
}
