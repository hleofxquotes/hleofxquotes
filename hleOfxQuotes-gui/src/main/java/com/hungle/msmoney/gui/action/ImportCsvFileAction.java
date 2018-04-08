package com.hungle.msmoney.gui.action;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;

import com.hungle.msmoney.core.stockprice.AbstractStockPrice;
import com.hungle.msmoney.core.stockprice.StockPriceCsvUtils;
import com.hungle.msmoney.gui.GUI;
import com.hungle.msmoney.qs.DefaultQuoteSource;
import com.hungle.msmoney.qs.QuoteSource;

public final class ImportCsvFileAction extends AbstractAction {
    private static final Logger LOGGER = Logger.getLogger(ImportCsvFileAction.class);

    /**
     * 
     */
    private final GUI gui;
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private JFileChooser fc = null;

    public ImportCsvFileAction(GUI gui, String name) {
        super(name);
        this.gui = gui;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (fc == null) {
            initFileChooser();
        }
        if (fc.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = fc.getSelectedFile();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            char delimiter = StockPriceCsvUtils.CSV_DELIMITER_COMMA_CHAR;
            List<AbstractStockPrice> stockPrices = StockPriceCsvUtils.toStockPrices(reader, delimiter);
            LOGGER.info("From file=" + file + ", count=" + stockPrices.size());

            QuoteSource quoteSource = new DefaultQuoteSource();
            this.getGui().getQuoteSourceListener().stockPricesReceived(quoteSource, stockPrices);
        } catch (IOException e) {
            LOGGER.warn(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.warn(e);
                } finally {
                    reader = null;
                }
            }
        }

    }

    private void initFileChooser() {
        String key = SaveOfxAction.PREF_SAVE_OFX_DIR;
        fc = new JFileChooser(le.com.tools.moneyutils.ofx.quotes.GUI.PREFS.get(key, "."));
        FileFilter filter = new FileFilter() {

            @Override
            public String getDescription() {
                return "yahoo.com CSV";
            }

            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return true;
                }
                String name = file.getName();
                return name.endsWith(".csv");
            }
        };
        this.fc.setFileFilter(filter);
    }

    private GUI getGui() {
        return gui;
    }
}