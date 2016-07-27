package com.le.tools.moneyutils.yahoo;

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
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import com.le.tools.moneyutils.ofx.quotes.GetQuotesListener;
import com.le.tools.moneyutils.ofx.quotes.GetQuotesTask;
import com.le.tools.moneyutils.ofx.quotes.HttpUtils;
import com.le.tools.moneyutils.ofx.quotes.OfxUtils;
import com.le.tools.moneyutils.ofx.quotes.StopWatch;
import com.le.tools.moneyutils.ofx.quotes.Utils;
import com.le.tools.moneyutils.ofx.quotes.net.HttpQuoteGetter;
import com.le.tools.moneyutils.stockprice.AbstractStockPrice;
import com.le.tools.moneyutils.stockprice.FxSymbol;

public class GetYahooQuotes implements HttpQuoteGetter {
    private static final Logger log = Logger.getLogger(GetYahooQuotes.class);
    private static final long DEFAULT_TIMEOUT = 120L;
    private static final int DEFAULT_BUCKET_SIZE = 25;
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(3);

    private static final String DEFAULT_SCHEME = "http";
    public static final String DEFAULT_HOST = "download.finance.yahoo.com";
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

    private static final int DEFAULT_PORT = -1;
    private static final String DEFAULT_PATH = "/d/quotes.csv";

    private String scheme = DEFAULT_SCHEME;
    private String host = DEFAULT_HOST;
    private int port = DEFAULT_PORT;
    private String path = DEFAULT_PATH;

    private int bucketSize = DEFAULT_BUCKET_SIZE;
    private TimeUnit timeOutUnit = TimeUnit.SECONDS;
    private long timeout = DEFAULT_TIMEOUT;

    private boolean filterFxQuotes = true;
    private List<AbstractStockPrice> fxSymbols;
    private String fxFileName = "fx.csv";
    private boolean keepFxSymbols = true;

    @Override
    public HttpResponse httpGet(List<String> stocks, String format) throws URISyntaxException, IOException, ClientProtocolException {
        URI uri = createURI(stocks, format);
        HttpGet httpGet = new HttpGet(uri);
        if (log.isDebugEnabled()) {
            log.debug("uri=" + uri);
        }
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse response = httpClient.execute(httpGet);
        return response;
    }

    @Override
    public List<AbstractStockPrice> httpEntityToStockPriceBean(HttpEntity entity, boolean skipIfNoPrice) throws IOException {
        return HttpUtils.toStockPriceBean(entity, skipIfNoPrice);
    }

    public List<AbstractStockPrice> getQuotes(List<String> stocks) throws ClientProtocolException, URISyntaxException, IOException {
        GetQuotesListener listener = null;
        List<AbstractStockPrice> quotes = getQuotes(stocks, listener, true);
        return quotes;
    }

    public List<AbstractStockPrice> getQuotes(List<String> stocks, GetQuotesListener listener) throws ClientProtocolException, URISyntaxException, IOException {
        List<AbstractStockPrice> quotes = getQuotes(stocks, listener, true);
        return quotes;
    }

    public List<AbstractStockPrice> getQuotes(List<String> stocks, GetQuotesListener listener, boolean skipNoPrice) throws URISyntaxException,
            ClientProtocolException, IOException {
        log.info("> getQuotes");

        fxSymbols = new ArrayList<AbstractStockPrice>();

        StopWatch stopWatch = new StopWatch();
        List<AbstractStockPrice> beans = new ArrayList<AbstractStockPrice>();
        try {
            if (stocks == null) {
                return beans;
            }

            log.info("stocks.size=" + stocks.size());

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
                log.error(e);
            }

            if (futures == null) {
                log.warn("Failed to invokeAll");
                return beans;
            }

            for (Future<List<AbstractStockPrice>> future : futures) {
                if (future.isCancelled()) {
                    log.warn("One of the tasks was timeout.");
                    continue;
                }
                try {
                    List<AbstractStockPrice> receivedFromQuoteSource = future.get();
                    addBeans(beans, receivedFromQuoteSource);
                } catch (InterruptedException e) {
                    log.warn(e);
                } catch (ExecutionException e) {
                    log.warn(e, e);
                }
            }

            if (fxSymbols != null) {
                writeFxFile(fxSymbols, fxFileName);
            }
        } finally {
            long delta = stopWatch.click();
            log.info("< getQuotes, delta=" + delta);
        }

        return beans;
    }

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

    protected URI createURI(List<String> stocks, String format) throws URISyntaxException {
        return URIUtils.createURI(getScheme(), getHost(), getPort(), getPath(), createQueries(stocks, format), null);
    }

    private static String createQueries(List<String> stocks, String format) {
        // http://download.finance.yahoo.com/d/quotes.csv?s=IBM&f=sl1d1t1c1ohgv&e=.csv
        // http://download.finance.yahoo.com/d/quotes.csv?s=EDL.L&f=sl1d1t1c1ohgv&e=.csv
        // http://uk.old.finance.yahoo.com/d/quotes.csv?s=GB0032346800.L&f=sl1d1t1c1ohgv&e=.csv
        // ^GSPC
        // http://download.finance.yahoo.com/d/quotes.csv?s=%5EGSPC&f=sl1d1t1c1ohgv&e=.csv
        List<NameValuePair> qParams = new ArrayList<NameValuePair>();
        qParams.add(new BasicNameValuePair("s", OfxUtils.toSeparatedString(stocks)));
        qParams.add(new BasicNameValuePair("f", format));
        qParams.add(new BasicNameValuePair("e", ".csv"));
        return URLEncodedUtils.format(qParams, "UTF-8");
    }

    public String getScheme() {
        return scheme;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public String getPath() {
        return path;
    }

    public void shutdown() {
        threadPool.shutdown();
    }

    public boolean isFilterFxQuotes() {
        return filterFxQuotes;
    }

    public void setFilterFxQuotes(boolean filterFxQuotes) {
        this.filterFxQuotes = filterFxQuotes;
    }

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
                log.warn("Cannot delete file=" + backupFile);
            }
        }

        File file = new File("fx.csv");
        if (file.exists()) {
            if (!file.renameTo(backupFile)) {
                log.warn("Cannot rename from " + file + " to " + backupFile);
            }
        }

        log.info("Writing fx rates to " + file);

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

    protected void writeFxCsvEntry(PrintWriter writer, FxSymbol fxSymbol, Date now) {
        writer.println(fxSymbol.getFromCurrency() + ", " + fxSymbol.getToCurrency() + ", " + fxSymbol.getRate() + ", " + now);
    }

    public List<AbstractStockPrice> getFxSymbols() {
        return fxSymbols;
    }

    public String getFxFileName() {
        return fxFileName;
    }

    public void setFxFileName(String fxFileName) {
        this.fxFileName = fxFileName;
    }

    public boolean isKeepFxSymbols() {
        return keepFxSymbols;
    }

    public void setKeepFxSymbols(boolean keepFxSymbols) {
        this.keepFxSymbols = keepFxSymbols;
    }
}
