package com.hungle.msmoney.gui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.log4j.Logger;

import com.hungle.msmoney.core.fx.FxTable;
import com.hungle.msmoney.core.fx.FxTableUtils;
import com.hungle.msmoney.core.mapper.SymbolMapper;
import com.hungle.msmoney.core.stockprice.AbstractStockPrice;
import com.hungle.msmoney.core.stockprice.FxSymbol;
import com.hungle.msmoney.core.stockprice.Price;
import com.hungle.msmoney.qs.ft.FtEquitiesQuoteGetter;

import ca.odell.glazedlists.EventList;

public class MDUtils {
    public static final Logger LOGGER = Logger.getLogger(FtEquitiesQuoteGetter.class);

    private static final String DEFAULT_SEPARATOR = ",";

    public static void saveToCsv(EventList<AbstractStockPrice> priceList, boolean convert, String defaultCurrency,
            SymbolMapper symbolMapper, FxTable fxTable, File file) throws IOException {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            MDUtils.saveToCsv(priceList, convert, defaultCurrency, symbolMapper, fxTable, writer);
        } finally {
            if (writer != null) {
                writer.close();
                writer = null;
            }
        }
    }

    static void saveToCsv(EventList<AbstractStockPrice> priceList, boolean convert, String defaultCurrency,
            SymbolMapper symbolMapper, FxTable fxTable, PrintWriter writer) {

        // HEADER
        // Price,Ticker ISIN
        writer.println("Price,Ticker_ISIN");

        // ROWS
        for (AbstractStockPrice price : priceList) {
            // ROW
            StringBuilder sb = toCsvRow(convert, symbolMapper, price, fxTable, defaultCurrency);
            writer.println(sb.toString());
        }
        
        // Now write the derived FX symbols
        for (AbstractStockPrice price : priceList) {
            FxSymbol fxSymbol = price.getFxSymbol();
            if (fxSymbol == null) {
                continue;
            }
            
            
            // ROW
            StringBuilder sb = toCsvRowAsDerivedValue(fxSymbol, price);
            writer.println(sb.toString());
        }        
    }

    private static StringBuilder toCsvRowAsDerivedValue(FxSymbol fxSymbol, AbstractStockPrice price) {
        String separator = DEFAULT_SEPARATOR;

        StringBuilder sb = new StringBuilder();
        
        Price lastPrice = price.getLastPrice().clonePrice();
        lastPrice.setPrice(new Double(1.00) / lastPrice.getPrice());
        sb.append(lastPrice.getPriceFormatter().format(lastPrice));

        sb.append(separator);
        String symbol = fxSymbol.getToCurrency() + fxSymbol.getFromCurrency() + "=X";
        sb.append(symbol);
        
        return sb;
    }

    private static StringBuilder toCsvRow(boolean convert, SymbolMapper symbolMapper, AbstractStockPrice price, FxTable fxTable,
            String defaultCurrency) {
        // symbol
        String separator = DEFAULT_SEPARATOR;

        StringBuilder sb = new StringBuilder();

        // Closing price
        Price lastPrice = price.getLastPrice();
        if (convert) {
            if (price.getFxSymbol() == null) {
                lastPrice = FxTableUtils.getPrice(price.getStockSymbol(), price.getLastPrice(), defaultCurrency, symbolMapper,
                        fxTable);
            }
        }
        sb.append(lastPrice.getPriceFormatter().format(lastPrice));

        sb.append(separator);
        String symbol = price.getStockSymbol();
        if (price.getFxSymbol() != null) {
            // MD specific
            symbol = symbol + "=X";
        } else {
            if (convert) {
                symbol = SymbolMapper.getStockSymbol(price.getStockSymbol(), symbolMapper);
            }
        }
        if (symbol == null) {
            symbol = price.getStockName();
        }
        if (symbol == null) {
            symbol = "";
        }
        // sb.append("\"");
        sb.append(symbol);
        // sb.append("\"");

        return sb;
    }

}
