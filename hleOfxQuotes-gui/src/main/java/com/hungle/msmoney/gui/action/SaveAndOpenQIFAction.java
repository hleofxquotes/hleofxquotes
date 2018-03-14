package com.hungle.msmoney.gui.action;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JOptionPane;

import com.hungle.msmoney.core.gui.PriceTableViewOptions;
import com.hungle.msmoney.core.ofx.ImportStatus;
import com.hungle.msmoney.core.stockprice.AbstractStockPrice;
import com.hungle.msmoney.gui.GUI;

import ca.odell.glazedlists.EventList;

public class SaveAndOpenQIFAction extends SaveQIFAction {

    public SaveAndOpenQIFAction(String name, GUI gui, EventList<AbstractStockPrice> priceList,
            PriceTableViewOptions priceTableViewOptions, Component parent) {
        super(name, gui, priceList, priceTableViewOptions, parent);
    }

    @Override
    protected File getOutFile() throws IOException {
        File file = File.createTempFile("hleofxquotes", ".qif");
        file.deleteOnExit();
        return file;
    }

    @Override
    protected void saveToQif(File outFile) throws IOException {
        super.saveToQif(outFile);
        ImportStatus importStatus = openFile(outFile);
        if (importStatus.getStatusCode() != 0) {
            String message = null;
            List<String> lines = importStatus.getStderrLines();
            if (lines.size() > 0) {
                message = lines.get(0);
            }
            if (message == null) {
                lines = importStatus.getStdoutLines();
                if (lines.size() > 0) {
                    message = lines.get(0);
                }
            }
            if (message == null) {
                message = "Failed to open file=" + outFile;
            }
            JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private ImportStatus openFile(File file) throws IOException {
        return OpenUtils.open(getGui().getThreadPool(), file);
    }

}
