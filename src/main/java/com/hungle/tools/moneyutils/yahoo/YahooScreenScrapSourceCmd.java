package com.le.tools.moneyutils.yahoo;

import java.io.IOException;

import org.apache.log4j.Logger;

public class YahooScreenScrapSourceCmd {
    private static final Logger log = Logger.getLogger(YahooScreenScrapSourceCmd.class);

    /**
     * @param args
     */
    public static void main(String[] args) {
        YahooScreenScrapSource screenScrapper = new YahooScreenScrapSource();
        String[] symbols = { "CSCO110128C00017000", "CSCO", "AAPL", "123", "A", "B", "C", "IBM" };
        // String[] symbols = { "CSCO110128C00017000" };
        try {
            if (log.isDebugEnabled()) {
                log.debug("Getting price for symbols=" + symbols);
            }
            for (String symbol : symbols) {
                String price = screenScrapper.getPrice(symbol);
                log.info(symbol + ": " + price);
            }
        } catch (IOException e) {
            log.error(e, e);
        } finally {
            log.info("< DONE");
        }
    }

}
