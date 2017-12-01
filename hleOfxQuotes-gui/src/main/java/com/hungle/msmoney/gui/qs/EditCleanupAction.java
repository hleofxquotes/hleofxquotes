package com.hungle.msmoney.gui.qs;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

final class EditCleanupAction extends AbstractAction {
    private static final Logger LOGGER = Logger.getLogger(EditCleanupAction.class);

    /**
     * 
     */
    private final YahooQuoteSourcePanel yahooQuoteSourcePanel;
    private final JTextArea textArea;

    EditCleanupAction(YahooQuoteSourcePanel yahooQuoteSourcePanel, String name, JTextArea textArea) {
        super(name);
        this.yahooQuoteSourcePanel = yahooQuoteSourcePanel;
        this.textArea = textArea;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String currentText = textArea.getText();
        currentText = cleanupText(currentText);
        textArea.setText(currentText);
        textArea.setCaretPosition(0);
    }

    private String cleanupText(String stocksString) {
        String cleanupText = stocksString;
        try {
            List<String> stockSymbols = QuoteSourceUtils.toStockSymbols(stocksString);
            TreeSet<String> cleanupStockSymbols = new TreeSet<String>();
            cleanupStockSymbols.addAll(stockSymbols);

            int count = 0;
            StringBuilder sb = new StringBuilder();
            for (String stockSymbol : cleanupStockSymbols) {
                if (count > 0) {
                    sb.append("\r\n");
                }
                sb.append(stockSymbol);
                count++;
            }
            cleanupText = sb.toString();
        } catch (IOException e) {
            LOGGER.error(e);
        }
        return cleanupText;
    }
}