package com.hungle.tools.moneyutils.beansbinding;

import org.apache.log4j.Logger;

public class FinancialInstitution {
    private static final Logger log = Logger.getLogger(FinancialInstitution.class);

    private String name;

    private String id;

    private String org;

    private String url;

    private String brokerId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        log.info("setName: name=" + name);
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
}
