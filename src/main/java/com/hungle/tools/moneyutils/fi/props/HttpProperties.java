package com.hungle.tools.moneyutils.fi.props;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.apache.commons.beanutils.BeanUtilsBean;


// TODO: Auto-generated Javadoc
/**
 * The Class HttpProperties.
 */
public class HttpProperties {
    
    /** The Constant DEFAULT_HTTPS_ONLY. */
    private static final boolean DEFAULT_HTTPS_ONLY = true;
    
    /** The Constant DEFAULT_CHECK_SSL_CERTIFICATE. */
    private static final boolean DEFAULT_CHECK_SSL_CERTIFICATE = true;
    
    /** The Constant DEFAULT_ACCEPT_ANY_SSL_CERTIFICATE. */
    private static final boolean DEFAULT_ACCEPT_ANY_SSL_CERTIFICATE = false;

    /** The check SSL certificate. */
    private boolean checkSSLCertificate = DEFAULT_CHECK_SSL_CERTIFICATE;

    /** The https only. */
    private boolean httpsOnly = DEFAULT_HTTPS_ONLY;

    /** The accept any ssl certificate. */
    private boolean acceptAnySslCertificate = DEFAULT_ACCEPT_ANY_SSL_CERTIFICATE;

    /**
     * Checks if is accept any ssl certificate.
     *
     * @return true, if is accept any ssl certificate
     */
    public boolean isAcceptAnySslCertificate() {
        return acceptAnySslCertificate;
    }

    /**
     * Sets the accept any ssl certificate.
     *
     * @param acceptAnySslCertificate the new accept any ssl certificate
     */
    public void setAcceptAnySslCertificate(boolean acceptAnySslCertificate) {
        this.acceptAnySslCertificate = acceptAnySslCertificate;
    }

    /**
     * Gets the check SSL certificate.
     *
     * @return the check SSL certificate
     */
    public boolean getCheckSSLCertificate() {
        return checkSSLCertificate;
    }

    /**
     * Sets the check SSL certificate.
     *
     * @param checkSSLCertificate the new check SSL certificate
     */
    public void setCheckSSLCertificate(boolean checkSSLCertificate) {
        this.checkSSLCertificate = checkSSLCertificate;
    }

    /**
     * Gets the https only.
     *
     * @return the https only
     */
    public boolean getHttpsOnly() {
        return httpsOnly;
    }

    /**
     * Sets the https only.
     *
     * @param httpsOnly the new https only
     */
    public void setHttpsOnly(boolean httpsOnly) {
        this.httpsOnly = httpsOnly;
    }

    /**
     * Parses the http properties.
     *
     * @param props the props
     * @param beanUtilsBean the bean utils bean
     * @return the http properties
     */
    public static HttpProperties parseHttpProperties(Properties props, BeanUtilsBean beanUtilsBean) {
        HttpProperties bean = new HttpProperties();
        String prefix = "http";
        Collection<String> keys = new ArrayList<String>();
    
        keys.add("checkSSLCertificate");
        keys.add("httpsOnly");
        keys.add("acceptAnySslCertificate");
    
        PropertiesUtils.setProperties(prefix, keys, props, bean, beanUtilsBean);
        return bean;
    }
}
