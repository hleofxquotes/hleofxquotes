package com.hungle.msmoney.statements.fi;

import java.io.File;

import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.log4j.Logger;

import com.hungle.msmoney.core.encryption.EncryptionHelper;
import com.hungle.tools.moneyutils.fi.props.HttpProperties;
import com.hungle.tools.moneyutils.ssl.CertificatesChanges;

// TODO: Auto-generated Javadoc
/**
 * The Class OfxPostClientParams.
 */
public class OfxPostClientParams {
    
    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(OfxPostClientParams.class);
    
    /** The uri string. */
    private String uriString;
    
    /** The req file. */
    private File reqFile;
    
    /** The resp file. */
    private File respFile;
    
    /** The http properties. */
    private HttpProperties httpProperties;
    
    /** The trust strategy. */
    private TrustStrategy trustStrategy;
    
    /** The encryption helper. */
    private EncryptionHelper encryptionHelper;

    /**
     * Instantiates a new ofx post client params.
     *
     * @param url the url
     * @param reqFile the req file
     * @param respFile the resp file
     * @param httpProperties the http properties
     */
    public OfxPostClientParams(String url, File reqFile, File respFile, HttpProperties httpProperties) {
        super();
        setUriString(url);
        setReqFile(reqFile);
        setRespFile(respFile);
        setHttpProperties(httpProperties);

        CertificatesChanges trustStrategy = null;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("getCheckSSLCertificate=" + httpProperties.getCheckSSLCertificate());
        }
        if (httpProperties.getCheckSSLCertificate()) {
            trustStrategy = new CertificatesChanges(url, respFile);
            trustStrategy.setErrorIfCertificatesChanged(httpProperties.getCheckSSLCertificate());
        }
        setTrustStrategy(trustStrategy);
    }

    /**
     * Gets the uri string.
     *
     * @return the uri string
     */
    public String getUriString() {
        return uriString;
    }

    /**
     * Sets the uri string.
     *
     * @param uriString the new uri string
     */
    public void setUriString(String uriString) {
        this.uriString = uriString;
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
     * Sets the req file.
     *
     * @param reqFile the new req file
     */
    public void setReqFile(File reqFile) {
        this.reqFile = reqFile;
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
     * Sets the resp file.
     *
     * @param respFile the new resp file
     */
    public void setRespFile(File respFile) {
        this.respFile = respFile;
    }

    /**
     * Gets the trust strategy.
     *
     * @return the trust strategy
     */
    public TrustStrategy getTrustStrategy() {
        return trustStrategy;
    }

    /**
     * Sets the trust strategy.
     *
     * @param trustStrategy the new trust strategy
     */
    public void setTrustStrategy(TrustStrategy trustStrategy) {
        this.trustStrategy = trustStrategy;
    }

    /**
     * Gets the http properties.
     *
     * @return the http properties
     */
    public HttpProperties getHttpProperties() {
        return httpProperties;
    }

    /**
     * Sets the http properties.
     *
     * @param httpProperties the new http properties
     */
    public void setHttpProperties(HttpProperties httpProperties) {
        this.httpProperties = httpProperties;
    }

    /**
     * Gets the encryption helper.
     *
     * @return the encryption helper
     */
    public EncryptionHelper getEncryptionHelper() {
        return encryptionHelper;
    }

    /**
     * Sets the encryption helper.
     *
     * @param encryptionHelper the new encryption helper
     */
    public void setEncryptionHelper(EncryptionHelper encryptionHelper) {
        this.encryptionHelper = encryptionHelper;
    }

}
