package com.hungle.msmoney.qs.yahoo;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.hungle.msmoney.core.stockprice.AbstractStockPrice;
import com.hungle.msmoney.qs.net.AbstractHttpQuoteGetter;
import com.hungle.msmoney.qs.yahoo.YahooQuotesGetter;


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
	@Ignore
	public void testGet() throws ClientProtocolException, URISyntaxException, IOException {
		AbstractHttpQuoteGetter quoteGetter = new YahooQuotesGetter();
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
