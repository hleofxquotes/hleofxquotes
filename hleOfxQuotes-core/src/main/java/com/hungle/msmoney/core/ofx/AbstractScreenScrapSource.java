package com.hungle.msmoney.core.ofx;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public abstract class AbstractScreenScrapSource<T> {

    /** The Constant DEFAULT_YAHOOAPIS_SERVER. */
    // http://developer.yahoo.com/yql/console/
    public static final String DEFAULT_YAHOOAPIS_SERVER = "query.yahooapis.com";

    /** The Constant DEFAULT_ENCODING. */
    protected static final String DEFAULT_ENCODING = "UTF-8";

    private final List<String> stockSymbols;

    public AbstractScreenScrapSource(List<String> stockSymbols) {
        this.stockSymbols = stockSymbols;
    }

    /**
     * Creates the document.
     *
     * @param stream
     *            the stream
     * @return the document
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    protected Document createDocument(InputStream stream) throws IOException {
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

    public abstract List<T> scrap();

    public List<String> getStockSymbols() {
        return stockSymbols;
    }

}
