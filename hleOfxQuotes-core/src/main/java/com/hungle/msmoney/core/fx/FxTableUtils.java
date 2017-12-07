package com.hungle.msmoney.core.fx;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.hungle.msmoney.core.stockprice.AbstractStockPrice;
import com.hungle.msmoney.core.stockprice.FxSymbol;

import ca.odell.glazedlists.EventList;

public class FxTableUtils {
    private static final Logger LOGGER = Logger.getLogger(FxTableUtils.class);

    public static final String DEFAULT_FX_FILENAME = "fx.csv";

    private FxTableUtils() {
        // TODO Auto-generated constructor stub
    }

    public static void clearFxTable(EventList<AbstractStockPrice> list) {
        list.clear();
    }

    public static final void updateFxTable(List<AbstractStockPrice> newExchangeRates,
            EventList<AbstractStockPrice> list) {
        list.getReadWriteLock().writeLock().lock();
        try {
            list.clear();

            if (newExchangeRates != null) {
                list.addAll(newExchangeRates);

            }
        } finally {
            list.getReadWriteLock().writeLock().unlock();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("< exchangeRatesReceived");
            }
        }
    }

    /**
     * Creates the default fx table.
     *
     * @return the fx table
     */
    public static FxTable loadFxFile() {
        String fileName = FxTableUtils.DEFAULT_FX_FILENAME;
        return loadFxFile(fileName);
    }

    public static FxTable loadFxFile(String fileName) {
        FxTable fxTable = new FxTable();
        File fxTableFile = new File(fileName);
        LOGGER.info("Looking for fxTable=" + fxTableFile.getAbsoluteFile().getAbsolutePath());
        if (fxTableFile.exists()) {
            try {
                fxTable.load(fxTableFile);
                LOGGER.info("Loaded fxTableFile=" + fxTableFile);
            } catch (IOException e) {
                LOGGER.warn("Cannot load fxTableFile=" + fxTableFile);
            }
        } else {
            LOGGER.info("No " + fileName + " file.");
        }
        return fxTable;
    }

    /**
     * Write fx file.
     *
     * @param fxStockPrices
     *            the fx stock prices
     * @param fileName
     *            the file name
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static void writeFxFile(List<AbstractStockPrice> fxStockPrices, String fileName) throws IOException {
        if (fxStockPrices == null) {
            return;
        }

        if (fxStockPrices.size() <= 0) {
            return;
        }

        if (fileName == null) {
            return;
        }

        // String fileName = "fx.csv";
        File backupFile = new File(fileName + ".bak");
        if (backupFile.exists()) {
            if (!backupFile.delete()) {
                LOGGER.warn("Cannot delete file=" + backupFile);
            }
        }

        File file = new File(fileName);
        if (file.exists()) {
            if (!file.renameTo(backupFile)) {
                LOGGER.warn("Cannot rename from " + file + " to " + backupFile);
            }
        }

        LOGGER.info("Writing fx rates to " + file);

        PrintWriter writer = null;
        try {
            Date now = new Date();
            writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            writer.println("FromCurrency,ToCurrency,Rate,Date");
            writer.println();
            for (AbstractStockPrice fxStockPrice : fxStockPrices) {
                FxSymbol fxSymbol = fxStockPrice.getFxSymbol();
                if (fxSymbol == null) {
                    continue;
                }

                FxTableUtils.writeFxCsvEntry(writer, fxSymbol, now);
            }
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } finally {
                    writer = null;
                }
            }
        }
    }

    /**
     * Write fx csv entry.
     *
     * @param writer
     *            the writer
     * @param fxSymbol
     *            the fx symbol
     * @param now
     *            the now
     */
    private static void writeFxCsvEntry(PrintWriter writer, FxSymbol fxSymbol, Date now) {
        writer.println(
                fxSymbol.getFromCurrency() + ", " + fxSymbol.getToCurrency() + ", " + fxSymbol.getRate() + ", " + now);
    }

    public static final void addExchangeRates(List<AbstractStockPrice> exchangeRates, FxTable fxTable) {
        for (AbstractStockPrice exchangeRate : exchangeRates) {
            FxSymbol fxSymbol = exchangeRate.getFxSymbol();
            if (fxSymbol == null) {
                continue;
            }
            FxTableEntry fxTableEntry = new FxTableEntry();
            fxTableEntry.setFromCurrency(fxSymbol.getFromCurrency());
            fxTableEntry.setToCurrency(fxSymbol.getToCurrency());
            fxTableEntry.setRate(fxTable.getRateFormatter().format(fxSymbol.getRate()));
            fxTable.add(fxTableEntry);
        }
    }

}
