package com.le.tools.moneyutils.fi.props;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.log4j.Logger;


public class Account {
    private static final Logger log = Logger.getLogger(Account.class);

    private String bankId;
    private String id;
    private String type;

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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
                                log.error(e);
                            } catch (InvocationTargetException e) {
                                log.error(e);
                            }
                        }
                    }
                }
            } catch (NumberFormatException e) {
                log.warn(e);
            }
        }
        return accounts;
    }

}
