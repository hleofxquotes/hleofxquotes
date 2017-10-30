package com.hungle.tools.moneyutils.fi.props;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import javax.swing.filechooser.FileSystemView;

import org.apache.commons.beanutils.BeanUtilsBean;


// TODO: Auto-generated Javadoc
/**
 * The Class FIBean.
 */
public class FIBean {
    
    public static final String FI_PREFIX = "fi";

    /** The Constant DEFAULT_FI_DIR. */
    public static final String DEFAULT_FI_DIR = "fi";

    /** The name. */
    private String name;
    
    /** The org. */
    private String org;
    
    /** The broker id. */
    private String brokerId;
    
    /** The id. */
    private String id;
    
    /** The url. */
    private String url;

    /** The start date. */
    // Does not really belong here?
    private String startDate;

    /**
     * Gets the org.
     *
     * @return the org
     */
    public String getOrg() {
        return org;
    }

    /**
     * Sets the org.
     *
     * @param org the new org
     */
    public void setOrg(String org) {
        this.org = org;
    }

    /**
     * Gets the broker id.
     *
     * @return the broker id
     */
    public String getBrokerId() {
        return brokerId;
    }

    /**
     * Sets the broker id.
     *
     * @param brokerId the new broker id
     */
    public void setBrokerId(String brokerId) {
        this.brokerId = brokerId;
    }

    /**
     * Gets the url.
     *
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the url.
     *
     * @param url the new url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the start date.
     *
     * @return the start date
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * Sets the start date.
     *
     * @param startDate the new start date
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    /**
     * Gets the default fi dir.
     *
     * @return the default fi dir
     */
    public static final String getDefaultFiDir() {
        File dir = FileSystemView.getFileSystemView().getDefaultDirectory();
        if ((dir == null) || (! dir.exists()) || (! dir.isDirectory())) {
            dir = new File(".");
        }
        File fiDir = new File(dir, FIBean.DEFAULT_FI_DIR);
        return fiDir.getAbsolutePath();
    }

    /**
     * Parses the FI.
     *
     * @param props the props
     * @param beanUtilsBean the bean utils bean
     * @return the FI bean
     */
    public static FIBean parse(Properties props, BeanUtilsBean beanUtilsBean) {
        String prefix = FI_PREFIX;
    
        FIBean bean = new FIBean();
        Collection<String> keys = new ArrayList<String>();
    
        keys.add("name");
        keys.add("org");
        keys.add("id");
        keys.add("brokerId");
        keys.add("url");
    
        PropertiesUtils.setProperties(prefix, keys, props, bean, beanUtilsBean);
        
        return bean;
    }
}
