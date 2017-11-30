package com.hungle.msmoney.core.beansbinding;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class FinancialInstitution.
 */
public class FinancialInstitution {
    
    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(FinancialInstitution.class);

    /** The name. */
    private String name;

    /** The id. */
    private String id;

    /** The org. */
    private String org;

    /** The url. */
    private String url;

    /** The broker id. */
    private String brokerId;

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
        LOGGER.info("setName: name=" + name);
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
}
