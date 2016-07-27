package com.le.tools.moneyutils.scholarshare;

import java.io.IOException;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;

public class TIAACREFScreenScrapSourceCmd {
    private static final Logger log = Logger.getLogger(TIAACREFScreenScrapSourceCmd.class);

    /**
     * @param args
     */
    public static void main(String[] args) {
        String fileName = null;
        if (args.length == 0) {
            fileName = null;
        } else if (args.length == 1) {
            fileName = args[0];
        } else {
            Class<TIAACREFScreenScrapSourceCmd> clz = TIAACREFScreenScrapSourceCmd.class;
            System.out.println("Usage: java " + clz.getName() + " [file.xml]");
            System.exit(1);
        }

        TIAACREFScreenScrapSource screenScrapper = new TIAACREFScreenScrapSource();
        try {
            if (fileName != null) {
                screenScrapper.query(fileName);
            } else {
                screenScrapper.query();
            }

            screenScrapper.parse();
            List<TIAACREFPriceInfo> prices = screenScrapper.getPrices();
            for (TIAACREFPriceInfo price : prices) {
//                log.info(price);
                System.out.println(price);
            }
        } catch (IOException e) {
            log.error(e, e);
        } catch (XPathExpressionException e) {
            log.error(e, e);
        } finally {
            log.info("< DONE");
        }
    }

}
