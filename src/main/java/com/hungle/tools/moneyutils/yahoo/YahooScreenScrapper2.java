package com.hungle.tools.moneyutils.yahoo;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hungle.tools.moneyutils.ofx.quotes.StopWatch;

public class YahooScreenScrapper2 implements Closeable {
	private static final Logger LOGGER = Logger.getLogger(YahooScreenScrapper2.class);
	
	private CloseableHttpClient client;

	public YahooScreenScrapper2() {
		this.client = HttpClientBuilder.create().build();
	}

	public YahooScreenScrapper2StockInfo getStockInfo(String stockSymbol) throws IOException {
		YahooScreenScrapper2StockInfo stockInfo = null;

		LOGGER.info("> START, " + stockSymbol);

		StopWatch stopWatch = new StopWatch();
		try {
			URI uri = new URL("https://finance.yahoo.com/quote/" + stockSymbol + "?p=" + stockSymbol).toURI();
			HttpGet httpGet = new HttpGet(uri);
			HttpResponse httpResponse = client.execute(httpGet);
			HttpEntity entity = httpResponse.getEntity();
			InputStream stream = entity.getContent();

			String prefix = "root.App.main =";
			String data = null;
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
				String line = null;
				while ((line = reader.readLine()) != null) {
					if (line.startsWith(prefix)) {
						data = line;
						break;
					}
				}
			} finally {
				if (reader != null) {
					reader.close();
					reader = null;
				}
			}

			if (data == null) {
				throw new IOException("Cannot find stockInfo data");
			}

			data = data.substring(prefix.length());
			if (data == null) {
				throw new IOException("Cannot find stockInfo data");
			}

			ObjectMapper mapper = new ObjectMapper();
			JsonNode nodes = mapper.readTree(data);
			if (nodes == null) {
				throw new IOException("Cannot find stockInfo JsonNode");
			}

			String fieldName = "currentPrice";
			List<JsonNode> parents = nodes.findParents(fieldName);
			if (parents == null) {
				throw new IOException("Cannot find stockInfo JsonNode parents");
			}
			if (parents.size() != 1) {
				throw new IOException("Cannot find stockInfo JsonNode parents");
			}
			
			JsonNode parent = parents.get(0);
			if (parent == null) {
				throw new IOException("Cannot find stockInfo JsonNode parent");
			}
			JsonNode currentPrice = parent.get(fieldName);
			if (currentPrice == null) {
				throw new IOException("Cannot find stockInfo JsonNode currentPrice");
			}

			String raw = currentPrice.get("raw").asText();
			if (raw == null) {
				throw new IOException("Cannot find stockInfo JsonNode currentPrice.raw");
			}
			Double price = Double.parseDouble(raw);
			// String fmt = currentPrice.get("fmt").asText();

			stockInfo = new YahooScreenScrapper2StockInfo();
			stockInfo.setSymbol(stockSymbol);
			stockInfo.setPrice(price);

		} catch (UnsupportedOperationException e) {
			throw new IOException(e);
		} catch (URISyntaxException e) {
			throw new IOException(e);
		} finally {
			long delta = stopWatch.click();
			LOGGER.info("< DONE, " + stockSymbol + ", delta=" + delta);
		}

		return stockInfo;
	}

	@Override
	public void close() throws IOException {
		if (client != null) {
			client.close();
			client = null;
		}
	}

}
