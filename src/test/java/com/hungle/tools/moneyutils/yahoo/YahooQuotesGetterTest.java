package com.hungle.tools.moneyutils.yahoo;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.junit.Test;

import com.hungle.tools.moneyutils.stockprice.AbstractStockPrice;

import junit.framework.Assert;

// TODO: Auto-generated Javadoc
/**
 * The Class YahooQuotesGetterTest.
 */
public class YahooQuotesGetterTest {
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(YahooQuotesGetterTest.class);

	/**
	 * Test get.
	 *
	 * @throws ClientProtocolException the client protocol exception
	 * @throws URISyntaxException the URI syntax exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void testGet() throws ClientProtocolException, URISyntaxException, IOException {
		YahooQuotesGetter quoteGetter = new YahooQuotesGetter();
		try {
			List<String> stockNames = new ArrayList<String>();
			List<AbstractStockPrice> stockPrices = null;

			stockNames.clear();
			stockPrices = quoteGetter.getQuotes(stockNames);
			Assert.assertNotNull(stockPrices);

			stockNames.clear();
			stockNames.add("IBM");
			stockPrices = quoteGetter.getQuotes(stockNames);
			Assert.assertNotNull(stockPrices);
			Assert.assertEquals(1, stockPrices.size());

			stockNames.clear();
			stockNames.add("IBM");
			stockNames.add("IBM");
			stockNames.add("IBM");
			stockPrices = quoteGetter.getQuotes(stockNames);
			Assert.assertNotNull(stockPrices);
			Assert.assertEquals(3, stockPrices.size());
		} finally {
			quoteGetter.shutdown();
			LOGGER.info("< DONE");
		}
	}
}
