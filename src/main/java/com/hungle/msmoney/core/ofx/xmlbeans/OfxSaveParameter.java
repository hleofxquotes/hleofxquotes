package com.hungle.msmoney.core.ofx.xmlbeans;

// TODO: Auto-generated Javadoc
/**
 * The Class OfxSaveParameter.
 */
public class OfxSaveParameter {
    
    /** The default currency. */
    private String defaultCurrency = null;
    
    /** The force generating INVTRANLIST. */
    private boolean forceGeneratingINVTRANLIST = false;
    
    /** The date offset. */
    private Integer dateOffset = 0;
    
    /** The account id. */
    private String accountId;

    /**
     * Sets the default currency.
     *
     * @param defaultCurrency the new default currency
     */
    public void setDefaultCurrency(String defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }

    /**
     * Gets the default currency.
     *
     * @return the default currency
     */
    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    /**
     * Sets the force generating INVTRANLIST.
     *
     * @param forceGeneratingINVTRANLIST the new force generating INVTRANLIST
     */
    public void setForceGeneratingINVTRANLIST(boolean forceGeneratingINVTRANLIST) {
        this.forceGeneratingINVTRANLIST = forceGeneratingINVTRANLIST;
    }

    /**
     * Checks if is force generating INVTRANLIST.
     *
     * @return true, if is force generating INVTRANLIST
     */
    public boolean isForceGeneratingINVTRANLIST() {
        return forceGeneratingINVTRANLIST;
    }

    /**
     * Gets the date offset.
     *
     * @return the date offset
     */
    public Integer getDateOffset() {
        return dateOffset;
    }

    /**
     * Sets the date offset.
     *
     * @param dateOffset the new date offset
     */
    public void setDateOffset(Integer dateOffset) {
        this.dateOffset = dateOffset;
    }

    /**
     * Gets the account id.
     *
     * @return the account id
     */
    public String getAccountId() {
        return accountId;
    }

    /**
     * Sets the account id.
     *
     * @param accountId the new account id
     */
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}