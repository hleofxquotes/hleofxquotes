package com.hungle.tools.moneyutils.yahoo;

import java.io.IOException;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class YahooScreenScrapSourceCmd.
 */
public class YahooScreenScrapSourceCmd {
    
    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(YahooScreenScrapSourceCmd.class);

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        YahooScreenScrapSource screenScrapper = new YahooScreenScrapSource();
        String[] symbols = { "CSCO110128C00017000", "CSCO", "AAPL", "123", "A", "B", "C", "IBM" };
        // String[] symbols = { "CSCO110128C00017000" };
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Getting price for symbols=" + symbols);
            }
            for (String symbol : symbols) {
                String price = screenScrapper.getPrice(symbol);
                LOGGER.info(symbol + ": " + price);
            }
        } catch (IOException e) {
            LOGGER.error(e, e);
        } finally {
            LOGGER.info("< DONE");
        }
    }

}
