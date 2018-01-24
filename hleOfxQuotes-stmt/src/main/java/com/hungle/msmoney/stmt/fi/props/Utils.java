package com.hungle.msmoney.stmt.fi.props;

import com.hungle.msmoney.stmt.fi.AbstractFiContext;

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