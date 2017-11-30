package com.hungle.msmoney.qs.yahoo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.hungle.msmoney.core.data.SymbolMapper;
import com.hungle.msmoney.core.fx.FxTable;
import com.hungle.msmoney.core.fx.FxTableUtils;
import com.hungle.msmoney.core.ofx.xmlbeans.OfxPriceInfo;
import com.hungle.msmoney.core.ofx.xmlbeans.OfxSaveParameter;
import com.hungle.msmoney.core.stockprice.AbstractStockPrice;

// TODO: Auto-generated Javadoc
/**
 * The Class GetYahooHistoricalQuotesCmd.
 */
public class GetYahooHistoricalQuotesCmd {
    
    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(GetYahooHistoricalQuotesCmd.class);

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        GetYahooHistoricalQuotes getQuotes = null;

        try {
            getQuotes = new GetYahooHistoricalQuotes();
            List<String> stocks = new ArrayList<String>();
            stocks.add("MSFT");
            LOGGER.info("stocks=" + stocks);
            List<AbstractStockPrice> stockPrices = getQuotes.getQuotes(stocks);
            int i = 0;
            for (AbstractStockPrice stockPrice : stockPrices) {
                List<AbstractStockPrice> prices = new ArrayList<AbstractStockPrice>();
                prices.add(stockPrice);
                File outputFile = new File("target/" + i + ".ofx");
                SymbolMapper symbolMapper = SymbolMapper.loadMapperFile();
                FxTable fxTable = FxTableUtils.loadFxFile();
                OfxSaveParameter params = new OfxSaveParameter();
                OfxPriceInfo.save(prices, outputFile, params, symbolMapper, fxTable);
                i++;
            }
        } catch (IOException e) {
            LOGGER.error(e, e);
        } finally {
            if (getQuotes != null) {
                getQuotes.shutdown();
                getQuotes = null;
            }
            LOGGER.info("< DONE");
        }

    }

}
