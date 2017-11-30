package com.hungle.msmoney.qs.yahoo;

import org.apache.log4j.Logger;

import com.hungle.msmoney.qs.net.AbstractHttpQuoteGetter;
import com.hungle.tools.moneyutils.gui.GUI;

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
