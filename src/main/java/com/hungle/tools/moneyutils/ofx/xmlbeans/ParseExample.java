package com.hungle.tools.moneyutils.ofx.xmlbeans;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;

import net.ofx.types.x2003.x04.OFXDocument;

// TODO: Auto-generated Javadoc
/**
 * The Class ParseExample.
 */
public class ParseExample {
    
    /** The Constant log. */
    private static final Logger log = Logger.getLogger(ParseExample.class);

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        File file = new File("example1.xml");
        try {
            XmlOptions xmlOptions = new XmlOptions();
            Map<String, String> map = new HashMap<String, String>();
            map.put("", "http://ofx.net/types/2003/04");
            xmlOptions.setLoadSubstituteNamespaces(map);
            OFXDocument doc = OFXDocument.Factory.parse(file, xmlOptions);
        } catch (XmlException e) {
            log.error(e, e);
        } catch (IOException e) {
            log.error(e, e);
        }

    }

}
