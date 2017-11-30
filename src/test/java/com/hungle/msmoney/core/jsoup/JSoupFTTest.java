package com.hungle.msmoney.core.jsoup;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.hungle.msmoney.core.ofx.OfxUtils;
import com.hungle.msmoney.qs.ft.FtPriceModel;

public class JSoupFTTest {
    private static final Logger LOGGER = Logger.getLogger(JSoupFTTest.class);

    @Test
    public void testFundIE00BYY18M47() throws IOException {
        // IE00BYY18M47:GBP.html
        String symbol = "IE00BYY18M47:GBP";
        String testResource = symbol + ".html";
        FtPriceModel model = getModel(testResource, symbol);
        Assert.assertNotNull(model);
        LOGGER.info(model);

        String timeZoneId = model.getTimeZone();
        Assert.assertNull(timeZoneId);

        // TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);
        // Assert.assertNotNull(timeZone);
        // Assert.assertEquals(TimeZone.getTimeZone("GMT"), timeZone);

        Date date = model.getDate();
        Assert.assertNotNull(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // calendar.setTimeZone(timeZone);
        // Mon Nov 27 00:00:00 PST 2017
        Assert.assertEquals(2017, calendar.get(Calendar.YEAR));
        Assert.assertEquals(11 - 1, calendar.get(Calendar.MONTH));
        Assert.assertEquals(27, calendar.get(Calendar.DAY_OF_MONTH));
        // Assert.assertEquals(21, calendar.get(Calendar.HOUR_OF_DAY));
        // Assert.assertEquals(01, calendar.get(Calendar.MINUTE));

        Double price = model.getPrice();
        Assert.assertNotNull(price);
        Assert.assertEquals(102.98, price, 0.01);

        String currency = model.getCurrency();
        Assert.assertNotNull(currency);
        Assert.assertEquals("GBP", currency);
    }

    @Test
    public void testEquityIBM() throws IOException {
        String symbol = "IBM:NYQ";
        String testResource = symbol + ".html";
        FtPriceModel model = getModel(testResource, symbol);
        Assert.assertNotNull(model);
        LOGGER.info(model);

        String timeZoneId = model.getTimeZone();
        Assert.assertNotNull(timeZoneId);

        TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);
        Assert.assertNotNull(timeZone);
        Assert.assertEquals(TimeZone.getTimeZone("GMT"), timeZone);

        Date date = model.getDate();
        Assert.assertNotNull(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setTimeZone(timeZone);
        // Nov 28 2017 21:01 GMT
        Assert.assertEquals(2017, calendar.get(Calendar.YEAR));
        Assert.assertEquals(11 - 1, calendar.get(Calendar.MONTH));
        Assert.assertEquals(28, calendar.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(21, calendar.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(01, calendar.get(Calendar.MINUTE));

        Double price = model.getPrice();
        Assert.assertNotNull(price);
        Assert.assertEquals(152.47, price, 0.01);

        String currency = model.getCurrency();
        Assert.assertNotNull(currency);
        Assert.assertEquals("USD", currency);
    }

    private FtPriceModel getModel(String testResource, String symbol) throws IOException {
        FtPriceModel model = null;

        InputStream stream = null;

        try {
            URL url = OfxUtils.getResource(testResource, this);
            Assert.assertNotNull(url);
            stream = url.openStream();
            Document doc = Jsoup.parse(stream, "UTF-8", "http://localhost");

            if (doc != null) {
                model = FtPriceModel.parseFtDoc(doc, symbol);
            } else {
                model = null;
            }
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return model;
    }

    @Test
    @Ignore
    public void testBlock02() throws Exception {
        InputStream stream = null;

        try {
            URL url = OfxUtils.getResource("IBM:NYQ.html", this);
            Assert.assertNotNull(url);
            stream = url.openStream();
            Document doc = Jsoup.parse(stream, "UTF-8", "http://localhost");
            Assert.assertNotNull(doc);

            // <div data-f2-app-id="mod-tearsheet-key-stats">
            Element topDiv = doc.selectFirst("div[data-f2-app-id=mod-tearsheet-key-stats]");
            Assert.assertNotNull(topDiv);

            // <div class="mod-tearsheet-key-stats__data__table">
            Element divTable = topDiv.selectFirst("div.mod-tearsheet-key-stats__data__table");
            Assert.assertNotNull(divTable);
            // <table class="mod-ui-table mod-ui-table--two-column">
            Element table = divTable.selectFirst("table[class=mod-ui-table mod-ui-table--two-column]");
            Assert.assertNotNull(table);

        } finally {
            if (stream != null) {
                stream.close();
            }
        }

    }

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
        String[] etfs = { "VUKE:LSE:GBP", "FTAL:LSE:GBP" };
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
