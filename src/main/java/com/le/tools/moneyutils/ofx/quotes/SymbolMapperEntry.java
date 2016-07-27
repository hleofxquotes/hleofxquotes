/**
 * 
 */
package com.le.tools.moneyutils.ofx.quotes;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.csvreader.CsvReader;

public class SymbolMapperEntry {
    private static final Logger log = Logger.getLogger(SymbolMapperEntry.class);

    private String msMoneySymbol;
    private String quotesSourceSymbol;
    private boolean isMutualFund;
    private boolean isOptions;
    private boolean isBond;
    private String msMoneyCurrency;
    private String quotesSourceCurrency;

    public String getMsMoneySymbol() {
        return msMoneySymbol;
    }

    public void setMsMoneySymbol(String msMoneySymbol) {
        this.msMoneySymbol = msMoneySymbol;
    }

    public String getQuotesSourceSymbol() {
        return quotesSourceSymbol;
    }

    public void setQuotesSourceSymbol(String quotesSourceSymbol) {
        this.quotesSourceSymbol = quotesSourceSymbol;
    }

    public boolean isMutualFund() {
        return isMutualFund;
    }

    public void setMutualFund(boolean isMutualFund) {
        this.isMutualFund = isMutualFund;
    }

    public boolean isOptions() {
        return isOptions;
    }

    public void setOptions(boolean isOptions) {
        this.isOptions = isOptions;
    }

    public boolean isBond() {
        return isBond;
    }

    public void setBond(boolean isBond) {
        this.isBond = isBond;
    }

    public String getMsMoneyCurrency() {
        return msMoneyCurrency;
    }

    public void setMsMoneyCurrency(String msMoneyCurrency) {
        this.msMoneyCurrency = msMoneyCurrency;
    }

    public String getQuotesSourceCurrency() {
        return quotesSourceCurrency;
    }

    public void setQuotesSourceCurrency(String quotesSourceCurrency) {
        this.quotesSourceCurrency = quotesSourceCurrency;
    }

    public void load(CsvReader csvReader) throws IOException {
        // MSMoneySymbol
        String msMoneySymbol = csvReader.get("MSMoneySymbol");
        if (isNull(msMoneySymbol)) {
            log.warn("MSMoneySymbol column is blank");
            return;
        } else {
            setMsMoneySymbol(msMoneySymbol);
        }

        // QuotesSourceSymbol
        String quotesSourceSymbol = csvReader.get("QuotesSourceSymbol");
        if (isNull(quotesSourceSymbol)) {
            log.warn("QuotesSourceSymbol column is blank");
            return;
        } else {
            setQuotesSourceSymbol(quotesSourceSymbol);
        }

        // IsMutualFund
        String isMutualFund = csvReader.get("IsMutualFund");
        if (isNull(quotesSourceSymbol)) {
            setMutualFund(false);
        } else {
            setMutualFund(Boolean.valueOf(isMutualFund));
        }

        // IsOptions
        String isOptions = csvReader.get("IsOptions");
        if (isNull(quotesSourceSymbol)) {
            setOptions(false);
        } else {
            setOptions(Boolean.valueOf(isOptions));
        }

        // Type
        String type = csvReader.get("Type");
        if (isNull(type)) {
            if (log.isDebugEnabled()) {
                log.warn("Type column is blank");
            }
            type = "STOCK";
        } else {
            if (type.compareToIgnoreCase("STOCK") == 0) {
            } else if (type.compareToIgnoreCase("MFUND") == 0) {
                setMutualFund(true);
                setOptions(false);
                setBond(false);
            } else if (type.compareToIgnoreCase("OPTIONS") == 0) {
                setMutualFund(false);
                setOptions(true);
                setBond(false);
            } else if (type.compareToIgnoreCase("BOND") == 0) {
                setMutualFund(false);
                setOptions(false);
                setBond(true);
            }
        }

        // MSMoneyCurrency
        String mSMoneyCurrency = csvReader.get("MSMoneyCurrency");
        if (isNull(mSMoneyCurrency)) {
            if (log.isDebugEnabled()) {
                log.warn("MSMoneyCurrency column is blank");
            }
        } else {
            setMsMoneyCurrency(mSMoneyCurrency);
        }

        // QuotesSourceCurrency
        String quotesSourceCurrency = csvReader.get("QuotesSourceCurrency");
        if (isNull(quotesSourceCurrency)) {
            if (log.isDebugEnabled()) {
                log.warn("QuotesSourceCurrency column is blank");
            }
        } else {
            setQuotesSourceCurrency(quotesSourceCurrency);
        }
    }

    private static boolean isNull(String str) {
        if (str == null) {
            return true;
        }

        if (str.length() <= 0) {
            return true;
        }

        return false;
    }

    public String getType() {
        String type = "STOCK";
        
        if (isMutualFund()) {
            type = "MFUND";
        }
        
        if (isBond()) {
            type = "BOND";
        }
        
        if (isOptions()) {
            type = "OPTIONS";
        }
        
        return type;
    }
}