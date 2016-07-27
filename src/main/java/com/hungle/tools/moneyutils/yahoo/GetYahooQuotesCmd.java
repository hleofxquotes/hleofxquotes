package com.le.tools.moneyutils.yahoo;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.le.tools.moneyutils.data.SymbolMapper;
import com.le.tools.moneyutils.ofx.quotes.FxTable;
import com.le.tools.moneyutils.ofx.xmlbeans.CurrencyUtils;
import com.le.tools.moneyutils.ofx.xmlbeans.OfxPriceInfo;
import com.le.tools.moneyutils.ofx.xmlbeans.OfxSaveParameter;
import com.le.tools.moneyutils.stockprice.AbstractStockPrice;

public class GetYahooQuotesCmd {
    private static final Logger log = Logger.getLogger(GetYahooQuotesCmd.class);

    /**
     * @param args
     */
    public static void main(String[] args) {
        Map<String, String> sourceHostNames = GetYahooQuotes.QUOTE_HOSTS;

        String sourceHostName = null;
        sourceHostName = sourceHostNames.get("US");
        log.info("sourceHostName=" + sourceHostName);

        List<String> stockNames = new ArrayList<String>();
        stockNames.add("USDARS=X");
        stockNames.add("IBM");

        File outFile = new File("quotes.xml");
        log.info("outFile=" + outFile.getAbsolutePath());

        GetYahooQuotes quoteGetter = new GetYahooQuotes();
        quoteGetter.setHost(sourceHostName);
        try {
            List<AbstractStockPrice> stockPrices = quoteGetter.getQuotes(stockNames);

            String defaultCurrency = CurrencyUtils.getDefaultCurrency();
            boolean forceGeneratingINVTRANLIST = false;

            OfxSaveParameter params = new OfxSaveParameter();
            params.setDefaultCurrency(defaultCurrency);
            params.setForceGeneratingINVTRANLIST(forceGeneratingINVTRANLIST);

            SymbolMapper symbolMapper = new SymbolMapper();

            FxTable fxTable = new FxTable();

            OfxPriceInfo.save(stockPrices, outFile, params, symbolMapper, fxTable);
        } catch (IOException e) {
            log.error(e, e);
        } catch (URISyntaxException e) {
            log.error(e, e);
        } finally {
            quoteGetter.shutdown();
            log.info("< DONE");
        }
    }

}
