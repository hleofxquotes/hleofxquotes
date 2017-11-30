package com.hungle.msmoney.qs.bloomberg;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import com.hungle.msmoney.core.stockprice.AbstractStockPrice;
import com.hungle.msmoney.gui.GUI;
import com.hungle.msmoney.qs.yahoo.YahooApiQuoteSourcePanel;

// TODO: Auto-generated Javadoc
/**
 * The Class BloombergQuoteSourcePanel.
 */
public class BloombergQuoteSourcePanel extends YahooApiQuoteSourcePanel {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(BloombergQuoteSourcePanel.class);

    /** The Constant STOCK_SYMBOLS_PREF_KEY. */
    private static final String STOCK_SYMBOLS_PREF_KEY = "bloombergStockSymbols";

    /**
     * Instantiates a new bloomberg quote source panel.
     *
     * @param gui the gui
     */
    public BloombergQuoteSourcePanel(GUI gui) {
        super(gui, STOCK_SYMBOLS_PREF_KEY);
    }

    /* (non-Javadoc)
     * @see com.le.tools.moneyutils.yahoo.YahooApiQuoteSourcePanel#addPrice(java.lang.String, java.util.List)
     */
    @Override
    protected void addPrice(String symbol, List<AbstractStockPrice> stockPrices) throws IOException {
        todo(symbol, stockPrices);
    }

    /**
     * Todo.
     *
     * @param symbol the symbol
     * @param stockPrices the stock prices
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void todo(String symbol, List<AbstractStockPrice> stockPrices) throws IOException {
//        AbstractScreenScrapSource scrapper = new BloombergScreenScrapSource();
//        scrapper.query(symbol);
//        
//        String price = scrapper.getPrice();
//        if (price == null) {
//            throw new IOException("Cannot get price for symbol=" + symbol);
//        }
//        AbstractStockPrice bean = new StockPrice();
//        bean.setStockSymbol(symbol);
//        bean.setStockName(symbol);
//        
//        String currency = scrapper.getCurrency();
//        LOGGER.info(symbol + ", " + price + ", " + currency);
//        if (currency != null) {
//            bean.setCurrency(currency);
//        }
//        
//        NumberFormat formatter = null;
//        if (getDecimalLocale() != null) {
//            formatter = NumberFormat.getNumberInstance(getDecimalLocale());
//        } else {
//            formatter = NumberFormat.getNumberInstance();
//        }
//        Number number = null;
//        try {
//            number = formatter.parse(price);
//            if (LOGGER.isDebugEnabled()) {
//                LOGGER.debug("price number=" + number);
//            }
//            Price lastPrice = new Price(number.doubleValue());
//            if (currency != null) {
//                lastPrice.setCurrency(currency);
//            }
//            bean.setLastPrice(lastPrice);
//
//            // TODO: add trade date
//
//            stockPrices.add(bean);
//        } catch (ParseException e) {
//            throw new IOException(e);
//        }
    }

}