package com.hungle.msmoney.core.fx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.csvreader.CsvReader;
import com.hungle.msmoney.core.misc.CheckNullUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class FxTable.
 */
public class FxTable {
    
    /** The Constant log. */
    public static final Logger LOGGER = Logger.getLogger(FxTable.class);

    /** The entries. */
    private List<FxTableEntry> entries = new ArrayList<FxTableEntry>();

    /** The rate formatter. */
    private NumberFormat rateFormatter;

    /**
     * Instantiates a new fx table.
     */
    public FxTable() {
        super();
        this.rateFormatter = NumberFormat.getNumberInstance();
        this.rateFormatter.setGroupingUsed(false);
        this.rateFormatter.setMinimumFractionDigits(2);
        this.rateFormatter.setMaximumFractionDigits(10);
    }

    /**
     * Gets the rate string.
     *
     * @param fromCurrency the from currency
     * @param toCurrency the to currency
     * @return the rate string
     */
    public Double getCurrencyRate(String fromCurrency, String toCurrency) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("getRateString: " + fromCurrency + ", " + toCurrency);
        }

        for (FxTableEntry entry : entries) {
            String fCurr = entry.getFromCurrency();
            String toCurr = entry.getToCurrency();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("getRateString: fCur=" + fCurr + ", toCurr=" + toCurr);
            }

            if (fCurr.compareToIgnoreCase(fromCurrency) != 0) {
                continue;
            }
            if (toCurr.compareToIgnoreCase(toCurrency) != 0) {
                continue;
            }
            Double value = Double.valueOf(entry.getRate());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("getRateString: " + fromCurrency + ", " + toCurrency + ", rate=" + value);
            }
            return value;
        }

        // derived rate
        for (FxTableEntry entry : entries) {
            String fCurr = entry.getFromCurrency();
            String toCurr = entry.getToCurrency();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("getRateString (derived): fCur=" + fCurr + ", toCurr=" + toCurr);
            }
            if (fCurr.compareToIgnoreCase(toCurrency) != 0) {
                continue;
            }
            if (toCurr.compareToIgnoreCase(fromCurrency) != 0) {
                continue;
            }
            Double value = Double.valueOf(entry.getRate());
            value = 1.00 / value;
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("getRateString (derived): " + fromCurrency + ", " + toCurrency + ", rate=" + value);
            }
            return value;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("getRateString (NOT_FOUND): " + fromCurrency + ", " + toCurrency + ", rate=" + null);
        }
        return null;
    }

    /**
     * Load.
     *
     * @param file the file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void load(File file) throws IOException {
        Reader reader = null;
        CsvReader csvReader = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            csvReader = new CsvReader(reader);
            csvReader.readHeaders();
            String columnName = null;
            FxTableEntry entry = null;
            while (csvReader.readRecord()) {
                entry = new FxTableEntry();

                columnName = "FromCurrency";
                String fromCurrency = csvReader.get(columnName);
                if (CheckNullUtils.isNull(fromCurrency)) {
                    LOGGER.warn("Value for column=" + columnName + " is null.");
                    continue;
                }
                if (fromCurrency != null) {
                    fromCurrency = fromCurrency.trim();
                }
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("fromCurrency=" + fromCurrency);
                }
                entry.setFromCurrency(fromCurrency);

                columnName = "ToCurrency";
                String toCurrency = csvReader.get(columnName);
                if (CheckNullUtils.isNull(toCurrency)) {
                    LOGGER.warn("Value for column=" + columnName + " is null.");
                    continue;
                }
                if (toCurrency != null) {
                    toCurrency = toCurrency.trim();
                }
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("toCurrency=" + toCurrency);
                }
                entry.setToCurrency(toCurrency);

                columnName = "Rate";
                String rate = csvReader.get(columnName);
                if (CheckNullUtils.isNull(rate)) {
                    LOGGER.warn("Value for column=" + columnName + " is null.");
                    continue;
                }
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("rate=" + rate);
                }
                if (rate != null) {
                    rate = rate.trim();
                }
                entry.setRate(rate);

                entries.add(entry);
            }
        } finally {
            if (csvReader != null) {
                csvReader.close();
                csvReader = null;
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.warn(e);
                } finally {
                    reader = null;
                }
            }
        }
    }
}
