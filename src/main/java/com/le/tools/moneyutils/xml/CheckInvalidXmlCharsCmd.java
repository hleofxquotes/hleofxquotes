package com.le.tools.moneyutils.xml;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.impl.common.XMLChar;

public class CheckInvalidXmlCharsCmd {
    private static final Logger log = Logger.getLogger(CheckInvalidXmlCharsCmd.class);

    /**
     * @param args
     */
    public static void main(String[] args) {
        int c = 0;
        XMLChar.isValid(c);

    }

}
