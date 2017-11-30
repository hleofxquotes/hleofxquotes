package com.hungle.msmoney.qs.ft;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.hungle.msmoney.core.stockprice.AbstractStockPrice;
import com.hungle.msmoney.core.stockprice.Price;
import com.hungle.msmoney.core.stockprice.StockPrice;
import com.hungle.msmoney.qs.net.AbstractHttpQuoteGetter;

public class FtEquitiesQuoteGetter extends AbstractHttpQuoteGetter {
    private static final Logger LOGGER = Logger.getLogger(FtEquitiesQuoteGetter.class);

    private ThreadLocal<String> stockSymbol = new ThreadLocal<String>();

    public FtEquitiesQuoteGetter() {
        super();
        setBucketSize(1);
    }

    @Override
    public HttpResponse httpGet(List<String> stocks, String format)
            throws URISyntaxException, IOException, ClientProtocolException {

        setStockSymbol(stocks.get(0));

        String stockSymbolStr = stockSymbol.get();
        boolean encodeSymbol = false;
        if (encodeSymbol) {
            Charset enc = Charset.forName("UTF-8");
            if (enc == null) {
                enc = Charset.defaultCharset();
            }
            stockSymbolStr = URLEncoder.encode(stockSymbolStr, enc.toString());
        }
        String urlString = getUrlString(stockSymbolStr);

        URI uri = new URL(urlString).toURI();

        HttpGet httpGet = new HttpGet(uri);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("uri=" + uri);
        }
        HttpClient httpClient = createHttpClient();
        HttpResponse response = httpClient.execute(httpGet);
        return response;
    }

    protected void setStockSymbol(String symbol) {
        this.stockSymbol.set(symbol);
    }

    protected String getStockSymbol() {
        return this.stockSymbol.get();
    }

    protected String getUrlString(String symbol) throws IOException {
        String urlString = null;
        try {
            URL url = FtPriceModel.getFtEquityURL(symbol);
            urlString = url.toString();
        } catch (MalformedURLException e) {
            throw new IOException(e);
        } catch (UnsupportedEncodingException e) {
            throw new IOException(e);
        }
        return urlString;
    }

    @Override
    public List<AbstractStockPrice> httpEntityToStockPriceBean(HttpEntity entity, boolean skipIfNoPrice)
            throws IOException {
        InputStream stream = entity.getContent();

        List<AbstractStockPrice> stockPrices = new ArrayList<>();

        Document doc = Jsoup.parse(stream, "UTF-8", "http://localhost");

        FtPriceModel model = FtPriceModel.parseFtDoc(doc, getStockSymbol());

        if (model != null) {
            StockPrice stockPrice = new StockPrice();
            stockPrice.setStockSymbol(model.getSymbol());
            Double modelPrice = model.getPrice();
            Price lastPrice = null;
            if (modelPrice != null) {
                lastPrice = new Price(modelPrice);
                lastPrice.setCurrency(model.getCurrency());
            }
            stockPrice.setLastPrice(lastPrice);
            stockPrice.setDayHigh(lastPrice);
            stockPrice.setDayHigh(lastPrice);
            stockPrice.setLastTrade(model.getDate());
            stockPrice.postSetProperties();

            stockPrices.add(stockPrice);
        }
        return stockPrices;
    }

}