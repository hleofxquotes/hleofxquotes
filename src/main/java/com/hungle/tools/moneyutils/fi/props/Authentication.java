package com.le.tools.moneyutils.fi.props;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.apache.commons.beanutils.BeanUtilsBean;

import com.le.tools.moneyutils.fi.AbstractFiContext;


public class Authentication {
    private String id;
    private String password;
    private String language = AbstractFiContext.LANGUAGE_ENG;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String passsword) {
        this.password = passsword;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public static Authentication parseAuthentication(Properties props, BeanUtilsBean beanUtilsBean) {
        String prefix;
        Collection<String> keys;
        Authentication auth = new Authentication();
        prefix = "user";
        keys = new ArrayList<String>();
        keys.add("id");
        keys.add("password");
        PropertiesUtils.setProperties(beanUtilsBean, auth, prefix, keys, props);
        return auth;
    }
}
