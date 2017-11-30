package com.hungle.tools.moneyutils.fi.model.bean;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class FiInfoBean.
 */
public class FiInfoBean implements Cloneable {
    
    /** The Constant LOGGER. */
    private static final Logger LOGGER = Logger.getLogger(FiInfoBean.class);

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
    
    /** The login. */
    private String login;
    
    /** The password. */
    private String password;

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

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Clone bean.
     *
     * @return the fi info bean
     * @throws CloneNotSupportedException the clone not supported exception
     */
    public FiInfoBean cloneBean() throws CloneNotSupportedException {
        return (FiInfoBean) this.clone();
    }

    /**
     * Gets the login.
     *
     * @return the login
     */
    public String getLogin() {
        return login;
    }

    /**
     * Sets the login.
     *
     * @param login the new login
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * Gets the password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     *
     * @param password the new password
     */
    public void setPassword(String password) {
        this.password = password;
    }

}
