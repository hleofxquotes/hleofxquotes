package com.hungle.tools.moneyutils.ft;

import org.apache.log4j.Logger;

import com.hungle.tools.moneyutils.gui.GUI;
import com.hungle.tools.moneyutils.ofx.quotes.net.AbstractHttpQuoteGetter;
import com.hungle.tools.moneyutils.yahoo.YahooQuoteSourcePanel;

public class FtFundsSourcePanel extends YahooQuoteSourcePanel {
    private static final Logger LOGGER = Logger.getLogger(FtFundsSourcePanel.class);

    private static final String STOCK_SYMBOLS_PREF_KEY = "ftFundsSourcePanel";


    @Override
    protected AbstractHttpQuoteGetter getHttpQuoteGetter() {
        AbstractHttpQuoteGetter getter = new FtFundsQuoteGetter();

        return getter;
    }

    public FtFundsSourcePanel(GUI gui) {
        super(gui, STOCK_SYMBOLS_PREF_KEY);
    }

}
