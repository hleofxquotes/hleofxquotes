package com.hungle.msmoney.core.ofx;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;

import com.hungle.msmoney.core.fx.FxTable;
import com.hungle.msmoney.core.fx.FxTableUtils;
import com.hungle.msmoney.core.mapper.SymbolMapper;
import com.hungle.msmoney.core.stockprice.AbstractStockPrice;
import com.hungle.msmoney.core.stockprice.Price;

import ca.odell.glazedlists.EventList;

// TODO: Auto-generated Javadoc
/**
 * The Class QifUtils.
 */
public class QifUtils {

    /** The calendar. */
    private static Calendar calendar = Calendar.getInstance();

    /**
     * Save to qif.
     *
     * @param priceList
     *            the price list
     * @param file
     *            the file
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static void saveToQif(EventList<AbstractStockPrice> priceList, boolean convert, String defaultCurrency,
            SymbolMapper symbolMapper, FxTable fxTable, File file) throws IOException {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            QifUtils.saveToQif(priceList, convert, defaultCurrency, symbolMapper, fxTable, writer);
        } finally {
            if (writer != null) {
                writer.close();
                writer = null;
            }
        }
    }

    /**
     * Save to qif.
     *
     * @param priceList
     *            the price list
     * @param writer
     *            the writer
     */
    static void saveToQif(EventList<AbstractStockPrice> priceList, boolean convert, String defaultCurrency,
            SymbolMapper symbolMapper, FxTable fxTable, PrintWriter writer) {
        // HEADER
        // !Type:Prices
        writer.println("!Type:Prices");

        // ROWS
        for (AbstractStockPrice price : priceList) {
            StringBuilder sb = toQifRow(price, convert, defaultCurrency, symbolMapper, fxTable);

            writer.println(sb.toString());
        }
    }

    private static StringBuilder toQifRow(AbstractStockPrice price, boolean convert, String defaultCurrency,
            SymbolMapper symbolMapper, FxTable fxTable) {
        // "010869AQ",105.730,"6/6/03",,,0
        // the QIF Type:Prices fields are:
        // Stock Exchange code for stock (above is the ASX
        // code for
        // Commonwealth Bank of Australia, CBA)
        // Closing price
        // Date
        // Day's high price (empty in above example)
        // Day's low price (empty in above example)
        // Volume
        String separator = ",";

        StringBuilder sb = new StringBuilder();

        // symbol
        String symbol = price.getStockSymbol();
        if (convert) {
            symbol = SymbolMapper.getStockSymbol(price.getStockSymbol(), symbolMapper);
        }
        if (symbol == null) {
            symbol = price.getStockName();
        }
        if (symbol == null) {
            symbol = "";
        }
        sb.append("\"" + symbol + "\"");

        // Closing price
        sb.append(separator);
        Price lastPrice = price.getLastPrice();
        if (convert) {
            if (price.getFxSymbol() == null) {
                if (price.getFxSymbol() == null) {
                    lastPrice = FxTableUtils.getPrice(price.getStockSymbol(), price.getLastPrice(), defaultCurrency, symbolMapper,
                            fxTable);
                }
            }
        }
        sb.append(lastPrice.getPriceFormatter().format(lastPrice));

        // Date
        Date lastTrade = price.getLastTrade();
        sb.append(separator);
        sb.append("\"" + toQifDateString(lastTrade) + "\"");

        // Day's high price
        sb.append(separator);

        // Day's low price
        sb.append(separator);

        // Volume
        sb.append(separator);
        return sb;
    }

    /**
     * To qif date string.
     *
     * @param date
     *            the date
     * @return the string
     */
    private static String toQifDateString(Date date) {
        StringBuilder sb = new StringBuilder();

        calendar.setTime(date);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        if (year < 2000) {
            // 12/31/1999
            sb.append(month + 1);
            sb.append("/");
            sb.append(dayOfMonth);
            sb.append("/");
            sb.append(year);
        } else {
            // 10/7'2002
            sb.append(month + 1);
            sb.append("/");
            sb.append(dayOfMonth);
            sb.append("'");
            sb.append(year);
        }
        return sb.toString();
    }

}
