package com.le.tools.moneyutils.csv2ofx;

import org.apache.log4j.Logger;

public class STMTTRN {
    /*
     * <STMTTRN> <TRNTYPE>DEBIT <DTPOSTED>20110822063000[-6:CST]
     * <DTUSER>20110822063000 <TRNAMT>-9.58 <FITID>201108221462840 <NAME>TARGET
     * NAT'L BK </STMTTRN> <STMTTRN> <TRNTYPE>CREDIT
     * <DTPOSTED>20110822054200[-6:CST] <DTUSER>20110822054200 <TRNAMT>10.00
     * <FITID>201108221387727 <NAME>Alliant CU. : ONLINE WD CO: <MEMO>Alliant
     * CU. : ONLINE WD CO: Alliant CU.WELLS FARGO BANK NA </STMTTRN>
     */
    // 11.4.3 Statement Transaction <STMTTRN>
    private static final Logger log = Logger.getLogger(STMTTRN.class);

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
    // OTHER Other
    private String TRNTYPE = null;

    // Date transaction was posted to account, datetime
    private String DTPOSTED = null;

    // (Optional) Date user initiated transaction, if known, datetime
    private String DTUSER = null;

    // Amount of transaction, amount
    private String TRNAMT = null;

    // Each <STMTTRN> contains an <FITID> that the client uses to detect whether
    // the server has previously downloaded the transaction.
    private String FITID = null;

    // Name of payee or description of transaction
    private String NAME = null;

    // <MEMO> Extra information (not in <NAME>), MEMO
    private String MEMO = null;

    public String getTRNTYPE() {
        return TRNTYPE;
    }

    public void setTRNTYPE(String tRNTYPE) {
        TRNTYPE = tRNTYPE;
    }

    public String getDTPOSTED() {
        return DTPOSTED;
    }

    public void setDTPOSTED(String dTPOSTED) {
        DTPOSTED = dTPOSTED;
    }

    public String getDTUSER() {
        return DTUSER;
    }

    public void setDTUSER(String dTUSER) {
        DTUSER = dTUSER;
    }

    public String getTRNAMT() {
        return TRNAMT;
    }

    public void setTRNAMT(String tRNAMT) {
        TRNAMT = tRNAMT;
    }

    public String getFITID() {
        return FITID;
    }

    public void setFITID(String fITID) {
        FITID = fITID;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String nAME) {
        NAME = nAME;
    }

    public String getMEMO() {
        return MEMO;
    }

    public void setMEMO(String mEMO) {
        MEMO = mEMO;
    }

}
