package com.hungle.tools.moneyutils.yahoo;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.hungle.tools.moneyutils.gui.GUI;
import com.hungle.tools.moneyutils.ofx.quotes.StopWatch;
import com.hungle.tools.moneyutils.stockprice.AbstractStockPrice;
import com.hungle.tools.moneyutils.stockprice.Price;
import com.hungle.tools.moneyutils.stockprice.StockPrice;

public class YahooScreenScrapper2SourcePanel extends YahooApiQuoteSourcePanel {
	private static final Logger LOGGER = Logger.getLogger(YahooScreenScrapper2SourcePanel.class);
	
    /** The last trade date formatter. */
    private SimpleDateFormat lastTradeDateFormatter = new SimpleDateFormat("MM/dd/yyyy");
    
    /** The last trade time formatter. */
    private SimpleDateFormat lastTradeTimeFormatter = new SimpleDateFormat("hh:mm");

	@Override
	protected List<AbstractStockPrice> getStockQuotes(List<String> stockSymbols) throws IOException {
		LOGGER.info("> getStockQuotes");

		List<AbstractStockPrice> stockPrices = new ArrayList<AbstractStockPrice>();

		try {
			if (listener != null) {
				listener.setSubTaskSize(stockSymbols.size());
			}
			YahooScreenScrapper2 scrapper = null;
			StopWatch stopWatch = new StopWatch();
			try {
				if (listener != null) {
					listener.started(stockSymbols);
				}

				scrapper = new YahooScreenScrapper2();
				for (String stockSymbol : stockSymbols) {
					try {
						AbstractStockPrice stockPrice = scrapper.getStockPrice(stockSymbol);
						if (stockPrice == null) {
							LOGGER.warn("Cannot get stock price for symbol=" + stockSymbol);
						} else {
							stockPrices.add(stockPrice);
						}
					} catch (Exception e) {
						LOGGER.warn(e);
					}
				}
			} finally {
				long delta = stopWatch.click();
				if (listener != null) {
					listener.ended(stockSymbols, stockPrices, delta);
				}
				if (scrapper != null) {
					scrapper.close();
					scrapper = null;
				}
			}
		} finally {
			
		}

		return stockPrices;
	}

	private static final String STOCK_SYMBOLS_PREF_KEY = "yahooScreenScrapper2";

	public YahooScreenScrapper2SourcePanel(GUI gui) {
		super(gui, STOCK_SYMBOLS_PREF_KEY);
	}

}
