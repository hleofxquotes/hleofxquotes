package com.hungle.tools.moneyutils.ofx.xmlbeans;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;

public class OfxDateTimeUtils {

    /**
     * Creates the dt client.
     *
     * @param pattern
     *            the pattern
     * @return the string
     */
    public static String createDtClient(String pattern) {
        // return createDtClientGmt(pattern);
        // return OfxDateTime.formatGmt(new Date());
        return OfxDateTime.formatLocalTime(new Date());
    }

    private static String createDtClientGmt(String pattern) {
        // example: A8C53164-B771-42EA-944F-05ADCF517D9E
        if ((pattern == null) || (pattern.length() <= 0)) {
            pattern = "yyyyMMddHHmmss.SSS";
        }
        // <DTCLIENT>19961029101000 <!-- Oct. 29, 1996, 10:10:00 am -->
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        Calendar cal = Calendar.getInstance(new SimpleTimeZone(0, "GMT"));
        formatter.setCalendar(cal);
        return formatter.format(new Date());
    }

    static final String getDtServer(Date date) {
        // return XmlBeansUtils.formatGmt(date);
        // return OfxDateTime.formatGmt(date);
        return OfxDateTime.formatLocalTime(date);
    }

    static final String getStatementResponseDtAsOf(Date date) {
        // return XmlBeansUtils.formatGmt(date);
        // return OfxDateTime.formatGmt(date);
        return OfxDateTime.formatLocalTime(date);
    }

    static final String getGeneralSecurityInfoDateAsOf(Date date) {
        // return XmlBeansUtils.formatGmt(date);
        // return OfxDateTime.formatGmt(date);
        return OfxDateTime.formatLocalTime(date);
    }

    static final String getInvestmentPositionListDtPriceAsOf(Date date) {
        // return XmlBeansUtils.formatGmt(date);
        // return OfxDateTime.formatGmt(date);
        return OfxDateTime.formatLocalTime(date);
    }

    static final String getforceGeneratingINVTRANLISTDtStart(Date date) {
        // return XmlBeansUtils.formatGmt(date);
        // return OfxDateTime.formatGmt(date);
        return OfxDateTime.formatLocalTime(date);
    }

    static final String getforceGeneratingINVTRANLISTDtEnd(Date date) {
        // return XmlBeansUtils.formatGmt(date);
        // return OfxDateTime.formatGmt(date);
        return OfxDateTime.formatLocalTime(date);
    }

}
