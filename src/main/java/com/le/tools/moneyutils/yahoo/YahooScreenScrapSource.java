package com.le.tools.moneyutils.yahoo;

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
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class YahooScreenScrapSource {
    private static final Logger log = Logger.getLogger(YahooScreenScrapSource.class);

    private static final String DEFAULT_ENCODING = "UTF-8";

    // http://developer.yahoo.com/yql/console/
    private static final String DEFAULT_YAHOOAPIS_SERVER = "query.yahooapis.com";

    private static final String DEFAULT_QUOTE_SERVER = "finance.yahoo.com";

    private String quoteServer = DEFAULT_QUOTE_SERVER;

    private String yahooApisServer = DEFAULT_YAHOOAPIS_SERVER;

    private String enc = DEFAULT_ENCODING;

    public String getPrice(String symbol) throws IOException {
        String price = null;
        InputStream stream = null;
        try {
            URL url = createUrl(symbol);

            log.info("> Getting information for symbol=" + symbol);
            if (log.isDebugEnabled()) {
                log.info("debug=" + url);
            }

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

    private String getPrice(Document document) throws XPathExpressionException {
        String price = null;

        XPath xpath = XPathFactory.newInstance().newXPath();

        String expression = "//results/span";
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
        // http://finance.yahoo.com/q?s=CSCO110128C00017000&d=s
        // currency: http://finance.yahoo.com/q?s=CADUSD=X
        // http://finance.yahoo.com/q?s=CSCO120218C00022000
        // http://developer.yahoo.com/yql/console/
        //
        String selectUrl = "url=" + "\"http://" + quoteServer + "/q?s=" + symbol + "&d=s\"";
        if (log.isDebugEnabled()) {
            log.debug("selectUrl=" + selectUrl);
        }

        // xpath="//span[@class='time_rtq_ticker']/span"
        String selectXPath = "xpath=" + "\"" + "//span[@class=\'" + "time_rtq_ticker" + "\']/span" + "\"";
        String selectStatement = "select content from html" + " where " + selectUrl + " and " + selectXPath;
        log.info("selectStatement=" + selectStatement);

        selectStatement = URLEncoder.encode(selectStatement, enc);
        URL url = new URL("http://" + yahooApisServer + "/v1/public/yql?q=" + selectStatement);

        return url;
    }
}
