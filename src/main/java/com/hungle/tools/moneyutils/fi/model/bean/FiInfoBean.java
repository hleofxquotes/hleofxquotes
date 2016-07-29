package com.hungle.tools.moneyutils.fi.model.bean;

import org.apache.log4j.Logger;

public class FiInfoBean implements Cloneable {
    private static final Logger LOGGER = Logger.getLogger(FiInfoBean.class);

    private String name;
    private String id;
    private String org;
    private String url;
    private String brokerId;
    private String login;
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(String brokerId) {
        this.brokerId = brokerId;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public FiInfoBean cloneBean() throws CloneNotSupportedException {
        return (FiInfoBean) this.clone();
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
