package com.hungle.tools.moneyutils.bloomberg;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;

import org.apache.log4j.Logger;

import com.hungle.tools.moneyutils.ofx.quotes.GUI;
import com.hungle.tools.moneyutils.stockprice.AbstractStockPrice;
import com.hungle.tools.moneyutils.stockprice.Price;
import com.hungle.tools.moneyutils.stockprice.StockPrice;
import com.hungle.tools.moneyutils.yahoo.YahooApiQuoteSourcePanel;

// TODO: Auto-generated Javadoc
/**
 * The Class BloombergQuoteSourcePanel.
 */
public class BloombergQuoteSourcePanel extends YahooApiQuoteSourcePanel {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The Constant log. */
    private static final Logger log = Logger.getLogger(BloombergQuoteSourcePanel.class);

    /**
     * Instantiates a new bloomberg quote source panel.
     *
     * @param gui the gui
     */
    public BloombergQuoteSourcePanel(GUI gui) {
        super(gui, "bloombergStockSymbols");
    }

    /* (non-Javadoc)
     * @see com.le.tools.moneyutils.yahoo.YahooApiQuoteSourcePanel#addPrice(java.lang.String, java.util.List)
     */
    @Override
    protected void addPrice(String symbol, List<AbstractStockPrice> stockPrices) throws IOException {
        BloombergScreenScrapSource scrapper = new BloombergScreenScrapSource();
        scrapper.query(symbol);
        String price = scrapper.getPrice();
        if (price == null) {
            throw new IOException("Cannot get price for symbol=" + symbol);
        }
        AbstractStockPrice bean = new StockPrice();
        bean.setStockSymbol(symbol);
        bean.setStockName(symbol);
        String currency = scrapper.getCurrency();
        log.info(symbol + ", " + price + ", " + currency);
        if (currency != null) {
            bean.setCurrency(currency);
        }
        
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
            if (currency != null) {
                lastPrice.setCurrency(currency);
            }
            bean.setLastPrice(lastPrice);

            // TODO: add trade date

            stockPrices.add(bean);
        } catch (ParseException e) {
            throw new IOException(e);
        }
    }

}