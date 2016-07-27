package com.le.tools.moneyutils.yahoo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class YahooBond {
    private static final Logger log = Logger.getLogger(YahooBond.class);

    private static void printDocument(Document doc, OutputStream out) throws IOException, TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        transformer.transform(new DOMSource(doc), new StreamResult(new OutputStreamWriter(out, "UTF-8")));
    }

    String getPrice(String token) throws IOException {
        String priceString = null;

        InputStream stream = null;
        try {
            URL url = createUrl(token);
            log.info("url=" + url);
            stream = url.openStream();

            Document document = createDocument(stream);
            if (log.isDebugEnabled()) {
                try {
                    printDocument(document, System.out);
                } catch (TransformerException e1) {
                    throw new IOException(e1);
                }
            }

            if (document != null) {
                try {
                    priceString = getPrice(document);
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
        return priceString;
    }

    private String getPrice(Document document) throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();

        String expression = null;
        NodeList nodeList = null;
        int length = 0;

        String key = null;
        expression = "//results/td[@class='yfnc_tablehead1']/p";
        nodeList = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);
        length = nodeList.getLength();
        List<String> keys = new ArrayList<String>();
        if (log.isDebugEnabled()) {
            log.debug("length=" + length);
        }
        for (int i = 0; i < length; i++) {
            Element element = (Element) nodeList.item(i);
            key = (String) xpath.evaluate("text()", element, XPathConstants.STRING);
            keys.add(key);
        }

        String value = null;
        expression = "//results/td[@class='yfnc_tabledata1']/p";
        nodeList = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);
        length = nodeList.getLength();
        List<String> values = new ArrayList<String>();
        if (log.isDebugEnabled()) {
            log.debug("length=" + length);
        }
        for (int i = 0; i < length; i++) {
            Element element = (Element) nodeList.item(i);
            value = (String) xpath.evaluate("text()", element, XPathConstants.STRING);
            values.add(value);
        }
        Map<String, String> map = new HashMap<String, String>();
        length = Math.min(keys.size(), values.size());
        for (int i = 0; i < length; i++) {
            map.put(keys.get(i), values.get(i));
        }
        String price = map.get("Price:");
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

    private URL createUrl(String token) throws MalformedURLException, UnsupportedEncodingException {
        String sourceUrlString = token;
        sourceUrlString = URLEncoder.encode(sourceUrlString, "UTF-8");
        String urlString = "http://" + "query.yahooapis.com" + "/v1/public/yql" + "?" + "q" + "=" + "select%20*%20from%20html%20where%20url%3D" + "'"
                + sourceUrlString + "'" + "%20" + "and" + "%20" + "xpath" + "%3D"
                + "%22%2F%2Ftd%5B%40class%3D'yfnc_tablehead1'%20or%20%40class%3D'yfnc_tabledata1'%5D%22%0A&diagnostics=true";
        URL url = new URL(urlString);
        return url;
    }

}
