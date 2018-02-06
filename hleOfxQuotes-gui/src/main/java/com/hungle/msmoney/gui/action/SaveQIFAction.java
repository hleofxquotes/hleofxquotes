package com.hungle.msmoney.gui.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.hungle.msmoney.core.gui.PriceTableViewOptions;
import com.hungle.msmoney.core.qif.QifUtils;
import com.hungle.msmoney.core.stockprice.AbstractStockPrice;
import com.hungle.msmoney.gui.GUI;

import ca.odell.glazedlists.EventList;

public final class SaveQIFAction extends AbstractAction {
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

    public SaveQIFAction(String arg0, GUI gui, EventList<AbstractStockPrice> priceList, PriceTableViewOptions priceTableViewOptions,
            Component parent) {
        super(arg0);
        this.parent = parent;
        this.priceTableViewOptions = priceTableViewOptions;
        this.gui = gui;
        this.priceList = priceList;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (fc == null) {
            initFileChooser();
        }
        // Component parent = view;
        if (this.fc.getSelectedFile() == null) {
            this.fc.setSelectedFile(new File("quotes.qif"));
        }

        if (fc.showSaveDialog(parent) == JFileChooser.CANCEL_OPTION) {
            return;
        }
        File toFile = fc.getSelectedFile();
        GUI.PREFS.put(Action.ACCELERATOR_KEY, toFile.getAbsoluteFile().getParentFile().getAbsolutePath());
        try {
            QifUtils.saveToQif(priceList, priceTableViewOptions.isConvertWhenExport(), gui.getDefaultCurrency(),
                    gui.getSymbolMapper(), gui.getFxTable(), toFile, gui.getTemplateDecimalSeparator());
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
}