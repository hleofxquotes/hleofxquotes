package com.hungle.tools.moneyutils.ofx.quotes;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.hungle.msmoney.statements.fi.ResponseUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class FixMissingTags.
 */
public class FixMissingTags {
    
    /** The Constant log. */
    private static final Logger log = Logger.getLogger(FixMissingTags.class);

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        FixMissingTags fixMissingTags = new FixMissingTags();
        String str = null;
        str = "<OFX>"
                + "<SIGNONMSGSRSV1>"
                + "<SONRS><STATUS><CODE>0<SEVERITY>INFO<MESSAGE>SUCCESS</STATUS><DTSERVER>20110206011510.834[-5:EDT]<LANGUAGE>ENG<FI>"
                + "<ORG>fidelity.com<FID>7776</FI></SONRS>"
                + "</SIGNONMSGSRSV1>"
                + "<SIGNUPMSGSRSV1><ACCTINFOTRNRS><TRNUID>c81595a3-19b8-43fe-b8e8-cfb2d45fb348<STATUS>"
                + "<CODE>0<SEVERITY>INFO<MESSAGE>SUCCESS</STATUS><ACCTINFORS><DTACCTUP>20110205033027.000[-5:EDT]"
                + "<ACCTINFO><DESC>Brokerage Account<INVACCTINFO>"
                + "<INVACCTFROM><BROKERID>fidelity.com<ACCTID>X11606413</INVACCTFROM>"
                + "<USPRODUCTTYPE>OTHER<CHECKING>Y<SVCSTATUS>ACTIVE<OPTIONLEVEL>None</INVACCTINFO></ACCTINFO>"
                + "<ACCTINFO><DESC>Brokerage Account<INVACCTINFO><INVACCTFROM><BROKERID>fidelity.com<ACCTID>615491381</INVACCTFROM>"
                + "<USPRODUCTTYPE>OTHER<CHECKING>Y<SVCSTATUS>ACTIVE<OPTIONLEVEL>None</INVACCTINFO></ACCTINFO>"
                + "<ACCTINFO><DESC>Brokerage Account<INVACCTINFO><INVACCTFROM><BROKERID>fidelity.com<ACCTID>615491390</INVACCTFROM>"
                + "<USPRODUCTTYPE>OTHER<CHECKING>Y<SVCSTATUS>ACTIVE<OPTIONLEVEL>None</INVACCTINFO></ACCTINFO><ACCTINFO>"
                + "<DESC>Brokerage Account<INVACCTINFO><INVACCTFROM><BROKERID>fidelity.com<ACCTID>615491403</INVACCTFROM>"
                + "<USPRODUCTTYPE>OTHER<CHECKING>Y<SVCSTATUS>ACTIVE<OPTIONLEVEL>None</INVACCTINFO></ACCTINFO><ACCTINFO>"
                + "<DESC>Brokerage Account<INVACCTINFO><INVACCTFROM><BROKERID>fidelity.com<ACCTID>615491411</INVACCTFROM>"
                + "<USPRODUCTTYPE>OTHER<CHECKING>Y<SVCSTATUS>ACTIVE<OPTIONLEVEL>None</INVACCTINFO></ACCTINFO><ACCTINFO>"
                + "<DESC>Brokerage Account<INVACCTINFO><INVACCTFROM><BROKERID>fidelity.com<ACCTID>617324132</INVACCTFROM>"
                + "<USPRODUCTTYPE>OTHER<CHECKING>Y<SVCSTATUS>ACTIVE<OPTIONLEVEL>None</INVACCTINFO></ACCTINFO><ACCTINFO>"
                + "<DESC>Brokerage Account<INVACCTINFO><INVACCTFROM><BROKERID>fidelity.com<ACCTID>617324159</INVACCTFROM>"
                + "<USPRODUCTTYPE>OTHER<CHECKING>Y<SVCSTATUS>ACTIVE<OPTIONLEVEL>None</INVACCTINFO></ACCTINFO><ACCTINFO><DESC>Brokerage Account<INVACCTINFO><INVACCTFROM>"
                + "<BROKERID>fidelity.com<ACCTID>618233633</INVACCTFROM><USPRODUCTTYPE>OTHER"
                + "<CHECKING>Y<SVCSTATUS>ACTIVE<INVACCTTYPE>INDIVIDUAL<OPTIONLEVEL>None</INVACCTINFO>"
                + "</ACCTINFO></ACCTINFORS></ACCTINFOTRNRS></SIGNUPMSGSRSV1>" + "</OFX>";
        File file = new File("fi/fidelity/accountInquiry-v1-resp.ofx");

        try {
            ResponseUtils.checkRespFileV1(file);
        } catch (IOException e) {
            log.error(e, e);
        }
    }

}
