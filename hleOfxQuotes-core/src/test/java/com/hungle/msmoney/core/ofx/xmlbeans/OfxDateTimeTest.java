package com.hungle.msmoney.core.ofx.xmlbeans;

import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

public class OfxDateTimeTest {
    private static final Logger LOGGER = Logger.getLogger(OfxDateTimeTest.class);

    // https://en.wikipedia.org/wiki/List_of_tz_database_time_zones

    @Test
    public void testUTCGMTTimeZones() {
        Date date = new Date(0);

        TimeZone tz;
        OfxDateTime ofxDateTime;
        tz = TimeZone.getTimeZone("UTC");
        ofxDateTime = new OfxDateTime(tz);
        Assert.assertNotNull(ofxDateTime);
        Assert.assertEquals("19700101000000.000[+0:UTC]", ofxDateTime.format(date));

        tz = TimeZone.getTimeZone("GMT");
        ofxDateTime = new OfxDateTime(tz);
        Assert.assertNotNull(ofxDateTime);
        Assert.assertEquals("19700101000000.000[+0:GMT]", ofxDateTime.format(date));
    }

    @Test
    public void testUSTimeZones() {
        Date date = new Date(0);

        TimeZone tz;
        OfxDateTime ofxDateTime;
        tz = TimeZone.getTimeZone("EST");
        ofxDateTime = new OfxDateTime(tz);
        Assert.assertNotNull(ofxDateTime);
        Assert.assertEquals("19691231190000.000[-5:EST]", ofxDateTime.format(date));

        tz = TimeZone.getTimeZone("CST");
        ofxDateTime = new OfxDateTime(tz);
        Assert.assertNotNull(ofxDateTime);
        Assert.assertEquals("19691231180000.000[-6:CST]", ofxDateTime.format(date));

        tz = TimeZone.getTimeZone("MST");
        ofxDateTime = new OfxDateTime(tz);
        Assert.assertNotNull(ofxDateTime);
        Assert.assertEquals("19691231170000.000[-7:MST]", ofxDateTime.format(date));

        tz = TimeZone.getTimeZone("PST");
        ofxDateTime = new OfxDateTime(tz);
        Assert.assertNotNull(ofxDateTime);
        Assert.assertEquals("19691231160000.000[-8:PST]", ofxDateTime.format(date));

        tz = TimeZone.getTimeZone("HST");
        ofxDateTime = new OfxDateTime(tz);
        Assert.assertNotNull(ofxDateTime);
        Assert.assertEquals("19691231140000.000[-10:HST]", ofxDateTime.format(date));
    }

    @Test
    public void testNegativeTimeZones() {
        Date date = new Date(0);

        TimeZone tz;
        OfxDateTime ofxDateTime;

        // Pacific/Pago_Pago -11
        tz = TimeZone.getTimeZone("Pacific/Pago_Pago");
        ofxDateTime = new OfxDateTime(tz);
        Assert.assertNotNull(ofxDateTime);
        Assert.assertEquals("19691231130000.000[-11:SST]", ofxDateTime.format(date));

        // Pacific/Tahiti -10
        tz = TimeZone.getTimeZone("Pacific/Tahiti");
        ofxDateTime = new OfxDateTime(tz);
        Assert.assertNotNull(ofxDateTime);
        Assert.assertEquals("19691231140000.000[-10:TAHT]", ofxDateTime.format(date));

        // Pacific/Gambier -9
        tz = TimeZone.getTimeZone("Pacific/Gambier");
        ofxDateTime = new OfxDateTime(tz);
        Assert.assertNotNull(ofxDateTime);
        Assert.assertEquals("19691231150000.000[-9:GAMT]", ofxDateTime.format(date));

        // Atlantic/Cape_Verde -1
        tz = TimeZone.getTimeZone("Atlantic/Cape_Verde");
        ofxDateTime = new OfxDateTime(tz);
        Assert.assertNotNull(ofxDateTime);
        Assert.assertEquals("19691231220000.000[-2:CVT]", ofxDateTime.format(date));
    }

    @Test
    public void testPositiveTimeZones() {
        Date date = new Date(0);
        String tzId = null;
        String expected = null;

        // Pacific/Fiji +12
        tzId = "Pacific/Fiji";
        expected = "19700101120000.000[+12:FJT]";
        testTimeZone(date, tzId, expected);

        // Pacific/Noumea +11
        tzId = "Pacific/Noumea";
        expected = "19700101110000.000[+11:NCT]";
        testTimeZone(date, tzId, expected);

        // Asia/Taipei +8
        tzId = "Asia/Taipei";
        expected = "19700101080000.000[+8:CST]";
        testTimeZone(date, tzId, expected);

    }

    @Test
    public void testFractionalTimeZones() {
        Date date = new Date(0);
        String tzId = null;
        String expected = null;
    
        // Asia/Kabul +4:30
        tzId = "Asia/Kabul";
        expected = "19700101043000.000[+4.50:AFT]";
        testTimeZone(date, tzId, expected);
    
        // Asia/Colombo +5:30
        tzId = "Asia/Colombo";
        expected = "19700101053000.000[+5.50:IST]";
        testTimeZone(date, tzId, expected);
    
        // Pacific/Marquesas -9:30
        tzId = "Pacific/Marquesas";
        expected = "19691231143000.000[-9.50:MART]";
        testTimeZone(date, tzId, expected);
    
    }

    private void testTimeZone(Date date, String tzId, String expected) {
        TimeZone tz;
        OfxDateTime ofxDateTime;
        tz = TimeZone.getTimeZone(tzId);
        ofxDateTime = new OfxDateTime(tz);
        Assert.assertNotNull(ofxDateTime);
        Assert.assertEquals(expected, ofxDateTime.format(date));
    }
}
