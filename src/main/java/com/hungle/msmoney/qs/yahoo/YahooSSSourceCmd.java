package com.hungle.msmoney.qs.yahoo;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.hungle.msmoney.core.ofx.AbstractScreenScrapSource;
import com.hungle.msmoney.qs.scholarshare.TIAACREFPriceInfo;

// TODO: Auto-generated Javadoc
/**
 * The Class YahooScreenScrapSourceCmd.
 */
public class YahooSSSourceCmd {

    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(YahooSSSourceCmd.class);

    /**
     * The main method.
     *
     * @param args
     *            the arguments
     */
    public static void main(String[] args) {
//        String[] symbols = { "CSCO110128C00017000", "CSCO", "AAPL", "123", "A", "B", "C", "IBM" };
        // String[] symbols = { "CSCO110128C00017000" };
        String[] symbols = { "CSCO" };
        AbstractScreenScrapSource<TIAACREFPriceInfo> screenScrapper = new YahooSSSource(Arrays.asList(symbols));
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Getting price for symbols:");
                for(String symbol : symbols) {
                    LOGGER.debug("  symbol=" + symbol);
                }
            }
            List<TIAACREFPriceInfo> prices = screenScrapper.scrap();
            for (TIAACREFPriceInfo price : prices) {
                LOGGER.info(price);
            }
        } finally {
            LOGGER.info("< DONE");
        }
    }

}
