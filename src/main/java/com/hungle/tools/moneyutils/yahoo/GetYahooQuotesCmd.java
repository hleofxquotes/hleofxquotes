package com.hungle.tools.moneyutils.yahoo;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.hungle.tools.moneyutils.ofx.xmlbeans.OfxPriceInfo;
import com.hungle.tools.moneyutils.stockprice.AbstractStockPrice;

// TODO: Auto-generated Javadoc
/**
 * The Class GetYahooQuotesCmd.
 */
public class GetYahooQuotesCmd {
    
    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(GetYahooQuotesCmd.class);

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        final Map<String, String> sourceHostNames = GetYahooQuotes.QUOTE_HOSTS;

        String sourceHostName = null;
        sourceHostName = sourceHostNames.get("US");
        LOGGER.info("sourceHostName=" + sourceHostName);

        List<String> stockNames = new ArrayList<String>();
        stockNames.add("USDARS=X");
        stockNames.add("IBM");

        File outFile = new File("quotes.xml");
        LOGGER.info("outFile=" + outFile.getAbsolutePath());

        GetYahooQuotes quoteGetter = new GetYahooQuotes();
        quoteGetter.setHost(sourceHostName);
        try {
            List<AbstractStockPrice> stockPrices = quoteGetter.getQuotes(stockNames);

            OfxPriceInfo.save(stockPrices, outFile);
        } catch (IOException e) {
            LOGGER.error(e, e);
        } catch (URISyntaxException e) {
            LOGGER.error(e, e);
        } finally {
            quoteGetter.shutdown();
            LOGGER.info("< DONE");
        }
    }

}
