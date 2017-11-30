package com.hungle.tools.moneyutils.ft;

import org.apache.log4j.Logger;

import com.hungle.tools.moneyutils.gui.GUI;
import com.hungle.tools.moneyutils.ofx.quotes.net.AbstractHttpQuoteGetter;
import com.hungle.tools.moneyutils.yahoo.YahooQuoteSourcePanel;

public class FtEquitiesSourcePanel extends YahooQuoteSourcePanel {
    private static final Logger LOGGER = Logger.getLogger(FtEquitiesSourcePanel.class);

    private static final String STOCK_SYMBOLS_PREF_KEY = "ftEquitiesSourcePanel";


    @Override
    protected AbstractHttpQuoteGetter getHttpQuoteGetter() {
        AbstractHttpQuoteGetter getter = new FtEquitiesQuoteGetter();

        return getter;
    }

    public FtEquitiesSourcePanel(GUI gui) {
        super(gui, STOCK_SYMBOLS_PREF_KEY);
    }

}
