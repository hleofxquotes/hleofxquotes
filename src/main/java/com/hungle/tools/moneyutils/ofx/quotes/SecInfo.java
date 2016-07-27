package com.le.tools.moneyutils.ofx.quotes;

public class SecInfo {
    // 13.8.5.1 General Securities Information <SECINFO>
    // SECID
    private String secId;

    // SECNAME
    private String secName;

    // UNIQUEID: see UNIQUEIDTYPE
    private String uniqueId;

    // UNIQUEIDTYPE: CUSIP, TICKER?
    private String uniqueIdtType;
}
