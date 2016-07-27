package com.le.tools.moneyutils.stockprice;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.csvreader.CsvReader;

public class CsvUtils {
    private static final Logger log = Logger.getLogger(CsvUtils.class);

    private static AbstractStockPrice parseCurrentRecord(final CsvReader reader) throws IOException {
        if (log.isDebugEnabled()) {
            int count = reader.getColumnCount();
            log.debug("");
            for (int i = 0; i < count; i++) {
                String value = reader.get(i);
                log.debug("i=" + i + ", value=" + value);
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

    private static List<AbstractStockPrice> toStockPriceBeans(CsvReader reader, boolean skipIfNoPrice) throws IOException {
        List<AbstractStockPrice> beans = new ArrayList<AbstractStockPrice>();
        // reader.readHeaders();
        while (reader.readRecord()) {
            String line = reader.getRawRecord();
            if (log.isDebugEnabled()) {
                log.debug(line);
            }
            AbstractStockPrice bean = parseCurrentRecord(reader);
            if (bean != null) {
                if ((bean.getLastPrice().getPrice() <= 0.0) && (skipIfNoPrice)) {
                    if (log.isDebugEnabled()) {
                        log.warn("SKIP: " + line);
                    }
                } else {
                    if (beans != null) {
                        beans.add(bean);
                    }
                }
            } else {
                log.warn("Cannot parse line=" + line);
            }
        }
        return beans;
    }

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

    public static List<AbstractStockPrice> toStockPrices(Reader reader) throws IOException {
        boolean skipIfNoPrice = true;
        return toStockPrices(reader, skipIfNoPrice);
    }

}
