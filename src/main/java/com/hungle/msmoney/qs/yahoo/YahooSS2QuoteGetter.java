package com.hungle.msmoney.qs.yahoo;

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
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hungle.msmoney.core.misc.StopWatch;
import com.hungle.msmoney.core.stockprice.AbstractStockPrice;
import com.hungle.msmoney.core.stockprice.Price;
import com.hungle.msmoney.core.stockprice.StockPrice;
import com.hungle.msmoney.qs.net.AbstractHttpQuoteGetter;
import com.hungle.msmoney.qs.net.GetQuotesListener;

public class YahooSS2QuoteGetter extends AbstractHttpQuoteGetter {
    private static final Logger LOGGER = Logger.getLogger(YahooSS2QuoteGetter.class);

    private ThreadLocal<String> stockSymbol = new ThreadLocal<String>();

    public YahooSS2QuoteGetter() {
        super();
        setBucketSize(1);
    }

    @Override
    public HttpResponse httpGet(List<String> stocks, String format)
            throws URISyntaxException, IOException, ClientProtocolException {

        setStockSymbol(stocks.get(0));

        Charset enc = Charset.forName("UTF-8");
        if (enc == null) {
            enc = Charset.defaultCharset();
        }
        String stockSymbolStr = stockSymbol.get();
        stockSymbolStr = URLEncoder.encode(stockSymbolStr, enc.toString());

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

    public void setStockSymbol(String symbol) {
        this.stockSymbol.set(symbol);
    }

    protected String getUrlString(String symbol) {
        return "https://finance.yahoo.com/quote/" + symbol + "?p=" + symbol;
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

    public List<AbstractStockPrice> parseInputStream(InputStream stream) throws IOException, OfxDataNotFound {
        String jsonString = getJsonString(stream);

        return parseJsonString(jsonString);
    }

    protected List<AbstractStockPrice> parseJsonString(String jsonString) throws IOException, OfxDataNotFound {
        JsonNode treeTop = getTreeTop(jsonString);

        JsonNode priceNode = getPriceNode(treeTop);

        // String stockSymbol = getSymbol(priceNode);

        String shortName = getShortName(priceNode);
        if (shortName == null) {
            shortName = stockSymbol.get();
        }

        String marketState = getMarketState(priceNode);
        // PREPRE, REGULAR, POST, CLOSED
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("  marketState=" + marketState);
        }

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

        String currency = getCurrency(priceNode, treeTop);
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

        setDayHighLow(regularMarketDayHighPrice, regularMarketDayLowPrice, stockPrice);

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

    protected void setDayHighLow(Price regularMarketDayHighPrice, Price regularMarketDayLowPrice,
            StockPrice stockPrice) {
        if (regularMarketDayHighPrice != null) {
            stockPrice.setDayHigh(regularMarketDayHighPrice);
        }

        if (regularMarketDayLowPrice != null) {
            stockPrice.setDayLow(regularMarketDayLowPrice);
        }
    }

    protected String getMarketState(JsonNode fromJsonNode) {
        return JsonNodeUtils.getMarketState(fromJsonNode);
    }

    private String getExchangeTimezoneName(JsonNode fromJsonNode) {
        return JsonNodeUtils.getExchangeTimezoneName(fromJsonNode);
    }

    private JsonNode getTreeTop(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(jsonString);
        if (node == null) {
            throw new OfxDataNotFound("Cannot find stockPrice treeTop");
        }
        return node;
    }

    protected JsonNode getPriceNode(JsonNode fromJsonNode) throws OfxDataNotFound {
        String fieldName = "price";
        return JsonNodeUtils.getRequiredJsonNode(fromJsonNode, fieldName);
    }

    private JsonNode getQuoteTypeNode(JsonNode fromJsonNode) {
        String fieldName = "QuoteSummaryStore";
        JsonNode jsonNode = JsonNodeUtils.getOptionalJsonNode(fromJsonNode, fieldName);
        if (jsonNode != null) {
            jsonNode = jsonNode.path("quoteType");
            if (!jsonNode.isMissingNode()) {
                return jsonNode;
            }
        }

        return jsonNode;
    }

    protected String getSymbol(JsonNode fromJsonNode) throws OfxDataNotFound {
        return JsonNodeUtils.getSymbol(fromJsonNode);
    }

    protected String getShortName(JsonNode fromJsonNode) {
        return JsonNodeUtils.getShortName(fromJsonNode);
    }

    protected Price getRegularMarketPricePrice(JsonNode priceNode) throws OfxDataNotFound {
        return JsonNodeUtils.getRegularMarketPricePrice(priceNode);
    }

    protected Price getRegularMarketDayLowPrice(JsonNode fromJsonNode) {
        return JsonNodeUtils.getRegularMarketDayLowPrice(fromJsonNode);
    }

    protected String getCurrency(JsonNode fromJsonNode, JsonNode treeTop) {
        return JsonNodeUtils.getCurrency(fromJsonNode);
    }

    protected Price getRegularMarketDayHighPrice(JsonNode fromJsonNode) {
        return JsonNodeUtils.getRegularMarketDayHighPrice(fromJsonNode);
    }

    protected String getRegularMarketTime(JsonNode fromJsonNode) {
        return JsonNodeUtils.getRegularMarketTime(fromJsonNode);
    }

    protected String getJsonString(InputStream stream) throws IOException {
        String prefix = "root.App.main =";
        return getJsonString(stream, prefix);
    }

    private String getJsonString(InputStream stream, String prefix) throws IOException, OfxDataNotFound {
        String jsonString = null;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(prefix)) {
                    jsonString = line;
                    break;
                }
            }
        } finally {
            if (reader != null) {
                reader.close();
                reader = null;
            }
        }

        if (jsonString == null) {
            throw new OfxDataNotFound("Cannot find stockPrice data");
        }

        jsonString = jsonString.substring(prefix.length());
        if (jsonString == null) {
            throw new OfxDataNotFound("Cannot find stockPrice data");
        }
        return jsonString;
    }

    private List<AbstractStockPrice> getStockQuotesSingleThread(List<String> stockSymbols, GetQuotesListener listener)
            throws IOException {
        LOGGER.info("> getStockQuotes");

        List<AbstractStockPrice> stockPrices = new ArrayList<AbstractStockPrice>();

        try {
            if (listener != null) {
                listener.setSubTaskSize(stockSymbols.size());
            }
            YahooSS2 scrapper = null;
            StopWatch stopWatch = new StopWatch();
            try {
                if (listener != null) {
                    listener.started(stockSymbols);
                }

                scrapper = new YahooSS2();
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