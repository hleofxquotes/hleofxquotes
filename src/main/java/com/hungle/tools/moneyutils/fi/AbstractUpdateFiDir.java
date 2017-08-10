package com.hungle.tools.moneyutils.fi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
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

// TODO: Auto-generated Javadoc
/**
 * The Class UpdateFiDir.
 */
public abstract class AbstractUpdateFiDir {
    
    /** The Constant LOGGER. */
    private static final Logger LOGGER = Logger.getLogger(AbstractUpdateFiDir.class);
    
    /** The Constant DEFAULT_PROPERTIES_FILENAME. */
    public static final String DEFAULT_PROPERTIES_FILENAME = "fi.properties";
    
    /** The dir. */
    private File dir;
    
    /** The properties file name. */
    private String propertiesFileName = DEFAULT_PROPERTIES_FILENAME;
    
    /** The template. */
    private String template;
    
    /** The request file name. */
    private String requestFileName = AbstractUpdateFiDir.REQ_FILE_OFX;
    
    /** The req file. */
    private File reqFile;

    /** The resp file name. */
    private String respFileName = AbstractUpdateFiDir.RESP_FILE_OFX;
    
    /** The resp file. */
    private File respFile;

    /** The velocity context. */
    private final VelocityContext velocityContext;

    /** The Constant APPLICATION_X_OFX. */
    static final String APPLICATION_X_OFX = "application/x-ofx";

    /** The Constant DEFAULT_TEMPLATE_ENCODING. */
    public static final String DEFAULT_TEMPLATE_ENCODING = "UTF-8";

    /** The Constant RESP_FILE_OFX. */
    public static final String RESP_FILE_OFX = "resp.ofx";

    /** The Constant REQ_FILE_OFX. */
    public static final String REQ_FILE_OFX = "req.ofx";

    /**
     * Instantiates a new update fi dir.
     *
     * @param dir the dir
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public AbstractUpdateFiDir(File dir) throws IOException {
        this.dir = dir;
        this.velocityContext = createVelocityContext();
    }

    /**
     * Update.
     *
     * @return true, if successful
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public boolean update() throws IOException {
        String workingTemplate = getWorkingTemplate();
        LOGGER.info("template=" + workingTemplate);

        reqFile = new File(dir, requestFileName);
        generateRequestFileContent(workingTemplate, reqFile);

        respFile = new File(dir, respFileName);

        HttpProperties httpProperties = (HttpProperties) velocityContext.get("httpProperties");
        FIBean fi = (FIBean) velocityContext.get("fi");
        String url = fi.getUrl();
        if (PropertiesUtils.isNull(url)) {
            LOGGER.warn("Skip sending request, fi.url is null");
            return false;
        }

        LOGGER.info("FI.name=" + fi.getName());
        LOGGER.info("Sending request to " + url);
        
        URI uri = URI.create(url);
        
        boolean httpsOnly = httpProperties.getHttpsOnly();
        LOGGER.info("httpsOnly=" + httpsOnly);
        if (httpsOnly) {
            checkHttpsOnly(uri);
        }

        update(url, reqFile, respFile, httpProperties);

        List<ResponseFilter> responseFilters = (List<ResponseFilter>) velocityContext.get("filters.onResponse");
        if (responseFilters != null) {
            for (ResponseFilter responseFilter : responseFilters) {
                responseFilter.filter(respFile, velocityContext);
            }
        }

        com.hungle.tools.moneyutils.fi.props.OFX ofx = (com.hungle.tools.moneyutils.fi.props.OFX) velocityContext.get("ofx");
        checkRespFile(respFile, ofx);

        return true;
    }

    /**
     * Update.
     *
     * @param url the url
     * @param reqFile the req file
     * @param respFile the resp file
     * @param httpProperties the http properties
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected abstract void update(String url, File reqFile, File respFile, HttpProperties httpProperties) throws IOException;


    /**
     * Generate request file content.
     *
     * @param workingTemplate the working template
     * @param reqFile the req file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void generateRequestFileContent(String workingTemplate, File reqFile) throws IOException {
        String encoding = AbstractUpdateFiDir.DEFAULT_TEMPLATE_ENCODING;
        VelocityUtils.mergeTemplate(velocityContext, workingTemplate, encoding, reqFile);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Created reqFile=" + reqFile);
        }
    }

    /**
     * Gets the working template.
     *
     * @return the working template
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private String getWorkingTemplate() throws IOException {
        String workingTemplate = null;
        if (!PropertiesUtils.isNull(this.template)) {
            workingTemplate = this.template;
        } else {
            workingTemplate = getTemplateFromContext(velocityContext);
        }
        if (PropertiesUtils.isNull(workingTemplate)) {
            throw new IOException("template is null");
        }

        workingTemplate = "/templates/" + workingTemplate;
        return workingTemplate;
    }

    /**
     * Check resp file.
     *
     * @param respFile the resp file
     * @param ofx the ofx
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected void checkRespFile(File respFile, com.hungle.tools.moneyutils.fi.props.OFX ofx) throws IOException {
        checkVersionedRespFile(respFile, ofx);
    }

    /**
     * Check versioned resp file.
     *
     * @param respFile the resp file
     * @param ofx the ofx
     * @throws IOException Signals that an I/O exception has occurred.
     */
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
                LOGGER.warn(e);
                version = 1;
            }
        }

        if (version == 1) {
            checkRespFileV1(respFile);
        } else if (version == 2) {
            checkRespFileV2(respFile);
        } else {
            LOGGER.warn("Unsupported ofx.version=" + version);
        }
    }

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
            checkRespFileV1(reader);
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

            sb.append(fixesOfxV1LongLine(line));
        }

        String str = sb.toString();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(str);
        }
        checkRespFileV1(str);
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
     * Creates the velocity context.
     *
     * @return the velocity context
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private VelocityContext createVelocityContext() throws IOException {
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

    /**
     * Gets the template from context.
     *
     * @param context the context
     * @return the template from context
     */
    private static String getTemplateFromContext(VelocityContext context) {
        String requestType = (String) context.get("requestType");
        if (PropertiesUtils.isNull(requestType)) {
            LOGGER.error("Cannot create template, requestType is null");
            return null;
        }
        com.hungle.tools.moneyutils.fi.props.OFX ofx = (com.hungle.tools.moneyutils.fi.props.OFX) context.get("ofx");
        if (ofx == null) {
            LOGGER.error("Cannot create template, OFX object is null");
            return null;
        }
        String version = ofx.getVersion();
        if (PropertiesUtils.isNull(version)) {
            LOGGER.error("Cannot create template, OFX version is null");
        }
        return requestType + "-v" + version + ".vm";
    }

    /**
     * Gets the template.
     *
     * @return the template
     */
    public String getTemplate() {
        return template;
    }

    /**
     * Sets the template.
     *
     * @param template the new template
     */
    public void setTemplate(String template) {
        this.template = template;
    }

    /**
     * Gets the request file name.
     *
     * @return the request file name
     */
    public String getRequestFileName() {
        return requestFileName;
    }

    /**
     * Sets the request file name.
     *
     * @param requestFileName the new request file name
     */
    public void setRequestFileName(String requestFileName) {
        this.requestFileName = requestFileName;
    }

    /**
     * Gets the resp file name.
     *
     * @return the resp file name
     */
    public String getRespFileName() {
        return respFileName;
    }

    /**
     * Sets the resp file name.
     *
     * @param respFileName the new resp file name
     */
    public void setRespFileName(String respFileName) {
        this.respFileName = respFileName;
    }

    /**
     * Gets the req file.
     *
     * @return the req file
     */
    public File getReqFile() {
        return reqFile;
    }

    /**
     * Gets the resp file.
     *
     * @return the resp file
     */
    public File getRespFile() {
        return respFile;
    }

    /**
     * Gets the dir.
     *
     * @return the dir
     */
    public File getDir() {
        return dir;
    }

    /**
     * Sets the dir.
     *
     * @param dir the new dir
     */
    public void setDir(File dir) {
        this.dir = dir;
    }

    /**
     * Gets the velocity context.
     *
     * @return the velocity context
     */
    public VelocityContext getVelocityContext() {
        return velocityContext;
    }

    /**
     * Gets the fi bean.
     *
     * @return the fi bean
     */
    public FIBean getFiBean() {
        VelocityContext context = getVelocityContext();
        FIBean fi = (FIBean) context.get("fi");
        return fi;
    }

    /**
     * Check https only.
     *
     * @param uri the uri
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private static void checkHttpsOnly(URI uri) throws IOException {
        String scheme = uri.getScheme();
        if (scheme == null) {
            throw new IOException("URL must be https, uri=" + uri.toString());
        }
        if (scheme.length() <= 0) {
            throw new IOException("URL must be https, uri=" + uri.toString());
        }
        if (scheme.compareToIgnoreCase("https") != 0) {
            throw new IOException("URL must be https, uri=" + uri.toString());
        }
        LOGGER.info("YES, url is https - " + uri.toString());
    }

}
