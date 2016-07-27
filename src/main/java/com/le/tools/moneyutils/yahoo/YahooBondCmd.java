package com.le.tools.moneyutils.yahoo;

import java.io.IOException;

import org.apache.log4j.Logger;

public class YahooBondCmd {
    private static final Logger log = Logger.getLogger(YahooBondCmd.class);

    /**
     * @param args
     */
    public static void main(String[] args) {
        // http://developer.yahoo.com/yql/console
        // select * from html where
        // url='http://reports.finance.yahoo.com/z2?ce=5515249148511575917955&q=b=1%26is=ford%26so=d'
        // and
        // xpath="//td[@class='yfnc_tablehead1' or @class='yfnc_tabledata1']"
        YahooBond yahooBond = new YahooBond();
        String[] tokens = {
                "http://reports.finance.yahoo.com/z2?ce=5515249148491507515753&q=b%3d2%26is%3dford%26so%3dd",
                "http://reports.finance.yahoo.com/z2?ce=5515249148491507517352&q=b%3d2%26is%3dford%26so%3dd",
                "http://reports.finance.yahoo.com/z2?ce=5015245146551535115848&q=b%3d3%26is%3dford%26so%3dd",
                "http://reports.finance.yahoo.com/z2?ce=4915552143561495516149&q=b%3d1%26cpl%3d-1.000000%26cpu%3d-1.000000%26mtl%3d-1%26mtu%3d-1%26pr%3d0%26rl%3d-1%26ru%3d-1%26sf%3dm%26so%3da%26stt%3d-%26tt%3d1%26yl%3d-1.000000%26ytl%3d-1.000000%26ytu%3d-1.000000%26yu%3d-1.000000",
                "http://reports.finance.yahoo.com/z2?ce=4915552143561495516652&q=b%3d1%26cpl%3d-1.000000%26cpu%3d-1.000000%26mtl%3d-1%26mtu%3d-1%26pr%3d0%26rl%3d-1%26ru%3d-1%26sf%3dm%26so%3da%26stt%3d-%26tt%3d1%26yl%3d-1.000000%26ytl%3d-1.000000%26ytu%3d-1.000000%26yu%3d-1.000000", };
        try {
            for (String token : tokens) {
                String price = yahooBond.getPrice(token);
                log.info("price=" + price);
            }
        } catch (IOException e) {
            log.error(e, e);
        } finally {
            log.info("< DONE");
        }
    }

}
