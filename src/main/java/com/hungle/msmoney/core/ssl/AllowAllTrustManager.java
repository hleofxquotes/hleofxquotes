package com.hungle.msmoney.core.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

// TODO: Auto-generated Javadoc
/**
 * The Class AllowAllTrustManager.
 */
public final class AllowAllTrustManager implements X509TrustManager {
    
    /* (non-Javadoc)
     * @see javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert.X509Certificate[], java.lang.String)
     */
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    /* (non-Javadoc)
     * @see javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.X509Certificate[], java.lang.String)
     */
    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    /* (non-Javadoc)
     * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
     */
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }
}