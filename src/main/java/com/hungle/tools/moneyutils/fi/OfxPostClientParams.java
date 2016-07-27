package com.hungle.tools.moneyutils.fi;

import java.io.File;

import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.log4j.Logger;

import com.hungle.tools.moneyutils.encryption.EncryptionHelper;
import com.hungle.tools.moneyutils.fi.props.HttpProperties;
import com.hungle.tools.moneyutils.ssl.CertificatesChanges;

public class OfxPostClientParams {
    private static final Logger log = Logger.getLogger(OfxPostClientParams.class);
    
    private String uriString;
    private File reqFile;
    private File respFile;
    private HttpProperties httpProperties;
    private TrustStrategy trustStrategy;
    private EncryptionHelper encryptionHelper;

    public OfxPostClientParams(String url, File reqFile, File respFile, HttpProperties httpProperties) {
        super();
        setUriString(url);
        setReqFile(reqFile);
        setRespFile(respFile);
        setHttpProperties(httpProperties);

        CertificatesChanges trustStrategy = null;
        log.info("getCheckSSLCertificate=" + httpProperties.getCheckSSLCertificate());
        if (httpProperties.getCheckSSLCertificate()) {
            trustStrategy = new CertificatesChanges(url, respFile);
            trustStrategy.setErrorIfCertificatesChanged(httpProperties.getCheckSSLCertificate());
        }
        setTrustStrategy(trustStrategy);
    }

    public String getUriString() {
        return uriString;
    }

    public void setUriString(String uriString) {
        this.uriString = uriString;
    }

    public File getReqFile() {
        return reqFile;
    }

    public void setReqFile(File reqFile) {
        this.reqFile = reqFile;
    }

    public File getRespFile() {
        return respFile;
    }

    public void setRespFile(File respFile) {
        this.respFile = respFile;
    }

    public TrustStrategy getTrustStrategy() {
        return trustStrategy;
    }

    public void setTrustStrategy(TrustStrategy trustStrategy) {
        this.trustStrategy = trustStrategy;
    }

    public HttpProperties getHttpProperties() {
        return httpProperties;
    }

    public void setHttpProperties(HttpProperties httpProperties) {
        this.httpProperties = httpProperties;
    }

    public EncryptionHelper getEncryptionHelper() {
        return encryptionHelper;
    }

    public void setEncryptionHelper(EncryptionHelper encryptionHelper) {
        this.encryptionHelper = encryptionHelper;
    }

}
