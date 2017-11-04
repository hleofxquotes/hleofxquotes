package com.hungle.tools.moneyutils.misc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.hungle.tools.moneyutils.ofx.quotes.OfxUtils;
import com.hungle.tools.moneyutils.ofx.quotes.net.AbstractHttpQuoteGetter;
import com.hungle.tools.moneyutils.ofx.xmlbeans.OfxPriceInfo;
import com.hungle.tools.moneyutils.stockprice.AbstractStockPrice;
import com.hungle.tools.moneyutils.yahoo.YahooQuotesGetter;

// TODO: Auto-generated Javadoc
/**
 * The Class CreateExample.
 */
public class CreateExample {
    
    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(CreateExample.class);

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        List<String> stockNames = null;

        if (args.length < 1) {
            Class<CreateExample> clz = CreateExample.class;
            System.out.println("Usage: java " + clz.getName() + " out.ofx stock1 ... stockN");
            System.exit(1);
        } else {
            stockNames = new ArrayList<String>();
            for (int i = 1; i < args.length; i++) {
                stockNames.add(args[i]);
            }
        }
        if (stockNames.size() <= 0) {
            try {
                stockNames = OfxUtils.getList("dotd.txt");
            } catch (IOException e) {
                LOGGER.warn(e);
                stockNames = new ArrayList<String>();
                stockNames.add("CRM");
            }
        }

        File outFile = new File(args[0]);
        LOGGER.info("outFile=" + outFile);

        AbstractHttpQuoteGetter quoteGetter = new YahooQuotesGetter();
        try {
            List<AbstractStockPrice> stockPrices = quoteGetter.getQuotes(stockNames);
            double stockPriceOffset = 1000.00;
            OfxPriceInfo ofxPriceInfo = new OfxPriceInfo(stockPrices, stockPriceOffset);
            ofxPriceInfo.save(outFile);
        } catch (IOException e) {
            LOGGER.warn(e);
        } finally {
            if (quoteGetter != null) {
                quoteGetter.shutdown();
            }
            LOGGER.info("< DONE, file=" + outFile);
        }
    }
}
