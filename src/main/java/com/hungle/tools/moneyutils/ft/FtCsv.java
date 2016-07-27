package com.hungle.tools.moneyutils.ft;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.csvreader.CsvReader;
import com.hungle.tools.moneyutils.csv2ofx.AbstractCsvConverter;
import com.hungle.tools.moneyutils.stockprice.StockPrice;

public class FtCsv extends AbstractCsvConverter {
    private static final String COLUMN_NAME_UNITS = "Quantity";
    private static final String COLUMN_NAME_CURRENCY = "Currency for Last Price";
    private static final String COLUMN_NAME_LAST_TRADE = "Quote Date & Time";
    private static final String COLUMN_NAME_LAST_PRICE = "Last Price";
    private static final String COLUMN_NAME_STOCK_SYMBOL = "Symbol: Exchange";
    private static final String COLUMN_NAME_STOCK_NAME = "Name";
    private static final Logger LOGGER = Logger.getLogger(FtCsv.class);

    @Override
    public void setStockName(CsvReader csvReader, StockPrice stockPrice) throws IOException {
        // "Name"
        String columnName = COLUMN_NAME_STOCK_NAME;
        setStockName(csvReader, stockPrice, columnName);
    }

    @Override
    public void setStockSymbol(CsvReader csvReader, StockPrice stockPrice) throws IOException {
        // "Symbol: Exchange"
        String columnName = COLUMN_NAME_STOCK_SYMBOL;
        setStockSymbol(csvReader, stockPrice, columnName);
    }

    @Override
    public void setLastPrice(CsvReader csvReader, StockPrice stockPrice) throws IOException {
        // "Last Price"
        String columnName = COLUMN_NAME_LAST_PRICE;
        setLastPrice(csvReader, stockPrice, columnName);
    }

    @Override
    public void setLastTrade(CsvReader csvReader, StockPrice stockPrice) throws IOException {
        // Quote Date & Time
        String columnName = COLUMN_NAME_LAST_TRADE;
        setLastTrade(csvReader, stockPrice, columnName);
    }

    @Override
    public void setCurrency(CsvReader csvReader, StockPrice stockPrice) throws IOException {
        // Currency for Last Price
        String columnName = COLUMN_NAME_CURRENCY;
        setCurrency(csvReader, stockPrice, columnName);
    }

    @Override
    public void setUnits(CsvReader csvReader, StockPrice stockPrice) throws IOException {
        // Quantity
        String columnName = COLUMN_NAME_UNITS;

        setUnits(csvReader, stockPrice, columnName);
    }
}
