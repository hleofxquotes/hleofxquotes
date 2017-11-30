package com.hungle.tools.moneyutils.yahoo;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.hungle.tools.moneyutils.stockprice.Price;
import com.hungle.tools.moneyutils.stockprice.StockPrice;

public class YahooHistoricalSS2QuoteGetter extends YahooScreenScrapper2QuoteGetter {
    private static final Logger LOGGER = Logger.getLogger(YahooHistoricalSS2QuoteGetter.class);

    @Override
    protected String getUrlString(String symbol) {
        return "https://finance.yahoo.com/quote/" + symbol + "/history?p=" + symbol;
    }

    @Override
    protected JsonNode getPriceNode(JsonNode fromJsonNode) throws OfxDataNotFound {
        // prices, array
        String fieldName = "prices";
        JsonNode pricesNode = JsonNodeUtils.getRequiredJsonNode(fromJsonNode, fieldName);
        JsonNode price = pricesNode.get(0);
        if ((price == null) || (price.isMissingNode())) {
            throw new OfxDataNotFound("Cannot find JsonNode=" + fieldName);
        }
        return price;
    }

    @Override
    protected Price getRegularMarketPricePrice(JsonNode priceNode) throws OfxDataNotFound {
        String fieldName = "close";
        JsonNode node = JsonNodeUtils.getRequiredJsonNode(priceNode, fieldName);

        Price price = new Price(node.asDouble());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("close=" + price);
        }

        return price;
    }

    @Override
    protected Price getRegularMarketDayLowPrice(JsonNode fromJsonNode) {
        String fieldName = "low";
        JsonNode node = JsonNodeUtils.getOptionalJsonNode(fromJsonNode, fieldName);

        Price price = null;
        if (node != null) {
            if (!node.isMissingNode()) {
                price = new Price(node.asDouble());
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("low=" + price);
        }

        return price;
    }

    @Override
    protected Price getRegularMarketDayHighPrice(JsonNode fromJsonNode) {
        String fieldName = "high";
        JsonNode node = JsonNodeUtils.getOptionalJsonNode(fromJsonNode, fieldName);

        Price price = null;
        if (node != null) {
            if (!node.isMissingNode()) {
                price = new Price(node.asDouble());
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("high=" + price);
        }

        return price;
    }

    @Override
    protected String getRegularMarketTime(JsonNode fromJsonNode) {
        String fieldName = "date";
        JsonNode node = JsonNodeUtils.getOptionalJsonNode(fromJsonNode, fieldName);
        if (node == null) {
            return null;
        }

        return node.asText();
    }

    @Override
    protected void setDayHighLow(Price dayHigh, Price dayLow, StockPrice stockPrice) {
        if ((dayHigh != null) && (dayLow != null)) {
            if (dayHigh.compareTo(dayLow) == 0) {
                // to preserve the semantic of how we detected MFUND
                dayHigh = null;
                dayLow = null;
            }
        }
        super.setDayHighLow(dayHigh, dayLow, stockPrice);
    }

    @Override
    protected String getCurrency(JsonNode fromJsonNode, JsonNode treeTop) {
        String currency = null;

        JsonNode priceNode = null;
        try {
            priceNode = super.getPriceNode(treeTop);
            currency = super.getCurrency(priceNode, treeTop);
        } catch (OfxDataNotFound e) {
            LOGGER.warn(e);
        }
        return currency;
    }

}
