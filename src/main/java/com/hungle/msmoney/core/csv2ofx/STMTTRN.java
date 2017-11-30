package com.hungle.msmoney.core.csv2ofx;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class STMTTRN.
 */
public class STMTTRN {
    /*
     * <STMTTRN> <TRNTYPE>DEBIT <DTPOSTED>20110822063000[-6:CST]
     * <DTUSER>20110822063000 <TRNAMT>-9.58 <FITID>201108221462840 <NAME>TARGET
     * NAT'L BK </STMTTRN> <STMTTRN> <TRNTYPE>CREDIT
     * <DTPOSTED>20110822054200[-6:CST] <DTUSER>20110822054200 <TRNAMT>10.00
     * <FITID>201108221387727 <NAME>Alliant CU. : ONLINE WD CO: <MEMO>Alliant
     * CU. : ONLINE WD CO: Alliant CU.WELLS FARGO BANK NA </STMTTRN>
     */
    /** The Constant log. */
    // 11.4.3 Statement Transaction <STMTTRN>
    private static final Logger LOGGER = Logger.getLogger(STMTTRN.class);

    // Transaction type
    // CREDIT Generic credit
    // DEBIT Generic debit
    // INT Interest earned or paid
    // Note: Depends on signage of amount
    // DIV Dividend
    // FEE FI fee
    // SRVCHG Service charge
    // DEP Deposit
    // ATM ATM debit or credit
    // Note: Depends on signage of amount
    // POS
    // Point of sale debit or credit
    // Note: Depends on signage of amount
    // XFER Transfer
    // CHECK Check
    // PAYMENT Electronic payment
    // CASH Cash withdrawal
    // DIRECTDEP Direct deposit
    // DIRECTDEBIT Merchant initiated debit
    // REPEATPMT Repeating payment/standing order
    /** The trntype. */
    // OTHER Other
    private String TRNTYPE = null;

    /** The dtposted. */
    // Date transaction was posted to account, datetime
    private String DTPOSTED = null;

    /** The dtuser. */
    // (Optional) Date user initiated transaction, if known, datetime
    private String DTUSER = null;

    /** The trnamt. */
    // Amount of transaction, amount
    private String TRNAMT = null;

    // Each <STMTTRN> contains an <FITID> that the client uses to detect whether
    /** The fitid. */
    // the server has previously downloaded the transaction.
    private String FITID = null;

    /** The name. */
    // Name of payee or description of transaction
    private String NAME = null;

    /** The memo. */
    // <MEMO> Extra information (not in <NAME>), MEMO
    private String MEMO = null;

    /**
     * Gets the trntype.
     *
     * @return the trntype
     */
    public String getTRNTYPE() {
        return TRNTYPE;
    }

    /**
     * Sets the trntype.
     *
     * @param tRNTYPE the new trntype
     */
    public void setTRNTYPE(String tRNTYPE) {
        TRNTYPE = tRNTYPE;
    }

    /**
     * Gets the dtposted.
     *
     * @return the dtposted
     */
    public String getDTPOSTED() {
        return DTPOSTED;
    }

    /**
     * Sets the dtposted.
     *
     * @param dTPOSTED the new dtposted
     */
    public void setDTPOSTED(String dTPOSTED) {
        DTPOSTED = dTPOSTED;
    }

    /**
     * Gets the dtuser.
     *
     * @return the dtuser
     */
    public String getDTUSER() {
        return DTUSER;
    }

    /**
     * Sets the dtuser.
     *
     * @param dTUSER the new dtuser
     */
    public void setDTUSER(String dTUSER) {
        DTUSER = dTUSER;
    }

    /**
     * Gets the trnamt.
     *
     * @return the trnamt
     */
    public String getTRNAMT() {
        return TRNAMT;
    }

    /**
     * Sets the trnamt.
     *
     * @param tRNAMT the new trnamt
     */
    public void setTRNAMT(String tRNAMT) {
        TRNAMT = tRNAMT;
    }

    /**
     * Gets the fitid.
     *
     * @return the fitid
     */
    public String getFITID() {
        return FITID;
    }

    /**
     * Sets the fitid.
     *
     * @param fITID the new fitid
     */
    public void setFITID(String fITID) {
        FITID = fITID;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getNAME() {
        return NAME;
    }

    /**
     * Sets the name.
     *
     * @param nAME the new name
     */
    public void setNAME(String nAME) {
        NAME = nAME;
    }

    /**
     * Gets the memo.
     *
     * @return the memo
     */
    public String getMEMO() {
        return MEMO;
    }

    /**
     * Sets the memo.
     *
     * @param mEMO the new memo
     */
    public void setMEMO(String mEMO) {
        MEMO = mEMO;
    }

}
