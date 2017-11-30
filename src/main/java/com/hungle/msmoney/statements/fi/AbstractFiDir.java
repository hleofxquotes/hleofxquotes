package com.hungle.msmoney.statements.fi;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;

import com.hungle.msmoney.core.misc.CheckNullUtils;
import com.hungle.msmoney.statements.fi.props.FIBean;
import com.hungle.msmoney.statements.fi.props.HttpProperties;
import com.hungle.msmoney.statements.fi.props.PropertiesUtils;
import com.hungle.msmoney.statements.scrubber.ResponseFilter;

// TODO: Auto-generated Javadoc
/**
 * The Class UpdateFiDir.
 */
public abstract class AbstractFiDir {
    
    /** The Constant LOGGER. */
    private static final Logger LOGGER = Logger.getLogger(AbstractFiDir.class);
    
    /** The Constant DEFAULT_PROPERTIES_FILENAME. */
    public static final String DEFAULT_PROPERTIES_FILENAME = "fi.properties";
    
    /** The dir. */
    private File dir;
    
    /** The properties file name. */
    private String propertiesFileName = DEFAULT_PROPERTIES_FILENAME;
    
    /** The template. */
    private String template;
    
    /** The request file name. */
    private String requestFileName = AbstractFiDir.REQ_FILE_OFX;
    
    /** The req file. */
    private File reqFile;

    /** The resp file name. */
    private String respFileName = AbstractFiDir.RESP_FILE_OFX;
    
    /** The resp file. */
    private File respFile;

    /** The velocity context. */
    private final VelocityContext velocityContext;

    /** The Constant APPLICATION_X_OFX. */
    public static final String APPLICATION_X_OFX = "application/x-ofx";

    /** The Constant DEFAULT_TEMPLATE_ENCODING. */
    public static final String DEFAULT_TEMPLATE_ENCODING = "UTF-8";

    /** The Constant REQ_FILE_OFX. */
    public static final String REQ_FILE_OFX = "req.ofx";

    /** The Constant RESP_FILE_OFX. */
    public static final String RESP_FILE_OFX = "resp.ofx";
    
    static {
        VelocityUtils.initVelocity();
    }

    /**
     * Instantiates a new update fi dir.
     *
     * @param dir the dir
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public AbstractFiDir(File dir) throws IOException {
        this.dir = dir;
        this.velocityContext = createVelocityContext();
    }

    public File generateRequestFileContent() throws IOException {
        String workingTemplate = getWorkingTemplate();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("template=" + workingTemplate);
        }
    
        File reqFile = new File(dir, requestFileName);
        generateRequestFileContent(workingTemplate, reqFile);
        return reqFile;
    }

    /**
     * Update.
     *
     * @return true, if successful
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public boolean sendRequest() throws IOException {
        FIBean fi = PropertiesUtils.getFiBean(velocityContext);
        
        LOGGER.info("FI.name=" + fi.getName());
        
        String url = fi.getUrl();
        if (CheckNullUtils.isNull(url)) {
            LOGGER.warn("SKIP sending request, fi.url is null");
            return false;
        }
        URI uri = URI.create(url);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Sending request to " + url);
        }

        this.reqFile = generateRequestFileContent();

        respFile = new File(dir, respFileName);
        
        HttpProperties httpProperties = PropertiesUtils.getHttpProperties(velocityContext);
        boolean httpsOnly = httpProperties.getHttpsOnly();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("httpsOnly=" + httpsOnly);
        }
        if (httpsOnly) {
            ensureHttpsAlways(uri);
        }

        sendRequest(url, reqFile, respFile, httpProperties);

        List<ResponseFilter> responseFilters = PropertiesUtils.getResponseFilters(velocityContext);
        if (responseFilters != null) {
            for (ResponseFilter responseFilter : responseFilters) {
                responseFilter.filter(respFile, velocityContext);
            }
        }

        checkRespFile(this.respFile);

        return true;
    }

    public void checkRespFile(File respFile) throws IOException {
        com.hungle.msmoney.statements.fi.props.OFX ofx = PropertiesUtils.getOfx(velocityContext);
        checkRespFile(respFile, ofx);
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
    protected abstract void sendRequest(String url, File reqFile, File respFile, HttpProperties httpProperties) throws IOException;


    /**
     * Generate request file content.
     *
     * @param workingTemplate the working template
     * @param reqFile the req file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void generateRequestFileContent(String workingTemplate, File reqFile) throws IOException {
        String encoding = AbstractFiDir.DEFAULT_TEMPLATE_ENCODING;
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
        if (!CheckNullUtils.isNull(this.template)) {
            workingTemplate = this.template;
        } else {
            workingTemplate = VelocityUtils.getTemplateNameFromContext(velocityContext);
        }
        if (CheckNullUtils.isNull(workingTemplate)) {
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
    protected void checkRespFile(File respFile, com.hungle.msmoney.statements.fi.props.OFX ofx) throws IOException {
        ResponseUtils.checkRespFile(respFile, ofx);
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
     * Gets the fi bean.
     *
     * @return the fi bean
     */
    public FIBean getFiBean() {
        return PropertiesUtils.getFiBean(velocityContext);
    }

    /**
     * Check https only.
     *
     * @param uri the uri
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private static void ensureHttpsAlways(URI uri) throws IOException {
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
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("YES, url is https - " + uri.toString());
        }
    }

}
