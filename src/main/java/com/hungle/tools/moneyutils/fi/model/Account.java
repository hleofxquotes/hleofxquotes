package com.hungle.tools.moneyutils.fi.model;

// TODO: Auto-generated Javadoc
/**
 * The Class Account.
 *
 * @author lobas_av
 */
public class Account extends AbstractModelObject {
    
    /** The m name. */
    private String m_name;
    
    /** The m email. */
    private String m_email;
    
    /** The m phone. */
    private String m_phone;
    
    /** The m mobile phone 1. */
    private String m_mobilePhone1;
    
    /** The m mobile phone 2. */
    private String m_mobilePhone2;

    /**
     * Instantiates a new account.
     */
    public Account() {
        m_name = "???????";
    }

    /**
     * Instantiates a new account.
     *
     * @param name the name
     * @param email the email
     * @param phone the phone
     * @param phone1 the phone 1
     * @param phone2 the phone 2
     */
    public Account(String name, String email, String phone, String phone1, String phone2) {
        m_name = name;
        m_email = email;
        m_phone = phone;
        m_mobilePhone1 = phone1;
        m_mobilePhone2 = phone2;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return m_name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        String oldValue = m_name;
        m_name = name;
        firePropertyChange("name", oldValue, m_name);
    }

    /**
     * Gets the email.
     *
     * @return the email
     */
    public String getEmail() {
        return m_email;
    }

    /**
     * Sets the email.
     *
     * @param email the new email
     */
    public void setEmail(String email) {
        String oldValue = m_email;
        m_email = email;
        firePropertyChange("email", oldValue, m_email);
    }

    /**
     * Gets the phone.
     *
     * @return the phone
     */
    public String getPhone() {
        return m_phone;
    }

    /**
     * Sets the phone.
     *
     * @param phone the new phone
     */
    public void setPhone(String phone) {
        String oldValue = m_phone;
        m_phone = phone;
        firePropertyChange("phone", oldValue, m_phone);
    }

    /**
     * Gets the mobile phone 1.
     *
     * @return the mobile phone 1
     */
    public String getMobilePhone1() {
        return m_mobilePhone1;
    }

    /**
     * Sets the mobile phone 1.
     *
     * @param phone1 the new mobile phone 1
     */
    public void setMobilePhone1(String phone1) {
        String oldValue = m_mobilePhone1;
        m_mobilePhone1 = phone1;
        firePropertyChange("mobilePhone1", oldValue, m_mobilePhone1);
    }

    /**
     * Gets the mobile phone 2.
     *
     * @return the mobile phone 2
     */
    public String getMobilePhone2() {
        return m_mobilePhone2;
    }

    /**
     * Sets the mobile phone 2.
     *
     * @param phone2 the new mobile phone 2
     */
    public void setMobilePhone2(String phone2) {
        String oldValue = m_mobilePhone2;
        m_mobilePhone2 = phone2;
        firePropertyChange("mobilePhone2", oldValue, m_mobilePhone2);
    }
}