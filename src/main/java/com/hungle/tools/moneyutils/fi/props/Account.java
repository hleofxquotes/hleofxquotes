package com.hungle.tools.moneyutils.fi.props;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.log4j.Logger;


// TODO: Auto-generated Javadoc
/**
 * The Class Account.
 */
public class Account {
    
    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(Account.class);

    /** The bank id. */
    private String bankId;
    
    /** The id. */
    private String id;
    
    /** The type. */
    private String type;

    /**
     * Gets the bank id.
     *
     * @return the bank id
     */
    public String getBankId() {
        return bankId;
    }

    /**
     * Sets the bank id.
     *
     * @param bankId the new bank id
     */
    public void setBankId(String bankId) {
        this.bankId = bankId;
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
     * Gets the type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type.
     *
     * @param type the new type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Parses the accounts.
     *
     * @param props the props
     * @param beanUtilsBean the bean utils bean
     * @return the list
     */
    public static List<Account> parseAccounts(Properties props, BeanUtilsBean beanUtilsBean) {
        String property;
        List<Account> accounts = new ArrayList<Account>();
        property = props.getProperty("accounts");
        if (!PropertiesUtils.isNull(property)) {
            property = property.trim();
            try {
                int count = Long.valueOf(property).intValue();
                for (int i = 0; i < count; i++) {
                    int index = i + 1;
                    String[] keys = { "bankId", "id", "type", };
                    Account account = new Account();
                    accounts.add(account);
                    for (String key : keys) {
                        property = props.getProperty("account" + "." + index + "." + key);
                        if (!PropertiesUtils.isNull(property)) {
                            property = property.trim();
                            try {
                                beanUtilsBean.setProperty(account, key, property);
                            } catch (IllegalAccessException e) {
                                LOGGER.error(e);
                            } catch (InvocationTargetException e) {
                                LOGGER.error(e);
                            }
                        }
                    }
                }
            } catch (NumberFormatException e) {
                LOGGER.warn(e);
            }
        }
        return accounts;
    }

}
