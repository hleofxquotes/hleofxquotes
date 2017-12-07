package com.hungle.msmoney.gui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.hungle.msmoney.core.fx.FxTable;
import com.hungle.msmoney.core.mapper.SymbolMapper;
import com.hungle.msmoney.core.mapper.SymbolMapperEntry;
import com.hungle.msmoney.core.stockprice.AbstractStockPrice;
import com.hungle.msmoney.core.stockprice.Price;
import com.hungle.msmoney.qs.ft.FtEquitiesQuoteGetter;

import ca.odell.glazedlists.EventList;

public class MDUtils {
    private static final Logger LOGGER = Logger.getLogger(FtEquitiesQuoteGetter.class);

    private static final String DEFAULT_SEPARATOR = ",";

    public static void saveToCsv(EventList<AbstractStockPrice> priceList, String defaultCurrency,
            SymbolMapper symbolMapper, FxTable fxTable, File file) throws IOException {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            MDUtils.saveToCsv(priceList, defaultCurrency, symbolMapper, fxTable, writer);
        } finally {
            if (writer != null) {
                writer.close();
                writer = null;
            }
        }
    }

    static void saveToCsv(EventList<AbstractStockPrice> priceList, String defaultCurrency, SymbolMapper symbolMapper,
            FxTable fxTable, PrintWriter writer) {
        // Price,Ticker ISIN
        writer.println("Price,Ticker_ISIN");
        for (AbstractStockPrice price : priceList) {
            StringBuilder sb = new StringBuilder();

            // Closing price
            Price lastPrice = getPrice(price.getStockSymbol(), price.getLastPrice(), defaultCurrency, symbolMapper,
                    fxTable);
            sb.append(lastPrice.getPriceFormatter().format(lastPrice));

            // symbol
            String separator = DEFAULT_SEPARATOR;
            sb.append(separator);
            String symbol = getStockSymbol(price.getStockSymbol(), symbolMapper, fxTable);
            if (symbol == null) {
                symbol = price.getStockName();
            }
            if (symbol == null) {
                symbol = "";
            }
            // sb.append("\"");
            sb.append(symbol);
            // sb.append("\"");

            writer.println(sb.toString());
        }
    }

    private static String getStockSymbol(String qsSymbol, SymbolMapper symbolMapper, FxTable fxTable) {
        String symbol = qsSymbol;
        List<SymbolMapperEntry> entries = symbolMapper.getMapByQuotesSourceSymbol().get(qsSymbol);
        if (entries == null) {
            return symbol;
        }

        for (SymbolMapperEntry entry : entries) {
            String s = entry.getMsMoneySymbol();
            if (!StringUtils.isBlank(s)) {
                symbol = s;
                break;
            }
        }

        return symbol;
    }

    private static Price getPrice(String qsSymbol, Price qsPrice, String defaultCurrency, SymbolMapper symbolMapper,
            FxTable fxTable) {
        Price price = qsPrice;

        String fromCurrency = null;
        String toCurrency = null;

        List<SymbolMapperEntry> entries = symbolMapper.getMapByQuotesSourceSymbol().get(qsSymbol);
        if (entries != null) {
            for (SymbolMapperEntry entry : entries) {
                String from = entry.getQuotesSourceCurrency();
                String to = entry.getMsMoneyCurrency();
                if (!StringUtils.isBlank(from)) {
                    fromCurrency = from;
                }
                if (!StringUtils.isBlank(to)) {
                    toCurrency = to;
                }
                if ((!StringUtils.isBlank(fromCurrency)) && (!StringUtils.isBlank(toCurrency))) {
                    break;
                }
            }
        }

        if (StringUtils.isBlank(fromCurrency)) {
            fromCurrency = qsPrice.getCurrency();
        }
        if (StringUtils.isBlank(toCurrency)) {
            toCurrency = defaultCurrency;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("fromCurrency=" + fromCurrency + ", toCurrency=" + toCurrency);
        }

        Double rate = null;
        if ((!StringUtils.isBlank(fromCurrency)) && (!StringUtils.isBlank(toCurrency))) {
            if (fromCurrency.compareToIgnoreCase(toCurrency) != 0) {
                if (isGBP(fromCurrency) && isGBX(toCurrency)) {
                    rate = new Double(100.00);
                } else if (isGBX(fromCurrency) && isGBP(toCurrency)) {
                    rate = new Double(0.01);
                } else {
                    rate = fxTable.getCurrencyRate(fromCurrency, toCurrency);
                }
            }
        }
        if (rate != null) {
            if (rate != null) {
                double oldPrice = qsPrice.getPrice();
                price.setPrice(qsPrice.getPrice() * rate);
                double newPrice = price.getPrice();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Converting price for symbol=" + qsSymbol + ", qsPrice=" + oldPrice + ", price="
                            + newPrice);
                }
            }
        }

        return price;
    }

    private static boolean isGBX(String currency) {
        return currency.compareToIgnoreCase("GBX") == 0;
    }

    private static boolean isGBP(String currency) {
        return currency.compareToIgnoreCase("GBP") == 0;
    }

}
