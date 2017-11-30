package com.hungle.msmoney.statements.fi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class ResponseUtils {
    private static final Logger LOGGER = Logger.getLogger(ResponseUtils.class);


    /**
     * Check resp file V 2.
     *
     * @param respFile the resp file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void checkRespFileV2(File respFile) throws IOException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true); // never forget this!
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(respFile);
    
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();
    
            String expression = null;
            Object source = null;
            QName returnType = null;
            Node node = null;
            String nodeValue = null;
            expression = "//SIGNONMSGSRSV1/SONRS/STATUS";
            source = doc;
            returnType = XPathConstants.NODE;
            node = (Node) xpath.evaluate(expression, source, returnType);
            if (node == null) {
                throw new IOException("Cannot find tag <STATUS>");
            }
            expression = "CODE/text()";
            source = node;
            returnType = XPathConstants.STRING;
            nodeValue = (String) xpath.evaluate(expression, source, returnType);
            if (nodeValue == null) {
                throw new IOException("Cannot find tag <STATUS><CODE>");
            }
            String code = nodeValue;
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("code=" + code);
            }
    
            expression = "SEVERITY/text()";
            nodeValue = (String) xpath.evaluate(expression, source, returnType);
            if (nodeValue == null) {
                throw new IOException("Cannot find tag <STATUS><SEVERITY>");
            }
            String severity = nodeValue;
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("severity=" + severity);
            }
            // MESSAGE is optional
            expression = "MESSAGE/text()";
            nodeValue = (String) xpath.evaluate(expression, source, returnType);
            // if (nodeValue == null) {
            // throw new IOException("Cannot find tag <STATUS><MESSAGE>");
            // }
            String message = nodeValue;
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("message=" + message);
            }
    
            if (code.compareToIgnoreCase("0") != 0) {
                if (severity == null) {
                    severity = "SEVERITY_UNKNOWN";
                }
                if (message == null) {
                    message = "MESSAGE_UNKNOWN";
                }
                throw new IOException(severity + " " + message);
            }
        } catch (ParserConfigurationException e) {
            throw new IOException(e);
        } catch (SAXException e) {
            throw new IOException(e);
        } catch (XPathExpressionException e) {
            throw new IOException(e);
        }
    }


    /**
     * Check resp file V 1.
     *
     * @param respFile the resp file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void checkRespFileV1(File respFile) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(respFile));
            ResponseUtils.checkRespFileV1(reader);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.warn(e);
                } finally {
                    reader = null;
                }
            }
        }
    }


    /**
     * Check resp file V 1.
     *
     * @param reader the reader
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private static void checkRespFileV1(BufferedReader reader) throws IOException {
        String line = null;
        boolean inHeader = true;
        StringBuilder sb = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            // <STATUS>
            // <CODE>0
            // <SEVERITY>INFO
            line = line.trim();
            if (line.length() <= 0) {
                inHeader = false;
                continue;
            }
    
            if (inHeader) {
                continue;
            }
    
            sb.append(ResponseUtils.fixesOfxV1LongLine(line));
        }
    
        String str = sb.toString();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(str);
        }
        ResponseUtils.checkRespFileV1(str);
    }


    /**
     * Fixes ofx V 1 long line.
     *
     * @param str the str
     * @return the string
     */
    private static String fixesOfxV1LongLine(String str) {
        String newline = System.getProperty("line.separator");
        int max = str.length();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < max; i++) {
            char c = str.charAt(i);
            if (c == '<') {
                sb.append(newline);
            }
            sb.append(c);
        }
        return sb.toString();
    }


    /**
     * Check resp file V 1.
     *
     * @param string the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private static void checkRespFileV1(String string) throws IOException {
        boolean status = false;
        String code = null;
        String severity = null;
        String message = null;
        String line = null;
    
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new StringReader(string));
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("<STATUS>")) {
                    status = true;
                }
                if (status) {
                    if (line.startsWith("<CODE>")) {
                        code = line.substring(6).trim();
                    } else if (line.startsWith("<SEVERITY>")) {
                        severity = line.substring(10).trim();
                    } else if (line.startsWith("<MESSAGE>")) {
                        message = line.substring(9).trim();
                    } else if (line.startsWith("</STATUS>")) {
                        status = false;
                        break;
                    }
                }
            }
            if (code == null) {
                throw new IOException("Cannot find response's code");
            }
    
            if (code.compareToIgnoreCase("0") != 0) {
                if (severity == null) {
                    severity = "SEVERITY_UNKNOWN";
                }
                if (message == null) {
                    message = "MESSAGE_UNKNOWN";
                }
    
                throw new IOException(severity + " " + message);
            }
    
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.warn(e);
                } finally {
                    reader = null;
                }
            }
        }
    }


    /**
     * Check versioned resp file.
     *
     * @param respFile the resp file
     * @param ofx the ofx
     * @throws IOException Signals that an I/O exception has occurred.
     */
    static void checkRespFile(File respFile, com.hungle.tools.moneyutils.fi.props.OFX ofx) throws IOException {
        int version = 1;
        String ofxVersion = null;
    
        if (ofx != null) {
            ofxVersion = ofx.getVersion();
        }
        if (ofxVersion != null) {
            try {
                version = Integer.valueOf(ofxVersion);
            } catch (NumberFormatException e) {
                LOGGER.warn(e);
                version = 1;
            }
        }
    
        checkRespFile(respFile, version);
    }


    public static void checkRespFile(File respFile, int version) throws IOException {
        if (version == 1) {
            checkRespFileV1(respFile);
        } else if (version == 2) {
            checkRespFileV2(respFile);
        } else {
            LOGGER.warn("Unsupported ofx.version=" + version);
        }
    }

}
