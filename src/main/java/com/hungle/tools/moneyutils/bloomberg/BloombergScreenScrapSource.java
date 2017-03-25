package com.hungle.tools.moneyutils.bloomberg;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.hungle.tools.moneyutils.ofx.quotes.AbstractScreenScrapSource;

// TODO: Auto-generated Javadoc
/**
 * The Class BloombergScreenScrapSource.
 */
public class BloombergScreenScrapSource extends AbstractScreenScrapSource {
    
    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(BloombergScreenScrapSource.class);

    /** The Constant DEFAULT_QUOTE_SERVER. */
    private static final String DEFAULT_QUOTE_SERVER = "www.bloomberg.com";

    /** The quote server. */
    private String quoteServer = DEFAULT_QUOTE_SERVER;

    /** The yahoo apis server. */
    private String yahooApisServer = AbstractScreenScrapSource.DEFAULT_YAHOOAPIS_SERVER;

    /** The enc. */
    private String enc = DEFAULT_ENCODING;

    /** The symbol. */
    private String symbol;

    /** The document. */
    private Document document;

    /** The price. */
    private String price;

    /** The currency. */
    private String currency;

    public BloombergScreenScrapSource(List<String> stockSymbols) {
        super(stockSymbols);
    }

    /**
     * Query.
     *
     * @param symbol the symbol
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void query(String symbol) throws IOException {
        this.symbol = symbol;
        InputStream stream = null;
        try {
            URL url = createUrl(symbol);

            LOGGER.info("> Getting information for symbol=" + symbol);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("url=" + url);
            }

            stream = url.openStream();

            document = createDocument(stream);
            if (document != null) {
                try {
                    this.price = getPrice(document);
                    this.currency = getCurrency(document);
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
    }

    /**
     * Gets the price.
     *
     * @param symbol the symbol
     * @return the price
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String getPrice(String symbol) throws IOException {
        String price = null;
        InputStream stream = null;
        try {
            URL url = createUrl(symbol);

            LOGGER.info("url=" + url);

            stream = url.openStream();

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
     * Gets the currency.
     *
     * @param document the document
     * @return the currency
     * @throws XPathExpressionException the x path expression exception
     */
    private String getCurrency(Document document) throws XPathExpressionException {
        String currency = "USD";

        XPath xpath = XPathFactory.newInstance().newXPath();

        String expression = "//results" + "//div[@class='price']/p";
        NodeList nodeList = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);
        int length = nodeList.getLength();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("length=" + length);
        }
        for (int i = 0; i < length; i++) {
            Element element = (Element) nodeList.item(i);
            NodeList nodes = element.getChildNodes();
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < nodes.getLength(); j++) {
                Node node = nodes.item(j);
                if (node.getNodeType() == Node.TEXT_NODE) {
                    sb.append(" ");
                    sb.append(node.getTextContent().trim());
                }
            }
            String[] tokens = sb.toString().trim().split(" ");
            if (LOGGER.isDebugEnabled()) {
                LOGGER.info(tokens.length);
                for (String token : tokens) {
                    LOGGER.debug(token);
                }
            }
            if ((tokens != null) && (tokens.length > 1)) {
                currency = tokens[tokens.length - 1];
            }
        }
        return currency;
    }

    /**
     * Gets the price.
     *
     * @param document the document
     * @return the price
     * @throws XPathExpressionException the x path expression exception
     */
    private String getPrice(Document document) throws XPathExpressionException {
        String price = null;

        XPath xpath = XPathFactory.newInstance().newXPath();

        // <span class="amount">
        String expression = "//results" + "//span[@class='amount']";
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

    protected URL createUrl(String symbol) throws UnsupportedEncodingException, MalformedURLException {
        // http://www.bloomberg.com/apps/quote?ticker=AAPL:US
        String selectUrl = "url=" + "\"http://" + quoteServer + "/apps/quote?ticker=" + symbol + "\"";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("selectUrl=" + selectUrl);
        }
        // select * from html where
        // url='http://www.bloomberg.com/apps/quote?ticker=AAPL:US' and
        // xpath="//div[@id='price_info']"
        // http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20html%20where%20url%3D'http%3A%2F%2Fwww.bloomberg.com%2Fapps%2Fquote%3Fticker%3DAAPL%3AUS'%20and%20xpath%3D%22%2F%2Fdiv%5B%40id%3D'price_info'%5D%22%0A&diagnostics=true
        // JSON
        // http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20html%20where%20url%3D'http%3A%2F%2Fwww.bloomberg.com%2Fapps%2Fquote%3Fticker%3DAAPL%3AUS'%20and%20xpath%3D%22%2F%2Fdiv%5B%40id%3D'price_info'%5D%22%0A&format=json&diagnostics=true&callback=cbfunc
        String selectStatement = createSelectStatement(selectUrl);
        URL url = new URL("http://" + yahooApisServer + "/v1/public/yql?q=" + selectStatement);
        return url;
    }

    private String createSelectStatement(String selectUrl) throws UnsupportedEncodingException {
        String selectXPath = "xpath=" + "\"" + "//div[@id='price_info']" + "\"";
        String selectStatement = "select * from html" + " where " + selectUrl + " and " + selectXPath;
        boolean json = false;
        if (json) {
            // &format=json&callback=cbfunc
            selectStatement = selectStatement + "&format=json&callback=cbfunc";
        }
        selectStatement = URLEncoder.encode(selectStatement, enc);
        return selectStatement;
    }

    /**
     * Gets the symbol.
     *
     * @return the symbol
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Gets the price.
     *
     * @return the price
     */
    public String getPrice() {
        return price;
    }

    /**
     * Gets the currency.
     *
     * @return the currency
     */
    public String getCurrency() {
        return currency;
    }

    @Override
    public List scrap() {
        // TODO Auto-generated method stub
        return null;
    }
}
