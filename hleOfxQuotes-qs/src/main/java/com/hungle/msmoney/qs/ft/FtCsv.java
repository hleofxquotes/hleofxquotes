package com.hungle.msmoney.qs.ft;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.csvreader.CsvReader;
import com.hungle.msmoney.core.csv2ofx.AbstractCsvConverter;
import com.hungle.msmoney.core.stockprice.AbstractStockPrice;

// TODO: Auto-generated Javadoc
/**
 * The Class FtCsv.
 */
public class FtCsv extends AbstractCsvConverter {
    
    /** The Constant LOGGER. */
    private static final Logger LOGGER = Logger.getLogger(FtCsv.class);
    
    /** The Constant COLUMN_NAME_UNITS. */
    private static final String COLUMN_NAME_UNITS = "Quantity";
    
    /** The Constant COLUMN_NAME_CURRENCY. */
    private static final String COLUMN_NAME_CURRENCY = "Currency for Last Price";
    
    /** The Constant COLUMN_NAME_LAST_TRADE. */
    private static final String COLUMN_NAME_LAST_TRADE = "Quote Date & Time";
    
    /** The Constant COLUMN_NAME_LAST_PRICE. */
    private static final String COLUMN_NAME_LAST_PRICE = "Last Price";
    
    /** The Constant COLUMN_NAME_STOCK_SYMBOL. */
    private static final String COLUMN_NAME_STOCK_SYMBOL = "Symbol: Exchange";
    
    /** The Constant COLUMN_NAME_STOCK_NAME. */
    private static final String COLUMN_NAME_STOCK_NAME = "Name";
    
    /* (non-Javadoc)
     * @see com.hungle.tools.moneyutils.csv2ofx.CsvConverter#setStockName(com.csvreader.CsvReader, com.hungle.tools.moneyutils.stockprice.AbstractStockPrice)
     */
    @Override
    public void setStockName(CsvReader csvReader, AbstractStockPrice stockPrice) throws IOException {
        // "Name"
        String columnName = COLUMN_NAME_STOCK_NAME;
        setStockName(csvReader, stockPrice, columnName);
    }

    /* (non-Javadoc)
     * @see com.hungle.tools.moneyutils.csv2ofx.CsvConverter#setStockSymbol(com.csvreader.CsvReader, com.hungle.tools.moneyutils.stockprice.AbstractStockPrice)
     */
    @Override
    public void setStockSymbol(CsvReader csvReader, AbstractStockPrice stockPrice) throws IOException {
        // "Symbol: Exchange"
        String columnName = COLUMN_NAME_STOCK_SYMBOL;
        setStockSymbol(csvReader, stockPrice, columnName);
    }

    /* (non-Javadoc)
     * @see com.hungle.tools.moneyutils.csv2ofx.CsvConverter#setLastPrice(com.csvreader.CsvReader, com.hungle.tools.moneyutils.stockprice.AbstractStockPrice)
     */
    @Override
    public void setLastPrice(CsvReader csvReader, AbstractStockPrice stockPrice) throws IOException {
        // "Last Price"
        String columnName = COLUMN_NAME_LAST_PRICE;
        setLastPrice(csvReader, stockPrice, columnName);
    }

    /* (non-Javadoc)
     * @see com.hungle.tools.moneyutils.csv2ofx.CsvConverter#setLastTrade(com.csvreader.CsvReader, com.hungle.tools.moneyutils.stockprice.AbstractStockPrice)
     */
    @Override
    public void setLastTrade(CsvReader csvReader, AbstractStockPrice stockPrice) throws IOException {
        // Quote Date & Time
        String columnName = COLUMN_NAME_LAST_TRADE;
        setLastTrade(csvReader, stockPrice, columnName);
    }

    /* (non-Javadoc)
     * @see com.hungle.tools.moneyutils.csv2ofx.CsvConverter#setCurrency(com.csvreader.CsvReader, com.hungle.tools.moneyutils.stockprice.AbstractStockPrice)
     */
    @Override
    public void setCurrency(CsvReader csvReader, AbstractStockPrice stockPrice) throws IOException {
        // Currency for Last Price
        String columnName = COLUMN_NAME_CURRENCY;
        setCurrency(csvReader, stockPrice, columnName);
    }

    /* (non-Javadoc)
     * @see com.hungle.tools.moneyutils.csv2ofx.CsvConverter#setUnits(com.csvreader.CsvReader, com.hungle.tools.moneyutils.stockprice.AbstractStockPrice)
     */
    @Override
    public void setUnits(CsvReader csvReader, AbstractStockPrice stockPrice) throws IOException {
        // Quantity
        String columnName = COLUMN_NAME_UNITS;

        setUnits(csvReader, stockPrice, columnName);
    }
}
