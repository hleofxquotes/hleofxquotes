package com.le.tools.moneyutils.ofx.xmlbeans;

public class OfxSaveParameter {
    private String defaultCurrency = null;
    private boolean forceGeneratingINVTRANLIST = false;
    private Integer dateOffset = 0;
    private String accountId;

    public void setDefaultCurrency(String defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }

    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setForceGeneratingINVTRANLIST(boolean forceGeneratingINVTRANLIST) {
        this.forceGeneratingINVTRANLIST = forceGeneratingINVTRANLIST;
    }

    public boolean isForceGeneratingINVTRANLIST() {
        return forceGeneratingINVTRANLIST;
    }

    public Integer getDateOffset() {
        return dateOffset;
    }

    public void setDateOffset(Integer dateOffset) {
        this.dateOffset = dateOffset;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}