package com.hungle.tools.moneyutils.ofx.quotes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;

// TODO: Auto-generated Javadoc
/**
 * The Class OfxUtils.
 */
public class OfxUtils {
    
    /** The Constant log. */
    private static final Logger log = Logger.getLogger(OfxUtils.class);

    /**
     * Checks if is na.
     *
     * @param value the value
     * @return true, if is na
     */
    public static boolean isNA(String value) {
        if (value == null) {
            return true;
        }
        value = value.trim();
        if (value.length() <= 0) {
            return true;
        }

        if (value.equals("N/A")) {
            return true;
        }

        return false;
    }

    /**
     * From separated string.
     *
     * @param str the str
     * @return the list
     */
    private static List<String> fromSeparatedString(String str) {
        return OfxUtils.fromSeparatedString(str, ",");
    }

    /**
     * From separated string.
     *
     * @param str the str
     * @param sep the sep
     * @return the list
     */
    private static List<String> fromSeparatedString(String str, String sep) {
        List<String> list = new ArrayList<String>();
        if (str == null) {
            return list;
        }
        str = str.trim();
        if (str.length() <= 0) {
            return list;
        }

        String[] tokens = str.split(sep);
        for (int i = 0; i < tokens.length; i++) {
            list.add(tokens[i].trim());
        }

        return list;
    }

    /**
     * To separated string.
     *
     * @param tokens the tokens
     * @return the string
     */
    public static String toSeparatedString(List<String> tokens) {
        return OfxUtils.toSeparatedString(tokens, ",");
    }

    /**
     * To separated string.
     *
     * @param tokens the tokens
     * @param sep the sep
     * @return the string
     */
    private static String toSeparatedString(List<String> tokens, String sep) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (String token : tokens) {
            if (i > 0) {
                sb.append(sep);
            }
            sb.append(token);
            i++;
        }
        return sb.toString();
    }

    /**
     * Adds the to list.
     *
     * @param reader the reader
     * @param stocks the stocks
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void addToList(BufferedReader reader, List<String> stocks) throws IOException {
        String line = null;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.length() <= 0) {
                continue;
            }
            if (line.charAt(0) == '#') {
                continue;
            }
            List<String> list = fromSeparatedString(line);
            if (list != null) {
                stocks.addAll(list);
            }
        }
    }

    /**
     * Adds the to list.
     *
     * @param in the in
     * @param stocks the stocks
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private static void addToList(InputStream in, List<String> stocks) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            addToList(reader, stocks);
            if (log.isDebugEnabled()) {
                log.debug("stocks.size=" + stocks.size());
            }
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.warn(e);
                } finally {
                    reader = null;
                }
            }
        }
    }

    /**
     * Gets the resource.
     *
     * @param name the name
     * @return the resource
     */
    public static URL getResource(String name) {
        return OfxUtils.getResource(name, name);
    }

    /**
     * Gets the resource.
     *
     * @param name the name
     * @param obj the obj
     * @return the resource
     */
    public static URL getResource(String name, Object obj) {
        if (obj == null) {
            obj = name;
        }

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL url = cl.getResource(name);
        if (url == null) {
            cl = obj.getClass().getClassLoader();
            if (cl != null) {
                url = cl.getResource(name);
            }
        }

        if (url == null) {
            url = obj.getClass().getResource(name);
        }
        return url;
    }

    /**
     * Adds the to list.
     *
     * @param url the url
     * @param stocks the stocks
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void addToList(URL url, List<String> stocks) throws IOException {
        InputStream in = null;
        try {
            in = url.openStream();
            addToList(in, stocks);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.warn(e);
                } finally {
                    in = null;
                }
            }
        }
    }

    /**
     * Adds the to list.
     *
     * @param resourceName the resource name
     * @param stocks the stocks
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private static void addToList(String resourceName, List<String> stocks) throws IOException {
        URL url = getResource(resourceName);
        if (url != null) {
            addToList(url, stocks);
        } else {
            log.warn("Cannot find url for resourceName=" + resourceName);
        }
    }

    /**
     * Gets the list.
     *
     * @param resourceName the resource name
     * @return the list
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static List<String> getList(String resourceName) throws IOException {
        List<String> stocks = new ArrayList<String>();
        addToList(resourceName, stocks);
        return stocks;
    }

    /**
     * Gets the NYSE list.
     *
     * @return the NYSE list
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static List<String> getNYSEList() throws IOException {
        return getList("nyse.txt");
    }

    /**
     * Gets the NASDAQ list.
     *
     * @return the NASDAQ list
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static List<String> getNASDAQList() throws IOException {
        return getList("nasdaq.txt");
    }

    /**
     * Gets the USMF list.
     *
     * @return the USMF list
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static List<String> getUSMFList() throws IOException {
        return getList("usmf.txt");
    }

    /**
     * Gets the LSE list.
     *
     * @return the LSE list
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static List<String> getLSEList() throws IOException {
        return getList("lse.txt");
    }

    /**
     * Store stock symbols.
     *
     * @param prefs the prefs
     * @param nodeName the node name
     * @param stocksString the stocks string
     * @return the int
     */
    public static int storeStockSymbols(Preferences prefs, String nodeName, String stocksString) {
        if (nodeName == null) {
            nodeName = "stockSymbols";
        }
        try {
            clearNode(prefs, nodeName);
        } catch (BackingStoreException e) {
            log.error(e);
        }

        Preferences node = prefs.node(nodeName);

        int count = 0;
        try {
            String key = null;
            int beginIndex = 0;
            int endIndex = 0;
            int bucketSize = Preferences.MAX_VALUE_LENGTH;
            int length = stocksString.length();
            while (endIndex < length) {
                key = "" + count;
                beginIndex = endIndex;
                endIndex += bucketSize;
                endIndex = Math.min(endIndex, length);
                String value = stocksString.substring(beginIndex, endIndex);
                node.put(key, value);
                count++;
            }
            node.putInt("count", count);
            node.sync();
        } catch (BackingStoreException e) {
            log.warn(e);
        }

        return count;
    }

    /**
     * Retrieve stock symbols.
     *
     * @param prefs the prefs
     * @param nodeName the node name
     * @return the string
     */
    public static String retrieveStockSymbols(Preferences prefs, String nodeName) {
        if (nodeName == null) {
            nodeName = "stockSymbols";
        }

        StringBuilder sb = new StringBuilder();
        Preferences node = prefs.node(nodeName);
        int count = node.getInt("count", 0);
        for (int i = 0; i < count; i++) {
            String key = "" + i;
            String value = node.get(key, null);
            if (value == null) {
                continue;
            }
            if (value.length() <= 0) {
                continue;
            }
            sb.append(value);
        }
        return sb.toString();
    }

    /**
     * Clear node.
     *
     * @param prefs the prefs
     * @param nodeName the node name
     * @throws BackingStoreException the backing store exception
     */
    private static void clearNode(Preferences prefs, String nodeName) throws BackingStoreException {
        if (!prefs.nodeExists(nodeName)) {
            return;
        }
        Preferences node = prefs.node(nodeName);
        node.removeNode();
        node.flush();
    }

    /**
     * Store stock symbols.
     *
     * @param stocksString the stocks string
     * @return the int
     */
    public static int storeStockSymbols(String stocksString) {
        // TODO: le.com.tools.moneyutils.ofx.quotes.GUI
        Preferences prefs = Preferences.userNodeForPackage(le.com.tools.moneyutils.ofx.quotes.GUI.class);
        return OfxUtils.storeStockSymbols(prefs, stocksString, null);
    }

    /**
     * Retrieve stock symbols.
     *
     * @return the string
     */
    public static String retrieveStockSymbols() {
        // TODO: le.com.tools.moneyutils.ofx.quotes.GUI
        Preferences prefs = Preferences.userNodeForPackage(le.com.tools.moneyutils.ofx.quotes.GUI.class);
        return OfxUtils.retrieveStockSymbols(prefs, null);
    }

    /**
     * Break lines.
     *
     * @param stockSymbolsString the stock symbols string
     * @param maxTokens the max tokens
     * @param sep the sep
     * @return the string
     */
    public static String breakLines(String stockSymbolsString, int maxTokens, String sep) {
        String newLine = "\n";
        String[] tokens = stockSymbolsString.split(sep);
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (int i = 0; i < tokens.length; i++) {
            if ((i % maxTokens) == (maxTokens - 1)) {
                sb.append(newLine);
                count = 0;
            }

            if (count > 0) {
                sb.append(sep);
            }

            sb.append(tokens[i]);

            count++;
        }
        return sb.toString();
    }

    /**
     * Break lines.
     *
     * @param stocksString the stocks string
     * @return the string
     */
    public static String breakLines(String stocksString) {
        return breakLines(stocksString, 20, ",");
    }

    /**
     * Remove all the namespace stuffs.
     *
     * @param xmlObject the xml object
     * @return the xml object
     */
    public static XmlObject localizeXmlFragment(XmlObject xmlObject) {
        String s;
        XmlCursor c = xmlObject.newCursor();
        c.toNextToken();
        while (c.hasNextToken()) {
            if (c.isNamespace()) {
                c.removeXml();
            } else {
                if (c.isStart() || c.isAttr()) {
                    s = c.getName().getLocalPart();
                    c.setName(new QName(s));
                }
                c.toNextToken();
            }
        }
        c.dispose();
        return xmlObject;
    }

}
