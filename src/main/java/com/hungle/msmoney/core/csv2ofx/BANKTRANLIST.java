package com.hungle.msmoney.core.csv2ofx;

import java.util.List;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class BANKTRANLIST.
 */
public class BANKTRANLIST {
    
    /** The Constant log. */
    /*
     * <BANKTRANLIST> <DTSTART>20110822 <DTEND>20110915 <STMTTRN> <TRNTYPE>DEBIT
     * <DTPOSTED>20110915070000[-6:CST] <DTUSER>20110915070000 <TRNAMT>-5067.39
     * <FITID>201109151791258 <NAME>PENTAGON FEDERAL </STMTTRN>
     */
    private static final Logger LOGGER = Logger.getLogger(BANKTRANLIST.class);

    /** The dtstart. */
    private String DTSTART = null;
    
    /** The dtend. */
    private String DTEND = null;

    /** The stmttrns. */
    private List<STMTTRN> STMTTRNS;

    /**
     * Gets the dtstart.
     *
     * @return the dtstart
     */
    public String getDTSTART() {
        return DTSTART;
    }

    /**
     * Sets the dtstart.
     *
     * @param dTSTART the new dtstart
     */
    public void setDTSTART(String dTSTART) {
        DTSTART = dTSTART;
    }

    /**
     * Gets the dtend.
     *
     * @return the dtend
     */
    public String getDTEND() {
        return DTEND;
    }

    /**
     * Sets the dtend.
     *
     * @param dTEND the new dtend
     */
    public void setDTEND(String dTEND) {
        DTEND = dTEND;
    }

    /**
     * Gets the stmttrns.
     *
     * @return the stmttrns
     */
    public List<STMTTRN> getSTMTTRNS() {
        return STMTTRNS;
    }

    /**
     * Sets the stmttrns.
     *
     * @param sTMTTRNS the new stmttrns
     */
    public void setSTMTTRNS(List<STMTTRN> sTMTTRNS) {
        STMTTRNS = sTMTTRNS;
    }

}
