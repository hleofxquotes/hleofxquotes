package com.hungle.tools.moneyutils.csv2ofx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.csvreader.CsvReader;
import com.hungle.msmoney.core.data.SymbolMapper;
import com.hungle.msmoney.core.fx.FxTable;
import com.hungle.msmoney.core.fx.FxTableUtils;
import com.hungle.msmoney.core.misc.CheckNullUtils;
import com.hungle.msmoney.core.ofx.xmlbeans.CurrencyUtils;
import com.hungle.msmoney.core.ofx.xmlbeans.OfxPriceInfo;
import com.hungle.msmoney.core.ofx.xmlbeans.OfxSaveParameter;
import com.hungle.msmoney.core.stockprice.AbstractStockPrice;
import com.hungle.msmoney.core.stockprice.Price;
import com.hungle.msmoney.core.stockprice.StockPrice;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractCsvConverter.
 */
public abstract class AbstractCsvConverter implements CsvConverter {
    
    /** The Constant LOGGER. */
    private static final Logger LOGGER = Logger.getLogger(AbstractCsvConverter.class);

    /** The symbol mapper. */
    private SymbolMapper symbolMapper = SymbolMapper.loadMapperFile();

    /** The decimal locale. */
    private Locale decimalLocale = null;

    /** The use quote source share count. */
    private boolean useQuoteSourceShareCount = true;

    /** The quote date and time formatter. */
    private SimpleDateFormat quoteDateAndTimeFormatter = new SimpleDateFormat("MMM dd yyy HH:mm z");

    /** The last trade date formatter. */
    private SimpleDateFormat lastTradeDateFormatter = new SimpleDateFormat("MM/dd/yyyy");
    
    /** The last trade time formatter. */
    private SimpleDateFormat lastTradeTimeFormatter = new SimpleDateFormat("hh:mm");

    /**
     * Instantiates a new abstract csv converter.
     */
    public AbstractCsvConverter() {
        super();
    }

    /**
     * Convert.
     *
     * @param fromFile the from file
     * @return the list
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public List<AbstractStockPrice> convert(File fromFile) throws IOException {
        File toFile = null;
        boolean forceGeneratingINVTRANLIST = false;
        return convert(fromFile, forceGeneratingINVTRANLIST, toFile);
    }

    /**
     * Convert.
     *
     * @param inFile the in file
     * @param forceGeneratingINVTRANLIST the force generating INVTRANLIST
     * @param outFile the out file
     * @return the list
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public List<AbstractStockPrice> convert(File inFile, boolean forceGeneratingINVTRANLIST, File outFile) throws IOException {
        List<AbstractStockPrice> beans = new ArrayList<AbstractStockPrice>();
        CsvReader csvReader = null;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(inFile));
            csvReader = new CsvReader(reader);
            LOGGER.info("Reading from inFile=" + inFile);
            csvReader.readHeaders();
            while (csvReader.readRecord()) {
                if (LOGGER.isDebugEnabled()) {
                    String line = csvReader.getRawRecord();
                    LOGGER.debug(line);
                }
                AbstractStockPrice bean = null;
                try {
                    bean = convert(csvReader);
                    if (bean != null) {
                        beans.add(bean);
                    }
                } catch (Exception e) {
                    LOGGER.warn(e);
                }
            }
            if (outFile != null) {
                LOGGER.info("Writing to outFile=" + outFile);
                String defaultCurrency = CurrencyUtils.getDefaultCurrency();
                OfxSaveParameter params = new OfxSaveParameter();
                params.setDefaultCurrency(defaultCurrency);
                params.setForceGeneratingINVTRANLIST(forceGeneratingINVTRANLIST);
                Integer dateOffset = 0;
                params.setDateOffset(dateOffset);
                FxTable fxTable = FxTableUtils.loadFxFile();
                OfxPriceInfo.save(beans, outFile, params, this.symbolMapper, fxTable);
            }
        } finally {
            if (csvReader != null) {
                try {
                    csvReader.close();
                } finally {
                    csvReader = null;
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } finally {
                    reader = null;
                }
            }
        }
        return beans;
    }

    /* (non-Javadoc)
     * @see com.hungle.tools.moneyutils.csv2ofx.CsvConverter#convert(com.csvreader.CsvReader)
     */
    @Override
    public AbstractStockPrice convert(CsvReader csvReader) throws IOException {
        StockPrice stockPrice = new StockPrice();
    
        try {
            setStockName(csvReader, stockPrice);
    
            setStockSymbol(csvReader, stockPrice);
    
            setLastPrice(csvReader, stockPrice);
    
            setLastTrade(csvReader, stockPrice);
            
            setCurrency(csvReader, stockPrice);
    
            setUnits(csvReader, stockPrice);
        } finally {
            if (CheckNullUtils.isNull(stockPrice.getStockName())) {
                stockPrice.setStockName(stockPrice.getStockSymbol());
            }
            stockPrice.calculateSecType();
            stockPrice.updateLastPriceCurrency();
        }
    
        return stockPrice;
    }

    /**
     * Sets the stock name.
     *
     * @param csvReader the csv reader
     * @param stockPrice the stock price
     * @param columnName the column name
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected void setStockName(CsvReader csvReader, AbstractStockPrice stockPrice, String columnName) throws IOException {
        String stockName = csvReader.get(columnName);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(columnName + ": " + stockName);
        }
        if (CheckNullUtils.isNull(stockName)) {
            throw new IOException("SKIP: invalid name=" + stockName);
        }
        stockPrice.setStockName(stockName);
    }

    /**
     * Sets the stock symbol.
     *
     * @param csvReader the csv reader
     * @param stockPrice the stock price
     * @param columnName the column name
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected void setStockSymbol(CsvReader csvReader, AbstractStockPrice stockPrice, String columnName) throws IOException {
        String stockSymbol = csvReader.get(columnName);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(columnName + ": " + stockSymbol);
        }
        if (CheckNullUtils.isNull(stockSymbol)) {
            throw new IOException("SKIP: invalid symbolExchange=" + stockSymbol + ", name=" + stockPrice.getStockName());
        }
        stockPrice.setStockSymbol(stockSymbol);
    }

    /**
     * Sets the last price.
     *
     * @param csvReader the csv reader
     * @param stockPrice the stock price
     * @param columnName the column name
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected void setLastPrice(CsvReader csvReader, AbstractStockPrice stockPrice, String columnName) throws IOException {
        String lastPrice = csvReader.get(columnName);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(columnName + ": " + lastPrice);
        }
        if (CheckNullUtils.isNull(lastPrice)) {
            throw new IOException("SKIP: no price for " + stockPrice.getStockSymbol());
        }
        NumberFormat formatter = null;
        if (decimalLocale != null) {
            formatter = NumberFormat.getNumberInstance(decimalLocale);
        } else {
            formatter = NumberFormat.getNumberInstance();
        }
        Number number = null;
        try {
            number = formatter.parse(lastPrice);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("lastPrice number=" + number);
            }
        } catch (ParseException e) {
            throw new IOException(e);
        }
        Price price = new Price(number.doubleValue());
        stockPrice.setLastPrice(price);
    }

    /**
     * Sets the last trade.
     *
     * @param csvReader the csv reader
     * @param stockPrice the stock price
     * @param columnName the column name
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected void setLastTrade(CsvReader csvReader, AbstractStockPrice stockPrice, String columnName) throws IOException {
        String quoteDateAndTime = csvReader.get(columnName);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(columnName + ": " + quoteDateAndTime);
        }
        if (!CheckNullUtils.isNull(quoteDateAndTime)) {
            try {
                Date date = quoteDateAndTimeFormatter.parse(quoteDateAndTime);
//                stockPrice.setLastTradeDate(lastTradeDateFormatter.format(date));
//                stockPrice.setLastTradeTime(lastTradeTimeFormatter.format(date));
                stockPrice.setLastTrade(date);
            } catch (ParseException e) {
                LOGGER.warn(e);
            }
        }

    }

    /**
     * Sets the currency.
     *
     * @param csvReader the csv reader
     * @param stockPrice the stock price
     * @param columnName the column name
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected void setCurrency(CsvReader csvReader, AbstractStockPrice stockPrice, String columnName) throws IOException {
        String currency = csvReader.get(columnName);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(columnName + ": " + currency);
        }
        stockPrice.setCurrency(currency);
    }

    /**
     * Sets the units.
     *
     * @param csvReader the csv reader
     * @param stockPrice the stock price
     * @param columnName the column name
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected void setUnits(CsvReader csvReader, AbstractStockPrice stockPrice, String columnName) throws IOException {
        if (useQuoteSourceShareCount) {
            String quantity = csvReader.get(columnName);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(columnName + ": " + quantity);
            }
            if (!CheckNullUtils.isNull(quantity)) {
                Double units;
                try {
                    units = Double.valueOf(quantity);
                    stockPrice.setUnits(units);
                } catch (NumberFormatException e) {
                    LOGGER.warn("Cannot convert quantity=" + quantity);
                }
            }
        } else {
            stockPrice.setUnits(0.0);
        }
    }

    /**
     * Gets the decimal locale.
     *
     * @return the decimal locale
     */
    public Locale getDecimalLocale() {
        return decimalLocale;
    }

    /**
     * Sets the decimal locale.
     *
     * @param decimalLocale the new decimal locale
     */
    public void setDecimalLocale(Locale decimalLocale) {
        this.decimalLocale = decimalLocale;
    }

    /**
     * Sets the use quote source share count.
     *
     * @param useQuoteSourceShareCount the new use quote source share count
     */
    public void setUseQuoteSourceShareCount(boolean useQuoteSourceShareCount) {
        this.useQuoteSourceShareCount = useQuoteSourceShareCount;
    }
}