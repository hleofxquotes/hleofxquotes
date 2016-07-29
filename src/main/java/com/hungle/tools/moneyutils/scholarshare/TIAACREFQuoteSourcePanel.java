package com.hungle.tools.moneyutils.scholarshare;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;

import com.hungle.tools.moneyutils.ofx.quotes.GUI;
import com.hungle.tools.moneyutils.ofx.xmlbeans.OfxPriceInfo;
import com.hungle.tools.moneyutils.stockprice.AbstractStockPrice;
import com.hungle.tools.moneyutils.stockprice.Price;
import com.hungle.tools.moneyutils.stockprice.StockPrice;
import com.hungle.tools.moneyutils.yahoo.YahooApiQuoteSourcePanel;

// TODO: Auto-generated Javadoc
/**
 * The Class TIAACREFQuoteSourcePanel.
 */
public class TIAACREFQuoteSourcePanel extends YahooApiQuoteSourcePanel {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(TIAACREFQuoteSourcePanel.class);
    
    /** The scrapper. */
    private TIAACREFScreenScrapSource scrapper;

    /** The date formatter. */
    // December 9, 2011
    private SimpleDateFormat dateFormatter = new SimpleDateFormat(OfxPriceInfo.DEFAULT_LAST_TRADE_DATE_PATTERN);

    /**
     * Instantiates a new TIAACREF quote source panel.
     *
     * @param gui the gui
     */
    public TIAACREFQuoteSourcePanel(GUI gui) {
        super(gui, "TIAACREFStockSymbols");
    }

    /* (non-Javadoc)
     * @see com.hungle.tools.moneyutils.yahoo.YahooApiQuoteSourcePanel#addPrice(java.lang.String, java.util.List)
     */
    @Override
    protected void addPrice(String symbol, List<AbstractStockPrice> stockPrices) throws IOException {
        String price = scrapper.getPrice(symbol);
        if (price == null) {
            throw new IOException("Cannot get price for symbol=" + symbol);
        }
        AbstractStockPrice bean = new StockPrice();
        bean.setStockSymbol(symbol);
        bean.setStockName(symbol);
        String currency = scrapper.getCurrency();
        LOGGER.info(symbol + ", " + price + ", " + currency);
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
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("price number=" + number);
            }
            Price lastPrice = new Price(number.doubleValue());
            if (currency != null) {
                lastPrice.setCurrency(currency);
            }
            bean.setLastPrice(lastPrice);

            String stockName = scrapper.getName(symbol);
            bean.setStockName(stockName);

            Date date = scrapper.getDate(symbol);

            if (date != null) {
                bean.setLastTradeDate(dateFormatter.format(date));
            }

            bean.setLastTrade(date);

            stockPrices.add(bean);
        } catch (ParseException e) {
            throw new IOException(e);
        }
    }

    /* (non-Javadoc)
     * @see com.hungle.tools.moneyutils.yahoo.YahooApiQuoteSourcePanel#getStockQuotes(java.util.List)
     */
    @Override
    protected List<AbstractStockPrice> getStockQuotes(final List<String> stockSymbols) throws IOException {
        scrapper = new TIAACREFScreenScrapSource();
        scrapper.query();
        try {
            scrapper.parse();
        } catch (XPathExpressionException e) {
            throw new IOException(e);
        }
        return super.getStockQuotes(stockSymbols);
    }

    /* (non-Javadoc)
     * @see com.hungle.tools.moneyutils.yahoo.YahooQuoteSourcePanel#addPopupMenu(javax.swing.JTextArea)
     */
    @Override
    protected JPopupMenu addPopupMenu(JTextArea textArea) {
        JPopupMenu popup = super.addPopupMenu(textArea);
        popup.addSeparator();

        JMenu menu = null;
        JMenuItem menuItem = null;

        menu = new JMenu("Scholarshare");
        menuItem = new JMenuItem(new AbstractAction("Show auto-generated symbols") {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent event) {
                TIAACREFScreenScrapSource screenScrapper = new TIAACREFScreenScrapSource();
                try {
                    screenScrapper.query();
                    screenScrapper.parse();
                    List<TIAACREFPriceInfo> prices = screenScrapper.getPrices();
                    for (TIAACREFPriceInfo price : prices) {
                        System.out.println(price);
                    }
                } catch (IOException e) {
                    LOGGER.error(e, e);
                } catch (XPathExpressionException e) {
                    LOGGER.error(e, e);
                } finally {
                    LOGGER.info("< DONE");
                }
            }
        });
        menu.add(menuItem);
        popup.add(menu);

        return popup;
    }

}