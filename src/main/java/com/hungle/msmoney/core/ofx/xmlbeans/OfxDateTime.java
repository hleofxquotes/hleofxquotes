package com.hungle.msmoney.core.ofx.xmlbeans;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;

public class OfxDateTime {
    private static final Logger LOGGER = Logger.getLogger(OfxDateTime.class);

    private static final String PATTERN_NO_TIMEZONE = "yyyyMMddHHmmss.SSS";
    private static final String PATTERN_TIMEZONE_OFFSET_ONLY = "Z";
    private static final String PATTERN_TIMEZONE_NAME_ONLY = "z";

    private static final String DEFAULT_PATTERN = PATTERN_NO_TIMEZONE;

    // 3.2.8.1 Dates, Times, and Time Zones
    // YYYYMMDDHHMMSS.XXX [gmt offset[:tz name]]
    /*
     * For example, “19961005132200.124[-5:EST]” represents October 5, 1996, at
     * 1:22 and 124 milliseconds p.m., in Eastern Standard Time. This is the
     * same as 6:22 p.m. Greenwich Mean Time (GMT).
     */

    /*
     * Note that times zones are specified by an offset and optionally, a time
     * zone name. The offset defines the time zone. Valid offset values are in
     * the range from –12 to +12 for whole number offsets. Formatting is +12.00
     * to -12.00 for fractional offsets, plus sign may be omitted.
     */

    private final SimpleDateFormat formatter;

    private final String pattern;

    private final TimeZone timezone;

    private final SimpleDateFormat tzOffsetFormatter = new SimpleDateFormat(PATTERN_TIMEZONE_OFFSET_ONLY);

    private final SimpleDateFormat tzNameFormatter = new SimpleDateFormat(PATTERN_TIMEZONE_NAME_ONLY);

    public OfxDateTime() {
        this(DEFAULT_PATTERN, null);
    }

    public OfxDateTime(TimeZone timezone) {
        this(DEFAULT_PATTERN, timezone);
    }

    private OfxDateTime(String pattern, TimeZone timezone) {
        super();

        this.pattern = pattern;
        this.formatter = new SimpleDateFormat(this.pattern);

        this.timezone = timezone;
        if (timezone != null) {
            Calendar cal = Calendar.getInstance(this.timezone);
            this.formatter.setCalendar(cal);
            this.tzOffsetFormatter.setCalendar(cal);
            this.tzNameFormatter.setCalendar(cal);
        }
    }

    public String format(Date date) {
        String str = formatter.format(date);

        String tzOffsetString = getTzOffsetString(date);
        String tzName = getTzName(date);

        // 20171113104504.993[-0800:PST]
        return str + "[" + tzOffsetString + ":" + tzName + "]";

    }

    private String getTzOffsetString(Date date) {
        String str = tzOffsetFormatter.format(date);
        // -0800
        int offset = Integer.valueOf(str);
        int hours = offset / 100;
        int minutes = offset % 100;
        if (minutes != 0) {
            minutes = Math.abs(minutes);
            int fractionalOffset = (minutes * 100) / 60;
            minutes = fractionalOffset;
            str = String.format("%s%d.%d", getSign(hours), hours, minutes);
        } else {
            str = String.format("%s%d", getSign(hours), hours);
        }
        return str;
    }

    private String getSign(int hours) {
        if (hours >= 0) {
            return "+";
        } else {
            return "";
        }
    }

    private String getTzName(Date date) {
        String str = tzNameFormatter.format(date);
        return str;
    }

    public static String formatGmt(Date date) {
        TimeZone tz = TimeZone.getTimeZone("GMT");
        OfxDateTime ofxDateTime = new OfxDateTime(tz);
        return ofxDateTime.format(date);
    }

    public static String formatLocalTime(Date date) {
        OfxDateTime ofxDateTime = new OfxDateTime();
        return ofxDateTime.format(date);
    }

}
