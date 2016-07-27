package com.le.tools.moneyutils.csv2ofx;

import java.util.List;

import org.apache.log4j.Logger;

public class BANKTRANLIST {
    /*
     * <BANKTRANLIST> <DTSTART>20110822 <DTEND>20110915 <STMTTRN> <TRNTYPE>DEBIT
     * <DTPOSTED>20110915070000[-6:CST] <DTUSER>20110915070000 <TRNAMT>-5067.39
     * <FITID>201109151791258 <NAME>PENTAGON FEDERAL </STMTTRN>
     */
    private static final Logger log = Logger.getLogger(BANKTRANLIST.class);

    private String DTSTART = null;
    private String DTEND = null;

    private List<STMTTRN> STMTTRNS;

    public String getDTSTART() {
        return DTSTART;
    }

    public void setDTSTART(String dTSTART) {
        DTSTART = dTSTART;
    }

    public String getDTEND() {
        return DTEND;
    }

    public void setDTEND(String dTEND) {
        DTEND = dTEND;
    }

    public List<STMTTRN> getSTMTTRNS() {
        return STMTTRNS;
    }

    public void setSTMTTRNS(List<STMTTRN> sTMTTRNS) {
        STMTTRNS = sTMTTRNS;
    }

}
