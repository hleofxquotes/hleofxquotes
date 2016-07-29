package com.hungle.tools.moneyutils.yahoo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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

import com.hungle.tools.moneyutils.ofx.quotes.GetQuotesListener;
import com.hungle.tools.moneyutils.ofx.quotes.GetQuotesTask;
import com.hungle.tools.moneyutils.ofx.quotes.HttpUtils;
import com.hungle.tools.moneyutils.ofx.quotes.OfxUtils;
import com.hungle.tools.moneyutils.ofx.quotes.StopWatch;
import com.hungle.tools.moneyutils.ofx.quotes.Utils;
import com.hungle.tools.moneyutils.ofx.quotes.net.HttpQuoteGetter;
import com.hungle.tools.moneyutils.stockprice.AbstractStockPrice;
import com.hungle.tools.moneyutils.stockprice.FxSymbol;

// TODO: Auto-generated Javadoc
/**
 * The Class GetYahooQuotes.
 */
public class YahooQuotesGetter implements HttpQuoteGetter {
    
    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(YahooQuotesGetter.class);
    
    /** The Constant DEFAULT_TIMEOUT. */
    private static final long DEFAULT_TIMEOUT = 120L;
    
    /** The Constant DEFAULT_BUCKET_SIZE. */
    private static final int DEFAULT_BUCKET_SIZE = 25;
    
    /** The Constant threadPool. */
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(3);

    /** The Constant DEFAULT_SCHEME. */
    private static final String DEFAULT_SCHEME = "http";
    
    /** The Constant DEFAULT_HOST. */
    public static final String DEFAULT_HOST = "download.finance.yahoo.com";
    
    /** The Constant QUOTE_HOSTS. */
    public static final Map<String, String> QUOTE_HOSTS = new TreeMap<String, String>();
    static {
        QUOTE_HOSTS.put("US", "download.finance.yahoo.com");
        QUOTE_HOSTS.put("Argentina", "ar.finance.yahoo.com");
        QUOTE_HOSTS.put("Australia & NZ", "au.finance.yahoo.com");
        QUOTE_HOSTS.put("Brazil", "br.finance.yahoo.com");
        QUOTE_HOSTS.put("Canada", "ca.finance.yahoo.com");
        QUOTE_HOSTS.put("China", "cn.finance.yahoo.com");
        // QUOTE_HOSTS.put("Chinese", "chinese.finance.yahoo.com");
        QUOTE_HOSTS.put("France", "fr.finance.yahoo.com");
        QUOTE_HOSTS.put("French Canada", "cf.finance.yahoo.com");
        QUOTE_HOSTS.put("Germany", "de.finance.yahoo.com");
        QUOTE_HOSTS.put("Hong Kong", "hk.finance.yahoo.com");
        QUOTE_HOSTS.put("India", "in.finance.yahoo.com");
        QUOTE_HOSTS.put("Italy", "it.finance.yahoo.com");
        QUOTE_HOSTS.put("Japan", "finance.yahoo.co.jp");
        QUOTE_HOSTS.put("Korea", "kr.finance.yahoo.com");
        QUOTE_HOSTS.put("Mexico", "mx.finance.yahoo.com");
        QUOTE_HOSTS.put("Singapore", "sg.finance.yahoo.com");
        QUOTE_HOSTS.put("Spain", "es.finance.yahoo.com");
        QUOTE_HOSTS.put("Spanish", "espanol.finance.yahoo.com");
        QUOTE_HOSTS.put("Taiwan", "tw.stock.yahoo.com");
        QUOTE_HOSTS.put("UK & Ireland", "uk.finance.yahoo.com");
    }

    /** The Constant DEFAULT_PORT. */
    private static final int DEFAULT_PORT = -1;
    
    /** The Constant DEFAULT_PATH. */
    private static final String DEFAULT_PATH = "/d/quotes.csv";

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
    
    /** The time out unit. */
    private TimeUnit timeOutUnit = TimeUnit.SECONDS;
    
    /** The timeout. */
    private long timeout = DEFAULT_TIMEOUT;

    /** The filter fx quotes. */
    private boolean filterFxQuotes = true;
    
    /** The fx symbols. */
    private List<AbstractStockPrice> fxSymbols;
    
    /** The fx file name. */
    private String fxFileName = "fx.csv";
    
    /** The keep fx symbols. */
    private boolean keepFxSymbols = true;

    /* (non-Javadoc)
     * @see com.le.tools.moneyutils.ofx.quotes.net.HttpQuoteGetter#httpGet(java.util.List, java.lang.String)
     */
    @Override
    public HttpResponse httpGet(List<String> stocks, String format) throws URISyntaxException, IOException, ClientProtocolException {
        URI uri = createURI(stocks, format);
        HttpGet httpGet = new HttpGet(uri);
        LOGGER.info("uri=" + uri);
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpResponse response = httpClient.execute(httpGet);
        return response;
    }

    /* (non-Javadoc)
     * @see com.le.tools.moneyutils.ofx.quotes.net.HttpQuoteGetter#httpEntityToStockPriceBean(org.apache.http.HttpEntity, boolean)
     */
    @Override
    public List<AbstractStockPrice> httpEntityToStockPriceBean(HttpEntity entity, boolean skipIfNoPrice) throws IOException {
        return HttpUtils.toStockPriceBean(entity, skipIfNoPrice);
    }

    /**
     * Gets the quotes.
     *
     * @param stocks the stocks
     * @return the quotes
     * @throws ClientProtocolException the client protocol exception
     * @throws URISyntaxException the URI syntax exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public List<AbstractStockPrice> getQuotes(List<String> stocks) throws ClientProtocolException, URISyntaxException, IOException {
        GetQuotesListener listener = null;
        boolean skipNoPrice = true;
        List<AbstractStockPrice> quotes = getQuotes(stocks, listener, skipNoPrice);
        return quotes;
    }

    /**
     * Gets the quotes.
     *
     * @param stocks the stocks
     * @param listener the listener
     * @return the quotes
     * @throws ClientProtocolException the client protocol exception
     * @throws URISyntaxException the URI syntax exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public List<AbstractStockPrice> getQuotes(List<String> stocks, GetQuotesListener listener) throws ClientProtocolException, URISyntaxException, IOException {
        boolean skipNoPrice = true;
        List<AbstractStockPrice> quotes = getQuotes(stocks, listener, skipNoPrice);
        return quotes;
    }

    /**
     * Gets the quotes.
     *
     * @param stocks the stocks
     * @param listener the listener
     * @param skipNoPrice the skip no price
     * @return the quotes
     * @throws URISyntaxException the URI syntax exception
     * @throws ClientProtocolException the client protocol exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public List<AbstractStockPrice> getQuotes(List<String> stocks, GetQuotesListener listener, boolean skipNoPrice) throws URISyntaxException,
            ClientProtocolException, IOException {
        LOGGER.info("> getQuotes");

        fxSymbols = new ArrayList<AbstractStockPrice>();

        StopWatch stopWatch = new StopWatch();
        List<AbstractStockPrice> beans = new ArrayList<AbstractStockPrice>();
        try {
            if (stocks == null) {
                return beans;
            }

            LOGGER.info("stocks.size=" + stocks.size());

            if (stocks.size() <= 0) {
                return beans;
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
                return beans;
            }

            for (Future<List<AbstractStockPrice>> future : futures) {
                if (future.isCancelled()) {
                    LOGGER.warn("One of the tasks was timeout.");
                    continue;
                }
                try {
                    List<AbstractStockPrice> receivedFromQuoteSource = future.get();
                    addBeans(beans, receivedFromQuoteSource);
                } catch (InterruptedException e) {
                    LOGGER.warn(e);
                } catch (ExecutionException e) {
                    LOGGER.warn(e, e);
                }
            }

            if (fxSymbols != null) {
                writeFxFile(fxSymbols, fxFileName);
            }
        } finally {
            long delta = stopWatch.click();
            LOGGER.info("< getQuotes, delta=" + delta);
        }

        return beans;
    }

    /**
     * Adds the beans.
     *
     * @param beans the beans
     * @param receivedFromQuoteSource the received from quote source
     */
    protected void addBeans(List<AbstractStockPrice> beans, List<AbstractStockPrice> receivedFromQuoteSource) {
        List<AbstractStockPrice> filtered = receivedFromQuoteSource;
        if (filterFxQuotes) {
            filtered = new ArrayList<AbstractStockPrice>();
            for (AbstractStockPrice bean : receivedFromQuoteSource) {
                FxSymbol fxSymbol = bean.getFxSymbol();
                if (fxSymbol == null) {
                    filtered.add(bean);
                } else {
                    fxSymbols.add(bean);
                    if (keepFxSymbols) {
                        filtered.add(bean);
                    }
                }
            }
        }
        beans.addAll(filtered);
    }

    /**
     * Creates the URI.
     *
     * @param stocks the stocks
     * @param format the format
     * @return the uri
     * @throws URISyntaxException the URI syntax exception
     */
    protected URI createURI(List<String> stocks, String format) throws URISyntaxException {
        URI uri =  null;
        
//        uri =  URIUtils.createURI(getScheme(), getHost(), getPort(), getPath(), createQueries(stocks, format), null);
        uri = new URIBuilder()
                .setScheme(getScheme())
                .setHost(getHost())
                .setPort(getPort())
                .setPath(getPath())
                .setParameters(createParameters(stocks, format))
                .setFragment(null)
                .build();
        
        return uri;
    }

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
     * @param host the new host
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
     * Shutdown.
     */
    public void shutdown() {
        threadPool.shutdown();
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
     * @param filterFxQuotes the new filter fx quotes
     */
    public void setFilterFxQuotes(boolean filterFxQuotes) {
        this.filterFxQuotes = filterFxQuotes;
    }

    /**
     * Write fx file.
     *
     * @param fxStockPrices the fx stock prices
     * @param fileName the file name
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void writeFxFile(List<AbstractStockPrice> fxStockPrices, String fileName) throws IOException {
        if (fxStockPrices == null) {
            return;
        }

        if (fxStockPrices.size() <= 0) {
            return;
        }

        if (fileName == null) {
            return;
        }

        // String fileName = "fx.csv";
        File backupFile = new File(fileName + ".bak");
        if (backupFile.exists()) {
            if (!backupFile.delete()) {
                LOGGER.warn("Cannot delete file=" + backupFile);
            }
        }

        File file = new File("fx.csv");
        if (file.exists()) {
            if (!file.renameTo(backupFile)) {
                LOGGER.warn("Cannot rename from " + file + " to " + backupFile);
            }
        }

        LOGGER.info("Writing fx rates to " + file);

        PrintWriter writer = null;
        try {
            Date now = new Date();
            writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            writer.println("FromCurrency,ToCurrency,Rate, Date");
            writer.println();
            for (AbstractStockPrice fxStockPrice : fxStockPrices) {
                FxSymbol fxSymbol = fxStockPrice.getFxSymbol();
                if (fxSymbol == null) {
                    continue;
                }

                writeFxCsvEntry(writer, fxSymbol, now);
            }
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } finally {
                    writer = null;
                }
            }
        }
    }

    /**
     * Write fx csv entry.
     *
     * @param writer the writer
     * @param fxSymbol the fx symbol
     * @param now the now
     */
    private void writeFxCsvEntry(PrintWriter writer, FxSymbol fxSymbol, Date now) {
        writer.println(fxSymbol.getFromCurrency() + ", " + fxSymbol.getToCurrency() + ", " + fxSymbol.getRate() + ", " + now);
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
     * @param fxFileName the new fx file name
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
     * @param keepFxSymbols the new keep fx symbols
     */
    public void setKeepFxSymbols(boolean keepFxSymbols) {
        this.keepFxSymbols = keepFxSymbols;
    }
}
