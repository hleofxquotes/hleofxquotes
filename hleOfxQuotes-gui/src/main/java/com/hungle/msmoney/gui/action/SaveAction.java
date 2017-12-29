package com.hungle.msmoney.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.hungle.msmoney.gui.qs.YahooQuoteSourcePanel;

public class SaveAction extends AbstractAction {

    private YahooQuoteSourcePanel yahooQuoteSourcePanel;

    public SaveAction(YahooQuoteSourcePanel yahooQuoteSourcePanel, String name) {
        super(name);
        this.yahooQuoteSourcePanel = yahooQuoteSourcePanel;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        yahooQuoteSourcePanel.storeStockSymbols();
    }

}