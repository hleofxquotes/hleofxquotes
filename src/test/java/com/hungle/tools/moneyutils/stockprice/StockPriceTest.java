package com.le.tools.moneyutils.stockprice;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class StockPriceTest {
    @Test
    public void parseFromCsvFile() throws Exception {
        Reader reader = null;
        reader = new FileReader(new File("src/test/data/quotes_001.csv"));
        List<AbstractStockPrice> stockPrices = CsvUtils.toStockPrices(reader);

        Assert.assertNotNull(stockPrices);
        Assert.assertEquals(1, stockPrices.size());

        AbstractStockPrice stockPrice = stockPrices.get(0);

        Assert.assertEquals("YHOO", stockPrice.getStockSymbol());
        Assert.assertEquals("Yahoo! Inc.", stockPrice.getStockName());

        Price price = stockPrice.getLastPrice();
        Double expected = 12.76;
        Assert.assertEquals(expected, price.getPrice());

        Date lastTrade = stockPrice.getLastTrade();
        SimpleDateFormat lastTradeDateFormatter = new SimpleDateFormat(StockPrice.DEFAULT_LAST_TRADE_DATE_PATTERN);
        Date expectedDate = lastTradeDateFormatter.parse("8/2/2011");
        Assert.assertEquals(expectedDate, lastTrade);
    }

    @Test
    public void valueOfTest() throws IOException {
        CsvRow row = new CsvRow() {
            private String[] columns = { "IBM", "INTL BUSINESS MAC", "121.64", "10/16/2009", "4:00pm", "121.25", "123.70" };

            public String getColumnValue(int i) {
                return columns[i];
            }

            public String getRawRecord() {
                return columns.toString();
            }
        };
        StockPrice stockPrice = new StockPrice(row);
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

    @Test
    public void testFieldInfo() throws IOException {
        CsvRow row;
        StockPrice stockPrice;
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
}
