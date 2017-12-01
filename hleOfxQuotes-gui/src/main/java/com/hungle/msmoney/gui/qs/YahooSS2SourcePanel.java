package com.hungle.msmoney.gui.qs;

import org.apache.log4j.Logger;

import com.hungle.msmoney.gui.GUI;
import com.hungle.msmoney.qs.net.AbstractHttpQuoteGetter;
import com.hungle.msmoney.qs.yahoo.YahooSS2QuoteGetter;

public class YahooSS2SourcePanel extends YahooQuoteSourcePanel {
    private static final Logger LOGGER = Logger.getLogger(YahooSS2SourcePanel.class);

    private static final String STOCK_SYMBOLS_PREF_KEY = "yahooScreenScrapper2";

    @Override
    protected AbstractHttpQuoteGetter getHttpQuoteGetter() {
        AbstractHttpQuoteGetter getter = new YahooSS2QuoteGetter();

        return getter;
    }

    public YahooSS2SourcePanel(GUI gui) {
        super(gui, STOCK_SYMBOLS_PREF_KEY);
    }

}
