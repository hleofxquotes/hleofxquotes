package com.hungle.tools.moneyutils.yahoo;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.hungle.tools.moneyutils.gui.GUI;
import com.hungle.tools.moneyutils.gui.GetHistoricalQuotesDialog;
import com.hungle.tools.moneyutils.stockprice.AbstractStockPrice;

// TODO: Auto-generated Javadoc
/**
 * The Class YahooHistoricalSourcePanel.
 */
public class YahooHistoricalSourcePanel extends YahooQuoteSourcePanel {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(YahooHistoricalSourcePanel.class);

    /** The Constant STOCK_SYMBOLS_PREF_KEY. */
    private static final String STOCK_SYMBOLS_PREF_KEY = "yahooHistoricalStockSymbols";

    /**
     * Instantiates a new yahoo historical source panel.
     *
     * @param gui the gui
     * @param stockSymbolsPrefKey the stock symbols pref key
     */
    public YahooHistoricalSourcePanel(GUI gui, String stockSymbolsPrefKey) {
        super(gui, stockSymbolsPrefKey);
        getQuoteSource().setHistoricalQuotes(true);
    }

    /**
     * Instantiates a new yahoo historical source panel.
     *
     * @param gui the gui
     */
    public YahooHistoricalSourcePanel(GUI gui) {
        this(gui, STOCK_SYMBOLS_PREF_KEY);
    }

    /* (non-Javadoc)
     * @see com.hungle.tools.moneyutils.yahoo.YahooQuoteSourcePanel#getStockQuotes(java.util.List)
     */
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
            LOGGER.warn("Canceled GetHistoricalQuotesDialog");
            stockPrices = new ArrayList<AbstractStockPrice>();
            return stockPrices;
        }
        Date fromDate = dialog.getFromDate();
        Date toDate = dialog.getToDate();
        LOGGER.info("user selected, fromDate=" + fromDate);
        LOGGER.info("user selected, toDate=" + toDate);
        LOGGER.info("user selected, limitToFriday=" + dialog.getLimitToFriday());
        LOGGER.info("user selected, limitToEOM=" + dialog.getLimitToEOM());
        
        YahooQuotesGetter quoteGetter = new GetYahooHistoricalQuotes(fromDate, toDate, dialog.getLimitToFriday(), dialog.getLimitToEOM());
        if (quoteServer != null) {
            quoteGetter.setHost(quoteServer);
        }
        try {
            stockPrices = quoteGetter.getQuotes(stockSymbols, listener);
            this.fxSymbols = quoteGetter.getFxSymbols();
        } finally {
        }
        return stockPrices;
    }
}