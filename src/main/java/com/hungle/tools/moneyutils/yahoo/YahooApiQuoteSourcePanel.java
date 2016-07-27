package com.le.tools.moneyutils.yahoo;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.le.tools.moneyutils.ofx.quotes.GUI;
import com.le.tools.moneyutils.ofx.quotes.StopWatch;
import com.le.tools.moneyutils.stockprice.AbstractStockPrice;
import com.le.tools.moneyutils.stockprice.Price;
import com.le.tools.moneyutils.stockprice.StockPrice;

public class YahooApiQuoteSourcePanel extends YahooQuoteSourcePanel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static final int DEFAULT_RETRIES = 3;

    private static final Logger log = Logger.getLogger(YahooApiQuoteSourcePanel.class);

    private Locale decimalLocale = null;

    private int retries = DEFAULT_RETRIES;

    public YahooApiQuoteSourcePanel(GUI gui) {
        super(gui, "yahooApiStockSymbols");
    }

    public YahooApiQuoteSourcePanel(GUI gui, String stockSymbolsPrefKey) {
        super(gui, stockSymbolsPrefKey);
    }

    @Override
    protected List<AbstractStockPrice> getStockQuotes(final List<String> stockSymbols) throws IOException {
        log.info("> getStockQuotes");

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
                            log.warn("Cannot get price for symbol=" + symbol + ", attempt " + i + "/" + retries);
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

    protected void addPrice(String symbol, List<AbstractStockPrice> stockPrices) throws IOException {
        YahooScreenScrapSource scrapper = new YahooScreenScrapSource();
        String price = scrapper.getPrice(symbol);
        if (price == null) {
            log.warn("Cannot get price for symbol=" + symbol);
            return;
        }
        StockPrice bean = new StockPrice();
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
            if (log.isDebugEnabled()) {
                log.debug("price number=" + number);
            }
            Price lastPrice = new Price(number.doubleValue());
            bean.setLastPrice(lastPrice);

            // TODO: add trade date

            stockPrices.add(bean);
        } catch (ParseException e) {
            throw new IOException(e);
        }
    }

    public Locale getDecimalLocale() {
        return decimalLocale;
    }

    public void setDecimalLocale(Locale decimalLocale) {
        this.decimalLocale = decimalLocale;
    }
}
