package com.hungle.msmoney.gui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.hungle.msmoney.core.stockprice.AbstractStockPrice;
import com.hungle.msmoney.core.stockprice.Price;

import ca.odell.glazedlists.EventList;

public class MDUtils {
    public static void saveToCsv(EventList<AbstractStockPrice> priceList, File file) throws IOException {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            MDUtils.saveToCsv(priceList, writer);
        } finally {
            if (writer != null) {
                writer.close();
                writer = null;
            }
        }
    }

    static void saveToCsv(EventList<AbstractStockPrice> priceList, PrintWriter writer) {
        // Price,Ticker ISIN
        writer.println("Price,Ticker_ISIN");
        for (AbstractStockPrice price : priceList) {
            StringBuilder sb = new StringBuilder();

            // Closing price
            Price lastPrice = price.getLastPrice();
            sb.append(lastPrice.getPriceFormatter().format(lastPrice));

            // symbol
            sb.append(",");
            String symbol = price.getStockSymbol();
            if (symbol == null) {
                symbol = price.getStockName();
            }
            if (symbol == null) {
                symbol = "";
            }
//            sb.append("\"");
            sb.append(symbol);
//            sb.append("\"");

            writer.println(sb.toString());
        }
    }

}
