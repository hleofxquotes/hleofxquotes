package com.le.tools.moneyutils.csv2ofx;

public class LEDGERBAL {
    // <BALAMT>1234.00-TODO
    private String BALAMT = "1.00";

    // <DTASOF>20110918-TODO
    private String DTASOF = "20110918";

    public String getBALAMT() {
        return BALAMT;
    }

    public void setBALAMT(String bALAMT) {
        BALAMT = bALAMT;
    }

    public String getDTASOF() {
        return DTASOF;
    }

    public void setDTASOF(String dTASOF) {
        DTASOF = dTASOF;
    }
}
