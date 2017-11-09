package com.hungle.tools.moneyutils.yahoo;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hungle.tools.moneyutils.ofx.quotes.StopWatch;
import com.hungle.tools.moneyutils.stockprice.AbstractStockPrice;
import com.hungle.tools.moneyutils.stockprice.Price;
import com.hungle.tools.moneyutils.stockprice.StockPrice;

public class YahooScreenScrapper2 implements Closeable {
    private static final Logger LOGGER = Logger.getLogger(YahooScreenScrapper2.class);

    private CloseableHttpClient client;

    private SimpleDateFormat priceInfoLastTradeDateFormatter = new SimpleDateFormat("MM/dd/yyyy");

    public YahooScreenScrapper2() {
        this.client = HttpClientBuilder.create().build();
    }

    public AbstractStockPrice getStockPrice(String stockSymbol) throws IOException {
        StockPrice stockPrice = null;

        LOGGER.info("> START, " + stockSymbol);

        StopWatch stopWatch = new StopWatch();
        try {
            URI uri = new URL("https://finance.yahoo.com/quote/" + stockSymbol + "?p=" + stockSymbol).toURI();
            HttpGet httpGet = new HttpGet(uri);
            HttpResponse httpResponse = client.execute(httpGet);
            HttpEntity entity = httpResponse.getEntity();
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
            LOGGER.info("regularMarketPricePrice=" + regularMarketPricePrice);
            
            fieldName = "regularMarketDayLow";
            Price regularMarketDayLowPrice = null;
            node = price.findPath(fieldName);
            if (!node.isMissingNode()) {
                node = node.path("raw");
                if (! node.isMissingNode()) {
                    regularMarketDayLowPrice = new Price(node.asDouble());
                }
            }
            LOGGER.info("regularMarketDayLowPrice=" + regularMarketDayLowPrice);

            fieldName = "regularMarketDayHigh";
            Price regularMarketDayHighPrice = null;
            node = price.findPath(fieldName);
            if (!node.isMissingNode()) {
                node = node.path("raw");
                if (!node.isMissingNode()) {
                    regularMarketDayHighPrice = new Price(node.asDouble());
                }
            }
            LOGGER.info("regularMarketDayHighPrice=" + regularMarketDayHighPrice);

            String currency = price.get("currency").asText();

            String shortName = price.get("shortName").asText();
            LOGGER.info("stockName=" + shortName);

            Date lastTrade = null;

            stockPrice = new StockPrice();

            stockPrice.setCurrency(currency);

            if (regularMarketDayHighPrice != null) {
                stockPrice.setDayHigh(regularMarketDayHighPrice);
            }
            
            if (regularMarketDayLowPrice != null) {
                stockPrice.setDayLow(regularMarketDayLowPrice);
            }

            stockPrice.setLastPrice(regularMarketPricePrice);

            stockPrice.setLastTrade(lastTrade);
            stockPrice.setLastTradeDate(priceInfoLastTradeDateFormatter.format(lastTrade));


            stockPrice.setStockName(shortName);
            stockPrice.setStockSymbol(stockSymbol);
            
            stockPrice.postSetProperties();
            
            LOGGER.info("isMutualFund=" + StockPrice.isMutualFund(stockPrice));
            LOGGER.info("getSecType=" + stockPrice.getSecType());
            LOGGER.info("isMf=" + stockPrice.isMf());
            LOGGER.info("isBond=" + stockPrice.isBond());
            
        } catch (UnsupportedOperationException e) {
            throw new IOException(e);
        } catch (URISyntaxException e) {
            throw new IOException(e);
        } finally {
            long delta = stopWatch.click();
            LOGGER.info("< DONE, " + stockSymbol + ", delta=" + delta);
        }

        return stockPrice;
    }

    @Override
    public void close() throws IOException {
        if (client != null) {
            client.close();
            client = null;
        }
    }

}
