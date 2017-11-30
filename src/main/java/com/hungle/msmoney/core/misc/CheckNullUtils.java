package com.hungle.msmoney.core.misc;

public class CheckNullUtils {

    /**
     * Checks if is null.
     *
     * @param property
     *            the property
     * @return true, if is null
     */
    public static boolean isNull(String property) {
        if (property == null) {
            return true;
        }
    
        if (property.length() <= 0) {
            return true;
        }
    
        return false;
    }

    /**
     * Checks if is na.
     *
     * @param value the value
     * @return true, if is na
     */
    public static boolean isNA(String value) {
        if (value == null) {
            return true;
        }
        value = value.trim();
        if (value.length() <= 0) {
            return true;
        }
    
        if (value.equals("N/A")) {
            return true;
        }
    
        return false;
    }

}