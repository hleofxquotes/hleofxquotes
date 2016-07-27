package com.le.tools.moneyutils.encryption;

public class EncryptionHelperException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public EncryptionHelperException(Exception e) {
        super(e);
    }

    public EncryptionHelperException(String string) {
        super(string);
    }

}
