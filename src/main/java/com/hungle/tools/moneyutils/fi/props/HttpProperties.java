package com.le.tools.moneyutils.fi.props;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.apache.commons.beanutils.BeanUtilsBean;


public class HttpProperties {
    private static final boolean DEFAULT_HTTPS_ONLY = true;
    private static final boolean DEFAULT_CHECK_SSL_CERTIFICATE = true;
    private static final boolean DEFAULT_ACCEPT_ANY_SSL_CERTIFICATE = false;

    private boolean checkSSLCertificate = DEFAULT_CHECK_SSL_CERTIFICATE;

    private boolean httpsOnly = DEFAULT_HTTPS_ONLY;

    private boolean acceptAnySslCertificate = DEFAULT_ACCEPT_ANY_SSL_CERTIFICATE;

    public boolean isAcceptAnySslCertificate() {
        return acceptAnySslCertificate;
    }

    public void setAcceptAnySslCertificate(boolean acceptAnySslCertificate) {
        this.acceptAnySslCertificate = acceptAnySslCertificate;
    }

    public boolean getCheckSSLCertificate() {
        return checkSSLCertificate;
    }

    public void setCheckSSLCertificate(boolean checkSSLCertificate) {
        this.checkSSLCertificate = checkSSLCertificate;
    }

    public boolean getHttpsOnly() {
        return httpsOnly;
    }

    public void setHttpsOnly(boolean httpsOnly) {
        this.httpsOnly = httpsOnly;
    }

    public static HttpProperties parseHttpProperties(Properties props, BeanUtilsBean beanUtilsBean) {
        HttpProperties bean = new HttpProperties();
        String prefix = "http";
        Collection<String> keys = new ArrayList<String>();
    
        keys.add("checkSSLCertificate");
        keys.add("httpsOnly");
        keys.add("acceptAnySslCertificate");
    
        PropertiesUtils.setProperties(beanUtilsBean, bean, prefix, keys, props);
        return bean;
    }
}
