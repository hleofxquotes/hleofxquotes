package com.hungle.msmoney.qs.yahoo;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.hungle.msmoney.core.misc.ResourceUtils;
import com.hungle.msmoney.core.stockprice.AbstractStockPrice;
import com.hungle.msmoney.core.stockprice.Price;

public class YahooSS2QuoteGetterTest {
    private static final Logger LOGGER = Logger.getLogger(YahooSS2QuoteGetterTest.class);

    @Test
    public void testRegularMarketTime() {
        // "regularMarketTime": 1510124462
        // "quoteType": {
        // "exchange": "MUN",
        // "exchangeTimezoneName": "Europe/Berlin",
        // "exchangeTimezoneShortName": "CET",
        // "gmtOffSetMilliseconds": "3600000",

        long regularMarketTime = 1510124462;
        String exchangeTimezoneName = "Europe/Berlin";
        ZonedDateTime zoneDateTime = YahooSS2QuoteGetter.getMarketZonedDateTime(regularMarketTime,
                exchangeTimezoneName);

        Assert.assertEquals(2017, zoneDateTime.getYear());
        Assert.assertEquals(11, zoneDateTime.getMonthValue());
        Assert.assertEquals(8, zoneDateTime.getDayOfMonth());

        Assert.assertEquals(8, zoneDateTime.getHour());
        Assert.assertEquals(1, zoneDateTime.getMinute());
        Assert.assertEquals(2, zoneDateTime.getSecond());

        Assert.assertEquals(exchangeTimezoneName, zoneDateTime.getZone().getId());
    }

    @Test
    public void testYahooSS2QuoteGetterFromStream() throws IOException {
        InputStream stream = ResourceUtils.getResource("TSLA.html", this).openStream();
        YahooSS2QuoteGetter getter = new YahooSS2QuoteGetter();
        getter.setStockSymbol("TSLA");
        List<AbstractStockPrice> stockPrices = getter.parseInputStream(stream);

        Assert.assertNotNull(stockPrices);

        Assert.assertEquals(1, stockPrices.size());

        AbstractStockPrice stockPrice = stockPrices.get(0);
        Assert.assertNotNull(stockPrice);

        Price price = stockPrice.getLastPrice();
        Assert.assertNotNull(price);

        Assert.assertEquals(297.5151, price.doubleValue(), 0.1);
    }

    @Test
    public void testYahooSS2QuoteGetterLive() throws IOException {
        String[] symbols = { "IBM", "AAPL", };

        List<String> list = Arrays.asList(symbols);
        YahooSS2QuoteGetter getter = new YahooSS2QuoteGetter();
        List<AbstractStockPrice> stockPrices = getter.getQuotes(list);
        Assert.assertNotNull(stockPrices);
        Assert.assertTrue(stockPrices.size() == symbols.length);
    }
}
