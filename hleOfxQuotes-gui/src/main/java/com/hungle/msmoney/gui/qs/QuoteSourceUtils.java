package com.hungle.msmoney.gui.qs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.hungle.msmoney.core.ofx.OfxUtils;

public class QuoteSourceUtils {
    private static final Logger LOGGER = Logger.getLogger(QuoteSourceUtils.class);

    /**
     * To stock symbols.
     *
     * @param stocksString
     *            the stocks string
     * @return the list
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static List<String> toStockSymbols(String stocksString) throws IOException {
        List<String> stocks = new ArrayList<String>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new StringReader(stocksString));
            OfxUtils.addToList(reader, stocks);
        } finally {
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
        return stocks;
    }

}
