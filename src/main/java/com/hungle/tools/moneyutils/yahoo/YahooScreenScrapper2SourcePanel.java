package com.hungle.tools.moneyutils.yahoo;

import org.apache.log4j.Logger;

import com.hungle.tools.moneyutils.gui.GUI;
import com.hungle.tools.moneyutils.ofx.quotes.net.AbstractHttpQuoteGetter;

public class YahooScreenScrapper2SourcePanel extends YahooQuoteSourcePanel {
    private static final Logger LOGGER = Logger.getLogger(YahooScreenScrapper2SourcePanel.class);

    private static final String STOCK_SYMBOLS_PREF_KEY = "yahooScreenScrapper2";

    @Override
    protected AbstractHttpQuoteGetter getHttpQuoteGetter() {
        AbstractHttpQuoteGetter getter = new YahooScreenScrapper2QuoteGetter();

        return getter;
    }

    public YahooScreenScrapper2SourcePanel(GUI gui) {
        super(gui, STOCK_SYMBOLS_PREF_KEY);
    }

}
