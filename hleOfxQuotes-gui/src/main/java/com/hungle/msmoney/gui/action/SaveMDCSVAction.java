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
import com.hungle.msmoney.core.stockprice.AbstractStockPrice;
import com.hungle.msmoney.gui.GUI;
import com.hungle.msmoney.gui.OfxFileIo;
import com.hungle.msmoney.gui.md.MdUtils;

import ca.odell.glazedlists.EventList;

public final class SaveMDCSVAction extends AbstractAction {
    private static final Logger LOGGER = Logger.getLogger(SaveMDCSVAction.class);
    
    private final EventList<AbstractStockPrice> priceList;
    private final Component parent;
    private final GUI gui;
    private final PriceTableViewOptions priceTableViewOptions;
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private JFileChooser fc = null;

    public SaveMDCSVAction(String name, GUI gui, EventList<AbstractStockPrice> priceList, PriceTableViewOptions priceTableViewOptions,
            Component parent) {
        super(name);
        this.priceList = priceList;
        this.parent = parent;
        this.gui = gui;
        this.priceTableViewOptions = priceTableViewOptions;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (fc == null) {
            initFileChooser();
        }
        if (this.fc.getSelectedFile() == null) {
            this.fc.setSelectedFile(new File("mdQuotes.csv"));
        }

        if (fc.showSaveDialog(parent) == JFileChooser.CANCEL_OPTION) {
            return;
        }
        File toFile = fc.getSelectedFile();
        GUI.PREFS.put(Action.ACCELERATOR_KEY, toFile.getAbsoluteFile().getParentFile().getAbsolutePath());
        try {
            EventList<AbstractStockPrice> list1 = priceList;
            EventList<AbstractStockPrice> list2 = this.getGui().getNotFoundPriceList();
            List<AbstractStockPrice> list = OfxFileIo.concatPriceList(list1, list2);
            MdUtils.saveToCsv(list, priceTableViewOptions.isConvertWhenExport(), getGui().getDefaultCurrency(),
                    getGui().getSymbolMapper(), getGui().getFxTable(), toFile, getGui().getTemplateDecimalSeparator());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(parent, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initFileChooser() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("> creating FileChooser");
        }
        String key = Action.ACCELERATOR_KEY;
        fc = new JFileChooser(GUI.PREFS.get(key, "."));
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("< creating FileChooser");
        }
    }

    private GUI getGui() {
        return gui;
    }
}