package com.hungle.msmoney.qs.bloomberg;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.hungle.msmoney.qs.scholarshare.TIAACREFPriceInfo;
import com.hungle.tools.moneyutils.ofx.quotes.AbstractScreenScrapSource;

// TODO: Auto-generated Javadoc
/**
 * The Class BloombergScreenScrapSourceCmd.
 */
public class BloombergScreenScrapSourceCmd {

    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(BloombergScreenScrapSourceCmd.class);

    /**
     * The main method.
     *
     * @param args
     *            the arguments
     */
    public static void main(String[] args) {
        String[] symbols = { "AAPL:US" };

        AbstractScreenScrapSource<TIAACREFPriceInfo> screenScrapper = new BloombergScreenScrapSource(
                Arrays.asList(symbols));
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Getting price for symbols:");
                for (String symbol : symbols) {
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
