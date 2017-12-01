/**
 * 
 */
package com.hungle.msmoney.core.data;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.csvreader.CsvReader;
import com.hungle.msmoney.core.misc.CheckNullUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class SymbolMapperEntry.
 */
public class SymbolMapperEntry {
    
    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(SymbolMapperEntry.class);

    /** The ms money symbol. */
    private String msMoneySymbol;
    
    /** The quotes source symbol. */
    private String quotesSourceSymbol;
    
    /** The is mutual fund. */
    private boolean isMutualFund;
    
    /** The is options. */
    private boolean isOptions;
    
    /** The is bond. */
    private boolean isBond;
    
    /** The ms money currency. */
    private String msMoneyCurrency;
    
    /** The quotes source currency. */
    private String quotesSourceCurrency;

    /**
     * Gets the ms money symbol.
     *
     * @return the ms money symbol
     */
    public String getMsMoneySymbol() {
        return msMoneySymbol;
    }

    /**
     * Sets the ms money symbol.
     *
     * @param msMoneySymbol the new ms money symbol
     */
    public void setMsMoneySymbol(String msMoneySymbol) {
        this.msMoneySymbol = msMoneySymbol;
    }

    /**
     * Gets the quotes source symbol.
     *
     * @return the quotes source symbol
     */
    public String getQuotesSourceSymbol() {
        return quotesSourceSymbol;
    }

    /**
     * Sets the quotes source symbol.
     *
     * @param quotesSourceSymbol the new quotes source symbol
     */
    public void setQuotesSourceSymbol(String quotesSourceSymbol) {
        this.quotesSourceSymbol = quotesSourceSymbol;
    }

    /**
     * Checks if is mutual fund.
     *
     * @return true, if is mutual fund
     */
    public boolean isMutualFund() {
        return isMutualFund;
    }

    /**
     * Sets the mutual fund.
     *
     * @param isMutualFund the new mutual fund
     */
    public void setMutualFund(boolean isMutualFund) {
        this.isMutualFund = isMutualFund;
    }

    /**
     * Checks if is options.
     *
     * @return true, if is options
     */
    public boolean isOptions() {
        return isOptions;
    }

    /**
     * Sets the options.
     *
     * @param isOptions the new options
     */
    public void setOptions(boolean isOptions) {
        this.isOptions = isOptions;
    }

    /**
     * Checks if is bond.
     *
     * @return true, if is bond
     */
    public boolean isBond() {
        return isBond;
    }

    /**
     * Sets the bond.
     *
     * @param isBond the new bond
     */
    public void setBond(boolean isBond) {
        this.isBond = isBond;
    }

    /**
     * Gets the ms money currency.
     *
     * @return the ms money currency
     */
    public String getMsMoneyCurrency() {
        return msMoneyCurrency;
    }

    /**
     * Sets the ms money currency.
     *
     * @param msMoneyCurrency the new ms money currency
     */
    public void setMsMoneyCurrency(String msMoneyCurrency) {
        this.msMoneyCurrency = msMoneyCurrency;
    }

    /**
     * Gets the quotes source currency.
     *
     * @return the quotes source currency
     */
    public String getQuotesSourceCurrency() {
        return quotesSourceCurrency;
    }

    /**
     * Sets the quotes source currency.
     *
     * @param quotesSourceCurrency the new quotes source currency
     */
    public void setQuotesSourceCurrency(String quotesSourceCurrency) {
        this.quotesSourceCurrency = quotesSourceCurrency;
    }

    /**
     * Load.
     *
     * @param csvReader the csv reader
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void load(CsvReader csvReader) throws IOException {
        // MSMoneySymbol
        String msMoneySymbol = csvReader.get("MSMoneySymbol");
        if (CheckNullUtils.isNull(msMoneySymbol)) {
            LOGGER.warn("MSMoneySymbol column is blank");
            return;
        } else {
            setMsMoneySymbol(msMoneySymbol);
        }

        // QuotesSourceSymbol
        String quotesSourceSymbol = csvReader.get("QuotesSourceSymbol");
        if (CheckNullUtils.isNull(quotesSourceSymbol)) {
            LOGGER.warn("QuotesSourceSymbol column is blank");
            return;
        } else {
            setQuotesSourceSymbol(quotesSourceSymbol);
        }

        // IsMutualFund
        String isMutualFund = csvReader.get("IsMutualFund");
        if (CheckNullUtils.isNull(quotesSourceSymbol)) {
            setMutualFund(false);
        } else {
            setMutualFund(Boolean.valueOf(isMutualFund));
        }

        // IsOptions
        String isOptions = csvReader.get("IsOptions");
        if (CheckNullUtils.isNull(quotesSourceSymbol)) {
            setOptions(false);
        } else {
            setOptions(Boolean.valueOf(isOptions));
        }

        // Type
        String type = csvReader.get("Type");
        if (CheckNullUtils.isNull(type)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.warn("Type column is blank");
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
        if (CheckNullUtils.isNull(mSMoneyCurrency)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.warn("MSMoneyCurrency column is blank");
            }
        } else {
            setMsMoneyCurrency(mSMoneyCurrency);
        }

        // QuotesSourceCurrency
        String quotesSourceCurrency = csvReader.get("QuotesSourceCurrency");
        if (CheckNullUtils.isNull(quotesSourceCurrency)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.warn("QuotesSourceCurrency column is blank");
            }
        } else {
            setQuotesSourceCurrency(quotesSourceCurrency);
        }
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
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