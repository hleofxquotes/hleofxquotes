package com.hungle.tools.moneyutils.fi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.hungle.tools.moneyutils.fi.props.FIBean;
import com.hungle.tools.moneyutils.fi.props.HttpProperties;
import com.hungle.tools.moneyutils.fi.props.PropertiesUtils;
import com.hungle.tools.moneyutils.scrubber.ResponseFilter;

public class UpdateFiDir {
    private static final Logger log = Logger.getLogger(UpdateFiDir.class);
    
    public static final String DEFAULT_PROPERTIES_FILENAME = "fi.properties";
    
    private File dir;
    private String propertiesFileName = DEFAULT_PROPERTIES_FILENAME;
    private String template;
    private String requestFileName = OfxPostClient.REQ_FILE_OFX;
    private String respFileName = OfxPostClient.RESP_FILE_OFX;
    private File reqFile;
    private File respFile;

    public UpdateFiDir(File dir) {
        this.dir = dir;
    }

    public boolean update() throws IOException {
        VelocityContext context = createVelocityContext();

        String workingTemplate = null;
        if (!PropertiesUtils.isNull(this.template)) {
            workingTemplate = this.template;
        } else {
            workingTemplate = getTemplate(context);
        }
        if (PropertiesUtils.isNull(workingTemplate)) {
            throw new IOException("template is null");
        }

        workingTemplate = "/templates/" + workingTemplate;

        if (log.isDebugEnabled()) {
            log.debug("template=" + workingTemplate);
        }
        reqFile = new File(dir, requestFileName);
        String encoding = OfxPostClient.DEFAULT_TEMPLATE_ENCODING;
        VelocityUtils.mergeTemplate(context, workingTemplate, encoding, reqFile);
        if (log.isDebugEnabled()) {
            log.debug("Created reqFile=" + reqFile);
        }

        respFile = new File(dir, respFileName);

        HttpProperties httpProperties = (HttpProperties) context.get("httpProperties");

        FIBean fi = (FIBean) context.get("fi");
        String url = fi.getUrl();

        if (PropertiesUtils.isNull(url)) {
            log.warn("Skip sending request, fi.url is null");
            return false;
        }

        log.info("FI.name=" + fi.getName());
        log.info("Sending request to " + url);
        // respFile = new File(dir, respFileName);
        OfxPostClientParams params = new OfxPostClientParams(url, reqFile, respFile, httpProperties);
//        try {
//            params.setEncryptionHelper(new EncryptionHelper());
//        } catch (EncryptionHelperException e) {
//            throw new IOException(e);
//        }
        OfxPostClient.sendRequest(params);
        if (log.isDebugEnabled()) {
            log.debug("Created respFile=" + respFile.getAbsolutePath());
        }
        com.hungle.tools.moneyutils.fi.props.OFX ofx = (com.hungle.tools.moneyutils.fi.props.OFX) context.get("ofx");

        List<ResponseFilter> responseFilters = (List<ResponseFilter>) context.get("filters.onResponse");
        if (responseFilters != null) {
            for (ResponseFilter responseFilter : responseFilters) {
                responseFilter.filter(respFile, context);
            }
        }

        checkRespFile(respFile, ofx);

        return true;
    }

    protected void checkRespFile(File respFile, com.hungle.tools.moneyutils.fi.props.OFX ofx) throws IOException {
        checkVersionedRespFile(respFile, ofx);
    }

    public static void checkVersionedRespFile(File respFile, com.hungle.tools.moneyutils.fi.props.OFX ofx) throws IOException {
        int version = 1;
        String ofxVersion = null;

        if (ofx != null) {
            ofxVersion = ofx.getVersion();
        }
        if (ofxVersion != null) {
            try {
                version = Integer.valueOf(ofxVersion);
            } catch (NumberFormatException e) {
                log.warn(e);
                version = 1;
            }
        }

        if (version == 1) {
            checkRespFileV1(respFile);
        } else if (version == 2) {
            checkRespFileV2(respFile);
        } else {
            log.warn("Unsupported ofx.version=" + version);
        }
    }

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
            if (log.isDebugEnabled()) {
                log.debug("code=" + code);
            }

            expression = "SEVERITY/text()";
            nodeValue = (String) xpath.evaluate(expression, source, returnType);
            if (nodeValue == null) {
                throw new IOException("Cannot find tag <STATUS><SEVERITY>");
            }
            String severity = nodeValue;
            if (log.isDebugEnabled()) {
                log.debug("severity=" + severity);
            }
            // MESSAGE is optional
            expression = "MESSAGE/text()";
            nodeValue = (String) xpath.evaluate(expression, source, returnType);
            // if (nodeValue == null) {
            // throw new IOException("Cannot find tag <STATUS><MESSAGE>");
            // }
            String message = nodeValue;
            if (log.isDebugEnabled()) {
                log.debug("message=" + message);
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

    public static void checkRespFileV1(File respFile) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(respFile));
            checkRespFileV1(reader);
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

            sb.append(fixesOfxV1LongLine(line));
        }

        String str = sb.toString();
        if (log.isDebugEnabled()) {
            log.debug(str);
        }
        checkRespFileV1(str);
    }

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
                    log.warn(e);
                } finally {
                    reader = null;
                }
            }
        }
    }

    public VelocityContext createVelocityContext() throws IOException {
        if (dir == null) {
            throw new IOException("dir is null.");
        }
        if (!dir.isDirectory()) {
            throw new IOException("dir=" + dir + " is not a directory.");
        }

        File propsFile = new File(dir, propertiesFileName);
        VelocityContext context = PropertiesUtils.createVelocityContext(propsFile);
        return context;
    }

    public static String getTemplate(VelocityContext context) {
        String requestType = (String) context.get("requestType");
        if (PropertiesUtils.isNull(requestType)) {
            log.error("Cannot create template, requestType is null");
            return null;
        }
        com.hungle.tools.moneyutils.fi.props.OFX ofx = (com.hungle.tools.moneyutils.fi.props.OFX) context.get("ofx");
        if (ofx == null) {
            log.error("Cannot create template, OFX object is null");
            return null;
        }
        String version = ofx.getVersion();
        if (PropertiesUtils.isNull(version)) {
            log.error("Cannot create template, OFX version is null");
        }
        return requestType + "-v" + version + ".vm";
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getRequestFileName() {
        return requestFileName;
    }

    public void setRequestFileName(String requestFileName) {
        this.requestFileName = requestFileName;
    }

    public String getRespFileName() {
        return respFileName;
    }

    public void setRespFileName(String respFileName) {
        this.respFileName = respFileName;
    }

    public File getReqFile() {
        return reqFile;
    }

    public File getRespFile() {
        return respFile;
    }

    public File getDir() {
        return dir;
    }

    public void setDir(File dir) {
        this.dir = dir;
    }

}
