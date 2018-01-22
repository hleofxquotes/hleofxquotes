package com.hungle.msmoney.qs.yahoo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.hungle.msmoney.core.fx.FxTable;
import com.hungle.msmoney.core.fx.FxTableUtils;
import com.hungle.msmoney.core.mapper.SymbolMapper;
import com.hungle.msmoney.core.ofx.xmlbeans.OfxPriceInfo;
import com.hungle.msmoney.core.ofx.xmlbeans.OfxSaveParameter;
import com.hungle.msmoney.core.stockprice.AbstractStockPrice;

// TODO: Auto-generated Javadoc
/**
 * The Class GetYahooHistoricalQuotesCmd.
 */
public class GetYahooHistQuotesCmd {
    
    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(GetYahooHistQuotesCmd.class);

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        YahooHistQuoteGetter getter = null;

        try {
            getter = new YahooHistQuoteGetter();
            List<String> stocks = new ArrayList<String>();
            stocks.add("MSFT");
            LOGGER.info("stocks=" + stocks);
            List<AbstractStockPrice> stockPrices = getter.getQuotes(stocks);
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
            if (getter != null) {
                getter.shutdown();
                try {
                    getter.close();
                } catch (IOException e) {
                    LOGGER.warn(e);
                } finally {
                    getter = null;
                }
            }
            LOGGER.info("< DONE");
        }

    }

}
