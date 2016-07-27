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

    /**
     * Parses the current record.
     *
     * @param reader the reader
     * @return the abstract stock price
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private static AbstractStockPrice parseCurrentRecord(final CsvReader reader) throws IOException {
        if (LOGGER.isDebugEnabled()) {
            int count = reader.getColumnCount();
            LOGGER.debug("");
            for (int i = 0; i < count; i++) {
                String value = reader.get(i);
                LOGGER.debug("i=" + i + ", value=" + value);
            }
        }

        CsvRow cvsRow = new CsvRow() {

            @Override
            public String getColumnValue(int i) throws IOException {
                return reader.get(i);
            }

            @Override
            public String getRawRecord() {
                return reader.getRawRecord();
            }
        };

        AbstractStockPrice stockPrice = new StockPrice(cvsRow);

        return stockPrice;
    }

    /**
     * To stock price beans.
     *
     * @param reader the reader
     * @param skipIfNoPrice the skip if no price
     * @return the list
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private static List<AbstractStockPrice> toStockPriceBeans(CsvReader reader, boolean skipIfNoPrice) throws IOException {
        List<AbstractStockPrice> beans = new ArrayList<AbstractStockPrice>();
        // reader.readHeaders();
        while (reader.readRecord()) {
            String line = reader.getRawRecord();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(line);
            }
            AbstractStockPrice bean = parseCurrentRecord(reader);
            if (bean != null) {
                if ((bean.getLastPrice().getPrice() <= 0.0) && (skipIfNoPrice)) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.warn("SKIP: " + line);
                    }
                } else {
                    if (beans != null) {
                        beans.add(bean);
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
    public static List<AbstractStockPrice> toStockPrices(Reader reader, boolean skipIfNoPrice) throws IOException {
        List<AbstractStockPrice> stockPrices = null;

        CsvReader csvReader = null;
        try {
            csvReader = new CsvReader(reader);
            stockPrices = toStockPriceBeans(csvReader, skipIfNoPrice);
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
    public static List<AbstractStockPrice> toStockPrices(Reader reader) throws IOException {
        boolean skipIfNoPrice = true;
        return toStockPrices(reader, skipIfNoPrice);
    }

}
