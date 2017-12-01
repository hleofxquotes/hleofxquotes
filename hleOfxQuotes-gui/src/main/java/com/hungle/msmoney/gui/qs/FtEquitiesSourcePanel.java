package com.hungle.msmoney.gui.qs;

import org.apache.log4j.Logger;

import com.hungle.msmoney.gui.GUI;
import com.hungle.msmoney.qs.ft.FtEquitiesQuoteGetter;
import com.hungle.msmoney.qs.net.AbstractHttpQuoteGetter;

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
