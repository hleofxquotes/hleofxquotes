package com.hungle.tools.moneyutils.yahoo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hungle.tools.moneyutils.ofx.quotes.OfxUtils;
import com.hungle.tools.moneyutils.stockprice.AbstractStockPrice;

public class YahooScreenScrapper2Test {
    private static final Logger LOGGER = Logger.getLogger(YahooScreenScrapper2Test.class);

    public YahooScreenScrapper2Test() {
        // TODO Auto-generated constructor stub
    }

    @Test
    @Ignore
    public void testParseLocalFile() throws IOException {
        String name = "TSLA.html";
        URL url = OfxUtils.getResource(name, this);
        Assert.assertNotNull(url);

        String fileName = url.getFile();
        Assert.assertNotNull(fileName);

        List<String> list = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {

            // 1. filter line 3
            // 2. convert all content to upper case
            // 3. convert it into a List
            list = stream.filter(line -> {
                String prefix = "root.App.main =";
                return line.startsWith(prefix);
            }).collect(Collectors.toList());
        } catch (IOException e) {
            throw e;
        }
        Assert.assertEquals(1, list.size());

        String data = list.get(0);
        data = data.substring("root.App.main =".length());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode nodes = mapper.readTree(data);
        Assert.assertNotNull(nodes);

        String fieldName = "price";
        List<JsonNode> parents = nodes.findParents(fieldName);
        Assert.assertNotNull(parents);
        Assert.assertTrue(parents.size() == 1);

        JsonNode parent = parents.get(0);
        Assert.assertNotNull(parent);

        JsonNode price = parent.get(fieldName);
        Assert.assertNotNull(price);

        LOGGER.info("XXX " + price.toString());

        // String raw = price.get("raw").asText();
        // Assert.assertEquals("297.5151", raw);
        // String fmt = price.get("fmt").asText();
        // Assert.assertEquals("297.52", fmt);
    }

    @Test
    public void testParseViaUrl() throws IOException, URISyntaxException {
        HttpClient client = HttpClientBuilder.create().build();
        String stockSymbol = "TSLA";
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

        Assert.assertNotNull(data);
        data = data.substring(prefix.length());
        Assert.assertNotNull(data);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode treeTop = mapper.readTree(data);
        Assert.assertNotNull(treeTop);

        String fieldName = "price";

        JsonNode price = treeTop.findPath(fieldName);
        LOGGER.info("findPath price=" + price);

        JsonNode regularMarketPrice = price.findPath("regularMarketPrice");
        Assert.assertFalse(regularMarketPrice.isMissingNode());
        LOGGER.info("findPath regularMarketPrice=" + regularMarketPrice);

        JsonNode regularMarketDayLow = price.findPath("regularMarketDayLow");
        Assert.assertFalse(regularMarketDayLow.isMissingNode());
        LOGGER.info("findPath regularMarketDayLow=" + regularMarketDayLow);

        JsonNode regularMarketDayHigh = price.findPath("regularMarketDayHigh");
        Assert.assertFalse(regularMarketDayHigh.isMissingNode());
        LOGGER.info("findPath regularMarketDayHigh=" + regularMarketDayHigh);
    }

    public String prettyPrintJsonString(JsonNode jsonNode) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Object json = mapper.readValue(jsonNode.toString(), Object.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (Exception e) {
            return "Sorry, pretty print didn't work";
        }
    }

    @Test
    @Ignore
    public void testYahooScreenScrapper2() throws IOException {
        YahooScreenScrapper2 scrapper = null;

        int errors = 0;
        try {
            scrapper = new YahooScreenScrapper2();

            String[] stockSymbols1 = { "TSLA", "AAPL", "VWINX", "INVALID", };
            String[] stockSymbols2 = { "VWINX", };

            String[] stockSymbols = stockSymbols1;
            for (String stockSymbol : stockSymbols) {
                try {
                    AbstractStockPrice stockPrice = scrapper.getStockPrice(stockSymbol);
                    Assert.assertNotNull(stockPrice);
                    LOGGER.info(stockPrice);
                } catch (Exception e) {
                    errors++;
                    LOGGER.warn(e, e);
                }
            }
        } finally {
            if (scrapper != null) {
                scrapper.close();
            }
        }

        Assert.assertEquals(1, errors);

    }

    @Test
    public void testParseJson() throws IOException {
        InputStream jsonStream = OfxUtils.getResource("TSLA.json", this).openStream();
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        YahooScreenScrapper2Json json = mapper.readValue(jsonStream, YahooScreenScrapper2Json.class);
        json = null;
    }

}
