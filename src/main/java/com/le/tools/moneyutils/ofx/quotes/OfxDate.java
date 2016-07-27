package com.le.tools.moneyutils.ofx.quotes;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;

public class OfxDate extends Date {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_PATTERN = "yyyyMMddhhmmss.SSS";

    private SimpleDateFormat formatter = null;

    // YYYYMMDDHHMMSS.XXX[gmt offset:tz name]
    // 19961005132200.124[-5:EST] represents
    // October 5, 1996, at 1:22 and 124 milliseconds p.m., in Eastern Standard
    // Time
    private String pattern = DEFAULT_PATTERN;

    public OfxDate() {
        super();
        setPattern(DEFAULT_PATTERN);
    }

    public static String getCurrentDateTime(long offset) {
        // String pattern = "yyyyMMddHHmmss.SSS[Z:z]";
        String pattern = "yyyyMMddHHmmss";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        Calendar cal = Calendar.getInstance(new SimpleTimeZone(0, "GMT"));
        formatter.setCalendar(cal);
        Date date = null;
        if (offset > 0L) {
            date = new Date(System.currentTimeMillis() + offset);
        } else {
            date = new Date();
        }
        return formatter.format(date);
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
        this.formatter = new SimpleDateFormat(this.pattern);
        Calendar cal = Calendar.getInstance(new SimpleTimeZone(0, "GMT"));
        formatter.setCalendar(cal);
    }

    public String toOfxString() {
        StringBuilder sb = new StringBuilder(formatter.format(this));

        return sb.toString();
    }
}
