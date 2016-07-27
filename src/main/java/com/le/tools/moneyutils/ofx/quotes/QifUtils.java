package com.le.tools.moneyutils.ofx.quotes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;

import com.le.tools.moneyutils.stockprice.AbstractStockPrice;
import com.le.tools.moneyutils.stockprice.Price;

import ca.odell.glazedlists.EventList;

public class QifUtils {
    private static Calendar calendar = Calendar.getInstance();

    static void saveToQif(EventList<AbstractStockPrice> priceList, File file) throws IOException {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            QifUtils.saveToQif(priceList, writer);
        } finally {
            if (writer != null) {
                writer.close();
                writer = null;
            }
        }
    }

    static void saveToQif(EventList<AbstractStockPrice> priceList, PrintWriter writer) {
        // !Type:Prices
        writer.println("!Type:Prices");
        for (AbstractStockPrice price : priceList) {
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
            StringBuilder sb = new StringBuilder();

            // symbol
            String symbol = price.getStockSymbol();
            if (symbol == null) {
                symbol = price.getStockName();
            }
            if (symbol == null) {
                symbol = "";
            }
            sb.append("\"" + symbol + "\"");

            // Closing price
            sb.append(",");
            Price lastPrice = price.getLastPrice();
            sb.append(lastPrice.getPriceFormatter().format(lastPrice));

            // Date
            Date lastTrade = price.getLastTrade();
            sb.append(",");
            sb.append("\"" + toQifDateString(lastTrade) + "\"");

            // Day's high price
            sb.append(",");

            // Day's low price
            sb.append(",");

            // Volume
            sb.append(",");
            
            writer.println(sb.toString());
        }
    }

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
