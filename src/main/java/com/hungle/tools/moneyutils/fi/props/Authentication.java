package com.hungle.tools.moneyutils.fi.props;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.apache.commons.beanutils.BeanUtilsBean;

import com.hungle.tools.moneyutils.fi.AbstractFiContext;


// TODO: Auto-generated Javadoc
/**
 * The Class Authentication.
 */
public class Authentication {
    
    /** The id. */
    private String id;
    
    /** The password. */
    private String password;
    
    /** The language. */
    private String language = AbstractFiContext.LANGUAGE_ENG;

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
     * @param passsword the new password
     */
    public void setPassword(String passsword) {
        this.password = passsword;
    }

    /**
     * Gets the language.
     *
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the language.
     *
     * @param language the new language
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Parses the authentication.
     *
     * @param props the props
     * @param beanUtilsBean the bean utils bean
     * @return the authentication
     */
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
