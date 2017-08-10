package com.hungle.tools.moneyutils.ssl;

import java.io.File;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.log4j.Logger;

import com.hungle.tools.moneyutils.ofx.quotes.Utils;

// TODO: Auto-generated Javadoc
/**
 * The Class CertificatesChanges.
 */
public final class CertificatesChanges implements TrustStrategy {
    
    /** The Constant log. */
    private static final Logger log = Logger.getLogger(CertificatesChanges.class);

    /** The uri string. */
    private final String uriString;

    /** The current certificates. */
    private File currentCertificates;

    /** The saved certificates. */
    private File savedCertificates;

    /** The error if certificates changed. */
    private boolean errorIfCertificatesChanged = true;

    /**
     * Instantiates a new certificates changes.
     *
     * @param uriString the uri string
     * @param respFile the resp file
     */
    public CertificatesChanges(String uriString, File respFile) {
        this.uriString = uriString;
        this.savedCertificates = new File(respFile.getAbsoluteFile().getParentFile(), "savedCertificates.txt");
        this.currentCertificates = new File(respFile.getAbsoluteFile().getParentFile(), "currentCertificates.txt");
    }

    /* (non-Javadoc)
     * @see org.apache.http.ssl.TrustStrategy#isTrusted(java.security.cert.X509Certificate[], java.lang.String)
     */
    @Override
    public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        if (log.isDebugEnabled()) {
            log.debug("> TrustStrategy:" + ", authType=" + authType + ", chain.length=" + chain.length);
            log.debug("  uriString=" + uriString);
        }
        log.info("errorIfCertificatesChanged=" + errorIfCertificatesChanged);

        if (chain != null) {
            CertificateUtils.writeCerts(chain, currentCertificates);
            if (!savedCertificates.exists()) {
                try {
                    Utils.copyFile(currentCertificates, savedCertificates);
                    log.info("SSL CERTIFICATE: Created saved certificates file=" + savedCertificates.getAbsolutePath());
                } catch (IOException e) {
                    log.error(e);
                } finally {

                }
            } else {
                log.info("SSL CERTIFICATE: Already have saved certificates file=" + savedCertificates.getAbsolutePath());
            }
        } else {
            log.info("certificate chain=" + chain);
        }

        boolean certificatesChanged = false;
        try {
            if (!Utils.compareFiles(savedCertificates, currentCertificates)) {
                certificatesChanged = true;
                if (errorIfCertificatesChanged) {
                    throw new CertificateException("SSL certificates has changed, url=" + uriString);
                }
            }
        } catch (IOException e) {
            throw new CertificateException(e);
        } finally {
            if (certificatesChanged) {
                log.info("SSL CERTIFICATE: has CHANGED");
            } else {
                log.info("SSL CERTIFICATE: has NOT CHANGED");
            }
        }

        // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=7113275
        // always return false to let the real trustmanager to check
        return false;
    }

    /**
     * Checks if is error if certificates changed.
     *
     * @return true, if is error if certificates changed
     */
    public boolean isErrorIfCertificatesChanged() {
        return errorIfCertificatesChanged;
    }

    /**
     * Sets the error if certificates changed.
     *
     * @param errorIfCertificatesChanged the new error if certificates changed
     */
    public void setErrorIfCertificatesChanged(boolean errorIfCertificatesChanged) {
        this.errorIfCertificatesChanged = errorIfCertificatesChanged;
    }
}