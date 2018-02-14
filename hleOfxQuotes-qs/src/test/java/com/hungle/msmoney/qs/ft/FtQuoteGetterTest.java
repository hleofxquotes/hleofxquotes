package com.hungle.msmoney.qs.ft;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.hungle.msmoney.core.stockprice.AbstractStockPrice;
import com.hungle.msmoney.qs.net.AbstractHttpQuoteGetter;

public class FtQuoteGetterTest {
    private static final Logger LOGGER = Logger.getLogger(FtQuoteGetterTest.class);

    @Test
    public void testLiveEquities() throws IOException {
        String[] equities = { "IBM:NYQ", "AAPL:NSQ", "RDSA:LSE", "NESN:VTX", "FP:PAR", };
        List<String> list = Arrays.asList(equities);
        AbstractHttpQuoteGetter getter = null;
        try {
            getter = new FtEquitiesQuoteGetter();
            List<AbstractStockPrice> stockPrices = getter.getQuotes(list);
            Assert.assertNotNull(stockPrices);
            Assert.assertTrue(stockPrices.size() == equities.length);
        } finally {
            if (getter != null) {
                getter.close();
                getter = null;
            }
        }

    }

    @Test
    public void testLiveFunds() throws IOException {
        String[] funds = { "IE00BD2M9K78:GBP", "GB00B0CNH270:GBX", "GB00BYYV0405:GBX" };
        List<String> list = Arrays.asList(funds);
        FtFundsQuoteGetter getter = null;
        try {
            getter = new FtFundsQuoteGetter();
            List<AbstractStockPrice> stockPrices = getter.getQuotes(list);
            Assert.assertNotNull(stockPrices);
            Assert.assertTrue(stockPrices.size() == funds.length);
        } finally {
            if (getter != null) {
                getter.close();
                getter = null;
            }
        }
    }

    @Test
    public void testLiveETF() throws IOException {
        String[] etfs = { "IEUT:LSE:GBX", "FTAL:LSE:GBP" };
        List<String> list = Arrays.asList(etfs);
        FtEtfsQuoteGetter getter = null;
        try {
            getter = new FtEtfsQuoteGetter();
            List<AbstractStockPrice> stockPrices = getter.getQuotes(list);
            Assert.assertNotNull(stockPrices);
            Assert.assertTrue(stockPrices.size() == etfs.length);
        } finally {
            getter.close();
            getter = null;
        }
    }
}
