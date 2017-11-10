
package com.hungle.tools.moneyutils.stockprice;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

// TODO: Auto-generated Javadoc
/**
 * The Class StockPriceTest.
 */
public class StockPriceTest {
    private static final Logger LOGGER = Logger.getLogger(StockPriceTest.class);

    /**
     * Parses the from csv file.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void parseFromCsvFile() throws Exception {
        Reader reader = null;
        reader = new FileReader(new File("src/test/data/quotes_001.csv"));
        // "YHOO","Yahoo! Inc.",12.76,"8/2/2011","4:00pm",12.75,13.18

        List<AbstractStockPrice> stockPrices = StockPriceCsvUtils.toStockPrices(reader);

        Assert.assertNotNull(stockPrices);
        Assert.assertEquals(1, stockPrices.size());

        AbstractStockPrice stockPrice = stockPrices.get(0);

        Assert.assertEquals("YHOO", stockPrice.getStockSymbol());
        Assert.assertEquals("Yahoo! Inc.", stockPrice.getStockName());

        Price price = stockPrice.getLastPrice();
        Double expected = 12.76;
        Assert.assertEquals(expected, price.getPrice());

        Date lastTrade = stockPrice.getLastTrade();
        SimpleDateFormat lastTradeDateTimeFormatter = new SimpleDateFormat(
                AbstractStockPrice.DEFAULT_LAST_TRADE_DATE_TIME_PATTERN);
        Date expectedDate = lastTradeDateTimeFormatter.parse("8/2/2011 4:00pm");
        Assert.assertEquals(expectedDate, lastTrade);
    }

    /**
     * Value of test.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    public void valueOfTest() throws IOException {
        CsvRow row = new CsvRow() {
            private String[] columns = { "IBM", "INTL BUSINESS MAC", "121.64", "10/16/2009", "4:00pm", "121.25",
                    "123.70" };

            public String getColumnValue(int i) {
                return columns[i];
            }

            public String getRawRecord() {
                return columns.toString();
            }
        };
        AbstractStockPrice stockPrice = new StockPrice(row);
        Assert.assertNotNull(stockPrice);
        String expected = "snl1d1t1gh";
        Assert.assertEquals(expected, stockPrice.getFormat());

        row = new CsvRow() {
            private String[] columns = { "IBM", "", "121.64", "10/16/2009", "4:00pm", "121.25", "123.70" };

            public String getColumnValue(int i) {
                return columns[i];
            }

            public String getRawRecord() {
                return columns.toString();
            }
        };
        stockPrice = new StockPrice(row);
        Assert.assertNotNull(stockPrice);
        Assert.assertNotNull(stockPrice.getStockName());

    }

    /**
     * Test field info.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    public void testFieldInfo() throws IOException {
        CsvRow row;
        AbstractStockPrice stockPrice;
        String expected;
        Set<FieldInfo> properties = FieldInfo.createFieldInfoSet();
        // format = "snl1d1t1gh";
        // properties = new LinkedHashMap<String, String>();
        properties.add(new FieldInfo(0, "s", "stockSymbol"));
        properties.add(new FieldInfo(1, "n", "stockName"));
        // properties.put("l1", "lastPrice");
        // properties.put("d1", "lastTradeDate");
        // properties.put("t1", "lastTradeTime");
        properties.add(new FieldInfo(2, "g", "dayLow"));
        properties.add(new FieldInfo(3, "h", "dayHigh"));
        row = new CsvRow() {
            private String[] columns = { "IBM", "INTL BUSINESS MAC", "121.25", "123.70" };

            public String getColumnValue(int i) {
                return columns[i];
            }

            public String getRawRecord() {
                return columns.toString();
            }
        };
        stockPrice = new StockPrice(row, properties);
        Assert.assertNotNull(stockPrice);

        expected = "sngh";
        Assert.assertEquals(expected, stockPrice.getFormat());
    }

    @Test
    public void testTradeDateParsing() throws ParseException {
        Date date = null;

        SimpleDateFormat lastTradeDateFormatter = new SimpleDateFormat(
                AbstractStockPrice.DEFAULT_LAST_TRADE_DATE_PATTERN);

        SimpleDateFormat lastTradeTimeFormatter = new SimpleDateFormat(
                AbstractStockPrice.DEFAULT_LAST_TRADE_TIME_PATTERN);

        SimpleDateFormat lastTradeDateTimeFormatter = new SimpleDateFormat(
                AbstractStockPrice.DEFAULT_LAST_TRADE_DATE_TIME_PATTERN);

        // "MM/dd/yyyy"
        String lastTradeDate = null;
        // "hh:mm"
        String lastTradeTime = null;

        Date now = new Date();
        
        // both fields are available
        lastTradeDate = lastTradeDateFormatter.format(now);
        lastTradeTime = lastTradeTimeFormatter.format(now);

        date = StockPrice.createLastTradeDate(lastTradeDate, lastTradeTime, lastTradeDateTimeFormatter,
                lastTradeDateFormatter, lastTradeTimeFormatter);
        Assert.assertNotNull(date);

        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTime(now);

        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(date);

        int[] fields = {
                Calendar.YEAR,
                Calendar.MONTH,
                Calendar.DATE,
                Calendar.HOUR,
                Calendar.MINUTE,
        };
        for (int field : fields) {
            Assert.assertEquals(nowCalendar.get(field), dateCalendar.get(field));
        }

        // ONLY lastTradeDate
        lastTradeDate = lastTradeDateFormatter.format(now);
        lastTradeTime = null;
        
        date = StockPrice.createLastTradeDate(lastTradeDate, lastTradeTime, lastTradeDateTimeFormatter,
                lastTradeDateFormatter, lastTradeTimeFormatter);
        Assert.assertNotNull(date);
        
        dateCalendar.setTime(date);

        int[] fields2 = {
                Calendar.YEAR,
                Calendar.MONTH,
                Calendar.DATE,
        };
        for (int field : fields2) {
            Assert.assertEquals(nowCalendar.get(field), dateCalendar.get(field));
        }
        
        // ONLY lastTradeTime
        lastTradeDate = null;
        lastTradeTime = lastTradeTimeFormatter.format(now);
        
        date = StockPrice.createLastTradeDate(lastTradeDate, lastTradeTime, lastTradeDateTimeFormatter,
                lastTradeDateFormatter, lastTradeTimeFormatter);
        Assert.assertNotNull(date);
        
        dateCalendar.setTime(date);

        int[] fields3 = {
                Calendar.HOUR,
                Calendar.MINUTE,
        };
        for (int field : fields3) {
            Assert.assertEquals(nowCalendar.get(field), dateCalendar.get(field));
        }
    }
}
