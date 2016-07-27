package com.le.tools.moneyutils.bloomberg;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class BloombergScreenScrapSource {
    private static final Logger log = Logger.getLogger(BloombergScreenScrapSource.class);

    private static final String DEFAULT_ENCODING = "UTF-8";

    // http://developer.yahoo.com/yql/console/
    private static final String DEFAULT_YAHOOAPIS_SERVER = "query.yahooapis.com";

    private static final String DEFAULT_QUOTE_SERVER = "www.bloomberg.com";

    private String quoteServer = DEFAULT_QUOTE_SERVER;

    private String yahooApisServer = DEFAULT_YAHOOAPIS_SERVER;

    private String enc = DEFAULT_ENCODING;

    private String symbol;

    private Document document;

    private String price;

    private String currency;

    public void query(String symbol) throws IOException {
        this.symbol = symbol;
        InputStream stream = null;
        try {
            URL url = createUrl(symbol);

            log.info("> Getting information for symbol=" + symbol);
            if (log.isDebugEnabled()) {
                log.debug("url=" + url);
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
                    log.warn(e);
                } finally {
                    stream = null;
                }
            }
        }
    }

    public String getPrice(String symbol) throws IOException {
        String price = null;
        InputStream stream = null;
        try {
            URL url = createUrl(symbol);

            log.info("url=" + url);

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
                    log.warn(e);
                } finally {
                    stream = null;
                }
            }
        }
        return price;
    }

    private String getCurrency(Document document) throws XPathExpressionException {
        String currency = "USD";

        XPath xpath = XPathFactory.newInstance().newXPath();

        String expression = "//results" + "//div[@class='price']/p";
        NodeList nodeList = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);
        int length = nodeList.getLength();
        if (log.isDebugEnabled()) {
            log.debug("length=" + length);
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
            if (log.isDebugEnabled()) {
                log.info(tokens.length);
                for (String token : tokens) {
                    log.debug(token);
                }
            }
            if ((tokens != null) && (tokens.length > 1)) {
                currency = tokens[tokens.length - 1];
            }
        }
        return currency;
    }

    private String getPrice(Document document) throws XPathExpressionException {
        String price = null;

        XPath xpath = XPathFactory.newInstance().newXPath();

        // <span class="amount">
        String expression = "//results" + "//span[@class='amount']";
        NodeList nodeList = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);
        int length = nodeList.getLength();
        if (log.isDebugEnabled()) {
            log.debug("length=" + length);
        }
        for (int i = 0; i < length; i++) {
            Element element = (Element) nodeList.item(i);
            price = (String) xpath.evaluate("text()", element, XPathConstants.STRING);
        }
        return price;
    }

    private Document createDocument(InputStream stream) throws IOException {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = null;
        Document document = null;
        try {
            builder = domFactory.newDocumentBuilder();
            document = builder.parse(stream);
        } catch (ParserConfigurationException e) {
            throw new IOException(e);
        } catch (SAXException e) {
            throw new IOException(e);
        }
        return document;
    }

    private URL createUrl(String symbol) throws UnsupportedEncodingException, MalformedURLException {
        // http://www.bloomberg.com/apps/quote?ticker=AAPL:US
        String selectUrl = "url=" + "\"http://" + quoteServer + "/apps/quote?ticker=" + symbol + "\"";
        if (log.isDebugEnabled()) {
            log.debug("selectUrl=" + selectUrl);
        }
        // select * from html where
        // url='http://www.bloomberg.com/apps/quote?ticker=AAPL:US' and
        // xpath="//div[@id='price_info']"
        // http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20html%20where%20url%3D'http%3A%2F%2Fwww.bloomberg.com%2Fapps%2Fquote%3Fticker%3DAAPL%3AUS'%20and%20xpath%3D%22%2F%2Fdiv%5B%40id%3D'price_info'%5D%22%0A&diagnostics=true
        // JSON
        // http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20html%20where%20url%3D'http%3A%2F%2Fwww.bloomberg.com%2Fapps%2Fquote%3Fticker%3DAAPL%3AUS'%20and%20xpath%3D%22%2F%2Fdiv%5B%40id%3D'price_info'%5D%22%0A&format=json&diagnostics=true&callback=cbfunc
        String selectXPath = "xpath=" + "\"" + "//div[@id='price_info']" + "\"";
        String selectStatement = "select * from html" + " where " + selectUrl + " and " + selectXPath;
        boolean json = false;
        if (json) {
            // &format=json&callback=cbfunc
            selectStatement = selectStatement + "&format=json&callback=cbfunc";
        }
        selectStatement = URLEncoder.encode(selectStatement, enc);
        URL url = new URL("http://" + yahooApisServer + "/v1/public/yql?q=" + selectStatement);
        return url;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getPrice() {
        return price;
    }

    public String getCurrency() {
        return currency;
    }
}
