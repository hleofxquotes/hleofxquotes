package com.hungle.tools.moneyutils.bloomberg;

import java.io.IOException;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class BloombergScreenScrapSourceCmd.
 */
public class BloombergScreenScrapSourceCmd {
    
    /** The Constant log. */
    private static final Logger log = Logger.getLogger(BloombergScreenScrapSourceCmd.class);

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        BloombergScreenScrapSource screenScrapper = new BloombergScreenScrapSource();
        String[] symbols = { "AAPL:US" };
        try {
            if (log.isDebugEnabled()) {
                log.debug("Getting price for symbols=" + symbols);
            }
            for (String symbol : symbols) {
                screenScrapper.query(symbol);
                String price = screenScrapper.getPrice();
                String currency = screenScrapper.getCurrency();
                log.info(symbol + "," + price + "," + currency);
            }
        } catch (IOException e) {
            log.error(e, e);
        } finally {
            log.info("< DONE");
        }
    }

}
