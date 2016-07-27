package com.le.tools.moneyutils.yahoo;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.le.tools.moneyutils.ofx.quotes.GUI;
import com.le.tools.moneyutils.stockprice.AbstractStockPrice;

public class YahooHistoricalSourcePanel extends YahooQuoteSourcePanel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(YahooHistoricalSourcePanel.class);

    public YahooHistoricalSourcePanel(GUI gui, String stockSymbolsPrefKey) {
        super(gui, stockSymbolsPrefKey);
        getQuoteSource().setHistoricalQuotes(true);
    }

    @Override
    protected List<AbstractStockPrice> getStockQuotes(final List<String> stockSymbols) throws IOException {
        String stockSymbol = stockSymbols.get(0);

        if (stockSymbols.size() > 1) {
            JOptionPane.showMessageDialog(this, "You specify multiple symbols." + "\n" + "Only the first symbol \"" + stockSymbol
                    + "\" will be used to retrieve historical quotes.");
        }

        List<AbstractStockPrice> stockPrices = null;
        GetHistoricalQuotesDialog dialog = new GetHistoricalQuotesDialog(stockSymbol);
        dialog.showDialog(this);
        if (dialog.isCanceled()) {
            log.warn("Canceled GetHistoricalQuotesDialog");
            stockPrices = new ArrayList<AbstractStockPrice>();
            return stockPrices;
        }
        Date fromDate = dialog.getFromDate();
        Date toDate = dialog.getToDate();
        log.info("user selected, fromDate=" + fromDate);
        log.info("user selected, toDate=" + toDate);
        log.info("user selected, limitToFriday=" + dialog.getLimitToFriday());
        log.info("user selected, limitToEOM=" + dialog.getLimitToEOM());
        
        GetYahooQuotes quoteGetter = new GetYahooHistoricalQuotes(fromDate, toDate, dialog.getLimitToFriday(), dialog.getLimitToEOM());
        if (quoteServer != null) {
            quoteGetter.setHost(quoteServer);
        }
        try {
            stockPrices = quoteGetter.getQuotes(stockSymbols, listener);
            this.fxSymbols = quoteGetter.getFxSymbols();
        } catch (URISyntaxException e) {
            throw new IOException(e);
        } finally {
        }
        return stockPrices;
    }
}