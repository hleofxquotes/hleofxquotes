package com.hungle.tools.moneyutils.yahoo;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.hungle.tools.moneyutils.ofx.quotes.GUI;
import com.hungle.tools.moneyutils.ofx.quotes.StopWatch;
import com.hungle.tools.moneyutils.stockprice.AbstractStockPrice;
import com.hungle.tools.moneyutils.stockprice.Price;
import com.hungle.tools.moneyutils.stockprice.StockPrice;

// TODO: Auto-generated Javadoc
/**
 * The Class YahooApiQuoteSourcePanel.
 */
public class YahooApiQuoteSourcePanel extends YahooQuoteSourcePanel {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The Constant DEFAULT_RETRIES. */
    private static final int DEFAULT_RETRIES = 3;

    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(YahooApiQuoteSourcePanel.class);

    /** The decimal locale. */
    private Locale decimalLocale = null;

    /** The retries. */
    private int retries = DEFAULT_RETRIES;

    /**
     * Instantiates a new yahoo api quote source panel.
     *
     * @param gui the gui
     */
    public YahooApiQuoteSourcePanel(GUI gui) {
        super(gui, "yahooApiStockSymbols");
    }

    /**
     * Instantiates a new yahoo api quote source panel.
     *
     * @param gui the gui
     * @param stockSymbolsPrefKey the stock symbols pref key
     */
    public YahooApiQuoteSourcePanel(GUI gui, String stockSymbolsPrefKey) {
        super(gui, stockSymbolsPrefKey);
    }

    /* (non-Javadoc)
     * @see com.hungle.tools.moneyutils.yahoo.YahooQuoteSourcePanel#getStockQuotes(java.util.List)
     */
    @Override
    protected List<AbstractStockPrice> getStockQuotes(final List<String> stockSymbols) throws IOException {
        LOGGER.info("> getStockQuotes");

        List<AbstractStockPrice> stockPrices = new ArrayList<AbstractStockPrice>();
        try {
            if (listener != null) {
                listener.setSubTaskSize(stockSymbols.size());
            }
            for (String symbol : stockSymbols) {
                StopWatch stopWatch = new StopWatch();
                if (listener != null) {
                    listener.started(stockSymbols);
                }
                try {
                    for (int i = 0; i < retries; i++) {
                        try {
                            addPrice(symbol, stockPrices);
                            break;
                        } catch (IOException e) {
                            LOGGER.warn("Cannot get price for symbol=" + symbol + ", attempt " + i + "/" + retries);
                        }
                    }
                } finally {
                    long delta = stopWatch.click();
                    if (listener != null) {
                        listener.ended(stockSymbols, stockPrices, delta);
                    }
                }
            }
        } finally {
            //
        }
        return stockPrices;
    }

    /**
     * Adds the price.
     *
     * @param symbol the symbol
     * @param stockPrices the stock prices
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected void addPrice(String symbol, List<AbstractStockPrice> stockPrices) throws IOException {
        YahooScreenScrapSource scrapper = new YahooScreenScrapSource();
        String price = scrapper.getPrice(symbol);
        if (price == null) {
            LOGGER.warn("Cannot get price for symbol=" + symbol);
            return;
        }
        AbstractStockPrice bean = new StockPrice();
        bean.setStockSymbol(symbol);
        bean.setStockName(symbol);

        NumberFormat formatter = null;
        if (getDecimalLocale() != null) {
            formatter = NumberFormat.getNumberInstance(getDecimalLocale());
        } else {
            formatter = NumberFormat.getNumberInstance();
        }
        Number number = null;
        try {
            number = formatter.parse(price);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("price number=" + number);
            }
            Price lastPrice = new Price(number.doubleValue());
            bean.setLastPrice(lastPrice);

            // TODO: add trade date

            stockPrices.add(bean);
        } catch (ParseException e) {
            throw new IOException(e);
        }
    }

    /**
     * Gets the decimal locale.
     *
     * @return the decimal locale
     */
    public Locale getDecimalLocale() {
        return decimalLocale;
    }

    /**
     * Sets the decimal locale.
     *
     * @param decimalLocale the new decimal locale
     */
    public void setDecimalLocale(Locale decimalLocale) {
        this.decimalLocale = decimalLocale;
    }
}
