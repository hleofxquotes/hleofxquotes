package com.hungle.msmoney.core.ofx;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.hungle.msmoney.core.ofx.OfxUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class OfxUtilsTest.
 */
public class OfxUtilsTest {
    
    /** The Constant LOGGER. */
    private static final Logger LOGGER = Logger.getLogger(OfxUtilsTest.class);

    /**
     * Test nyse list conversion.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testNyseListConversion() throws IOException {
        List<String> stocks = OfxUtils.getNYSEList();
        int expected = 3240;
        Assert.assertEquals(expected, stocks.size());
    }

    /**
     * Test nasdaq list conversion.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testNasdaqListConversion() throws IOException {
        List<String> stocks = OfxUtils.getNASDAQList();
        int expected = 2841;
        Assert.assertEquals(expected, stocks.size());
    }

    /**
     * Test store stock symbols.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
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
