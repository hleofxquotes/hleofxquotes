package com.hungle.msmoney.qs.ft;

import java.io.IOException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;

public class FtJSoupTest {
    private static final Logger LOGGER = Logger.getLogger(FtJSoupTest.class);

    @Test
    public void testLiveEquities() throws IOException {
        String[] equities = { "IBM:NYQ", "AAPL:NSQ", "RDSA:LSE", "NESN:VTX", "FP:PAR", };
        for (String equity : equities) {
            testLiveEquity(equity);
        }
    }

    @Test
    public void testLiveFunds() throws IOException {
        String[] funds = { "IE00BD2M9K78:GBP", "GB00B0CNH270:GBX", "GB00BYYV0405:GBX" };
        for (String fund : funds) {
            testLiveFund(fund);
        }
    }

    @Test
    public void testLiveETF() throws IOException {
        String[] etfs = { "IEUT:LSE:GBX", "FTAL:LSE:GBP" };
        for (String etf : etfs) {
            testLiveEtf(etf);
        }
    }

    private void testLiveEquity(String equity) throws IOException {
        // https://markets.ft.com/data/equities/tearsheet/summary?s=IBM:NYQ
        URL url = FtPriceModel.getFtEquityURL(equity);
        int timeoutMillis = 30 * 1000;
        LOGGER.info("url=" + url);
        Document doc = Jsoup.parse(url, timeoutMillis);
        Assert.assertNotNull(doc);
        FtPriceModel model = FtPriceModel.parseFtDoc(doc, equity);
        Assert.assertNotNull(model);
        LOGGER.info(model);
    }

    private void testLiveFund(String fund) throws IOException {
        // https://markets.ft.com/data/funds/tearsheet/summary?s=IE00BD2M9K78:GBP
        URL url = FtPriceModel.getFtFundURL(fund);
        int timeoutMillis = 30 * 1000;
        LOGGER.info("url=" + url);
        Document doc = Jsoup.parse(url, timeoutMillis);
        Assert.assertNotNull(doc);
        FtPriceModel model = FtPriceModel.parseFtDoc(doc, fund);
        Assert.assertNotNull(model);
        LOGGER.info(model);
    }

    private void testLiveEtf(String etf) throws IOException {
        // https://markets.ft.com/data/etfs/tearsheet/summary?s=VUKE:LSE:GBP
        URL url = FtPriceModel.getFtEtfURL(etf);
        int timeoutMillis = 30 * 1000;
        LOGGER.info("url=" + url);
        Document doc = Jsoup.parse(url, timeoutMillis);
        Assert.assertNotNull(doc);
        FtPriceModel model = FtPriceModel.parseFtDoc(doc, etf);
        Assert.assertNotNull(model);
        LOGGER.info(model);
    }

}
