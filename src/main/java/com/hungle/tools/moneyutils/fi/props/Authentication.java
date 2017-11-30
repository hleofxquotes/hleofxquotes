package com.hungle.tools.moneyutils.fi.props;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.apache.commons.beanutils.BeanUtilsBean;

import com.hungle.msmoney.statements.fi.AbstractFiContext;


// TODO: Auto-generated Javadoc
/**
 * The Class Authentication.
 */
public class Authentication {
    
    /** The Constant KEY_PASSWORD. */
    private static final String KEY_PASSWORD = "password";

    /** The Constant KEY_ID. */
    private static final String KEY_ID = "id";

    /** The Constant PREFIX. */
    private static final String PREFIX = "user";

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
    public static Authentication parse(Properties props, BeanUtilsBean beanUtilsBean) {
        String prefix;
        Collection<String> keys;
        Authentication auth = new Authentication();
        prefix = PREFIX;
        keys = new ArrayList<String>();
        keys.add(KEY_ID);
        keys.add(KEY_PASSWORD);
        PropertiesUtils.setProperties(prefix, keys, props, auth, beanUtilsBean);
        return auth;
    }
}
