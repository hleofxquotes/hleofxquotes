package com.hungle.msmoney.stmt.fi.props;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.apache.commons.beanutils.BeanUtilsBean;


// TODO: Auto-generated Javadoc
/**
 * The Class OFX.
 */
public class OFX {
    
    public static final String PREFIX = "ofx";
    public static final String KEY_VERSION = "version";
    /** The version. */
    private String version;

    /**
     * Gets the version.
     *
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the version.
     *
     * @param version the new version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Parses the OFX.
     *
     * @param props the props
     * @param beanUtilsBean the bean utils bean
     * @return the ofx
     */
    public static OFX parse(Properties props, BeanUtilsBean beanUtilsBean) {
        String prefix = PREFIX;
    
        OFX ofx = new OFX();
    
        Collection<String> keys = new ArrayList<String>();
        keys.add(KEY_VERSION);
        
        PropertiesUtils.setProperties(prefix, keys, props, ofx, beanUtilsBean);
        
        return ofx;
    }
}
