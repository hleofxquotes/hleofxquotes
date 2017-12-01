package com.hungle.msmoney.qs.yahoo;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.hungle.msmoney.core.ofx.AbstractScreenScrapSource;
import com.hungle.msmoney.qs.scholarshare.TIAACREFPriceInfo;

// TODO: Auto-generated Javadoc
/**
 * The Class YahooScreenScrapSource.
 */
public class YahooSSSource extends AbstractScreenScrapSource<TIAACREFPriceInfo> {

    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(YahooSSSource.class);

    /** The Constant DEFAULT_QUOTE_SERVER. */
    private static final String DEFAULT_QUOTE_SERVER = "finance.yahoo.com";

    /** The quote server. */
    private String quoteServer = DEFAULT_QUOTE_SERVER;

    /** The yahoo apis server. */
    private String yahooApisServer = DEFAULT_YAHOOAPIS_SERVER;

    /** The enc. */
    private String encoding = DEFAULT_ENCODING;

    /**
     * Instantiates a new yahoo screen scrap source.
     *
     * @param stockSymbols the stock symbols
     */
    public YahooSSSource(List<String> stockSymbols) {
        super(stockSymbols);
    }

    /**
     * Gets the price.
     *
     * @param symbol the symbol
     * @return the price
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private String getPrice(String symbol) throws IOException {
        String price = null;
        InputStream stream = null;
        try {
            stream = getStream(symbol);

            Document document = createDocument(stream);
            if (document != null) {
                try {
                    price = getPrice(document);
                } catch (XPathExpressionException e) {
                    throw new IOException(e);
                }
            }
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    LOGGER.warn(e);
                } finally {
                    stream = null;
                }
            }
        }
        return price;
    }

    /**
     * Gets the stream.
     *
     * @param symbol the symbol
     * @return the stream
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws MalformedURLException the malformed URL exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private InputStream getStream(String symbol)
            throws UnsupportedEncodingException, MalformedURLException, IOException {
        InputStream stream;
        URL url = createUrl(symbol);

        LOGGER.info("> Getting information for symbol=" + symbol);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.info("url=" + url);
        }

        stream = url.openStream();
        
        return stream;
    }

    /**
     * Gets the price.
     *
     * @param document
     *            the document
     * @return the price
     * @throws XPathExpressionException
     *             the x path expression exception
     */
    private String getPrice(Document document) throws XPathExpressionException {
        String price = null;

        XPath xpath = XPathFactory.newInstance().newXPath();

        String expression = "//results/span";
        NodeList nodeList = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);
        int length = nodeList.getLength();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("length=" + length);
        }
        for (int i = 0; i < length; i++) {
            Element element = (Element) nodeList.item(i);
            price = (String) xpath.evaluate("text()", element, XPathConstants.STRING);
        }
        return price;
    }

    /**
     * Creates the url.
     *
     * @param symbol the symbol
     * @return the url
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws MalformedURLException the malformed URL exception
     */
    private URL createUrl(String symbol) throws UnsupportedEncodingException, MalformedURLException {
        // http://finance.yahoo.com/q?s=CSCO110128C00017000&d=s
        // currency: http://finance.yahoo.com/q?s=CADUSD=X
        // http://finance.yahoo.com/q?s=CSCO120218C00022000
        // http://developer.yahoo.com/yql/console/
        //
        String selectUrl = "url=" + "\"http://" + quoteServer + "/q?s=" + symbol + "&d=s\"";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("selectUrl=" + selectUrl);
        }

        String selectStatement = createSelectStatement(selectUrl);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("selectStatement=" + selectStatement);
        }

        URL url = new URL("http://" + yahooApisServer + "/v1/public/yql?q=" + selectStatement);

        return url;
    }

    /**
     * Creates the select statement.
     *
     * @param selectUrl the select url
     * @return the string
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    private String createSelectStatement(String selectUrl) throws UnsupportedEncodingException {
        // xpath="//span[@class='time_rtq_ticker']/span"
        String selectXPath = "xpath=" + "\"" + "//span[@class=\'" + "time_rtq_ticker" + "\']/span" + "\"";
        String selectStatement = "select content from html" + " where " + selectUrl + " and " + selectXPath;
        selectStatement = URLEncoder.encode(selectStatement, encoding);
        return selectStatement;
    }

    /* (non-Javadoc)
     * @see com.hungle.tools.moneyutils.ofx.quotes.AbstractScreenScrapSource#scrap()
     */
    @Override
    public List<TIAACREFPriceInfo> scrap() {
        List<TIAACREFPriceInfo> prices = new ArrayList<TIAACREFPriceInfo>();

        List<String> stockSymbols = getStockSymbols();
        for (String stockSymbol : stockSymbols) {
            try {
                String price = getPrice(stockSymbol);
                if (StringUtils.isNotEmpty(price)) {
                    TIAACREFPriceInfo priceInfo = new TIAACREFPriceInfo();
                    priceInfo.setSymbol(stockSymbol);
                    priceInfo.setPrice(Double.valueOf(price));
                    prices.add(priceInfo);
                } else {
                    LOGGER.warn("Cannot get price for stockSymbol=" + stockSymbol);
                }
            } catch (IOException e) {
                LOGGER.warn("Cannot get price for stockSymbol=" + stockSymbol);
            } catch (NumberFormatException e) {
                LOGGER.warn("Cannot get price for stockSymbol=" + stockSymbol);
            }
        }
        return prices;
    }
}
