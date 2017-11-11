package com.hungle.tools.moneyutils.yahoo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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

    private ThreadLocal<String> stockSymbol = new ThreadLocal<String>();

    public YahooScreenScrapper2QuoteGetter() {
        super();
        setBucketSize(1);
    }

    @Override
    public HttpResponse httpGet(List<String> stocks, String format)
            throws URISyntaxException, IOException, ClientProtocolException {

        this.stockSymbol.set(stocks.get(0));
        Charset enc = Charset.forName("UTF-8");
        if (enc == null) {
            enc = Charset.defaultCharset();
        }
        String stockSymbolStr = stockSymbol.get();
        stockSymbolStr = URLEncoder.encode(stockSymbolStr, enc.toString());

        String urlString = "https://finance.yahoo.com/quote/" + stockSymbolStr + "?p=" + stockSymbolStr;
        
        URI uri = new URL(urlString).toURI();

        HttpGet httpGet = new HttpGet(uri);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("uri=" + uri);
        }
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpResponse response = httpClient.execute(httpGet);
        return response;
    }

    @Override
    public List<AbstractStockPrice> httpEntityToStockPriceBean(HttpEntity entity, boolean skipIfNoPrice)
            throws IOException {
        InputStream stream = entity.getContent();

        List<AbstractStockPrice> prices = null;

        try {
            prices = parseInputStream(stream);
        } catch (OfxDataNotFound e) {
            LOGGER.warn("Cannot parse stream data, symbol=" + this.stockSymbol.get() + ", msg=" + e.getMessage());
        }

        return prices;
    }

    private List<AbstractStockPrice> parseInputStream(InputStream stream) throws IOException, OfxDataNotFound {
        String data = getData(stream);

        JsonNode treeTop = getTreeTop(data);

        JsonNode priceNode = getPriceNode(treeTop);

        // String stockSymbol = getSymbol(priceNode);

        String shortName = getShortName(priceNode);
        if (shortName == null) {
            shortName = stockSymbol.get();
        }
        
        String marketState = getMarketState(priceNode);
        // PREPRE, REGULAR, POST, CLOSED
        LOGGER.info("  marketState=" + marketState);

        Price regularMarketPricePrice = getRegularMarketPricePrice(priceNode);
        if (regularMarketPricePrice != null) {
            regularMarketPricePrice.setMarketState(marketState);
        }

        Price regularMarketDayLowPrice = getRegularMarketDayLowPrice(priceNode);
        if (regularMarketDayLowPrice != null) {
            regularMarketDayLowPrice.setMarketState(marketState);
        }
        
        Price regularMarketDayHighPrice = getRegularMarketDayHighPrice(priceNode);
        if (regularMarketDayHighPrice != null) {
            regularMarketDayHighPrice.setMarketState(marketState);
        }
        
        String currency = getCurrency(priceNode);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("    currency=" + currency);
        }

        if (currency != null) {
            if (regularMarketPricePrice != null) {
                regularMarketPricePrice.setCurrency(currency);
            }
            if (regularMarketDayLowPrice != null) {
                regularMarketDayLowPrice.setCurrency(currency);
            }
            if (regularMarketDayLowPrice != null) {
                regularMarketDayLowPrice.setCurrency(currency);
            }
        }

        Date lastTrade = null;

        String regularMarketTime = getRegularMarketTime(priceNode);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("    regularMarketTime=" + regularMarketTime);
        }

        JsonNode quoteTypeNode = getQuoteTypeNode(treeTop);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("    quoteTypeNode=" + quoteTypeNode);
        }
        String exchangeTimezoneName = null;
        if (quoteTypeNode != null) {
            exchangeTimezoneName = getExchangeTimezoneName(quoteTypeNode);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("    exchangeTimezoneName=" + exchangeTimezoneName);
        }

        if ((regularMarketTime != null) && (exchangeTimezoneName != null)) {
            long longValue;
            try {
                longValue = Long.valueOf(regularMarketTime);
                ZonedDateTime marketZonedDateTime = getMarketZonedDateTime(longValue, exchangeTimezoneName);
                java.util.Date utilDate = java.util.Date.from(marketZonedDateTime.toInstant());

                lastTrade = utilDate;
            } catch (NumberFormatException e) {
                LOGGER.warn(e);
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("    lastTrade=" + lastTrade);
        }

        StockPrice stockPrice = new StockPrice();

        stockPrice.setStockSymbol(stockSymbol.get());

        stockPrice.setStockName(shortName);

        stockPrice.setLastPrice(regularMarketPricePrice);

        if (currency != null) {
            stockPrice.setCurrency(currency);
        }

        if (regularMarketDayHighPrice != null) {
            stockPrice.setDayHigh(regularMarketDayHighPrice);
        }

        if (regularMarketDayLowPrice != null) {
            stockPrice.setDayLow(regularMarketDayLowPrice);
        }

        stockPrice.setLastTrade(lastTrade);
        if (lastTrade != null) {
            // stockPrice.setLastTradeDate(lastTradeDateFormatter.format(lastTrade));
            // stockPrice.setLastTradeTime(lastTradeTimeFormatter.format(lastTrade));
        }

        stockPrice.postSetProperties();

        LOGGER.info(stockSymbol.get() + ", " + stockPrice.getSecType());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("    isMutualFund=" + StockPrice.isMutualFund(stockPrice));
            LOGGER.debug("    isMf=" + stockPrice.isMf());
            LOGGER.debug("    isBond=" + stockPrice.isBond());
        }

        List<AbstractStockPrice> stockPrices = new ArrayList<AbstractStockPrice>();
        stockPrices.add(stockPrice);

        return stockPrices;
    }

    private String getMarketState(JsonNode fromJsonNode) {
        String fieldName = "marketState";
        JsonNode node = getOptionalJsonNode(fromJsonNode, fieldName);

        if (node != null) {
            return node.asText();
        }

        return null;
    }

    private String getExchangeTimezoneName(JsonNode fromJsonNode) {
        String fieldName = "exchangeTimezoneName";
        JsonNode jsonNode = getOptionalJsonNode(fromJsonNode, fieldName);
        if (jsonNode == null) {
            return null;
        }

        return jsonNode.asText();
    }

    private JsonNode getTreeTop(String data) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode treeTop = mapper.readTree(data);
        if (treeTop == null) {
            throw new OfxDataNotFound("Cannot find stockPrice treeTop");
        }
        return treeTop;
    }

    private JsonNode getPriceNode(JsonNode fromJsonNode) throws OfxDataNotFound {
        String fieldName = "price";
        return getRequiredJsonNode(fromJsonNode, fieldName);
    }

    private JsonNode getQuoteTypeNode(JsonNode fromJsonNode) {
        String fieldName = "QuoteSummaryStore";
        JsonNode jsonNode = getOptionalJsonNode(fromJsonNode, fieldName);
        if (jsonNode != null) {
            jsonNode = jsonNode.path("quoteType");
            if (!jsonNode.isMissingNode()) {
                return jsonNode;
            }
        }

        return jsonNode;
    }

    private String getSymbol(JsonNode fromJsonNode) throws OfxDataNotFound {
        String fieldName = "symbol";
        JsonNode jsonNode = getRequiredJsonNode(fromJsonNode, fieldName);
        return jsonNode.asText();
    }

    private String getShortName(JsonNode fromJsonNode) {
        String fieldName = "shortName";
        JsonNode jsonNode = getOptionalJsonNode(fromJsonNode, fieldName);
        if (jsonNode == null) {
            return null;
        }

        return jsonNode.asText();
    }

    private Price getRegularMarketPricePrice(JsonNode priceNode) throws OfxDataNotFound {
        String fieldName = "regularMarketPrice";
        JsonNode node = getRequiredJsonNode(priceNode, fieldName);

        Price price = null;

        node = node.path("raw");
        if (node.isMissingNode()) {
            throw new OfxDataNotFound("Cannot find JsonNode " + fieldName + ".raw");
        }
        price = new Price(node.asDouble());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("regularMarketPricePrice=" + price);
        }

        return price;
    }

    private Price getRegularMarketDayLowPrice(JsonNode fromJsonNode) {
        String fieldName = "regularMarketDayLow";
        JsonNode node = getOptionalJsonNode(fromJsonNode, fieldName);

        Price price = null;
        if (node != null) {
            node = node.path("raw");
            if (!node.isMissingNode()) {
                price = new Price(node.asDouble());
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("regularMarketDayLowPrice=" + price);
        }

        return price;
    }

    private String getCurrency(JsonNode fromJsonNode) {
        String fieldName = "currency";
        JsonNode jsonNode = getOptionalJsonNode(fromJsonNode, fieldName);
        if (jsonNode == null) {
            return null;
        }

        return jsonNode.asText();
    }

    private Price getRegularMarketDayHighPrice(JsonNode fromJsonNode) {
        String fieldName = "regularMarketDayHigh";
        JsonNode node = getOptionalJsonNode(fromJsonNode, fieldName);

        Price price = null;
        if (node != null) {
            node = node.path("raw");
            if (!node.isMissingNode()) {
                price = new Price(node.asDouble());
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("regularMarketDayHighPrice=" + price);
        }

        return price;
    }

    private String getRegularMarketTime(JsonNode fromJsonNode) {
        String fieldName = "regularMarketTime";
        JsonNode node = getOptionalJsonNode(fromJsonNode, fieldName);
        if (node == null) {
            return null;
        }

        return node.asText();
    }

    private static final JsonNode getRequiredJsonNode(JsonNode fromJsonNode, String fieldName) throws OfxDataNotFound {
        JsonNode jsonNode = fromJsonNode.findPath(fieldName);
        if (jsonNode.isMissingNode()) {
            throw new OfxDataNotFound("Cannot find JsonNode=" + fieldName);
        }
        return jsonNode;
    }

    private static final JsonNode getOptionalJsonNode(JsonNode fromJsonNode, String fieldName) {
        JsonNode jsonNode = fromJsonNode.findPath(fieldName);
        if (jsonNode.isMissingNode()) {
            return null;
        }
        return jsonNode;
    }

    private String getData(InputStream stream) throws IOException {
        String data = null;
        String prefix = "root.App.main =";
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
            throw new OfxDataNotFound("Cannot find stockPrice data");
        }

        data = data.substring(prefix.length());
        if (data == null) {
            throw new OfxDataNotFound("Cannot find stockPrice data");
        }
        return data;
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

    public static final ZonedDateTime getMarketZonedDateTime(long regularMarketTime, String exchangeTimezoneName) {
        long epoch = regularMarketTime;
        Instant instant = Instant.ofEpochSecond(epoch);
        TimeZone timeZone = TimeZone.getTimeZone(exchangeTimezoneName);
        ZoneId zoneId = timeZone.toZoneId();
        ZonedDateTime zoneDateTime = ZonedDateTime.ofInstant(instant, zoneId);
        return zoneDateTime;
    }

}