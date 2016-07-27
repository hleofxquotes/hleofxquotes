package com.le.tools.moneyutils.ofx.quotes;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class OfxUtilsTest {
    private static final Logger log = Logger.getLogger(OfxUtilsTest.class);

    @Test
    public void testNyseListConversion() throws IOException {
        List<String> stocks = OfxUtils.getNYSEList();
        int expected = 3240;
        Assert.assertEquals(expected, stocks.size());
    }

    @Test
    public void testNasdaqListConversion() throws IOException {
        List<String> stocks = OfxUtils.getNASDAQList();
        int expected = 2841;
        Assert.assertEquals(expected, stocks.size());
    }

    @Test
    @Ignore
    public void testStoreStockSymbols() throws IOException {
        List<String> stocks = OfxUtils.getNASDAQList();
        String stocksString = OfxUtils.toSeparatedString(stocks);
        stocksString = OfxUtils.breakLines(stocksString);
        Assert.assertNotNull(stocksString);
        
        int count = OfxUtils.storeStockSymbols(stocksString);
        Assert.assertTrue(count > 0);

        Assert.assertTrue(count < stocksString.length());

        String expected = stocksString;
        String actual = OfxUtils.retrieveStockSymbols();
        Assert.assertNotNull(actual);

        Assert.assertEquals(expected, actual);
    }

}
