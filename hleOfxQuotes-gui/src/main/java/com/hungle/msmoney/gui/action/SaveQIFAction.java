package com.hungle.msmoney.gui.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.hungle.msmoney.core.gui.PriceTableViewOptions;
import com.hungle.msmoney.core.qif.QifUtils;
import com.hungle.msmoney.core.stockprice.AbstractStockPrice;
import com.hungle.msmoney.gui.GUI;
import com.hungle.msmoney.gui.OfxFileIo;

import ca.odell.glazedlists.EventList;

public class SaveQIFAction extends AbstractAction {
    private static final Logger LOGGER = Logger.getLogger(SaveQIFAction.class);

    private final Component parent;
    private final PriceTableViewOptions priceTableViewOptions;
    private final GUI gui;
    private final EventList<AbstractStockPrice> priceList;
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private JFileChooser fc = null;

    public SaveQIFAction(String name, GUI gui, EventList<AbstractStockPrice> priceList,
            PriceTableViewOptions priceTableViewOptions, Component parent) {
        super(name);
        this.gui = gui;
        this.priceList = priceList;
        this.priceTableViewOptions = priceTableViewOptions;
        this.parent = parent;
    }

    @Override
    public void actionPerformed(ActionEvent event) {

        try {
            File outFile = getOutFile();
            if (outFile == null) {
                return;
            }

            le.com.tools.moneyutils.ofx.quotes.GUI.PREFS.put(Action.ACCELERATOR_KEY, outFile.getAbsoluteFile().getParentFile().getAbsolutePath());
            saveToQif(outFile);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    protected void saveToQif(File outFile) throws IOException {
        EventList<AbstractStockPrice> list1 = priceList;
        EventList<AbstractStockPrice> list2 = this.getGui().getNotFoundPriceList();
        List<AbstractStockPrice> list = OfxFileIo.concatPriceList(list1, list2);
        QifUtils.saveToQif(list, outFile, priceTableViewOptions.isConvertWhenExport(), getGui().getDefaultCurrency(),
                getGui().getSymbolMapper(), getGui().getFxTable(), getGui().getTemplateDecimalSeparator());
    }

    protected File getOutFile() throws IOException {
        if (fc == null) {
            initFileChooser();
        }
        // Component parent = view;
        if (this.fc.getSelectedFile() == null) {
            this.fc.setSelectedFile(new File("quotes.qif"));
        }

        if (fc.showSaveDialog(parent) == JFileChooser.CANCEL_OPTION) {
            return null;
        }

        File outFile = fc.getSelectedFile();
        return outFile;
    }

    private void initFileChooser() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("> creating FileChooser");
        }
        String key = Action.ACCELERATOR_KEY;
        fc = new JFileChooser(le.com.tools.moneyutils.ofx.quotes.GUI.PREFS.get(key, "."));
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("< creating FileChooser");
        }
    }

    public GUI getGui() {
        return gui;
    }
}