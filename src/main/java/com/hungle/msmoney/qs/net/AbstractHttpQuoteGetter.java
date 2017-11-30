package com.hungle.msmoney.qs.net;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import com.hungle.msmoney.core.fx.FxTableUtils;
import com.hungle.msmoney.core.misc.StopWatch;
import com.hungle.msmoney.core.misc.Utils;
import com.hungle.msmoney.core.ofx.OfxUtils;
import com.hungle.msmoney.core.stockprice.AbstractStockPrice;
import com.hungle.msmoney.core.stockprice.FxSymbol;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractHttpQuoteGetter.
 */
public abstract class AbstractHttpQuoteGetter implements HttpQuoteGetter {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = Logger.getLogger(AbstractHttpQuoteGetter.class);

    /** The Constant DEFAULT_SCHEME. */
    private static final String DEFAULT_SCHEME = "http";

    /** The Constant DEFAULT_HOST. */
    private static final String DEFAULT_HOST = "download.finance.yahoo.com";

    /** The Constant DEFAULT_PORT. */
    private static final int DEFAULT_PORT = -1;

    /** The Constant DEFAULT_PATH. */
    private static final String DEFAULT_PATH = "/d/quotes.csv";

    /** The Constant DEFAULT_BUCKET_SIZE. */
    private static final int DEFAULT_BUCKET_SIZE = 25;

    /** The Constant DEFAULT_TIMEOUT. */
    private static final long DEFAULT_TIMEOUT = 120L;

    /** The Constant DEFAULT_FX_FILENAME. */
    private static final String DEFAULT_FX_FILENAME = "fx.csv";

    /** The Constant threadPool. */
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(3);

    /** The scheme. */
    private String scheme = DEFAULT_SCHEME;

    /** The host. */
    private String host = DEFAULT_HOST;

    /** The port. */
    private int port = DEFAULT_PORT;

    /** The path. */
    private String path = DEFAULT_PATH;

    /** The bucket size. */
    private int bucketSize = DEFAULT_BUCKET_SIZE;

    /** The timeout. */
    private long timeout = DEFAULT_TIMEOUT;

    /** The time out unit. */
    private TimeUnit timeOutUnit = TimeUnit.SECONDS;

    /** The filter fx quotes. */
    private boolean filterFxQuotes = true;

    /** The fx symbols. */
    private List<AbstractStockPrice> fxSymbols;

    /** The fx file name. */
    private String fxFileName = FxTableUtils.DEFAULT_FX_FILENAME;

    /** The keep fx symbols. */
    private boolean keepFxSymbols = true;

    /**
     * Instantiates a new abstract http quote getter.
     */
    public AbstractHttpQuoteGetter() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.le.tools.moneyutils.ofx.quotes.net.HttpQuoteGetter#httpGet(java.util.
     * List, java.lang.String)
     */
    @Override
    public HttpResponse httpGet(List<String> stocks, String format)
            throws URISyntaxException, IOException, ClientProtocolException {
        URI uri = createURI(stocks, format);
        HttpGet httpGet = new HttpGet(uri);
        LOGGER.info("uri=" + uri);
        HttpClient httpClient = createHttpClient();
        HttpResponse response = httpClient.execute(httpGet);
        return response;
    }

    protected HttpClient createHttpClient() {
        String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";
        HttpClient httpClient = HttpClientBuilder.create()
                .setUserAgent(userAgent)
                .build();
        return httpClient;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.le.tools.moneyutils.ofx.quotes.net.HttpQuoteGetter#
     * httpEntityToStockPriceBean(org.apache.http.HttpEntity, boolean)
     */
    @Override
    public List<AbstractStockPrice> httpEntityToStockPriceBean(HttpEntity entity, boolean skipIfNoPrice)
            throws IOException {
        return HttpUtils.toStockPriceBean(entity, skipIfNoPrice);
    }

    /**
     * Creates the parameters.
     *
     * @param stocks
     *            the stocks
     * @param format
     *            the format
     * @return the list
     */
    private static List<NameValuePair> createParameters(List<String> stocks, String format) {
        // http://download.finance.yahoo.com/d/quotes.csv?s=IBM&f=sl1d1t1c1ohgv&e=.csv
        // http://download.finance.yahoo.com/d/quotes.csv?s=EDL.L&f=sl1d1t1c1ohgv&e=.csv
        // http://uk.old.finance.yahoo.com/d/quotes.csv?s=GB0032346800.L&f=sl1d1t1c1ohgv&e=.csv
        // ^GSPC
        // http://download.finance.yahoo.com/d/quotes.csv?s=%5EGSPC&f=sl1d1t1c1ohgv&e=.csv
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("s", OfxUtils.toSeparatedString(stocks)));
        parameters.add(new BasicNameValuePair("f", format));
        parameters.add(new BasicNameValuePair("e", ".csv"));
        return parameters;
    }

    /**
     * Creates the URI.
     *
     * @param stocks
     *            the stocks
     * @param format
     *            the format
     * @return the uri
     * @throws URISyntaxException
     *             the URI syntax exception
     */
    protected URI createURI(List<String> stocks, String format) throws URISyntaxException {
        URI uri = null;

        uri = new URIBuilder().setScheme(getScheme()).setHost(getHost()).setPort(getPort()).setPath(getPath())
                .setParameters(createParameters(stocks, format)).setFragment(null).build();

        return uri;
    }

    /**
     * Gets the scheme.
     *
     * @return the scheme
     */
    public String getScheme() {
        return scheme;
    }

    /**
     * Gets the host.
     *
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * Sets the host.
     *
     * @param host
     *            the new host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Gets the port.
     *
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * Gets the path.
     *
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * Gets the quotes.
     *
     * @param stocks
     *            the stocks
     * @param listener
     *            the listener
     * @param skipNoPrice
     *            the skip no price
     * @return the quotes
     * @throws URISyntaxException
     *             the URI syntax exception
     * @throws ClientProtocolException
     *             the client protocol exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    protected List<AbstractStockPrice> getQuotes(List<String> stocks, GetQuotesListener listener, boolean skipNoPrice)
            throws IOException {
        LOGGER.info("> getQuotes");

        fxSymbols = new ArrayList<AbstractStockPrice>();

        StopWatch stopWatch = new StopWatch();
        List<AbstractStockPrice> prices = new ArrayList<AbstractStockPrice>();
        try {
            if (stocks == null) {
                return prices;
            }

            LOGGER.info("stocks.size=" + stocks.size());
            LOGGER.info("bucketSize=" + bucketSize);

            if (stocks.size() <= 0) {
                return prices;
            }

            List<List<String>> subLists = Utils.splitToSubLists(stocks, bucketSize);
            List<GetQuotesTask> subTasks = new ArrayList<GetQuotesTask>();
            for (List<String> subList : subLists) {
                GetQuotesTask task = new GetQuotesTask(this, subList, skipNoPrice);
                task.setListener(listener);
                subTasks.add(task);
            }
            if (listener != null) {
                listener.setSubTaskSize(subTasks.size());
            }

            List<Future<List<AbstractStockPrice>>> futures = null;
            try {
                futures = threadPool.invokeAll(subTasks, timeout, timeOutUnit);
            } catch (InterruptedException e) {
                LOGGER.error(e);
            }

            if (futures == null) {
                LOGGER.warn("Failed to invokeAll");
                return prices;
            }

            for (Future<List<AbstractStockPrice>> future : futures) {
                if (future.isCancelled()) {
                    LOGGER.warn("One of the tasks has timeout.");
                    continue;
                }
                try {
                    List<AbstractStockPrice> receivedFromQuoteSource = future.get();
                    addPrices(receivedFromQuoteSource, prices);
                } catch (InterruptedException e) {
                    LOGGER.warn(e);
                } catch (ExecutionException e) {
                    LOGGER.warn(e, e);
                }
            }

            if (fxSymbols != null) {
                FxTableUtils.writeFxFile(fxSymbols, fxFileName);
            }
        } finally {
            long delta = stopWatch.click();
            LOGGER.info("< getQuotes, delta=" + delta);
        }

        return prices;
    }

    /**
     * Shutdown.
     */
    public void shutdown() {
        threadPool.shutdown();
    }

    /**
     * Adds the prices we received to the all price list. Filter out fx as
     * needed.
     * @param newPrices
     *            the received from quote source
     * @param prices
     *            the beans
     */
    protected void addPrices(List<AbstractStockPrice> newPrices, List<AbstractStockPrice> prices) {
        if (newPrices == null) {
            return;
        }
        
        List<AbstractStockPrice> filtered = newPrices;
        if (filterFxQuotes) {
            filtered = new ArrayList<AbstractStockPrice>();
            for (AbstractStockPrice newPrice : newPrices) {
                FxSymbol fxSymbol = newPrice.getFxSymbol();
                if (fxSymbol == null) {
                    filtered.add(newPrice);
                } else {
                    fxSymbols.add(newPrice);
                    if (keepFxSymbols) {
                        filtered.add(newPrice);
                    }
                }
            }
        }
        prices.addAll(filtered);
    }

    /**
     * Checks if is filter fx quotes.
     *
     * @return true, if is filter fx quotes
     */
    public boolean isFilterFxQuotes() {
        return filterFxQuotes;
    }

    /**
     * Sets the filter fx quotes.
     *
     * @param filterFxQuotes
     *            the new filter fx quotes
     */
    public void setFilterFxQuotes(boolean filterFxQuotes) {
        this.filterFxQuotes = filterFxQuotes;
    }

    /**
     * Gets the fx symbols.
     *
     * @return the fx symbols
     */
    public List<AbstractStockPrice> getFxSymbols() {
        return fxSymbols;
    }

    /**
     * Gets the fx file name.
     *
     * @return the fx file name
     */
    public String getFxFileName() {
        return fxFileName;
    }

    /**
     * Sets the fx file name.
     *
     * @param fxFileName
     *            the new fx file name
     */
    public void setFxFileName(String fxFileName) {
        this.fxFileName = fxFileName;
    }

    /**
     * Checks if is keep fx symbols.
     *
     * @return true, if is keep fx symbols
     */
    public boolean isKeepFxSymbols() {
        return keepFxSymbols;
    }

    /**
     * Sets the keep fx symbols.
     *
     * @param keepFxSymbols
     *            the new keep fx symbols
     */
    public void setKeepFxSymbols(boolean keepFxSymbols) {
        this.keepFxSymbols = keepFxSymbols;
    }

    /**
     * Gets the quotes.
     *
     * @param stocks
     *            the stocks
     * @return the quotes
     * @throws ClientProtocolException
     *             the client protocol exception
     * @throws URISyntaxException
     *             the URI syntax exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public List<AbstractStockPrice> getQuotes(List<String> stocks) throws IOException {
        GetQuotesListener listener = null;
        boolean skipNoPrice = true;
        List<AbstractStockPrice> quotes = getQuotes(stocks, listener, skipNoPrice);
        return quotes;
    }

    /**
     * Gets the quotes.
     *
     * @param stocks
     *            the stocks
     * @param listener
     *            the listener
     * @return the quotes
     * @throws ClientProtocolException
     *             the client protocol exception
     * @throws URISyntaxException
     *             the URI syntax exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public List<AbstractStockPrice> getQuotes(List<String> stocks, GetQuotesListener listener) throws IOException {
        boolean skipNoPrice = true;
        List<AbstractStockPrice> quotes = getQuotes(stocks, listener, skipNoPrice);
        return quotes;
    }

    public int getBucketSize() {
        return bucketSize;
    }

    public void setBucketSize(int bucketSize) {
        this.bucketSize = bucketSize;
    }
}