package com.le.tools.moneyutils.fi.props;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.apache.commons.beanutils.BeanUtilsBean;


public class FIBean {
    private String name;
    private String org;
    private String brokerId;
    private String id;
    private String url;

    // Does not really belong here?
    private String startDate;

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(String brokerId) {
        this.brokerId = brokerId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public static FIBean parseFI(Properties props, BeanUtilsBean beanUtilsBean) {
        String prefix = "fi";
    
        FIBean bean = new FIBean();
        Collection<String> keys = new ArrayList<String>();
    
        keys.add("name");
        keys.add("org");
        keys.add("id");
        keys.add("brokerId");
        keys.add("url");
    
        PropertiesUtils.setProperties(beanUtilsBean, bean, prefix, keys, props);
        
        return bean;
    }
}
