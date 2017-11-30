package com.hungle.tools.moneyutils.yahoo;

import java.time.ZonedDateTime;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.hungle.msmoney.qs.yahoo.YahooScreenScrapper2QuoteGetter;


public class YahooScreenScrapper2QuoteGetterTest {
    private static final Logger LOGGER = Logger.getLogger(YahooScreenScrapper2QuoteGetterTest.class);
    
    @Test
    public void testRegularMarketTime() {
        // "regularMarketTime": 1510124462
//        "quoteType": {
//        "exchange": "MUN",
//        "exchangeTimezoneName": "Europe/Berlin",
//        "exchangeTimezoneShortName": "CET",
//        "gmtOffSetMilliseconds": "3600000",
        
        long regularMarketTime = 1510124462;
        String exchangeTimezoneName = "Europe/Berlin";
        ZonedDateTime zoneDateTime = YahooScreenScrapper2QuoteGetter.getMarketZonedDateTime(regularMarketTime, exchangeTimezoneName); 
        
        Assert.assertEquals(2017, zoneDateTime.getYear());
        Assert.assertEquals(11, zoneDateTime.getMonthValue());
        Assert.assertEquals(8, zoneDateTime.getDayOfMonth());

        Assert.assertEquals(8, zoneDateTime.getHour());
        Assert.assertEquals(1, zoneDateTime.getMinute());
        Assert.assertEquals(2, zoneDateTime.getSecond());
        
        Assert.assertEquals(exchangeTimezoneName, zoneDateTime.getZone().getId());
    }
}
