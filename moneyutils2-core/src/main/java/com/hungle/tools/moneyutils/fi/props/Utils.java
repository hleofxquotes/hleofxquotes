package com.hungle.tools.moneyutils.fi.props;

import com.hungle.tools.moneyutils.fi.AbstractFiContext;

// TODO: Auto-generated Javadoc
/**
 * The Class Utils.
 */
public class Utils {
    
    /**
     * Gets the dt client.
     *
     * @return the dt client
     */
    public String getDtClient() {
        return AbstractFiContext.createDtClient();
    }

    /**
     * Gets the trn uid.
     *
     * @return the trn uid
     */
    public String getTrnUid() {
        return AbstractFiContext.createTrnUid();
    }
}
