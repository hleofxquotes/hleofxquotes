package com.hungle.msmoney.gui.qs;

import org.apache.log4j.Logger;

import com.hungle.msmoney.gui.GUI;
import com.hungle.msmoney.qs.ft.FtEtfsQuoteGetter;
import com.hungle.msmoney.qs.net.AbstractHttpQuoteGetter;

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
