package com.hungle.tools.moneyutils.ft;

import org.apache.log4j.Logger;

import com.hungle.tools.moneyutils.gui.GUI;
import com.hungle.tools.moneyutils.ofx.quotes.net.AbstractHttpQuoteGetter;
import com.hungle.tools.moneyutils.yahoo.YahooQuoteSourcePanel;

public class FtEtfsSourcePanel extends YahooQuoteSourcePanel {
    private static final Logger LOGGER = Logger.getLogger(FtEtfsSourcePanel.class);

    private static final String STOCK_SYMBOLS_PREF_KEY = "ftEtfsSourcePanel";

    @Override
    protected AbstractHttpQuoteGetter getHttpQuoteGetter() {
        AbstractHttpQuoteGetter getter = new FtEtfsQuoteGetter();

        return getter;
    }

    public FtEtfsSourcePanel(GUI gui) {
        super(gui, STOCK_SYMBOLS_PREF_KEY);
    }

}
