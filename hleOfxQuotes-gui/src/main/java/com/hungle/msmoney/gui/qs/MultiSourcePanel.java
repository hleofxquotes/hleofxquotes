package com.hungle.msmoney.gui.qs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.hungle.msmoney.core.ofx.OfxUtils;
import com.hungle.msmoney.core.stockprice.AbstractStockPrice;
import com.hungle.msmoney.gui.GUI;
import com.hungle.msmoney.qs.multi.MultiSourcesQuoteGetter;
import com.hungle.msmoney.qs.net.AbstractHttpQuoteGetter;

public class MultiSourcePanel extends YahooQuoteSourcePanel {
    private static final Logger LOGGER = Logger.getLogger(MultiSourcePanel.class);

    private static final String STOCK_SYMBOLS_PREF_KEY = "multiSourcePanel";

    public MultiSourcePanel(GUI gui) {
        super(gui, STOCK_SYMBOLS_PREF_KEY);
    }

    @Override
    protected List<AbstractStockPrice> getStockQuotes(List<String> qsNames) throws IOException {
        List<AbstractStockPrice> stockPrices = new ArrayList<AbstractStockPrice>();

        MultiSourcesQuoteGetter quoteGetter = null;

        try {
            quoteGetter = new MultiSourcesQuoteGetter();
            setQuoteSourcesSymbols(qsNames, quoteGetter);

            stockPrices = quoteGetter.getQuotes(qsNames);
            this.setFxSymbols(quoteGetter.getFxSymbols());
            this.setNotFoundSymbols(quoteGetter.getNotFoundSymbols());
        } finally {
            if (quoteGetter != null) {
                quoteGetter.close();
            }
        }

        return stockPrices;
    }

    private void setQuoteSourcesSymbols(List<String> qsNames, MultiSourcesQuoteGetter getter) {
        for (String qsName : qsNames) {
            String key = getter.normalizeQsName(qsName);

            String prefKey = null;
            if (key.compareTo(MultiSourcesQuoteGetter.YAHOO2_QUOTE_SOURCE_NAME) == 0) {
                prefKey = YahooSS2SourcePanel.STOCK_SYMBOLS_PREF_KEY;
            } else if (key.compareTo(MultiSourcesQuoteGetter.FT_QUOTE_SOURCE_NAME) == 0) {
                prefKey = FtEquitiesSourcePanel.STOCK_SYMBOLS_PREF_KEY;
            } else {
                prefKey = null;
            }
            String stocksString = OfxUtils.retrieveStockSymbols(GUI.getPrefs(), prefKey);
            LOGGER.info("stocksString=" + stocksString);
            try {
                List<String> stockSymbols = QuoteSourceUtils.toStockSymbols(stocksString);
                getter.setSymbols(key, stockSymbols);
            } catch (IOException e) {
                LOGGER.warn(e);
            }
        }
    }

    @Override
    protected AbstractHttpQuoteGetter getHttpQuoteGetter() {
        throw new UnsupportedOperationException("getHttpQuoteGetter is not implemented.");
    }

    @Override
    protected String getTitle() {
        return "Quote Sources";
    }

    @Override
    protected String getNoStockRequestErrorMessage() {
        return "Please enter Quote Source names.";
    }

}
