package com.le.tools.moneyutils.ssl;

import java.io.File;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.log4j.Logger;

import com.le.tools.moneyutils.ofx.quotes.Utils;

public final class CertificatesChanges implements TrustStrategy {
    private static final Logger log = Logger.getLogger(CertificatesChanges.class);

    private final String uriString;

    private File currentCertificates;

    private File savedCertificates;

    private boolean errorIfCertificatesChanged = true;

    public CertificatesChanges(String uriString, File respFile) {
        this.uriString = uriString;
        this.savedCertificates = new File(respFile.getAbsoluteFile().getParentFile(), "savedCertificates.txt");
        this.currentCertificates = new File(respFile.getAbsoluteFile().getParentFile(), "currentCertificates.txt");
    }

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

    public boolean isErrorIfCertificatesChanged() {
        return errorIfCertificatesChanged;
    }

    public void setErrorIfCertificatesChanged(boolean errorIfCertificatesChanged) {
        this.errorIfCertificatesChanged = errorIfCertificatesChanged;
    }
}