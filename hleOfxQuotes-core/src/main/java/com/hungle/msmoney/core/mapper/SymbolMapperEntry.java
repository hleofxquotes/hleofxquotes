/**
 * 
 */
package com.hungle.msmoney.core.mapper;

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

    private static final String MS_MONEY_SYMBOL = "MSMoneySymbol";

    private static final String QUOTES_SOURCE_SYMBOL = "QuotesSourceSymbol";

    private static final String IS_MUTUAL_FUND = "IsMutualFund";

    private static final String IS_OPTIONS = "IsOptions";

    private static final String TYPE = "Type";

    private static final String MS_MONEY_CURRENCY = "MSMoneyCurrency";

    private static final String QUOTES_SOURCE_CURRENCY = "QuotesSourceCurrency";

    private static final String TYPE_STOCK = "STOCK";

    private static final String TYPE_MFUND = "MFUND";

    private static final String TYPE_OPTIONS = "OPTIONS";

    private static final String TYPE_BOND = "BOND";
    
    public static final String[] HEADERS = {
            MS_MONEY_SYMBOL,
            QUOTES_SOURCE_SYMBOL,
//            IS_MUTUAL_FUND,
//            IS_OPTIONS,
            MS_MONEY_CURRENCY,
            QUOTES_SOURCE_CURRENCY,
            TYPE,
    };

    /** The ms money symbol. */
    private String msMoneySymbol;
    
    /** The quotes source symbol. */
    private String quotesSourceSymbol;
    
    /** The ms money currency. */
    private String msMoneyCurrency;

    /** The quotes source currency. */
    private String quotesSourceCurrency;

    /** The is mutual fund. */
    private boolean isMutualFund;
    
    /** The is options. */
    private boolean isOptions;
    
    /** The is bond. */
    private boolean isBond;
    
    private Integer bondDivider = null;
    
    @Override
    public String toString() {
        return "SymbolMapperEntry [msMoneySymbol=" + msMoneySymbol + ", quotesSourceSymbol=" + quotesSourceSymbol
                + ", msMoneyCurrency=" + msMoneyCurrency + ", quotesSourceCurrency=" + quotesSourceCurrency
                + ", isMutualFund=" + isMutualFund + ", isOptions=" + isOptions + ", isBond=" + isBond
                + ", bondDivider=" + bondDivider + "]";
    }

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
        String msMoneySymbol = csvReader.get(MS_MONEY_SYMBOL);
        if (CheckNullUtils.isEmpty(msMoneySymbol)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.warn("MSMoneySymbol column is blank");
            }
//            return;
        } else {
            setMsMoneySymbol(msMoneySymbol);
        }

        // QuotesSourceSymbol
        String quotesSourceSymbol = csvReader.get(QUOTES_SOURCE_SYMBOL);
        if (CheckNullUtils.isEmpty(quotesSourceSymbol)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.warn("QuotesSourceSymbol column is blank");
            }
//            return;
        } else {
            setQuotesSourceSymbol(quotesSourceSymbol);
        }
        
        if ((CheckNullUtils.isEmpty(msMoneySymbol)) && (CheckNullUtils.isEmpty(quotesSourceSymbol))) {
            LOGGER.warn("MSMoneySymbol column is blank");
            LOGGER.warn("QuotesSourceSymbol column is blank");
            return;
        } else if (CheckNullUtils.isEmpty(msMoneySymbol)) {
            msMoneySymbol = getQuotesSourceSymbol();
            setMsMoneySymbol(msMoneySymbol);
        } else {
            // CheckNullUtils.isEmpty(quotesSourceSymbol)
            quotesSourceSymbol = getMsMoneySymbol();
            setQuotesSourceSymbol(quotesSourceSymbol);
        }

        // IsMutualFund
        String isMutualFund = csvReader.get(IS_MUTUAL_FUND);
        if (CheckNullUtils.isEmpty(quotesSourceSymbol)) {
            setMutualFund(false);
        } else {
            setMutualFund(Boolean.valueOf(isMutualFund));
        }

        // IsOptions
        String isOptions = csvReader.get(IS_OPTIONS);
        if (CheckNullUtils.isEmpty(quotesSourceSymbol)) {
            setOptions(false);
        } else {
            setOptions(Boolean.valueOf(isOptions));
        }

        // Type
        String type = csvReader.get(TYPE);
        if (CheckNullUtils.isEmpty(type)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.warn("Type column is blank");
            }
            type = TYPE_STOCK;
        } else {
            setType(type);
        }

        // MSMoneyCurrency
        String mSMoneyCurrency = csvReader.get(MS_MONEY_CURRENCY);
        if (CheckNullUtils.isEmpty(mSMoneyCurrency)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.warn("MSMoneyCurrency column is blank");
            }
        } else {
            setMsMoneyCurrency(mSMoneyCurrency);
        }

        // QuotesSourceCurrency
        String quotesSourceCurrency = csvReader.get(QUOTES_SOURCE_CURRENCY);
        if (CheckNullUtils.isEmpty(quotesSourceCurrency)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.warn("QuotesSourceCurrency column is blank");
            }
        } else {
            setQuotesSourceCurrency(quotesSourceCurrency);
        }
    }

    public void setType(String type) {
        String subType = null;
        String[] tokens = type.split("/");
        if (tokens.length == 1) {
            // no change
        } else if (tokens.length == 2) {
            // BOND/100
            type = tokens[0];
            subType = tokens[1];
        }
        
        if (type.compareToIgnoreCase(TYPE_STOCK) == 0) {
        } else if (type.compareToIgnoreCase(TYPE_MFUND) == 0) {
            setMutualFund(true);
            setOptions(false);
            setBond(false);
        } else if (type.compareToIgnoreCase(TYPE_OPTIONS) == 0) {
            setMutualFund(false);
            setOptions(true);
            setBond(false);
        } else if (type.compareToIgnoreCase(TYPE_BOND) == 0) {
            setMutualFund(false);
            setOptions(false);
            setBond(true);
            if (! CheckNullUtils.isEmpty(subType)) {
                Integer bondDivider = Integer.valueOf(subType);
                LOGGER.info("bondDivider=" + bondDivider);
                setBondDivider(bondDivider);
            }
        }
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public String getType() {
        String type = TYPE_STOCK;
        
        if (isMutualFund()) {
            type = TYPE_MFUND;
        }
        
        if (isBond()) {
            type = TYPE_BOND;
        }
        
        if (isOptions()) {
            type = TYPE_OPTIONS;
        }
        
        return type;
    }

    public Integer getBondDivider() {
        return bondDivider;
    }

    public void setBondDivider(Integer bondDivider) {
        this.bondDivider = bondDivider;
    }
}