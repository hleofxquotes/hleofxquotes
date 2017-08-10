package com.hungle.tools.moneyutils.scholarshare;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.hungle.tools.moneyutils.ofx.quotes.AbstractScreenScrapSource;

// TODO: Auto-generated Javadoc
/**
 * The Class TIAACREFScreenScrapSource.
 */
public class TIAACREFScreenScrapSource extends AbstractScreenScrapSource {
    
    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(TIAACREFScreenScrapSource.class);

    /** The Constant DEFAULT_URL. */
    private static final String DEFAULT_URL = "https://www.scholarshare.com/research/daily.shtml";

    

    /** The Constant DEFAULT_YAHOOAPIS_SERVER. */
    // http://developer.yahoo.com/yql/console/
//    private static final String DEFAULT_YAHOOAPIS_SERVER = "query.yahooapis.com";

    /** The yahoo apis server. */
    private String yahooApisServer = AbstractScreenScrapSource.DEFAULT_YAHOOAPIS_SERVER;

    /** The enc. */
    private String enc = DEFAULT_ENCODING;

    /** The document. */
    private Document document;

    /** The current porfolio name. */
    private String currentPorfolioName;

    /** The prices. */
    private List<TIAACREFPriceInfo> prices;

    /** The date. */
    private Date date;

    /** The currency. */
    private String currency;

    /**
     * Instantiates a new TIAACREF screen scrap source.
     *
     * @param stockSymbols the stock symbols
     */
    public TIAACREFScreenScrapSource(List<String> stockSymbols) {
        super(stockSymbols);
    }

    /**
     * Instantiates a new TIAACREF screen scrap source.
     */
    public TIAACREFScreenScrapSource() {
        this(new ArrayList<String>());
    }

    /**
     * Query.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void query() throws IOException {
        LOGGER.info("> query");
        
        InputStream stream = null;
        try {
            URL url = createUrl(null);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("url=" + url);
            }

            stream = url.openStream();

            this.document = createDocument(stream);
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
     * Query.
     *
     * @param fileName the file name
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void query(String fileName) throws IOException {
        LOGGER.info("fileName=" + fileName);

        InputStream stream = null;
        try {
            stream = new FileInputStream(new File(fileName));

            this.document = createDocument(stream);
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
     * Parses the.
     *
     * @throws XPathExpressionException the x path expression exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void parse() throws XPathExpressionException, IOException {
        this.prices = new ArrayList<TIAACREFPriceInfo>();

        String expression = null;
        XPath xpath = XPathFactory.newInstance().newXPath();

        // tr class="rowbanner"
        expression = "//thead/tr[@class='rowbanner']/th/p/text()";
        // Daily Fund Performance - for the Period Ending November 14, 2011
        String dateString = (String) xpath.evaluate(expression, document, XPathConstants.STRING);

        date = parseDate(dateString);
        if(date == null) {
            throw new IOException("Cannot parse dateString=" + dateString);
        }

        expression = "//table/tr";
        NodeList nodeList = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);
        int length = nodeList.getLength();
        for (int i = 0; i < length; i++) {
            Node rowNode = nodeList.item(i);
            parseRow(xpath, rowNode);
        }
        for (TIAACREFPriceInfo price : prices) {
            calculateSymbolName(price);
        }

    }

    /**
     * Parses the date.
     *
     * @param dateString the date string
     * @return the date
     */
    private Date parseDate(String dateString) {
        Date date = null;
        // for the Period Ending December 2, 2011
        LOGGER.info("dateString=" + dateString);
        Pattern p = Pattern.compile("(.*) for the Period Ending (.*)$");
        Matcher matcher = p.matcher(dateString);
        if ((matcher != null) && (matcher.matches())) {
            // December 2, 2011
            SimpleDateFormat format = new SimpleDateFormat("MMMMM d, yyyy");
            String source = matcher.group(2);
            LOGGER.info(source);
            try {
                date = format.parse(source);
            } catch (ParseException e) {
                LOGGER.warn(e);
            }
        } else {
            LOGGER.warn("Cannot parse date string=" + dateString);
        }
        if (date == null) {
            date = new Date();
        }
        return date;
    }

    /**
     * Calculate symbol name.
     *
     * @param price the price
     */
    private void calculateSymbolName(TIAACREFPriceInfo price) {
        StringBuilder sb = new StringBuilder();
        String[] tokens = null;
        tokens = price.getPortfolioName().split("[\\s_-]");
        for (String token : tokens) {
            if (token == null) {
                continue;
            }
            token = token.trim();
            sb.append(token.charAt(0));
        }

        Pattern p = Pattern.compile("[0-9\\+]+");
        tokens = price.getFundName().split("[\\s_-]");
        for (String token : tokens) {
            if (token == null) {
                continue;
            }
            token = token.trim();
            Matcher m = p.matcher(token);
            if (m.matches()) {
                sb.append(token);
            } else {
                sb.append(token.charAt(0));
            }
        }

        price.setSymbol(sb.toString());
    }

    /**
     * Parses the row.
     *
     * @param xpath the xpath
     * @param rowNode the row node
     * @throws XPathExpressionException the x path expression exception
     */
    private void parseRow(XPath xpath, Node rowNode) throws XPathExpressionException {
        String classValue = null;
        NamedNodeMap attributes = rowNode.getAttributes();
        if (attributes == null) {
            return;
        }
        if (attributes.getLength() <= 0) {
            return;
        }
        Node classAttribute = attributes.getNamedItem("class");
        if (classAttribute == null) {
            return;
        }
        classValue = classAttribute.getNodeValue();
        if (classValue == null) {
            return;
        }
        if (classValue.compareToIgnoreCase("row") == 0) {
            TIAACREFPriceInfo priceInfo = parseRowData(xpath, rowNode);
            if (priceInfo != null) {
                priceInfo.setPortfolioName(currentPorfolioName);
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("priceInfo=" + priceInfo);
            }
            if (priceInfo != null) {
                prices.add(priceInfo);
            }
        } else if (classValue.compareToIgnoreCase("rowbanner") == 0) {
            currentPorfolioName = parseRowBanner(xpath, rowNode);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("currentPorfolioName=" + currentPorfolioName);
            }
        }
    }

    /**
     * Parses the row banner.
     *
     * @param xpath the xpath
     * @param rowNode the row node
     * @return the string
     * @throws XPathExpressionException the x path expression exception
     */
    private String parseRowBanner(XPath xpath, Node rowNode) throws XPathExpressionException {
        String expression;
        expression = "./td/p/text()";
        String bannerText = (String) xpath.evaluate(expression, rowNode, XPathConstants.STRING);
        return bannerText;
    }

    /**
     * Parses the row data.
     *
     * @param xpath the xpath
     * @param rowNode the row node
     * @return the TIAACREF price info
     * @throws XPathExpressionException the x path expression exception
     */
    private TIAACREFPriceInfo parseRowData(XPath xpath, Node rowNode) throws XPathExpressionException {
        TIAACREFPriceInfo priceInfo = new TIAACREFPriceInfo();
        priceInfo.setDate(date);

        String expression;
        expression = "./th/p/text()";
        String fundName = (String) xpath.evaluate(expression, rowNode, XPathConstants.STRING);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("fundName=" + fundName);
        }
        priceInfo.setFundName(fundName);

        expression = "./td/p";
        NodeList nodeList = (NodeList) xpath.evaluate(expression, rowNode, XPathConstants.NODESET);
        if ((nodeList != null) && (nodeList.getLength() > 0)) {
            Node node = nodeList.item(0);
            String priceString = (String) xpath.evaluate("text()", node, XPathConstants.STRING);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("priceString=" + priceString);
            }
            if (priceString != null) {
                priceString = priceString.trim();
                priceString = priceString.replace("$", "");
                Double price = null;
                try {
                    price = Double.valueOf(priceString);
                    priceInfo.setPrice(price);
                } catch (NumberFormatException e) {
                    LOGGER.warn(e);
                }
            }
        }
        return priceInfo;
    }

    /**
     * Creates the url.
     *
     * @param symbol the symbol
     * @return the url
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws MalformedURLException the malformed URL exception
     */
    protected URL createUrl(String symbol) throws UnsupportedEncodingException, MalformedURLException {
        // http://www.scholarshare.com/performance/fundperformance.shtml
        // http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20html%20where%20url%3D'http%3A%2F%2Fwww.scholarshare.com%2Fperformance%2Ffundperformance.shtml'%20and%20xpath%3D%22%2F%2Ftable%5B%40id%3D'realtimechart'%5D%22%0A&diagnostics=true
        String selectUrl = "url=" + "\"" + DEFAULT_URL + "\"";
        LOGGER.info("selectUrl=" + selectUrl);
        String selectXPath = "xpath=" + "\"" + "//table[@id='realtimechart']" + "\"";
        String selectStatement = "select * from html" + " where " + selectUrl + " and " + selectXPath;
        selectStatement = URLEncoder.encode(selectStatement, enc);
        URL url = new URL("http://" + yahooApisServer + "/v1/public/yql?q=" + selectStatement);
        return url;
    }

    /**
     * Gets the prices.
     *
     * @return the prices
     */
    public List<TIAACREFPriceInfo> getPrices() {
        return prices;
    }

    /**
     * Sets the prices.
     *
     * @param prices the new prices
     */
    public void setPrices(List<TIAACREFPriceInfo> prices) {
        this.prices = prices;
    }

    /**
     * Gets the price.
     *
     * @param symbol the symbol
     * @return the price
     */
    public String getPrice(String symbol) {
        TIAACREFPriceInfo price = getPriceBySymbol(symbol);
        if (price == null) {
            return null;
        }
        return "" + price.getPrice();
    }

    /**
     * Gets the currency.
     *
     * @return the currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Sets the currency.
     *
     * @param currency the new currency
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * Gets the name.
     *
     * @param symbol the symbol
     * @return the name
     */
    public String getName(String symbol) {
        TIAACREFPriceInfo price = getPriceBySymbol(symbol);
        if (price == null) {
            return null;
        }
        return price.getFundName() + " - " + price.getPortfolioName();
    }

    /**
     * Gets the date.
     *
     * @param symbol the symbol
     * @return the date
     */
    public Date getDate(String symbol) {
        TIAACREFPriceInfo price = getPriceBySymbol(symbol);
        if (price == null) {
            return null;
        }
        return price.getDate();
    }
    
    /**
     * Gets the price by symbol.
     *
     * @param symbol the symbol
     * @return the price by symbol
     */
    private TIAACREFPriceInfo getPriceBySymbol(String symbol) {
        for (TIAACREFPriceInfo price : prices) {
            if (price.getSymbol().compareToIgnoreCase(symbol) == 0) {
                return price;
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see com.hungle.tools.moneyutils.ofx.quotes.AbstractScreenScrapSource#scrap()
     */
    @Override
    public List scrap() {
        // TODO Auto-generated method stub
        return null;
    }
}
