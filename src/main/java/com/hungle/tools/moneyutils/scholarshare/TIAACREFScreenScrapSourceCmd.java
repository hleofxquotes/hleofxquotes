package com.hungle.tools.moneyutils.scholarshare;

import java.io.IOException;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class TIAACREFScreenScrapSourceCmd.
 */
public class TIAACREFScreenScrapSourceCmd {
    
    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(TIAACREFScreenScrapSourceCmd.class);

    /**
     * The main method.
     *
     * @param args the arguments
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
                System.out.println(price);
            }
        } catch (IOException e) {
            LOGGER.error(e, e);
        } catch (XPathExpressionException e) {
            LOGGER.error(e, e);
        } finally {
            LOGGER.info("< DONE");
        }
    }

}
