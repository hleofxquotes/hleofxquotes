package com.hungle.tools.moneyutils.yahoo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hungle.tools.moneyutils.ofx.quotes.StopWatch;
import com.hungle.tools.moneyutils.ofx.quotes.net.AbstractHttpQuoteGetter;
import com.hungle.tools.moneyutils.ofx.quotes.net.GetQuotesListener;
import com.hungle.tools.moneyutils.stockprice.AbstractStockPrice;
import com.hungle.tools.moneyutils.stockprice.Price;
import com.hungle.tools.moneyutils.stockprice.StockPrice;

final class YahooScreenScrapper2QuoteGetter extends AbstractHttpQuoteGetter {
    private static final Logger LOGGER = Logger.getLogger(YahooScreenScrapper2QuoteGetter.class);

    public YahooScreenScrapper2QuoteGetter() {
        super();
        setBucketSize(1);
    }

    @Override
    public HttpResponse httpGet(List<String> stocks, String format)
            throws URISyntaxException, IOException, ClientProtocolException {

        String stockSymbol = stocks.get(0);
        URI uri = new URL("https://finance.yahoo.com/quote/" + stockSymbol + "?p=" + stockSymbol).toURI();

        HttpGet httpGet = new HttpGet(uri);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("uri=" + uri);
        }
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpResponse response = httpClient.execute(httpGet);
        return response;
    }

    @Override
    public List<AbstractStockPrice> httpEntityToStockPriceBean(HttpEntity entity, boolean skipIfNoPrice) throws IOException {
        InputStream stream = entity.getContent();

        String prefix = "root.App.main =";
        String data = null;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(prefix)) {
                    data = line;
                    break;
                }
            }
        } finally {
            if (reader != null) {
                reader.close();
                reader = null;
            }
        }

        if (data == null) {
            throw new IOException("Cannot find stockPrice data");
        }

        data = data.substring(prefix.length());
        if (data == null) {
            throw new IOException("Cannot find stockPrice data");
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode treeTop = mapper.readTree(data);
        if (treeTop == null) {
            throw new IOException("Cannot find stockPrice tree");
        }

        String fieldName = null;

        fieldName = "price";
        JsonNode price = treeTop.findPath(fieldName);
        if (price.isMissingNode()) {
            throw new IOException("Cannot find JsonNode " + fieldName);
        }

        JsonNode node = null;

        fieldName = "regularMarketPrice";
        Price regularMarketPricePrice = null;
        node = price.findPath(fieldName);
        if (node.isMissingNode()) {
            throw new IOException("Cannot find JsonNode " + fieldName);
        } else {
            node = node.path("raw");
            if (node.isMissingNode()) {
                throw new IOException("Cannot find JsonNode " + fieldName + ".raw");
            }
            regularMarketPricePrice = new Price(node.asDouble());
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("regularMarketPricePrice=" + regularMarketPricePrice);
        }

        fieldName = "regularMarketDayLow";
        Price regularMarketDayLowPrice = null;
        node = price.findPath(fieldName);
        if (!node.isMissingNode()) {
            node = node.path("raw");
            if (!node.isMissingNode()) {
                regularMarketDayLowPrice = new Price(node.asDouble());
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("regularMarketDayLowPrice=" + regularMarketDayLowPrice);
        }

        fieldName = "regularMarketDayHigh";
        Price regularMarketDayHighPrice = null;
        node = price.findPath(fieldName);
        if (!node.isMissingNode()) {
            node = node.path("raw");
            if (!node.isMissingNode()) {
                regularMarketDayHighPrice = new Price(node.asDouble());
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("regularMarketDayHighPrice=" + regularMarketDayHighPrice);
        }

        String currency = price.get("currency").asText();

        String shortName = price.get("shortName").asText();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("stockName=" + shortName);
        }

        Date lastTrade = null;

        String stockSymbol = price.get("symbol").asText();

        StockPrice stockPrice = new StockPrice();

        stockPrice.setCurrency(currency);

        if (regularMarketDayHighPrice != null) {
            stockPrice.setDayHigh(regularMarketDayHighPrice);
        }

        if (regularMarketDayLowPrice != null) {
            stockPrice.setDayLow(regularMarketDayLowPrice);
        }

        stockPrice.setLastPrice(regularMarketPricePrice);

        stockPrice.setLastTrade(lastTrade);

        stockPrice.setStockName(shortName);
        stockPrice.setStockSymbol(stockSymbol);

        stockPrice.postSetProperties();

        LOGGER.info(stockSymbol + ", " + stockPrice.getSecType());
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("isMutualFund=" + StockPrice.isMutualFund(stockPrice));
            LOGGER.debug("isMf=" + stockPrice.isMf());
            LOGGER.debug("isBond=" + stockPrice.isBond());
        }

        List<AbstractStockPrice> stockPrices = new ArrayList<AbstractStockPrice>();
        stockPrices.add(stockPrice);

        return stockPrices;
    }

    private List<AbstractStockPrice> getStockQuotesSingleThread(List<String> stockSymbols, GetQuotesListener listener)
            throws IOException {
        LOGGER.info("> getStockQuotes");

        List<AbstractStockPrice> stockPrices = new ArrayList<AbstractStockPrice>();

        try {
            if (listener != null) {
                listener.setSubTaskSize(stockSymbols.size());
            }
            YahooScreenScrapper2 scrapper = null;
            StopWatch stopWatch = new StopWatch();
            try {
                if (listener != null) {
                    listener.started(stockSymbols);
                }

                scrapper = new YahooScreenScrapper2();
                for (String stockSymbol : stockSymbols) {
                    try {
                        AbstractStockPrice stockPrice = scrapper.getStockPrice(stockSymbol);
                        if (stockPrice == null) {
                            LOGGER.warn("Cannot get stock price for symbol=" + stockSymbol);
                        } else {
                            stockPrices.add(stockPrice);
                        }
                    } catch (Exception e) {
                        LOGGER.warn(e);
                    }
                }
            } finally {
                long delta = stopWatch.click();
                if (listener != null) {
                    listener.ended(stockSymbols, stockPrices, delta);
                }
                if (scrapper != null) {
                    scrapper.close();
                    scrapper = null;
                }
            }
        } finally {

        }

        return stockPrices;
    }

}