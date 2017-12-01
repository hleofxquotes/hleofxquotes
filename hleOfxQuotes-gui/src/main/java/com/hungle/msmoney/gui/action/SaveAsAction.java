package com.hungle.msmoney.gui.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.hungle.msmoney.gui.qs.YahooQuoteSourcePanel;

/**
 * The Class SaveAsAction.
 */
public final class SaveAsAction extends AbstractAction {
    private static final Logger LOGGER = Logger.getLogger(OpenAction.class);

    /**
     * 
     */
    private final YahooQuoteSourcePanel yahooQuoteSourcePanel;

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The fc. */
    private JFileChooser fc = null;

    /**
     * Instantiates a new save as action.
     *
     * @param name
     *            the name
     * @param yahooQuoteSourcePanel
     *            TODO
     */
    public SaveAsAction(YahooQuoteSourcePanel yahooQuoteSourcePanel, String name) {
        super(name);
        this.yahooQuoteSourcePanel = yahooQuoteSourcePanel;
    }

    /**
     * Inits the file chooser.
     */
    private void initFileChooser() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("> pre creating JFileChooser");
        }
        this.fc = new JFileChooser(".");
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("> post creating JFileChooser");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        if (fc == null) {
            initFileChooser();
        }
        if (fc.showSaveDialog(this.yahooQuoteSourcePanel) == JFileChooser.CANCEL_OPTION) {
            return;
        }
        File outFile = fc.getSelectedFile();
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(outFile));
            writer.write(this.yahooQuoteSourcePanel.getStockSymbolsView().getText());
            LOGGER.info("Save stock symbols to file=" + outFile);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this.yahooQuoteSourcePanel, e.getMessage(), "Failed To Save To File",
                    JOptionPane.ERROR_MESSAGE);
            LOGGER.error(e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } finally {
                    writer = null;
                }
            }
        }
    }
}