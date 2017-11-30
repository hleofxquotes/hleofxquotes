package com.hungle.msmoney.qs.ft;

import org.apache.log4j.Logger;

import com.hungle.msmoney.gui.GUI;
import com.hungle.msmoney.qs.net.AbstractHttpQuoteGetter;
import com.hungle.msmoney.qs.yahoo.YahooQuoteSourcePanel;

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
