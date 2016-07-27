package com.le.tools.moneyutils.ft;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.csvreader.CsvReader;
import com.le.tools.moneyutils.stockprice.StockPrice;

public class FtCsv extends AbstractCsvConverter {
    private static final Logger log = Logger.getLogger(FtCsv.class);

    @Override
    public void setStockName(CsvReader csvReader, StockPrice stockPrice) throws IOException {
        // "Name"
        String columnName = "Name";
        setStockName(csvReader, stockPrice, columnName);
    }

    @Override
    public void setStockSymbol(CsvReader csvReader, StockPrice stockPrice) throws IOException {
        // "Symbol: Exchange"
        String columnName = "Symbol: Exchange";
        setStockSymbol(csvReader, stockPrice, columnName);
    }

    @Override
    public void setLastPrice(CsvReader csvReader, StockPrice stockPrice) throws IOException {
        // "Last Price"
        String columnName = "Last Price";
        setLastPrice(csvReader, stockPrice, columnName);
    }

    @Override
    public void setLastTrade(CsvReader csvReader, StockPrice stockPrice) throws IOException {
        // Quote Date & Time
        String columnName = "Quote Date & Time";
        setLastTrade(csvReader, stockPrice, columnName);
    }

    @Override
    public void setCurrency(CsvReader csvReader, StockPrice stockPrice) throws IOException {
        // Currency for Last Price
        String columnName = "Currency for Last Price";
        setCurrency(csvReader, stockPrice, columnName);
    }

    @Override
    public void setUnits(CsvReader csvReader, StockPrice stockPrice) throws IOException {
        // Quantity
        String columnName = "Quantity";

        setUnits(csvReader, stockPrice, columnName);
    }
}
