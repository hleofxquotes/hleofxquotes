package com.hungle.msmoney.qs.multi;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.hungle.msmoney.core.stockprice.AbstractStockPrice;
import com.hungle.msmoney.qs.ft.FtQuoteGetterTest;
import com.hungle.msmoney.qs.net.GetQuotesListener;
import com.hungle.msmoney.qs.yahoo.YahooSS2QuoteGetterTest;

public class MultiSourcesQuoteGetterTest {
    private static final Logger LOGGER = Logger.getLogger(MultiSourcesQuoteGetterTest.class);

    @Test
    public void testMultiSources() throws IOException {
        MultiSourcesQuoteGetter quoteGetter = null;

        try {
            quoteGetter = new MultiSourcesQuoteGetter();
            String[] qsNames = { "yahoo", "ft.com" };

            int expectedCount = 0;
            List<String> list = null;

            list = Arrays.asList(getYahoo2SampleSymbols());
            expectedCount += list.size();
            quoteGetter.setSymbols("yahoo", list);

            list = Arrays.asList(getFtSampleSymbols());
            expectedCount += list.size();
            quoteGetter.setSymbols("ft.com", list);

            GetQuotesListener listener = null;
            List<AbstractStockPrice> stockPrices = quoteGetter.getQuotes(Arrays.asList(qsNames), listener);
            for (AbstractStockPrice stockPrice : stockPrices) {
                LOGGER.info(stockPrice);
            }

            Assert.assertEquals(expectedCount, stockPrices.size());
        } finally {
            if (quoteGetter != null) {
                try {
                    quoteGetter.close();
                } catch (IOException e) {
                    LOGGER.warn(e);
                } finally {
                    quoteGetter = null;
                }
            }
        }
    }

    private String[] getYahoo2SampleSymbols() {
        return YahooSS2QuoteGetterTest.SAMPLE_STOCK_SYMBOLS;
    }

    private String[] getFtSampleSymbols() {
        String[] result = Stream.of(FtQuoteGetterTest.SAMPLE_EQUITY_SYMBOLS, FtQuoteGetterTest.SAMPLE_FUND_SYMBOLS,
                FtQuoteGetterTest.SAMPLE_ETF_SYMBOLS).flatMap(Stream::of).toArray(String[]::new);
        return result;
    }
}
