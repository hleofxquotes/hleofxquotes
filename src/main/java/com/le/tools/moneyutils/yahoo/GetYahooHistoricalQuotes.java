package com.le.tools.moneyutils.yahoo;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import com.csvreader.CsvReader;
import com.le.tools.moneyutils.ofx.quotes.HttpUtils;
import com.le.tools.moneyutils.stockprice.AbstractStockPrice;
import com.le.tools.moneyutils.stockprice.Price;
import com.le.tools.moneyutils.stockprice.StockPrice;

public class GetYahooHistoricalQuotes extends GetYahooQuotes {
    private static final Logger log = Logger.getLogger(GetYahooHistoricalQuotes.class);

    private Date fromDate = null;
    private Date toDate = null;
    private Boolean limitToFriday = false;
    private Boolean limitToEOM = false;

    private String symbol;

    private SimpleDateFormat priceInfoLastTradeDateFormatter = new SimpleDateFormat("MM/dd/yyyy");

    private SimpleDateFormat lastTradeDateFormatter = new SimpleDateFormat("yyyy-MM-dd");

    public GetYahooHistoricalQuotes(Date fromDate, Date toDate, Boolean limitToFriday, Boolean limitToEOM) {
        super();
        setFxFileName(null);

        if (toDate == null) {
            toDate = new Date();
        }
        if (fromDate == null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(toDate);
            int days = -30;
            cal.add(Calendar.DATE, days);
            fromDate = cal.getTime();
        }
        this.setFromDate(fromDate);
        this.setToDate(toDate);
        this.setLimitToFriday(limitToFriday);
        this.setLimitToEOM(limitToEOM);

        log.info("fromDate=" + getFromDate());
        log.info("toDate=" + getToDate());
        log.info("limitToFriday=" + getLimitToFriday());
        log.info("limitToEOM=" + getLimitToEOM());

    }

    public GetYahooHistoricalQuotes() {
        this(null, null, false, false);
    }

    // http://ichart.finance.yahoo.com/table.csv?s=CSCO&d=9&e=30&f=2011&g=d&a=2&b=26&c=1990&ignore=.csv
    @Override
    protected URI createURI(List<String> stocks, String format) throws URISyntaxException {
        String scheme = "http";
        String host = "ichart.finance.yahoo.com";
        int port = -1;
        String path = "/table.csv";
        String queries = createQueries(stocks);
        return URIUtils.createURI(scheme, host, port, path, queries, null);
    }

    private String createQueries(List<String> stocks) {
        List<NameValuePair> qParams = new ArrayList<NameValuePair>();
        symbol = stocks.get(0);
        qParams.add(new BasicNameValuePair("s", symbol));

        Calendar cal = Calendar.getInstance();

        log.info("Getting historical quotes for symbol=" + symbol);
        log.info("    fromDate=" + getFromDate());
        log.info("    toDate=" + getToDate());

        cal.setTime(getFromDate());
        qParams.add(new BasicNameValuePair("a", "" + cal.get(Calendar.MONTH)));
        qParams.add(new BasicNameValuePair("b", "" + cal.get(Calendar.DAY_OF_MONTH)));
        qParams.add(new BasicNameValuePair("c", "" + cal.get(Calendar.YEAR)));

        cal.setTime(getToDate());
        qParams.add(new BasicNameValuePair("d", "" + cal.get(Calendar.MONTH)));
        qParams.add(new BasicNameValuePair("e", "" + cal.get(Calendar.DAY_OF_MONTH)));
        qParams.add(new BasicNameValuePair("f", "" + cal.get(Calendar.YEAR)));

        qParams.add(new BasicNameValuePair("g", "d"));

        qParams.add(new BasicNameValuePair("ignore", ".csv"));

        String urlString = URLEncodedUtils.format(qParams, "UTF-8");

        return urlString;
    }

    @Override
    public List<AbstractStockPrice> httpEntityToStockPriceBean(HttpEntity entity, boolean skipIfNoPrice) throws IOException {
        List<AbstractStockPrice> beans = null;

        if (entity == null) {
            return beans;
        }

        Reader reader = null;
        try {
            reader = HttpUtils.toReader(entity);
            beans = toStockPrices(reader, skipIfNoPrice);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } finally {
                    reader = null;
                }
            }
        }
        return beans;
    }

    private List<AbstractStockPrice> toStockPrices(Reader reader, boolean skipIfNoPrice) throws IOException {
        List<AbstractStockPrice> stockPrices = null;

        CsvReader csvReader = null;
        try {
            csvReader = new CsvReader(reader);
            stockPrices = toStockPriceBeans(csvReader, skipIfNoPrice);
        } finally {
            if (csvReader != null) {
                try {
                    csvReader.close();
                } finally {
                    csvReader = null;
                }
            }
        }
        return stockPrices;
    }

    private List<AbstractStockPrice> toStockPriceBeans(CsvReader reader, boolean skipIfNoPrice) throws IOException {
        List<AbstractStockPrice> beans = new ArrayList<AbstractStockPrice>();
        reader.readHeaders();
        while (reader.readRecord()) {
            String line = reader.getRawRecord();
            if (log.isDebugEnabled()) {
                log.debug(line);
            }
            AbstractStockPrice bean = parseCurrentRecord(reader);
            if (bean != null) {
                if ((bean.getLastPrice().getPrice() <= 0.0) && (skipIfNoPrice)) {
                    if (log.isDebugEnabled()) {
                        log.warn("SKIP: " + line);
                    }
                } else {
                    if (beans != null) {
                        if (acceptBean(bean)) {
                            beans.add(bean);
                        }
                    }
                }
            } else {
                log.warn("Cannot parse line=" + line);
            }
        }

        boolean sort = true;
        if (sort) {
            Comparator<AbstractStockPrice> c = new Comparator<AbstractStockPrice>() {
                @Override
                public int compare(AbstractStockPrice o1, AbstractStockPrice o2) {
                    return o1.getLastTrade().compareTo(o2.getLastTrade());
                }
            };
            Collections.sort(beans, c);
        }
        return beans;
    }

    private boolean acceptBean(AbstractStockPrice bean) {
        boolean rv = true;

        if (getLimitToFriday()) {
            rv = false;
            Date date = bean.getLastTrade();
            if (date != null) {
                GregorianCalendar calendar = new GregorianCalendar();
                calendar.setTime(date);
                int dateOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                if (dateOfWeek == Calendar.FRIDAY) {
                    return true;
                }
            }
        }

        if (getLimitToEOM()) {
            rv = false;
            Date date = bean.getLastTrade();
            if (date != null) {
                GregorianCalendar calendar = new GregorianCalendar();
                calendar.setTime(date);
                int dateOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                calendar.add(Calendar.MONTH, 1);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.add(Calendar.DATE, -1);

                int lastDateOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                if (dateOfMonth == lastDateOfMonth) {
                    return true;
                }
            }
        }

        return rv;
    }

    private AbstractStockPrice parseCurrentRecord(final CsvReader reader) throws IOException {
        if (log.isDebugEnabled()) {
            int count = reader.getColumnCount();
            log.debug("");
            for (int i = 0; i < count; i++) {
                String value = reader.get(i);
                log.debug("i=" + i + ", value=" + value);
            }
        }

        // Date,Open,High,Low,Close,Volume,Adj Close
        // 2011-10-28,18.28,18.60,18.21,18.56,47736100,18.56
        StockPrice bean = new StockPrice();
        bean.setStockSymbol(symbol);

        bean.setStockName(symbol);

        String val = null;

        // Close
        // 18.60
        double price = 0;
        val = reader.get("Close");
        if (val != null) {
            try {
                price = Double.valueOf(val);
            } catch (NumberFormatException e) {
                log.warn(e);
                price = 0;
            }
        }
        Price lastPrice = new Price(price);
        bean.setLastPrice(lastPrice);

        Date lastTrade = null;

        val = reader.get("Date");
        if (val != null) {
            try {
                lastTrade = lastTradeDateFormatter.parse(val);
            } catch (ParseException e) {
                log.warn(e);
                lastTrade = new Date();
            }
        }
        // Date
        // 2011-10-28
        bean.setLastTrade(lastTrade);

        bean.setLastTradeDate(priceInfoLastTradeDateFormatter.format(lastTrade));

        // TODO:
        bean.init();

        return bean;
    }

    @Override
    public HttpResponse httpGet(List<String> stocks, String format) throws URISyntaxException, IOException, ClientProtocolException {
        // Calendar cal = Calendar.getInstance();
        // cal.setTime(getToDate());
        // cal.add(Calendar.DATE, days);
        // setFromDate(cal.getTime());
        return super.httpGet(stocks, format);
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public Boolean getLimitToFriday() {
        return limitToFriday;
    }

    public void setLimitToFriday(Boolean limitToFriday) {
        this.limitToFriday = limitToFriday;
    }

    public Boolean getLimitToEOM() {
        return limitToEOM;
    }

    public void setLimitToEOM(Boolean limitToEOM) {
        this.limitToEOM = limitToEOM;
    }
}