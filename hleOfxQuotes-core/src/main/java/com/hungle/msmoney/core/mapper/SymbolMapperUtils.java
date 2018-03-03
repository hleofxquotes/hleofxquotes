package com.hungle.msmoney.core.mapper;

import java.util.List;

import org.apache.log4j.Logger;

import com.hungle.msmoney.core.misc.CheckNullUtils;
import com.hungle.msmoney.core.stockprice.AbstractStockPrice;
import com.hungle.msmoney.core.stockprice.Price;

import le.com.tools.moneyutils.ofx.quotes.GUI;

public class SymbolMapperUtils {
    private static final Logger LOGGER = Logger.getLogger(GUI.class);

    /**
     * Gets the mapper currency.
     *
     * @param symbol
     *            the symbol
     * @param defaultValue
     *            the default value
     * @param mapper
     *            the mapper
     * @return the mapper currency
     */
    public static String getMapperCurrency(String symbol, String defaultValue, SymbolMapper mapper) {
        String quoteSourceSymbol = null;
        String currency = defaultValue;
        for (SymbolMapperEntry entry : mapper.getEntries()) {
            quoteSourceSymbol = entry.getQuotesSourceSymbol();
            if (CheckNullUtils.isEmpty(quoteSourceSymbol)) {
                continue;
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("s=" + quoteSourceSymbol + ", symbol=" + symbol);
            }
            if (quoteSourceSymbol.compareToIgnoreCase(symbol) != 0) {
                continue;
            }
            currency = entry.getQuotesSourceCurrency();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("getMapperCurrency: s=" + quoteSourceSymbol + ", currency=" + currency);
            }
            if (!CheckNullUtils.isEmpty(currency)) {
                return currency;
            }
        }
        return currency;
    }

    /**
     * Update last price currency.
     *
     * @param stockPrices
     *            the stock prices
     * @param defaultCurrency
     *            the default currency
     * @param symbolMapper
     *            the symbol mapper
     */
    public static void updateLastPriceCurrency(List<AbstractStockPrice> stockPrices, String defaultCurrency,
            SymbolMapper symbolMapper) {
        for (AbstractStockPrice stockPrice : stockPrices) {
            Price price = stockPrice.getLastPrice();
            if ((defaultCurrency != null) && (price.getCurrency() == null)) {
                price.setCurrency(defaultCurrency);
            }
            String currency = stockPrice.getCurrency();
            if (CheckNullUtils.isEmpty(currency)) {
                String symbol = stockPrice.getStockSymbol();
                String overridingCurrency = null;
                overridingCurrency = getMapperCurrency(symbol, overridingCurrency, symbolMapper);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.info("symbol: " + symbol + ", overridingCurrency=" + overridingCurrency);
                }
                if (!CheckNullUtils.isEmpty(overridingCurrency)) {
                    stockPrice.setCurrency(overridingCurrency);
                    stockPrice.updateLastPriceCurrency();
                }
            }
        }
    }

}
