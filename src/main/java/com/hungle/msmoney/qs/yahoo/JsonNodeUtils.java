package com.hungle.msmoney.qs.yahoo;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.hungle.msmoney.core.stockprice.Price;

public class JsonNodeUtils {
    private static final Logger LOGGER = Logger.getLogger(JsonNodeUtils.class);

    public static final String getShortName(JsonNode fromJsonNode) {
        String fieldName = "shortName";
        JsonNode jsonNode = JsonNodeUtils.getOptionalJsonNode(fromJsonNode, fieldName);
        if (jsonNode == null) {
            return null;
        }

        return jsonNode.asText();
    }

    public static final Price getRegularMarketPricePrice(JsonNode priceNode) throws OfxDataNotFound {
        String fieldName = "regularMarketPrice";
        JsonNode node = JsonNodeUtils.getRequiredJsonNode(priceNode, fieldName);

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

    public static final Price getRegularMarketDayLowPrice(JsonNode fromJsonNode) {
        String fieldName = "regularMarketDayLow";
        JsonNode node = JsonNodeUtils.getOptionalJsonNode(fromJsonNode, fieldName);

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

    public static final String getCurrency(JsonNode fromJsonNode) {
        String fieldName = "currency";
        JsonNode jsonNode = JsonNodeUtils.getOptionalJsonNode(fromJsonNode, fieldName);
        if (jsonNode == null) {
            return null;
        }

        return jsonNode.asText();
    }

    public static final Price getRegularMarketDayHighPrice(JsonNode fromJsonNode) {
        String fieldName = "regularMarketDayHigh";
        JsonNode node = JsonNodeUtils.getOptionalJsonNode(fromJsonNode, fieldName);

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

    public static final String getRegularMarketTime(JsonNode fromJsonNode) {
        String fieldName = "regularMarketTime";
        JsonNode node = JsonNodeUtils.getOptionalJsonNode(fromJsonNode, fieldName);
        if (node == null) {
            return null;
        }

        return node.asText();
    }

    static final JsonNode getRequiredJsonNode(JsonNode fromJsonNode, String fieldName) throws OfxDataNotFound {
        JsonNode jsonNode = fromJsonNode.findPath(fieldName);
        if (jsonNode.isMissingNode()) {
            throw new OfxDataNotFound("Cannot find JsonNode=" + fieldName);
        }
        return jsonNode;
    }

    static final JsonNode getOptionalJsonNode(JsonNode fromJsonNode, String fieldName) {
        JsonNode jsonNode = fromJsonNode.findPath(fieldName);
        if (jsonNode.isMissingNode()) {
            return null;
        }
        return jsonNode;
    }

    public static final String getSymbol(JsonNode fromJsonNode) throws OfxDataNotFound {
        String fieldName = "symbol";
        JsonNode jsonNode = getRequiredJsonNode(fromJsonNode, fieldName);
        return jsonNode.asText();
    }

    public static final String getMarketState(JsonNode fromJsonNode) {
        String fieldName = "marketState";
        JsonNode node = getOptionalJsonNode(fromJsonNode, fieldName);

        if (node != null) {
            return node.asText();
        }

        return null;
    }

    public static final String getExchangeTimezoneName(JsonNode fromJsonNode) {
        String fieldName = "exchangeTimezoneName";
        JsonNode jsonNode = getOptionalJsonNode(fromJsonNode, fieldName);
        if (jsonNode == null) {
            return null;
        }

        return jsonNode.asText();
    }

}
