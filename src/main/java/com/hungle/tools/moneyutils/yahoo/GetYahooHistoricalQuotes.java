package com.hungle.tools.moneyutils.yahoo;

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
import com.hungle.tools.moneyutils.ofx.quotes.HttpUtils;
import com.hungle.tools.moneyutils.stockprice.AbstractStockPrice;
import com.hungle.tools.moneyutils.stockprice.Price;
import com.hungle.tools.moneyutils.stockprice.StockPrice;

// TODO: Auto-generated Javadoc
/**
 * The Class GetYahooHistoricalQuotes.
 */
public class GetYahooHistoricalQuotes extends YahooQuotesGetter {
    
    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(GetYahooHistoricalQuotes.class);

    /** The from date. */
    private Date fromDate = null;
    
    /** The to date. */
    private Date toDate = null;
    
    /** The limit to friday. */
    private Boolean limitToFriday = false;
    
    /** The limit to EOM. */
    private Boolean limitToEOM = false;

    /** The symbol. */
    private String symbol;

    /** The price info last trade date formatter. */
    private SimpleDateFormat priceInfoLastTradeDateFormatter = new SimpleDateFormat("MM/dd/yyyy");

    /** The last trade date formatter. */
    private SimpleDateFormat lastTradeDateFormatter = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Instantiates a new gets the yahoo historical quotes.
     *
     * @param fromDate the from date
     * @param toDate the to date
     * @param limitToFriday the limit to friday
     * @param limitToEOM the limit to EOM
     */
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

        LOGGER.info("fromDate=" + getFromDate());
        LOGGER.info("toDate=" + getToDate());
        LOGGER.info("limitToFriday=" + getLimitToFriday());
        LOGGER.info("limitToEOM=" + getLimitToEOM());

    }

    /**
     * Instantiates a new gets the yahoo historical quotes.
     */
    public GetYahooHistoricalQuotes() {
        this(null, null, false, false);
    }

    /* (non-Javadoc)
     * @see com.hungle.tools.moneyutils.yahoo.GetYahooQuotes#createURI(java.util.List, java.lang.String)
     */
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

    /**
     * Creates the queries.
     *
     * @param stocks the stocks
     * @return the string
     */
    private String createQueries(List<String> stocks) {
        List<NameValuePair> qParams = new ArrayList<NameValuePair>();
        symbol = stocks.get(0);
        qParams.add(new BasicNameValuePair("s", symbol));

        Calendar cal = Calendar.getInstance();

        LOGGER.info("Getting historical quotes for symbol=" + symbol);
        LOGGER.info("    fromDate=" + getFromDate());
        LOGGER.info("    toDate=" + getToDate());

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

    /* (non-Javadoc)
     * @see com.hungle.tools.moneyutils.yahoo.GetYahooQuotes#httpEntityToStockPriceBean(org.apache.http.HttpEntity, boolean)
     */
    @Override
    public List<AbstractStockPrice> httpEntityToStockPriceBean(HttpEntity entity, boolean skipIfNoPrice) throws IOException {
        List<AbstractStockPrice> beans = null;

        if (entity == null) {
            return beans;
        }

        Reader reader = null;
        try {
            reader = HttpUtils.entityToReader(entity);
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

    /**
     * To stock prices.
     *
     * @param reader the reader
     * @param skipIfNoPrice the skip if no price
     * @return the list
     * @throws IOException Signals that an I/O exception has occurred.
     */
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

    /**
     * To stock price beans.
     *
     * @param reader the reader
     * @param skipIfNoPrice the skip if no price
     * @return the list
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private List<AbstractStockPrice> toStockPriceBeans(CsvReader reader, boolean skipIfNoPrice) throws IOException {
        List<AbstractStockPrice> beans = new ArrayList<AbstractStockPrice>();
        reader.readHeaders();
        while (reader.readRecord()) {
            String line = reader.getRawRecord();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(line);
            }
            AbstractStockPrice bean = parseCurrentRecord(reader);
            if (bean != null) {
                if ((bean.getLastPrice().getPrice() <= 0.0) && (skipIfNoPrice)) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.warn("SKIP: " + line);
                    }
                } else {
                    if (beans != null) {
                        if (acceptBean(bean)) {
                            beans.add(bean);
                        }
                    }
                }
            } else {
                LOGGER.warn("Cannot parse line=" + line);
            }
        }

        boolean sort = true;
        if (sort) {
            Comparator<AbstractStockPrice> c = new Comparator<AbstractStockPrice>() {
                @Override
                public int compare(AbstractStockPrice o1, AbstractStockPrice o2) {
                    return o1.getLastTradeDate().compareTo(o2.getLastTradeDate());
                }
            };
            Collections.sort(beans, c);
        }
        return beans;
    }

    /**
     * Accept bean.
     *
     * @param bean the bean
     * @return true, if successful
     */
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

    /**
     * Parses the current record.
     *
     * @param reader the reader
     * @return the abstract stock price
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private AbstractStockPrice parseCurrentRecord(final CsvReader reader) throws IOException {
        if (LOGGER.isDebugEnabled()) {
            int count = reader.getColumnCount();
            LOGGER.debug("");
            for (int i = 0; i < count; i++) {
                String value = reader.get(i);
                LOGGER.debug("i=" + i + ", value=" + value);
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
                LOGGER.warn(e);
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
                LOGGER.warn(e);
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

    /* (non-Javadoc)
     * @see com.hungle.tools.moneyutils.yahoo.GetYahooQuotes#httpGet(java.util.List, java.lang.String)
     */
    @Override
    public HttpResponse httpGet(List<String> stocks, String format) throws URISyntaxException, IOException, ClientProtocolException {
        // Calendar cal = Calendar.getInstance();
        // cal.setTime(getToDate());
        // cal.add(Calendar.DATE, days);
        // setFromDate(cal.getTime());
        return super.httpGet(stocks, format);
    }

    /**
     * Gets the from date.
     *
     * @return the from date
     */
    public Date getFromDate() {
        return fromDate;
    }

    /**
     * Sets the from date.
     *
     * @param fromDate the new from date
     */
    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    /**
     * Gets the to date.
     *
     * @return the to date
     */
    public Date getToDate() {
        return toDate;
    }

    /**
     * Sets the to date.
     *
     * @param toDate the new to date
     */
    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    /**
     * Gets the limit to friday.
     *
     * @return the limit to friday
     */
    public Boolean getLimitToFriday() {
        return limitToFriday;
    }

    /**
     * Sets the limit to friday.
     *
     * @param limitToFriday the new limit to friday
     */
    public void setLimitToFriday(Boolean limitToFriday) {
        this.limitToFriday = limitToFriday;
    }

    /**
     * Gets the limit to EOM.
     *
     * @return the limit to EOM
     */
    public Boolean getLimitToEOM() {
        return limitToEOM;
    }

    /**
     * Sets the limit to EOM.
     *
     * @param limitToEOM the new limit to EOM
     */
    public void setLimitToEOM(Boolean limitToEOM) {
        this.limitToEOM = limitToEOM;
    }
}