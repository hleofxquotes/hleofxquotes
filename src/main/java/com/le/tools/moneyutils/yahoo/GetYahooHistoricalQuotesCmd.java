package com.le.tools.moneyutils.yahoo;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;

import com.le.tools.moneyutils.data.SymbolMapper;
import com.le.tools.moneyutils.ofx.quotes.FxTable;
import com.le.tools.moneyutils.ofx.xmlbeans.OfxPriceInfo;
import com.le.tools.moneyutils.ofx.xmlbeans.OfxSaveParameter;
import com.le.tools.moneyutils.stockprice.AbstractStockPrice;

public class GetYahooHistoricalQuotesCmd {
    private static final Logger log = Logger.getLogger(GetYahooHistoricalQuotesCmd.class);

    /**
     * @param args
     */
    public static void main(String[] args) {
        GetYahooHistoricalQuotes getQuotes = null;

        try {
            getQuotes = new GetYahooHistoricalQuotes();
            List<String> stocks = new ArrayList<String>();
            stocks.add("MSFT");
            log.info("stocks=" + stocks);
            List<AbstractStockPrice> stockPrices = getQuotes.getQuotes(stocks);
            int i = 0;
            for (AbstractStockPrice stockPrice : stockPrices) {
                List<AbstractStockPrice> prices = new ArrayList<AbstractStockPrice>();
                prices.add(stockPrice);
                File outputFile = new File("target/" + i + ".ofx");
                SymbolMapper symbolMapper = new SymbolMapper();
                FxTable fxTable = new FxTable();
                OfxSaveParameter params = new OfxSaveParameter();
                OfxPriceInfo.save(prices, outputFile, params, symbolMapper, fxTable);
                i++;
            }
        } catch (ClientProtocolException e) {
            log.error(e, e);
        } catch (URISyntaxException e) {
            log.error(e, e);
        } catch (IOException e) {
            log.error(e, e);
        } finally {
            if (getQuotes != null) {
                getQuotes.shutdown();
                getQuotes = null;
            }
            log.info("< DONE");
        }

    }

}
