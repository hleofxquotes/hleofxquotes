package com.le.tools.moneyutils.misc;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.le.tools.moneyutils.ofx.quotes.OfxUtils;
import com.le.tools.moneyutils.ofx.xmlbeans.OfxPriceInfo;
import com.le.tools.moneyutils.stockprice.AbstractStockPrice;
import com.le.tools.moneyutils.yahoo.GetYahooQuotes;

public class CreateExample {
    private static final Logger log = Logger.getLogger(CreateExample.class);

    /**
     * @param args
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
                log.warn(e);
                stockNames = new ArrayList<String>();
                stockNames.add("CRM");
            }
        }

        File outFile = new File(args[0]);
        log.info("outFile=" + outFile);

        GetYahooQuotes quoteGetter = new GetYahooQuotes();
        try {
            List<AbstractStockPrice> stockPrices = quoteGetter.getQuotes(stockNames);
            double stockPriceOffset = 1000.00;
            OfxPriceInfo ofxPriceInfo = new OfxPriceInfo(stockPrices, stockPriceOffset);
            ofxPriceInfo.save(outFile);
        } catch (IOException e) {
            log.warn(e);
        } catch (URISyntaxException e) {
            log.warn(e);
        } finally {
            if (quoteGetter != null) {
                quoteGetter.shutdown();
            }
            log.info("< DONE, file=" + outFile);
        }
    }
}
