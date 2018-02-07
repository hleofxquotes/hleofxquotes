package com.hungle.msmoney.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.hungle.msmoney.core.stockprice.AbstractStockPrice;
import com.hungle.msmoney.core.stockprice.Price;

import ca.odell.glazedlists.EventList;

public class OfxFileIo {
    private static final Logger LOGGER = Logger.getLogger(OfxFileIo.class);
    
    static final void deleteFiles(List<File> files) {
        if (files != null) {
            for (File file : files) {
                OfxFileIo.deleteOutputFile(file);
            }
        }
    }

    static final List<AbstractStockPrice> concatPriceList(List<AbstractStockPrice> list1,
            EventList<AbstractStockPrice> list2) {
        List<AbstractStockPrice> prices = new ArrayList<>();
        prices.addAll(list1);
        for (AbstractStockPrice notFoundPrice : list2) {
            if (notFoundPrice == null) {
                continue;
            }
            Price lastPrice = notFoundPrice.getLastPrice();
            if (lastPrice == null) {
                continue;
            }
            Double p = lastPrice.getPrice();
            if (p == null) {
                continue;
            }
            if (p.doubleValue() <= 0.00) {
                continue;
            }
            prices.add(notFoundPrice);
        }
        return prices;
    }

    /**
     * Delete output file.
     *
     * @param outputFile
     *            the output file
     */
    static final void deleteOutputFile(File outputFile) {
        if ((outputFile != null) && (outputFile.exists())) {
            LOGGER.info("FILE DELETING -> ofxFile=" + outputFile.getAbsolutePath());
            if (!outputFile.delete()) {
                LOGGER.warn("Failed to delete outputFile=" + outputFile);
            }
        }
    }

}
