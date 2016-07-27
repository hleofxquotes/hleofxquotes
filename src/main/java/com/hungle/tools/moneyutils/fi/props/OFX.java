package com.le.tools.moneyutils.fi.props;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.apache.commons.beanutils.BeanUtilsBean;


public class OFX {
    private String version;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public static OFX parseOFX(Properties props, BeanUtilsBean beanUtilsBean) {
        String prefix = "ofx";
    
        OFX ofx = new OFX();
    
        Collection<String> keys = new ArrayList<String>();
        keys.add("version");
        
        PropertiesUtils.setProperties(beanUtilsBean, ofx, prefix, keys, props);
        
        return ofx;
    }
}
