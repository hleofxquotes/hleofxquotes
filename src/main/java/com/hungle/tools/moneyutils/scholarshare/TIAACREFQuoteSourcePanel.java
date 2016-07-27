package com.le.tools.moneyutils.scholarshare;

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

import com.le.tools.moneyutils.ofx.quotes.GUI;
import com.le.tools.moneyutils.ofx.xmlbeans.OfxPriceInfo;
import com.le.tools.moneyutils.stockprice.AbstractStockPrice;
import com.le.tools.moneyutils.stockprice.Price;
import com.le.tools.moneyutils.stockprice.StockPrice;
import com.le.tools.moneyutils.yahoo.YahooApiQuoteSourcePanel;

public class TIAACREFQuoteSourcePanel extends YahooApiQuoteSourcePanel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(TIAACREFQuoteSourcePanel.class);
    private TIAACREFScreenScrapSource scrapper;

    // December 9, 2011
    private SimpleDateFormat dateFormatter = new SimpleDateFormat(OfxPriceInfo.DEFAULT_LAST_TRADE_DATE_PATTERN);

    public TIAACREFQuoteSourcePanel(GUI gui) {
        super(gui, "TIAACREFStockSymbols");
    }

    @Override
    protected void addPrice(String symbol, List<AbstractStockPrice> stockPrices) throws IOException {
        String price = scrapper.getPrice(symbol);
        if (price == null) {
            throw new IOException("Cannot get price for symbol=" + symbol);
        }
        StockPrice bean = new StockPrice();
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
                    log.error(e, e);
                } catch (XPathExpressionException e) {
                    log.error(e, e);
                } finally {
                    log.info("< DONE");
                }
            }
        });
        menu.add(menuItem);
        popup.add(menu);

        return popup;
    }

}