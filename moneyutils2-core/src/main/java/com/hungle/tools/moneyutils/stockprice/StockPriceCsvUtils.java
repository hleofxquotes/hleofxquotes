package com.hungle.tools.moneyutils.stockprice;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.csvreader.CsvReader;

// TODO: Auto-generated Javadoc
/**
 * The Class CsvUtils.
 */
public class StockPriceCsvUtils {
    
    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(StockPriceCsvUtils.class);

    public static final char CSV_DELIMITER_TAB_CHAR = '\t';

    public static final char CSV_DELIMITER_COMMA_CHAR = ',';

    /**
     * Parses the current record.
     *
     * @param csvReader the reader
     * @return the abstract stock price
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private static AbstractStockPrice parseCurrentRecord(final CsvReader csvReader) throws IOException {
        CsvRow cvsRow = getCurrentCsvRow(csvReader);

        AbstractStockPrice stockPrice = new StockPrice(cvsRow);

        return stockPrice;
    }

    /**
     * Read from reader and return the current CsvRow.
     *
     * @param csvReader the csv reader
     * @return the current csv row
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private static CsvRow getCurrentCsvRow(final CsvReader csvReader) throws IOException {
        if (LOGGER.isDebugEnabled()) {
            int count = csvReader.getColumnCount();
            LOGGER.debug("");
            for (int i = 0; i < count; i++) {
                String value = csvReader.get(i);
                LOGGER.debug("i=" + i + ", value=" + value);
            }
        }

        CsvRow cvsRow = new CsvRow() {

            @Override
            public String getColumnValue(int i) throws IOException {
                return csvReader.get(i);
            }

            @Override
            public String getRawRecord() {
                return csvReader.getRawRecord();
            }
        };
        
        return cvsRow;
    }

    /**
     * To stock price beans.
     *
     * @param csvReader the reader
     * @param skipIfNoPrice the skip if no price
     * @return the list
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private static List<AbstractStockPrice> toStockPrices(CsvReader csvReader, boolean skipIfNoPrice) throws IOException {
        List<AbstractStockPrice> beans = new ArrayList<AbstractStockPrice>();
        // reader.readHeaders();
        while (csvReader.readRecord()) {
            String line = csvReader.getRawRecord();
            LOGGER.info(line);
            
            if (line != null) {
                line = line.trim();
            }
            if (line.startsWith("#") ) {
                continue;
            }
            
            AbstractStockPrice price = parseCurrentRecord(csvReader);
            LOGGER.info(price);
            
            if (price != null) {
                if ((price.getLastPrice().getPrice() <= 0.0) && (skipIfNoPrice)) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.warn("SKIP: " + line);
                    }
                } else {
                    if (beans != null) {
                        beans.add(price);
                    }
                }
            } else {
                LOGGER.warn("Cannot parse line=" + line);
            }
        }
        return beans;
    }

    /**
     * To stock prices.
     *
     * @param reader the reader
     * @param skipIfNoPrice the skip if no price
     * @return the list
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static List<AbstractStockPrice> toStockPrices(Reader reader, boolean skipIfNoPrice, char delimiter) throws IOException {
        List<AbstractStockPrice> stockPrices = null;

        CsvReader csvReader = null;
        try {
            csvReader = new CsvReader(reader, delimiter);
            stockPrices = toStockPrices(csvReader, skipIfNoPrice);
        } finally {
            if (csvReader != null) {
                try {
                    csvReader.close();
                } finally {
                    csvReader = null;
                }
            }
        }
        return stockPrices;
    }

    /**
     * To stock prices.
     *
     * @param reader the reader
     * @return the list
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static List<AbstractStockPrice> toStockPrices(Reader reader, char delimiter) throws IOException {
        boolean skipIfNoPrice = true;
        return toStockPrices(reader, skipIfNoPrice, delimiter);
    }

}
