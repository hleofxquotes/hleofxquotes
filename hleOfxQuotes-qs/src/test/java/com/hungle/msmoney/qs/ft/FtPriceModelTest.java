package com.hungle.msmoney.qs.ft;

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

import com.hungle.msmoney.core.misc.ResourceUtils;

public class FtPriceModelTest {
    private static final Logger LOGGER = Logger.getLogger(FtPriceModelTest.class);

    @Test
    public void testFundIE00BYY18M47() throws IOException {
        // IE00BYY18M47:GBP.html
//        String symbol = "IE00BYY18M47:GBP";
        String symbol = "IE00BYY18M47_GBP";
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
        
        // name: Nomura Cross Asset Momentum UCITS Fund S GBP
        String name = model.getName();
        Assert.assertNotNull(name);
        Assert.assertEquals("Nomura Cross Asset Momentum UCITS Fund S GBP", name);
        
    }

    @Test
    public void testEquityIBM() throws IOException {
        // String symbol = "IBM:NYQ";
        String symbol = "IBM_NYQ";
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
        
        // name: International Business Machines Corp
        String name = model.getName();
        Assert.assertNotNull(name);
        Assert.assertEquals("International Business Machines Corp", name);

    }

    private FtPriceModel getModel(String testResource, String symbol) throws IOException {
        FtPriceModel model = null;

        InputStream stream = null;

        try {
            URL url = ResourceUtils.getResource(testResource, this);
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
//            String symbol = "IBM:NYQ";
            String symbol = "IBM_NYQ";
            URL url = ResourceUtils.getResource(symbol + ".html", this);
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
}
