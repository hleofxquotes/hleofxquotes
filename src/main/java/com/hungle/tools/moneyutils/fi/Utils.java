package com.hungle.tools.moneyutils.fi;


public class Utils {
    public String getDtClient() {
        return AbstractFiContext.createDtClient();
    }

    public String getTrnUid() {
        return AbstractFiContext.createTrnUid();
    }
}
