package com.hungle.msmoney.core.ofx.xmlbeans;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.SimpleTimeZone;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlOptions;

import net.ofx.types.x2003.x04.OFX;

// TODO: Auto-generated Javadoc
/**
 * The Class XmlBeansUtils.
 */
public class XmlBeansUtils {
    
    /** The Constant SUCCESSFUL_SIGN_ON. */
    public static final String SUCCESSFUL_SIGN_ON = "Successful Sign On";
    
    /** The Constant DEFAULT_NAME_SPACE_VALUE. */
    private static final String DEFAULT_NAME_SPACE_VALUE = "http://ofx.net/types/2003/04";
    
    /** The Constant random. */
    private static final Random random = new Random();
    
    /** The dt as of pattern. */
    private static String dtAsOfPattern = "yyyyMMddHHmmss";
    
    /** The dt as of gmtf formatter. */
    private static SimpleDateFormat dtAsOfGmtfFormatter = new SimpleDateFormat(dtAsOfPattern);
    
    /** The dt as of local formatter. */
    private static SimpleDateFormat dtAsOfLocalFormatter = new SimpleDateFormat(dtAsOfPattern);
    static {
        Calendar cal = Calendar.getInstance(new SimpleTimeZone(0, "GMT"));
        dtAsOfGmtfFormatter.setCalendar(cal);
    }

    /**
     * Creates the xml options.
     *
     * @return the xml options
     */
    public static XmlOptions createXmlOptions() {
        // on output:
        // http://wiki.apache.org/xmlbeans/XmlBeansFaq#suggestedPrefixes

        XmlOptions xmlOptions = new XmlOptions();

        Map<String, String> map = new HashMap<String, String>();
        String nameSpaceValue = XmlBeansUtils.DEFAULT_NAME_SPACE_VALUE;
        map.put("", nameSpaceValue);
        // map.put("hle", nameSpaceValue);
        // xmlOptions.setLoadSubstituteNamespaces(map);

        Map<String, String> suggestedPrefixes = new HashMap<String, String>();
        suggestedPrefixes.put(nameSpaceValue, "");
        // xmlOptions.setSaveSuggestedPrefixes(suggestedPrefixes);

        xmlOptions.setSavePrettyPrint();
        // xmlOptions.setUseDefaultNamespace();
        Map<String, String> prefixes = new HashMap<String, String>();
        prefixes.put("", nameSpaceValue);
        // prefixes.put("hle", nameSpaceValue);
        // xmlOptions.setSaveImplicitNamespaces(prefixes);
        // xmlOptions.setSaveAggressiveNamespaces();

        // xmlOptions.setUseDefaultNamespace();
        return xmlOptions;
    }

    /**
     * Adds the key value pair.
     *
     * @param key the key
     * @param value the value
     * @param sb the sb
     */
    public static void addKeyValuePair(String key, String value, StringBuilder sb) {
        if (sb.length() > 0) {
            sb.append(" ");
        }
        sb.append(key);
        sb.append("=");
        sb.append("\"");
        sb.append(value);
        sb.append("\"");
    }

    /**
     * Gets the random.
     *
     * @return the random
     */
    public static Random getRandom() {
        return random;
    }

    /**
     * Insert proc inst.
     *
     * @param ofx the ofx
     */
    public static void insertProcInst(OFX ofx) {
        StringBuilder sb = new StringBuilder();
        String value = null;

        // OFXHEADER specifies the version number of the Open Financial Exchange
        // declaration
        addKeyValuePair("OFXHEADER", "200", sb);

        // VERSION specifies the version number of the following OFX data block.
        addKeyValuePair("VERSION", "200", sb);

        /*
         * SECURITY defines the type of application-level security, if any, that
         * is used for the <OFX> block. The values for SECURITY can be NONE or
         * TYPE1.
         */
        addKeyValuePair("SECURITY", "NONE", sb);

        /*
         * File-Based Error Recovery
         */
        addKeyValuePair("OLDFILEUID", "NONE", sb);

        // value = "" + Math.abs(getRandom().nextLong());
        value = "NONE";
        addKeyValuePair("NEWFILEUID", value, sb);

        String target = "OFX";
        XmlCursor cursor = null;
        try {
            cursor = ofx.newCursor();
            cursor.insertProcInst(target, sb.toString());
        } finally {
            if (cursor != null) {
                cursor.dispose();
                cursor = null;
            }
        }
    }

    /**
     * Gets the current date time.
     *
     * @return the current date time
     */
    // return localtime
    public static String getCurrentDateTime() {
        return getCurrentDateTime(0L);
    }

    /**
     * Gets the current date time.
     *
     * @param offset the offset
     * @return the current date time
     */
    // return localtime
    public static String getCurrentDateTime(long offset) {
        Date date = null;
        if (offset > 0L) {
            date = new Date(System.currentTimeMillis() + offset);
        } else {
            date = new Date();
        }

        return formatLocal(date);
    }

    /**
     * Format local.
     *
     * @param date the date
     * @return the string
     */
    public static String formatLocal(Date date) {
        return dtAsOfLocalFormatter.format(date);
    }

    /**
     * Format gmt.
     *
     * @param date the date
     * @return the string
     */
    // return GMT
    public static String formatGmt(Date date) {
        return dtAsOfGmtfFormatter.format(date);
    }

    /**
     * Parses the gmt.
     *
     * @param gmtTime the gmt time
     * @return the date
     * @throws ParseException the parse exception
     */
    // return GMT
    public static Date parseGmt(String gmtTime) throws ParseException {
        return dtAsOfGmtfFormatter.parse(gmtTime);
    }

    /**
     * Last trade date to dt as of.
     *
     * @param maxLastTradeDate the max last trade date
     * @return the string
     */
    public static String lastTradeDateToDtAsOf(Date maxLastTradeDate) {
        // TODO Auto-generated method stub
        return null;
    }

}
